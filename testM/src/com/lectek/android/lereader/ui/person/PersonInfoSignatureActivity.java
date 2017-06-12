package com.lectek.android.lereader.ui.person;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.user.PersonInfoSignatureViewModel;
import com.lectek.android.lereader.binding.model.user.PersonInfoSignatureViewModel.UserAction;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.utils.KeyBoardUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 我的资料--个性签名界面
 * @author think
 *
 */
public class PersonInfoSignatureActivity extends BaseActivity {
	
	private final String PAGE_NAME = "我的资料--个性签名界面";
	
	private PersonInfoSignatureViewModel mMyInfoViewModel;
	
	private boolean isModified;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleContent(getString(R.string.person_info_modify_signature));
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
		mMyInfoViewModel = new PersonInfoSignatureViewModel(this, this);
		mMyInfoViewModel.onStart();
		mMyInfoViewModel.setUserAction(new UserAction() {
			
			@Override
			public void modifyUserInfo() {
				PersonInfoSignatureActivity.this.setResult(PersonInfoSignatureViewModel.RESULT_CODE_MODIFIED);
				PersonInfoSignatureActivity.this.finish();
			}
		});
		return bindView(R.layout.my_info_layout, mMyInfoViewModel);
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
			PersonInfoSignatureActivity.this.setResult(PersonInfoActivity.RESULT_CODE_MODIFIED);
		}
		super.finish();
	}
	

}
