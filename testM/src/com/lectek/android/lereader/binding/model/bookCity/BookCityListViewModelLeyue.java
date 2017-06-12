package com.lectek.android.lereader.binding.model.bookCity;

import java.util.ArrayList;

import android.content.Context;

import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.ui.INetLoadView;

public class BookCityListViewModelLeyue extends BookCityCommonViewModeLeyue {

	private BookCityBookListModelLeyue mBookCityBookListModelLeyue;
	
	public BookCityListViewModelLeyue(Context context, INetLoadView loadView,int column) {
		super(context, loadView);
		mBookCityBookListModelLeyue = new BookCityBookListModelLeyue(column);
		mBookCityBookListModelLeyue.addCallBack(this);
	}

	
	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		// TODO Auto-generated method stub
		if(!isSucceed)
			return false;
		if(mBookCityBookListModelLeyue.getTag().equals(tag)){
			ArrayList<ContentInfoLeyue> list = (ArrayList<ContentInfoLeyue>)result;
			if(list != null && list.size() > 0){
				fillTheBookList(list,true);
			}else{
				setLoadPageCompleted(true);
			}
		}
		hideLoadView();
		return super.onPostLoad(result, tag, isSucceed, isCancel, params);
	}


	@Override
	public PagingLoadModel<?> getPagingLoadModel() {
		// TODO Auto-generated method stub
		return mBookCityBookListModelLeyue;
	}

}
