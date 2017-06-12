/*
 * ========================================================
 * ClassName:KeyBoardUtil.java* 
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
 * 2013-9-10     chendt          #00000       create
 */
package com.lectek.android.lereader.utils;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyBoardUtil {
	/**
	 * 用于显示键盘
	 * 
	 * @param mContext
	 */
	public static InputMethodManager showInputMethodManager(Context mContext) {
		final InputMethodManager mInputMethodManager = (InputMethodManager) mContext
		.getSystemService(Context.INPUT_METHOD_SERVICE);
		mInputMethodManager.toggleSoftInput(InputType.TYPE_CLASS_PHONE,
				InputMethodManager.HIDE_NOT_ALWAYS);
		return mInputMethodManager;
	}

	/**
	 * 隐藏键盘
	 */
	public static void hideInputMethodManager(
			InputMethodManager mInputMethodManager, View mView) {
		
		if (mInputMethodManager != null && mView != null) {
			mInputMethodManager.hideSoftInputFromWindow(mView.getWindowToken(),0);
		}
	}
	
	/**
	 * 隐藏键盘
	 */
	public static void hideInputMethodManager(
			Context aContext, View mView) {
		final InputMethodManager mInputMethodManager = (InputMethodManager) aContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (mInputMethodManager != null && mView != null) {
			mInputMethodManager.hideSoftInputFromWindow(mView.getWindowToken(),0);
		}
	}
}
