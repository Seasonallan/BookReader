package com.lectek.android.lereader.download;


import java.io.File;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.CrossProcessCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.CursorWrapper;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.response.DownloadInfo;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.permanent.DownloadAPI;
import com.lectek.android.lereader.permanent.DownloadConstants;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.dbase.mark.BookMarkDB;
import com.lectek.android.lereader.ui.specific.WelcomeActivity;
import com.lectek.android.lereader.utils.CommonUtil;
/**
 * 每次数据操作成功都会发出数据变跟的通知
 * @author linyiwei
 * @email 21551594@qq.com
 * @date 2011-11-1
 */
public class DownloadProvider extends ContentProvider {
	private static final String TAG = "DownloadProvider";
    private static final String DB_NAME = "downloads.db";
    /**
     * 第一版本
     */
//    private static final int DB_VERSION_FIRST = 100;
    /**
     * 添加下载类型
     */
    private static final int DB_VERSION_ADD_DOWNLOAD_TYPE = 101;
    private static final int DB_VERSION_7 = 102;//乐阅0.7
    private static final int DB_VERSION_7_2 = 103;//乐阅0.7.2
    private static final int DB_VERSION_8_0 = 104;//乐阅0.8.0
    
    private static final int DB_VERSION = DB_VERSION_8_0;
    
    private static final String DB_TABLE = "download_unit";

    /** MIME type for the entire download list */
    private static final String DOWNLOAD_LIST_TYPE = "vnd.android.cursor.dir/download";
    /** MIME type for an individual download */
//    private static final String DOWNLOAD_TYPE = "vnd.android.cursor.item/download";

  
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    /**
     * 对外开放uri
     */
    private static final int DOWNLOADS = 1;
   
    /**
     * 模块内部访问uri
     */
    private static final int DOWNLOADS_ROOT = 2;
    
