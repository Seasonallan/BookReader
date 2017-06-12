package com.lectek.android.lereader.lib.thread.internal;


/**
 *  创建一个运行在特定线程池里的线程
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public class TerminableThreadPool extends AbsTerminableThread {

	private static final String Tag = TerminableThreadPool.class.getSimpleName(); 
	
	private IThreadPool mThreadPool;

	public TerminableThreadPool(IThreadPool threadPool, Runnable task){
		super(task);
		mThreadPool =  threadPool;
	}

	@Override
	protected void runTask() {
		mThreadPool.executeTask(this);
	}

    @Override
    public void cancel() {
        super.cancel();
        mThreadPool.removeThread(this);
    }
}
