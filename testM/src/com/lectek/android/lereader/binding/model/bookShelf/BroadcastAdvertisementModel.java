package com.lectek.android.lereader.binding.model.bookShelf;

import java.util.ArrayList;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataBackgroundModel;
import com.lectek.android.lereader.lib.storage.dbase.JsonArrayList;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.BookSubjectClassification;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;

/**
 * 书架广告栏数据获取[后台线程]
 */
public class BroadcastAdvertisementModel extends BaseLoadNetDataBackgroundModel<ArrayList<BookSubjectClassification>> {

	@Override
	protected ArrayList<BookSubjectClassification> onLoad(Object... params)
			throws Exception {
		JsonArrayList<BookSubjectClassification> list = ApiProcess4Leyue.getInstance(getContext()).getBookSubjectInfo(10, 0, 3);
		String gsonStr = list.toJsonArray().toString();
		PreferencesUtil.getInstance(getContext()).setBroadCastAdvertisementInfo(gsonStr);
		return list;
	}

}
