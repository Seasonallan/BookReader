package com.lectek.android.app;

import java.util.HashMap;
import java.util.Map.Entry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lectek.android.lereader.lib.cache.DataCacheManage;
import com.lectek.android.lereader.lib.thread.ThreadPoolFactory;
import com.lectek.android.lereader.lib.utils.ApnUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.utils.CommonUtil;
/**
 * @author linyiwei
 * @email 21551594@qq.com
 * @date 2012-07-27
 */
public abstract class BaseContextActivity extends AbsContextActivity 
	implements IAppContextObserver,IAppContextObservable,IActivityObservable,ITitleBar{
	
	private static final String Tag = BaseContextActivity.class.getSimpleName();
	private static final long DELAY_TASK_DELAY_TIME = 5000;
	private static boolean isInitApp = false;
	private static Runnable mInitAppDelayTask;
	
	protected BaseContextActivity this_ = this;
	
	private HashMap<String, BroadcastReceiver> mBroadcastReceivers = new HashMap<String, BroadcastReceiver>();
	private NetWorkListener mNetWorkListener;
	private AppContextObservable mAppContextObservable;
	private Boolean isNetAvailable;
	private boolean isFirst;
	private ActivityObservable mActivityObservable;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		isFirst = true;
		mNetWorkListener = new NetWorkListener();
		mAppContextObservable = new AppContextObservable();
		mActivityObservable = new ActivityObservable();
		initApp();
		initBroadcastReceiver();
		registerContextObservable(this);
		isNetAvailable = ApnUtil.isNetAvailable(this_);
	}
	
	/**
	 * 初始化应用程序
	 */
	private static void initApp(){
		if(!isInitApp){
			isInitApp = true;
			DownloadPresenterLeyue.initDownloadModule(BaseApplication.getInstance());
//			startInitAppDelayTask();
//			MassageServiceChild.getInstance().setNeedRelaod(true);
		}
	}
	
