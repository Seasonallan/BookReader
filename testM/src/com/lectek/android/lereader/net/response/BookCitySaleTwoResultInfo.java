package com.lectek.android.lereader.net.response;

import java.util.ArrayList;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

public class BookCitySaleTwoResultInfo extends BaseDao{
	/**
	 * 书城排行二级目录返回信息
	 * @author yyl
	 * @date 2014-7-2
	 */
//	
//	 ArrayList<TBook> list=new ArrayList<TBook>();
//
//	@Override
//	public String toString() {
//		return list.toString();
//	}

	@Json(name = "bookId")
	public int bookId;// 书籍ID
	
	@Json(name = "bookName")
	public String bookName;// 书名
	
	@Json(name = "author")
	public String author;// 作者
	
	@Json(name = "bookType")
	public String bookType;// 书籍分类
	
	@Json(name = "coverPath")
	public String coverPath;// 封面图片路径
	
	@Json(name = "price")
	public double price;// 全本价格
	
	@Json(name = "feeType")
	public String feeType;// 收费方式（0：按本，1：按章）
	
	@Json(name = "promotionPrice")
	public String promotionPrice;// 优惠价
	
	@Json(name = "isFee")
	public String isFee;// 是否收费（0：免费，1：收费，2：优惠）
	
	@Json(name = "sourceType")
	public int sourceType;// 来源（0：乐阅平台，1：天翼阅读）
	
	@Json(name = "outBookId")
	public String outBookId;// 外部系统书籍ID
	
	public int getBookId() {
		return bookId;
	}
	public void setBookId(int bookId) {
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
	public String getCoverPath() {
		return coverPath;
	}
	public void setCoverPath(String coverPath) {
		this.coverPath = coverPath;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getFeeType() {
		return feeType;
	}
	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}
	public String getPromotionPrice() {
		return promotionPrice;
	}
	public void setPromotionPrice(String promotionPrice) {
		this.promotionPrice = promotionPrice;
	}
	public String getIsFee() {
		return isFee;
	}
	public void setIsFee(String isFee) {
		this.isFee = isFee;
	}
	public int getSourceType() {
		return sourceType;
	}
	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}
	public String getOutBookId() {
		return outBookId;
	}
	public void setOutBookId(String outBookId) {
		this.outBookId = outBookId;
	}
	@Override
	public String toString() {
		return "BookCitySaleTwoResultInfo [bookId=" + bookId + ", bookName="
				+ bookName + ", author=" + author + ", bookType=" + bookType
				+ ", coverPath=" + coverPath + ", price=" + price
				+ ", feeType=" + feeType + ", promotionPrice=" + promotionPrice
				+ ", isFee=" + isFee + ", sourceType=" + sourceType
				+ ", outBookId=" + outBookId + "]";
	}
 
	

}
