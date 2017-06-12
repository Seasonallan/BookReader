package com.lectek.android.lereader.net.openapi;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import android.content.Context;
import android.text.TextUtils;

import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.lib.api.response.OrderResponseToTYRead;
import com.lectek.android.lereader.lib.api.surfing.IOrderRechargeTY;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.net.exception.GsonResultException.GsonErrorEnum;
import com.lectek.android.lereader.lib.net.http.ApiHttpConnect;
import com.lectek.android.lereader.lib.net.request.RequestData4Leyue;
import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.utils.EncryptUtils;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.openapi.TokenService.TokenParam;
import com.lectek.android.lereader.net.request.LeyueRequestMessage;
import com.lectek.android.lereader.net.response.tianyi.Chapter;
import com.lectek.android.lereader.net.response.tianyi.ChapterInfoPaser;
import com.lectek.android.lereader.net.response.tianyi.ChapterListPaser;
import com.lectek.android.lereader.net.response.tianyi.CommonEntiyPaser;
import com.lectek.android.lereader.net.response.tianyi.ContentInfo;
import com.lectek.android.lereader.net.response.tianyi.ContentInfoPaser;
import com.lectek.android.lereader.net.response.tianyi.GetContentCoverPaser;
import com.lectek.android.lereader.net.response.tianyi.OrderedPaser;
import com.lectek.android.lereader.net.response.tianyi.OrderedResult;
import com.lectek.android.lereader.net.response.tianyi.PayAfterResult;
import com.lectek.android.lereader.net.response.tianyi.PayResult;
import com.lectek.android.lereader.net.response.tianyi.PointRule;
import com.lectek.android.lereader.net.response.tianyi.PointRuleListPaser;
import com.lectek.android.lereader.net.response.tianyi.UserInfoPaser;
import com.lectek.android.lereader.permanent.ApiConfigTY;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.storage.dbase.TianYiUserInfo;
import com.lectek.android.lereader.utils.UserManager;

/**
 * @author mingkg21
 * @email  mingkg21@gmail.com
 * @date   2013-8-21
 */
public class ApiProcess4TianYi implements IHttpRequest4TianYi, IOrderRechargeTY{

    private String getResponseData(RequestData4Leyue requestData){
        return getResponseData(ApiConfigTY.URL_TianYi_api, requestData); 
    }
    
    private String getResponseData(String url, RequestData4Leyue requestData){
        if (requestData.requestMethod.equals(HttpGet.METHOD_NAME)){
            
        	if(TextUtils.isEmpty(requestData.accessToken)) {
	        	if (!TextUtils.isEmpty(UserManager.getInstance(mContext).getCurrentAccessToken())) {
	                requestData.accessToken = UserManager.getInstance(mContext).getCurrentAccessToken();
	            }else {
	                requestData.accessToken = UserManager.ACCESS_TOKEN_DEFAULT;
	            }
        	}
            StringBuffer sb = new StringBuffer();
            sb.append("client_id=");
            sb.append(ApiConfigTY.TY_CLIENT_ID);
            sb.append("&access_token=");
            sb.append(requestData.accessToken);
            if(!TextUtils.isEmpty(requestData.sendData)){
                sb.append(requestData.sendData);
            }
            requestData.sendData = sb.toString();
        }
    	 String str = ApiHttpConnect.createCustomConnection(mContext, url).submitHttpRequest(requestData);
         printResult(str);
         return str;
    }

    @Override
    public String getContentCover(String contentId, int coverSize){
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpGet.METHOD_NAME;
        requestData.action = "get_content_cover.json";
        requestData.sendData = RequestMessage.getContentCover(contentId, coverSize);
        String responseData = getResponseData(requestData);
        if(responseData != null){
            GetContentCoverPaser paser = new GetContentCoverPaser();
            return paser.paser(responseData);
        }
        return null;
    }

    @Override
    public ContentInfo getBaseContent(String contentId) {
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpGet.METHOD_NAME;
        requestData.action = "v2/get_base_content.json";
        requestData.sendData = RequestMessage.getBaseContent(contentId);

        String responseData = getResponseData(requestData);
        if (responseData != null) {
            ContentInfoPaser paser = new ContentInfoPaser();
            return paser.paser(responseData);
        }
        return null;
    }

