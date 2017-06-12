package com.lectek.android.lereader.binding.model.account;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.AccountBindingInfo;

/**
 * 保存绑定账号Model
 * 
 * @author yangwq
 * @date 2014年7月11日
 * @email 57890940@qq.com
 */
public class SaveAccountBindingModel extends BaseLoadNetDataModel<AccountBindingInfo>{

	@Override
	protected AccountBindingInfo onLoad(Object... params) throws Exception {
		AccountBindingInfo info = null;
		if(params != null){
			String uid = (String) params[0];
			String source = (String) params[1];
			String account = (String) params[2];
			String password = (String) params[3];
			info = ApiProcess4Leyue.getInstance(getContext()).saveBindingAccount(uid, source, account, password);
			if(info != null){
				info.setUid(uid);
				info.setSource(source);
				info.setAccount(account);
				info.setPassword(password);
			}
			
		}
		return info;
	}

}
