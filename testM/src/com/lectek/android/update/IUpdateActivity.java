package com.lectek.android.update;

import android.app.Activity;
import android.view.View.OnClickListener;

import com.lectek.android.lereader.net.response.UpdateInfo;

/**
 * 升级界面
 * @author lyw
 *
 */
public interface IUpdateActivity{
	/**
	 * 显示升级提示界面
	 */
	public void onShowUpdateInfo(UpdateInfo updateInfo,OnClickListener confirmListener,OnClickListener cancelListener);
	/**
	 * 显示安装提示界面
	 * @param updateInfo 升级信息
	 * @param confirmListener 确认按钮监听事件
	 * @param cancelListener 取消按钮监听事件
	 * @param isOldApk 是否是之前已下载的安装包
	 */
	public void onShowInstallUpdate(UpdateInfo updateInfo,OnClickListener confirmListener,OnClickListener cancelListener,boolean isOldApk);
	/**
	 * 显示下载进度界面
	 * @param updateInfo 升级信息
	 * @param backgrounderListener 后台运行按钮监听事件
	 * @param cancelListener 取消按钮监听事件
	 * @param currentBytes 当前下载的流
	 * @param totalBytes 总大小
	 */
	public void onShowUpdateDownlaodInfo(UpdateInfo updateInfo,OnClickListener backgrounderListener,OnClickListener cancelListener,long currentBytes, long totalBytes);
	/**
	 * 更新下载进度
	 * @param updateInfo 升级信息
	 * @param currentBytes 当前下载的流
	 * @param totalBytes 总大小
	 */
	public void onUpdateProgressBar(UpdateInfo updateInfo,long currentBytes,long totalBytes);
	/**
	 * 显示下载失败界面
	 * @param updateInfo 升级信息
	 * @param retryListener 重试按钮监听事件
	 * @param cancelListener 取消按钮监听事件
	 */
	public void onShowDownloadFail(UpdateInfo updateInfo,OnClickListener retryListener,OnClickListener cancelListener);
	/**
	 * 获得用于显示当前升级界面的Activity
	 * @return
	 */
	public Activity getActivity();
}
