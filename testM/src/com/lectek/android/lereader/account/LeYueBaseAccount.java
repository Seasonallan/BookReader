package com.lectek.android.lereader.account;

import android.content.Context;
import android.text.TextUtils;

import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.binding.model.BaseLoadDataModel.ILoadDataCallBack;
import com.lectek.android.lereader.binding.model.account.AutoLoginByDeviceIdModel;
import com.lectek.android.lereader.binding.model.contentinfo.ScoreUploadModel;
import com.lectek.android.lereader.binding.model.login_leyue.UserLoginModel;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.presenter.SyncPresenter;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.storage.dbase.UserScoreInfo;
import com.lectek.android.lereader.storage.dbase.util.UserInfoLeYueDB;
import com.lectek.android.lereader.storage.dbase.util.UserScoreRecordDB;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.utils.CommonUtil;


/**
 * 乐阅账号基类
 * @author wuwq
 *
 */
public class LeYueBaseAccount extends UseAccount implements ILoadDataCallBack{
	
	private UserLoginModel mLoginModel;
	
	private UserInfoLeyue mUserInfoLeyue;
	//设备id自动注册登录
	private AutoLoginByDeviceIdModel mAutoLoginByDeviceIdModel;
	//积分上传相关
	private ScoreUploadModel mUploadModel;
	
	/** 天翼账号基本属性bean. */
	private static TYBaseAccount tyBaseAccount = new TYBaseAccount();
	
	private Context this_ = MyAndroidApplication.getInstance().getBaseContext();
	
	
	public LeYueBaseAccount() {
		mLoginModel = new UserLoginModel();
		mLoginModel.addCallBack(this);
		mAutoLoginByDeviceIdModel = new AutoLoginByDeviceIdModel();
		mAutoLoginByDeviceIdModel.addCallBack(this);
		mUploadModel = new ScoreUploadModel();
		mUploadModel.addCallBack(this);
	}
	
	public void uniKeyLogin(String uniKey){
		mAutoLoginByDeviceIdModel.start(uniKey);
	}
	
	public void saveLoginInfo(){
		UserInfoLeYueDB.getInstance(this_).setUserLeYueInfo(mUserInfoLeyue);
//		AccountManage_old.getInstence().setmUserInfo(mUserInfoLeyue); 
		AccountManager.getInstance().setUserInfo(mUserInfoLeyue);
		PreferencesUtil.getInstance(this_).setIsLogin(true);
		PreferencesUtil.getInstance(this_).setUserId(mUserInfoLeyue.getUserId());
		PreferencesUtil.getInstance(this_).setUserNickName(mUserInfoLeyue.getNickName());
		if(!TextUtils.isEmpty(mUserInfoLeyue.getEmail()))
			PreferencesUtil.getInstance(this_).setBindEmail(mUserInfoLeyue.getEmail());
		if(!TextUtils.isEmpty(mUserInfoLeyue.getMobile()))
			PreferencesUtil.getInstance(this_).setBindPhoneNum(mUserInfoLeyue.getMobile());
		if(!TextUtils.isEmpty(mUserInfoLeyue.getSource()))
			PreferencesUtil.getInstance(this_).setUserSource(mUserInfoLeyue.getSource());
		PreferencesUtil.getInstance(this_).setUserName(mUserInfoLeyue.getAccount());
		PreferencesUtil.getInstance(this_).setUserPSW(mUserInfoLeyue.getPassword());
		//更新积分
		//更新积分记录
		LogUtil.e("---登陆成功，userId = "+mUserInfoLeyue.getUserId()+";---更新积分记录表！");
		boolean isUpdate = UserScoreRecordDB.getInstance(this_).updateGuestRecordToUser(mUserInfoLeyue.getUserId());
		LogUtil.e("---更新结果 = "+isUpdate);
        SyncPresenter.startSyncTask();
		mUploadModel.start(UserScoreInfo.ANDROID_LOGIN,CommonUtil.getMyUUID(this_));
		
		tyBaseAccount.saveTYAccount(mUserInfoLeyue.getUserId());
	}
	
	//更新当前用户个人信息
	public void updateUserInfo(UserInfoLeyue info){
		UserInfoLeYueDB.getInstance(this_).setUserLeYueInfo(info);
//		AccountManage_old.getInstence().setmUserInfo(info);
		AccountManager.getInstance().setUserInfo(info);
		PreferencesUtil.getInstance(this_).setUserNickName(info.getNickName());
		if(!TextUtils.isEmpty(info.getEmail()))
			PreferencesUtil.getInstance(this_).setBindEmail(info.getEmail());
		if(!TextUtils.isEmpty(info.getMobile()))
			PreferencesUtil.getInstance(this_).setBindPhoneNum(info.getMobile());
		if(!TextUtils.isEmpty(info.getPassword())){
			PreferencesUtil.getInstance(this_).setUserPSW(info.getPassword());
		}
		
	}

	public Context getThis_() {
		return this_;
	}
	
	/**
	 * 获取当天登录用户乐阅账号信息 
	 * @return
	 */
	public UserInfoLeyue getLeYueAccount(){
		UserInfoLeyue userInfo = UserInfoLeYueDB.getInstance(this_).getLastLoginUserLeYueInfo();
		return userInfo;
	}
	/**
	 * 根据用户id获取用户信息
	 * @param context
	 * @param account
	 * @param password
	 * @param userId
	 * @return
	 */
	public boolean loginByUserId(Context context, String account, String password,String userId){
		mLoginModel.start(account, password,userId);
		return false;
	}
	
	@Override
	public boolean login(Context context, String account, String password) {
		mLoginModel.start(account, password);
		return false;
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
		mAccountLoginResult.loginFail();
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if(result != null){
			if(mLoginModel.getTag().equals(tag)){
				mUserInfoLeyue = (UserInfoLeyue) result;
				//登录成功，把账号信息保存到本地数据库
				saveLoginInfo();
				mAccountLoginResult.loginSuccess();
			}else if(mAutoLoginByDeviceIdModel.getTag().equals(tag)){
				mUserInfoLeyue = (UserInfoLeyue) result;
				saveLoginInfo();
				mAccountLoginResult.loginSuccess();
			}
		}
		
		return false;
	}
	
	public UserInfoLeyue getmUserInfoLeyue() {
		return mUserInfoLeyue;
	}

	
	
}
