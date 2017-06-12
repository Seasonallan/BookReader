package com.lectek.android.lereader.net.response;

import java.util.ArrayList;
import java.util.Date;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

public class BookCityClassifyResultDetailInfo extends BaseDao {
	/**
	 * 书城二级分类返回信息
	 * 
	 * @author yyl
	 * @date 2014-7-3
	 */
	@Json(name = "bookId")
	public int bookId;// 类别ID

	@Json(name = "bookType")
	public String bookType;// 名称

	@Json(name = "bookName")
	public String bookName;// 名称

	@Json(name = "author")
	public String author;// 作者

	@Json(name = "serialProp")
	public String serialProp;//

	@Json(name = "isAccreditForever")
	public String isAccreditForever;//

	@Json(name = "introduce")
	public String introduce;//

	@Json(name = "keyword")
	public String keyword;//

	@Json(name = "coverPath")
	public String coverPath;//

	@Json(name = "isPublish")
	public String isPublish;//

	@Json(name = "isFee")
	public String isFee;//

	@Json(name = "feeType")
	public String feeType;//

	@Json(name = "price")
	public double price;//

	@Json(name = "feeStart")
	public String feeStart;//

	@Json(name = "recordTime")
	public Date recordTime;//

	@Json(name = "updateTime")
	public Date updateTime;//

	@Json(name = "auditStatus")
	public String auditStatus;//

	@Json(name = "deleteStatus")
	public String deleteStatus;//

	@Json(name = "status")
	public String status;//

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public String getBookType() {
		return bookType;
	}

	public void setBookType(String bookType) {
		this.bookType = bookType;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getSerialProp() {
		return serialProp;
	}

	public void setSerialProp(String serialProp) {
		this.serialProp = serialProp;
	}

	public String getIsAccreditForever() {
		return isAccreditForever;
	}

	public void setIsAccreditForever(String isAccreditForever) {
		this.isAccreditForever = isAccreditForever;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getCoverPath() {
		return coverPath;
	}

	public void setCoverPath(String coverPath) {
		this.coverPath = coverPath;
	}

	public String getIsPublish() {
		return isPublish;
	}

	public void setIsPublish(String isPublish) {
		this.isPublish = isPublish;
	}

	public String getIsFee() {
		return isFee;
	}

	public void setIsFee(String isFee) {
		this.isFee = isFee;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getFeeStart() {
		return feeStart;
	}

	public void setFeeStart(String feeStart) {
		this.feeStart = feeStart;
	}

	 

	public Date getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(Date recordTime) {
		this.recordTime = recordTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(String deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "BookCityClassifyResultDetailInfo [bookId=" + bookId
				+ ", bookType=" + bookType + ", bookName=" + bookName
				+ ", author=" + author + ", serialProp=" + serialProp
				+ ", isAccreditForever=" + isAccreditForever + ", introduce="
				+ introduce + ", keyword=" + keyword + ", coverPath="
				+ coverPath + ", isPublish=" + isPublish + ", isFee=" + isFee
				+ ", feeType=" + feeType + ", price=" + price + ", feeStart="
				+ feeStart + ", recordTime=" + recordTime + ", updateTime="
				+ updateTime + ", auditStatus=" + auditStatus
				+ ", deleteStatus=" + deleteStatus + ", status=" + status + "]";
	}

}
