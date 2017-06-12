package com.lectek.android.lereader.storage.dbase.util;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.lib.utils.DateUtil;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.storage.dbase.UserScoreInfo;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;

/**
 * @description
	用户 获取积分记录
 * @author chendt
 * @date 2013-9-18
 * @Version 1.0
 */
public class UserScoreRecordDB { 

	/** 登陆积分上限次数 */
	public static final int LOGIN_LIMIT = 1;
	/** 分享积分上限次数 */
	public static final int SHARE_LIMIT = 7;
	public static final String TABLE_NAME = "user_score_record";

	public static final String USER_ID = "user_id";

	/**
	 * 书籍id，如果是login的话，传:{@link #SOURCE_ID_BY_LOGIN}
	 * */
	public static final String SOURCE_ID = "source_id";

	/**
	 * {@link #TYPE_LOGIN}，{@link #TYPE_WX_FRIEND}，{@link #TYPE_WX_ZONE}，
	 * {@link #TYPE_YX_FRIEND}，{@link #TYPE_YX_ZONE}，{@link #TYPE_SINA}，
	 * {@link #TYPE_QQ_FRIEND}
	 */
	public static final String TYPE = "type";

	public static final String DATE = "date";

	/** 记录状态：{@link #STATUS_OK}，{@link #STATUS_FAIL} */
	public static final String STATUS = "status";

	/** 来源id：0——登陆 */
	public static final String SOURCE_ID_BY_LOGIN = "0";

	/** 状态：0失败 */
	public static final int STATUS_FAIL = 0;
	/** 状态：1成功 */
	public static final int STATUS_OK = 1;

	/** 类型：android登陆 */
	public static final String TYPE_LOGIN = UserScoreInfo.ANDROID_LOGIN;
	/** 类型：微信好友 */
	public static final String TYPE_WX_FRIEND = UserScoreInfo.WX_FRIEND;
	/** 类型：微信好友圈 */
	public static final String TYPE_WX_ZONE = UserScoreInfo.WX_ZONE;
	/** 类型：易信好友 */
	public static final String TYPE_YX_FRIEND = UserScoreInfo.YX_FRIEND;
	/** 类型：易信好友圈 */
	public static final String TYPE_YX_ZONE = UserScoreInfo.YX_ZONE;
	/** 类型：新浪微博 */
	public static final String TYPE_SINA = UserScoreInfo.SINA;
	/** 类型：QQ好友 */
	public static final String TYPE_QQ_FRIEND = UserScoreInfo.QQ_FRIEND;

	public static UserScoreRecordDB userScoreRecordDB = null;

	private UserScoreRecordDB(Context context) {
	}

	public static UserScoreRecordDB getInstance(Context context) {
		if (userScoreRecordDB == null) {
			userScoreRecordDB = new UserScoreRecordDB(context);
		}
		return userScoreRecordDB;
	}

	protected String getUserId() {
		return PreferencesUtil.getInstance(BaseApplication.getInstance())
				.getUserId();
	}

    private ContentResolver getDatabase(){
        return BaseApplication.getInstance().getContentResolver();
    }

    /**
	 * 存储当前用户的相应类型、状态的积分记录。
	 * 
	 * @param type
	 *            积分类型 {@link #TYPE_LOGIN}，{@link #TYPE_WX_FRIEND}，
	 *            {@link #TYPE_WX_ZONE}， {@link #TYPE_YX_FRIEND}，
	 *            {@link #TYPE_YX_ZONE}，{@link #TYPE_SINA}，
	 *            {@link #TYPE_QQ_FRIEND}
	 * @param sourceId
	 *            分享资源id。登陆积分来源请传 {@link #SOURCE_ID_BY_LOGIN}
	 * @param status
	 *            提交服务器状态 {@link #STATUS_OK}，{@link #STATUS_FAIL}
	 */
	public void setUserScoreRecordByType(String type, String sourceId,
			int status) {
		UserScoreInfo info = new UserScoreInfo(getUserId(), null, type,
				sourceId, DateUtil.getCurrentTimeStyle1(), status, 0);
		getDatabase().insert(DBConfig.CONTENT_URI_SCORE, info.toContentValues());
	}

