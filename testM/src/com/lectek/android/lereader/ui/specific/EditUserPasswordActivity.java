package com.lectek.android.lereader.ui.specific;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.user.EditUserPasswordViewModel;
import com.lectek.android.lereader.binding.model.user.PersonInfoNickNameViewModel;
import com.lectek.android.lereader.binding.model.user.EditUserPasswordViewModel.UserAction;
import com.lectek.android.lereader.ui.common.BaseActivity;

public class EditUserPasswordActivity extends BaseActivity {
	
	public static final String EXTRA_USERID = "extra_userid";
	public static final String EXTRA_NICKNAME = "extra_nickname";
	public static final String EXTRA_USERNAME = "extra_nickname";
	public static final String EXTRA_USERPWD = "extra_userpwd";
	
	private EditUserPasswordViewModel mEditUserPasswordViewModel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		setTitleContent(getString(R.string.btn_text_modify_psw));
		Intent intent = getIntent();
		
		mEditUserPasswordViewModel = new EditUserPasswordViewModel(this, this, intent.getStringExtra(EXTRA_USERID)
				, intent.getStringExtra(EXTRA_NICKNAME)
				, intent.getStringExtra(EXTRA_USERNAME)
				, intent.getStringExtra(EXTRA_USERPWD));
		mEditUserPasswordViewModel.onStart();
		mEditUserPasswordViewModel.setUserAction(new UserAction() {
			
			@Override
			public void modifyUserInfo() {
				EditUserPasswordActivity.this.setResult(PersonInfoNickNameViewModel.RESULT_CODE_MODIFIED);
				EditUserPasswordActivity.this.finish();
			}
		});
		return bindView(R.layout.edit_user_psw_layout, mEditUserPasswordViewModel);
	}

}
