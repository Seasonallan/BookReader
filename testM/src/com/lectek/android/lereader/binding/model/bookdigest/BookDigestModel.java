package com.lectek.android.lereader.binding.model.bookdigest;

import android.text.TextUtils;

import com.lectek.android.lereader.binding.model.BaseLoadDataModel;
import com.lectek.android.lereader.binding.model.bookmark_sys.SysBookMarkModel;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;
import com.lectek.android.lereader.utils.CommonUtil;

/**
 * 同步笔记控制
 * @author ljp
 * @date 2014年4月29日
 * @email 451360508@qq.com.com
 */
public class BookDigestModel implements BaseLoadDataModel.ILoadDataCallBack {
    private static final boolean ENABLE = true;
    private AddBookDigestModel mAddBookDigestModel;
    private DelBookDigestModel mDeleteBookDigestModel;
    private UpdateBookDigestModel mUpdateBookDigestModel;
    private GetBookDigestModel mGetBookDigestListModel;

    private SyncBookDigestListModel mSyncBookLabelListModel;

    private static BookDigestModel mInstance;
    public static BookDigestModel getInstance(){
        if(mInstance == null){
            mInstance = new BookDigestModel();
        }
        return mInstance;
    }

    public BookDigestModel() {

        mAddBookDigestModel = new AddBookDigestModel();
        mDeleteBookDigestModel = new DelBookDigestModel();
        mUpdateBookDigestModel = new UpdateBookDigestModel();
        mGetBookDigestListModel = new GetBookDigestModel();

        mSyncBookLabelListModel = new SyncBookDigestListModel();
        mSyncBookLabelListModel.addCallBack(this);
        mGetBookDigestListModel.addCallBack(this);
    }

    /**
     * 用户是否登录
     *
     */
    private boolean isLogin(){
        return ENABLE && !CommonUtil.isGuest();
    }

    /**
     * 同步添加笔记到服务器
     * @param digest
     */
    public void addBookDigest(BookDigests digest){
        if(isLogin()){
            mAddBookDigestModel.start(digest);
        }
    }

    /**
     * 同步删除笔记到服务器
     * @param digest
     */
    public void deleteBookDigest(BookDigests digest){
        if(isLogin()){
            if(TextUtils.isEmpty(digest.getServerId())){
                BookDigests bd = BookDigestsDB.getInstance().getBookDigests(digest);
                if(bd != null)
                    digest.setServerId(bd.getServerId());
            }
            mDeleteBookDigestModel.start(digest);
        }
    }

    /**
     * 同步更新笔记到服务器
     * @param digest
     */
    public void updateBookDigest(BookDigests digest){
        if(isLogin()){
            if(TextUtils.isEmpty(digest.getServerId())){
                BookDigests bd = BookDigestsDB.getInstance().getBookDigests(digest);
                if(bd != null)
                    digest.setServerId(bd.getServerId());
            }
            if(TextUtils.isEmpty(digest.getServerId())){
                digest.setAction(BookDigestsDB.ACTION_ADD);
                mAddBookDigestModel.start(digest);
            }else{
                mUpdateBookDigestModel.start(digest);
            }
        }
    }

    /**
     * 同步本地笔记到服务器
     */
    public void syncBookDigest(String contentId){
        if(isLogin()){
            mSyncBookLabelListModel.start(contentId);
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
    public boolean onPostLoad(Object result, String tag, boolean isSucceed,
                              boolean isCancel, Object... params) {
        dealResult(tag, isSucceed && !isCancel, result);
        return false;
    }

    private void dealResult(String tag, boolean isSucceed, Object result){
        if(mSyncBookLabelListModel.getTag().equals(tag)){
            mGetBookDigestListModel.start(result);
        }else if(mGetBookDigestListModel.getTag().equals(tag)){
            if(result instanceof Boolean && (Boolean)result){
                SysBookMarkModel.getInstance().setSyncDigestMark(false);
            }
        }
    }
}
