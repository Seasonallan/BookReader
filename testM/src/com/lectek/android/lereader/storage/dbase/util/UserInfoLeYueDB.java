package com.lectek.android.lereader.storage.dbase.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;


/**
 * 乐阅用户数据库操作类
 * @author wuwq
 *
 */
public class UserInfoLeYueDB{ 
	
	
	public static UserInfoLeYueDB  userInfoLeYueDB = null;
	
	public UserInfoLeYueDB(Context context) {
	}

	public static UserInfoLeYueDB getInstance(Context context){
		if (userInfoLeYueDB == null) {
			userInfoLeYueDB = new UserInfoLeYueDB(context);
		}
		return userInfoLeYueDB;
	}

    private ContentResolver getDatabase(){
        return BaseApplication.getInstance().getContentResolver();
    }
	
	/**
	 * 保存用户信息
	 * @param userLeYueInfo
	 * @return
	 */
	public long setUserLeYueInfo(UserInfoLeyue userLeYueInfo){
        Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_USER_LYUE,null, userLeYueInfo.getPrimaryKeyWhereClause(), null, null);
        if (cursor != null  && cursor.getCount() > 0) {
            getDatabase().update(DBConfig.CONTENT_URI_USER_LYUE, userLeYueInfo.toContentValues(), userLeYueInfo.getPrimaryKeyWhereClause(), null);
        } else{
            getDatabase().insert(DBConfig.CONTENT_URI_USER_LYUE, userLeYueInfo.toContentValues());
        }
        return 1;
	}
	
	
	/**
	 * 获取最后一条登录记录
	 * @return
	 */
	public UserInfoLeyue getLastLoginUserLeYueInfo(){
        Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_USER_LYUE, null, null, null, null);
        if (cursor != null  && cursor.moveToFirst()) {
            UserInfoLeyue userInfoLeyue = new UserInfoLeyue();
            userInfoLeyue.fromCursor(cursor);
            return userInfoLeyue;
        }
        return null;
	}
	 
}
