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
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.app.ActivityManage;
import com.lectek.android.lereader.lib.utils.IProguardFilterOnlyPublic;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.ui.IRetryClickListener;
import com.lectek.android.lereader.ui.specific.BaseWebView.WebViewHandleEventListener;
import com.lectek.android.lereader.utils.CommonUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * @description
	专题详情
 * @author chendt
 * @date 2013-9-6
 * @Version 1.0
 */
public class SubjectDetailActivity extends FlingExitBaseAcitity implements IRetryClickListener, IProguardFilterOnlyPublic{

    public static final String EXTRA_SUBJECT_TYPE = "extra_subject_type";

    public static final String SUBJECT_TYPE_LIMIT = "限免专题";
    public static final String SUBJECT_TYPE_DETAIL = "专题详情";
    public static final String SUBJECT_TYPE_CATALOG = "专题栏目";

	private String PAGE_NAME = "专题详情";
	private final String EVENT_SUBJECT_ID = "SubjectDetail";
	public static String SUBJECT_TAG = "leyue://sujectinfo/";
	public static String GE_TUI_SUBJECT_TAG = "ge_tui_subject_tag";//推送消息进入专题详情
	public static String GET_SUBJECT_FROM_MY_MESSAGE_TAG = "get_subject_from_my_message_tag";//从我的消息进入专题详情
	public static final String TAG = SubjectDetailActivity.class.getSimpleName();
	String targetUrl;
	String bookListTitleName;
	private BaseWebView mView;
	private boolean isTuiSongScene;
	private boolean isMyMessageScene;
	private Activity this_ = SubjectDetailActivity.this;

    private boolean mFromNotification = false;
    public static final String NOTI_TAG = "from_notification";
	@Override
	protected View newContentView(Bundle savedInstanceState) {
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null){
            mFromNotification = bundle.getBoolean(NOTI_TAG, false);
			targetUrl = bundle.getString(LeyueConst.GOTO_SUBJECT_DETAIL_TAG);
			isTuiSongScene = bundle.getBoolean(GE_TUI_SUBJECT_TAG,false);
			isMyMessageScene = bundle.getBoolean(GET_SUBJECT_FROM_MY_MESSAGE_TAG,false);
			if(TextUtils.isEmpty(targetUrl)) return null;
            if(bundle.containsKey(EXTRA_SUBJECT_TYPE)){
                PAGE_NAME = bundle.getString(EXTRA_SUBJECT_TYPE);
            }
			mView = new BaseWebView(this_);
			return mView;
		}
		targetUrl = getIntent().getStringExtra(LeyueConst.GOTO_SUBJECT_DETAIL_TAG);
		if(!TextUtils.isEmpty(targetUrl))
		{
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
				//取消缓存实时刷新
				//WebViewUrlCacheDB.getInstance(SubjectDetailActivity.this).deleteUrlInfo(BaseWebView.getCurrentWebUrlTag(SubjectDetailActivity.this));
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
				LogUtil.e("---专题----");
//				mView.mWebView.loadUrl("javascript:GetJSON('""')");
			}
			
		});
		mView.onCreate();
		if(!isTuiSongScene) handleUrl();
//		setTitleContent(bookListTitleName);
		setTitleContent("专题话题");
		mView.mHandler.sendEmptyMessage(BaseWebView.DELAY_LOAD_OTHERS);
	}
	
	@Override
	protected void onResume() {
		MobclickAgent.onPageStart(PAGE_NAME);
		MobclickAgent.onEvent(this_, EVENT_SUBJECT_ID, bookListTitleName);
		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd(PAGE_NAME);
		super.onPause();
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mView.mHandler.removeMessages(BaseWebView.DELAY_LOAD_OTHERS);
	}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mFromNotification){
            if(ActivityManage.getTopActivity() == null){
                startActivity(new Intent(this, WelcomeActivity.class));
            }
        }
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
		if(targetUrl.lastIndexOf(LeyueConst.GET_TITLE_TAG) != -1)
			targetUrl = targetUrl.substring(0,targetUrl.lastIndexOf(LeyueConst.GET_TITLE_TAG));
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
		CommonUtil.saveTimeStampForRetryNetClick(BaseWebView.getCurrentWebUrlTag(this));
	}
	
	@Override
	public void finish() {
		if(isTuiSongScene && !isMyMessageScene){
			startActivity(new Intent(this, MainActivity.class));
			super.finish();
		}else {
			super.finish();
		}
	}
}
