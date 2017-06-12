package com.lectek.android.lereader.ui.login_leyue;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.account.thirdPartApi.factory.WebViewFactory;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.lib.account.thirdPartApi.AbsWebClient;
import com.lectek.android.lereader.lib.account.thirdPartApi.IWebClient;
import com.lectek.android.lereader.permanent.ThirdPartLoginConfig;
import com.lectek.android.lereader.ui.common.BaseActivity;

/**
 *	第三方登陆
 * @author chends@lectek.com
 * @date 2014-06-21
 */
public class ThirdPartyLoginActivity extends BaseActivity implements INetAsyncTask{

	public static final int ACTIVITY_REQUEST_CODE = 97;
	public static final int ACTIVITY_RESULT_CODE_SUCCESS = ACTIVITY_REQUEST_CODE + 1;
	public static final int ACTIVITY_RESULT_CODE_FAIL = ACTIVITY_RESULT_CODE_SUCCESS + 1;
	public static final int ACTIVITY_RESULT_CODE_CANCEL = ACTIVITY_RESULT_CODE_FAIL + 1;
	
	private View mRootView;
	private WebView mWebView;
	private AbsWebClient mThirdHandler;
	
//	private int whichThirdLogin;
//	private boolean isLoading = false;
//	private boolean isBindSSOService = false;
	
//	private ServiceConnection conn;
	
//	private ThirdPartyLoginViewModelLeyue mViewModelLeyue;
	
	private int mActivityResult = ACTIVITY_RESULT_CODE_CANCEL;
	
	public static void openActivityForResult(Activity context, int thirdType, Bundle params) {
		Intent intent = new Intent(context, ThirdPartyLoginActivity.class);
		intent.putExtra(ThirdPartLoginConfig.EXTRA_TYPE, thirdType);
		if(params != null) {
			intent.putExtras(params);
		}
		
		context.startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int type = getIntent().getIntExtra(ThirdPartLoginConfig.EXTRA_TYPE, ThirdPartLoginConfig.TYPE_NORMAL);
		
		mThirdHandler = getWebViewClient(type);
		if(mThirdHandler != null) {
			
			mWebView.setFocusable(true);
			mWebView.requestFocus();
			mWebView.setInitialScale(100);
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.getSettings().setSupportZoom(true);
			mWebView.getSettings().setBuiltInZoomControls(true);
			
			mThirdHandler.onSettingWebView(mWebView);
			mThirdHandler.onInit(getIntent(), mRootView);
		}
		mWebView.setWebViewClient(mThirdHandler);
		setTitleContent(getString(ThirdPartLoginConfig.UI_TITLE[type]));
	}

	@Override
	protected void onStart() {
		super.onStart();
		tryStartNetTack(this);
	}
	
	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mRootView = LayoutInflater.from(this_).inflate(R.layout.thrid_party_login_layout, null);
		mWebView = (WebView) mRootView.findViewById(R.id.login_wv);
		
		return mRootView;
	}
	
	private AbsWebClient getWebViewClient(int type){
		return WebViewFactory.getInstance().getWebViewClientInstance(type, new IWebClient() {
			
			@Override
			public Context getContext() {
				if(this_.isFinishing()) {
					return MyAndroidApplication.getInstance();
				}
				return this_;
			}
			
			@Override
			public void showWaitTip() {
				setWebViewVisible(false);
				showLoadView();
			}
			
			@Override
			public void onSuccess(int tag, Bundle data) {
				mActivityResult = ACTIVITY_RESULT_CODE_SUCCESS;
				
				if(getIntent() != null && data != null) {
					getIntent().putExtras(data);
				}
				
				finish();
			}
			
			@Override
			public void onCancel() {
				mActivityResult = ACTIVITY_RESULT_CODE_CANCEL;
				finish();
			}
			
			@Override
			public void dimissWaitTip() {
				setWebViewVisible(true);
				hideLoadView();
			}
			
			@Override
			public void onFail(int code, String msg, String failUrl) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(!isFinishing()){
							mActivityResult = ACTIVITY_RESULT_CODE_FAIL;
							finish();
						}
					}
				});
			}
			
		});
	}
	
	private void setWebViewVisible(boolean isVisible){
		if(mWebView != null){
			if(isVisible){
				mWebView.setVisibility(View.VISIBLE);
				mWebView.requestFocus();
			}else{
				mWebView.setVisibility(View.GONE);
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(mThirdHandler != null) {
			mThirdHandler.handleActivityResult(requestCode, resultCode, data);
		}
	}
	
	@Override
	public boolean isNeedReStart() {
		return true;
	}

	@Override
	public boolean isStop() {
		return true;
	}

	@Override
	public void start() {
		mThirdHandler.loadUrl();
	}

	@Override
	public void finish() {
		Intent intent = getIntent();
		if(intent != null) {
			setResult(mActivityResult, intent);
		}else {
			setResult(mActivityResult);
		}
		super.finish();
	}
	
	@Override
	protected void onDestroy() {
		
		if(mThirdHandler != null) {
			mThirdHandler.onDestroy();
		}
		
		if(mWebView != null) {
			try {
				mWebView.stopLoading();
				mWebView.clearCache(true);
				mWebView.clearView();
				mWebView.clearHistory();
				mWebView.freeMemory();
				mWebView.destroy();
				mWebView = null;
			} catch (Exception e) {
			}
		}
		super.onDestroy();
	}
}
