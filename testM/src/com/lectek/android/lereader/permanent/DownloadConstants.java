package com.lectek.android.lereader.permanent;

import android.net.Uri;
/**
* @author linyiwei
* @email 21551594@qq.com
* @date 2011-11-1
*/
public class DownloadConstants {
	public static final String CONTENT_URI_AUTHORITIES = "com.lectek.android.LYReader.provider.DownloadProvider";
	public static final String CONTENT_URI_PATH = "download";
	public static final String CONTENT_URI_PATH_ROOT = "downloadRoot";
	
	
	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ CONTENT_URI_AUTHORITIES + "/" + CONTENT_URI_PATH_ROOT);
	
	/**
	 * 根据时间戳获取下载单元信息
	 */
	public static final int WHAT_LOAD_DOWNLOAD_UNITS = 1;
	/**
	 *  删除下载单元
	 */
	public static final int WHAT_DELETE_DOWNLOAD_UNITS = 2;
	
	/**
	 *  下载进度改变时
	 */
	public static final int WHAT_ON_DOWNLOAD_PROGRESS_CHANGE = 3;
	
	/**
	 *  保存下载单元信息
	 */
	public static final int WHAT_SAVE_DOWNLOAD = 4;
}
