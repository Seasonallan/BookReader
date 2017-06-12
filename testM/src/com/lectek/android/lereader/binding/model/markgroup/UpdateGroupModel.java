package com.lectek.android.lereader.binding.model.markgroup;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.binding.model.bookmark_sys.SyncSysBookMarkListModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.SystemBookMarkAddResponseInfo;
import com.lectek.android.lereader.storage.dbase.GroupMessage;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.dbase.mark.BookMarkDB;
import com.lectek.android.lereader.storage.dbase.util.GroupInfoDB;

import java.util.ArrayList;

/**
 * 添加分组
 * @author ljp
 * @date 2014年4月29日
 * @email 451360508@qq.com.com
 */
public class UpdateGroupModel extends BaseLoadNetDataModel<Integer> {

	@Override
	protected Integer onLoad(Object... params) throws Exception {
        GroupMessage groupMessage = null;
        BookMark bookMark1 = null,bookMark2 =null;
        if (params != null && params.length == 3){
            groupMessage = (GroupMessage) params[0];
            bookMark1 = (BookMark) params[1];
            bookMark2  = (BookMark) params[2];
        }
        BookMarkDB.getInstance().updateSysBookMarkGroup(bookMark2.contentID, groupMessage.groupId);
        if (groupMessage.groupId < 0){
            SystemBookMarkAddResponseInfo responseInfo = ApiProcess4Leyue.getInstance(getContext()).addSysBookMarkGroup(groupMessage.groupName);
            if (responseInfo != null){
                int id = responseInfo.groupId;
                if (id > 0){
                    GroupInfoDB.getInstance().updateSysBookMarkGroupId(id, groupMessage.groupId);
                    if (bookMark1 != null){
                        BookMarkDB.getInstance().updateSysBookMarkGroup(bookMark1.contentID, id);
                    }
                    BookMarkDB.getInstance().updateSysBookMarkGroup(bookMark2.contentID, id);
                    new SyncSysBookMarkListModel().onLoad();
                }
                return id;
            }
        }else{
            new SyncSysBookMarkListModel().onLoad();
        }
        return -1;
    }

}
