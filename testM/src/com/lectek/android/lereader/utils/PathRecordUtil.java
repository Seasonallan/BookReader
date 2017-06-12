package com.lectek.android.lereader.utils;


/**
 * 路径记录类
 * 
 * @author shizq
 * @email real.kobe@163.com
 * @date 2012-12-5
 */
public class PathRecordUtil extends PathRecordManager {
	
	/**
	 * 首页推荐位
	 */
	public static String TAG_RECOMMEND = "home";
	/**
	 * 专题
	 */
	public static String TAG_SUBJECT = "subject";
	/**
	 * 专题列表
	 */
	public static String TAG_SUBJECT_LIST = "subject_list";
	/**
	 * 广告
	 */
	public static String TAG_ADVERTISE = "ad";
	/**
	 * 免费书籍推荐
	 */
	public static String TAG_FREEBOOK = "freeBook";
	/**
	 * 包月专区
	 */
	public static String TAG_PACKAGE = "package";
	/**
	 * 有声包月专区
	 */
	public static String TAG_PACKAGE_VOICE = "package-voice";
	/**
	 * 我的包月
	 */
	public static String TAG_MY_PACKAGE = "myPackage";
	/**
	 * 包月包
	 */
	public static String TAG_PACKAGE_CONTENT = "packageContent";
	/**
	 * 频道
	 * <br> channel=（1：书籍；2：漫画；3：杂志）
	 */
	public static String TAG_CHANNEL = "channel";
	/**
	 * 分类
	 * <br> catalog=分类名字
	 */
	public static String TAG_CATALOG = "catalog";
	/**
	 * 排行榜
	 * <br> rank=1（1：书籍；2：漫画；3：杂志）
	 */
	public static String TAG_RANK = "rank";
	/**
	 * 排行榜细分
	 * <br> rankId = 排行榜细分ID
	 */
	public static String TAG_RANKD_ID = "rankId";
	/**
	 * 搜索
	 */
	public static String TAG_SEARCH = "search";
	/**
	 * 搜索热词结果
	 * <br> searchWithKeyWord=搜索词
	 */
	public static String TAG_SEARCH_WITH_KEYWORD = "searchWithKeyWord=";
	/**
	 * 搜索文本结果
	 * <br> searchWithInput=搜索词
	 */
	public static String TAG_SEARCH_WITH_INPUT = "searchWithInput=";
	/**
	 * 消费记录
	 */
	public static String TAG_CONSUME = "consume";
	/**
	 * 详情页-相关推荐
	 */
	public static String TAG_CONTENTINFO_RECOMMEND = "contentInfo-recommend";
	/**
	 * 我的消息
	 */
	public static String TAG_MESSAGE = "message";
	/**
	 * 赠送信息
	 */
	public static String TAG_GIFT_BOOK = "giftBook";
	/**
	 * 被赠送信息
	 */
	public static String TAG_BE_GIFT_BOOK = "beGiftBook";
	/**
	 * 阅读到书籍末尾的相关推荐
	 */
	public static String TAG_GET_ALSO_LIKE_CONTENT = "getAlsoLikeContent";
	/**
	 * 桌面插件
	 */
	public static String TAG_WIDGET = "widget";
	/**
	 * 消息
	 */
	public static String TAG_NOTIFICATION  = "notification";
	/**
	 * push
	 */
	public static String TAG_SMS_PUSH = "smspush";
	/**
	 * 外部程序调用
	 * <br> packageName=程序包名
	 */
	public static String TAG_PACKAGE_NAME = "packageName=";
	
	/**
	 * 书籍详情页
	 */
	public static String TAG_CONTENT_INFO = "contentInfo";
	/**
	 * 有声书籍详情页
	 */
	public static String TAG_CONTENT_INFO_VOICE = "contentInfo-voice";
	/**
	 * 在线
	 */
	public static String TAG_ONLINE = "online";
	/**
	 * 试读
	 */
	public static String TAG_TRY_READ = "tryRead";
	/**
	 * 书架
	 */
	public static String TAG_BOOK_SHELF = "bookshelf";
	/**
	 * 连载更新
	 */
	public static String TAG_ORDER = "order";
	/**
	 * 下载
	 */
	public static String TAG_DOWNLOAD = "download";
	
