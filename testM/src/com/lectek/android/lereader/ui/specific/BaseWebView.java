/*
 * ========================================================
 * ClassName:BaseWebView.java* 
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
 * 2013-9-5     chendt          #00000       create
 */
package com.lectek.android.lereader.ui.specific;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.bookCity.BookCityViewModel;
import com.lectek.android.lereader.lib.utils.DateUtil;
import com.lectek.android.lereader.lib.utils.IProguardFilterOnlyPublic;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.ui.common.BaseNetPanelView;
import com.lectek.android.lereader.ui.specific.LoadResultFromJs.LoadReselutCallBack;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.widgets.PullRefreshLayout;
import com.lectek.android.lereader.widgets.PullRefreshLayout.OnPullListener;
import com.lectek.android.lereader.widgets.PullRefreshLayout.OnPullStateListener;
import com.umeng.analytics.MobclickAgentJSInterface;

/**
 * @description
	BS模式 下 所有使用webView的基类。
 * @author chendt
 * @date 2013-9-5
 * @Version 1.0
 */
public class BaseWebView extends BaseNetPanelView implements OnPullListener,OnPullStateListener, IProguardFilterOnlyPublic{
	public BookCityViewModel mBookCityViewModel;
	private static final String TAG = BaseWebView.class.getSimpleName();
	protected Activity this_; 
	protected WebView mWebView;
	private View view ;
//	private FrameLayout mWebContainer;
	public static final int DELAY_LOAD_RECOMMEND = 0x1;
	public static final int DELAY_LOAD_CLASSIFY = 0x2;
	public static final int DELAY_LOAD_RANK = 0x3;
	public static final int DELAY_LOAD_SPECIAL_SALE = 0x4;
	public static final int DELAY_LOAD_OTHERS = 0x5;
	public static final int DELAY_TIME = 150;//150
	private WebViewHandleEventListener mListener;
	protected LoadResultFromJs mResultFromJs;

	private final static int MSG_LOADING = 0x6;
	private final static int MSG_LOADED = 0x7;
	
	private PullRefreshLayout mPullLayout;
	private TextView mActionText;
	private TextView mTimeText;
	private View mProgress;
	private View mActionImage;

	private Animation mRotateUpAnimation;
	private Animation mRotateDownAnimation;

	private boolean mInLoading = false;
	
	public BaseWebView(Context context) {
		super(context);
		this_ = (Activity) context;
	}
	@Override
	public void onCreate() {
		
	}
	 
	protected Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case DELAY_LOAD_RECOMMEND:
			case DELAY_LOAD_CLASSIFY:
			case DELAY_LOAD_RANK:
			case DELAY_LOAD_SPECIAL_SALE:
			case DELAY_LOAD_OTHERS:
				initWebInfo();
				initPullView();
				break;
			case MSG_LOADING:
				//oldUrlTimestamp = WebViewUrlCacheDB.getInstance(BaseApplication.getInstance())
				//			.getUrlTimeByUrlTag(BaseWebView.getCurrentWebUrlTag(BaseWebView.this));
				CommonUtil.saveTimeStampForRetryNetClick(
						BaseWebView.getCurrentWebUrlTag(BaseWebView.this));
//				mListener.loadWebView();//TEST
				mListener.reloadWeb("");
				break;
			case MSG_LOADED:
				dataLoaded();
				break;

