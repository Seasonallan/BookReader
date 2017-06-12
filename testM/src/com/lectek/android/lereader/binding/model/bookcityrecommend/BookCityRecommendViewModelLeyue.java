package com.lectek.android.lereader.binding.model.bookcityrecommend;

import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.binding.command.OnItemClickCommand;
import com.lectek.android.lereader.animation.OpenBookAnimManagement;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.binding.model.bookCity.SubjectInfoItem;
import com.lectek.android.lereader.download.DownloadHttpEngine;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.net.response.SubjectResultInfo;
import com.lectek.android.lereader.ui.IBaseUserAction;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.basereader_leyue.BaseReaderActivityLeyue;
import com.lectek.android.lereader.ui.basereader_leyue.Book;
import com.lectek.android.lereader.ui.specific.ActivityChannels;
import com.lectek.android.lereader.ui.specific.BookCityBookListActivity;

public class BookCityRecommendViewModelLeyue extends BaseLoadNetDataViewModel implements INetAsyncTask{
	private static final String Tag = BookCityRecommendViewModelLeyue.class.getSimpleName();
	private BookCityRecommendViewUserAciton mUserAction;

	//数据获取model
	private BookCityRecommendDataModelLeyue mBookCityRecommendDataModelLeyue;
	public final ArrayListObservable<HeavyRecommendItem> bHeavyRecommendItems = new ArrayListObservable<HeavyRecommendItem>(
			HeavyRecommendItem.class);
	public final ArrayListObservable<BookItem> bNewBookItems = new ArrayListObservable<BookItem>(
			BookItem.class);
	public final ArrayListObservable<BookItem> bAllLoveItems = new ArrayListObservable<BookItem>(
			BookItem.class);
	
	public final ArrayListObservable<SubjectInfoItem> bSubjectItems1 = new ArrayListObservable<SubjectInfoItem>(
			SubjectInfoItem.class);
	
	public final ArrayListObservable<SubjectInfoItem> bSubjectItems2 = new ArrayListObservable<SubjectInfoItem>(
			SubjectInfoItem.class);
	
	public final ArrayListObservable<SubjectInfoItem> bEditorRecommendItems = new ArrayListObservable<SubjectInfoItem>(
			SubjectInfoItem.class);
	
	private BookCityRecommendData mRecommendData;
	
	public BookCityRecommendViewModelLeyue(Context context,
			INetLoadView loadView, BookCityRecommendViewUserAciton userAction) {
		super(context, loadView);
		this.mUserAction = userAction;
		mBookCityRecommendDataModelLeyue = new BookCityRecommendDataModelLeyue();
		mBookCityRecommendDataModelLeyue.addCallBack(this);
		registerNetworkChange(this);
	}

	// 重磅推荐更多点击
	public final OnClickCommand bRecommendMoreClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			ActivityChannels.embedBookRecommendActivityToBookCity(getContext(), 
					BookCityBookListActivity.HEAVY_RECOMMEND_BOOKS_COLUMN,getString(R.string.bookcity_heavyrecommend_tip));
//			BookCityBookListActivity.openActivity(getContext(), BookCityBookListActivity.HEAVY_RECOMMEND_BOOKS_COLUMN);
		}
	};
	
	// 新书抢先看更多点击
	public final OnClickCommand bNewbookMoreClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			ActivityChannels.embedBookRecommendActivityToBookCity(getContext(), 
					BookCityBookListActivity.NEW_BOOKS_RECOMMEND_COLUMN,getString(R.string.bookcity_newbook_tip));
//			BookCityBookListActivity.openActivity(getContext(), BookCityBookListActivity.NEW_BOOKS_RECOMMEND_COLUMN);
		}
	};
	
	// 大家都爱看更多点击
	public final OnClickCommand bAllLoveMoreClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			ActivityChannels.embedBookRecommendActivityToBookCity(getContext(), 
					BookCityBookListActivity.ALL_READ_BOOKS_RECOMMEND_COLUMN,getString(R.string.bookcity_alllovebook_tip));
//			BookCityBookListActivity.openActivity(getContext(), BookCityBookListActivity.ALL_READ_BOOKS_RECOMMEND_COLUMN);
		}
	};

	// 重磅推荐item点击
	public OnItemClickCommand bHeavyRecommendItemClick = new OnItemClickCommand() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			HeavyRecommendItem book = bHeavyRecommendItems.get(position);
			ActivityChannels.embedContentInfoLeyueActivityToBookCity(getContext(), book.outBookId, book.bookId);
