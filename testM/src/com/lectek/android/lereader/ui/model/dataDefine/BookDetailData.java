package com.lectek.android.lereader.ui.model.dataDefine;

import java.util.ArrayList;

import com.lectek.android.lereader.net.response.BookCommentInfo;
import com.lectek.android.lereader.net.response.BookTagInfo;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.net.response.tianyi.ContentInfo;

public class BookDetailData {

	/**
	 * 书籍信息
	 */
	public ContentInfoLeyue bookInfo;
	/**
	 * 书籍评论
	 */
	public ArrayList<BookCommentInfo> bookComments;
	/**
	 * 推荐书籍
	 */
	public ArrayList<ContentInfoLeyue> recommendBooks;
	/**
	 * 天翼书籍详情
	 */
	
	public ContentInfo tyBookInfo;
	
	/**
	 * 书籍标签
	 */
	public ArrayList<BookTagInfo> tagInfos;
	
}
