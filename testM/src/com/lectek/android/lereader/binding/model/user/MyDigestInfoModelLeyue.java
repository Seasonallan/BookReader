package com.lectek.android.lereader.binding.model.user;


import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyDigestInfoModelLeyue extends PagingLoadModel<BookDigests> {

	private int mPageSize = Integer.MAX_VALUE;
	private int mPageItemSize = 20;
	private int mLoadPage = 0;
	private int start = 0;
	private int totalCurPageCount;
	private BookDigests mBookDigest;

	public MyDigestInfoModelLeyue(BookDigests digest) {
		mBookDigest = digest;
	}

	@Override
	protected ArrayList<BookDigests> onLoadCurrentPageData(int loadPage,
			int pageItemSize, Object... params) throws Exception {
		ArrayList<BookDigests> res = BookDigestsDB.getInstance()
				.getListBookDigests(mBookDigest.getContentId());
		Collections.sort(res, new SortByDate());
		 
		return res;

	}
 
	@Override
	protected int getPageItemSize() {
		return mPageItemSize;
	}

	@Override
	protected Integer getPageSize() {
		return mPageSize;
	}

	class SortByDate implements Comparator<BookDigests> {
		@Override
		public int compare(BookDigests obj1, BookDigests obj2) {
			BookDigests bookDigests1 = obj1;
			BookDigests bookDigests2 = obj2;
			if (bookDigests1.getDate() <= bookDigests2.getDate()) {
				return 1;
			} else {
				return -1;
			}

		}
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
