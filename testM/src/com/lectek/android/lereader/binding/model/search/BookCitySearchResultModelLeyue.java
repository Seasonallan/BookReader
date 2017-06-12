package com.lectek.android.lereader.binding.model.search;

import java.util.ArrayList;

import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.permanent.LeyueConst;

public class BookCitySearchResultModelLeyue extends PagingLoadModel<ContentInfoLeyue> {

	private String mkeyWord;
	
	public void setSearchKeyWork(String keyword){
		this.mkeyWord = keyword;
	}
	@Override
	protected ArrayList<ContentInfoLeyue> onLoadCurrentPageData(int loadPage,
			int pageItemSize, Object... params) throws Exception {
		ArrayList<ContentInfoLeyue> resultList = ApiProcess4Leyue.getInstance(getContext()).getBookCitySearchResult(mkeyWord,loadPage*getPageItemSize(),getPageItemSize());
		return resultList;
	}

	@Override
	protected int getPageItemSize() {
		return LeyueConst.pageLoadingPageItemSize;
	}

	@Override
	protected Integer getPageSize() {
		return LeyueConst.pageLoadingPageSize;
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
