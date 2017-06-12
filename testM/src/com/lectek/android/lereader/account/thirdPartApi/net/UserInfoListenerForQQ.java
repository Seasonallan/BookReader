package com.lectek.android.lereader.account.thirdPartApi.net;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.lectek.android.lereader.account.thirdPartApi.entity.UserInfo;
import com.lectek.android.lereader.lib.utils.LogUtil;

public class UserInfoListenerForQQ extends BaseRequestListener{
	
	private static final String TAG = "UserInfoListener";
	private Callback mCallback;

	public UserInfoListenerForQQ(Callback callback){
		this.mCallback = callback;
	}

	public void onComplete(String response, Object state){
		super.onComplete(response, state);
	    try {
	    	JSONObject obj = ParseResoneJson.parseJson(response);

	    	int ret = 0;
	    	String msg = "";
	    	try {
	    		ret = obj.getInt("ret");
	    		msg = obj.getString("msg");
	    	}catch (JSONException localJSONException1){
	    	}
	    	if (ret == 0) {
	    		String nickname = obj.getString("nickname");
	    		String icon_30 = obj.getString("figureurl");
	    		String icon_50 = obj.getString("figureurl_1");
	    		String icon_100 = obj.getString("figureurl_2");
	    		UserInfo info = new UserInfo(nickname, icon_30, icon_50, icon_100);
	    		this.mCallback.onSuccess(info);
	    	} else {
	    		this.mCallback.onFail(ret, msg);
	    	}
	    } catch (NumberFormatException e) {
	    	this.mCallback.onFail(-2147483648, e.getMessage());
	    	e.printStackTrace();
	    } catch (JSONException e) {
	    	this.mCallback.onFail(-2147483648, e.getMessage());
	    	e.printStackTrace();
	    } catch (CommonException e) {
	    	this.mCallback.onFail(-2147483648, e.getMessage());
	    	e.printStackTrace();
	    }
	    LogUtil.i("UserInfoListenerForQQ", response);
	}

	public void onFileNotFoundException(FileNotFoundException e, Object state){
		this.mCallback.onFail(-2147483648, "Resource not found:" + e.getMessage());
	}

	public void onIOException(IOException e, Object state){
		this.mCallback.onFail(-2147483648, "Network Error:" + e.getMessage());
	}
}
