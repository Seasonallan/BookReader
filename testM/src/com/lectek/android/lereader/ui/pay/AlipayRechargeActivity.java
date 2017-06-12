package com.lectek.android.lereader.ui.pay;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import android.webkit.WebView;

import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.openapi.ApiProcess4TianYi;
import com.lectek.android.lereader.net.openapi.PayOpenApiHttpConnect;
import com.lectek.android.lereader.permanent.ApiConfigTY;
import com.lectek.android.lereader.storage.dbase.TianYiUserInfo;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.common.BaseWebViewActivity;
import com.lectek.android.lereader.utils.MD5;

/** 支付宝充值界面
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-9-4
 */
public class AlipayRechargeActivity extends BaseWebViewActivity {
	
	public static final String FINISH_URL = "http://wap.tyread.com/";
	
	public static final int REQUEST_CODE = 0x11;
	public static final int RECHARGE_SUCCESS = 0x22;

	@Override
	protected String getContentUrl() {
		return alipay();
	}


    public String alipay(){
        StringBuilder sb = new StringBuilder();
        sb.append(ApiConfigTY.URL);
        sb.append("alipay_input.htm?");

        HashMap<String, String> parmas = new HashMap<String, String>();
        parmas.put("redirect_url", "http://wapread.189.cn");
        parmas.put("client_id", ApiConfigTY.TY_CLIENT_ID);
        String userId = PreferencesUtil.getInstance(this).getUserId();
        TianYiUserInfo tyInfo = AccountManager.getInstance().getTYAccountInfo(userId);
        if(tyInfo != null) {
        	parmas.put("user_id",  tyInfo.getUserId().replace(" ", ""));
        }
        parmas.put("token", getToken(parmas));
        sb.append(getParmas(parmas));
        return sb.toString();
    }

    private String getToken(Map<String, String> parmas){
        StringBuilder tokenSb = new StringBuilder();
        ArrayList<String> keys = new ArrayList<String>(parmas.keySet());
        Collections.sort(keys);
        int size = keys.size();
        for (int i = 0; i < size; i++) {
            String key = keys.get(i);
            tokenSb.append(key);
            tokenSb.append("=");
            if (!TextUtils.isEmpty(parmas.get(key))) {
                tokenSb.append(parmas.get(key));
                if (i != (size - 1)) {
                    tokenSb.append("&");
                }
            }
        }
        tokenSb.append(ApiConfigTY.CLIENT_SECRET);
        String tokenStr = tokenSb.toString();
        String result = "";
        try {
            byte[] data = MessageDigest.getInstance("MD5").digest(
                    tokenStr.getBytes());
            result = new String(data);
            result = MD5.getMD5(tokenStr.getBytes());
        } catch (NoSuchAlgorithmException e) {
        }
        return result;
    }


    private String getParmas(Map<String, String> parmas) {
        StringBuilder tokenSb = new StringBuilder();
        ArrayList<String> keys = new ArrayList<String>(parmas.keySet());
        int size = keys.size();
        for (int i = 0; i < size; i++) {
            String key = keys.get(i);
            tokenSb.append(key);
            tokenSb.append("=");
            tokenSb.append(URLEncoder.encode(parmas.get(key)));
            if (i != (size - 1)) {
                tokenSb.append("&");
            }
        }
        String result = tokenSb.toString();
        return result;
    }

	@Override
	protected boolean shouldOverrideUrlLoading(WebView view, String url) {
		LogUtil.i("AlipayRechargeActivity", url);
		if(url.equals(FINISH_URL)){
			this.finish();
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.lectek.android.sfreader.ui.common.BaseWebViewActivity#webViewLoadUrl(android.webkit.WebView, java.lang.String)
	 */
	@Override
	protected void webViewLoadUrl(WebView webView, String url) {
		webView.postUrl(url, null);
	}

}
