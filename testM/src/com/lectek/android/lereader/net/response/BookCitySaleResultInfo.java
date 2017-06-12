package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

public class BookCitySaleResultInfo extends BaseDao{
	/**
	 * 书城排行返回信息
	 * @author yyl
	 * @date 2014-7-2
	 */
	@Json(name = "rankId")
	public int rankId;//排行栏目id
	
	@Json(name = "rankName")
	public String rankName;//排行栏目名称
	
	@Json(name = "icon")
	public String icon;//图标
	
	@Json(name = "description")
	public String description;//排行描述
	
	@Json(name = "bookNum")
	public int bookNum;//书籍数量
	
	@Json(name = "sequence")
	public int sequence;//排序
	
	@Json(name = "memo")
	public String memo;//备注
	
	public int getRankId() {
		return rankId;
	}
	public void setRankId(int rankId) {
		this.rankId = rankId;
	}
	public String getRankName() {
		return rankName;
	}
	public void setRankName(String rankName) {
		this.rankName = rankName;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getBookNum() {
		return bookNum;
	}
	public void setBookNum(int bookNum) {
		this.bookNum = bookNum;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	@Override
	public String toString() {
		return "BookCitySaleResultInfo [rankId=" + rankId + ", rankName="
				+ rankName + ", icon=" + icon + ", description=" + description
				+ ", bookNum=" + bookNum + ", sequence=" + sequence + ", memo="
				+ memo + "]";
	}
	
}
