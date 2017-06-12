package com.lectek.android.lereader.binding.model.bookCityClassify;

import java.util.ArrayList;

import android.content.Context;

import com.lectek.android.lereader.binding.model.bookCity.BookCityCommonViewModeLeyue;
import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.ui.INetLoadView;

public class BookCityClassifyDetailViewModelLeyue extends BookCityCommonViewModeLeyue {

	private BookCityClassifyDetailModelLeyue mBookCityClassifyDetailModelLeyue;
	
	public BookCityClassifyDetailViewModelLeyue(Context context, INetLoadView loadView,int bookType) {
		super(context, loadView);
		mBookCityClassifyDetailModelLeyue = new BookCityClassifyDetailModelLeyue(bookType);
		mBookCityClassifyDetailModelLeyue.addCallBack(this);
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		// TODO Auto-generated method stub
		if(!isSucceed)
			return false;
		if(mBookCityClassifyDetailModelLeyue.getTag().equals(tag)){
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
		return mBookCityClassifyDetailModelLeyue;
	}

}
