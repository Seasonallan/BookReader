package com.lectek.android.lereader.binding.model.bookCityWelfare;

import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.DoubleObservable;
import gueei.binding.observables.FloatObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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
import com.lectek.android.lereader.net.response.DownloadInfo;
import com.lectek.android.lereader.net.response.SubjectResultInfo;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.permanent.DownloadAPI;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.ui.IBaseUserAction;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.basereader_leyue.BaseReaderActivityLeyue;
import com.lectek.android.lereader.ui.basereader_leyue.Book;
import com.lectek.android.lereader.ui.specific.ActivityChannels;
import com.lectek.android.lereader.ui.specific.BookCityBookListActivity;
import com.lectek.android.lereader.ui.specific.ContentInfoActivityLeyue;

public class BookCityWelfareViewModelLeyue extends BaseLoadNetDataViewModel implements INetAsyncTask{
	private static final String Tag = BookCityWelfareViewModelLeyue.class.getSimpleName();
	
	public final BooleanObservable bLimitFreeVisibility = new BooleanObservable(true);
	public final ArrayListObservable<SpecialOfferItem> bSpecialOfferGridViewItems = new ArrayListObservable<SpecialOfferItem>(SpecialOfferItem.class);
	public final ArrayListObservable<SpecialOfferItem> bFreeGridViewItems = new ArrayListObservable<SpecialOfferItem>(SpecialOfferItem.class);
	public final ArrayListObservable<SubjectInfoItem> bSubjectItems1 = new ArrayListObservable<SubjectInfoItem>(SubjectInfoItem.class);
	public final ArrayListObservable<SubjectInfoItem> bSubjectItems2 = new ArrayListObservable<SubjectInfoItem>(SubjectInfoItem.class);
	
	public final StringObservable bFreeOffTime = new StringObservable();
	public final StringObservable bLimitFreeCoverUrl = new StringObservable();
	public final StringObservable bLimitFreeTitle = new StringObservable();
	public final FloatObservable bLimitFreeRatingValue = new FloatObservable();
	public final StringObservable bLimitFreeZanNum = new StringObservable();
	public final BooleanObservable bLimitFreeZanNumVisible = new BooleanObservable();
	public final StringObservable bLimitFreeAuthor = new StringObservable();
	public final StringObservable bLimitFreeContent = new StringObservable();
	public final StringObservable bFreeGetText = new StringObservable();
	public final BooleanObservable bSpecialViewVisibility = new BooleanObservable(true);
	public final BooleanObservable bFreeViewVisibility = new BooleanObservable(true);

	private BookCityWelfareViewUserAciton mUserAction;
	private BookCityWelfareDataModelLeyue mBookCityWelfareDataModelLeyue;
	private BookCityWelfareData mBookCityWelfareData;
	
	public OnClickCommand bGotoContentInfoView = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			if(mBookCityWelfareData == null)
				return;
			
			if(mBookCityWelfareData.mFreeLimitBooksList == null)
				return;
			
			ContentInfoLeyue info = mBookCityWelfareData.mFreeLimitBooksList.get(0);
			if(info == null)
				return;
			ActivityChannels.embedContentInfoLeyueActivityToBookCity(getContext(), info.outBookId, info.bookId);
//			//天翼阅读书籍
//			if(!TextUtils.isEmpty(info.getOutBookId())){
//				ActivityChannels.gotoLeyueBookDetail(getContext(), info.getOutBookId(),
//						LeyueConst.EXTRA_BOOLEAN_IS_SURFINGREADER, true,
//						LeyueConst.EXTRA_LE_BOOKID, info.getBookId());
//			}else{
//				//乐阅书籍
//				ContentInfoActivityLeyue.openActivity(getContext(), info.getBookId());
//			}
		}
	};
	
	public OnClickCommand bFreeGetClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			if(mBookCityWelfareData == null)
				return;
			
			if(mBookCityWelfareData.mFreeLimitBooksList == null)
				return;
			
			ContentInfoLeyue info = mBookCityWelfareData.mFreeLimitBooksList.get(0);
			if(info != null){
				if(bFreeGetText.get().equals(getString(R.string.free_to_get))){
					DownloadInfo downloadInfo = new DownloadInfo();
					downloadInfo.authorName = info.getAuthor();
			        downloadInfo.contentID = info.getBookId();
			        downloadInfo.contentName = info.getBookName();
			        downloadInfo.logoUrl =info.getCoverPath();
			        downloadInfo.timestamp = System.currentTimeMillis();
			        downloadInfo.url = info.getFilePath();
			        downloadInfo.state = DownloadAPI.STATE_ONLINE;
			        
			        DownloadPresenterLeyue.addBookDownload(downloadInfo);
			        bFreeGetText.set(v.getContext().getString(R.string.tools_read_try));
			        v.getContext().sendBroadcast(new Intent(AppBroadcast.ACTION_UPDATE_BOOKSHELF));
				}else{
					readBook(info);
				}
			}
		}
	};
	
	private void readBook(ContentInfoLeyue contentInfo) {
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
		ImageView cover =(ImageView)((Activity) getContext()).findViewById(R.id.limit_free_book_cover);
		if(cover != null){
			OpenBookAnimManagement.getInstance().setOpenBookAnimVIew(cover);			
		}
		BaseReaderActivityLeyue.openActivity(getContext(), book, true);
	}
	
	public OnItemClickCommand bSpecialOfferGridViewItemClickedCommand = new OnItemClickCommand() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			SpecialOfferItem book = bSpecialOfferGridViewItems.get(position);
			ActivityChannels.embedContentInfoLeyueActivityToBookCity(getContext(), book.bOutBookId, book.bRecommendedBookId);
