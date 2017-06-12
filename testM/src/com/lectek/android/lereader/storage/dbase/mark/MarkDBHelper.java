package com.lectek.android.lereader.storage.dbase.mark;

import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.storage.dbase.BaseDBHelper;



/**
 * 全局 数据库
 * @author Administrator
 *
 */
public class MarkDBHelper extends BaseDBHelper{
 
	public MarkDBHelper() {
		super(DBConfig.DB_NAME, DBConfig.DB_VERSION);
	} 

	private static MarkDBHelper sDbHelper;
	public static MarkDBHelper getDBHelper(){
		if (sDbHelper == null) {
			sDbHelper = new MarkDBHelper();
		}
		return sDbHelper;
	}
	
	
	/**
	 * 获取需要创建在该库中的实体
	 * @return
	 */
    public Class<?>[] getDaoLists(){
    	return new Class<?>[]{BookMark.class};
    }
    
}
