package com.lectek.android.lereader.ui.common;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.lib.utils.LogUtil;

public abstract class BaseWebViewActivity extends BaseActivity implements
		INetAsyncTask {

	private WebView mWebView;
	private FrameLayout contentFrameLayout;
	private boolean isBackTOWebView;
	private boolean isServerTitle;
	private boolean isNeedCheckConnectStatus = true;
	private String mLastUrl;
	private boolean isLoading = false;
	private boolean isInit = false;
	private boolean isReceivedError = false;
	private boolean isOpenWithCustom = false;
	private String mInitialUrl;

	public boolean isServerTitle() {
		return isServerTitle;
	}

	public void setServerTitle(boolean isServerTitle) {
		this.isServerTitle = isServerTitle;
	}

	public boolean isBackTOWebView() {
		return isBackTOWebView;
	}

	public void setBackTOWebView(boolean isBackTOWebView) {
		this.isBackTOWebView = isBackTOWebView;
	}

	public boolean isNeedCheckConnectStatus() {
		return isNeedCheckConnectStatus;
	}

	public void setNeedCheckConnectStatus(boolean isNeedCheckConnectStatus) {
		this.isNeedCheckConnectStatus = isNeedCheckConnectStatus;
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		View view = View.inflate(this, R.layout.scoredetailwebview, null);
		contentFrameLayout = (FrameLayout) view
				.findViewById(R.id.activity_content_lay2);
		setWebView();
		contentFrameLayout.addView(mWebView);
		return view;
	}

	private void setWebView() {
		mWebView = new WebView(this);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebView.getSettings().setSavePassword(false);
		mWebView.setWebViewClient(new MyRenrenWebViewClient());
		mWebView.clearCache(true);
		mWebView.clearHistory();
		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onReceivedTitle(WebView view, String title) {
				if (!TextUtils.isEmpty(title)) {
					if (isServerTitle) {
						setTitleContent(title);
					}
				}
				super.onReceivedTitle(view, title);
			}
		});
		FrameLayout.LayoutParams fill = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		mWebView.setLayoutParams(fill);
		mInitialUrl = getContentUrl();
		mWebView.setHapticFeedbackEnabled(false);
	}

	protected void loadUrl() {
		isInit = false;
		mLastUrl = getInitialUrl();
		tryStartNetTack(this);
	}

	protected WebView getWebView() {
		return mWebView;
	}

	protected String getInitialUrl() {
		return mInitialUrl;
	}

	protected abstract String getContentUrl();

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 积分记录直接返回，不在wap上返回
		if (isBackTOWebView) {
			if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
				if (findViewById(R.id.web_err_tip_iv).getVisibility() == View.VISIBLE) {
					findViewById(R.id.web_err_tip_iv).setVisibility(View.GONE);
				}
				mWebView.goBack();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		if (mWebView != null) {
			try {
				mWebView.setVisibility(View.GONE);
				mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8",
						null);
				mWebView.stopLoading();
				mWebView.clearCache(true);
				mWebView.clearView();
				mWebView.clearHistory();
				mWebView.freeMemory();
				mWebView.destroy();
				mWebView = null;
			} catch (Exception e) {
			}
			// CookieSyncManager.getInstance().stopSync();
		}
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadUrl();
	}

	protected String getCurrentUrl() {
		return mWebView.getUrl();
	}
	
	protected void webViewLoadUrl(WebView webView, String url){
		mWebView.loadUrl(url);
	}

	private void loadData() {
		isReceivedError = false;
		webViewLoadUrl(mWebView, mLastUrl);
//		// 判断是否离线登录
//		final Runnable loadDataTask = new Runnable() {
//
//			@Override
//			public void run() {
//				isReceivedError = false;
//				mWebView.loadUrl(mLastUrl);
//			}
//		};
//		if (isNeedCheckConnectStatus && !ClientInfoUtil.isConnect(this)) {
//			Runnable runnable = new Runnable() {
//
//				@Override
//				public void run() {
//					runOnUiThread(loadDataTask);
//				}
//			};
//			new ConnectPresenter(this, runnable) {
//				@Override
//				protected void showLoadingUI() {
//
//				}
//
//				@Override
//				protected void onStart() {
//					super.onStart();
//					isLoading = true;
//				}
//
//				@Override
//				protected void onStop() {
//					super.onStop();
//					isLoading = false;
//				}
//
//				@Override
//				protected void onStateChange(long state) {
//					super.onStateChange(state);
//					if (state == ConnectPresenter.BACKGROUD_VALUE_CONNECT_FALUT) {
//						showRetryView();
//					}
//				}
//			}.start();
//		} else {
//			loadDataTask.run();
//		}
	}

	protected void cutomeUrlLoading(WebView view, String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri content_uri_browsers = Uri.parse(url);
		intent.setData(content_uri_browsers);
		ComponentName componentName = new ComponentName("com.android.browser",
				"com.android.browser.BrowserActivity");
		try {
			LogUtil.i("url: " + url);
			if (getPackageManager().getActivityInfo(componentName, 0) == null) {
			}
			intent.setComponent(componentName);
		} catch (NameNotFoundException e) {
		}
		startActivity(intent);
	}

	public boolean isOpenWithCustom() {
		return isOpenWithCustom;
	}

	public void setOpenWithCustom(boolean isOpenWithCustom) {
		this.isOpenWithCustom = isOpenWithCustom;
	}

	private class MyRenrenWebViewClient extends WebViewClient {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit
		 * .WebView, java.lang.String)
		 */
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			LogUtil.i("shouldOverrideUrlLoading url: " + url);
			if (isOpenWithCustom()) {
				BaseWebViewActivity.this.cutomeUrlLoading(view, url);
				return true;
			}
			if(BaseWebViewActivity.this.shouldOverrideUrlLoading(view, url)){
				return true;
			}
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			LogUtil.i("onPageStarted url: " + url);
			if (!isReceivedError) {
				showLoadView();
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			LogUtil.i("onPageFinished url: " + url);
			if (!isReceivedError) {
				if(mWebView != null){
					mWebView.setVisibility(View.VISIBLE);
					mWebView.requestFocus();
				}
				hideLoadView();
				if (!isInit) {
					isInit = true;
				}
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			LogUtil.i("onReceivedError", "onReceivedError");
			super.onReceivedError(view, errorCode, description, failingUrl);
			view.setVisibility(View.GONE);
			mLastUrl = failingUrl;
			showRetryView();
			isReceivedError = true;
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
	
	protected abstract boolean shouldOverrideUrlLoading(WebView view, String url);

	@Override
	public boolean isNeedReStart() {
		return !isInit;
	}

	@Override
	public boolean isStop() {
		return !isLoading;
	}

	@Override
	public void start() {
		loadData();
	}
}
