package com.lectek.android.lereader.storage.dbase;

import com.lectek.android.lereader.permanent.DBConfig;



/**
 * 全局 数据库
 * @author Administrator
 *
 */
public class GlobalDBHelper extends BaseDBHelper{

	public GlobalDBHelper() {
		super(DBConfig.DATABASE_NAME, DBConfig.DATABASE_VERSION13);
	} 

	private static GlobalDBHelper sDbHelper;
	public static GlobalDBHelper getDBHelper(){
		if (sDbHelper == null) {
			sDbHelper = new GlobalDBHelper();
		}
		return sDbHelper;
	}
	
	
	/**
	 * 获取需要创建在该库中的实体
	 * @return
	 */
    public Class<?>[] getDaoLists(){
    	return new Class<?>[]{SearchKey.class, PushMessage.class, TianYiUserInfo.class
    			,UserInfoLeyue.class, UserScoreInfo.class, GroupMessage.class};
    }
    
}
