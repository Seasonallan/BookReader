package com.lectek.android.lereader.ui.specific;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.app.IBindViewCallBack;
import com.lectek.android.lereader.binding.model.user.MyDigestViewModelLeyue;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.Collection;

import gueei.binding.IObservable;
import gueei.binding.Observer;

/**
 * @description
	 个人中心
 * @author chendt
 * @date 2013-12-29
 * @Version 1.0
 * @SEE UserInfoViewModelLeyue
 */
public class MyDigestActivity extends BaseActivity implements IBindViewCallBack, Observer {
	
	private final String PAGE_NAME = "我的笔记";
	private MyDigestViewModelLeyue mMyOrderViewModel;
	private View mFootLoadingView;
	private ViewGroup mFootLoadingLay;


	public static void open(Activity activity) {
		Intent intent = new Intent();
		intent.setClass(activity, MyDigestActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(intent);
	}
	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mMyOrderViewModel = new MyDigestViewModelLeyue(this, this);
		mMyOrderViewModel.bFootLoadingVisibility.subscribe(this);
		mFootLoadingView = getLayoutInflater().inflate(R.layout.loading_data_lay, null);
		mFootLoadingLay = new FrameLayout(this);
		return bindView(R.layout.my_digest_layout, this, mMyOrderViewModel);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleContent(getString(R.string.my_digest));
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(PAGE_NAME);
		MobclickAgent.onResume(this);
		mMyOrderViewModel.onStart();
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

	@Override
	public void onPreBindView(View rootView, int layoutId) {
		if (layoutId == R.layout.my_order_layout) {
			//ListView listView = (ListView) rootView.findViewById(R.id.order_list);
			//listView.addFooterView(mFootLoadingLay);
		}
	}

	@Override
	public void onPropertyChanged(IObservable<?> prop, Collection<Object> initiators) {
		if ((Boolean) prop.get()) {
			if (mFootLoadingView.getParent() == null) {
				mFootLoadingLay.addView(mFootLoadingView);
			}
		} else {
			mFootLoadingLay.removeAllViews();
		}
	}
}
