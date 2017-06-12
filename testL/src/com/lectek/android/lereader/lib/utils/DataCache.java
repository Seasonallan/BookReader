package com.lectek.android.lereader.lib.utils;

import java.util.ArrayList;


/**
 * 数据缓存
 * 
 * @author mingkg21
 * @date 2010-4-2
 * @email mingkg21@gmail.com
 */
public class DataCache {
	
	private static DataCache instance;

	private ArrayList<String> searchWordList;

	private String guestID;
	private String userID;
	private String phoneNumber;
	private String psw;
	private String cookie;

	// 缓存CTNET的ID
	public long ctnetID = -1;
	public boolean hadChangeNetwork;

	private DataCache() {
		if (searchWordList == null) {
			searchWordList = new ArrayList<String>();
		}
	}

	public static DataCache getInstance() {
		if (instance == null) {
			instance = new DataCache();
		}
		return instance;
	}

	public void init() {
		ctnetID = -1L;
		userID = "";
		phoneNumber = "";
		psw = "";
		hadChangeNetwork = false;
	}

	public void clear() {
		cookie = "";
	}

//	public String getGuestID() {
//		if (TextUtils.isEmpty(guestID)) {
//			guestID = PreferencesUtil.getInstance(
//					MyAndroidApplication.getInstance()).getGuestId();
//		}
//		return guestID;
//	}
	
//	public void setUserID(String userId) {
//		userID = userId;
//		PreferencesUtil.getInstance(MyAndroidApplication.getInstance())
//				.setUserId(userId);
//	}

//	public String getUserID() {
//		if (TextUtils.isEmpty(userID)) {
//			userID = PreferencesUtil.getInstance(
//					MyAndroidApplication.getInstance()).getUserId();
//		}
//		return userID;
//	}

//	public String getPhoneNumber() {
//		if (TextUtils.isEmpty(phoneNumber)) {
//			phoneNumber = PreferencesUtil.getInstance(
//					MyAndroidApplication.getInstance()).getPhoneNum();
//		}
//		return phoneNumber;
//	}

//	public void setPsw(String aPsw) {
//		psw = aPsw;
//		LogUtil.v("DataCache", "Save psw: " + psw + " aPsw: " + aPsw);
//		PreferencesUtil.getInstance(MyAndroidApplication.getInstance()).setPsw(
//				psw);
//	}

//	public String getPsw() {
//		if (TextUtils.isEmpty(psw)) {
//			psw = PreferencesUtil.getInstance(
//					MyAndroidApplication.getInstance()).getPsw();
//		}
//		return psw;
//	}

	public void setCookie(String value) {
		cookie = value;
	}

	public String getCookie() {
		return cookie;
	}



//	public void clearVolumInfoList() {
//	}

}
