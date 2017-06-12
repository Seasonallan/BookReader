package com.lectek.android.lereader.binding.model.feedback;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.permanent.LeyueConst;

/**
 * 回复意见反馈
 * @author donghz
 * @ 2013-3-22
 */
public class FeedbackReplyModel extends BaseLoadNetDataModel<Boolean>{

	@Override
	protected Boolean onLoad(Object... params)
			throws Exception {
		if(params ==null)
			return false;
		Integer  userId = null;
		String account = null;
		String imei = null;
		String feedbackId = null;
		String content = null;
		String contentType = null;

		if(params[1]!=null){
			account = params[1].toString();
		}
		if(params[2]!=null){
			imei = params[2].toString();
		}
		if(params[3]!=null){
			feedbackId = params[3].toString();
		}
		if(params[4]!=null){
			content = params[4].toString();
		}
		if(params[5]!=null){
			contentType =params[5].toString();
		}
		return ApiProcess4Leyue.getInstance(getContext()).addReplyFeedbackInfo( account, imei, feedbackId, content,contentType);
	}

	 
}
