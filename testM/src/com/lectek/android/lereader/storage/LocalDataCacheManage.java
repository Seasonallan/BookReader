package com.lectek.android.lereader.storage;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.lectek.android.lereader.lib.Constants;
import com.lectek.android.lereader.lib.utils.FileUtil;

/**
 * 本地缓存处理
 * 
 * @author yangwq
 * @date 2014年7月10日
 * @email 57890940@qq.com
 */
public class LocalDataCacheManage {
	
	private static LocalDataCacheManage instance;
	
	private List<String> mCacheDirs;
	
	
	private LocalDataCacheManage(){
		initCacheDir();
	}
	
	public static LocalDataCacheManage getInstance(){
		if(instance == null){
			syncInit();
		}
		return instance;
	}
	
	public synchronized static void syncInit(){
		if(instance == null){
			instance = new LocalDataCacheManage();
		}
		
	}
	
	/**
	 * 初始化缓存目录
	 */
	private void initCacheDir(){
		mCacheDirs = new ArrayList<String>();
		String imageDir = Constants.BOOKS_TEMP_IMAGE;
		String tempDir = Constants.BOOKS_TEMP;
		String onlineDir = Constants.BOOKS_ONLINE;
		mCacheDirs.add(tempDir);
		mCacheDirs.add(imageDir);
		mCacheDirs.add(onlineDir);
		
	}
	
	/**
	 * 获取当前缓存容量
	 * @return
	 */
	public long getCurrentCacheSize(Context aContxt){
		long filesSize = 0;
		int size = mCacheDirs.size();
		for (int i = 0; i < size; i++) {
			String dir = mCacheDirs.get(i);
//			filesSize += FileUtil.getDirStorage(dir);
		}
		
		return filesSize;
		
	}
	
	/**
	 * 删除本地缓存
	 */
	public void deleteCache(){
		int size = mCacheDirs.size();
		for (int i = 0; i < size; i++) {
			String dir = mCacheDirs.get(i);
			FileUtil.deleteDir(dir, true);
		}
	}
	
	
	
	

}
