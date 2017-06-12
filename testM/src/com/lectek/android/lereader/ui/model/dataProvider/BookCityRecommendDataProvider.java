package com.lectek.android.lereader.ui.model.dataProvider;

import com.lectek.android.lereader.ui.model.dataDefine.BookCityRecommendData;

/**
 * 书城推荐界面数据提供者
 * @author chends@lectek.com
 * @date 2014/07/08
 */
public class BookCityRecommendDataProvider extends AbsUiDataProvider<BookCityRecommendData> {

	public BookCityRecommendDataProvider() {
		super(new BookCityRecommendData());
	}

	@Override
	public void onRequestData(String dataID, BookCityRecommendData outData, Object[] params) {
		
	}
	
	@Override
	protected boolean onReadCachData(BookCityRecommendData data) {
		return false;
	}
}
