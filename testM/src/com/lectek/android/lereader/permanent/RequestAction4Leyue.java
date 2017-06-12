package com.lectek.android.lereader.permanent;

/**
 * @description
	乐阅平台的Action
 * @author chendt
 * @date 2013-8-26
 * @Version 1.0
 */
public class RequestAction4Leyue {
	
	/** 用户登录接口*/
	public static final String ACTION_USER_LOGIN = "/login";
	
	/** 客户端第三方账号记录接口  */
	public static final String GET_THRID_ACTION = "/user/third";
	
	/** 根据用户id获取用户第三方关系记录信息列表 */
	public static final String GET_THRID_BY_USERID = "/user/";
	
	/** 更具指定id获取用户信息接口*/
	public static final String ACTION_GET_USER_INFO_BY_ID = "/user/";
	
	/** 根据后台第三方关系ID更新第三方关系记录 */
	public static final String ACTION_POST_UPDATE_THRID_ACCTOKEN = "/user/third/";
	
	public static final String ACTION_POST_EXIT_LOG = "/exit";
	
	/** 用户是否存在接口*/
	public static final String ACTION_IS_USER_AVAILABLE = "/user/account";
	
	/** 注册用户接口*/
	public static final String ACTION_REGIST_USER = "/user";
	
	/**书籍详情*/
	public static final String CONTENTINFO = "/book/";
	
	/** 积分兑书接口*/
	public static final String ACTION_GET_SCOREORDER = "/order/scoreOrder";
	
	/** 购买书籍接口*/
	public static final String ACTION_GET_ORDER_INFO = "/order/add";
	
	public static final String ACTION_GET_ORDER_INFO_BY_ID = "/order/";
	
	public static final String ACTION_GET_ORDER_LIST = "/order/user/";
	
	/** 客户端支付完回调服务端接口*/
	public static final String ACTION_PAY_ACCEPTREQUEST = "/pay/acceptRequest";
	
	/** 获取积分上传统计接口*/
	public static final String ACTION_POST_SCORE = "/score/postscore";
	
	/** 获取书籍解密秘钥接口*/
	public static final String ACTION_GET_DECODE_KEY = "/encrypt/book/";
	
	/** 获取最新版本接口*/
	public static final String ACTION_CHECK_UPDATE = "/client/getVersion";
	
	/** 获取书城 推荐页专题一接口ACTION */
	public static final String GET_BOOKCITY_RECOMMEND_SUBJECT1_ACTION = "/bookSubject/list/module/1?start=0&count=4";
	
	/** 获取书城 推荐页接口ACTION */
	public static final String GET_BOOKCITY_RECOMMEND_ACTION = "/bookCity/citic/recommend";

	/** 获取书城 分类页接口ACTION */
	public static final String GET_BOOKCITY_CLASSIFY_ACTION = "/bookCity/citic/type";
	
	/** 获取书城 特价页接口ACTION */
	public static final String GET_BOOKCITY_SPECIAL_OFFER_ACTION = "/bookCity/citic/special";
	
	/** 获取书城 榜单页接口ACTION */
	public static final String GET_BOOKCITY_SALE_ACTION = "/bookRank/list";
	
	/** 获取热词列表接口ACTION */
	public static final String GET_HOT_WORDS_ACTION = "/search/hotwords";
	
	/** 获取与该书标签相关的书籍接口ACTION */
	public static final String GET_RECOMMENDED_BOOK_ACTION = "/bookTag/relactionBooks";
	
	/** 获取发表评论的接口ACTION */
	public static final String GET_ADD_COMMENT_ACTION = "/book/comment/add";
	
	/** 获取最新评论接口ACTION */
	public static final String GET_NEW_COMMENT_LIST_ACTION = "/book/comment/newList/";

    /** 获取书籍专题接口ACTION */
    public static final String GET_BOOK_SUBJECT_ACTION = "/bookSubject/list/module/";

    /**批量删除收藏  */
    public static final String COLLECT_DELELIST_ACTION="/book/collect/batchDelete";
    
    /**获取收藏列表  */
    public static final String COLLECT_LITS_GET_ACTION="/book/collect/query";

