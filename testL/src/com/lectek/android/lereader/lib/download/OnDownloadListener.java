package com.lectek.android.lereader.lib.download;

public interface OnDownloadListener {
	boolean onDownloadProgressChange(long id,long currentSize,long size);
	void onDownloadErr(long id,Exception e);
	void onDownloading(long id,long currentSize,long size);
	void onDownloadFinish(long id);
}
