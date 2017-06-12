package com.lectek.android.lereader.binding.model.bookCityClassify;

import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnItemClickCommand;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.response.BookCityClassifyResultInfo;
import com.lectek.android.lereader.net.response.SubjectResultInfo;
import com.lectek.android.lereader.ui.IBaseUserAction;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.specific.ActivityChannels;
import com.lectek.android.lereader.ui.specific.BookCityClassifyDetailActivity;
import com.lectek.android.lereader.utils.ToastUtil;

public class BookCityClassifyViewModelLeyue extends BaseLoadNetDataViewModel implements INetAsyncTask {

	private static final String Tag = BookCityClassifyViewModelLeyue.class.getSimpleName();
	
	private BookCityClassifyDataModelLeyue mBookCityClassifyDataModelLeyue;
	private BookCityClassifyViewUserAciton mUserAction;
	private BookCityClassifyData mBookCityClassifyData;
	public final ArrayListObservable<ClassifyItem> bClassifyItems = new ArrayListObservable<ClassifyItem>(ClassifyItem.class);
	
	public BookCityClassifyViewModelLeyue(Context context, INetLoadView loadView, BookCityClassifyViewUserAciton userAction) {
		super(context, loadView);
		mUserAction = userAction;
		mBookCityClassifyDataModelLeyue = new BookCityClassifyDataModelLeyue();
		mBookCityClassifyDataModelLeyue.addCallBack(this);
		registerNetworkChange(this);
	}
	
	public OnItemClickCommand bClassifyItemClickedCommand = new OnItemClickCommand() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ActivityChannels.embedBookClassifyActivityToBookCity(getContext(), bClassifyItems.get(position).classifyId,
					bClassifyItems.get(position).bClassifyName.get());
//			BookCityClassifyDetailActivity.openActivity(getContext(), bClassifyItems.get(position).classifyId);
		}
	};
	@Override
	public boolean onPreLoad(String tag, Object... params) {
//		showLoadView();
		return false;
	}

	@Override
	public void onRelease() {
		super.onRelease();
		
		mBookCityClassifyDataModelLeyue.release();
		bClassifyItems.clear();
		
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
		if(!fillTheClassifyData(true)) {
			tryStartNetTack(this);
		}
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		showRetryView();
		return false;
	}

	private boolean fillTheClassifyData(boolean async){
		
		if(mBookCityClassifyData == null || mBookCityClassifyData.mClassifySubjectList == null || mBookCityClassifyData.mClassifySubjectList.size() <= 0
								|| mBookCityClassifyData.mClassifyInfoList == null || mBookCityClassifyData.mClassifyInfoList.size() <= 0) {
			return false;
		}
		
		if(async) {
			showLoadView();
			MyAndroidApplication.getHandler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					mUserAction.loadSubjectOver(mBookCityClassifyData.mClassifySubjectList);
					getClassifyList(mBookCityClassifyData.mClassifyInfoList);
					
					hideLoadView();
				}
			}, 200);
		}else {
			mUserAction.loadSubjectOver(mBookCityClassifyData.mClassifySubjectList);
			getClassifyList(mBookCityClassifyData.mClassifyInfoList);
		}
		return true;
	}
	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if(!isSucceed)
			return false;
		if(mBookCityClassifyDataModelLeyue.getTag().equals(tag)){
			mBookCityClassifyData = (BookCityClassifyData) result;
			if(mBookCityClassifyData!= null){
				fillTheClassifyData(false);
			}
		}

		mUserAction.loadDataEnd();
		hideLoadView();
		return false;
	}

	private void getClassifyList(ArrayList<BookCityClassifyResultInfo> classifyList){
		bClassifyItems.clear();
		for (int i = 0; i < classifyList.size(); i++) {
			BookCityClassifyResultInfo bookCityClassifyResultInfo = classifyList.get(i);
			ClassifyItem item = new ClassifyItem();
			item.classifyId = bookCityClassifyResultInfo.getId();
			item.bClassifyName.set(bookCityClassifyResultInfo.getName());
			item.bBookCount.set(bookCityClassifyResultInfo.getQuantity());
			if(i%4 == 0 || i%4 == 1){
				item.bClassifyItemBg.set(R.color.white);
			}else{
				item.bClassifyItemBg.set(R.color.common_18);
			}
			bClassifyItems.add(item);
		}
	}
	public interface BookCityClassifyViewUserAciton extends IBaseUserAction{  
		public void loadDataEnd();
		public void loadSubjectOver(ArrayList<SubjectResultInfo> subjectList);
	}
	
	public static class ClassifyItem{
		public int classifyId;
		public StringObservable bClassifyName = new StringObservable();
		public IntegerObservable bBookCount = new IntegerObservable();
		public IntegerObservable bClassifyItemBg = new IntegerObservable();
	}

	@Override
	public boolean isNeedReStart() {
		return !hasLoadedData();
	}

	@Override
	public boolean isStop() {
		return !mBookCityClassifyDataModelLeyue.isStart();
	}

	@Override
	public void start() {
		showLoadView();
//		mBookCityClassifyData = mBookCityClassifyDataModelLeyue.getCachedData();
//		if(mBookCityClassifyData != null){
//			fillTheClassifyData();
//		}else{
		mBookCityClassifyDataModelLeyue.start(true);
//		}
	}
	
	public void pullRefershStart(){
		mBookCityClassifyDataModelLeyue.start(true);
	}

	@Override
	protected boolean hasLoadedData() {
		return mBookCityClassifyData != null;
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
