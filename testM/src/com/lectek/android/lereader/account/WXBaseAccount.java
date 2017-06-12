package com.lectek.android.lereader.account;

import android.content.Context;

import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.binding.model.BaseLoadDataModel.ILoadDataCallBack;
import com.lectek.android.lereader.binding.model.account.WeiXinInfoModel;
/**
 * 微信账号处理
 * @author wuwq
 *
 */
public class WXBaseAccount extends UseAccount implements ILoadDataCallBack{

	private WeiXinInfoModel mWeiXinInfoModel;
	private Context this_ = MyAndroidApplication.getInstance().getBaseContext();
	public WXBaseAccount() {
		mWeiXinInfoModel = new WeiXinInfoModel();
		mWeiXinInfoModel.addCallBack(this);
	}
	
	public boolean registAndLogin(String wxUserId,String wxNikeName){
		mWeiXinInfoModel.start(wxUserId,wxNikeName);
		return false;
	}
	
	@Override
	public boolean login(Context context, String account, String password) {
		return super.login(context, account, password);
	}
	
	/**
	 * 微信登录直接有后台用户id
	 */
	public boolean loginByUserId(Context context, String account, String leYueUserId, LeYueBaseAccount leYueBaseAccount) {
		return leYueBaseAccount.loginByUserId(context, account, account, leYueUserId);
	}
	
	@Override
	public boolean onStartFail(String tag, String state, Object... params) {
		// TODO Auto-generated method stub
		return false;
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
		if(result != null){
			if(mWeiXinInfoModel.getTag().equals(tag)){
				String wxUserId = (String) result;
				login(this_, wxUserId, wxUserId);
			}
		}
		return false;
	}

}
