package com.lectek.android.app;


/**
 * @author linyiwei
 * @email 21551594@qq.com
 * @date 2012-07-27
 */
public interface IAppContextObserver {
	/**
	 * 主题加载回调
	 */
	public void onLoadTheme();
	/**
	 * 用户登录状态变更回调
	 * @param isLogin 是否是登录
	 */
	public void onUserLoginStateChange(boolean isLogin);
	
	/**
	 * 用户网络状态变更回调
	 * @param isAvailable 是否有网络
	 */
	public void onNetworkChange(boolean isAvailable);
	/**
	 * 语言变化的回调
	 */
	public void onLanguageChange();
}
