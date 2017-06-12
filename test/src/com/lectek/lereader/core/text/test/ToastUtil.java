package com.lectek.lereader.core.text.test;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	static Toast mToast;
	public static void showToast(Context context, int contentId){
		showToast(context, context.getString(contentId));
	}
	
	public static void showToast(Context context, String contentId){
		if(mToast == null){
			mToast = Toast.makeText(context.getApplicationContext(), contentId, Toast.LENGTH_SHORT);
		}else{
			mToast.setText(contentId);
		}
		mToast.show();
	}

	public static void dismissToast() {
		if(mToast != null){
			mToast.cancel();
		}
	}
}
