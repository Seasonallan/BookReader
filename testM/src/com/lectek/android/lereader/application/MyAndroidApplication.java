package com.lectek.android.lereader.application;

import gueei.binding.Binder;
import gueei.binding.BindingLog;

import android.app.NotificationManager;
import android.content.Context;
import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.lib.thread.ThreadFactory;
import com.lectek.android.lereader.lib.thread.ThreadPoolFactory;
import com.lectek.android.lereader.lib.thread.internal.ITerminableThread;
import com.lectek.android.lereader.lib.thread.internal.TerminableThreadPool;
import com.lectek.android.lereader.lib.utils.ApnUtil;
import com.lectek.android.lereader.lib.utils.ClientInfoUtil;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.push.PushManager;
import com.lectek.android.lereader.utils.Constants;
import com.lectek.android.update.AppUpdate;
import com.umeng.analytics.MobclickAgent;

/**
 * Application，可在此做一些系统级的初始化工作
 * 
 * @author yangwc
 * 
 */
public class MyAndroidApplication extends BaseApplication{
	
	private final static String TAG = MyAndroidApplication.class.getSimpleName();
	
	public static MyAndroidApplication getInstance() {
		return (MyAndroidApplication) BaseApplication.getInstance();
	}

	@Override
	public void onCreate() {
		super.onCreate(); 
		if(ClientInfoUtil.isOrder(this)){
			ClientInfoUtil.init(this);
		}
		Binder.init(this);
		BindingLog.setDebug(isDebug());
		
		//AppUpdate.init(this);
		umengInit();

        PushManager.register(this);
    }

	/**友盟统计初始化*/
	private void umengInit() {
		MobclickAgent.setDebugMode(isDebug());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
	}
	
//	public void closeDB(){
//	}
//
//	@Override
//	public void onTerminate() {
//		closeDB();
//		super.onTerminate();
//	}
	

	@Override
	protected String getAppSdcardDir() {
		return Constants.bookStoredDiretory;
	}

	@Override
	protected boolean isDebug() {
		return LeyueConst.IS_DEBUG;
	}

    /**
     * 清除通知栏推送内容
     * @param context
     */
    public static void clearNotityContent(Context context){
    	NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE); 
        mNotificationManager.cancelAll();
    }
    
    public static void addBackgroundTask(Runnable runnable, boolean networkTask) {
    	if(networkTask && !ApnUtil.isNetAvailable(getInstance())) {
    		return;
    	}
    	ThreadFactory.createTerminableThreadInPool(runnable, ThreadPoolFactory.getBackgroundThreadPool()).start();
    }
}
