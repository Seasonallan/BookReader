package com.lectek.android.lereader.binding.model.bookmark;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataBackgroundModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.BookMarkResponse;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.dbase.mark.BookMarkDB;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;

/**
 * 添加用户书签到云端
 * @author ljp
 * @date 2014年4月29日
 * @email 451360508@qq.com.com
 */
public class AddUserBookMarkModel extends BaseLoadNetDataBackgroundModel<BookMark> {

	@Override
	protected BookMark onLoad(Object... params) throws Exception {
        BookMark bookMark = (BookMark) params[0];
		String userId = PreferencesUtil.getInstance(getContext()).getUserId();
        BookMarkResponse temp = ApiProcess4Leyue.getInstance(getContext()).addBookLabel(bookMark);
		if(temp != null){
            String markId = temp.getUserTagId();
            bookMark.setBookmarkID(markId);
            bookMark.setUserID(userId);
            bookMark.setStatus(BookDigestsDB.STATUS_SYNC);
            BookMarkDB.getInstance().updateUserBookMarkServerId(bookMark);
		}
		return null;
	}

}
