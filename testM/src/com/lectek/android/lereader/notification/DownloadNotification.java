package com.lectek.android.lereader.notification;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.download.DownloadHttpEngine;
import com.lectek.android.lereader.lib.download.IDownloadNotification;
import com.lectek.android.lereader.lib.thread.ThreadFactory;
import com.lectek.android.lereader.permanent.DownloadAPI;
import com.lectek.android.lereader.ui.common.AppWidgetSplashActivity;
import com.lectek.android.lereader.ui.specific.ActivityChannels;
import com.lectek.android.lereader.ui.specific.MainActivity;
import com.lectek.android.lereader.utils.ToastUtil;
//import com.lectek.android.sfreader.data.ContentInfo;
//import com.lectek.bookformats.BookMetaInfo;
//import com.lectek.bookformats.FormatPlugin;

public class DownloadNotification implements IDownloadNotification{
	private static final String DOWNLOAD_NOTIFI_TAG = "DOWNLOAD_NOTIFI_TAG";
	private static final int RESIDENT_TIEM_ID = 0;
	public static final int AUTO_CANCEL_TIEM_ID_OFFSET = 1;

    private Context mContext;
    private Handler mHandler;

//	private DownloadListener mDownloadListener;
	private DownLoadInfoObserver mDownLoadInfoObserver;
	private NotificationManager mNotificationMgr;

	private ArrayList<DownloadInfo> mDatas = new ArrayList<DownloadNotification.DownloadInfo>();
	
	private int mStartSize = 0;
	
	private long mTimesTemp = 0;


    private Context getContext(){
        return mContext;
    }

	@Override
	public void onCreate(Context context) {
        mContext = context;
        mHandler = new Handler(Looper.getMainLooper());

//		mDownloadListener = new DownloadListener();
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(DownloadAPI.ACTION_ON_DOWNLOAD_PROGRESS_CHANGE);
//		filter.addAction(DownloadAPI.ACTION_ON_DOWNLOAD_STATE_CHANGE);//DownloadAPI.ACTION_ON_DOWNLOAD_DELETE
//		filter.addAction(DownloadAPI.ACTION_ON_DOWNLOAD_DELETE);
//		context.registerReceiver(mDownloadListener, filter);
		
		mDownLoadInfoObserver = new DownLoadInfoObserver(mHandler);
		context.getContentResolver().registerContentObserver(DownloadAPI.CONTENT_URI, true, mDownLoadInfoObserver);
		
		mTimesTemp = -1;
		
		mNotificationMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		loadData();
	}

	@Override
	public void onStart(Intent intent, int startId) {

	}

	@Override
	public void onDestroy() {
//		getContext().unregisterReceiver(mDownloadListener);
		getContext().getContentResolver().unregisterContentObserver(mDownLoadInfoObserver);
		cancelNotify();
	}
	
//	private void onDownloadStateChange(long id,int state){
//		if(getData(id) == null){
//			DownloadInfo downloadInfo = getDataFromDB(id);
//			if(downloadInfo != null){
//				mDatas.add(downloadInfo);
//			}
//		}
//		
//		if(state == DownloadAPI.STATE_FINISH){
//			onDownLoadFinish(id);
//		}
//		if(state == DownloadAPI.STATE_FAIL_OUT_MEMORY){
//			onDownLoadOutMemory(id);
//		}
//		updateState(id , state);
//	}
	
	private void updateState(long id,int state){
		for(DownloadInfo dataDownloadInfo : mDatas){
			if(dataDownloadInfo.id == id){
				dataDownloadInfo.state = state;
				break;
			}
		}
		computeDownloadStateSize();
	}
	
	private void onDeleteDownload(long [] ids){
		for(int i = 0 ; i < ids.length ; i++){
			long id = ids[i];
			for(int j = 0;j < mDatas.size() ;j++){
				DownloadInfo oldData = mDatas.get(j);

				if(oldData.id == id){
					mDatas.remove(j);
					if(oldData.state == DownloadAPI.STATE_FINISH){
						mNotificationMgr.cancel(DOWNLOAD_NOTIFI_TAG,(int) id + AUTO_CANCEL_TIEM_ID_OFFSET);
					}
				}
			}
		}
		computeDownloadStateSize();
	}
	
