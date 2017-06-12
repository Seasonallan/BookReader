package com.lectek.lereader.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.TextUtils;

/**
 * 捕捉程序异常保存到指定的文件
 * 
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-7-14
 */
public class CrashCatchHandler implements UncaughtExceptionHandler {

	private static final String CRASH_REPORTER_EXTENSION = "crash-file.txt";
	private static final int MAX_SIZE = 512 * 1024;
	private UncaughtExceptionHandler mUncaughtExceptionHandler;
	private String outPutDir;

	public CrashCatchHandler(String outPutDir) {
		this.outPutDir = outPutDir;
		mUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		saveCrashInfoToFile(ex);
		if (mUncaughtExceptionHandler != null) {
			mUncaughtExceptionHandler.uncaughtException(thread, ex);
		}
	}

	/**
	 * 保存异常信息到文件
	 * 
	 * @param ex
	 * @return
	 */
	private String saveCrashInfoToFile(Throwable ex) {
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		ex.printStackTrace(printWriter);

		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}

		String result = info.toString();
		printWriter.close();

		StringBuilder sb = new StringBuilder();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd HH:mm:ss");
		Date date = new Date();
		String timestamp = sdf.format(date);

		sb.append(timestamp);
		sb.append("\n");
		sb.append(result);
		sb.append("\n\n\n");

		String filePath = getFilePath();
		if (FileUtil.outCrashToFile(sb.toString(), filePath, MAX_SIZE)) {
			return filePath;
		}
		return null;
	}

	private String getFilePath() {
		if (TextUtils.isEmpty(outPutDir)) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(outPutDir);
		sb.append(CRASH_REPORTER_EXTENSION);
		return sb.toString();
	}

}
