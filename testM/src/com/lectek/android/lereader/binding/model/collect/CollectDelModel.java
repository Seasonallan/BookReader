package com.lectek.android.lereader.binding.model.collect;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.CollectDeleteResultInfo;

/**
 * 删除收藏
 * @author donghz
 */
public class CollectDelModel extends BaseLoadNetDataModel<CollectDeleteResultInfo>{

	@Override
	protected CollectDeleteResultInfo onLoad(Object... params) throws Exception {
		Integer collectionId =Integer.parseInt(params[0].toString());
		Integer userId =Integer.parseInt(params[1].toString());
		return ApiProcess4Leyue.getInstance(getContext()).colletDelete(collectionId);
	}

}
