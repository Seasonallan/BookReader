package com.lectek.android.lereader.binding.model;

import com.lectek.android.lereader.binding.model.BaseLoadDataModel.ILoadDataCallBack;
import com.lectek.android.lereader.lib.thread.ThreadFactory;
import com.lectek.android.lereader.lib.thread.internal.ITerminableThread;

/**
 * 加载数据的基类
 * @author linyiwei
 * @param <Result>
 */
public abstract class BaseLoadDataModel<Result> extends BaseModel<ILoadDataCallBack>{
	/**
	 * 任务启动成功
	 */
	public static final String START_RESULT_SUCCEED = "START_RESULT_SUCCEED";
	/**
	 * 任务已经启动了
	 */
	public static final String START_RESULT_STARTING = "START_RESULT_STARTING";
	/**
	 * 方法已经弃用
	 */
	public static final String START_RESULT_DEPRECATED = "START_RESULT_DEPRECATED";
	
	private ITerminableThread mTerminableThread;
	private boolean isStart = false;
	private Task mCurrentTask;
	
	protected void dispatchOnStartFail(final String tag,final String state,final Object... params){
		traversalCallBacks(new CallBackHandler<ILoadDataCallBack>() {
			@Override
			public boolean handle(ILoadDataCallBack callBack) {
				return callBack.onStartFail(tag, state, params);
			}
		});
	}
	
	protected void dispatchOnPreLoad(final String tag,final Object... params){
		traversalCallBacks(new CallBackHandler<ILoadDataCallBack>() {
			@Override
			public boolean handle(ILoadDataCallBack callBack) {
				return callBack.onPreLoad(tag, params);
			}
		});
	}
	
	protected void dispatchOnFail(final Exception e,final String tag,final Object... params){
		traversalCallBacks(new CallBackHandler<ILoadDataCallBack>() {
			@Override
			public boolean handle(ILoadDataCallBack callBack) {
				return callBack.onFail(e, tag, params);
			}
		});
	}
	
	protected void dispatchOnPostLoad(final Result result,final String tag,final boolean isSucceed
			,final boolean isCancel,final Object... params){
		traversalCallBacks(new CallBackHandler<ILoadDataCallBack>() {
			@Override
			public boolean handle(ILoadDataCallBack callBack) {
				return callBack.onPostLoad(result, tag, isSucceed, isCancel, params);
			}
		});
	}
	/**
	 * 开始执行任务
	 * @param params
	 * @return 返回启动结果 
	 */
	public String start(Object... params){
		check();
		String checkResult = null;
		if(isStart){
			checkResult = START_RESULT_STARTING;
		}
		if(checkResult == null){
			checkResult = onStartPreCheck(params);
		}
		if(!START_RESULT_SUCCEED.equals(checkResult)){
			dispatchOnStartFail(getTag(), checkResult,params);
			return checkResult;
		}
		isStart = true;
		dispatchOnPreLoad(getTag(),params);
		mCurrentTask = new Task(params);
		if(!onStartThread(mCurrentTask)){
          //  mTerminableThread = ThreadFactory.createTerminableThreadInPool(mCurrentTask
          //      , ThreadPoolFactory.getMainPool());
            mTerminableThread = newThread(mCurrentTask);
          // mTerminableThread.setMainThread(isMainThread());
			mTerminableThread.start();
		}
		return checkResult;
	}

    /**
     * 创建一个线程，可被装饰
     * @param runnable
     * @return
     */
    protected ITerminableThread newThread(Runnable runnable){
        return ThreadFactory.createTerminableThread(runnable);
    }

	private void cancelThread() {
		if(mTerminableThread != null) {
			mTerminableThread.cancel();
		}
		mTerminableThread = null;
	}

	/**
	 * 释放资源
	 */
	public void release() {
		cancel();
	}	
	
