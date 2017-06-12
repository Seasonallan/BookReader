/*
 * ========================================================
 * ClassName:FlingExitBaseAcitity.java* 
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
 * 2013-10-7     chendt          #00000       create
 */
package com.lectek.android.lereader.ui.specific;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.ui.specific.ChildViewExitGestureDetectorListener.ChildViewSwitcher;

/**
 * @description
	那些需要 实现 向右手势滑动，退出界面的Activity可继承此基类。
 * @author chendt
 * @date 2013-10-7
 * @Version 1.0
 */
public abstract class FlingExitBaseAcitity extends BaseActivity {

	private ChildViewExitGestureDetectorListener mDetectorListener;
	private GestureDetector mDetector;
	
	@Override
	protected void onStart() {
		super.onStart();
		if (mDetectorListener == null) {
			mDetectorListener = new ChildViewExitGestureDetectorListener(switcher);
			mDetector = new GestureDetector(this, mDetectorListener);
			if (getFlingView()!=null) {
				getFlingView().setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						return mDetector.onTouchEvent(event);
					}
				});
			}
		}
	}

	private ChildViewSwitcher switcher = new ChildViewSwitcher() {
		
		@Override
		public void flingHandle(boolean isLeft) {
			if (isLeft) {
				flingExitHandle();
			}
		}
	};
	
	/**监听手势操作的view*/
	public abstract View getFlingView();
	
	/**滑动事件，子类业务处理*/
	public abstract void flingExitHandle();
}
