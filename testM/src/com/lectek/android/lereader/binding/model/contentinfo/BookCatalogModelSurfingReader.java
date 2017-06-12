package com.lectek.android.lereader.binding.model.contentinfo;

import java.util.ArrayList;

import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.net.openapi.ApiProcess4TianYi;
import com.lectek.android.lereader.net.response.BookCatalog;
import com.lectek.android.lereader.net.response.tianyi.Chapter;

public class BookCatalogModelSurfingReader extends PagingLoadModel<BookCatalog> {
	
	private int mPageSize = Integer.MAX_VALUE;
	private int mPageItemSize = 3000;
	private String mBookId;
	
	public void setmBookId(String mBookId) {
		this.mBookId = mBookId;
	}

	@Override
	protected ArrayList<BookCatalog> onLoadCurrentPageData(int loadPage,
			int pageItemSize, Object... params) throws Exception {
		ArrayList<BookCatalog> list = new ArrayList<BookCatalog>();
		ArrayList<Chapter> tempList = ApiProcess4TianYi.getInstance(getContext()).getBookChapterListNew(mBookId,loadPage*getPageItemSize(), getPageItemSize());
		if(tempList != null){
			for(int i = 0; i < tempList.size(); i++){
				Chapter chapter = tempList.get(i);
				BookCatalog catalog = new BookCatalog();
				catalog.setBookId(mBookId);
				catalog.setName(chapter.getChapterName());
				catalog.setCalpoint(chapter.getIsFree()+"");
				catalog.setPath(i+"");
				list.add(catalog);
			}
			return list;
		}
		return null;
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
