package com.lectek.android.lereader.binding.model;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.lectek.android.app.BaseApplication;
/**
 * 所有Model和VModel基类
 * @author linyiwei
 *
 */
public class BaseModel<CallBack>{
	private static Handler mHandler = new Handler(Looper.getMainLooper());
	private ArrayList<WeakReference<CallBack>> mCallBacks = new ArrayList<WeakReference<CallBack>>();
	
	protected ArrayList<WeakReference<CallBack>> getCallBacks(){
		return mCallBacks;
	}
	/**
	 * 添加回调
	 * @param l
	 */
	public void addCallBack(CallBack l){
		check();
		if(!containsCallBack(l)){
			mCallBacks.add(new WeakReference<CallBack>(l));
		}
	}
	
	private boolean containsCallBack(CallBack l){
		for (WeakReference<CallBack> callBack : mCallBacks) {
			if(callBack.get() != null && callBack.get().equals(l)){
				return true;
			}
		}
		return false;
	}
	/**
	 * 便利回调
	 * @param handler 如果CallBackHandler返回true回调就不会继续传播
	 */
	public void traversalCallBacks(CallBackHandler<CallBack> handler){
		boolean isHandle = false;
		for (int i = 0; i < getCallBacks().size(); i++) {
			WeakReference<CallBack> callBack = getCallBacks().get(i);
			if(callBack.get() != null){
				if(!isHandle){
					isHandle = handler.handle(callBack.get());
				}
			}else{
				getCallBacks().remove(i);
				i--;
			}
		}
	}
	
	public Context getContext() {
		return BaseApplication.getInstance();
	}
	
	/**
	 * 检测当前线程是否是主线程
	 */
	public static void check(){
		if(Thread.currentThread() != Looper.getMainLooper().getThread()){
			new RuntimeException("Must be running on the UI thread");
		}
	}
	/**
	 * 运行在主线程
	 */
	public static void runOnUiThread(Runnable action) {
		if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
			mHandler.post(action);
		} else {
			action.run();
		}
	}
	
	public interface CallBackHandler<CallBack>{
		public boolean handle(CallBack callBack);
	}
}
