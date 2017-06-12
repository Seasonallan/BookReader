/*
 * ========================================================
 * ClassName:SubjectDetailActivity.java* 
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
 * 2013-9-6     chendt          #00000       create
 */
package com.lectek.android.lereader.ui.specific;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.utils.IProguardFilterOnlyPublic;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.ui.IRetryClickListener;
import com.lectek.android.lereader.ui.specific.BaseWebView.WebViewHandleEventListener;
import com.lectek.android.lereader.utils.CommonUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * @description
	书籍列表
 * @author chendt
 * @date 2013-9-6
 * @Version 1.0
 */
public class BookListActivity extends FlingExitBaseAcitity implements IRetryClickListener, IProguardFilterOnlyPublic{
	
	private final String PAGE_NAME = "书籍列表";
	private final String EVENT_BOOK_LIST_ID = "BookList";
	
	public static final String TAG = BookListActivity.class.getSimpleName();
	private String targetUrl;
	private String bookListTitleName;
	private BaseWebView mView;
	private Activity this_ = BookListActivity.this;
	@Override
	protected View newContentView(Bundle savedInstanceState) {
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null){
			targetUrl = bundle.getString(LeyueConst.GOTO_BOOK_LIST_TAG);
			if(TextUtils.isEmpty(targetUrl)) return null;
			mView = new BaseWebView(this_);
			return mView;
		}
		return null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mView.setmListener(new WebViewHandleEventListener() {
			
			@Override
			public void loadWebView() {
				mView.mResultFromJs.setTargetStr(targetUrl);
				//去掉缓存实时刷新
				//WebViewUrlCacheDB.getInstance(BookListActivity.this).deleteUrlInfo(BaseWebView.getCurrentWebUrlTag(BookListActivity.this));
				mView.mWebView.clearCache(true);
				
				mView.mWebView.loadUrl(CommonUtil.getBasicStr("url", targetUrl));
			}

			@Override
			public void responeViewForAcitityType(String requestStatus) {
				responseView(requestStatus);
			}

			@Override
			public boolean isPullEnabled() {
				return false;
			}

			@Override
			public void reloadWeb(String webTag) {
				// TODO Auto-generated method stub
				
			}
		});
		mView.onCreate();
		handleUrl();
		setTitleContent(bookListTitleName);
		mView.mHandler.sendEmptyMessage(BaseWebView.DELAY_LOAD_OTHERS);
	}
	
	@Override
	protected void onResume() {
		MobclickAgent.onPageStart(PAGE_NAME);
		MobclickAgent.onEventBegin(this_, EVENT_BOOK_LIST_ID, bookListTitleName);
		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd(PAGE_NAME);
		MobclickAgent.onEventEnd(this_, EVENT_BOOK_LIST_ID, bookListTitleName);
		super.onPause();
	}

	/**对带中文的url进行解码处理*/
	private void handleUrl() {
		// 剪切书籍列表——类型名称
		bookListTitleName = targetUrl.substring(targetUrl.lastIndexOf(LeyueConst.GET_TITLE_TAG)+1);
		try {
			bookListTitleName = java.net.URLDecoder.decode(bookListTitleName, LeyueConst.UTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		targetUrl = targetUrl.substring(0,targetUrl.lastIndexOf(LeyueConst.GET_TITLE_TAG));
	}
	
	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		// TODO Auto-generated method stub
		mView.mHandler.removeMessages(BaseWebView.DELAY_LOAD_RANK);
	}
	
	/**
	 * 界面响应 js 处理结果。
	 */
	public void responseView(String resultStatus) {
		if ("success".equals(resultStatus)){
		}else if ("empty".equals(resultStatus)) {
				setTipImageResource(R.drawable.icon_no_book_tip);
				setOprBtnGone();
				mView.mBookCityViewModel.showRetryView();
		}else if ("error".equals(resultStatus)) {
			setTipImageResource(R.drawable.icon_request_fail_tip);
			mView.mBookCityViewModel.showRetryView();
		} 
	}

	@Override
	public View getFlingView() {
		return mView.mWebView;
	}

	@Override
	public void flingExitHandle() {
		finish();
	}

	@Override
	public void onRetryClick() {
		// TODO Auto-generated method stub
		CommonUtil.saveTimeStampForRetryNetClick(
				BaseWebView.getCurrentWebUrlTag(this));
	}
	
}
