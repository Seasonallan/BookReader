package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 * Created by Administrator on 14-4-29.
 */
public class SystemBookMarkResponseInfo extends BaseDao{
    @Json(name = "bookMarkId")
    public String bookMarkId;
    @Json(name = "bookId")
    public String bookId;
    @Json(name = "bookName")
    public String bookName;
    @Json(name = "coverPath")
    public String coverPath;
    @Json(name = "author")
    public String author;
    @Json(name = "chapterId")
    public int chapterId;
    @Json(name = "sequence")
    public int sequence;
    @Json(name = "position")
    public int position;
    @Json(name = "bookKey")
    public String bookKey;

    public String getBookMarkId() {
        return bookMarkId;
    }

    public void setBookMarkId(String bookMarkId) {
        this.bookMarkId = bookMarkId;
    }

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

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getBookKey() {
        return bookKey;
    }

    public void setBookKey(String bookKey) {
        this.bookKey = bookKey;
    }
}