	private void onDownloadProgress(long id,float progress){
		if(mStartSize > 1){
			return;
		}
		
		if(getData(id) == null){
			DownloadInfo downloadInfo = getDataFromDB(id);
			if(downloadInfo != null){
				mDatas.add(downloadInfo);
			}
		}
		
		for(DownloadInfo dataDownloadInfo : mDatas){
			if(dataDownloadInfo.id == id){
				dataDownloadInfo.progress = progress;
				break;
			}
		}
		computeDownloadStateSize();
	}
	
	private void computeDownloadStateSize(){
		int pauseSize = 0;
		int failSize = 0;
		int finishedSize = 0;
		int startSize = 0;
		for(DownloadInfo dataDownloadInfo : mDatas){
			switch (dataDownloadInfo.state) {
			case DownloadAPI.STATE_FAIL:
				failSize ++;
				break;
			case DownloadAPI.STATE_FAIL_OUT_MEMORY:
				failSize ++;
				break;
			case DownloadAPI.STATE_PAUSE:
				pauseSize ++;
				break;
			case DownloadAPI.STATE_FINISH:
				finishedSize ++;
				break;
			case DownloadAPI.STATE_START:
				startSize ++;
				break;
			case DownloadAPI.STATE_STARTING:
				startSize ++;
				break;
			}
		}
//		onFillData(pauseSize, failSize, finishedSize, startSize);
		//FIXME:需求未明确，只是单纯跳转到书架。
		gotoBookShelf(pauseSize, failSize, finishedSize, startSize);
	}
	
	private void gotoBookShelf(int pauseSize, int failSize, int finishedSize,int startSize){
		mStartSize = startSize;
		Notification notification = new Notification();
		notification.icon = R.drawable.menu_item_download;
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        
        Intent intent = new Intent(getContext(),MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_NAME_GO_TO_VIEW, MainActivity.RESULT_CODE_GOTO_DOWNLOAD);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(
				getContext(),
				this.hashCode(),
				intent,
				PendingIntent.FLAG_UPDATE_CURRENT); 
        if(android.os.Build.VERSION.SDK_INT >= 8){
	        notification.contentView = new RemoteViews(getContext().getPackageName(), R.layout.notification_download_lay);
			if(startSize == 1){
				DownloadInfo data = getFirstStartItem();
		        notification.contentView.setViewVisibility(R.id.notification_download_content_tv, View.GONE);
		        notification.contentView.setTextViewText(R.id.notification_download_title_tv,
		        		getContext().getString(R.string.notification_download_start_single_tip,data.name));
	//	        notification.contentView.setTextViewText(R.id.notification_download_title_tv_post,
	//	        		getContext().getString(R.string.notification_download_start_single_tip_post));
		        notification.contentView.setProgressBar(R.id.notification_download_progress, 100, (int) (data.progress * 100), false);
			}else if(startSize > 1){
				notification.contentView.setViewVisibility(R.id.notification_download_progress, View.GONE);
		        notification.contentView.setTextViewText(R.id.notification_download_title_tv,
		        		getContext().getString(R.string.notification_download_start_tip,startSize));
		        notification.contentView.setTextViewText(R.id.notification_download_content_tv,
		        		getContext().getString(R.string.notification_download_info_tip,pauseSize,failSize,finishedSize));
			}else if(pauseSize > 0 || failSize > 0){
		        notification.contentView.setViewVisibility(R.id.notification_download_progress, View.GONE);
		        notification.contentView.setTextViewText(R.id.notification_download_title_tv,
		        		getContext().getString(R.string.notification_download_unfinish_tip,pauseSize,failSize));
		        notification.contentView.setTextViewText(R.id.notification_download_content_tv,
		        		getContext().getString(R.string.notification_download_click_tip));
			}else{
				mNotificationMgr.cancel(DOWNLOAD_NOTIFI_TAG,RESIDENT_TIEM_ID);
				return;
			}
	        notification.contentIntent = contentIntent;
        }else{
        	String contentTitle = "";
        	String contentText = "";
        	if(startSize > 0){
        		contentTitle = getContext().getString(R.string.notification_download_start_tip,startSize);
        		contentText = getContext().getString(R.string.notification_download_info_tip,pauseSize,failSize,finishedSize);
			}else if(pauseSize > 0 || failSize > 0){
				contentTitle = getContext().getString(R.string.notification_download_unfinish_tip,pauseSize,failSize);
        		contentText = getContext().getString(R.string.notification_download_click_tip);
			}else{
				mNotificationMgr.cancel(DOWNLOAD_NOTIFI_TAG,RESIDENT_TIEM_ID);
				return;
			}
        	notification.setLatestEventInfo(getContext(), contentTitle, contentText, contentIntent);
        }
        mNotificationMgr.notify(DOWNLOAD_NOTIFI_TAG,RESIDENT_TIEM_ID, notification);
		
	}

//	private Intent getGotoBookIntent(DownloadInfo data){
//		if(data.mimeType.equals(DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_VOICE)){
//			return gotoReadVoice(data);
//		}else{
//			return gotoReadBook(data);
//		}
//	}
	
