package com.lectek.android.lereader.binding.model.bookdigest;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataBackgroundModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.AddBookDigestInfo;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;

/**
 * 添加一条笔记信息到服务端
 * @author ljp
 * @date 2014年4月29日
 * @email 451360508@qq.com.com
 */
public class AddBookDigestModel extends BaseLoadNetDataBackgroundModel<BookDigests> {
	 
	@Override
	protected BookDigests onLoad(Object... params) throws Exception {
		String userId = PreferencesUtil.getInstance(getContext()).getUserId();
		BookDigests digest = (BookDigests) params[0];
        AddBookDigestInfo res = ApiProcess4Leyue.getInstance(getContext()).addBookDigest(digest);
		digest.setAction(BookDigestsDB.ACTION_ADD);
		if(res.isSuccess()){
			digest.setServerId(res.getNoteId()+"");
		}
		digest.setStatus(res.isSuccess()? BookDigestsDB.STATUS_SYNC: BookDigestsDB.STATUS_LOCAL);
		BookDigestsDB.getInstance().updateBookDigest(digest, false);
		return digest;
	}

}
