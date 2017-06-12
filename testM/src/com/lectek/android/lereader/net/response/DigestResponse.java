package com.lectek.android.lereader.net.response;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.TextUtils;

import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;

/**
 * Created by Administrator on 14-4-14.
 */
public class DigestResponse extends CommonResultInfo {

    @Json(name = "noteId")
    public String noteId;
    @Json(name = "bookId")
    public String bookId;
    @Json(name = "userId")
    public String userId;
    @Json(name = "chapterId")
    public int chapterId;
    @Json(name = "sequence")
    public int sequence;
    @Json(name = "start")
    public int start;
    @Json(name = "end")
    public int end;
    @Json(name = "sourceContent")
    public String sourceContent;
    @Json(name = "noteContent")
    public String noteContent;
    @Json(name = "clientNoteId")
    public String clientNoteId;
    @Json(name = "updateDate")
    public String updateDate;
    @Json(name = "backColor")
    public int backColor;

    @Json(name = "serial")
    public int serial;
    @Json(name = "action")
    public String action;

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getSourceContent() {
        return sourceContent;
    }

    public void setSourceContent(String sourceContent) {
        this.sourceContent = sourceContent;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getClientNoteId() {
        return clientNoteId;
    }

    public void setClientNoteId(String clientNoteId) {
        this.clientNoteId = clientNoteId;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public int getBackColor() {
        return backColor;
    }

    public void setBackColor(int backColor) {
        this.backColor = backColor;
    }

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
    
    public DigestResponse(){
    	
    }

    public DigestResponse(BookDigests digest, int serial, String userId){
        this.action = digest.getAction() == BookDigestsDB.ACTION_ADD ?"add":
                digest.getAction() == BookDigestsDB.ACTION_UPDATE ?"update":"delete";
        try{
            this.userId = userId;
            this.backColor = digest.getBGColor();
            this.bookId = digest.getContentId();
            this.chapterId = digest.getChaptersId();
            this.clientNoteId = digest.hashCode()+"";
            this.end = digest.getPosition() + digest.getCount();
            this.noteContent = digest.getMsg();
            this.noteId = digest.getServerId();
            this.sequence = digest.getChaptersId();
            this.serial = serial;
            this.sourceContent = digest.getContent();
            this.start = digest.getPosition();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            date.setTime(digest.getDate());
            this.updateDate = sdf.format(date);
        }catch (Exception e){}
    }

    /**
     * 服务端数据转化为本地数据模型
     * @return
     */
    public BookDigests toBookDigest(){
        BookDigests digest = new BookDigests();
        try {
            digest.setServerId(noteId);
            digest.setContentId(bookId);
            digest.setContent(sourceContent);
            digest.setMsg(noteContent);
            digest.setStatus(BookDigestsDB.STATUS_SYNC);
            digest.setChaptersId(chapterId);
            digest.setPosition(start);
            digest.setCount(end - start); 
            digest.setBGColor(backColor);
            if(!TextUtils.isEmpty(updateDate)){
                try{
                    digest.setDate(Long.parseLong(updateDate));
                }catch (Exception e){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = sdf.parse(updateDate);
                    digest.setDate(date.getTime());
                }
            }
        } catch (Exception e) {
        }
        return digest;
    }

}
