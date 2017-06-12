package com.lectek.android.lereader.permanent;

/**
 * 订购配置文件
 * @author chends@lectek.com
 * @date 2014-07-04
 */
public interface OrderConfig {

	/** 限免 */
	public static final int LIMIT_TYPE_FREE = 1;
	/** 限价 */
	public static final int LIMIT_TYPE_PRICE = 2;
	
	/**
	 * 天翼书籍按月
	 */
	public static final String MONTHLY_PAY = "monthly_pay";
	
	/**
	 * 支付类型:帐户
	 */
	public static final int PURCHASE_TYPE_ACCOUNT = 1;
	/**
	 * 支付类型:支付宝
	 */
	public static final int PURCHASE_TYPE_ALIPAY = PURCHASE_TYPE_ACCOUNT + 1;
	/**
	 * 支付类型:银行卡
	 */
	public static final int PURCHASE_TYPE_BANK_CARD = PURCHASE_TYPE_ALIPAY + 1;
	/**
	 * 支付类型：快捷支付
	 */
	public static final int PURCHASE_TYPE_FAST_PAY = PURCHASE_TYPE_BANK_CARD + 1;
	/**
	 * 支付类型：话费支付
	 */
	public static final int PURCHASE_TYPE_FEE = PURCHASE_TYPE_FAST_PAY + 1;
	/**
	 * 支付类型：天翼阅点
	 */
	public static final int PURCHASE_TYPE_TY_READ_POINT = PURCHASE_TYPE_FEE + 1;
	/**
	 * 支付类型：移动MM
	 */
	public static final int PURCHASE_TYPE_TY_MM_PAY = PURCHASE_TYPE_TY_READ_POINT + 1;
			
}
