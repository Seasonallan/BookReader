package com.lectek.android.lereader.binding.model.bookmark;

import java.util.List;

import android.text.TextUtils;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataBackgroundModel;
import com.lectek.android.lereader.lib.storage.dbase.JsonArrayList;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.BookMarkResponse;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.dbase.mark.BookMarkDB;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
/**
 * 同步本地未同步用户书签到云端
 * @author ljp
 * @date 2014年4月29日
 * @email 451360508@qq.com.com
 */
public class SyncUserBookMarkModel extends BaseLoadNetDataBackgroundModel<String> {

	@Override
	protected String onLoad(Object... params) {
        List<BookMark> bookDigests;
        String bookId = null;
        if(params.length == 1){
            Object object = params[0];
            if(object instanceof  String && !TextUtils.isEmpty((String)object)){
                bookId = (String)object;
                bookDigests = BookMarkDB.getInstance().getSyncUserBookMarks(bookId);
            }else{
                bookDigests = BookMarkDB.getInstance().getSyncUserBookMarks();
            }
        }else{
            bookDigests = BookMarkDB.getInstance().getSyncUserBookMarks();
        }
        if (bookDigests == null){
            return bookId;
        }
		JsonArrayList<BookMarkResponse> reqLists = new JsonArrayList<BookMarkResponse>(BookMarkResponse.class);
		String userId = PreferencesUtil.getInstance(getContext()).getUserId();
		for (int i = 0; i < bookDigests.size(); i++) {
            BookMark mark = bookDigests.get(i);
            if(mark.getSoftDelete() == DBConfig.BOOKMARK_STATUS_SOFT_DELETE){
                if(TextUtils.isEmpty(mark.getBookmarkID())){
                    BookMarkDB.getInstance().deleteUserBookmark(mark);
                    continue;
                }
            }
			reqLists.add(new BookMarkResponse(mark, reqLists.size(), userId));
		}
        if (reqLists.size() == 0){
            return bookId;
        }
        try{
            List<BookMarkResponse> res = ApiProcess4Leyue.getInstance(getContext()).syncBookMarkList(reqLists);
            for (int i = 0; i < res.size(); i++) {
                BookMarkResponse response = res.get(i);
                int position = response.getClientTagId();
                BookMarkResponse reqBookMark = reqLists.get(position);
                if(response.getAction().equals("true")){
                    if(reqBookMark.getAction().equals("delete")){//同步删除
                        BookMarkDB.getInstance().deleteUserBookmark(reqBookMark.toBookMark());
                    }else{
                        BookMark bookMark = reqBookMark.toBookMark();
                        bookMark.setBookmarkID(response.getUserBookTagId());
                        bookMark.setUserID(userId);
                        BookMarkDB.getInstance().updateUserBookMarkServerId(bookMark);
                    }
                }
            }
        }catch (Exception e){
            LogUtil.i("SyncUserBookMarkModel exception occur>> "+e.toString());
        }
		return bookId;
	}

}
