package com.lectek.android.lereader.ui.templete;

import android.content.Context;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.ui.model.dataDefine.BookCityRecommendData;
import com.lectek.android.lereader.ui.model.dataProvider.AbsUiDataProvider;
import com.lectek.android.lereader.ui.model.dataProvider.BookCityRecommendDataProvider;

/**
 * 书城推荐界面模板
 * @author chends@lectek.com
 * @date 2014/07/08
 */
public class BookCityRecommendTemplete extends AbsTemplete<BookCityRecommendData> {

	public BookCityRecommendTemplete(Context context) {
		super(context, R.layout.a_key_signin);
	}

	@Override
	protected AbsUiDataProvider<BookCityRecommendData> onGetDataProvider() {
		return new BookCityRecommendDataProvider();
	}
}
