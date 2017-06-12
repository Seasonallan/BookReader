/*
 * ========================================================
 * ClassName:UserScoreInfo.java* 
 * Description:
 * Copyright (C) 
 * ========================================================
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *===================================================================*
 * Revision History
 *
 * Modification                    Tracking
 * Date         Author          Number       Description of changes
 *____________________________________________________________________
 * 
 * 2013-12-25     chendt          #00000       create
 */
package com.lectek.android.lereader.storage.dbase;

import android.database.sqlite.SQLiteDatabase;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.IDbHelper;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Column;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Table;

/**
 * @description
	用户积分
 * @author chendt
 * @date 2013-12-25
 * @Version 1.0
 */
@Table(name = "user_score_record")
public class UserScoreInfo extends BaseDao{
	/**android客户端登陆*/
	public static final String ANDROID_LOGIN = "ANDROID_LOGIN";
	/**微信好友*/
	public static final String WX_FRIEND = "WX_FRIEND";
	/**微信好友圈*/
	public static final String WX_ZONE = "WX_ZONE";
	/**易信好友*/
	public static final String YX_FRIEND = "YX_FRIEND";
	/**易信好友圈*/
	public static final String YX_ZONE = "YX_ZONE";
	/**新浪微博*/
	public static final String SINA = "SINA";
	/**QQ好友*/
	public static final String QQ_FRIEND = "QQ_FRIEND";
	/**1:android客户端登陆*/
	public static final String RULE_LOGIN = "1";
	/**2:android客户端分享*/
	public static final String RULE_SHARE = "2";
	
	/**0:历史记录*/
	public static final int IS_HISTORY_RECORD = 0;
	/**1:当前请求记录*/
	public static final int IS_CURRENT_RECORD = 1;
	
	public UserScoreInfo(){}

	public UserScoreInfo(String userId){
		this.userId = userId;
	}
	/**
	 * @param userId
	 * @param ruleId {@link #ruleId}
	 * @param scoreWay {@link #scoreWay}
	 * @param objectId {@link #objectId}
	 * @param recordTime {@link #recordTime}
	 * @param status {@link #status}
	 * @param recordStatus {@link #recordStatus}
	 */
	public UserScoreInfo(String userId,String ruleId,String scoreWay,String objectId,String recordTime,int status,
			int recordStatus){
		this.userId = userId;
		this.ruleId = ruleId;
		this.scoreWay = scoreWay;
		this.objectId = objectId;
		this.recordTime = recordTime;
		this.status = status;
		this.recordStatus = recordStatus;
	}
	
	/**该条记录是否是历史记录
	 * {@link #IS_HISTORY_RECORD};{@link #IS_CURRENT_RECORD}*/
	@Json(name ="recordStatus")
	public int recordStatus;
	
	@Json(name ="userId")
	@Column(name = "user_id", isPrimaryKey = true)
	public String userId;
	
	/**积分规则id：登陆传1 {@link #RULE_LOGIN}，分享传2 {@link #RULE_SHARE}*/
	@Json(name ="ruleId")
	public String ruleId;
	
	/**
	 * 积分途径：
	 * {@link #ANDROID_LOGIN}，{@link #QQ_FRIEND}，{@link #SINA}，
	 * {@link #YX_ZONE}，{@link #YX_FRIEND}，{@link #WX_ZONE}，{@link #WX_FRIEND}*/
	@Json(name ="scoreWay")
	@Column(name = "type")
	public String scoreWay;
	
	/**积分渠道id（书籍分享传书籍id。如果登陆的话,传CommonUtil.getMyUUID生成的唯一识别码）*/
	@Json(name ="objectId")
	@Column(name = "source_id")
	public String objectId;
	
	/**时间
	 * 按这种格式：2013-12-12 12:12:12*/
	@Json(name ="recordTime")
	@Column(name = "date")
	public String recordTime;

	/**
	 * 用于本地数据库存储判断提交状态
	 * {@link com.lectek.android.lereader.storage.dbase.util.UserScoreRecordDB#STATUS_OK STATUS_OK}，
	 * {@link com.lectek.android.lereader.storage.dbase.util.UserScoreRecordDB#STATUS_FAIL STATUS_FAIL}
	 * */
	@Column(name = "status", type = "INTEGER")
	public int status;

	
	@Override
	public IDbHelper newDatabaseHelper() { 
		return GlobalDBHelper.getDBHelper();
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
		if (oldVersion < 9) {  
			dropTable(db);
			createTable(db);
		} 
	}
	
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getScoreWay() {
		return scoreWay;
	}

	public void setScoreWay(String scoreWay) {
		this.scoreWay = scoreWay;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}
	
	public int getIsHistoryRecord() {
		return recordStatus;
	}

	public void setIsHistoryRecord(int isHistoryRecord) {
		this.recordStatus = isHistoryRecord;
	}

	@Override
	public String toString() {
		return "UserScoreInfo [isHistoryRecord=" + recordStatus
				+ ", userId=" + userId + ", ruleId=" + ruleId + ", scoreWay="
				+ scoreWay + ", objectId=" + objectId + ", recordTime="
				+ recordTime + ", status=" + status + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof UserScoreInfo) {
			UserScoreInfo targetInfo = (UserScoreInfo) o;
			if (targetInfo.objectId.equals(objectId) && targetInfo.ruleId.equals(ruleId)
					&& targetInfo.scoreWay.equals(scoreWay) && targetInfo.userId.equals(userId)) {
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}
	
}
