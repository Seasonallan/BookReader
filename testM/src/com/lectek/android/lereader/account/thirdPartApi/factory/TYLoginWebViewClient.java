package com.lectek.android.lereader.account.thirdPartApi.factory;

import java.util.HashMap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.account.thirdPartApi.AbsWebClient;
import com.lectek.android.lereader.lib.account.thirdPartApi.IWebClient;
import com.lectek.android.lereader.lib.utils.StringUtil;
import com.lectek.android.lereader.permanent.ThirdPartLoginConfig;

public class TYLoginWebViewClient extends AbsWebClient {
	
	private WebView mWebView;
	private IWebClient mWeiboRegistRunnadle;
	
	public TYLoginWebViewClient(IWebClient iWeiboRegistRunnadle){
		this.mWeiboRegistRunnadle = iWeiboRegistRunnadle;
	}

	@Override
	public void onInit(Object...params) {
		if(params != null && params.length >=2 && (params[1] instanceof View) && (params[0] instanceof Intent)) {
			if(((Intent)params[0]).getBooleanExtra(ThirdPartLoginConfig.TYConfig.Extra_ShowSwitchTip, false)) {
				((View)params[1]).findViewById(R.id.ty_pay_tip).setVisibility(View.VISIBLE);
			}
		}
	}
	
	@Override
	public void onDestroy() {}
	
	@Override
	public void loadUrl() {
		mWebView.loadUrl(ThirdPartLoginConfig.TYConfig.getAuthorizeUrl());
	}
	
	@Override
	public void onSettingWebView(WebView webSetting) {
		mWebView = webSetting;
	}
	
	protected boolean handleResult(WebView view, String url) {
		if(url.contains(ThirdPartLoginConfig.TYConfig.REDIRECT_URI)) {
			Uri uri = Uri.parse(url);
//			try {
				HashMap<String, String> map = parseResult(uri.getEncodedFragment());
				boolean success = map.get(ThirdPartLoginConfig.TYConfig.Extra_AccessToken) != null;
				
				if(mWeiboRegistRunnadle != null) {
					if(success) {
						Bundle resultData = new Bundle();
						resultData.putString(ThirdPartLoginConfig.TYConfig.Extra_AccessToken, map.get(ThirdPartLoginConfig.TYConfig.Extra_AccessToken));
						resultData.putString(ThirdPartLoginConfig.TYConfig.Extra_UserID, map.get(ThirdPartLoginConfig.TYConfig.Extra_UserID) + "");
						resultData.putString(ThirdPartLoginConfig.TYConfig.Extra_RefreshToken, map.get(ThirdPartLoginConfig.TYConfig.Extra_RefreshToken) + "");
						mWeiboRegistRunnadle.onSuccess(ThirdPartLoginConfig.TYPE_TY, resultData);
					}else {
						mWeiboRegistRunnadle.onFail(-1, null, url);
					}
				}
				
				CookieSyncManager.createInstance(view.getContext());
				CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.removeAllCookie();
				
				return true;
				
//			}catch(UnsupportedOperationException e){
//			}catch(NullPointerException e){}
		}
		
		return false;
	}
	
	private HashMap<String, String> parseResult(String encodedFragment){
		HashMap<String, String> result = new HashMap<String, String>();
	    
		if(!StringUtil.isEmpty(encodedFragment)) {
			String[] arr = encodedFragment.split("&");
			for (String item : arr) {
		    	String[] data = item.split("=");
		    	result.put(data[0], data[1]);
		    }
		}
		
	    return result;
	}
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		
		if(handleResult(view, url)){
			return true;
		}
		return super.shouldOverrideUrlLoading(view, url);
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		if(mWeiboRegistRunnadle != null) {
			mWeiboRegistRunnadle.showWaitTip();
		}
		super.onPageStarted(view, url, favicon);
	}
	
	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
		if(mWeiboRegistRunnadle != null){
    		mWeiboRegistRunnadle.dimissWaitTip();
    	}
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		super.onReceivedError(view, errorCode, description, failingUrl);

		if(mWeiboRegistRunnadle != null){
    		mWeiboRegistRunnadle.onFail(errorCode, description, failingUrl);
    	}
	}

	@Override
	public void onFormResubmission(WebView view, Message dontResend,
			Message resend) {
		if (android.os.Build.VERSION.SDK_INT <= 7) {
			resend.sendToTarget();
		}

		super.onFormResubmission(view, dontResend, resend);
	}
}