	/**
	 * start方法执行前进行是否可以执行的验证
	 * @return 返回 START_RESULT_SUCCEED 标示可以执行，其他折不可以执行且值会通过start方法返回给调用者。
	 */
	protected String onStartPreCheck(Object... params){
		return START_RESULT_SUCCEED;
	}
	/**
	 * 任务是否已经开始
	 * @return
	 */
	public boolean isStart(){
		return isStart;
	}
	/**
	 * 实现具体要执行的任务,不抛异常都视为请求重构 
	 * @param params
	 * @return 请求成功且服务器无数据的情况下返回Null
	 * @throws Exception 如果失败子类必须通过抛异常的方式通知
	 */
	protected abstract Result onLoad(Object... params)throws Exception;
	/**
	 * BaseLoadMode唯一标识，回调的所有方法都会返回这个tag，用来在有需要时区分来自不同BaseLoadMode的回调事件
	 * @return 
	 */
	public final String getTag(){
		return this.getClass().getName() + "_" + String.valueOf(this.hashCode());
	}
	/**
	 * 最小加载等待时间单位是毫秒
	 * @return
	 */
	protected long getMinLoadingTime(){
		return 0;
	}
	/**
	 * 在线程中启动任务功能
	 * @param task
	 * @return 返回True 代表替代默认的启动任务功能
	 */
	protected boolean onStartThread(Runnable task){
		return false;
	}
	/**
	 * 取消任务 如果重写onStartThread方法必须同时实现这个方法否则取消功能就不在起作用
	 * @return 返回True 代表替代默认的取消功能
	 */
	protected boolean onCancel(){
		return false;
	}
	/**
	 * 取消任务
	 */
	public final void cancel(){
		check();
		if(!onCancel()){
            cancelThread();
		}
		if(isStart){
			isStart = false;
			dispatchOnPostLoad(null,getTag(),false,true);
		}
		mCurrentTask = null;
	}
	
	private class Task implements Runnable{
		Object[] mParams;
		Result result = null;
		Exception failException = null;
		long delay = 0;
		private Task(Object... params){
			mParams = params;
		}
		
		@Override
		public void run() {
			delay = System.currentTimeMillis();
			try {
               // Thread.sleep(6555);
				result = onLoad(mParams);
			} catch (Exception e) {
				failException = e;
			}
			delay = System.currentTimeMillis() - delay;
			if(delay < getMinLoadingTime() && delay > 0){
				try {
					Thread.sleep(getMinLoadingTime() - delay);
				} catch (InterruptedException e) {}
			}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(!Task.this.equals(mCurrentTask) || !isStart){
						isStart = false;
						return;
					}
                    mCurrentTask = null;
					isStart = false;
                    mCurrentTask = null;//修正线程失败后直接重启导致的mCurrentTask为空异常
					mTerminableThread = null;//TODO:替换完release()后去掉这一句
					if(failException != null){
						dispatchOnFail(failException,getTag(),mParams);
					}
					dispatchOnPostLoad(result,getTag(),failException == null,false,mParams);
				}
			});
		}
	}
	
	public interface ILoadDataCallBack{
		/**
		 * 启动失败的回调
		 * @param tag
		 * @param state 
		 */
		public boolean onStartFail(String tag,String state, Object... params);
		/**
		 * 任务启动前
		 * @param params 请求参数
		 * @return 返回true标识事件已经消耗，不会继续传递
		 */
		public boolean onPreLoad(String tag, Object... params);
		/**
		 * 任务执行失败回调,onFail不执行都视为成功
		 * @param e 失败的异常
		 * @return 返回true标识事件已经消耗，不会继续传递
		 */
		public boolean onFail(Exception e,String tag, Object... params);
		/**
		 * @param result onFail不执行都视为成功，请求成功且服务器无数据的情况下返回Null
		 * @param tag 任务标识用于同时使用多个BaseLoadModel时区分来自哪个BaseLoadModel
		 * @param isSucceed 是否请求成功
		 * @param isCancel 是否是被取消的任务
		 * @return 返回true标识事件已经消耗，不会继续传递
		 */
		public boolean onPostLoad(Object result,String tag,boolean isSucceed, boolean isCancel, Object... params);
	}
}
