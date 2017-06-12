package com.lectek.android.lereader.binding.model.bookmark;

import java.util.List;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataBackgroundModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.dbase.mark.BookMarkDB;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
/**
 * 获取云端用户书签
 * @author ljp
 * @date 2014年4月29日
 * @email 451360508@qq.com.com
 */
public class GetUserBookMarkModel extends BaseLoadNetDataBackgroundModel<Boolean> {

	@Override
	protected Boolean onLoad(Object... params) throws Exception {
		
		List<BookMark> list = null;
		String userId = PreferencesUtil.getInstance(getContext()).getUserId();
        if(params.length == 1 && params[0] != null){
            String bookId = params[0].toString();
            list = ApiProcess4Leyue.getInstance(getContext()).getBookMarkListByBookId(userId, bookId);
        }else{
            list = ApiProcess4Leyue.getInstance(getContext()).getUserBookMark( 0, Integer.MAX_VALUE);
        }
        if(list != null){
            for(BookMark bookMark: list){
                bookMark.setBookmarkType(DBConfig.BOOKMARK_TYPE_USER);
                bookMark.setStatus(BookDigestsDB.STATUS_SYNC);
                bookMark.setSoftDelete(DBConfig.BOOKMARK_STATUS_SOFT_DELETE_NO);
                BookMarkDB.getInstance().createUserBookMarkIfNotExit(bookMark);
            }
        }
		return true;
	}

}
