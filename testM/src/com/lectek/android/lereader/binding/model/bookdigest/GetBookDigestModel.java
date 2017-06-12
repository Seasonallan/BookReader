package com.lectek.android.lereader.binding.model.bookdigest;


import java.util.List;

import android.text.TextUtils;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataBackgroundModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;

/**
 * 获取云端笔记到本地
 * @author ljp
 * @date 2014年4月29日
 * @email 451360508@qq.com.com
 */
public class GetBookDigestModel extends BaseLoadNetDataBackgroundModel<Boolean> {

	@Override
	public Boolean onLoad(Object... params) throws Exception {
		String userId = PreferencesUtil.getInstance(getContext()).getUserId();
		String bookId = null;
		if(params != null && params.length > 0 && params[0] != null){
			bookId = params[0].toString();
		}
        List<BookDigests> netDigests = ApiProcess4Leyue.getInstance(getContext()).getBookDigestListByBookId(bookId);
        for(BookDigests digest: netDigests){
            if(TextUtils.isEmpty(digest.getContentId())){

            }else{
                BookDigestsDB.getInstance().saveOrUpdateBookDigest(digest ,false);
            }
        }
        return true;
	}

}
