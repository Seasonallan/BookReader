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
import android.widget.GridView;
import android.widget.ListView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.bookCityClassify.BookCityClassifyViewModelLeyue;
import com.lectek.android.lereader.binding.model.bookCityClassify.BookCityClassifyViewModelLeyue.BookCityClassifyViewUserAciton;
import com.lectek.android.lereader.net.response.SubjectResultInfo;
import com.lectek.android.lereader.ui.common.BaseNetPanelView;
import com.lectek.android.lereader.widgets.SlidingViewSwitcher;

/**
 * @description
	书城分类
 * @author chendt
 * @date 2013-8-29
 * @Version 1.0
 */
public class BookCityClassifyView extends BaseNetPanelView implements BookCityClassifyViewUserAciton{

	public static final String TAG = BookCityClassifyView.class.getSimpleName();
	private BookCityClassifyViewModelLeyue mBookCityClassifyViewModelLeyue;
	private SlidingViewSwitcher mSubjectSwitecherView;
	
	public BookCityClassifyView(Context context) {
		super(context);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		mBookCityClassifyViewModelLeyue = new BookCityClassifyViewModelLeyue(getContext(), this,this);
		bindView(R.layout.bookcity_classify_layout, this, mBookCityClassifyViewModelLeyue);
		ViewGroup subject = (ViewGroup) findViewById(R.id.subject_container);
		mSubjectSwitecherView = new SlidingViewSwitcher(getContext(), null);
		subject.addView(mSubjectSwitecherView);
		mBookCityClassifyViewModelLeyue.onStart();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exceptionHandle(String str) {
		setTipImageResource(R.drawable.icon_request_fail_tip);
	}

	@Override
	public void optToast(String str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadSubjectOver(ArrayList<SubjectResultInfo> subjectList) {
		mSubjectSwitecherView.setData(subjectList);
	}

	@Override
	public void loadDataEnd() {
		// TODO Auto-generated method stub
		
	}
	
}
