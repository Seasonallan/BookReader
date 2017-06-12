package com.lectek.android.lereader.binding.model.bookCitySearch;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;

import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.binding.model.search.SearchKeyWordModel;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.response.KeyWord;
import com.lectek.android.lereader.ui.IBaseUserAction;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.specific.ActivityChannels;

public class BookCitySearchViewModelLeyue extends BaseLoadNetDataViewModel implements INetAsyncTask{
	
	private static final String Tag = BookCitySearchViewModelLeyue.class.getSimpleName();
	
	private SearchKeyWordModel mSearchKeyWordModel;
	private BookCitySearchViewUserAciton mUserAction;
	private ArrayList<KeyWord> mkeywordList;
	
	public BookCitySearchViewModelLeyue(Context context, INetLoadView loadView, BookCitySearchViewUserAciton userAction) {
		super(context, loadView);
		mUserAction = userAction;
		mSearchKeyWordModel = new SearchKeyWordModel();
		mSearchKeyWordModel.addCallBack(this);
	}
	
	public final OnClickCommand bSearchBtnClicked = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			ActivityChannels.gotoSearchListActivity(getContext());
		}
	};
	
	@Override
	public void onStart() {
		super.onStart();
		tryStartNetTack(this);
	}

	@Override
	public void onRelease() {
		super.onRelease();
		
		mSearchKeyWordModel.release();
		if(mkeywordList != null){			
			mkeywordList.clear();
			mkeywordList = null;
		}
		
		LogUtil.i(Tag, "onRelease");
	}
	
	@Override
	public void finish() {
		onRelease();
		super.finish();
		
		LogUtil.i(Tag, "finish");
	}
	
	@Override
	public boolean onPreLoad(String tag, Object... params) {
		showLoadView();
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		hideLoadView();
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if(mSearchKeyWordModel.getTag().equals(tag)){
			mkeywordList = (ArrayList<KeyWord>)result;
			
			if( mkeywordList!= null && mkeywordList.size() > 0){
				mUserAction.loadKeyWordOver(mkeywordList);
			}
		}
		hideLoadView();
		return false;
	}
	
	public interface BookCitySearchViewUserAciton extends IBaseUserAction{  
		public void loadKeyWordOver(ArrayList<KeyWord> subjectList);
	}

	@Override
	public boolean isNeedReStart() {
		return !hasLoadedData();
	}

	@Override
	public boolean isStop() {
		return !mSearchKeyWordModel.isStart();
	}

	@Override
	public void start() {
		mSearchKeyWordModel.start();
	}

	@Override
	protected boolean hasLoadedData() {
		return mkeywordList != null;
	}
	
}
