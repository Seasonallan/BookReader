package com.lectek.android.lereader.binding.model.user;

import android.text.TextUtils;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.openapi.ApiProcess4TianYi;
import com.lectek.android.lereader.storage.dbase.TianYiUserInfo;
import com.lectek.android.lereader.utils.UserManager;

/**
 * @author mingkg21
 * @email  mingkg21@gmail.com
 * @date   2013-8-29
 */
public class UserInfoModel extends BaseLoadNetDataModel<TianYiUserInfo> {
	@Override
	protected TianYiUserInfo onLoad(Object... params) throws Exception {
		TianYiUserInfo userInfo = UserManager.getInstance(getContext()).getUserInfo();
		if(userInfo == null || TextUtils.isEmpty(userInfo.getNickName())){
			TianYiUserInfo info = ApiProcess4TianYi.getInstance(getContext()).queryUserInfo(userInfo.getAccessToken());
			if(userInfo != null){
				info.setUserId(userInfo.getUserId());
			}
			UserManager.getInstance(getContext()).saveUserInfo(info);
			userInfo = info;
		}
		return userInfo;
	}
}