	private Intent gotoReadVoice(DownloadInfo data){
		return ActivityChannels.getVoicePlayerIntent(getContext(), data.contentType, data.position, true);
	}
	
//	private Intent gotoReadBook(DownloadInfo data){
//		FormatPlugin fb = null;
//		try {
//			//fb = PluginManager.instance().getPlugin(data.filePathLocation);
//		} catch (Exception e) {}
//		if(fb == null){
//			return null;
//		}
//		Intent intent = new Intent(getContext(),AppWidgetSplashActivity.class);
//		intent.setAction(AppWidgetSplashActivity.ACTION_EXTERNAL_READ_BOOK);
//		BookMetaInfo bookMetaInfo = fb.getBookMetaInfo();
//		if(bookMetaInfo != null && !TextUtils.isEmpty(bookMetaInfo.type)){
//			intent.putExtra(AppWidgetSplashActivity.EXTRA_CONTENT_NAME,bookMetaInfo.bookTitle);
//			intent.putExtra(AppWidgetSplashActivity.EXTRA_CONTENT_ID,bookMetaInfo.contentId );
//			if(bookMetaInfo.bookType.equals(BookMetaInfo.CONTENT_TYPE_BOOKS)){
//				intent.putExtra(AppWidgetSplashActivity.EXTRA_CONTENT_TYPE,ContentInfo.CONTENT_TYPE_BOOK);
//			}else if(bookMetaInfo.bookType.equals(BookMetaInfo.CONTENT_TYPE_CARTOON)){
//				intent.putExtra(AppWidgetSplashActivity.EXTRA_CONTENT_TYPE,ContentInfo.CONTENT_TYPE_CARTOON);
//			}else if(bookMetaInfo.bookType.equals(BookMetaInfo.CONTENT_TYPE_MAGAZINE)){
//				if(bookMetaInfo.type.equals(BookMetaInfo.BOOK_META_INFO_STREAM)){
//					intent.putExtra(AppWidgetSplashActivity.EXTRA_CONTENT_TYPE,ContentInfo.CONTENT_TYPE_MAGAZINE_STREAM);
//				}else if(bookMetaInfo.type.equals(BookMetaInfo.BOOK_META_INFO_FORMAT)){
//					intent.putExtra(AppWidgetSplashActivity.EXTRA_CONTENT_TYPE,ContentInfo.CONTENT_TYPE_MAGAZINE);
//				}
//			}
//		}
//		fb.recyle();
//		return intent;
//	}
	
