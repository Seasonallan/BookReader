/*
 * ========================================================
 * ClassName:CatalogListActivity.java* 
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
 * 2013-9-17     chendt          #00000       create
 */
package com.lectek.android.lereader.ui.specific;

import android.os.Bundle;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.contentinfo.CatalogListViewModel;
import com.lectek.android.lereader.net.response.tianyi.ContentInfo;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.ui.common.BaseActivity;

/**
 * @description
	目录列表
 * @author chendt
 * @date 2013-9-17
 * @Version 1.0
 * @see CatalogListViewModel
 */
public class CatalogListActivity extends BaseActivity {
	private CatalogListViewModel mViewModel; 
	private ContentInfo contentInfo = null;
	@Override
	protected View newContentView(Bundle savedInstanceState) {
		Bundle bundle = getIntent().getExtras();
		if (bundle!=null) {
			contentInfo = (ContentInfo) bundle.getSerializable(LeyueConst.GOTO_BOOK_CATALOG_LIST_TAG);
		}else {
			finish();
			return null;
		}
		mViewModel = new CatalogListViewModel(this_, this, contentInfo);
		return bindView(R.layout.catalog_list_view, mViewModel);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setTitleContent(getString(R.string.catalog_title));
		setTitleContent(contentInfo.getBookName());
		mViewModel.onStart();
	}
}
