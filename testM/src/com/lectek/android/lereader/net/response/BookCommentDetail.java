package com.lectek.android.lereader.net.response;

import java.util.ArrayList;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 *  评论详情HTTP数据结构
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public class BookCommentDetail extends BaseDao{
	
	@Json(name = "bookComment")
	public BookCommentInfo bookCommentInfo;
	
	public BookCommentInfo getBookCommentInfo() {
		return bookCommentInfo;
	}
	public void setBookCommentInfo(BookCommentInfo bookCommentInfo) {
		this.bookCommentInfo = bookCommentInfo;
	}
	@Json(name = "commentReplys",className=BookCommentReplyInfo.class)
	public ArrayList<BookCommentReplyInfo> commentReplyInfos;
	
	public ArrayList<BookCommentReplyInfo> getCommentReplyInfos() {
		return commentReplyInfos;
	}
	public void setCommentReplyInfos(
			ArrayList<BookCommentReplyInfo> commentReplyInfos) {
		this.commentReplyInfos = commentReplyInfos;
	}

}
