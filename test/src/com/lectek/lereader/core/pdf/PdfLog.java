package com.lectek.lereader.core.pdf;

import android.util.Log;

public class PdfLog {

	private static boolean isDebug = false;
	
	private static final String TAG_PDF = "pdflog";
	
	public static void i(String msg) {
		if(isDebug) {
			Log.i(TAG_PDF, msg);
		}
	}
	
	public static void e(String msg) {
		if(isDebug) {
			Log.e(TAG_PDF, msg);
		}
	}
	
	public static void d(String msg) {
		if(isDebug) {
			Log.d(TAG_PDF, msg);
		}
	}
	
	public static void v(String msg) {
		if(isDebug) {
			Log.v(TAG_PDF, msg);
		}
	}
	
	public static void w(String msg) {
		if(isDebug) {
			Log.w(TAG_PDF, msg);
		}
	}
	
}
