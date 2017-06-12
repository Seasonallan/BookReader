package com.lectek.android.lereader.net.response.tianyi;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/** 章节列表解析
 * @author mingkg21
 * @email  mingkg21@gmail.com
 * @date   2013-8-23
 */
public class ChapterListPaser implements PaserInterface {

	@Override
	public ArrayList<Chapter> paser(String str) { 
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
		
		@Json(name ="chapter_list",className = Chapter.class)
		public ArrayList<Chapter> content;

		/**
		 * @return the content
		 */
		public ArrayList<Chapter> getContent() {
			return content;
		}

		/**
		 * @param content the content to set
		 */
		public void setContent(ArrayList<Chapter> content) {
			this.content = content;
		}
		
	}

}
