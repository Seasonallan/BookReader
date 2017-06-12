/*
 * ========================================================
 * ClassName:UserLoginViewModelLeYueNew.java* 
 * Description:
 * Copyright (C) 
 * ========================================================
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *===================================================================*
 * Revision History
 *
 * Modification                    Tracking
 * Date         Author          Number       Description of changes
 *____________________________________________________________________
 * 
 * 2013-9-26     chendt          #00000       create
 */
package com.lectek.android.lereader.binding.model.login_tianyiandleyue;

import gueei.binding.observables.StringObservable;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.binding.model.BaseViewModel;
import com.lectek.android.lereader.lib.thread.internal.ITerminableThread;
import com.lectek.android.lereader.permanent.ThirdPartLoginConfig;
import com.lectek.android.lereader.ui.ILoadView;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.IView;
import com.lectek.android.lereader.ui.login_leyue.UserLoginLeYueNewActivity;
import com.lectek.android.lereader.ui.login_leyue.UserRegistActivity;
import com.lectek.android.lereader.utils.KeyBoardUtil;

/**
 * 由天翼登录到乐阅登录的界面操作
 * @author wuwq
 *
 */
public class UserLoginViewModelLeYueNew extends BaseViewModel{
	private LoginUserAciton mUserAciton;
	public StringObservable bUserNameContent = new StringObservable();
	public StringObservable bPswContent = new StringObservable();
//	private UserInfoLeyue mUserInfoLeyue;
	private UserLoginLeYueNewActivity mActivity;
	
	private ITerminableThread mTerminableThread;
	
	public final OnClickCommand bLoginCommand = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			if (mUserAciton.loginCheck(bUserNameContent.get(),bPswContent.get())) {
				InputMethodManager manager = (InputMethodManager) mActivity.getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
				KeyBoardUtil.hideInputMethodManager(manager, mActivity.getWindow().peekDecorView());
				
				if(mTerminableThread != null) {
					mTerminableThread.cancel();
				}
				
				if(getCallBack() instanceof ILoadView) {
					((ILoadView)getCallBack()).showLoadView();
				}
				
				mTerminableThread = AccountManager.getInstance().lereadLogin(bUserNameContent.get(), bPswContent.get());
			}
		}
	};
	
	@Override
	public void onRelease() {
		super.onRelease();
		
		if(mTerminableThread != null) {
			mTerminableThread.cancel();
		}
	};
	
	/*
	 * 跳转到新浪微博登录界面
	 */
	public final OnClickCommand bSinaLoginCommond = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			mUserAciton.loginByThirdPart(ThirdPartLoginConfig.TYPE_SINA, mActivity);
		}
	};
	/*
	 * 跳转到QQ登录界面
	 */
	public final OnClickCommand bQQLoginCommond = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			mUserAciton.loginByThirdPart(ThirdPartLoginConfig.TYPE_QQ, mActivity);
		}
	};
	/*
	 * 跳转到乐阅自主登录界面
	 */
	public final OnClickCommand bTianYiLoginCommond = new OnClickCommand(){

		@Override
		public void onClick(View v) {
			mUserAciton.loginByThirdPart(ThirdPartLoginConfig.TYPE_TY, mActivity);
		}
	};
	
	public final OnClickCommand bRegisterCommand = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			Intent intent = null;
			intent = new Intent(mActivity, UserRegistActivity.class);
			mActivity.startActivity(intent);
			mActivity.finish();
		}
	};
	
	public UserLoginViewModelLeYueNew(UserLoginLeYueNewActivity context, INetLoadView loadView,LoginUserAciton userAciton) {
		super(context, loadView);
		mActivity = context;
		mUserAciton = userAciton;
//		mLoginModel = new UserLoginModel();
//		mLoginModel.addCallBack(UserLoginViewModelLeYueNew.this);
//		mUploadModel = new ScoreUploadModel();
//		mUploadModel.addCallBack(this);
	}

//	@Override
//	public boolean onPreLoad(String tag, Object... params) {
//		showLoadView();
//		return false;
//	}
//
//	@Override
//	public boolean onFail(Exception e, String tag, Object... params) {
//		if (e instanceof GsonResultException) {
//			GsonResultException exception = (GsonResultException) e;
//			if (exception.getmResponseInfo()!=null && !TextUtils.isEmpty(exception.getmResponseInfo().getErrorCode())) {
//				String toastStr =  ResultCodeControl.
//						getResultString(getContext(), 
//								exception.getmResponseInfo().getErrorCode()); 
//				mUserAciton.exceptionToast(toastStr);
//			}
//		}
//		hideLoadView();
//		return false;
//	}
	/**
	 * 改造后乐阅登录回调登录
	 */
