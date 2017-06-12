package com.lectek.android.lereader.storage.dbase.mark;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.data.BookShelfItem;
import com.lectek.android.lereader.lib.storage.dbase.BaseDatabase;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.lib.utils.StringUtil;
import com.lectek.android.lereader.net.response.DownloadInfo;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;

/**
 * 笔记数据库管理类
 * @author ljp
 */
public class BookMarkDB{
 
    private static BookMarkDB mBookDigestsDB;
    private double mFirstShelfPosition = 0;

    public static BookMarkDB getInstance(){
        if(mBookDigestsDB == null){
            mBookDigestsDB = new BookMarkDB();
        }
        return mBookDigestsDB;
    }

    private BookMarkDB() { 
    }

    public void resetShelfPosition(double position){
        this.mFirstShelfPosition = position;
    }

    public double getShelfPosition(){
        return  this.mFirstShelfPosition;
    }

    private ContentResolver getDatabase() {
		return BaseApplication.getInstance().getContentResolver();
	}

    /**
     * 是否存在某书签
     * @param bookMarkInfo
     * @return
     */
    public boolean hasBookMark(BookMark bookMarkInfo) {
        if (bookMarkInfo == null){
            return false;
        }
        List<BookMark> res = getBookMark(null, "contentID", bookMarkInfo.contentID,
                "chapterID", bookMarkInfo.chapterID,
                "position", bookMarkInfo.position,
                "bookmarkType", DBConfig.BOOKMARK_TYPE_USER);
        return res != null && res.size()>0;
    }
    /**
     * 根据条件获取书签
     * @return
     */
    private List<BookMark> getBookMark(String sortOrder, Object... params) { 
    	String where = null;
    	if (params != null && params.length > 0) {
			if (params.length == 1) {
                Object object = params[0];
                if (object instanceof BookMark){
                    where = ((BookMark) object).getPrimaryKeyWhereClause();
                }else{
                    where = object.toString();
                }
			}else{
				where = BaseDatabase.getWhere(params);
			}
		}
        Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_MARK, null,where, null, sortOrder);