    @Override
    public TianYiUserInfo queryUserInfo(String accessToken){
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpGet.METHOD_NAME;
        requestData.action = "query_userinfo.json";
        requestData.accessToken = accessToken;
        String responseData = getResponseData(requestData);
        if(responseData != null){
            UserInfoPaser paser = new UserInfoPaser();
            return paser.paser(responseData);
        }
        return null;
    }


    private static final String TAG = ApiProcess4TianYi.class.getSimpleName();
	
	private static ApiProcess4TianYi instance;
	
	/**话费支付:_5*/
	public static final int FEE_PRICE = 5;
	/**阅点支付:_6*/
	public static final int READPOINT_PRICE = 6;

	
	protected Context mContext;
	
	private static final String FILTER_RESULT_STR = "{\"result\":";
	private static final String FILTER_RESPONSE_STR = "{\"response\":";
	
	private ApiProcess4TianYi(Context context){
		mContext = context;
	}
	
	public static IHttpRequest4TianYi getInstance(Context context){
		if(instance == null){
			instance = new ApiProcess4TianYi(BaseApplication.getInstance());
		}
		return instance;
	}


	private void printResult(String str){
		if (LeyueConst.IS_DEBUG) {
			if(str != null){
				LogUtil.v(TAG, "result xml " + str);
			}
		}
	}

	/**
	 * 处理Gson数据解析
	 * @param c 对应的映射关系的解析 实体类
	 * @param requestData 组建完成的RequestData
	 * @return
	 * @throws GsonResultException
	 */
	private <T extends BaseDao> T commonPaserHandle(Class<T> c,RequestData4Leyue requestData) throws GsonResultException{
		String responseData = getResponseData(requestData);
		if(responseData != null){
			CommonEntiyPaser<T> paser = new CommonEntiyPaser<T>();
			return paser.parseCommonEntity(responseData, c);
		}else {
			throw new GsonResultException(GsonErrorEnum.CONNET_FAIL_EXCEPTION,null);
		}
	}
	
	/**
	 * 处理Gson数据解析
	 * @param c 对应的映射关系的解析 实体类
	 * @param requestData 组建完成的RequestData
	 * @return
	 * @throws GsonResultException
	 */
	private <T extends BaseDao> T commonPaserHandle(Class<T> c, String url, RequestData4Leyue requestData) throws GsonResultException{
		String responseData = getResponseData(url, requestData);
		if(responseData != null){
			CommonEntiyPaser<T> paser = new CommonEntiyPaser<T>();
			return paser.parseCommonEntity(responseData, c);
		}else {
			throw new GsonResultException(GsonErrorEnum.CONNET_FAIL_EXCEPTION,null);
		}
	}


	@Override
	public ArrayList<Chapter> getBookChapterList(String contentId) {
        RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "get_book_chapter_list.json";
		requestData.sendData = RequestMessage.getBookChapterList(contentId);
		String responseData = getResponseData(requestData);
		if (responseData != null) {
			ChapterListPaser paser = new ChapterListPaser();
			return paser.paser(responseData);
		}
		return null;
	}

    @Override
	public ArrayList<Chapter> getBookChapterListNew(String contentId, int start, int count) {
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "get_content_dir.json";
		requestData.sendData = RequestMessage.getBookChapterList(contentId, start, count);
		String responseData = getResponseData(requestData);
		if (responseData != null) {
			ChapterListPaser paser = new ChapterListPaser();
			return paser.paser(responseData);
		}
		return null;
	}

	@Override
	public Chapter getChapterInfo(String contentId, String chapterId) {
        RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "v3/get_chapter_info.json";
		requestData.sendData = RequestMessage.getChapterInfo(contentId, chapterId);
		String responseData = getResponseData(requestData);
		if (responseData != null) {
			ChapterInfoPaser paser = new ChapterInfoPaser();
			return paser.paser(responseData);
		}
		return null;
	}
	
