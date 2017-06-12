package com.lectek.android.lereader.ui.basereader_leyue;

import java.io.Serializable;
/**
 * 包含打开一本书所需要的信息
 * @author lyw
 *
 */
public class Book implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6567421128337632467L;
	private String bookId;
	private String bookName;
	private String author;
	private String bookType;
	private String path;
	private boolean isOrder;
	private String feeStart;
	private String price;
	private String promotionPrice;
	private String limitPrice;

	private String coverPath;
	private boolean isDownloadFullVersonBook;
	private String bookFormatType;
    private boolean isOnline;

	public String getBookId() {
		return bookId;
	}
	
	public void setBookId(String bookId) {
		this.bookId = bookId;
	}
	
	public String getCoverPath() {
		return coverPath;
	}

	public void setCoverPath(String coverPath) {
		this.coverPath = coverPath;
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isOrder() {
		return isOrder;
	}

	public void setOrder(boolean isOrder) {
		this.isOrder = isOrder;
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
	
	public String getLimitPrice() {
		return limitPrice;
	}

	public void setLimitPrice(String limitPrice) {
		this.limitPrice = limitPrice;
	}

	public String getFeeStart() {
		return feeStart;
	}

	public void setFeeStart(String feeStart) {
		this.feeStart = feeStart;
	}
	
	public boolean isDownloadFullVersonBook() {
		return isDownloadFullVersonBook;
	}

	public void setDownloadFullVersonBook(boolean isDownloadFullVersonBook) {
		this.isDownloadFullVersonBook = isDownloadFullVersonBook;
	}

	public String getBookFormatType() {
		return bookFormatType;
	}

	public void setBookFormatType(String bookFormatType) {
		this.bookFormatType = bookFormatType;
	}

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

}
