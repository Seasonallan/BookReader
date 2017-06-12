package com.lectek.android.LYReader.pay;

import com.lectek.android.lereader.lib.api.IOrderRecharge;

/**
 * 乐阅积分兑换书籍订单信息
 * @author chends@lectek.com
 * @date 2014/07/17
 */
public class RewardPointOrderInfo {

	public String bookID;
	public float fee;
	public String bookName;
	public String purchaseType;
	public IOrderRecharge apiHandler;
}
