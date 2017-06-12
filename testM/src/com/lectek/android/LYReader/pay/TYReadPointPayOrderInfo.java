package com.lectek.android.LYReader.pay;

import com.lectek.android.lereader.net.IHttpRequest4Leyue;
import com.lectek.android.lereader.net.openapi.IHttpRequest4TianYi;

public class TYReadPointPayOrderInfo {

	public String userID;
	public String bookID;
	/**
	 * == OrderConfig.MONTHLY_PAY按月,否则其他
	 */
	public String payMode = "none";
	public String bookName;
	public String leBookID;
	public String fee;
	public int sourceType;
	public int purchaseType;
	
	/**
	 * 计费类型（1：按书，2：按卷，3：按章，4：包月）
	 */
	public int orderType;
	
	public IHttpRequest4TianYi apiHandlerTY;
	public IHttpRequest4Leyue apiHandler;
}
