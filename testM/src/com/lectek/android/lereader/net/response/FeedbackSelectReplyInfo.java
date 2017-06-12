package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

public class FeedbackSelectReplyInfo  extends BaseDao{

	@Json(name = "id")
	public Integer id;
	
	@Json(name = "content")
	public String content;
	
	@Json(name = "contentType")
	public String contentType;
	
	@Json(name = "createTime")
	public String createTime;
	
	@Json(name = "updateTime")
	public String updateTime;
	
	@Json(name = "score")
	public Integer score;
	
	@Json(name = "isClose")
	public String isClose;

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

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
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

	@Override
	public String toString() {
		return "FeedbackSelectReplyInfo [id=" + id + ", content=" + content
				+ ", contentType=" + contentType + ", createTime=" + createTime
				+ ", updateTime=" + updateTime + ", score=" + score
				+ ", isClose=" + isClose + "]";
	}
}
