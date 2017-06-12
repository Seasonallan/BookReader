package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 *  根据第三方id注册接口HTTP数据结构
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public class UserThridInfo  extends BaseDao{
	
	@Json(name = "userId")
	public String userId;
	
	@Json(name = "isUpdated")
	public boolean isUpdated;
	
	public boolean getIsUpdated() {
		return isUpdated;
	}

	public void setIsUpdated(boolean isUpdated) {
		this.isUpdated = isUpdated;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "UserThridInfo [userId=" + userId + ", isUpdated=" + isUpdated
				+ "]";
	}

}