	protected void onDownLoadOutMemory(long id){
		DownloadInfo data = getData(id);//notification_download_fail_out_memory_tip
		if(data == null){
			return;
		}
		String contentStr = getContext().getString(R.string.sdcard_no_exist_Free_Not_Enough_tip);;
		ToastUtil.showToast(getContext(), contentStr);
	}
	
//	protected void onDownLoadFinish(long id){
//		DownloadInfo data = getData(id);
//		if(data == null){
//			return;
//		}
//        Intent intent = getGotoBookIntent(data);
//        if(intent != null){
//        	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            
//    		String str = getContext().getString(R.string.notification_download_finish_title,data.name);
//    		Notification notification = new Notification();
//    		notification.icon = R.drawable.menu_item_download;// 显示图标   
//    		notification.tickerText = str;
//            notification.defaults = Notification.DEFAULT_SOUND;// 默认声音   
//            notification.flags = Notification.FLAG_AUTO_CANCEL;
//            
//            PendingIntent contentIntent = PendingIntent.getActivity(
//    				getContext(),
//    				(int)id,
//    				intent,  
//    				PendingIntent.FLAG_UPDATE_CURRENT);
//            
//            notification.setLatestEventInfo(getContext()
//            		, getContext().getString(R.string.notification_download_finish_title,data.name)
//            		, getContext().getString(R.string.notification_download_finish_content)
//            		, contentIntent);
//            
//    		mNotificationMgr.notify(DOWNLOAD_NOTIFI_TAG,(int) id + AUTO_CANCEL_TIEM_ID_OFFSET, notification);
//        }
//	}
	
	private DownloadInfo getFirstStartItem(){
		for(DownloadInfo dataDownloadInfo : mDatas){
			if(dataDownloadInfo.state == DownloadAPI.STATE_START
					|| dataDownloadInfo.state == DownloadAPI.STATE_STARTING){
				return dataDownloadInfo;
			}
		}
		return null;	
	}
	
	protected void onFillData(int pauseSize, int failSize, int finishedSize,int startSize){
		mStartSize = startSize;
		Notification notification = new Notification();
		notification.icon = R.drawable.menu_item_download;
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        
        Intent intent = new Intent(getContext(),AppWidgetSplashActivity.class);
		intent.putExtra(AppWidgetSplashActivity.EXTRA_TYPE, AppWidgetSplashActivity.TYPE_VALUE_GOTO_DOWNLOAD);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(
				getContext(),
				this.hashCode(),
				intent,
				PendingIntent.FLAG_UPDATE_CURRENT); 
        if(android.os.Build.VERSION.SDK_INT >= 8){
	        notification.contentView = new RemoteViews(getContext().getPackageName(), R.layout.notification_download_lay);
			if(startSize == 1){
				DownloadInfo data = getFirstStartItem();
		        notification.contentView.setViewVisibility(R.id.notification_download_content_tv, View.GONE);
		        notification.contentView.setTextViewText(R.id.notification_download_title_tv,
		        		getContext().getString(R.string.notification_download_start_single_tip,data.name));
	//	        notification.contentView.setTextViewText(R.id.notification_download_title_tv_post,
	//	        		getContext().getString(R.string.notification_download_start_single_tip_post));
		        notification.contentView.setProgressBar(R.id.notification_download_progress, 100, (int) (data.progress * 100), false);
			}else if(startSize > 1){
				notification.contentView.setViewVisibility(R.id.notification_download_progress, View.GONE);
		        notification.contentView.setTextViewText(R.id.notification_download_title_tv,
		        		getContext().getString(R.string.notification_download_start_tip,startSize));
		        notification.contentView.setTextViewText(R.id.notification_download_content_tv,
		        		getContext().getString(R.string.notification_download_info_tip,pauseSize,failSize,finishedSize));
			}else if(pauseSize > 0 || failSize > 0){
		        notification.contentView.setViewVisibility(R.id.notification_download_progress, View.GONE);
		        notification.contentView.setTextViewText(R.id.notification_download_title_tv,
		        		getContext().getString(R.string.notification_download_unfinish_tip,pauseSize,failSize));
		        notification.contentView.setTextViewText(R.id.notification_download_content_tv,
		        		getContext().getString(R.string.notification_download_click_tip));
			}else{
				mNotificationMgr.cancel(DOWNLOAD_NOTIFI_TAG,RESIDENT_TIEM_ID);
				return;
			}
	        notification.contentIntent = contentIntent;
        }else{
        	String contentTitle = "";
        	String contentText = "";
        	if(startSize > 0){
        		contentTitle = getContext().getString(R.string.notification_download_start_tip,startSize);
        		contentText = getContext().getString(R.string.notification_download_info_tip,pauseSize,failSize,finishedSize);
			}else if(pauseSize > 0 || failSize > 0){
				contentTitle = getContext().getString(R.string.notification_download_unfinish_tip,pauseSize,failSize);
        		contentText = getContext().getString(R.string.notification_download_click_tip);
			}else{
				mNotificationMgr.cancel(DOWNLOAD_NOTIFI_TAG,RESIDENT_TIEM_ID);
				return;
			}
        	notification.setLatestEventInfo(getContext(), contentTitle, contentText, contentIntent);
        }
        mNotificationMgr.notify(DOWNLOAD_NOTIFI_TAG,RESIDENT_TIEM_ID, notification);
	}
	
