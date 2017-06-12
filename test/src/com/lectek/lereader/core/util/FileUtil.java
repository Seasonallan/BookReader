package com.lectek.lereader.core.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

/**
 * 文件操作工具
 * 
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-7-15
 */
public final class FileUtil {

	private static final String TAG = FileUtil.class.getSimpleName();
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
	
	/** SDCARD的根目录
	 * @return
	 */
	public static File getExternalStorageDirectory(){
		return Environment.getExternalStorageDirectory();
	}
	
	/** 获取SDCARD的DCIM目录
	 * @return
	 */
	public static File getExternalStorageDcimDirectory(){
		if(Build.VERSION.SDK_INT >= 8) {
			return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		} else {
			return new File(Environment.getExternalStorageDirectory() + "/dcim");
		}
	}
	
//	public static boolean isSDcardExistEx() {
//		// Do something for ICS and above versions
//		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//			return true;
//		} else {
//			Context context = MyAndroidApplication.getInstance().getApplicationContext();
//			// operate on inner sdcard
//			StorageManager sStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
//			StorageVolume storage[] = sStorageManager.getVolumeList();
//			String state = null;
//			try {
//				state = sStorageManager.getVolumeState(storage[0].getPath());
//				if (state.equals(Environment.MEDIA_MOUNTED)) {
//					return true;
//				}
//			} catch (Exception e) {
//			}
//		}
//		return false;
//	}

	/**
	 * 判断某个文件是否存在
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean isFileExists(String filePath) {
		if (!isSDcardExist()) {
			return false;
		}
		File file = new File(filePath);
		if (file != null && file.exists()) {
			if (file.length() == 0) {
				file.delete();
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * 获取存储卡的可用空间
	 * 
	 * @return
	 */
	public static long getStorageSize() {
		if (isSDcardExist()) {
			StatFs stat = new StatFs(getExternalStorageDirectory()
					.getAbsolutePath());
			long blockSize = stat.getBlockSize();
			return stat.getAvailableBlocks() * blockSize;
		}
		return 0;
	}

	private static boolean updateFileTime(String dir, String fileName) {
		File file = new File(dir, fileName);
		if (!file.exists()) {
			return false;
		}
		long newModifiedTime = System.currentTimeMillis();
		return file.setLastModified(newModifiedTime);
	}

