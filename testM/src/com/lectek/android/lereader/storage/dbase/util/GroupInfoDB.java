package com.lectek.android.lereader.storage.dbase.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.lib.storage.dbase.JsonArrayList;
import com.lectek.android.lereader.lib.utils.DateUtil;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.storage.dbase.GroupMessage;
import com.lectek.android.lereader.storage.dbase.PushMessage;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * @description
	书架分组记录表
 * @author ljp
 * @date 2014-7-14
 * @Version 1.0
 */
public class GroupInfoDB {
	
	public static GroupInfoDB notifyCustomInfoDB = null;
 
	public static GroupInfoDB getInstance(){
		if (notifyCustomInfoDB == null) {
			notifyCustomInfoDB = new GroupInfoDB();
		}
		return notifyCustomInfoDB;
	}
	
	private ContentResolver getDatabase(){
		return BaseApplication.getInstance().getContentResolver();
	}
	 
    /**
     * 创建分组
     */
    public boolean addMessageInfo(GroupMessage groupMessage) {
        Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_GROUP, null,
                new GroupMessage(groupMessage.groupId).getPrimaryKeyWhereClause(), null, null);
        if (cursor != null && cursor.moveToFirst()) {
            getDatabase().update(DBConfig.CONTENT_URI_GROUP, groupMessage.toContentValues(), groupMessage.getPrimaryKeyWhereClause(), null);
            return false;
        }else{
            cursor = getDatabase().query(DBConfig.CONTENT_URI_GROUP, null, "groupName='"+groupMessage.groupName+"'" , null, null);
            if (cursor != null && cursor.moveToFirst()) {
                getDatabase().update(DBConfig.CONTENT_URI_GROUP, groupMessage.toContentValues(), "groupName='"+groupMessage.groupName+"'", null);
                return false;
            }else{
                getDatabase().insert(DBConfig.CONTENT_URI_GROUP, groupMessage.toContentValues());
                return true;
            }
        }
    }

	
	/**
	 * 获取分组
	 * @return
	 */
	public ArrayList<GroupMessage> getMessageInfos() {
        Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_GROUP, null, null, null, null);
        return new JsonArrayList<GroupMessage>(GroupMessage.class).fromCursor(cursor);
	}
	
	
	/**
	 * 删除分组
	 * @param msgId
	 */
	public void delMessageInfo(int msgId){
		getDatabase().delete(DBConfig.CONTENT_URI_GROUP, new GroupMessage(msgId).getPrimaryKeyWhereClause(), null);
	}
	
	/**
	 * 删除所有分组
	 */
	public void delAllNotifyMsgInfos(){
        getDatabase().delete(DBConfig.CONTENT_URI_GROUP, null, null);
	}

    /**
     * 更新分组ID
     * @param groupId
     * @param oldGroupId
     */
    public void updateSysBookMarkGroupId(int groupId, int oldGroupId){
        ContentValues contentValues = new ContentValues();
        contentValues.put("groupId", groupId);
        GroupMessage groupMessage = new GroupMessage();
        groupMessage.groupId = oldGroupId;
        getDatabase().update(DBConfig.CONTENT_URI_GROUP, contentValues, groupMessage.getPrimaryKeyWhereClause(), null);
    }

    /**
     * 更新系统书籍位置
     * @param groupId
     * @param position
     */
    public void updateSysBookMarkGroupPosition(int groupId, double position){
        ContentValues contentValues = new ContentValues();
        contentValues.put("shelfPosition", position);
        GroupMessage groupMessage = new GroupMessage();
        groupMessage.groupId = groupId;
        getDatabase().update(DBConfig.CONTENT_URI_GROUP, contentValues, groupMessage.getPrimaryKeyWhereClause(), null);
    }

	/**
	 * 更新分组名
	 */
	public void updateSysBookMarkGroupName(int groupId, String name){
        Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_GROUP, null,
                new GroupMessage(groupId).getPrimaryKeyWhereClause(), null, null);
		if (cursor != null && cursor.moveToFirst()) {
            GroupMessage pushMessage = new GroupMessage();
            pushMessage.fromCursor(cursor);
			pushMessage.groupName = name;
            getDatabase().update(DBConfig.CONTENT_URI_GROUP, pushMessage.toContentValues(), pushMessage.getPrimaryKeyWhereClause(), null);
		}
	}
}
