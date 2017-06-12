package com.lectek.android.lereader.utils;

import android.content.Context;
import android.text.TextUtils;

import com.lectek.android.lereader.storage.dbase.TianYiUserInfo;
import com.lectek.android.lereader.storage.sprefrence.UserPrefrenceUtil;

/** 用户管理类
 * @author mingkg21
 * @email  mingkg21@gmail.com
 * @date   2013-8-27
 * @ deprecated
 */
public class UserManager {
	
	public static final String ACCESS_TOKEN_DEFAULT = "3f181d98-10a6-47ae-854c-a191a67dfa62";
	
	private static UserManager instance;
	private Context context;
	private TianYiUserInfo userInfo;
	private String currentAccessToken;
	private String userId;
	
	private UserManager(Context context) {
		this.context = context;
	}

	public static UserManager getInstance(Context context) {
		if (instance == null) {
			instance = new UserManager(context.getApplicationContext());
		}
		return instance;
	}
	
	/** 
	 * 天翼阅读服务器 ----判断是否游客
	 * @return boolean; true为游客，false为已登陆用户
	 * @deprecated
	 */
	public boolean isGuset() {
		if (TextUtils.isEmpty(currentAccessToken)) {
			return true;
		}
		return false;
	}
	
	/** 读取用户信息；从缓存读取或从存储读取
	 * @return UserInfo 返回用户信息，或为null
	 */
	public TianYiUserInfo getUserInfo() {
		if (userInfo == null) {
			userInfo = UserPrefrenceUtil.getInstance(context).getUserInfo(currentAccessToken);
		}
		return userInfo;
	}
	
	public void setCurrentAccessToken(String accessToken){
		currentAccessToken = accessToken;
		UserPrefrenceUtil.getInstance(context).setCurrentUserAccessToken(accessToken);
	}
	
	public String getCurrentAccessToken(){
		if(TextUtils.isEmpty(currentAccessToken)){
			currentAccessToken = UserPrefrenceUtil.getInstance(context).getCurrentUserAccessToken();
		}
		return currentAccessToken;
	}
	
	public boolean saveUserInfo(TianYiUserInfo userInfo){
		return UserPrefrenceUtil.getInstance(context).saveUserInfo(currentAccessToken, userInfo);
	}
	
	/** 获取当前用户的ID
	 * @return String 返回用户ID
	 */
	public String getCurrentUserId() {
		if (TextUtils.isEmpty(userId)) {
			TianYiUserInfo userInfo = getUserInfo();
			if (userInfo != null) {
				userId = userInfo.getUserId();
			}
		}
		return userId;
	}
	
	public void login(String accessToken, String userID){
		if (TextUtils.isEmpty(accessToken)) {
			return;
		}
		setCurrentAccessToken(accessToken);
		TianYiUserInfo info = UserPrefrenceUtil.getInstance(context).getUserInfo(accessToken);
		if (info == null) {
			info = new TianYiUserInfo();
		}
		info.setUserId(userID);
		this.userId = userID;
		UserPrefrenceUtil.getInstance(context).saveUserInfo(accessToken, info);
		userInfo = info;
	}
	
	/** 处理用户退出
	 * 
	 */
	public void logout() {
		setCurrentAccessToken(null);
	}

}
