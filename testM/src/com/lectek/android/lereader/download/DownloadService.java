package com.lectek.android.lereader.download;

import java.util.LinkedList;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.lectek.android.lereader.lib.download.DownloadUnitInfo;
import com.lectek.android.lereader.lib.download.IDownloadNotification;
import com.lectek.android.lereader.lib.utils.ApnUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.permanent.DownloadAPI;
import com.lectek.android.lereader.permanent.DownloadConstants;
/**
 * 
 * 主要负责：控制DownloadManagement、发送广播给UI、数据库操作、数据库变化监听
 * @author linyiwei
 * @email 21551594@qq.com
 * @date 2011-11-1
 */
public class DownloadService extends Service {
	private static final String TAG = "DownloadService";
	private Context _this = this;
	/**
	 * 下载信息变化监听
	 */
	private Observer mObserver;
	/**
	 * 系统时间被修改监听 
	 */
	private TimeListener mTimeListener;
	/**
	 * 下载管理
	 */
	private DownloadManagement mDownloadManagement;
	private NetWorkListener mNetWorkListener;

	/**
	 * 时间戳
	 */
	private static long mTimesTemp = 0;
	
	private IDownloadNotification mDownloadNotification;
	
	@Override
	public IBinder onBind(Intent arg0) {
		throw new UnsupportedOperationException("Cannot bind to Download Manager Service");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.i(TAG,"DownloadService onCreate() ");
		
		//监听时间下载信息数据库变化
		mObserver = new Observer(mHandler);
				
		//监听时间是否被设置，如果被设置重置时间戳
		mTimeListener = new TimeListener();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
		intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		this.registerReceiver(mTimeListener, intentFilter);
		
		mDownloadManagement = new DownloadManagement(mHandler,this);
//		mHandler.sendEmptyMessage(DownloadConstants.WHAT_LOAD_DOWNLOAD_UNITS);
		mNetWorkListener = new NetWorkListener();
		registerReceiver(mNetWorkListener , new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
		ContentValues values = new  ContentValues();
	 	values.put(DownloadAPI.STATE, DownloadAPI.STATE_PAUSE);
	 	this.getContentResolver().update(DownloadConstants.CONTENT_URI, values, 
	 			DownloadAPI.STATE +" != "+DownloadAPI.STATE_FINISH
                +" AND "+DownloadAPI.STATE +" != "+DownloadAPI.STATE_ONLINE
                +" AND "+DownloadAPI.STATE +" != "+DownloadAPI.STATE_FAIL
	 			+" AND "+DownloadAPI.STATE +" != "+DownloadAPI.STATE_FAIL_OUT_MEMORY, null);
	 	this.getContentResolver().registerContentObserver(DownloadAPI.CONTENT_URI, true, mObserver);
	 	mTimesTemp = 0;
	 	
	 	mDownloadNotification = DownloadAPI.Setting.mDownloadNotification;
	 	if(mDownloadNotification != null){
	 		mDownloadNotification.onCreate(_this);
	 	}
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		LogUtil.i(TAG,"DownloadService onStart() ");
		if(mDownloadNotification != null){
	 		mDownloadNotification.onStart(intent, startId);
	 	}
	}
	
	@Override
	public void onDestroy() {
		mDownloadManagement.stopDownload();
		mTimesTemp = 0;
		LogUtil.i(TAG,"DownloadService onDestroy() ");
		this.getContentResolver().unregisterContentObserver(mObserver);
		this.unregisterReceiver(mTimeListener);
		mDownloadManagement.stopDownload();
		unregisterReceiver(mNetWorkListener);
		super.onDestroy();
		if(mDownloadNotification != null){
	 		mDownloadNotification.onDestroy();
	 	}
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case DownloadConstants.WHAT_LOAD_DOWNLOAD_UNITS:
				new Thread("loadDownloadUnits"){
					@Override
					public void run() {
						mDownloadManagement.updateDownloadUnits(loadDownloadUnits());
					}
				}.start();
				break;
			case DownloadConstants.WHAT_DELETE_DOWNLOAD_UNITS:{
				DownloadUnitInfo data = (DownloadUnitInfo) msg.obj;
				_this.getContentResolver().delete(DownloadConstants.CONTENT_URI,DownloadAPI._ID + " = " + data.mID, null);
			}
				break;
			case DownloadConstants.WHAT_ON_DOWNLOAD_PROGRESS_CHANGE:{
				DownloadUnitInfo data = (DownloadUnitInfo) msg.obj;
				Intent intent = new Intent(DownloadAPI.ACTION_ON_DOWNLOAD_PROGRESS_CHANGE);
				intent.putExtra(DownloadAPI.BROAD_CAST_DATA_KEY_ID,data.mID);
				intent.putExtra(DownloadAPI.BROAD_CAST_DATA_KEY_FILE_BYTE_SIZE,data.mFileByteSize);
				intent.putExtra(DownloadAPI.BROAD_CAST_DATA_KEY_FILE_BYTE_CURRENT_SIZE,data.mFileByteCurrentSize);
				_this.sendBroadcast(intent);
			}
				break;
			case DownloadConstants.WHAT_SAVE_DOWNLOAD:{
				DownloadUnitInfo data = (DownloadUnitInfo) msg.obj;
				ContentValues contentValues = new ContentValues();
				contentValues.put(DownloadAPI.BROAD_CAST_DATA_KEY_FILE_BYTE_CURRENT_SIZE,data.mFileByteCurrentSize);
				contentValues.put(DownloadAPI.BROAD_CAST_DATA_KEY_FILE_BYTE_SIZE,data.mFileByteSize);
				contentValues.put(DownloadAPI.STATE,data.mState);
				_this.getContentResolver().update(DownloadConstants.CONTENT_URI,contentValues,DownloadAPI._ID + " = " + data.mID, null);
				if(data.mState == DownloadAPI.STATE_FINISH 
						|| data.mState == DownloadAPI.STATE_FAIL 
						|| data.mState == DownloadAPI.STATE_FAIL_OUT_MEMORY){
					Intent intent = new Intent(DownloadAPI.ACTION_ON_DOWNLOAD_STATE_CHANGE);
					intent.putExtra(DownloadAPI.BROAD_CAST_DATA_KEY_ID,data.mID);
					intent.putExtra(DownloadAPI.BROAD_CAST_DATA_KEY_STATE,data.mState);
					_this.sendBroadcast(intent);
					if(DownloadAPI.Setting.mToastNoticeRunnable != null && data.mState == DownloadAPI.STATE_FINISH){
						DownloadAPI.Setting.mToastNoticeRunnable.run(data.mFileName);
					}
					LogUtil.i(TAG,"发出广播通知UI  ID："+data.mID + "mState :" +data.mState);
				}
			}
				break;
			default:
				break;
			}
		}
		
	};
	/**
	 *  根据时间戳获取下载单元信息
	 * @return
	 */
	private LinkedList<DownloadUnitInfo> loadDownloadUnits(){
		
		LinkedList<DownloadUnitInfo> downloadUnits = new LinkedList<DownloadUnitInfo>();
		LogUtil.i(TAG,"DownloadService loadDownload() mTimesTemp"+mTimesTemp);
		Cursor cursor  = this.getContentResolver().query(DownloadConstants.CONTENT_URI, null, 
				DownloadAPI.TIMES_TAMP + " > " + mTimesTemp + " AND " + DownloadAPI.STATE + " != " + DownloadAPI.STATE_FINISH 
				+ " AND " + DownloadAPI.STATE + " != " + DownloadAPI.STATE_FAIL 
				+ " AND " + DownloadAPI.STATE + " != " + DownloadAPI.STATE_FAIL_OUT_MEMORY,
				null, DownloadAPI._ID + " DESC");
		long MaxTimesTemp = 0;
		if(cursor == null || cursor.getCount() == 0){
			LogUtil.i(TAG,"DownloadService loadDownload() "+cursor);
			if(cursor != null){
				cursor.close();
			}
			return downloadUnits;
		}else{
			LogUtil.i(TAG,"DownloadService loadDownload() Count" + cursor.getCount());
			while(cursor.moveToNext()){
				DownloadUnitInfo downloadUnitInfo = new DownloadUnitInfo(
						cursor.getLong(cursor.getColumnIndexOrThrow(DownloadAPI._ID)), 
						cursor.getString(cursor.getColumnIndexOrThrow(DownloadAPI.FILE_PATH)), 
						cursor.getString(cursor.getColumnIndexOrThrow(DownloadAPI.DOWNLOAD_URL)), 
						cursor.getInt(cursor.getColumnIndexOrThrow(DownloadAPI.STATE)), 
						cursor.getLong(cursor.getColumnIndexOrThrow(DownloadAPI.FILE_BYTE_SIZE)), 
						cursor.getLong(cursor.getColumnIndexOrThrow(DownloadAPI.FILE_BYTE_CURRENT_SIZE)), 
						cursor.getString(cursor.getColumnIndexOrThrow(DownloadAPI.FILE_NAME)), 
						cursor.getInt(cursor.getColumnIndexOrThrow(DownloadAPI.ACTION_TYPE)), 
						cursor.getInt(cursor.getColumnIndexOrThrow(DownloadAPI.DELETE))
						);
				if(downloadUnitInfo.mFilePath == null){
					downloadUnitInfo.mFilePath = "";
				}
				downloadUnits.add(downloadUnitInfo);
				long timesTemp = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadAPI.TIMES_TAMP));
				if(MaxTimesTemp < timesTemp){
					MaxTimesTemp = timesTemp;
				}
			}
			if(cursor != null){
				cursor.close();
			}
			mTimesTemp = MaxTimesTemp;
			return downloadUnits;
		}
	}
	private class TimeListener extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(mDownloadNotification != null){
		 		mDownloadNotification.onTimeSettingChange();
		 	}
			mTimesTemp = System.currentTimeMillis();
			LogUtil.i(TAG,"DownloadService onReceive() mTimesTemp:"+mTimesTemp);
		}
	}
	private class Observer extends ContentObserver{
		private Handler mHandler;
//		private RunSendMsg mRunnable;
		private long timeTemp = 0;
		public Observer(Handler handler) {
			super(handler);
			mHandler = handler;
			timeTemp = System.currentTimeMillis();
		}

		@Override
		public void onChange(boolean selfChange) {
			LogUtil.i(TAG, "DownloadService onChange() ");
//			long time = System.currentTimeMillis() - timeTemp;
//			if(time < 100 && time > 0){
//				if( mRunnable == null ){
//					mRunnable = new RunSendMsg();
//					mHandler.postDelayed(mRunnable, 200);
//				}else{
//					mHandler.removeCallbacks(mRunnable);
//					mHandler.postDelayed(mRunnable, 200);
//				}
//			}else{
//				if( mRunnable == null ){
//					mRunnable = new RunSendMsg();
//					mHandler.postDelayed(mRunnable, 200);
//				}
//			}
//			timeTemp = System.currentTimeMillis();
//		}
//		private class RunSendMsg implements Runnable{
//			@Override
//			public void run() {
				mHandler.sendEmptyMessage(DownloadConstants.WHAT_LOAD_DOWNLOAD_UNITS);
//				mRunnable = null;
//			}
		}
	}
	private class NetWorkListener extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent == null || context == null){
				return;
			}
			if(ApnUtil.isConnected(_this)){
        		mTimesTemp = 0;
    			mHandler.sendEmptyMessage(DownloadConstants.WHAT_LOAD_DOWNLOAD_UNITS);
        	}
		}  
	                          
	}
}
