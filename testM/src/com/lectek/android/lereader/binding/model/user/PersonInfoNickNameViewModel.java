package com.lectek.android.lereader.binding.model.user;

import gueei.binding.Command;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.StringObservable;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.binding.command.OnTouchCommand;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.binding.model.BaseLoadDataViewModel;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.utils.ToastUtil;

public class PersonInfoNickNameViewModel extends BaseLoadDataViewModel {
	
	public static final int RESULT_CODE_MODIFIED = 65321;
	public static final int REQUEST_CODE = 12345;
	
	public final StringObservable bNickName = new StringObservable("");
	public final StringObservable bAttributeTip = new StringObservable("");
	public BooleanObservable bClearBtnVisible = new BooleanObservable(false);
	
	private UserInfoModelLeyue mUserInfoModel;
	private UserInfoLeyue mDataSource;
	
	private UserAction mUserAction;
	private EditText nickNameET;
	
	private String mUserId;
	private String mAccount;
	private String mPassword;
	
	/**文本变更监听*/
	public final Command TextChanging = new Command() {
		
		@Override
		public void Invoke(View view, Object... arg1) {
			if(TextUtils.isEmpty(bNickName.get())||TextUtils.isEmpty(bNickName.get().trim())){
				bClearBtnVisible.set(false);
				
			}else {
				bClearBtnVisible.set(true);
			}
		}
	};
	
	public final OnTouchCommand bOnTouchEvent = new OnTouchCommand() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(TextUtils.isEmpty(bNickName.get())||TextUtils.isEmpty(bNickName.get().trim())){
				
			}
			return false;
		}
	};
	
	/**清除文本操作*/
	public final Command bClearTextClick = new Command() {
		
		@Override
		public void Invoke(View arg0, Object... arg1) {
			bNickName.set("");
//			mUserAction.showKeyBoardAction();
		}
	};
	
	public final OnClickCommand bSaveClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			String nickName = bNickName.get().trim();
			if(TextUtils.isEmpty(nickName)){
				ToastUtil.showToast(getContext(), R.string.result_content_nickname_empty);
			}else if(nickName.length() > 16){
				ToastUtil.showToast(getContext(), R.string.dialog_person_nickname_hint);
			}else{
				showLoadView();
				saveUserInfo(mUserId, bNickName.get(), mAccount, mPassword, mPassword, new ISaveUserInfoHandler() {
					
					@Override
					public void saveUserInfoSuccess() {
						ToastUtil.showLongToast(getContext(), R.string.save_success);
						if(mUserAction != null){
							mUserAction.modifyUserInfo();
							UserInfoLeyue userInfo = AccountManager.getInstance().getUserInfo();
							userInfo.setNickName(bNickName.get());
							AccountManager.getInstance().updateUserInfo(userInfo, null);
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

	public PersonInfoNickNameViewModel(Context context, INetLoadView loadView) {
		super(context, loadView);
		mUserInfoModel = new UserInfoModelLeyue();
		mUserInfoModel.addCallBack(this);
		bAttributeTip.set(getString(R.string.dialog_person_username));
	}
	
	public void windowFocus(){
		refreshView();
	}

	@Override
	public void onStart() {
		super.onStart();
		boolean isLogin = PreferencesUtil.getInstance(getContext()).getIsLogin();
		if(isLogin){
			
			UserInfoLeyue userInfo = AccountManager.getInstance().getUserInfo();
			mUserId = PreferencesUtil.getInstance(getContext()).getUserId();
			mAccount = PreferencesUtil.getInstance(getContext()).getUserName();
			mPassword = PreferencesUtil.getInstance(getContext()).getUserPSW();
			if(userInfo == null){
				mUserInfoModel.start(mUserId, mAccount, mPassword);
				return;
			}
			mDataSource = userInfo;
			refreshView();
//			changeLoginState(true);
		}else{
//			changeLoginState(false);
		}
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		showLoadView();
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		hideLoadView();
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		// TODO Auto-generated method stub
		mDataSource = (UserInfoLeyue)result;
		refreshView();
		hideLoadView();
		return false;
	}
	
	private void refreshView(){
		if(mDataSource != null){
			if(!TextUtils.isEmpty(mDataSource.getNickName())){
				bNickName.set(mDataSource.getNickName());
				nickNameET = (EditText)((Activity)getContext()).findViewById(R.id.EditText01);
				if(nickNameET != null)
					nickNameET.setSelection(nickNameET.getText().toString().length());
			}
		}
	}
	
	private void saveUserInfo(final String userId, final String nickName, final String userName, final String password, final String newPassword, final ISaveUserInfoHandler saveUserInfoHandler){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean result = false;
				try {
					result = ApiProcess4Leyue.getInstance(getContext()).updateUserInfo(userId, nickName, userName, password, newPassword, null, null, null, null, null, null);
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
	
	public void setUserAction(UserAction userAction){
		mUserAction = userAction;
	}
	
	public interface UserAction{
		public void modifyUserInfo();
	}
	
	public static interface ISaveUserInfoHandler{
		void saveUserInfoSuccess();
		void saveUserInfoFail();
	}

}
