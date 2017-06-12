package com.lectek.android.lereader.download;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;

import com.lectek.android.lereader.lib.download.DownloadUnitInfo;
import com.lectek.android.lereader.lib.download.HttpHandler;
import com.lectek.android.lereader.lib.download.OnDownloadListener;
import com.lectek.android.lereader.lib.download.HttpHandler.DownloadResultCode;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.permanent.DownloadAPI;
import com.lectek.android.lereader.permanent.DownloadConstants;

/**
 * 控制下载任务
 * @author linyiwei
 * @email 21551594@qq.com
 * @date 2011-11-1
 */
class DownloadManagement {
	private String TAG = "DownloadManagement";
	/**
	 * 表示下载是否可以开始
	 */
	private boolean isStart = true; 
	/**
	 * 待处理下载单元信息列表
	 */
	private LinkedList<DownloadUnitInfo> mDownloadUnits;
	/**
	 * 正在被线程处理的下载单元
	 */
	private HashMap<Long, DownloadUnitInfo> mDownloadTaskMap = new HashMap<Long, DownloadUnitInfo>();
	/**
	 * 当前所有线程集
	 */
	private ArrayList<DownloadThread> mThreadList = new ArrayList<DownloadThread>();
	/**
	 * 最大任务数
	 */
	private static int mMaxThreadSize = DownloadAPI.Setting.mMaxThreadSize;
	/**
	 * 允许处于等待状态的线程最大值
	 */
	private static int mMaxWaitThreadSize = DownloadAPI.Setting.mMaxWaitThreadSize;
	
	/**
	 * 服务器连接失败重连次数
	 */
	private static int mReconnect = DownloadAPI.Setting.mReconnectSize;
//	private static int mReconnect = 1;
	private static int mReconnectSleepTime = 1000;
	private Handler mHandler;
	private Context mContext ;
	
	private Handler mUpdateHandler = new Handler(){};
	
	DownloadManagement(Handler handler,Context context){
		mHandler = handler;
		mContext = context;
	}
	/**
	 * 结束所有线程包括在等待的
	 */
	void stopDownload(){
		synchronized (mThreadList) {
			LogUtil.i(TAG,"停止所以下载任务");
			isStart = false;
			for(int j = 0 ;j < mThreadList.size(); j++){
				if(mThreadList.get(j).isWait()){
					mThreadList.get(j).restart();
				}
			}
		}
	}
	
	/**
	 * 开始并根据待处理下载单元启动线程
	 */
	void startDownload(){
		if(mDownloadUnits == null)return;
		int newTaskSize = getNewTaskSize(mDownloadUnits);
		LogUtil.i(TAG,"启动未执行任务");
		upateDownloadTask(newTaskSize);
	}
	
