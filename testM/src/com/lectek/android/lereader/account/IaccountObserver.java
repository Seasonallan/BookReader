package com.lectek.android.lereader.account;

import com.lectek.android.lereader.lib.utils.IProguardFilterOnlyPublic;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;

public interface IaccountObserver extends IProguardFilterOnlyPublic{

	public void onAccountChanged();
	public void onLoginComplete(boolean success, String msg, Object...params);
	public void onGetUserInfo(UserInfoLeyue userInfo);
}
