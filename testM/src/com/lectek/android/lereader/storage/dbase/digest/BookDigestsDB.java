package com.lectek.android.lereader.storage.dbase.digest;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.database.Cursor;
import android.text.TextUtils;

import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.binding.model.bookdigest.BookDigestModel;
import com.lectek.android.lereader.lib.storage.dbase.JsonArrayList;
import com.lectek.android.lereader.permanent.DBConfig;

/**
 * 笔记数据库管理类
 * @author ljp
 */
public class BookDigestsDB{ 
    
	private static BookDigestsDB mBookDigestsDB; 
 
    /**
     * 书籍ID
     */
    private static final String CONTENT_ID = "content_id";
    /**
     * 章节ID
     */
    private static final String CHAPTERS_ID = "chapters_id";
   
    /**
     * 同步状态
     */
    public static final String STATUS = "status";
    //状态：未上传
    public static final int STATUS_LOCAL = -1;
    //状态：已上传
    public static final int STATUS_SYNC = 1;
    //动作：添加
    public static final int ACTION_ADD = -1;
    //动作：删除
    public static final int ACTION_DEL = 0;
    //动作：更新
    public static final int ACTION_UPDATE = 1; 

    public static BookDigestsDB getInstance(){
        if(mBookDigestsDB == null){
            mBookDigestsDB = new BookDigestsDB();
        }
        return mBookDigestsDB;
    }

    private ContentResolver getDatabase() {
		return BaseApplication.getInstance().getContentResolver();
	}


    /**
     * 获取某本书籍未同步笔记列表
     * @return
     */
    public ArrayList<BookDigests> getSyncBookDigests(String contentId) {
        Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_DIGEST, null, STATUS + " = " + STATUS_LOCAL
                + " and " + CONTENT_ID + " = " + contentId, null, null);
        return new JsonArrayList<BookDigests>(BookDigests.class).fromCursor(cursor);

    }
    /**
     * 获取未同步笔记列表
     * @return
     */
    public ArrayList<BookDigests> getSyncBookDigests() {
        Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_DIGEST, null, STATUS +" = " +STATUS_LOCAL, null, null);
        return new JsonArrayList<BookDigests>(BookDigests.class).fromCursor(cursor);

    }

    private String getWhere(String contentId ,int chaptersId) {
        String where = "";
        where += CONTENT_ID + " = '" + contentId + "'";
        if(chaptersId != -1){
            where += " AND " + CHAPTERS_ID + "=" + chaptersId ;
        }
        return where;
    } 
 
    public ArrayList<BookDigests> getListBookDigests(String contentId) {
        return getListBookDigests(contentId,-1);

    }


    public ArrayList<BookDigests> getListBookDigests(String contentId ,int chaptersId) {
        ArrayList<BookDigests> list = new ArrayList<BookDigests>();
        if(contentId == null){
            return null;
        }
        BookDigests bookDigests;
        Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_DIGEST, null, getWhere(contentId,chaptersId), null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    bookDigests = new BookDigests();
                    bookDigests.fromCursor(cursor);
                    bookDigests.setBGColor(bookDigests.mBGColor);
                    if(bookDigests.getAction() != ACTION_DEL){
                        list.add(bookDigests);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            cursor = null;
        }

        return list;

    }

    public ArrayList<BookDigests> getListBookDigests() {
        ArrayList<BookDigests> list = new ArrayList<BookDigests>();

        BookDigests bookDigests;
        Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_DIGEST, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    bookDigests = new BookDigests();
                    bookDigests.fromCursor(cursor);
                    bookDigests.setBGColor(bookDigests.mBGColor);
                    if(bookDigests.getAction() != ACTION_DEL && !TextUtils.isEmpty(bookDigests.getContentId())){
                        list.add(bookDigests);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            cursor = null;
        }

        return list;

    }

    public BookDigests getBookDigests(BookDigests digest) {
        BookDigests digests = null;
        Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_DIGEST, null, digest.getPrimaryKeyWhereClause(), null, null);
        if (cursor != null && cursor.moveToFirst()){
            digests = new BookDigests();
            digests.fromCursor(cursor);
            digests.setBGColor(digests.mBGColor);
        }
        return digests;

    }
 
    /**
     * 是否存在某书签
     * @param bookDigest
     * @return
     */
    public boolean hasBookDigests(BookDigests bookDigest) {
        Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_DIGEST, null, bookDigest.getPrimaryKeyWhereClause(), null, null);
        if (cursor != null && cursor.moveToFirst()){
            return true;
        }
    	return false;
    }

    public void saveOrUpdateBookDigest(BookDigests bookDigest){
        saveOrUpdateBookDigest(bookDigest, true);
    }

    public void saveOrUpdateBookDigest(BookDigests bookDigest, boolean sync){
        if(bookDigest == null){
            return;
        } 
        if(hasBookDigests(bookDigest)){
            updateBookDigest(bookDigest,sync);
        }else{
            saveBookDigest(bookDigest,sync);
        }
    }


    public long saveBookDigest(BookDigests bookDigest){
        return saveBookDigest(bookDigest, true);
    }

    public long saveBookDigest(BookDigests bookDigest, boolean sync){
        long rowID = 1;
        if(bookDigest == null){
            return rowID;
        }
        getDatabase().insert(DBConfig.CONTENT_URI_DIGEST, bookDigest.toContentValues());
        if(rowID > 0 && sync){
            bookDigest.setAction(ACTION_ADD);
            BookDigestModel.getInstance().addBookDigest(bookDigest);
        }
        return rowID;
    }

    public int updateBookDigest(BookDigests bookDigest){
        return updateBookDigest(bookDigest, true);
    }

    public int updateBookDigest(BookDigests bookDigest, boolean sync){
        int count = 0;
        if(bookDigest == null){
            return count;
        }
        count = getDatabase().update(DBConfig.CONTENT_URI_DIGEST, bookDigest.toContentValues(), bookDigest.getPrimaryKeyWhereClause(), null);
        if(count > 0 && sync){
            bookDigest.setAction(ACTION_UPDATE);
            BookDigestModel.getInstance().updateBookDigest(bookDigest);
        }
        return count;
    }

    public int deleteBookDigest(BookDigests bookDigest){
        return deleteBookDigest(bookDigest, true);
    }

    public int deleteBookDigest(BookDigests bookDigest, boolean sync){
        if(!sync){
            int count = 0;
            if(bookDigest == null){
                return count;
            } 
            count = getDatabase().delete(DBConfig.CONTENT_URI_DIGEST, bookDigest.getPrimaryKeyWhereClause(), null);//getWritableDatabase().delete(TABLE_NAME, getWhere(id), null);
            
            return count;
        }else{
            bookDigest.setAction(ACTION_DEL);
            int res = updateBookDigest(bookDigest, false);
            if(res > 0 && sync){
                BookDigestModel.getInstance().deleteBookDigest(bookDigest);
            }
            return res;
        }
    }

    public int deleteBookDigestAll(ArrayList<BookDigests> bookDigestsList){
        int count = 0;
        if(bookDigestsList == null || bookDigestsList.size() == 0){
            return count;
        }
        for (BookDigests bookDigests : bookDigestsList) {
			getDatabase().delete(DBConfig.CONTENT_URI_DIGEST, bookDigests.getPrimaryKeyWhereClause(), null);
		} 
        return count;
    }

    public int clearBookDigests(){ 
        return getDatabase().delete(DBConfig.CONTENT_URI_DIGEST, null, null);
    } 
}
