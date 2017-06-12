package com.lectek.android.lereader.utils;

import java.io.File;

import com.lectek.android.lereader.lib.utils.FileUtil;
import com.lectek.android.lereader.permanent.LeyueConst;

/**
 * 常量
 * 
 * @author yangwc
 * 
 */
public class Constants {
 
	public static final String bookStoredDiretory = FileUtil.getExternalStorageDirectory()
			+ File.separator + LeyueConst.FILENAME_SDCARD + File.separator;
	/**
	 * 存放wifi传书的目录
	 */
	public static final String WIFI_STORED_DIRECTORY = bookStoredDiretory + "wifi/";
	
	/** 存放LOG */
	public static final String FILE_LOG = bookStoredDiretory + "log.txt";
	/** 存放分类目录 */
	public static final String BOOKS_CATALOG = bookStoredDiretory + "catalog/";
	/** 缓存目录 */
	public static final String BOOKS_TEMP = bookStoredDiretory + "temp/";
	/** 图片缓存目录 */
	public static final String BOOKS_TEMP_IMAGE = BOOKS_TEMP + "image/";
	/** 更新缓存目录 */
	public static final String BOOKS_TEMP_SELF = BOOKS_TEMP + "self/";
	/** 下载存储的目录 */
	public static final String BOOKS_DOWNLOAD = bookStoredDiretory + "download/";
	/** 在线阅读存储的临时目录 */
	public static final String BOOKS_ONLINE = BOOKS_TEMP + "online/";
	/** 在线收听存储的临时目录 */
	public static final String BOOKS_VOICE = BOOKS_TEMP + "voice/";
	/** 在线阅读存储的临时目录的最大容量(Byte) */
	public static final long BOOKS_ONLINE_MAX_SIZE = 10 * 1024 * 1024;// 5M?
	/** 存储频道：图书 */
	public static final String FILE_CATALOG_FIGURE_BOOK = BOOKS_CATALOG
			+ "figureBook.xml";
	/** 存储频道：书籍 */
	public static final String FILE_CATALOG_BOOK = BOOKS_CATALOG + "book.xml";
	/** 存储频道：连载 */
	public static final String FILE_CATALOG_SERIAL = BOOKS_CATALOG
			+ "serial.xml";
	/** 存储频道：杂志 */
	public static final String FILE_CATALOG_MAGAZINE = BOOKS_CATALOG
			+ "magazine.xml";
	/** 存储频道：漫画 */
	public static final String FILE_CATALOG_CARTOON = BOOKS_CATALOG
			+ "cartoon.xml";
	/** 存储频道：专区 */
	public static final String FILE_CATALOG_AREA = BOOKS_CATALOG + "area.xml";
	/** 存储频道：有声 */
	public static final String FILE_CATALOG_AUDIO = BOOKS_CATALOG + "audio.xml";

	public static final String FILE_LOGIN_PAGE = BOOKS_CATALOG
			+ "login_page.xml";
	public static final String FILE_CHANNEL_BOOKS = BOOKS_CATALOG
			+ "channel_books.xml";
	public static final String FILE_CHANNEL_MAGAZINE = BOOKS_CATALOG
			+ "channel_magazine.xml";
	public static final String FILE_CHANNEL_CARTOON = BOOKS_CATALOG
			+ "channel_cartoon.xml";

	public static final int PAGE_ITEM_NUM = 18;// Grid每页显示18个

	public static final String URL_HTTP_START = "http://";

	// ********************************************************************
	// **************************阅读的亮度最小值***************************
	// ********************************************************************
	// 通用的最小值
	public static final float READ_LIGHT_MIN_VALUE_COMMON = 0.1f;
	// 宇龙的D530最小值
	public static final float READ_LIGHT_MIN_VALUE_D530 = 0.1f;
	// 宇龙的E230A最小值
	public static final float READ_LIGHT_MIN_VALUE_E230A = 0.17f;

    /** 意见反馈图片缓存目录 */
    public static final String BOOKS_TEMP_FEEDBACK_IMAGE = BOOKS_TEMP + "feedback/";
}
