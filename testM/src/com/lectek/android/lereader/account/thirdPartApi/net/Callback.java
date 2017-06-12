package com.lectek.android.lereader.account.thirdPartApi.net;

public abstract interface Callback{
	/**
	 * 成功
	 * */
    public abstract void onSuccess(Object paramObject);
    
    /**
     * 失败
     * */
    public abstract void onFail(int paramInt, String paramString);
    
    /**
     * 结束
     * */
    public abstract void onCancel(int paramInt);
}
