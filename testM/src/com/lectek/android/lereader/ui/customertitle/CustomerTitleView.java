package com.lectek.android.lereader.ui.customertitle;

import android.content.Context;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.customertitle.CustomerTitleViewModel;
import com.lectek.android.lereader.binding.model.customertitle.CustomerTitleViewModel.OnClickCallback;
import com.lectek.android.lereader.ui.common.BasePanelView;

public class CustomerTitleView extends BasePanelView {
	private CustomerTitleViewModel mCustomerTitleViewModel;

	public CustomerTitleView(Context context) {
		super(context);
		mCustomerTitleViewModel = new CustomerTitleViewModel(getContext(), this);
		bindView(R.layout.customer_title_lay, this, mCustomerTitleViewModel);
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDestroy() {
	}
	
	public void setRightBtnText(String text) {
		mCustomerTitleViewModel.setRightBtnText(text);
	}
	
	@Override
	public void setTitleContent(String text) {
		mCustomerTitleViewModel.setTitleContent(text);
	}
	
	public void setLeftLineVisiblity(int visibility) {
		mCustomerTitleViewModel.setLeftLineVisibility(visibility);
	}
	
	public void setLeftClickEvent(OnClickCallback leftClickCallback) {
		mCustomerTitleViewModel.setOnLeftClickCallback(leftClickCallback);
	}
	
	public void setRightClickEvent(OnClickCallback rightClickCallback) {
		mCustomerTitleViewModel.setOnRightClickCallback(rightClickCallback);
	}

}
