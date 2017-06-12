package com.lectek.android.lereader.data;

import android.text.TextUtils;


public class FileData {
	public String mAbsolutePath;
	public String mName;
	public String mInfo;
	public String mBookTitle = null;
	public String mImagePath = null;
	public FileData(String absolutePath,String name,String info){
		mAbsolutePath = absolutePath;
		mName = name;
		mInfo = info;
		if(!TextUtils.isEmpty(mAbsolutePath)){
			int statr = mAbsolutePath.lastIndexOf("/");
			if(statr != -1 && statr + 1 < mAbsolutePath.length()){
				int end = mAbsolutePath.lastIndexOf(".");
				if(statr >= end){
					end = mAbsolutePath.length();
				}
				mBookTitle = mAbsolutePath.substring(statr + 1, end);
			}
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof FileData){
			if(((FileData) o).mAbsolutePath.equals(mAbsolutePath)){
				return true;
			}
		}
		return super.equals(o);
	}
}
