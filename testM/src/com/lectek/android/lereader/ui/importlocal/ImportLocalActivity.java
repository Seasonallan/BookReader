package com.lectek.android.lereader.ui.importlocal;

import android.os.Bundle;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.imports.ImportLocalViewModel;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.ui.customertitle.CustomerTitleView;

public class ImportLocalActivity extends BaseActivity { 
//implements ImportLocalCallback {
	
	private static final String Tag = ImportLocalActivity.class.getSimpleName();
	
	private CustomerTitleView mTitleView;
	private ImportLocalViewModel mImportLocalViewModel;

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mImportLocalViewModel = new ImportLocalViewModel(this_, this);
//		View rootView = bindView(R.layout.import_local_activity_lay, this, mImportLocalViewModel);
//		((TextView)rootView.findViewById(R.id.right_button)).set
//		return rootView;
		return bindView(R.layout.import_local_activity_lay, this, mImportLocalViewModel);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTitleView = new CustomerTitleView(this_);
		setHeadView(mTitleView);
		
		mTitleView.setTitleContent(getString(R.string.import_local_title));
		mTitleView.setLeftClickEvent(mImportLocalViewModel.getTitleLeftButtonClick());
		mImportLocalViewModel.onStart();
	}
	
	@Override
	public void finish() {
		mImportLocalViewModel.noticeDataChange();
		super.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mImportLocalViewModel.onRelease();
	}
	
	@Override
	public void onBackPressed() {
		if(mImportLocalViewModel.onBackPressed()) {
			return;
		}
		super.onBackPressed();
	}

//	@Override
//	public void setTitleLeftBtn(OnClickCallback onClickCallback) {
//		mTitleView.setLeftClickEvent(onClickCallback);
//	}
//	
//	@Override
//	public void setActivityTitleContent(String title) {
//		mTitleView.setTitleContent(title);
//	}
	
}
