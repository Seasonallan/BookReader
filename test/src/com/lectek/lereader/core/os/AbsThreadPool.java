package com.lectek.lereader.core.os;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbsThreadPool{
    private static ArrayList<AbsThreadPool> mPools = new ArrayList<AbsThreadPool>();
    {
    	mPools.add(this);
    }
    protected BlockingQueue<Runnable> queue;
    protected ThreadPoolExecutor executor;
    private boolean isDestroy = false;
    private IClearOutmodedTaskSetting mClearOutmodedTaskSetting;
    
    protected abstract int getCorePoolSize();
    
    protected abstract int getMaximumPoolSize();
    
    protected abstract long getKeepAliveTime();
    
    protected abstract TimeUnit getTimeUnit();
    
    protected abstract BlockingQueue<Runnable> newQueue();
    
    protected IClearOutmodedTaskSetting newClearOutmodedTaskSetting(){
		return null;
    };
    
    protected ThreadFactory newThreadFactory(){
		return new DefaultThreadFactory();
    }
    
    private boolean checkInit(){
    	if(isDestroy){
    		return false;
    	}
    	if(executor == null){
    		queue = newQueue();
    	}
    	if(executor == null || executor.isShutdown()){
    		mClearOutmodedTaskSetting = newClearOutmodedTaskSetting();
    		executor = new ThreadPoolExecutor(
        			getCorePoolSize(),
        			getMaximumPoolSize(),
        			getKeepAliveTime(),
        			getTimeUnit(),
        			queue,newThreadFactory());
    	}
    	return true;
    }
    
    /**
    * 增加新的任务
    * 每增加一个新任务，都要唤醒任务队列
    * @param newTask
    */
    public final void addTask(Runnable newTask) {
    	if(newTask == null){
    		return;
    	}
    	newTask = onAddTask(newTask);
    	if(newTask == null){
    		return;
    	}
    	synchronized (this) {
    		if(checkInit()){
    			clearOutmodedTask();
        		executor.execute(newTask);
        	}
		}
    }
    
    protected void clearOutmodedTask(){
    	if(mClearOutmodedTaskSetting == null){
    		return;
    	}
    	BlockingQueue<Runnable> queue = executor.getQueue();
    	for(;true;){
    		if(mClearOutmodedTaskSetting.isNeedPoll(queue)){
				queue.poll();
			}else{
				break;
			}
		}
    }
    
    /**
     * 可通过重写此方法对添加的任务进行装饰
     * @param newTask
     * @return
     */
    protected Runnable onAddTask(Runnable newTask){
		return newTask;
    }
    
    protected boolean isNeedAutoRelease(){
    	return true;
    }
    
    public final void releaseRes(){
    	synchronized (this) {
	    	if(executor != null && !executor.isShutdown() ){
	    		executor.shutdown();
	    		if(queue != null) {
	    			queue.clear();
	    			queue =  null;
	    		}
	    		executor = null;
	    		mClearOutmodedTaskSetting = null;
	    	}
    	}
    }
    
    protected void onDestroy(){
    	
    }
    
    /**
    * 销毁线程池
    */
    public final synchronized void destroy() {
    	synchronized (this) {
	    	if(!isDestroy){
	    		releaseRes();
	    		isDestroy = true;
	    		onDestroy();
	    		mPools.remove(this);
	    	}
    	}
    }
    
    public static synchronized void releaseAllPools(){
    	for(AbsThreadPool pool: mPools){
    		if(pool.isNeedAutoRelease()){
        		pool.releaseRes();
    		}
    	}
    }
    
    public static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private int mPriority;
        DefaultThreadFactory() {
            this(Thread.NORM_PRIORITY);
        }
        
        DefaultThreadFactory(int priority) {
        	mPriority = priority;
            SecurityManager s = System.getSecurityManager();
            group = (s != null)? s.getThreadGroup() :
                                 Thread.currentThread().getThreadGroup();
            namePrefix = "AbsThreadPool - " +
                          poolNumber.getAndIncrement() +
                         " - thread - ";
        }

        @Override
		public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                                  namePrefix + threadNumber.getAndIncrement(),
                                  0);
            if (t.isDaemon())
                t.setDaemon(false);
            t.setPriority(mPriority);
            return t;
        }
    }
    
    public interface IClearOutmodedTaskSetting{
    	public abstract boolean isNeedPoll(BlockingQueue<Runnable> queue);
    }
}