	/**
	 * 同步mDownloadUnits和数据库中的数据
	 * @param downloadUnits
	 */
	void updateDownloadUnits(LinkedList<DownloadUnitInfo> downloadUnits){
		if(downloadUnits == null)return;
		int size = 0;
		if(mDownloadUnits != null){
			size = mDownloadUnits.size();
		}
		LogUtil.i(TAG,"数据库记录更新了          ："+downloadUnits.size()  
				+" 任务池中的任务数"+ size +"  正在执行的任务数 "+ mDownloadTaskMap.size());
		if(mDownloadUnits == null){
			mDownloadUnits = downloadUnits;
		}else{
			for(int i = 0;i < downloadUnits.size() && downloadUnits.size() > 0;i++){
				LogUtil.i(TAG," i "+ i +" size "+downloadUnits.size());
				LogUtil.i(TAG,"数据库记录更新     内容  ID： "+ downloadUnits.get(i).mID 
						+" 状态： "+downloadUnits.get(i).mState +" isDelete "+  downloadUnits.get(i).isDelete);
				if(mDownloadTaskMap.containsKey(downloadUnits.get(i).mID)){
					DownloadUnitInfo downloadUnitInfo  = mDownloadTaskMap.get(downloadUnits.get(i).mID);
					if(downloadUnitInfo != null){
						synchronized (downloadUnitInfo) {
							LogUtil.i(TAG,"数据库记录更新     更新记录正在被处理    ID： "+downloadUnits.get(i).mID);
							downloadUnitInfo.update(downloadUnits.remove(i));
							i--;
						}
					}
				}else{
					synchronized (mDownloadUnits) {
						int index = mDownloadUnits.indexOf( downloadUnits.get(i) ) ;
						if(index != -1){
							mDownloadUnits.get( index ).update( downloadUnits.get(i) );
						}else{
							mDownloadUnits.add(downloadUnits.get(i));
						}
					}
				}
			}
		}
		LogUtil.i(TAG,"数据库记录更新了     同步数据完成");
		if(downloadUnits.size() > 0){
			upateDownloadTask(getNewTaskSize(mDownloadUnits));
		}
	}
	/**
	 * 根据待处理下载单元启动线程
	 * @param newTaskSize
	 */
	private void upateDownloadTask(int newTaskSize){
		synchronized (mThreadList) {
			LogUtil.i(TAG,"判断是否需要启动线程          需要处理任务数："+newTaskSize);
			for(int i = 0 ;i < newTaskSize; i++){
				if(!openWaitThread()){
					if(mThreadList.size() < mMaxThreadSize){
						DownloadThread downloadThread = new DownloadThread(new DownloadRunnable());
						downloadThread.start();
						mThreadList.add(downloadThread);
						LogUtil.i(TAG,"启动新的线程");
					}
				}
			}
		}
	}
	private boolean openWaitThread(){
		for(int j = 0 ;j < mThreadList.size(); j++){
			if(mThreadList.get(j).isWait()){
				mThreadList.get(j).restart();
				LogUtil.i(TAG,"重启一条线程");
				return true;
			}
		}
		return false;
	}
	/**
	 * 判断是否是待处理事务
	 * @param downloadUnit
	 * @return
	 */
	private boolean isTask(DownloadUnitInfo downloadUnit){
		if( ( downloadUnit.isDelete || downloadUnit.mState != DownloadAPI.STATE_FINISH )
						&& !mDownloadTaskMap.containsKey(downloadUnit.mID) 
						&& downloadUnit.mState != DownloadAPI.STATE_FAIL 
						&& downloadUnit.mState != DownloadAPI.STATE_FAIL_OUT_MEMORY){
			return true;
		}
		return false;
	}
	/**
	 * 赛选待处理下载单元
	 * @param downloadUnits
	 * @return
	 */
	private DownloadUnitInfo getNewTask(LinkedList<DownloadUnitInfo> downloadUnits){
		if(downloadUnits == null){
			return null ;
		}
		synchronized (downloadUnits) {
			for(int i = 0;i < downloadUnits.size();i++){
				if(isTask(downloadUnits.get(i))){		
						return downloadUnits.remove(i);
				}
			}
		}
		return null;
	}
	/**
	 * 计算新增待处理下载单元个数
	 * @param downloadUnits
	 * @return
	 */
	private int getNewTaskSize(LinkedList<DownloadUnitInfo> downloadUnits){
		if(downloadUnits == null){
			return 0 ;
		}
		int size = 0;
		for(int i = 0;i < downloadUnits.size();i++){
			if(isTask(downloadUnits.get(i))){
				size ++;
			}
		}
		return size;
	}
	/**
	 * 删除下载单元
	 * @param downloadUnitInfo
	 */
	private void deleteDownloadUnit(DownloadUnitInfo downloadUnitInfo){
		if(downloadUnitInfo == null){
			return;
		}
        if(!TextUtils.isEmpty(downloadUnitInfo.mFilePath)){
            File file = new File(downloadUnitInfo.mFilePath);
            if(file.isFile()){
                file.delete();
            }
            int index = downloadUnitInfo.mFilePath.lastIndexOf(".");
            if(index != -1){
                file = new File(downloadUnitInfo.mFilePath.subSequence(0, index) + ".tmp");
                if(file.isFile()){
                    file.delete();
                }
            }
        }
        LogUtil.i(TAG, "执行删除       下载单元 ID :"+downloadUnitInfo.mID);
		Message msg = new Message();
		msg.what = DownloadConstants.WHAT_DELETE_DOWNLOAD_UNITS;
		msg.obj = downloadUnitInfo;
		mHandler.sendMessage(msg);
		
	}
	/**
	 * 通知service 下载进度变化
	 */
	private void notifyDownloadProgressChange(DownloadUnitInfo downloadUnitInfo){
		if(downloadUnitInfo == null){
			return;
		}
		Message msg = new Message();
		msg.what = DownloadConstants.WHAT_ON_DOWNLOAD_PROGRESS_CHANGE;
		msg.obj = downloadUnitInfo;
		mHandler.sendMessage(msg);
	}
	/**
	 * 保存下载单元
	 * @param downloadUnitInfo
	 */
	private void saveDownloadUnit(DownloadUnitInfo downloadUnitInfo){
		
		if(downloadUnitInfo == null){
			return;
		}
		LogUtil.i(TAG,"执行保存     下载单元  ID:"+downloadUnitInfo.mID +" 状态："+downloadUnitInfo.mState
				+" 已下载大小："+downloadUnitInfo.mFileByteCurrentSize+" 文件大小："+downloadUnitInfo.mFileByteSize);
		Message msg = new Message();
		msg.what = DownloadConstants.WHAT_SAVE_DOWNLOAD;
		msg.obj = downloadUnitInfo;
		mHandler.sendMessage(msg);
	}
	/**
	 * 针对每个下载单元定时发送广播和保存状态更新数据库
	 * @author Administrator
	 *
	 */
	private class UpdateRunnable implements Runnable{
		long mId;
		long oldSaveSize = 0;
		long oldUpdateSize = 0;
		long time = 0;
		long updateDelayed = 1000;
		long saveDelayed = 2000;
		private UpdateRunnable(long id){
			mId = id;
		}
		@Override
		public void run() {
			if(!mDownloadTaskMap.containsKey(mId)){
				LogUtil.i("UpdateRunnable","退出定时刷新  ID： " + mId);
				return;
			}
			DownloadUnitInfo downloadUnitInfo = mDownloadTaskMap.get(mId);
			if(downloadUnitInfo == null){
				LogUtil.i("UpdateRunnable","退出定时刷新  ID： " + mId);
				return;
			}
			if(downloadUnitInfo.mState != DownloadAPI.STATE_START && downloadUnitInfo.mState != DownloadAPI.STATE_STARTING){
				LogUtil.i("UpdateRunnable","退出定时刷新  ID： " + mId);
				return;
			}
			if(downloadUnitInfo.mFileByteCurrentSize < oldSaveSize
					|| downloadUnitInfo.mFileByteCurrentSize < oldUpdateSize){
				oldSaveSize = downloadUnitInfo.mFileByteCurrentSize;
				oldUpdateSize = downloadUnitInfo.mFileByteCurrentSize;
			}
			if(time >= saveDelayed){
				if(oldSaveSize <  downloadUnitInfo.mFileByteCurrentSize){
					LogUtil.i("UpdateRunnable","定时 :"+time+"后保存下载单元  ID： " + mId);
					saveDownloadUnit(downloadUnitInfo);
					oldSaveSize = downloadUnitInfo.mFileByteCurrentSize;
				}
				time = -updateDelayed;
			}
			if(isNeedsUpdate(downloadUnitInfo.mFileByteCurrentSize,oldUpdateSize,downloadUnitInfo.mFileByteSize,100)){
				if(downloadUnitInfo.mFileByteSize != 0){
					notifyDownloadProgressChange(downloadUnitInfo);
					oldUpdateSize = downloadUnitInfo.mFileByteCurrentSize;
				}
			}
			time += updateDelayed;
			mUpdateHandler.postDelayed(this, updateDelayed);
		}
	}
	private class DownloadRunnable implements Runnable{
		@Override
		public void run() {
			while(isStart){
				DownloadUnitInfo downloadUnitInfo = null;
				synchronized (mDownloadUnits) {
					synchronized (mDownloadTaskMap) {
						downloadUnitInfo = getNewTask(mDownloadUnits);
						if(downloadUnitInfo == null){
							LogUtil.i("DownloadService 退出 DownloadRunnable"+this);
							return;
						}
						LogUtil.i(TAG,"开始处理        下载单元ID："+downloadUnitInfo.mID +"状态 ："+downloadUnitInfo.mState
								+" 当前大小 ："+downloadUnitInfo.mFileByteCurrentSize+" 文件大小 :"+downloadUnitInfo.mFileByteSize);
						mDownloadTaskMap.put(downloadUnitInfo.mID, downloadUnitInfo);
					}
				}
				if(downloadUnitInfo.isDelete){
					deleteDownloadUnit(downloadUnitInfo);
					LogUtil.i(TAG," 正在处理   (删除)下载单元ID："+downloadUnitInfo.mID);
					
				}
				else if(downloadUnitInfo.mFileByteCurrentSize == downloadUnitInfo.mFileByteSize 
						&& downloadUnitInfo.mFileByteSize != 0 && downloadUnitInfo.mState != DownloadAPI.STATE_FINISH){
					String path = downloadUnitInfo.mFilePath;
					File file = new File((String) path.subSequence(0, path.lastIndexOf(".")) + ".tmp");
					if(file.exists()){
						file.renameTo(new File(path));
						downloadUnitInfo.mState = DownloadAPI.STATE_FINISH;
						LogUtil.i(TAG," 正在处理       (修复,已完成下载单元但状态不是已完成)下载单元ID："+downloadUnitInfo.mID);
					}else{
						file = new File(path);
						if(file.exists()){
							downloadUnitInfo.mState = DownloadAPI.STATE_FINISH;
						}else{
							downloadUnitInfo.mState = DownloadAPI.STATE_FAIL;
						}
					}
					saveDownloadUnit(downloadUnitInfo);
					//TODO 修复功能还不完善
				}
				else if(( downloadUnitInfo.mState == DownloadAPI.STATE_STARTING || downloadUnitInfo.mState == DownloadAPI.STATE_START )
						&& !downloadUnitInfo.isDelete){
					
					LogUtil.i(TAG,"正在处理      (下载任务)下载单元ID："+downloadUnitInfo.mID);
					new UpdateRunnable(downloadUnitInfo.mID).run();
					downloadTask(downloadUnitInfo);
					if(downloadUnitInfo.isDelete){
						deleteDownloadUnit(downloadUnitInfo);
						LogUtil.i(TAG," 正在处理      (删除)下载单元ID："+downloadUnitInfo.mID);
					}
					
				}
				synchronized (mDownloadTaskMap) {
					LogUtil.i(TAG,"处理完成      下载单元 ID："+downloadUnitInfo.mID+"状态 ："+downloadUnitInfo.mState);
					mDownloadTaskMap.remove(downloadUnitInfo.mID);
				}

			}
		}
		
	} 
	
