package com.lectek.android.lereader.binding.model.account;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.AccountBindingInfo;
import com.lectek.android.lereader.net.response.CommonResultInfo;

/**
 * 解除绑定账号Model
 * 
 * @author yangwq
 * @date 2014年7月11日
 * @email 57890940@qq.com
 */
public class UnbindAccountModel extends BaseLoadNetDataModel<CommonResultInfo>{

	@Override
	protected CommonResultInfo onLoad(Object... params) throws Exception {
		CommonResultInfo info = null;
		if(params != null){
			String id = (String) params[0];
			info = ApiProcess4Leyue.getInstance(getContext()).unBindAccount(id);
		}
		return info;
	}

}
