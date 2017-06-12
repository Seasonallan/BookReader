package com.lectek.android.lereader.ui;
/**
 * 代表网络请求相关界面
 * @author linyiwei
 */
public interface INetLoadView extends ILoadView{
	/**
	 * 注册网络变化监听,只注册不注销实现者必须处理释放问题，建议使用弱引用
	 */
	public void registerNetworkChange(NetworkChangeListener l);
	/**
	 * 显示这种网络的界面
	 */
	public void showNetSettingView();
	/**
	 * 显示这种网络的界面
	 */
	public void showNetSettingDialog();
	/**
	 * 隐藏这种网络的界面
	 */
	public void hideNetSettingView();
	/**
	 * 显示重试界面
	 */
	public void showRetryView(Runnable retryTask);
	/**
	 * 隐藏重试界面
	 */
	public void hideRetryView();
	/**
	 * 网络变化监听者
	 * @author linyiwei
	 */
	public interface NetworkChangeListener{
		/**
		 * 网络变化回调
		 * @param isAvailable
		 */
		public void onChange(boolean isAvailable);
	}
}
