package com.lectek.android.lereader.binding.model.user;

import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;

public class UserInfoModelLeyue extends BaseLoadNetDataModel<UserInfoLeyue> {

	@Override
	protected UserInfoLeyue onLoad(Object... params) throws Exception {
//		UserInfoLeyue userInfo = AccountManage.getInstence().getmUserInfo();
//		if(userInfo == null){
//			UserInfoLeyue info = ApiProcess4Leyue.getInstance(getContext()).getUserInfo(params[0].toString(), params[1].toString(), params[2].toString());
		UserInfoLeyue info = ApiProcess4Leyue.getInstance(getContext()).getUserInfo(params[0].toString());
			info.setUserId(PreferencesUtil.getInstance(getContext()).getUserId());
//			AccountManage_old.getInstence().setmUserInfo(info);
			AccountManager.getInstance().setUserInfo(info);
//			userInfo = info;
//		}
			
		return info;
	}

}
