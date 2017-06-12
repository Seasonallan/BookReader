package com.lectek.android.app;



/**
 * @author linyiwei
 * @email 21551594@qq.com
 * @date 2012-07-27
 */
public interface IAppContextObservable {
	/**
	 * 注册回调
	 * @param contextCallBack
	 */
	public void registerContextObservable(IAppContextObserver contextCallBack);
	/**
	 * 注销回调
	 * @param contextCallBack
	 */
	public void unregisterContextObservable(IAppContextObserver contextCallBack);
	/**
	 * 派遣主题变更事件
	 */
	public void dispatchLoadTheme();
	/**
	 * 派遣登录状态变更事件
	 */
	public void dispatchUserLoginStateChange(boolean isLogin);
	/**
	 * 派遣网络状态变更事件
	 */
	public void dispatchContextNetworkChange(boolean isAvailable);
	/**
	 * 派遣语言变更事件
	 */
	public void dispatchLanguageChange();
}
