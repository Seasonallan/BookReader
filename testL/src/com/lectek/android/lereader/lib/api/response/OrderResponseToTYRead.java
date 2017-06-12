package com.lectek.android.lereader.lib.api.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

public class OrderResponseToTYRead extends BaseDao{

	@Json(name = "response")
	private OrderToTYRead response;

	public OrderToTYRead getOrderToTYRead() {
		return response;
	}

	public void setOrderToTYRead(OrderToTYRead response) {
		this.response = response;
	}
}
