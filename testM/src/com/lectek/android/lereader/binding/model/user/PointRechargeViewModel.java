package com.lectek.android.lereader.binding.model.user;

import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.StringObservable;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.net.response.tianyi.PointRule;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.specific.ActivityChannels;

/**
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-9-9
 */
public class PointRechargeViewModel extends BaseLoadNetDataViewModel {
	
	public final ArrayListObservable<ItemViewModel> bItems = new ArrayListObservable<ItemViewModel>(ItemViewModel.class);
	
	public final StringObservable bUserPoint = new StringObservable();
	
	private PointRechargeModel mPointManageModel;
	private ArrayList<PointRule> mDataSource;
	
	public final OnClickCommand bAlipayClick = new OnClickCommand(){
		@Override
		public void onClick(View v) {
			ActivityChannels.gotoAlipayRechargeActivity(getContext());
		}
	};
	
	public final OnClickCommand bRechargeClick = new OnClickCommand(){
		@Override
		public void onClick(View v) {
			ActivityChannels.gotoMyOrderActivity(getContext());
		}
	};
	
	public PointRechargeViewModel(Context context,INetLoadView loadView) {
		super(context, loadView);
		mPointManageModel = new PointRechargeModel();
		mPointManageModel.addCallBack(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		mPointManageModel.start();
	}

	@Override
	public void onRelease() {
		super.onRelease();
		mPointManageModel.cancel();
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		showLoadView();
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed, boolean isCancel, Object... params) {
		if (result != null && !isCancel) {
			mDataSource = (ArrayList<PointRule>) result;
			
			for (PointRule pointRule : mDataSource) {
				bItems.add(newItemViewModel(pointRule));
			}
		}
		hideLoadView();
		return false;
	}
	
	private ItemViewModel newItemViewModel(PointRule pointRule) {
		ItemViewModel itemViewModel = new ItemViewModel();
		itemViewModel.pointRule = pointRule;
		itemViewModel.bReadPoint.set(pointRule.getReadPoint());
		itemViewModel.bConvertPoint.set(getContext().getString(R.string.account_points2readpoint_point, pointRule.getConverPoint()));
		return itemViewModel;
	}
	
	public class ItemViewModel {
		public PointRule pointRule;
		public final StringObservable bReadPoint = new StringObservable();
		public final StringObservable bConvertPoint = new StringObservable();
	}

	@Override
	protected boolean hasLoadedData() {
		return mDataSource != null;
	}
}
