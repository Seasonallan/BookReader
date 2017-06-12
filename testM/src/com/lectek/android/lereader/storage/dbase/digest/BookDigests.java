package com.lectek.android.lereader.storage.dbase.digest;

import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;

import com.lectek.android.lereader.lib.net.exception.ErrorMessage;
import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.IDbHelper;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Column;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Table;
import com.lectek.android.lereader.net.response.SyncDigestResponse;
import com.lectek.android.lereader.storage.dbase.BaseDBHelper;

/**
 * 
 * @author linyiwei
 *
 */
@Table(name = "book_digests")
public class BookDigests  extends BaseDao{

    @Override
    public void onUpgrade(final SQLiteDatabase db, int oldV, final int newV) {
        if (oldV == 1) {
            if(!BaseDBHelper.contains(db, "book_digests", "action")){
                db.execSQL("ALTER TABLE book_digests ADD COLUMN action INTEGER;");
            }
            if(!BaseDBHelper.contains(db, "book_digests", "status")) {
                db.execSQL("ALTER TABLE book_digests ADD COLUMN status INTEGER;");
            }
            if(!BaseDBHelper.contains(db, "book_digests", "note_id")){
                db.execSQL("ALTER TABLE book_digests ADD COLUMN note_id TEXT;");
            }
            if(!BaseDBHelper.contains(db, "book_digests", "position_real")){
                db.execSQL("ALTER TABLE book_digests ADD COLUMN position_real INTEGER DEFAULT -1;");
            }
        }else if (oldV == 2) {
            if(!BaseDBHelper.contains(db, "book_digests", "position_real")){
                db.execSQL("ALTER TABLE book_digests ADD COLUMN position_real INTEGER DEFAULT -1;");
            }
        }
    }

	@Override
	public IDbHelper newDatabaseHelper() { 
		return DigestDBHelper.getDBHelper();
	} 
	
	public BookDigestsSpan mBookDigestsSpan;
 
	@Column(name = "content_id",isPrimaryKey = true)
	public String mContentId;
	
	@Column(name = "chapters_id", type="Integer DEFAULT -1",isPrimaryKey = true)
	public int mChaptersId;
	
	@Column(name = "data1")
	public String mContentName;
	
	@Column(name = "data2")
	public String mChaptersName;
	
	@Column(name = "data0")
	public String mContent;
	
	@Column(name = "position", type="Integer DEFAULT -1",isPrimaryKey = true)
	public int mPosition;
	
	@Column(name = "count", type="Integer DEFAULT -1")
	public int mCount;
	
	@Column(name = "msg")
	public String mMsg;
	
	@Column(name = "corlor", type="Integer DEFAULT -1")
	public int mBGColor;
	
	@Column(name = "date", type="Integer DEFAULT -1")
	public long mDate;
 
	public int mTextColor;
	
	@Column(name = "data3")
	public int mFromType;
	
	@Column(name = "data4")
	public String author;

	@Column(name = "status", type="Integer DEFAULT -1")
	public int status = BookDigestsDB.STATUS_LOCAL;
	
	@Column(name = "action", type="Integer DEFAULT -1")
	public int action = BookDigestsDB.ACTION_ADD;
	
	@Column(name = "note_id")
	public String serverId;

	@Column(name = "position_real", type="Integer DEFAULT -1")
    public int position4Txt = -1;
	public BookDigests(){ 
		mPosition = -1;
		mCount = -1;
		mMsg = "";
		mBGColor = 0;
		mContentId = "";
		mChaptersId = -1;
		mTextColor = 0;
		mContentName = "";
		mChaptersName = "";
		mContent = "";
		mFromType = -1;
		author = ""; 
	}

