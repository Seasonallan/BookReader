package com.lectek.android.lereader.account.thirdPartApi.net;

import java.io.FileNotFoundException;
import java.io.IOException;
import android.os.Bundle;

public class AsyncHttpRequestRunner {
	
	private static final String SUCCESS_STATE = "0";
	private static final String FILE_NOT_FOUND_STATE = "1";
	private static final String IO_EXCEPTION_STATE = "2";
	
	public void request(String url, Bundle parameters, IRequestListener listener){
	    request(url, parameters, "GET", listener);
	}
	
	public void postRequest(String url, Bundle parameters, IRequestListener listener){
		request(url, parameters, "POST", listener);
	}
	
	public void request(final String url,final Bundle parameters, final String httpMethod, final IRequestListener listener){
	    new Thread(){
	    	public void run() {
	    		try {
	    			String resp = null;
	    			resp = ClientHttpRequest.openUrl(url, httpMethod, parameters);
	    			listener.onComplete(resp, SUCCESS_STATE);
				} catch (FileNotFoundException e) {
			        listener.onFileNotFoundException(e, FILE_NOT_FOUND_STATE);
		        } catch (IOException e) {
		            listener.onIOException(e, IO_EXCEPTION_STATE);
		        } catch (Exception e) {
					e.printStackTrace();
				}
	    	};
	    }.start();
	}
}
