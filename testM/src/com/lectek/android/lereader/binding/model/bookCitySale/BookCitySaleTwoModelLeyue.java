package com.lectek.android.lereader.binding.model.bookCitySale;

import java.util.ArrayList;

import com.lectek.android.lereader.binding.model.BaseLoadDataModel;
import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.permanent.LeyueConst;
/**
 * 书籍排行接口
 * @author yll
 *
 */
public class BookCitySaleTwoModelLeyue extends PagingLoadModel<ContentInfoLeyue>  {

	public static final String BOOK_CITY_RANK_BOOKS_CACHE_GROUP_KEY = "BOOK_CITY_RANK_BOOKS_CACHE_GROUP_KEY";
	private int mRankid;
	
	public void setRankid(int rankid){
		this.mRankid = rankid;
	}

	@Override
	protected ArrayList<ContentInfoLeyue> onLoadCurrentPageData(int loadPage,
			int pageItemSize, Object... params) throws Exception {
		ArrayList<ContentInfoLeyue> bookCitySaleTwos = null;
		
		//第一页先取缓存数据
		if(loadPage == 0){
			bookCitySaleTwos = loadCacheData();
			if(bookCitySaleTwos != null && bookCitySaleTwos.size() > 0){
				return bookCitySaleTwos;
			}
		}
		bookCitySaleTwos = ApiProcess4Leyue.getInstance(getContext()).getBookCitySalesTwo(mRankid,loadPage*getPageItemSize(),getPageItemSize());
		return bookCitySaleTwos;
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
		return BOOK_CITY_RANK_BOOKS_CACHE_GROUP_KEY;
	}

	@Override
	protected String getKey() {
		return String.format("%s", mRankid);
	}

	@Override
	protected boolean isDataCacheEnabled() {
		return true;
	}

	@Override
	protected boolean isValidDataCacheEnabled() {
		return true;
	}
}
