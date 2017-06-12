package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;
import com.lectek.android.lereader.lib.widget.SearchKeyWordView.BaseKeyWord;

/**
 *  搜索关键字HTTP数据结构
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public class KeyWord extends BaseDao implements BaseKeyWord {
	
	@Json(name = "id")
	public int id;
	
	@Json(name = "searchContent")
	public String searchContent;
	
	@Json(name = "searchCount")
	public int searchCount;
	
	@Json(name = "searchType")
	public int searchType;
	
	@Json(name = "sourceType")
	public int sourceType;
	
	@Json(name = "sequence")
	public int sequence;
	
	@Json(name = "createTime")
	public String createTime;
	
	@Json(name = "updateTime")
	public String updateTime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSearchContent() {
		return searchContent;
	}
	public void setSearchContent(String searchContent) {
		this.searchContent = searchContent;
	}
	public int getSearchCount() {
		return searchCount;
	}
	public void setSearchCount(int searchCount) {
		this.searchCount = searchCount;
	}
	public int getSearchType() {
		return searchType;
	}
	public void setSearchType(int searchType) {
		this.searchType = searchType;
	}
	public int getSourceType() {
		return sourceType;
	}
	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
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
	@Override
	public String toString() {
		return "KeyWord [id=" + id + ", searchContent=" + searchContent
				+ ", searchCount=" + searchCount + ", searchType=" + searchType
				+ ", sourceType=" + sourceType + ", sequence=" + sequence
				+ ", createTime=" + createTime + ", updateTime=" + updateTime
				+ "]";
	}
	@Override
	public String getKeyWord() {
		return getSearchContent();
	}
}
