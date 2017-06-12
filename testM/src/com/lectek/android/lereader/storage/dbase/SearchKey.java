package com.lectek.android.lereader.storage.dbase;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.IDbHelper;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Column;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Table;

/** 
 *  搜索关键字
 * @author laijp
 * @date 2014-6-18
 * @email 451360508@qq.com
 */
@Table(name = "search_record")
public class SearchKey extends BaseDao{

	@Column(name = "search_string", isPrimaryKey = true)
	public String key;
	
	public SearchKey(){}
	
	public SearchKey(String key){
		this.key = key;
	}
	
	@Override
	public IDbHelper newDatabaseHelper() { 
		return GlobalDBHelper.getDBHelper();
	}

	 
}
