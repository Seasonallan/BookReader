package com.lectek.android.lereader.binding.model.user;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.openapi.ApiProcess4TianYi;
import com.lectek.android.lereader.net.response.tianyi.OrderedResult;

public class PointConvertModel extends BaseLoadNetDataModel<OrderedResult> {

	@Override
	protected OrderedResult onLoad(Object... params) throws Exception {
		if(params == null)
			return null;
		return ApiProcess4TianYi.getInstance(getContext()).pointConvert((String)params[0]);
	}

}
