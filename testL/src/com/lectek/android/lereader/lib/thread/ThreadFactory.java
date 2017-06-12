package com.lectek.android.lereader.lib.thread;

import com.lectek.android.lereader.lib.thread.internal.ITerminableThread;
import com.lectek.android.lereader.lib.thread.internal.IThreadPool;
import com.lectek.android.lereader.lib.thread.internal.TerminableThread;
import com.lectek.android.lereader.lib.thread.internal.TerminableThreadPool;

/**
 *  线程工厂
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
*/
public class ThreadFactory {
	
	/**
	 * 创建一个单线程
	 * @param runnable
	 * @return
	 */
	public static ITerminableThread createTerminableThread(Runnable runnable){
		return new TerminableThread(runnable);
	}
	
	/**
	 * 创建一个运行在线程池中的线程
	 * @param runnable
	 * @return
	 */
	public static ITerminableThread createTerminableThreadInPool(Runnable runnable, IThreadPool absThreadPool){
		return new TerminableThreadPool(absThreadPool, runnable);
	}
	
	
}
