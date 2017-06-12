package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 *  书籍目录HTTP数据结构
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public class BookCatalog extends BaseDao{

	@Json(name = "id")
	public int id;

	@Json(name = "bookId")
	public String bookId;
	
	@Json(name = "pid")
	public int pid;
	
	@Json(name = "name")
	public String name;
	
	@Json(name = "introduce")
	public String introduce;
	
	@Json(name = "sequence")
	public int sequence;
	
	@Json(name = "path")
	public String path;
	
	@Json(name = "price")
	public double price;
	
	@Json(name = "status")
	public String status;
	
	@Json(name = "auditStatus")
	public String auditStatus;
	
	@Json(name = "filterStatus")
	public String filterStatus;
	
	@Json(name = "calpoint")
	public String calpoint;
	
	@Json(name = "createDate")
	public String createDate;
	
	@Json(name = "updateDate")
	public String updateDate;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getFilterStatus() {
		return filterStatus;
	}

	public void setFilterStatus(String filterStatus) {
		this.filterStatus = filterStatus;
	}

	public String getCalpoint() {
		return calpoint;
	}

	public void setCalpoint(String calpoint) {
		this.calpoint = calpoint;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	@Override
	public String toString() {
		return "BookCatalog [id=" + id + ", bookId=" + bookId + ", pid=" + pid
				+ ", name=" + name + ", introduce=" + introduce + ", sequence="
				+ sequence + ", path=" + path + ", price=" + price
				+ ", status=" + status + ", auditStatus=" + auditStatus
				+ ", filterStatus=" + filterStatus + ", calpoint=" + calpoint
				+ ", createDate=" + createDate + ", updateDate=" + updateDate
				+ "]";
	}
	
}
