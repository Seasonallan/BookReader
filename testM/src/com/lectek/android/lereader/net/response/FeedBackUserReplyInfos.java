package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;


public class FeedBackUserReplyInfos  extends BaseDao{

    @Json(name = "id")
    public Integer id;

    @Json(name = "feedbackId")
    public Integer feedbackId;

    @Json(name = "content")
    public String content;

    @Json(name = "contentType")
    public String contentType;

    @Json(name = "userType")
    public String userType;

    @Json(name = "userId")
    public Integer userId;

    @Json(name = "imei")
    public String imei;

    @Json(name = "createTime")
    public String createTime;

    public String isRead;

    public Integer getId() {
        return id;
    }

    public Integer getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Integer feedbackId) {
        this.feedbackId = feedbackId;
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "FeedBackUserReplyInfos [id=" + id + ", feedbackId="
                + feedbackId + ", content=" + content + ", contentType="
                + contentType + ", userType=" + userType + ", userId=" + userId
                + ", imei=" + imei + ", createTime=" + createTime + ", isRead="
                + isRead + "]";
    }
}
