package com.lectek.android.lereader.lib.account.thirdPartApi;

import com.lectek.android.lereader.lib.utils.IProguardFilterOnlyPublic;

import android.content.Context;
import android.os.Bundle;

public interface IWebClient extends IProguardFilterOnlyPublic{
	public void showWaitTip();
	public void dimissWaitTip();
	public void onSuccess(int tag, Bundle data);
	public void onFail(int code, String msg, String failUrl);
	public void onCancel();
	public Context getContext();
}
