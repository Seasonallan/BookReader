package com.lectek.android.lereader.storage.dbase;

import android.database.sqlite.SQLiteDatabase;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.IDbHelper;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Column;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Table;

/** 
 *  推送消息
 * @author laijp
 * @date 2014-6-18
 * @email 451360508@qq.com
 */
@Table(name = "notify_custom_record", isOrderBy=true)
public class PushMessage extends BaseDao{
	/**消息id*/
	@Column(name = "msg_id",isPrimaryKey = true)
	public String msgId;
	
	/**消息json信息体,通过 {@link #NotifyCustomInfo} 进行Gson映射解析*/
	@Column(name = "msg_json_str")
	public String msgJsonStr;
	
	/**创建时间*/
	@Column(name = "msg_create_time",isOrderDesc=true)
	public String msgCreateTime;
	
	/**消息状态*/
	@Column(name = "msg_status", type = "INTEGER")
	public int msgStatus;
	
	public PushMessage(){}
	
	public PushMessage(String id){
		this.msgId = id;
	}
	
	@Override
	public IDbHelper newDatabaseHelper() { 
		return GlobalDBHelper.getDBHelper();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
		if (oldVersion < 10) {
			createTable(db);
		}
	}
	
	
}





