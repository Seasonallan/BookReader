package com.lectek.android.lereader.binding.model.contentinfo;

import java.util.ArrayList;

import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.BookCommentInfo;

public class BookCommentListPagingloadModel extends PagingLoadModel<BookCommentInfo> {
	
	private int mPageSize = Integer.MAX_VALUE;
	private int mPageItemSize = 30;
	private String mBookId;

	public void setmBookId(String mBookId) {
		this.mBookId = mBookId;
	}

	@Override
	protected ArrayList<BookCommentInfo> onLoadCurrentPageData(
			int loadPage, int pageItemSize, Object... params) throws Exception {
		return ApiProcess4Leyue.getInstance(getContext()).getLatestBookCommentListByBookId(mBookId, loadPage*getPageItemSize(), getPageItemSize());
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
