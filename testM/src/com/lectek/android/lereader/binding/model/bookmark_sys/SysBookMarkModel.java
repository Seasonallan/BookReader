package com.lectek.android.lereader.binding.model.bookmark_sys;

import android.content.Context;
import android.text.TextUtils;

import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.binding.model.BaseLoadDataModel;
import com.lectek.android.lereader.binding.model.bookdigest.BookDigestModel;
import com.lectek.android.lereader.binding.model.bookmark.UserBookMarkModel;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.utils.CommonUtil;


/**
 * 系统书签同步控制
 * @author ljp
 * @date 2014年4月29日
 * @email 451360508@qq.com.com
 */
public class SysBookMarkModel implements BaseLoadDataModel.ILoadDataCallBack {

    public static final String SOURCE_LEYUE = "1";//乐阅平台
    public static final String SOURCE_SURFING = "7";//天翼阅读平台

    private SyncSysBookMarkListModel mAddBookDigestModel;
    private GetSysBookMarkModel mGetBookDigestListModel;

    private static SysBookMarkModel mInstance;
    public static SysBookMarkModel getInstance(){
        if(mInstance == null){
            mInstance = new SysBookMarkModel();
        }
        return mInstance;
    }

    public SysBookMarkModel() {
        mAddBookDigestModel = new SyncSysBookMarkListModel();
        mGetBookDigestListModel = new GetSysBookMarkModel();
        mAddBookDigestModel.addCallBack(this);
        mGetBookDigestListModel.addCallBack(this);
    }
    
    private boolean mSyncDigestMark = true;
    private boolean mAddNativeBook = false;

    /**
     * 是否同步用户书签 ，笔记
     * @return
     */
    public boolean isSyncDigestMark() {
        return mSyncDigestMark;
    }

    public void setSyncDigestMark(boolean mSyncDigestMark) {
        this.mSyncDigestMark = mSyncDigestMark;
    }

    /**
     * 同步系统书签
     */
    public void syncSystemBookMark(boolean syncDigestMark, boolean addNativeBook){
        if(AccountManager.getInstance().isLogin()){
            this.mAddNativeBook = addNativeBook;
            setSyncDigestMark(syncDigestMark);;
            mAddBookDigestModel.start();
        }
    }

    /**
     * 上传本地未同步系统书签
     */
    public void syncLocalSystemBookMark(){
        if(AccountManager.getInstance().isLogin()){
            setSyncDigestMark(false);;
            mAddBookDigestModel.start(false);
        }
    }


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
        if(mAddBookDigestModel.getTag().equals(tag)){
            if(result instanceof Boolean && (Boolean)result){
                mGetBookDigestListModel.start(mAddNativeBook);
            }
        }else  if(mGetBookDigestListModel.getTag().equals(tag)){
            if(result instanceof Boolean && (Boolean)result && isSyncDigestMark()){
                //同步笔记信息
                BookDigestModel.getInstance().syncBookDigest(null);
                //同步用户书签信息
                UserBookMarkModel.getInstance().syncUserBookMark(null);
            }
        }
        return false;
    }
}
