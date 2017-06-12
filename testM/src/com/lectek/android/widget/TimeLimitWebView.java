package com.lectek.android.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebViewClient;

public class TimeLimitWebView extends android.webkit.WebView{
	private TimeLimitWebView this_ = this;
	private WebViewClient mclient;
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private String mLastUrl = "";
	private long mLoadTime = 30000L;
	private boolean isTimeLimitEnable = true;
	
	private Runnable mTimeOutRunnable;
	
	public TimeLimitWebView(Context context) {
		super(context);
		init();
	}
	public TimeLimitWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public TimeLimitWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		isTimeLimitEnable = Build.VERSION.SDK_INT > 7;
		if(!isTimeLimitEnable){
			return;
		}
		
		mTimeOutRunnable = new Runnable() {
			@Override
			public void run() {
				if(this_ != null && this_.isShown()){//null
					this_.stopLoading();
				}
				if(mclient != null){
					mclient.onReceivedError(this_, WebViewClient.ERROR_TIMEOUT, "", mLastUrl);
				}
			}
		};
		
		super.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(
					android.webkit.WebView view, String url) {
				if(mclient != null){
					return mclient.shouldOverrideUrlLoading(view, url);
				}
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageStarted(android.webkit.WebView view, String url,
					Bitmap favicon) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mHandler.removeCallbacks(mTimeOutRunnable);
						mHandler.postDelayed(mTimeOutRunnable, mLoadTime);
					}
				});
				mLastUrl = url;
				if(mclient != null){
					mclient.onPageStarted(view, url, favicon);
				}
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(android.webkit.WebView view, String url) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mHandler.removeCallbacks(mTimeOutRunnable);
					}
				});
				if(mclient != null){
					mclient.onPageFinished(view, url);
				}
				super.onPageFinished(view, url);
			}

			@Override
			public void onLoadResource(android.webkit.WebView view, String url) {
				if(mclient != null){
					mclient.onLoadResource(view, url);
				}
				super.onLoadResource(view, url);
			}

			@Override
			public void onTooManyRedirects(android.webkit.WebView view,
					Message cancelMsg, Message continueMsg) {
				if(mclient != null){
					mclient.onTooManyRedirects(view, cancelMsg, continueMsg);
				}
				super.onTooManyRedirects(view, cancelMsg, continueMsg);
			}

			@Override
			public void onReceivedError(android.webkit.WebView view,
					int errorCode, String description, String failingUrl) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mHandler.removeCallbacks(mTimeOutRunnable);
					}
				});
				if(mclient != null){
					mclient.onReceivedError(view, errorCode, description, failingUrl);
				}
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

			@Override
			public void onFormResubmission(android.webkit.WebView view,
					Message dontResend, Message resend) {
				if(mclient != null){
					mclient.onFormResubmission(view, dontResend, resend);
				}
				super.onFormResubmission(view, dontResend, resend);
			}

			@Override
			public void doUpdateVisitedHistory(android.webkit.WebView view,
					String url, boolean isReload) {
				if(mclient != null){
					mclient.doUpdateVisitedHistory(view, url, isReload);
				}
				super.doUpdateVisitedHistory(view, url, isReload);
			}

			@Override
			public void onReceivedSslError(android.webkit.WebView view,
					SslErrorHandler handler, SslError error) {
				if(mclient != null){
					mclient.onReceivedSslError(view, handler, error);
				}
				super.onReceivedSslError(view, handler, error);
			}

			@Override
			public void onReceivedHttpAuthRequest(android.webkit.WebView view,
					HttpAuthHandler handler, String host, String realm) {
				if(mclient != null){
					mclient.onReceivedHttpAuthRequest(view, handler, host, realm);
				}
				super.onReceivedHttpAuthRequest(view, handler, host, realm);
			}

			@Override
			public boolean shouldOverrideKeyEvent(android.webkit.WebView view,
					KeyEvent event) {
				if(mclient != null){
					return mclient.shouldOverrideKeyEvent(view, event);
				}
				return super.shouldOverrideKeyEvent(view, event);
			}

			@Override
			public void onUnhandledKeyEvent(android.webkit.WebView view,
					KeyEvent event) {
				if(mclient != null){
					mclient.onUnhandledKeyEvent(view, event);
				}
				super.onUnhandledKeyEvent(view, event);
			}

			@Override
			public void onScaleChanged(android.webkit.WebView view,
					float oldScale, float newScale) {
				if(mclient != null){
					mclient.onScaleChanged(view, oldScale, newScale);
				}
				super.onScaleChanged(view, oldScale, newScale);
			}
		});
	}
	
	@Override
	public void setWebViewClient(WebViewClient client) {
		if(!isTimeLimitEnable){
			super.setWebViewClient(client);
		}else{
			mclient = client;
		}
	}

	public void setLoadTime(long time){
		this.mLoadTime = time;
	}
}
