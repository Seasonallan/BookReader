package com.lectek.android.app;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public interface IActivityObserver {
	
	/**
	 * 传递 Activity 的 onMenuOpened事件
	 * @param featureId
	 * @param menu
	 * @return
	 */
	public boolean onMenuOpened(int featureId, Menu menu);
	/**
	 * 传递 Activity 的 onOptionsItemSelected事件
	 * @param item
	 * @return
	 */
	public boolean onOptionsItemSelected(MenuItem item);
	/**
	 * 传递 Activity 的 onActivityPause事件
	 */
	public void onActivityPause();
	/**
	 * 传递 Activity 的 onActivityResume事件
	 * @param isFirst
	 */
	public void onActivityResume(boolean isFirst);
	/**
	 * 传递 Activity 的 onBackPressed事件
	 * @return
	 */
	public boolean onBackPressed();
	/**
	 * 传递 Activity 的 onActivityResult事件
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 * @return
	 */
	public boolean onActivityResult(int requestCode, int resultCode, Intent data);
}
