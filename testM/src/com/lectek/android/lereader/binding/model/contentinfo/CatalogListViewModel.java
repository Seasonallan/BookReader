/*
 * ========================================================
 * ClassName:CatalogListViewModel.java* 
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
package com.lectek.android.lereader.binding.model.contentinfo;

import gueei.binding.Observable;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.StringObservable;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import com.lectek.android.binding.command.OnItemClickCommand;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.net.response.tianyi.Chapter;
import com.lectek.android.lereader.net.response.tianyi.ContentInfo;
import com.lectek.android.lereader.ui.INetLoadView;

public class CatalogListViewModel extends BaseLoadNetDataViewModel implements INetAsyncTask{
	private CatalogListInfoModel mListInfoModel;
	private ArrayList<Chapter> mDataSource;
	private ContentInfo contentInfo;
	public final Observable<Object> ClickedItem = new Observable<Object>(Object.class);
	public ArrayListObservable<ListItem> bItems = new ArrayListObservable<CatalogListViewModel.ListItem>(ListItem.class);  
	public CatalogListViewModel(Context context, INetLoadView loadView,ContentInfo contentInfo) {
		super(context, loadView);
		this.contentInfo = contentInfo;
		mListInfoModel = new CatalogListInfoModel();
		mListInfoModel.addCallBack(this);
	}

	public OnItemClickCommand bItemClickedCommand = new OnItemClickCommand() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Chapter chapter = ((ListItem)ClickedItem.get()).mChapter;
			//TODO: 提供入口
		}
	};
	
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
		if (!isCancel&&result!=null) {
			mDataSource = (ArrayList<Chapter>) result; 
			for (Chapter c : mDataSource) {
				bItems.add(newListItem(c));
			}
			hideLoadView();
		}
		return false;
	}

	@Override
	protected boolean hasLoadedData() {
		return mDataSource!=null;
	}

	@Override
	public boolean isNeedReStart() {
		return !hasLoadedData();
	}

	@Override
	public boolean isStop() {
		return !mListInfoModel.isStart();
	}

	@Override
	public void start() {
		mListInfoModel.start(contentInfo.getBookId());
	}
		
	@Override
	public void onStart() {
		super.onStart();
		tryStartNetTack(this);
	}
	
	private ListItem newListItem(Chapter chapter){
		ListItem lItem = new ListItem(chapter);
		lItem.bCatalogText.set(chapter.getChapterName());
		return lItem;
	}
	
	class ListItem{
		public StringObservable bCatalogText = new StringObservable();
		public Chapter mChapter;
		public ListItem(Chapter chapter){
			mChapter = chapter;
		}
	}
}