    private SQLiteOpenHelper mOpenHelper = null;
    
    
    static {
        sURIMatcher.addURI(DownloadConstants.CONTENT_URI_AUTHORITIES, DownloadConstants.CONTENT_URI_PATH, DOWNLOADS);
        sURIMatcher.addURI(DownloadConstants.CONTENT_URI_AUTHORITIES, DownloadConstants.CONTENT_URI_PATH_ROOT, DOWNLOADS_ROOT);
    }
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		LogUtil.i(TAG,"DownloadProvider delete() Uri:"+arg0);
		if (sURIMatcher.match(arg0) != DOWNLOADS && sURIMatcher.match(arg0) != DOWNLOADS_ROOT) {
            throw new IllegalArgumentException("Unknown/Invalid URI " + arg0);
        }
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		if(db == null){
			LogUtil.i(TAG, "DownloadProvider insert() Failed to get SQLiteDatabase");
			return 0;
		}
		Context context = getContext();
		int count = 0;
		if(sURIMatcher.match(arg0) == DOWNLOADS_ROOT ){
			 count = db.delete(DB_TABLE, arg1, arg2);
		}else{
			//模块外部调用为软删除
			 ContentValues values = new ContentValues();
			 values.put(DownloadAPI.DELETE, true);
			 values.put(DownloadAPI.TIMES_TAMP, System.currentTimeMillis());
			 values.put(DownloadAPI.STATE,DownloadAPI.STATE_START);
			 count = db.update(DB_TABLE, values, arg1, arg2);
			 sendDeleteBroadcast(context,arg1, arg2);
		}
		LogUtil.i(TAG,"DownloadProvider delete() count:"+count);
		if(count > 0  && sURIMatcher.match(arg0) != DOWNLOADS_ROOT){
			context.getContentResolver().notifyChange(arg0, null);
			context.startService(new Intent(context, DownloadService.class));
		}
		return count;
	}
	
	private void sendDeleteBroadcast(Context context, String arg1, String[] arg2){
		 Cursor cursor = mOpenHelper.getReadableDatabase().query(DB_TABLE, null, arg1, arg2, null, null, null);
		 long [] ids = null;
		 if(cursor != null){
			 ids = new long [cursor.getCount()];
			 for(int i = 0;cursor.moveToNext();i++){
				 ids[i] = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadAPI._ID));
			 }
			 cursor.close();
		 }
		 
		 if(ids != null && ids.length > 0){
			 Intent intent = new Intent(DownloadAPI.ACTION_ON_DOWNLOAD_DELETE);
			 intent.putExtra(DownloadAPI.BROAD_CAST_DATA_KEY_IDS, ids);
			 context.sendBroadcast(intent);
		 }
	}
	
	@Override
	public String getType(Uri uri) {
		switch (sURIMatcher.match(uri)) {
		case DOWNLOADS:
			return DOWNLOAD_LIST_TYPE; // List of items.
		default:
			return null;
		}

	}
	/**
	 * 位添加非法参数报错
	 */
	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		return insert(arg0,arg1,true);
	}
	
	private Uri insert(Uri arg0, ContentValues arg1,boolean isNeedNotify) {
		LogUtil.i(TAG, "DownloadProvider insert() Uri:"+arg0);
		if (sURIMatcher.match(arg0) != DOWNLOADS && sURIMatcher.match(arg0) != DOWNLOADS_ROOT) {
            throw new IllegalArgumentException("Unknown/Invalid URI " + arg0);
        }
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		if(db == null){
			LogUtil.i(TAG, "DownloadProvider insert() Failed to get SQLiteDatabase");
			return Uri.parse(arg0 + "/" + -1);
		}
		Context context = getContext();
		
		arg1.put(DownloadAPI.TIMES_TAMP, System.currentTimeMillis());
		long rowID = db.insert(DB_TABLE, null, arg1);
		Uri ret = null;
		if(rowID != -1  && sURIMatcher.match(arg0) != DOWNLOADS_ROOT){
			ret = Uri.parse(arg0 + "/" + rowID);
			if(isNeedNotify){
				context.getContentResolver().notifyChange(arg0, null);
			}
			context.startService(new Intent(context, DownloadService.class));
			LogUtil.i(TAG, "DownloadProvider insert() rowID:"+rowID);
		}else{
			LogUtil.i(TAG, "DownloadProvider insert() couldn't insert into downloads database");
		}
		
		if(BookMarkDB.getInstance().getSepecificBookMark(arg1.getAsString(DownloadHttpEngine.CONTENT_ID)) == null) {
			BookMark tmpBookMark = new BookMark();
			tmpBookMark.setContentID(arg1.getAsString(DownloadHttpEngine.CONTENT_ID));
			tmpBookMark.setStatus(BookDigestsDB.STATUS_LOCAL);
			tmpBookMark.setSoftDelete(DBConfig.BOOKMARK_STATUS_SOFT_DELETE_NO);
			tmpBookMark.setBookmarkType(DBConfig.BOOKMARK_TYPE_SYSTEM);
			tmpBookMark.setIsRecentRead(BookMark.RECENT_READ_STATUS_NOT_RECCENT);
            if(arg1.getAsString(DownloadHttpEngine.CONTENT_NAME) != null) {
            	tmpBookMark.setContentName(arg1.getAsString(DownloadHttpEngine.CONTENT_NAME));
            }
            if(arg1.getAsString(DownloadHttpEngine.ICON_URL) != null) {
            	tmpBookMark.setLogoUrl(arg1.getAsString(DownloadHttpEngine.ICON_URL));
            }
			BookMarkDB.getInstance().createOrReusedSysBookMark(tmpBookMark);
		}
		
		return ret;
	}
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		int numValues = values.length;
        for (int i = 0; i < numValues; i++) {
        	if(i == numValues - 1){
        		insert(uri, values[i],true);
        	}else{
        		insert(uri, values[i],false);
        	}
        }
        return numValues;
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}
	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		LogUtil.i(TAG, "DownloadProvider query() Uri:"+arg0);
		if (sURIMatcher.match(arg0) != DOWNLOADS && sURIMatcher.match(arg0) != DOWNLOADS_ROOT) {
            throw new IllegalArgumentException("Unknown/Invalid URI " + arg0);
        }
		
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Context context = getContext();
		//对外是软删除的
		if(sURIMatcher.match(arg0) == DOWNLOADS){
			if(arg2 != null){
				arg2 = "(" + arg2 + ") AND ";
			}else{
				arg2 = "";
			}
			arg2 += DownloadAPI.DELETE + " != 1";
		}
		Cursor cursor = db.query(DB_TABLE, arg1, arg2, arg3, null, null, arg4);
		if (cursor != null) {
			cursor = new ReadOnlyCursorWrapper(cursor);
	    }
        if (cursor != null) {
//        	cursor.setNotificationUri(context.getContentResolver(), arg0);
        	LogUtil.i(TAG, "DownloadProvider created cursor count :" + cursor.getCount() );
        } else {
        	LogUtil.i(TAG, "DownloadProvider query failed in downloads database");
        }
		return cursor;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		LogUtil.i(TAG,"DownloadProvider update() Uri:"+arg0);
		/**
		 * 以下字段不支持更新修改，进行过滤处理
		 */
		if(sURIMatcher.match(arg0) == DOWNLOADS){
			if(arg1.containsKey(DownloadAPI.ACTION_TYPE)){
				arg1.remove(DownloadAPI.ACTION_TYPE);
			}else if(arg1.containsKey(DownloadAPI.FILE_PATH)){
				arg1.remove(DownloadAPI.FILE_PATH);
			}else if(arg1.containsKey(DownloadAPI.DOWNLOAD_URL)){
				arg1.remove(DownloadAPI.DOWNLOAD_URL);
			}
        }else if(sURIMatcher.match(arg0) != DOWNLOADS_ROOT){
        	throw new IllegalArgumentException("Unknown/Invalid URI " + arg0);
        }
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		if(db == null){
			LogUtil.i(TAG, "DownloadProvider insert() Failed to get SQLiteDatabase");
			return 0;
		}
		Context context = getContext();
		
		arg1.put(DownloadAPI.TIMES_TAMP, System.currentTimeMillis());
		int count = db.update(DB_TABLE, arg1, arg2, arg3);
		
		LogUtil.i(TAG, "DownloadProvider update() count:"+count);
		if(count > 0 && sURIMatcher.match(arg0) != DOWNLOADS_ROOT){
			context.getContentResolver().notifyChange(arg0, null);
			context.startService(new Intent(context, DownloadService.class));
			LogUtil.i(TAG,"数据库发起通知           Url ："  +arg0);
		}
		return count;
	}
	
	 private class ReadOnlyCursorWrapper extends CursorWrapper implements CrossProcessCursor {
			private CrossProcessCursor mCursor;
			
	        public ReadOnlyCursorWrapper(Cursor cursor) {
	            super(cursor);
	            mCursor = (CrossProcessCursor) cursor;
	        }
	        @Override
	        public void fillWindow(int pos, CursorWindow window) {
	            mCursor.fillWindow(pos, window);
	        }
	        @Override
	        public CursorWindow getWindow() {
	            return mCursor.getWindow();
	        }
	        @Override
	        public boolean onMove(int oldPosition, int newPosition) {
	            return mCursor.onMove(oldPosition, newPosition);
	        }
	        
	        @Override
			public String getString(int columnIndex) {
				
				return super.getString(columnIndex);
			}

	    }

    private final class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(final Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }


        @Override
        public void onCreate(final SQLiteDatabase db) {
            createTable(db);
            if(DownloadAPI.Setting.mDBUpdateRunnable != null){
            	DownloadAPI.Setting.mDBUpdateRunnable.onCreate(db, DB_TABLE);
            }
        }
        
        private void createTable(SQLiteDatabase db){
        	 db.execSQL("CREATE TABLE " + DB_TABLE + "(" +
        			 DownloadAPI._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        			 DownloadAPI.DOWNLOAD_URL + " TEXT," +
        			 DownloadAPI.FILE_PATH + " TEXT," +
        			 DownloadAPI.FILE_NAME + " TEXT," +
        			 DownloadAPI.STATE + " INTEGER default 0," +
        			 DownloadAPI.FILE_BYTE_SIZE + " INTEGER," +
        			 DownloadAPI.FILE_BYTE_CURRENT_SIZE + " INTEGER," +
        			 DownloadAPI.TIMES_TAMP + " INTEGER," +
        			 DownloadAPI.ACTION_TYPE + " INTEGER," +
        			 DownloadAPI.DELETE + " BOOLEAN default 0," +
        			 DownloadAPI.DATA0 + " TEXT," +
        			 DownloadAPI.DATA1 + " TEXT," +
        			 DownloadAPI.DATA2 + " TEXT," +
        			 DownloadAPI.DATA3 + " TEXT," +
        			 DownloadAPI.DATA4 + " TEXT," +
        			 DownloadAPI.DATA5 + " TEXT," +
        			 DownloadAPI.DATA6 + " TEXT," +
        			 DownloadAPI.DATA7 + " TEXT," +
        			 DownloadAPI.DATA8 + " TEXT," +
        			 DownloadAPI.DATA9 + " TEXT," +
        			 DownloadAPI.DATA10 + " TEXT," +
        			 DownloadAPI.DATA11 + " TEXT," +
        			 DownloadAPI.DATA12 + " TEXT," +
        			 DownloadAPI.DATA13 + " TEXT," +
        			 DownloadAPI.DATA14 + " TEXT," +
        			 DownloadAPI.DATA15 + " TEXT," +
        			 DownloadAPI.DATA16 + " TEXT," +
        			 DownloadAPI.DATA17 + " TEXT" +
                     ");");
        }

        @Override
        public void onUpgrade(final SQLiteDatabase db, int oldV, final int newV) {
            if (oldV < newV) {
                if(DownloadAPI.Setting.mDBUpdateRunnable != null){
                	DownloadAPI.Setting.mDBUpdateRunnable.onUpgrade(db, DB_TABLE);
                }
            }
            if (oldV < DB_VERSION_7) {
            	updateSQLiteDatabaseWhenVersion102(db);
			}
            if (oldV < DB_VERSION_7_2) {
        		db.delete(DB_TABLE,  DownloadHttpEngine.CONTENT_ID + " = ? ", new String[]{"1000000126"});
        		db.delete(DB_TABLE,  DownloadHttpEngine.CONTENT_ID + " = ? ", new String[]{"1000000068"});
        		DownloadInfo infoA = WelcomeActivity.getDownloadInfo("download_info_1984.txt");
    			DownloadInfo infoB = WelcomeActivity.getDownloadInfo("download_info_cmssj.txt");
    			if (infoA!=null) {
    				addBookHadDownloaded(db, infoA);
				}
    			if (infoB!=null) {
    				addBookHadDownloaded(db, infoB);
				}
            	// 修改本地文件名称
            	String newName_1984 = com.lectek.android.lereader.utils.Constants.BOOKS_DOWNLOAD +  "1000000126.epub";
            	String newName_cuimianshi = com.lectek.android.lereader.utils.Constants.BOOKS_DOWNLOAD +  "1000000068.epub";
            	
            	//替换0.6~0.7.1版本
            	String old_version1_cuimianshi = com.lectek.android.lereader.utils.Constants.BOOKS_DOWNLOAD +  "CuiMianShiShouJi.epub";
            	String old_version1_1984 = com.lectek.android.lereader.utils.Constants.BOOKS_DOWNLOAD +  "1984.epub";
            	File old_version1_cuimianshiFile = new File(old_version1_cuimianshi);
            	if (old_version1_cuimianshiFile.exists()) {
            		old_version1_cuimianshiFile.renameTo(new File(newName_cuimianshi));
				}
            	File old_version1_1984File = new File(old_version1_1984);
            	if (old_version1_1984File.exists()) {
            		old_version1_1984File.renameTo(new File(newName_1984));
            	}
            	//替换0.5版本
            	String old_version2_cuimianshi = com.lectek.android.lereader.utils.Constants.BOOKS_DOWNLOAD +  "cx2rZlJmVQ6Aan2GAAIwa5StqI892.epub";
            	String old_version2_1984 = com.lectek.android.lereader.utils.Constants.BOOKS_DOWNLOAD +  "cx2rZlJorv2AfIMMAAgMyVQv_uk60.epub";
            	File old_version2_cuimianshiFile = new File(old_version2_cuimianshi);
            	if (old_version2_cuimianshiFile.exists()) {
            		old_version2_cuimianshiFile.renameTo(new File(newName_cuimianshi));
				}
            	File old_version2_1984File = new File(old_version2_1984);
            	if (old_version2_1984File.exists()) {
            		old_version2_1984File.renameTo(new File(newName_1984));
            	}
            }
            if (oldV < DB_VERSION_8_0) {//内置音乐书籍书架信息
            	DownloadInfo infoA = WelcomeActivity.getDownloadInfo("download_info_jjl.txt");
        		DownloadInfo infoB = WelcomeActivity.getDownloadInfo("download_info_bbqner.txt");
        		if (infoA!=null && CommonUtil.isBookExist(infoA.contentID)) {
        			infoA.state = 3;
        		}
        		if (infoB!=null && CommonUtil.isBookExist(infoB.contentID)) {
        			infoB.state = 3;
        		}
        		if (infoA!=null) 
        		addBookHadDownloaded(db, infoA);
        		if (infoB!=null)
    			addBookHadDownloaded(db, infoB);
			}
        }
        
        @Override
    	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    		if (newVersion == 0) {
    			onCreate(db);
    		} else {
    			if (oldVersion > newVersion) {
    				super.onDowngrade(db, oldVersion, newVersion);
    			} else {
    				onUpgrade(db, oldVersion, newVersion);
    			}
    		}

    	}
    }
    

	/**
	 * 添加已下载好的到数据库
	 * @param contentInfo
	 * @return
	 */
	public static void addBookHadDownloaded(SQLiteDatabase db,DownloadInfo contentInfo) {
		boolean isExist = false;
		Cursor cursor = db.query(DB_TABLE, null, DownloadHttpEngine.CONTENT_ID + " = ?", new String[]{contentInfo.contentID}, null, null, null);
		if(cursor != null){
			if(cursor.moveToFirst()){
				isExist = true;
			}
			cursor.close();
		}
		if(!isExist) {
			ContentValues values = DownloadPresenterLeyue.getBookHadDownloadedContentValues(contentInfo);
			db.insert(DB_TABLE,null, values);
		}
	}
    
	public static void deleteRecordByDownloadInfo(SQLiteDatabase db,DownloadInfo info){
		if(info == null){
			return;
		}
		// 删除下载数据库的数据
		if(info.filePathLocation == null){
			info.filePathLocation = "";
		}
		File file = new File(info.filePathLocation);
		if(file.exists()){
			file.delete();
		}
		db.delete(DB_TABLE, DownloadHttpEngine.CONTENT_ID + " = ", new String[]{info.contentID});
	}
	
    private void updateSQLiteDatabaseWhenVersion102(SQLiteDatabase db){
    	try {
    		db.execSQL("ALTER TABLE " + DB_TABLE + " ADD COLUMN "
    				+ DownloadAPI.DATA17 + " TEXT");
		} catch (SQLException e) {
			useDbWhenCatchThrowable(e,db);
			handleForEditDB(db);
		}
    }
    
    /**直接删除数据库表，重新创建*/
    private void handleForEditDB(SQLiteDatabase db) {
    	try {
    		db.beginTransaction();
    		db.execSQL(" DROP TABLE "+ DB_TABLE);
    		db.execSQL(getCreateTableStr());
    		//由于此处执行晚于MyApplication初始化操作。故书籍写入。。。
			db.setTransactionSuccessful();
		} catch (Exception e) {
			useDbWhenCatchThrowable(e,db);
		}finally {
			db.endTransaction();
		}
	}
    
	private void useDbWhenCatchThrowable(Throwable e,SQLiteDatabase db){
		LogUtil.e("DBException", e);
		openDB(db);
	}
	
	public boolean openDB(SQLiteDatabase db) {
  		try {
  			if (db == null) {
  				db = mOpenHelper.getWritableDatabase();
  			} else {
  				if (!db.isOpen()) {//如果存在实例，但未被打开，则重新打开
  					db = null;
  					db = mOpenHelper.getWritableDatabase();
  				}
  			}
  			return true;
  		} catch (Throwable e) {
  			e.printStackTrace();
  			closeDB(db);
  			return false;
  		}
  	}

  	public void closeDB(SQLiteDatabase db) {
  		if (db != null && db.isOpen()) {
  			db.close();
  			db = null;
  		}
  	}
  	
	public static String getCreateTableStr(){
  		return new String("CREATE TABLE " + DB_TABLE + "(" +
  				 DownloadAPI._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
    			 DownloadAPI.DOWNLOAD_URL + " TEXT," +
    			 DownloadAPI.FILE_PATH + " TEXT," +
    			 DownloadAPI.FILE_NAME + " TEXT," +
    			 DownloadAPI.STATE + " INTEGER default 0," +
    			 DownloadAPI.FILE_BYTE_SIZE + " INTEGER," +
    			 DownloadAPI.FILE_BYTE_CURRENT_SIZE + " INTEGER," +
    			 DownloadAPI.TIMES_TAMP + " INTEGER," +
    			 DownloadAPI.ACTION_TYPE + " INTEGER," +
    			 DownloadAPI.DELETE + " BOOLEAN default 0," +
    			 DownloadAPI.DATA0 + " TEXT," +
    			 DownloadAPI.DATA1 + " TEXT," +
    			 DownloadAPI.DATA2 + " TEXT," +
    			 DownloadAPI.DATA3 + " TEXT," +
    			 DownloadAPI.DATA4 + " TEXT," +
    			 DownloadAPI.DATA5 + " TEXT," +
    			 DownloadAPI.DATA6 + " TEXT," +
    			 DownloadAPI.DATA7 + " TEXT," +
    			 DownloadAPI.DATA8 + " TEXT," +
    			 DownloadAPI.DATA9 + " TEXT," +
    			 DownloadAPI.DATA10 + " TEXT," +
    			 DownloadAPI.DATA11 + " TEXT," +
    			 DownloadAPI.DATA12 + " TEXT," +
    			 DownloadAPI.DATA13 + " TEXT," +
    			 DownloadAPI.DATA14 + " TEXT," +
    			 DownloadAPI.DATA15 + " TEXT," +
    			 DownloadAPI.DATA16 + " TEXT," +
    			 DownloadAPI.DATA17 + " TEXT" +
                 ");");
  	}
}
