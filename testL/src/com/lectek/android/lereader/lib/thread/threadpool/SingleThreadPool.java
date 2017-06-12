package com.lectek.android.lereader.lib.thread.threadpool;

import com.lectek.android.lereader.lib.thread.internal.AbsThreadPool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 只运行一个线程的线程池。
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 *
 */
public class SingleThreadPool extends AbsThreadPool {

    @Override
    public int getCorePoolSize() {
		return 1;
	}

	@Override
    public int getMaximumPoolSize() {
		return 1;
	}

	@Override
    public long getKeepAliveTime() {
		return 500;
	}

	@Override
    public TimeUnit getTimeUnit() {
		return TimeUnit.MILLISECONDS;
	}

	@Override
    public BlockingQueue<Runnable> newQueue() {
		return new LinkedBlockingQueue<Runnable>();
	}
}
