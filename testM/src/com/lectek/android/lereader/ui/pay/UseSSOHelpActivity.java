package com.lectek.android.lereader.ui.pay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.ui.common.BaseActivity;

public class UseSSOHelpActivity extends BaseActivity{

	public static void openUseSSOHelpActivity(Context context){
		Intent intent = new Intent(context, UseSSOHelpActivity.class);
		context.startActivity(intent);
	}
	
	@Override
	protected View newContentView(Bundle savedInstanceState) {
		return getLayoutInflater().inflate(R.layout.use_sso_help_lay, null);
	}

	@Override
	protected String getContentTitle() {
		return getString(R.string.tip_help);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
