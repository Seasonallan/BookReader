package com.lectek.android.lereader.binding.model.common;

import android.content.Context;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.binding.model.BaseModel;
import com.lectek.android.lereader.ui.INetLoadView;

public class FrameNetDataViewModel extends BaseLoadNetDataViewModel{
	private BaseModel<NetworkChangeListener> mBaseModel;

	public FrameNetDataViewModel(Context context,INetLoadView loadView) {
		super(context,loadView);
		mBaseModel = new BaseModel<NetworkChangeListener>();
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed, boolean isCancel, Object... params) {
		return false;
	}

	@Override
	public void registerNetworkChange(NetworkChangeListener l) {
		mBaseModel.addCallBack(l);
	}

	public void dispatchNetworkChange(final boolean isAvailable) {
		mBaseModel.traversalCallBacks(new CallBackHandler<NetworkChangeListener>() {
			@Override
			public boolean handle(NetworkChangeListener callBack) {
				callBack.onChange(isAvailable);
				return false;
			}
		});
	}

	@Override
	protected boolean hasLoadedData() {
		return false;
	}
}
