package com.lectek.android.lereader.lib.utils;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;

/**
 * 尺寸计算工具类
 * @author linyiwei
 * @email 21551594@qq.com
 * @date 2012-07-27
 */
public class DimensionsUtil {
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(float dipValue, Context context){ 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int)(dipValue * scale + 0.5f);
	}
	
	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(float pxValue, Context context){ 
        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (pxValue / scale + 0.5f);  
        return (int)( ( pxValue - 0.5f) / scale); 
	}
	
	public static void measureView(View view){
		final int widthMeasureSpec =
			    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		final int heightMeasureSpec =
		    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		view.measure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 获取设备的分辨率
	 * @return
	 */
    public static String getDeviceResolution(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels + "_" + dm.heightPixels;
    }
    
    /**
     * 获取系统的真实分辨率
     * @param activity
     * @return
     */
    public static DisplayMetrics getRealDisplayMetrics(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		@SuppressWarnings("rawtypes")
		Class c;
		try {
			c = Class.forName("android.view.Display");
			@SuppressWarnings("unchecked")
			Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
			method.invoke(display, dm);
		} catch (Exception e) {
			e.printStackTrace();
		}
        if(dm.widthPixels <= 0 || dm.heightPixels <= 0){
            dm.widthPixels = activity.getResources().getDisplayMetrics().widthPixels;
            dm.heightPixels = activity.getResources().getDisplayMetrics().heightPixels;
        }
		return dm;
	}
}
