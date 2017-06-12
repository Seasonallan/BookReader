/*
 * ========================================================
 * ClassName:BookCityModel.java* 
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
 * 2013-8-29     chendt          #00000       create
 */
package com.lectek.android.lereader.binding.model.bookCity;

import com.lectek.android.lereader.binding.model.BaseLoadDataModel;
import com.lectek.android.lereader.ui.specific.BaseWebView.WebViewHandleEventListener;

public class BookCityModel extends BaseLoadDataModel implements WebViewHandleEventListener{
	private Boolean isSuccess;
	@Override
	protected Boolean onLoad(Object... params) throws Exception {
		return isSuccess;
	}

	@Override
	public void loadWebView() {}

	@Override
	public void responeViewForAcitityType(String requestStatus) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isPullEnabled() {
		return false;
	}

	@Override
	public void reloadWeb(String webTag) {
		// TODO Auto-generated method stub
		
	}

}
