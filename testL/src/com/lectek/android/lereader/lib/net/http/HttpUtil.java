package com.lectek.android.lereader.lib.net.http;

import android.content.Context;
import android.text.TextUtils;

import com.lectek.android.lereader.lib.utils.ApnUtil;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * Created by Administrator on 14-5-29.
 */
public class HttpUtil {

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

    public static HttpGet getHttpGet(String url) {
        return new HttpGet(url);
    }

}
