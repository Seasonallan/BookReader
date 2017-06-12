package com.lectek.android.update;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import com.lectek.android.lereader.net.response.UpdateInfo;

/**
 * 后台通知回调
 * @author lyw
 *
 */
public interface INotification {
	/**
	 * 设置下载进度状态栏通知
	 * @param updateInfo 下载信息
	 * @param intent 点击状态栏发送的事件
	 * @param currentBytes 当前流大小
	 * @param totalBytes 总大小
	 * @return
	 */
	public Notification fillDownlaodInfoNotification(Context context,UpdateInfo updateInfo,Intent intent,long currentBytes, long totalBytes);
	/**
	 * 设置下载失败状态栏通知
	 * @param updateInfo 下载信息
	 * @param intent 点击状态栏发送的事件
	 * @return
	 */
	public Notification fillDownloadFailNotification(Context context,UpdateInfo updateInfo,Intent intent);
	/**
	 * 取消强制升级，通知退出应用，展示更新界面的Activity同时也会返回ActivityResultCode值是常量RESULT_CODE_EXIT
	 */
	public void notifyExitApp(Context context);
}
