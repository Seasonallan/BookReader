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
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;

public class UserLoginModel extends BaseLoadNetDataModel<UserInfoLeyue> {

	@Override
	protected UserInfoLeyue onLoad(Object... params) throws Exception {
		UserInfoLeyue infoLeyue = null;
		if (params!=null && params.length>0) {
			String name = (String) params[0];
			String psw = (String) params[1];
			if(params.length == 3){
				String userId = (String) params[2];
				infoLeyue = ApiProcess4Leyue.getInstance(getContext()).getUserInfo(userId);
			}else{
				infoLeyue = ApiProcess4Leyue.getInstance(getContext()).login(name, psw);
			}
			if(infoLeyue != null){
				infoLeyue.setPassword(psw);
			}
		}
		return infoLeyue;
	}

}
