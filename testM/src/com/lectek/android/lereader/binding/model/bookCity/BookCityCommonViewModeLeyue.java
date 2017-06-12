package com.lectek.android.lereader.binding.model.bookCity;

import java.util.ArrayList;

import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.IntegerObservable;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnItemClickCommand;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.binding.model.common.PagingLoadViewModel;
import com.lectek.android.lereader.lib.utils.IProguardFilter;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.ui.ILoadDialog;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.specific.ActivityChannels;
import com.lectek.android.lereader.ui.specific.ContentInfoActivityLeyue;

public abstract class BookCityCommonViewModeLeyue extends PagingLoadViewModel implements INetAsyncTask {

	public final ArrayListObservable<BookCityCommonBookItem> bBookItems = new ArrayListObservable<BookCityCommonBookItem>(BookCityCommonBookItem.class);
	public final BooleanObservable bBookCityListVisibility = new BooleanObservable(true);
	public final FooterViewModel bFooterViewModel = new FooterViewModel();
	
	public BookCityCommonViewModeLeyue(Context context, INetLoadView loadView) {
		super(context, loadView);
	}

	//点击列表，进入书籍详情
	public final OnItemClickCommand bBookCityItemClickCommand = new OnItemClickCommand() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			//屏蔽footview造成越界
			if(bBookItems.size() < position || position == 0)
				return;
			//下拉刷新ListView，position 要减1
			BookCityCommonBookItem book = bBookItems.getItem(position-1);
			if(book.embedToBookCity){
				ActivityChannels.embedContentInfoLeyueActivityToBookCity(getContext(), book.outbookId, book.bookId);
			}else{
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
		}
	};
	protected void fillTheBookList(ArrayList<ContentInfoLeyue> list,boolean isEmbedToBookCity){
		for(int i = 0; i < list.size(); i++){
			ContentInfoLeyue info = list.get(i);
			BookCityCommonBookItem book = new BookCityCommonBookItem();
			book.bookId = info.getBookId();
			book.outbookId = info.getOutBookId();
			book.filePath = info.getFilePath();
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
			book.embedToBookCity = isEmbedToBookCity;
			bBookItems.add(book);
		}
	}
	
	@Override
	public boolean onPreLoad(String tag, Object... params) {
		return super.onPreLoad(tag, params);
	}

	@Override
	public abstract PagingLoadModel<?> getPagingLoadModel();

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		hideLoadView();
		return super.onFail(e, tag, params);
	}

	@Override
	public void onStart() {
		super.onStart();
		tryStartNetTack(this);
	}

	@Override
	public void onRelease() {
		super.onRelease();
		getPagingLoadModel().release();
		bBookItems.clear();
		
		bFooterViewModel.bFooterLoadingCompletedVisibility.set(false);
		bFooterViewModel.bFooterLoadingViewVisibility.set(false);
	}
	
	@Override
	public void finish() {
		onRelease();
		super.finish();
	}
	
	@Override
	public boolean isNeedReStart() {
		return !hasLoadedData();
	}

	@Override
	public boolean isStop() {
		return !getPagingLoadModel().isStart();
	}

	@Override
	public void start() {
//		showLoadView();
		getPagingLoadModel().loadPage(true);
	}

	@Override
	protected boolean hasLoadedData() {
		return bBookItems.size() > 0;
	}
	
	private class FooterViewModel implements IProguardFilter{
		public final BooleanObservable bFooterLoadingViewVisibility = bFootLoadingVisibility;
		public final BooleanObservable bFooterLoadingCompletedVisibility = bFootLoadCompletedVisibility;
	}
}
