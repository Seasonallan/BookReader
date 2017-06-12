package com.lectek.android.lereader.binding.model.bookCityClassify;

import java.util.ArrayList;

import com.lectek.android.lereader.binding.model.BaseLoadDataModel;
import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.permanent.LeyueConst;

public class BookCityClassifyDetailModelLeyue extends PagingLoadModel<ContentInfoLeyue>  {

	public static final String BOOK_CITY_CLASSIFY_DETAIL_BOOKS_GROUP_KEY = "BOOK_CITY_CLASSIFY_DETAIL_BOOKS_GROUP_KEY";
	private int mBookType;
	
	
	public BookCityClassifyDetailModelLeyue(int bookType) {
		super();
		this.mBookType = bookType;
	}

	@Override
	protected ArrayList<ContentInfoLeyue> onLoadCurrentPageData(int loadPage,
			int pageItemSize, Object... params) throws Exception {
		ArrayList<ContentInfoLeyue> bookCityClassify = null;
		
		//第一页先取缓存数据
		if(loadPage == 0){
			bookCityClassify = loadCacheData();
			if(bookCityClassify != null && bookCityClassify.size() > 0){
				return bookCityClassify;
			}
		}
		bookCityClassify= ApiProcess4Leyue.getInstance(getContext()).getBookCityClassifyDetail(mBookType,loadPage*getPageItemSize(),getPageItemSize(),null);
		return bookCityClassify;
	}

	@Override
	protected int getPageItemSize() {
		// TODO Auto-generated method stub
		return LeyueConst.pageLoadingPageItemSize;
	}

	@Override
	protected Integer getPageSize() {
		// TODO Auto-generated method stub
		return LeyueConst.pageLoadingPageSize;
	}

	@Override
	protected String getGroupKey() {
		// TODO Auto-generated method stub
		return BOOK_CITY_CLASSIFY_DETAIL_BOOKS_GROUP_KEY;
	}

	@Override
	protected String getKey() {
		// TODO Auto-generated method stub
		return String.format("%s", mBookType);
	}

	@Override
	protected boolean isDataCacheEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean isValidDataCacheEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
	 
}
