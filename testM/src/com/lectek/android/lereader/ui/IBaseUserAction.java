/*
 * ========================================================
 * ClassName:IBaseUserAction.java* 
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
 * 2013-9-29     chendt          #00000       create
 */
package com.lectek.android.lereader.ui;


public interface IBaseUserAction extends IView {

	/**
	 * 异常处理
	 * @param str
	 */
	public void exceptionHandle(String str);
	
	/**
	 * 操作成功提示
	 * @param str
	 */
	public void optToast(String str);
	
}
