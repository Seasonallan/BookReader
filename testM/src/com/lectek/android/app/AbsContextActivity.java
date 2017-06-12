package com.lectek.android.app;

import java.util.ArrayList;

import android.os.Bundle;

import com.lectek.android.binding.app.BindingActivity;

public class AbsContextActivity extends BindingActivity{
	private ArrayList<LifeCycleListener> mLifeCycleListeners = new ArrayList<LifeCycleListener>();
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
//		ChangeLanguageUtil.changeContextResource(this);
	}
	
	@Override
	protected void onDestroy() {
		ArrayList<LifeCycleListener> temp = new ArrayList<LifeCycleListener>(mLifeCycleListeners);
		mLifeCycleListeners.clear();
		for (LifeCycleListener listener : temp) {
			listener.onActivityDestroy();
		}
		ActivityManage.removeActivity(this);
		super.onDestroy();
	}

	public void registerLifeCycleListener(LifeCycleListener l){
		if(l != null && !mLifeCycleListeners.contains(l)){
			mLifeCycleListeners.add(l);
		}
	}
	
	public void unregisterLifeCycleListener(LifeCycleListener l){
		if(l != null){
			mLifeCycleListeners.remove(l);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		ActivityManage.handlerResume(this);
	}
	
	public interface LifeCycleListener{
		public void onActivityDestroy();
	}
}
