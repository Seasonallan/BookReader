package com.lectek.android.lereader.binding.model.bookCity;

import java.util.ArrayList;

import com.lectek.android.lereader.binding.model.BaseLoadDataModel;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.permanent.LeyueConst;

/**
 * 书城二级界面数据获取接口
 * @author Administrator
 *
 */
public class BookCityBookListModelLeyue extends PagingLoadModel<ContentInfoLeyue> {

	public static final String BOOK_CITY_BOOKLIST_CACHE_GROUP_KEY = "BOOK_CITY_BOOKLIST_CACHE_GROUP_KEY";
	private int mColumn;
	
	public BookCityBookListModelLeyue(int column) {
		super();
		this.mColumn = column;
	}

	@Override
	protected ArrayList<ContentInfoLeyue> onLoadCurrentPageData(int loadPage,
			int pageItemSize, Object... params) throws Exception {
		ArrayList<ContentInfoLeyue> bookCityBooks = null;
		
		//第一页先取缓存数据
		if(loadPage == 0){
			bookCityBooks = loadCacheData();
			if(bookCityBooks != null && bookCityBooks.size() > 0){
				return bookCityBooks;
			}
		}
		bookCityBooks = ApiProcess4Leyue.getInstance(getContext()).getBookCityNewBookRecommend(mColumn,loadPage*getPageItemSize(),getPageItemSize());
		return bookCityBooks;
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
		return BOOK_CITY_BOOKLIST_CACHE_GROUP_KEY;
	}

	@Override
	protected String getKey() {
		// TODO Auto-generated method stub
		return String.format("%s", mColumn);
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
