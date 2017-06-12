package com.lectek.android.lereader.ui.specific;

import android.os.Bundle;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.user.MyOrderViewModelLeyue;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.umeng.analytics.MobclickAgent;

/** 我的订购界面
 * @author mingkg21
 * @email  mingkg21@gmail.com
 * @date   2013-8-28
 */
public class MyOrderActivity extends BaseActivity {// implements IBindViewCallBack, Observer {
	
	private final String PAGE_NAME = "我的订购界面";
	private MyOrderViewModelLeyue mMyOrderViewModel;
//	private View mFootLoadingView;
//	private ViewGroup mFootLoadingLay;
	
	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mMyOrderViewModel = new MyOrderViewModelLeyue(this, this);
//		mMyOrderViewModel.bFootLoadingVisibility.subscribe(this);
//		mFootLoadingView = getLayoutInflater().inflate(R.layout.loading_data_lay, null);
//		mFootLoadingLay = new FrameLayout(this);
		return bindTempView(R.layout.my_order_layout, mMyOrderViewModel);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleContent(getString(R.string.user_order));
		mMyOrderViewModel.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(PAGE_NAME);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(PAGE_NAME);
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMyOrderViewModel.onRelease();
	}

//	@Override
//	public void onPreBindView(View rootView, int layoutId) {
//		if (layoutId == R.layout.my_order_layout) {
//			ListView listView = (ListView) rootView.findViewById(R.id.order_list);
//			listView.addFooterView(mFootLoadingLay);
//		}
//	}
//
//	@Override
//	public void onPropertyChanged(IObservable<?> prop, Collection<Object> initiators) {
//		if ((Boolean) prop.get()) {
//			if (mFootLoadingView.getParent() == null) {
//				mFootLoadingLay.addView(mFootLoadingView);
//			}
//		} else {
//			mFootLoadingLay.removeAllViews();
//		}
//	}
}