//	@Override
//	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
//			boolean isCancel, Object... params) {
//		if (result!=null && !isCancel) {
//			if (mLoginModel.getTag().equals(tag)) {
//				mUserInfoLeyue =  (UserInfoLeyue) result;
//				if(mUserInfoLeyue != null){
//					mUserAciton.onloginSuccess();
//					PreferencesUtil.getInstance(getContext()).setIsLogin(true);
//					AccountManage_old.getInstence().setmUserInfo(mUserInfoLeyue); 
//					PreferencesUtil.getInstance(getContext()).setUserId(mUserInfoLeyue.getUserId());
//					PreferencesUtil.getInstance(getContext()).setUserNickName(mUserInfoLeyue.getNickName());
//					if(!TextUtils.isEmpty(mUserInfoLeyue.getEmail()))
//						PreferencesUtil.getInstance(getContext()).setBindEmail(mUserInfoLeyue.getEmail());
//					if(!TextUtils.isEmpty(mUserInfoLeyue.getMobile()))
//						PreferencesUtil.getInstance(getContext()).setBindPhoneNum(mUserInfoLeyue.getMobile());
//					if(!TextUtils.isEmpty(mUserInfoLeyue.getSource()))
//						PreferencesUtil.getInstance(getContext()).setUserSource(mUserInfoLeyue.getSource());
//					if (TextUtils.isEmpty(autoLoginAccount)) 
//						PreferencesUtil.getInstance(getContext()).setUserName(bUserNameContent.get());
//					else
//						PreferencesUtil.getInstance(getContext()).setUserName(autoLoginAccount);
//					if (TextUtils.isEmpty(autoLoginPsw)) 
//						PreferencesUtil.getInstance(getContext()).setUserPSW(bPswContent.get());
//					else
//						PreferencesUtil.getInstance(getContext()).setUserPSW(autoLoginPsw);
//					//更新积分记录
//					LogUtil.e("---登陆成功，userId = "+mUserInfoLeyue.getUserId()+";---更新积分记录表！");
//					boolean isUpdate = UserScoreRecordDB.getInstance(getContext()).updateGuestRecordToUser(mUserInfoLeyue.getUserId());
//					LogUtil.e("---更新结果 = "+isUpdate);
//					
//					mUploadModel.start(UserScoreInfo.ANDROID_LOGIN,CommonUtil.getMyUUID(getContext()));
//					
//					mUserAciton.finish();
//				}
//				hideLoadView();
//			}
//		}
//		return false;
//	}

//	@Override
//	protected boolean hasLoadedData() {
//		return mUserInfoLeyue!=null;
//	}

	public static interface LoginUserAciton extends IView{
		
		/**
		 * 登陆前 验证
		 * @param userName
		 * @param psw
		 * @return
		 */
		public boolean loginCheck(String userName,String psw);
		
//		/**
//		 * 异常提示
//		 * @param str
//		 */
//		public void exceptionToast(String str);
//		/**登陆成功提示*/
//		public void onloginSuccess();
		
		/**
		 * 启动第三方登录界面
		 * @param type
		 * @param params
		 */
		public void loginByThirdPart(int type, Object...params);
		
//		public void leyueloginSuccess();
//		public void leyueloginFail();
	}
	
//	private ILoginInterface mLoginInterface = new ILoginInterface() {
//		
//		@Override
//		public void loginSuccess() {
//			mUserAciton.leyueloginSuccess();
//		}
//		
//		@Override
//		public void loginFail() {
//			mUserAciton.leyueloginFail();
//		}
//
//		@Override
//		public void showLoading() {
//			mActivity.showLoadDialog();
//		}
//
//		@Override
//		public void hideLoading() {
//			mActivity.hideLoadDialog();
//		}
//	};
	
	/**
	 * 自动登陆
	 * @param account
	 * @param psw
	 */
//	public void autoLogin(String account,String psw){
//		autoLoginAccount = account;
//		autoLoginPsw = psw;
//		mLoginModel.start(account.trim(),psw);
//	}
//	
//	/**键盘登陆*/
//	public void loginStartMethod(){
//		mLoginModel.start(bUserNameContent.get().trim(),bPswContent.get());
//	}
	
}
