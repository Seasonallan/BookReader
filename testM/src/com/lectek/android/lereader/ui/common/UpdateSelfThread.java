package com.lectek.android.lereader.ui.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.RemoteViews;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.lib.net.http.AbsConnect;
import com.lectek.android.lereader.lib.net.http.HttpUtil;
import com.lectek.android.lereader.lib.utils.FileUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.common.UpdateDialogActivity.UpdateInfo;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.Constants;
import com.lectek.android.update.ClientInfo;

/**
 * 更新模块
 * 
 * @author mingkg21
 * @date 2010-5-6
 * @email mingkg21@gmail.com
 */
public class UpdateSelfThread extends Thread {

	private static final String TAG = UpdateSelfThread.class.getSimpleName();
	private static final String DOWNLOAD_POST_PATH = ".temp";
	private static final int NOTIFICAtiON_ID = 0;
	
	private Context context;
	private ClientInfo clientInfo;
	private Handler externalHandler;
	private Handler handler;
	private Notification mNotification;
	private NotificationManager mNotificationManager;
	private BroadcastReceiver mBroadcastReceiver;
	private boolean isStop = true;
	
	public UpdateSelfThread(Context context, ClientInfo clientInfo,
			Handler handler) {
		dispatchStart(context, clientInfo, handler);
	}

	public UpdateSelfThread(ClientInfo clientInfo) {
		dispatchStart(MyAndroidApplication.getInstance(), clientInfo, null);
	}
	
