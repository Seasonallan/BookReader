package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;


/**
 * 获取书籍类型
 * @author donghz
 * @2014-3-21
 */
public class BookTypeInfo extends BaseDao{

   @Json(name = "bookId")
   public Integer bookId;
   
   @Json(name = "bookName")
   public String bookName;
   
   @Json(name = "author")
   public String author;
   
   @Json(name = "bookType")
   public String bookType;
   
   @Json(name = "contentFormat")
   public Integer contentFormat;
   
   @Json(name = "chargeFrom")
   public String chargeFrom;
   
   @Json(name = "chargeFromIndex")
   public String chargeFromIndex;

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
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

    public Integer getContentFormat() {
        return contentFormat;
    }

    public void setContentFormat(Integer contentFormat) {
        this.contentFormat = contentFormat;
    }

    public String getChargeFrom() {
        return chargeFrom;
    }

    public void setChargeFrom(String chargeFrom) {
        this.chargeFrom = chargeFrom;
    }

    public String getChargeFromIndex() {
        return chargeFromIndex;
    }

    public void setChargeFromIndex(String chargeFromIndex) {
        this.chargeFromIndex = chargeFromIndex;
    }
}
