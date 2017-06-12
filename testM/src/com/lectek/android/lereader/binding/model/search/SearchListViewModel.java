/*
 * ========================================================
 * ClassName:SearchListViewModel.java* 
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
 * 2013-9-10     chendt          #00000       create
 */
package com.lectek.android.lereader.binding.model.search;

import gueei.binding.Command;
import gueei.binding.Observable;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.binding.command.OnItemClickCommand;
import com.lectek.android.binding.command.OnTouchCommand;
import com.lectek.android.lereader.binding.model.bookCity.BookCityCommonBookItem;
import com.lectek.android.lereader.binding.model.bookCity.BookCityCommonViewModeLeyue;
import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.data.BookShelfItem;
import com.lectek.android.lereader.lib.storage.dbase.BaseDatabase;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.net.response.DownloadInfo;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.storage.dbase.SearchKey;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.IView;
import com.lectek.android.lereader.ui.specific.ActivityChannels;
import com.lectek.android.lereader.ui.specific.ContentInfoActivityLeyue;
import com.lectek.android.lereader.utils.ToastUtil;
import com.lectek.lereader.core.util.LogUtil;

public class SearchListViewModel extends BookCityCommonViewModeLeyue{
	private Boolean isLoadSuccess = false;
	private boolean isFirstEnter = true;
	private BookCitySearchResultModelLeyue mBookCitySearchResultModelLeyue;
	private BookShelfSearchResultModelLeyue mBookShelfSearchResultModelLeyue;
	private SearchKeyWordModel mSearchKeyWordModel;
	private SearchUserAction mUserAction;
	public IntegerObservable bClearBtnImageResourceId = new IntegerObservable(R.drawable.search_box_clean_bg);
	public BooleanObservable bClearBtnVisible = new BooleanObservable(false);
	public final BooleanObservable bSearchKeywordVisible = new BooleanObservable(true);
	public StringObservable bSearchBoxContent = new StringObservable();
	public ArrayListObservable<ArrayListItem> bPopSource = new ArrayListObservable<ArrayListItem>(ArrayListItem.class);
	public final Observable<Object> ClickedItem = new Observable<Object>(Object.class);
	public final BooleanObservable bLocalBookListVisible = new BooleanObservable(true);
	public final BooleanObservable bNotFoundTipVisiblity = new BooleanObservable(false);
	public final ArrayListObservable<BookCityCommonBookItem> bLocalSearchItems = new ArrayListObservable<BookCityCommonBookItem>(BookCityCommonBookItem.class);
	
	public SearchListViewModel(Context context, INetLoadView loadView,
							SearchUserAction userAction) {
		super(context, loadView);
		mUserAction = userAction;

		mBookCitySearchResultModelLeyue = new BookCitySearchResultModelLeyue();
		mBookCitySearchResultModelLeyue.addCallBack(this);
		mBookShelfSearchResultModelLeyue = new BookShelfSearchResultModelLeyue();
		mBookShelfSearchResultModelLeyue.addCallBack(this);
		bBookCityListVisibility.set(false);
	}
	
	public void onCreate(){

	}
	
