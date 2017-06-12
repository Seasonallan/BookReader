package com.lectek.android.lereader.binding.model.collect;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.CollectAddResultInfo;

/**
 * 添加收藏
 * @author donghz
 *  
 */
public class CollectAddModel extends BaseLoadNetDataModel<CollectAddResultInfo> {

	@Override
	protected CollectAddResultInfo onLoad(Object... params) throws Exception {
		int userId =Integer.parseInt(params[0].toString());
		int resourceId =Integer.parseInt(params[1].toString());
		int resourceType = 1;
		return ApiProcess4Leyue.getInstance(getContext()).collectAdd(resourceId, resourceType, null);
	}

}
