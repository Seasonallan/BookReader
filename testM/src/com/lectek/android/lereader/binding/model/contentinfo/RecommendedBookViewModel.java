package com.lectek.android.lereader.binding.model.contentinfo;

import java.util.ArrayList;

import android.content.Context;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.basereader_leyue.BaseReaderActivityLeyue;

public class RecommendedBookViewModel extends BaseLoadNetDataViewModel{
	
	private RecommendBookModel bookModel;
	private BaseReaderActivityLeyue mActivity;
	private String mBookId;
    private boolean mRunning =false;

	public RecommendedBookViewModel(Context context, INetLoadView loadView,String bookId) {
		super(context, loadView);
		bookModel = new RecommendBookModel();
		bookModel.addCallBack(this);
		mActivity = (BaseReaderActivityLeyue)context;
		mBookId = bookId;
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		if(bookModel.getTag().equals(tag)){
			mActivity.bRecommendedLoadVisible.set(false);
			mActivity.bRecommendedRelatedTagVisible.set(false);
		}
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if (!isCancel && result != null) {
			if(bookModel.getTag().equals(tag)){
					ArrayList<ContentInfoLeyue> list = (ArrayList<ContentInfoLeyue>)result;
					mActivity.bRecommendedLoadVisible.set(false);
					if(list != null && list.size()>0){
						mActivity.getRecommendBook(list);
						mActivity.bRecommendedRelatedTagVisible.set(true);
					}else{
						mActivity.bRecommendedRelatedTagVisible.set(false);
					}
			}
		}
		return false;
	}

	@Override
	protected boolean hasLoadedData() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onStart() {
        mRunning = true;
		bookModel.start(mBookId);
	}

    public boolean isRunning(){
        return mRunning;
    }

}
