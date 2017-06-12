package com.lectek.android.lereader.net.response.tianyi;

import org.json.JSONException;
import org.json.JSONObject;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/** 章节内容解析
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-9-15
 */
public class ChapterInfoPaser implements PaserInterface {

	@Override
	public Chapter paser(String str) {
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
	
	public static class Data extends BaseDao{
		
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
		
		@Json(name ="chapter_info")
		public Chapter content;

		/**
		 * @return the content
		 */
		public Chapter getContent() {
			return content;
		}

		/**
		 * @param content the content to set
		 */
		public void setContent(Chapter content) {
			this.content = content;
		}
		
	}

}
