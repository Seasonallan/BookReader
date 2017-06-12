package com.lectek.android.lereader.account.thirdPartApi.net;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.lectek.android.lereader.lib.utils.LogUtil;


public class BaseRequestListener implements IRequestListener{
	  private static final String TAG = "BaseRequestListener";

	@Override
	public void onComplete(String paramString, Object paramObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onIOException(IOException paramIOException, Object paramObject) {
		LogUtil.i("BaseRequestListener", "Resource not found:" + paramIOException.getMessage());
	}

	@Override
	public void onFileNotFoundException(
			FileNotFoundException paramFileNotFoundException, Object paramObject) {
		LogUtil.i("BaseRequestListener", "Network Error:" + paramFileNotFoundException.getMessage());
	}
}
