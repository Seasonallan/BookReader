package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;

/**
 * HTTP请求模型
 */
public class BookMarkResponse extends BaseDao{

	
	public BookMarkResponse(){}
	
    public BookMarkResponse(BookMark bookMark, int i, String userId){

        position = bookMark.getPosition();
        chapterId = bookMark.getChapterID();
        bookId = bookMark.getContentID();
        tagName = bookMark.getBookmarkName();
        userTagId = bookMark.getBookmarkID();
        action = bookMark.getSoftDelete() == DBConfig.BOOKMARK_STATUS_SOFT_DELETE ? "delete":"add";
        clientTagId = i;
        this.userId = userId;
    }

    /**
     * 转化为书签
     * @return
     */
    public BookMark toBookMark(){
        BookMark bookMark = new BookMark();
        bookMark.setPosition(position);
        bookMark.setChapterID(chapterId);
        bookMark.setContentID(bookId);
        bookMark.setStatus(BookDigestsDB.STATUS_SYNC);
        bookMark.setSoftDelete(DBConfig.BOOKMARK_STATUS_SOFT_DELETE_NO);
        bookMark.setBookmarkName(tagName);
        bookMark.setBookmarkID(userTagId);
        bookMark.setId(clientTagId);
        bookMark.setUserID(userId);
        return bookMark;
    }

    @Json(name = "userBookTagId")
    public String userBookTagId;

    @Json(name = "userTagId")
    public String userTagId;

    @Json(name = "userId")
    public String userId;

    @Json(name = "bookId")
    public String bookId;

    @Json(name = "tagName")
    public String tagName;

    @Json(name = "chapterId")
    public int chapterId;

    @Json(name = "sourceType")
    public String sourceType;

    @Json(name = "position")
    public int position;

    @Json(name = "action")
    public String action;

    @Json(name = "clientTagId")
    public int clientTagId;

    public String getUserBookTagId() {
        return userBookTagId;
    }

    public void setUserBookTagId(String userBookTagId) {
        this.userBookTagId = userBookTagId;
    }

    public String getUserTagId() {
        return userTagId;
    }

    public void setUserTagId(String userTagId) {
        this.userTagId = userTagId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getClientTagId() {
        return clientTagId;
    }

    public void setClientTagId(int clientTagId) {
        this.clientTagId = clientTagId;
    }
}
