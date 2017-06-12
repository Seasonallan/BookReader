package com.lectek.android.lereader.storage.cprovider;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.lectek.android.lereader.lib.storage.dbase.BaseDatabase;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.storage.dbase.GroupMessage;
import com.lectek.android.lereader.storage.dbase.PushMessage;
import com.lectek.android.lereader.storage.dbase.SearchKey;
import com.lectek.android.lereader.storage.dbase.TianYiUserInfo;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.storage.dbase.UserScoreInfo;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;


/**
 * 数据提供,用于存取数据库和文件数据
 * @author laijp
 * @date 2014-6-13
 * @email 451360508@qq.com
 */
public class DataProvider extends ContentProvider {

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(DBConfig.AUTHORITIES, DBConfig.PATH_PREFRENCE + "/#", DBConfig.CODE_PREFRENCE);
        sURIMatcher.addURI(DBConfig.AUTHORITIES, DBConfig.PATH_PREFRENCE_COMMON + "/#", DBConfig.CODE_PREFRENCE_COMMON);

        sURIMatcher.addURI(DBConfig.AUTHORITIES, DBConfig.PATH_SEARCH_KEY, DBConfig.CODE_SEARCH_KEY);
        sURIMatcher.addURI(DBConfig.AUTHORITIES, DBConfig.PATH_DIGEST, DBConfig.CODE_DIGEST);
        sURIMatcher.addURI(DBConfig.AUTHORITIES, DBConfig.PATH_MARK, DBConfig.CODE_MARK);
        sURIMatcher.addURI(DBConfig.AUTHORITIES, DBConfig.PATH_PUSH, DBConfig.CODE_PUSH);
        sURIMatcher.addURI(DBConfig.AUTHORITIES, DBConfig.PATH_SCORE, DBConfig.CODE_SCORE);
        sURIMatcher.addURI(DBConfig.AUTHORITIES, DBConfig.PATH_USER_LYUE, DBConfig.CODE_USER_LYUE);
        sURIMatcher.addURI(DBConfig.AUTHORITIES, DBConfig.PATH_USER_TY, DBConfig.CODE_USER_TY);
        sURIMatcher.addURI(DBConfig.AUTHORITIES, DBConfig.PATH_GROUP, DBConfig.CODE_GROUP);
    }


    private BaseDatabase getDatabase(int code){
        switch(code){
            case DBConfig.CODE_SEARCH_KEY:
                return new BaseDatabase<SearchKey>(SearchKey.class);
            case DBConfig.CODE_DIGEST:
                return new BaseDatabase<BookDigests>(BookDigests.class);
            case DBConfig.CODE_MARK:
                return new BaseDatabase<BookMark>(BookMark.class);
            case DBConfig.CODE_PUSH:
                return new BaseDatabase<PushMessage>(PushMessage.class);
            case DBConfig.CODE_SCORE:
                return new BaseDatabase<UserScoreInfo>(UserScoreInfo.class);
            case DBConfig.CODE_USER_LYUE:
                return new BaseDatabase<UserInfoLeyue>(UserInfoLeyue.class);
            case DBConfig.CODE_USER_TY:
                return new BaseDatabase<TianYiUserInfo>(TianYiUserInfo.class);
            case DBConfig.CODE_GROUP:
                return new BaseDatabase<GroupMessage>(GroupMessage.class);
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        switch(sURIMatcher.match(uri)){
            case DBConfig.CODE_SEARCH_KEY:
                return "vnd.android.cursor.dir/" + DBConfig.PATH_SEARCH_KEY;
            case DBConfig.CODE_DIGEST:
                return "vnd.android.cursor.dir/" + DBConfig.PATH_DIGEST;
            case DBConfig.CODE_MARK:
                return "vnd.android.cursor.dir/" + DBConfig.PATH_MARK;
            case DBConfig.CODE_PUSH:
                return "vnd.android.cursor.dir/" + DBConfig.PATH_PUSH;
            case DBConfig.CODE_SCORE:
                return "vnd.android.cursor.dir/" + DBConfig.PATH_SCORE;
            case DBConfig.CODE_USER_LYUE:
                return "vnd.android.cursor.dir/" + DBConfig.PATH_USER_LYUE;
            case DBConfig.CODE_USER_TY:
                return "vnd.android.cursor.dir/" + DBConfig.PATH_USER_TY;
            case DBConfig.CODE_GROUP:
                return "vnd.android.cursor.dir/" + DBConfig.PATH_GROUP;
            case DBConfig.CODE_PREFRENCE:
                return "vnd.android.cursor.item/" + DBConfig.PATH_PREFRENCE;
            case DBConfig.CODE_PREFRENCE_COMMON:
                return "vnd.android.cursor.item/" + DBConfig.PATH_PREFRENCE_COMMON;
            default:
                throw new IllegalArgumentException("Unknown Uri : " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        int code = sURIMatcher.match(uri);
        if (code == DBConfig.CODE_PREFRENCE || code == DBConfig.CODE_PREFRENCE_COMMON){
            SharedPreferences preferences = getContext().getSharedPreferences(
                    code == DBConfig.CODE_PREFRENCE?  "PREFS_USER_INFO" : "info_prefs" , 0);
            if (!preferences.contains(s)){
                return null;
            }
            Object value = null;
            String type = uri.getLastPathSegment();
            if (type.equals(DBConfig.PrefrenceType.BOOLEAN)){
                value = preferences.getBoolean(s, false)? 1:0;
            }else if (type.equals(DBConfig.PrefrenceType.LONG)){
                value = preferences.getLong(s, -1);
            }else if (type.equals(DBConfig.PrefrenceType.FLOAT)){
                value = preferences.getFloat(s, -1);
            }else if (type.equals(DBConfig.PrefrenceType.INT)){
                value = preferences.getInt(s, -1);
            }else if (type.equals(DBConfig.PrefrenceType.STRING)){
                value = preferences.getString(s, null);
            }
            return new DataCursor(value);
        }
        BaseDatabase database = getDatabase(code);
        if (database != null) {
        	if(s2 != null) {
        		return database.select(s, s2);
        	}else {
        		return database.select(s);
        	}
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int code = sURIMatcher.match(uri);
        if (code == DBConfig.CODE_PREFRENCE || code == DBConfig.CODE_PREFRENCE_COMMON){
            SharedPreferences.Editor editor = getContext().getSharedPreferences(
                    code == DBConfig.CODE_PREFRENCE?  "PREFS_USER_INFO" : "info_prefs" , 0).edit();
            Set<Map.Entry<String, Object>> entrySet = contentValues.valueSet();
            Iterator<Map.Entry<String, Object>> entriesIter = entrySet.iterator();
            while (entriesIter.hasNext()) {
                Map.Entry<String, Object> entry = entriesIter.next();
                Object value = entry.getValue();
                if (value instanceof String){
                    editor.putString(entry.getKey(), (String) value);
                }else if (value instanceof Integer){
                    editor.putInt(entry.getKey(), (Integer) value);
                }else if (value instanceof Float){
                    editor.putFloat(entry.getKey(), (Float) value);
                }else if (value instanceof Boolean){
                    editor.putBoolean(entry.getKey(), (Boolean) value);
                }else if (value instanceof Long){
                    editor.putLong(entry.getKey(), (Long) value);
                }
            }
            return editor.commit() ? uri : null;
        }
        BaseDatabase database = getDatabase(code);
        if (database != null)
            database.insert(contentValues);
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int code = sURIMatcher.match(uri);
        BaseDatabase database = getDatabase(code);
        if (database != null)
            return database.delete(s);
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        int code = sURIMatcher.match(uri);
        BaseDatabase database = getDatabase(code);
        if (database != null)
            return database.update(contentValues, s);
        return 0;
    }

}
