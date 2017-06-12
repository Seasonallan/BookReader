package com.lectek.android.lereader.ui.wifiTransfer;

import android.os.Bundle;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.imports.WifiTransferViewModel;
import com.lectek.android.lereader.ui.common.BaseActivity;
/**
 * wifi传书
 * @author zhouxinghua
 *
 */
public class WifiTransferActivity extends BaseActivity {
	
	private WifiTransferViewModel mWifiTransferViewModel;

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mWifiTransferViewModel = new WifiTransferViewModel(this_, this);
		return bindView(R.layout.wifi_transfer, mWifiTransferViewModel);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleContent(getString(R.string.bookshelf_wifi_transfer));
		setLeftButtonEnabled(true);
		setRightButtonEnabled(false);
		mWifiTransferViewModel.onStart(savedInstanceState);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWifiTransferViewModel.onRelease();
	}

}
