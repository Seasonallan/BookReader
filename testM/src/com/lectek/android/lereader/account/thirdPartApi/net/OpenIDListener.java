package com.lectek.android.lereader.account.thirdPartApi.net;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.lectek.android.lereader.account.thirdPartApi.entity.OpenId;
import com.lectek.android.lereader.lib.utils.LogUtil;

public class OpenIDListener extends BaseRequestListener{
	
	private Callback mCallback;

	public OpenIDListener(Callback callback){
	    this.mCallback = callback;
	}

	public void onComplete(String response, Object state){
	    super.onComplete(response, state);
	    try {
		      JSONObject obj = ParseResoneJson.parseJson(response);
	
		      int ret = 0;
		      String msg = "";
		      try {
		          ret = obj.getInt("error");
		          msg = obj.getString("error_description");
		      }catch (JSONException localJSONException1){
		    	  
		      }
		      
		      if (ret == 0) {
		          String openid = obj.getString("openid");
		          String client_id = obj.getString("client_id");
		          OpenId id = new OpenId(openid, client_id);
		          this.mCallback.onSuccess(id);
		      } else {
		          this.mCallback.onFail(ret, msg);
		      }
		    }catch (CommonException e) {
		         this.mCallback.onFail(-2147483648, e.getMessage());
		    } catch (NumberFormatException e) {
		         this.mCallback.onFail(-2147483648, e.getMessage());
		    } catch (JSONException e) {
		         this.mCallback.onFail(-2147483648, e.getMessage());
		    }
		    LogUtil.i("OpenIDListener", response);
	  }

	  public void onFileNotFoundException(FileNotFoundException e, Object state){
	    this.mCallback.onFail(-2147483648, "Resource not found:" + e.getMessage());
	  }

	  public void onIOException(IOException e, Object state){
	    this.mCallback.onFail(-2147483648, "Network Error:" + e.getMessage());
	  }
}
