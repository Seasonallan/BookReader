package com.lectek.android.lereader.binding.model.user;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
/**
 * 上传头像Model
 * 
 * @author yangwq
 * @date 2014年7月15日
 * @email 57890940@qq.com
 */
public class UploadHeadModel extends BaseLoadNetDataModel<UserInfoLeyue> {

	@Override
	protected UserInfoLeyue onLoad(Object... params) throws Exception {
		UserInfoLeyue info = null;
		
		if(params != null){
			String uid = (String) params[0];
			String filePath = (String) params[1];
			info = ApiProcess4Leyue.getInstance(getContext()).uploadUserHead(uid, filePath);
		}
		return info; 
	}

}
