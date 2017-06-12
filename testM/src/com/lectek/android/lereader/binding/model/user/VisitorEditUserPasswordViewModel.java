package com.lectek.android.lereader.binding.model.user;

import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.SpanObservable;
import gueei.binding.observables.StringObservable;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.binding.model.BaseLoadDataViewModel;
import com.lectek.android.lereader.binding.model.user.PersonInfoNickNameViewModel.ISaveUserInfoHandler;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.presenter.SyncPresenter;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.ILoadView;
import com.lectek.android.lereader.ui.login_leyue.UserLoginLeYueNewActivity;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.ToastUtil;

public class VisitorEditUserPasswordViewModel extends BaseLoadDataViewModel {
	
	public final StringObservable bNewPassword = new StringObservable();
	public final StringObservable bAccountText = new StringObservable();
	public final IntegerObservable bNewPasswordSelection = new IntegerObservable();
	public final SpanObservable bChangeAccountSpan = new SpanObservable(new UnderlineSpan());
	public final StringObservable bChangeAccountText = new StringObservable();
	private Activity activity;
	
	
	private static final String DEFAULT_PASSWORD = "123456";
	
	private String mUserId;
	private String mUserName;
	private String mPassword;
	
	public final OnClickCommand bChangeAccountClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			SyncPresenter.setSwitchTagAction(true);
			Intent intent = new Intent(getContext(), UserLoginLeYueNewActivity.class);
//			activity.startActivityForResult(intent, UserInfoViewModelLeyue.REQUEST_CODE_ACCOUNT_CHANGE);
			activity.startActivity(intent);
		}
	};
	
	public final OnClickCommand bSaveClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			String account = bAccountText.get();
			String newPassword = bNewPassword.get();
			if(TextUtils.isEmpty(account) || !CommonUtil.checkEmail(account)){
				//账号不符合规则
				ToastUtil.showToast(getContext(), R.string.error_email_tip);
			}else if(TextUtils.isEmpty(newPassword) || newPassword.length() < 6){
				//密码不符合规则
				ToastUtil.showToast(getContext(), R.string.edit_psw_min_len_tip);
			}else if(newPassword.length() > 16){
				ToastUtil.showToast(getContext(), R.string.edit_psw_check_exceed);
			}else{
				showLoadView();
				saveUserInfo(mUserId, null, mUserName, mPassword, newPassword, account, new ISaveUserInfoHandler() {
					
					@Override
					public void saveUserInfoSuccess() {
						ToastUtil.showLongToast(getContext(), R.string.save_success);
						if(mUserAction != null){
							UserInfoLeyue userInfo = AccountManager.getInstance().getUserInfo();
							userInfo.setPassword(bNewPassword.get());
							AccountManager.getInstance().updateUserInfo(userInfo, null);
							mUserAction.modifyUserInfo();
						}
					}
					
					@Override
					public void saveUserInfoFail() {
						ToastUtil.showLongToast(getContext(), R.string.fail_save);
					}
				});
			}
			
		}
	};
	private UserAction mUserAction;
	
	public VisitorEditUserPasswordViewModel(Context context, ILoadView loadView, String userId, String nickname, String userName, String password) {
		super(context, loadView);
		activity = (Activity) context;
		bChangeAccountText.set(getString(R.string.visitor_edit_password_change_account));
		boolean isLogin = PreferencesUtil.getInstance(getContext()).getIsLogin();
		if(isLogin){
			
			UserInfoLeyue userInfo = AccountManager.getInstance().getUserInfo();
			mUserId = PreferencesUtil.getInstance(getContext()).getUserId();
			mPassword = PreferencesUtil.getInstance(getContext()).getUserPSW();
			mUserName = PreferencesUtil.getInstance(getContext()).getUserName();
//			String account = userInfo.getAccount();
//			bAccountText.set(getContext().getString(R.string.user_info_account, account));
			bNewPassword.set(DEFAULT_PASSWORD);
			
			bNewPasswordSelection.set(bNewPassword.get().length());
		}
	}

	private void saveUserInfo(final String userId, final String nickName, final String userName, final String password, final String newPassword, final String account,final ISaveUserInfoHandler saveUserInfoHandler){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean result = false;
				try {
					result = ApiProcess4Leyue.getInstance(getContext()).updateUserInfo(userId, null, userName, password, newPassword, null, null, null, null, account, null);
				} catch (GsonResultException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				final boolean r = result;
				if(saveUserInfoHandler != null){
					((Activity)getContext()).runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							hideLoadView();
							if(r){
								saveUserInfoHandler.saveUserInfoSuccess();
							}else{
								saveUserInfoHandler.saveUserInfoFail();
							}
						}
					});
				}
			}
		}).start();
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setUserAction(UserAction userAction){
		mUserAction = userAction;
	}
	
	public interface UserAction{
		public void modifyUserInfo();
	}

}
