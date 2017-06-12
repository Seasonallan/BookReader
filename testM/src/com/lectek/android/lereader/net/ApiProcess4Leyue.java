package com.lectek.android.lereader.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import android.content.Context;
import android.text.TextUtils;

import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.lib.api.request.GenerateOrderInfo;
import com.lectek.android.lereader.lib.api.response.OrderResponse;
import com.lectek.android.lereader.lib.api.response.ResponsePlayStatusInfo;
import com.lectek.android.lereader.lib.api.response.ScoreExchangeBookResultInfo;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.net.exception.GsonResultException.GsonErrorEnum;
import com.lectek.android.lereader.lib.net.http.ApiHttpConnect;
import com.lectek.android.lereader.lib.net.request.RequestData4Leyue;
import com.lectek.android.lereader.lib.recharge.OrderInfo;
import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.JsonArrayList;
import com.lectek.android.lereader.lib.utils.ApnUtil;
import com.lectek.android.lereader.lib.utils.EncryptUtils;
import com.lectek.android.lereader.lib.utils.PkgManagerUtil;
import com.lectek.android.lereader.lib.utils.UniqueIdUtils;
import com.lectek.android.lereader.net.request.LeyueRequestMessage;
import com.lectek.android.lereader.net.response.AccountBindingInfo;
import com.lectek.android.lereader.net.response.AddBookDigestInfo;
import com.lectek.android.lereader.net.response.BookCatalog;
import com.lectek.android.lereader.net.response.BookCityClassifyResultInfo;
import com.lectek.android.lereader.net.response.BookCommentDetail;
import com.lectek.android.lereader.net.response.BookCommentInfo;
import com.lectek.android.lereader.net.response.BookDecodeInfo;
import com.lectek.android.lereader.net.response.BookGroupMarkResponse;
import com.lectek.android.lereader.net.response.BookMarkResponse;
import com.lectek.android.lereader.net.response.BookSubjectClassification;
import com.lectek.android.lereader.net.response.BookTagInfo;
import com.lectek.android.lereader.net.response.BookTypeInfo;
import com.lectek.android.lereader.net.response.CollectAddResultInfo;
import com.lectek.android.lereader.net.response.CollectCancelListResultItem;
import com.lectek.android.lereader.net.response.CollectDeleteResultInfo;
import com.lectek.android.lereader.net.response.CollectQueryResultInfo;
import com.lectek.android.lereader.net.response.CollectResultInfo;
import com.lectek.android.lereader.net.response.CommonResultInfo;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.net.response.DigestResponse;
import com.lectek.android.lereader.net.response.FeedBackInfo;
import com.lectek.android.lereader.net.response.FeedbackSelectReplyInfo;
import com.lectek.android.lereader.net.response.KeyWord;
import com.lectek.android.lereader.net.response.OnlineReadContentInfo;
import com.lectek.android.lereader.net.response.Plugin;
import com.lectek.android.lereader.net.response.RegisterInfo;
import com.lectek.android.lereader.net.response.ScoreUploadResponseInfo;
import com.lectek.android.lereader.net.response.SubjectDetailResultInfo;
import com.lectek.android.lereader.net.response.SubjectResultInfo;
import com.lectek.android.lereader.net.response.SupportCommentInfo;
import com.lectek.android.lereader.net.response.SyncDigestResponse;
import com.lectek.android.lereader.net.response.SystemBookMarkAddResponseInfo;
import com.lectek.android.lereader.net.response.SystemBookMarkGroupResponseInfo;
import com.lectek.android.lereader.net.response.SystemBookMarkResponseInfo;
import com.lectek.android.lereader.net.response.UpdateInfo;
import com.lectek.android.lereader.net.response.UserThirdLeyueInfo;
import com.lectek.android.lereader.net.response.UserThridInfo;
import com.lectek.android.lereader.net.response.WelcomeImageInfo;
import com.lectek.android.lereader.net.response.tianyi.CommonEntiyPaser;
import com.lectek.android.lereader.permanent.ApiConfig;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.permanent.RequestAction4Leyue;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;

/**
 * @description
	乐阅服务器接口 数据解析
 * @author chendt
 * @date 2013-8-23
 * @Version 1.0
 */
public class ApiProcess4Leyue implements IHttpRequest4Leyue {

    protected Context mContext;

    /**
     * 获取一个HTTP请求
     * @param this_
     * @return
     */
    public static IHttpRequest4Leyue getInstance(Context this_) {
        return new ApiProcess4Leyue();
    }

    private ApiProcess4Leyue(){
        mContext = BaseApplication.getInstance();
    }

    /**
     * 处理Gson非列表数据解析
     * @param c 对应的映射关系的解析 实体类
     * @param requestData 组建完成的RequestData
     * @return
     * @throws com.lectek.android.lereader.net.exception.GsonResultException
     */ 
	private <T extends BaseDao> T commonPaserHandle(Class<T> c, RequestData4Leyue requestData) throws GsonResultException {
        String responseData = getResponseData(requestData);
        if(responseData != null){
        	CommonEntiyPaser<T> paser = new CommonEntiyPaser<T>();
            return paser.parseCommonEntity(responseData, c);
        }else {
            throw new GsonResultException(GsonErrorEnum.CONNET_FAIL_EXCEPTION,null);
        }
    }
	
    /**
     * 处理Gson列表数据解析
     * @param requestData 组建完成的RequestData
     * @return
     * @throws GsonResultException
     */
    private <T extends BaseDao> JsonArrayList<T> commonPaseListHandle(Class<T> c, RequestData4Leyue requestData) throws GsonResultException{
        String responseData = getResponseData(requestData);
        if(responseData != null){
            CommonEntiyPaser<T> paser = new CommonEntiyPaser<T>();
            return paser.parseLeyueListEntity(responseData, c);
        }else {
            throw new GsonResultException(GsonErrorEnum.CONNET_FAIL_EXCEPTION,null);
        }
    }

    /**
     * 获取登录用户ID
     * @return
     */
    private String getUserId(){
    	UserInfoLeyue userInfo = AccountManager.getInstance().getUserInfo();
    	return userInfo == null ? "" : userInfo.getUserId();
//        return  PreferencesUtil.getInstance(mContext).getUserId();
    }

