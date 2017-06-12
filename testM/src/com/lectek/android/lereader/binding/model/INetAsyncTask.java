package com.lectek.android.lereader.binding.model;

/** 
 * 网络异步任务接口，定义异步任务
 * @author linyiwwei
 * @email 21551594@qq.com
 * @date 2012-11-08
 */
public interface INetAsyncTask{
	/**
	 * 网络重连后，任务是否需要重新启动
	 * @return
	 */
	public boolean isNeedReStart();
	/**
	 * 异步任务是否停止，用于判断任务是否在执行
	 * @return
	 */
	public boolean isStop();
	/**
	 * 启动任务
	 */
	public void start();
}
