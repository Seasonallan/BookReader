package com.lectek.android.lereader.binding.model.collect;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.CollectQueryResultInfo;

/**
 * 查询收藏状态
 * @author donghz
 */
public class CollectQueryModel extends BaseLoadNetDataModel<CollectQueryResultInfo>{

	@Override
	protected CollectQueryResultInfo onLoad(Object... params) throws Exception {
		Integer userId=null;
		Integer resourceId =null;
		if(params[0]!=null){
			 userId =Integer.parseInt(params[0].toString());
		}
		if(params[1]!=null){
			resourceId =Integer.parseInt(params[1].toString());
		}
		return ApiProcess4Leyue.getInstance(getContext()).colletQuery(resourceId);
	}

}
