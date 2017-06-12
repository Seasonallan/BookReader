package com.lectek.android.lereader.lib.api.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

public class OrderToTYRead extends BaseDao{

	
	@Json(name = "order_no")
	public String order_no;
	
	@Json(name = "message_content")
	public String message_content;
	
	@Json(name = "sender_number")
	public String sender_number;

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public String getMessage_content() {
		return message_content;
	}

	public void setMessage_content(String message_content) {
		this.message_content = message_content;
	}

	public String getSender_number() {
		return sender_number;
	}

	public void setSender_number(String sender_number) {
		this.sender_number = sender_number;
	}

	
}
