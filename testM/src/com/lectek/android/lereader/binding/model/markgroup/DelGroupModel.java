package com.lectek.android.lereader.binding.model.markgroup;

import com.lectek.android.lereader.binding.model.BaseLoadDataModel;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.data.BookShelfItem;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.SystemBookMarkAddResponseInfo;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.storage.dbase.GroupMessage;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.dbase.mark.BookMarkDB;
import com.lectek.android.lereader.storage.dbase.util.GroupInfoDB;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.storage.sprefrence.UserPrefrenceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 删除书籍
 * @author ljp
 * @date 2014年4月29日
 * @email 451360508@qq.com.com
 */
public class DelGroupModel extends BaseLoadDataModel<Integer> {

	@Override
	protected Integer onLoad(Object... params) throws Exception {
        List<BookShelfItem> delItems = null; boolean isDelete = false;
        if (params != null && params.length == 2){
            delItems = (List<BookShelfItem>) params[0];
            isDelete  = (Boolean) params[1];
        }

        int defaultGroupId = PreferencesUtil.getInstance(getContext()).getBookMarkGroupId();
        for (BookShelfItem item: delItems){
            if (item.isFile){
                GroupMessage groupMessage = item.mGroupMessage;
                List<BookShelfItem> items = item.mItems;
                for (BookShelfItem it: items){
                    if (!isDelete){
                        BookMarkDB.getInstance().updateSysBookMarkGroup(it.mDownLoadInfo.contentID, defaultGroupId);
                    }else{
                        DownloadPresenterLeyue.deleteDB(it.mDownLoadInfo.contentID);
                        BookMarkDB.getInstance().softDeleteSystemBookmark(it.mDownLoadInfo.contentID);
                    }
                }
                GroupInfoDB.getInstance().delMessageInfo(groupMessage.groupId);
                try {
                    ApiProcess4Leyue.getInstance(getContext()).delSysBookMarkGroup(groupMessage.groupId, isDelete?1:2);
                }catch (Exception e){}
            }else{
            	if(item.mDownLoadInfo != null) {
            		DownloadPresenterLeyue.deleteDB(item.mDownLoadInfo.contentID);
            	}
                BookMarkDB.getInstance().softDeleteSystemBookmark(item.mBookMark.contentID);
            }
        }
        return -1;
    }

}