	private class DownloadThread extends Thread{
		private Runnable mRunnable;
		private boolean isWait = false;
		DownloadThread(Runnable runnable){
			mRunnable = runnable;
			this.setName("DownloadThread"+this);
		}
		@Override
		public void run() {
	        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
	        
			while(isStart){
				
				if(mRunnable != null)mRunnable.run();
				LogUtil.i(TAG,"判断线程是否进入等待 "+this);
				boolean isNeedWait = false;
				synchronized (mThreadList) {
					isNeedWait = getWaitThreadListSize(mThreadList) < mMaxWaitThreadSize;
				}
				if(isStart && isNeedWait){
					synchronized (this) {
						try {
							isWait = true;
							LogUtil.i(TAG,"判断线程是否进入等待        线程开始等待"+this);
							this.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
							isWait = false; 
						}
					}
				}else{
					synchronized (mThreadList) {
						mThreadList.remove(this);
					}
					LogUtil.i(TAG,"判断线程是否进入等待        结束线程"+this);
					return ;
				}
			}
		}
		
		boolean isWait(){
			return isWait;
		}
		void restart(){
			synchronized (this) {
				LogUtil.i(TAG," 线程被重启"+this);
				this.notify();
				isWait = false; 
			}
		}
	}
	
	/**
	 * 计算处于等待状态的线程数
	 * @param mThreadList
	 * @return
	 */
	private int getWaitThreadListSize(ArrayList<DownloadThread> mThreadList){
		int size = 0;
		for(int j = 0 ;j < mThreadList.size(); j++){
			if(mThreadList.get(j).isWait()){
				size++;
			}
		}
		return size;
	}
	
