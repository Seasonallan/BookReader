package com.lectek.android.lereader.binding.viewModel;

import gueei.binding.Binder;
import gueei.binding.Binder.InflateResult;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.lectek.android.app.AbsContextActivity;
import com.lectek.android.app.AbsContextActivity.LifeCycleListener;
import com.lectek.android.binding.app.IBindViewCallBack;
import com.lectek.android.lereader.binding.model.BaseViewModel;
import com.lectek.android.lereader.ui.IDialogView;
/**
 * MVVM 模式的Dialog基类
 * @author linyiwei
 */
public class BaseDialog extends Dialog implements LifeCycleListener,IDialogView{
	private AbsContextActivity mAbsContextActivity;
	
	public BaseDialog(Context context) {
		super(context);
		init(context);
	}
	
	protected BaseDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}
	
	public BaseDialog(Context context, int theme) {
		super(context, theme);
		init(context);
	}
	
	protected void onInit(){}
	
	private void init(Context context){
		if(context instanceof AbsContextActivity){
			mAbsContextActivity = (AbsContextActivity) context;
		}
		onInit();
	}

	@Override
	public void show() {
		super.show();
		if(mAbsContextActivity != null){
			mAbsContextActivity.registerLifeCycleListener(this);
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
		if(mAbsContextActivity != null){
			mAbsContextActivity.unregisterLifeCycleListener(this);
		}
	}

	@Override
	public void onActivityDestroy() {
		if(isShowing()){
			dismiss();
		}
	}
	
	@Override
	public void finish() {
		dismiss();
	}
	
	@Override
	public int getRes(String type) {
		return 0;
	}
	
	protected View bindView(int layoutId,ViewGroup parent, Object... contentViewModel){
		return bindView(layoutId, parent, null, contentViewModel);
	}
	
	protected View bindView(int layoutId,ViewGroup parent,IBindViewCallBack callBack, Object... contentViewModel){
		InflateResult result = Binder.inflateView(getContext(), layoutId, parent, parent != null);
		if(callBack != null){
			callBack.onPreBindView(result.rootView,layoutId);
		}
		for(int i = 0; i < contentViewModel.length; i++){
			Binder.bindView(getContext(), result, contentViewModel[i]);
		}
		return result.rootView;
	}

	@Override
	public boolean bindDialogViewModel(Context context,
			BaseViewModel baseViewModel) {
		return false;
	}
}
