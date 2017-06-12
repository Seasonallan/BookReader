/*
 * ========================================================


 * ClassName:ThirdPartyLoginViewModelLeyue.java* 
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
package com.lectek.android.lereader.binding.model.login_leyue;

import android.content.Context;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.net.exception.ResultCodeControl;
import com.lectek.android.lereader.net.response.RegisterInfo;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.IView;

/**
 * @description
	第三方登陆 ViewModel
 * @author chendt
 * @date 2013-9-26
 * @Version 1.0
 * 
 * @SEE ThirdPartyLoginActivity  
 * @SEE ThirdPartyRegistModel
 * @SEE ThirdPartyUserIsExitModel
 */
public class ThirdPartyLoginViewModelLeyue extends BaseLoadNetDataViewModel {
	private LoginUserAciton mUserAciton;
	private ThirdPartyRegistModel mRegistModel;
	
	private RegisterInfo mRegisterInfo;
//	private IsUserExitInfo mExitInfo;
	private String thirdPartyAccount,thirdPartyNick;
	private int thirdPartyType;
	public ThirdPartyLoginViewModelLeyue(Context context, INetLoadView loadView,LoginUserAciton userAciton) {
		super(context, loadView);
		mUserAciton = userAciton;
		mRegistModel = new ThirdPartyRegistModel();
		mRegistModel.addCallBack(ThirdPartyLoginViewModelLeyue.this);
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		showLoadView();
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		if (e instanceof GsonResultException) {
			GsonResultException exception = (GsonResultException) e;
			String toastStr =  ResultCodeControl.
									getResultString(getContext(), 
													 exception.getResponseInfo().getErrorCode()); 
			mUserAciton.exceptionToast(toastStr);
		}
		hideLoadView();
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if (result!=null && !isCancel) {
			if (mRegistModel.getTag().equals(tag)) {
				mRegisterInfo =  (RegisterInfo) result;
				if(mRegisterInfo != null){
					PreferencesUtil.getInstance(getContext()).setUserId(mRegisterInfo.getUserId());
					PreferencesUtil.getInstance(getContext()).setUserName(thirdPartyAccount);
					PreferencesUtil.getInstance(getContext()).setUserPSW(thirdPartyAccount);
					PreferencesUtil.getInstance(getContext()).setUserNickName(thirdPartyNick);
					mUserAciton.registerSuccess();
				}
				hideLoadView();
			}
//			if (mUserIsExitModel.getTag().equals(tag)) {
//				mExitInfo =  (IsUserExitInfo) result;
//				if (mExitInfo.getIsUserExit()) {
//					PreferencesUtil.getInstance(getContext()).setUserName(thirdPartyAccount);
//					PreferencesUtil.getInstance(getContext()).setUserPSW(thirdPartyAccount);//由于用户可以修改密码，故第三方账号无需登录，只需获取用户id即可
//					PreferencesUtil.getInstance(getContext()).setUserId(mExitInfo.get)//TODO:服务器接口。
//					PreferencesUtil.getInstance(getContext()).setIsLogin(true);
////					mUserAciton.exitAccountAutoLogin();
//				}else {
//					mRegistModel.start(thirdPartyType,thirdPartyAccount,thirdPartyNick);
//				}
//				hideLoadView();
//			}
		}
		return false;
	}

	@Override
	protected boolean hasLoadedData() {
		return mRegisterInfo!=null;
	}

	public static interface LoginUserAciton extends IView{
		
		/**
		 * 异常提示
		 * @param str
		 */
		public void exceptionToast(String str);
		
		/**注册成功提示*/
		public void registerSuccess();
		
		/**已存在用户，直接登陆*/
		public void exitAccountAutoLogin();
	}

	/**
	 * 启动第三方注册
	 * @param type
	 * @param account
	 * @param nickname
	 */
	public void onStart(int type,String account,String nickname){
		thirdPartyType = type;
		thirdPartyAccount = account;
		thirdPartyNick = nickname;
//		mUserIsExitModel.start(account);//第三方 不管注册与否，每次都走注册流程。
		mRegistModel.start(thirdPartyType,thirdPartyAccount,thirdPartyNick);
	}
}
