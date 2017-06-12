package com.lectek.android.lereader.binding.model.feedback;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.FeedBackInfo;
import com.lectek.android.lereader.permanent.LeyueConst;

import java.util.ArrayList;

/**
 * 获取反馈列表
 * @author donghz
 *
 */
public class FeedBackGetListInfoModel extends BaseLoadNetDataModel<ArrayList<FeedBackInfo>>{

	@Override
	protected ArrayList<FeedBackInfo> onLoad(Object... params) throws Exception {
		if(params ==null)
			return null;
		Integer  userId = null;
		String imei = null;
		Integer start = 0;
		Integer count = 1;
		String lastUpdateTime ="";
		if(!params[0].toString().equals(LeyueConst.TOURIST_USER_ID)){
			userId = Integer.parseInt(params[0].toString());
		}else{
			imei = params[1].toString();
		}
		lastUpdateTime =params[2].toString();
		return ApiProcess4Leyue.getInstance(getContext()).getFeedBackInfoList(userId,imei, start, count,lastUpdateTime);
	}

	

}
