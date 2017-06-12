package com.lectek.android.lereader.ui.specific;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.account.IaccountObserver;
import com.lectek.android.lereader.binding.model.user.PersonInfoNickNameViewModel;
import com.lectek.android.lereader.binding.model.user.UserInfoViewModelLeyue;
import com.lectek.android.lereader.binding.model.user.VisitorEditUserPasswordViewModel;
import com.lectek.android.lereader.binding.model.user.VisitorEditUserPasswordViewModel.UserAction;
import com.lectek.android.lereader.presenter.SyncPresenter;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.ui.login_leyue.UserLoginLeYueNewActivity;
import com.lectek.android.lereader.utils.ToastUtil;

public class VisitorEditUserPasswordActivity extends BaseActivity {
	
	public static final String EXTRA_USERID = "extra_userid";
	public static final String EXTRA_NICKNAME = "extra_nickname";
	public static final String EXTRA_USERNAME = "extra_nickname";
	public static final String EXTRA_USERPWD = "extra_userpwd";
	
	private VisitorEditUserPasswordViewModel mEditUserPasswordViewModel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		setTitleContent(getString(R.string.btn_text_modify_psw));
		Intent intent = getIntent();
		
		mEditUserPasswordViewModel = new VisitorEditUserPasswordViewModel(this, this, intent.getStringExtra(EXTRA_USERID)
				, intent.getStringExtra(EXTRA_NICKNAME)
				, intent.getStringExtra(EXTRA_USERNAME)
				, intent.getStringExtra(EXTRA_USERPWD));
		mEditUserPasswordViewModel.onStart();
		mEditUserPasswordViewModel.setUserAction(new UserAction() {
			
			@Override
			public void modifyUserInfo() {
				VisitorEditUserPasswordActivity.this.setResult(PersonInfoNickNameViewModel.RESULT_CODE_MODIFIED);
				VisitorEditUserPasswordActivity.this.finish();
			}
		});
		return bindView(R.layout.visitor_edit_user_psw_layout, mEditUserPasswordViewModel);
	}
	
	private IaccountObserver mAccountObserver = new IaccountObserver() {
		
		@Override
		public void onLoginComplete(final boolean success, final String msg, Object... params) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					ToastUtil.showToast(this_, msg);
					if(success) {
//						setResult(UserLoginLeYueNewActivity.RESULT_CODE_SUCCESS);
						VisitorEditUserPasswordActivity.this.finish();
					}else {
						SyncPresenter.setSwitchTagAction(false);
					}
				}
			});
		}
		
		@Override
		public void onGetUserInfo(UserInfoLeyue userInfo) {}
		
		@Override
		public void onAccountChanged() {}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		//切换帐号
//		if(resultCode == UserLoginLeYueNewActivity.RESULT_CODE_SUCCESS){
//			//切换帐号成功m
//			ToastUtil.showToast(this_, R.string.account_change_success);
//			setResult(UserLoginLeYueNewActivity.RESULT_CODE_SUCCESS);
//			this.finish();
//		}else{
//			//切换失败
//			ToastUtil.showToast(this_, R.string.account_change_fail);
//			SyncPresenter.setSwitchTagAction(false);
//		}
        
	}

}
