package com.lectek.android.lereader.account.thirdPartApi.factory;

import com.lectek.android.lereader.lib.account.thirdPartApi.AbsWebClient;
import com.lectek.android.lereader.lib.account.thirdPartApi.IWebClient;
import com.lectek.android.lereader.lib.account.thirdPartApi.factory.AbsWebViewFactory;
import com.lectek.android.lereader.permanent.ThirdPartLoginConfig;


public class WebViewFactory extends AbsWebViewFactory{
	
	private static AbsWebViewFactory mInstance;
	
	public static synchronized AbsWebViewFactory getInstance() {
		if(mInstance == null) {
			mInstance = new WebViewFactory();
		}
		
		return mInstance;
	}
	
	public AbsWebClient getWebViewClientInstance(int type, Object...params) {
		
		IWebClient onWeiboRegistRunnadle = null;
		if(params != null && params.length > 0) {
			if(params[0] instanceof IWebClient) {
				onWeiboRegistRunnadle = (IWebClient)params[0];
			}
		}
		
		switch (type) {
		case ThirdPartLoginConfig.TYPE_QQ:
			return new QQLoginWebViewClient(onWeiboRegistRunnadle);
		case ThirdPartLoginConfig.TYPE_SINA:
			return new SinaLoginWebViewClient(onWeiboRegistRunnadle);
		case ThirdPartLoginConfig.TYPE_TY:
			return new TYLoginWebViewClient(onWeiboRegistRunnadle);	
		default:
			return null;
		}
	}
}
