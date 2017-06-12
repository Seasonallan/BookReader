package com.lectek.android.lereader.binding.model.bookCity;

import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.StringObservable;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnItemClickCommand;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.net.response.SubjectDetailResultInfo;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.specific.ActivityChannels;
import com.lectek.android.lereader.ui.specific.ContentInfoActivityLeyue;

public class BookCitySubjectViewModelLeyue extends BaseLoadNetDataViewModel implements INetAsyncTask {

	private BookCitySubjectDetailModelLeyue mBookCitySubjectDetailModelLeyue;
	public final StringObservable bSubjectTitle = new StringObservable();
	public final StringObservable bSubjectContent = new StringObservable();
	public final ArrayListObservable<BookCityCommonBookItem> bBookItems = new ArrayListObservable<BookCityCommonBookItem>(BookCityCommonBookItem.class);
	
	public BookCitySubjectViewModelLeyue(Context context, INetLoadView loadView,int subjectId) {
		super(context, loadView);
		mBookCitySubjectDetailModelLeyue = new BookCitySubjectDetailModelLeyue(subjectId);
		mBookCitySubjectDetailModelLeyue.addCallBack(this);
	}

	//点击列表，进入书籍详情
	public final OnItemClickCommand bBookCityItemClickCommand = new OnItemClickCommand() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			BookCityCommonBookItem book = bBookItems.getItem(position);
			ActivityChannels.embedContentInfoLeyueActivityToBookCity(getContext(), book.outbookId, book.bookId);
//			//天翼阅读书籍
//			if(!TextUtils.isEmpty(book.outbookId)){
//				ActivityChannels.gotoLeyueBookDetail(getContext(), book.outbookId,
//						LeyueConst.EXTRA_BOOLEAN_IS_SURFINGREADER, true,
//						LeyueConst.EXTRA_LE_BOOKID, book.bookId
//						);
//			}else{
//				//乐阅书籍
//				ContentInfoActivityLeyue.openActivity(getContext(), book.bookId);
//			}
		}
	};
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		tryStartNetTack(this);
	}


	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		// TODO Auto-generated method stub
		if(!isSucceed)
			return false;
		if(mBookCitySubjectDetailModelLeyue.getTag().equals(tag)){
			SubjectDetailResultInfo info = (SubjectDetailResultInfo)result;
			bSubjectTitle.set(info.subjectName);
			bSubjectContent.set(info.subjectIntro);
			if(info.bookList != null && info.bookList.size() > 0){
				bBookItems.clear();
				fillTheBookList(info.bookList);
			}
		}
		hideLoadView();
		return false;
	}

	private void fillTheBookList(ArrayList<ContentInfoLeyue> list){
		for(int i = 0; i < list.size(); i++){
			ContentInfoLeyue info = list.get(i);
			BookCityCommonBookItem book = new BookCityCommonBookItem();
			book.bookId = info.getBookId();
			book.outbookId = info.getOutBookId();
			book.bBookName.set(info.getBookName());
			book.bAuthorName.set(info.getAuthor());
			book.bRecommendedCoverUrl.set(info.getCoverPath());
			book.bAddBookVisibility.set(true);
			if(DownloadPresenterLeyue.checkBookDownloadedExist(info.getBookId())){
				book.bAddBookBtn.set(getString(R.string.book_in_bookshelf));
				book.bAddBookTextColor.set(getResources().getColor(R.color.common_17));
//				book.bAddBtnBackGround.set(R.drawable.btn_go_to_bookcity_bg);
				book.bAddBtnBackGround.set(R.color.white);
			}else{
				book.bAddBookBtn.set(getString(R.string.add_book_to_bookshelf));
				book.bAddBookTextColor.set(getResources().getColor(R.color.color_ed5145));
				book.bAddBtnBackGround.set(R.drawable.btn_addto_bookshelf_bg);
			}
			book.bDecContent.set(info.getIntroduce());
			book.bDecContentVisibility.set(true);
			book.bReadStateVisibility.set(false);
			bBookItems.add(book);
		}
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		// TODO Auto-generated method stub
		showLoadView();
		return false;
	}


	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		hideLoadView();
		showRetryView();
		return false;
	}


	@Override
	public boolean isNeedReStart() {
		// TODO Auto-generated method stub
		return !hasLoadedData();
	}


	@Override
	public boolean isStop() {
		// TODO Auto-generated method stub
		return !mBookCitySubjectDetailModelLeyue.isStart();
	}


	@Override
	public void start() {
		mBookCitySubjectDetailModelLeyue.start();
	}


	@Override
	protected boolean hasLoadedData() {
		return bBookItems.size() > 0;
	}

}
