package com.lectek.android.lereader.binding.model.collect;


import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.CollectResultInfo;

import java.util.ArrayList;

/**
 * 收藏列表
 * @author donghz
 */
public class CollectListInfoModel extends BaseLoadNetDataModel<ArrayList<CollectResultInfo>> {

	@Override
	protected ArrayList<CollectResultInfo> onLoad(Object... params) throws Exception {
		Integer userId =Integer.parseInt(params[0].toString());
		Integer start =Integer.parseInt(params[1].toString());
		Integer count =Integer.parseInt(params[2].toString());
		return ApiProcess4Leyue.getInstance(getContext()).collectGetList(userId, start, count);
	}

}
