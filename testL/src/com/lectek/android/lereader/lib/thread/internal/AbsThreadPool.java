package com.lectek.android.lereader.lib.thread.internal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import com.lectek.android.lereader.lib.utils.LogUtil;
/**
 * 代表一个线程池
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public abstract class AbsThreadPool implements IThreadPool{

	private static final String Tag = AbsThreadPool.class.getSimpleName();
	
    protected BlockingQueue<Runnable> queue;
    protected ThreadPoolExecutor executor;
    private boolean isDestroy = false;

    protected ThreadFactory newThreadFactory(){
		return new DefaultThreadFactory();
    }
    
    private boolean checkInit(){
    	if(isDestroy){
    		return false;
    	}
    	if(queue == null){
    		queue = newQueue();
    	}
    	if(executor == null || executor.isShutdown()){
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
    public void executeTask(Runnable newTask) {
    	if(newTask == null){
    		return;
    	}
    	
    	newTask = onAddTask(newTask);
    	if(newTask == null){
    		return;
    	}
    	synchronized (this) {
    		if(checkInit()){
        		executor.execute(newTask);
        	}
		}
    	
    	LogUtil.i(Tag, "currentThread queue size:" + String.valueOf(queue.size()));
    	LogUtil.i(Tag, "currentThread pool activity Thread:" + String.valueOf(executor.getActiveCount()));
    	LogUtil.i(Tag, "currentThread pool task count:" + String.valueOf(executor.getTaskCount()));
    	
    }
    
    /**
     * 可通过重写此方法对添加的任务进行装饰，-改变优先级
     * @param newTask
     * @return
     */
    protected Runnable onAddTask(Runnable newTask){
		return newTask;
    }

    /**
    * 销毁线程池
    */
    public synchronized void destroy() {
    	synchronized (this) {
	    	if(!isDestroy){
                if(executor != null && !executor.isShutdown() ){
                    executor.shutdown();
                    if(queue != null) {
                        queue.clear();
                        queue =  null;
                    }
                    executor = null;
                }
	    		isDestroy = true;
	    	}
    	}
    }

    /**
     * 移除线程
     * @param thread
     */
    @Override
    public void removeThread(Runnable thread){
        executor.remove(thread);
        queue.remove(thread);
    }


    public static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
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
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

}
