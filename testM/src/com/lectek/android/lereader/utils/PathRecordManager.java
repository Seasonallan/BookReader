package com.lectek.android.lereader.utils;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 路径统计基类
 * 
 * @author shizq
 * @email real.kobe@163.com
 * @date 2012-12-5
 */
public class PathRecordManager {
	protected PathInfo mMainPathInfo;

	private static PathRecordManager instance;
	
	protected PathRecordManager() {
		mMainPathInfo = new PathInfo();
		mMainPathInfo.extraArrayList = new ArrayList<PathInfo>();
	}
	
	public static PathRecordManager getInstance() {
		if(instance == null) {
			instance = new PathRecordManager();
		}
		return instance;
	}
	
	/**
	 * 创建路径节点
	 * @param path 路径字符串
	 * @return PathInfo 返回当前创建的路径对象(PathInfo)；如果该路径之前已存在，返回已存在的路径对象
	 */
	public PathInfo createPath(String path) {
			PathInfo pathInfo = new PathInfo();
			pathInfo.pathString = path;
		return createPath(pathInfo);
	}
	/**
	 * 创建路径节点
	 * @param pathInfo 路径对象
	 * @return PathInfo 返回当前创建的路径对象(PathInfo)；如果该路径之前已存在，返回已存在的路径对象
	 */
	public PathInfo createPath(PathInfo pathInfo) {
		int index = checkPathExist(mMainPathInfo.extraArrayList, pathInfo.pathString);
		if(index == -1) {
			mMainPathInfo.extraArrayList.add(pathInfo);
			return pathInfo;
		}
		return mMainPathInfo.extraArrayList.get(index);
	}
	
	/**
	 * 销毁路径对象，如果该对象不存在不做任何处理
	 * @param pathInfoname
	 */
	public void destoryPath(String pathInfoname) {
		deletePaths(pathInfoname);
	}
	
	private void deletePaths(String pathInfoname) {
		ArrayList<PathInfo> pathInfos = mMainPathInfo.extraArrayList;
		int index = checkPathExist(pathInfos, pathInfoname);
		if(index >= 0) {
			pathInfos.remove(index);
		}
	}
	
	/**
	 * 销毁路径对象，如果该对象不存在不做任何处理
	 * @param pathInfo
	 */
	public void destoryPath(PathInfo pathInfo) {
		destoryPath(pathInfo.pathString);
	}
	
	/**
	 * 清除所有路径节点
	 */
	public void clearAllPath() {
		if(mMainPathInfo.extraArrayList == null && mMainPathInfo.extraArrayList.size() < 1) {
			return;
		}
		mMainPathInfo.extraArrayList.clear();
	}
	
	/**
	 * 清除多余的节点
	 */
	public void clearRedundant(String[] redundants) {
		for(int i = 0; i < redundants.length; i++){
			deletePaths(redundants[i]);
		}
	}
	
