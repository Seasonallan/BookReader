package com.lectek.android.lereader.binding.model.contentinfo;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.SupportCommentInfo;

public class BookCommentSupportModel extends BaseLoadNetDataModel<SupportCommentInfo> {

	@Override
	protected SupportCommentInfo onLoad(Object... params) throws Exception {
		int commentId = Integer.parseInt(params[0].toString());
		return ApiProcess4Leyue.getInstance(getContext()).doSupportComment(commentId);
	}

}
