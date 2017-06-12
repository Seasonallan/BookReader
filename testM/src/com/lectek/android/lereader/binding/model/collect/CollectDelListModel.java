package com.lectek.android.lereader.binding.model.collect;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.CollectCancelListResultItem;

import java.util.ArrayList;

/**
 * 
 * @author Administrator
 *
 */
public class CollectDelListModel extends BaseLoadNetDataModel<ArrayList<CollectCancelListResultItem>> {

	@Override
	protected ArrayList<CollectCancelListResultItem> onLoad(Object... params)throws Exception {
		String collectionIds =null;
		Integer userId = null;
		if(params[0]!=null){
			collectionIds =params[0].toString();
		}
		if(params[1]!=null){
			userId =Integer.parseInt(params[1].toString());
		}
		return ApiProcess4Leyue.getInstance(getContext()).collectCancelList(collectionIds,userId);
	}

}
