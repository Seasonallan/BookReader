package com.lectek.android.lereader.binding.model.user;

import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.CommonResultInfo;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;

public class PersonInfoEditModel extends BaseLoadNetDataModel<CommonResultInfo>{

	@Override
	protected CommonResultInfo onLoad(Object... params) throws Exception {
		CommonResultInfo info = null;
		boolean isLogin = PreferencesUtil.getInstance(getContext()).getIsLogin();
		if(isLogin && params != null){
			UserInfoLeyue userInfo = AccountManager.getInstance().getUserInfo();
			String userId = PreferencesUtil.getInstance(getContext()).getUserId();
			String account = PreferencesUtil.getInstance(getContext()).getUserName();
			String password = PreferencesUtil.getInstance(getContext()).getUserPSW();
			String nickName = (String) params[0];
			String signature = (String) params[1];
			String sexIndex = (String) params[2];
			boolean result = ApiProcess4Leyue.getInstance(getContext()).updateUserInfo(userId, nickName, account, password, password, null, null, sexIndex, null, null, signature);
			info = new CommonResultInfo();
			info.result = result + "";
		}
		return info;
	}

}
