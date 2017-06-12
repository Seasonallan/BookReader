package com.lectek.android.update;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;

import com.lectek.android.lereader.net.response.UpdateInfo;
import com.lectek.android.update.IHttpFactory.IHttpController;

/**
 * 升级模块下载线程
 * @author lyw
 */
class UpdateSelfThread extends Thread {
	public static final int HANDLER_MSG_DOWNLOAD_RUNNING = 1;
	public static final int HANDLER_MSG_DOWNLOAD_COMPLETE = 2;
	public static final int HANDLER_MSG_DOWNLOAD_FAILED = 3;
	public static final int HANDLER_MSG_THREAD_STOP = 4;
	
	private static final int NOTIFICAtiON_ID = 0;
	private static final String TAG = UpdateSelfThread.class.getSimpleName();
	
	private Context mContext;
	private UpdateInfo mUpdateInfo;
	private Handler mExternalHandler;
	private Handler mMainHandler;
	private boolean isStop = true;
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private boolean isNotifyView = true;
	private long mCurrentBytes;
	private long mTotalBytes;
	
	public UpdateSelfThread(Context context, UpdateInfo updateInfo,Handler externalHandler) {
		dispatchStart(context, updateInfo,externalHandler);
	}
	
	private void dispatchStart(Context context, UpdateInfo updateInfo,Handler externalHandler){
		this.mContext = context.getApplicationContext();
		this.mUpdateInfo = updateInfo;
		this.mMainHandler = new Handler(Looper.getMainLooper());
		this.mExternalHandler = externalHandler;
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		onStart();
	}
	
	public void setNotifyView(final boolean isNotifyView){
		runInUiThread(new Runnable() {
			@Override
			public void run() {
				UpdateSelfThread.this.isNotifyView = isNotifyView;
			}
		});
	}
	
	private void dispatchStop(){
		cancelNotification();
		mExternalHandler.sendEmptyMessage(HANDLER_MSG_THREAD_STOP);
		onStop();
	}
	
	protected void onStart(){
		
	}

	protected void onStop(){
		
	}
	
	@Override
	public synchronized void start() {
		isStop = false;
		super.start();
	}

	public long getCurrentBytes(){
		return mCurrentBytes;
	}
	
	public long getTotalBytes(){
		return mTotalBytes;
	}

