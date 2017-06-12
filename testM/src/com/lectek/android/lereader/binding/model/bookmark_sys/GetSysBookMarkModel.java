package com.lectek.android.lereader.binding.model.bookmark_sys;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.text.TextUtils;

import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.download.DownloadHttpEngine;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.BookGroupMarkResponse;
import com.lectek.android.lereader.net.response.DownloadInfo;
import com.lectek.android.lereader.net.response.SystemBookMarkGroupResponseInfo;
import com.lectek.android.lereader.net.response.SystemBookMarkResponseInfo;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.permanent.DownloadAPI;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.storage.dbase.GroupMessage;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.dbase.mark.BookMarkDB;
import com.lectek.android.lereader.storage.dbase.util.GroupInfoDB;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.utils.AES;
import com.lectek.android.lereader.utils.Constants;

/**
 * 获取云端系统书签到本地
 * @author ljp
 * @date 2014年4月29日
 * @email 451360508@qq.com.com
 */
public class GetSysBookMarkModel extends BaseLoadNetDataModel<Boolean> {

	@Override
	protected Boolean onLoad(Object... params) throws Exception {
        boolean addNativeBook = false;
        if (params != null && params.length == 1){
            Object object = params[0];
            if (object instanceof  Boolean){
                addNativeBook = (Boolean)object;
            }
        }
      /*  List<BookMark> sarray = BookMarkDB.getInstance(getContext()).getSyncSystemBookMarks();
        if(sarray != null && sarray.size() > 0){
            return true;
        }*/
        boolean isChanged = false;
        //存储所有书签
        String[] sources = new String[]{SysBookMarkModel.SOURCE_LEYUE, SysBookMarkModel.SOURCE_SURFING};
        for (String source: sources){
            ArrayList<SystemBookMarkResponseInfo> sysMarkLists = ApiProcess4Leyue.getInstance(getContext()).getSysBookMark(source);
            if(sysMarkLists != null){
                if(sysMarkLists != null && sysMarkLists.size() > 0){
                    for(SystemBookMarkResponseInfo sysBookMark : sysMarkLists){
                        BookMark bookMark = new BookMark();
                        bookMark.setStatus(BookDigestsDB.STATUS_SYNC);
                        bookMark.setSoftDelete(DBConfig.BOOKMARK_STATUS_SOFT_DELETE_NO);
                        bookMark.setContentID(sysBookMark.getBookId());
                        bookMark.setBookmarkType(DBConfig.BOOKMARK_TYPE_SYSTEM);
                        bookMark.setBookmarkID(sysBookMark.getBookMarkId());
                        bookMark.contentName = sysBookMark.getBookName();
                        bookMark.logoUrl = sysBookMark.getCoverPath();
                        boolean resT = BookMarkDB.getInstance().createSysBookMarkIfNotExit(bookMark);
                        //res为false代表本地软删除未提交到网络
                        if (resT){
                            DownloadInfo downloadInfo = new DownloadInfo();
                            downloadInfo.authorName = sysBookMark.getAuthor();
                            downloadInfo.contentID = sysBookMark.getBookId();
                            downloadInfo.contentName = sysBookMark.getBookName();
                            downloadInfo.logoUrl = sysBookMark.getCoverPath();
                            downloadInfo.secret_key = AES.encrypt(sysBookMark.getBookKey());
                            downloadInfo.contentType = source.equals(SysBookMarkModel.SOURCE_SURFING) ?
                                    DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_CEB : DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_BOOK;
                            downloadInfo.downloadType = downloadInfo.contentType;
                            downloadInfo.state = DownloadAPI.STATE_ONLINE;
                            downloadInfo.filePathLocation = Constants.BOOKS_DOWNLOAD + downloadInfo.contentID+".epub";
                            File file = new File(Constants.BOOKS_DOWNLOAD + downloadInfo.contentID+".epub");
                            if (file.exists() && file.length() > 0){
                                downloadInfo.state = DownloadAPI.STATE_FINISH;
                                downloadInfo.isOrder = true;
                            }
                            boolean res = DownloadPresenterLeyue.addBookDownloadedInfoIfNotExit(downloadInfo);
                            if (res){
                                isChanged = true;
                            }
                        }
                    }
                }
            }
            if (source.equals(SysBookMarkModel.SOURCE_LEYUE) && addNativeBook){
                addNativeBookLeyue(sysMarkLists);
            }
        }
        //更新所有书签的分组ID
        ArrayList<SystemBookMarkGroupResponseInfo> groupInfoList =  ApiProcess4Leyue.getInstance(getContext()).getSysBookMarkGroup(SysBookMarkModel.SOURCE_LEYUE);
        int defGroupId =  PreferencesUtil.getInstance(getContext()).getBookMarkGroupId();
        if(groupInfoList != null && groupInfoList.size() > 0){
            for (SystemBookMarkGroupResponseInfo group: groupInfoList){
                if (defGroupId > 0){
                    if (defGroupId == group.groupId){
                        group.isDefault = 0;
                    }else{
                        group.isDefault = 1;
                    }
                }else{
                    if (group.isDefault == 0){
                        PreferencesUtil.getInstance(getContext()).setBookMarkGroupId(group.groupId);
                    }
                }
                if (group.bookTotal <= 0){
                    continue;
                }
                GroupMessage groupMessage = new GroupMessage();
                groupMessage.groupId = group.groupId;
                groupMessage.groupName = group.groupName;
                groupMessage.defaultType = group.isDefault;
                groupMessage.createTime = group.createTime;
                if (!TextUtils.isEmpty(group.groupName) && group.groupName.contains("未命名分组")){
                    try {
                        String ic = group.groupName.substring(group.groupName.indexOf("未命名分组")+"未命名分组".length());
                        int index = Integer.parseInt(ic);
                        groupMessage.unNameNumber = index;
                    }catch (Exception e){}
                }
                boolean res = GroupInfoDB.getInstance().addMessageInfo(groupMessage);
                if (res){
                    isChanged = true;
                }
                BookGroupMarkResponse bookGroupMarkResponse = ApiProcess4Leyue.getInstance(getContext()).getBookMarkByGroup(group.groupId, LeyueConst.SOURCE_LEYUE);
                if (bookGroupMarkResponse != null){
                    SystemBookMarkGroupResponseInfo itemGroup = bookGroupMarkResponse.bookMarkGroup;
                    List<SystemBookMarkResponseInfo> itemMarkList = bookGroupMarkResponse.systemMarks;
                    if (itemMarkList != null && itemMarkList.size() > 0){
                        for(SystemBookMarkResponseInfo systemBookMarkResponseInfo: itemMarkList){
                            BookMarkDB.getInstance().updateSysBookMarkGroupSync(systemBookMarkResponseInfo.getBookId(), group.getGroupId());
                        }
                    }
                }
            }
        }
        if (isChanged)
            getContext().sendBroadcast(new Intent(AppBroadcast.ACTION_UPDATE_BOOKSHELF));
		return true;
	}

