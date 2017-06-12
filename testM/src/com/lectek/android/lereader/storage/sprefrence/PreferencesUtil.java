package com.lectek.android.lereader.storage.sprefrence;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.lectek.android.lereader.lib.net.http.AbsConnect;
import com.lectek.android.lereader.lib.storage.sprefrence.BasePreferences;
import com.lectek.android.lereader.lib.utils.ApnUtil;
import com.lectek.android.lereader.lib.utils.PkgManagerUtil;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.storage.cprovider.UriUtil;
import com.lectek.android.lereader.utils.SimpleCrypto;

/**
 * 存储一些设置
 * 
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2010-6-22
 */
public class PreferencesUtil extends BasePreferences{

	private static PreferencesUtil instance;

    private static final String TAG_APP_VERSION = "tag_app_version"; 
	private static final String TAG_INIT = "init";

	// 保持使用本机IMSI登录的UserID映射
	private static final String TAG_USER_IMSI_MAP = "_TAG_USER_IMSI";
    // userid
    private static final String TAG_USER_ID = "user_id";
    // 插件
    private static final String TAG_PLUGINS = "plugin";
    // group_id
    private static final String TAG_GROUP_ID = "group_id";
	
	private static final String TAG_USER_NICKNAME = "tag_user_nickname";
	//用户名
	private static final String TAG_USER_NAME = "user_name";
	//用户密码
	private static final String TAG_USER_PSW = "user_psw";
	//登录方式
	private static final String TAG_USER_SOURCE = "user_source";
	//用户是否登录
	private static final String TAG_USER_IS_LOGIN = "user_is_login";
	//新浪微博accessToken
	private static final String TAG_SINA_WEIBO_ACCESS_TOKEN = "sina_access_token";
	
	// 手机号码
	private static final String TAG_PHONE_NUMBER = "phone_number";
	// 绑定用户电话号码
	private static final String TAG_BIND_PHONE_NUM = "bind_phone_num";
	// 绑定用户Email
	private static final String TAG_BIND_EMAIL = "bindemail";
	// 密码
	private static final String TAG_PSW = "psw";
	// 书籍阅读的帮助提示
	private static final String TAG_HELP_READ_BOOK_TIP = "help_read_book_tip";
	// 听书界面的帮助提示
	private static final String TAG_HELP_VOICE_TIP = "help_voice_tip";
	// 是否使用X-ONLINE-HOST
	private static final String TAG_CONNET_TYPE = "connet_type";

	// 存储REGCODE
	private static final String TAG_REG_CODE = "regCode";

	/* end by xzz 2012-03-05 */

	// 默认支付--方式
	private static final String TAG_PAY_MODE = "pay_mode";

	// 游客ID
	private static final String TAG_GUEST_ID = "guestId";
	/**
	 * 状态栏的高度
	 */
	private static final String TAG_STATUS_BAR_HEIGHT = "tag_status_bar_height";
	/** 默认支付方式：手机话费 */
	public static final int PAY_MOBILE_FEE = 0;
	/** 默认支付方式：阅点 */
	public static final int PAY_READ_POINT = 1;


	// 是否显示更新提示
	private static final String SHOW_UPDATE_AGAIN = "show_update_again";
	private static final String SHOW_UPDATE_TIME = "show_update_time";


	//
	private static final String TAG_SETTING_LANGUAGE = "setting_language";

	private static final String TAG_PERSON_INFO_VIEW_BG = "person_info_view_bg";


	private static final String TAG_NEW_FUNCTION_UPDATE = "tag_new_function_update";
	
	//音量键翻页
	private static final String TAG_VOLUMN_TURN_PAGE = "volumn_turn_page";
	//阅读休息提醒
	private static final String TAG_RESTREMINDER = "restReminder";
	//阅读屏保
	private static final String TAG_SCREENSAVER = "screensaver";
	//是否显示更新提示
	private static final String SHOW_UPDATE_AGAIN_HAS_CHECKED = "show_update_again_has_checked";

	
	private static final String TAG_IS_FIRST_ENTER_APP = "tag_is_First_enter_app";
	
	private static final String TAG_GUIDE_VIEW = "tag_guide_view";
	
	/**是否进行过重复弹出框提示*/
	private static final String TAG_IS_SHOW_DIALOG_REPEAT = "tag_is_show_dialog_repeat";
	/**是否达到今日积分上限提示*/
	private static final String TAG_IS_RECORD_LIMIT = "tag_is_record_limit";
	/**单本书书城进入倒计时*/
	private static final String TAG_GOTO_BOOK_CITY_TIME_LIMIT = "tag_goto_book_city_time_limit";
	/** 热词 */
	private static final String TAG_SEARCH_KEYWORD = "tag_search_keyword";