	/**
	 * 获取获取提交失败的记录。（会删除当天以前的记录）
	 * */
	public ArrayList<UserScoreInfo> getUserScoreRecordByStatusFail() {
		getDatabase().delete(DBConfig.CONTENT_URI_SCORE, DATE + " <  datetime('now','start of day') ", null);
		/** 删除历史记录 */
		getDatabase().delete(DBConfig.CONTENT_URI_SCORE, getDelMutilRecordClause(), null);

		Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_SCORE, null,
                USER_ID + " = '" + getUserId() + "' " + "AND " + STATUS + " = "
						+ STATUS_FAIL, null, null);
		ArrayList<UserScoreInfo> infos = null;
		infos = new ArrayList<UserScoreInfo>();
		if (cursor != null && cursor.moveToFirst()) {
			do {
				UserScoreInfo userScoreInfo = new UserScoreInfo();
				userScoreInfo.fromCursor(cursor);
				String ruleId = null;
				String type = userScoreInfo.getScoreWay();
				if (TYPE_LOGIN.equals(type)) {
					ruleId = UserScoreInfo.RULE_LOGIN;
				} else {
					ruleId = UserScoreInfo.RULE_SHARE;
				}
				userScoreInfo.ruleId = ruleId;
				userScoreInfo.recordTime = DateUtil.getCurrentTimeStyle1();
				userScoreInfo.recordStatus = UserScoreInfo.IS_HISTORY_RECORD;
				userScoreInfo.status = STATUS_FAIL;
				infos.add(userScoreInfo);
			} while (cursor.moveToNext());
		}
		return infos;
	}

	/**
	 * 获取当前用户相应类型的记录。（会删除当天以前的记录）
	 * 
	 * @param type
	 *            积分类型 {@link #TYPE_LOGIN}，{@link #TYPE_WX_FRIEND}，
	 *            {@link #TYPE_WX_ZONE}， {@link #TYPE_YX_FRIEND}，
	 *            {@link #TYPE_YX_ZONE}，{@link #TYPE_SINA}，
	 *            {@link #TYPE_QQ_FRIEND}
	 * @param sourceId
	 *            分享资源id。登陆积分来源请传 {@link #SOURCE_ID_BY_LOGIN}
	 * */
	public ArrayList<UserScoreInfo> getUserScoreRecordByType(String type,
			String sourceId) {
		getDatabase().delete(DBConfig.CONTENT_URI_SCORE, DATE + " <  datetime('now','start of day') ", null);
		/** 删除历史记录 */
		getDatabase().delete(DBConfig.CONTENT_URI_SCORE, getDelMutilRecordClause(), null);

		Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_SCORE, null,
                USER_ID + " = '" + getUserId() + "' AND " + SOURCE_ID + " = '"
						+ sourceId + "' AND " + TYPE + " = '" + type +"'", null, null);
		ArrayList<UserScoreInfo> infos = null;
		infos = new ArrayList<UserScoreInfo>();
		if (cursor != null && cursor.moveToFirst()) {
			do {
				UserScoreInfo userScoreInfo = new UserScoreInfo();
				userScoreInfo.fromCursor(cursor);
				String ruleId = null;
				String scoreWay = null;
				String _type = userScoreInfo.scoreWay;
				if (TYPE_LOGIN.equals(_type)) {
					ruleId = UserScoreInfo.RULE_LOGIN;
					scoreWay = UserScoreInfo.ANDROID_LOGIN;
				} else if (TYPE_QQ_FRIEND.equals(_type)) {
					ruleId = UserScoreInfo.RULE_SHARE;
					scoreWay = UserScoreInfo.QQ_FRIEND;
				} else if (TYPE_SINA.equals(_type)) {
					ruleId = UserScoreInfo.RULE_SHARE;
					scoreWay = UserScoreInfo.SINA;
				} else if (TYPE_WX_FRIEND.equals(_type)) {
					ruleId = UserScoreInfo.RULE_SHARE;
					scoreWay = UserScoreInfo.WX_FRIEND;
				} else if (TYPE_WX_ZONE.equals(_type)) {
					ruleId = UserScoreInfo.RULE_SHARE;
					scoreWay = UserScoreInfo.WX_ZONE;
				} else if (TYPE_YX_FRIEND.equals(_type)) {
					ruleId = UserScoreInfo.RULE_SHARE;
					scoreWay = UserScoreInfo.YX_FRIEND;
				} else if (TYPE_YX_ZONE.equals(_type)) {
					ruleId = UserScoreInfo.RULE_SHARE;
					scoreWay = UserScoreInfo.YX_ZONE;
				}
				UserScoreInfo info = new UserScoreInfo(userScoreInfo.userId,
						ruleId, scoreWay, userScoreInfo.objectId,
						DateUtil.getCurrentTimeStyle1(), userScoreInfo.status,
						UserScoreInfo.IS_HISTORY_RECORD);
				infos.add(info);
			} while (cursor.moveToNext());
		}
		return infos;
	}

	/**
	 * 获取用户指定规则的本地记录数
	 * 
	 * @param ruleId
	 *            {@link UserScoreInfo #RULE_LOGIN}，
	 *            {@link UserScoreInfo #RULE_SHARE}
	 * @return
	 */
	public int getUserScoreRecordByType(String ruleId) {
		Cursor cursor = null;
		int count = 0;
		String conditonString = "";
		StringBuilder sb = new StringBuilder();
		ArrayList<String> rules = new ArrayList<String>();
		if (ruleId.equals(UserScoreInfo.RULE_LOGIN)) {
			rules.add(UserScoreInfo.ANDROID_LOGIN);
		} else if (ruleId.equals(UserScoreInfo.RULE_SHARE)) {
			rules.add(UserScoreInfo.QQ_FRIEND);
			rules.add(UserScoreInfo.SINA);
			rules.add(UserScoreInfo.WX_FRIEND);
			rules.add(UserScoreInfo.WX_ZONE);
			rules.add(UserScoreInfo.YX_FRIEND);
			rules.add(UserScoreInfo.YX_ZONE);
		}
		for (int i = 0; i < rules.size(); i++) {
			sb.append(TYPE);
			sb.append(" = ");
			// TODO:
			sb.append("'" + rules.get(i) + "'");
			if (i != rules.size() - 1) {
				sb.append(" OR ");
			}
		}
		conditonString = sb.toString();
		getDatabase().delete(DBConfig.CONTENT_URI_SCORE, DATE + " <  datetime('now','start of day') ", null);
		/** 删除历史记录 */
		getDatabase().delete(DBConfig.CONTENT_URI_SCORE, getDelMutilRecordClause(), null);

		cursor = getDatabase().query(DBConfig.CONTENT_URI_SCORE, null,
                USER_ID + " = '" + getUserId() + "' AND " + conditonString, null , null);
		if (cursor != null && cursor.moveToFirst()) {
			count = cursor.getCount();
		}

		return count;
	}

	/**
	 * 删除当天以前的所有记录。
	 */
	public void delNotCurrentRecords() {
		getDatabase().delete(DBConfig.CONTENT_URI_SCORE, DATE + " <  datetime('now','start of day') ", null);
	}

	/**
	 * 更新游客记录为指定用户。 TODO:如果用户已存在相应记录，是否能存储成功？还是有两条一样的记录。
	 * 异常：android.database.sqlite.SQLiteDatabaseLockedException: database is
	 * locked (code 5): , while compiling: PRAGMA journal_mode 先同步处理
	 */
	public synchronized boolean updateGuestRecordToUser(String userId) {
        Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_SCORE, null,
                new UserScoreInfo(LeyueConst.TOURIST_USER_ID).getPrimaryKeyWhereClause(), null, null);
        if(cursor != null && cursor.getCount() > 0){
            ContentValues values = new ContentValues();
            values.put(USER_ID, userId);
            return getDatabase().update(DBConfig.CONTENT_URI_SCORE, values,
                    USER_ID + "='" + LeyueConst.TOURIST_USER_ID+"'", null) > 0;
        }
        return false;
	}

	/**
	 * 更新当前用户指定记录的状态
	 * 
	 * @param type
	 * @param sourceId
	 * @param status
	 */
	public void updateRecordStatus(String type, String sourceId, int status) {
		ContentValues values = new ContentValues();
		values.put(STATUS, status);
		getDatabase().update(DBConfig.CONTENT_URI_SCORE,
                values,
				USER_ID + " = '" + getUserId() + "' AND " + SOURCE_ID + " = '"
						+ sourceId + "' AND " + TYPE + " = " + type +"'", null);
	}

	/**
	 * 批量更新当前用户指定记录的状态
	 * 
	 * @param infos
	 */
	public void updateRecordStatus(ArrayList<UserScoreInfo> infos) {
		for (UserScoreInfo userScoreInfo : infos) {
			ContentValues values = new ContentValues();
			values.put(STATUS, userScoreInfo.status);
			getDatabase().update(DBConfig.CONTENT_URI_SCORE,
                    values,
					USER_ID + " = '" + getUserId() + "' AND " + SOURCE_ID + " = '"
							+ userScoreInfo.getObjectId() + "' AND " + TYPE
							+ " = " + userScoreInfo.getScoreWay()+"'", null);
		}
	}

	/**
	 * 批量更新当前用户指定记录的状态为成功
	 * 
	 * @param infos
	 */
	public void updateRecordStatusOk(ArrayList<UserScoreInfo> infos) {
		for (UserScoreInfo userScoreInfo : infos) {
			ContentValues values = new ContentValues();
			values.put(STATUS, STATUS_OK);
			getDatabase().update(DBConfig.CONTENT_URI_SCORE,
                    values,
					USER_ID + " = '" + getUserId() + "' AND " + SOURCE_ID + " = '"
							+ userScoreInfo.getObjectId() + "' AND " + TYPE
							+ " = '" + userScoreInfo.getScoreWay()+ "'", null);
		}
	}

	/**
	 * 更新当前用户指定规则id的记录的状态为成功
	 * 
	 * @param ruleIds
	 *            规则ids {@link UserScoreInfo #RULE_LOGIN}，
	 *            {@link UserScoreInfo #RULE_SHARE}
	 */
	public void updateRecordStatusOkByRuleId(String[] ruleIds) {
		ContentValues values = null;
		if (ruleIds == null || ruleIds.length < 1) {
			return;
		}
		String conditonString = null;
		StringBuilder sb = new StringBuilder();
		ArrayList<String> rules = new ArrayList<String>();
		for (int i = 0; i < ruleIds.length; i++) {
			if (ruleIds[i].equals(UserScoreInfo.RULE_LOGIN)) {
				if (!rules.contains(UserScoreInfo.ANDROID_LOGIN)) {
					rules.add(UserScoreInfo.ANDROID_LOGIN);
				}
			} else if (ruleIds[i].equals(UserScoreInfo.RULE_SHARE)) {
				String[] shareArray = new String[] { UserScoreInfo.QQ_FRIEND,
						UserScoreInfo.SINA, UserScoreInfo.WX_FRIEND,
						UserScoreInfo.WX_ZONE, UserScoreInfo.YX_FRIEND,
						UserScoreInfo.YX_ZONE };
				for (int j = 0; j < shareArray.length; j++) {
					if (!rules.contains(shareArray[i])) {
						rules.add(shareArray[i]);
					}
				}
			}
		}
		for (int i = 0; i < rules.size(); i++) {
			sb.append(TYPE);
			sb.append(" = ");
			// TODO:
			sb.append("'" + rules.get(i) + "'");
			if (i != rules.size() - 1) {
				sb.append(" OR ");
			}
		}
		conditonString = sb.toString();
		values = new ContentValues();
		values.put(STATUS, STATUS_OK);
		getDatabase().update(DBConfig.CONTENT_URI_SCORE, values,
				USER_ID + " = '" + getUserId() + "' AND " + conditonString, null);
	}

	private String getDelMutilRecordClause() {
		StringBuilder sb = new StringBuilder();
		sb.append("rowid not in (select min(rowid) from ");
		sb.append(TABLE_NAME);
		sb.append(" group by ");
		sb.append(USER_ID);
		sb.append(",");
		sb.append(SOURCE_ID);
		sb.append(",");
		sb.append(TYPE);
		sb.append(",");
		sb.append(STATUS);
		sb.append(" ) ");
		return sb.toString();
	}

}
