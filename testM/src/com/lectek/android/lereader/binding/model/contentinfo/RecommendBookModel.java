package com.lectek.android.lereader.binding.model.contentinfo;

import java.util.ArrayList;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;

public class RecommendBookModel extends BaseLoadNetDataModel<ArrayList<ContentInfoLeyue>> {

	@Override
	protected ArrayList<ContentInfoLeyue> onLoad(Object... params) throws Exception {
		if(params == null)
			return null;
		
		String bookId = params[0].toString();
		ArrayList<ContentInfoLeyue> list = ApiProcess4Leyue.getInstance(getContext()).getRecommendedBookByBookId(bookId,0,3);
		return list;
	}

}
