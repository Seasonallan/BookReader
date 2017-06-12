package com.lectek.android.lereader.binding.model.contentinfo;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.BookCommentDetail;

public class BookCommentDetailModel extends BaseLoadNetDataModel<BookCommentDetail> {

	@Override
	protected BookCommentDetail onLoad(Object... params) throws Exception {
		if(params == null)
			return null;
		
		String commentId = params[0].toString();
		
		return ApiProcess4Leyue.getInstance(getContext()).getBookCommentDetail(commentId, 0, 30);
	}

}
