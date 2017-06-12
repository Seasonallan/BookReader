package com.lectek.android.lereader.lib.net;

/** 响应的状态码
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2012-8-30
 */
public final class ResponseResultCode {
	
	/** 成功*/
	public static final String STATUS_OK = "200";
	/** 失败*/
	public static final String STATUS_FAILD = "-1";
	
	/** 系统错误*/
	public static final String STATUS_SYSTEM_ERROR = "10001";
	/** 数据库错误*/
	public static final String STATUS_DATA_BASE_ERROR = "10002";
	/** 未经授权*/
	public static final String STATUS_UNAUTHORRIZED = "10003";
	
	/** 参数为空*/
	public static final String STATUS_PARAM_EMPTY = "20001";
	/** 更新不成功*/
	public static final String STATUS_UPDATE_FAILD = "20002";
	/** 删除不成功*/
	public static final String STATUS_DELETE_FAILD = "20003";
	/** 用户不存在*/
	public static final String STATUS_USER_UNAVAILABLE = "20101";
	/** 用户已存在*/
	public static final String STATUS_USER_AVAILABLE = "20102";
	/** 账号为空*/
	public static final String STATUS_ACCOUNT_EMPTY = "20103";
	/** 密码错误*/
	public static final String STATUS_PSW_ERROR = "20104";
	/** 密码为空*/
	public static final String STATUS_PSW_EMPTY = "20105";
	/** 用户充值失败*/
	public static final String STATUS_RECHARGE_FAILD = "20106";
	/** 用户余额不足*/
	public static final String STATUS_USER_NO_ENOUGH_MONEY = "20107";
	/** 用户订购失败*/
	public static final String STATUS_USER_ORDER_FAILD = "20108";
	/** 用户无订购书籍*/
	public static final String STATUS_USER_NO_ORDER_BOOK = "20109";
	/** 用户已注销*/
	public static final String STATUS_USER_UNREGISTER = "20110";
	/** 该书已不存在*/
	public static final String STATUS_NO_AVAILABLE_BOOK = "20201";
	/** 无该类型子分类*/
	public static final String STATUS_NO_AVAILABLE_ASSORTMENT = "20202";
	/** 无该类型书籍*/
	public static final String STATUS_NO_KIND_BOOK = "20203";
	/** 无该频道书籍*/
	public static final String STATUS_NO_CHANNEL_BOOK = "20204";
	/** 无该专题书籍*/
	public static final String STATUS_NO_SPECIAL_BOOK = "20205";
	/** 无该排行书籍*/
	public static final String STATUS_NO_RANK_BOOK = "20206";
	/** 未找到跟关键字相关的书籍*/
	public static final String STATUS_NO_FIND_REAL_KEY_BOOK = "20207";
	/** 该书已下线*/
	public static final String STATUS_NO_FIND_BOOK_OFF_LINE = "20208";
	
	/** 话费支付订购成功 */
	public static final int SSO_ORDER_OK = 1;
	/** 登录账号和所使用的手机号码不匹配 */
	public static final int UNSUBCRIBE_FAIL_NOT_OAUTH = 403032;
	/** 订购成功 */
	public static final int ORDER_OK = 1;//108000;
	/** 余额不足,您当前剩余{0}阅点,购买需要{1}阅点 */
	public static final int NOT_SUFFICIENT_FUNDS = 10117;//108010;

}
