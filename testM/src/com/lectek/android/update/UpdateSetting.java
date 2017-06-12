package com.lectek.android.update;

import android.app.Activity;
/**
 *  升级模块设置
 * @author lyw
 *
 */
public class UpdateSetting {
	/**
	 * 下载文件保存路径
	 */
	public String mApkSavePath;
	/**
	 * HTTP请求构造工厂
	 */
	public IHttpFactory mHttpFactory;
	/**
	 * 后台通知
	 */
	public INotification mNotification;
	/**
	 * 升级模块显示界面的Activity
	 */
	public Class<? extends Activity> mUpdateActivityCls;
}