	private class DownloadListener implements OnDownloadListener{

		@Override
		public void onDownloading(long id,long currentSize,long size) {
			DownloadUnitInfo downloadUnitInfo = mDownloadTaskMap.get(id);
			if(downloadUnitInfo == null){
				return;
			}
			if(size < currentSize){
				currentSize = size;
			}
			downloadUnitInfo.mFileByteCurrentSize = currentSize;
			downloadUnitInfo.mFileByteSize = size;
//			Cursor cursor = mContext.getContentResolver().query(DownloadConstants.CONTENT_URI, null, DownloadAPI._ID +" = "+downloadUnitInfo.mID, null, null);
//			if(cursor != null && cursor.moveToFirst()){
//				downloadUnitInfo.mState = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadAPI.STATE));
//				cursor.close();
//			}
			if(downloadUnitInfo.mState == DownloadAPI.STATE_START ){
				downloadUnitInfo.mState = DownloadAPI.STATE_STARTING;
				saveDownloadUnit(downloadUnitInfo);
				Intent intent = new Intent(DownloadAPI.ACTION_ON_DOWNLOAD_STATE_CHANGE);
				intent.putExtra(DownloadAPI.BROAD_CAST_DATA_KEY_ID,downloadUnitInfo.mID);
				intent.putExtra(DownloadAPI.BROAD_CAST_DATA_KEY_STATE,downloadUnitInfo.mState);
				mContext.sendBroadcast(intent);
			}
			notifyDownloadProgressChange(downloadUnitInfo);
		}