//			ActivityChannels.goToContentInfoActivity(getContext(), book.bOutBookId, book.bRecommendedBookId);
		}
	};
	
	public OnItemClickCommand bFreeGridViewItemClickedCommand = new OnItemClickCommand() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			SpecialOfferItem book = bFreeGridViewItems.get(position);
			ActivityChannels.embedContentInfoLeyueActivityToBookCity(getContext(), book.bOutBookId, book.bRecommendedBookId);
//			ActivityChannels.goToContentInfoActivity(getContext(), book.bOutBookId, book.bRecommendedBookId);
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
	
	
	public final OnClickCommand bFreeMoreClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			ActivityChannels.embedBookRecommendActivityToBookCity(getContext(), 
					BookCityBookListActivity.FREE_BOOKS_RECOMMEND_COLUMN,getString(R.string.bookcity_free_tip));
//			BookCityBookListActivity.openActivity(getContext(), BookCityBookListActivity.FREE_BOOKS_RECOMMEND_COLUMN);
		}
	};
	
	public final OnClickCommand bNewbookMoreClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			ActivityChannels.embedBookRecommendActivityToBookCity(getContext(), 
					BookCityBookListActivity.LATEST_PRICE_BOOKS_RECOMMEND_COLUMN,getString(R.string.bookcity_latest_price));
