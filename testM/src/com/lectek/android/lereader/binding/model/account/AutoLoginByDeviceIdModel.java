package com.lectek.android.lereader.binding.model.account;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.UserThridInfo;
import com.lectek.android.lereader.permanent.ApiConfig;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
/**
 * 通过设备id自动请求网络登录或注册操作
 * @author wuwq
 *
 */
public class AutoLoginByDeviceIdModel extends BaseLoadNetDataModel<UserInfoLeyue>{

	@Override
	protected UserInfoLeyue onLoad(Object... params) throws Exception {
		if (params!=null && params.length>0) {
			UserInfoLeyue infoLeyue = null;
			String deviceId = (String) params[0];
			//根据用户设备号注册用户
			UserThridInfo thridInfo = ApiProcess4Leyue.getInstance(getContext()).registByDeviceId(deviceId,ApiConfig.DEVICEID_REGISTER);
			if(thridInfo != null){
				//获取用户信息返回
				infoLeyue = ApiProcess4Leyue.getInstance(getContext()).getUserInfo(thridInfo.getUserId());
				infoLeyue.setPassword(deviceId);
			}
			return infoLeyue;
		}
		return null;
	}

}
