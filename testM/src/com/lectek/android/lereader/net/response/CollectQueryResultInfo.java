package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 * 查询收藏状态
 * @author donghz
 *
 */
public class CollectQueryResultInfo extends BaseDao{
    
	@Json(name = "collectionId")
	public Integer collectionId;

	public Integer getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(Integer collectionId) {
		this.collectionId = collectionId;
	}

	@Override
	public String toString() {
		return "CollectQueryResultInfo [collectionId=" + collectionId + "]";
	}
}
