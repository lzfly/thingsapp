package com.smartdevice.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import com.smartdevice.control.DBHelper;
import com.smartdevice.main.MainActivity;
import com.smartdevice.main.MessageDetailActivity;
import com.smartdevice.main.MessageFragment;
import com.smartdevice.main.R;
import com.smartdevice.mode.Device;
import com.smartdevice.mode.DeviceAdapter;
import com.smartdevice.mode.Message;
import com.smartdevice.utils.LogUtil;
import com.smartdevice.utils.ParamsUtils;
import com.smartdevice.service.MqttService;

public class PushCallback implements MqttCallback {

    private MqttService context;
    private static int notificationId = 0;
    DBHelper dbHelper;

    public PushCallback(MqttService context) {

        this.context = context;
    }
    
    public PushCallback() {
    	this(null);
    }

    @Override
    public void connectionLost(Throwable cause) {
        //We should reconnect here
    	Log.i("PushCallback", "PushCallback---connectionLost");
    	this.context.reConnectMqtt();

    }

	@Override
	public void messageArrived(String topic, MqttMessage message)
			throws Exception {
		// TODO Auto-generated method stub
		Log.i("PushCallback", "topic arrived : " + topic);
		Log.i("PushCallback", "message arrived : " + new String(message.getPayload()));
		
		String attr_value = new String(message.getPayload());  
		String[] names = topic.split("/");  
		/*for (int i = 0; i < names.length; i++) {  
			Log.i("name" + i, "=" + names[i]); 
		}*/

		if(names[1].equals("v1"))
		{
             if(names[2].equals("from_device"))
             {
            	  if(names[3].length() != 0 || names[4].length() != 0)
            	  {
            		  Log.i("PushCallback", "topic arrived : " + topic);
            	      String devicesn = names[3];
            	      int attr_code = Integer.parseInt(names[4]);
            	      Log.i("PushCallback", "devicesn : " + devicesn);
            	      //save message located
            	      sendNotification(devicesn,  attr_value);
            		  
            	  }
            	 
             }
             else if(names[1].equals("from_gateway"))
             {
            	 
             }	 
		}
	}
	
	

	public void sendNotification(String deviceSN, String attrValue) {
		// TODO Auto-generated method stub
		Message message = wrapMessage(deviceSN);
		String content = message.getContent();
        final NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        final Notification notification = new Notification(R.drawable.icon_msg,
        		message.getFromName(), System.currentTimeMillis());

        // Hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        //notification.flags |= Notification.FLAG_NO_CLEAR;   // ֪ͨ���������
        
        notification.defaults |= Notification.DEFAULT_LIGHTS;  // ֪ͨ�ƹ�
        //notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        //notification.ledARGB = 0xff00ff00;
        //notification.ledOnMS =2000;
        //notification.ledOffMS=1000;
        
        notification.defaults |= Notification.DEFAULT_VIBRATE; // ��
        notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // ϵͳĬ������
        //notification.sound = Uri.parse("file:///sdcard/notification/ringer.mp3");// �����Զ��������
        notification.flags |= Notification.FLAG_INSISTENT; // ����һֱ�쵽�û���Ӧ������֪ͨ��һֱ����ֱ���㴥��֪ͨ����ʱ��ͻ�ֹͣ
        //      ��������״̬����֪ͨ������
        
        wrapMessage(deviceSN);
        buildMessageDB();
        String sql = "insert into sd_message(fromname,fromtype,title,time,content) values (" +
        		"'" + message.getFromName() + "','" + message.getFromType() + "','" + message.getTitle() + "','" + message.getTime() + "','" + message.getContent() + "');";
        LogUtil.d("PushCallback", "SQL " + sql);
        dbHelper.insert(sql);
        Intent intent = new Intent(context, MessageDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("com.smartdevice.mode.Message", message);
        Log.i("PushCallback", "message content" + content);
        intent.putExtras(bundle);
        //intent.addCategory(Intent.ACTION_MAIN);
        //intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final PendingIntent activity = PendingIntent.getActivity(context, 
        		(int)System.currentTimeMillis(), 
        		intent, 
        		PendingIntent.FLAG_UPDATE_CURRENT);
        
        notification.setLatestEventInfo(context, message.getFromName(), content, activity);
        notification.number += 1;
        notificationId++;
        
        notificationManager.notify(notificationId, notification);
        
        //***
        
	}
	
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}
	
	private Message wrapMessage(String deviceSN){
		Message message = new Message();
		String fromName = "";
		List<Device> deviceList = DeviceAdapter.getDeviceList();
		for (int i = 0; i < deviceList.size(); i++) {
			if(deviceSN.equals(deviceList.get(i).getDeviceSN())){
				fromName = deviceList.get(i).getDeviceName();
				message.setFromName(fromName);
				message.setTitle(fromName + ParamsUtils.ALARM_INFO);
				message.setFromType(deviceList.get(i).getDeviceType().getTypeName());
				String content = wrapContent(0, deviceList.get(i).getDeviceType().getTypeCode());
				message.setContent(fromName + content);
			}
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		message.setTime(dateFormat.format(date));
		
		return message;
	}
	
	private String wrapContent(int messageType, int deviceType){
		String content = new String();
		switch (messageType) {
//		case value:
//			
//			break;

		default:
			content = "�豸����������Ϣ��";
			break;
		}
		switch (deviceType) {
//		case value:
//			
//			break;

		default:
			content += "����˽������ܿ��ܱ����룬������豸��װ���ĲƲ��Ȱ�ȫ";
			break;
		}
		return content;
	}
	
	private void buildMessageDB(){
    	dbHelper = new DBHelper(context, ParamsUtils.DB_NAME);
    	dbHelper.setSqlString("create table IF NOT EXISTS sd_message(id integer primary key autoincrement, fromname varchar(20), fromtype varchar(20)," +
    			"title varchar(60), time varchar(20), content varchar(200));");
    }
}
