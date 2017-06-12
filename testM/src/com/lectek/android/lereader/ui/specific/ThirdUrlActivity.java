/*
 * ========================================================
 * ClassName:ThirdUrlActivity.java* 
 * Description:
 * Copyright (C) 
 * ========================================================
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *===================================================================*
 * Revision History
 *
 * Modification                    Tracking
 * Date         Author          Number       Description of changes
 *____________________________________________________________________
 * 
 * 2013-10-19     chendt          #00000       create
 */
package com.lectek.android.lereader.ui.specific;

import java.io.UnsupportedEncodingException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.account.IaccountObserver;
import com.lectek.android.lereader.lib.utils.IProguardFilterOnlyPublic;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.lib.utils.PkgManagerUtil;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.shake.ShakeListenerUtils;
import com.lectek.android.lereader.ui.shake.ShakeListenerUtils.ShakeOverListener;
import com.lectek.android.lereader.ui.specific.LoadResultFromJs.LoadReselutCallBack;
import com.lectek.android.lereader.utils.AES;

/**
 * @description
	FIXME：使用binding 有一个bug: webview 中的输入框 键盘无法调起，也无法输入内容。所以这里单独处理。
 * @author chendt
 * @date 2014-3-13
 * @Version 1.0
 */
public class ThirdUrlActivity extends FlingExitBaseAcitity implements ShakeOverListener, IProguardFilterOnlyPublic{
	public static String TAG = ThirdUrlActivity.class.getSimpleName(); 
	public static String EXTRA_TITLE = "extra_title";
	public static int THIRD_URL_TAG = 0x11001; 
	private WebView mWebView;
	private String targetUrl;
	private String bookListTitleName;
	
	private ShakeListenerUtils shakeUtils;
	private SensorManager mSensorManager; //定义sensor管理器, 注册监听器用
	private boolean isAccessShakeOpt = false; 
	private boolean isClearHistory = false; 
	@Override
	public View getFlingView() {
		return mWebView;
	}

	@Override
	public void flingExitHandle() {
		finish();
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null){
			targetUrl = bundle.getString(LeyueConst.GOTO_THIRD_PARTY_URL_TAG);
			if(TextUtils.isEmpty(targetUrl)) return null;
			mWebView = new WebView(this);
			mWebView.requestFocus();
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
			LoadResultFromJs mResultFromJs = new LoadResultFromJs(this,mLoadReselutCallBack); 
			mWebView.addJavascriptInterface(mResultFromJs, "java4js");
			mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			mWebView.setDownloadListener(new MyWebViewDownLoadListener()); 
			return mWebView;
		}
		return null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handleUrl();
		setTitleContent(bookListTitleName);
		registerReceiver(shakeReceiver, new IntentFilter(AppBroadcast.ACTION_BEGIN_SHAKE));
		registerReceiver(shakeReceiver, new IntentFilter(AppBroadcast.ACTION_CANNOT_SHAKE));
		shakeUtils = new ShakeListenerUtils(this,this);String args = getShakeViewArgs(ThirdUrlActivity.this,targetUrl);
		mWebView.setWebViewClient(new MyWebViewClient());
		mWebView.loadUrl(targetUrl+args);
		showLoadView();
		
