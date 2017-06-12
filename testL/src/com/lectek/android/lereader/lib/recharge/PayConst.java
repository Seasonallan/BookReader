package com.lectek.android.lereader.lib.recharge;

public class PayConst {
	
	/** 限免 */
	public static final int LIMIT_TYPE_FREE = 1;
	/** 限价 */
	public static final int LIMIT_TYPE_PRICE = 2;
	
	/**
	 * 免费书籍
	 */
	public static final int ORDER_TYPE_FREE = 0 ;
	/**
	 * 1：按书购买
	 */
	public static final int ORDER_TYPE_BOOK = 1 ;
	/**
	 * 2：按卷购买
	 */
	public static final int ORDER_TYPE_JUAN = 2 ;
	/**
	 * 3：按章购买
	 */
	public static final int ORDER_TYPE_CHAPTER = 3 ;
	/**
	 * 4：按包月购买
	 */
	public static final int ORDER_TYPE_MONTHLY = 4 ;
	
	/**
	 * 支付宝支付，(2:支付宝，9：沃支付)
	 */
	public static final int PAY_TYPE_ALIPAY = 2;
	/**
	 * 联通wo+支付
	 */
	public static final int PAY_TYPE_WOPAY = 9;
	/**
	 * 电信话费支付
	 */
	public static final int PAY_TYPE_CHINATELECOM_FEE = 11;
	/**
	 * 电信短代支付
	 */
	public static final int PAY_TYPE_CHINATELECOM_MESSAGE_PAY = PAY_TYPE_CHINATELECOM_FEE + 1;
	/**
	 * 天翼阅点支付
	 */
	public static final int PAY_TYPE_TY_READ_POINT = PAY_TYPE_CHINATELECOM_MESSAGE_PAY + 1;
	/**
	 * 乐阅积分支付
	 */
	public static final int PAY_TYPE_LEREADER_READ_POINT = PAY_TYPE_TY_READ_POINT + 1;
	
	public static final String PAY_TYPE_TOP_UP = "1";//充值
	public static final String PAY_TYPE_PAYMENT = "2";//支付
	public static final String PAY_TRADE_TYPE_CONSUME = "1";//消费
	public static final String PAY_TRADE_TYPE_REFUND = "2";//退款
	public static final String PAY_SOURCE = "ALIPAY";//支付平台
	
}
