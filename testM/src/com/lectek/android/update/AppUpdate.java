package com.lectek.android.update;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.view.View;
import android.widget.RemoteViews;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.net.http.AbsConnect;
import com.lectek.android.lereader.net.response.UpdateInfo;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.utils.Constants;

public class AppUpdate{
	public static void init(Context context){
		UpdateSetting updateSetting = new UpdateSetting();
		updateSetting.mApkSavePath = Constants.BOOKS_TEMP_SELF;//TODO:
		updateSetting.mHttpFactory = new HttpFactory();
		updateSetting.mNotification = new UpdateNotification();
		updateSetting.mUpdateActivityCls = UpdateActivity.class;
		UpdateManager.init(context, updateSetting);
	}
	
	private static class HttpFactory implements IHttpFactory{
		@Override
		public IHttpController createHttp(Context context, String url) {
			final DefaultHttpClient client = AbsConnect.getDefaultHttpClient(context);
			final HttpGet get = new HttpGet(url);
			return new IHttpController() {
				
				@Override
				public void shutdown() {
					if(client != null){
						client.getConnectionManager().shutdown();
					}
				}
				
				@Override
				public HttpResponse execute() throws Exception {
					return client.execute(get);
				}
			};
		}
	}
	
	private static class UpdateNotification implements INotification{
		@Override
		public Notification fillDownlaodInfoNotification(Context context,
				UpdateInfo updateInfo, Intent intent, long currentBytes,
				long totalBytes) {
			int progress = 0;
			
			if(totalBytes != 0 && totalBytes > 0){
				progress = (int) ( ( currentBytes * 1f / totalBytes ) * 100 );
			}
			Notification mNotification = new Notification();
			mNotification.icon = R.drawable.menu_item_download;
			mNotification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
			
			mNotification.contentView = new RemoteViews(context.getPackageName(), R.layout.notification_download_lay_a);
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
			
			mNotification.contentIntent = PendingIntent.getActivity(
					context,
					this.hashCode() + 1,
					intent,
					PendingIntent.FLAG_UPDATE_CURRENT); 
			return mNotification;
		}

		@Override
		public Notification fillDownloadFailNotification(Context context,
				UpdateInfo updateInfo, Intent intent) {
			String str = context.getString(R.string.update_download_fail_tip);
			Notification mNotification = new Notification();
			mNotification.icon = R.drawable.menu_item_download;// 显示图标   
			mNotification.tickerText = str;
			mNotification.defaults = Notification.DEFAULT_SOUND;// 默认声音   
			mNotification.flags = Notification.FLAG_AUTO_CANCEL;
			
	        PendingIntent contentIntent = PendingIntent.getActivity(
					context,
					this.hashCode(),   
					intent,  
					PendingIntent.FLAG_UPDATE_CURRENT);
	        
	        mNotification.setLatestEventInfo(context,
	        		str,
	        		context.getString(R.string.update_download_fail_click_tip),
					contentIntent);
	        
			return mNotification;
		}

		@Override
		public void notifyExitApp(Context context) {
			context.sendBroadcast(new Intent(AppBroadcast.ACTION_CLOSE_APP));
		}
	}
}