    /**
     * 获取登录用户名
     * @return
     */
    private String getUserName(){
    	UserInfoLeyue userInfo = AccountManager.getInstance().getUserInfo();
    	return userInfo == null ? "" : userInfo.getAccount();
//        return  PreferencesUtil.getInstance(mContext).getUserName();
    }

    /** 是否需要验证用户信息    */
    private boolean authorization = false;
    /** 是否需要验证用户信息    */
    public boolean isAuthorization() {
        return authorization;
    }

    /**
     * 设置手机需要验证用户签名
     */
    public IHttpRequest4Leyue setAuthorization(boolean authorization) {
        this.authorization = authorization;
        return this;
    }

    /**
     * 解析HTTP请求为字符串
     * @param requestData
     * @return
     */
    private String getResponseData(RequestData4Leyue requestData){
        ApiHttpConnect connect = ApiHttpConnect.createCustomConnection(mContext, ApiConfig.URL);
        if (authorization){
            requestData.headMessage.put("Authorization", ApiHttpConnect.genAuthorization(getUserName(),
                    PreferencesUtil.getInstance(mContext).getUserPSW())); 
        }
        return connect.submitHttpRequest(requestData);
    }

    @Override
    public ArrayList<WelcomeImageInfo> getWelcomImageInfo(String ratio, String platform, String version) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpGet.METHOD_NAME;
        requestData.action = "/client/getStartPic?ratio="+ratio+"&platform="+platform;//+"&version="+version;
        requestData.actionName = "获取客户端启动图片接口";
        return commonPaseListHandle(WelcomeImageInfo.class, requestData);
    }

    @Override
    public ScoreUploadResponseInfo updateUserScoreListNormal(String jsonScore) throws GsonResultException{
        setAuthorization(true);
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpPost.METHOD_NAME;
        requestData.sendData = LeyueRequestMessage.updateUserScoreListURL(getUserId(), jsonScore);
        requestData.action = "/score/postscore";
        requestData.actionName = "积分上传统计接口";
        return commonPaserHandle(ScoreUploadResponseInfo.class, requestData);
    }

    @Override
    public JsonArrayList<BookSubjectClassification> getBookSubjectInfo(int model,int start, int count) throws GsonResultException {
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpGet.METHOD_NAME;
        requestData.action = "/bookSubject/list/module/" + model + "?start=" + start + "&count=" + count;
        requestData.actionName = "获取书籍专题接口";
        return commonPaseListHandle(BookSubjectClassification.class, requestData);
    }

    @Override
    public JsonArrayList<KeyWord> getSearchKeyWords(String sourceType, String searchType, int start, int count) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpGet.METHOD_NAME;
        if(start == 0 && count == 0){
            requestData.action = "/search/hotwords?sourceType="+sourceType+"&searchType="+searchType;
        }else{
            requestData.action = "/search/hotwords?sourceType="+sourceType+"&searchType="+searchType+"&start="+start+"&count="+count;
        }
        requestData.actionName = "获取热词列表接口";
        return  commonPaseListHandle(KeyWord.class, requestData);
    }

    @Override
    public UserInfoLeyue getUserInfo(String userId) throws GsonResultException{
        setAuthorization(true);
        try {
            userId = URLEncoder.encode(userId + "", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpGet.METHOD_NAME;
        requestData.action = "/user/" + userId;
        requestData.actionName = "根据用户id获取用户信息接口";
        return commonPaserHandle(UserInfoLeyue.class, requestData);
    }

    @Override
    public UserInfoLeyue login(String account, String password) throws GsonResultException {
        try {
            account = URLEncoder.encode(account + "", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpPost.METHOD_NAME;
        requestData.sendData = LeyueRequestMessage.userLoginURL(account, password);
        requestData.action = "/login";
        requestData.headMessage.put("userName",account);
        requestData.headMessage.put("password",password);
        fillRequestHead(requestData);
        requestData.actionName = "登录接口";
        return commonPaserHandle(UserInfoLeyue.class, requestData);
    }
    
    /**
     * 填充登录需求的头部信息
     * @param requestData
     */
    private void fillRequestHead(RequestData4Leyue requestData) {
        requestData.headMessage.put("source", "");
        requestData.headMessage.put("releaseChannel", ApiConfig.PUBLISH_CHANNEL);
        requestData.headMessage.put("salesChannel", ApiConfig.SALE_CHANNEL);
        requestData.headMessage.put("terminalType", "");
        requestData.headMessage.put("terminalBrand", UniqueIdUtils.getTerminalBrand(mContext));
        requestData.headMessage.put("netWork", ApnUtil.getApnType(mContext));
        requestData.headMessage.put("mobileOperator", "");
        requestData.headMessage.put("location", "");
        requestData.headMessage.put("version", PkgManagerUtil.getApkVersion(mContext));
        requestData.headMessage.put("deviceId", UniqueIdUtils.getDeviceId(mContext));
        
    }

    @Override
    public boolean updateThirdAccessToken(String id,String accessToken,String refreshToken) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpPost.METHOD_NAME;
        requestData.sendData = LeyueRequestMessage.updateThirdAccessTokenURL(accessToken, refreshToken);
        requestData.action = "/user/third/" + id + "/update";
        requestData.actionName = "判断指定用户是否存在接口";
        return ((CommonResultInfo)commonPaserHandle(CommonResultInfo.class, requestData)).isSuccess(); 
    }

	@Override
	public boolean exitLog(String userId,String deviceId,String terminalBrand) throws GsonResultException{
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpPost.METHOD_NAME;
		requestData.sendData = LeyueRequestMessage.exitPostURL(userId, deviceId,terminalBrand);
		requestData.action = "/exit";
		requestData.actionName = "按两次退出按钮退出时记录登出日志";
        return ((CommonResultInfo)commonPaserHandle(CommonResultInfo.class, requestData)).isSuccess(); 
	}

	@Override
	public RegisterInfo regist(String account, String password, String nickname, String source)throws GsonResultException{
		try {
			account = URLEncoder.encode(account + "", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpPost.METHOD_NAME;
		requestData.sendData = LeyueRequestMessage.userRegistURL(account, password, nickname, source,
												ApiConfig.VERSION_NAME, ApiConfig.PUBLISH_CHANNEL,ApiConfig.SALE_CHANNEL,ApiConfig.PLATFORM_SOURCE);
		requestData.action = "/user";
		requestData.actionName = "注册接口";
		return  commonPaserHandle(RegisterInfo.class, requestData);
	}
	
	@Override
	public boolean updateUserInfo(String userId, String nickname, String userName, String password, String newPassword, String mobile, String email, String sex, String birthday, String account, String signature)throws GsonResultException{
        setAuthorization(true);
        try {
			account = URLEncoder.encode(account + "", "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpPost.METHOD_NAME;
		requestData.action = "/user/" + userId;
		requestData.sendData = LeyueRequestMessage.userInfoModifyURL(nickname, newPassword, mobile, email, sex, birthday, account, signature);
		requestData.actionName = "更新用户信息接口";
        return ((CommonResultInfo)commonPaserHandle(CommonResultInfo.class, requestData)).isSuccess();
	}

    @Override
	public ArrayList<ContentInfoLeyue> getOrderedBooks(String userId, String userName, String password,int start, int count)throws GsonResultException{
        setAuthorization(true);
        RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "/order/user/"+ userId +"/orderedBooks?start="+ start +"&count="+count;
		requestData.actionName = "已订购书籍列表接口";
        return commonPaseListHandle(ContentInfoLeyue.class, requestData);
	}
	
	@Override
	public ContentInfoLeyue getContentInfo(String bookId,String userId) throws GsonResultException{
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.sendData = LeyueRequestMessage.packageParamGetContentInfo(userId);
		requestData.action = "/book/" + bookId;
		requestData.actionName = "书籍详情";
		return commonPaserHandle(ContentInfoLeyue.class, requestData);
	}
	
	@Override
	public OrderInfo getOrderInfoLeyue(String bookId,int calType,
			String calObj, String charge,String sourceType,int version,String bookName, int purchaseType)throws GsonResultException{
        setAuthorization(true);
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpPost.METHOD_NAME;
        requestData.action = "/order/add";
        requestData.sendData = LeyueRequestMessage.
                bookOrderURL(getUserId(), bookId, null, null, null, calType, calObj, charge,
                        ApiConfig.PLATFORM_SOURCE,ApiConfig.VERSION_NAME,bookName,ApiConfig.PUBLISH_CHANNEL,ApiConfig.SALE_CHANNEL,purchaseType);
        requestData.actionName = "购买书籍接口";
        return commonPaserHandle(OrderInfo.class, requestData);
	}

	@Override
	public UpdateInfo checkUpdate() throws GsonResultException{
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.sendData = LeyueRequestMessage.checkUpdateURL(LeyueConst.CLIENT_NAME, ApiConfig.VERSION_NAME, ApiConfig.VERSION_CODE);
		requestData.action = "/client/getVersion";
		requestData.actionName = "获取最新版本接口";
		return  commonPaserHandle(UpdateInfo.class, requestData);
	}
	
	@Override
	public ResponsePlayStatusInfo responseClientPlayStatus(String tradeNo,String payPlatform,String payType
			,String payTradeType,String payGoodName,String payGoodDesc,Double paymentAmount) throws GsonResultException{
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpPost.METHOD_NAME;
		requestData.sendData = LeyueRequestMessage.responseClientPlayStatusURL(
				tradeNo, payPlatform, payType, payTradeType, payGoodName, payGoodDesc, paymentAmount);
		requestData.action = "/pay/acceptRequest";
		requestData.actionName = "客户端支付完回调服务端";
		return commonPaserHandle(ResponsePlayStatusInfo.class, requestData);
	}
	
	@Override
	public BookDecodeInfo getBookDecodeKey(String bookId,String userId) throws GsonResultException{
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.sendData = LeyueRequestMessage.responseDecodeKeyURL(ApiConfig.VERSION_CODE, userId, bookId);
		requestData.action =  "/encrypt/book/"+bookId+"/SecretKey";
		requestData.actionName = "获取书籍解密秘钥接口";
		BookDecodeInfo info = commonPaserHandle(BookDecodeInfo.class, requestData);
		info.setBookId(bookId);
		return info;
	}
	

    @Override
	public ScoreExchangeBookResultInfo getScoreExchangeBook(String bookId,int calType,String calObj, int charge,String purchaseType,
					String calObjName)throws GsonResultException{
        setAuthorization(true);
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpPost.METHOD_NAME;
        requestData.action = "/order/scoreOrder";
        requestData.sendData = LeyueRequestMessage.
                scoreExchangeBookURL(getUserId(), bookId, null, null, null, calType, calObj,
                        charge, purchaseType, ApiConfig.PLATFORM_SOURCE, ApiConfig.VERSION_NAME, getUserName(), calObjName, ApiConfig.PUBLISH_CHANNEL, ApiConfig.SALE_CHANNEL);
        requestData.actionName = "积分兌换书籍接口";
        return  commonPaserHandle(ScoreExchangeBookResultInfo.class, requestData);
	}
	
	@Override
	public UserThridInfo recordThird(String thirdId,String accessToken, 
									String refreshToken,String userId) throws GsonResultException{
		try {
			thirdId = URLEncoder.encode(thirdId, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpPost.METHOD_NAME;
		requestData.sendData = LeyueRequestMessage.getThirdUrl(thirdId, ApiConfig.TIAN_YI_REGISTER, accessToken, refreshToken
										,ApiConfig.VERSION_NAME,ApiConfig.PUBLISH_CHANNEL,ApiConfig.SALE_CHANNEL,ApiConfig.SOURCE_LEYUE,userId);
		requestData.action = "/user/third";
		requestData.actionName = "客户端第三方账号记录接口";
        fillRequestHead(requestData);
        requestData.headMessage.put("userName",thirdId);
        requestData.headMessage.put("password",thirdId);
		return commonPaserHandle(UserThridInfo.class, requestData);
	}
	
	@Override
	public UserThridInfo registByDeviceId(String uniqueId,String type) throws GsonResultException{
		try {
			uniqueId = URLEncoder.encode(uniqueId, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpPost.METHOD_NAME;
		requestData.sendData = LeyueRequestMessage.getThirdUrl(uniqueId, type, null, null
										,ApiConfig.VERSION_NAME,ApiConfig.PUBLISH_CHANNEL,ApiConfig.SALE_CHANNEL, ApiConfig.SOURCE_SURFING,null);
		requestData.action = "/user/third";
        fillRequestHead(requestData);
        requestData.headMessage.put("userName",uniqueId);
        requestData.headMessage.put("password",uniqueId);
		requestData.actionName = "客户端设备标识账号记录接口";
		return commonPaserHandle(UserThridInfo.class, requestData);
	}

	@Override
	public ArrayList<ContentInfoLeyue> getRecommendedBookByBookId(String bookId, int start, int count) throws GsonResultException{
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		if(start == 0 && count == 0){
			requestData.action = "/bookTag/relactionBooks?bookId="+bookId;
		}else{
			requestData.action = "/bookTag/relactionBooks?bookId="+bookId+"&start="+start+"&count="+count;
		}
		requestData.actionName = "获取与该书标签相关的书籍接口";
        return commonPaseListHandle(ContentInfoLeyue.class, requestData);
	}

    @Override
	public boolean addComment(String bookId, String title, String content, String userId, String commentSource, String starsNum, String nickname, String username, String password) throws GsonResultException{
        setAuthorization(true);
        RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpPost.METHOD_NAME;
		requestData.action = "/book/comment/add";
		String na = username;
		if(!TextUtils.isEmpty(nickname)){
			na = nickname;
		}
		requestData.sendData = LeyueRequestMessage.getAddBookCommentParams(bookId, title, content, userId, na, commentSource, starsNum);
		requestData.actionName = "发表评论接口";
        return ((CommonResultInfo)commonPaserHandle(CommonResultInfo.class, requestData)).isSuccess();
	}
	
	@Override
	public boolean addReplyComment(int commentId, String userId, String userAccount, String username, String content, String commentSource, String password) throws GsonResultException{
        setAuthorization(true);
        RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpPost.METHOD_NAME;
		requestData.action = "/book/comment/reply";
		requestData.sendData = LeyueRequestMessage.getReplyBookCommentParams(commentId, userId, userAccount, content, commentSource);
		requestData.actionName = "添加评论回复接口";
        return ((CommonResultInfo)commonPaserHandle(CommonResultInfo.class, requestData)).isSuccess();
	}

    @Override
	public ArrayList<BookCommentInfo> getLatestBookCommentListByBookId(String bookId, int start,int count) throws GsonResultException{
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "/book/comment/newList/" + bookId;
		requestData.sendData = LeyueRequestMessage.getBookCommentListParams(bookId, start, count);
		requestData.actionName = "获取最新评论接口";
        return commonPaseListHandle(BookCommentInfo.class, requestData);
	}

	@Override
	public SupportCommentInfo doSupportComment(int commentId) throws GsonResultException{
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpPost.METHOD_NAME;
		requestData.action =  "/book/comment/support/" + commentId;
		requestData.sendData = "commentId="+commentId;
		requestData.actionName = "获取支持评论接口";
		return commonPaserHandle(SupportCommentInfo.class, requestData);
	}
	
	@Override
	public BookCommentDetail getBookCommentDetail(String commentId, int start, int count) throws GsonResultException{
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		if(start == 0 && count == 0){
			requestData.action = "/book/comment/detail/" +commentId;
		}else{
			requestData.action = "/book/comment/detail/" +commentId+"?start="+start+"&count="+count;
		}
		requestData.actionName = "获取评论详情接口";
		return commonPaserHandle(BookCommentDetail.class, requestData);
	}
	
    @Override
	public ArrayList<BookCommentInfo> getHotBookCommentListByBookId(String bookId, int start,int count) throws GsonResultException{
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "/book/comment/usefulList/" + bookId;
		requestData.sendData = LeyueRequestMessage.getBookCommentListParams(bookId, start, count);
		requestData.actionName = "获取最有用评论接口";
        return commonPaseListHandle(BookCommentInfo.class, requestData);
	}


    @Override
    public ArrayList<CollectResultInfo> collectGetList(Integer userId,Integer start,Integer count)throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpGet.METHOD_NAME;
        requestData.action= "/book/collect/query";
        requestData.sendData = LeyueRequestMessage.getCollectGetListParames(userId, start,count);
        requestData.actionName="收藏列表接口";
        return  commonPaseListHandle(CollectResultInfo.class, requestData);
    }

    @Override
    public ArrayList<CollectCancelListResultItem> collectCancelList(String collectionIds,Integer userId)throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpPost.METHOD_NAME;
        requestData.action= "/book/collect/batchDelete";
        requestData.sendData = LeyueRequestMessage.getCollectDelListParames(collectionIds, userId);
        requestData.actionName=" 批量删除收藏接口";
        return commonPaseListHandle(CollectCancelListResultItem.class, requestData);
    }

    @Override
    public ArrayList<FeedBackInfo> getFeedBackInfoList(Integer userId, String imei,Integer start,Integer count,String lastUpdateTime) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpGet.METHOD_NAME;
        requestData.action = "/feedback/process/lists";
        requestData.sendData = LeyueRequestMessage.getFeedBackListParames(userId, imei, start, count,lastUpdateTime);
        requestData.actionName="获取意见反馈列表接口";
        return commonPaseListHandle(FeedBackInfo.class, requestData);
    }


    @Override
    public CommonResultInfo addFeedbackInfo(String account,String imei,String simCode,
                                                 String sourceType,String deviceModel,String mdnCode,String appVserion,String content,String contentType) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpPost.METHOD_NAME;
        requestData.action = "/feedback/process/add";
        requestData.sendData = LeyueRequestMessage.addFeedbackParames(getUserId(), account, imei, simCode, sourceType, deviceModel, mdnCode, appVserion, content,contentType);
        requestData.actionName ="提交新的意见反馈接口";
        return commonPaserHandle(CommonResultInfo.class, requestData);
    }

    @Override
    public boolean addReplyFeedbackInfo(String account,String imei,
                                                           String feedbackId,String content,String contentType) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpPost.METHOD_NAME;
        requestData.action = "/feedback/process/addReply";
        requestData.sendData = LeyueRequestMessage.addReplyFeedbackParames(getUserId(), account, imei,feedbackId,content,contentType);
        requestData.actionName ="回复意见反馈接口";
        return ((CommonResultInfo)commonPaserHandle(CommonResultInfo.class, requestData)).isSuccess();
    }

    @Override
    public boolean doFeedbackScore(String feedbackId,Integer score) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpPost.METHOD_NAME;
        requestData.action = "/feedback/process/score/" +feedbackId;
        requestData.sendData = LeyueRequestMessage.getFeedbackScoreParames(score);
        requestData.actionName ="意见反馈评分接口";
        return ((CommonResultInfo)commonPaserHandle(CommonResultInfo.class, requestData)).isSuccess();
    }

    @Override
    public FeedbackSelectReplyInfo getFeedbackSelect(String feedbackId) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpGet.METHOD_NAME;
        requestData.action = "/feedback/process/"+feedbackId;
        requestData.actionName ="查询意见反馈是否是关闭状态接口";
        return commonPaserHandle(FeedbackSelectReplyInfo.class, requestData);
    }

    @Override
    public OnlineReadContentInfo getOnlineReadContent(String bookid, String sequence, int width, int height) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpGet.METHOD_NAME;
        // 测试 http://112.124.13.62:8082/lereader/book/1000000028/2/bookContents?width=480&height=960
        requestData.action = "/book/"+bookid+"/"+sequence+"/bookContents?width="+width+"&height="+height;
        requestData.actionName = "获取在线阅读接口";

        OnlineReadContentInfo info = commonPaserHandle(OnlineReadContentInfo.class, requestData);
        String headerString = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
        if(!info.getBookContents().contains("<!DOCTYPE")){
            info.setBookContents(headerString+info.getBookContents());
        }
        return info;
    }


    @Override
    public ArrayList<BookCatalog> getBookCatalogList(String bookId, int start, int count) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpGet.METHOD_NAME;
        if(count == 0){
            requestData.action = "/book/" + bookId + "/cataloguesCharged?start=" + start;
        }else{
            requestData.action = "/book/" + bookId + "/cataloguesCharged?start=" + start + "&count=" + count;
        }
        requestData.actionName="获取目录列表接口(带是否收费标记)";
        return (ArrayList<BookCatalog>) commonPaseListHandle(BookCatalog.class, requestData);
    }

    @Override
    public AddBookDigestInfo addBookDigest(BookDigests digest) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpPost.METHOD_NAME;
        requestData.action = "/note/add";
        requestData.sendData = LeyueRequestMessage.getAddBookDigestParams(getUserId(), digest, null);
        requestData.actionName = "添加用户笔记接口";

        return  commonPaserHandle(AddBookDigestInfo.class, requestData);
    }

    @Override
    public boolean deleteBookDigest(String userBookTagId) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpPost.METHOD_NAME;
        requestData.action = "/note/delete/" + userBookTagId;
        requestData.sendData = LeyueRequestMessage.getDeleteBookDigestParams(userBookTagId);
        requestData.actionName = "删除用户笔记接口";
        return ((CommonResultInfo)commonPaserHandle(CommonResultInfo.class, requestData)).isSuccess();
    }

    @Override
    public boolean updateBookDigest(String userId, BookDigests digest, String noteId) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpPost.METHOD_NAME;
        requestData.action = "/note/update";
        requestData.sendData = LeyueRequestMessage.getAddBookDigestParams(userId, digest, noteId);
        requestData.actionName = "更新用户笔记接口";
        return ((CommonResultInfo)commonPaserHandle(CommonResultInfo.class, requestData)).isSuccess();
    }

    @Override
    public ArrayList<BookDigests> getBookDigestListByBookId(String bookId) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpPost.METHOD_NAME;
        requestData.action = "/note/search";
        requestData.sendData = LeyueRequestMessage.getBookDigestListParams(getUserId(), bookId, null);
        requestData.actionName = "查看用户当前书籍的所有笔记接口";
        ArrayList<DigestResponse> responses = commonPaseListHandle(DigestResponse.class, requestData);
        ArrayList<BookDigests> digests = new ArrayList<BookDigests>();
        if(responses != null){
            for (DigestResponse bookDigests : responses) {
                digests.add(bookDigests.toBookDigest());
            }
        }
        return digests;
    }

    @Override
    public ArrayList<SyncDigestResponse> syncBookDigestList(JsonArrayList<DigestResponse> bookLabels) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpPost.METHOD_NAME;
        requestData.action = "/note/sync";
        requestData.sendData = LeyueRequestMessage.getSyncBookDigestListParams(bookLabels);
        requestData.actionName = "批量同步用户笔记的接口";
        return commonPaseListHandle(SyncDigestResponse.class, requestData);
    }

    @Override
    public ArrayList<BookMark> getUserBookMark(int start, int count) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpGet.METHOD_NAME;
        requestData.action = "/userBookTag/userTags";
        requestData.sendData = LeyueRequestMessage.getBookLabelListParams(getUserId(), null, start+"", count+"");
        requestData.actionName = "获取用户所有书签接口";
        ArrayList<BookMarkResponse> infos =  (ArrayList<BookMarkResponse>) commonPaseListHandle(BookMarkResponse.class, requestData);
        if(infos == null){
            return null;
        }
        ArrayList<BookMark> res = new ArrayList<BookMark>();
        for(BookMarkResponse response : infos){
            res.add(response.toBookMark());
        }
        return res;
    }

    @Override
    public ArrayList<BookMark> getBookMarkListByBookId(String userId, String bookId) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpGet.METHOD_NAME;
        requestData.action = "/userBookTag/bookTags";
        requestData.sendData = LeyueRequestMessage.getBookLabelListParams(userId, bookId, null, null);
        requestData.actionName = "查看用户当前书籍的所有书签接口";
        ArrayList<BookMarkResponse> infos =  (ArrayList<BookMarkResponse>) commonPaseListHandle(BookMarkResponse.class, requestData);
        if(infos == null){
            return null;
        }
        ArrayList<BookMark> res = new ArrayList<BookMark>();
        for(BookMarkResponse response : infos){
            res.add(response.toBookMark());
        }
        return res;
    }

    @Override
    public BookMarkResponse addBookLabel(BookMark bookMark) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpPost.METHOD_NAME;
        requestData.action = "/userBookTag";
        requestData.sendData = LeyueRequestMessage.getAddBookLabelParams(getUserId(), bookMark);
        requestData.actionName = "添加用户书签接口";

        return(BookMarkResponse)commonPaserHandle(BookMarkResponse.class, requestData);
    }

    @Override
    public boolean deleteBookLabel(String userBookTagId) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpPost.METHOD_NAME;
        requestData.action = "/userBookTag/" + userBookTagId;
        requestData.sendData = LeyueRequestMessage.getDeleteBookLabelParams(userBookTagId);
        requestData.actionName = "删除用户书签接口";
        return ((CommonResultInfo)commonPaserHandle(CommonResultInfo.class, requestData)).isSuccess();
    }

    @Override
    public ArrayList<BookMarkResponse> syncBookMarkList(JsonArrayList<BookMarkResponse> bookLabels) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpPost.METHOD_NAME;
        requestData.action = "/userBookTag/sync";
        requestData.sendData = LeyueRequestMessage.getSyncBookLabelListParams(bookLabels);
        requestData.actionName = "批量同步用户书签的接口";
        return commonPaseListHandle(BookMarkResponse.class, requestData);
    }

    
    @Override
	public CollectQueryResultInfo colletQuery(Integer resourceId)throws GsonResultException{
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action= "/book/collect/queryBook";
		requestData.sendData = LeyueRequestMessage.getCollectQueryParames(getUserId(), resourceId);
		requestData.actionName="查询收藏状态接口";
		return (CollectQueryResultInfo)commonPaserHandle(CollectQueryResultInfo.class, requestData);
	}
    
	@Override
	public CollectAddResultInfo collectAdd(int resourceId,int resourceType,Integer groupId) throws GsonResultException{
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpPost.METHOD_NAME;
		requestData.action= "/book/collect/add";
		requestData.sendData = LeyueRequestMessage.getCollectAddParames(getUserId(), resourceId, resourceType, groupId);
		requestData.actionName="添加收藏接口";
		return commonPaserHandle(CollectAddResultInfo.class, requestData);
	}
	
	@Override
	public CollectDeleteResultInfo colletDelete(Integer collectionId)throws GsonResultException{
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpPost.METHOD_NAME;
		requestData.action= "/book/collect/delete";
		requestData.sendData = LeyueRequestMessage.getCollectDelParames(collectionId, getUserId());
		requestData.actionName="删除收藏接口";
		return commonPaserHandle(CollectDeleteResultInfo.class, requestData);
	}


    @Override
    public SystemBookMarkAddResponseInfo addSysBookMark(int groupId, BookMark bookMark, String source) throws GsonResultException{
        setAuthorization(true);
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpPost.METHOD_NAME;
        requestData.action = "/systemMark/addOrUpdate";
        requestData.sendData = LeyueRequestMessage.getAddUpdateSysMarkParams(getUserId(), bookMark, groupId, source);
        requestData.actionName = "添加或者更新系统书签";

        return commonPaserHandle(SystemBookMarkAddResponseInfo.class, requestData);
    }

    @Override
    public boolean delSysBookMark(String[] ids) throws GsonResultException{
        setAuthorization(true);
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpDelete.METHOD_NAME;
        requestData.action = "/systemMark/batchDelete";
        requestData.sendData = LeyueRequestMessage.getDelSysMarkParams(getUserId(), ids);
        requestData.actionName = "批量删除系统书签";
        return ((CommonResultInfo)commonPaserHandle(CommonResultInfo.class, requestData)).isSuccess();
    }

    @Override
    public ArrayList<SystemBookMarkResponseInfo> getSysBookMark(String source) throws GsonResultException{
        setAuthorization(true);
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpGet.METHOD_NAME;
        requestData.action = "/systemMark/query";
        requestData.sendData = LeyueRequestMessage.getGetUpdateSysMarkParams(getUserId(), source);
        requestData.actionName = "查询用户的系统书签";
        return commonPaseListHandle(SystemBookMarkResponseInfo.class, requestData);
    }

    @Override
    public ArrayList<SystemBookMarkGroupResponseInfo> getSysBookMarkGroup(String source) throws GsonResultException{
        setAuthorization(true);
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpGet.METHOD_NAME;
        requestData.action = "/systemMarkGroup/queryAll";
        requestData.sendData = LeyueRequestMessage.getGetSysMarkGroupParams(getUserId(), source);
        requestData.actionName = "查询用户的系统书签分组";
        return commonPaseListHandle(SystemBookMarkGroupResponseInfo.class, requestData);
    }

    @Override
    public SystemBookMarkAddResponseInfo addSysBookMarkGroup(String groupName) throws GsonResultException {
        setAuthorization(true);
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpPost.METHOD_NAME;
        requestData.action = "/systemMarkGroup";
        requestData.sendData = "userId="+ getUserId() +"&groupName="+groupName+"&source="+ LeyueConst.SOURCE_LEYUE;
        requestData.actionName = "添加用户的系统书签分组";
        return commonPaserHandle(SystemBookMarkAddResponseInfo.class, requestData);
    }

    @Override
    public CommonResultInfo delSysBookMarkGroup(int groupId, int isDelete) throws GsonResultException {
        setAuthorization(true);
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpDelete.METHOD_NAME;
        requestData.action = "/systemMarkGroup";
        requestData.sendData = "userId="+ getUserId() +"&groupId="+groupId
                +"&source="+ LeyueConst.SOURCE_LEYUE +"&isDelete="+ isDelete;
        requestData.actionName = "删除用户的系统书签分组";
        return commonPaserHandle(CommonResultInfo.class, requestData);
    }

    @Override
    public SystemBookMarkAddResponseInfo updateSysBookMarkGroup(int groupId, String groupName) throws GsonResultException {
        setAuthorization(true);
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpPut.METHOD_NAME;
        requestData.action = "/systemMarkGroup";
        requestData.sendData = "userId="+ getUserId() +"&groupName="+groupName +"&groupId="+groupId+"&source="+ LeyueConst.SOURCE_LEYUE;
        requestData.actionName = "更新用户的系统书签分组";
        return commonPaserHandle(SystemBookMarkAddResponseInfo.class, requestData);
    }

    @Override
    public BookGroupMarkResponse getBookMarkByGroup(int groupId, String source) throws GsonResultException {
        setAuthorization(true);
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpGet.METHOD_NAME;
        requestData.action = "/systemMarkGroup/"+groupId;
        if (TextUtils.isEmpty(source)){
            requestData.sendData = "userId="+ getUserId() +"&groupId="+groupId;
        }else{
            requestData.sendData = "userId="+ getUserId() +"&source="+source +"&groupId="+groupId;
        }
        requestData.actionName = "查询分组下的书籍信息";
        return commonPaserHandle(BookGroupMarkResponse.class, requestData);
    }


    @Override
   public ArrayList<UserThirdLeyueInfo> getThirdIdByLeYueId(String userID) throws GsonResultException{
       setAuthorization(true);
	   RequestData4Leyue requestData = new RequestData4Leyue();
       requestData.requestMethod = HttpGet.METHOD_NAME;
       requestData.action = "/user/"+ userID +"/thirds";
       requestData.sendData = LeyueRequestMessage.getGetSysMarkGroupParams(getUserId(), LeyueConst.SOURCE_TYPE);
       requestData.actionName = "用户id获取后台第三方用户信息";
       return commonPaseListHandle(UserThirdLeyueInfo.class, requestData);
   }


    @Override
	public boolean getAddOrderDetail(String userId, String bookId, String bookName, int calType, String calObj, String charge, int purchaseType, int sourceType) throws GsonResultException {
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpPost.METHOD_NAME;
		requestData.sendData = LeyueRequestMessage.getAddOrderDetailURL(userId,bookId, bookName, calType, calObj,charge, userId, purchaseType+"", sourceType, ApiConfig.VERSION);
		requestData.action =  "/order/addOrderDetail";
		requestData.actionName = "电信下单记录接口";
        return ((CommonResultInfo)commonPaserHandle(CommonResultInfo.class, requestData)).isSuccess();
	}

    @Override
    public BookTypeInfo getBookType(String bookId) throws GsonResultException{
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpGet.METHOD_NAME;
        requestData.action =  "/book/"+bookId+"/detail";
        requestData.actionName = "获取书籍展现详情接口";
        return (BookTypeInfo)commonPaserHandle(BookTypeInfo.class, requestData);
    }
    
    @Override
	public OrderResponse generateOrder(GenerateOrderInfo goi) throws GsonResultException {
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpPost.METHOD_NAME;
		requestData.sendData = LeyueRequestMessage.generateOrderURL(goi);
		requestData.action = RequestAction4Leyue.GET_GENERATE_ORDER_ACTION;
		requestData.actionName = "下单接口";
		return commonPaserHandle(OrderResponse.class, requestData);
	}

	@Override
	public OrderResponse updateOrder(Integer orderId, String orderNo) throws GsonResultException {
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpPost.METHOD_NAME;
		requestData.sendData = LeyueRequestMessage.updateOrderURL(orderId, orderNo, GetToken(orderId, orderNo));
		requestData.action = RequestAction4Leyue.GET_UPDATE_ORDER_ACTION;
		requestData.actionName = "更新接口";
		return commonPaserHandle(OrderResponse.class, requestData);
	}

	
	
	@Override
	public OrderInfo getOrderInfoById(String orderId)
			throws GsonResultException {
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = RequestAction4Leyue.ACTION_GET_ORDER_INFO_BY_ID + orderId;
		requestData.sendData = LeyueRequestMessage.getOrderInfoParames(orderId);
		requestData.actionName = "根据订单id查看订单详情接口";
		return commonPaserHandle(OrderInfo.class, requestData);
	}

    @Override
    public JsonArrayList<Plugin> getPlugins() throws GsonResultException {
        RequestData4Leyue requestData = new RequestData4Leyue();
        requestData.requestMethod = HttpGet.METHOD_NAME;
        requestData.sendData = "clientName="+ MyAndroidApplication.getInstance().getPackageName()
                +"&clientVersion="+PkgManagerUtil.getApkVersionName(MyAndroidApplication.getInstance());
        requestData.action = "/plugin/getPlugins";
        requestData.actionName = "根据客服端名称和客服端版本号获取插件列表";
        return commonPaseListHandle(Plugin.class, requestData);
    }


	private String GetToken(Integer orderId, String orderNo) {
		StringBuilder tokenSb = new StringBuilder();

		tokenSb.append("orderId=");
		tokenSb.append(orderId);

		tokenSb.append("&orderNo=");
		tokenSb.append(orderNo);

		tokenSb.append("b0a33e11d3b242a4aaf9bfa8d638f3ae");

		/*
		 * tokenSb = new StringBuilder(); tokenSb.append(
		 * "call_back_url=http://wapread.189.com&client_app_key=tonr&product_id=135000000000000217829&timestamp=1389774416792secret"
		 * );
		 */

		String token = EncryptUtils.encodeByMd5(tokenSb.toString()).toLowerCase();
		return token;
	}


	@Override
	public ArrayList<ContentInfoLeyue> getBookCitySalesTwo(int rankId,
			int start, int count) throws GsonResultException {
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "/bookRank/" + rankId + "/books";
		requestData.sendData = "start=" + start + "&count=" + count;
		requestData.actionName = "客户端获取排行榜书籍列表接口。";
		return commonPaseListHandle(ContentInfoLeyue.class,
				requestData);
	}

	@Override
	public ArrayList<BookCityClassifyResultInfo> getBookCityClassify(int parentId)
			throws GsonResultException {
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "/bookType/1/types";
		requestData.actionName = "客户端获取书籍分类信息。";
		return commonPaseListHandle(BookCityClassifyResultInfo.class, requestData);

	}

	@Override
	public ArrayList<ContentInfoLeyue> getBookCityClassifyDetail(
			int bookType, int start, int count, String isFee)
			throws GsonResultException {
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "/book/type/" + bookType;
		requestData.sendData=LeyueRequestMessage.getBookCityClassifyDetailParams(start,count,isFee);
		requestData.actionName = "客户端获取分类书籍列表接口。";
		return commonPaseListHandle(ContentInfoLeyue.class, requestData);
	}
	
	@Override
	public ArrayList<SubjectResultInfo> getBookCitySubjectInfo(
			int module, int start, int count) throws GsonResultException {
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
	    requestData.action = "/bookSubject/list/module/" + module + "?start=" + start + "&count=" + count;
	    requestData.actionName = "获取书城各页面专题接口";
		return commonPaseListHandle(SubjectResultInfo.class,
				requestData);
	}

	@Override
	public SubjectDetailResultInfo getBookCitySubjectClassifyDetail(int subjectId,int start, int count) throws GsonResultException {
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "/bookSubject/"+subjectId+"/books?";
		requestData.sendData="start="+start+"&count="+count;
		requestData.actionName = "客户端获取专题书籍列表接口。";
		return commonPaserHandle(SubjectDetailResultInfo.class,requestData);
	}

	@Override
	public ArrayList<ContentInfoLeyue> getBookCitySubjectHeavyRecommend(
			int column, int start, int count) throws GsonResultException {
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "/bookRecommend/"+column;
		requestData.sendData=LeyueRequestMessage.getBookCitySubjectHeavyRecommendParams(start,count);
		requestData.actionName = "书城页面获取重磅推荐接口。";
		return commonPaseListHandle(ContentInfoLeyue.class,requestData);

	}

	@Override
	public ArrayList<ContentInfoLeyue> getBookCityNewBookRecommend(
			int column, int start, int count) throws GsonResultException {
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "/bookRecommend/"+column;
		requestData.sendData=LeyueRequestMessage.getBookCitySubjectHeavyRecommendParams(start,count);
		requestData.actionName = "客户端获取新书抢新看接口。";
		return commonPaseListHandle(ContentInfoLeyue.class,requestData);

	}

	@Override
	public ArrayList<ContentInfoLeyue> getBookCityFreeLimitSubject(
			int start, int count) throws GsonResultException {
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "/limit/limitPrice/today/5";
		requestData.sendData=LeyueRequestMessage.getBookCityOnlyStartAndCount(start,count);
		requestData.actionName = "福利页面今日限免数据接口";
		return commonPaseListHandle(ContentInfoLeyue.class,requestData);

	}

	@Override
	public ArrayList<ContentInfoLeyue> getBookCityLatestSpecialOfferSubject(
			int start, int count) throws GsonResultException {
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "/bookRecommend/100003";
		requestData.sendData=LeyueRequestMessage.getBookCityOnlyStartAndCount(start,count);
		requestData.actionName = "最新专题接口。";
		return commonPaseListHandle(ContentInfoLeyue.class,requestData);

	}

	@Override
	public ArrayList<ContentInfoLeyue> getBookCityFreeSubject(
			int start, int count) throws GsonResultException {
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "/bookRecommend/100004";
		requestData.sendData=LeyueRequestMessage.getBookCityOnlyStartAndCount(start,count);
		requestData.actionName = "免费专题接口。";
		return commonPaseListHandle(ContentInfoLeyue.class,requestData);

	}

	@Override
	public ArrayList<ContentInfoLeyue> getBookCitySearchResult(String word,int start,int count)
			throws GsonResultException {
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "/search/books";
		requestData.sendData=LeyueRequestMessage.getBookCitySearchResult(word,start,count);
		requestData.actionName = "书籍搜索。";
		return commonPaseListHandle(ContentInfoLeyue.class,requestData);
	}

	@Override
	public ArrayList<AccountBindingInfo> getBindingAccountList(String userId)
			throws GsonResultException {
		setAuthorization(true);
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "/user/" + userId + "/bindAccounts";
		requestData.sendData=LeyueRequestMessage.getAccountBindingListParam(userId);
		requestData.actionName = "获取绑定列表接口";
		return commonPaseListHandle(AccountBindingInfo.class,requestData);
	}

	@Override
	public AccountBindingInfo saveBindingAccount(String uid, String source,
			String account, String password) throws GsonResultException {
		setAuthorization(true);
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpPost.METHOD_NAME;
		requestData.action = "/user/bindAccount";
		requestData.sendData = LeyueRequestMessage.getSaveAccountBindingParam(uid, source, account, password);
		requestData.actionName = "保存绑定用户账号接口";
		return commonPaserHandle(AccountBindingInfo.class,requestData);
	}

	@Override
	public CommonResultInfo unBindAccount(String id) throws GsonResultException {
		setAuthorization(true);
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpDelete.METHOD_NAME;
		requestData.action = "/user/bindAccount/" + id;
		requestData.sendData = LeyueRequestMessage.getUnbindAccountParam(id);
		requestData.actionName = "删除绑定账号接口";
		return commonPaserHandle(CommonResultInfo.class,requestData);
	}

	@Override
	public UserInfoLeyue uploadUserHead(String uid, String fieldPath)
			throws GsonResultException {
		setAuthorization(true);
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpPost.METHOD_NAME;
		requestData.action = "/user/icon";
		requestData.sendExtraFileData = LeyueRequestMessage.getUploadHeadParams(uid, fieldPath);
		requestData.actionName = "客服端上传和更新用户头像接口";
		return commonPaserHandle(UserInfoLeyue.class, requestData);
	}

	@Override
	public ArrayList<BookTagInfo> getBookTagsByBookId(String bookId)
			throws GsonResultException {
		RequestData4Leyue requestData = new RequestData4Leyue();
		requestData.requestMethod = HttpGet.METHOD_NAME;
		requestData.action = "/bookTag/"+bookId+"/tags";
		
		requestData.actionName = "获取与该书标签相关的书籍接口";
        return commonPaseListHandle(BookTagInfo.class, requestData);
	}
	
	
}
