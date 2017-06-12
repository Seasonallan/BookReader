package com.lectek.android.lereader.lib.thread.threadpool;


import com.lectek.android.lereader.lib.thread.internal.IThreadPool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * 阅读器图片下载 单线程线程池
 * 后进先出原则，翻到哪页若线程池中没有运行的线程则直接运行，有的话会在下一个运行，通过设置队列优先级
 * 队列使用PriorityBlockingQueue，她是一个无界的阻塞队列，使用与类 PriorityQueue 相同的顺序规则，并且提供了阻塞检索的操作。 只需要把放入该队列的对象实现Comparable接口就可以轻松实现线程优先级调度了。
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 *
 */
public class LifoSingleThreadPool extends SingleThreadPool {

    /** 线程优先级， 创建的越晚，优先级越高 */
    private static long LAST_PRIORITY = 0;
    /* 单例 */
    private static IThreadPool instance = new LifoSingleThreadPool();

    public static synchronized IThreadPool getInstance() {
        if (instance == null){
            instance = new LifoSingleThreadPool();
        }
        return instance;
    }
	@Override
    public BlockingQueue<Runnable> newQueue() {
		return new PriorityBlockingQueue<Runnable>();
	}
	
	@Override
	protected Runnable onAddTask(Runnable newTask) {
		return super.onAddTask(new PriorityRunnable(newTask));
	}


	public class PriorityRunnable implements Runnable,Comparable<PriorityRunnable> {
		private Runnable mTask;
		private long priority;
		
		public PriorityRunnable(Runnable newTask){
			mTask = newTask;
			priority = ++LAST_PRIORITY;
		}
		@Override
		public int compareTo(PriorityRunnable another) {
			return (int) (another.priority - priority);
		}

		@Override
		public void run() {
			if(mTask != null){
				mTask.run();
				mTask = null;
			}
		}
	}


    @Override
    public synchronized void destroy() {
        super.destroy();
        instance = null;
    }

}
