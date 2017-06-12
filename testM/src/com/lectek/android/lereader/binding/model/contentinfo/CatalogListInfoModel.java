/*
 * ========================================================
 * ClassName:CatalogListInfoModel.java* 
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
 * 2013-9-17     chendt          #00000       create
 */
package com.lectek.android.lereader.binding.model.contentinfo;

import java.util.ArrayList;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.openapi.ApiProcess4TianYi;
import com.lectek.android.lereader.net.response.tianyi.Chapter;

public class CatalogListInfoModel extends BaseLoadNetDataModel<ArrayList<Chapter>> {

	@Override
	protected ArrayList<Chapter> onLoad(Object... params) throws Exception {
		ArrayList<Chapter> catalogList = null;
		if (params!=null && params.length>0) {
			catalogList = ApiProcess4TianYi.getInstance(getContext()).getBookChapterList((String)params[0]);
		}
		return catalogList;
	}

}
