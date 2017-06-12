package com.lectek.android.lereader.storage.dbase.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.lib.storage.dbase.BaseDatabase;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.storage.dbase.TianYiUserInfo;

public class TianYiUserInfoDB {
 
	public static TianYiUserInfoDB tianYiUserInfoDB;
	 
	
	public TianYiUserInfoDB(Context context) {
		 
	}
	
	public static TianYiUserInfoDB getInstance(Context context){
		if (tianYiUserInfoDB == null) {
			tianYiUserInfoDB = new TianYiUserInfoDB(context); 
		}
		return tianYiUserInfoDB;
	}

	private ContentResolver getDatabase(){
		return BaseApplication.getInstance().getContentResolver();
	}
	
	/**
	 * 保存用户信息
	 * @param userInfo
	 * @return
	 */
	public int setTianYiUserInfo(TianYiUserInfo userInfo){
		if(userInfo == null){
			return 0;
		}
        Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_USER_TY,null, userInfo.getPrimaryKeyWhereClause(), null, null);
        if (cursor != null  && cursor.getCount() > 0) {
            getDatabase().update(DBConfig.CONTENT_URI_USER_TY, userInfo.toContentValues(), userInfo.getPrimaryKeyWhereClause(), null);
        } else{
            getDatabase().insert(DBConfig.CONTENT_URI_USER_TY, userInfo.toContentValues());
        }
        return 1;
	}
	/**
	 * 根据用户天翼userid获取本地用户信息
	 * @param userId
	 * @return
	 */
	public TianYiUserInfo getTianYiUserInfo(String userId){
		Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_USER_TY,null, BaseDatabase.getWhere("feature_leyue_userid", userId), null, null);
		if (cursor != null  && cursor.moveToFirst()) {
			TianYiUserInfo tianYiUserInfo = new TianYiUserInfo();
			tianYiUserInfo.fromCursor(cursor);
			return tianYiUserInfo;
		}
		return null;
	}
	     
}
