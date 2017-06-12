package com.lectek.android.lereader.binding.model.user;

import java.util.ArrayList;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.storage.dbase.PushMessage;
import com.lectek.android.lereader.storage.dbase.util.NotifyCustomInfoDB;
/**
 * 我的消息数据获取
 * @author ljp
 * @since 2014年5月4日
 */
public class MyMessageModel extends BaseLoadNetDataModel<ArrayList<PushMessage>> {

	@Override
	protected ArrayList<PushMessage> onLoad(Object... params) throws Exception {
		return NotifyCustomInfoDB.getInstance().getMessageInfos(); 
	}

}
