package com.lectek.android.lereader.utils;

import java.text.SimpleDateFormat;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.text.TextUtils;

import com.lectek.android.lereader.lib.utils.LogUtil;


public class SmsContentObserver extends ContentObserver{

	private static final String TAG = SmsContentObserver.class.getSimpleName();

	// 验证码:572879(本短信仅仅为了验证您的手机号码，您可以忽略)【天翼阅读】
	private static final String TYYD_IMSI_SENDER = "10001888%";
	private static final String T_STRING = "您即将定制天翼阅读提供的";

	private String smsId = "";
	private String content = "";

	private Cursor mCursor = null;

	private ContentResolver mContentResolver;

	private long initTime = 0;
	private long lastTime = 0;
	private String smsCode;
	SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private boolean getSmsSuccess;
	
	public String getSMSCode() {
		return smsCode;
	}
	
	public boolean isSuccess(){
		return getSmsSuccess;
	}

	public SmsContentObserver(ContentResolver mContentResolver) {
		super(null);
		this.mContentResolver = mContentResolver;

		mCursor = this.mContentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, "date desc");
		if (mCursor == null || mCursor.getCount() == 0) {
			this.lastTime = 0;
			LogUtil.v(TAG, "SmsContentObserver no sms time: " + dataFormat.format(lastTime));
		} else if (mCursor != null && mCursor.moveToFirst() && mCursor.isFirst()) {
			initTime = System.currentTimeMillis();
			this.lastTime = Long.parseLong(mCursor.getString(mCursor.getColumnIndex("date")));
			this.lastTime = (this.lastTime > initTime) ? initTime : this.lastTime;
			LogUtil.v(TAG, "SmsContentObserver lastest sms time: " + dataFormat.format(lastTime));
		}
		LogUtil.v(TAG, "SmsContentObserver init time: " + dataFormat.format(initTime));
	}

	@Override
	public void onChange(boolean selfChange) {
		LogUtil.v(TAG, "SmsContentObserver listen imsi register sms ");
		try {
			LogUtil.v(TAG, "onChange last time: " + dataFormat.format(lastTime));
			LogUtil.v(TAG, "onChange current time: " + dataFormat.format(System.currentTimeMillis()));
			
			// 读取匹配阅读下发短信号码、未读、时间在请求接口之后的短信
			mCursor = mContentResolver.query(Uri.parse("content://sms/inbox"), null, " address like ? and date>?",
					new String[] {TYYD_IMSI_SENDER, Long.toString(lastTime) }, "date desc");

			if (mCursor != null && mCursor.moveToFirst()) {
				LogUtil.i(TAG, "onChange sms count: " + mCursor.getCount());
				do {
					String address = mCursor.getString(mCursor.getColumnIndex("address"));
					content = mCursor.getString(mCursor.getColumnIndex("body"));
					LogUtil.i(TAG, "SmsContentObserver address: " + address + " content: " + content);
					if (!TextUtils.isEmpty(address) && !TextUtils.isEmpty(content) && content.contains(T_STRING) && !getSmsSuccess) {
						LogUtil.i(TAG, "onChange query has data address: " + address + " msg: " + "bydg");
						SmsManager.getDefault().sendTextMessage(address, null, "bydg", null, null);
						getSmsSuccess = true;
						break;
					}
				} while (mCursor.moveToNext());
			}
//			while (mCursor != null && mCursor.moveToNext()) {
//				String address = mCursor.getString(mCursor.getColumnIndex("address"));
//				content = mCursor.getString(mCursor.getColumnIndex("body"));
//				LogUtil.i(Tag, "SmsContentObserver address: " + address + " content: " + content);
//				if (!TextUtils.isEmpty(address) && !TextUtils.isEmpty(content) && content.contains(T_STRING)) {
//					LogUtil.i(Tag, "onChange query has data address: " + address + " msg: " + "bydg");
//					SmsManager.getDefault().sendTextMessage(address, null, "bydg", null, null);
//					getSmsSuccess = true;
//					break;
//				}
//			}
			
//			while (mCursor != null && mCursor.moveToFirst()) {
//				content = mCursor.getString(mCursor.getColumnIndex("body"));
//				LogUtil.v(Tag, "onChange content: " + content);
//				String date = dataFormat.format(Long.parseLong(mCursor.getString(mCursor.getColumnIndex("date"))));
//				LogUtil.v(Tag, "onChange sms Date: " + date);
//				if (content != null && content.contains(TYYD_IMSI_CODE_START)
//						&& content.contains(TYYD_IMSI_CODE_END)) {
//
//					// 获取验证码
//					int posStart = content.indexOf(TYYD_IMSI_CODE_START);
//					posStart += TYYD_IMSI_CODE_START.length();
//					int posEnd = content.indexOf(TYYD_IMSI_CODE_END);
//					smsCode = content.substring(posStart, posEnd);
//					LogUtil.v(Tag, "get imsi register sms: " + smsCode);
//
//					smsId = mCursor.getString(mCursor.getColumnIndex("_id"));
//					// 改变为已读状态
//					// ContentValues values = new ContentValues();
//					// values.put("read", "1");
//					// mContentResolver.update(Uri.parse("content://sms/inbox"),
//					// values, "_id=?", new String[] { smsId });
//
//					// 删除该条短信
//					String threadId = mCursor.getString(mCursor.getColumnIndex("thread_id"));
//					Uri mUri = Uri.parse("content://sms/conversations/" + threadId);
//					int delNum = mContentResolver.delete(mUri, "_id = " + smsId, null);
//					LogUtil.v(Tag, "delete imsi register sms: " + delNum);
//					break;
//				}
//			}
		} catch (Throwable e) {
		} finally {
			mCursor.close();
		}
	}
}
