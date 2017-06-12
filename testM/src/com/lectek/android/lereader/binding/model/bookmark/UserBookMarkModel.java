package com.lectek.android.lereader.binding.model.bookmark;

import com.lectek.android.lereader.binding.model.BaseLoadDataModel;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.utils.CommonUtil;


/**
 * 用户书签同步控制
 * @author ljp
 * @date 2014年4月29日
 * @email 451360508@qq.com.com
 */
public class UserBookMarkModel implements BaseLoadDataModel.ILoadDataCallBack  {

    private static final boolean ENABLE = true;
    private AddUserBookMarkModel mAddBookMarkModel;
    private DelUserBookMarkModel mDeleteBookMarkModel;
    private GetUserBookMarkModel mGetBookMarkListModel;

    private SyncUserBookMarkModel mSyncBookMarkListModel;


    private static UserBookMarkModel mInstance;
    public static UserBookMarkModel getInstance(){
        if(mInstance == null){
            mInstance = new UserBookMarkModel();
        }
        return mInstance;
    }

    public UserBookMarkModel() {
        mAddBookMarkModel = new AddUserBookMarkModel();
        mDeleteBookMarkModel = new DelUserBookMarkModel();
        mGetBookMarkListModel = new GetUserBookMarkModel();

        mSyncBookMarkListModel = new SyncUserBookMarkModel();
        mSyncBookMarkListModel.addCallBack(this);
    }

    /**
     * 用户是否登录
     *
     */
    private boolean isLogin(){
        return ENABLE && !CommonUtil.isGuest();
    }

    /**
     * 同步添加书签到服务器
     */
    public void addBookMark(BookMark bookMark){
        if(isLogin()){
            mAddBookMarkModel.start(bookMark);
        }
    }

    /**
     * 同步删除笔记到服务器
     * @param bookMark
     */
    public void deleteBookMark(BookMark bookMark){
        if(isLogin()){
            mDeleteBookMarkModel.start(bookMark);
        }
    }

    /**
     * 同步本地书签到服务器
     */
    public void syncUserBookMark(String contentId){
        if(isLogin()){
            mSyncBookMarkListModel.start(contentId);
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
        if(mSyncBookMarkListModel.getTag().equals(tag)){
            mGetBookMarkListModel.start(result);
        }
        return false;
    }
}
