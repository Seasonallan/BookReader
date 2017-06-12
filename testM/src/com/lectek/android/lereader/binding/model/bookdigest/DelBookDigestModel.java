package com.lectek.android.lereader.binding.model.bookdigest;

import android.text.TextUtils;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataBackgroundModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;


/**
 * 删除笔记同步
 * @author ljp
 * @date 2014年4月29日
 * @email 451360508@qq.com.com
 */
public class DelBookDigestModel extends BaseLoadNetDataBackgroundModel<BookDigests> {

	@Override
	protected BookDigests onLoad(Object... params) throws Exception {
		BookDigests digest = (BookDigests) params[0];
		String userBookTagId = digest.getServerId();
		if(TextUtils.isEmpty(userBookTagId)){
			BookDigestsDB.getInstance().deleteBookDigest(digest, false);
			return digest;
		}
		boolean res = ApiProcess4Leyue.getInstance(getContext()).deleteBookDigest(userBookTagId);
		digest.setStatus(res? BookDigestsDB.STATUS_SYNC: BookDigestsDB.STATUS_LOCAL);
		digest.setAction(BookDigestsDB.ACTION_DEL);
		if(res){
			BookDigestsDB.getInstance().deleteBookDigest(digest, false);
		}
		return digest;
	}

}