	private void cancelNotify(){
		mNotificationMgr.cancel(DOWNLOAD_NOTIFI_TAG, RESIDENT_TIEM_ID);
		if(mDatas == null){
			return;
		}
		for(DownloadInfo dataDownloadInfo : mDatas){
			if(dataDownloadInfo.state == DownloadAPI.STATE_FINISH){
				mNotificationMgr.cancel(DOWNLOAD_NOTIFI_TAG,(int) dataDownloadInfo.id + AUTO_CANCEL_TIEM_ID_OFFSET);
			}
		}
	}
	
	public static void cancelNotify(Context context,int id){
		NotificationManager notificationMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationMgr.cancel(DOWNLOAD_NOTIFI_TAG,id + AUTO_CANCEL_TIEM_ID_OFFSET);
	}
	
	private DownloadInfo getData(long id){
		for(DownloadInfo dataDownloadInfo : mDatas){
			if(dataDownloadInfo.id == id){
				return dataDownloadInfo;
			}
		}
		return null;
	}
	
	private DownloadInfo getDataFromDB(long id){
		DownloadInfo downloadInfo = null;
		Cursor cursor = queryData(DownloadAPI._ID + " = " + id);
		if(cursor != null){
			while (cursor.moveToNext()) {
				downloadInfo = toDownloadInfo(cursor);
			}
			cursor.close();
		}
		return downloadInfo;
	}

	private void loadData(){
		ThreadFactory.createTerminableThread(new Runnable() {

            @Override
            public void run() {

                Cursor cursor = queryData(DownloadAPI.TIMES_TAMP + " > " + mTimesTemp);
                long maxTimesTemp = mTimesTemp;

                if (cursor != null) {
                    final ArrayList<DownloadInfo> dataTemp = new ArrayList<DownloadNotification.DownloadInfo>();
                    while (cursor.moveToNext()) {
                        DownloadInfo downloadInfo = toDownloadInfo(cursor);
                        if (downloadInfo != null) {
                            if (downloadInfo.mimeType.equals(DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_VOICE)) {
                                downloadInfo.name += getContext().getString(R.string.notification_download_chapter_tip, (downloadInfo.position + 1));
                            }
                            if (maxTimesTemp < downloadInfo.timesTemp) {
                                maxTimesTemp = downloadInfo.timesTemp;
                            }
                            dataTemp.add(downloadInfo);
                        }
                    }
                    cursor.close();
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            fillData(dataTemp);
                        }
                    });
                }
                mTimesTemp = maxTimesTemp;
            }
        }).start();
	}
	
	private void fillData(ArrayList<DownloadInfo> data){
		if(data == null || data.size() == 0){
			return;
		}
		boolean hasData = false;
		int oldSize = mDatas.size();
		for(int i = 0 ; i < data.size() ; i++,hasData = false){
			DownloadInfo upateData = data.get(i);
			for(int j = 0;j < oldSize;j++){
				DownloadInfo oldData = mDatas.get(j);

				if(oldData.id == upateData.id){
					hasData = true;
					oldData.set(upateData);
				}
			}
			
			if(!hasData){
				mDatas.add(upateData);
			}
		}
		computeDownloadStateSize();
	}
	
	private Cursor queryData(String where){
		return getContext().getContentResolver().query(DownloadAPI.CONTENT_URI, null,where,null, DownloadAPI._ID + " DESC");
	}
	
	private DownloadInfo toDownloadInfo(Cursor cursor){
		if(cursor == null || cursor.getCount() == 0 || cursor.isClosed()){
			return null;
		}
		DownloadInfo downloadInfo = new DownloadInfo();
		downloadInfo.id = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadAPI._ID));
		downloadInfo.state = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadAPI.STATE));
		downloadInfo.name = cursor.getString(cursor.getColumnIndexOrThrow(DownloadHttpEngine.CONTENT_NAME));
		downloadInfo.timesTemp = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadAPI.TIMES_TAMP));
		downloadInfo.filePathLocation = cursor.getString(cursor.getColumnIndexOrThrow(DownloadAPI.FILE_PATH));
		downloadInfo.position = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadHttpEngine.VOICE_POSITiON));
		downloadInfo.mimeType = cursor.getString(cursor.getColumnIndexOrThrow(DownloadHttpEngine.DOWNLOAD_TYPE));
		downloadInfo.contentType = cursor.getString(cursor.getColumnIndexOrThrow(DownloadHttpEngine.CONTENT_TYPE));
		downloadInfo.logoUrl = cursor.getString(cursor.getColumnIndexOrThrow(DownloadHttpEngine.ICON_URL));
		long maxSize = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadAPI.FILE_BYTE_SIZE));
		long curSize = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadAPI.FILE_BYTE_CURRENT_SIZE));
		if(maxSize != 0){
			downloadInfo.progress = curSize * 1f /maxSize;
		}else{
			downloadInfo.progress = 0;
		}
		return downloadInfo;
	}
	