//			ActivityChannels.goToContentInfoActivity(getContext(),book.outBookId, book.bookId);
		}
	};
	
	public OnItemClickCommand bNewBookItemClick = new OnItemClickCommand() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			BookItem book = bNewBookItems.get(position);
			ActivityChannels.embedContentInfoLeyueActivityToBookCity(getContext(), book.outBookId, book.bookId);
//			ActivityChannels.goToContentInfoActivity(getContext(),book.outBookId, book.bookId);
		}
	};
	
	public OnItemClickCommand bAllLoveItemClick = new OnItemClickCommand() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			BookItem book = bAllLoveItems.get(position);
			ActivityChannels.embedContentInfoLeyueActivityToBookCity(getContext(), book.outBookId, book.bookId);
//			ActivityChannels.goToContentInfoActivity(getContext(),book.outBookId, book.bookId);
		}
	};
	
	public OnItemClickCommand bSubjectItems1Click = new OnItemClickCommand() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			SubjectInfoItem item = bSubjectItems1.get(position);
			ActivityChannels.goToSubjectDetailActivity(getContext(), item);
		}
	};
	
	public OnItemClickCommand bSubjectItems2Click = new OnItemClickCommand() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			SubjectInfoItem item = bSubjectItems2.get(position);
			ActivityChannels.goToSubjectDetailActivity(getContext(), item);
		}
	};
	
	public OnItemClickCommand bEditorRecommendItems1Click = new OnItemClickCommand() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			SubjectInfoItem item = bEditorRecommendItems.get(position);
			ActivityChannels.goToSubjectDetailActivity(getContext(), item);
		}
	};
	
	@Override
	public void onStart() {
		super.onStart();
		if(!fillTheData(true)) {
			tryStartNetTack(this);
		}
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
//		showLoadView();
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		hideLoadView();
		showRetryView();
		return false;
	}

	private boolean fillTheData(boolean async){
		
		if(mRecommendData == null || mRecommendData.mRecommendSubjectList == null || mRecommendData.mRecommendSubjectList.size() <= 0
				|| mRecommendData.mEditorRecommendSubjectList == null || mRecommendData.mEditorRecommendSubjectList.size() <= 0
				|| mRecommendData.mHeavyRecommendList == null || mRecommendData.mHeavyRecommendList.size() <= 0
				|| mRecommendData.mAllLoveRecommendList == null || mRecommendData.mAllLoveRecommendList.size() <= 0) {
			return false;
		}
	
		if(async) {
			showLoadView();
			MyAndroidApplication.getHandler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					mUserAction.loadSubjectOver(mRecommendData.mRecommendSubjectList);
					getEditorRecommendList(mRecommendData.mEditorRecommendSubjectList);
					getHeavyRecommendList(mRecommendData.mHeavyRecommendList);
					getAllLoveBookList(mRecommendData.mAllLoveRecommendList);
					getNewBookList(mRecommendData.mNewBookRecommendList);
					hideLoadView();
				}
			}, 300);
		}else {
			mUserAction.loadSubjectOver(mRecommendData.mRecommendSubjectList);
			getEditorRecommendList(mRecommendData.mEditorRecommendSubjectList);
			getHeavyRecommendList(mRecommendData.mHeavyRecommendList);
			getAllLoveBookList(mRecommendData.mAllLoveRecommendList);
			getNewBookList(mRecommendData.mNewBookRecommendList);
		}
		return true;
	}
	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if(!isSucceed)
			return false;
		if(mBookCityRecommendDataModelLeyue.getTag().equals(tag)){
			mRecommendData = (BookCityRecommendData) result;
			if(mRecommendData != null){
				fillTheData(false);
			}
		}
		mUserAction.loadDataEnd();
		hideLoadView();
		return false;
	}

	private void getHeavyRecommendList(
			ArrayList<ContentInfoLeyue> heavyRecommendList) {
		bHeavyRecommendItems.clear();
		for (int i = 0; i < heavyRecommendList.size(); i++) {
			ContentInfoLeyue heavyRecommend = heavyRecommendList.get(i);
			HeavyRecommendItem item = new HeavyRecommendItem();
			item.bookId = heavyRecommend.getBookId();
			item.outBookId = heavyRecommend.getOutBookId();
			item.bRecommendedCoverUrl.set(heavyRecommend.getCoverPath());
			item.bBookName.set(heavyRecommend.getBookName());
			item.bAuthorName.set(heavyRecommend.author);
			item.bDecContent.set(heavyRecommend.introduce);
			item.bFreeReadBtn.set(getString(R.string.content_info_btn_try_read));
			item.bFreeReadTextColor.set(Color.RED);
			item.bFreeReadReadVisibility.set(View.VISIBLE);
			item.bRoundProgressBarVisibility.set(false);
			item.bProgress.set(0);
			item.mContentInfo = heavyRecommend;
			bHeavyRecommendItems.add(item);
		}
	}

	private void getNewBookList(ArrayList<ContentInfoLeyue> newBookList) {
		bNewBookItems.clear();
		for (int i = 0; i < newBookList.size(); i++) {
			ContentInfoLeyue newBookContentinfo = newBookList.get(i);
			BookItem item = new BookItem();
			item.bookId = newBookContentinfo.getBookId();
			item.outBookId = newBookContentinfo.getOutBookId();
			item.bRecommendedCoverUrl.set(newBookContentinfo.getCoverPath());
			item.bRecommendedBookName.set(newBookContentinfo.getBookName());
			item.bRecommendedBookAuthor.set(newBookContentinfo.author);
			if(newBookContentinfo.isFee != null && newBookContentinfo.isFee.equals("0")){
				item.bRecommendedBookDiscountPrice.set(getResources().getString(R.string.catalog_chapter_for_free));
				item.bTextColor.set(getResources().getColor(R.color.common_16));
				item.bOldPriceVisibility.set(false);
			}else{
				item.bTextColor.set(getResources().getColor(R.color.color_ed5145));
				if(!TextUtils.isEmpty(newBookContentinfo.getPromotionPrice())){
					item.bRecommendedBookDiscountPrice.set("￥"+newBookContentinfo
							.getPromotionPrice());
					
					if(TextUtils.isEmpty(newBookContentinfo.getPrice())){
						item.bOldPriceVisibility.set(false);
					}else{
						item.bRecommendedBookPrice.set("￥"+newBookContentinfo.getPrice());
						item.bOldPriceVisibility.set(true);
					}
				}else{
					item.bRecommendedBookDiscountPrice.set("￥"+newBookContentinfo.getPrice());
					item.bOldPriceVisibility.set(false);
				}
			}
			bNewBookItems.add(item);
		}
	}

	private void getAllLoveBookList(ArrayList<ContentInfoLeyue> allLoveBookList) {
		bAllLoveItems.clear();
		for (int i = 0; i < allLoveBookList.size(); i++) {
			ContentInfoLeyue newBookContentinfo = allLoveBookList.get(i);
			BookItem item = new BookItem();
			item.bookId = newBookContentinfo.getBookId();
			item.outBookId = newBookContentinfo.getOutBookId();
			item.bRecommendedCoverUrl.set(newBookContentinfo.getCoverPath());
			item.bRecommendedBookName.set(newBookContentinfo.getBookName());
			item.bRecommendedBookAuthor.set(newBookContentinfo.author);
			
			if(newBookContentinfo.isFee != null && newBookContentinfo.isFee.equals("0")){
				item.bRecommendedBookDiscountPrice.set(getResources().getString(R.string.catalog_chapter_for_free));
				item.bTextColor.set(getResources().getColor(R.color.common_16));
				item.bOldPriceVisibility.set(false);
			}else{
				item.bTextColor.set(getResources().getColor(R.color.color_ed5145));
				if(!TextUtils.isEmpty(newBookContentinfo.getPromotionPrice())){
					item.bRecommendedBookDiscountPrice.set("￥"+newBookContentinfo
							.getPromotionPrice());
					
					if(TextUtils.isEmpty(newBookContentinfo.getPrice())){
						item.bOldPriceVisibility.set(false);
					}else{
						item.bRecommendedBookPrice.set("￥"+newBookContentinfo.getPrice());
						item.bOldPriceVisibility.set(true);
					}
				}else{
					item.bRecommendedBookDiscountPrice.set("￥"+newBookContentinfo.getPrice());
					item.bOldPriceVisibility.set(false);
				}
			}
			bAllLoveItems.add(item);
		}
	}
	
	private void getEditorRecommendList(List<SubjectResultInfo> subjectList) {
		bSubjectItems1.clear();
		bSubjectItems2.clear();
		bEditorRecommendItems.clear();
		for (int i = 0; i < subjectList.size(); i++) {
			SubjectResultInfo subjectInfo = subjectList.get(i);
			SubjectInfoItem editorItem = new SubjectInfoItem();
			editorItem.subjectId = subjectInfo.getSubjectId();
			editorItem.subjectTitle = subjectInfo.getSubjectName();
			editorItem.subjectContent = subjectInfo.getSubjectIntro();
			editorItem.bSubjectUrl.set(subjectInfo.getSubjectPic());
			if(i<2){
				bSubjectItems1.add(editorItem);
			}else if(i<4 && i>1){
				bSubjectItems2.add(editorItem);
			}else{
				bEditorRecommendItems.add(editorItem);
			}
		}
	}

	@Override
	protected boolean hasLoadedData() {
		return mRecommendData != null;
	}

	public interface BookCityRecommendViewUserAciton extends IBaseUserAction {
		public void loadSubjectOver(ArrayList<SubjectResultInfo> subjectList);
		public void loadDataEnd();
	}

	/**
	 * 重磅推荐
	 * 
	 * @author Administrator
	 * 
	 */
	public class HeavyRecommendItem {
		public String bookId;
		public String outBookId;
		public StringObservable bRecommendedCoverUrl = new StringObservable();
		public StringObservable bBookName = new StringObservable();
		public StringObservable bAuthorName = new StringObservable();
		public StringObservable bDecContent = new StringObservable();

		public StringObservable bFreeReadBtn = new StringObservable();
		public IntegerObservable bFreeReadTextColor = new IntegerObservable();
		public IntegerObservable bFreeReadReadVisibility = new IntegerObservable();
		public BooleanObservable bRoundProgressBarVisibility = new BooleanObservable(false);
		public IntegerObservable bProgress = new IntegerObservable();
		
		private ContentInfoLeyue mContentInfo;
		public OnClickCommand bFreeReadClick = new OnClickCommand() {
			
			@Override
			public void onClick(View v) {
				readBook(mContentInfo,(ImageView)v.findViewById(R.id.book_info_cover));
			}
		};
	}
	
	private void readBook(ContentInfoLeyue contentInfo,ImageView cover) {
		Book book = new Book();
		book.setBookId(contentInfo.getBookId());
		book.setBookType(contentInfo.getBookType());
		book.setBookFormatType(DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_BOOK);
		book.setFeeStart(contentInfo.getFeeStart());
		// 书籍阅读界面，将根据isOrder字段来判断需要排版的章节数，所以免费书籍也要在进入阅读前，设置为isOrder。
		if ("0".equals(contentInfo.getIsFee())) {
			book.setOrder(true);
		} else {
			book.setOrder(contentInfo.isOrder());
		}
		book.setPrice(contentInfo.getPrice());
		book.setPromotionPrice(contentInfo.getPromotionPrice());
		book.setAuthor(contentInfo.getAuthor());
		book.setBookName(contentInfo.getBookName());
		book.setCoverPath(contentInfo.getCoverPath());
		book.setOnline(true);
		if (contentInfo.getLimitType() != null
				&& contentInfo.getLimitType() == 2) {
			book.setLimitPrice(contentInfo.getLimitPrice() + "");
		}
		if(cover != null){
			OpenBookAnimManagement.getInstance().setOpenBookAnimVIew(cover);			
		}
		BaseReaderActivityLeyue.openActivity(getContext(), book, true);
	}

	/**
	 * 新书抢先看
	 * 
	 * @author Administrator
	 * 
	 */
	public class BookItem {
		public String bookId;
		public String outBookId;
		public StringObservable bRecommendedCoverUrl = new StringObservable();
		public StringObservable bRecommendedBookName = new StringObservable();
		public StringObservable bRecommendedBookAuthor = new StringObservable();
		public StringObservable bRecommendedBookPrice = new StringObservable();
		public StringObservable bRecommendedBookDiscountPrice = new StringObservable();
		public IntegerObservable bTextColor = new IntegerObservable();
		public BooleanObservable bOldPriceVisibility = new BooleanObservable();
	}

	@Override
	public boolean isNeedReStart() {
		return !hasLoadedData();
	}

	@Override
	public boolean isStop() {
		return !mBookCityRecommendDataModelLeyue.isStart();
	}

	@Override
	public void onRelease() {
		super.onRelease();
		
		mBookCityRecommendDataModelLeyue.release();
		
		bHeavyRecommendItems.clear();
		bNewBookItems.clear();
		bAllLoveItems.clear();
		bSubjectItems1.clear();
		bSubjectItems2.clear();
		bEditorRecommendItems.clear();
		
		LogUtil.i(Tag, "onReleasse");
	}

	@Override
	public void finish() {
		onRelease();
		super.finish();
		
		LogUtil.i(Tag, "finish");
	}
	
	@Override
	public void start() {
		showLoadView();
		//先读缓存数据
//		mRecommendData = mBookCityRecommendDataModelLeyue.getCachedData();
//		if(mRecommendData != null){
//			fillTheData();
//		}else{
			mBookCityRecommendDataModelLeyue.start(true);
//		}
	}
	/**
	 * 下拉刷新
	 */
	public void pullRefershStart(){
		mBookCityRecommendDataModelLeyue.start(true);
	}

	@Override
	public void onChange(boolean isAvailable) {
		if(isAvailable){
			hideNetSettingView();
			if(isNeedReStart()){
				tryStartNetTack(this);
			}
		}
	}
}