	@Override
	public void run() {
		onDownloadProgressChange(0 , mUpdateInfo.getUpdateSize());
		String filePath = UpdateUtil.getUpdateApkPath(mContext,mUpdateInfo);
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		InputStream is = null;
		PowerManager.WakeLock wakeLock = null;
		File file = null;
		file = new File(filePath + UpdateUtil.DOWNLOAD_POST_NAME);
		IHttpFactory httpFactory = UpdateManager.getUpdateSetting().mHttpFactory;
		IHttpController httpController = null;
		try {
			mCurrentBytes = 0;
			mTotalBytes = 0;

			PowerManager pm = (PowerManager) mContext
					.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
			wakeLock.acquire();
			httpController = httpFactory.createHttp(mContext,mUpdateInfo.getUpdateURL());
			HttpResponse rp = httpController.execute();
			// for(Header header : rp.getAllHeaders()){
			// LogUtil.v(Tag, "respone header: " + header.getName() + " value: "
			// + header.getValue());
			// }
			int statusCode = rp.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK
					|| statusCode == HttpStatus.SC_PARTIAL_CONTENT) {
				Header h = rp.getFirstHeader("Content-Length");
				if (h != null) {
					mTotalBytes = Integer.valueOf(h.getValue());
				}
				if(mTotalBytes < 0){
					mTotalBytes = 0;
				}
				is = rp.getEntity().getContent();
				BufferedInputStream bis = new BufferedInputStream(is);

				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();

				if (file.isFile()) {
					RandomAccessFile oSavedFile = new RandomAccessFile(file,
							"rw");
					oSavedFile.seek(0);
					int bufferSize = 4096;
					byte[] b = new byte[bufferSize];
					int nRead;
					long bytesNotified = mCurrentBytes;
					long timeLastNotification = 0;
					for (;;) {
						nRead = bis.read(b, 0, bufferSize);

						if (nRead == -1) {
							break;
						}
						mCurrentBytes += nRead;
						oSavedFile.write(b, 0, nRead);
						long now = System.currentTimeMillis();
						if (mCurrentBytes - bytesNotified > 4096
								&& now - timeLastNotification > 1500) {
							onDownloadProgressChange(mCurrentBytes , mTotalBytes);
							bytesNotified = mCurrentBytes;
							timeLastNotification = now;
						}
						Thread.sleep(10L);
					}
					oSavedFile.close();
					if (( mTotalBytes != 0 && mCurrentBytes == mTotalBytes) || ( mTotalBytes == 0 && nRead == -1 ) ) {// 下载完成
						file.renameTo(new File(filePath));
						if(UpdateUtil.checkApkIntegrity(mContext,filePath)){
							Runtime.getRuntime().exec("chmod 666 " + filePath)
							.waitFor();// 改变一下权限
							onDownloadSucceed();
						}else{
							onDownloadFail();
						}
					}else{// 下载没完全，即下载失败
						onDownloadFail();
					}
					UpdateUtil.clearOldUpdateApk(mContext,mUpdateInfo);
				}
			} else {// 没找到下载文件
				onDownloadFail();
			}
		} catch (InterruptedException ie) {
			if(!isStop){
				onDownloadFail();
			}
		} catch (Exception e) {
			onDownloadFail();
		} finally {
			if (wakeLock != null) {
				wakeLock.release();
				wakeLock = null;
			}
			if (is != null) {
				try {
					is.close();
					is = null;
				} catch (IOException e) {
				}
			}
			if (httpController != null) {
				httpController.shutdown();
			}
		}
		isStop = true;
		dispatchStop();
	}
	
	protected void onDownloadFail(){
		if(UpdateUtil.isDownloadUpdateApk(mContext,mUpdateInfo)){
			return;
		}
		runInUiThread(new Runnable() {
			@Override
			public void run() {
				if(!isNotifyView){
					UpdateSetting updateSetting = UpdateManager.getUpdateSetting();
					Intent intent = new Intent(mContext,updateSetting.mUpdateActivityCls);
					intent.putExtra(UpdateManager.EXTRA_ACTION_TYPE, UpdateManager.VALUE_ACTION_TYPE_UPDATE_DOWNLOAD_FAIL);
					intent.putExtra(UpdateManager.EXTRA_UPDATE_INFO, mUpdateInfo);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mNotification = updateSetting.mNotification.fillDownloadFailNotification(mContext,mUpdateInfo,intent);
					recoverNotification();
				}else{
					mExternalHandler.sendEmptyMessage(HANDLER_MSG_DOWNLOAD_FAILED);
				}
				mNotification = null;
			}
		});
	}
	
	protected void onDownloadProgressChange(final long currentBytes,final long totalBytes){
		runInUiThread(new Runnable() {
			@Override
			public void run() {
				UpdateSetting updateSetting = UpdateManager.getUpdateSetting();
				Intent intent = new Intent(mContext,updateSetting.mUpdateActivityCls);
				intent.putExtra(UpdateManager.EXTRA_ACTION_TYPE, UpdateManager.VALUE_ACTION_TYPE_UPDATE_DOWNLOAD_INFO);
				intent.putExtra(UpdateManager.EXTRA_UPDATE_INFO, mUpdateInfo);
				intent.putExtra(UpdateManager.EXTRA_UPDATE_DOWNLAOD_CURRENT_BYTES, currentBytes);
				intent.putExtra(UpdateManager.EXTRA_UPDATE_DOWNLAOD_TOTAL_BYTES, totalBytes);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mNotification = updateSetting.mNotification.fillDownlaodInfoNotification(mContext,mUpdateInfo,intent, currentBytes, totalBytes);
				if(!isNotifyView){
					recoverNotification();
				}else{
					Message msg = new Message();
					msg.what = HANDLER_MSG_DOWNLOAD_RUNNING;
					msg.arg1 = (int) currentBytes;
					msg.arg2 = (int) totalBytes;
					mExternalHandler.sendMessage(msg);
				}
			}
		});
	}
	
	protected void onDownloadSucceed(){
		runInUiThread(new Runnable() {
			
			@Override
			public void run() {
				cancelNotification();
				if(!isNotifyView){
					UpdateManager.getInstance().startInstallUpdate(mUpdateInfo);
				}else{
					mExternalHandler.sendEmptyMessage(HANDLER_MSG_DOWNLOAD_COMPLETE);
				}
				mNotification = null;
			}
		});
	}
	
	public void stopDownlaod(){
		if(isStop){
			return;
		}
		isStop = true;
		mNotification = null;
		this.interrupt();
	}
	
	public boolean isStop(){
		return isStop;
	}
	
	public static void cancelNotification(Context context){
		((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(TAG, NOTIFICAtiON_ID);
	}
	
	public void cancelNotification(){
		try{
			mNotificationManager.cancel(TAG, NOTIFICAtiON_ID);
		}catch (Exception e) {}
	}
	
	public void recoverNotification(){
		try{
			if(mNotification != null && !isStop){
				mNotificationManager.notify(TAG, NOTIFICAtiON_ID, mNotification);
			}
		}catch (Exception e) {}
	}
	
	private void runInUiThread(Runnable runnable){
		if(runnable == null){
			return;
		}
		if(Thread.currentThread() == Looper.getMainLooper().getThread()){
			runnable.run();
		}else{
			mMainHandler.post(runnable);
		}
	}
}
