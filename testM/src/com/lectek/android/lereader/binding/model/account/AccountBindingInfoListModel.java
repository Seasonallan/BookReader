package com.lectek.android.lereader.binding.model.account;

import java.util.List;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.AccountBindingInfo;

/**
 * 获取绑定账号列表Model
 * 
 * @author yangwq
 * @date 2014年7月11日
 * @email 57890940@qq.com
 */
public class AccountBindingInfoListModel extends BaseLoadNetDataModel<List<AccountBindingInfo>>{

	@Override
	protected List<AccountBindingInfo> onLoad(Object... params) throws Exception {
		List<AccountBindingInfo> list = null;
		if(params != null){
			String userId = (String) params[0];
			list = ApiProcess4Leyue.getInstance(getContext()).getBindingAccountList(userId);
		}
		return list;
	}

}
