package com.lectek.android.lereader.ui.specific;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.user.PointManageViewModel;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.ui.pay.AlipayRechargeActivity;

/** 阅点管理
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-9-9
 * @SEE PointManageViewModel
 */
public class PointManageActivity extends BaseActivity {
	
	public static final Integer REQUEST_FOR_PIONT_MANAGE = 0x1000001;
	public static final Integer RESULT_FOR_PIONT_MANAGE = 0x1000002;
	
	private PointManageViewModel mPointManageViewModel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleContent(getString(R.string.user_info_read_point_manager));
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mPointManageViewModel = new PointManageViewModel(this, this,this);
		mPointManageViewModel.onStart();
		return bindView(R.layout.point_manage_layout, mPointManageViewModel);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == AlipayRechargeActivity.REQUEST_CODE){
			if(resultCode == AlipayRechargeActivity.RECHARGE_SUCCESS){
				mPointManageViewModel.onStart();
			}
		}
	}

	@Override
	protected void onPause() {
		setResult(RESULT_FOR_PIONT_MANAGE);
		super.onPause();
	}
	
	@Override
	public void finish() {
		setResult(RESULT_FOR_PIONT_MANAGE);
		super.finish();
	}
	
}
