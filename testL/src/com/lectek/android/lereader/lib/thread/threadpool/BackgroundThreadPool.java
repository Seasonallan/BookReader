package com.lectek.android.lereader.lib.thread.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.lectek.android.lereader.lib.thread.internal.AbsThreadPool;
import com.lectek.android.lereader.lib.thread.internal.IThreadPool;

/**
 * 运行在后台的线程池，线程执行时间不影响UI响应的线程池
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 *
 */
public class BackgroundThreadPool extends AbsThreadPool {

    /* 单例 */
    private static IThreadPool instance = new BackgroundThreadPool();

    public static synchronized IThreadPool getInstance() {
        if (instance == null){
            instance = new BackgroundThreadPool();
        }
        return instance;
    }

    @Override
    public int getCorePoolSize() {
		return 5;
	}

	@Override
    public int getMaximumPoolSize() {
		return 10;
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
