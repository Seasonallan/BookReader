package com.lectek.android.lereader.account.thirdPartApi.net;

import org.json.JSONException;
import org.json.JSONObject;

public class ParseResoneJson {
	public static JSONObject parseJson(String response) throws JSONException, NumberFormatException, CommonException{
	    if (response.equals("false")) {
	        throw new CommonException("request failed");
	    }
	    if (response.equals("true")) {
	        response = "{value : true}";
	    }
	
	    if (response.contains("allback(")) {
	        response = response.replaceFirst("[\\s\\S]*allback\\(([\\s\\S]*)\\);[^\\)]*\\z", "$1");
	        response = response.trim();
	    }
	
	    JSONObject json = new JSONObject(response);
	
	    return json;
	}
}