			default:
				break;
			}
		}
		
	};
	
	/**
	 * 适用于4.1以下
	 */
	private void enableCrossDomainUnderSDK15() {
		try {
			Field field = WebView.class.getDeclaredField("mWebViewCore");
			field.setAccessible(true);
			Object webviewcore = field.get(mWebView);
			Method method = webviewcore.getClass().getDeclaredMethod(
					"nativeRegisterURLSchemeAsLocal", String.class);
			method.setAccessible(true);
			method.invoke(webviewcore, "http");
			method.invoke(webviewcore, "https");
		} catch (Exception e) {
			Log.d("pinotao", "enablecrossdomain error");
			e.printStackTrace();
		}
	}
	
	protected void initPullView() {
		mRotateUpAnimation = AnimationUtils.loadAnimation(this_,
				R.anim.rotate_up);
		mRotateDownAnimation = AnimationUtils.loadAnimation(this_,
				R.anim.rotate_down);

//		mPullLayout = (PullRefreshLayout) findViewById(R.id.pull_container);
		mPullLayout.setOnActionPullListener(this);
		mPullLayout.setOnPullStateChangeListener(this);

		mProgress = findViewById(android.R.id.progress);
		mActionImage = findViewById(android.R.id.icon);
		mActionText = (TextView) findViewById(R.id.pull_note);
		mTimeText = (TextView) findViewById(R.id.refresh_time);

		mTimeText.setText(R.string.note_not_update);
		mActionText.setText(R.string.note_pull_down);
	}
	
	int webview = 0x9999;
	@SuppressLint("NewApi")
	protected void initWebInfo() {
		mBookCityViewModel = new BookCityViewModel(this_, BaseWebView.this,mListener);
		view = bindView(R.layout.bs_web_view, BaseWebView.this, mBookCityViewModel);
		mPullLayout = (PullRefreshLayout)view.findViewById(R.id.pull_container);
		mWebView = new WebView(this_.getApplicationContext());//TODO:如果webView 需要弹出对话框或者有flash加载。可能会报类型转换异常
		mWebView.setId(webview);
		mPullLayout.addView(mWebView,mPullLayout.getChildCount()-1);  //,mPullLayout.getChildCount()-1
		mWebView.setWebViewClient(new MyWebViewClient());
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mWebView.setLayoutParams(lp);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		mResultFromJs = new LoadResultFromJs(BaseWebView.this,mLoadReselutCallBack); 
		mWebView.addJavascriptInterface(mResultFromJs, "java4js");
//		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//		mWebView.getSettings().setAppCacheEnabled(false);
		mWebView.requestFocus();
		
		//4.1以上版本的手机webview打不开的bug
		if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
			mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
		}
		
