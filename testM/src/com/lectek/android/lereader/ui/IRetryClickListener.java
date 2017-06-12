/*
 * ========================================================
 * ClassName:IRetryClickListener.java* 
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
 * 2013-10-8     chendt          #00000       create
 */
package com.lectek.android.lereader.ui;

/**
 * @description
	新增界面需要对重试时 对请求作变更。提供处理接口;</br>
	<b>注：</b>此接口 仅webView进行网络请求的界面所需
 * @author chendt
 * @date 2013-10-8
 * @Version 1.0
 */
public interface IRetryClickListener {
	
	/**
	 * 对重试时 回调监听;</br>
		<b>注：</b>此回调 仅webView进行网络请求的界面所需
	 */
	public void onRetryClick();
}
