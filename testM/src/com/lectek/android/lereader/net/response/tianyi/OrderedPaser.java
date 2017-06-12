package com.lectek.android.lereader.net.response.tianyi;

import org.json.JSONException;
import org.json.JSONObject;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/** 购买解析
 * @author mingkg21
 * @email  mingkg21@gmail.com
 * @date   2013-8-23
 */
public class OrderedPaser implements PaserInterface{

	@Override
	public OrderedResult paser(String str) {
		Data data = new Data();
		try {
			data.fromJsonObject(new JSONObject(str));
		} catch (JSONException e) {
			data = null;
		}  
		if(data != null){
			return data.getResponse();
		}
		return null;
	}
	
	public static class Data extends BaseDao{
		
		@Json(name ="response")
		public OrderedResult response;

		/**
		 * @return the response
		 */
		public OrderedResult getResponse() {
			return response;
		}

		/**
		 * @param response the response to set
		 */
		public void setResponse(OrderedResult response) {
			this.response = response;
		}
		
	}
	

}
