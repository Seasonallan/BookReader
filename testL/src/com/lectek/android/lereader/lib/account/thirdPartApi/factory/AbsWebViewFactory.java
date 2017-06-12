package com.lectek.android.lereader.lib.account.thirdPartApi.factory;

import com.lectek.android.lereader.lib.account.thirdPartApi.AbsWebClient;

public abstract class AbsWebViewFactory {
	
	public abstract AbsWebClient getWebViewClientInstance(int type, Object...params);
	
}
