package com.lectek.android.lereader.binding.model.markgroup;

import com.lectek.android.lereader.binding.model.BaseLoadDataModel;

/**
 * 简易回调
 * Created by Administrator on 14-7-16.
 */
public class SimpeLoadDataCallBack implements BaseLoadDataModel.ILoadDataCallBack {

    @Override
    public boolean onStartFail(String tag, String state, Object... params) {
        return false;
    }

    @Override
    public boolean onPreLoad(String tag, Object... params) {
        return false;
    }

    @Override
    public boolean onFail(Exception e, String tag, Object... params) {
        return false;
    }

    @Override
    public boolean onPostLoad(Object result, String tag, boolean isSucceed, boolean isCancel, Object... params) {
        return false;
    }
}
