package com.lectek.android.lereader.storage.sprefrence;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.lib.storage.sprefrence.BasePreferences;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.storage.cprovider.UriUtil;
import com.lectek.android.lereader.storage.dbase.TianYiUserInfo;

/** 用户信息存储；用于存储用户信息和当前用户accessToken，用户信息用JSON存储
 * @author mingkg21
 * @email  mingkg21@gmail.com
 * @date   2013-8-27
 */
public class UserPrefrenceUtil extends BasePreferences{
	
	private static UserPrefrenceUtil instance;
	
	private static final String TAG_USER = "user_";
	private static final String TAG_CURRENT_USER_ACCESS_TOKEN = "current_user_access_token";

	protected UserPrefrenceUtil(Context context) {
        super(context);
	}


    @Override
    protected Uri getUri(Class className) {
        return UriUtil.getPrefrenceUri(className, DBConfig.PATH_PREFRENCE);
    }

    public UserPrefrenceUtil() {
        super(BaseApplication.getInstance());
    }

	public static UserPrefrenceUtil getInstance(Context context) {
		if (instance == null) {
			instance = new UserPrefrenceUtil();
		}
		return instance;
	}
	
	/** 读取用户信息
	 * @param accessToken 用户的accessToken
	 * @return
	 */
	public TianYiUserInfo getUserInfo(String accessToken){
		if(TextUtils.isEmpty(accessToken)){
			return null;
		}
		String str = getStringValue(TAG_USER + accessToken, null);
		if(TextUtils.isEmpty(str)){
			return null;
		}
		TianYiUserInfo info = new TianYiUserInfo();
		try {
			info.fromJsonObject(new JSONObject(str));
		} catch (JSONException e) {
			info = null;
		} 
		return info;
	}
	
	/** 存储用户信息
	 * @param accessToken 用户的accessToken
	 * @param info 用户信息实体
	 * @return
	 */
	public boolean saveUserInfo(String accessToken, TianYiUserInfo info){
		if(TextUtils.isEmpty(accessToken) || info == null){
			return false;
		} 
		String str = info.toJsonObject().toString();
		if(TextUtils.isEmpty(str)){
			return false;
		}
        return setStringValue(TAG_USER + accessToken, str);
	}

	public String getCurrentUserAccessToken(){
        return getStringValue(TAG_CURRENT_USER_ACCESS_TOKEN, null);
	}
	
	public boolean setCurrentUserAccessToken(String accessToken){
        return setStringValue(TAG_CURRENT_USER_ACCESS_TOKEN, accessToken);
	}

}
