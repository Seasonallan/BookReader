package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

public class BookCityClassifyResultInfo extends BaseDao{
	/**
	 * 书城分类返回信息
	 * @author yyl
	 * @date 2014-7-3
	 */
	@Json(name = "id")
	public int id;//类别ID
	
	@Json(name = "name")
	public String name;//名称
	
	@Json(name = "parentId")
	public int parentId;//父节点
	
	@Json(name = "icon")
	public String icon;//分类图标
	
	@Json(name = "level")
	public int level;//级别
	
	@Json(name = "quantity")
	public int quantity;//书本数量
	
	@Json(name = "memo")
	public String memo;//分类备注
	
	@Json(name = "introduce")
	public String introduce;//分类介绍
	
	@Json(name = "sequence")
	public int sequence	;//序号
	
	@Json(name = "status")
	public String status;//状态（0：下线，1：上线）

	@Override
	public String toString() {
		return "BookCityClassifyResultInfo [id=" + id + ", name=" + name
				+ ", parentId=" + parentId + ", icon=" + icon + ", level="
				+ level + ", quantity=" + quantity + ", memo=" + memo
				+ ", introduce=" + introduce + ", sequence=" + sequence
				+ ", status=" + status + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