		AccountManager.getInstance().registerObserver(mAccountObserver);
	}
	
	/**对带中文的url进行解码处理*/
	private void handleUrl() {
		// 剪切书籍列表——类型名称
		if (targetUrl.contains(LeyueConst.GET_TITLE_TAG)) {
			bookListTitleName = targetUrl.substring(targetUrl.lastIndexOf(LeyueConst.GET_TITLE_TAG)+1);
			try {
				bookListTitleName = java.net.URLDecoder.decode(bookListTitleName, LeyueConst.UTF8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			targetUrl = targetUrl.substring(0,targetUrl.lastIndexOf(LeyueConst.GET_TITLE_TAG));
		}
		String titleStr = getIntent().getStringExtra(EXTRA_TITLE);
		if(!TextUtils.isEmpty(titleStr)){
			bookListTitleName = titleStr;
		}
	}
	
	LoadReselutCallBack mLoadReselutCallBack = new LoadReselutCallBack() {
		
		@Override
		public void onLoadReselutCallBack() {
			this_.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					hideLoadView();
					mWebView.setVisibility(View.VISIBLE);
				}
			});
		}
	};
	
	public	class MyWebViewClient extends WebViewClient implements IProguardFilterOnlyPublic{
		
		/**在页面加载结束时调用。*/
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			LogUtil.e(TAG, "--third--onPageFinished---url--"+url);
			hideLoadView();
			//unable
//			mView.requestFocus();
//			mView.mWebView.requestFocusFromTouch();
//			mWebView.mWebView.requestFocus();
		}
		
		/**在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。*/
		@Override
		public void onLoadResource(WebView view, String url) {
			super.onLoadResource(view, url);
//			LogUtil.e(Tag, "--onLoadResource--url = "+url);
//			if (url.contains("file:///android_asset/webkit/android-weberror.png")) {
////				hideLoadView();
//				setTipImageResource(R.drawable.icon_request_fail_tip);
//				mWebView.mBookCityViewModel.showRetryView();
//			}
		}
		
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			LogUtil.e(TAG, "--onReceivedError---description"+description);
			hideLoadView();
		}
		
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			super.onReceivedSslError(view, handler, error);
			//onReceivedSslError --- 重写此方法可以让webview处理https请求。
			LogUtil.e(TAG, "--onReceivedSslError---");
			hideLoadView();
		}
		
		@Override
		public void onUnhandledKeyEvent(WebView view, KeyEvent event) {//无效
			super.onUnhandledKeyEvent(view, event);
			if ((event.getKeyCode() == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
				if (!isClearHistory) {
					mWebView.goBack(); //goBack()表示返回WebView的上一页面 
				}
		    } 
		}
	} 
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {//有效
		if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
			if (!isClearHistory) {
				mWebView.goBack(); 
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 获取访问专题要传递的参数
	 * @param context
	 * @param subjectId 专题id
	 * @return
	 */
	public static String getShakeViewArgs(Context context,String targetUrl){
		StringBuilder sb = new StringBuilder();
		if (targetUrl.indexOf("?")<0) {
			sb.append("?");
		}else {
			sb.append("&");
		}
		sb.append("version="+PkgManagerUtil.getApkVersionCode(context));
		if (PreferencesUtil.getInstance(context).getIsLogin() &&
				!TextUtils.isEmpty(PreferencesUtil.getInstance(context).getUserId())) {
			sb.append("&userid="+AES.encrypt(PreferencesUtil.getInstance(context).getUserId()));
		}
		return sb.toString();
	}
	
	private IaccountObserver mAccountObserver = new IaccountObserver() {
		
		@Override
		public void onLoginComplete(boolean success, String msg, Object... params) {
			if(success) {
				if (mWebView!=null) {
					String args = getShakeViewArgs(ThirdUrlActivity.this,targetUrl);
					mWebView.loadUrl(targetUrl+args);//变成原来界面的子层级了
					isClearHistory = true;
				}
			}
		}
		
		@Override
		public void onGetUserInfo(UserInfoLeyue userInfo) {}
		
		@Override
		public void onAccountChanged() {}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		if (requestCode == THIRD_URL_TAG) {
//			if (resultCode == UserLoginLeYueNewActivity.RESULT_CODE_SUCCESS) {
//				//重新加载即可
//				if (mWebView!=null) {
//					String args = getShakeViewArgs(ThirdUrlActivity.this,targetUrl);
//					mWebView.loadUrl(targetUrl+args);//变成原来界面的子层级了
//					isClearHistory = true;
//				}
//			}
//		}
		
	}
	
	
	private void registerShake(){
		if (isAccessShakeOpt) {
			// 获取传感器管理服务
			mSensorManager = (SensorManager) this
					.getSystemService(Service.SENSOR_SERVICE);
			// 加速度传感器
			mSensorManager.registerListener(shakeUtils,
					mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					// 还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，
					// 根据不同应用，需要的反应速率不同，具体根据实际情况设定
					SensorManager.SENSOR_DELAY_NORMAL);
		}
	}
	
	private void unregisterShake(){
		if (shakeUtils!=null && mSensorManager!=null) {
			mSensorManager.unregisterListener(shakeUtils);
		}
	}
	
	@Override
	protected void onPause() {
		if (isAccessShakeOpt) {
			unregisterShake();
		}
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (isAccessShakeOpt) {
			registerShake();
		}
	}
	
	@Override
	public void shakeOver() {
		// 调用web调用摇一摇操作
		if(mWebView!=null){
			LogUtil.e(TAG, "shakeOver");
			mWebView.loadUrl("javascript:beginShake()");
			Vibrator vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);  
			vibrator.vibrate(500);
		}
	}
	
	private BroadcastReceiver shakeReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent == null){
				return;
			}
			if(AppBroadcast.ACTION_BEGIN_SHAKE.equals(intent.getAction())){
				isAccessShakeOpt = true;
				LogUtil.e(TAG, "ACTION_BEGIN_SHAKE");
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						registerShake();
					}
				});
			}else if (AppBroadcast.ACTION_CANNOT_SHAKE.equals(intent.getAction())) {
				isAccessShakeOpt = false;
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						unregisterShake();
					}
				});
			}
		}

	};
	
	protected void onDestroy() {
		super.onDestroy();
		
		AccountManager.getInstance().unRegisterObserver(mAccountObserver);
		
		if (shakeReceiver!=null) {
			unregisterReceiver(shakeReceiver);
			shakeReceiver = null;
		}
	};
	
	private class MyWebViewDownLoadListener implements DownloadListener,IProguardFilterOnlyPublic{

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {        	
        	Log.i("tag", "url="+url);        	
        	Log.i("tag", "userAgent="+userAgent);
        	Log.i("tag", "contentDisposition="+contentDisposition);        	
        	Log.i("tag", "mimetype="+mimetype);
        	Log.i("tag", "contentLength="+contentLength);
        	String _360Url = "http://m.shouji.360tpcdn.com/130729/5b306c7923e705e4fda5ea9017f13757/com.qihoo.appstore_109011301.apk";
        	if (_360Url.equals(url)) {
//        		UpdateInfo updateInfo = new UpdateInfo(false, "360手机助手",contentLength, url, 
//        				"-10000000", "", "360手机助手", 0);//由于下载下来的应用在SD卡上，更新时，会判断对应版本，是否无需再下载，这里将值设为-10000000，避免影响升级的APK
//        		UpdateManager.getInstance().startDownloadInfo(this_, updateInfo, null);
			}
        	Uri uri = Uri.parse(url);  
        	Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
        	this_.startActivity(intent);
        }
    }
}
