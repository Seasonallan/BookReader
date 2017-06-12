package com.lectek.android.lereader.net.openapi;


import android.text.TextUtils;

/**
 * @author mingkg21
 * @email  mingkg21@gmail.com
 * @date   2013-8-23
 */
public class RequestMessage {

	
	/** 获取内容封面接口（get_content_cover）
	 * @param contentId 内容ID
	 * @param coverSize 封面分辨率类型
						1、480*640
						2、124*165
						3、84*112
						4、62*83
						5、48*64
	 * @return
	 */
	public static String getContentCover(String contentId, int coverSize){
		StringBuilder sb = new StringBuilder();
		sb.append("&content_id=");
		sb.append(contentId);
		sb.append("&cover_size=");
		sb.append(coverSize);
		return sb.toString();
	}

	
	/** 获取内容基本信息接口（get_base_content）
	 * @param contentId 内容ID
	 * @return
	 */
	public static String getBaseContent(String contentId){
		StringBuilder sb = new StringBuilder();
		sb.append("&content_id=");
		sb.append(contentId);
		return sb.toString();
	}
	
	/** 获取内容目录列表接口（get_book_chapter_list）
	 * @param contentId 内容ID
	 * @return
	 */
	public static String getBookChapterList(String contentId){
		StringBuilder sb = new StringBuilder();
		sb.append("&content_id=");
		sb.append(contentId);
		sb.append("&start=1&count=100000");
		return sb.toString();
	}
	
	/** 获取内容目录列表接口（get_book_chapter_list）
	 * @param contentId 内容ID
	 * @return
	 */
	public static String getBookChapterList(String contentId, int start, int count){
		StringBuilder sb = new StringBuilder();
		sb.append("&content_id=");
		sb.append(contentId);
		sb.append("&start=");
		sb.append(start);
		sb.append("&count=");
		sb.append(count);
		return sb.toString();
	}

	/** 获取内容章节信息接口（get_chapter_info）
	 * @param contentId 内容ID
	 * @param chapterId 当前章节ID，不传默认第一章
	 * @return
	 */
	public static String getChapterInfo(String contentId, String chapterId){
		StringBuilder sb = new StringBuilder();
		sb.append("&content_id=");
		sb.append(contentId);
		if(!TextUtils.isEmpty(chapterId)){
			sb.append("&chapter_id=");
			sb.append(chapterId);
		}
		return sb.toString();
	}

	/** 判断内容是否被订购（is_content_ordered）
	 * @param contentId 内容ID
	 * @return
	 */
	public static String isContentOrdered(String contentId){
		StringBuilder sb = new StringBuilder();
		sb.append("&book_id=");
		sb.append(contentId);
		return sb.toString();
	}
	
	/** 阅点点播内容接口（subscribe_content_readpoint） 
	 * @param contentId 内容ID
	 * @param chapterId 章节ID，按章节购买时必须包含此参数
	 * @return string
	 */
	public static String subscribeContentReadpoint(String contentId, String chapterId) {
		StringBuilder sb = new StringBuilder();
		sb.append("&content_id=");
		sb.append(contentId);
		if (!TextUtils.isEmpty(chapterId)) {
			sb.append("&chapter_id=");
			sb.append(chapterId);
		}
		return sb.toString();
	}
	
	/** 阅点点播包月接口（subscribe_month_product_readpoint） 
	 * @param productId 产品ID
	 * @return
	 */
	public static String subscribeMonthProductReadpoint(String productId){
		StringBuilder sb = new StringBuilder();
		sb.append("&product_id=");
		sb.append(productId);
		return sb.toString();
	}

	
	/** 判断包月产品是否被订购（is_monthproduct_ordered）
	 * @param productId 包月id
	 * @return
	 */
	public static String getIsOrderInfo(String productId){
		StringBuilder sb = new StringBuilder();
		sb.append("&month_productid=");
		sb.append(productId);
		return sb.toString();
	}

	/**
	 * 积分兑换阅点接口请求参数
	 * @param ruleId
	 * @return
	 */
	public static String getPointConvertSendData(String ruleId){
		StringBuilder sb = new StringBuilder();
		sb.append("&rule_id=");
		sb.append(ruleId);
		return sb.toString();
	}
}
