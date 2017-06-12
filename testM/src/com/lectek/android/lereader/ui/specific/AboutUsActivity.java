package com.lectek.android.lereader.ui.specific;

import android.os.Bundle;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.user.AboutUsViewModel;
import com.lectek.android.lereader.binding.model.user.AboutUsViewModel.ShowAndHideLoadViewHandler;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 关于界面
 * @author Shizq
 * @date 2013-9-16
 */
public class AboutUsActivity extends BaseActivity {
	
	private final String PAGE_NAME = "关于界面";
	private AboutUsViewModel mAboutUsViewModel;

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mAboutUsViewModel = new AboutUsViewModel(this, this);
		mAboutUsViewModel.setShowAndHideLoadViewHandler(new ShowAndHideLoadViewHandler() {
			
			@Override
			public void showLoad() {
				showLoadView();
			}
			
			@Override
			public void hideLoad() {
				hideLoadView();
			}
		});
		return bindView(R.layout.about_us_layout, mAboutUsViewModel);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleContent(getString(R.string.user_info_about));
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(PAGE_NAME);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(PAGE_NAME);
		MobclickAgent.onPause(this);
	}

}
