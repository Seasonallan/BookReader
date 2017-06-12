package com.lectek.android.lereader.binding.model.user;

import java.util.ArrayList;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.openapi.ApiProcess4TianYi;
import com.lectek.android.lereader.net.response.tianyi.PointRule;

/**
 * @author mingkg21
 * @email  mingkg21@gmail.com
 * @date   2013-8-29
 */
public class PointRechargeModel extends BaseLoadNetDataModel<ArrayList<PointRule>> {
	@Override
	protected ArrayList<PointRule> onLoad(Object... params) throws Exception {
		ArrayList<PointRule> pointRules = ApiProcess4TianYi.getInstance(getContext()).getPointRuleList();
		return pointRules;
	}
}
