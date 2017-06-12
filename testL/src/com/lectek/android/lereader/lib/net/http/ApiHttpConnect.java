package com.lectek.android.lereader.lib.net.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.text.TextUtils;

import com.lectek.android.lereader.lib.net.request.ExtraFileEntity;
import com.lectek.android.lereader.lib.net.request.RequestData4Leyue;
import com.lectek.android.lereader.lib.thread.internal.AsyncTaskManage;
import com.lectek.android.lereader.lib.thread.internal.AsyncTaskManage.IAsyncTask;
import com.lectek.android.lereader.lib.utils.EncryptUtils;
import com.lectek.android.lereader.lib.utils.FileUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;

/**
 * @description
	乐阅服务器请求处理
 * @author chendt
 * @date 2013-8-23
 * @Version 1.0
 */
public final class ApiHttpConnect {
	
	private static final String TAG = ApiHttpConnect.class.getSimpleName();

	private static final int connectionTimeout = 40*1000;
	private static final int socketTimeOut = 35*1000;
	
	private Context mContext;
	private static final int BUFFER_SIZE = 8 * 1024;

    public String serverUrl;
	private ApiHttpConnect(Context context) {
		mContext = context;
	} 
    
    public static ApiHttpConnect createCustomConnection(Context context,String url){
        ApiHttpConnect connect = new ApiHttpConnect(context);
        connect.serverUrl = url;
        return connect;
    }
    
    /**
     * 提交HTTP请求
     * @param requestData
     * @return
     */
    public String submitHttpRequest(final RequestData4Leyue requestData) {
		DefaultHttpClient httpClient = null;
		InputStream is = null;
		LogUtil.v(TAG, "URL " + serverUrl);
		LogUtil.v(TAG, "Request action " + requestData.action);
		LogUtil.v(TAG, "actionName " + requestData.actionName);
		LogUtil.v(TAG, "send data " + requestData.sendData);
		try {
			
			httpClient = HttpUtil.getDefaultHttpClient(mContext);
			
			BasicHttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, connectionTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, socketTimeOut);
			
			HttpResponse httpResponse = null;
			HttpUriRequest httpUriRequest = null;
			if (requestData.requestMethod.equals(HttpPost.METHOD_NAME)) {
				httpUriRequest = postType(requestData);
			} else if (requestData.requestMethod.equals(HttpDelete.METHOD_NAME)){
				httpUriRequest = deleteType(requestData);
			} else if (requestData.requestMethod.equals(HttpPut.METHOD_NAME)){
                httpUriRequest = putType(requestData);
            } else {
                httpUriRequest = getType(requestData);
            }
            setCoommonHeader(httpUriRequest, requestData);
			for (Header head : httpUriRequest.getAllHeaders()) {
				LogUtil.v(TAG, "header: " + head.getName() + " value "
						+ head.getValue());
			}
			final DefaultHttpClient tempHttpClient = httpClient;
			int result = AsyncTaskManage.getInstance().registerHttpTask(new IAsyncTask() {
				@Override
				public void onCancel() {
					LogUtil.i("网络请求过程监听", "执行网络中断代码 id="+requestData.hashCode());
					tempHttpClient.getConnectionManager().shutdown();
				}
			});
			LogUtil.i("网络请求过程监听", "开始请求网络--> 状态="+ (result == AsyncTaskManage.RESULT_STOP ? "线程被中断":"正常执行") +" id="+requestData.hashCode());
			if(result == AsyncTaskManage.RESULT_STOP){
				return null;
			}
			
			httpResponse = httpClient.execute(httpUriRequest);
			
			int httpCode = httpResponse.getStatusLine().getStatusCode();
			LogUtil.v(TAG, "http status code " + httpCode);
			if (httpCode != HttpStatus.SC_OK && httpCode < 10001) {
				httpUriRequest.abort();
				return null;
			}
			
			Header[] headers = httpResponse.getAllHeaders();
			for (Header head : headers) {
				LogUtil.v(TAG, "header: " + head.getName() + " value "
						+ head.getValue());
			}

			HttpEntity entity = httpResponse.getEntity();
			if (entity == null) {
				return null;
			}

			is = entity.getContent();

			LogUtil.v(TAG, "is lenght " + is.available());
			byte[] responseByteArray = new byte[BUFFER_SIZE];
			ByteArrayBuffer bab = new ByteArrayBuffer(BUFFER_SIZE);
			int line = -1;
			while ((line = is.read(responseByteArray)) != -1) {
				bab.append(responseByteArray, 0, line);
				responseByteArray = new byte[BUFFER_SIZE];
			}
			LogUtil.v(TAG, "bab length " + bab.length());
			String res = new String(bab.toByteArray(), "UTF-8");
            LogUtil.v(TAG, res);
            return res;
		} catch (Exception e) {
			LogUtil.e(TAG, "connection errs", e);
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
		return null;
	}

