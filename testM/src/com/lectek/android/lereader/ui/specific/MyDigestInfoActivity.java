package com.lectek.android.lereader.ui.specific;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.app.IBindViewCallBack;
import com.lectek.android.lereader.binding.model.user.MyDigestInfoViewModelLeyue;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
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
public class MyDigestInfoActivity extends BaseActivity implements IBindViewCallBack, Observer {
	
	private final String PAGE_NAME = "我的笔记";
	private MyDigestInfoViewModelLeyue mMyOrderViewModel;
	private View mFootLoadingView;
	private ViewGroup mFootLoadingLay;

	public static final String EXTRA_BOOK_ID = "extra_book_id"; 
	public static final String EXTRA_DIGEST_NAME = "extra_digest_name"; 
	public static final String EXTRA_DIGEST_DATE = "extra_digest_date"; 
	public static final String EXTRA_DIGEST_COUNT = "extra_digest_count"; 
	public static final String EXTRA_DIGEST_COVER = "extra_digest_cover";
 
	
	public static void openActivity(Context context, BookDigests digest){
		Intent intent = new Intent(context, MyDigestInfoActivity.class);
		intent.putExtra(EXTRA_BOOK_ID, digest.getContentId()); 
		intent.putExtra(EXTRA_DIGEST_NAME, digest.getContentName()); 
		intent.putExtra(EXTRA_DIGEST_DATE, digest.getDate()); 
		intent.putExtra(EXTRA_DIGEST_COUNT, digest.getCount()); 
		intent.putExtra(EXTRA_DIGEST_COVER, digest.getAuthor());
		context.startActivity(intent);
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		Intent intent = getIntent();
		BookDigests digest = new BookDigests();
		digest.setContentId(intent.getStringExtra(EXTRA_BOOK_ID));
		digest.setContentName(intent.getStringExtra(EXTRA_DIGEST_NAME));
		digest.setDate(intent.getLongExtra(EXTRA_DIGEST_DATE, 0));
		digest.setCount(intent.getIntExtra(EXTRA_DIGEST_COUNT, 0));
		digest.setAuthor(intent.getStringExtra(EXTRA_DIGEST_COVER));
		
		mMyOrderViewModel = new MyDigestInfoViewModelLeyue(this, digest, this);
		mMyOrderViewModel.bFootLoadingVisibility.subscribe(this);
		mFootLoadingView = getLayoutInflater().inflate(R.layout.loading_data_lay, null);
		mFootLoadingLay = new FrameLayout(this);
		return bindView(R.layout.my_digest_info_layout, this, mMyOrderViewModel);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleContent(getString(R.string.my_digest)); 
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

	@Override
	public void onPreBindView(View rootView, int layoutId) {
		if (layoutId == R.layout.my_digest_info_layout) {
			ListView listView = (ListView) rootView.findViewById(R.id.order_list);
			listView.addFooterView(mFootLoadingLay);
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