		@Override
		public boolean onDownloadProgressChange(long id, long currentSize,
				long size) {
			DownloadUnitInfo downloadUnitInfo = mDownloadTaskMap.get(id);
			if(downloadUnitInfo == null){
				LogUtil.i(TAG," 正在执行任务Map 找不到指定任务   异常终止 ID ："+id);
				return false;
			}
			if(size < currentSize){
				currentSize = size;
			}
			downloadUnitInfo.mFileByteCurrentSize = currentSize;
			downloadUnitInfo.mFileByteSize = size;
		
//			if(size == currentSize && size != 0 ){
//				downloadUnitInfo.mState = DownloadAPI.STATE_FINISH;
//			}
//			long oldSize = downloadUnitInfo.oldSize;
//			if(isNeedsUpdate(currentSize,oldSize,size)){
//				notifyDownloadProgressChange(downloadUnitInfo);
//				downloadUnitInfo.oldSize = currentSize;
//			}
			if( ( downloadUnitInfo.mState != DownloadAPI.STATE_START && downloadUnitInfo.mState != DownloadAPI.STATE_STARTING )
					|| downloadUnitInfo.isDelete || !isStart){
				if(downloadUnitInfo.mState != DownloadAPI.STATE_FINISH){
					LogUtil.i(TAG," 任务被用户终止  ID ："+ id + "mState :" +downloadUnitInfo.mState+ "isDelete :" +downloadUnitInfo.isDelete);
				}
//				saveDownloadUnit(downloadUnitInfo);
				notifyDownloadProgressChange(downloadUnitInfo);
				LogUtil.i(TAG,"状态修改     并准备退出 ID："+downloadUnitInfo.mID + "mState :" +downloadUnitInfo.mState);
				return false;
			}
			return true;
		}
		@Override
		public void onDownloadErr(long id,Exception e) {
			DownloadUnitInfo downloadUnitInfo = mDownloadTaskMap.get(id);
			if(downloadUnitInfo == null){
				return ;
			}
//			if(mReconnect == 1){
//				downloadUnitInfo.mState = DownloadAPI.STATE_FAIL;
//				saveDownloadUnit(downloadUnitInfo);
//			}
			if(e != null){
				LogUtil.i(TAG,"下载时出现错误     ID："+downloadUnitInfo.mID + 
						"    curSize :" +downloadUnitInfo.mFileByteCurrentSize  + "  size  :" + downloadUnitInfo.mFileByteSize
						+ "\n" +e.getMessage());
			}
		}

