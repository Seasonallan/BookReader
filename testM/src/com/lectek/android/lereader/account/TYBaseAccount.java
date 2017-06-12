package com.lectek.android.lereader.account;

import android.content.Context;
import android.text.TextUtils;

import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.binding.model.BaseLoadDataModel.ILoadDataCallBack;
import com.lectek.android.lereader.binding.model.account.TianYiInfoModel;
import com.lectek.android.lereader.storage.dbase.TianYiUserInfo;
import com.lectek.android.lereader.storage.dbase.util.TianYiUserInfoDB;
import com.lectek.android.lereader.utils.UserManager;

/**
 * 天翼账号基类bean
 * 
 * @author wuwq
 *
 */
public class TYBaseAccount extends UseAccount implements ILoadDataCallBack {
	
	// 后台天翼账号信息
	private TianYiInfoModel mTianYiInfoModel;

	private Context context = MyAndroidApplication.getInstance().getBaseContext();

	public TYBaseAccount(){
		mTianYiInfoModel = new TianYiInfoModel();
		mTianYiInfoModel.addCallBack(this);
	}
	
	public void saveTYAccount(String userId){
		mTianYiInfoModel.start(userId);
	}
	
	public boolean loginSuccess(String url,int type){

		if (TextUtils.isEmpty(url)) {
			return false;
		}
		final String accessToken = getUrlValue(url, "access_token=");
		if (accessToken == null) {
			return false;
		}else{
			UserManager.getInstance(context).setCurrentAccessToken(accessToken);
		}
		final String userId = getUrlValue(url, "user_id=");
		
		final String refreshToken = getUrlValue(url, "refresh_token=");
		
		if(type == LoginType.PAY_LOGIN.getCode()){
			AccountManager.getInstance().loginByTY(userId, accessToken, refreshToken, true);
		}else{
			AccountManager.getInstance().loginByTY(userId, accessToken, refreshToken, false);
		}
		return true;
	
	}
	
	/**
	 * 获取url信息
	 * @param url
	 * @param tag
	 * @return
	 */
	private String getUrlValue(String url, String tag){
		String accessTokenTag = tag;
		int atIndex = url.indexOf(accessTokenTag);
		if (atIndex < 0) {
			return null;
		}
		int atStartIndex = atIndex + accessTokenTag.length();
		int atEndIndex = url.indexOf("&", atStartIndex);
		if (atEndIndex < 0) {
			atEndIndex = url.length();
		}
		return url.substring(atStartIndex, atEndIndex);
	}
	
	/**
	 * 获取当前登录用户天翼账户信息
	 * @return
	 */
	public TianYiUserInfo getTianYiAccount(String userId){ 
		return TianYiUserInfoDB.getInstance(context).getTianYiUserInfo(userId); 
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
			if(mTianYiInfoModel.getTag().equals(tag)){
				TianYiUserInfo info = (TianYiUserInfo) result;
				TianYiUserInfoDB.getInstance(context).setTianYiUserInfo(info);
			}
		}
		return false;
	}

}
