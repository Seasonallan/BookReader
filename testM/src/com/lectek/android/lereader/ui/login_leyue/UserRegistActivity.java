package com.lectek.android.lereader.ui.login_leyue;

import gueei.binding.viewAttributes.compoundButton.OnCheckedChangeViewEvent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.account.IaccountObserver;
import com.lectek.android.lereader.lib.thread.internal.ITerminableThread;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.ToastUtil;

/**
 * @description 用户注册界面
 * @author chendt
 * @edited by chends@lectek.com 2014-07-01
 * @date 2013-9-27
 * @Version 1.0
 * @SEE UserRegisterViewModel
 */
public class UserRegistActivity extends BaseActivity {

	private EditText mEmailET;
	private EditText mNickNameET;
	private EditText mPasswordET;
	private CheckBox mCheckBox;

	private ITerminableThread mTerminableThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitleContent(getString(R.string.ly_regist_title));

		AccountManager.getInstance().registerObserver(mAccountObserver);
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {

		View view = LayoutInflater.from(this_).inflate(
				R.layout.user_regist_layout, null);
		mEmailET = (EditText) view.findViewById(R.id.user_email_account_et);
		mNickNameET = (EditText) view.findViewById(R.id.user_nick_name_et);
		mPasswordET = (EditText) view.findViewById(R.id.user_password_et);
		
		view.findViewById(R.id.check_psw_lay).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new OnCheckedChangeViewEvent(
						(CompoundButton) ((LinearLayout) v)
								.getChildAt(0));
				mCheckBox.setChecked(!mCheckBox.isChecked());

				if (mPasswordET.getTransformationMethod().equals( PasswordTransformationMethod.getInstance())) {
					mPasswordET.setTransformationMethod(HideReturnsTransformationMethod
									.getInstance());
				} else {
					mPasswordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
				Editable editable = mPasswordET.getEditableText();
				Selection.setSelection(editable, editable.length());
			}
		});
		
		mCheckBox = (CheckBox) view.findViewById(R.id.check_psw_cb);

		view.findViewById(R.id.regist_btn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (registerCheck()) {
							showLoadView();
							
							if(mTerminableThread != null) {
								mTerminableThread.cancel();
							}
							mTerminableThread = AccountManager.getInstance().lereadRegister(mEmailET.getEditableText().toString(), mNickNameET.getEditableText().toString(),
												mPasswordET.getEditableText().toString());
						}
					}
				});
		
		return view;
	}

	@Override
	public void onBackPressed() {
		
		if(mTerminableThread != null && !mTerminableThread.isCancel()) {
			mTerminableThread.cancel();
			mTerminableThread = null;
			hideLoadView();
		}else {
			super.onBackPressed();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		AccountManager.getInstance().unRegisterObserver(mAccountObserver);
		
		if(mTerminableThread != null) {
			mTerminableThread.cancel();
		}
	}

	private IaccountObserver mAccountObserver = new IaccountObserver() {

		@Override
		public void onLoginComplete(final boolean success, final String msg,
				Object... params) {

			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					hideLoadView();
					ToastUtil.showShortAppToast(msg);
					
					if (success) {
						finish();
					}
				}
			});
		}

		@Override
		public void onGetUserInfo(UserInfoLeyue userInfo) {}
		
		@Override
		public void onAccountChanged() {}
	};

	private boolean registerCheck() {
		String email = mEmailET.getEditableText().toString();
		String psw = mPasswordET.getEditableText().toString();
		if (TextUtils.isEmpty(email)) {
			ToastUtil.showToast(this_, R.string.email_empty_tip);
			return false;
		}

		if (!CommonUtil.checkEmail(email)) {
			ToastUtil.showToast(this_, R.string.email_faild_tip);
			return false;
		}

		if (TextUtils.isEmpty(psw)) {
			ToastUtil.showToast(this_, R.string.psw_empty_tip);
			return false;
		}

		if (psw.length() < 6) {
			ToastUtil.showToast(this_, R.string.psw_minlength_tip);
			return false;
		}

		if (!DialogUtil.isDeviceNetword(this_)) {
			return false;
		}
		return true;
	}
}
