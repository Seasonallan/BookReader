package com.lectek.android.lereader.net.response.tianyi;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/** 章节
 * @author mingkg21
 * @email  mingkg21@gmail.com
 * @date   2013-8-23
 */
public class Chapter  extends BaseDao{
	
	/** 章节Id */
	@Json(name ="chapter_id")
	public String chpaterId;
	/** 章节名 */
	@Json(name ="chapter_name")
	public String chapterName;
	/** 章节序列 */
	@Json(name ="order")
	public int order;
	/** 0免费章节，1收费章节,2已订购 */
	@Json(name ="is_free")
	public int isFree;
	/** Wap章节信息url */
	@Json(name ="wap_chapter_url")
	public String wapChapterUrl;
	/** 返回书籍详情的URl  */
	@Json(name ="wap_url")
	public String wapUrl;
	
	/** 章节数 */
	@Json(name ="chapter_num")
	public String chapterNun;
	/** 书名 */
	@Json(name ="content_name")
	public String contentName;
	/** 章节内容 */
	@Json(name ="content")
	public String content;
	/** 下一章Id */
	@Json(name ="next_chapter_id")
	public String nextChapterId;
	/** 下一章名 */
	@Json(name ="next_chapter_name")
	public String nextChapterName;
	/** 上一章Id */
	@Json(name ="pre_chapter_id")
	public String preChapterId;
	/** 上一章名 */
	@Json(name ="pre_chapter_name")
	public String preChapterName;
	
	/**
	 * @return the chpaterId
	 */
	public String getChpaterId() {
		return chpaterId;
	}
	/**
	 * @param chpaterId the chpaterId to set
	 */
	public void setChpaterId(String chpaterId) {
		this.chpaterId = chpaterId;
	}
	/**
	 * @return the chapterName
	 */
	public String getChapterName() {
		return chapterName;
	}
	/**
	 * @param chapterName the chapterName to set
	 */
	public void setChapterName(String chapterName) {
		this.chapterName = chapterName;
	}
	/**
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}
	/**
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}
	/**
	 * @return the isFree
	 */
	public int getIsFree() {
		return isFree;
	}
	/**
	 * @param isFree the isFree to set
	 */
	public void setIsFree(int isFree) {
		this.isFree = isFree;
	}
	/**
	 * @return the wapChapterUrl
	 */
	public String getWapChapterUrl() {
		return wapChapterUrl;
	}
	/**
	 * @param wapChapterUrl the wapChapterUrl to set
	 */
	public void setWapChapterUrl(String wapChapterUrl) {
		this.wapChapterUrl = wapChapterUrl;
	}
	/**
	 * @return the wapUrl
	 */
	public String getWapUrl() {
		return wapUrl;
	}
	/**
	 * @param wapUrl the wapUrl to set
	 */
	public void setWapUrl(String wapUrl) {
		this.wapUrl = wapUrl;
	}
	/**
	 * @return the chapterNun
	 */
	public String getChapterNun() {
		return chapterNun;
	}
	/**
	 * @param chapterNun the chapterNun to set
	 */
	public void setChapterNun(String chapterNun) {
		this.chapterNun = chapterNun;
	}
	/**
	 * @return the contentName
	 */
	public String getContentName() {
		return contentName;
	}
	/**
	 * @param contentName the contentName to set
	 */
	public void setContentName(String contentName) {
		this.contentName = contentName;
	}
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the nextChapterId
	 */
	public String getNextChapterId() {
		return nextChapterId;
	}
	/**
	 * @param nextChapterId the nextChapterId to set
	 */
	public void setNextChapterId(String nextChapterId) {
		this.nextChapterId = nextChapterId;
	}
	/**
	 * @return the nextChapterName
	 */
	public String getNextChapterName() {
		return nextChapterName;
	}
	/**
	 * @param nextChapterName the nextChapterName to set
	 */
	public void setNextChapterName(String nextChapterName) {
		this.nextChapterName = nextChapterName;
	}
	/**
	 * @return the preChapterId
	 */
	public String getPreChapterId() {
		return preChapterId;
	}
	/**
	 * @param preChapterId the preChapterId to set
	 */
	public void setPreChapterId(String preChapterId) {
		this.preChapterId = preChapterId;
	}
	/**
	 * @return the preChapterName
	 */
	public String getPreChapterName() {
		return preChapterName;
	}
	/**
	 * @param preChapterName the preChapterName to set
	 */
	public void setPreChapterName(String preChapterName) {
		this.preChapterName = preChapterName;
	}

}
