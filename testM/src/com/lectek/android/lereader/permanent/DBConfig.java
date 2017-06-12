package com.lectek.android.lereader.permanent;

import android.net.Uri;

public class DBConfig {

	public static final String DATABASE_NAME = "surfingReader.db"; 
	public static final int DATABASE_VERSION13 = 14;//乐阅新版本
	
	 /** 主机名，匹配Manifest.xml中注册provider的 android:authorities*/
    public static final String AUTHORITIES = "com.lectek.android.LYReader.provider.DataProvider";

    /**  SharedPreferences存储*/
    public static final String PATH_PREFRENCE = "pref_user";
    public static final int CODE_PREFRENCE = 100;

    public static final String PATH_PREFRENCE_COMMON = "pref_common";
    public static final int CODE_PREFRENCE_COMMON = 101;

    public static interface PrefrenceType{
        public static String STRING = "0";
        public static String INT = "1";
        public static String FLOAT = "2";
        public static String LONG = "3";
        public static String BOOLEAN = "4";
    }
	
    
    /** 搜索关键字*/
    public static final String PATH_SEARCH_KEY = "searchKey";
    public static final int CODE_SEARCH_KEY = 1;
    public static final Uri CONTENT_URI = Uri.parse("content://"
            + AUTHORITIES + "/" + PATH_SEARCH_KEY);


    /** 书籍笔记*/
    public static final String PATH_DIGEST = "digest";
    public static final int CODE_DIGEST  = 2;
    public static final Uri CONTENT_URI_DIGEST = Uri.parse("content://"
            + AUTHORITIES + "/" + PATH_DIGEST);


    /** 书籍书签*/
    public static final String PATH_MARK = "bookmark";
    public static final int CODE_MARK  = 3;
    public static final Uri CONTENT_URI_MARK = Uri.parse("content://"
            + AUTHORITIES + "/" + PATH_MARK);

    /** 天翼阅读账号信息*/
    public static final String PATH_USER_TY = "userTianyi";
    public static final int CODE_USER_TY  = 4;
    public static final Uri CONTENT_URI_USER_TY = Uri.parse("content://"
            + AUTHORITIES + "/" + PATH_USER_TY);

    /** 乐阅账号信息*/
    public static final String PATH_USER_LYUE = "userLeyue";
    public static final int CODE_USER_LYUE  = 5;
    public static final Uri CONTENT_URI_USER_LYUE = Uri.parse("content://"
            + AUTHORITIES + "/" + PATH_USER_LYUE);

    /** 用户积分*/
    public static final String PATH_SCORE = "score";
    public static final int CODE_SCORE  = 6;
    public static final Uri CONTENT_URI_SCORE = Uri.parse("content://"
            + AUTHORITIES + "/" + PATH_SCORE);

    /** 推送信息*/
    public static final String PATH_PUSH = "push";
    public static final int CODE_PUSH  = 7;
    public static final Uri CONTENT_URI_PUSH = Uri.parse("content://"
            + AUTHORITIES + "/" + PATH_PUSH);

    /** 书架分组信息*/
    public static final String PATH_GROUP = "shelf_group";
    public static final int CODE_GROUP  = 8;
    public static final Uri CONTENT_URI_GROUP = Uri.parse("content://"
            + AUTHORITIES + "/" + PATH_GROUP);
    /******************* 书签 ***************************************/
    
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
 
	public static final int BOOKMARK_STATUS_SOFT_DELETE = 1;
	public static final int BOOKMARK_STATUS_SOFT_DELETE_NO = 0;
	public static final int BOOKMARK_TYPE_USER = 1;
	public static final int BOOKMARK_TYPE_SYSTEM = 0;

	public static final String DB_NAME = "BookMark.db"; 
    public static final int DB_VERSION = 4;
	
    /******************* 书签end ***************************************/
}