    private boolean contains(ArrayList<SystemBookMarkResponseInfo> sysMarkLists , String id){
        if(sysMarkLists != null){
            if(sysMarkLists != null && sysMarkLists.size() > 0){
                for(SystemBookMarkResponseInfo sysBookMark : sysMarkLists){
                    if (sysBookMark.getBookId().equals(id)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 添加内置书籍
     * @param temp
     */
    private void addNativeBookLeyue(ArrayList<SystemBookMarkResponseInfo> temp){
        String[] assetsUrl = new String[]{"download_info_jjl.txt", "download_info_bbqner.txt"};
        for (String url: assetsUrl){
            DownloadInfo info = getDownloadInfo(url);
            if (!contains(temp, info.contentID)){
                BookMark bookMark = new BookMark();
                bookMark.setContentID(info.contentID);
                bookMark.setStatus(BookDigestsDB.STATUS_LOCAL);
                bookMark.setSoftDelete(DBConfig.BOOKMARK_STATUS_SOFT_DELETE_NO);
                bookMark.setBookmarkType(DBConfig.BOOKMARK_TYPE_SYSTEM);
                BookMarkDB.getInstance().createSysBookMarkIfNotExit(bookMark);

                DownloadPresenterLeyue.addBookDownloadedInfoIfNotExit(info);
            }
        }
    }


    /**
     * 获取本地文件中的DownloadInfo json数据
     * @param jsonFileName
     * @return
     */
    public static DownloadInfo getDownloadInfo(String jsonFileName){
        InputStream fis = null;
        DownloadInfo downloadInfo = new DownloadInfo();
        byte[] buf;
        try {
            fis = MyAndroidApplication.getInstance().getAssets().open(jsonFileName);
            buf = new byte[fis.available()];
            fis.read(buf);
			downloadInfo.fromJsonObject(new JSONObject(new String(buf,LeyueConst.UTF8))); 
        } catch (Exception e) {
        	downloadInfo = null;
            try {
                if (fis!=null) {
                    fis.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally{
            try {
                if (fis!=null) {
                    fis.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        if (downloadInfo!=null) {
            downloadInfo.timestamp = System.currentTimeMillis();
            downloadInfo.filePathLocation = Constants.BOOKS_DOWNLOAD + downloadInfo.contentID+".epub";
            downloadInfo.state = DownloadAPI.STATE_ONLINE;
            downloadInfo.secret_key = AES.encrypt(downloadInfo.secret_key);
        }
        return downloadInfo;
    }

}
