package com.lectek.android.lereader.lib.recharge;

import com.lectek.android.lereader.lib.api.IOrderRecharge;

public class PayOrder {
	/**
	 * 书本id
	 */
	private String bookId;
	/**
	 * 支付费用
	 */
	private String charge;
	/**
	 * 计费类型(0：免费，1：按书，2：按卷，3：按章，4：按频道)
	 */
	private String calType; 
	/**
	 * 书本名称
	 */
	private String bookName;
	
	/**
	 * 书本描述
	 */
	private String bookDesc;
	
	/**
	 * 支付方式
	 */
	private String payType;

	private String calObj;
	
	/**
	 * 计费对象(按书时，bookId，按卷，volumnId，按章，chapterId，按频道，channelId)
	 */
	public String getCalObj() {
		return calObj;
	}
	
	/**
	 * 计费对象(按书时，bookId，按卷，volumnId，按章，chapterId，按频道，channelId)
	 * @param calObj
	 */
	public void setCalObj(String calObj) {
		this.calObj = calObj;
	}
	
	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

	public String getCalType() {
		return calType;
	}

	public void setCalType(String calType) {
		this.calType = calType;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getBookDesc() {
		return bookDesc;
	}

	public void setBookDesc(String bookDesc) {
		this.bookDesc = bookDesc;
	}
	
	public IOrderRecharge apiHandler;
	public String sourceType;
	public int appVersionCode;
	public String userID;
	
	private String key;
	public void setKey(String key) {
		this.key = key;
	}
	public String getKey() {
		return key;
	}
}
