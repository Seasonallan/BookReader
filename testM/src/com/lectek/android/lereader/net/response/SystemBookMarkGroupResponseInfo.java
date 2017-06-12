package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 * Created by Administrator on 14-4-29.
 */
public class SystemBookMarkGroupResponseInfo extends BaseDao{
    @Json(name = "groupId")
    public int groupId;
    @Json(name = "userId")
    public int userId;
    @Json(name = "groupName")
    public String groupName;
    @Json(name = "isDefault")
    public int isDefault;
    @Json(name = "source")
    public int source;
    @Json(name = "sequence")
    public int sequence;
    @Json(name = "bookTotal")
    public int bookTotal;
    @Json(name = "createTime")
    public String createTime;
    @Json(name = "updateTime")
    public String updateTime;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int isDefault() {
        return isDefault;
    }

    public void setDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getBookTotal() {
        return bookTotal;
    }

    public void setBookTotal(int bookTotal) {
        this.bookTotal = bookTotal;
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
}

