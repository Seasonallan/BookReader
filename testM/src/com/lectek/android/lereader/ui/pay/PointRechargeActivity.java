package com.lectek.android.lereader.ui.pay;

import android.os.Bundle;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.user.PointRechargeViewModel;
import com.lectek.android.lereader.ui.common.BaseActivity;

/** 积分兑换阅点
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-9-9
 */
public class PointRechargeActivity extends BaseActivity {
	
	private PointRechargeViewModel mPointRechargeViewModel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleContent(getString(R.string.user_info_center));
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mPointRechargeViewModel = new PointRechargeViewModel(this, this);
		mPointRechargeViewModel.onStart();
		return bindView(R.layout.point_recharge_layout, mPointRechargeViewModel);
	}

}
