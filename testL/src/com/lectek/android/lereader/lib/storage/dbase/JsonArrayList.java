package com.lectek.android.lereader.lib.storage.dbase;

import android.database.Cursor;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Http json格式转换列表数据
 * @author laijp
 * @date 2014-6-13
 * @email 451360508@qq.com
 */
public class JsonArrayList<T extends BaseDao> extends ArrayList<T> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6279092799396446519L;
	Class<T> tClass;
    public JsonArrayList(Class<T> tClass){
        this.tClass = tClass;
    }

    /**
     * 由cursor转换为列表数据
     * @param cursor
     */
    public JsonArrayList<T> fromCursor(Cursor cursor){
        if (cursor != null && cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                T newTObject;
                try {
                    newTObject = tClass.newInstance();
                    newTObject.fromCursor(cursor);
                    add(newTObject);
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                }
            }
            cursor.close();
            cursor = null;
        }
        return this;
    }

    /**
     * 网络请求转化
     * @param array
     */
    public JsonArrayList<T> fromJsonArray(JSONArray array){
        int count = array.length();
        for(int i = 0;i < count; i++){
            try {
                JSONObject obj = array.getJSONObject(i);
                T item = tClass.newInstance();
                item.fromJsonObject(obj);
                add(item);
            } catch (JSONException e) {
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }
        }
        return this;
    }

    /**
     * 转化为网络请求数据
     * @return
     */
    public JSONArray toJsonArray(){
        JSONArray array = new JSONArray();
        for (T item: this){
                JSONObject obj = item.toJsonObject();
                array.put(obj);
        }
        return array;
    }
}














