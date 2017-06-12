package com.lectek.android.lereader.lib.recharge;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 *  我的订购HTTP数据结构
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public class OrderInfo extends BaseDao {

	public static final long serialVersionUID = -190734710746841476L;
	
	public static final String PAY_ORDER_STATUS_NO_PAY = "0";	//未支付
	public static final String PAY_ORDER_STATUS_SUCCESS = "1";	//支付成功
	public static final String PAY_ORDER_STATUS_FAIL = "2";	//支付失败
	
	@Json(name = "orderId")
	public String orderId; //订单编号
	
	@Json(name = "deductionAmount")
	public String deductionAmount; //扣减金额
	
	@Json(name = "payAmount")
	public String payAmount; //支付金额
	
	@Json(name = "notifyURL")
	public String notifyURL; //支付宝通知服务端的回调地址
	
	@Json(name = "isPayed")
	public boolean hasOrderPay; //订单已付款，是否需要支付流程操作。
	
	@Json(name = "payCode")  //计费代码
	public String payCode;
	
	@Json(name = "payName")	//计费名称
	public String payName;
	
	@Json(name = "payOrderStatus")
	public String payOrderStatus;	//支付交易状态（0：未支付、1：已支付、2：支付失败）
	

	public boolean isHasOrderPay() {
		return hasOrderPay;
	}

	public void setHasOrderPay(boolean hasOrderPay) {
		this.hasOrderPay = hasOrderPay;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getDeductionAmount() {
		return deductionAmount;
	}

	public void setDeductionAmount(String deductionAmount) {
		this.deductionAmount = deductionAmount;
	}

	public String getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}

	public String getNotifyURL() {
		return notifyURL;
	}

	public void setNotifyURL(String notifyURL) {
		this.notifyURL = notifyURL;
	}
	
	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}

	public String getPayName() {
		return payName;
	}

	public void setPayName(String payName) {
		this.payName = payName;
	}
	
	public String getPayOrderStatus() {
		return payOrderStatus;
	}

	public void setPayOrderStatus(String payOrderStatus) {
		this.payOrderStatus = payOrderStatus;
	}

	@Override
	public String toString() {
		return "OrderInfo [orderId=" + orderId + ", deductionAmount="
				+ deductionAmount + ", payAmount=" + payAmount + ", notifyURL="
				+ notifyURL + ", hasOrderPay=" + hasOrderPay + ", payCode="
				+ payCode + ", payName=" + payName + ", payOrderStatus="
				+ payOrderStatus + "]";
	}

}
