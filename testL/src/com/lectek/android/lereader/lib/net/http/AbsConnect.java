package com.lectek.android.lereader.lib.net.http;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.text.TextUtils;

import com.lectek.android.lereader.lib.utils.ApnUtil;

public abstract class AbsConnect {
	//request
	protected static final String HEADER_REQUEST_AUTHORIZATION = "Authorization";
	// request
	protected static final String HEADER_REQUEST_CLIENT_AGENT = "Client-Agent";
	// request
	protected static final String HEADER_REQUEST_X_UP_CALLING_LINE_ID = "x-up-calling-line-id";
	protected static final String HEADER_REQUEST_PHONE_NUMBER = "phone-number";
	// request
	protected static final String HEADER_REQUEST_USER_ID = "user-id";
	// request
	protected static final String HEADER_REQUEST_API_VERSION = "APIVersion";
	// option
	protected static final String HEADER_REQUEST_CONTENT_TYPE = "Content-Type";
	// option
	protected static final String HEADER_REQUEST_ACCEPT_CHARSET = "Accept-Charset";
	// option
	protected static final String HEADER_REQUEST_COOKIE = "Cookie";
	// request
	protected static final String HEADER_REQUEST_ACTION = "Action";

	protected static final String HEADER_REQUEST_X_ONLINE_HOST = "X-Online-Host";

	protected static final String HEADER_REQUEST_USER_AGENT = "User-Agent";

	public static final String HEADER_REQUEST_X_REFERRED = "x-referred";

	protected static final String HEADER_REQUEST_USER_TYPE = "userType";
	//option
	protected static final String HEADER_REQUEST_GUEST_ID = "guest-id";
	//option
	protected static final String HEADER_REQUEST_CLIENT_IMSI = "client-imsi";
	
	protected static final String HEADER_REQUEST_PATH = "from_tag";

	// option
	protected static final String HEADER_RESPONSE_CONTENT_ENCODING = "Content-Encoding";
	// request
	protected static final String HEADER_RESPONSE_RESULT_CODE = "result-code";
	// option
	protected static final String HEADER_RESPONSE_CONTENT_TYPE = "Content-Type";
	// option
	protected static final String HEADER_RESPONSE_SET_COOKIE = "Set-Cookie";
	// request
	protected static final String HEADER_RESPONSE_API_VERSION = "APIVersion";
	// request
	protected static final String HEADER_RESPONSE_TIMESTAMP = "TimeStamp";

	protected static final String HEADER_RESPONSE_X_CLIENT_TYPE = "X-ClientType";

	public static final int CONNET_TYPE_X_ONLINE_HOST = 1;
	public static final int CONNET_TYPE_NORMAL = 2;

	protected static final String API_VERSION = "1.0.0";
	protected static final String ACCEPT_CHARSET = "utf-8";
	protected static final String USER_AGENT = "Openwave";
	public static final String X_REFERRED = "10.118.156.56";
	
	private static final int TIME_OUT_CON = 40 * 1000;
	private static final int TIME_OUT_SO = 35 * 1000;

	public static DefaultHttpClient getDefaultHttpClient(Context context) {

		DefaultHttpClient httpClient = getHttpClient();

		System.getProperties().remove("proxySet");
    	System.getProperties().remove("http.proxyHost");
    	System.getProperties().remove("http.proxyPort");
		if (!ApnUtil.isWifiWork(context)) {
			// 设置代理。CDMA的CTWAP代理是10.0.0.200，端口80.移动的CMWAP代理是10.0.0.172，端口80
			String proxyAdd = null;
			if (ApnUtil.isCtwap(context)) {// 是否CTWAP
				proxyAdd = "10.0.0.200";
			} else if (ApnUtil.isCmwap(context) || ApnUtil.isUniwap(context)) {// 是否CMWAP或UNIWAP
				proxyAdd = "10.0.0.172";
			}
			if (!TextUtils.isEmpty(proxyAdd)) {
				HttpHost proxy = new HttpHost(proxyAdd, 80);
				httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
		}

		return httpClient;
	}

	public static DefaultHttpClient getHttpClient() {
		// 创建 HttpParams 以用来设置 HTTP 参数（这一部分不是必需的）
		HttpParams params = new BasicHttpParams();
		// 设置连接超时和 Socket 超时，以及 Socket 缓存大小
		HttpConnectionParams.setConnectionTimeout(params, TIME_OUT_CON);
		HttpConnectionParams.setSoTimeout(params, TIME_OUT_SO);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		// 设置重定向，缺省为 true
		HttpClientParams.setRedirecting(params, true);

		DefaultHttpClient httpClient = new DefaultHttpClient(params);
		return httpClient;
	}
	
	public HttpURLConnection getURLConnection(Context context, URL url) throws Exception{
		// 创建一个 URL 连接，如果有代理的话可以指定一个代理。
		Proxy proxy = null;
		if (!ApnUtil.isWifiWork(context)) {
			if (ApnUtil.isCtwap(context)) {// 是否CTWAP
				proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.200", 80));
			}
		}
		HttpURLConnection connection = null;
		if(proxy != null){
			connection = (HttpURLConnection) url.openConnection(proxy);
		}else{
			connection = (HttpURLConnection) url.openConnection();
		}
//		URLConnection connection = url.openConnection();
		// 对于 HTTP 连接可以直接转换成 HttpURLConnection，这样就可以使用一些 HTTP 连接特定的方法，如
		// setRequestMethod() 等:
		// HttpURLConnection connection =
		// (HttpURLConnection)url.openConnection(Proxy_yours);

		// 在开始和服务器连接之前，可能需要设置一些网络参数
		connection.setConnectTimeout(TIME_OUT_CON);
		connection.setDoOutput(true);// 使用 URL 连接进行输出
		connection.setDoInput(true);// 使用 URL 连接进行输入
		connection.setUseCaches(true);// 忽略缓存
		connection.setRequestMethod("GET");// 设置URL请求方法

		return connection;
	}

}