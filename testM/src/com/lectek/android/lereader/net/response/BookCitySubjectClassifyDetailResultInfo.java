package com.lectek.android.lereader.net.response;

import java.util.Date;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

public class BookCitySubjectClassifyDetailResultInfo extends BaseDao {
	@Json(name = "bookId")
	public int bookId;
	
	@Json(name = "bookType")
	public String bookType;
	
	@Json(name = "bookName")
	public String bookName;
	
	@Json(name = "author")
	public String author;
	
	@Json(name = "introduce")
	public String introduce;
	
	@Json(name = "coverPath")
	public String coverPath;
	
	@Json(name = "isPublish")
	public String isPublish;
	
	@Json(name = "isFee")
	public String isFee;
	
	@Json(name = "feeType")
	public String feeType;
	
	@Json(name = "price")
	public double price;
	
	@Json(name = "promotionPrice")
	public double promotionPrice;
	
	@Json(name = "recordTime")
	public Date recordTime;
	
	@Json(name = "auditStatus")
	public String auditStatus;
	
	@Json(name = "deleteStatus")
	public String deleteStatus;
	
	@Json(name = "status")
	public String status;
	
	@Json(name = "outBookId")
	public String outBookId;
	
	@Json(name = "sourceType")
	public int sourceType;

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

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
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

	public double getPromotionPrice() {
		return promotionPrice;
	}

	public void setPromotionPrice(double promotionPrice) {
		this.promotionPrice = promotionPrice;
	}

	public Date getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(Date recordTime) {
		this.recordTime = recordTime;
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

	public String getOutBookId() {
		return outBookId;
	}

	public void setOutBookId(String outBookId) {
		this.outBookId = outBookId;
	}

	public int getSourceType() {
		return sourceType;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}

	@Override
	public String toString() {
		return "BookCitySubjectClassifyDetailResultInfo [bookId=" + bookId
				+ ", bookType=" + bookType + ", bookName=" + bookName
				+ ", author=" + author + ", introduce=" + introduce
				+ ", coverPath=" + coverPath + ", isPublish=" + isPublish
				+ ", isFee=" + isFee + ", feeType=" + feeType + ", price="
				+ price + ", promotionPrice=" + promotionPrice
				+ ", recordTime=" + recordTime + ", auditStatus=" + auditStatus
				+ ", deleteStatus=" + deleteStatus + ", status=" + status
				+ ", outBookId=" + outBookId + ", sourceType=" + sourceType
				+ "]";
	}
	
}
