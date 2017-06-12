/*
 * ========================================================
 * ClassName:ScoreRuleActivity.java* 
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
 * 2013-12-27     chendt          #00000       create
 */
package com.lectek.android.lereader.ui.specific;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

/**
 * @description
	积分规则显示界面
 * @author chendt
 * @date 2013-12-27
 * @Version 1.0
 */
public class ScoreRuleActivity extends FlingExitBaseAcitity{
	private WebView webView;
	public static void gotoScoreRuleActivity(Context context){
		context.startActivity(new Intent(context, ScoreRuleActivity.class));
	}

	@Override
	public View getFlingView() {
		return webView;
	}

	@Override
	public void flingExitHandle() {
		finish();
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		webView = new WebView(ScoreRuleActivity.this);
		webView.loadUrl("http://www.leread.com/help.html");
		return webView;
	}
}
