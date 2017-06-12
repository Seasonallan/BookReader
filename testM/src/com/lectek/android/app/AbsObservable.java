package com.lectek.android.app;

import java.util.LinkedList;
import java.util.List;

public abstract class AbsObservable<Observer>{
	private LinkedList<Observer> mObservers = new LinkedList<Observer>();

	public void registerObserver(Observer observer) {
		if(!mObservers.contains(observer)){
			mObservers.add(observer);
		}
	}

	public void unregisterObserver(Observer observer) {
		if(mObservers.contains(observer)){
			mObservers.remove(observer);
		}
	}

	public void release() {
		mObservers.clear();
	}

	public List<Observer> getObservers(){
		return mObservers;
	}
}
