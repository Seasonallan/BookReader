package com.lectek.android.lereader.storage.dbase;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lectek.android.LYReader.R;
import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.IDbHelper;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Column;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Table;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.storage.dbase.util.GroupInfoDB;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;

import java.security.acl.Group;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 *  书架分组信息
 * @author laijp
 * @date 2014-6-18
 * @email 451360508@qq.com
 */
@Table(name = "mark_group")
public class GroupMessage extends BaseDao{

	@Column(name = "groupId",type = "Integer", isPrimaryKey = true)
	public int groupId;

    @Column(name = "groupName")
    public String groupName;

    @Column(name = "number")
    public int unNameNumber;

    @Column(name = "isDefault",type = "Integer")
    public int defaultType;

    @Column(name = "createTime")
    public String createTime;

    @Column(name = "shelfPosition",type = "double DEFAULT -1")
    public double shelfPosition;

    public GroupMessage(){

    }

    public GroupMessage(int msgId) {
        this.groupId = msgId;
    }

    @Override
	public IDbHelper newDatabaseHelper() { 
		return GlobalDBHelper.getDBHelper();
	}


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 14) {
            dropTable(db);
            createTable(db);
            int groupId = PreferencesUtil.getInstance(BaseApplication.getInstance()).getBookMarkGroupId();
            if (groupId > 0){
                GroupMessage groupMessage = new GroupMessage();
                groupMessage.groupId = groupId;
                groupMessage.unNameNumber = 1;
                groupMessage.defaultType = 0;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                groupMessage.createTime = sdf.format(date);
  //               GroupInfoDB.getInstance().addMessageInfo(groupMessage);

                Cursor cursor = db.query("mark_group", null, "groupId=?" , new String[]{groupMessage.groupId+""}, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                }else{
                    db.insert("mark_group", null, groupMessage.toContentValues());
                }
            }
        }
    }


}