        ArrayList<BookMark> list = new ArrayList<BookMark>();
        BookMark bookMark;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    bookMark = new BookMark();
                    bookMark.fromCursor(cursor);
                    list.add(bookMark);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }  
 
    /**
     * 创建用户书签,数据库没有则创建
     *
     * @return
     */
    public long createUserBookMarkIfNotExit(BookMark bookMarkInfo) {
        if (!hasBookMark(bookMarkInfo)){
        	getDatabase().insert(DBConfig.CONTENT_URI_MARK, bookMarkInfo.toContentValues());
            return 1;
        }
        return -1;
    }

    /**
     * 更新用户书签 bookMarkInfo；如果有存在，则更新数据，如果没有则创建
     *
     * @return
     */
    public long updateOrCreateUserBookMark(BookMark bookMarkInfo) {
        Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_MARK, null, bookMarkInfo.getPrimaryKeyWhereClause(), null, null);
        if (cursor != null && cursor.getCount() > 0){
            getDatabase().update(DBConfig.CONTENT_URI_MARK, bookMarkInfo.toContentValues(), bookMarkInfo.getPrimaryKeyWhereClause(), null);
        }else{
            getDatabase().insert(DBConfig.CONTENT_URI_MARK, bookMarkInfo.toContentValues());
        }
        return 1;
    }
    /**
     * 更新用户书签 bookMarkInfo；如果有存在，则更新数据，如果没有则创建
     *
     * @return
     */
    public void updateUserBookMarkServerId(BookMark bookMarkInfo) {
    	bookMarkInfo.setStatus(BookDigestsDB.STATUS_SYNC);
    	bookMarkInfo.setSoftDelete(DBConfig.BOOKMARK_STATUS_SOFT_DELETE_NO);
        getDatabase().update(DBConfig.CONTENT_URI_MARK, bookMarkInfo.toContentValues(), bookMarkInfo.getPrimaryKeyWhereClause(), null);
    	//getDatabase().intelligenceInsert(bookMarkInfo);
    }

    /**
     * 读取相应的用户书签
     */
    public BookMark getUserBookMark(BookMark bookMarkInfo) {
        Cursor cursor = getDatabase().query(DBConfig.CONTENT_URI_MARK, null, bookMarkInfo.getPrimaryKeyWhereClause(), null, null);
        if (cursor != null && cursor.moveToFirst()){
            BookMark bookMark =new BookMark();
            bookMark.fromCursor(cursor);
            return bookMark;
        }
        return null;
    }

    /**
     * 读取相应用户的用户书签
     *
     * @return
     */
    public List<BookMark> getUserBookMark(String contentID) {
        return getBookMark(null, "contentID", contentID,
                "bookmarkType", DBConfig.BOOKMARK_TYPE_USER,
                "softDelete", DBConfig.BOOKMARK_STATUS_SOFT_DELETE_NO);
    }
    /**
     * 清空书签
     *
     * @return
     */
    public int clearBookmarks(){ 
        return getDatabase().delete(DBConfig.CONTENT_URI_MARK, null, null);
    }

    /**
     * 删除某用户书签
     *
     * @return
     */
    public void deleteUserBookmark(BookMark bookMarkInfo) {
        getDatabase().delete(DBConfig.CONTENT_URI_MARK, bookMarkInfo.getPrimaryKeyWhereClause(), null);
    }

    /**
     * 软删除本地用户书签[通过书籍信息+章节+位置]
     *
     * @return
     */
    public void softDeleteUserBookmark(BookMark bookMarkInfo) {
    	bookMarkInfo.setStatus(BookDigestsDB.STATUS_LOCAL);
        bookMarkInfo.setSoftDelete(DBConfig.BOOKMARK_STATUS_SOFT_DELETE);
    	getDatabase().update(DBConfig.CONTENT_URI_MARK, bookMarkInfo.toContentValues(), bookMarkInfo.getPrimaryKeyWhereClause(), null);
    }

    /**
     * 获取某本书籍的本地用户书签
     *
     * @return
     */
    public List<BookMark> getSyncUserBookMarks(String contentId) {
        return getBookMark(null, "bookmarkType", DBConfig.BOOKMARK_TYPE_USER,
                "status", BookDigestsDB.STATUS_LOCAL,
                "contentID", contentId);
    }

    /**
     * 获取全部本地用户书签
     *
     * @return
     */
    public List<BookMark> getSyncUserBookMarks() {
        return getBookMark(null, "bookmarkType", DBConfig.BOOKMARK_TYPE_USER,
                "status", BookDigestsDB.STATUS_LOCAL);
    }


    /**
     * 获取所有书签
     *
     * @return
     */
    public List<BookMark> getAllBookMarks(String sortOrder) {
        return getBookMark(sortOrder);
    }

    /**
     * 创建书签表
     *
     * @return
     */
    public void newBookMark(String bookmarkID, String bookmarkName,
                            String createTime, String contentID, String chapterID,
                            String chapterName, int position, int bookmarkType,
                            int contentType, String contentName, String author, int status,
                            String logoUrl, int softDelete, String userID) {
        BookMark bookMark = new BookMark();
        bookMark.setBookmarkID(bookmarkID);
        bookMark.setBookmarkName(bookmarkName);
        bookMark.setCreateTime(createTime);
        bookMark.setContentID(contentID);
//		bookMark.setChapterID(chapterID);
        bookMark.setChapterName(chapterName);
        bookMark.setPosition(position);
        bookMark.setBookmarkType(bookmarkType);
        bookMark.setContentType(contentType);
        bookMark.setContentName(contentName);
        bookMark.setAuthor(author);
        bookMark.setStatus(status);
        bookMark.setLogoUrl(logoUrl);
        bookMark.setSoftDelete(softDelete);
        bookMark.setUserID(userID);
        getDatabase().insert(DBConfig.CONTENT_URI_MARK, bookMark.toContentValues());
    }
    
    private String getSystemWhereClause(BookMark bookMarkInfo){
    	return BaseDatabase.getWhere("contentID", bookMarkInfo.getContentID(),
                "bookmarkType", DBConfig.BOOKMARK_TYPE_SYSTEM);
    }
    
    /**
     * 创建系统书签
     *
     * @return
     */
    public boolean createSysBookMarkIfNotExit(BookMark bookMarkInfo) {
        List<BookMark> bookMarks = getBookMark(null, getSystemWhereClause(bookMarkInfo));
        if (bookMarks != null && bookMarks.size() > 0) {
            BookMark bookMark = bookMarks.get(0);
            if (bookMark.softDelete == DBConfig.BOOKMARK_STATUS_SOFT_DELETE){
                return false;
            }
        } else {
            getDatabase().insert(DBConfig.CONTENT_URI_MARK, bookMarkInfo.toContentValues());
        }
        
        return true;
    }

    /**
     * 创建或激活系统书签
     *
     * @return
     */
    public void createOrReusedSysBookMark(BookMark bookMarkInfo) {
        List<BookMark> bookMarks = getBookMark(null, getSystemWhereClause(bookMarkInfo));
        if (bookMarks != null && bookMarks.size() > 0) {
            BookMark bookMark = bookMarks.get(0);
            if (bookMark.softDelete == DBConfig.BOOKMARK_STATUS_SOFT_DELETE){
            	getDatabase().update(DBConfig.CONTENT_URI_MARK, bookMarkInfo.toContentValues(),BaseDatabase.getWhere("contentID", bookMarkInfo.contentID,
                        "bookmarkType", DBConfig.BOOKMARK_TYPE_SYSTEM), null);
            }
        } else {
            getDatabase().insert(DBConfig.CONTENT_URI_MARK, bookMarkInfo.toContentValues());
        }
        
        LogUtil.e("BookMarkDB", String.format("insert bookMark id:%s name:%s", bookMarkInfo.contentID, bookMarkInfo.contentName));
    }
    
    /**
     * 更新系统书籍位置
     * @param contentId
     * @param position
     */
    public int updateSysBookMarkPosition(String contentId, double position){
        ContentValues contentValues = new ContentValues();
        contentValues.put("shelfPosition", position);
        return getDatabase().update(DBConfig.CONTENT_URI_MARK, contentValues, BaseDatabase.getWhere("contentID", contentId,
                "bookmarkType", DBConfig.BOOKMARK_TYPE_SYSTEM), null);
    }
    
    /**
     * 更新最新阅读书籍状态值
     * @param contentId
     * @param status
     */
    public int updateSysBookMarkRecentReadStatus(String contentId, int status){
        ContentValues contentValues = new ContentValues();
        contentValues.put("is_recent_read", status);
        return getDatabase().update(DBConfig.CONTENT_URI_MARK, contentValues, BaseDatabase.getWhere("contentID", contentId,
                "bookmarkType", DBConfig.BOOKMARK_TYPE_SYSTEM), null);
    }

    /**
     * 更新系统书籍分组ID
     * @param oldGroupId
     * @param groupId
     */
    public void updateSysBookMarkGroup(int oldGroupId, int groupId){
        ContentValues contentValues = new ContentValues();
        contentValues.put("groupId", groupId);
        contentValues.put("status", BookDigestsDB.STATUS_LOCAL);
        getDatabase().update(DBConfig.CONTENT_URI_MARK, contentValues, BaseDatabase.getWhere("groupId", oldGroupId,
                "bookmarkType", DBConfig.BOOKMARK_TYPE_SYSTEM), null);
    }

    /**
     * 更新系统书籍分组ID
     * @param contentId
     * @param groupId
     */
    public void updateSysBookMarkGroup(String contentId, int groupId){
        ContentValues contentValues = new ContentValues();
        contentValues.put("groupId", groupId);
        contentValues.put("status", BookDigestsDB.STATUS_LOCAL);
        getDatabase().update(DBConfig.CONTENT_URI_MARK, contentValues, BaseDatabase.getWhere("contentID", contentId,
                "bookmarkType", DBConfig.BOOKMARK_TYPE_SYSTEM), null);
    }

    /**
     * 更新系统书籍分组ID
     * @param contentId
     * @param groupId
     */
    public void updateSysBookMarkGroupSync(String contentId, int groupId){
        ContentValues contentValues = new ContentValues();
        contentValues.put("groupId", groupId);
        getDatabase().update(DBConfig.CONTENT_URI_MARK, contentValues, BaseDatabase.getWhere("contentID", contentId,
                "bookmarkType", DBConfig.BOOKMARK_TYPE_SYSTEM), null);
    }

    /**
     * 更新系统书签 bookMark bookMarkInfo；如果有存在，则更新数据，如果没有则创建
     *
     * @return
     */
    public void updateOrCreateSysBookMark(BookMark bookMarkInfo, boolean hasRead) {
        List<BookMark> bookMarks = getBookMark(null, getSystemWhereClause(bookMarkInfo));
        if (bookMarks != null && bookMarks.size() > 0) {
            bookMarkInfo.setId(bookMarks.get(0).getId());
            bookMarkInfo.shelfPosition = bookMarks.get(0).shelfPosition;
            bookMarkInfo.groupId = bookMarks.get(0).groupId;
            bookMarkInfo.setStatus(BookDigestsDB.STATUS_SYNC);
            bookMarkInfo.setSoftDelete(DBConfig.BOOKMARK_STATUS_SOFT_DELETE_NO);
            getDatabase().update(DBConfig.CONTENT_URI_MARK, bookMarkInfo.toContentValues(), getSystemWhereClause(bookMarkInfo), null);
        } else {
//            bookMarkInfo.shelfPosition = BookMarkDB.getInstance().getShelfPosition() - 1;
//            bookMarkInfo.shelfPosition = bookMarkInfo.shelfPosition == -1 ? -0.1:bookMarkInfo.shelfPosition;
            getDatabase().insert(DBConfig.CONTENT_URI_MARK, bookMarkInfo.toContentValues());
        }
        
        //TODO:更新最近阅读书籍状态值
        if(hasRead) {
	        int size = getRecentReadSize();
	        if(size == BookMark.RECENT_READ_MAX_NUM){
	        	//最近阅读已经到4个了，改变最早阅读的那本书最近阅读状态，再更新状态。
	        	BookMark bookMarkTemp = getRecentReadBooks().get(0);
	        	updateSysBookMarkRecentReadStatus(bookMarkTemp.getContentID(), BookMark.RECENT_READ_STATUS_NOT_RECCENT);
	        	updateSysBookMarkRecentReadStatus(bookMarkInfo.getContentID(), BookMark.RECENT_READ_STATUS_RECENT);
	        }else{
	        	//最近阅读未到最大个数，直接更新状态。
	        	updateSysBookMarkRecentReadStatus(bookMarkInfo.getContentID(), BookMark.RECENT_READ_STATUS_RECENT);
	        }
        }
    }
    /**
     * 删除某系统书签
     *
     * @return
     */
    public void deleteSystemBookmark(BookMark bookMarkInfo) {
    	getDatabase().delete(DBConfig.CONTENT_URI_MARK, getSystemWhereClause(bookMarkInfo), null);
    }
    /**
     * 获取全部本地系统书签
     *
     * @return
     */
    public List<BookMark> getSyncSystemBookMarks() {
        return getBookMark(null, "status", BookDigestsDB.STATUS_LOCAL,
                "bookmarkType", DBConfig.BOOKMARK_TYPE_SYSTEM);
    }

    /**
     * 读取指定书籍的系统书签；由于特殊需求，这里系统书签对一本书只保存一份记录。TODO：
     *
     * @return
     */
    public BookMark getSepecificBookMark(String contentID) {
        List<BookMark> bookMarks = getBookMark(null, "contentID", contentID,
                "bookmarkType", DBConfig.BOOKMARK_TYPE_SYSTEM,
                "softDelete", DBConfig.BOOKMARK_STATUS_SOFT_DELETE_NO);
        if (bookMarks != null && bookMarks.size() > 0) {
            return bookMarks.get(0);
        }
        return null;
    }
 

    /**
     * 读取相应用户的系统书签
     *
     * @return
     */
    public List<BookMark> getAllSysBookMark(String sortOrder) {
        return getBookMark(sortOrder,
                "bookmarkType", DBConfig.BOOKMARK_TYPE_SYSTEM,
                "softDelete", DBConfig.BOOKMARK_STATUS_SOFT_DELETE_NO);
    }

    /**
     * 获取当前账号单本书籍系统书签；每个用户每本书只有一个系统书签
     *
     * @return
     */
    public BookMark getSystemBookmark(String contentID, String userID) {
        List<BookMark> bookMarks = getBookMark(null, "contentID", contentID,
                "bookmarkType", DBConfig.BOOKMARK_TYPE_SYSTEM,
                "userID", userID,
                "softDelete", DBConfig.BOOKMARK_STATUS_SOFT_DELETE_NO);
        if (bookMarks != null && bookMarks.size() > 0) {
            return bookMarks.get(0);
        }
        return null;
    }


    /**
     * 软删除本地当前用户系统书签
     *
     * @return
     * @param contentIds
     */
    public void softDeleteSystemBookmark(ArrayList<DownloadInfo> contentIds) {
        if(contentIds != null){
            for(DownloadInfo downloadInfo: contentIds){
                List<BookMark> bookMarks = getBookMark(null, "contentID", downloadInfo.contentID,
                        "bookmarkType", DBConfig.BOOKMARK_TYPE_SYSTEM);
                if (bookMarks != null && bookMarks.size() > 0) {
                    BookMark bookMark = bookMarks.get(0);
                    bookMark.setStatus(BookDigestsDB.STATUS_LOCAL);
                    bookMark.setSoftDelete(DBConfig.BOOKMARK_STATUS_SOFT_DELETE);
                    bookMark.setIsRecentRead(BookMark.RECENT_READ_STATUS_NOT_RECCENT);
                    getDatabase().update(DBConfig.CONTENT_URI_MARK, bookMark.toContentValues(), getSystemWhereClause(bookMark), null);
                    
                }
            }
        }
    }

    /**
     * 软删除本地当前用户系统书签
     *
     */
    public void softDeleteSystemBookmark(String contentId) {
        List<BookMark> bookMarks = getBookMark(null, "contentID", contentId,
                "bookmarkType", DBConfig.BOOKMARK_TYPE_SYSTEM);
        if (bookMarks != null && bookMarks.size() > 0) {
            BookMark bookMark = bookMarks.get(0);
            bookMark.setStatus(BookDigestsDB.STATUS_LOCAL);
            bookMark.setSoftDelete(DBConfig.BOOKMARK_STATUS_SOFT_DELETE);
            bookMark.setIsRecentRead(BookMark.RECENT_READ_STATUS_NOT_RECCENT);
            getDatabase().update(DBConfig.CONTENT_URI_MARK, bookMark.toContentValues(), getSystemWhereClause(bookMark), null);
        }
    }
    
    /**
     * 获取最近阅读书籍个数
     * @return
     */
    public int getRecentReadSize(){
    	int size = 0;
        List<BookMark> bookMarks = getBookMark(null, "is_recent_read", BookMark.RECENT_READ_STATUS_RECENT,
                "bookmarkType", DBConfig.BOOKMARK_TYPE_SYSTEM);
    	if (bookMarks != null) {
			size = bookMarks.size();
		} 
    	return size;
    }
    
    /**
     * 获取最近阅读的书籍
     * @return
     */
    public List<BookMark> getRecentReadBooks(){
    	List<BookMark> bookMarks = null;
    	bookMarks = getBookMark(null, "is_recent_read", BookMark.RECENT_READ_STATUS_RECENT,
                 "bookmarkType", DBConfig.BOOKMARK_TYPE_SYSTEM, "softDelete", DBConfig.BOOKMARK_STATUS_SOFT_DELETE_NO);
    	try {
    		Collections.sort(bookMarks, new Comparator<BookMark>() {
                @Override
                public int compare(BookMark bookMarkItem, BookMark bookMarkItem2) {
                	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                	
                    Date date1,date2;
					try {
						date1 = sdf.parse(bookMarkItem.getCreateTime());
						date2 =  sdf.parse(bookMarkItem2.getCreateTime());
						return date2.compareTo(date1);
					} catch (ParseException e) {
						e.printStackTrace();
					}
                    return -1;
                }
            });
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return bookMarks;
    }
    
    /**
     * 删除最近阅读书籍
     * @param contentIds
     */
    public void deleteRecentReadBook(ArrayList<DownloadInfo> contentIds){
    	if(contentIds != null){
            for(DownloadInfo downloadInfo: contentIds){
                List<BookMark> bookMarks = getBookMark(null, "contentID", downloadInfo.contentID,
                        "bookmarkType", DBConfig.BOOKMARK_TYPE_SYSTEM);
                if (bookMarks != null && bookMarks.size() > 0) {
                    BookMark bookMark = bookMarks.get(0);
                    bookMark.setIsRecentRead(BookMark.RECENT_READ_STATUS_NOT_RECCENT);
                    getDatabase().update(DBConfig.CONTENT_URI_MARK, bookMark.toContentValues(), getSystemWhereClause(bookMark), null);
                    
                }
            }
        }
    }
    
    /**
     * 删除单本最近阅读书籍
     * @param downloadInfo
     */
//    public void deleteRecentReadBook(DownloadInfo downloadInfo){
    public void deleteRecentReadBook(String contentID){
    	if(!StringUtil.isEmpty(contentID)){
            List<BookMark> bookMarks = getBookMark(null, "contentID", contentID,
                    "bookmarkType", DBConfig.BOOKMARK_TYPE_SYSTEM);
            if (bookMarks != null && bookMarks.size() > 0) {
                BookMark bookMark = bookMarks.get(0);
                bookMark.setIsRecentRead(BookMark.RECENT_READ_STATUS_NOT_RECCENT);
                getDatabase().update(DBConfig.CONTENT_URI_MARK, bookMark.toContentValues(), getSystemWhereClause(bookMark), null);
                
            }
        }
    }
}
