package com.lectek.android.lereader.lib.recharge;

import com.lectek.android.lereader.lib.api.IOrderRecharge;
import com.lectek.android.lereader.lib.api.request.GenerateOrderInfo;
import com.lectek.android.lereader.lib.api.surfing.IOrderRechargeTY;


public class CTCMessageOrderInfo extends GenerateOrderInfo{
	public IOrderRecharge apiHandler; 
	public IOrderRechargeTY apiTYHandler;
}
