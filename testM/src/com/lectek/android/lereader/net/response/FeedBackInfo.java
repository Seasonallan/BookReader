package com.lectek.android.lereader.net.response;

import java.util.ArrayList;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 * 获取反馈列表
 * @author donghz
 * @2014-3-21
 */
public class FeedBackInfo extends BaseDao{

   @Json(name = "id")
   public Integer id;
   
   @Json(name = "content")
   public String content;
   
   @Json(name = "createTime")
   public String createTime;
   
   @Json(name = "updateTime")
   public String updateTime;
   
   @Json(name = "score")
   public Integer score;
   
   @Json(name = "isClose")
   public String isClose;
   
   @Json(name = "contentType")
   public String contentType;
   
   @Json(name = "userReplyList", className= FeedBackUserReplyInfos.class)
   public  ArrayList<FeedBackUserReplyInfos>  feedBackUserReplyInfos;

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	public String getUpdateTime() {
		return updateTime;
	}
	
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
	public Integer getScore() {
		return score;
	}
	
	public void setScore(Integer score) {
		this.score = score;
	}
	
	public String getIsClose() {
		return isClose;
	}
	
	public void setIsClose(String isClose) {
		this.isClose = isClose;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public ArrayList<FeedBackUserReplyInfos> getFeedBackUserReplyInfos() {
		return feedBackUserReplyInfos;
	}
	
	public void setFeedBackUserReplyInfos(
			ArrayList<FeedBackUserReplyInfos> feedBackUserReplyInfos) {
		this.feedBackUserReplyInfos = feedBackUserReplyInfos;
	}

	@Override
	public String toString() {
		return "FeedBackInfo [id=" + id + ", content=" + content
				+ ", createTime=" + createTime + ", updateTime=" + updateTime
				+ ", score=" + score + ", isClose=" + isClose
				+ ", contentType=" + contentType + ", feedBackUserReplyInfos="
				+ feedBackUserReplyInfos + "]";
	}

}
