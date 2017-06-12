package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 * HTTP笔记同步格式
 * @author Administrator
 *
 */
public class SyncDigestResponse extends CommonResultInfo{
	@Json(name = "serial")
    public String serial;
	@Json(name = "action")
    public String action;

    public String getSerial() {
        return serial;
    }


    public void setSerial(String serial) {
        this.serial = serial;
    }


    public String getAction() {
        return action;
    }


    public void setAction(String action) {
        this.action = action;
    }

}
