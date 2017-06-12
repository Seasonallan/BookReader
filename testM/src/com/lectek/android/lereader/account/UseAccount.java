package com.lectek.android.lereader.account;

import android.content.Context;

/**
 * 账号信息基础类.
 *
 * @author wuwq
 */
public class UseAccount implements AccountInfoInterface<UseAccount>,AccountActionInterface{
	
	protected IUserAccountResult mAccountLoginResult;
	
	/**
	 * Instantiates a new use account.
	 */
	public UseAccount() {
		super();
	}

	@Override
	public boolean checkAuthorization(Context context) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean login(Context context, String account, String password) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public void logout(Context context) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public boolean getAccountInfo(Context context) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public void onActivity(Context context) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public UseAccount getAccountInfo(String userId, int type) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public int setAccountInfo(UseAccount t) {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public UseAccount updateAccountInfo(UseAccount t) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public int deleteAccountInfo(UseAccount t) {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public String decode(String value) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public String encode(String value) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public void onDestory() {
		// TODO Auto-generated method stub
		
	}
	
	public IUserAccountResult getmAccountLoginResult() {
		return mAccountLoginResult;
	}

	public void setmAccountLoginResult(IUserAccountResult mAccountLoginResult) {
		this.mAccountLoginResult = mAccountLoginResult;
	}



	public interface IUserAccountResult{
		public void loginSuccess();
		public void loginFail();
	}
	
}
