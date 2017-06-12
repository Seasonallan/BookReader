package com.lectek.android.lereader.permanent;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.account.thirdPartApi.net.ClientHttpRequest;
import com.lectek.android.lereader.lib.utils.IProguardFilterOnlyPublic;
import com.lectek.android.lereader.net.openapi.ApiProcess4TianYi;


public class ThirdPartLoginConfig {
	public static final String EXTRA_TYPE = "type";
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_QQ = 1;
	public static final int TYPE_SINA = TYPE_QQ + 1;
	public static final int TYPE_TY = TYPE_SINA + 1;
	
	public static final int[] UI_TITLE = new int[]{0, R.string.tencent, R.string.sina_weibo, R.string.tianyi};
	
	/**
	 * QQ配置
	 */
	public static class QQConfig implements IProguardFilterOnlyPublic{
		
		public static final String Extra_OpenID = "OpenID";
		public static final String Extra_NickName = "NickName";
		
		private static final String CLIENT_ID = "2100025417";
		public static final String REDIRECT_URI = "auth://tauth.qq.com/";
		public static final String AUTHORIZE_URL = String.format("https://graph.qq.com/oauth2.0/authorize?response_type=token&client_id=%s&redirect_uri=%s&scope=%s", CLIENT_ID, REDIRECT_URI, "");
		
		public static String getAccessTokenUrl(String accessToken) {
			return String.format("https://graph.qq.com/oauth2.0/me?access_token=%s", accessToken);
		}
		
		public static String getUserInfoUrl(String accessToken, String openId) {
			return String.format("https://graph.qq.com/user/get_user_info?access_token=%s&oauth_consumer_key=%s&openid=%s&format=json",
									accessToken, CLIENT_ID, openId);
		}
		
		public static String getAccessToken(String url) {
			ArrayList<String> list = new ArrayList<String>();
		    String tmp = url;
		    if (tmp.startsWith(REDIRECT_URI + "?#"))
		    	tmp = tmp.substring(tmp.indexOf('#') + 1);
		    else {
		    	tmp = tmp.substring(tmp.indexOf('?') + 1);
		    }
		    String[] arr = tmp.split("&");
		    HashMap<String, String> res = new HashMap<String, String>();
		    for (String item : arr) {
		    	String[] data = item.split("=");
		    	res.put(data[0], data[1]);
		    	list.add(data[1]);
		    }
		    return (String)res.get("access_token");
		}
	}
	
	/*
	 * Sina配置
	 */
	
	public static class SinaConfig implements IProguardFilterOnlyPublic {
		
		public static final String Extra_Access_Token = "access_token";
		public static final String Extra_UID = "uid";
		public static final String Extra_Nick_Name = "nick_name";
		
		public static final String APP_KEY = "1361622183";
//		private static final String CONSUMER_SECRET = "a27755b9903977c8108dd3c76aa3a540";
		public static final String REDIRECT_URI = "http://www.sina.com";
		public static String SERVER = "https://api.weibo.com/2/";
		private static String URL_OAUTH2_ACCESS_AUTHORIZE = "https://api.weibo.com/oauth2/authorize";
		public static final String WEIBO_SIGNATURE = "30820295308201fea00302010202044b4ef1bf300d"
	            + "06092a864886f70d010105050030818d310b300906035504061302434e3110300e0603550408130"
	            + "74265694a696e673110300e060355040713074265694a696e67312c302a060355040a132353696e"
	            + "612e436f6d20546563686e6f6c6f677920284368696e612920436f2e204c7464312c302a0603550"
	            + "40b132353696e612e436f6d20546563686e6f6c6f677920284368696e612920436f2e204c746430"
	            + "20170d3130303131343130323831355a180f32303630303130323130323831355a30818d310b300"
	            + "906035504061302434e3110300e060355040813074265694a696e673110300e0603550407130742"
	            + "65694a696e67312c302a060355040a132353696e612e436f6d20546563686e6f6c6f67792028436"
	            + "8696e612920436f2e204c7464312c302a060355040b132353696e612e436f6d20546563686e6f6c"
	            + "6f677920284368696e612920436f2e204c746430819f300d06092a864886f70d010101050003818"
	            + "d00308189028181009d367115bc206c86c237bb56c8e9033111889b5691f051b28d1aa8e42b66b7"
	            + "413657635b44786ea7e85d451a12a82a331fced99c48717922170b7fc9bc1040753c0d38b4cf2b2"
	            + "2094b1df7c55705b0989441e75913a1a8bd2bc591aa729a1013c277c01c98cbec7da5ad7778b2fa"
	            + "d62b85ac29ca28ced588638c98d6b7df5a130203010001300d06092a864886f70d0101050500038"
	            + "181000ad4b4c4dec800bd8fd2991adfd70676fce8ba9692ae50475f60ec468d1b758a665e961a3a"
	            + "edbece9fd4d7ce9295cd83f5f19dc441a065689d9820faedbb7c4a4c4635f5ba1293f6da4b72ed3"
	            + "2fb8795f736a20c95cda776402099054fccefb4a1a558664ab8d637288feceba9508aa907fc1fe2"
	            + "b1ae5a0dec954ed831c0bea4";
//		private static final String CONST_HMAC_SHA1 = "HmacSHA1";
//		private static final String CONST_SIGNATURE_METHOD = "HMAC-SHA1";
//		private static final String CONST_OAUTH_VERSION = "1.0";
	    
	    public static String getAuthorizeUrl(){
			Bundle parameters = new Bundle();
			parameters.putString("client_id", APP_KEY);
			parameters.putString("response_type", "token");
			parameters.putString("redirect_uri", REDIRECT_URI);
			parameters.putString("display", "mobile");
			String url = URL_OAUTH2_ACCESS_AUTHORIZE + ClientHttpRequest.encodeUrl(parameters);
			return url;
		}
	}
	
	public static class TYConfig implements IProguardFilterOnlyPublic {
		public static final String Extra_AccessToken = "access_token";
		public static final String Extra_UserID = "user_id";
		public static final String Extra_RefreshToken = "refresh_token";
		/**
		 * 如果当前绑定的天翼帐号不是正在登录的天翼帐号，是否显示切换提示
		 */
		public static final String Extra_ShowSwitchTip = "ShowSwitchTip";
		
		public static final String REDIRECT_URI = "http://115.29.171.102:8083/lereaderplatform/oauth/callback";
		private static final String AUTHORIZE_URL_FORMAT = "%soauth/authorize?client_id=%s&response_type=token&redirect_uri=%s";
		
		public static String getAuthorizeUrl(){
	        return String.format(AUTHORIZE_URL_FORMAT, ApiConfigTY.URL_TianYi_Host, ApiConfigTY.TY_CLIENT_ID, REDIRECT_URI);
		}
	}
}
