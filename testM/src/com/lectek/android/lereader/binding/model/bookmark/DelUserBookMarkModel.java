package com.lectek.android.lereader.binding.model.bookmark;

import android.text.TextUtils;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataBackgroundModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.dbase.mark.BookMarkDB;

/**
 * 从云端删除用户书签
 * @author ljp
 * @date 2014年4月29日
 * @email 451360508@qq.com.com
 */
public class DelUserBookMarkModel extends BaseLoadNetDataBackgroundModel<BookMark> {

	@Override
	protected BookMark onLoad(Object... params) throws Exception {
        BookMark bookDigest = (BookMark) params[0];
        String markId = bookDigest.getBookmarkID();
        if(TextUtils.isEmpty(markId)){
            bookDigest = BookMarkDB.getInstance().getUserBookMark(bookDigest);
        }
        markId = bookDigest.getBookmarkID();
        if(TextUtils.isEmpty(markId)){
            BookMarkDB.getInstance().deleteUserBookmark(bookDigest);
        }else{
            boolean isDel = ApiProcess4Leyue.getInstance(getContext()).deleteBookLabel(markId);
            if(isDel){
                BookMarkDB.getInstance().deleteUserBookmark(bookDigest);
                return null;
            }else{
            }
        }
        return null;
	}

}
