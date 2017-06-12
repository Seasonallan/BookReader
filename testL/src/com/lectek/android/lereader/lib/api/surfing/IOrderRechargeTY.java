package com.lectek.android.lereader.lib.api.surfing;

import com.lectek.android.lereader.lib.api.response.OrderResponseToTYRead;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;

public interface IOrderRechargeTY {
	/**
	 * 订单生产接口 短代的订单
	 * 
	 * @param bookId
	 * @return
	 * @throws GsonResultException
	 */
	public OrderResponseToTYRead generateOrderToTYRead(String clientAppKey, String productId, String callBackUrl)throws GsonResultException;
}
