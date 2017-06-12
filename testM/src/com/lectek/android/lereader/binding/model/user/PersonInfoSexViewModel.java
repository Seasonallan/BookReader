package com.lectek.android.lereader.binding.model.user;

import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.StringObservable;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.binding.model.BaseLoadDataViewModel;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.utils.ToastUtil;

public class PersonInfoSexViewModel extends BaseLoadDataViewModel {
	
	public static final int RESULT_CODE_MODIFIED = 65321;
	public static final int REQUEST_CODE = 12345;
	
	public final StringObservable bAttributeTip = new StringObservable("");
	public BooleanObservable bSexBoyCheckable = new BooleanObservable(false);
	public BooleanObservable bSexGirlCheckable = new BooleanObservable(false);
	public BooleanObservable bSexSecretCheckable = new BooleanObservable(true);
	
	public static final int PERSON_INFO_SEX_BOY_VALUE = 1;
	public static final int PERSON_INFO_SEX_GIRL_VALUE = 2;
	public static final int PERSON_INFO_SEX_SECRET_VALUE = 0;
	
	private UserInfoModelLeyue mUserInfoModel;
	private UserInfoLeyue mDataSource;
	
	private UserAction mUserAction;
	private EditText nickNameET;
	
	private String mUserId;
	private String mAccount;
	private String mPassword;
	
	public final OnClickCommand bSaveClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			showLoadView();
			saveUserInfo(mUserId, getSexValue()+"", mAccount, mPassword, mPassword, new ISaveUserInfoHandler() {
				
				@Override
				public void saveUserInfoSuccess() {
					ToastUtil.showLongToast(getContext(), R.string.save_success);
					if(mUserAction != null){
						mUserAction.modifyUserInfo();
						UserInfoLeyue userInfo = AccountManager.getInstance().getUserInfo();
						userInfo.setSex(getSexValue() + "");
						AccountManager.getInstance().updateUserInfo(userInfo, null);
					}
				}
				
				@Override
				public void saveUserInfoFail() {
					ToastUtil.showLongToast(getContext(), R.string.fail_save);
				}
			});
		}
	};

	public PersonInfoSexViewModel(Context context, INetLoadView loadView) {
		super(context, loadView);
		mUserInfoModel = new UserInfoModelLeyue();
		mUserInfoModel.addCallBack(this);
		bAttributeTip.set(getString(R.string.dialog_person_email));
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
			String sexStr = mDataSource.getSex();
			Integer sex = PERSON_INFO_SEX_SECRET_VALUE;
			if(!TextUtils.isEmpty(sexStr)){
				try {
					sex = Integer.parseInt(sexStr);
				} catch (Exception e) {
				}
				setSexValue(sex);
			}
		}
	}
	
	private void saveUserInfo(final String userId, final String nickName, final String userName, final String password, final String newPassword, final ISaveUserInfoHandler saveUserInfoHandler){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean result = false;
				try {
					result = ApiProcess4Leyue.getInstance(getContext()).updateUserInfo(userId, null, userName, password, newPassword, null, null, nickName, null, null, null);
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
	
	private int getSexValue(){
		if(bSexBoyCheckable.get()){
			return PERSON_INFO_SEX_BOY_VALUE;
		}else if(bSexGirlCheckable.get()){
			return PERSON_INFO_SEX_GIRL_VALUE;
		}else{
			return PERSON_INFO_SEX_SECRET_VALUE;
		}
	}
	
	private void setSexValue(int sexValue){
		switch (sexValue) {
		case PERSON_INFO_SEX_BOY_VALUE:
			bSexBoyCheckable.set(true);
			break;
		case PERSON_INFO_SEX_GIRL_VALUE:
			bSexGirlCheckable.set(true);
			break;
		default:
			bSexSecretCheckable.set(true);
			break;
		}
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
