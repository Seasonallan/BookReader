package com.lectek.android.lereader.lib.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

/**
 * LOG日志工具类；如果需要指定是否需要输入，指定的TAG，知道的输出目录，调用之前需要调用init()方法
 * 
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-10-8
 */
public class LogUtil {

	private static String TAG = "Lectek";
	private static boolean DEBUG = false;
	private static int MAX_LOG_FILE_SIZE = 512 * 1024;	//默认512k，超过后清除所有内容
	private static final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss.ms");
	private static String LOG_FILE = "";
	
	/**
	 * 初始化
	 * @param tag	默认标签
	 * @param debug	 调试模式下才会输出log
	 * @param logFile	记录log的文件，传空或文件无法读写则log不输出到文件
	 * @param maxLogFileSize log文件大小，如果 < 0 默认512k。超过后覆盖前面的内容
	 */
	public static void init(String tag, boolean debug, String logFile, int maxLogFileSize) {
		TAG = tag;
		DEBUG = debug;
		LOG_FILE = logFile;
		if(maxLogFileSize > 0) {
			MAX_LOG_FILE_SIZE = maxLogFileSize;
		}
	}

	public static void i(String msg) {
		i(TAG, msg);
	}

	public static void i(String tag, String msg) {
		i(tag, msg, null);
	}

	public static void i(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			Log.i(tag, msg, tr);
		}
		outMessage(tag, msg, tr);
	}

	public static void v(String msg) {
		v(TAG, msg);
	}

	public static void v(String tag, String msg) {
		if (DEBUG) {
			Log.v(tag, msg);
		}
		outMessage(tag, msg);
	}

	public static void e(String msg) {
		e(TAG, msg);
	}

	public static void e(String tag, String msg) {
		e(tag, msg, null);
	}
	
	public static void e(String msg, Throwable tr) {
		e(TAG, msg, tr);
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			Log.e(tag, msg, tr);
		}
		outMessage(tag, msg, tr);
	}

	private static void outMessage(String tag, String msg) {
		outMessage(tag, msg, null);
	}

	private static void outMessage(String tag, String msg, Throwable tr) {
		if (StringUtil.isEmpty(LOG_FILE)) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(sdf.format(new Date()));
		sb.append(": ");
		sb.append(tag);
		sb.append(": ");
		sb.append(msg);
		sb.append("\n");
		if (tr != null) {
			sb.append(Log.getStackTraceString(tr));
			sb.append("\n");
		}
		
		FileUtil.writeFileOfLimitedSize(sb.toString(), LOG_FILE, true, MAX_LOG_FILE_SIZE);
	}

}
