package com.lectek.android.lereader.ui;

import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
/**
 * Dialog抽象接口
 * @author linyiwei
 *
 */
public interface IDialogView extends IView {
	/**
	 * 显示Dialog
	 */
	public void show();
	/**
	 * Dialog是否显示
	 * @return
	 */
	public boolean isShowing();
	/**
	 * 设置取消Dialog回调
	 * @param listener
	 */
	public void setOnCancelListener(OnCancelListener listener);
	/**
	 * 设置Dialog关闭回调
	 * @param listener
	 */
	public void setOnDismissListener(OnDismissListener listener);
	/**
	 * 设置Dialog显示回调
	 * @param listener
	 */
	public void setOnShowListener(OnShowListener listener);
}