//	private static void startInitAppDelayTask(){
//		mInitAppDelayTask = new Runnable() {
//			@Override
//			public void run() {
//				mInitAppDelayTask = null;
//			}
//		};
//		BaseApplication.getHandler().postDelayed(mInitAppDelayTask, DELAY_TASK_DELAY_TIME);
//	}
	
	/**
	 * 退出应用程序
	 */
	private static void exitApp(){
		if(isInitApp){
			isInitApp = false;
			if(mInitAppDelayTask != null){
				BaseApplication.getHandler().removeCallbacks(mInitAppDelayTask);
				mInitAppDelayTask = null;
			}
			DownloadPresenterLeyue.stopDownloadModule(BaseApplication.getInstance());
			DataCacheManage.getInstance().clearAllData();
			ActivityManage.finishAll();
			
//			final ArrayList<String> messageIDs = new ArrayList<String>();
//			ArrayList<Notice> msgData = MassageServiceChild.getInstance().getMessages();
//			for (Notice message : msgData) {
//				if (message.isOverRead) {
//					messageIDs.add(message.id);
//				}
//			}

            ThreadPoolFactory.destroyAllThreadPools();
			new Thread() {
				@Override
				public void run() {
//					if (!messageIDs.isEmpty()) {
//						DataSaxParser.getInstance(BaseApplication.getInstance()).readMessage(messageIDs);
//					}
					ApnUtil.setCtnetMode(BaseApplication.getInstance());
					 // 应用程序退出位置，整个应用最好只有一个退出口
					// 如果整个应用不只有一个退出口，在每个退出的位置调用此方法
				}
			}.start();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterAllBroadcastReceiver();
		mAppContextObservable.release();
		mActivityObservable.release();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//后台运行时恢复情况,在onPause保存网络状态，在下一次onResume判断是否变化
		tryDispatchNetworkChange(ApnUtil.isNetAvailable(this_));
		registerBroadcastReceiver(android.net.ConnectivityManager.CONNECTIVITY_ACTION, mNetWorkListener);
		dispatchActivityResume(isFirst);
		if(isFirst){
			isFirst = false;
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterBroadcastReceiver(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
		isNetAvailable = ApnUtil.isNetAvailable(this_);
		dispatchActivityPause();
	}

//	@Override
//	public Object getSystemService(String name) {
//		if(Context.TELEPHONY_SERVICE.equals(name)){
//			return BaseApplication.getInstance().getTelephonyManager();
//		}
//		return super.getSystemService(name);
//	}
	
	private void dispatchExitApp(){
		onExitApp();
		finish();
		exitApp();
	}
	
	@Override
	public void setTitleBarEnabled(boolean isEnabled){
		//由子类完成TitleBar的逻辑
	}
	@Override
	public void setLeftButton(String tip, int icon) {
		//由子类完成TitleBar的逻辑
	}
	@Override
	public void setRightButton(String tip, int icon) {
		//由子类完成TitleBar的逻辑
	}
	@Override
	public void setLeftButtonEnabled(boolean isEnabled) {
		//由子类完成TitleBar的逻辑
	}
	@Override
	public void setRightButtonEnabled(boolean isEnabled) {
		//由子类完成TitleBar的逻辑
	}
	@Override
	public void resetTitleBar() {
		//由子类完成TitleBar的逻辑
	}
	@Override
	public void setTitleView(View view) {
		//由子类完成TitleBar的逻辑
	}
	@Override
	public void setTitleContent(String titleStr) {
		//由子类完成TitleBar的逻辑
	}
	@Override
	public void setHeadView(View view) {
		//由子类完成TitleBar的逻辑
	}
	
	/**
	 * 当应用程序退出时执行的回调，父类会在onExitApp执行完后执行{@link #finish()}方法
     *
	 */
	protected void onExitApp() {
		
	}
	
	@Override
	public void onLanguageChange(){
		
	}
	
	@Override
	public void onNetworkChange(boolean isAvailable) {
		LogUtil.e(Tag, "onNetworkChange");
	}
	
	@Override
	public void onLoadTheme() {
		
	}
	
	@Override
	public void onUserLoginStateChange(boolean isLogin) {
		
	}
	
	@Override
	public void dispatchLanguageChange() {
		mAppContextObservable.dispatchLanguageChange();
	}
	
	@Override
	public void dispatchLoadTheme(){
		mAppContextObservable.dispatchLoadTheme();
	}
	
	@Override
	public void dispatchUserLoginStateChange(boolean isLogin){
		mAppContextObservable.dispatchUserLoginStateChange(isLogin);
	}
	
	private void tryDispatchNetworkChange(boolean isAvailable){
		if(isNetAvailable == null || isNetAvailable != isAvailable){
			isNetAvailable = isAvailable;
			dispatchContextNetworkChange(isAvailable);
		}
	}
	
	@Override
	public void dispatchContextNetworkChange(boolean isAvailable) {
		mAppContextObservable.dispatchContextNetworkChange(isAvailable);
	}
	
	@Override
	public void registerContextObservable(IAppContextObserver contextCallBack){
		mAppContextObservable.registerContextObservable(contextCallBack);
	}
	
	@Override
	public void unregisterContextObservable(IAppContextObserver contextCallBack){
		mAppContextObservable.unregisterContextObservable(contextCallBack);
	}
	
	@Override
	public void registerActivityObserver(IActivityObserver observer) {
		mActivityObservable.registerActivityObserver(observer);
	}

	@Override
	public void unregisterActivityObserver(IActivityObserver observer) {
		mActivityObservable.unregisterActivityObserver(observer);
	}

	@Override
	public boolean dispatchMenuOpened(int featureId, Menu menu) {
		return mActivityObservable.dispatchMenuOpened(featureId, menu);
	}

	@Override
	public boolean dispatchOptionsItemSelected(MenuItem item) {
		return mActivityObservable.dispatchOptionsItemSelected(item);
	}
	
	@Override
	public boolean dispatchActivityResult(int requestCode, int resultCode, Intent data){
		return mActivityObservable.dispatchActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void dispatchActivityResume(boolean isFirst) {
		mActivityObservable.dispatchActivityResume(isFirst);
	}
	
	@Override
	public void dispatchActivityPause() {
		mActivityObservable.dispatchActivityPause();
	}

	@Override
	public boolean dispatchBackPressed() {
		return mActivityObservable.dispatchBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		dispatchActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			if(!dispatchBackPressed()){
				return super.onKeyUp(keyCode, event);
			}else{
				return true;
			}
        }
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if(!dispatchMenuOpened(featureId, menu)){
			return super.onMenuOpened(featureId, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(!dispatchOptionsItemSelected(item)){
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	private void initBroadcastReceiver(){
		//初始化退出应用程序监听
		mBroadcastReceivers.put(AppBroadcast.ACTION_CLOSE_APP, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				dispatchExitApp();
			}
		});
		//初始化登录状态变更监听
		mBroadcastReceivers.put(AppBroadcast.ACTION_USER_LOGIN_STATE_CHANGE, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
//				dispatchUserLoginStateChange(!ClientInfoUtil.isGuest());
				dispatchUserLoginStateChange(!CommonUtil.isGuest());
			}
		});
		//初始化主题变更监听
		mBroadcastReceivers.put(AppBroadcast.ACITON_THEME_CHANGE, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				dispatchLoadTheme();
			}
		});
		mBroadcastReceivers.put(AppBroadcast.ACTION_LANGUAGE_CHANGE, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				dispatchLanguageChange();
			}
		});
		registerAllBroadcastReceiver();
	}
	
	private void registerBroadcastReceiver(String action,BroadcastReceiver receiver){
		BroadcastReceiver oldReceiver = mBroadcastReceivers.get(action);
		if(oldReceiver == null){
			mBroadcastReceivers.put(action, receiver);
			super.registerReceiver(receiver, new IntentFilter(action));
		}
	}

	private void unregisterBroadcastReceiver(String action){
		BroadcastReceiver receiver = mBroadcastReceivers.get(action);
		if(receiver != null){
			mBroadcastReceivers.remove(action);
			super.unregisterReceiver(receiver);
		}
	}
	
	private void registerAllBroadcastReceiver(){
		for(Entry<String, BroadcastReceiver> entry : mBroadcastReceivers.entrySet()){
			super.registerReceiver(entry.getValue(), new IntentFilter(entry.getKey()));
		}
	}

	private void unregisterAllBroadcastReceiver(){
		for(BroadcastReceiver receiver: mBroadcastReceivers.values()){
			super.unregisterReceiver(receiver);
		}
		mBroadcastReceivers.clear();
	}
	
	private class NetWorkListener extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			tryDispatchNetworkChange(ApnUtil.isNetAvailable(this_));
		}  
	                          
	}
}
