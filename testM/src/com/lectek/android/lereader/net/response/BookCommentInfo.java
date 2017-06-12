package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 *  书籍评论HTTP数据结构
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public class BookCommentInfo extends BaseDao{

	@Json(name = "id")
	public int id;
	
	@Json(name = "bookId")
	public long bookId;
	
	@Json(name = "title")
	public String title;
	
	@Json(name = "content")
	public String content;
	
	@Json(name = "userId")
	public long userId;
	
	@Json(name = "username")
	public String username;
	
	@Json(name = "createTime")
	public String createTime;
	
	@Json(name = "commentSource")
	public int commentSource;
	
	@Json(name = "starsNum")
	public int starsNum;
	
	@Json(name = "commentReplyNum")
	public int commentReplyNum;
	
	@Json(name = "supportNum")
	public int supportNum;
	
	@Json(name = "manualSupportNum")
	public int manualSupportNum;
	
	@Json(name = "userIcon")
	public String userIcon;
	
	@Json(name = "isVip")
	public int isVip;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getBookId() {
		return bookId;
	}
	public void setBookId(long bookId) {
		this.bookId = bookId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public int getCommentSource() {
		return commentSource;
	}
	public void setCommentSource(int commentSource) {
		this.commentSource = commentSource;
	}
	public int getStarsNum() {
		return starsNum;
	}
	public void setStarsNum(int starsNum) {
		this.starsNum = starsNum;
	}
	public int getCommentReplyNum() {
		return commentReplyNum;
	}
	public void setCommentReplyNum(int commentReplyNum) {
		this.commentReplyNum = commentReplyNum;
	}
	public int getSupportNum() {
		return supportNum;
	}
	public void setSupportNum(int supportNum) {
		this.supportNum = supportNum;
	}
	public int getManualSupportNum() {
		return manualSupportNum;
	}
	public void setManualSupportNum(int manualSupportNum) {
		this.manualSupportNum = manualSupportNum;
	}
	
	public void setUserIcon(String userIcon){
		this.userIcon = userIcon;
	}
	
	public String getUserIcon(){
		return userIcon;
	}
	
	public void setIsVip(int isVip){
		this.isVip = isVip;
	}
	
	public int getIsVip(){
		return isVip;
	}
	
	@Override
	public String toString() {
		return "BookCommentInfo [id=" + id + ", bookId=" + bookId + ", title="
				+ title + ", content=" + content + ", userId=" + userId
				+ ", username=" + username + ", createTime=" + createTime
				+ ", commentSource=" + commentSource + ", starsNum=" + starsNum
				+ ", commentReplyNum=" + commentReplyNum + ", supportNum="
				+ supportNum + ", manualSupportNum=" + manualSupportNum + "]";
	}

}
