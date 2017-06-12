package com.lectek.android.lereader.ui.common;

import gueei.binding.Binder;
import gueei.binding.Binder.InflateResult;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.lectek.android.app.ActivityObservable;
import com.lectek.android.app.IActivityObservable;
import com.lectek.android.app.IActivityObserver;
import com.lectek.android.app.IAppContextObservable;
import com.lectek.android.app.IAppContextObserver;
import com.lectek.android.app.ITitleBar;
import com.lectek.android.binding.app.IBindViewCallBack;
import com.lectek.android.lereader.binding.model.BaseViewModel;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.ui.IPanelView;
import com.lectek.android.lereader.ui.IView;
import com.lectek.android.lereader.ui.common.dialog.CommonDialog;

public abstract class BasePanelView extends FrameLayout implements IAppContextObserver,
		IActivityObserver,IActivityObservable,IPanelView,ITitleBar,IView{
	private static final String TAG = "BasePanelView";
	private boolean isDetachedFromWindow = true;
	private boolean isSoftDetached = false;
	private boolean isCallBackRegister = false;
	/**
	 * 不需要去new 一个ActivityObservable对象,它依赖被注册时生成对象
	 */
	private ActivityObservable mActivityObservable;
	private ViewParent mParent = null;
	private boolean isPanelViewCreate = false;
	/**
	 * 自动控制生命周期，测试的时候发现会导致一些错误，暂时屏蔽测功能
	 */
	private boolean isAutoStartLifeCycle = false;
	
	public BasePanelView(Context context) {
		super(context);
		init();
	}
	
	private void init(){
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
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		tryAutoCreate();
	}
	
	private ViewParent findParent(){
		ViewParent parent = this.getParent();
		for(;parent != null;){
			if(parent instanceof BasePanelView){
				return parent;
			}
			parent = parent.getParent();
		}
		return null;
	}
	
	private void registerObservable(){
		if(!isCallBackRegister){
			mParent = findParent();
			if(mParent == null){
				if(getContext() instanceof IActivityObservable){
					((IActivityObservable)getContext()).registerActivityObserver(this);
				}
			}else{
				((IActivityObservable)mParent).registerActivityObserver(this);
			}	
			if(getContext() instanceof IAppContextObservable){
				((IAppContextObservable)getContext()).registerContextObservable(this);
			}
			isCallBackRegister = true;
		}
	};
	
	private void unregisterObservable(){
		if(isCallBackRegister){
			if(mParent == null){
				if(getContext() instanceof IActivityObservable){
					((IActivityObservable)getContext()).unregisterActivityObserver(this);
				}
			}else{
				((IActivityObservable)mParent).unregisterActivityObserver(this);
			}	
			if(getContext() instanceof IAppContextObservable){
				((IAppContextObservable)getContext()).unregisterContextObservable(this);
			}
			isCallBackRegister = false;
		}
	};

	/**
	 * 是否已经被标识为软销毁
	 * @param isSoftDetached
	 */
	public boolean isSoftDetached(){
		return isSoftDetached;
	}
	/**
	 * 软销毁策略，在不销毁View的情况下，标识View以脱了UI框架，注销所有监听
	 * @param isSoftDetached
	 */
	public void setSoftDetached(boolean isSoftDetached){
		if(this.isSoftDetached == isSoftDetached || isDetachedFromWindow){
			return;
		}
		if(isSoftDetached){
			unregisterObservable();
			releaseActivityObservable();
		}else{
			registerObservable();
		}
		this.isSoftDetached = isSoftDetached;
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		LogUtil.i(TAG, "Start onAttachedToWindow");
		registerObservable();
		tryAutoCreate();
		isDetachedFromWindow = false;
		LogUtil.i(TAG, "End onAttachedToWindow");
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		LogUtil.i(TAG, "Start onDetachedFromWindow");
		unregisterObservable();
		releaseActivityObservable();
		tryAutoDestroy();
		isDetachedFromWindow = true;
		LogUtil.i(TAG, "End onDetachedFromWindow");
	}

	@Override
	public void onLanguageChange(){
		
	}
	
	@Override
	public void onLoadTheme() {
		
	}

	@Override
	public void onUserLoginStateChange(boolean isLogin) {
		
	}

	@Override
	public void onNetworkChange(boolean isAvailable) {
		
	}

	private void tryAutoCreate(){
		if(!isPanelViewCreate && isAutoStartLifeCycle){
			if(isShown()){
				dispatchCreate();
			}
		}
	}
	
	private void tryAutoDestroy(){
		if(isPanelViewCreate && isAutoStartLifeCycle){
			dispatchDestroy();
		}
	}

	protected void dispatchCreate(){
		isPanelViewCreate = true;
		try{
			onCreate();
		}catch (Exception e) {
			LogUtil.e(TAG, e);
		}
	}
	
	protected void dispatchDestroy(){
		try{
			onDestroy();
		}catch (Exception e) {
			LogUtil.e(TAG, e);
		}
		isPanelViewCreate = false;
	}
	
	public boolean isDestroy(){
		return !isPanelViewCreate;
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		return dispatchMenuOpened(featureId, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return dispatchOptionsItemSelected(item);
	}

	@Override
	public void onActivityResume(boolean isFirst) {
		dispatchActivityResume(isFirst);
	}

	@Override
	public void onActivityPause() {
		dispatchActivityPause();
	}

	@Override
	public boolean onBackPressed() {
		return dispatchBackPressed();
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		return dispatchActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void registerActivityObserver(IActivityObserver observer) {
		getActivityObservable().registerActivityObserver(observer);
	}

	@Override
	public void unregisterActivityObserver(IActivityObserver observer) {
		getActivityObservable().unregisterActivityObserver(observer);
	}

	@Override
	public void setTitleBarEnabled(boolean isEnabled) {
		if(getContext() instanceof ITitleBar){
			((ITitleBar)getContext()).setTitleBarEnabled(isEnabled);
		}
	}

	@Override
	public void setTitleView(View view) {
		if(getContext() instanceof ITitleBar){
			((ITitleBar)getContext()).setTitleView(view);
		}
	}
	
	@Override
	public void setTitleContent(String titleStr) {
		if(getContext() instanceof ITitleBar){
			((ITitleBar)getContext()).setTitleContent(titleStr);
		}
	}

	@Override
	public void setLeftButton(String tip, int icon) {
		if(getContext() instanceof ITitleBar){
			((ITitleBar)getContext()).setLeftButton(tip,icon);
		}
	}

	@Override
	public void setRightButton(String tip, int icon) {
		if(getContext() instanceof ITitleBar){
			((ITitleBar)getContext()).setRightButton(tip,icon);
		}
	}

	@Override
	public void setLeftButtonEnabled(boolean isEnabled) {
		if(getContext() instanceof ITitleBar){
			((ITitleBar)getContext()).setLeftButtonEnabled(isEnabled);
		}
	}

	@Override
	public void setRightButtonEnabled(boolean isEnabled) {
		if(getContext() instanceof ITitleBar){
			((ITitleBar)getContext()).setRightButtonEnabled(isEnabled);
		}
	}

	@Override
	public void resetTitleBar() {
		if(getContext() instanceof ITitleBar){
			((ITitleBar)getContext()).resetTitleBar();
		}
	}
	
	@Override
	public void setHeadView(View view) {
		if(getContext() instanceof ITitleBar){
			((ITitleBar)getContext()).setHeadView(view);
		}
	}

	@Override
	public boolean dispatchMenuOpened(int featureId, Menu menu) {
		if(mActivityObservable != null){
			return getActivityObservable().dispatchMenuOpened(featureId, menu);
		}
		return false;
	}

	@Override
	public boolean dispatchOptionsItemSelected(MenuItem item) {
		if(mActivityObservable != null){
			return getActivityObservable().dispatchOptionsItemSelected(item);
		}
		return false;
	}

	@Override
	public boolean dispatchBackPressed() {
		if(mActivityObservable != null){
			return getActivityObservable().dispatchBackPressed();
		}
		return false;
	}
	
	@Override
	public void dispatchActivityResume(boolean isFirst) {
		if(mActivityObservable != null){
			getActivityObservable().dispatchActivityResume(isFirst);
		}
	}

	@Override
	public void dispatchActivityPause() {
		if(mActivityObservable != null){
			getActivityObservable().dispatchActivityPause();
		}
	}

	@Override
	public boolean dispatchActivityResult(int requestCode, int resultCode,
			Intent data) {
		if(mActivityObservable != null){
			return getActivityObservable().dispatchActivityResult(requestCode, resultCode, data);
		}
		return false;
	}
	
	private ActivityObservable getActivityObservable(){
		if(mActivityObservable == null){
			mActivityObservable = new ActivityObservable();
		}
		return mActivityObservable;
	}
	
	private void releaseActivityObservable(){
		if(mActivityObservable != null){
			mActivityObservable.release();
			mActivityObservable = null;
		}
	}
	
	@Override
	public void onReLoad(){
		
	}

	@Override
	public void finish() {
		if(getContext() instanceof Activity){
			Activity activity = (Activity) getContext();
			if(!activity.isFinishing()){
				activity.finish();
			}
		}
	}

	@Override
	public boolean bindDialogViewModel(Context context, BaseViewModel baseViewModel) {
		return CommonDialog.bindViewModel(context, baseViewModel);
	}

	@Override
	public int getRes(String type) {
		return 0;
	}
}
