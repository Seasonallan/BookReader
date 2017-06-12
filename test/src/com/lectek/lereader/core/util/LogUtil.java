package com.lectek.lereader.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.TextUtils;
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
	public static boolean DEBUG = true;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss.ms");
	private static String LOG_FILE = "";

	public static void init(String tag, boolean debug, String logFile) {
		TAG = tag;
		DEBUG = debug;
		LOG_FILE = logFile;
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
		if (TextUtils.isEmpty(LOG_FILE)) {
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
		FileUtil.outPutToFile(sb.toString(), LOG_FILE);
	}

}
