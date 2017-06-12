package com.lectek.android.lereader.ui.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.utils.LogUtil;

public class CommWebView extends BaseWebViewActivity{

	public static final String EXTRA_NAME_URL = "url";
	public static final String EXTRA_NAME_BACK_OUT = "back";
	public static final String EXTRA_NAME_SERVER_TILE = "server_title";
	public static final String EXTRA_NAME_TITLE = "title"; 
	public static final String EXTRA_IS_NEED_CHECK_CONNECT_STATUS = "is_need_check_connect_status"; 
	public static final String EXTRA_IS_OPEN_WITH_CUSTOM = "is_open_with_custom"; 
	private String mURI;
	
	@Override
	public void finish() {
		super.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean isNeedCheckConnectStatus = getIntent().getBooleanExtra(EXTRA_IS_NEED_CHECK_CONNECT_STATUS, true);
		setNeedCheckConnectStatus(isNeedCheckConnectStatus);
		boolean isOpenWithCustom = getIntent().getBooleanExtra(EXTRA_IS_OPEN_WITH_CUSTOM, false);
		setOpenWithCustom(isOpenWithCustom);
	}

	/**
	 * @param url 地址
	 * @param isBack 是否让webview有返回功能
	 * @param title 自定义标题
	 * */
	public static void openMyWebView(Context context, String url, boolean isBack, String title) {
		openMyWebView(context,url,isBack,false,title,true);
	}
	
	/**
	 * @param url 地址
	 * @param isBack 是否让webview有返回功能
	 * @param isServerTile 服务端发起的标题
	 * */
	public static void openMyWebView(Context context, String url, boolean isBack, boolean isServerTile){
		openMyWebView(context,url,isBack,isServerTile,null,true);
	}
	
	/**
	 * @param url 地址
	 * @param isBack 是否让webview有返回功能
	 * */
	//无标题，返回默认标题”天翼阅读“
	public static void openMyWebView(Context context, String url, boolean isBack){
		openMyWebView(context,url,isBack,false,null,true);
	}
	/**
	 * 
	 * @param context
	 * @param url 地址
	 * @param title 自定义标题
	 */
	public static void openMyWebView(Context context, String url, String title){
		openMyWebView(context,url,true,false,title,true);
	}
	/**
	 * 
	 * @param context
	 * @param url 地址
	 * @param title 自定义标题
	 */
	public static void openMyWebView(Context context, String url, int title){
		openMyWebView(context,url,true,false,context.getString(title),true);
	}
	
	public static void openMyWebView(Context context, String url, int title,boolean isNeedCheckConnectStatus){
		openMyWebView(context,url,true,false,context.getString(title),isNeedCheckConnectStatus);
	}
	/**
	 * 
	 * @param context
	 * @param url 地址
	 */
	public static void openMyWebView(Context context, String url){
		openMyWebView(context,url,true,false,null,true);
	}
	
//	public static void openServiceAgreement(Context context){
//		openServiceAgreement(context, true);
//	}
	
//	public static void openServiceAgreement(Context context,boolean isNeedCheckConnectStatus){
//		openMyWebView(context
//				,Constant.DECLARATION_SERVICE_URL
//				,R.string.menu_disclaimer_view_title
//				,isNeedCheckConnectStatus);
//	}
	/**
	 * 
	 * @param context
	 * @param url 地址
	 * @param isBack 是否让webview有返回功能
	 * @param isServerTile 服务端发起的标题
	 * @param title 自定义标题
	 */
	public static void openMyWebView(Context context, String url, boolean isBack, boolean isServerTile, String title,boolean isNeedCheckConnectStatus) {
		openMyWebView(context, url, isBack, isServerTile, title, isNeedCheckConnectStatus, false);
	}
	
	public static void openMyWebView(Context context, String url, boolean isBack, boolean isServerTile, String title,boolean isNeedCheckConnectStatus, boolean isOpenWithCustom) {
		LogUtil.i("CommWebView", "url: " + url);
		Intent intent = new Intent(context, CommWebView.class);
		intent.putExtra(EXTRA_NAME_URL, url);
		intent.putExtra(EXTRA_NAME_BACK_OUT, isBack);
		intent.putExtra(EXTRA_NAME_SERVER_TILE, isServerTile);
		intent.putExtra(EXTRA_NAME_TITLE, title);
		intent.putExtra(EXTRA_IS_NEED_CHECK_CONNECT_STATUS, isNeedCheckConnectStatus);
		intent.putExtra(EXTRA_IS_OPEN_WITH_CUSTOM, isOpenWithCustom);
		context.startActivity(intent);
	}
	
	@Override
	protected String getContentUrl() {
		mURI = getIntent().getExtras().getString(EXTRA_NAME_URL);
		if(mURI != null){
			return mURI;
		}else{
			return "";
		}
	}
	
	@Override
	protected String getContentTitle() {
		super.setBackTOWebView(getIntent().getExtras().getBoolean(EXTRA_NAME_BACK_OUT));
		super.setServerTitle(getIntent().getExtras().getBoolean(EXTRA_NAME_SERVER_TILE));
		String titleName = getIntent().getExtras().getString(EXTRA_NAME_TITLE);
		if(titleName != null){
			return titleName;
		}else{
			return getString(R.string.app_name);
		}
	}

	@Override
	protected boolean shouldOverrideUrlLoading(WebView view, String url) {
		return false;
	}
}
