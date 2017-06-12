package com.lectek.android.LYReader.pay;

import com.lectek.android.lereader.net.response.tianyi.OrderedResult;
import com.lectek.android.lereader.net.response.tianyi.PayAfterResult;
import com.lectek.android.lereader.net.response.tianyi.PayResult;

public class TYMonthlyPayResult {

	/**话费支付 第一步结果*/
	public PayResult sso1PayResult;
	/**话费支付 第二步结果*/
	public PayAfterResult sso2PayAfterResult;
	/**阅点支付结果*/
	public OrderedResult pointPayOrderResult;
	/**付费方式--话费，阅点*/
	public String type;
	/**付费类型--包月，按本*/
	public String payMode;
}
