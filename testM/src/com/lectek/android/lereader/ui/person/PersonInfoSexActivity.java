package com.lectek.android.lereader.ui.person;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.user.PersonInfoNickNameViewModel;
import com.lectek.android.lereader.binding.model.user.PersonInfoSexViewModel;
import com.lectek.android.lereader.binding.model.user.PersonInfoSexViewModel.UserAction;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.utils.KeyBoardUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 我的资料-修改性别界面
 * 
 * @author yangwq
 * @date 2014年4月28日
 * @email 57890940@qq.com
 */
public class PersonInfoSexActivity extends BaseActivity {
	
	private final String PAGE_NAME = "我的资料-修改性别界面";
	
	private PersonInfoSexViewModel mMyInfoViewModel;
	
	private boolean isModified;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleContent(getString(R.string.person_info_modify_sex));
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

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mMyInfoViewModel = new PersonInfoSexViewModel(this, this);
		mMyInfoViewModel.onStart();
		mMyInfoViewModel.setUserAction(new UserAction() {
			
			@Override
			public void modifyUserInfo() {
				PersonInfoSexActivity.this.setResult(PersonInfoActivity.RESULT_CODE_MODIFIED);
				PersonInfoSexActivity.this.finish();
			}
		});
		return bindView(R.layout.person_info_sex_layout, mMyInfoViewModel);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if(hasFocus){
			if(mMyInfoViewModel != null){
				mMyInfoViewModel.windowFocus();
			}
		}
		super.onWindowFocusChanged(hasFocus);
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if(requestCode == MyInfoViewModel.REQUEST_CODE){
//			if(resultCode == MyInfoViewModel.RESULT_CODE_MODIFIED){
//				isModified = true;
//			}
//		}
//		super.onActivityResult(requestCode, resultCode, data);
//	}

	@Override
	public void finish() {
		InputMethodManager manager = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
		KeyBoardUtil.hideInputMethodManager(manager, getWindow().peekDecorView());
		if(isModified){
			PersonInfoSexActivity.this.setResult(PersonInfoActivity.RESULT_CODE_MODIFIED);
		}
		super.finish();
	}
	

}
