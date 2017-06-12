package com.lectek.android.lereader.permanent;
/**
 * @author linyiwei
 * @email 21551594@qq.com
 * @date 2012-07-27
 */
public class AppBroadcast {
	/** 关闭应用程序的ACTION */
	public static final String ACTION_CLOSE_APP = "com.lectek.android.action.CLOSE_APP";
	/** 用户登录状态改变的ACTION */
	public static final String ACTION_USER_LOGIN_STATE_CHANGE = "com.lectek.android.action.ACTION_USER_LOGIN_STATE_CHANGE";
	/** 用户信息状态改变的ACTION */ //20140423-wuwq
	public static final String ACTION_USER_INFO_STATE_CHANGE = "com.lectek.android.action.ACTION_USER_INFO_STATE_CHANGE";
	
	/** Activity的命令 */
	public static final String EXTRA_ACTIVITY_COMMAND = "com.lectek.android.extra.ACTIVITY_COMMAND";
	/** 标示关闭Activity的命令 */
	public static final int VALUE_ACTIVITY_COMMAND_FINISH_ACTIVITY = 0;
	
	/** 通知更换皮肤ACTION */
	public static final String ACITON_THEME_CHANGE = "com.lectek.android.action.THEME_CHANGE";
	/** 通知消息变化 */
	public static final String ACTION_USER_GET_MESSAGE = "com.lectek.android.action.MESSAGE_CHANGE";
	/** 通知简繁体变化 */
	public static final String ACTION_LANGUAGE_CHANGE = "com.lectek.android.action.LANGUAGE_CHANGE";
	/** 通知充值成功的广播*/
	public static final String ACTION_RECHARGE_SUCCEED = "com.lectek.android.action.RECHARGE_SUCCEED";
	/** 购买成功通知*/
	public static final String ACTION_BUY_SUCCEED = "com.lectek.android.action.BUY_SUCCEED";
	/** 购买失败通知*/
	public static final String ACTION_BUY_FAIL = "com.lectek.android.action.BUY_FAIL";
	/**
	 * 阅点不足，进入充值界面，按返回键重新选择
	 * */
	public static final String ACTION_BOOKINFO_BRO = "com.lectek.android.action.BOOKINFO_RECHARGE_SHOW";
	public static final String ACTION_MONTH_PACKAGE_BRO = "com.lectek.android.action.MONTH_PACKAGE_RECHARGE_SHOW";
	public static final String ACTION_BOOKSHELF_ORDER_BRO = "com.lectek.android.action.BOOKSHELF_ORDER_RECHARGE_SHOW";
	
	public final static String VALUE_TYPE_BOOK_BUY = "VALUE_TYPE_BOOK_BUY";
	public final static String VALUE_TYPE_VOICE_BUY = "VALUE_TYPE_VOICE_BUY";
	public final static String VALUE_TYPE_VOICE_MONTH = "VALUE_TYPE_VOICE_MONTH";
	public final static String VALUE_TYPE_BOOK_MONTH = "VALUE_TYPE_BOOK_MONTH";
	
	public final static String VALUE_MESSAGE_CHANGE_ID = "VALUE_MESSAGE_CHANGE_ID";
	/**
	 * 广播监听短信收取情况
	 * */
	public final static String ACTION_SENT_SMS_MESSAGE = "SENT_SMS_ACTION";
	public final static String ACTION_DELIVERED_SMS_MESSAGE = "DELIVERED_SMS_ACTION";
	
	/**
	 * 广播监听短代的加载基本流程
	 * */
	public final static String ACTION_SMS_BUY_LOADING_MESSAGE = "SMS_BUY_LOADING_MESSAGE";
	
	public final static String ACTION_UPDATE_BOOKSHELF = "UPDATE_BOOKSHELF";
	/**摇一摇*/
	public static final String ACTION_BEGIN_SHAKE = "action_begin_shake";
	public static final String ACTION_CANNOT_SHAKE = "action_cannot_shake";
}
