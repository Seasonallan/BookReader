package com.lectek.android.lereader.binding.model.contentinfo;

import java.util.ArrayList;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.BookCommentInfo;

public class BookCommentHotModel extends BaseLoadNetDataModel<ArrayList<BookCommentInfo>> {

	@Override
	protected ArrayList<BookCommentInfo> onLoad(Object... params)
			throws Exception {
		
		if(params == null)
			return null;
		
		int count = 30;
		String bookId = null;
		if(params.length >= 1){
			bookId = params[0].toString();
			if(params.length >= 2) {
				count = Integer.parseInt(params[1].toString());
			}
		}
		return ApiProcess4Leyue.getInstance(getContext()).getHotBookCommentListByBookId(bookId, 0, count);
	}

}
