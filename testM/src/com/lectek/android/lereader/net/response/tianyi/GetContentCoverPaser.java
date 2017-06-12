package com.lectek.android.lereader.net.response.tianyi;

import org.json.JSONException;
import org.json.JSONObject;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

public class GetContentCoverPaser implements PaserInterface{

	@Override
	public String paser(String str) {
		Data data = new Data();
		try {
			data.fromJsonObject(new JSONObject(str));
		} catch (JSONException e) {
			data = null;
		} 
		if(data != null){
			Response response = data.getResponse();
			if(response != null){
				return response.getContent();
			}
		}
		return null;
	}
	
	public static class Data  extends BaseDao{
		
		@Json(name ="response")
		private Response response;

		/**
		 * @return the response
		 */
		public Response getResponse() {
			return response;
		}

		/**
		 * @param response the response to set
		 */
		public void setResponse(Response response) {
			this.response = response;
		}
		
	}
	
	public static class Response  extends BaseDao{
		
		@Json(name ="imageUrl")
		private String content;

		/**
		 * @return the content
		 */
		public String getContent() {
			return content;
		}

		/**
		 * @param content the content to set
		 */
		public void setContent(String content) {
			this.content = content;
		}
		
	}

}
