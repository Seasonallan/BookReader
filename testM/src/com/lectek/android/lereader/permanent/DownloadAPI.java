package com.lectek.android.lereader.permanent;

import com.lectek.android.lereader.lib.download.IDownloadNotification;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * DownloadUnit表字段名定义
 * 
 *	对外提供的ContentProvider操作参数及数据库字段
 * @author linyiwei
 * @email 21551594@qq.com
 * @date 2011-11-1
 *
 */
public class DownloadAPI{
	/**
	 * ContentProvider访问Uri
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ DownloadConstants.CONTENT_URI_AUTHORITIES + "/" + DownloadConstants.CONTENT_URI_PATH);
	
	/**
	 * ContentProvider访问Uri
	 */
	public static final Uri CONTENT_URI_NOT_NOTICE = DownloadConstants.CONTENT_URI;
	//--------------------字段名----------------------
	public static final String _ID = "id";
	/**
	 * 本地文件存放路径 包括文件名
	 * 此字段不支持update操作
	 */
	public static final String FILE_PATH = "file_path";
	/**
	 * 文件网络地址
	 * 此字段不支持update操作【该字段暂时废弃，使用 DownloadHttpEngine.URL】
	 */
	public static final String DOWNLOAD_URL = "download_url";
	/**
	 * 文件下载或上传状态
	 */
	public static final String STATE = "state";
	/**
	 * 文件名
	 */
	public static final String FILE_NAME = "file_name";
	/**
	 * 文件动作类型，（如上传或下载  预留）
	 */
	public static final String ACTION_TYPE = "action_type";
	
	/**
	 * 文件总大小
	 */
	public static final String FILE_BYTE_SIZE = "file_byte_size";
	/**
	 * 文件当前大小
	 */
	public static final String FILE_BYTE_CURRENT_SIZE = "file_byte_current_size";
	/**
	 * 时间戳
	 */
	public static final String TIMES_TAMP  = "times_tamp";
	/**
	 * 删除标识,被标识为删除后对外隐藏
	 */
	public static final String DELETE = "_delete";
	/**
	 *  预留字段0
	 */
	public static final String DATA0 = "data0";
	/**
	 *  预留字段1
	 */
	public static final String DATA1 = "data1";
	
	/**
	 *  预留字段2
	 */
	public static final String DATA2 = "data2";
	
	/**
	 *  预留字段3
	 */
	public static final String DATA3 = "data3";
	
	/**
	 *  预留字段4
	 */
	public static final String DATA4 = "data4";
	
	/**
	 *  预留字段5
	 */
	public static final String DATA5 = "data5";
	/**
	 *  预留字段6
	 */
	public static final String DATA6 = "data6";
	/**
	 *  预留字段7
	 */
	public static final String DATA7 = "data7";
	/**
	 *  预留字段8
	 */
	public static final String DATA8 = "data8";
	/**
	 *  预留字段9
	 */
	public static final String DATA9 = "data9";
	/**
	 *  预留字段10
	 */
	public static final String DATA10 = "data10";
	/**
	 *  预留字段11
	 */
	public static final String DATA11 = "data11";
	/**
	 *  预留字段12
	 */
	public static final String DATA12 = "data12";
	/**
	 *  预留字段13
	 */
	public static final String DATA13 = "data13";
	/**
	 *  预留字段14
	 */
	public static final String DATA14 = "data14";
	/**
	 *  预留字段15
	 */
	public static final String DATA15 = "data15";
	/**
	 *  预留字段16
	 */
	public static final String DATA16 = "data16";
	/**
	 *  预留字段17
	 */
	public static final String DATA17 = "data17";
	//----------------下载状态参数-------------
	/**
	 * 文件下载或上传状态值   准备 开始
	 */
	public static final int STATE_START = 0;
	/**
	 * 文件下载或上传状态值  开始下载
	 */
	public static final int STATE_STARTING = 1;
	/**
	 * 文件下载或上传状态值  暂停
	 */
	public static final int STATE_PAUSE = 2;
	/**
	 * 文件下载或上传状态值  完成
	 */
	public static final int STATE_FINISH = 3;
	/**
	 * 文件下载或上传状态值  错误
	 */
	public static final int STATE_FAIL= 4;
	/**
	 * 内存空间不足
	 */
	public static final int STATE_FAIL_OUT_MEMORY= 5;
    /**
     * 在线阅读状态值
     */
    public static final int STATE_ONLINE= 6;
	
	//---------------------Service 发出的广播 Action定义
	/**
	 * 当下载进度变化时
	 * 包含数据：
	 * 	BROAD_CAST_DATA_KEY_ID
	 * 	BROAD_CAST_DATA_KEY_FILE_BYTE_SIZE
	 * 	BROAD_CAST_DATA_KEY_FILE_BYTE_CURRENT_SIZE
	 */
	public static final String ACTION_ON_DOWNLOAD_PROGRESS_CHANGE = "com.lectek.action.downloadProgressChange";
	/**
	 * 当下载状态变化时
	 * 包含数据：
	 *	BROAD_CAST_DATA_KEY_ID
	 * 	BROAD_CAST_DATA_KEY_STATE
	 */
	public static final String ACTION_ON_DOWNLOAD_STATE_CHANGE = "com.lectek.action.downloadStateChange";
	
	public static final String ACTION_ON_DOWNLOAD_DELETE = "com.lectek.action.downloadDelete";
	
	//-------------------------广播数据定义
	public static final String BROAD_CAST_DATA_KEY_ID = "id";
	
	public static final String BROAD_CAST_DATA_KEY_IDS = "ids";
	
	public static final String BROAD_CAST_DATA_KEY_FILE_BYTE_SIZE = "file_byte_size";
	
	public static final String BROAD_CAST_DATA_KEY_FILE_BYTE_CURRENT_SIZE = "file_byte_current_size";
	
	public static final String BROAD_CAST_DATA_KEY_STATE = "file_byte_current_size";
	//------------------------Service 控制  Action定义---------------
	public static final String ACTION_START_DOWNLOAD = "com.lectek.action.startDownload";
	public static final String ACTION_STOP_DOWNLOAD = "com.lectek.action.stopDownload";
	
	public static class Setting {
		public static int mMaxThreadSize = 3;
		public static int mMaxWaitThreadSize = 1;
		public static int mReconnectSize = 3;
		public static Class mHttpHandler;
		public static ToastNoticeRunnable mToastNoticeRunnable;
		public static DBUpdateRunnable mDBUpdateRunnable;
		public static IDownloadNotification mDownloadNotification;
	}
	public static interface ToastNoticeRunnable{
		public void run(String msg);
	}
	public static interface DBUpdateRunnable{
		public void onCreate(final SQLiteDatabase db,String table);
		public void onUpgrade(final SQLiteDatabase db,String table);
	}
}
