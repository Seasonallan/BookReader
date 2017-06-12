/*
 * ========================================================
 * ClassName:BookCityViewModel.java* 
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
 * 2013-8-29     chendt          #00000       create
 */
package com.lectek.android.lereader.binding.model.bookCity;

import android.content.Context;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.IRetryClickListener;
import com.lectek.android.lereader.ui.specific.BaseWebView;
import com.lectek.android.lereader.ui.specific.BaseWebView.WebViewHandleEventListener;
import com.lectek.android.lereader.utils.CommonUtil;

/**
 * @description

 * @author chendt
 * @date 2013-10-9
 * @Version 1.0
 * @SEE BaseWebView
 */
public class BookCityViewModel extends BaseLoadNetDataViewModel implements INetAsyncTask,IRetryClickListener{
	private static final String TAG = BookCityViewModel.class.getSimpleName();
	private WebViewHandleEventListener mListener;
	private Boolean isLoadSuccess = false;
	private BookCityModel mBookCityModel;
	private INetLoadView currentView;
	public BookCityViewModel(Context context, INetLoadView loadView,WebViewHandleEventListener listener) {
		super(context, loadView);
		currentView = loadView;
		mListener = listener;
		mBookCityModel = new BookCityModel();
		mBookCityModel.addCallBack(this);
		setmRetryClickListener(this);
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		showLoadView();
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		return false;
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		return false;
	}

	@Override
	public boolean isNeedReStart() {
		return !isLoadSuccess;
	}

	@Override
	public boolean isStop() {
		return !mBookCityModel.isStart();
	}

	@Override
	public void start() {
		LogUtil.e(TAG, "---start");
		mListener.loadWebView();
		mBookCityModel.start(mListener);
	}

	@Override
	protected boolean hasLoadedData() {
		return false;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		tryStartNetTack(this);
	}
	
	public void onLoadSuccess(Boolean isSuccess) {
		isLoadSuccess = isSuccess;
	}

	@Override
	public void onRetryClick() {
		CommonUtil.saveTimeStampForRetryNetClick(
				BaseWebView.getCurrentWebUrlTag(currentView));
	}
	
}
