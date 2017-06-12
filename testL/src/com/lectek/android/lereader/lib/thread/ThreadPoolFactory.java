package com.lectek.android.lereader.lib.thread;

import com.lectek.android.lereader.lib.thread.internal.IThreadPool;
import com.lectek.android.lereader.lib.thread.threadpool.BackgroundThreadPool;
import com.lectek.android.lereader.lib.thread.threadpool.ImageLoaderThreadPool;
import com.lectek.android.lereader.lib.thread.threadpool.LifoSingleThreadPool;
import com.lectek.android.lereader.lib.thread.threadpool.MainThreadPool;

/**
 *  线程池工厂
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public class ThreadPoolFactory {

    /**
     * 获取主线程池
     * @return
     */
    public static IThreadPool getMainPool(){
        return MainThreadPool.getInstance();
    }

    /**
     * 获取图片下载线程池
     * @return
     */
	public static IThreadPool getImageDownloaderPool(){
        return ImageLoaderThreadPool.getInstance();
    }

    /**
     * 获取阅读器图片下载线程池
     * @return
     */
    public static IThreadPool getReaderImageDownloaderPool(){
        return LifoSingleThreadPool.getInstance();
    }

    /**
     * 获取后台运行线程池
     * @return
     */
    public static IThreadPool getBackgroundThreadPool(){
        return BackgroundThreadPool.getInstance();
    }

    /**
     * 销毁阅读器图片下载线程池
     */
    public static void destroyReaderThreadPools(){
        LifoSingleThreadPool.getInstance().destroy();
    }

    /**
     * 销毁所有线程池
     */
    public static void destroyAllThreadPools(){
        ImageLoaderThreadPool.getInstance().destroy();
    }
	
}
