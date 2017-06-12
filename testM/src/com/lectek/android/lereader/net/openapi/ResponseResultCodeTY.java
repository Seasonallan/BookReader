package com.lectek.android.lereader.net.openapi;

/**
 * @description
	天翼开放平台 状态码
 * @author chendt
 * @date 2013-11-20
 * @Version 1.0
 */
public final class ResponseResultCodeTY {
	public static final int SSO_ORDER_OK = 1; //话费支付订购成功
	public static final int UNSUBCRIBE_AGAIN = 5; //重复包月退订
	public static final int CONVERT_POINT_SUCCESS = 100000; //积分兑换阅点成功
	public static final int ORDERED_STATUS_TIP = 104003; //尊敬的XX 您好,您没有订购当前X,您当前剩余X阅点,订购需要X阅点或话费X元
	public static final int NOT_LOGIN_FOR_FEE_CHAPTER = 104004; //当前章节为收费章节，您还未登录
	public static final int ORDER_OK = 108000; //订购成功
	public static final int ORDER_FAIL = 108003;//订购失败
	public static final int NOT_ORDERED = 108004; //不存在订购关系

	public static final int HAD_EXIST_ORDERED = 108007; //已经存在订购关系
	public static final int NOT_SUFFICIENT_FUNDS = 108010;//余额不足,您当前剩余{0}阅点,购买需要{1}阅点
	public static final int POINT_NOT_ENOUGH = 109003;//用户积分不足
	public static final int UNSUBCRIBE_FAIL_NOT_OAUTH = 403032;//登录账号和所使用的手机号码不匹配
	

}
