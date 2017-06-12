package com.lectek.android.lereader.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.lib.utils.DimensionsUtil;

/** 对话框工具
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-08-15
 */
public final class ToastUtil {
	private static Toast toast;
	public static void showToast(Context context, int contentId){
		showToast(context, context.getString(contentId));
	}
	public static void showLongToast(Context context, int contentId){
		markToast(context, null, context.getString(contentId), Gravity.CENTER_HORIZONTAL, Toast.LENGTH_LONG).show();
	}
	public static void showLongToast(Context context, String content){
		markToast(context, null, content, Gravity.CENTER_HORIZONTAL, Toast.LENGTH_LONG).show();
	}
	public static void showToast(Context context, int titleId, int contentId){
		showToast(context, context.getString(titleId), context.getString(contentId));
	}
	
	public static void showToast(Context context, String contentStr){
		if(TextUtils.isEmpty(contentStr)){
			return;
		}
		showToast(context, null, contentStr);
	}
	
	public static void showShortAppToast(String contentStr) {
		markToast(MyAndroidApplication.getInstance(),  null, contentStr, Gravity.CENTER_HORIZONTAL, Toast.LENGTH_SHORT).show();
	}
	
	public static void showToast(Context context, String titleStr, String contentStr){
		markToast(context, titleStr, contentStr, Gravity.CENTER_HORIZONTAL).show();
	}
	
	/** 自定义toast
	 * @param context
	 * @param titleStr 标题
	 * @param contentStr 内容
	 * @param gravity 在屏幕显示的位置
	 * @return
	 */
	private static Toast markToast(Context context, String titleStr, String contentStr, int gravity){
		return markToast(context,titleStr,contentStr,gravity,Toast.LENGTH_SHORT);
	}
	private static Toast markToast(Context context, String titleStr, String contentStr, int gravity,int duration){
		if(toast == null)
		toast = new Toast(context.getApplicationContext());
		toast.setDuration(duration);
		toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, DimensionsUtil.dip2px(100,context));
		LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
		View view = inflater.inflate(R.layout.toast_common_layout, null);
		toast.setView(view);
		TextView title = (TextView) view.findViewById(R.id.toast_title);
		if(!TextUtils.isEmpty(titleStr)){
			title.setVisibility(View.VISIBLE);
			title.setText(titleStr);
		}else{
			title.setVisibility(View.GONE);
		}
		TextView content = (TextView) view.findViewById(R.id.toast_content);
		content.setGravity(gravity);
		if(!TextUtils.isEmpty(contentStr)){
			content.setVisibility(View.VISIBLE);
			content.setText(contentStr);
		}else{
			content.setVisibility(View.GONE);
		}
		return toast;
	}
	public static Toast showToast(Context context,View view ,int duration ){
		Toast toast = markToast(context, view, duration);
		toast.show();
		return toast;
	}
	public static Toast markToast(Context context,View view ,int duration){
		if(toast == null)
		toast = new Toast(context.getApplicationContext());
		toast.setDuration(duration);
		toast.setGravity(Gravity.NO_GRAVITY, 0, 0);
		toast.setView(view);
		return toast;
	}
	public static void dismissToast(){
		if(toast != null)
			toast.cancel();
	}
}
