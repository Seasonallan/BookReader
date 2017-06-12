package com.lectek.lereader.core.text.test;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

public class MyAndroidApplication extends Application {
	private static MyAndroidApplication mMyAndroidApplication;

	private Handler mHandler;
	
	public static Context getInstance() {
		return mMyAndroidApplication;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mMyAndroidApplication = this;
		mHandler = new Handler(getMainLooper());
	}

	public static Handler getHandler() {
		return mMyAndroidApplication.mHandler;
	}
}
