package com.lectek.android.lereader.binding.model.bookdigest;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataBackgroundModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;

/**
 * 更新云端笔记信息
 * @author ljp
 * @date 2014年4月29日
 * @email 451360508@qq.com.com
 */
public class UpdateBookDigestModel extends BaseLoadNetDataBackgroundModel<BookDigests> {
	 
	@Override
	protected BookDigests onLoad(Object... params) throws Exception {
		String userId = PreferencesUtil.getInstance(getContext()).getUserId();
		BookDigests digest = (BookDigests) params[0];
		boolean res = ApiProcess4Leyue.getInstance(getContext()).updateBookDigest(userId, digest, digest.getServerId());
		digest.setAction(BookDigestsDB.ACTION_UPDATE);
		digest.setStatus(res? BookDigestsDB.STATUS_SYNC: BookDigestsDB.STATUS_LOCAL);
		BookDigestsDB.getInstance().updateBookDigest(digest, false);
		return digest;
	}

}