    /**
     * 获取post请求信息
     * @param requestData
     * @return
     * @throws UnsupportedEncodingException
     */
	private HttpPost postType(RequestData4Leyue requestData) throws UnsupportedEncodingException {
		HttpPost httpPost = null;

		StringBuilder urlSb = new StringBuilder();
		urlSb.append(serverUrl);
		if (!TextUtils.isEmpty(requestData.action)) {
			urlSb.append(requestData.action);
		}
		String url = urlSb.toString();
		LogUtil.i("post type url: " + url);
		httpPost = new HttpPost(url);
		
		AbstractHttpEntity entity = null;
		if(requestData.isUpLoadFile && FileUtil.isFileExists(requestData.sendData)){
			File file = new File(requestData.sendData);
			entity = new FileEntity(file, "binary/octet-stream");
		}
		else if(requestData.sendExtraFileData == null || requestData.sendExtraFileData.size() == 0){
			entity = new StringEntity(requestData.sendData, "UTF-8");
			entity.setContentType("application/x-www-form-urlencoded");  
		}
		else{
			entity = new ExtraFileEntity(requestData.sendExtraFileData);
		}
		
		httpPost.setEntity(entity);

		return httpPost;
	}

    /**
     * 获取put请求信息
     * @param requestData
     * @return
     * @throws UnsupportedEncodingException
     */
    private HttpEntityEnclosingRequestBase putType(RequestData4Leyue requestData) {
        StringBuilder sb = new StringBuilder();
        sb.append(serverUrl);
        if (!TextUtils.isEmpty(requestData.action)) {
            sb.append(requestData.action);
        }
        if (!TextUtils.isEmpty(requestData.sendData)) {
            sb.append("?");
            sb.append(requestData.sendData);
        }
        String url = sb.toString();
        LogUtil.i("put type url: " + url);
        HttpEntityEnclosingRequestBase httpPut = new HttpEntityEnclosingRequestBase() {
            @Override
            public String getMethod() {
                return HttpPut.METHOD_NAME;
            }
        };
        httpPut.setURI(URI.create(url));
        return httpPut;
    }
    /**
     * 获取delete请求信息
     * @param requestData
     * @return
     * @throws UnsupportedEncodingException
     */
    private HttpEntityEnclosingRequestBase deleteType(RequestData4Leyue requestData) {
        StringBuilder sb = new StringBuilder();
        sb.append(serverUrl);
        if (!TextUtils.isEmpty(requestData.action)) {
            sb.append(requestData.action);
        }
        if (!TextUtils.isEmpty(requestData.sendData)) {
            sb.append("?");
            sb.append(requestData.sendData);
        }
        String url = sb.toString();
        LogUtil.i("delete type url: " + url);
        HttpEntityEnclosingRequestBase httpDelete = new HttpEntityEnclosingRequestBase() {
            @Override
            public String getMethod() {
                return HttpDelete.METHOD_NAME;
            }
        };
        httpDelete.setURI(URI.create(url));;
        return httpDelete;
    }

