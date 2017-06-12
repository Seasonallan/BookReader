package com.lectek.android.lereader.lib.thread.threadpool;

import com.lectek.android.lereader.lib.thread.internal.AbsThreadPool;
import com.lectek.android.lereader.lib.thread.internal.IThreadPool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *  图片下载器线程池
 *  同时启动4个下载图片线程，超过则等待
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public class ImageLoaderThreadPool extends AbsThreadPool {
    /* 单例 */
    private static IThreadPool instance = new ImageLoaderThreadPool();

    public static synchronized IThreadPool getInstance() {
	    if (instance == null){
	      	instance = new ImageLoaderThreadPool();
	    }
        return instance;
    }

    @Override
    public int getCorePoolSize() {
        return 4;
    }

    @Override
    public int getMaximumPoolSize() {
        return 8;
    }

    @Override
    public long getKeepAliveTime() {
        return 2;
    }

    @Override
    public TimeUnit getTimeUnit() {
            return TimeUnit.SECONDS;
        }

    @Override
    public BlockingQueue<Runnable> newQueue() {
        return new LinkedBlockingQueue<Runnable>();
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        instance = null;
    }
}