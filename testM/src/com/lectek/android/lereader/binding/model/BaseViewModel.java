package com.lectek.android.lereader.binding.model;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import com.lectek.android.lereader.ui.IView;
/**
 * 所有ViewModel基类
 * @author linyiwei
 *
 * @param <CallBack>
 */
public class BaseViewModel extends BaseModel<IView> implements IView{
	protected BaseViewModel this_ = this;
	private Context mContext;
	
	public BaseViewModel(Context context,IView loadView){
		mContext = context;
		if(loadView != null){
			super.addCallBack(loadView);
		}
	}
	
	protected IView getCallBack(){
		WeakReference<IView> weak = super.getCallBacks().get(0);
		if(weak != null){
			return weak.get();
		}
		return null;
	}
	
	@Deprecated
	@Override
	public final void addCallBack(IView l) {}
	
	@Deprecated
	@Override
	protected final ArrayList<WeakReference<IView>> getCallBacks() {
		return null;
	}

	@Deprecated
	@Override
	public final void traversalCallBacks(CallBackHandler<IView> handler) {}
	
	@Override
	public Context getContext() {
		return mContext;
	}
	
	public Resources getResources(){
		return getContext().getResources();
	}
	
	public String getString(int resId) {
		return getContext().getString(resId);
	}
	
	public String getString(int resId, Object...formatArgs) {
		return getContext().getString(resId, formatArgs);
	}
	
	/**
	 * ViewModel生命周期的开始，需要在使用对象的生命周期开始时调用
	 */
	public void onStart(){
		
	}
	/**
	 * ViewModel生命周期的结束，需要在使用对象的生命周期结束时调用
	 */
	public void onRelease(){
		
	}
	/**
	 * ViewModel生命周期的开始，需要在使用对象的生命周期开始时调用
	 * <br>这个方法主要是针对使用对象为Activity<br/>
	 * @param savedInstanceState
	 */
	public void onStart(Bundle savedInstanceState) {
		
	}

	@Override
	public void finish() {
		IView view = getCallBack();
		if(view != null){
			view.finish();
		}
	}

	@Override
	public boolean bindDialogViewModel(Context context, BaseViewModel baseViewModel) {
		IView view = getCallBack();
		if(view != null){
			return view.bindDialogViewModel(context, baseViewModel);
		}
		return false;
	}

	@Override
	public int getRes(String type) {
		IView view = getCallBack();
		if(view != null){
			return view.getRes(type);
		}
		return 0;
	}
}
