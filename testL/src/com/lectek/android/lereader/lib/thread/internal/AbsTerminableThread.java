package com.lectek.android.lereader.lib.thread.internal;

import com.lectek.android.lereader.lib.utils.LogUtil;

/**
 *  线程运作机制，状态的控制
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public abstract class AbsTerminableThread implements ITerminableThread, Runnable{
	private static final String TAG = "AsyncTaskManage";
	private AsyncTaskManage mAsyncTaskManage;
	private AsyncTaskManage.ThreadInfo mThreadInfo;
	private Runnable mTask;
	private boolean isStarted = false;
	private boolean isCancel = true;

    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        LogUtil.i(TAG, "InterruptibleThread run() -->开始:isCancel="+isCancel+";tId="+currentThread.getId()+";tHCode="+currentThread.hashCode());
        if(isCancel){
            return;
        }
        mThreadInfo = mAsyncTaskManage.registerThread();
        if(mTask != null){
            mTask.run();
        }
        mThreadInfo = null;
        mAsyncTaskManage.unregisterThread();
        isCancel = true;
    }

	public AbsTerminableThread(Runnable task){
		mAsyncTaskManage = AsyncTaskManage.getInstance();
		mTask = task;
	}

	@Override
	public final void start(){
		if(isStarted){
			return;
		}
		isStarted = true;
		isCancel = false;
		runTask();
	}

    /**
     * 执行线程，可直接执行或添加到线程池中执行
     */
	protected abstract void runTask();
	
	@Override
	public void cancel(){
		isCancel = true;
		if(mThreadInfo != null){
			mAsyncTaskManage.cancelAsyncTask(mThreadInfo);
		}
	}

	@Override
	public boolean isCancel() {
		return isCancel;
	}
	
}
