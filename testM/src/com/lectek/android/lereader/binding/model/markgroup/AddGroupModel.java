package com.lectek.android.lereader.binding.model.markgroup;

import android.content.Intent;
import android.text.TextUtils;

import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.binding.model.bookmark_sys.SyncSysBookMarkListModel;
import com.lectek.android.lereader.binding.model.bookmark_sys.SysBookMarkModel;
import com.lectek.android.lereader.download.DownloadHttpEngine;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.BookGroupMarkResponse;
import com.lectek.android.lereader.net.response.DownloadInfo;
import com.lectek.android.lereader.net.response.SystemBookMarkAddResponseInfo;
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
import com.tencent.android.tpush.data.MessageId;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

/**
 * 添加分组
 * @author ljp
 * @date 2014年4月29日
 * @email 451360508@qq.com.com
 */
public class AddGroupModel extends BaseLoadNetDataModel<Integer> {

    public static interface CallBack{
        public void onCallBack(GroupMessage groupMessage);
    }

    CallBack mCallBack;
    public AddGroupModel(CallBack callback){
        this.mCallBack = callback;
    }

	@Override
	protected Integer onLoad(Object... params) throws Exception {
        String groupName = "未命名分组";
        int unNameNumber = 1;
        double shelfPosition = 1;BookMark bookMark1 = null,bookMark2 =null;
        if (params != null && params.length == 5){
            bookMark1 = (BookMark) params[0];
            bookMark2  = (BookMark) params[1];
            shelfPosition = (Double)params[2];
            groupName = (String)params[3];
            unNameNumber = (Integer)params[4];
        }
        GroupMessage groupMessage = new GroupMessage();
        groupMessage.groupId = (int) - System.currentTimeMillis();
        groupMessage.defaultType = 1;
        groupMessage.groupName = groupName;
        groupMessage.unNameNumber = unNameNumber;
        groupMessage.shelfPosition = shelfPosition;
        mCallBack.onCallBack(groupMessage);
        GroupInfoDB.getInstance().addMessageInfo(groupMessage);
        BookMarkDB.getInstance().updateSysBookMarkGroup(bookMark1.contentID, groupMessage.groupId);
        BookMarkDB.getInstance().updateSysBookMarkGroup(bookMark2.contentID, groupMessage.groupId);

        SystemBookMarkAddResponseInfo responseInfo = ApiProcess4Leyue.getInstance(getContext()).addSysBookMarkGroup(groupName);
        if (responseInfo != null){
            int id = responseInfo.groupId;
            if (id > 0){
                GroupInfoDB.getInstance().updateSysBookMarkGroupId(id, groupMessage.groupId);
                BookMarkDB.getInstance().updateSysBookMarkGroup(bookMark1.contentID, id);
                BookMarkDB.getInstance().updateSysBookMarkGroup(bookMark2.contentID, id);
                groupMessage.groupId = id;
                mCallBack.onCallBack(groupMessage);
                new SyncSysBookMarkListModel().onLoad();
            }
            return id;
        }
        return -1;
    }

}
