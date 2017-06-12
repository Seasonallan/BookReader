package com.lectek.android.lereader.lib.utils;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

/** 
 * @author Kinson Wu
 * @email kinson.wu@gmail.com
 * @date 2011-12-21
 */
public class PkgManagerUtil {
	
	private static final String TAG = PkgManagerUtil.class.getSimpleName();
	
	/**
	 * 获取APK信息
	 * @param context
	 */
	public static PackageInfo getApkInfo(Context context) {
		PackageManager pm = context.getPackageManager();
		String packageName = context.getPackageName();
		PackageInfo apkInfo = null;
		try {
			apkInfo = pm.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		return apkInfo;
	}
	
	/**
	 * 获取未安装的APK信息
	 * 
	 * @param context
	 * @param archiveFilePath  APK文件的路径。如：/sdcard/download/XX.apk
	 */
	public static PackageInfo getApkInfo(Context context, String archiveFilePath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo apkInfo = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_META_DATA);
		return apkInfo;
	}
	
	/**
	 * 获取APK版本号
	 * @param context
	 */
	public static String getApkVersionName(Context context) {
		PackageInfo apkInfo = getPackageInfo(context, context.getPackageName());
		String versionName = "";
		
		if (apkInfo != null && !TextUtils.isEmpty(apkInfo.versionName)) {
			versionName = apkInfo.versionName;
		}
		
		return versionName;
	}
	
	/**
	 * 获取APK版本序号
	 * @param context
	 */
	public static int getApkVersionCode(Context context) {
		PackageInfo apkInfo = getPackageInfo(context, context.getPackageName());
		int versionCode = -1;
		if (apkInfo != null) {
			versionCode = apkInfo.versionCode;
		}
		return versionCode;
	}
	
	/**
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static PackageInfo getPackageInfo(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pkgInfo = null;
		try {
			pkgInfo = pm.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			LogUtil.e(TAG, e.getMessage());
		}
		
		return pkgInfo;
	}
	
	public static ApplicationInfo getAppInfo(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		ApplicationInfo appInfo = null;
		try {
			appInfo = pm.getApplicationInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			LogUtil.e(TAG, e.getMessage());
		}
		return appInfo;
	}
	
	public static boolean isAppInstall(Context context, String packageName) {
		PackageInfo pkgInfo = getPackageInfo(context, packageName);
		if (pkgInfo != null) {
			return true;
		}
		return false;
	}
	
	public static boolean isAppExist(Context context, String packageName) {
		PackageManager manager = context.getPackageManager();
		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
		for (int i = 0; i < pkgList.size(); i++) {
			PackageInfo pI = pkgList.get(i);
			if (pI.packageName.equalsIgnoreCase(packageName))
				return true;
		}

		return false;
	}
	
	/**
	 * 获取友盟的渠道标识和key 组成的字符串
	 * @param context
	 * @return
	 */
	public static String getUmengMetaDataValue(Activity context){
		ApplicationInfo appInfo;
		String channelId = null,umengKey = null;
		try {
		     appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
            PackageManager.GET_META_DATA);
			if (appInfo.metaData!=null) {
				channelId =appInfo.metaData.getString("UMENG_CHANNEL");
				umengKey =appInfo.metaData.getString("UMENG_APPKEY");
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return " 渠道标识 = "+channelId+",友盟key = "+umengKey;
	}
	
	/**
	 * 获取友盟的渠道标识
	 * @param context
	 * @return
	 */
	public static String getUmengChannelId(Context context){
		ApplicationInfo appInfo;
		String channelId = null;
		try {
			appInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (appInfo.metaData!=null) {
				channelId =appInfo.metaData.getString("UMENG_CHANNEL");
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return channelId;
	}
	
	/**
	 * 获取友盟的渠道标识
	 * @param context
	 * @return
	 */
	public static String getSaleChannelId(Context context){
		ApplicationInfo appInfo;
		String channelId = null;
		try {
			appInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (appInfo.metaData!=null) {
				channelId =appInfo.metaData.getString("SALE_CHANNEL");
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return channelId;
	}
	
	/**
	 * 获取APK版本号
	 * @param context
	 */
	public static String getApkVersion(Context context) {
		PackageInfo apkInfo = getPackageInfo(context, context.getPackageName());
		String versionName = "2.9.6";
		
		if (apkInfo != null && !TextUtils.isEmpty(apkInfo.versionName)) {
			versionName = apkInfo.versionName;
		}
		
		return versionName;
	}
}
