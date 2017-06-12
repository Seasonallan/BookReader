package com.lectek.android.lereader.binding.model.user;

import java.util.ArrayList;

import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;

public class MyOrderModelLeyue extends PagingLoadModel<ContentInfoLeyue> {
	
	private int mPageSize = Integer.MAX_VALUE;
	private int mPageItemSize = 20;
	private int mLoadPage = 0;
	private int start = 0;
	private int totalCurPageCount;

	@Override
	protected ArrayList<ContentInfoLeyue> onLoadCurrentPageData(int loadPage,
			int pageItemSize, Object... params) throws Exception {
		
		String userId = PreferencesUtil.getInstance(getContext()).getUserId();
		String userName = PreferencesUtil.getInstance(getContext()).getUserName();
		String password = PreferencesUtil.getInstance(getContext()).getUserPSW();
		
//		ArrayList<ContentInfoLeyue> contentInfos = ApiProcess4Leyue.getInstance(getContext()).getOrderedBooks(userId, userName, password, (start + mLoadPage*getPageItemSize()), getPageItemSize());
//		
//		if(contentInfos != null && contentInfos.size() > 0){
//			if(contentInfos.size() < mPageItemSize){
//				start = contentInfos.size();
//				totalCurPageCount += start;
//				if(totalCurPageCount >= getPageItemSize()){
//					totalCurPageCount = 0;
//					mLoadPage++;
//				}
//			}else{
//				start = 0;
//				mLoadPage++;
//			}
//		}else{
//			start = 0;
//		}
		
		ArrayList<ContentInfoLeyue> contentInfos = ApiProcess4Leyue.getInstance(getContext()).getOrderedBooks(userId, userName, password, loadPage*getPageItemSize(), getPageItemSize());
		
		return contentInfos;
	}

	@Override
	protected int getPageItemSize() {
		return mPageItemSize;
	}

	@Override
	protected Integer getPageSize() {
		return mPageSize;
	}

	@Override
	protected String getGroupKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isDataCacheEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isValidDataCacheEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

}