	public BookDigests(int position, int count, String msg, int color, int chaptersId, String contentId, int fromType, String author){
		mPosition = position;
		mCount = count;
		mMsg = msg;
		setBGColor(color);
		mContentId = contentId;
		mChaptersId = chaptersId;
		mFromType = fromType;
		this.author = author;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookDigests other = (BookDigests) obj;
		if (mBGColor != other.mBGColor)
			return false;
		if (mChaptersId != other.mChaptersId)
			return false;
		if (mChaptersName == null) {
			if (other.mChaptersName != null)
				return false;
		} else if (!mChaptersName.equals(other.mChaptersName))
			return false;
		if (mContent == null) {
			if (other.mContent != null)
				return false;
		} else if (!mContent.equals(other.mContent))
			return false;
		if (mContentId == null) {
			if (other.mContentId != null)
				return false;
		} else if (!mContentId.equals(other.mContentId))
			return false;
		if (mContentName == null) {
			if (other.mContentName != null)
				return false;
		} else if (!mContentName.equals(other.mContentName))
			return false;
		if (mCount != other.mCount)
			return false;
		if (mDate != other.mDate)
			return false;
		if (mMsg == null) {
			if (other.mMsg != null)
				return false;
		} else if (!mMsg.equals(other.mMsg))
			return false;
		if (mPosition != other.mPosition)
			return false;
		if (mTextColor != other.mTextColor)
			return false;
		if (mFromType != other.mFromType)
			return false;
        if (position4Txt != other.position4Txt)
            return false;
		return true;
	}
	
	public boolean isSynced(){
		return status == BookDigestsDB.STATUS_SYNC;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public int getPosition() {
		return mPosition;
	}

	public void setPosition(int mPosition) {
		this.mPosition = mPosition;
	}

	public long getDate() {
		return mDate;
	}

	public void setDate(long mDate) {
		this.mDate = mDate;
	}

	public int getCount() {
		return mCount;
	}

	public void setCount(int mCount) {
		this.mCount = mCount;
	}

	public String getMsg() {
		return mMsg;
	}

	public void setMsg(String msg) {
		this.mMsg = msg;
	}

	public int getBGColor() {
		return mBGColor;
	}

	public void setBGColor(int mColor) {
		this.mBGColor = mColor;
		mBookDigestsSpan = new BookDigestsSpan(mColor);
	}

	public int getTextColor() {
		return mTextColor;
	}
	
	public void setTextColor(int mColor) {
		this.mTextColor = mColor;
	}
	
	public String getContentId() {
		return mContentId;
	}

	public void setContentId(String mContentId) {
		this.mContentId = mContentId;
	}

	public int getChaptersId() {
		return mChaptersId;
	}

	public void setChaptersId(int mChaptersId) {
		this.mChaptersId = mChaptersId;
	}
	
	public boolean isBoundChange(int position,int count){
		if(mPosition != position || mCount != count){
			return true;
		}
		return false;
	}

	public String getContentName() {
		return mContentName;
	}

	public void setContentName(String mContentName) {
		this.mContentName = mContentName;
	}

	public String getChaptersName() {
		return mChaptersName;
	}

	public void setChaptersName(String mChaptersName) {
		this.mChaptersName = mChaptersName;
	}

	public String getContent() {
		return mContent;
	}

	public void setContent(String mContent) {
		this.mContent = mContent;
	}

	public int getFromType() {
		return mFromType;
	}

	public void setFromType(int mFromType) {
		this.mFromType = mFromType;
	}

	public void set(BookDigests bookDigests){
		if(bookDigests != null){
			
			this.mContentId = bookDigests.mContentId;
			this.mChaptersId = bookDigests.mChaptersId;
			this.mContentName = bookDigests.mContentName;
			this.mChaptersName = bookDigests.mChaptersName;
			this.mContent = bookDigests.mContent;
			this.mPosition = bookDigests.mPosition;
			this.mCount = bookDigests.mCount;
			this.mMsg = bookDigests.mMsg;
			this.mBGColor = bookDigests.mBGColor;
			this.mDate = bookDigests.mDate;
			this.mTextColor = bookDigests.mTextColor;
			this.mFromType = bookDigests.mFromType;
			this.author = bookDigests.author;

            this.mBookDigestsSpan = bookDigests.mBookDigestsSpan;
            this.serverId = bookDigests.serverId;
			this.status = bookDigests.status;
			this.action = bookDigests.action;
            this.position4Txt = bookDigests.position4Txt;
		}
	}

    public int getPosition4Txt() {
        return position4Txt;
    }

    public void setPosition4Txt(int position4Txt) {
        this.position4Txt = position4Txt;
    }

    public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
		

	
	public static class SyncDigestResponseInfoList extends ErrorMessage {

		
		public static final long serialVersionUID = 7030755409687947091L;
		@Json(name ="leyue_list",className= SyncDigestResponse.class)
		ArrayList<SyncDigestResponse> infos;

		public ArrayList<SyncDigestResponse> getInfos() {
			return infos;
		} 

		public void setInfos(ArrayList<SyncDigestResponse> infos) {
			this.infos = infos;
		}
	}

}
