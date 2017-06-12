package com.lectek.android.lereader.ui.login_leyue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.account.IaccountObserver;
import com.lectek.android.lereader.binding.model.login_tianyiandleyue.UserLoginViewModelLeYueNew;
import com.lectek.android.lereader.binding.model.login_tianyiandleyue.UserLoginViewModelLeYueNew.LoginUserAciton;
import com.lectek.android.lereader.lib.thread.internal.ITerminableThread;
import com.lectek.android.lereader.permanent.ThirdPartLoginConfig;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.KeyBoardUtil;
import com.lectek.android.lereader.utils.ToastUtil;

/**
 * 用户登录界面(乐阅自主服务器登录)
 * @author wuwq
 *
 */
public class UserLoginLeYueNewActivity extends BaseActivity implements LoginUserAciton{
	
	public static final int RESULT_CODE_SUCCESS = 123456;
	
//	public static final String EXTRA_THIRD_URL = "extra_third_url";
//	public static final int EXTRA_REQUEST_CODE_AUTO_LOGIN = 1;
//	public static final int EXTRA_RESULT_CODE_AUTO_LOGIN = 20;
//	public static final int EXTRA_is = 20;
	private UserLoginViewModelLeYueNew mViewModelLeYueNew;
	private EditText mEditText;
	
	private ITerminableThread mTerminableThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleContent(getString(R.string.ly_login_title));
		mEditText = (EditText) findViewById(R.id.user_psw_et);
		mEditText.setOnEditorActionListener(mEditorActionListener);
		
