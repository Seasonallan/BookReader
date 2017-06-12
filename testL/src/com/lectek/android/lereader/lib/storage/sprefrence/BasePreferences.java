package com.lectek.android.lereader.lib.storage.sprefrence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;


/**
 * sharePrefrence文件存储数据基类
 * @author laijp
 * @date 2014-6-13
 * @email 451360508@qq.com
 */
public abstract class BasePreferences {

	protected Context context;

    protected BasePreferences(Context context){
        this.context = context;
    }

    /**
     * 获取URI的路径
     * @return
     */
    protected abstract Uri getUri(Class className);

    private boolean setValue(Class className, ContentValues values){
        return context.getContentResolver().insert(getUri(String.class) ,
                values) != null;
    }

    /**
     * 通过key获取String类型数据
     */
    public String getStringValue(String key, String defaultValue){
        Cursor cursor = context.getContentResolver().query(getUri(String.class), null,
                key, null, null);
        if (cursor == null){
            return defaultValue;
        }
        try{
            return cursor.getString(0);
        }finally {
            cursor.close();
        }
    }

    /**
     * 设置String类型数据的值
     */
    public boolean setStringValue(String key, String value){
        ContentValues values = new ContentValues();
        values.put(key, value);
        return setValue(String.class, values);
    }


    /**
     * 通过key获取int类型数据
     */
    public int getIntValue(String key, int defaultValue){
        Cursor cursor = context.getContentResolver().query(getUri(Integer.class), null,
                key, null, null);
        if (cursor == null){
            return defaultValue;
        }
        try{
            return cursor.getInt(0);
        }finally {
            cursor.close();
        }
    }

    /**
     * 设置int类型数据的值
     */
    public boolean setIntValue(String key, int value){
        ContentValues values = new ContentValues();
        values.put(key, value);
        return setValue(Integer.class, values);
    }


    /**
     * 通过key获取float类型数据
     */
    public float getFloatValue(String key, float defaultValue){
        Cursor cursor = context.getContentResolver().query(getUri(Float.class), null,
                key, null, null);
        if (cursor == null){
            return defaultValue;
        }
        try{
            return cursor.getFloat(0);
        }finally {
            cursor.close();
        }
    }

    /**
     * 设置float类型数据的值
     */
    public boolean setFloatValue(String key, float value){
        ContentValues values = new ContentValues();
        values.put(key, value);
        return setValue(Float.class, values);
    }


    /**
     * 设置long类型数据的值
     */
    public boolean setLongValue(String key, long value){
        ContentValues values = new ContentValues();
        values.put(key, value);
        return setValue(Long.class, values);
    }


    /**
     * 通过key获取long类型数据
     */
    public long getLongValue(String key, long defaultValue){
        Cursor cursor = context.getContentResolver().query(getUri(Long.class), null,
                key, null, null);
        if (cursor == null){
            return defaultValue;
        }
        try{
            return cursor.getLong(0);
        }finally {
            cursor.close();
        }
    }

    /**
     * 通过key获取boolean类型数据
     */
    public boolean getBooleanValue(String key, boolean defaultValue){
        Cursor cursor = context.getContentResolver().query(getUri(Boolean.class), null,
                key, null, null);
        if (cursor == null){
            return defaultValue;
        }
        try{
            return cursor.getInt(0) == 1;
        }finally {
            cursor.close();
        }
    }

    /**
     * 设置boolean类型数据的值
     */
    public boolean setBooleanValue(String key, boolean value){
        ContentValues values = new ContentValues();
        values.put(key, value);
        return setValue(Boolean.class, values);
    }

}
