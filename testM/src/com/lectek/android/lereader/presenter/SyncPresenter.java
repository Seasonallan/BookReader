package com.lectek.android.lereader.presenter;

import android.content.Context;
import android.content.Intent;

import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.binding.model.bookdigest.BookDigestModel;
import com.lectek.android.lereader.binding.model.bookmark.UserBookMarkModel;
import com.lectek.android.lereader.binding.model.bookmark_sys.SysBookMarkModel;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;
import com.lectek.android.lereader.storage.dbase.mark.BookMarkDB;

/**
 * 同步管理
 * @author ljp
 * @date 2014年4月29日
 * @email 451360508@qq.com.com
 */
public class SyncPresenter {

    public static Context getContext() {
        return MyAndroidApplication.getInstance();
    }

    private static boolean sSwitchCountTag = false;

    /**
     * 设置为切换用户的操作
     * @param action
     */
    public static void setSwitchTagAction(boolean action){
        sSwitchCountTag = action;
    }

    /**
     * 开启同步任务【笔记，用户书签，系统书签】
     */
    public static void startSyncTask(){
        LogUtil.i("SYNC.. action: startSyncTask; ");
        if(sSwitchCountTag){
            clearUserRecords();
        }
        //同步系统书签信息
        SysBookMarkModel.getInstance().syncSystemBookMark(true, sSwitchCountTag);
        sSwitchCountTag = false;
    }


    /**
     * 同步提交本地系统书签
     * */
    public static void startSyncLocalSysBookMarkTask(){
        LogUtil.i("SYNC.. action: startSyncLocalSysBookMarkTask;");
        SysBookMarkModel.getInstance().syncLocalSystemBookMark();
    }



    /**
     * 开启同步任务【笔记，用户书签】
     */
    public static void startSyncTask(String contentId){
        LogUtil.i("SYNC.. action: startSyncTask; " + contentId);
        //同步笔记信息
        BookDigestModel.getInstance().syncBookDigest(contentId);
        //同步用户书签信息
        UserBookMarkModel.getInstance().syncUserBookMark(contentId);
    }

    /**
     * 开启同步系统书签
     */
    public static void syncSystemBookMark(){
        //同步系统书签信息
        SysBookMarkModel.getInstance().syncSystemBookMark(false, false);
        LogUtil.i("SYNC.. action: syncSystemBookMark; result>> ");
    }


    /**
     * 清空用户书签笔记
     */
    public static void clearUserRecords(){
        int resDigest = BookDigestsDB.getInstance().clearBookDigests();
        int resMark = BookMarkDB.getInstance().clearBookmarks();
        int resDownloadinfo= DownloadPresenterLeyue.clearDownloadInfo();
        LogUtil.i("SYNC.. action: clearUserRecords; result>> " + resDigest + "," + resMark + "," + resDownloadinfo);
        getContext().sendBroadcast(new Intent(AppBroadcast.ACTION_UPDATE_BOOKSHELF));
    }
}