	private void dispatchStart(Context context, ClientInfo clientInfo,Handler handler){
		this.context = context.getApplicationContext();
		this.clientInfo = clientInfo;
		this.externalHandler = handler;
		this.handler = new Handler(Looper.getMainLooper());
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mBroadcastReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				stopDownlaod();
			}
		};
		this.context.registerReceiver(mBroadcastReceiver, new IntentFilter(AppBroadcast.ACTION_CLOSE_APP));
		onStart();
	}
	
	private void dispatchStop(){
		context.unregisterReceiver(mBroadcastReceiver);
		onStop();
	}
	
	protected void onStart(){
		
	}

	protected void onStop(){
		
	}
	
	public static void clearOldUpdateApk(ClientInfo clientInfo){
		if (FileUtil.isSDcardExist()) {// SDCARD
			String filePath = Constants.BOOKS_TEMP_SELF;
			searchAndclearOldUpdateApk(filePath,clientInfo);
		} 
		String filePath = MyAndroidApplication.getInstance().getCacheDir() + File.separator;
		searchAndclearOldUpdateApk(filePath,clientInfo);
	}
	
	private static void searchAndclearOldUpdateApk(String filePath,ClientInfo clientInfo){
		if(clientInfo == null){
			return;
		}
		File file = new File(filePath);
		if(file.isDirectory()){
			File [] files = file.listFiles();
			if(files != null && files.length > 0){
				for(File fileTemp : files){
					if(fileTemp.isFile() && fileTemp.getName().lastIndexOf("SurfingReader.apk") != -1){
						if(fileTemp.getName().lastIndexOf(DOWNLOAD_POST_PATH) != -1 
								|| fileTemp.getName().lastIndexOf(clientInfo.updateVersion + "SurfingReader.apk") == -1){
							fileTemp.delete();
						}
					}
				}
			}
		}
	}
	
	public static String getUpdateApkPath(ClientInfo clientInfo){
		String filePath = "";
		if (FileUtil.isSDcardExist()) {// SDCARD
			FileUtil.createFileDir(Constants.BOOKS_TEMP_SELF);
			filePath = Constants.BOOKS_TEMP_SELF + clientInfo.updateVersion + "SurfingReader.apk";
		} else {// 包名/cache
			filePath = MyAndroidApplication.getInstance().getCacheDir() + File.separator
					+ clientInfo.updateVersion + "SurfingReader.apk";
		}
		return filePath;
	}
	
	public static boolean isDownloadUpdateApk(ClientInfo clientInfo){
		String filePath = getUpdateApkPath(clientInfo);
		File file = new File(filePath);
		if(file.isFile() && checkApkIntegrity(filePath)){
			return true;
		}
		return false;
	}
	
	public static void installUpdate(ClientInfo clientInfo){
		String filePath = getUpdateApkPath(clientInfo);
		if(isDownloadUpdateApk(clientInfo)){
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setDataAndType(Uri.parse("file://" + filePath),
					"application/vnd.android.package-archive");
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			MyAndroidApplication.getInstance().startActivity(i);
			PreferencesUtil.getInstance(MyAndroidApplication.getInstance())
			.setNewFunctionUpdate("1");
		}
	}
	
	@Override
	public synchronized void start() {
		isStop = false;
		super.start();
	}

	@Override
	public void run() {
		onDownloadProgressChange(0 , clientInfo.updateSize);
		
		String filePath = getUpdateApkPath(clientInfo);
		
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

		DefaultHttpClient client = null;
		InputStream is = null;
		PowerManager.WakeLock wakeLock = null;
		File file = null;
		file = new File(filePath + DOWNLOAD_POST_PATH);

		try {

			long currentBytes = 0;
			long totalBytes = 0;

			PowerManager pm = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
			wakeLock.acquire();

			client = AbsConnect.getDefaultHttpClient(context);
			// HttpGet get = new HttpGet(downloadInfo.url);
			HttpGet get = HttpUtil.getHttpGet(clientInfo.updateURL);
			// for(Header header : get.getAllHeaders()){
			// LogUtil.v(Tag, "reuest header: " + header.getName() + " value: "
			// + header.getValue());
			// }
			long tempTime = System.currentTimeMillis();
			HttpResponse rp = client.execute(get);
			// for(Header header : rp.getAllHeaders()){
			// LogUtil.v(Tag, "respone header: " + header.getName() + " value: "
			// + header.getValue());
			// }
			int statusCode = rp.getStatusLine().getStatusCode();
			LogUtil.v(TAG, "status code " + statusCode);
			if (statusCode == HttpStatus.SC_OK
					|| statusCode == HttpStatus.SC_PARTIAL_CONTENT) {
				Header h = rp.getFirstHeader("Content-Length");
				if (h != null) {
					totalBytes = Integer.valueOf(h.getValue());
				}
				if(totalBytes < 0){
					totalBytes = 0;
				}
				LogUtil.v(TAG, "total bytes " + totalBytes);
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
					long bytesNotified = currentBytes;
					long timeLastNotification = 0;
					for (;;) {
						nRead = bis.read(b, 0, bufferSize);

						if (nRead == -1) {
							break;
						}
						currentBytes += nRead;
						oSavedFile.write(b, 0, nRead);
						long now = System.currentTimeMillis();
						if (currentBytes - bytesNotified > 4096
								&& now - timeLastNotification > 1500) {
							onDownloadProgressChange(currentBytes , totalBytes);
							bytesNotified = currentBytes;
							timeLastNotification = now;
						}

						LogUtil.v(TAG, "current bytes " + currentBytes
								+ " total bytes " + totalBytes);
						Thread.sleep(10L);
					}

					CommonUtil.outLog("updateSelf", (int) totalBytes, tempTime);
					oSavedFile.close();
					// TODO 下载完成判断,是否完成
					if (( totalBytes != 0 && currentBytes == totalBytes) || ( totalBytes == 0 && nRead == -1 ) ) {// 下载完成
						file.renameTo(new File(filePath));
						if(checkApkIntegrity(filePath)){
							Runtime.getRuntime().exec("chmod 666 " + filePath)
							.waitFor();// 改变一下权限
							onDownloadSucceed(filePath);
						}else{
							onDownloadFail(filePath);
						}
					}else{// 下载没完全，即下载失败
						onDownloadFail(filePath);
					}
					clearOldUpdateApk(clientInfo);
				}
			} else {// 没找到下载文件
				onDownloadFail(filePath);
			}
		} catch (InterruptedException ie) {
			LogUtil.e(TAG, "download err ", ie);
			if(!isStop){
				onDownloadFail(filePath);
			}
		} catch (Exception e) {
			LogUtil.e(TAG, "download err ", e);
			onDownloadFail(filePath);
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
					LogUtil.e(TAG, "InputStream close is err", e);
				}
			}
			if (client != null) {
				client.getConnectionManager().shutdown();
				client = null;
			}
		}
		mNotification = null;
		isStop = true;
		dispatchStop();
	}
	
	protected void onDownloadFail(final String filePath){
		if(isDownloadUpdateApk(clientInfo)){
			return;
		}
		PreferencesUtil.getInstance(context).setShowUpdateAgain("", false);
		runInUiThread(new Runnable() {
			@Override
			public void run() {
				if(externalHandler == null){ 
					String str = context.getString(R.string.update_download_fail_tip);
					mNotification = new Notification();
					mNotification.icon = R.drawable.menu_item_download;// 显示图标   
					mNotification.tickerText = str;
					mNotification.defaults = Notification.DEFAULT_SOUND;// 默认声音   
					mNotification.flags = Notification.FLAG_AUTO_CANCEL;
					
					Intent intent = new Intent(context,UpdateDialogActivity.class);
					intent.putExtra(UpdateDialogActivity.EXTRA_ACTION_TYPE, UpdateDialogActivity.VALUE_ACTION_TYPE_UPDATE_DOWNLOAD_FAIL);
					intent.putExtra(UpdateDialogActivity.EXTRA_UPDATE_INFO, new UpdateInfo(clientInfo));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					
			        PendingIntent contentIntent = PendingIntent.getActivity(
							context,
							this.hashCode(),   
							intent,  
							PendingIntent.FLAG_UPDATE_CURRENT);
			        
			        mNotification.setLatestEventInfo(context,
			        		str,
			        		context.getString(R.string.update_download_fail_click_tip),
							contentIntent);
			        recoverNotification();
				}else{
					externalHandler.sendEmptyMessage(UpdateDialogActivity.HANDLER_MSG_DOWNLOAD_FAILED);
				}
				mNotification = null;
			}
		});
	}
	
	protected void onDownloadProgressChange(final long currentBytes,final long totalBytes){
		runInUiThread(new Runnable() {
			@Override
			public void run() {
				int progress = 0;
				
				if(totalBytes != 0 && totalBytes > 0){
					progress = (int) ( ( currentBytes * 1f / totalBytes ) * 100 );
				}
				mNotification = new Notification();
				mNotification.icon = R.drawable.menu_item_download;
				mNotification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
				
				mNotification.contentView = new RemoteViews(context.getPackageName(), R.layout.notification_download_lay);
				mNotification.contentView.setTextViewText(R.id.notification_download_title_tv,
						context.getString(R.string.update_client_tip));
				if(totalBytes > 0){
					mNotification.contentView.setProgressBar(R.id.notification_download_progress, 100, progress , false);
					mNotification.contentView.setViewVisibility(R.id.notification_download_progress, View.VISIBLE);
					mNotification.contentView.setViewVisibility(R.id.notification_download_content_tv, View.GONE);
				}else{
					mNotification.contentView.setViewVisibility(R.id.notification_download_content_tv, View.VISIBLE);
					mNotification.contentView.setTextViewText(R.id.notification_download_content_tv,Formatter.formatFileSize(context, currentBytes));
					mNotification.contentView.setViewVisibility(R.id.notification_download_progress, View.GONE);
				}
				Intent intent = new Intent(context,UpdateDialogActivity.class);
				intent.putExtra(UpdateDialogActivity.EXTRA_ACTION_TYPE, UpdateDialogActivity.VALUE_ACTION_TYPE_UPDATE_DOWNLOAD_INFO);
				intent.putExtra(UpdateDialogActivity.EXTRA_UPDATE_INFO, new UpdateInfo(clientInfo));
				intent.putExtra(UpdateDialogActivity.EXTRA_UPDATE_DOWNLAOD_CURRENT_BYTES, currentBytes);
				intent.putExtra(UpdateDialogActivity.EXTRA_UPDATE_DOWNLAOD_TOTAL_BYTES, totalBytes);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				
				mNotification.contentIntent = PendingIntent.getActivity(
						context,
						this.hashCode() + 1,
						intent,
						PendingIntent.FLAG_UPDATE_CURRENT); 
				
				if(externalHandler == null){
					recoverNotification();
				}else{
					Message msg = new Message();
					msg.what = UpdateDialogActivity.HANDLER_MSG_DOWNLOAD_RUNNING;
					msg.arg1 = (int) currentBytes;
					msg.arg2 = (int) totalBytes;
					externalHandler.sendMessage(msg);
				}
			}
		});
	}
	
	protected void onDownloadSucceed(final String filePath){
		runInUiThread(new Runnable() {
			
			@Override
			public void run() {
				cancelNotification();
				if(externalHandler == null){
					UpdateDialogActivity.startInstallUpdate( context, clientInfo);
				}else{
					externalHandler.sendEmptyMessage(UpdateDialogActivity.HANDLER_MSG_DOWNLOAD_COMPLETE);
				}
				mNotification = null;
			}
		});
	}
	
	public void setHandler(Handler handler){
		this.externalHandler = handler;
	}
	
	public void stopDownlaod(){
		if(isStop){
			return;
		}
		mNotification = null;
		isStop = true;
		this.interrupt();
		cancelNotification();
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
		handler.post(runnable);
//		runnable.run();
	}
	
	public static boolean checkApkIntegrity(String path){
		if(!TextUtils.isEmpty(path)){
			try{
				PackageInfo plocalObject = MyAndroidApplication.getInstance()
						.getPackageManager().getPackageArchiveInfo(path,PackageManager.GET_ACTIVITIES);
				if(plocalObject.activities.length > 0){
					return true;
				}
			}catch (Exception e) {}
		}
		return false;
	}
}