		@Override
		public void onDownloadFinish(long id) {
			DownloadUnitInfo downloadUnitInfo = mDownloadTaskMap.get(id);
			if(downloadUnitInfo == null){
				LogUtil.i(TAG," 正在执行任务Map 找不到指定任务   异常终止 ID ："+id);
				return ;
			}
			downloadUnitInfo.mState = DownloadAPI.STATE_FINISH;
		}
		
	}
	private boolean isNeedsUpdate(long currentSize,long oldSize,long size,int n){
		if(currentSize - oldSize > size/n || currentSize == size)return true;
		return false;
	}

	private void downloadTask(DownloadUnitInfo downloadUnitInfo){
		HttpHandler mHttpHandler = getInstance();
		if(mHttpHandler == null){
			return;
		}
		mHttpHandler.setOnDownloadListener(new DownloadListener());
		int res = -1;
		for(int i = 0 ;i < mReconnect;i++){
			if(i != 0){
				LogUtil.i(TAG,"第"+i+"次重试下载任务 ID ："+downloadUnitInfo.mID);
			}
			LogUtil.i(TAG,"开始下载文件  ID ："+ downloadUnitInfo.mID);
			res = mHttpHandler.startDownload(downloadUnitInfo.mDownloadUrl, downloadUnitInfo.mFilePath,downloadUnitInfo.mFileByteCurrentSize,
					downloadUnitInfo.mFileByteSize,downloadUnitInfo.mID);
			if(res == HttpHandler.DownloadResultCode.DOWNLOAD_RES_CODE_SUCCESS){
				LogUtil.i(TAG,"退出下载文件  正常退出  ID ："+ downloadUnitInfo.mID);
				break;
			}else{
				if(res == HttpHandler.DownloadResultCode.DOWNLOAD_RES_CODE_FAILURE_FILE_OUT_MEMORY){
					LogUtil.i(TAG,"退出下载文件 内存不足");
					break;
				}
				LogUtil.i(TAG,"退出下载文件   错误退出     文件下载任务 失败  ID ："+downloadUnitInfo.mID  +" 返回值： " +res);
				if(mReconnect > 1){
					try {
						Thread.sleep(mReconnectSleepTime);
					} catch (InterruptedException e) {}
				}
			}
		}
		if(res != HttpHandler.DownloadResultCode.DOWNLOAD_RES_CODE_SUCCESS){
			if(res == HttpHandler.DownloadResultCode.DOWNLOAD_RES_CODE_FAILURE_FILE_OUT_MEMORY){
				downloadUnitInfo.mState = DownloadAPI.STATE_FAIL_OUT_MEMORY;
			}else{
				downloadUnitInfo.mState = DownloadAPI.STATE_FAIL;
			}
		}
		saveDownloadUnit(downloadUnitInfo);
	}
	
	public static HttpHandler getInstance(){
		if(DownloadAPI.Setting.mHttpHandler == null){
			return null;
		}
		try {
			return (HttpHandler) DownloadAPI.Setting.mHttpHandler.newInstance();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	};
}
