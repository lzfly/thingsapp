package com.smartdevice.utils;

import com.smartdevice.main.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SwitchView extends View implements View.OnTouchListener{

	private boolean isChoosed = false;
	private boolean isOnSlip = false;
	private boolean isChgLsnOn = false;
	private float downX, nowX; //����ʱ��x,��ǰ��x, 
	private Rect btnOn, btnOff; //�򿪺͹ر�״̬��,�α��Rect  
	private OnChangedListener changedListener;
	private Bitmap bgOn, bgOff, bgSlip;
	
	public SwitchView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	
	//�˹��캯����xml��ʹ�ÿؼ�ʱ����  
    public SwitchView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        // TODO Auto-generated constructor stub  
        init();  
    }
	
	private void init(){
		bgOn = BitmapFactory.decodeResource(getResources(), R.drawable.icon_switch_green);
		bgOff = BitmapFactory.decodeResource(getResources(), R.drawable.icon_switch_gray);
		bgSlip = BitmapFactory.decodeResource(getResources(), R.drawable.icon_half_switch_gray);
		
		btnOn = new Rect(0, 0, bgSlip.getWidth(), bgSlip.getHeight());
		btnOff = new Rect(bgOff.getWidth()-bgSlip.getWidth(),
				0,
				bgOff.getWidth(),
				bgSlip.getHeight());
		
		setOnTouchListener(this);
	}
	
	public boolean isChecked(){  
        return isChoosed;  
    }  
   
    public void setChecked(boolean check){  
        isChoosed = check;  
        invalidate();  
    }  
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		float x;
		
		if((nowX < (bgOn.getWidth()/2)) && !isChoosed){
			canvas.drawBitmap(bgOff, matrix, paint);  //�����ر�ʱ�ı���
		}else{
			canvas.drawBitmap(bgOn, matrix, paint);// ������ʱ�ı��� 
		}
		
		if (isOnSlip)// �Ƿ����ڻ���״  
        {  
            if (nowX >= bgOn.getWidth())// �Ƿ񻮳�ָ����Χ,�������α��ܵ����ƿؼ���Χ��?�����������?  
                x = bgOn.getWidth() - bgSlip.getWidth() / 2;// ��ȥ�α�1/2�ĳ���  
            else  
                x = nowX - bgSlip.getWidth() / 2;  
        } else {// �ǻ���״  
            if (isChoosed)// �������ڵĿ���״̬���û��α��λ  
                x = btnOff.left;  
            else  
                x = btnOn.left;  
        }  
        if (x < 0)// ���α�λ�ý����쳣��  
            x = 0;  
        else if (x > bgOn.getWidth() - bgSlip.getWidth())  
            x = bgOn.getWidth() - bgSlip.getWidth();  
        canvas.drawBitmap(bgSlip, x, 0, paint);// �����α�.  
	}

	@Override
	 public boolean onTouch(View v, MotionEvent event) {  
        // TODO Auto-generated method stub  
        switch (event.getAction())// ���ݶ�����ִ�д�  
        {  
        case MotionEvent.ACTION_MOVE:// ����  
            nowX = event.getX();  
            break;  
        case MotionEvent.ACTION_DOWN:// ����  
            if (event.getX() > bgOn.getWidth()  
                    || event.getY() > bgOn.getHeight())  
                return false;  
            isOnSlip = true;  
            downX = event.getX();  
            nowX = downX;  
            break;  
        case MotionEvent.ACTION_UP:// �ɿ�  
        	isOnSlip = false;  
            boolean LastChoose = isChoosed;  
            if (event.getX() >= (bgOn.getWidth() / 2))  
            	isChoosed = true;  
            else  
            	isChoosed = false;  
            if (isChgLsnOn && (LastChoose != isChoosed))// ��������˼�����,�͵����䷽��..  
                changedListener.OnChanged(isChoosed);  
            break;  
        default:  
   
        }  
        invalidate();// �ػ��ؼ�  
        return true;  
    }  
	
	public void SetOnChangedListener(OnChangedListener listener) {// ���ü���?��״̬�޸ĵ�ʱ?  
        isChgLsnOn = true;  
        changedListener = listener;  
    } 

}
