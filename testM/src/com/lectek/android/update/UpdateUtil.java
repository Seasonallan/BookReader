package com.lectek.android.update;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.lectek.android.lereader.net.response.UpdateInfo;

class UpdateUtil {
	public static final String APK_POST_NAME = "_LectekUpdate.apk";
	public static final String DOWNLOAD_POST_NAME = ".temp";

	/**
	 * 判断是否有SDCARD
	 * 
	 * @return
	 */
	public static boolean isSDcardExist() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isDownloadUpdateApk(Context context,UpdateInfo updateInfo){
		String filePath = getUpdateApkPath(context,updateInfo);
		File file = new File(filePath);
		if(file.isFile() && checkApkIntegrity(context,filePath)){
			return true;
		}
		return false;
	}
	
	public static boolean checkApkIntegrity(Context context,String path){
		if(!TextUtils.isEmpty(path)){
			try{
				PackageInfo plocalObject = context
						.getPackageManager().getPackageArchiveInfo(path,PackageManager.GET_ACTIVITIES);
				if(plocalObject.activities.length > 0){
					return true;
				}
			}catch (Exception e) {}
		}
		return false;
	}
	
	public static boolean createFileDir(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
			return true;
		}
		file = null;
		return false;
	}
	
	public static void clearOldUpdateApk(Context context,UpdateInfo updateInfo){
		if (isSDcardExist()) {// SDCARD
			String filePath = UpdateManager.getUpdateSetting().mApkSavePath;
			searchAndclearOldUpdateApk(filePath,updateInfo);
		} 
		String filePath = context.getCacheDir() + File.separator;
		searchAndclearOldUpdateApk(filePath,updateInfo);
	}
	
	public static String getUpdateApkPath(Context context,UpdateInfo updateInfo){
		String filePath = "";
		if (isSDcardExist()) {// SDCARD
			String savePath = UpdateManager.getUpdateSetting().mApkSavePath;
			createFileDir(savePath);
			filePath = savePath + getUpdateApkName(updateInfo);
		} else {// 包名/cache
			filePath = context.getCacheDir() + File.separator
					+ getUpdateApkName(updateInfo);
		}
		return filePath;
	}

	public static String getUpdateApkName(UpdateInfo updateInfo){
		return updateInfo.getUpdateVersion().hashCode() + APK_POST_NAME;
	}
	public static void searchAndclearOldUpdateApk(String filePath,UpdateInfo updateInfo){
		if(updateInfo == null){
			return;
		}
		File file = new File(filePath);
		if(file.isDirectory()){
			File [] files = file.listFiles();
			if(files != null && files.length > 0){
				for(File fileTemp : files){
					if(fileTemp.isFile() && fileTemp.getName().lastIndexOf(APK_POST_NAME) != -1){
						if(fileTemp.getName().lastIndexOf(DOWNLOAD_POST_NAME) != -1 
								|| fileTemp.getName().lastIndexOf(getUpdateApkName(updateInfo)) == -1){
							fileTemp.delete();
						}
					}
				}
			}
		}
	}
	
	public static void installUpdate(Context context,UpdateInfo updateInfo){
		String filePath = getUpdateApkPath(context,updateInfo);
		if(isDownloadUpdateApk(context,updateInfo)){
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setDataAndType(Uri.parse("file://" + filePath),
					"application/vnd.android.package-archive");
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
	}
}
