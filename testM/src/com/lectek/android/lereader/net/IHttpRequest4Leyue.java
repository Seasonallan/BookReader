package com.lectek.android.lereader.net;

import java.util.ArrayList;

import com.lectek.android.lereader.lib.api.IOrderRecharge;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.storage.dbase.JsonArrayList;
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
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;

/**
 *  乐阅网络请求接口
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public interface IHttpRequest4Leyue extends IOrderRecharge{
    /**
     * 获取客户端启动图片接口
     *
     * @param ratio 分辨率，以480_600格式上传
     * @param platform 1 代表安卓 2代表IOS
     * @param version 客户端版本号 非必须
     * @return
     * @throws com.lectek.android.lereader.net.exception.GsonResultException
     */
    public ArrayList<WelcomeImageInfo> getWelcomImageInfo(String ratio, String platform, String version) throws GsonResultException;


    /**
     *  积分上传统计接口
     *  必须先进行API认证授权，成功后根据其中的json传进行积分计算，并返回
     * @param jsonScore json串
     * @return
     * @throws GsonResultException
     */
    public ScoreUploadResponseInfo updateUserScoreListNormal(String jsonScore) throws GsonResultException;

    /**
     * 获取书籍专题接口
     * @param model
     * @param start
     * @param count
     * @return
     * @throws GsonResultException
     */
    public JsonArrayList<BookSubjectClassification> getBookSubjectInfo(int model,int start, int count) throws GsonResultException ;


    /**
     * 获取热词列表接口
     * @param sourceType
     * @param searchType
     * @param start
     * @param count
     */
    public JsonArrayList<KeyWord> getSearchKeyWords(String sourceType, String searchType, int start, int count) throws GsonResultException;


    /**
     * 获取指定用户信息接口
     * @param userId 如果是用户登录，传null
     * @return
     * @throws GsonResultException
     */
    public UserInfoLeyue getUserInfo(String userId) throws GsonResultException;

    /**
     * 登录接口
     * @param account
     * @param password
     * @return
     * @throws GsonResultException
     */
    public UserInfoLeyue login(String account, String password) throws GsonResultException;
    
    /**
     * 根据后台第三方关系ID更新第三方关系记录
     * @param id	第三方表的行id
     * @param accessToken
     * @param refreshToken
     */
    public boolean updateThirdAccessToken(String id,String accessToken,String refreshToken) throws GsonResultException;

    /**
     * 按两次退出按钮退出时记录登出日志
     * @param userId
     * @param deviceId
     * @param terminalBrand
     * @return
     */
    public boolean exitLog(String userId,String deviceId,String terminalBrand) throws GsonResultException;

    /**
     * 用户注册
     * @param account
     * @param password
     * @param nickname
     * @param source
     * @throws GsonResultException
     */
    public RegisterInfo regist(String account, String password, String nickname, String source)throws GsonResultException;



    /**
     * 更新用户信息接口
     * @param userId
     * @param nickname
     * @param userName
     * @param password
     * @param newPassword
     * @return
     */
    public boolean updateUserInfo(String userId, String nickname, String userName, String password, String newPassword,
                                           String mobile, String email, String sex, String birthday, String account, String signature)throws GsonResultException;

    /**
     * 获取已订购书籍信息
     * @param userId
     * @param userName
     * @param password
     * @param start
     * @param count
     * @return
     * @throws GsonResultException
     */
    public ArrayList<ContentInfoLeyue> getOrderedBooks(String userId, String userName, String password,int start, int count)throws GsonResultException;



    /**
     * 获取书籍详情
     * @param bookId
     * @param userId
     * @return
     * @throws GsonResultException
     */
    public ContentInfoLeyue getContentInfo(String bookId,String userId) throws GsonResultException;

    /**
     * 获取最新版本信息
     * @return UpdateInfo
     */
    public UpdateInfo checkUpdate() throws GsonResultException;

    /**
     * 获取书籍解密秘钥接口
     * @param bookId
     * @param userId
     * @return
     * @throws GsonResultException
     */
    public BookDecodeInfo getBookDecodeKey(String bookId,String userId) throws GsonResultException;

    /**
     * 客户端第三方账号记录接口。如果第三方帐号已绑定则返回绑定的乐阅帐号；如果传进去的乐阅帐号Id为空则生成一个新的乐阅帐号；
     * 如果传进去的乐阅帐号不为空，则绑定；传进去的乐阅帐号已绑定不同天翼帐号则返回已绑定提示
     * @param thirdId
     * @param accessToken
     * @param refreshToken
     * @return UserThridInfo
     * @throws GsonResultException
     */
    public UserThridInfo recordThird(String thirdId,String accessToken,
                                  String refreshToken,String userId) throws GsonResultException;

    /**
     * 根据第三方id注册接口
     * @return UserThridInfo
     * @throws GsonResultException
     */
    public UserThridInfo registByDeviceId(String uniqueId,String type) throws GsonResultException;



    /**
     * 获取与该书标签相关的书籍接口
     * @param bookId
     * @param start
     * @param count
     * @return
     * @throws GsonResultException
     */
    public ArrayList<ContentInfoLeyue> getRecommendedBookByBookId(String bookId, int start, int count) throws GsonResultException;

    
    /**
     * 获取书籍的标签
     */
    public ArrayList<BookTagInfo> getBookTagsByBookId(String bookId) throws GsonResultException;
    /**
     * 添加评论
     * @param bookId
     * @param title
     * @param content
     * @param userId
     * @param commentSource
     * @param starsNum
     * @param nickname
     * @param username
     * @param password
     * @return
     * @throws GsonResultException
     */
    public boolean addComment(String bookId, String title, String content, String userId, String commentSource, String starsNum, String nickname, String username, String password) throws GsonResultException;

    /**
     * 添加评论回复接口
     * @param commentId
     * @param userId
     * @param username
     * @param content
     * @param commentSource
     * @param password
     * @return
     * @throws GsonResultException
     */
    public boolean addReplyComment(int commentId, String userId, String userAccount, String username, String content, String commentSource, String password) throws GsonResultException;

    /**
     * 获取最新评论
     * @param bookId
     * @param start
     * @param count
     * @return
     * @throws GsonResultException
     */
    public ArrayList<BookCommentInfo> getLatestBookCommentListByBookId(String bookId, int start,int count) throws GsonResultException;

    /**
     * 获取支持评论接口
     * @param commentId
     * @return
     * @throws GsonResultException
     */
    public SupportCommentInfo doSupportComment(int commentId) throws GsonResultException;


    /**
     * 获取评论详情接口
     * @param commentId
     * @param start
     * @param count
     * @return
     * @throws GsonResultException
     */
    public BookCommentDetail getBookCommentDetail(String commentId, int start, int count) throws GsonResultException;


    /**
     * 获取最有用评论接口
     * @param bookId
     * @param start
     * @param count
     * @return
     * @throws GsonResultException
     */
    public ArrayList<BookCommentInfo> getHotBookCommentListByBookId(String bookId, int start,int count) throws GsonResultException;


    /**
     * 收藏列表接口
     * @param userId
     * @param start
     * @param count
     * @return
     * @throws GsonResultException
     */
    public ArrayList<CollectResultInfo> collectGetList(Integer userId,Integer start,Integer count)throws GsonResultException;

    /**
     * 批量删除收藏接口
     * @param collectionIds
     * @param userId
     * @return
     * @throws GsonResultException
     */

    public ArrayList<CollectCancelListResultItem> collectCancelList(String collectionIds,Integer userId)throws GsonResultException;

    /**
     * 获取意见反馈列表接口
     * @param userId
     * @param imei
     * @param start
     * @param count
     * @param lastUpdateTime
     * @return
     * @throws GsonResultException
     */
    public ArrayList<FeedBackInfo> getFeedBackInfoList(Integer userId, String imei,Integer start,Integer count,String lastUpdateTime) throws GsonResultException;

    /**
     * 提交新的意见反馈接口
     * @param account
     * @param imei
     * @param simCode
     * @param sourceType
     * @param deviceModel
     * @param mdnCode
     * @param appVserion
     * @param content
     * @param contentType
     * @return
     * @throws GsonResultException
     */
    public CommonResultInfo addFeedbackInfo(String account,String imei,String simCode,
                                                 String sourceType,String deviceModel,String mdnCode,String appVserion,String content,String contentType) throws GsonResultException;
    /**
     * 回复意见反馈接口
     * @param account
     * @param imei
     * @param feedbackId
     * @param content
     * @param contentType
     * @return
     * @throws GsonResultException
     */
    public boolean addReplyFeedbackInfo(String account,String imei,
                                                           String feedbackId,String content,String contentType) throws GsonResultException;

    /**
     * 意见反馈评分接口
     * @param feedbackId
     * @param score
     * @return
     * @throws GsonResultException
     */
    public boolean doFeedbackScore(String feedbackId,Integer score) throws GsonResultException;

    /**
     * 查询意见反馈是否是关闭状态接口
     * @param feedbackId
     * @return
     * @throws GsonResultException
     */
    public FeedbackSelectReplyInfo getFeedbackSelect(String feedbackId) throws GsonResultException;


    /**
     * 获得在线阅读内容接口
     * @param bookid  书籍id
     * @param sequence 章节序号
     * @param width 屏幕宽
     * @param height 屏幕高
     * @return 返回html数据
     * @throws GsonResultException
     */
    public OnlineReadContentInfo getOnlineReadContent(String bookid, String sequence, int width, int height) throws GsonResultException;



    /**
     * 获取目录列表接口(带是否收费标记)
     * @param bookId
     * @param start
     * @param count
     * @return
     * @throws GsonResultException
     */
    public ArrayList<BookCatalog> getBookCatalogList(String bookId, int start, int count) throws GsonResultException;



    /**
     * 添加笔记接口
     * @return
     * @throws GsonResultException
     */
    public AddBookDigestInfo addBookDigest(BookDigests digest) throws GsonResultException;


    /**
     * 删除笔记接口
     * @param userBookTagId
     * @return
     * @throws GsonResultException
     */
    public boolean deleteBookDigest(String userBookTagId) throws GsonResultException;
    /**
     * 更新笔记接口
     * @return
     * @throws GsonResultException
     */
    public boolean updateBookDigest(String userId, BookDigests digest, String noteId) throws GsonResultException;

    /**
     * 获取用户某本书的所有笔记接口
     * @param bookId
     * @return
     * @throws GsonResultException
     */
    public ArrayList<BookDigests> getBookDigestListByBookId(String bookId) throws GsonResultException;

    /**
     * 批量同步用户笔记的接口
     * @param bookLabels
     * @return
     * @throws GsonResultException
     */
    public ArrayList<SyncDigestResponse> syncBookDigestList(JsonArrayList<DigestResponse> bookLabels) throws GsonResultException;
    /**
     * 获取用户书签接口
     * @param start
     * @param count
     * @return
     * @throws GsonResultException
     */
    public ArrayList<BookMark> getUserBookMark(int start, int count) throws GsonResultException;

    /**
     * 获取用户某本书的所有书签接口
     * @param userId
     * @param bookId
     * @return
     * @throws GsonResultException
     */
    public ArrayList<BookMark> getBookMarkListByBookId(String userId, String bookId) throws GsonResultException;

    /**
     * 添加书签接口
     * @return
     * @throws GsonResultException
     */
    public BookMarkResponse addBookLabel(BookMark bookMark) throws GsonResultException;

    /**
     * 删除书签接口
     * @param userBookTagId
     * @return
     * @throws GsonResultException
     */
    public boolean deleteBookLabel(String userBookTagId) throws GsonResultException;


    /**
     * 批量同步用户书签的接口
     * @param bookLabels
     * @return
     * @throws GsonResultException
     */
    public ArrayList<BookMarkResponse> syncBookMarkList(JsonArrayList<BookMarkResponse> bookLabels) throws GsonResultException;


    /**
     * 查询收藏状态接口
     * @return
     * @throws GsonResultException
     */
    public CollectQueryResultInfo colletQuery(Integer resourceId)throws GsonResultException;


    /**
     * 添加收藏接口
     * @param resourceId 资源ID(如：书籍ID)
     * @param resourceType 资源类型（1书籍，2专题）
     * @param groupId 组id（默认组id，第一次允许为空，后续必填）
     * @return
     * @throws GsonResultException
     */
    public CollectAddResultInfo collectAdd(int resourceId,int resourceType,Integer groupId) throws GsonResultException;




    /**
     * 删除收藏接口
     * @param collectionId
     * @return
     * @throws GsonResultException
     */
    public CollectDeleteResultInfo colletDelete(Integer collectionId)throws GsonResultException;



    /**
     * 添加系统书签接口
     * @return
     * @throws GsonResultException
     */
    public SystemBookMarkAddResponseInfo addSysBookMark(int groupId, BookMark bookMark, String source) throws GsonResultException;


    /**
     * 批量删除系统书签
     * @return
     * @throws GsonResultException
     */
    public boolean delSysBookMark(String[] ids) throws GsonResultException;


    /**
     * 查询用户的系统书签
     * @return
     * @throws GsonResultException
     */
    public ArrayList<SystemBookMarkResponseInfo> getSysBookMark(String source) throws GsonResultException;

    /**
     * 查询用户的系统书签分组
     * @return
     * @throws GsonResultException
     */
    public ArrayList<SystemBookMarkGroupResponseInfo> getSysBookMarkGroup(String source) throws GsonResultException;

    /**
     * 添加用户的系统书签分组
     * @return
     * @throws GsonResultException
     */
    public SystemBookMarkAddResponseInfo addSysBookMarkGroup(String groupName) throws GsonResultException;


    /**
     * 删除用户的系统书签分组
     * @return
     * @throws GsonResultException
     */
    public CommonResultInfo delSysBookMarkGroup(int groupId, int isDelete) throws GsonResultException;


    /**
     * 更新用户的系统书签分组
     * @return
     * @throws GsonResultException
     */
    public SystemBookMarkAddResponseInfo updateSysBookMarkGroup(int groupId, String groupName) throws GsonResultException;

    /**
     * 获取书签分组下的书籍
     * @return
     * @throws GsonResultException
     */
    public BookGroupMarkResponse getBookMarkByGroup(int groupId, String source) throws GsonResultException;



    /**
     * 根据用户id获取后台第三方用户信息
     * @return
     */
    public ArrayList<UserThirdLeyueInfo> getThirdIdByLeYueId(String userID) throws GsonResultException;


    /**
     * 电信下单记录接口
     * @param bookId
     * @param calType
     * @param calObj
     * @param charge
     * @param purchaseType
     * @param sourceType
     * @return
     * @throws GsonResultException
     */
    public boolean getAddOrderDetail(String userId, String bookId, String bookName, int calType, String calObj, String charge, int purchaseType, int sourceType) throws GsonResultException;



    /**
     * 根据书籍ID获取书籍类型
     * @return
     */
    public BookTypeInfo getBookType(String bookId) throws GsonResultException;

    /**
     * 获取插件列表
     * @return
     * @throws GsonResultException
     */
    public JsonArrayList<Plugin> getPlugins() throws GsonResultException;
    
    
    /**
     * 书城排行二级接口
     * @param start 查询起始记录顺序号缺省从第一条记录开始返回
     * @param count 返回分页记录数缺省使用平台默认配置
     * @return
     * @throws GsonResultException
     */
    public ArrayList<ContentInfoLeyue> getBookCitySalesTwo(int rankId,int start,int count) throws GsonResultException;
    
    
    
    
    /**
     * 书城分类页面接口
     * @return
     * @throws GsonResultException
     */
    public ArrayList<BookCityClassifyResultInfo> getBookCityClassify(int parentId) throws GsonResultException;
    
    /** 
     * 
     * @param bookType 类型（该参数为REST风格的参数）
     * @param start 查询起始记录顺序号缺省从第一条记录开始返回 
     * @param count 返回分页记录数缺省使用平台默认配置
     * @param isFee 是否收费（0：免费，1：收费，2：优惠）缺省则查询全部
     * @return
     * @throws GsonResultException
     */
    public ArrayList<ContentInfoLeyue> getBookCityClassifyDetail(int id,int start,int count,String isFee) throws GsonResultException;

    /**
     * 书城专题获取接口
     * @return
     * @throws GsonResultException
     */
	public ArrayList<SubjectResultInfo> getBookCitySubjectInfo(int module,int start,int count)throws GsonResultException;

	 /**
     * 书城专题分类二级页面接口
     * @return
     * @throws GsonResultException
     */
	public SubjectDetailResultInfo getBookCitySubjectClassifyDetail(int subjectId,int start, int count)throws GsonResultException;
	
	/**
	 * 书城重磅推荐书籍接口
	 * @return
	 * @throws GsonResultException
	 */
	public ArrayList<ContentInfoLeyue> getBookCitySubjectHeavyRecommend(int column,
			int start,int count)throws GsonResultException;

	/**
	 * 书城新书抢先看页面接口
	 * @return
	 * @throws GsonResultException
	 */
	public ArrayList<ContentInfoLeyue> getBookCityNewBookRecommend(int column,
			int start,int count)throws GsonResultException;
	

	/**
	 * 书城限时免费接口
	 * @return
	 * @throws GsonResultException
	 */
	public ArrayList<ContentInfoLeyue> getBookCityFreeLimitSubject(int start, int count)throws GsonResultException;

	/**
	 * 书城最新特价接口
	 * @return
	 * @throws GsonResultException
	 */
	public ArrayList<ContentInfoLeyue> getBookCityLatestSpecialOfferSubject(int start, int count)throws GsonResultException;

	/**
	 * 书城免费中心接口
	 * @return
	 * @throws GsonResultException
	 */
	public ArrayList<ContentInfoLeyue> getBookCityFreeSubject(int start, int count)throws GsonResultException;


	/**
	 * 书城搜索接口
	 * @return
	 * @throws GsonResultException
	 */
	public ArrayList<ContentInfoLeyue> getBookCitySearchResult(String word,int start,int count)throws GsonResultException;
  
	/**
	 * 获取绑定账号列表接口
	 * @param userId
	 * @return
	 * @throws GsonResultException
	 */
	public ArrayList<AccountBindingInfo> getBindingAccountList(String userId) throws GsonResultException;
	
	/**
	 * 保存绑定账号接口
	 * @param uid
	 * @param source
	 * @param account
	 * @param password
	 * @return
	 * @throws GsonResultException
	 */
	public AccountBindingInfo saveBindingAccount(String uid, String source, String account, String password) throws GsonResultException;

	/**
	 * 解绑绑定账号接口
	 * @param id
	 * @return
	 * @throws GsonResultException
	 */
	public CommonResultInfo unBindAccount(String id) throws GsonResultException;
	
	/**
	 * 上传用户头像
	 * @param uid
	 * @param fieldPath
	 * @return
	 * @throws GsonResultException
	 */
	public UserInfoLeyue uploadUserHead(String uid, String fieldPath) throws GsonResultException;
}























