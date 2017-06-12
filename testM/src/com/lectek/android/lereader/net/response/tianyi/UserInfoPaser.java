package com.lectek.android.lereader.net.response.tianyi;

import org.json.JSONException;
import org.json.JSONObject;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;
import com.lectek.android.lereader.storage.dbase.TianYiUserInfo;

/** 个人信息解析
 * @author mingkg21
 * @email  mingkg21@gmail.com
 * @date   2013-8-21
 */
public class UserInfoPaser implements PaserInterface{

	@Override
	public TianYiUserInfo paser(String str) {
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
		
		@Json(name ="user_info")
		public TianYiUserInfo content;

		/**
		 * @return the content
		 */
		public TianYiUserInfo getContent() {
			return content;
		}

		/**
		 * @param content the content to set
		 */
		public void setContent(TianYiUserInfo content) {
			this.content = content;
		}
		
	}

}
