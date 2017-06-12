package com.lectek.android.lereader.lib.download;

import android.content.Context;
import android.content.Intent;

public interface IDownloadNotification {
	public void onCreate(Context context);
	
	public void onStart(Intent intent, int startId);
	
	public void onDestroy();
	
	public void onTimeSettingChange();
}