	@Override
	public OrderedResult isContentOrdered(String contentId){
        RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "is_content_ordered.json";
		requestData.sendData = RequestMessage.isContentOrdered(contentId);
		String responseData = getResponseData(requestData);
		if (responseData != null) {
			OrderedPaser paser = new OrderedPaser();
			return paser.paser(responseData);
		}
		return null;
	}

	@Override
	public OrderedResult subscribeContentReadpoint(String contentId, String chapterId) throws GsonResultException {
        RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "subscribe_content_readpoint.json";
		requestData.sendData = RequestMessage.subscribeContentReadpoint(contentId, chapterId);
		requestData.actionName = "阅点点播内容接口";
		String responseData = getResponseData(requestData);
		if (responseData != null) {
			OrderedPaser paser = new OrderedPaser();
			return paser.paser(responseData);
		}
		return null;
	}
	
	@Override
	public OrderedResult subscribeMonthProductReadpoint(String productId) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "subscribe_month_product_readpoint.json";
		requestData.sendData = RequestMessage.subscribeMonthProductReadpoint(productId);
		requestData.actionName = "阅点包月接口";
		String responseData = getResponseData( requestData);
		if (responseData != null) {
			OrderedPaser paser = new OrderedPaser();
			return paser.paser(responseData);
		}
		return null;
	}

	
	@Override
	public PayResult payConfirm(String productName, int costType, String bookId, String chapterId, String colId, String userId) throws GsonResultException {
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpPost.METHOD_NAME;
        HashMap<String ,String> parmas = new HashMap<String, String>();
		String timestamp = String.valueOf(System.currentTimeMillis());
		parmas.put("client_id", ApiConfigTY.TY_CLIENT_ID);
		parmas.put("timestamp", timestamp);
		parmas.put("user_id", userId);
		if (costType == 1) {
			parmas.put("col_id", colId);
		} else if ((costType == 2) || (costType == 3)) {
			parmas.put("book_id", bookId);
			if (costType == 3) {
				parmas.put("chapter_id", chapterId);
			}
		}

		// demo v2接口Token生成逻辑
		List<TokenParam> paramList = new ArrayList<TokenParam>(4);
		paramList.add(new TokenParam("client_id", ApiConfigTY.TY_CLIENT_ID));
		if (costType == 1) {
			paramList.add(new TokenParam("col_id", colId));
		} else if ((costType == 2) || (costType == 3)) {
			paramList.add(new TokenParam("book_id", bookId));
			if (costType == 3) {
				paramList.add(new TokenParam("chapter_id", chapterId));
			}
		}
		paramList.add(new TokenParam("timestamp", timestamp));
		paramList.add(new TokenParam("user_id", userId));
		parmas.put("token", TokenService.buildToken(paramList, ApiConfigTY.CLIENT_SECRET));

		parmas.remove("user_id");
		parmas.put("user_id", URLEncoder.encode(userId));
        requestData.action = "v2/pay_confirm.json?"+getToken(parmas);
		requestData.actionName = "话费支付——发起支付接口";
        String str = getResponseData( requestData); 
		str = printAndFilterResult(str, FILTER_RESULT_STR);
		str = printAndFilterResult(str, FILTER_RESPONSE_STR);
		if (!TextUtils.isEmpty(str)) {
			CommonEntiyPaser<PayResult> paser = new CommonEntiyPaser<PayResult>();
			return paser.parseCommonEntity(str, PayResult.class); 
		} else {
			throw new GsonResultException(GsonErrorEnum.CONNET_FAIL_EXCEPTION,
					null);
		}
	}

    private String getToken(Map<String, String> parmas){
        StringBuilder tokenSb = new StringBuilder();
        ArrayList<String> keys = new ArrayList<String>(parmas.keySet());
        int size = keys.size();
        for (int i = 0; i < size; i++) {
            String key = keys.get(i);
            tokenSb.append(key);
            tokenSb.append("=");
            tokenSb.append(parmas.get(key));
            if (i != (size - 1)) {
                tokenSb.append("&");
            }
        }
        String result = tokenSb.toString();
        LogUtil.v(TAG, "result str " + result);
        return result;
    }

	/**
	 * 过滤response 内容后打印xml
	 * @param str
	 */
	private String printAndFilterResult(String str,String filterTag){
		LogUtil.v(TAG, "original result xml " + str);
		//过滤掉{"response":*****} 这一层。FIXME:最好用正则表达式过滤
		if (!TextUtils.isEmpty(str) && str.contains(filterTag)) {
			str = str.substring(str.indexOf(filterTag)+filterTag.length());
			str = str.substring(0,str.length()-1);
		}
		if(!TextUtils.isEmpty(str)){
			LogUtil.v(TAG, "filter result xml " + str);
		}
		return str;
	}

    @Override
	public PayAfterResult pay(String url) throws GsonResultException{
		PayOpenApiHttpConnect connect = new PayOpenApiHttpConnect(mContext);
		String str = connect.pay(url);
		//由于该接口成功与失败返回最外层 解析不同，这里要过滤两次。
		//成功是result{xx:xxx}。失败是response{xx:xxx}
		str = printAndFilterResult(str,FILTER_RESULT_STR);
		str = printAndFilterResult(str, FILTER_RESPONSE_STR);
		if(!TextUtils.isEmpty(str)){
			CommonEntiyPaser<PayAfterResult> paser = new CommonEntiyPaser<PayAfterResult>();
			return paser.parseCommonEntity(str, PayAfterResult.class);  
		}else {
			throw new GsonResultException(GsonErrorEnum.CONNET_FAIL_EXCEPTION, null);
		}
	}

	@Override
	public ArrayList<PointRule> getPointRuleList(){
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "get_point_rule_list.json";
		requestData.sendData = "";
		String responseData = getResponseData(requestData);
		if (responseData != null) {
			PointRuleListPaser paser = new PointRuleListPaser();
			return paser.paser(responseData);
		}
		return null;
	}

	@Override
	public OrderedResult pointConvert(String ruleId) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "point_convert.json";
		requestData.sendData = RequestMessage.getPointConvertSendData(ruleId);
		requestData.actionName = "积分兑换阅点接口";
		return (OrderedResult)commonPaserHandle(OrderedResult.class, requestData);
	}
	
	@Override
	public OrderResponseToTYRead generateOrderToTYRead(String clientAppKey, String productId, String callBackUrl)
			throws GsonResultException {
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		String url = "http://pay.tyread.com/v2/generate_order.json";

		clientAppKey = ApiConfigTY.TY_CLIENT_ID;
		long timestamp = getTimeStamp();
		String token = GetToken(callBackUrl, clientAppKey, productId, timestamp);
		requestData.sendData = LeyueRequestMessage.generateOrderToTYReadURL(clientAppKey, productId, callBackUrl, token,
				timestamp);
		// requestData.action = "/pay/tyOpen/generateOrder";
		// requestData.actionName = "下单接口";
		return (OrderResponseToTYRead) commonPaserHandle(OrderResponseToTYRead.class, url, requestData);
	}
	
	private String GetToken(String call_back_url, String client_app_key, String product_id, long timestamp) {
		StringBuilder tokenSb = new StringBuilder();

		tokenSb.append("call_back_url=");
		tokenSb.append(call_back_url);

		tokenSb.append("&client_app_key=");
		tokenSb.append(client_app_key);

		tokenSb.append("&product_id=");
		tokenSb.append(product_id);

		tokenSb.append("&timestamp=");
		tokenSb.append(timestamp);

		tokenSb.append(ApiConfigTY.CLIENT_SECRET);

		/*
		 * tokenSb = new StringBuilder(); tokenSb.append(
		 * "call_back_url=http://wapread.189.com&client_app_key=tonr&product_id=135000000000000217829&timestamp=1389774416792secret"
		 * );
		 */

		String token = EncryptUtils.encodeByMd5(tokenSb.toString()).toLowerCase();
		return token;
	}
	
	private long getTimeStamp() {
		long today = System.currentTimeMillis();
		Timestamp timestamp = new Timestamp(today);

		return timestamp.getTime();
	}
	
	
}
