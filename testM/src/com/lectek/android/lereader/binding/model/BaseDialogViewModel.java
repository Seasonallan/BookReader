package com.lectek.android.lereader.binding.model;

import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.view.View;

import com.lectek.android.lereader.ui.IDialogView;
import com.lectek.android.lereader.ui.IView;
/**
 * Dialog的ViewModel基类
 * @author linyiwei
 *
 */
public class BaseDialogViewModel extends BaseViewModel implements IDialogView{
	private IDialogView mDialogView;
	public BaseDialogViewModel(Context context) {
		super(context,null);
	}

	public void bindDialog(IDialogView dialogView){
		mDialogView = dialogView;
	}
	
	@Override
	protected IView getCallBack() {
		return mDialogView;
	}
	
	@Override
	public void show() {
		IDialogView view = (IDialogView) getCallBack();
		if(view != null){
			view.show();
		}
	}

	@Override
	public boolean isShowing() {
		IDialogView view = (IDialogView) getCallBack();
		if(view != null){
			return view.isShowing();
		}
		return false;
	}

	@Override
	public void setOnCancelListener(OnCancelListener listener) {
		IDialogView view = (IDialogView) getCallBack();
		if(view != null){
			view.setOnCancelListener(listener);
		}
	}

	@Override
	public void setOnDismissListener(OnDismissListener listener) {
		IDialogView view = (IDialogView) getCallBack();
		if(view != null){
			view.setOnDismissListener(listener);
		}
	}

	@Override
	public void setOnShowListener(OnShowListener listener) {
		IDialogView view = (IDialogView) getCallBack();
		if(view != null){
			view.setOnShowListener(listener);
		}
	}
	
	public interface OnBtnClickCallBack{
		public void onClick(View v,BaseDialogViewModel viewModel);
	}
}
