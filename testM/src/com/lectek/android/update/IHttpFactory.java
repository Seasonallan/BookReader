package com.lectek.android.update;

import org.apache.http.HttpResponse;

import android.content.Context;
/**
 * HTTP请求构造工厂
 * @author lyw
 */
public interface IHttpFactory {
	/**
	 * 构造一个Http请求
	 * @param context
	 * @param url
	 * @return
	 */
	public IHttpController createHttp(Context context,String url);
	/**
	 * 代表一个Http请求
	 * @author Administrator
	 *
	 */
	public interface IHttpController{
		/**
		 * 中断请求
		 */
		public void shutdown();
		/**
		 * 执行请求
		 * @return
		 */
		public HttpResponse execute() throws Exception; 
	}
}
