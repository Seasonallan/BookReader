package com.lectek.android.lereader.lib.thread.internal;
/**
 * 代表一个线程
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public interface ITerminableThread {
    /**
     * 启动线程
     */
    public void start();
    /**
     * 取消线程
     */
    public void cancel();
    /**
     * 是否已经被取消
     */
    public boolean isCancel();
/*
    public boolean isMainThread();

    public void setMainThread(boolean isMainThread);*/

}