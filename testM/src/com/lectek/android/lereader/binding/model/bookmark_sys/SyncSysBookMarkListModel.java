package com.lectek.android.lereader.binding.model.bookmark_sys;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.download.DownloadHttpEngine;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.DownloadInfo;
import com.lectek.android.lereader.net.response.SystemBookMarkAddResponseInfo;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.storage.dbase.GroupMessage;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.dbase.mark.BookMarkDB;
import com.lectek.android.lereader.storage.dbase.util.GroupInfoDB;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;

/**
 * 同步本地系统书签到云端
 * @author ljp
 * @date 2014年4月29日
 * @email 451360508@qq.com.com
 */
public class SyncSysBookMarkListModel extends BaseLoadNetDataModel<Boolean> {

	@Override
	public Boolean onLoad(Object... params) {
        boolean res = true;
        if (params != null && params.length == 1 && params[0] instanceof Boolean){
            res = (Boolean)params[0];
        }
        List<GroupMessage> groupMessageList = GroupInfoDB.getInstance().getMessageInfos();
        if (groupMessageList != null){
            for(GroupMessage groupMessage: groupMessageList){
                if (groupMessage.groupId <= 0){// 同步分组
                    try {
                        SystemBookMarkAddResponseInfo responseInfo = ApiProcess4Leyue.getInstance(getContext()).addSysBookMarkGroup(groupMessage.groupName);
                        if (responseInfo != null){
                            int groupId = responseInfo.groupId;
                            if (groupId > 0){
                                GroupInfoDB.getInstance().updateSysBookMarkGroupId(groupId, groupMessage.groupId);
                                BookMarkDB.getInstance().updateSysBookMarkGroup(groupMessage.groupId, groupId);
                            }
                        }
                    } catch (GsonResultException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        List<BookMark> bookDigests = BookMarkDB.getInstance().getSyncSystemBookMarks();
        if (bookDigests == null || bookDigests.size() == 0){
            return res;
        }
		List<BookMark> reqAddLists = new ArrayList<BookMark>();
        List<BookMark> reqDelLists = new ArrayList<BookMark>();
		String userId = PreferencesUtil.getInstance(getContext()).getUserId();
        //过滤软删除却没有bookMarkId的书签【代表该书签未同步上服务端】
		for (int i = 0; i < bookDigests.size(); i++) {
            BookMark mark = bookDigests.get(i);
            if(mark.getSoftDelete() == DBConfig.BOOKMARK_STATUS_SOFT_DELETE){
                if(TextUtils.isEmpty(mark.getBookmarkID())){
                    BookMarkDB.getInstance().deleteSystemBookmark(mark);
                    continue;
                }else{
                    if (!TextUtils.isEmpty(mark.getContentID())){
                        reqDelLists.add(mark);
                    }
                }
            }else{
                if (!TextUtils.isEmpty(mark.getContentID())){
                    reqAddLists.add(mark);
                }
            }
		}
        //删除系统书签
        if (reqDelLists.size() > 0){

            for (BookMark bookMark: reqDelLists){
                try{
                    String[] ids = new String[]{bookMark.getBookmarkID()};
                    boolean temp = ApiProcess4Leyue.getInstance(getContext()).delSysBookMark(ids);
                    if(temp){
                        BookMarkDB.getInstance().deleteSystemBookmark(bookMark);
                    }
                }catch (Exception e){
                    LogUtil.i("SyncSysBookMarkListModel exception occur>> " + e.toString());
                }
            }
            /*String[] ids = new String[reqDelLists.size()];
            for (int i = 0; i< reqDelLists.size(); i++){
                BookMark bookMark = reqDelLists.get(i);
                ids [i]  = bookMark.getBookmarkID();
            }
            try{
                boolean temp = httpRequest.delSysBookMark(ids);
                if(temp){
                //    BookMarkDB.getInstance().deleteSystemBookmark(bookMark);
                }
            }catch (Exception e){
                LogUtil.i("SyncSysBookMarkListModel exception occur>> " + e.toString());
            }*/
        }
        //添加系统书签
        if (reqAddLists.size() > 0){
           for (BookMark bookMark: reqAddLists){
               try {
                   DownloadInfo downloadInfo = DownloadPresenterLeyue.getDownloadUnitById(bookMark.getContentID());
                    String source = SysBookMarkModel.SOURCE_LEYUE;
                    if (downloadInfo != null && downloadInfo.downloadType != null && downloadInfo.downloadType.equals(DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_CEB)){
                        source = SysBookMarkModel.SOURCE_SURFING;
                    }
                   SystemBookMarkAddResponseInfo temp = ApiProcess4Leyue.getInstance(getContext()).addSysBookMark(bookMark.groupId, bookMark,  source);
                   if(temp != null){
                       if(temp.getCode() == 0){
                           bookMark.setBookmarkID(temp.getBookMarkId());
                           bookMark.setStatus(BookDigestsDB.STATUS_SYNC);
                           bookMark.setSoftDelete(DBConfig.BOOKMARK_STATUS_SOFT_DELETE_NO);
                           BookMarkDB.getInstance().updateOrCreateSysBookMark(bookMark, false);
                       }else if(temp.getCode() == 20401){// {"code":"20401","desc":"该系统书签分组不存在","groupId":34}

                       }
                   }
               }catch (Exception e){
                   LogUtil.i("SyncSysBookMarkListModel exception occur>> "+e.toString());
               }
           }
        }
		return res;
	}

}
