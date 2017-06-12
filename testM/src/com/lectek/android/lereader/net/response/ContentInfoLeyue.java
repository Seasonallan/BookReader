/*
 * ========================================================
 * ClassName:ContentInfoLeyue.java* 
 * Description:
 * Copyright (C) 
 * ========================================================
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *===================================================================*
 * Revision History
 *
 * Modification                    Tracking
 * Date         Author          Number       Description of changes
 *____________________________________________________________________
 * 
 * 2013-9-28     chendt          #00000       create
 */
package com.lectek.android.lereader.net.response;

import java.io.Serializable;
import java.util.Date;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 * 书籍信息HTTP数据结构
 * 
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public class ContentInfoLeyue extends BaseDao implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3857887897750434570L;

	@Json(name = "bookId")
	public String bookId;

	@Json(name = "bookName")
	public String bookName;

	@Json(name = "author")
	public String author;

	@Json(name = "bookType")
	public String bookType;

	

	@Json(name = "introduce")
	public String introduce;

	/** 是否收费（0：免费，1：收费，2：优惠） */
	@Json(name = "isFee")
	public String isFee;

	@Json(name = "coverPath")
	public String coverPath;

	@Json(name = "price")
	public String price;

	/** 优惠价 */
	@Json(name = "promotionPrice")
	public String promotionPrice;

	/** 收费方式（0：按本，1：按章） */
	@Json(name = "feeType")
	public String feeType;

	/** 收费起点 */
	@Json(name = "feeStart")
	public String feeStart;

	@Json(name = "fileSize")
	public long fileSize;

	@Json(name = "fileName")
	public String fileName;

	@Json(name = "filePath")
	public String filePath;

	@Json(name = "tryFileSize")
	// 试读文件大小
	public long tryFileSize;

	@Json(name = "tryFileName")
	public String tryFileName;

	@Json(name = "tryFilePath")
	public String tryFilePath;

	@Json(name = "wordNum")
	public String wordNum;

	@Json(name = "isOrder")
	public boolean isOrder;

	@Json(name = "updateStatus")
	public String updateStatus;// 更新状态

	/** 为输出json到本地而加 */
	@Json(name = "secretKey")
	public String tempSecretKey;

	/**
	 * 出版时间
	 */
	@Json(name = "publishDate")
	public Date publishDate;
	
	/**
	 * 出版社或出版商
	 * @return
	 */
	@Json(name = "publisher")
	public String publisher;
	
	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	
	public Date getPublishDate(){
		return publishDate;
	}
	
	public void setPublishDate(Date publishDate){
		this.publishDate = publishDate;
	}
	
	
	public Double getStarLevel() {
		return starLevel;
	}

	public void setStarLevel(Double starLevel) {
		this.starLevel = starLevel;
	}

	public String getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(String commentCount) {
		this.commentCount = commentCount;
	}

	@Json(name = "cpid")
	private int cpid;

	@Json(name = "starLevel")
	public Double starLevel;
	
	@Json(name = "commentCount")
	public String commentCount;

	/**
	 * @return the cpid
	 */
	public int getCpid() {
		return cpid;
	}

	/**
	 * 限免类型（1、限免，2限价）
	 */
	@Json(name = "limitType")
	public Integer limitType;

	/**
	 * 限免价格
	 */
	@Json(name = "limitPrice")
	public Double limitPrice;

	@Json(name = "startTime")
	public String startTime;
	
	@Json(name = "endTime")
	public String endTime;
	
	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
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

	public String getIsFee() {
		return isFee;
	}

	public void setIsFee(String isFee) {
		this.isFee = isFee;
	}

	public String getCoverPath() {
		return coverPath;
	}

	public void setCoverPath(String coverPath) {
		this.coverPath = coverPath;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getPromotionPrice() {
		return promotionPrice;
	}

	public void setPromotionPrice(String promotionPrice) {
		this.promotionPrice = promotionPrice;
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

	public String getWordNum() {
		return wordNum;
	}

	public void setWordNum(String wordNum) {
		this.wordNum = wordNum;
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

	public Integer getLimitType() {
		return limitType;
	}

	public void setLimitType(Integer limitType) {
		this.limitType = limitType;
	}

	public Double getLimitPrice() {
		return limitPrice;
	}

	public void setLimitPrice(Double limitPrice) {
		this.limitPrice = limitPrice;
	}

	public String getLimitStartTime(){
		return startTime;
	}
	
	public void setLimitStartTime(String stratTime){
		this.startTime = stratTime;
	}
	
	public String getLimitEndTime(){
		return endTime;
	}
	
	public void setLimitEndTime(String endTime){
		this.endTime = endTime;
	}
	
	/*
	 * @Json(name = "contentType") public String contentType;
	 */

	/**
	 * 天翼书籍ID
	 */
	@Json(name = "outBookId")
	public String outBookId;

	public String getOutBookId() {
		return outBookId;
	}

	public void setOutBookId(String outBookId) {
		this.outBookId = outBookId;
	}
	@Override
	public String toString() {
		return "ContentInfoLeyue [bookId=" + bookId + ", bookName=" + bookName
				+ ", author=" + author + ", bookType=" + bookType
				+ ", introduce=" + introduce + ", isFee=" + isFee
				+ ", coverPath=" + coverPath + ", price=" + price
				+ ", promotionPrice=" + promotionPrice + ", feeType=" + feeType
				+ ", feeStart=" + feeStart + ", fileSize=" + fileSize
				+ ", fileName=" + fileName + ", filePath=" + filePath
				+ ", tryFileSize=" + tryFileSize + ", tryFileName="
				+ tryFileName + ", tryFilePath=" + tryFilePath + ", wordNum="
				+ wordNum + ", isOrder=" + isOrder + ", updateStatus="
				+ updateStatus + ", tempSecretKey=" + tempSecretKey + ", cpid="
				+ cpid + ", starLevel=" + starLevel + ", commentCount="
				+ commentCount + ", limitType=" + limitType + ", limitPrice="
				+ limitPrice + ", outBookId=" + outBookId + "]";
	}
}
