package com.lectek.android.lereader.utils;

import java.text.SimpleDateFormat;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

import com.lectek.android.lereader.lib.utils.LogUtil;


/**
 * @author Kinson.Wu
 * @email Kinson.Wu@gmail.com
 * @date 2011-06-27 
 * Java代码
 * 		public final static String SMS_URI_ALL = "content://sms/"; //0
 *      public final static String SMS_URI_INBOX = "content://sms/inbox";//1
 *      public final static String SMS_URI_SEND = "content://sms/sent";//2
 *      public final static String SMS_URI_DRAFT = "content://sms/draft";//3 
 *      public final static String SMS_URI_OUTBOX = "content://sms/outbox";//4 
 *      public final static String SMS_URI_FAILED = "content://sms/failed";//5 
 *      public final static String SMS_URI_QUEUED = "content://sms/queued";//6 
 * sms主要结构： 
 *      _id => 短消息序号 如100
 *      thread_id => 对话的序号 如100 
 *      address => 发件人地址，手机号.如+8613811810000 
 *      person => 发件人，返回一个数字就是联系人列表里的序号，陌生人为null 
 *      date => 日期 long型。如1256539465022 
 *      protocol => 协议 0 SMS_RPOTO, 1 MMS_PROTO 
 *      read => 是否阅读 0未读， 1已读 
 *      status => 状态 -1接收，0 complete, 64 pending, 128 failed 
 *      type => 类型 1是接收到的，2是已发出 
 *      body => 短消息内容 
 *      service_center => 短信服务中心号码编号。如+8613800755500
 *      记得在AndroidManifest.xml中加入android.permission.READ_SMS这个permission
 *      <uses-permission android:name="android.permission.READ_SMS" />
 */
public class SMSObserver extends ContentObserver {

	private static final String TAG = SMSObserver.class.getSimpleName();

	// 验证码:572879(本短信仅仅为了验证您的手机号码，您可以忽略)【天翼阅读】
	private static final String TYYD_IMSI_SENDER = "106590099";
	private static final String TYYD_IMSI_CODE_START = "验证码:";
	private static final String TYYD_IMSI_CODE_END = "(本短信仅仅为了验证您的手机号码，您可以忽略)【天翼阅读】";

	private String smsId = "";
	private String content = "";

	private Cursor mCursor = null;

	private ContentResolver mContentResolver;

	private long lastTime = 0;
	private String smsCode;

	public String getSMSCode() {
		return smsCode;
	}

	public SMSObserver(ContentResolver mContentResolver) {
		super(null);
		this.mContentResolver = mContentResolver;

		mCursor = this.mContentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, "date desc");
		SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		if (mCursor == null || mCursor.getCount() == 0) {
			this.lastTime = 0;
			LogUtil.v(TAG, "SMSObserver no sms time: " + dataFormat.format(lastTime));
		} else if (mCursor != null && mCursor.moveToFirst() && mCursor.isFirst()) {
			long initTime = System.currentTimeMillis();
			this.lastTime = Long.parseLong(mCursor.getString(mCursor.getColumnIndex("date")));
			this.lastTime = (this.lastTime > initTime) ? initTime : this.lastTime;
			LogUtil.v(TAG, "SMSObserver lastest sms time: " + dataFormat.format(lastTime));
		}
		LogUtil.v(TAG, "SMSObserver init time: " + dataFormat.format(lastTime));
	}

	@Override
	public void onChange(boolean selfChange) {
		LogUtil.v(TAG, "listen imsi register sms ");
		try {
			// 读取匹配阅读下发短信号码、未读、时间在请求接口之后的短信
			mCursor = mContentResolver.query(Uri.parse("content://sms/inbox"), null, " address=? and date>?",
					new String[] {TYYD_IMSI_SENDER, Long.toString(lastTime) }, "date desc");

			SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			LogUtil.v(TAG, "onChange last time: " + dataFormat.format(lastTime));
			LogUtil.v(TAG, "onChange current time: " + dataFormat.format(System.currentTimeMillis()));
			if (mCursor != null) {
				LogUtil.v(TAG, "onChange query has data: " + Integer.toString(mCursor.getCount()));
			}
			while (mCursor != null && mCursor.moveToFirst()) {
				content = mCursor.getString(mCursor.getColumnIndex("body"));
				LogUtil.v(TAG, "onChange content: " + content);
				String date = dataFormat.format(Long.parseLong(mCursor.getString(mCursor.getColumnIndex("date"))));
				LogUtil.v(TAG, "onChange sms Date: " + date);
				if (content != null && content.contains(TYYD_IMSI_CODE_START)
						&& content.contains(TYYD_IMSI_CODE_END)) {

					// 获取验证码
					int posStart = content.indexOf(TYYD_IMSI_CODE_START);
					posStart += TYYD_IMSI_CODE_START.length();
					int posEnd = content.indexOf(TYYD_IMSI_CODE_END);
					smsCode = content.substring(posStart, posEnd);
					LogUtil.v(TAG, "get imsi register sms: " + smsCode);

					smsId = mCursor.getString(mCursor.getColumnIndex("_id"));
					// 改变为已读状态
					// ContentValues values = new ContentValues();
					// values.put("read", "1");
					// mContentResolver.update(Uri.parse("content://sms/inbox"),
					// values, "_id=?", new String[] { smsId });

					// 删除该条短信
					String threadId = mCursor.getString(mCursor.getColumnIndex("thread_id"));
					Uri mUri = Uri.parse("content://sms/conversations/" + threadId);
					int delNum = mContentResolver.delete(mUri, "_id = " + smsId, null);
					LogUtil.v(TAG, "delete imsi register sms: " + delNum);
					break;
				}
			}
		} catch (Throwable e) {
		} finally {
			mCursor.close();
		}
	}

	public void delSmsMsg() {
		LogUtil.v(TAG, "del imsi register sms ");
		try {
			// 读取匹配阅读下发短信号码、未读、时间在请求接口之后的短信
			mCursor = mContentResolver.query(Uri.parse("content://sms/inbox"),
					null, " address=?", new String[] { TYYD_IMSI_SENDER },
					"date desc");
			while (mCursor != null && mCursor.moveToFirst()) {
				content = mCursor.getString(mCursor.getColumnIndex("body"));
				if (content != null && content.contains(TYYD_IMSI_CODE_START)
						&& content.contains(TYYD_IMSI_CODE_END)) {

					smsId = mCursor.getString(mCursor.getColumnIndex("_id"));
					// 改变为已读状态
					// ContentValues values = new ContentValues();
					// values.put("read", "1");
					// mContentResolver.update(Uri.parse("content://sms/inbox"),
					// values, "_id=?", new String[] { smsId });

					// 删除该条短信
					String threadId = mCursor.getString(mCursor
							.getColumnIndex("thread_id"));
					Uri mUri = Uri.parse("content://sms/conversations/"
							+ threadId);
					int delNum = mContentResolver.delete(mUri,
							"_id = " + smsId, null);
					LogUtil.v("SMSObserver", "delete imsi register sms "
							+ delNum);
					break;
				}
			}
		} catch (Throwable e) {
		} finally {
			mCursor.close();
		}
	}
}