	/**
	 * 返回末尾的路径
	 * @param count 返回末尾的路径个数
	 * @param StringremoveLastPath 需要先删除的最后一个路径
	 * @param pathFilters 需要删除的路径字符串数组
	 * @return 返回整理后的路径
	 */
	public String getRearOfPathByCount(int count, String StringremoveLastPath, String[] pathFilters) {
		String pathString = getWholePathString(StringremoveLastPath);
		
		String targetpaths[] = pathString.split(",");
		
		String extractString = extractPath(targetpaths, pathFilters);
		if(!TextUtils.isEmpty(extractString)) {
			return extractString;
		}
		
		int length = targetpaths.length;
		if(length <= count) {
			return pathString;
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		for(int i = length-count; i < length;i++) {
			if(i+1 == length){
				stringBuilder.append(targetpaths[i]);
			}else{
				stringBuilder.append(targetpaths[i]+",");
			}
		}
		
		return stringBuilder.toString();
	}
	
	private String extractPath(String[] targetPaths, String[] pathFilters) {
		if(targetPaths == null || targetPaths.length < 1) {
			return null;
		}
		
		if(pathFilters == null) {
			return null;
		}
		
		int lastIndex = targetPaths.length - 1;
		
		for(int i = 0; i < pathFilters.length; i++) {
			if(targetPaths[lastIndex].contains(pathFilters[i])) {
				return targetPaths[lastIndex];
			}
		}
		return null;
	}
	
	
	/**
	 * 获取完整路径
	 * @return
	 */
	public ArrayList<PathInfo> getWholePath() {
		return mMainPathInfo.extraArrayList;
	}
	
	/**
	 * 获取完整路径字符串
	 * @return
	 */
	public String getWholePathString() {
		return getWholePathString(null);
	}
	
	/**
	 * 获取完整路径字符串
	 * @param removeLastPath 删除最后一个路径的名词
	 * @return
	 */
	public String getWholePathString(String removeLastPath) {
		String pathString = recurPath(mMainPathInfo, "");
		
		if(TextUtils.isEmpty(pathString)) {
			return pathString;
		}
		
		if(pathString.lastIndexOf(",") == pathString.length()-1) {
			pathString = pathString.substring(0, pathString.length()-1);
		}
		if(removeLastPath != null) {
			int pos = pathString.lastIndexOf(",");
			if(pos != -1 && pathString.substring(pos+1).equalsIgnoreCase(removeLastPath)) {
				pathString = pathString.substring(0, pos);
			}
		}
		return pathString;
	}
	
	//递归输出路径
	private String recurPath(PathInfo pathInfo, String patString) {

		StringBuilder stringBuilder = new StringBuilder(patString);
		String path = pathInfo.pathString;
		if (path != null && path.length() > 0) {
			stringBuilder.append(path + ",");
		}
		ArrayList<PathInfo> pathInfos = pathInfo.extraArrayList;
		if (pathInfos != null && pathInfos.size() > 0) {
			for (int i = 0; i < pathInfos.size(); i++) {
				stringBuilder = new StringBuilder(recurPath(pathInfos.get(i),
						stringBuilder.toString()));
			}
		}
		return stringBuilder.toString();
	}
		
	public void saveInstanceState(Bundle outState) {
		outState.putParcelable("MainPathInfo", mMainPathInfo);
	}

	public void restoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		}
		PathInfo pathInfo = savedInstanceState.getParcelable("MainPathInfo");
		if (pathInfo == null) {
			return;
		}
		if (mMainPathInfo.extraArrayList.size() == 0) {
			this.mMainPathInfo = null;
			this.mMainPathInfo = pathInfo;
		}
	}
	
	/**
	 * 
	 * @param pathInfos
	 * @param pathInfoname
	 * @return 返回匹配pathInfoname对象在集合的索引；-1为该集合没有匹配pathInfoname的对象
	 */
	protected static int checkPathExist(ArrayList<PathInfo> pathInfos, String pathInfoname) {
		if(pathInfoname == null) {
			return -1;
		}
		for(int i = 0; i < pathInfos.size(); i++) {
			if(pathInfoname.equals(pathInfos.get(i).pathString)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 路径对象
	 * @author shizq
	 *
	 */
	public static class PathInfo implements Parcelable {
		public String pathString;
		public ArrayList<PathInfo> extraArrayList;
		
		public PathInfo() {
			
		}
		
		/**
		 * 为路径对象添加一个子路径对象于Extra集合
		 * @param pathInfo
		 * @return PathInfo 返回当前路径对象，如果该对象之前已存在，返回已存在对象
		 */
		public PathInfo addExtra(PathInfo pathInfo) {
			
			if(extraArrayList == null) {
				extraArrayList = new ArrayList<PathInfo>();
			}
			
			int index = checkPathExist(extraArrayList, pathInfo.pathString);
			if(index == -1) {
				extraArrayList.add(pathInfo);
				return pathInfo;
			}
			return extraArrayList.get(index);
		}
		
		/**
		 * 为路径对象添加一个子路径对象于Extra集合
		 * @param pathString
		 * @return PathInfo 返回当前路径对象，如果该对象之前已存在，返回已存在对象
		 */
		public PathInfo addExtra(String pathString) {
			if(extraArrayList == null) {
				extraArrayList = new ArrayList<PathInfo>();
			}
			
			int index = checkPathExist(extraArrayList, pathString);
			if(index == -1) {
				PathInfo pathInfo = new PathInfo();
				pathInfo.pathString = pathString;
				extraArrayList.add(pathInfo);
				return pathInfo;
			}
			return extraArrayList.get(index);
		}
		
		/**
		 * 清除路径对象的Extra集合
		 * @return PathInfo 返回当前路径对象
		 */
		public PathInfo clearExtra() {
			if(extraArrayList == null) {
				extraArrayList = new ArrayList<PathInfo>();
			}
			extraArrayList.clear();
			return this;
		}
		
		@SuppressWarnings("unchecked")
		public PathInfo(Parcel source) {
			this.pathString = source.readString();
			this.extraArrayList = (ArrayList<PathInfo>)source.readValue(PathInfo.class.getClassLoader());
		}
		
		@Override
		public int describeContents() {
			return 0;
		}
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(pathString);
			dest.writeValue(extraArrayList);
		}
		
		public static final Parcelable.Creator<PathInfo> CREATOR = new Creator<PathInfo>() {
			
			@Override
			public PathInfo[] newArray(int size) {
				return null;
			}
			
			@Override
			public PathInfo createFromParcel(Parcel source) {
				return new PathInfo(source);
			}
		};
	}
}
