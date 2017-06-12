package com.lectek.android.lereader.binding.model.feedback;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;

/**
 * 意见反馈评分
 * @author donghz
 * @2014-3-22
 */
public class FeedbackScoreModel extends BaseLoadNetDataModel<Boolean>{

	@Override
	protected Boolean onLoad(Object... params) throws Exception {
		if(params ==null)
			return false;
		String feedbackId =null;
		Integer score =null;
		if(params[0]!=null){
			 feedbackId = params[0].toString();
		}
		if(params[1]!=null){
			 score = Integer.parseInt(params[1].toString());
		}
		return ApiProcess4Leyue.getInstance(getContext()).doFeedbackScore(feedbackId,score);
	}

}
