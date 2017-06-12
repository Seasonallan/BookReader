package com.lectek.android.lereader.binding.model;

import android.content.Context;

import com.lectek.android.lereader.binding.model.BaseLoadDataModel.ILoadDataCallBack;
import com.lectek.android.lereader.ui.ILoadView;
/**
 * 加载非网络数据ViewModel基类
 * @author linyiwei
 *
 */
public abstract class BaseLoadDataViewModel extends BaseViewModel implements ILoadView,ILoadDataCallBack{
	public BaseLoadDataViewModel(Context context,ILoadView loadView) {
		super(context,loadView);
	}

	@Override
	public void showLoadView(){
		ILoadView loadView = (ILoadView) getCallBack();
		if(loadView != null){
			loadView.showLoadView();
		}
	};
	
	
	@Override
	public void hideLoadView(){
		ILoadView loadView = (ILoadView) getCallBack();
		if(loadView != null){
			loadView.hideLoadView();
		}
	}

	@Override
	public boolean onStartFail(String tag, String state, Object... params) {
		return false;
	};
}
