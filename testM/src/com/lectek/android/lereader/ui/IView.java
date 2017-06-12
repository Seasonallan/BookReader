package com.lectek.android.lereader.ui;

import android.content.Context;

import com.lectek.android.lereader.binding.model.BaseViewModel;

/**
 * 代表一个界面，所有界面或者界面接口都实现或继承它
 * @author linyiwei
 */
public interface IView {
	/**
	 * 退出界面
	 */
	public void finish();
	/**
	 * 绑定DialogViewModel窗口
	 * @param context TODO
	 * @param tag
	 * @param <T>
	 * @return 返回true表示已经绑定成功
	 */
	public boolean bindDialogViewModel(Context context, BaseViewModel baseViewModel);
	/**
	 * 获取资源ID
	 * @param type
	 * @return
	 */
	public int getRes(String type);
	
	/**暂不写入基类，考虑是否能以AB机制实现*/
//	public void requestResultToast(boolean isSuccess,String ToastStr);
	
}
