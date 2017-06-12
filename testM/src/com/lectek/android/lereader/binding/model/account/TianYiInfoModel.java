package com.lectek.android.lereader.binding.model.account;

import java.util.ArrayList;

import android.text.TextUtils;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.openapi.ApiProcess4TianYi;
import com.lectek.android.lereader.net.response.UserThirdLeyueInfo;
import com.lectek.android.lereader.storage.dbase.TianYiUserInfo;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.utils.UserManager;

/**
 * 获取后台绑定天翼账号信息
 * @author wuwq
 *
 */
public class TianYiInfoModel  extends BaseLoadNetDataModel<TianYiUserInfo>{
	@Override
	protected TianYiUserInfo onLoad(Object... params) throws Exception {
		if (params!=null && params.length>0) {
			TianYiUserInfo tianYiInfo = null;
			String userId = (String) params[0];
			//根据用户id获取后台绑定第三方账号信息
            ArrayList<UserThirdLeyueInfo> infos = ApiProcess4Leyue.getInstance(getContext()).getThirdIdByLeYueId(userId);
			if(infos != null && infos.size()>0){
				for(UserThirdLeyueInfo info : infos){
					if(!TextUtils.isEmpty(info.getSource()) && info.getSource().equals(UserInfoLeyue.TYPE_TIANYI)){
						//获取用户信息返回
						tianYiInfo = new TianYiUserInfo();
						tianYiInfo.setAccessToken(info.getAccessToken());
						tianYiInfo.setUserId(info.getThirdId());
						tianYiInfo.setLeyueUserId(userId);
						tianYiInfo.setRefreshToken(info.getRefreshToken());
						if(!TextUtils.isEmpty(info.getAccessToken())){
							UserManager.getInstance(getContext()).setCurrentAccessToken(info.getAccessToken());
							//获取天翼账号信息更新乐阅后台账号信息
							TianYiUserInfo userInfo = ApiProcess4TianYi.getInstance(getContext()).queryUserInfo(info.getAccessToken());
							userInfo.setLeyueUserId(userId);
							userInfo.setAccessToken(info.getAccessToken());
							userInfo.setRefreshToken(info.getRefreshToken());
							userInfo.setUserId(info.getThirdId());
							if(userInfo!=null){
								return userInfo;
							}
						}
					}
				}
				return tianYiInfo;
			}
		}
		return null;
	}
}
