/*
 * ========================================================
 * ClassName:ContentInfoModelLeyue.java* 
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
 * 2013-9-28     chendt          #00000       create
 */
package com.lectek.android.lereader.binding.model.contentinfo;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.BookDecodeInfo;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;

/**
 * @description

 * @author chendt
 * @date 2013-9-28
 * @Version 1.0
 * @SEE ContentInfoViewModelLeyue
 */
public class SecretKeyInfoModelLeyue extends BaseLoadNetDataModel<BookDecodeInfo> {
	
	@Override
	public BookDecodeInfo onLoad(Object... params) throws Exception {
		BookDecodeInfo info = null;
		if (params!=null && params.length >0) {
			String bookId = (String) params[0];
			info = ApiProcess4Leyue.getInstance(getContext()).getBookDecodeKey(
					bookId,PreferencesUtil.getInstance(getContext()).getUserId());
		}
		return info;
	}

}
