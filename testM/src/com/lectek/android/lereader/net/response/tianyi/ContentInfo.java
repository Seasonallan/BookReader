package com.lectek.android.lereader.net.response.tianyi;

import java.io.Serializable;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;
import com.lectek.android.lereader.utils.PriceUtil;

/** 书籍详情
 * @author mingkg21
 * @email  mingkg21@gmail.com
 * @date   2013-8-23
 */
public class ContentInfo extends BaseDao implements Serializable{
	
	public static final int CHANNEL_TYPE_BOOK = 1;
	public static final int CHANNEL_TYPE_CARTOON = 2;
	public static final int CHANNEL_TYPE_MAGAZINE = 3;
	public static final int CHANNEL_TYPE_SERIAL = 4;
	
	//0 免费 1 包月 2按本 3按章 
	public static final String FEE_TYPE_FREE = "0";
	public static final String FEE_TYPE_MONTHLY = "1";
	public static final String FEE_TYPE_BOOK = "2";
	public static final String FEE_TYPE_CHAPTER = "3";
	
	public static final String TYPE_STREAM = "流式";
	public static final String TYPE_FORMAT = "版式";
	
	public static final long serialVersionUID = 1L;
	
	/** 内容ID */
	@Json(name ="book_id")
	public String bookId;
	
	/** 内容名称 */
	@Json(name ="book_name")
	public String bookName;
	
	/** 内容作家ID */
	@Json(name ="author_id")
	public String authorId;
	
	/** 内容作家名称 */
	@Json(name ="author_name")
	public String authorName;
	
	/** 关键字 */
	public String keyword;
	
	/**简介—— 该字段用于我的订购 */
	@Json(name ="description")
	public String description;
	
	/** 短简介id */
	@Json(name ="short_description_id")
	public String shortDescriptionId;
	
	/** 短简介描述 */
	@Json(name ="short_description")
	public String shortDescription;
	
	/** 长简介id */
	@Json(name ="long_description_id")
	public String longDescriptionId;
	
	/** 长简介描述 */
	@Json(name ="long_description")
	public String longDescription;
	
	/** 频道id */
	@Json(name ="channel_id")
	public String channelId;
	
	/** 频道名称 */
	@Json(name ="channel_name")
	public String channelName;
	
	/** 频道类型；(1-书籍;2-漫画;3-杂志;4-连载.) */
	@Json(name ="channel_type")
	public int channelType;
	
	/** 集数  */
	@Json(name ="book_number")
	public String bookNumber;
	
	/** 总章数（书籍下面所有章节数目，不包含卷数目）  */
	@Json(name ="chapter_counts")
	public String chapterCounts;
	
	/** 阅点价   */
	@Json(name ="readpoint_price")
	public String readpointPrice;
	
	/** 话费价格   : <br/>注意——服务器话费价格下来跟阅点一样，这里getFeePrice 进行了处理。*/
	@Json(name ="fee_price")
	public String feePrice;
	
	/** 计费方式：0 免费 1 包月 2按本 3按章    */
	@Json(name ="fee_type")
	public String feeType;
	
	/**封面ID*/
	@Json(name ="cover_id")
	public String coverId;
	
	/**封面路径*/
	@Json(name ="cover_path")
	public String converPath;
	
	/**评分*/
	@Json(name ="mark")
	public String mark;
	
	/**分栏id*/
	@Json(name ="catalog_id")
	public String catalogId;
	
	/**分栏名称*/
	@Json(name ="catalog_name")
	public String catalogName;
	
	/**系列ID(书籍没有，杂志和漫画有所属系列ID)*/
	@Json(name ="serial_id")
	public String serialId;
	
	/**系列名称(书籍没有，杂志和漫画可能为空)*/
	@Json(name ="serial_name")
	public String serialName;
	
	/**是否连载：(返回true和false)*/
	@Json(name ="isserial")
	public boolean isserial;
	
	/**是否完成：(返回true和false)*/
	@Json(name ="finish_flag")
	public boolean finishFlag;
	
	/**是否存在同系列(返回true和false)*/
	@Json(name ="is_have_serial")
	public boolean isHaveSerial;
	
	/**是否允许下载(返回true和false)*/
	@Json(name ="is_download")
	public boolean isDownload;
	
	/**是否允许预定(返回true和false)*/
	@Json(name ="is_reserve")
	public boolean isReserve;
	
	/**是否允许评论(返回true和false)*/
	@Json(name ="is_allow_comment")
	public boolean isAllowComment;
	
	/**是否ISBN*/
	@Json(name ="isbn")
	public boolean isbn;
	
	/**版权有效期*/
	@Json(name ="validity")
	public String validity;
	
	/**版权提示信息*/
	@Json(name ="infomation")
	public String infomation;
	
	/**出版社*/
	@Json(name ="publisher")
	public String publisher;
	
	/** 点击数  */
	@Json(name ="click_counts")
	public String clickCounts;
	
	/**评论数*/
	@Json(name ="comment_counts")
	public Integer commentCounts;
	
	/**鲜花数*/
	@Json(name ="fllower_counts")
	public String fllowerCounts;
	
	/**鸡蛋数*/
	@Json(name ="egg_counts")
	public String eggCounts;
	
	/**内容大小*/
	@Json(name ="filesize")
	public String filesize;
	
	/**返回书籍详情的Url*/
	@Json(name ="wap_url")
	public String wapUrl;
	
