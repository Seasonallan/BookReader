package com.lectek.android.lereader.storage.dbase.util;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentResolver;
import android.database.Cursor;

import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.lib.storage.dbase.JsonArrayList;
import com.lectek.android.lereader.lib.utils.DateUtil;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.storage.dbase.PushMessage;

/**
 * @description
	消息推送记录表
 * @author chendt
 * @date 2013-9-18
 * @Version 1.0
 */
public class NotifyCustomInfoDB {
 
	/**已读*/
	public static final int STATUS_READ = 0x1;
	/**未读*/
	public static final int STATUS_UNREAD = 0x2;
	
	public static NotifyCustomInfoDB  notifyCustomInfoDB = null;
 
	public static NotifyCustomInfoDB getInstance(){
		if (notifyCustomInfoDB == null) {
			notifyCustomInfoDB = new NotifyCustomInfoDB(); 
		}
		return notifyCustomInfoDB;
	}
	
	private ContentResolver getDatabase(){
		return BaseApplication.getInstance().getContentResolver();
	}
	 
    /**
     * 存储当前用户的消息记录
     */
    public void setMessageInfo(String msg_id, String custom) {
    	PushMessage pushMessage = new PushMessage();
    	pushMessage.msgId = msg_id;
    	pushMessage.msgJsonStr = custom;
    	pushMessage.msgCreateTime = DateUtil.getNowDayYMDHMS(Calendar.getInstance().getTime());
    	pushMessage.msgStatus = STATUS_UNREAD;
    	getDatabase().insert(DBConfig.CONTENT_URI_PUSH, pushMessage.toContentValues());
        notifyMessageReceive();
    }
    
	/**
	 * 存储当前用户的消息记录
	 * @param uMessage
	 */
//	public void setMessageInfo(UMessage uMessage) {
//    	PushMessage pushMessage = new PushMessage();
//    	pushMessage.msgId = uMessage.msg_id;
//    	pushMessage.msgJsonStr = uMessage.custom;
//    	pushMessage.msgCreateTime = DateUtil.getNowDayYMDHMS(Calendar.getInstance().getTime());
//    	pushMessage.msgStatus = STATUS_UNREAD;
//    	getDatabase().insert(DBConfig.CONTENT_URI_PUSH, pushMessage.toContentValues());
//	}
	
	/**
	 * 获取当前用户所有消息记录

	 * @return
	 */
	public ArrayList<PushMessage> getMessageInfos() {
        Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_PUSH, null, null, null, null);
        return new JsonArrayList<PushMessage>(PushMessage.class).fromCursor(cursor);
	}
	
	
	/**
	 * 删除当前用户指定的消息记录
	 * @param msgId
	 */
	public void delNotifyMsgByMsgId(String msgId){
		getDatabase().delete(DBConfig.CONTENT_URI_PUSH, new PushMessage(msgId).getPrimaryKeyWhereClause(), null);
	}
	
	/**
	 * 删除当前用户所有推送消息记录
	 */
	public void delAllNotifyMsgInfos(){
        getDatabase().delete(DBConfig.CONTENT_URI_PUSH, null, null);
	}
	
	/**
	 * 更新消息状态
	 * @param msgId 消息id
	 * @param setRead 是否设置为已读
	 */
	public void updateMsgStatus(String msgId,boolean setRead){
        Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_PUSH, null, new PushMessage(msgId).getPrimaryKeyWhereClause(), null, null);
		if (cursor != null && cursor.moveToFirst()) {
            PushMessage pushMessage = new PushMessage();
            pushMessage.fromCursor(cursor);
			pushMessage.msgStatus = setRead? STATUS_READ: STATUS_UNREAD;
            getDatabase().update(DBConfig.CONTENT_URI_PUSH, pushMessage.toContentValues(), pushMessage.getPrimaryKeyWhereClause(), null);
		}
	}


    private ChangeListener mChangeListener;

    /**
     * 设置消息改变监听器
     * @param listener
     */
    public void setOnMessageChangeListener(ChangeListener listener){
        this.mChangeListener = listener;
    }

    /**
     * 通知接收到消息
     */
    public void notifyMessageReceive(){
        if (mChangeListener != null){
            mChangeListener.onMessageReceive();
        }
    }

    /**
     * 收到消息监听器
     */
    public interface ChangeListener{
        public void onMessageReceive();
    }


}
