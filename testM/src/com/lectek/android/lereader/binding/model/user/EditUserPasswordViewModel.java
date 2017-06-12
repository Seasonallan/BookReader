package com.lectek.android.lereader.binding.model.user;

import gueei.binding.observables.StringObservable;
import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.binding.model.BaseLoadDataViewModel;
import com.lectek.android.lereader.binding.model.user.PersonInfoNickNameViewModel.ISaveUserInfoHandler;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.ILoadView;
import com.lectek.android.lereader.utils.ToastUtil;

public class EditUserPasswordViewModel extends BaseLoadDataViewModel {
	
	public final StringObservable bNewPassword = new StringObservable();
	public final StringObservable bReNewPassword = new StringObservable();
	
	private String mUserId;
	private String mNickname;
	private String mUserName;
	private String mPassword;
	
	public final OnClickCommand bSaveClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			if(bNewPassword.get().equals(bReNewPassword.get())){
				showLoadView();
				saveUserInfo(mUserId, null, mUserName, mPassword, bNewPassword.get(), new ISaveUserInfoHandler() {
					
					@Override
					public void saveUserInfoSuccess() {
						ToastUtil.showLongToast(getContext(), R.string.save_success);
						if(mUserAction != null){
							mUserAction.modifyUserInfo();
						}
					}
					
					@Override
					public void saveUserInfoFail() {
						ToastUtil.showLongToast(getContext(), R.string.fail_save);
					}
				});
			}else{
				ToastUtil.showLongToast(getContext(), R.string.user_password_not_match);
			}
		}
	};
	private UserAction mUserAction;
	
	public EditUserPasswordViewModel(Context context, ILoadView loadView, String userId, String nickname, String userName, String password) {
		super(context, loadView);
		boolean isLogin = PreferencesUtil.getInstance(getContext()).getIsLogin();
		if(isLogin){
			
			UserInfoLeyue userInfo = AccountManager.getInstance().getUserInfo();
			mUserId = PreferencesUtil.getInstance(getContext()).getUserId();
			mPassword = PreferencesUtil.getInstance(getContext()).getUserPSW();
			mUserName = PreferencesUtil.getInstance(getContext()).getUserName();
		}
	}

	private void saveUserInfo(final String userId, final String nickName, final String userName, final String password, final String newPassword, final ISaveUserInfoHandler saveUserInfoHandler){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean result = false;
				try {
					result = ApiProcess4Leyue.getInstance(getContext()).updateUserInfo(userId, null, userName, password, newPassword, null, null, null, null, null, null);
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
