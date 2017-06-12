package com.lectek.android.lereader.lib.thread.internal;

/**
 *  创建一个单线程
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public class TerminableThread extends AbsTerminableThread {

    public TerminableThread(Runnable runnable) {
        super(runnable);
    }

    @Override
	protected void runTask() {
		Thread thread = new Thread(this);
		thread.setName("TerminableThread");
		thread.start();
	}
}
