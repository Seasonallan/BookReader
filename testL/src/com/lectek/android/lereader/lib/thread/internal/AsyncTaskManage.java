package com.lectek.android.lereader.lib.thread.internal;

import java.util.concurrent.ConcurrentHashMap;

import com.lectek.android.lereader.lib.utils.LogUtil;
/**
 * 控制异步任务快速结束
 * @author linyiwei
 * @date 2012-11-22
 * @email 21551594@qq.com
 *
 */
public class AsyncTaskManage {
	private static final String TAG = "AsyncTaskManage";
	private static AsyncTaskManage this_;
	/**
	 * 线程不支持异步任务管理
	 */
	public static final int RESULT_NONSUPPORT = 0;
	/**
	 * 注册任务成功
	 */
	public static final int RESULT_SUCCEED = 1;
	/**
	 * 线程已经停止
	 */
	public static final int RESULT_STOP = 2;
	private ConcurrentHashMap<Long,IAsyncTask> mAsyncTaskMap;
	private ConcurrentHashMap<Long,ThreadInfo> mThreadInfoMap;
	private ConcurrentHashMap<Long,byte[]> mLockMap;
	
	public synchronized static AsyncTaskManage getInstance(){
		if(this_ == null){
			this_ = new AsyncTaskManage();
		}
		return this_;
	}
	
	private AsyncTaskManage(){
		mAsyncTaskMap = new ConcurrentHashMap<Long,IAsyncTask>();
		mThreadInfoMap = new ConcurrentHashMap<Long,ThreadInfo>();
		mLockMap = new ConcurrentHashMap<Long,byte[]>();
	}
	/**
	 * 异步任务管理生命周期在此方法调用后开始，确保这个方法在线程要执行的任务之前调用。
	 */
	public ThreadInfo registerThread(){
		Thread currentThread = Thread.currentThread();
		LogUtil.i(TAG, "registerThread -->开始:tId="+currentThread.getId()+";tHCode="+currentThread.hashCode());
		synchronized(lock(currentThread.getId())){
			try{
				ThreadInfo threadInfo = mThreadInfoMap.get(currentThread.getId());
				if(threadInfo == null || threadInfo.getHashCode() != currentThread.hashCode()){
					threadInfo = new ThreadInfo(currentThread.getId(),currentThread.hashCode());
					mThreadInfoMap.put(currentThread.getId(), threadInfo);
				}
				threadInfo.setRegister(true);
				return ThreadInfo.copy(threadInfo);
			}finally{
				unlock(currentThread.getId());
				LogUtil.i(TAG, "registerThread -->结束:tId="+currentThread.getId()+";tHCode="+currentThread.hashCode());
			}
		}
	}
	/**
	  * 异步任务管理生命周期在此方法调用后结束，确保这个方法在线程要执行的任务之后调用。
	 */
	public void unregisterThread(){
		Thread currentThread = Thread.currentThread();
		LogUtil.i(TAG, "unregisterThread -->开始:tId="+currentThread.getId()+";tHCode="+currentThread.hashCode());
		synchronized(lock(currentThread.getId())){
			try{
				mThreadInfoMap.remove(currentThread.getId());
				mAsyncTaskMap.remove(currentThread.getId());
			}finally{
				unlock(currentThread.getId());
				LogUtil.i(TAG, "unregisterThread -->结束:tId="+currentThread.getId()+";tHCode="+currentThread.hashCode());
			}
		}
	}
	/**
	 * 注册任务
	 * @param httpTask
	 * @return 返回值是{@link #RESULT_NONSUPPORT}、{@link #RESULT_STOP}、{@link #RESULT_SUCCEED}  
	 */
	public int registerHttpTask(IAsyncTask httpTask){
		Thread currentThread = Thread.currentThread();
		LogUtil.i(TAG, "registerHttpTask -->开始:tId="+currentThread.getId()+";tHCode="+currentThread.hashCode());
		int result = RESULT_NONSUPPORT;
		synchronized(lock(currentThread.getId())){
			try{
				ThreadInfo threadInfo = mThreadInfoMap.get(currentThread.getId());
				if(threadInfo != null && threadInfo.getHashCode() == currentThread.hashCode()){
					if(threadInfo.getState() == ThreadInfo.STATE_STOP){
						result = RESULT_STOP;
						return result;
					}else{
						mAsyncTaskMap.put(currentThread.getId(), httpTask);
						result = RESULT_SUCCEED;
						return result;
					}
				}
				return result;
			}finally{
				unlock(currentThread.getId());
				LogUtil.i(TAG, "registerHttpTask -->结束:result="+result+";tId="+currentThread.getId()+";tHCode="+currentThread.hashCode());
			}
		}
	}
	
