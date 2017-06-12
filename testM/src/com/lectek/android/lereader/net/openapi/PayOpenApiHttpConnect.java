package com.lectek.android.lereader.net.openapi;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;

import com.lectek.android.lereader.lib.net.http.AbsConnect;
import com.lectek.android.lereader.lib.utils.LogUtil;

/**
 * @author mingkg21
 * @email  mingkg21@gmail.com
 * @date   2013-8-23
 */
public final class PayOpenApiHttpConnect extends AbsConnect {
	
	private static final String TAG = PayOpenApiHttpConnect.class.getSimpleName();

	private Context context;

	private static final int BUFFER_SIZE = 8 * 1024;

	public PayOpenApiHttpConnect(Context context) {
		this.context = context;
	}
	

	public String pay(String url) {
		String responseData = null;
		DefaultHttpClient httpClient = null;
		InputStream is = null;
		LogUtil.i(TAG, "URL " + url);
		try {
			httpClient = getDefaultHttpClient(context);

			HttpResponse httpResponse = null;
			HttpUriRequest httpUriRequest = null;
			httpUriRequest = new HttpPost(url);
			LogUtil.i(TAG, "connect type: request header start");
			LogUtil.i(TAG, "URL: " + httpUriRequest.getURI());
			Header[] hs = httpUriRequest.getAllHeaders();
			for (Header header : hs) {
				LogUtil.i(TAG, header.getName() + ": " + header.getValue());
			}
			LogUtil.i(TAG, "connect type: request header end");
			httpResponse = httpClient.execute(httpUriRequest);
			int httpCode = httpResponse.getStatusLine().getStatusCode();
			LogUtil.i(TAG, "http status code " + httpCode);
			if (httpCode != HttpStatus.SC_OK) {
				httpUriRequest.abort();
				return null;
			}

			Header[] headers = httpResponse.getAllHeaders();
			for (Header head : headers) {
				LogUtil.i(TAG, "header: " + head.getName() + " value "
						+ head.getValue());
			}

			HttpEntity entity = httpResponse.getEntity();
			if (entity == null) {
				return null;
			}
			is = entity.getContent();

			LogUtil.i(TAG, "is lenght " + is.available());
			byte[] responseByteArray = new byte[BUFFER_SIZE];
			ByteArrayBuffer bab = new ByteArrayBuffer(BUFFER_SIZE);
			int line = -1;
			while ((line = is.read(responseByteArray)) != -1) {
				bab.append(responseByteArray, 0, line);
				responseByteArray = new byte[BUFFER_SIZE];
			}
			LogUtil.i(TAG, "bab length " + bab.length());
			responseData = new String(bab.toByteArray());

		} catch (Exception e) {
			LogUtil.e(TAG, e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				LogUtil.e("close is", e);
			}
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
		return responseData;
	}

}