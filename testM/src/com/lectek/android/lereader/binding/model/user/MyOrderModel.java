package com.lectek.android.lereader.binding.model.user;

import java.util.ArrayList;

import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.tianyi.ContentInfo;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;

/**
 * @author mingkg21
 * @email  mingkg21@gmail.com
 * @date   2013-8-29
 */
public class MyOrderModel extends PagingLoadModel<ContentInfo>{
	private int mPageSize = Integer.MAX_VALUE;
	private int totalCount = -1;
	@Override
	protected ArrayList<ContentInfo> onLoadCurrentPageData(
			int loadPage, int pageItemSize, Object... params) throws Exception {
//		if(totalCount<0){
//			totalCount = ApiProcess4TianYi.getInstance(getContext())
//					.queryUserOrderListCount();
//		}
//		ArrayList<ContentInfo> contentInfos = 
//				ApiProcess4TianYi.getInstance(getContext()).queryUserOrderList(loadPage * getPageItemSize(),pageItemSize);
//		if(contentInfos != null){
//			if(contentInfos.size() > getPageItemSize()){
//				mPageSize = getLastPage();
//				return null;
//			}else if (contentInfos.size() < getPageItemSize()) {
//				mPageSize = getLastPage();
//			}else if (contentInfos.size() == totalCount) {
//				mPageSize = getLastPage();
//			}
//		}
//		return contentInfos;
		
		String userId = PreferencesUtil.getInstance(getContext()).getUserId();
		String userName = PreferencesUtil.getInstance(getContext()).getUserName();
		String password = PreferencesUtil.getInstance(getContext()).getUserPSW();
		ApiProcess4Leyue.getInstance(getContext()).getOrderedBooks(userId, userName, password, 0, 10);
		return null;
	}

	@Override
	protected int getPageItemSize() {
		return 6;
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
