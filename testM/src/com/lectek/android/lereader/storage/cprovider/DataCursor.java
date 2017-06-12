package com.lectek.android.lereader.storage.cprovider;

import android.database.AbstractCursor;
import android.database.CursorWindow;

/**
 * Created by Administrator on 14-6-20.
 */
public class DataCursor extends AbstractCursor {

    private Object mData;

    public DataCursor(Object data) {
        mData = data;
    }

    @Override
    public String getString(int column) {
        return (String)mData;
    }

    @Override
    public long getLong(int column) {
        return (Long)mData;
    }

    @Override
    public double getDouble(int column) {
        return (Double)mData;
    }


    @Override
    public final int getInt(int column) {
        return (Integer)mData;
    }

    @Override
    public final short getShort(int column) {
        return (short)getLong(column);
    }
    @Override
    public final float getFloat(int column) {
        return (float) getDouble(column);
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : 1;
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{};
    }

    @Override
    public boolean isNull(int column) {
        return mData == null;
    }

    @Override
    public void fillWindow(int position, CursorWindow window){
    }
}