//	private class DownloadListener extends BroadcastReceiver{
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if(intent == null){
//				return;
//			}
//			if(intent.getAction().equals(DownloadAPI.ACTION_ON_DOWNLOAD_STATE_CHANGE)){
//				long id = intent.getLongExtra(DownloadAPI.BROAD_CAST_DATA_KEY_ID, -1);
//				int state = intent.getIntExtra(DownloadAPI.BROAD_CAST_DATA_KEY_STATE, -1);
//				onDownloadStateChange(id,state);
//			}else if(intent.getAction().equals(DownloadAPI.ACTION_ON_DOWNLOAD_PROGRESS_CHANGE)){
//				long id = intent.getLongExtra(DownloadAPI.BROAD_CAST_DATA_KEY_ID, -1);
//				long currentSize  = intent.getLongExtra(DownloadAPI.BROAD_CAST_DATA_KEY_FILE_BYTE_CURRENT_SIZE, -1);
//				long size = intent.getLongExtra(DownloadAPI.BROAD_CAST_DATA_KEY_FILE_BYTE_SIZE, -1);
//				float progress = currentSize * 1f / size;
//				onDownloadProgress(id,progress);
//			}else if(intent.getAction().equals(DownloadAPI.ACTION_ON_DOWNLOAD_DELETE)){
//				long[] ids = intent.getLongArrayExtra(DownloadAPI.BROAD_CAST_DATA_KEY_IDS);
//				if(ids != null && ids.length > 0){
//					onDeleteDownload(ids);
//				}
//			}
//		}
//	}
	
	private class DownLoadInfoObserver extends ContentObserver{
		private Handler mHandler;
		public DownLoadInfoObserver(Handler handler) {
			super(handler);
			mHandler = handler;
		}

		@Override
		public void onChange(boolean selfChange) {
			if(selfChange){
				return;
			}
			loadData();
		}
		
	}
	
	private class DownloadInfo{
		public String contentType;
		public String mimeType;
		public int position;
		public long id;
		public int state;
		public float progress;
		public String name;
		public long timesTemp;
		public String filePathLocation;
		public String logoUrl;
		@Override
		public boolean equals(Object o) {
			if(super.equals(o)){
				return true;
			}
			
			if(o instanceof DownloadInfo && o != null){
				if( ( (DownloadInfo) o ).id == this.id){
					return true;
				}
			}
			if(o instanceof Long && o != null){
				if( (Long) o == this.id){
					return true;
				}
			}
			
			return false;
		}
		
		public void set(DownloadInfo newData) {
			this.id = newData.id;
			this.state = newData.state;
			this.progress = newData.progress;
			this.name = newData.name;
			this.timesTemp = newData.timesTemp;
			this.filePathLocation = newData.filePathLocation;
			this.logoUrl = newData.logoUrl;
			this.contentType = newData.contentType;
			this.mimeType = newData.mimeType;
			this.position = newData.position;
		}
		
	}

	@Override
	public void onTimeSettingChange() {
		mTimesTemp = System.currentTimeMillis();
	}
}
