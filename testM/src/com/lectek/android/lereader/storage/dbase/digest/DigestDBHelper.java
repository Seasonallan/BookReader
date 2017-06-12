package com.lectek.android.lereader.storage.dbase.digest;

import com.lectek.android.lereader.storage.dbase.BaseDBHelper;



/**
 * 全局 数据库
 * @author Administrator
 *
 */
public class DigestDBHelper extends BaseDBHelper{
 
    public static final String DB_NAME = "book_digests.db"; 
    private static final int DB_VERSION = 3;
	
	public DigestDBHelper() {
		super(DB_NAME, DB_VERSION);
	} 

	private static DigestDBHelper sDbHelper;
	public static DigestDBHelper getDBHelper(){
		if (sDbHelper == null) {
			sDbHelper = new DigestDBHelper();
		}
		return sDbHelper;
	}
	
	
	/**
	 * 获取需要创建在该库中的实体
	 * @return
	 */
    public Class<?>[] getDaoLists(){
    	return new Class<?>[]{BookDigests.class};
    }
    
}
