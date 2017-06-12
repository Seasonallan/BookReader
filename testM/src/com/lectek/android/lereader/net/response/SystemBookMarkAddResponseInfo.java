package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 * Created by Administrator on 14-4-28.
 */
public class SystemBookMarkAddResponseInfo extends BaseDao{
    //{"code":"0","desc":"成功","groupId":30,"bookMarkId":193}
    //{"code":"0","desc":"成功","bookMarkId":193}
    @Json(name = "code")
    public int code;
    @Json(name = "desc")
    public String desc;
    @Json(name = "msg")
    public String msg;
	@Json(name = "groupId")
    public int groupId;
    @Json(name = "bookMarkId")
    public String bookMarkId;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getBookMarkId() {
        return bookMarkId;
    }

    public void setBookMarkId(String bookMarkId) {
        this.bookMarkId = bookMarkId;
    }
    
    public String getMsg() {
		return msg;
	}
    public void setMsg(String msg) {
		this.msg = msg;
	}
}