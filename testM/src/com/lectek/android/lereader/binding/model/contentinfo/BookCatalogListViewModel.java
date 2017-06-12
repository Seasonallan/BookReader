package com.lectek.android.lereader.binding.model.contentinfo;

import gueei.binding.observables.BooleanObservable;

import java.util.ArrayList;

import android.content.Context;

import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.binding.model.common.PagingLoadViewModel;
import com.lectek.android.lereader.lib.utils.IProguardFilter;
import com.lectek.android.lereader.net.response.BookCatalog;
import com.lectek.android.lereader.ui.INetLoadView;

public class BookCatalogListViewModel extends PagingLoadViewModel {
	
	private BookCatalogModel mBookCatalogModel;
	private BookCatalogModelSurfingReader mBookCatalogModelSR;//天翼阅读目录接口
	private UserActionListener mUserActionListener;
	private ArrayList<BookCatalog> mDataSource;
	private boolean mIsSurfingReader;

	public final FooterViewModel bFooterViewModel = new FooterViewModel();
	
	public BookCatalogListViewModel(Context context, INetLoadView loadView, String bookId) {
		super(context, loadView);
		mBookCatalogModel = new BookCatalogModel();
		mBookCatalogModel.setmBookId(bookId);
		mBookCatalogModel.addCallBack(this);
		
		mBookCatalogModelSR = new BookCatalogModelSurfingReader();
		mBookCatalogModelSR.setmBookId(bookId);
		mBookCatalogModelSR.addCallBack(this);
	}
	
	public void setIsSurfingReader(boolean mIsSurfingReader) {
		this.mIsSurfingReader = mIsSurfingReader;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if(!isCancel && result != null){
			if (mBookCatalogModel.getTag().equals(tag)) {
				mDataSource = (ArrayList<BookCatalog>) result;
				if (mUserActionListener != null) {
					mUserActionListener.setCatalogListInfo(mDataSource);
				}
			}
			if(mBookCatalogModelSR.getTag().equals(tag)){
				mDataSource = (ArrayList<BookCatalog>) result;
				if (mUserActionListener != null) {
					mUserActionListener.setCatalogListInfo(mDataSource);
				}
			}
		}
        hideLoadView();
		return super.onPostLoad(result, tag, isSucceed, isCancel, params);
	}

	@Override
	protected boolean hasLoadedData() {
		return mDataSource != null;
	}

	@Override
	public void onStart() {
		super.onStart();
		if(mIsSurfingReader){
			mBookCatalogModelSR.loadPage();
		}else{
			mBookCatalogModel.loadPage();
		}
	}
	
	public void setUserActionListener(UserActionListener listener){
		mUserActionListener = listener;
	}
	
	public static interface UserActionListener{
		public void setCatalogListInfo(ArrayList<BookCatalog> list);
	}

	@Override
	protected PagingLoadModel<?> getPagingLoadModel() {
		if(mIsSurfingReader){
			return mBookCatalogModelSR;
		}else{
			return mBookCatalogModel;
		}
	}
	
	private class FooterViewModel implements IProguardFilter{
		public final BooleanObservable bFooterLoadingViewVisibility = BookCatalogListViewModel.this.bFootLoadingVisibility;
	}

}