	/**字数*/
	@Json(name ="word_counts")
	public String wordCounts;
	
	/**返回状态码*/
	@Json(name ="status")
	public String status;
	
	/**最新章节ID*/
	@Json(name ="lastest_id")
	public String lastestId;
	
	/**最新章节名称*/
	@Json(name ="lastest_name")
	public String lastestName;
	
	public boolean isOrdered;
	
	/** 普通书籍详情的 流式或版式  */
	@Json(name ="type")
	public String bookDetailType;
	
	/** 已购书籍详情的 图书类型(1、书籍，2、漫画，3、杂志，4、连载)  */
	@Json(name ="book_type")
	public int bookOrderType;

	/**存储该书籍对应的乐阅书籍id*/
	public String leyueBookId;
	
	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getShortDescriptionId() {
		return shortDescriptionId;
	}

	public void setShortDescriptionId(String shortDescriptionId) {
		this.shortDescriptionId = shortDescriptionId;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getLongDescriptionId() {
		return longDescriptionId;
	}

	public void setLongDescriptionId(String longDescriptionId) {
		this.longDescriptionId = longDescriptionId;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public int getChannelType() {
		return channelType;
	}

	public void setChannelType(int channelType) {
		this.channelType = channelType;
	}

	public String getBookNumber() {
		return bookNumber;
	}

	public void setBookNumber(String bookNumber) {
		this.bookNumber = bookNumber;
	}

	public String getChapterCounts() {
		return chapterCounts;
	}

	public void setChapterCounts(String chapterCounts) {
		this.chapterCounts = chapterCounts;
	}

	public String getReadpointPrice() {
		return readpointPrice;
	}

	public void setReadpointPrice(String readpointPrice) {
		this.readpointPrice = readpointPrice;
	}

	public String getFeePrice() {
		return PriceUtil.formatPrice(feePrice);
	}

	public void setFeePrice(String feePrice) {
		this.feePrice = feePrice;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getCoverId() {
		return coverId;
	}

	public void setCoverId(String coverId) {
		this.coverId = coverId;
	}

	public String getConverPath() {
		return converPath;
	}

	public void setConverPath(String converPath) {
		this.converPath = converPath;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}

	public String getSerialId() {
		return serialId;
	}

	public void setSerialId(String serialId) {
		this.serialId = serialId;
	}

	public String getSerialName() {
		return serialName;
	}

	public void setSerialName(String serialName) {
		this.serialName = serialName;
	}

	public boolean isIsserial() {
		return isserial;
	}

	public void setIsserial(boolean isserial) {
		this.isserial = isserial;
	}

	public boolean isFinishFlag() {
		return finishFlag;
	}

	public void setFinishFlag(boolean finishFlag) {
		this.finishFlag = finishFlag;
	}

	public boolean isHaveSerial() {
		return isHaveSerial;
	}

	public void setHaveSerial(boolean isHaveSerial) {
		this.isHaveSerial = isHaveSerial;
	}

	public boolean isDownload() {
		return isDownload;
	}

	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}

	public boolean isReserve() {
		return isReserve;
	}

	public void setReserve(boolean isReserve) {
		this.isReserve = isReserve;
	}

	public boolean isAllowComment() {
		return isAllowComment;
	}

	public void setAllowComment(boolean isAllowComment) {
		this.isAllowComment = isAllowComment;
	}

	public boolean isIsbn() {
		return isbn;
	}

	public void setIsbn(boolean isbn) {
		this.isbn = isbn;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public String getInfomation() {
		return infomation;
	}

	public void setInfomation(String infomation) {
		this.infomation = infomation;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getClickCounts() {
		return clickCounts;
	}

	public void setClickCounts(String clickCounts) {
		this.clickCounts = clickCounts;
	}

	public Integer getCommentCounts() {
		return commentCounts;
	}

	public void setCommentCounts(Integer commentCounts) {
		this.commentCounts = commentCounts;
	}

	public String getFllowerCounts() {
		return fllowerCounts;
	}

	public void setFllowerCounts(String fllowerCounts) {
		this.fllowerCounts = fllowerCounts;
	}

	public String getEggCounts() {
		return eggCounts;
	}

	public void setEggCounts(String eggCounts) {
		this.eggCounts = eggCounts;
	}

	public String getFilesize() {
		return filesize;
	}

	public void setFilesize(String filesize) {
		this.filesize = filesize;
	}

	public String getWapUrl() {
		return wapUrl;
	}

	public void setWapUrl(String wapUrl) {
		this.wapUrl = wapUrl;
	}

	public String getWordCounts() {
		return wordCounts;
	}

	public void setWordCounts(String wordCounts) {
		this.wordCounts = wordCounts;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLastestId() {
		return lastestId;
	}

	public void setLastestId(String lastestId) {
		this.lastestId = lastestId;
	}

	public String getLastestName() {
		return lastestName;
	}

	public void setLastestName(String lastestName) {
		this.lastestName = lastestName;
	}

	public boolean isOrdered() {
		return isOrdered;
	}

	public void setOrdered(boolean isOrdered) {
		this.isOrdered = isOrdered;
	}

	public String getBookDetailType() {
		return bookDetailType;
	}

	public void setBookDetailType(String bookDetailType) {
		this.bookDetailType = bookDetailType;
	}

	public int getBookOrderType() {
		return bookOrderType;
	}

	public void setBookOrderType(int bookOrderType) {
		this.bookOrderType = bookOrderType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
