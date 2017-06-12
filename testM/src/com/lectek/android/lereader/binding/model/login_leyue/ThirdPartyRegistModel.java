/*
 * ========================================================
 * ClassName:UserLoginModel.java* 
 * Description:
 * Copyright (C) 
 * ========================================================
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *===================================================================*
 * Revision History
 *
 * Modification                    Tracking
 * Date         Author          Number       Description of changes
 *____________________________________________________________________
 * 
 * 2013-9-26     chendt          #00000       create
 */
package com.lectek.android.lereader.binding.model.login_leyue;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.RegisterInfo;
import com.lectek.android.lereader.permanent.ApiConfig;
import com.lectek.android.lereader.permanent.ThirdPartLoginConfig;

/**
 * @description
	第三方注册
 * @author chendt
 * @date 2013-9-27
 * @Version 1.0
 * @SEE ThirdPartyLoginViewModelLeyue
 */
public class ThirdPartyRegistModel extends BaseLoadNetDataModel<RegisterInfo> {

	@Override
	protected RegisterInfo onLoad(Object... params) throws Exception {
		RegisterInfo info = null;
		if (params!=null && params.length>0) {
			int type = (Integer) params[0];
			String account = (String) params[1];
			String nickname = (String) params[2];
			
			switch (type) {
			case ThirdPartLoginConfig.TYPE_QQ:
				info = ApiProcess4Leyue.getInstance(getContext()).regist(account,account, nickname, ApiConfig.QQ_REGISTER);
				break;

			case ThirdPartLoginConfig.TYPE_SINA:
				info = ApiProcess4Leyue.getInstance(getContext()).regist(account, account, nickname, ApiConfig.SINA_REGISTER);
				break;
			}
		}
		return info;
	}

}