		AccountManager.getInstance().registerObserver(mAccountObserver);
	}
	
	private IaccountObserver mAccountObserver = new IaccountObserver() {
		
		@Override
		public void onAccountChanged() {}
	
		@Override
		public void onGetUserInfo(UserInfoLeyue userInfo) {}
		
		public void onLoginComplete(boolean success, String msg, Object...params) {
			hideLoadDialog();
			hideLoadView();
			ToastUtil.showToast(this_, msg);
			if(success){
				setResult(RESULT_CODE_SUCCESS);;
				finish();
			}
			
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AccountManager.getInstance().unRegisterObserver(mAccountObserver);
		mViewModelLeYueNew.onRelease();
		if(mTerminableThread != null) {
			mTerminableThread.cancel();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(ThirdPartyLoginActivity.ACTIVITY_REQUEST_CODE == requestCode && data != null) {
			int type = data.getIntExtra(ThirdPartLoginConfig.EXTRA_TYPE, ThirdPartLoginConfig.TYPE_NORMAL);
			switch (resultCode) {
			case ThirdPartyLoginActivity.ACTIVITY_RESULT_CODE_CANCEL:
				hideLoadDialog();
				hideLoadView();
				break;
			case ThirdPartyLoginActivity.ACTIVITY_RESULT_CODE_FAIL:
				ToastUtil.showToast(this_, R.string.user_login_faild);
				
				hideLoadDialog();
				hideLoadView();
				break;
			case ThirdPartyLoginActivity.ACTIVITY_RESULT_CODE_SUCCESS:
				
				if(data.getExtras() != null) {
					
					Bundle resultData = data.getExtras();
					
					switch(type) {
					case ThirdPartLoginConfig.TYPE_TY:
						showLoadDialog();
						if(mTerminableThread != null && !mTerminableThread.isCancel()) {
							mTerminableThread.cancel();
						}
						mTerminableThread = AccountManager.getInstance().loginByTY(resultData.getString(ThirdPartLoginConfig.TYConfig.Extra_UserID),
								resultData.getString(ThirdPartLoginConfig.TYConfig.Extra_AccessToken), resultData.getString(ThirdPartLoginConfig.TYConfig.Extra_RefreshToken), false);
						
						break;
					case ThirdPartLoginConfig.TYPE_QQ:
						showLoadDialog();
						if(mTerminableThread != null && !mTerminableThread.isCancel()) {
							mTerminableThread.cancel();
						}
						mTerminableThread = AccountManager.getInstance().loginBySinaOrQQ(ThirdPartLoginConfig.TYPE_QQ,
														resultData.getString(ThirdPartLoginConfig.QQConfig.Extra_OpenID),
														resultData.getString(ThirdPartLoginConfig.QQConfig.Extra_NickName));
						break;
					case ThirdPartLoginConfig.TYPE_SINA:
						showLoadDialog();
						if(mTerminableThread != null && !mTerminableThread.isCancel()) {
							mTerminableThread.cancel();
						}
						mTerminableThread = AccountManager.getInstance().loginBySinaOrQQ(ThirdPartLoginConfig.TYPE_SINA,
													resultData.getString(ThirdPartLoginConfig.SinaConfig.Extra_UID),
														resultData.getString(ThirdPartLoginConfig.SinaConfig.Extra_Nick_Name));
						break;
					}
				
				}
				
				break;
			default:
				break;
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mViewModelLeYueNew = new UserLoginViewModelLeYueNew(this, this,this);
		return bindView(R.layout.user_leyue_login_layout, mViewModelLeYueNew);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//添加友盟统计
//		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		//添加友盟统计
//		MobclickAgent.onPause(this);
	}

	@Override
	public boolean loginCheck(String userName, String psw) {
		if(!DialogUtil.isDeviceNetword(this_)){
			return false;
		}
		
		if(TextUtils.isEmpty(userName)){
			ToastUtil.showToast(this_, R.string.email_empty_tip);
			return false;
		}
		
		if(!CommonUtil.checkEmail(userName.trim())){
			ToastUtil.showToast(this_, R.string.email_faild_tip);
			return false;
		}
		
		if(TextUtils.isEmpty(psw)){
			ToastUtil.showToast(this_, R.string.psw_empty_tip);
			return false;
		}
		return true;
	}

//	@Override
//	public void exceptionToast(String str) {
//		ToastUtil.showToast(this_, str);
//	}
//
//	@Override
//	public void onloginSuccess() {
//		ToastUtil.showToast(this_, R.string.login_success_tip);
//		this.sendBroadcast(new Intent(MainActivity.ACTION_CHECK_USERLOGIN_STATE_BROADCAST));
//		//请求成功后设置成功码对应到接收时的应保持一致
//		setResult(RESULT_CODE_SUCCESS);
//	}

	@Override
	public void loginByThirdPart(int type, Object... params ){
		InputMethodManager manager = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
		KeyBoardUtil.hideInputMethodManager(manager, getWindow().peekDecorView());
		
		Intent intent = new Intent(this_, ThirdPartyLoginActivity.class);
		intent.putExtra(ThirdPartLoginConfig.EXTRA_TYPE, type);
		startActivityForResult(intent, ThirdPartyLoginActivity.ACTIVITY_REQUEST_CODE);
	}
	
	private OnEditorActionListener mEditorActionListener = new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_GO) {
				if (loginCheck(mViewModelLeYueNew.bUserNameContent.get(), mViewModelLeYueNew.bPswContent.get())) {
//					mViewModelLeYueNew.loginStartMethod();
				}
			}     
			return false;
			
		}
	};
	
//	private ILoginInterface mLoginInterface = new ILoginInterface() {
//		
//		@Override
//		public void loginSuccess() {
//			hideLoading();
//			ToastUtil.showToast(this_, R.string.login_success_tip);
//			//请求成功后设置成功码对应到接收时的应保持一致
//			setResult(RESULT_CODE_SUCCESS);
//			finish();
//		}
//		
//		@Override
//		public void loginFail() {
//			hideLoading();
//			ToastUtil.showToast(this_, R.string.login_fail_text);
//		}
//
//		@Override
//		public void showLoading() {
//			UserLoginLeYueNewActivity.this.showLoadDialog();
//			
//		}
//
//		@Override
//		public void hideLoading() {
//			UserLoginLeYueNewActivity.this.hideLoadDialog();
//			
//		}
//	};

//	/**
//	 * 乐阅账号登录成功处理
//	 */
//	@Override
//	public void leyueloginSuccess() {
//		if(mLoginInterface != null){
//			mLoginInterface.hideLoading();
//		}
//		ToastUtil.showToast(this_, R.string.login_success_tip);
//		//请求成功后设置成功码对应到接收时的应保持一致
//		setResult(RESULT_CODE_SUCCESS);
//		finish();
//	}
//
//	/**
//	 * 乐阅账号登录失败提示
//	 */
//	@Override
//	public void leyueloginFail() {
//		if(mLoginInterface != null){
//			mLoginInterface.hideLoading();
//		}
//		ToastUtil.showToast(this_, R.string.login_fail_text);
//	}

}