//		mWebView.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
//		mWebView.getSettings().setRenderPriority(RenderPriority.HIGH); 
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		mWebView.getSettings().setAppCacheEnabled(true);
//		mWebView.getSettings().setAppCacheMaxSize(2*1024*1024);
//		String appCaceDir =   this_.getDir("cache", Context.MODE_PRIVATE).getPath();
//		mWebView.getSettings().setAppCachePath(appCaceDir);
		mWebView.setWebChromeClient(client);
		
		//添加对WEBVIEW页面的友盟数据统计支持
		new MobclickAgentJSInterface(this_, mWebView, client);
		mWebView.setDownloadListener(new MyWebViewDownLoadListener()); 
		if(Integer.valueOf(android.os.Build.VERSION.SDK)>14){
			enableCrossDomainGE_SDK15(); 
		}else {
			enableCrossDomainUnderSDK15();
		}
		mBookCityViewModel.onStart();
	}
	
	/**
	 * 适用大于等于 15的SDK，即4.1以上
	 */
	private void enableCrossDomainGE_SDK15() {
		try {
//			mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
			Field webviewclassic_field = WebView.class
					.getDeclaredField("mProvider");
			webviewclassic_field.setAccessible(true);
			Object webviewclassic = webviewclassic_field.get(mWebView);
			Field webviewcore_field = webviewclassic.getClass()
					.getDeclaredField("mWebViewCore");
			webviewcore_field.setAccessible(true);
			Object mWebViewCore = webviewcore_field.get(webviewclassic);
			Field nativeclass_field = webviewclassic.getClass()
					.getDeclaredField("mNativeClass");
			nativeclass_field.setAccessible(true);
			Object mNativeClass = nativeclass_field.get(webviewclassic);

			Method method = mWebViewCore.getClass().getDeclaredMethod(
					"nativeRegisterURLSchemeAsLocal",
					new Class[] { int.class, String.class });
			method.setAccessible(true);
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public	class MyWebViewClient extends WebViewClient implements IProguardFilterOnlyPublic{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			//解析界面链接处理。跳转到新Activity的 在构造时传入 url内容参数。
			//如果不跳转新Activity的直接load地址；
			//return true.为了防止 出现错误页面后，能返回上一级界面。
			LogUtil.e(TAG, "---url---"+url);
			if(TextUtils.isEmpty(url)) return true;
			if(url.contains(LeyueConst.ASSERT_FOLDER_PATH.concat(LeyueConst.BOOK_CITY_SUBJECT_DETAIL_HTML))){
				//专题详情
				Intent intent = new Intent(this_,SubjectDetailActivity.class);
				intent.putExtra(LeyueConst.GOTO_SUBJECT_DETAIL_TAG, url);
				this_.startActivity(intent);
				return true;
			}else if(url.contains(LeyueConst.ASSERT_FOLDER_PATH.concat(LeyueConst.BOOK_CITY_BOOK_LIST_HTML))){
				//书籍列表
				Intent intent = new Intent(this_,BookListActivity.class);
				intent.putExtra(LeyueConst.GOTO_BOOK_LIST_TAG, url);
				this_.startActivity(intent);
				return true;
			}else if (url.contains(LeyueConst.ASSERT_FOLDER_PATH.concat(LeyueConst.GOTO_BOOK_DETAIL_HTML))) {
				//天翼阅读书籍详情
//				final String bookId = url.substring((url.lastIndexOf("="))+1);
//				if (!TextUtils.isEmpty(bookId)) {
//					ContentInfo info = new ContentInfo();
//					info.setBookId(bookId);
//					ContentInfoActivity.openActitiy(this_, info);
//				}
				//天翼阅读书籍详情
				String surfingBookId = url.substring((url.indexOf("="))+1, (url.lastIndexOf("&")));
                String leBookId = url.substring((url.lastIndexOf("="))+1);
				if (!TextUtils.isEmpty(surfingBookId)) {
					ActivityChannels.gotoLeyueBookDetail(this_, surfingBookId,
                            LeyueConst.EXTRA_BOOLEAN_IS_SURFINGREADER, true,
                            LeyueConst.EXTRA_LE_BOOKID, leBookId
                            );
				}
				return true;
			}
			else if (url.contains(LeyueConst.ASSERT_FOLDER_PATH.concat(LeyueConst.GOTO_BOOK_DETAIL_LEYUE_HTML))) {
				//乐阅书籍详情
				final String bookId = url.substring((url.lastIndexOf("="))+1);
				if (!TextUtils.isEmpty(bookId)) {
					ActivityChannels.gotoLeyueBookDetail(this_, bookId);
				}
				
				
				//天翼阅读书籍详情
//				final String bookIds = "100000216358732";
//				if (!TextUtils.isEmpty(bookIds)) {
//					
//					ActivityChannels.gotoLeyueBookDetail(this_, bookIds, LeyueConst.EXTRA_BOOLEAN_IS_SURFINGREADER, true);
//				}
				
				return true;
			}else if (url.contains(LeyueConst.ASSERT_FOLDER_PATH.concat(LeyueConst.GOTO_MONTHLY_PAYMENT_ZONE_HTML))) {
				//包月专区 
//				String targetId = url.substring(url.lastIndexOf("=")+1);
//				Intent mIntent = new Intent(this_,MonthlyPaymentActivity.class);
//				mIntent.putExtra(LeyueConst.GOTO_MONTHLY_PAYMENT_ZONE_TAG, targetId);
//				this_.startActivity(mIntent);
				return true;
			}else if (url.contains(LeyueConst.ASSERT_FOLDER_PATH.concat(LeyueConst.GOTO_THIRD_PARTY_URL_HTML))) {
				String targetUrl = url.substring(url.lastIndexOf("url=")+"url=".length());
				Intent mIntent = new Intent(this_,ThirdUrlActivity.class);
				mIntent.putExtra(LeyueConst.GOTO_THIRD_PARTY_URL_TAG, targetUrl);
				this_.startActivity(mIntent);
				return true;
			}
			return false;
		}
		
		/**在页面加载开始时调用。*/
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			mWebView.setVisibility(View.GONE);
		}
		/**在页面加载结束时调用。*/
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			LogUtil.e(TAG, "--onPageFinished---url--"+url);
			mWebView.setVisibility(View.VISIBLE);
			hideLoadView();
			mWebView.requestFocus();
		}
		
		/**在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。*/
		@Override
		public void onLoadResource(WebView view, String url) {
			super.onLoadResource(view, url);
			LogUtil.e(TAG, "--onLoadResource--url = "+url);
		}
		
		@Override
		public void doUpdateVisitedHistory(WebView view, String url,
				boolean isReload) {
			super.doUpdateVisitedHistory(view, url, isReload);
		}
		
		@Override
		public void onReceivedHttpAuthRequest(WebView view,
				HttpAuthHandler handler, String host, String realm) {
			super.onReceivedHttpAuthRequest(view, handler, host, realm);
//			LogUtil.e(Tag, "--onReceivedHttpAuthRequest---");
		}
		
		@Override
		public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
			LogUtil.e(TAG, "--shouldOverrideKeyEvent---event_kc = "+event.getKeyCode()
					+",event_ds"+event.describeContents());
			//shouldOverrideKeyEvent --- 重写此方法才能够处理在浏览器中的按键事件。
			return super.shouldOverrideKeyEvent(view, event);
		}
		
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			// TODO Auto-generated method stub
			LogUtil.e(TAG, "--onReceivedError---description"+description);
			hideLoadView();
			mWebView.setVisibility(View.VISIBLE);
			mResultFromJs.setResultStatus("error");
			responseView();
		}
		
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			super.onReceivedSslError(view, handler, error);
			//onReceivedSslError --- 重写此方法可以让webview处理https请求。
			LogUtil.e(TAG, "--onReceivedSslError---");
			hideLoadView();
			mWebView.setVisibility(View.VISIBLE);
			mResultFromJs.setResultStatus("error");
			responseView();
		}
		
		@Override
		public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
			super.onUnhandledKeyEvent(view, event);
			LogUtil.e("---onUnhandledKeyEvent--");
			if ((event.getKeyCode() == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) { 
		       mWebView.goBack(); //goBack()表示返回WebView的上一页面 
		    } 
		}
		
	} 
	
	public void setmListener(WebViewHandleEventListener mListener) {
		this.mListener = mListener;
	}
	
	/**
	 * 界面响应 js 处理结果。
	 */
	public void responseView() {
		if (mInLoading) {
			if ("success".equals(mResultFromJs.getResultStatus())){
				if(mBookCityViewModel!=null)
					mBookCityViewModel.onLoadSuccess(true);
			}else if ("empty".equals(mResultFromJs.getResultStatus())) {
				if(mBookCityViewModel!=null){
					mBookCityViewModel.onLoadSuccess(true);
					this_.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							//WebViewUrlCacheDB.getInstance(BaseApplication.getInstance()).
							//	setUrlInfo(BaseWebView.getCurrentWebUrlTag(BaseWebView.this), oldUrlTimestamp);
							mListener.loadWebView();
						}
					});
				}
			}else if ("error".equals(mResultFromJs.getResultStatus())) {
				if(mBookCityViewModel!=null){
					mBookCityViewModel.onLoadSuccess(false);
					this_.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							//WebViewUrlCacheDB.getInstance(BaseApplication.getInstance()).
							//setUrlInfo(BaseWebView.getCurrentWebUrlTag(BaseWebView.this), oldUrlTimestamp);
							mListener.loadWebView();
						}
					});
				}
			} 
			mHandler.sendEmptyMessage(MSG_LOADED);
		}else {
			if ("success".equals(mResultFromJs.getResultStatus())){
				if(mBookCityViewModel!=null)
					mBookCityViewModel.onLoadSuccess(true);
			}else if ("empty".equals(mResultFromJs.getResultStatus())) {
				if(mBookCityViewModel!=null){
					mBookCityViewModel.onLoadSuccess(true);
					this_.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							setTipImageResource(R.drawable.icon_no_info_tip);
							mBookCityViewModel.showRetryView();
							mWebView.setVisibility(View.GONE);
						}
					});
				}
			}else if ("error".equals(mResultFromJs.getResultStatus())) {
				if(mBookCityViewModel!=null){
					mBookCityViewModel.onLoadSuccess(false);
					this_.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							setTipImageResource(R.drawable.icon_request_fail_tip);
							mBookCityViewModel.showRetryView();
							mWebView.setVisibility(View.GONE);
						}
					});
				}
			} 
		}
	}
	
	@Override
	public void onDestroy() {
//		LogUtil.e(Tag, "----onDestroy---");
		onActivityDestroy();
	}
	
	public void onActivityDestroy(){
		mPullLayout.removeAllViews();
        mWebView.destroy();
	}
	
	/**
	 * 获取对应的url tag
	 * @param object
	 * @return
	 */
	public static String getCurrentWebUrlTag(Object object) {
		if (object instanceof BookCityClassifyView) {
			return LeyueConst.BOOK_CITY_RECOMMEND_HTML;
		}else if (object instanceof BookCityClassifyView) {
			return LeyueConst.BOOK_CITY_CLASSIFY_HTML;
		}else if (object instanceof BookCitySaleView) {
			return LeyueConst.BOOK_CITY_SALE_HTML;
		}else if (object instanceof BookCitySpecialOfferView) {
			return LeyueConst.BOOK_CITY_SPECIAL_HTML;
		}else if (object instanceof BookListActivity) {
			return LeyueConst.BOOK_CITY_BOOK_LIST_HTML;
		}else if (object instanceof SearchListActivity) {
			return LeyueConst.SEARCH_LIST_HTML;
		}else if (object instanceof SubjectDetailActivity) {
			return LeyueConst.BOOK_CITY_SUBJECT_DETAIL_HTML;
		}
		return null;
	}
	
	public static interface WebViewHandleEventListener extends IProguardFilterOnlyPublic{
		
		/**子类 加载具体界面*/
		public void loadWebView();
		
		/**直接通过new BaseWebView 的 Activity 监听 web回调结果*/
		public void responeViewForAcitityType(String requestStatus);
		
		/**是否启用下拉刷新功能*/
		public boolean isPullEnabled();
		
		public void reloadWeb(String webTag);
	}
	
	public void checkLoadedResult(){
		LogUtil.e(TAG,"--checkLoadedResult--");
		mWebView.loadUrl("javascript:checkLoadResult()");
	}
	
	WebChromeClient client = new WebChromeClient(){
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
//			ToastUtil.showToast(this_, message);
			LogUtil.e("--url--"+url+",msg = "+message);
			return false;
		}
	};
	
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
//		LogUtil.e(Tag, "----onDetachedFromWindow---");
	}
	
	/**刷新加载结束*/
	private void dataLoaded() {
		if (mInLoading) {
			mInLoading = false;
			mPullLayout.setEnableStopInActionView(false);
			mPullLayout.hideActionView();
			mActionImage.setVisibility(View.VISIBLE);
			mProgress.setVisibility(View.GONE);

			if (mPullLayout.isPullOut()) {
				mActionText.setText(R.string.note_pull_refresh);
				mActionImage.clearAnimation();
				mActionImage.startAnimation(mRotateUpAnimation);
			} else {
				mActionText.setText(R.string.note_pull_down);
			}

//			mTimeText.setText(this_.getString(R.string.note_update_at, DateUtil.getCurrentTimeByMDHM()));
		}
	}
	
	LoadReselutCallBack mLoadReselutCallBack = new LoadReselutCallBack() {
		
		@Override
		public void onLoadReselutCallBack() {
			this_.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					hideLoadView();
					mWebView.setVisibility(View.VISIBLE);
					responseView();
					mListener.responeViewForAcitityType(mResultFromJs.getResultStatus());
				}
			});
		}
	};
	@Override
	public void onPullOut() {
		if (!mInLoading) {
			mActionText.setText(R.string.note_pull_refresh);
			mActionImage.clearAnimation();
			mActionImage.startAnimation(mRotateUpAnimation);
		}
	}
	@Override
	public void onPullIn() {
		if (!mInLoading) {
			mActionText.setText(R.string.note_pull_down);
			mActionImage.clearAnimation();
			mActionImage.startAnimation(mRotateDownAnimation);
		}
	}
	@Override
	public void onSnapToTop() {
		if (!mInLoading) {
			mInLoading = true;
			mPullLayout.setEnableStopInActionView(false);
			mActionImage.clearAnimation();
			mActionImage.setVisibility(View.GONE);
			mProgress.setVisibility(View.VISIBLE);
			mActionText.setText(R.string.note_pull_loading);
			mHandler.sendEmptyMessage(MSG_LOADING);
			mTimeText.setText(this_.getString(R.string.note_update_at, DateUtil.getCurrentTimeByMDHM()));
		}
	}
	@Override
	public void onShow() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onHide() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isPullEnabled() {
		if (mListener!=null) {
			return mListener.isPullEnabled();
		}
		return true;
	}
	
	private class MyWebViewDownLoadListener implements DownloadListener, IProguardFilterOnlyPublic{

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
        	this_.startActivity(intent);//TODO: 
        }
    }
	
}
