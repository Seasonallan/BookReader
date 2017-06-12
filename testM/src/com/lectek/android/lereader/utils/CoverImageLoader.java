package com.lectek.android.lereader.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.net.Uri;

import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.lib.utils.LogUtil;

/**
 * 启动Cover界面加载类
 * 
 * @author yyl
 *
 */
public class CoverImageLoader {
	/**
	 * 通过文件名获取缓存目录中的图片文件对象
	 * 
	 * @param fileName
	 * @return
	 */
	public static File getCoverImageFile(String fileName) {
		File cacheDir = MyAndroidApplication.getInstance().getCacheDir();
		File imageFile = new File(cacheDir.getAbsolutePath() + File.separator+"CoverCache" + File.separator
				+ fileName);
		LogUtil.i("yyl", "缓存目录中的图片文件对象："+imageFile.getAbsolutePath());
		return imageFile;
	}

	/**
	 * 通过文件名获取缓存目录中的图片Uri
	 * 
	 * @param fileName
	 * @return
	 */
	public static Uri getCoverImageUri(String httpuri) {
		File cacheDir = new File(MyAndroidApplication.getInstance()
				.getCacheDir().toString()
				+ File.separator + "CoverCache");
		Uri imageUri = Uri.parse(cacheDir.getAbsolutePath() + File.separator
				+ httpuri.hashCode()+"");
		LogUtil.i("yyl", "缓存目录中的图片Uri："+imageUri);
		return imageUri;
	}

	/**
	 * 把网络图片下载到本地的缓存目录 把下载的图片信息拼接作为图片的文件名，再获取图片时通过分析图片文件名获得图片信息
	 * 
	 * @param newInfo
	 */
	public static void downloadCoverImage(String uri) {
		LogUtil.i("yyl", "下载地址："+uri);
		FileOutputStream fos = null;
		InputStream is = null;
		try {
			URL url = new URL(uri);
			is = url.openStream();
			byte datas[] = new byte[1024 * 1];
			File newPicFile = getCoverImageFile(generateFileName(uri));
			fos = new FileOutputStream(newPicFile);
			int byteRead = -1;
			while ((byteRead = is.read(datas)) != -1) {
				fos.write(datas, 0, byteRead);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 通过网络图片信息拼接图片名称,其实是网络地址的哈希码
	 * 
	 * @param info
	 * @return
	 */
	public static String generateFileName(String uri) {
		StringBuffer sb = new StringBuffer();
		sb.append(uri.hashCode() + "");
		LogUtil.i("yyl", "缓存文件："+sb.toString());
		return sb.toString();
	}

	/**
	 * 检查图片缓存是否存在
	 * 
	 * @param info
	 * @return
	 */
	public static boolean checkIfFileExist(ArrayList<String> list) {
		for (int i = 0; i < list.size(); i++) {
			File file = getCoverImageFile(list.get(i).hashCode() + "");
			if (!file.exists()) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * 缓存文件夹是否存在
	 * 
	 * @param info
	 * @return
	 */
	public static boolean checkIfFileFolderExist() {
		File file = new File(getCacheFolderName());
		if (!file.exists()) {
			return false;
		}
		return true;
	}
	
	/**
	 * 获取缓存文件
	 * 
	 * @param info
	 * @return
	 */
	public static ArrayList<Uri> getCacheUris() {
		ArrayList<Uri> uris=new ArrayList<Uri>();
		File file = new File(getCacheFolderName());
		String[] list = file.list();
		int length=0;
		try {
			length=list.length;
			for (int i = 0; i <length ; i++) {
				
				uris.add(Uri.parse(getCacheFolderName()+File.separator+list[i]));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		LogUtil.i("yyl","获取的缓存文件："+uris);
		return uris;
	}

	public static String getCacheFolderName() {
		String folderAddress = MyAndroidApplication.getInstance().getCacheDir()
				.toString()
				+ File.separator + "CoverCache";
		LogUtil.i("yyl", "缓存文件夹名字："+folderAddress);
		return folderAddress;
	}

	/**
	 * 创建缓存文件夹
	 * 
	 * @param info
	 * @return
	 */
	public static void createFileFolder() {
		File file = new File(getCacheFolderName());
		if (!file.exists()) {
			file.mkdir();
		}
		LogUtil.i("yyl", "创建缓存文件夹："+file.getAbsolutePath());
	}

	public static String showCacheFiles() {
		File file=new File(getCacheFolderName());
		String[] s=file.list();
		StringBuffer sb=new StringBuffer();
		for (int i = 0; i < s.length; i++) {
			sb.append(s[i]);
		}
		LogUtil.i("yyl", "显示缓存文件："+sb.toString());
		return sb.toString();
	}
}