	/**
	 * 创建文件目录
	 * 
	 * @param path
	 */
	public static boolean createFileDir(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
			return true;
		}
		file = null;
		return false;
	}
	/**
	 * 检测文件路径的目录是否存在，不存在就尝试创建目录
	 * @param path 文件路径
	 * @return 目录是否可用
	 */
	public static boolean checkPathDir(String path){
		if(TextUtils.isEmpty(path) || path.charAt(path.length() - 1) == '/'){
			return false;
		}
		String dirStr = path;
		int end = dirStr.lastIndexOf('/') + 1;
		if(end != -1){
			dirStr = dirStr.substring(0, end);
		}else{
			return false;
		}
		return checkDir(dirStr);
	}
	/**
	 * 检测目录是否存在，不存在就尝试创建目录
	 * @param path 文件目录
	 * @return 目录是否可用
	 */
	public static boolean checkDir(String dirPath){
		if(TextUtils.isEmpty(dirPath)){
			return false;
		}
		File dir = new File(dirPath);
		if(dir.exists() || dir.mkdirs()){
			return true;
		}
		return false;
	}
	/**
	 * 写文件到SDCARD指定的位置
	 * 
	 * @param str
	 * @param filePath
	 * @return
	 */
	public static boolean outPutToFile(String str, String filePath) {
		if (!isSDcardExist()) {
			return false;
		}
		if (TextUtils.isEmpty(str)) {
			return false;
		}
		if (TextUtils.isEmpty(filePath)) {
			return false;
		}
		try {
			FileWriter fw = new FileWriter(filePath, true);
			fw.write(str);
			fw.flush();
			fw.close();
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean outCrashToFile(String str, String filePath,
			int maxSize) {
		if (!isSDcardExist()) {
			return false;
		}
		if (TextUtils.isEmpty(str)) {
			return false;
		}
		if (TextUtils.isEmpty(filePath)) {
			return false;
		}
		try {
			File file = new File(filePath);
			if (file.exists()) {
				if (maxSize < file.length()) {
					file.delete();
				}
			}
			FileWriter fw = new FileWriter(filePath, true);
			fw.write(str);
			fw.flush();
			fw.close();
			return true;
		} catch (Exception e) {
		}
		return false;
	}
	
	public static void copyFileToSdcard(Context context, String fileName,
			String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			return;
		}
		try {
			AssetManager am = context.getAssets();
			InputStream is = am.open(fileName);
			BufferedInputStream bis = new BufferedInputStream(is);
			if (!file.exists()) {
				file.createNewFile();
			}
			if (file.isFile()) {
				RandomAccessFile oSavedFile = new RandomAccessFile(file, "rw");
				oSavedFile.seek(0);
				int bufferSize = 4096;
				byte[] b = new byte[bufferSize];
				int nRead;
				int currentBytes = 0;
				int bytesNotified = currentBytes;
				long timeLastNotification = 0;
				for (;;) {
					nRead = bis.read(b, 0, bufferSize);

					if (nRead == -1) {
						break;
					}
					currentBytes += nRead;
					oSavedFile.write(b, 0, nRead);
					long now = System.currentTimeMillis();
					if (currentBytes - bytesNotified > bufferSize
							&& now - timeLastNotification > 1500) {
						bytesNotified = currentBytes;
						timeLastNotification = now;
					}
				}
				oSavedFile.close();
			}
			is.close();
		} catch (Exception e) {

		}
	}
	
	public static void copyFileToFile(Context context, String fileName, InputStream is) {
		try {
//			AssetManager am = context.getAssets();
//			InputStream is = am.open(fileName);
			BufferedInputStream bis = new BufferedInputStream(is);
			FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_WORLD_WRITEABLE);
			
			int bufferSize = 4096;
			byte[] b = new byte[bufferSize];
			int nRead;
			int currentBytes = 0;
			int bytesNotified = currentBytes;
			long timeLastNotification = 0;
			for (;;) {
				nRead = bis.read(b, 0, bufferSize);

				if (nRead == -1) {
					break;
				}
				currentBytes += nRead;
				fos.write(b, 0, nRead);
				long now = System.currentTimeMillis();
				if (currentBytes - bytesNotified > bufferSize && now - timeLastNotification > 1500) {
					bytesNotified = currentBytes;
					timeLastNotification = now;
				}
			}
			fos.flush();
			fos.close();
			is.close();
		} catch (Exception e) {

		}
	}

	public static int getFileSize(String filePath) {
		File file = new File(filePath);
		FileInputStream fis = null;
		int fileLen = 0;
		try {
			fis = new FileInputStream(file);
			fileLen = fis.available(); // 这就是文件大小
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return fileLen;
	}
	
	/** 获取SDCARD的ID
	 * @return
	 */
//	public static int getSdcardID(){
//		return FileUtils.getFatVolumeId(getExternalStorageDirectory().getName());
//	}
	
	public static boolean delFiles(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return true;
		}
		File file = new File(filePath);
		if (!file.exists()) {
			return true;
		}
		
		if (file.isFile() && file.delete()) {	//若是文件且删除成功返回 true
			return true;
		} else if (file.isDirectory()) {
			return delFolder(filePath);
		}
		return false;
	}

	/**
	 * 删除文件夹
	 * 
	 * @param folderPath
	 *            String 文件夹路径及名称 如:/mmt/SDCard
	 * @return boolean
	 */
	public static boolean delFolder(String folderPath) {
		boolean delSuccessed = false;
		try {
			if (!delAllFiles(folderPath)) {	// 删除完里面所有内容
				return delSuccessed;
			}
			
			File file = new File(folderPath);
			if (file != null && (!file.exists() || file.delete())) {	//删除文件夹
				return true;
			}
		} catch (Exception e) {
			LogUtil.e("FileUtil", e);
		}
		
		return false;
	}
	
	/**
	 * 删除文件夹里面的所有文件
	 * 
	 * @param folderPath
	 *            String 文件夹路径 如 /mmt/SDCard
	 */
	public static boolean delAllFiles(String folderPath) {
		if (TextUtils.isEmpty(folderPath)) {
			return true;
		}
		
		File file = new File(folderPath);
		if (!file.exists()) {	//文件或文件夹不存在
			return true;
		}
		
		if (!file.isDirectory()) {	//不是文件夹
			return true;
		}
		
		boolean delSuccessed = false;
		String[] tempList = file.list();
		if (tempList == null) {
			return true;
		}
		
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {	//遍历文件夹里的所有文件夹和文件
			if (folderPath.endsWith(File.separator)) {
				temp = new File(folderPath + tempList[i]);
			} else {
				temp = new File(folderPath + File.separator + tempList[i]);
			}
			if (temp.isFile()) {	//如果是文件直接删除
				if (!temp.delete()) {
					LogUtil.i(TAG, "File deleted failed");
					return false;	//删除文件失败返回 false
				}
			} else if (temp.isDirectory()) {	//文件夹
				delSuccessed = delFolder(folderPath + "/" + tempList[i]);	// 再删除空文件夹
				if (!delSuccessed) {
					return false;	//删除文件夹失败
				}
			}
		}
		
		return true;
	}

//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private static String getVolumeState(String path){
//		try {
//			Class storageManager = Class.forName("android.os.storage.StorageManager");
//			Object storageManagerInstance = storageManager.getConstructor(
//					Looper.class).newInstance(Looper.getMainLooper());
//			return (String) storageManager.getMethod("getVolumeState", String.class).invoke(storageManagerInstance, path);
//		} catch (Exception e) {
//		}
//		return null;
//	}
//
//	/**
//	 * 获取SD
//	 * 
//	 * @return
//	 */
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public static StorageVolume[] getStorageVolumes() {
//		Class storageManager = null;
//		StorageVolume[] storageVolumeList = null;
//		try {
//			storageManager = Class.forName("android.os.storage.StorageManager");
//			Object storageManagerInstance = storageManager.getConstructor(
//					Looper.class).newInstance(Looper.getMainLooper());
//			Object[] volumeList = (Object[]) storageManager.getMethod(
//					"getVolumeList").invoke(storageManagerInstance);
//			if (volumeList != null) {
//				storageVolumeList = new StorageVolume[volumeList.length];
//				for (int i = 0; i < volumeList.length; i++) {
//					storageVolumeList[i] = new StorageVolume(volumeList[i]);
//				}
//			}
//		} catch (Exception e) {
//
//		}
//		return storageVolumeList;
//	}
	
}
