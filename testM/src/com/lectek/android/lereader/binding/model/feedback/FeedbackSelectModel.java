package com.lectek.android.lereader.binding.model.feedback;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.FeedbackSelectReplyInfo;

/**
 * 查询意见反馈是否是关闭状态
 * @author donghz
 * @date 2014-3-22
 */
public class FeedbackSelectModel extends BaseLoadNetDataModel<FeedbackSelectReplyInfo> {
	@Override
	protected FeedbackSelectReplyInfo onLoad(Object... params) throws Exception {
		if(params ==null)
			return null;
		String feedbackId =null;
		if(params[0]!=null){
			 feedbackId = params[0].toString();
		}
		return ApiProcess4Leyue.getInstance(getContext()).getFeedbackSelect(feedbackId);
	}
}
