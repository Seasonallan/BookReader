package com.lectek.android.lereader.lib.api.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

public class OrderResponse extends BaseDao{

	@Json(name = "orderId")
	public Integer orderId;
	
	@Json(name = "deductionAmount")
	public Double deductionAmount;
	
	@Json(name = "payAmount")
	public Double payAmount;
	
	@Json(name = "notifyURL")
	public String notifyURL;
	
	@Json(name = "isPayed")
	public Boolean isPayed;
	
	@Json(name = "productId")
	public String productId;
	

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Double getDeductionAmount() {
		return deductionAmount;
	}

	public void setDeductionAmount(Double deductionAmount) {
		this.deductionAmount = deductionAmount;
	}

	public Double getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(Double payAmount) {
		this.payAmount = payAmount;
	}

	public String getNotifyURL() {
		return notifyURL;
	}

	public void setNotifyURL(String notifyURL) {
		this.notifyURL = notifyURL;
	}

	public Boolean getIsPayed() {
		return isPayed;
	}

	public void setIsPayed(Boolean isPayed) {
		this.isPayed = isPayed;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

}
