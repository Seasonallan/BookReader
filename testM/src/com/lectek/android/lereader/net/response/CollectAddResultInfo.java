package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 * 添加收藏返回信息
 * @author donghz
 * @date 2014-3-27
 */
public class CollectAddResultInfo extends BaseDao{
	
	@Json(name = "desc")
	public String desc;
	
	@Json(name = "collectionId")
	public Integer collectionId;
	
	@Json(name = "collectionGroupId")
	public Integer collectionGroupId;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Integer getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(Integer collectionId) {
		this.collectionId = collectionId;
	}

	public Integer getCollectionGroupId() {
		return collectionGroupId;
	}

	public void setCollectionGroupId(Integer collectionGroupId) {
		this.collectionGroupId = collectionGroupId;
	}

	@Override
	public String toString() {
		return "CollectAddResultInfo [code=" + ", desc=" + desc
				+ ", collectionId=" + collectionId + ", collectionGroupId="
				+ collectionGroupId + "]";
	}
}