    private static final String TAG_BROADCAST_ADVERTISEMENT_INFO = "tag_broadcast_advertisement_info";
    
    /** 接收推送消息*/
    private static final String TAG_RECEIVE_SEND_MESSAGE = "tag_receive_send_message";
    
    /** 仅用wifi下下载标识*/
    private static final String TAG_WIFI_DOWNLOAD = "tag_wifi_download";
    
    /** 屏幕亮度百分比*/
    private static final String TAG_SCREEN_LIGHT_PROGRESS = "tag_screen_light_progress";

	private PreferencesUtil(Context context) {
        super(context); 
	}

    @Override
    protected Uri getUri(Class className) {
        return UriUtil.getPrefrenceUri(className, DBConfig.PATH_PREFRENCE_COMMON);
    }

    public static PreferencesUtil getInstance(Context context) {
		if (instance == null) {
			instance = new PreferencesUtil(context.getApplicationContext());
		}
		return instance;
	}  

    /**
     * 获取存储的版本号
     * @return
     */
    public int getAppVersion(){
        return getIntValue(TAG_APP_VERSION, -1);
    }

    /**
     * 重置版本号为当前版本
     */
    public void resetAppVersion(){ 
        try {
            setIntValue(TAG_APP_VERSION, PkgManagerUtil.getApkVersionCode(context));
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }


	/**
	 * 初始化设置̬
	 * 
	 * @return
	 */
	public boolean setInit() { 
		return setBooleanValue(TAG_INIT, true); 
	}

	/**
	 * 是否初始化
	 * 
	 * @return
	 */
	public boolean isInit() {
		return getBooleanValue(TAG_INIT, false);
	}


    /**
     * 系统书签分组ID存储
     * @return
     */
    public boolean setBookMarkGroupId(int groupId) {
        return setIntValue(TAG_GROUP_ID, groupId); 
    }

    /**
     * 系统书签分组ID存储
     * @return
     */
    public int getBookMarkGroupId() {
        return getIntValue(TAG_GROUP_ID, 0);
    }

	/**
	 * 存储UserID
	 * 
	 * @param userId
	 * @return
	 */
	public boolean setUserId(String userId) {
        return setStringValue(TAG_USER_ID, userId); 
	}

    /**
     * 获取UserID
     *
     * @return
     */
    public String getUserId() {
        return getStringValue(TAG_USER_ID, LeyueConst.TOURIST_USER_ID);
    }

	/**
	 * 获取解放书城限制时间
	 */
	public String getAccessBookCityDeadline() {
		return getStringValue(TAG_GOTO_BOOK_CITY_TIME_LIMIT, "");
	}
	
	/**
	 * 设置解放书城限制时间值
	 */
	public boolean setAccessBookCityDeadline() {
		Calendar c = Calendar.getInstance();
		long deadlineTime = c.getTimeInMillis()+2*24*3600*1000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String deadlineStr = sdf.format(new Date(deadlineTime)); 
        return setStringValue(TAG_GOTO_BOOK_CITY_TIME_LIMIT, deadlineStr); 
	}

	/** 存储UserName
	 * @param userName
	 * @return
	 */
	public boolean setUserName(String userName){
        return setStringValue(TAG_USER_NAME, userName); 
	}
	
	/** 获取UserName
	 * @return
	 */
	public String getUserName(){
		return getStringValue(TAG_USER_NAME, null);
	}

    /** 存储UserPSW
     * @param userPsw
     * @return
     */
    public boolean setUserPSW(String userPsw){ 
        return  setStringValue(TAG_USER_PSW, userPsw); 
    }

    /** 获取UserPSW
     * @return
     */
    public String getUserPSW(){
        return getStringValue(TAG_USER_PSW, null);
    }

	/**
	 * 存储NickName
	 * @param nickname
	 * @return
	 */
	public boolean setUserNickName(String nickname){
        return setStringValue(TAG_USER_NICKNAME, nickname); 
	}
	/**
	 * 获取NickName
	 * @return
	 */
	public String getUserNickName(){
		return getStringValue(TAG_USER_NICKNAME, null);
	}
	
	/**
	 * 存储Source
	 * @param nickname
	 * @return
	 */
	public boolean setUserSource(String nickname){ 
        return setStringValue(TAG_USER_SOURCE, nickname); 
	}
	/**
	 * 获取Source
	 * @return
	 */
	public String getUserSource(){
		return getStringValue(TAG_USER_SOURCE, null);
	}

	
	/** 存储用户是否登录条件
	 * @return
	 */
	public boolean getIsLogin(){
		return getBooleanValue(TAG_USER_IS_LOGIN, false);
	}
	
	/** 获取用户是否登录条件
	 * @return
	 */
	public boolean setIsLogin(boolean isLogin){ 
        return setBooleanValue(TAG_USER_IS_LOGIN, isLogin); 
	}
	
	/** 用户是否完成引导操作
	 * @return
	 */
	public boolean getIsFirstGuide(){
		return getBooleanValue(TAG_GUIDE_VIEW, true);
	}
	
	/** 存储用户是否完成引导操作
	 * @return
	 */
	public boolean setIsFirstGuideFinish(boolean isGuided){ 
        return setBooleanValue(TAG_GUIDE_VIEW, !isGuided); 
	}
	
	/** 存储新浪accesstoken
	 * @param accessToken
	 * @return
	 */
	public boolean setSinaAccessToken(String accessToken){ 
        return setStringValue(TAG_SINA_WEIBO_ACCESS_TOKEN, accessToken); 
	}
	
	/** 获取新浪accesstoken
	 * @return
	 */
	public String getSinaAccessToken(){
		return getStringValue(TAG_SINA_WEIBO_ACCESS_TOKEN, null);
	}

	/**
	 * 设置手机号码
	 *
	 * @param phoneNum
	 * @return
	 */
	public boolean setPhoneNumber(String phoneNum) {
		String phoneNumber = getPhoneNum();
		// 如果之前保存的手机号码和现在的不一样，则先把密码清空
		if (!TextUtils.isEmpty(phoneNum) && !TextUtils.isEmpty(phoneNumber)) {
			if (!phoneNum.equals(phoneNumber)) {
				setPsw("");
			}
		}
        return setStringValue(TAG_PHONE_NUMBER, phoneNum);
	}

	/**
	 * 获取手机号码
	 * 
	 * @return
	 */
	public String getPhoneNum() {
		return getStringValue(TAG_PHONE_NUMBER, null);
	}

	/**
	 * 存储绑定用户电话
	 * */
	public boolean setBindPhoneNum(String email) { 
        return setStringValue(TAG_BIND_PHONE_NUM, email); 
	}

	/**
	 * 获取绑定用户电话
	 * */
	public String getBindPhoneNum() {
		return getStringValue(TAG_BIND_PHONE_NUM, null);
	}

	/**
	 * 存储绑定用户email
	 * */
	public boolean setBindEmail(String email) { 
        return setStringValue(TAG_BIND_EMAIL, email); 
	}

	/**
	 * 获取绑定用户email
	 * */
	public String getBindEmail() {
		return getStringValue(TAG_BIND_EMAIL, null);
	}

	/**
	 * 设置密码
	 * 
	 * @param psw
	 * @return
	 */
	public boolean setPsw(String psw) {
		if (!TextUtils.isEmpty(psw)) {
			try {
				psw = SimpleCrypto.encrypt(SimpleCrypto.simplePasswd, psw);
			} catch (Exception e) {
			}
		} 
        return setStringValue(TAG_PSW, psw); 
	}

	/**
	 * 获取密码
	 * 
	 * @return
	 */
	public String getPsw() {
		String psw = getStringValue(TAG_PSW, null);
		if (!TextUtils.isEmpty(psw)) {
			try {
				psw = SimpleCrypto.decrypt(SimpleCrypto.simplePasswd, psw);
			} catch (Exception e) {
			}
		}
		return psw;
	}

	/**
	 * 设置书籍阅读帮助提示开关
	 * 
	 * @param value
	 * @return
	 */
	public boolean setReadBookHelpTip(boolean value) {
		return setBoolean(TAG_HELP_READ_BOOK_TIP, value);
	}

	/**
	 * 是否书籍阅读帮助提示打开
	 * 
	 * @return
	 */
	public boolean isReadBookHelpTip() {
		return isBoolean(TAG_HELP_READ_BOOK_TIP);
	}

	private boolean setInt(String tag, int value) { 
        return setIntValue(tag, value); 
	}

	private boolean setBoolean(String tag, boolean value) {
        return setBooleanValue(tag, value); 
	}

	private boolean isBoolean(String tag) {
		return getBooleanValue(tag, true);
	}
	
	/**
	 * 设置是否进行过重复弹出框提示
	 * @param value
	 * @return
	 */
	public boolean setShowRepeatTipDialog(boolean value) {
        return setBooleanValue(TAG_IS_SHOW_DIALOG_REPEAT, value); 
	}
	
	/**
	 * 是否进行过上限提示
	 * @return
	 */
	public boolean isLimitTipToast() {
		return getBooleanValue(TAG_IS_RECORD_LIMIT, true);
	}
	
	/**
	 * 设置是否进行上限提示
	 * @param value
	 * @return
	 */
	public boolean setLimitTipToast(boolean value) {
        return setBooleanValue(TAG_IS_RECORD_LIMIT, value); 
	}
	
	/**
	 * 是否进行过重复弹出框提示
	 * @return
	 */
	public boolean isShowRepeatTipDialog() {
		return getBooleanValue(TAG_IS_SHOW_DIALOG_REPEAT, true);
	}

	/**
	 * 获取是否X-ONLINE-HOST连接
	 * 
	 * @return
	 */
	public int getConnectType() {
		// return getIntValue(TAG_CONNET_TYPE,
		// AbsConnect.CONNET_TYPE_X_ONLINE_HOST);
		return AbsConnect.CONNET_TYPE_NORMAL;
	}


	/**
	 * 读取游客ID
	 * 
	 * @return
	 */
	public String getGuestId() {
		return getStringValue(TAG_GUEST_ID, null);
	}

	/**
	 * 初始化设置
	 * 
	 * @return
	 */
	public boolean init() {
		if (!isInit()) {
			if (reset()) {
				return setInit();
			} else {
				return false;
			}
		} else {
			return true;
		}
	}


	/**
	 * 重设
	 * 
	 * @return
	 */
	public boolean reset() {
		 
		setStringValue(SHOW_UPDATE_AGAIN, "");
		if (ApnUtil.isCtwap(context)) {
			setIntValue(TAG_PAY_MODE, PAY_MOBILE_FEE);
		} else {
			setIntValue(TAG_PAY_MODE, PAY_READ_POINT);
		}
		//
		/* end by xzz 2012-03-30 */
		setBooleanValue(TAG_HELP_VOICE_TIP, true);
		setBooleanValue(TAG_HELP_READ_BOOK_TIP, true);

		setStringValue(TAG_SETTING_LANGUAGE, "-1");
        return setBooleanValue(TAG_PERSON_INFO_VIEW_BG, true);

		 
	}

	public String getShowUpdateAgain() {
		long saveMil = getLongValue(SHOW_UPDATE_TIME, 0);
		if (saveMil > 0) {
			long curMil = System.currentTimeMillis();
			long durationMil = curMil - saveMil;
			float durationDay = (float) durationMil / (1000 * 60 * 60 * 24);
			if (durationDay > 7 || durationDay < 0) {
				return "";
			}
		}
		return getStringValue(SHOW_UPDATE_AGAIN, "");
	}

	public boolean setShowUpdateAgain(String value, boolean isTimeRecord) {
		 
		if (isTimeRecord) {
			long curMil = System.currentTimeMillis();
			 setLongValue(SHOW_UPDATE_TIME, curMil);
		} else {
			setLongValue(SHOW_UPDATE_TIME, 0);
		}
        return setStringValue(SHOW_UPDATE_AGAIN, value);
		 
	}

	public boolean setNewFunctionUpdate(String newFunctionUpdate) {

        return setStringValue(TAG_NEW_FUNCTION_UPDATE, newFunctionUpdate);
		 
	}

	public String getNewFunctionUpdate() {
		return getStringValue(TAG_NEW_FUNCTION_UPDATE, "");
	}

	public boolean setVolumnTurnPage(boolean isVolumnTurn){

        return setBooleanValue(TAG_VOLUMN_TURN_PAGE, isVolumnTurn);
		 
	}
	
	public boolean isVolumnTurnPage(){
		return getBooleanValue(TAG_VOLUMN_TURN_PAGE, true);
	}

	public boolean setRestReminder(int restReminderType){

        return setIntValue(TAG_RESTREMINDER, restReminderType);
		 
	}
	
	public int getRestReminder(){
		return getIntValue(TAG_RESTREMINDER, 0);
	}
	
	public boolean setScreensaver(int screensaver){

        return setIntValue(TAG_SCREENSAVER, screensaver);
		 
	}
	
	public int getScreensaver(){
		return getIntValue(TAG_SCREENSAVER, 0);
	}

	/**
	 * 是否弹出升级提示
	 * @return
	 */
	public boolean isShowUpdateAgain(){
		if(getBooleanValue(SHOW_UPDATE_AGAIN_HAS_CHECKED, false)){
			//如果选中 代表已勾选 “一周后再提示”；需要判断时间是否为一周后
			long saveMil = getLongValue(SHOW_UPDATE_TIME, 0);
			long curMil = System.currentTimeMillis();
			long durationMil = curMil - saveMil;
			float durationDay = (float)durationMil/(1000*60*60*24);
			if(durationDay > 7 || durationDay < 0) {
				return true;
			}else {
				return false;
			}
		}else {
			//未勾选，则会弹出
			return true;
		}
	}
	
	/**
	 * 设置下次是否弹出升级提示
	 * @param isUpdateAlert 是否弹出升级提示
	 * @return
	 */
	public boolean setShowUpdateAgain(boolean isUpdateAlert){
		 
		long curMil = System.currentTimeMillis();
		 setLongValue(SHOW_UPDATE_TIME, curMil);
        return setBooleanValue(SHOW_UPDATE_AGAIN_HAS_CHECKED, isUpdateAlert);
		 
	}
	
	/**
	 * 设置是否是第一次启动APP
	 * @param isFirst
	 * @return
	 */
	public boolean setIsFirstEnterApp(boolean isFirst){
		return setBooleanValue(TAG_IS_FIRST_ENTER_APP, isFirst);
	}
	
	/**
	 * 获取是否是第一次启动APP
	 * @param defValue
	 * @return
	 */
	public boolean getIsFirstEnterApp(boolean defValue){
		return getBooleanValue(TAG_IS_FIRST_ENTER_APP, defValue);
	}

	
	/**
	 * 保存热词列表的GSON字符串
	 * @param gson
	 * @return
	 */
	public boolean setSearchKeywords(String gson){
        return setStringValue(TAG_SEARCH_KEYWORD, gson);
	}
	
	/**
	 * 获取热词列表的GSON字符串
	 * @return
	 */
	public String getSearchKeywords(){
		return getStringValue(TAG_SEARCH_KEYWORD, null);
	}
	
	/**
	 * 保存状态栏的高度
	 * @param value
	 * @return
	 */
	public boolean setStatusBarHeight(int value){
		return setInt(TAG_STATUS_BAR_HEIGHT, value);
	}
	
	/**
	 * 获取状态栏的高度
	 * @return
	 */
	public int getStatusBarHeight(){
		return getIntValue(TAG_STATUS_BAR_HEIGHT, 0);
	}

    /**
     * 保存广告通知GSON字符串
     * @param gson
     * @return
     */
    public boolean setBroadCastAdvertisementInfo(String gson){
        return  setStringValue(TAG_BROADCAST_ADVERTISEMENT_INFO, gson);
    }

    /**
     * 获取广告通知GSON字符串
     * @return
     */
    public String getBroadCastAdvertisementInfo(){
        return getStringValue(TAG_BROADCAST_ADVERTISEMENT_INFO, null);
    }

    /**
     * 存储插件信息
     *
     * @param plugins
     * @return
     */
    public boolean setPluginStr(String plugins) {
        return setStringValue(TAG_PLUGINS, plugins);
    }

    /**
     * 获取插件信息
     *
     * @return
     */
    public String getPlugin() {
        return getStringValue(TAG_PLUGINS, null);
    }
    
    /**
     * 是否接收推送信息
     *
     * @param isReceive
     * @return
     */
    public boolean setReceiveSendMsgBoolean(boolean isReceive) {
        return setBooleanValue(TAG_RECEIVE_SEND_MESSAGE, isReceive);
    }

    /**
     * 获取是否接收推送信息
     *
     * @return
     */
    public boolean getReceiveSendMsgBoolean() {
        return getBooleanValue(TAG_RECEIVE_SEND_MESSAGE, true);
    }
    
    /**
     * 是否仅在wifi下下载
     *
     * @param isReceive
     * @return
     */
    public boolean setWifiDownloadBoolean(boolean isReceive) {
        return setBooleanValue(TAG_WIFI_DOWNLOAD, isReceive);
    }

    /**
     * 获取是否仅在wifi下下载
     *
     * @return
     */
    public boolean getWifiDownloadBoolean() {
        return getBooleanValue(TAG_WIFI_DOWNLOAD, true);
    }
    
    /**
     * 设置屏幕亮度百分比
     *
     * @param isReceive
     * @return
     */
    public boolean setScreenLightProgress(Integer progress) {
        return setIntValue(TAG_SCREEN_LIGHT_PROGRESS, progress);
    }

    /**
     * 获取屏幕亮度百分比
     *
     * @return
     */
    public Integer getScreenLightProgress() {
        return getIntValue(TAG_SCREEN_LIGHT_PROGRESS, 50);
    }

}
