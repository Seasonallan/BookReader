package com.lectek.android.lereader.account.thirdPartApi.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.os.Bundle;

import com.lectek.android.lereader.lib.utils.HttpUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;

public class ClientHttpRequest {
	
	 private static final String TAG = ClientHttpRequest.class.getSimpleName();
	 private static final int SET_CONNECTION_TIMEOUT = 5000;
	 private static final int SET_SOCKET_TIMEOUT = 10000;
	 
	 public static String encodeUrl(Bundle parameters) {
	    if (parameters == null) {
	    	return "";
	    }
	    StringBuilder sb = new StringBuilder();
	    boolean first = true;
	    for (String key : parameters.keySet()) {
	    	if (first){ 
	    		first = false;
	    		sb.append("?");
	    	} else {
	    		sb.append("&");
	    	}
	    	sb.append(URLEncoder.encode(key) + "=" + URLEncoder.encode(parameters.getString(key)));
	    }
	    return sb.toString();
	}

	public static String openUrl(String url, String method, Bundle params) throws Exception{
	 	String result = "";
	    HttpClient client = getHttpClient();
	    HttpUriRequest request = null;
	    ByteArrayOutputStream bos = null;
	    if (method.equals("GET")){
	    	url = url + encodeUrl(params);
	    	HttpGet get = new HttpGet(url);
	    	request = get;
	    }else if (method.equals("POST")) {
	    	HttpPost post = new HttpPost(url);
			request = post;
			byte[] data = null;
			String _contentType = params.getString("content-type");
			bos = new ByteArrayOutputStream();
		    if(_contentType != null){
		        params.remove("content-type");
		        post.setHeader("Content-Type", _contentType);
		    }
		    else{
		        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		    }
			String postParam = HttpUtil.encodeParameters(params);
			data = postParam.getBytes("UTF-8");
			bos.write(data);
			data = bos.toByteArray();
			bos.close();
			ByteArrayEntity formEntity = new ByteArrayEntity(data);
			post.setEntity(formEntity);
        } 
	    HttpResponse response = client.execute(request);
	    StatusLine status = response.getStatusLine();
	    int statusCode = status.getStatusCode();
	    LogUtil.v(TAG, "statusCode:"+statusCode);
	    if (statusCode != HttpStatus.SC_OK && statusCode < 10001) {
	    	request.abort();
	    	throw new IOException("Http statusCode:" + statusCode);
		}else{
			result = readHttpResponse(response);
		}
	    return result;
	}
	 
	private static HttpClient getHttpClient(){
		KeyStore trustStore = null;
	    try {
	    	trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	    }catch (KeyStoreException e) {
	    	e.printStackTrace();
	    }
	    try {
	    	trustStore.load(null, null);
	    }catch (NoSuchAlgorithmException e) {
	    	e.printStackTrace();
	    }catch (CertificateException e) {
	    	e.printStackTrace();
	    }catch (IOException e) {
	    	e.printStackTrace();
	    }

	    org.apache.http.conn.ssl.SSLSocketFactory sf = null;
	    try {
	    	sf = new TSSLSocketFactory(trustStore);
	    }catch (KeyManagementException e) {
	    	e.printStackTrace();
	    }catch (NoSuchAlgorithmException e) {
	    	e.printStackTrace();
	    }catch (KeyStoreException e) {
	    	e.printStackTrace();
	    }catch (UnrecoverableKeyException e) {
	    	e.printStackTrace();
	    }
	    sf.setHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	    HttpParams params = new BasicHttpParams();

	    HttpConnectionParams.setConnectionTimeout(params, 10000);
	    HttpConnectionParams.setSoTimeout(params, 10000);

	    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	    HttpProtocolParams.setContentCharset(params, "UTF-8");

	    SchemeRegistry registry = new SchemeRegistry();
	    registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	    registry.register(new Scheme("https", sf, 443));

	    ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	    HttpConnectionParams.setConnectionTimeout(params, SET_CONNECTION_TIMEOUT);
	    HttpConnectionParams.setSoTimeout(params, SET_SOCKET_TIMEOUT);
	    HttpClient client = new DefaultHttpClient(ccm, params);
	    return client;
	}
	
	private static String readHttpResponse(HttpResponse response)
		    throws IllegalStateException, IOException{
		if(response == null){
			return null;
		}
		String result = "";
		HttpEntity entity = response.getEntity();

		InputStream inputStream = entity.getContent();
	    ByteArrayOutputStream content = new ByteArrayOutputStream();

	    Header header = response.getFirstHeader("Content-Encoding");
	    if ((header != null) && (header.getValue().toLowerCase().indexOf("gzip") > -1)) {
	    	inputStream = new GZIPInputStream(inputStream);
	    }

	    int readBytes = 0;
	    byte[] sBuffer = new byte[512];
	    while ((readBytes = inputStream.read(sBuffer)) != -1) {
	    	content.write(sBuffer, 0, readBytes);
	    }
	    result = new String(content.toByteArray());
	    return result;
	}
	
	private static class TSSLSocketFactory extends org.apache.http.conn.ssl.SSLSocketFactory{
	    SSLContext sslContext = SSLContext.getInstance("TLS");
	    public TSSLSocketFactory(KeyStore truststore)
	    	throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException{
	    	super(truststore);

	    	TrustManager tm = new X509TrustManager(){

				@Override
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}
				@Override
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
	    	};
	    	this.sslContext.init(null, new TrustManager[] { tm }, null);
	    }

	    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
	      throws IOException, UnknownHostException{
	    	return this.sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
	    }

	    public Socket createSocket() throws IOException{
	    	return this.sslContext.getSocketFactory().createSocket();
	    }
	}
}	