	public void cancelAsyncTask(ThreadInfo threadInfo){
		if(threadInfo == null){
			return;
		}
		cancelAsyncTask(threadInfo.getThreadId(),threadInfo.getHashCode());
	}

	public void cancelAsyncTask(final long threadId,final int threadHashCode){
		LogUtil.i(TAG, "cancelAsyncTask -->开始:tId="+threadId+";tHCode="+threadHashCode);
		synchronized(lock(threadId)){
			try{
				ThreadInfo threadInfo = mThreadInfoMap.get(threadId);
				if(threadInfo != null){
					if(threadInfo.isRegister() && !threadInfo.isCancel()){
						IAsyncTask asyncTask = mAsyncTaskMap.get(threadId);
						if(asyncTask != null){
							LogUtil.i(TAG, "cancelAsyncTask -->执行onCancel():tId="+threadId+";tHCode="+threadHashCode);
							asyncTask.onCancel();
							threadInfo.setCancel(true);
						}
					}
					threadInfo.setState(ThreadInfo.STATE_STOP);
				}
			}finally{
				unlock(threadId);
				LogUtil.i(TAG, "cancelAsyncTask -->结束:tId="+threadId+";tHCode="+threadHashCode);
			}
		}
	}
	
	private synchronized byte[] lock(long threadId){
		byte[] lock = mLockMap.get(threadId);
		if(lock == null){
			lock = new byte[0];
			mLockMap.put(threadId, lock);
		}
		return lock;
	}
	
	private synchronized void unlock(long threadId){
		mLockMap.remove(threadId);
	}

	public static interface IAsyncTask{
		public void onCancel();
	}

	public static class ThreadInfo{
		public static final int STATE_RUNING = 0;
		public static final int STATE_STOP = 1;
		private long mThreadId;
		private int mHashCode;
		private int mState;
		private boolean isRegister;
		private boolean isCancel;
		public ThreadInfo(long threadId,int hashCode){
			setHashCode(hashCode);
			setThreadId(threadId);
			setState(STATE_RUNING);
			setRegister(false);
			setCancel(false);
		}
		
		/**
		 * @return the isCancel
		 */
		public boolean isCancel() {
			return isCancel;
		}

		/**
		 * @param isCancel the isCancel to set
		 */
		public void setCancel(boolean isCancel) {
			this.isCancel = isCancel;
		}

		/**
		 * @return the isRegister
		 */
		private boolean isRegister() {
			return isRegister;
		}

		/**
		 * @param isRegister the isRegister to set
		 */
		private void setRegister(boolean isRegister) {
			this.isRegister = isRegister;
		}

		/**
		 * @return the threadId
		 */
		public long getThreadId() {
			return mThreadId;
		}

		/**
		 * @param threadId the threadId to set
		 */
		public void setThreadId(long threadId) {
			this.mThreadId = threadId;
		}
		/**
		 * @return the hashCode
		 */
		public int getHashCode() {
			return mHashCode;
		}
		/**
		 * @param hashCode the hashCode to set
		 */
		public void setHashCode(int hashCode) {
			this.mHashCode = hashCode;
		}
		/**
		 * @return the state
		 */
		private int getState() {
			return mState;
		}
		/**
		 * @param state the state to set
		 */
		private void setState(int state) {
			this.mState = state;
		}
		
		public static ThreadInfo copy(ThreadInfo threadInfo){
			if(threadInfo != null){
				threadInfo = new ThreadInfo(threadInfo.getThreadId(), threadInfo.getHashCode());
			}
			return threadInfo;
		}
	}
}
