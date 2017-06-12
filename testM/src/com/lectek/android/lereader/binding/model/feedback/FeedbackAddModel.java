package com.lectek.android.lereader.binding.model.feedback;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.IHttpRequest4Leyue;
import com.lectek.android.lereader.net.response.CommonResultInfo;

public class FeedbackAddModel extends BaseLoadNetDataModel<CommonResultInfo>{

	@Override
	protected CommonResultInfo onLoad(Object... params) throws Exception {
		if(params ==null)
			return null;
		String account = null;
		String imei = null;
		String simCode = null;
		String sourceType = null;
		String deviceModel = null;
		String mdnCode = null;
		String appVserion = null;
		String content = null;
		String contentType =null;

		if(params[1]!=null){
			account = params[1].toString();
		}

		if(params[2]!=null){
			 imei = params[2].toString();
		}
		
		if(params[3]!=null){
			simCode = params[3].toString();
		}
		if(params[4]!=null){
			sourceType = params[4].toString();
		}
		if(params[5]!=null){
			deviceModel = params[5].toString();
		}
		if(params[6]!=null){
			mdnCode = params[6].toString();
		}
		if(params[7]!=null){
			appVserion = params[7].toString();
		}
		if(params[8]!=null){
			content = params[8].toString();
		}
		if(params[9]!=null){
			contentType =params[9].toString();
		}
		return ApiProcess4Leyue.getInstance(getContext()).addFeedbackInfo(account, imei,
                simCode, sourceType, deviceModel, mdnCode, appVserion, content, contentType);
	}

}
