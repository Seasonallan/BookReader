/*
 * ========================================================
 * ClassName:AliConfig.java* 
 * Description:
 * Copyright (C) 
 * ========================================================
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *===================================================================*
 * Revision History
 *
 * Modification                    Tracking
 * Date         Author          Number       Description of changes
 *____________________________________________________________________
 * 
 * 2014-1-17     chendt          #00000       create
 */
package com.lectek.android.LYReader.pay.alipay;

public class AliConfig {
	// 合作商户ID，以2088开头由16位纯数字组成的字符串；
	// 用签约支付宝账号登录ms.alipay.com后，在账户信息页面获取。
	public static final String PARTNER = "2088101636643891";

	// 账户ID，签约支付宝账号或卖家收款支付宝帐户用
	// 签约支付宝账号登录ms.alipay.com后，在账户信息页面获取。
	public static final String SELLER = "sales@lectek.com";

	// 商户（RSA）私钥
	public static final String RSA_PRIVATE = "MIICWwIBAAKBgQDP/VrRaGyqMhrzrIIADGlMcdVOk7jmAc4NsJkrMjwFwNnR9C0fCiykUmieIeXhpGHLZL2GNu5+PrK0HVJqMNImYO4sVa1GZHVy9CXZDGKdpjDJyJaQ/uo56gms8TQRL+I1afIWddAXWa1ZafrhM9R0lRzzV6J0nGIAuHlFIqr8oQIDAQABAoGAFFO5fX/MJvIQqkHwzppn7B2v8CcFygzhG3tn7M3TCejoINnRUxhGdqTLLrE3a08Kc7tbvnM96b6s7mdzN4ef8gz4dZHOGEkKBsRgtpBNg+UiZbXfX37Ytsda2VLIW6Al0fNc8Ws4jiXSKe2HPJQOeiFg6JCraPfTXGamXdTiG+UCQQD9SB7Dcbrzt2U7T2O8pu5NQ32Nmc2N6PrCL9QNRuWFmLnf704Ny/OOXvGlOQnipZ41DSDwniJyomZV10dHGdV7AkEA0jjL+M5OcVRYDDu90G3wEiFsWsJatubh3aTST2pL5legqaueoeRZk7y6rRbPeLW5e8xv0PmfsW3l2IzJ994FkwJAWPwqmr3tUczgCwtkXhZD6O9KQCKGHlJ5LxpApP2AjARRYYSg5qX+BsoCmerFN1S6labsKL1WGvJFg+kjiK5h2wJAJ9oskNZLeKCMsIUCzuiIgmdjqzD9EX9jgikk1XlJOjmSc5fdUsN/V8qHeRjB7BhHOXjgGbW42GIFGLBX+W1VkQJAYCwP6Ufnt3/MJ1XIwYIlrz44O4J+zFnDwsNsVu7Yu6eVySCz9ArrUmXdJxmSihKCUcYNjSNGbyaJ6udo1pWG3w==";

	// 支付宝（RSA）公钥 用签约支付宝账号登录ms.alipay.com后，在密钥管理页面获取。
	public static final String RSA_ALIPAY_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDag2N/pjDWz9qSWscrx5xzwsgxC7F1ak+VezxtTv/94fRBQwEuLcK9KVohk8aNKeDMbmF84Dz9KejWli4LHdQdTMto6tFKq1teFoKnrrbTmQkX5BYnuOJCJUxgP4h2fhWw5AQFBpn8BQVzo7FIxELmYqHJOnsF+tvNDUx1nDJaCwIDAQAB";

	public static final String ALIPAY_PLUGIN_NAME = "alipay_msp.apk";

	// 交易安全检验码，由数字和字母组成的32位字符串
	public static String key = "";

	// 商户地址：提供一个http的URL(例:http://www.partnertest.com/servlet/NotifyReceiver)，支付宝将以POST方式调用该地址。
	// 商用平台地址
	public static String NOTIFY_URL = "http://42.96.171.71:8082/lereader/alipay/consume";

	// 商品描述
	public static String BODY = "乐阅充值";
	// 商品名称
	public static String SUBJECT = "价钱充值";
}
