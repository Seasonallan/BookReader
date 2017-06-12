package com.lectek.android.lereader.binding.model.markgroup;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.SystemBookMarkAddResponseInfo;
import com.lectek.android.lereader.storage.dbase.GroupMessage;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.dbase.mark.BookMarkDB;
import com.lectek.android.lereader.storage.dbase.util.GroupInfoDB;

/**
 * 添加分组
 * @author ljp
 * @date 2014年4月29日
 * @email 451360508@qq.com.com
 */
public class UpdateGroupNameModel extends BaseLoadNetDataModel<Integer> {

    AddGroupModel.CallBack mCallBack;
    public UpdateGroupNameModel(AddGroupModel.CallBack callback){
        this.mCallBack = callback;
    }

	@Override
	protected Integer onLoad(Object... params) throws Exception {
        GroupMessage groupMessage = null;
        String name = null;
        if (params != null && params.length == 2){
            groupMessage = (GroupMessage) params[0];
            name = (String) params[1];
        }
        GroupInfoDB.getInstance().updateSysBookMarkGroupName(groupMessage.groupId, name);
        if (groupMessage.groupId > 0){
            SystemBookMarkAddResponseInfo responseInfo = ApiProcess4Leyue.getInstance(getContext()).updateSysBookMarkGroup(groupMessage.groupId, name);
        }else{
            SystemBookMarkAddResponseInfo responseInfo = ApiProcess4Leyue.getInstance(getContext()).addSysBookMarkGroup(name);
            if (responseInfo != null){
                int groupId = responseInfo.groupId;
                if (groupId > 0){
                    GroupInfoDB.getInstance().updateSysBookMarkGroupId(groupId, groupMessage.groupId);
                    BookMarkDB.getInstance().updateSysBookMarkGroup(groupMessage.groupId, groupId);
                    groupMessage.groupId = groupId;
                    mCallBack.onCallBack(groupMessage);
                }
            }
        }
        return -1;
    }

}










