package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 * 收藏列表返回信息
 * @author  donghz
 * @date 2014-3-27
 */
public class CollectResultBookInfo extends BaseDao {

	public static final long serialVersionUID = -5768894329690489235L;
	
	@Json(name = "bookId")
	public Integer bookId;
	
	@Json(name = "outBookId")
	public String outBookId;

	@Json(name = "bookName")
	public String bookName;
	
	@Json(name = "author")
	public String author;
	
	@Json(name = "bookType")
	public String bookType;
	
	@Json(name = "introduce")
	public String introduce;
	
	@Json(name = "coverPath")
	public String coverPath;
	
	@Json(name = "isFee")
	public String isFee;
	
	@Json(name = "price")
	public String price;
	
	@Json(name = "feeType")
	public String feeType;
	
	@Json(name = "feeStart")
	public String feeStart;
	
	@Json(name = "wordNum")
	public Integer wordNum;
	
	@Json(name = "fileSize")
	public long fileSize;
	
	@Json(name = "fileName")
	public String fileName;
	
	@Json(name = "filePath")
	public String filePath;
	
	@Json(name = "tryFileSize")
	public long tryFileSize;
	
	@Json(name = "tryFileName")
	public String tryFileName;
	
	@Json(name = "tryFilePath")
	public String tryFilePath;
	
	@Json(name = "isOrder")
	public boolean isOrder;
	
	@Json(name = "updateStatus")
	public String updateStatus;
	
	@Json(name = "serialProp")
	public String serialProp;
	
	@Json(name = "bookCatalogueNum")
	public int bookCatalogueNum;
	
	@Json(name = "cpid")
	public int cpid;
	
	@Json(name = "publisher")
	public String publisher;

	public Integer getBookId() {
		return bookId;
	}

	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}
	
	public String getOutBookId() {
		return outBookId;
	}

	public void setOutBookId(String outBookId) {
		this.outBookId = outBookId;
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

	public String getBookType() {
		return bookType;
	}

	public void setBookType(String bookType) {
		this.bookType = bookType;
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

	public String getIsFee() {
		return isFee;
	}

	public void setIsFee(String isFee) {
		this.isFee = isFee;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getFeeStart() {
		return feeStart;
	}

	public void setFeeStart(String feeStart) {
		this.feeStart = feeStart;
	}

	public Integer getWordNum() {
		return wordNum;
	}

	public void setWordNum(Integer wordNum) {
		this.wordNum = wordNum;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public long getTryFileSize() {
		return tryFileSize;
	}

	public void setTryFileSize(long tryFileSize) {
		this.tryFileSize = tryFileSize;
	}

	public String getTryFileName() {
		return tryFileName;
	}

	public void setTryFileName(String tryFileName) {
		this.tryFileName = tryFileName;
	}

	public String getTryFilePath() {
		return tryFilePath;
	}

	public void setTryFilePath(String tryFilePath) {
		this.tryFilePath = tryFilePath;
	}

	public boolean isOrder() {
		return isOrder;
	}

	public void setOrder(boolean isOrder) {
		this.isOrder = isOrder;
	}

	public String getUpdateStatus() {
		return updateStatus;
	}

	public void setUpdateStatus(String updateStatus) {
		this.updateStatus = updateStatus;
	}

	public String getSerialProp() {
		return serialProp;
	}

	public void setSerialProp(String serialProp) {
		this.serialProp = serialProp;
	}

	public int getBookCatalogueNum() {
		return bookCatalogueNum;
	}

	public void setBookCatalogueNum(int bookCatalogueNum) {
		this.bookCatalogueNum = bookCatalogueNum;
	}

	public int getCpid() {
		return cpid;
	}

	public void setCpid(int cpid) {
		this.cpid = cpid;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	@Override
	public String toString() {
		return "CollectResultBookInfo [bookId=" + bookId + "outBookId="+ outBookId +", bookName="
				+ bookName + ", author=" + author + ", bookType=" + bookType
				+ ", introduce=" + introduce + ", coverPath=" + coverPath
				+ ", isFee=" + isFee + ", price=" + price + ", feeType="
				+ feeType + ", feeStart=" + feeStart + ", wordNum=" + wordNum
				+ ", fileSize=" + fileSize + ", fileName=" + fileName
				+ ", filePath=" + filePath + ", tryFileSize=" + tryFileSize
				+ ", tryFileName=" + tryFileName + ", tryFilePath="
				+ tryFilePath + ", isOrder=" + isOrder + ", updateStatus="
				+ updateStatus + ", serialProp=" + serialProp
				+ ", bookCatalogueNum=" + bookCatalogueNum + ", cpid=" + cpid
				+ ", publisher=" + publisher + "]";
	}

}
