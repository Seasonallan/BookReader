package com.lectek.android.lereader.ui.model.dataProvider;

import java.lang.ref.SoftReference;

import com.lectek.android.lereader.lib.thread.ThreadFactory;
import com.lectek.android.lereader.lib.thread.internal.ITerminableThread;

/**
 * 界面数据提供者基类
 * @author chends@lectek.com
 * @date 2014/07/08
 * @param <T> T数据结构
 */
public abstract class AbsUiDataProvider<T> {

	protected T mData;
	protected SoftReference<Object[]> mParamsSoftRef;
	
	private ITerminableThread mRunThread;
	
	public AbsUiDataProvider(T data) {
		mData = data;
	}
	
	/**
	 * 刷数据
	 * @param forceRestart 如果正在刷数据，是否强制终止，重新刷
	 */
	public synchronized void request(boolean forceRestart, Object... params) {
		if(isBusy()) {
			if(forceRestart) {
				abort();
			}else {
				return;
			}
		}
		
		if(mParamsSoftRef != null) {
			mParamsSoftRef.clear();
		}
		
		mParamsSoftRef = new SoftReference<Object[]>(params);
		
		mRunThread = ThreadFactory.createTerminableThread(getTaskRunnable());
		mRunThread.start();
	}
	
	/**
	 * 获取刷数据任务Runnable
	 * @return
	 */
	private Runnable getTaskRunnable() {
		return new Runnable() {
			
			@Override
			public void run() {
				onRequestData("", mData, mParamsSoftRef != null ? mParamsSoftRef.get() : null);
			}
		};
	}
	
	/**
	 * 刷数据操作
	 * @param dataId 数据模板id，请求接口时用
	 * @param outData 数据填充容器
	 * @param params 数据请求参数
	 */
	public abstract void onRequestData(String dataID, T outData, Object[] params);
	
	/**
	 * 取消刷数据任务
	 */
	public void abort(){
		if(mRunThread != null) {
			mRunThread.cancel();
		}
		mRunThread = null;
	}
	
	/**
	 * 是否正在刷数据
	 * @return
	 */
	public boolean isBusy() {
		return mRunThread != null && !mRunThread.isCancel();
	}
	
	/**
	 * 释放资源。
	 */
	public void release(){
		abort();
	}
	
	/**
	 * 获取数据
	 * @return
	 */
	public T getData() {
		return mData;
	}
	
	protected boolean onReadCachData(T data) {
		return false;
	}
	
	/**
	 * 读取缓存数据
	 * @return 读取到缓存返回true,否则false
	 */
	public boolean readCachData() {
		return onReadCachData(mData);
	}
}
