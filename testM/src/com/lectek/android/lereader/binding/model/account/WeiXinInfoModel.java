package com.lectek.android.lereader.binding.model.account;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.UserThridInfo;
import com.lectek.android.lereader.permanent.ApiConfig;
/**
 * 微信账号注册返回乐阅账号信息
 * @author wuwq
 *
 */
public class WeiXinInfoModel extends BaseLoadNetDataModel<String>{

	@Override
	protected String onLoad(Object... params) throws Exception {
		if (params!=null && params.length>0) {
			String wxUserId = (String) params[0];
			String wxUserNikeName = (String) params[1];
			//根据用户设备号注册用户
			UserThridInfo thridInfo = ApiProcess4Leyue.getInstance(getContext()).registByDeviceId(wxUserId,ApiConfig.WEIXIN_REGISTER);
			try{
				if(!thridInfo.getIsUpdated()){
					//String userId, String nickname, String userName, String password, String newPassword, String mobile, String email, String sex, String birthday
					boolean isUploadUserInfoSuccess = ApiProcess4Leyue.getInstance(getContext()).updateUserInfo(thridInfo.getUserId()+"", wxUserNikeName, wxUserId, wxUserId, null, null, null, null, null, null, null);
					LogUtil.i("isUploadUserInfoSuccess", isUploadUserInfoSuccess+"");
				}
			}catch(Exception e){
				e.printStackTrace();
				LogUtil.e(e.getMessage());
			}
			return wxUserId;
		}
		return null;
	}

}
