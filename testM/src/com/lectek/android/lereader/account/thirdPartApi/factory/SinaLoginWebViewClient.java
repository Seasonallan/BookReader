package com.lectek.android.lereader.account.thirdPartApi.factory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;

import com.lectek.android.lereader.account.thirdPartApi.net.AsyncHttpRequestRunner;
import com.lectek.android.lereader.account.thirdPartApi.net.Callback;
import com.lectek.android.lereader.account.thirdPartApi.net.UserInfoListenerForSina;
import com.lectek.android.lereader.lib.account.thirdPartApi.AbsWebClient;
import com.lectek.android.lereader.lib.account.thirdPartApi.IWebClient;
import com.lectek.android.lereader.lib.utils.HttpUtil;
import com.lectek.android.lereader.permanent.ThirdPartLoginConfig;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;


public class SinaLoginWebViewClient extends AbsWebClient{
	
	private static final int REQUEST_CODE = SinaLoginWebViewClient.class.hashCode();
	
	private IWebClient mWeiboRegistRunnadle;
	private WebView mWebView;

//	private ServiceConnection mConn;
	
	public SinaLoginWebViewClient(IWebClient onWeiboRegistRunnadle){
		this.mWeiboRegistRunnadle = onWeiboRegistRunnadle;
	}
	
	@Override
	public void onInit(Object...params) {}
	
	@Override
	public void onDestroy() {};
	
	@Override
	public void loadUrl() {
		if(mWeiboRegistRunnadle != null) {
        	mWeiboRegistRunnadle.showWaitTip();
        }
		mWebView.loadUrl(ThirdPartLoginConfig.SinaConfig.getAuthorizeUrl());
	}
	
	@Override
	public void onSettingWebView(WebView webSetting) {
		mWebView = webSetting;
	}
	
	@Override
	public boolean handleActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode == REQUEST_CODE && mWeiboRegistRunnadle != null) {
            // Successfully redirected.
            if (resultCode == Activity.RESULT_OK) {
                // Check OAuth 2.0/2.10 error code.
                String error = data.getStringExtra("error");
                if (TextUtils.isEmpty(error)) {
                    error = data.getStringExtra("error_type");
                }
                // error occurred.
                if (!TextUtils.isEmpty(error)) {
                    if (error.equals("access_denied") || error.equals("OAuthAccessDeniedException")) {
                    	error = error + " access_denied or OAuthAccessDeniedException";
                    } else {
                        String description = data.getStringExtra("error_description");
                        if (!TextUtils.isEmpty(description)) {
                            error = error + ":" + description;
                        }
                    }
                    
                    mWeiboRegistRunnadle.onFail(-1, error, null);
                    
                } else {
                	String uid = data.getStringExtra("uid");
                	String userName = data.getStringExtra("userName") + "";
                	String accessToken  = data.getStringExtra("access_token") + "";
                	PreferencesUtil.getInstance(mWeiboRegistRunnadle.getContext()).setSinaAccessToken(accessToken);
                	if(!TextUtils.isEmpty(uid)){
                		Bundle resultData = new Bundle();
                		resultData.putString(ThirdPartLoginConfig.SinaConfig.Extra_UID, uid);
                		resultData.putString(ThirdPartLoginConfig.SinaConfig.Extra_Nick_Name, userName);
                		mWeiboRegistRunnadle.onSuccess(ThirdPartLoginConfig.TYPE_SINA, resultData);
                	}
                }
                // An error occurred before we could be redirected.
            } else if (resultCode == Activity.RESULT_CANCELED) {
            	mWeiboRegistRunnadle.onCancel();
            }else {
            	mWeiboRegistRunnadle.onFail(-1, null, null);
            }
            
            return true;
        }
		
		return false;
	}
	
//	@Override
//    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        view.loadUrl(url);
//		return super.shouldOverrideUrlLoading(view, url);
//    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description,
            String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        if(mWeiboRegistRunnadle != null){
    		mWeiboRegistRunnadle.onFail(errorCode, description, failingUrl);
    	}
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
    	if (url.startsWith(ThirdPartLoginConfig.SinaConfig.REDIRECT_URI)) {
            handleRedirectUrl(url);
            view.stopLoading();
            return;
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
    
    private void handleRedirectUrl(String url) {
		Bundle values = HttpUtil.parseUrl(url);
        String error = values.getString("error");
        String error_code = values.getString("error_code");
        if (TextUtils.isEmpty(error) && TextUtils.isEmpty(error_code)){
        	String access_token = values.getString(ThirdPartLoginConfig.SinaConfig.Extra_Access_Token);
	        String uid = values.getString(ThirdPartLoginConfig.SinaConfig.Extra_UID);
	        Bundle bundle = new Bundle();
			bundle.putString(ThirdPartLoginConfig.SinaConfig.Extra_Access_Token, access_token);
			bundle.putString(ThirdPartLoginConfig.SinaConfig.Extra_UID, uid);
			String tempUrl = ThirdPartLoginConfig.SinaConfig.SERVER + "users/show.json";
	        getUserInfo(uid, tempUrl, bundle);
        }else if(mWeiboRegistRunnadle != null){
        	mWeiboRegistRunnadle.onFail(-1, "error code:" + error_code + " description:" + error, url);
        }
    }
    
    /**
	 * 获取sina用户昵称
	 * */
	private void getUserInfo(final String account, String url, Bundle bundle){
		if (mWeiboRegistRunnadle != null) {
			mWeiboRegistRunnadle.showWaitTip();
			
			new AsyncHttpRequestRunner().request(url, bundle, new UserInfoListenerForSina(new Callback() {
				
				@Override
				public void onSuccess(final Object obj) {
					String nickName = obj.toString();
					if(mWeiboRegistRunnadle != null){
						Bundle data = new Bundle();
						data.putString(ThirdPartLoginConfig.SinaConfig.Extra_UID, account);
						data.putString(ThirdPartLoginConfig.SinaConfig.Extra_Nick_Name, nickName);
						mWeiboRegistRunnadle.onSuccess(ThirdPartLoginConfig.TYPE_SINA, data);
		    		}
				}
				@Override
				public void onFail(int ret, final String msg) {
					if(mWeiboRegistRunnadle != null){
						mWeiboRegistRunnadle.onFail(ret, msg, null);
		    		}
				}
				
				@Override
				public void onCancel(int paramInt) {
					if(mWeiboRegistRunnadle != null){
						mWeiboRegistRunnadle.onCancel();
		    		}
				}
			}));
		}
	}
}
