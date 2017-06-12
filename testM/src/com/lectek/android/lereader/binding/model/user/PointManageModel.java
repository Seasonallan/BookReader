package com.lectek.android.lereader.binding.model.user;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.openapi.ApiProcess4TianYi;
import com.lectek.android.lereader.storage.dbase.TianYiUserInfo;
import com.lectek.android.lereader.utils.UserManager;

/**
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-9-9
 */
public class PointManageModel extends BaseLoadNetDataModel<TianYiUserInfo> {
	@Override
	protected TianYiUserInfo onLoad(Object... params) throws Exception {
		TianYiUserInfo info = ApiProcess4TianYi.getInstance(getContext()).queryUserInfo(null);
		TianYiUserInfo userInfo = UserManager.getInstance(getContext()).getUserInfo();
		if (info != null) {
			if (userInfo != null) {
				info.setUserId(userInfo.getUserId());
			}
			UserManager.getInstance(getContext()).saveUserInfo(info);
		} else {
			info = userInfo;
		}
		return info;
	}
}
