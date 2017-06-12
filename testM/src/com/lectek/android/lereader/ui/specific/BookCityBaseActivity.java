package com.lectek.android.lereader.ui.specific;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.utils.DateUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.widgets.PullRefreshLayout;
import com.lectek.android.lereader.widgets.PullRefreshLayout.OnPullListener;
import com.lectek.android.lereader.widgets.PullRefreshLayout.OnPullStateListener;
/**
 * 带下拉刷新的Activity
 * @author gyz
 *
 */
public abstract class BookCityBaseActivity extends BaseActivity implements
		OnPullListener, OnPullStateListener {
	public PullRefreshLayout mPullRefreshLayout;
	//
	private Animation mRotateUpAnimation;
	private Animation mRotateDownAnimation;
	private TextView mActionText;
	private TextView mTimeText;
	private View mProgress;
	private View mActionImage;
	
	protected boolean mInLoading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initPullView();
	}

	protected void initPullView() {
		mRotateUpAnimation = AnimationUtils.loadAnimation(this_,
				R.anim.rotate_up);
		mRotateDownAnimation = AnimationUtils.loadAnimation(this_,
				R.anim.rotate_down);
		mPullRefreshLayout.setOnActionPullListener(this);
		mPullRefreshLayout.setOnPullStateChangeListener(this);

		mProgress = findViewById(android.R.id.progress);
		mActionImage = findViewById(android.R.id.icon);
		mActionText = (TextView) findViewById(R.id.pull_note);
		mTimeText = (TextView) findViewById(R.id.refresh_time);

		mTimeText.setText(R.string.note_not_update);
		mActionText.setText(R.string.note_pull_down);
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mPullRefreshLayout = (PullRefreshLayout)LayoutInflater.from(this).inflate(
				R.layout.bs_web_view, null);
		View contentView = getContentView();
		if (contentView != null) {
			mPullRefreshLayout.addView(contentView);
		}
		return mPullRefreshLayout;
	}

	protected abstract View getContentView();

	@Override
	public void onPullOut() {
//		LogUtil.e(">>>>>>>>>>>>>>>onPullOut");
		if (!mInLoading) {
			mActionText.setText(R.string.note_pull_refresh);
			mActionImage.clearAnimation();
			mActionImage.startAnimation(mRotateUpAnimation);
		}
	}

	@Override
	public void onPullIn() {
//		LogUtil.e(">>>>>>>>>>>>>>>onPullIn");
		if (!mInLoading) {
			mActionText.setText(R.string.note_pull_down);
			mActionImage.clearAnimation();
			mActionImage.startAnimation(mRotateDownAnimation);
		}
	}

	@Override
	public void onSnapToTop() {
		// TODO Auto-generated method stub
//		LogUtil.e(">>>>>>>>>>>>>>>onSnapToTop");
		// 数据加载
		if (!mInLoading) {
			mPullRefreshLayout.setEnableStopInActionView(false);
			mActionImage.clearAnimation();
			mActionImage.setVisibility(View.GONE);
			mProgress.setVisibility(View.VISIBLE);
			mActionText.setText(R.string.note_pull_loading);
			mTimeText.setText(this_.getString(R.string.note_update_at, DateUtil.getCurrentTimeByMDHM()));
		}
	}


	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onShow() {}

	@Override
	public void onHide() {}

	@Override
	public boolean isPullEnabled() {
		return true;
	}

	/** 刷新加载结束 */
	protected void dataLoadEnd() {
		if (mInLoading) {
			mInLoading = false;
			mPullRefreshLayout.setEnableStopInActionView(false);
			mPullRefreshLayout.hideActionView();
			mActionImage.setVisibility(View.VISIBLE);
			mProgress.setVisibility(View.GONE);

			if (mPullRefreshLayout.isPullOut()) {
				mActionText.setText(R.string.note_pull_refresh);
				mActionImage.clearAnimation();
				mActionImage.startAnimation(mRotateUpAnimation);
			} else {
				mActionText.setText(R.string.note_pull_down);
			}
		}
	}

}
