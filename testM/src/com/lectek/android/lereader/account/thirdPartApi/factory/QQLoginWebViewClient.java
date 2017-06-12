package com.lectek.android.lereader.account.thirdPartApi.factory;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.WebView;

import com.lectek.android.lereader.account.thirdPartApi.entity.UserInfo;
import com.lectek.android.lereader.account.thirdPartApi.net.AsyncHttpRequestRunner;
import com.lectek.android.lereader.account.thirdPartApi.net.Callback;
import com.lectek.android.lereader.account.thirdPartApi.net.OpenIDListener;
import com.lectek.android.lereader.account.thirdPartApi.net.UserInfoListenerForQQ;
import com.lectek.android.lereader.lib.account.thirdPartApi.AbsWebClient;
import com.lectek.android.lereader.lib.account.thirdPartApi.IWebClient;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.lib.utils.StringUtil;
import com.lectek.android.lereader.permanent.ThirdPartLoginConfig;

public class QQLoginWebViewClient extends AbsWebClient {

	private static final String TAG = "QqLoginWebViewClient";

	private IWebClient mWeiboRegistRunnadle;
	private WebView mWebView;

	public QQLoginWebViewClient(IWebClient iWeiboRegistRunnadle) {
		this.mWeiboRegistRunnadle = iWeiboRegistRunnadle;
	}

	@Override
	public void onInit(Object... params) {
	}

	@Override
	public void onDestroy() {
	}

	@Override
	public void loadUrl() {
//		if (mWeiboRegistRunnadle != null) {
//			mWeiboRegistRunnadle.showWaitTip();
//		}
		mWebView.loadUrl(ThirdPartLoginConfig.QQConfig.AUTHORIZE_URL);
	}

	@Override
	public void onSettingWebView(WebView webSetting) {
		mWebView = webSetting;
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (url.startsWith(ThirdPartLoginConfig.QQConfig.REDIRECT_URI + "?")) {
			String tempAccessToken = ThirdPartLoginConfig.QQConfig.getAccessToken(url);
			if(!StringUtil.isEmpty(tempAccessToken)) {
				getOpenId(tempAccessToken);
				return true;
			}else if (mWeiboRegistRunnadle != null) {
				mWeiboRegistRunnadle.dimissWaitTip();
			}
		}
		
		return super.shouldOverrideUrlLoading(view, url);
	}
	
	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		if (mWeiboRegistRunnadle != null) {
			mWeiboRegistRunnadle.showWaitTip();
		}
		super.onPageStarted(view, url, favicon);
	}
	
	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
		
		if (mWeiboRegistRunnadle != null && !url.startsWith(ThirdPartLoginConfig.QQConfig.REDIRECT_URI)) {
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

	/**
	 * 获取qq的openid,返回openid和用户名
	 * */
	private void getOpenId(final String accessToken) {
		if (!TextUtils.isEmpty(accessToken)) {
			// 用access token 来获取open id
			new AsyncHttpRequestRunner().request(ThirdPartLoginConfig.QQConfig.getAccessTokenUrl(accessToken), null, new OpenIDListener(new Callback() {

				@Override
				public void onSuccess(final Object obj) {
					// 成功后将openid当用户名使用
					final String openid = obj.toString();
					if (!TextUtils.isEmpty(openid)) {
						LogUtil.v(TAG, openid);
						
						new AsyncHttpRequestRunner().request(ThirdPartLoginConfig.QQConfig.getUserInfoUrl(accessToken, openid), null, 
								new UserInfoListenerForQQ(new Callback() {

							@Override
							public void onSuccess(Object paramObject) {
								UserInfo userInfo = (UserInfo) paramObject;
								LogUtil.v(TAG, userInfo.getNickName());
								if (mWeiboRegistRunnadle != null) {
									Bundle data = new Bundle();
									data.putString(ThirdPartLoginConfig.QQConfig.Extra_OpenID, openid);
									data.putString(ThirdPartLoginConfig.QQConfig.Extra_NickName, userInfo.getNickName());
									mWeiboRegistRunnadle.onSuccess(ThirdPartLoginConfig.TYPE_QQ, data);
								}
							}

							@Override
							public void onFail(int paramInt,
									String paramString) {
								if (mWeiboRegistRunnadle != null) {
									mWeiboRegistRunnadle.onFail(paramInt,paramString, null);
								}
							}

							@Override
							public void onCancel(int paramInt) {
								if (mWeiboRegistRunnadle != null) {
									mWeiboRegistRunnadle.onCancel();
								}
							}
						}));
						
					}
				}

				@Override
				public void onFail(int ret, final String msg) {
					if (mWeiboRegistRunnadle != null) {
						mWeiboRegistRunnadle.onFail(ret, msg, null);
					}
				}

				@Override
				public void onCancel(int paramInt) {
					if (mWeiboRegistRunnadle != null) {
						mWeiboRegistRunnadle.onCancel();
					}
				}
			}));
			
		}
	}
}
