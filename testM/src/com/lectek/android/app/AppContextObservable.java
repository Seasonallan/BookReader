package com.lectek.android.app;

import java.util.ArrayList;
import java.util.List;


public class AppContextObservable extends AbsObservable<IAppContextObserver> implements IAppContextObservable {
	
	@Override
	public void registerContextObservable(IAppContextObserver contextCallBack) {
		registerObserver(contextCallBack);
	}

	@Override
	public void unregisterContextObservable(IAppContextObserver contextCallBack) {
		unregisterObserver(contextCallBack);
	}
	
	@Override
	public void dispatchLoadTheme(){
		List<IAppContextObserver> list = new ArrayList<IAppContextObserver>(getObservers());
		for(IAppContextObserver contextCallBack : list){
			contextCallBack.onLoadTheme();
		}
	}
	
	@Override
	public void dispatchUserLoginStateChange(boolean isLogin){
		List<IAppContextObserver> list = new ArrayList<IAppContextObserver>(getObservers());
		for(IAppContextObserver contextCallBack : list){
			contextCallBack.onUserLoginStateChange(isLogin);
		}
	}

	@Override
	public void dispatchContextNetworkChange(boolean isAvailable) {
		List<IAppContextObserver> list = new ArrayList<IAppContextObserver>(getObservers());
		for(IAppContextObserver contextCallBack : list){
			contextCallBack.onNetworkChange(isAvailable);
		}
	}

	@Override
	public void dispatchLanguageChange() {
		List<IAppContextObserver> list = new ArrayList<IAppContextObserver>(getObservers());
		for(IAppContextObserver contextCallBack : list){
			contextCallBack.onLanguageChange();
		}
	}
}
