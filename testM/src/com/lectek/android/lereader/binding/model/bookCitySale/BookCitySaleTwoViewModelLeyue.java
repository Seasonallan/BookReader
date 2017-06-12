package com.lectek.android.lereader.binding.model.bookCitySale;

import java.util.ArrayList;

import android.content.Context;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.bookCity.BookCityCommonBookItem;
import com.lectek.android.lereader.binding.model.bookCity.BookCityCommonViewModeLeyue;
import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.ui.IBaseUserAction;
import com.lectek.android.lereader.ui.INetLoadView;

public class BookCitySaleTwoViewModelLeyue extends BookCityCommonViewModeLeyue {

	private BookCitySaleTwoModelLeyue mBookCitySaleTwoModelLeyue;
	public static final int TOTLE_BOOK_RANKID = 100005;//总榜
	public static final int BEST_BOOK_RANKID=100001;   //畅销榜
	public static final int FREE_BOOK_RANKID = 100006;  //免费榜
	public static final int NEW_BOOK_RANKID=100003;	//新书榜
	
	public BookCitySaleViewUserAciton mUserAction;
	
	public BookCitySaleTwoViewModelLeyue(Context context, INetLoadView loadView,int RankId,BookCitySaleViewUserAciton mUserAction) {
		super(context, loadView);
		this.mUserAction = mUserAction;
		//测试畅销榜
		mBookCitySaleTwoModelLeyue=new BookCitySaleTwoModelLeyue();
		mBookCitySaleTwoModelLeyue.addCallBack(this);
		mBookCitySaleTwoModelLeyue.setRankid(RankId);
		registerNetworkChange(this);
	}
	
	@Override
	public boolean onPreLoad(String tag, Object... params) {
		return super.onPreLoad(tag, params);
	}
	
//	@Override
//	public void onRelease() {
//		mBookCitySaleTwoModelLeyue.release();
//		super.onRelease();
//	}
//	
//	@Override
//	public void finish() {
//		super.finish();
//	}
	
	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if(!isSucceed){
			return false;
		}
		
		if(mBookCitySaleTwoModelLeyue.getTag().equals(tag)){
			ArrayList<ContentInfoLeyue> list = (ArrayList<ContentInfoLeyue>)result;
			if(params != null && params.length > 2){
				boolean isClearData = (Boolean) params[1];
				if(isClearData){
					bBookItems.clear();
					setLoadPageCompleted(false);
				}
			}
			if(list != null && list.size() > 0){
				fillTheBookList(list,true);
				setTopThreeTip();
			}else{
				setLoadPageCompleted(true);
			}
		}
		if(bBookItems.size() < 6){
			bFootLoadCompletedVisibility.set(false);
		}else{
			bFootLoadCompletedVisibility.set(true);
		}
		mUserAction.loadDataEnd();
//		hideLoadView();
		return super.onPostLoad(result, tag, isSucceed, isCancel, params);
	}
	
	
	public void pullToReflashData(){
		getPagingLoadModel().loadPage(false);
		//清除缓存
		getPagingLoadModel().clearDataCache();
		bFootLoadCompletedVisibility.set(false);
		bFootLoadingVisibility.set(false);
	}
	private void setTopThreeTip(){
		for(int i = 0; i<3 && i < bBookItems.size() ;i++){
			BookCityCommonBookItem book = bBookItems.get(i);
			if(i == 0){
				book.bTopThreeIVVisibility.set(true);
				book.bTopThreeIV.set(R.drawable.icon_nub1);
			}else if(i == 1){
				book.bTopThreeIVVisibility.set(true);
				book.bTopThreeIV.set(R.drawable.icon_nub2);
			}else if(i == 2){
				book.bTopThreeIVVisibility.set(true);
				book.bTopThreeIV.set(R.drawable.icon_nub3);
			}else{
				book.bTopThreeIVVisibility.set(false);
			}
		}
	}
	public interface BookCitySaleViewUserAciton extends IBaseUserAction {
		public void loadDataEnd();
	}
	
	@Override
	public PagingLoadModel<?> getPagingLoadModel() {
		return mBookCitySaleTwoModelLeyue;
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
