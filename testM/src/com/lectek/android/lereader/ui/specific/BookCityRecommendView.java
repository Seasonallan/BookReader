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

import java.util.ArrayList;

import android.content.Context;
import android.view.ViewGroup;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.bookcityrecommend.BookCityRecommendViewModelLeyue;
import com.lectek.android.lereader.binding.model.bookcityrecommend.BookCityRecommendViewModelLeyue.BookCityRecommendViewUserAciton;
import com.lectek.android.lereader.net.response.SubjectResultInfo;
import com.lectek.android.lereader.ui.common.BaseNetPanelView;
import com.lectek.android.lereader.widgets.SlidingViewSwitcher;
import com.umeng.analytics.MobclickAgent;


/**
 * @description
	书城推荐页
 * @author gyz
 * @date 2014-7-3
 * @Version 1.0
 */
public class BookCityRecommendView extends BaseNetPanelView implements BookCityRecommendViewUserAciton{
	private final String TAG = BookShelfView.class.getSimpleName();
	private SlidingViewSwitcher mSubjectSwitecherView;
	private BookCityRecommendViewModelLeyue mBookCityRecommendViewModel;

	public BookCityRecommendView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		MobclickAgent.onPageStart(TAG);
		setFocusable(true);
		mBookCityRecommendViewModel = new BookCityRecommendViewModelLeyue(getContext(), this,this);
		bindView(R.layout.bookcity_recommend_layout,this, mBookCityRecommendViewModel);
		ViewGroup subject = (ViewGroup) findViewById(R.id.recommend_container);
		mSubjectSwitecherView = new SlidingViewSwitcher(getContext(), null);
		subject.addView(mSubjectSwitecherView);
		
		mBookCityRecommendViewModel.onStart();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exceptionHandle(String str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void optToast(String str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadSubjectOver(ArrayList<SubjectResultInfo> subjectList) {
		// TODO Auto-generated method stub
		mSubjectSwitecherView.setData(subjectList);
	}

	@Override
	public void loadDataEnd() {
		// TODO Auto-generated method stub
		
	}
	
}
