package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 * 删除收藏  返回信息
 * @author donghz
 * @date 2014-3-27
 */
public class CollectDeleteResultInfo extends BaseDao{
	
	@Json(name = "isDelete")
	public boolean isDelete;

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	@Override
	public String toString() {
		return "CollectDeleteResultInfo [isDelete=" + isDelete + "]";
	}
	
}
