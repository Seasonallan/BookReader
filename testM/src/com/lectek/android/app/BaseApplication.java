package com.lectek.android.app;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.utils.CrashCatchHandler;

/** 基础的application类
 * 处理程序崩溃日志记录（目录是SDCARD/程序目录/crash.txt），程序日志记录（SDCARD/程序目录/log.txt）
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2012-7-27
 */
public abstract class BaseApplication extends Application {
	private static Handler mHandler;
	
//	private TelephonyManager mTelephonyManager;
	
	private static BaseApplication instance;
	
	/* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		//处理程序崩溃日志记录
		catchError();
		//处理程序日志
		dealLog();
//		mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);  
	}
	
	public static BaseApplication getInstance(){
		return instance;
	}
	
	public static synchronized Handler getHandler(){
		if(mHandler == null){
			mHandler = new Handler(Looper.getMainLooper());
		}
		return mHandler;
	}
	
//	public TelephonyManager getTelephonyManager(){
//		return mTelephonyManager;
//	}
	
	/**
	 *  异常扑捉的处理，记录日志
	 */
	private void catchError() {
		String fileDir = getAppSdcardDir();
		if(!TextUtils.isEmpty(fileDir)){
			new CrashCatchHandler(fileDir);
		}
	}
	
	/** 设置程序在SDCARD的目录
	 * @return
	 */
	protected abstract String getAppSdcardDir();
	
	/** 设置日志的输出到SDCARD和是否在LOGCAT打印
	 * 
	 */
	private void dealLog(){
		String TAG = getPackageName();
		LogUtil.init(TAG, isDebug(), null, -1);
	}
	
	/** 设置是否DEBUG模式
	 * @return
	 */
	protected abstract boolean isDebug();

}
