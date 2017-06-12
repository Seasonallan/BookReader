package com.lectek.lereader.core.util;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.View.MeasureSpec;

/**
 * 尺寸计算工具类
 * @author linyiwei
 * @email 21551594@qq.com
 * @date 2012-07-27
 */
public class ContextUtil {
	private static Context mContext;
	public static void init(Context context){
		mContext = context;
	} 
	
	private static Context getContext(){
		return mContext;
	}
	
	private static Resources getResources(){
		return getContext().getResources();
	}
	
	public static int DIPToPX(float dipValue){ 
        final float scale = getResources().getDisplayMetrics().density; 
        return (int)(dipValue * scale);
	}
	
	public static int PXToDIP(float pxValue){ 
        final float scale = getResources().getDisplayMetrics().density; 
        return (int)(pxValue / scale); 
	}
	
	public static void measureView(View view){
		final int widthMeasureSpec =
			    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		final int heightMeasureSpec =
		    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		view.measure(widthMeasureSpec, heightMeasureSpec);
	}
}
