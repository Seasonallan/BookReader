package com.lectek.android.lereader.storage.dbase.mark;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.IDbHelper;
import com.lectek.android.lereader.lib.storage.dbase.JsonArrayList;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Column;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Table;
import com.lectek.android.lereader.storage.dbase.BaseDBHelper;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.tencent.a.a.c;


@Table(name = "bookmark")
public class BookMark extends BaseDao{
	
	public static final int RECENT_READ_MAX_NUM = 4; 
	public static final int RECENT_READ_STATUS_RECENT = 1;
	public static final int RECENT_READ_STATUS_NOT_RECCENT = 0;

	@Override
	public IDbHelper newDatabaseHelper() { 
		return MarkDBHelper.getDBHelper();
	}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4){
            try{
                Cursor cursor = db.query("bookmark", null, null, null, null, null, "id asc");
                JsonArrayList<BookMark> lists = new JsonArrayList<BookMark>(BookMark.class);
                lists.fromCursor(cursor);
                dropTable(db);
                createTable(db);
                for(BookMark bookMark: lists){
                    db.insert("bookmark", null, bookMark.toContentValues());
                }
            }catch (Exception e){}
        }
        /*if(!BaseDBHelper.contains(db, "bookmark", "groupId")){
            db.execSQL("ALTER TABLE bookmark ADD COLUMN groupId INTEGER DEFAULT "+
                    PreferencesUtil.getInstance(BaseApplication.getInstance()).getBookMarkGroupId() +";");
        }
        if(!BaseDBHelper.contains(db, "bookmark", "max")){
            db.execSQL("ALTER TABLE bookmark ADD COLUMN max INTEGER DEFAULT -1;");
        }
        if(!BaseDBHelper.contains(db, "bookmark", "shelfPosition")){
            db.execSQL("ALTER TABLE bookmark ADD COLUMN shelfPosition double DEFAULT -1;");
        }
        if(!BaseDBHelper.contains(db, "bookmark", "is_recent_read")){
            db.execSQL("ALTER TABLE bookmark ADD COLUMN is_recent_read double DEFAULT -1;");
        }
        if(!BaseDBHelper.contains(db, "bookmark", "_id")){
            db.execSQL("ALTER TABLE bookmark ADD COLUMN is_recent_read double DEFAULT -1;");
        }*/
    }
    @Column(name = "shelfPosition",type = "double DEFAULT -1")
    public double shelfPosition = -1;

    @Column(name = "max",type = "Integer DEFAULT -1")
    public int max = -1;


    // 分组ID
    @Column(name = "groupId",type = "Integer")
    public int groupId = PreferencesUtil.getInstance(BaseApplication.getInstance()).getBookMarkGroupId();

	// 自增长的表ID
	public int id;

	// 书签ID
	@Column(name = "bookmarkID")
	public String bookmarkID;

	// 书签名字
	@Column(name = "bookmarkName")
	public String bookmarkName;

	// 书签创建时间
	@Column(name = "createTime")
	public String createTime;

	// 书籍ID
	@Column(name = "contentID", isPrimaryKey =true)
	public String contentID;

	// 章节ID
	@Column(name = "chapterID", type = "INTEGER DEFAULT -1", isPrimaryKey =true)
	public int chapterID;

	// 章节名称
	@Column(name = "chapterName")
	public String chapterName;

	// 在书籍中的位置
	@Column(name = "position", isPrimaryKey =true, type = "INTEGER DEFAULT -1")
	public int position;

	// 书签类型
	@Column(name = "bookmarkType", type = "INTEGER", isPrimaryKey =true)
	public int bookmarkType;

	// 书籍类型
	@Column(name = "contentType", type = "INTEGER")
	public int contentType;

	// 书籍名称
	@Column(name = "contentName")
	public String contentName;

	// 作者
	@Column(name = "author")
	public String author;


	// 书籍封面下载地址
	@Column(name = "logoUrl")
	public String logoUrl;

	// 是否软删除
	@Column(name = "softDelete", type = "INTEGER DEFAULT -1")
	public int softDelete;

	// 用户ID
	@Column(name = "userID")
	public String userID;
	
	// 状态
	@Column(name = "status", type = "INTEGER")
	public int status = BookDigestsDB.STATUS_LOCAL;
	
	//是否为最近阅读视图书籍
	@Column(name = "is_recent_read", type = "Integer DEFAULT -1")
	public int is_recent_read;

    public boolean isSynced(){
        return status == BookDigestsDB.STATUS_SYNC;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBookmarkID() {
		return bookmarkID;
	}

	public void setBookmarkID(String bookmarkID) {
		this.bookmarkID = bookmarkID;
	}

	public String getBookmarkName() {
		return bookmarkName;
	}

	public void setBookmarkName(String bookmarkName) {
		this.bookmarkName = bookmarkName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getContentID() {
		return contentID;
	}

	public void setContentID(String contentID) {
		this.contentID = contentID;
	}

	public int getChapterID() {
		return chapterID;
	}

	public void setChapterID(int chapterID) {
		this.chapterID = chapterID;
	}

	public String getChapterName() {
		return chapterName;
	}

	public void setChapterName(String chapterName) {
		this.chapterName = chapterName;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getBookmarkType() {
		return bookmarkType;
	}

	public void setBookmarkType(int bookmarkType) {
		this.bookmarkType = bookmarkType;
	}

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	public String getContentName() {
		return contentName;
	}

	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public int getSoftDelete() {
		return softDelete;
	}

	public void setSoftDelete(int softDelete) {
		this.softDelete = softDelete;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public int getIsRecentRead() {
		return is_recent_read;
	}

	public void setIsRecentRead(int is_recent_read) {
		this.is_recent_read = is_recent_read;
	}
	
	
}