    /** 在线阅读接口ACTION */
    public static final String GET_ONLINE_READ_CONTENT = "/book/";

    /** 获取反馈列表**/
    public static final String GET_FEEDBACK_LIST_ACTION="/feedback/process/lists";

    /**提交新的意见反馈 **/
    public static final String GET_ADD_FEEDBACK_ACTION ="/feedback/process/add";
    /** 回复意见反馈 **/
    public static final String GET_ADD_REPLY_FEEDBACK_ACTION="/feedback/process/addReply";
    /** 意见反馈评分**/
    public static final String GET_FEEDBACK_SCORE_ACTION="/feedback/process/score/";
    /** 查询意见反馈是否是关闭状态**/
    public static final String GET_FEEDBACK_SELECT_ACTION="/feedback/process/";

    /** 获取添加笔记的接口ACTION */
    public static final String ACTION_ADD_BOOK_DIGEST = "/note/add";

    /** 查看笔记的接口ACTION */
    public static final String ACTION_GET_BOOK_DIGEST = "/note/search";

    /** 更新笔记的接口ACTION */
    public static final String ACTION_UPDATE_BOOK_DIGEST = "/note/update";

    /** 查看笔记的接口ACTION */
    public static final String ACTION_DEL_BOOK_DIGEST = "/note/delete";

    /** 批量同步用户笔记的接口ACTION */
    public static final String ACTION_SYNC_BOOK_DIGEST = "/note/sync";
    /** 获取客户端启动图片接口ACTION */
    public static final String GET_START_PIC_ACTION = "/client/getStartPic";
    
    /** 获取最有用评论接口ACTION */
	public static final String GET_HOT_COMMENT_LIST_ACTION = "/book/comment/usefulList/";
	
	/**添加收藏  */
	public static final String COLLECT_ADD_ACTION="/book/collect/add";
	/**删除收藏  */
	public static final String COLLECT_DELE_ACTION="/book/collect/delete";
	
	/**查询收藏状态 */
	public static final String COLLECT_QUERY_ACTION="/book/collect/queryBook";

	/** 获取评论详情接口ACTION */
	public static final String GET_COMMENT_DETAIL_ACTION = "/book/comment/detail/";
	
	/** 获取评论回复接口ACTION */
	public static final String GET_REPLY_COMMENT_ACTION = "/book/comment/reply";
	
	/** 获取支持评论接口ACTION */
	public static final String GET_COMMENT_SUPPORT_ACTION = "/book/comment/support/";

    /** 获取添加书签的接口ACTION */
    public static final String ACTION_ADD_BOOK_LABEL = "/userBookTag";

    /** 查看用户所有书签的接口ACTION */
    public static final String ACTION_GET_USER_ALL_BOOK_LABEL = "/userBookTag/userTags";

    /** 查看用户某本书书签的接口ACTION */
    public static final String ACTION_GET_BOOK_LABEL = "/userBookTag/bookTags";

    /** 批量同步用户书签的接口ACTION */
    public static final String ACTION_SYNC_BOOK_LABEL = "/userBookTag/sync";

    /** 添加或者更新系统书签的接口ACTION */
    public static final String ACTION_ADD_UPDATE_SYSMARK = "/systemMark/addOrUpdate";
    /** 批量删除系统书签的接口ACTION */
    public static final String ACTION_DELETE_SYSMARK = "/systemMark/batchDelete";
    /** 查询用户的系统书签的接口ACTION */
    public static final String ACTION_GET_SYSMARK = "/systemMark/query";
    /** 查询用户的系统书签分组的接口ACTION */
    public static final String ACTION_GET_SYSMARK_GROUP = "/systemMarkGroup/queryAll";
    
    /** 电信下单记录  */
	public static final String GET_ADDORDERDETAIL_ACTION = "/order/addOrderDetail";
	
	/** 乐阅下单记录  */
	public static final String GET_GENERATE_ORDER_ACTION = "/pay/tyOpen/generateOrder";
	
	/** 乐阅更新记录  */
	public static final String GET_UPDATE_ORDER_ACTION = "/pay/tyOpen/updateOrder";
}
