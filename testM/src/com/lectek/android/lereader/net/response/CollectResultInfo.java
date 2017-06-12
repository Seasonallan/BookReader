package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 * 收藏列表返回信息
 * @author  donghz
 * @date 2014-3-27
 */
public class CollectResultInfo extends BaseDao{
	
	@Json(name = "resourceId")
	public Integer resourceId;
	
	@Json(name = "collectionId")
	public Integer collectionId;
	
	@Json(name = "groupId")
	public Integer groupId;

	@Json(name = "bookInfoVo")
	public CollectResultBookInfo collectBookInfo;

	public Integer getResourceId() {
		return resourceId;
	}

	public void setResourceId(Integer resourceId) {
		this.resourceId = resourceId;
	}

	public Integer getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(Integer collectionId) {
		this.collectionId = collectionId;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public CollectResultBookInfo getCollectBookInfo() {
		return collectBookInfo;
	}

	public void setCollectBookInfo(CollectResultBookInfo collectBookInfo) {
		this.collectBookInfo = collectBookInfo;
	}

	@Override
	public String toString() {
		return "CollectResultListInfo [resourceId=" + resourceId
				+ ", collectionId=" + collectionId + ", groupId=" + groupId
				+ ", collectBookInfo=" + collectBookInfo + "]";
	}

}
