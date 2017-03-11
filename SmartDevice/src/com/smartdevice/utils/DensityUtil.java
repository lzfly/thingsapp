package com.smartdevice.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class DensityUtil {

	private static final String TAG = DensityUtil.class.getSimpleName();  
    
    // ��ǰ��Ļ��densityDpi  
    private static float dmDensityDpi = 0.0f;  
    private static DisplayMetrics dm;  
    private static float scale = 0.0f;  
  
    /** 
     *  
     * ���ݹ��캯����õ�ǰ�ֻ�����Ļϵ�� 
     *  
     * */  
    public DensityUtil(Context context) {  
        // ��ȡ��ǰ��Ļ  
        dm = new DisplayMetrics();  
        dm = context.getApplicationContext().getResources().getDisplayMetrics();  
        // ����DensityDpi  
        setDmDensityDpi(dm.densityDpi);  
        // �ܶ�����  
        scale = getDmDensityDpi() / 160;  
        //Logger.i(TAG, toString());  
    }  
  
    /** 
     * ��ǰ��Ļ��density���� 
     *  
     * @param DmDensity 
     * @retrun DmDensity Getter 
     * */  
    public static float getDmDensityDpi() {  
        return dmDensityDpi;  
    }  
  
    /** 
     * ��ǰ��Ļ��density���� 
     *  
     * @param DmDensity 
     * @retrun DmDensity Setter 
     * */  
    public static void setDmDensityDpi(float dmDensityDpi) {  
        DensityUtil.dmDensityDpi = dmDensityDpi;  
    }  
  
    /** 
     * dip to pixel
     * */  
    public int dip2px(float dipValue) {  
  
        return (int) (dipValue * scale + 0.5f);  
  
    }  
  
    /** 
     * pixel to dip
     * */  
    public int px2dip(float pxValue) {  
        return (int) (pxValue / scale + 0.5f);  
    }  
  
    @Override  
    public String toString() {  
        return " dmDensityDpi:" + dmDensityDpi;  
    }  
}