//			BookCityBookListActivity.openActivity(getContext(), BookCityBookListActivity.LATEST_PRICE_BOOKS_RECOMMEND_COLUMN);
		}
	};

	public BookCityWelfareViewModelLeyue(Context context,
			INetLoadView loadView, BookCityWelfareViewUserAciton userAction) {
		super(context, loadView);
		mUserAction = userAction;
		mBookCityWelfareDataModelLeyue = new BookCityWelfareDataModelLeyue();
		mBookCityWelfareDataModelLeyue.addCallBack(this);
	}

	@Override
	public void onRelease() {
		super.onRelease();
		
		mBookCityWelfareDataModelLeyue.release();
		bSpecialOfferGridViewItems.clear();
		bFreeGridViewItems.clear();
		bSubjectItems1.clear();
		bSubjectItems2.clear();
		
		LogUtil.i(Tag, "onRelease");
	}
	
	@Override
	public void finish() {
		onRelease();
		super.finish();
		
		LogUtil.i(Tag, "finish");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if(!fillTheWelfareData(true)) {
			tryStartNetTack(this);
		}
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		showRetryView();
		return false;
	}

	private void subTheSubjectList(ArrayList<SubjectResultInfo> list){
		ArrayList<SubjectResultInfo> temp1 = new ArrayList<SubjectResultInfo>();
		ArrayList<SubjectResultInfo> temp2 = new ArrayList<SubjectResultInfo>();
		temp1.addAll(list.subList(0, 4));
		if(list.size() > 4) {
			temp2.addAll(list.subList(4, list.size()-4));
		}
//		for (int i = 0; i < list.size(); i++) {
//			if(list.size() > 6){
//				if(i < list.size()- 6){
//					temp1.add(list.get(i));
//				}else{
//					temp2.add(list.get(i));
//				}
//			}else{
//				temp1.add(list.get(i));
//			}
//		}
		
		mUserAction.loadSubjectOver(temp1);
		if(temp2.size() > 0){
			setTheSubjectList(temp2);
		}
	}
	private void setTheSubjectList(ArrayList<SubjectResultInfo> list){
		bSubjectItems1.clear();
		bSubjectItems2.clear();
		
		for(int i=0;i< Math.min(6, list.size());i++){
			SubjectResultInfo subjectInfo = list.get(i);
			SubjectInfoItem subjectItem = new SubjectInfoItem();
			subjectItem.subjectId = subjectInfo.getSubjectId();
			subjectItem.bSubjectUrl.set(subjectInfo.getSubjectPic());
			if(i<2){
				bSubjectItems1.add(subjectItem);
			}else{
				bSubjectItems2.add(subjectItem);
			}
		}
	}
	
	private boolean fillTheWelfareData(boolean async){
		
		if(mBookCityWelfareData == null || mBookCityWelfareData.mWelfareSubjectList == null || mBookCityWelfareData.mWelfareSubjectList.size() <= 0
							|| mBookCityWelfareData.mLatestSpecialOfferList == null || mBookCityWelfareData.mLatestSpecialOfferList.size() <= 0
							|| mBookCityWelfareData.mFreeBooksList == null || mBookCityWelfareData.mFreeBooksList.size() <= 0) {
			return false;
		}
		
		if(async) {
			showLoadView();
			
			MyAndroidApplication.getHandler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					subTheSubjectList(mBookCityWelfareData.mWelfareSubjectList);
					if(mBookCityWelfareData.mFreeLimitBooksList != null){
						fillFreeLimitSubject(mBookCityWelfareData.mFreeLimitBooksList);
					}else{
						bLimitFreeVisibility.set(false);
					}
					fillLatestSpecialOfferList(mBookCityWelfareData.mLatestSpecialOfferList);
					fillTheFreeSubjectList(mBookCityWelfareData.mFreeBooksList);
					hideLoadView();
				}
			}, 300);
		}else {
			subTheSubjectList(mBookCityWelfareData.mWelfareSubjectList);
			if(mBookCityWelfareData.mFreeLimitBooksList != null){
				fillFreeLimitSubject(mBookCityWelfareData.mFreeLimitBooksList);
			}else{
				bLimitFreeVisibility.set(false);
			}
			fillLatestSpecialOfferList(mBookCityWelfareData.mLatestSpecialOfferList);
			fillTheFreeSubjectList(mBookCityWelfareData.mFreeBooksList);
		}
		return true;
	}
	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if(!isSucceed)
			return false;
		if(mBookCityWelfareDataModelLeyue.getTag().equals(tag)){
			mBookCityWelfareData = (BookCityWelfareData) result;
			if(mBookCityWelfareData != null){
				fillTheWelfareData(false);
			}
		}

		hideLoadView();
		mUserAction.loadDataEnd();
		return false;
	}

	private void fillFreeLimitSubject(ArrayList<ContentInfoLeyue> list){
		if(list.size() > 0){
			bLimitFreeVisibility.set(true);
			ContentInfoLeyue book = list.get(0);
			bFreeOffTime.set(getString(R.string.cut_off_time, book.endTime));
			bLimitFreeCoverUrl.set(book.getCoverPath());
			bLimitFreeTitle.set(book.getBookName());
			if(book.getStarLevel() == null){
				bLimitFreeRatingValue.set(5.0f);;
				bLimitFreeZanNum.set("(" + 10.0f+")");	
			}else{
				bLimitFreeRatingValue.set((float) (book.getStarLevel()/2f));
				bLimitFreeZanNum.set("(" + book.getStarLevel()+")");				
			}
			bLimitFreeZanNumVisible.set(true);
			bLimitFreeAuthor.set(book.getAuthor());
			bLimitFreeContent.set(book.getIntroduce());
			if(DownloadPresenterLeyue.checkBookDownloadedExist(book.getBookId())){
				bFreeGetText.set(getString(R.string.tools_read_try));
			}else{
				bFreeGetText.set(getString(R.string.free_to_get));
			}
		}else{
			bLimitFreeVisibility.set(false);
		}
	}
	
	private void fillLatestSpecialOfferList(ArrayList<ContentInfoLeyue> list){
		bSpecialOfferGridViewItems.clear();
		for (int i = 0; i < list.size(); i++) {
			ContentInfoLeyue contentInfoLeyue = list.get(i);
			SpecialOfferItem item = new SpecialOfferItem();
			item.bRecommendedBookName.set(contentInfoLeyue.getBookName());
			item.bRecommendedCoverUrl.set(contentInfoLeyue.getCoverPath());
			item.bRecommendedBookAuthor.set(contentInfoLeyue.getAuthor());
			LogUtil.i("yyl", contentInfoLeyue.getPrice() + " "
					+ contentInfoLeyue.getPromotionPrice());
			// item.bRecommendedBookPrice.set(Double.parseDouble(contentInfoLeyue.getPrice()));
			// item.bRecommendedBookDiscountPrice.set(Double.parseDouble(contentInfoLeyue.getPromotionPrice()));
			if(contentInfoLeyue.isFee != null && contentInfoLeyue.isFee.equals("0")){
				item.bRecommendedBookDiscountPrice.set(getResources().getString(R.string.catalog_chapter_for_free));
				item.bTextColor.set(getResources().getColor(R.color.common_16));
				item.bOldPriceVisibility.set(false);
			}else{
				item.bTextColor.set(getResources().getColor(R.color.color_ed5145));
				if(!TextUtils.isEmpty(contentInfoLeyue.getPromotionPrice())){
					item.bRecommendedBookDiscountPrice.set("￥"+contentInfoLeyue
							.getPromotionPrice());
					
					if(TextUtils.isEmpty(contentInfoLeyue.getPrice())){
						item.bOldPriceVisibility.set(false);
					}else{
						item.bRecommendedBookPrice.set("￥"+contentInfoLeyue.getPrice());
						item.bOldPriceVisibility.set(true);
					}
				}else{
					item.bRecommendedBookDiscountPrice.set("￥"+contentInfoLeyue.getPrice());
					item.bOldPriceVisibility.set(false);
				}
			}
			item.bRecommendedBookId=contentInfoLeyue.getBookId();
			item.bOutBookId = contentInfoLeyue.getOutBookId();
			bSpecialOfferGridViewItems.add(item);
		}
	}
	
	private void fillTheFreeSubjectList(ArrayList<ContentInfoLeyue> list){
		bFreeGridViewItems.clear();
		for (int i = 0; i < list.size(); i++) {
			ContentInfoLeyue contentInfoLeyue = list.get(i);
			SpecialOfferItem item = new SpecialOfferItem();
			item.bRecommendedBookName.set(contentInfoLeyue
					.getBookName());
			item.bRecommendedCoverUrl.set(contentInfoLeyue
					.getCoverPath());
			item.bRecommendedBookAuthor.set(contentInfoLeyue
					.getAuthor());
			if(contentInfoLeyue.isFee != null && contentInfoLeyue.isFee.equals("0")){
				item.bRecommendedBookDiscountPrice.set(getResources().getString(R.string.catalog_chapter_for_free));
				item.bTextColor.set(getResources().getColor(R.color.common_16));
				item.bOldPriceVisibility.set(false);
			}else{
				item.bTextColor.set(getResources().getColor(R.color.color_ed5145));
				if(TextUtils.isEmpty(contentInfoLeyue.getPromotionPrice())){
					item.bRecommendedBookDiscountPrice.set("￥"+contentInfoLeyue
							.getPrice());
					item.bOldPriceVisibility.set(false);
				}else{
					item.bRecommendedBookDiscountPrice.set("￥"+contentInfoLeyue
							.getPromotionPrice());
					if(TextUtils.isEmpty(contentInfoLeyue.getPrice())){
						item.bOldPriceVisibility.set(false);
					}else{
						item.bRecommendedBookPrice.set("￥"+contentInfoLeyue.getPrice());
						item.bOldPriceVisibility.set(true);
					}
				}
			}
			
//			item.bRecommendedBookPrice.set("￥"+contentInfoLeyue
//					.getPrice());
			item.bRecommendedBookId=contentInfoLeyue.getBookId();
			item.bOutBookId = contentInfoLeyue.getOutBookId();
			
			bFreeGridViewItems.add(item);

		}
	}
	public interface BookCityWelfareViewUserAciton extends IBaseUserAction {
		public void loadDataEnd();
		public void loadSubjectOver(ArrayList<SubjectResultInfo> subjectList);
	}

	@Override
	protected boolean hasLoadedData() {
		return mBookCityWelfareData != null;
	}

	public static class SpecialOfferItem {
		public StringObservable bRecommendedCoverUrl = new StringObservable();
		public StringObservable bRecommendedBookName = new StringObservable();
		public StringObservable bRecommendedBookAuthor = new StringObservable();
		public StringObservable bRecommendedBookPrice = new StringObservable();
		public StringObservable bRecommendedBookDiscountPrice = new StringObservable();
		public IntegerObservable bTextColor = new IntegerObservable();
		public BooleanObservable bOldPriceVisibility = new BooleanObservable();
//		public StringObservable bRecommendedBookId = new StringObservable();
		public String bRecommendedBookId;
		public String bOutBookId;
	}

	@Override
	public boolean isNeedReStart() {
		return !hasLoadedData();
	}

	@Override
	public boolean isStop() {
		return !mBookCityWelfareDataModelLeyue.isStart();
	}

	@Override
	public void start() {
		showLoadView();
//		mBookCityWelfareData = mBookCityWelfareDataModelLeyue.getCachedData();
//		if(mBookCityWelfareData!= null){
//			fillTheWelfareData();
//		}else{
			mBookCityWelfareDataModelLeyue.start(true);
//		}
	}
	
	public void pullRefershStart(){
		mBookCityWelfareDataModelLeyue.start(true);
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
