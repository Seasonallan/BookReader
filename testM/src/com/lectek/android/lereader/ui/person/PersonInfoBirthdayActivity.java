package com.lectek.android.lereader.ui.person;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.user.PersonInfoBirthdayViewModel;
import com.lectek.android.lereader.binding.model.user.PersonInfoNickNameViewModel;
import com.lectek.android.lereader.binding.model.user.PersonInfoBirthdayViewModel.UserAction;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.utils.KeyBoardUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 我的资料-修改生日日期界面
 * 
 * @author yangwq
 * @date 2014年4月28日
 * @email 57890940@qq.com
 */
public class PersonInfoBirthdayActivity extends BaseActivity {
	
	private final String PAGE_NAME = "我的资料-修改生日日期界面";
	
	private PersonInfoBirthdayViewModel mMyInfoViewModel;
	
	private boolean isModified;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleContent(getString(R.string.person_info_modify_age));
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
		mMyInfoViewModel = new PersonInfoBirthdayViewModel(this, this);
		View contentView = bindView(R.layout.person_info_birthday_layout, mMyInfoViewModel);
		DatePicker datePicker = (DatePicker) contentView.findViewById(R.id.datePicker);
		mMyInfoViewModel.setDatePicker(datePicker);
		mMyInfoViewModel.onStart();
		mMyInfoViewModel.setUserAction(new UserAction() {
			
			@Override
			public void modifyUserInfo() {
				PersonInfoBirthdayActivity.this.setResult(PersonInfoNickNameViewModel.RESULT_CODE_MODIFIED);
				PersonInfoBirthdayActivity.this.finish();
			}
		});
		return contentView;
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
			PersonInfoBirthdayActivity.this.setResult(PersonInfoActivity.RESULT_CODE_MODIFIED);
		}
		super.finish();
	}
	

}
