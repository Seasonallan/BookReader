package com.lectek.android.lereader.lib.account.thirdPartApi;

import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public abstract class AbsWebClient extends WebViewClient{

	public abstract void onSettingWebView(WebView webSetting);
	
	public abstract void onInit(Object...params);

	public abstract void loadUrl();
	
	public abstract void onDestroy();
	
	/**
	 * 有些第三方api需要处理onActivityResult
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 * @return 如果有处理返回true, 否则false
	 */
	public boolean handleActivityResult(int requestCode, int resultCode, Intent data){return false;}
}
