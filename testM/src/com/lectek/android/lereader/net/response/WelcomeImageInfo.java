package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 *  欢迎界面图片HTTP数据结构
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public class WelcomeImageInfo extends BaseDao{
	
	@Json(name = "id")
	public String id;
	
	@Json(name = "name")
	public String name;
	
	@Json(name = "startTime")
	public String startTime;
	
	@Json(name = "endTime")
	public String endTime;
	
	@Json(name = "status")
	public String status;//0 待上线，1上线，2 暂停，3 下线
	
	@Json(name = "path")
	public String path;
	
	public String picCode;
	
	public String getPicCode() {
		return picCode;
	}
	public void setPicCode(String picCode) {
		this.picCode = picCode;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	/**
	 * 获取图片状态
	 * @return //0 待上线，1上线，2 暂停，3 下线
	 */
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

}