	/**
	 * 详情页封面
	 */
	public static String TAG_CONTENTINFO_COVER = "contentInfo-cover";
	/**
	 * 详情页阅读按钮
	 */
	public static String TAG_CONTENTINFO_BUTTON = "contentInfo-button";
	/**
	 * 最近
	 */
	public static String TAG_LASTEST = "lastest";
	/**
	 * 收藏
	 */
	public static String TAG_FAVORITE = "favorite";
	/**
	 * 本机
	 */
	public static String TAG_LOCAL = "local";
	/**
	 * 阅读
	 */
	public static String TAG_READ_BOOK = "readbook";
	/**
	 * 听书
	 */
	public static String TAG_VOICE_PLAY = "voice-play";
	/**
	 * 推荐的分栏
	 **/
	public static String TAG_BLOCK_CONTENT = "blockContent";
	
	/**
	 * 作者信息
	 */
	public static String TAG_AUTHOR_INFO = "authorInfo";
	
	private boolean mIsNeedClear;
	
	private static PathRecordUtil instance;
	
	
	protected PathRecordUtil() {
		super();
	}
	
	public static PathRecordUtil getInstance() {
		if(instance == null) {
			instance = new PathRecordUtil();
		}
		return instance;
	}
	
	/**
	 * 清除多余的节点
	 */
	public void clearRedundant() {
		String redundants[] = {TAG_WIDGET,TAG_PACKAGE_NAME,TAG_GET_ALSO_LIKE_CONTENT,TAG_ADVERTISE,TAG_NOTIFICATION,TAG_SMS_PUSH};
		clearRedundant(redundants);
	}
	
	
	@Override
	public PathInfo createPath(String path) {
		PathInfo pathInfo = new PathInfo();
		pathInfo.pathString = path;
		return createPath(pathInfo);
	}

	@Override
	public PathInfo createPath(PathInfo pathInfo) {
		if(TAG_CONTENT_INFO.equals(pathInfo.pathString) 
				|| TAG_READ_BOOK.equals(pathInfo.pathString)
				|| TAG_SUBJECT.equals(pathInfo.pathString)
				|| TAG_PACKAGE_CONTENT.equals(pathInfo.pathString)) {
			mIsNeedClear = true;
		}else {
			mIsNeedClear = false;
		}
		return super.createPath(pathInfo);
	}

	@Override
	public void destoryPath(String pathInfoname) {
		super.destoryPath(pathInfoname);
		if(mIsNeedClear) {
			clearRedundant();
		}
		
	}
	
	@Override
	public void destoryPath(PathInfo pathInfo) {
		if(pathInfo.pathString == null) {
			return;
		}
		destoryPath(pathInfo.pathString);
	}

	/**
	 * 返回末尾的路径
	 * @param count 返回末尾的路径个数
	 * @param StringremoveLastPath 需要先删除的最后一个路径
	 * @return 返回整理后的路径
	 */
	public String getRearOfPathByCount(int count, String StringremoveLastPath) {
		
		String pathFilters[] = {
				TAG_WIDGET,
				TAG_NOTIFICATION,
				TAG_SMS_PUSH,
				TAG_PACKAGE_NAME,
				TAG_FREEBOOK, 
				TAG_CATALOG + "=",
				TAG_SEARCH_WITH_KEYWORD, 
				TAG_SEARCH_WITH_INPUT, 
				TAG_CONTENTINFO_RECOMMEND, 
				TAG_GET_ALSO_LIKE_CONTENT, 
				TAG_CONTENTINFO_COVER, 
				TAG_CONTENTINFO_BUTTON };
		String path = getRearOfPathByCount(count, StringremoveLastPath, pathFilters);
		return path;
		
	}
	
	public static final String TAG_TAB_PUBLISH = "publish";
	public static final String TAG_TAB_ORIGINAL = "original";
	public static final String TAG_TAB_VOICE = "voice";
	public static final String TAG_TAB_MAGAZINE = "magazine";
	public static final String TAG_TAB_CARTOON = "cartoon";
	public static final String TAG_TAB_BOOK = "book";
	public static final String TAG_TAB_NEWS = "news";
	
	public static final String TAG_CATALOG_ALL = "all";
	public static final String TAG_CATALOG_COMPLETE = "complete";
	public static final String TAG_CATALOG_SERIAL = "serial";
	public static final String TAG_CATALOG_FREE = "free";
	
	public static final String TAG_SEARCH_INPUT = "input";
	public static final String TAG_SEARCH_HOTKEY = "hotkey";
	public static final String TAG_SEARCH_SEVERCE_BOOK = "book";
	public static final String TAG_SEARCH_SEVERCE_VOICE = "voice";
	
	
}