	public final OnItemClickCommand bOnItemClick = new OnItemClickCommand() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			bSearchBoxContent.set(bPopSource.get(position).bHistoryText.get());
			mUserAction.hidePopWindow();
		}
	};
	
	public final OnItemClickCommand bLocalItemClickCommand = new OnItemClickCommand() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			BookCityCommonBookItem book = bLocalSearchItems.getItem(position);
			//天翼阅读书籍
			if(!TextUtils.isEmpty(book.outbookId)){
				ActivityChannels.gotoLeyueBookDetail(getContext(), book.outbookId,
						LeyueConst.EXTRA_BOOLEAN_IS_SURFINGREADER, true,
						LeyueConst.EXTRA_LE_BOOKID, book.bookId
						);
			}else{
				//乐阅书籍
				ContentInfoActivityLeyue.openActivity(getContext(), book.bookId);
			}
		}
	};
	
	
	/**返回操作*/
	public final OnClickCommand bCancleClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			mUserAction.finish();
		}
	};
	
	public final OnClickCommand bGoToBookCity = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			startBookCitySearch();
		}
	};
	
	/**清除文本操作*/
	public final Command bClearTextClick = new Command() {
		
		@Override
		public void Invoke(View arg0, Object... arg1) {
			bSearchBoxContent.set("");
			bSearchKeywordVisible.set(true);
			hideRetryView();
			mUserAction.showKeyBoardAction();
			bBookCityListVisibility.set(false);
			bLocalBookListVisible.set(true);
		}
	};
	
	/**搜索操作*/
	public final Command bSearchClick = new Command() {
		
		@Override
		public void Invoke(View arg0, Object... arg1) {
			if(!TextUtils.isEmpty(bSearchBoxContent.get())){
				bSearchKeywordVisible.set(false);
				mUserAction.searchInBookCityAction(getTrimText(bSearchBoxContent.get()),false);
			}else{
				ToastUtil.showToast(getContext(), "请输入您要搜索的内容");
			}
		}
	};
	
	/**文本变更监听*/
	public final Command TextChanging = new Command() {
		
		@Override
		public void Invoke(View view, Object... arg1) {
			bNotFoundTipVisiblity.set(false);
			bBookCityListVisibility.set(false);
			if(TextUtils.isEmpty(bSearchBoxContent.get())||TextUtils.isEmpty(bSearchBoxContent.get().trim())){
				hideRetryView();
				bSearchKeywordVisible.set(true);
				bClearBtnVisible.set(false);
				if(isFirstEnter){
					isFirstEnter = false;
					return;
				}
				showSearchHistoryWindow(view);
			}else {
				bClearBtnVisible.set(true);
				startBookShelfSearch();
			}
		}
	};
	
	public void startBookShelfSearch(){
		bNotFoundTipVisiblity.set(false);
		mBookShelfSearchResultModelLeyue.start(bSearchBoxContent.get());
	}
	
	public void startBookCitySearch(){
		bNotFoundTipVisiblity.set(false);
		if(!TextUtils.isEmpty(bSearchBoxContent.get())){
			bBookCityListVisibility.set(true);
			bLocalBookListVisible.set(false);
			bBookItems.clear();
			mUserAction.searchInBookCityAction(getTrimText(bSearchBoxContent.get()),false);
		}
	}
	
	public void startBookCitySearchMode(String tagstr){
		showLoadView();
		mBookCitySearchResultModelLeyue.setSearchKeyWork(tagstr);
		mBookCitySearchResultModelLeyue.loadPage();
	}
	
	private void showSearchHistoryWindow(View view){
		ArrayList<SearchKey> list = new BaseDatabase<SearchKey>(SearchKey.class).getAll();
		//ArrayList<String> list = SearchRecordDB.getInstance(getContext()).getSearchHistoryInfos();
		if (list!=null && list.size()>0) {
			bPopSource.clear();
			for (int i = list.size() -1; i >=0 ;i --) {
                SearchKey str = list.get(i);
				if (bPopSource.size() > 3) {
					break;
				}
				newListItem(str.key); 
			}
			mUserAction.showHistoryRecord(view);
		}
	}
	
	public final OnTouchCommand bOnTouchEvent = new OnTouchCommand() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(TextUtils.isEmpty(bSearchBoxContent.get())||TextUtils.isEmpty(bSearchBoxContent.get().trim())){
				showSearchHistoryWindow(v);
			}
			return false;
		}
	};
	
	public void loadKeywordData(){
		mSearchKeyWordModel.start();
	}
	
	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if(!isSucceed)
			return false;
		if(result != null && !isCancel){
//			if(mSearchKeyWordModel.getTag().equals(tag)){
//				ArrayList<KeyWord> list = (ArrayList<KeyWord>)result;
//				if(list != null && list.size()>0){
//					mUserAction.loadKeywordListAction();
//					hideLoadView();
//				}
//			}
			if(mBookShelfSearchResultModelLeyue.getTag().equals(tag)){
				ArrayList<BookShelfItem> list = (ArrayList<BookShelfItem>)result;
				LogUtil.i("yyl", list.toString());
				bBookCityListVisibility.set(false);
				bNotFoundTipVisiblity.set(false);
				bLocalBookListVisible.set(true);
				if(list != null && list.size()>0){
					getSearchFromBookShelf(list);
				}else{
					bLocalSearchItems.clear();
				}
			}else if(mBookCitySearchResultModelLeyue.getTag().equals(tag)){
				ArrayList<ContentInfoLeyue> list = (ArrayList<ContentInfoLeyue>)result;
				if(list != null && list.size()> 0){
					bNotFoundTipVisiblity.set(false);
					bBookCityListVisibility.set(true);
					bLocalBookListVisible.set(false);
					fillTheBookList(list,false);
				}else{
					bLocalBookListVisible.set(false);
					if(bBookItems.size() == 0){
						bBookCityListVisibility.set(false);
						bNotFoundTipVisiblity.set(true);
					}else{
						setLoadPageCompleted(true);
					}
				}
			}
			hideLoadView();
		}
		return super.onPostLoad(result, tag, isSucceed, isCancel, params);
	}

	private void getSearchFromBookShelf(ArrayList<BookShelfItem> list){
		bLocalSearchItems.clear();
		for(int i = 0; i < list.size(); i++){
			BookShelfItem info = list.get(i);
			
			BookCityCommonBookItem book = new BookCityCommonBookItem();
			book.bookId = info.mDownLoadInfo.contentID;
//			book.outbookId = info.getOutBookId();
			book.bBookName.set(info.mDownLoadInfo.contentName);
			book.bAuthorName.set(info.mDownLoadInfo.authorName);
			book.bRecommendedCoverUrl.set(info.mDownLoadInfo.logoUrl);
			book.bAddBookVisibility.set(false);
//			book.bDecContent.set(info.getIntroduce());
			book.bDecContentVisibility.set(false);
			book.bReadStateVisibility.set(true);
			book.bRedotVisibility.set(!info.isReaded());
			book.bReadedVisibility.set(book.bRedotVisibility.get());
			book.bReadedText.set(book.bRedotVisibility.get()?"未读":"已读");
			book.embedToBookCity = false;
			bLocalSearchItems.add(book);
		}
	}
	
	public void onLoadSuccess(Boolean isSuccess) {
		isLoadSuccess = isSuccess;
	}
	
	public interface SearchUserAction extends IView{
		
		/**
		 * 搜索操作，隐藏popWindow
		 * @param targetStr 目标串
		 * @param dismissPop
		 */
		public void searchInBookCityAction(String targetStr,boolean dismissPop);
		
		/**显示历史记录*/
		public void showHistoryRecord(View view);
		
		public void showKeyBoardAction();
		
		public void showLoadingView();
		
		public void hidePopWindow();
	}
	
	private void newListItem(String str){
		bPopSource.add(new ArrayListItem(str));
	}
	
	public class ArrayListItem{
		public StringObservable bHistoryText = new StringObservable();
		ArrayListItem(String str){
			bHistoryText.set(str);
		}
	}
	
	private String getTrimText(String text){
		while(text.indexOf(" ") == 0 || text.lastIndexOf(" ") == text.length()-1){
			if(text.indexOf(" ") == 0){
				text = text.substring(1);
			}
			if(text.lastIndexOf(" ") == text.length()-1){
				text = text.substring(0, text.length()-1);
			}
		}
		return text;
	}

	@Override
	public PagingLoadModel<?> getPagingLoadModel() {
		return mBookCitySearchResultModelLeyue;
	}
	
}
