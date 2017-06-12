/*
 * ========================================================
 * ClassName:BookViewA.java* 
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
package com.lectek.android.lereader.ui.specific;

import gueei.binding.Binder;
import gueei.binding.Binder.InflateResult;
import android.content.Context;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.BaseViewModel;
import com.lectek.android.lereader.binding.model.bookCitySale.BookCitySaleTwoViewModelLeyue;
import com.lectek.android.lereader.binding.model.bookCitySale.BookCitySaleTwoViewModelLeyue.BookCitySaleViewUserAciton;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.widgets.PullToRefreshListView;
import com.lectek.android.lereader.widgets.PullToRefreshListView.OnRefreshListener;

/**
 * @description 书城排行
 * @author gyz
 * @date 2014-7-11
 * @Version 1.0
 */
public class BookCitySaleView extends BaseViewModel implements BookCitySaleViewUserAciton{
	private static final String Tag = BookCitySaleView.class.getSimpleName();
	
	public BookCitySaleTwoViewModelLeyue mBookCityModelLeyue;

	public int rankId;

	private InflateResult mBindResult;
	
	protected PullToRefreshListView mPullToRefreshListView;
	
	public BookCitySaleView(Context context, INetLoadView netLoadView,int mRankId) {
		super(context, netLoadView);
		this.rankId = mRankId;
		mBookCityModelLeyue = new BookCitySaleTwoViewModelLeyue(getContext(), netLoadView, rankId,this);
		mBindResult = bindView(R.layout.bookcity_common_listview,mBookCityModelLeyue);
		
		mPullToRefreshListView = (PullToRefreshListView)mBindResult.rootView.findViewById(R.id.common_listview);
		mPullToRefreshListView.setPullToRefreshEnable(true);
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				mBookCityModelLeyue.pullToReflashData();
//				LogUtil.e(">>>>>>>>>>>>>rankId" + rankId);
			}
		});
	}

	@Override
	public void onStart() {
		mBookCityModelLeyue.onStart();
	}

	public View getRootView() {
		if (mBindResult != null) {
//			View resultView = mBindResult.rootView;
//			return resultView;
			return mBindResult.rootView;
		}
		return null;
	}

	protected InflateResult bindView(int layoutId, Object... contentViewModel) {
		InflateResult result = Binder.inflateView(getContext(), layoutId, null,
				false);

		for (int i = 0; i < contentViewModel.length; i++) {
			Binder.bindView(getContext(), result, contentViewModel[i]);
		}
		return result;
	}

	@Override
	public void onRelease() {
		mBookCityModelLeyue.onRelease();
		
		LogUtil.i(Tag, "onRelease id: " + String.valueOf(rankId));
	}

	@Override
	public void finish() {
		mBookCityModelLeyue.finish();
		super.finish();
		
		LogUtil.i(Tag, "finish id: " + String.valueOf(rankId));
	}
	
	@Override
	public void exceptionHandle(String str) {}

	@Override
	public void optToast(String str) {}

	@Override
	public void loadDataEnd() {
		mPullToRefreshListView.onRefreshComplete();
	}

}
