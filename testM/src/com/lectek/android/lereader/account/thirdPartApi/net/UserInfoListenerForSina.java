package com.lectek.android.lereader.account.thirdPartApi.net;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.lectek.android.lereader.lib.utils.LogUtil;


public class UserInfoListenerForSina extends BaseRequestListener{
	private Callback mCallback;

	public UserInfoListenerForSina(Callback callback){
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
		          String nickName = obj.getString("name");
//		          String weihao = obj.getString("weihao");
//		          LogUtil.v("UserInfoListenerForSina", weihao);
		          this.mCallback.onSuccess(nickName);
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
		    LogUtil.i("UserInfoListenerForSina", response);
	  }

	  public void onFileNotFoundException(FileNotFoundException e, Object state){
	    this.mCallback.onFail(-2147483648, "Resource not found:" + e.getMessage());
	  }

	  public void onIOException(IOException e, Object state){
	    this.mCallback.onFail(-2147483648, "Network Error:" + e.getMessage());
	  }
}
