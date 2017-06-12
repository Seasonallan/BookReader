package com.lectek.android.lereader.net.response;


import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 * 批量取消收藏返回结果
 * @author donghz
 *
 */
public class CollectCancelListResultItem extends BaseDao{

	@Json(name = "collectionId")
	public Integer collectionId;
	
	@Json(name = "result")
	public Boolean result;
	
	public Integer getCollectionId() {
		return collectionId;
	}
	public void setCollectionId(Integer collectionId) {
		this.collectionId = collectionId;
	}
	public Boolean getResult() {
		return result;
	}
	public void setResult(Boolean result) {
		this.result = result;
	}
	public CollectCancelListResultItem(Integer collectionId, Boolean result) {
		super();
		this.collectionId = collectionId;
		this.result = result;
	}

}
