package com.lectek.android.lereader.net.response.tianyi;

import org.json.JSONException;
import org.json.JSONObject;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/** 书籍详情解析
 * @author mingkg21
 * @email  mingkg21@gmail.com
 * @date   2013-8-23
 */
public class ContentInfoPaser implements PaserInterface {

	@Override
	public ContentInfo paser(String str) {
		Data data = new Data();
		try {
			data.fromJsonObject(new JSONObject(str));
		} catch (JSONException e) {
			data = null;
		} 
		if (data != null) {
			Response response = data.getResponse();
			if (response != null) {
				return response.getContent();
			}
		}
		return null;
	}
	
	public static class Data  extends BaseDao{
		
		@Json(name ="response")
		public Response response;

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
		
		@Json(name ="content_info")
		public ContentInfo content;

		/**
		 * @return the content
		 */
		public ContentInfo getContent() {
			return content;
		}

		/**
		 * @param content the content to set
		 */
		public void setContent(ContentInfo content) {
			this.content = content;
		}
		
	}

}
