package com.lectek.android.lereader.binding.model.cover;

import java.util.ArrayList;

import android.content.Context;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.binding.model.bookcityrecommend.BookCityRecommendModelLeyue;
import com.lectek.android.lereader.net.response.SubjectResultInfo;
import com.lectek.android.lereader.ui.IBaseUserAction;
import com.lectek.android.lereader.ui.INetLoadView;

public class CoverViewModelLeyue extends BaseLoadNetDataViewModel {
	protected BookCityRecommendModelLeyue mBookCityRecommendModelLeyue; // 专题
	public CoverViewUserAciton mUserAction;


	public CoverViewModelLeyue(Context context,
			INetLoadView loadView, CoverViewUserAciton userAction) {
		super(context, loadView);
		this.mUserAction = userAction;
		mBookCityRecommendModelLeyue = new BookCityRecommendModelLeyue();
		mBookCityRecommendModelLeyue.addCallBack(this);
		mBookCityRecommendModelLeyue.start();
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		// TODO Auto-generated method stub
		showLoadView();
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		// TODO Auto-generated method stub
		hideLoadView();
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		// TODO Auto-generated method stub
		if (mBookCityRecommendModelLeyue.getTag().equals(tag)) {
			ArrayList<SubjectResultInfo> list = (ArrayList<SubjectResultInfo>) result;
			if (list != null && list.size() > 0) {
				mUserAction.loadSubjectOver(list);
			}
		}
		hideLoadView();
		return false;
	}


	@Override
	protected boolean hasLoadedData() {
		// TODO Auto-generated method stub
		return false;
	}

	public interface CoverViewUserAciton extends IBaseUserAction {
		public void loadSubjectOver(ArrayList<SubjectResultInfo> subjectList);
	}

}