    /**
     * 获取get请求信息
     * @param requestData
     * @return
     * @throws UnsupportedEncodingException
     */
	private HttpGet getType(RequestData4Leyue requestData) {
		StringBuilder sb = new StringBuilder();
		sb.append(serverUrl);
		if (!TextUtils.isEmpty(requestData.action)) {
			sb.append(requestData.action);
		}

        if (!TextUtils.isEmpty(requestData.sendData)) {
            sb.append("?");
            sb.append(requestData.sendData);
        }
		String url = sb.toString();
		LogUtil.i("get type url: " + url);
		HttpGet httpGet = new HttpGet(url);
		return httpGet;
	}

    /**
     * 设置头部授权信息
     * @param httpUriRequest
     * @param requestData
     */
	private void setCoommonHeader(HttpUriRequest httpUriRequest, RequestData4Leyue requestData){
		if(requestData.headMessage != null && !requestData.headMessage.isEmpty()){
            Set<Map.Entry<String, String>> set = requestData.headMessage.entrySet();
            for (Map.Entry<String, String> entry: set){
                httpUriRequest.setHeader(entry.getKey(), entry.getValue());
            }
		}
	}
	
	public static String genAuthorization(String username,String password) {
//		LogUtil.v(TAG, "userName:"+username);
//		LogUtil.v(TAG, "password:"+password);
		String authorization = "";
//		try {
//			byte[] token = (username + ":" + MD5.getMD5(password.getBytes())).getBytes("utf-8");
//			authorization = "Basic " + new String(HttpUtil.base64Encode(token));
//			
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		authorization = "default ";
		return authorization;
	}
	
	/**根据服务器新接口规则 生成新的授权串*/
	public static String genAuthorizationByBase643DES(String userId, String purchaseType, String bookId,int calType  
			,String calObj ,String charge,String purchaser,int sourceType,String 
			version,String key) {
		String authorization = "";
		StringBuilder sb = new StringBuilder();
		sb.append(userId);
		sb.append(purchaseType);
		sb.append(bookId);
		sb.append(calType);
		sb.append(calObj);
		sb.append(charge);
		sb.append(purchaser);
		sb.append(sourceType);
		sb.append(version);
		sb.append(key);
		LogUtil.v(TAG, "Authorization before encryptBase643DES " + sb.toString());
		authorization = EncryptUtils.encryptBase643DES(sb.toString(), key);
		LogUtil.v(TAG, "authorization " + authorization);
		return authorization;
	}
	
	
	/**根据服务器新接口规则 生成新的授权串*/
	public static String genAuthorizationByBase643DES(String userId,  String bookId,int calType  
			,String calObj ,String charge,String purchaser,int sourceType,String 
			version,String key) {
		String authorization = "";
		StringBuilder sb = new StringBuilder();
		sb.append(userId);
		sb.append(bookId);
		sb.append(calType);
		sb.append(calObj);
		sb.append(purchaser);
		sb.append(charge);
		sb.append(sourceType);
		sb.append(version);
		sb.append(key);
		LogUtil.v(TAG, "Authorization before encryptBase643DES " + sb.toString());
		authorization = EncryptUtils.encryptBase643DES(sb.toString(), key);
		LogUtil.v(TAG, "authorization " + authorization);
		return authorization;
	}
	
	/**根据服务器新接口规则 生成新的授权串 下单*/
	public static String genAuthorizationByBase643DES(String userId, String bookId, String volumnId, String chapterId,
			String channelId, int calType  
			,String calObj ,String charge,String purchaser,int sourceType,String 
			version,String key) {
		String authorization = "";
		StringBuilder sb = new StringBuilder();
		sb.append(userId);
		sb.append(bookId);
		sb.append(volumnId);
		sb.append(chapterId);
		sb.append(channelId);
		sb.append(calType);
		sb.append(calObj);
		sb.append(charge);
		sb.append(purchaser);
		sb.append(sourceType);
		sb.append(version);
		sb.append(key);
		LogUtil.v(TAG, "Authorization before encryptBase643DES " + sb.toString());
		authorization = EncryptUtils.encryptBase643DES(sb.toString(), key);
		LogUtil.v(TAG, "authorization " + authorization);
		return authorization;
	}


}