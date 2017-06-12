package com.lectek.android.lereader.lib.storage.dbase;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.lectek.android.lereader.lib.storage.dbase.iterface.Table;

/**
 * 数据库操作类
 * @author laijp
 * @date 2014-6-13
 * @email 451360508@qq.com
 */
public class BaseDatabase<T extends BaseDao>{

    private String mTableName;
    
	private Class<T> tClass;

    private IDbHelper mDbHelper;
    /**
     * 请在tClass中定义Table信息
     */
	public BaseDatabase(Class<T> tClass) {
		this.tClass = tClass;
        this.mTableName = tClass.getAnnotation(Table.class).name();
        T newTObject;
		try {
			newTObject = tClass.newInstance();
	        this.mDbHelper = newTObject.newDatabaseHelper();
	        List<String> primaryKeys = newTObject.getPrimaryKeyColumnName();
	        if (primaryKeys == null || primaryKeys.size() == 0) {
	            throw new RuntimeException("Table required at least one primary key!");
			}
		} catch (InstantiationException e) {
            throw new RuntimeException("Model lack of The empty constructor!");
		} catch (IllegalAccessException e) {
		}
        if (mDbHelper == null){
            throw new RuntimeException("Not a database! newDatabaseHelper method return null.");
        }
	}
	

    /**
     * 通过条件获取
     */
	public final Cursor select(String whereClause) { 
		String order = null;
		boolean isOrder = tClass.getAnnotation(Table.class).isOrderBy();
		if (isOrder) {
			T newTObject;
			try {
				newTObject = tClass.newInstance();
		        this.mDbHelper = newTObject.newDatabaseHelper();
		        order = newTObject.getOrderColumnMessage();
			} catch (InstantiationException e) {
	            throw new RuntimeException("Model lack of The empty constructor!");
			} catch (IllegalAccessException e) {
			} catch (Exception e) {
                e.printStackTrace();
            }
		}
		return mDbHelper.getDatabase().query(mTableName, null, whereClause, null, null, null,
				order); 
	}
	
	/**
     * 通过条件获取
     */
	public final Cursor select(String whereClause, String sortOrder) { 
		return mDbHelper.getDatabase().query(mTableName, null, whereClause, null, null, null,sortOrder); 
	}

    /**
     * 插入
     */
    public final long insert(ContentValues contentValues) {
        if (contentValues != null) {
            try {
                long id = mDbHelper.getDatabase().insert(mTableName, null, contentValues);
                return id;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
    /**
     * 插入
     */
	public final long insert(T item) {
		if (item != null) {
			try {
				ContentValues value = item.toContentValues();
				long id = mDbHelper.getDatabase().insert(mTableName, null, value);
				return id;
			} catch (Exception e) {
                e.printStackTrace();
            }
		}
		return -1;
	}
	

    /**
     * 根据条件删除
     * @param whereClause 条件，为空则为清空表单
     */
	public final int delete(String whereClause) {
        return mDbHelper.getDatabase().delete(mTableName, whereClause, null);
	}

    /**
     * 根据条件更新数据
     */
	public final int update(ContentValues values, String whereClause) {
        return mDbHelper.getDatabase().update(mTableName, values, whereClause, null); 
	}
	
    /**
     * 根据条件更新数据
     */
	public final int update(T model, String whereClause) {
        int result = 0;
        ContentValues values = null;
            values = model.toContentValues(); 
            result  = mDbHelper.getDatabase().update(mTableName, values, whereClause, null);

        return result;
	}

	/**
	 * 通过键值对获取条件
	 * @param params
	 * @return
	 */
	public static String getWhere(Object... params){
        String where = null;
        if (params != null && params.length % 2 == 0){
            where = "";
            for (int i =0 ; i< params.length/2; i++){
                if (i == 0){
                    where += params[i*2] + " = '" + params[i*2+1] + "'";
                }else{
                    where += " AND " + params[i*2] + " = '" + params[i*2+1] + "'";
                }
            }
        }
        return where;
	}
	
    /**
     * 根据唯一码构建SQL搜索条件语句
     * @param model
     * @return
     */
    public String getPrimaryKeyWhereClause(T model){
        List<String> keys = model.getPrimaryKeyColumnName();
        String where = null;
        if (keys != null && keys.size() > 0){
            where = "";
            for (int i =0 ; i< keys.size(); i++){
            	where += keys.get(i) + " = '" + model.getValue(keys.get(i)) + "'";
                if (i < keys.size() -1){ 
                    where += " AND ";
                }
            }
        }
        return where;
    }

    /**
     * 通过主键搜索数据
     * @param model
     * @return
     */
	public Cursor getCursorByPrimaryKey(T model) {
		return select(getPrimaryKeyWhereClause(model)); 
	}
	
    /**
     * 根据主键值更新数据
     */
	public int update(T model) {
		return update(model, getPrimaryKeyWhereClause(model));
	}

    /**
     * 根据主键值删除数据
     */
	public int deleteItem(T model) {
		return delete(getPrimaryKeyWhereClause(model));
	}

    /**
     * 清空数据库
     */
	public final int clearAll() {
		return delete(null);
	}
	
    /**
     * 获取所有数据
     * @return
     */
	public JsonArrayList<T> getAll() {
        JsonArrayList<T> items = new JsonArrayList<T>(tClass);
		Cursor cursor = select(null);
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				T newTObject;
				try {
					newTObject = tClass.newInstance();
                    newTObject.fromCursor(cursor);
					items.add(newTObject);
				} catch (InstantiationException e) {
				} catch (IllegalAccessException e) {
				}
			}
			cursor.close();
			cursor = null;
		}
		return items;
	}

    /**
     * 是否存在,通过主键索引
     * @param model
     * @return
     */
    public boolean exist(T model) {
        Cursor cursor = getCursorByPrimaryKey(model);
        if (cursor != null && cursor.getCount() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 智能插入，有则更新
     * @param model
     * @return
     */
    public long intelligenceInsert(T model){
        if (exist(model)){
            return update(model);
        }else{
            return insert(model);
        }
    }

    /**
     * 智能删除[通过伪主键 索引删除]
     * @param model
     * @return
     */
	public int intelligenceDeleteByPrimaryKey(T model) {  
		return delete(getPrimaryKeyWhereClause(model));
	}



	public T getItem(T model) {
		Cursor cursor = getCursorByPrimaryKey(model);
		// convert cursor to list items.
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			T newTObject = null;
			try {
				newTObject = tClass.newInstance();
                newTObject.fromCursor(cursor);
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
			cursor.close();
			cursor = null;
			return newTObject;
		}
		return null;
	}

}
