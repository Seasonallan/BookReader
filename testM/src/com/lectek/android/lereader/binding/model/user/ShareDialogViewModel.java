package com.lectek.android.lereader.binding.model.user;

import android.content.Context;

import com.lectek.android.lereader.binding.model.BaseLoadDataViewModel;
import com.lectek.android.lereader.ui.ILoadView;

public class ShareDialogViewModel extends BaseLoadDataViewModel {

	public ShareDialogViewModel(Context context, ILoadView loadView) {
		super(context, loadView);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		// TODO Auto-generated method stub
		return false;
	}

}
