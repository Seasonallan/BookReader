package com.lectek.android.app;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;


public interface IActivityObservable {
	/**
	 * 注册回调
	 * @param contextCallBack
	 */
	public void registerActivityObserver(IActivityObserver observer);
	/**
	 * 注销回调
	 * @param contextCallBack
	 */
	public void unregisterActivityObserver(IActivityObserver observer);
	/**
	 * 派遣 Activity 的 MenuOpened事件
	 * @return 如果返回true 表示事件已消耗
	 */
	public boolean dispatchMenuOpened(int featureId, Menu menu);
	/**
	 * 派遣 Activity 的 OptionsItemSelected事件
	 * @return 如果返回true 表示事件已消耗
	 */
	public boolean dispatchOptionsItemSelected(MenuItem item);
	/**
	 * 派遣 Activity 的 onResume事件
	 */
	public void dispatchActivityResume(boolean isFirst);
	/**
	 * 派遣 Activity 的 onPause事件
	 */
	public void dispatchActivityPause();
	/**
	 * 派遣按返回键事情
	 * @return 如果返回true 表示事件已消耗
	 */
	public boolean dispatchBackPressed();
	/**
	 * 派遣Activity.onActivityResult事件
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 * @return 如果返回true 表示事件已消耗
	 */
	public boolean dispatchActivityResult(int requestCode, int resultCode, Intent data);
}
