package com.lectek.android.lereader.data;

import java.io.Serializable;
import java.util.Date;

import android.text.TextUtils;

/**
 * 书籍实体
 * 
 * @author yangwc
 * 
 */
public class Book implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8374379247235398129L;
	
	/** 书籍的位置；程序本身下载的  */
	public static final byte FROM_TYPE_SELF = 1;
	/** 书籍的位置；手机存储卡的  */
	public static final byte FROM_TYPE_LOCAL = 2;
	/** 书籍的位置；在线获取的  */
	public static final byte FROM_TYPE_ONLINE = 3;
	
	/** 书籍类型；书籍 */
	public static final byte TYPE_BOOK = 1;
	/** 书籍类型；漫画 */
	public static final byte TYPE_CARTOON = 2;
	/** 书籍类型；板式杂志 */
	public static final byte TYPE_MAGAZINE = 3;
	/** 书籍类型；连载 */
	public static final byte TYPE_SERIAL = 4;
	/** 书籍类型；流式杂志*/
	public static final byte TYPE_MAGZINE_STREAM = 6;
	/** 书籍类型；其它类型*/
	public static final byte TYPE_OTHER = 10;
	/** 书籍类型；有声*/
	public static final byte TYPE_VOICE = 127;
	
	public long book_id;
	public String contentID;
	public String name;
	public String filePath;
	public Date created_time;
//	public String book_cover;// path
	public String author;
	public long file_size;
	public byte fromType;// ceb、online、local
//	public String is_order = "N";// 是否订购
	public String price;// 价格
	public String readPointPrice;// 阅点
	public String offersPrice;// 优惠价
	public Boolean isOrdered;
	public String type;// 书籍类型；电子书、漫画、杂志
	public String bookSeries;//当前是集数，只有杂志漫画有用
	public boolean isComplete;
	public boolean isSerial;
	public String logoUrl;	//书籍封面下载地址
	public static final String order = "Y";
	public static final String unOrder = "N";
	public static final String DOWNLOAD = "ceb";
	public static final String ONLINE = "online";
	public static final String LOCAL = "local";
	
	public boolean isNeedBuy = false;

	public Book(){
		
	}

	public Book(long book_id, String content_id, String book_name,
			String book_path, Date created_time, String book_cover,
			String author, long fileSize, byte fromType) {
		this.book_id = book_id;
		this.contentID = content_id;
		this.name = book_name;
		this.filePath = book_path;
		this.created_time = created_time;
//		this.book_cover = book_cover;
		this.author = author;
		this.file_size = fileSize;
		this.fromType = fromType;
	}

	// add by kinson 2011-08-31
	public Book(long book_id, String content_id, String book_name,
			String book_path, String book_cover, String author,
			byte fromType, String contentType) {
		this.book_id = book_id;
		this.contentID = content_id;
		this.name = book_name;
		this.filePath = book_path;
//		this.book_cover = book_cover;
		this.author = author;
		this.fromType = fromType;
		this.type = contentType;
	}
	
	public Book(long book_id, String content_id, String book_name, String book_path, String author,
			byte fromType, String contentType, String logoUrl) {
		this.book_id = book_id;
		this.contentID = content_id;
		this.name = book_name;
		this.filePath = book_path;
//		this.book_cover = book_cover;
		this.author = author;
		this.fromType = fromType;
		this.type = contentType;
		this.logoUrl = logoUrl;
	}
	// add by kinson end

	public Book(long book_id, String content_id, String book_name,
			String book_path, Date created_time, String book_cover,
			String author, long fileSize, byte fromType, String is_order,
			String price) {
		this.book_id = book_id;
		this.contentID = content_id;
		this.name = book_name;
		this.filePath = book_path;
		this.created_time = created_time;
//		this.book_cover = book_cover;
		this.author = author;
		this.file_size = fileSize;
		this.fromType = fromType;
//		this.is_order = is_order;
		this.price = price;
	}

	public Book(long book_id, String content_id, String book_name,
			String book_path, Date created_time, String book_cover,
			String author, long fileSize, byte fromType, String is_order,
			String price, String contentType, boolean isComplete) {
		this.book_id = book_id;
		this.contentID = content_id;
		this.name = book_name;
		this.filePath = book_path;
		this.created_time = created_time;
//		this.book_cover = book_cover;
		this.author = author;
		this.file_size = fileSize;
		this.fromType = fromType;
//		this.is_order = is_order;
		this.readPointPrice = "-1";
		if (!TextUtils.isEmpty(price)) {
			String[] prices = price.split(";");
			if (prices != null) {
				int priceLen = prices.length;
				for (int i = 0; i < priceLen; ++i) {
					if (i == 0) {
						this.price = prices[0];
					} else if (i == 1) {
						this.readPointPrice = prices[1];
					} else if (i == 2) {
						this.offersPrice = prices[2];
					}
				}
			}
		}
		this.type = contentType;
		this.isComplete = isComplete;
//		this.logoUrl = logoUrl;
	}

	public Book(long book_id, String content_id, String book_name,
			String book_path, Date created_time, String book_cover,
			String author, long fileSize, byte fromType, String is_order,
			String price, String readPointPrice, String offersPrice,
			String contentType, boolean isComplete, String logoUrl) {
		this.book_id = book_id;
		this.contentID = content_id;
		this.name = book_name;
		this.filePath = book_path;
		this.created_time = created_time;
//		this.book_cover = book_cover;
		this.author = author;
		this.file_size = fileSize;
		this.fromType = fromType;
//		this.is_order = is_order;
		this.price = price;
		this.readPointPrice = readPointPrice;
		this.offersPrice = offersPrice;
		this.type = contentType;
		this.isComplete = isComplete;
		this.logoUrl = logoUrl;
	}

}
