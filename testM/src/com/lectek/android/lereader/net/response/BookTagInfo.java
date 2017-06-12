package com.lectek.android.lereader.net.response;

import android.R.integer;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;
/**
 * 获取书籍标签
 * @author Administrator
 *
 */
public class BookTagInfo extends BaseDao {

	@Json(name = "tagId")
	public Integer tagId;
	
	@Json(name = "tagName")
	public String tagName;
	
	
	public Integer getTagId(){
		return tagId;
	}
	
	public void setTagId(Integer tagId){
		this.tagId = tagId;
	}
	
	public String getTagName(){
		return tagName;
	}
	
	public void setTagName(String tagName){
		this.tagName = tagName;
	}
	
}
