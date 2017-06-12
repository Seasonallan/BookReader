package com.lectek.android.lereader.account.thirdPartApi.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

/** 网络工具类
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-3-1
 */
public class NetworkUtil {
	
	/**
	 * 判断是否启动WIFI
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiWork(Context context) {
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wm == null) {// 有些手机阉割到WIFI，这里会为null。如：宇龙E230A
			return false;
		}
		if (wm.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if(cm != null){
				NetworkInfo networkInfo = cm.getActiveNetworkInfo();
				if (networkInfo != null) {
					String ei = networkInfo.getTypeName();
					if (!TextUtils.isEmpty(ei)) {
						int index = ei.toUpperCase().indexOf("WIFI");
						if (index > -1) {
							return true;
						}
					}
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 判断网络是否连接上
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetAvailable(Context context) {
		NetworkInfo networkInfo = getActiveNetworkInfo(context);
		if (networkInfo != null) {
			if (networkInfo.getState().compareTo(NetworkInfo.State.CONNECTED) == 0) {
				return true;
			}
		}
		return false;
	}
	
	private static NetworkInfo getActiveNetworkInfo(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		return networkInfo;
	}

}
