package com.lectek.android.lereader.ui.specific;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.bookcityrecommend.BookCityRecommendViewModelLeyue;
import com.lectek.android.lereader.binding.model.bookcityrecommend.BookCityRecommendViewModelLeyue.BookCityRecommendViewUserAciton;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.response.SubjectResultInfo;
import com.lectek.android.lereader.widgets.PullToRefreshScrollView;
import com.lectek.android.lereader.widgets.SlidingView.ISlideGuestureHandler;
import com.lectek.android.lereader.widgets.SlidingViewSwitcher;
/**
 * 书城推荐页面
 * @author Administrator
 *
 */
public class BookCityRecommendActivity extends BookCityBaseActivity implements
		BookCityRecommendViewUserAciton,ISlideGuestureHandler{

	private final String TAG = BookCityRecommendActivity.class.getSimpleName();
	private SlidingViewSwitcher mSubjectSwitecherView;
	private BookCityRecommendViewModelLeyue mBookCityRecommendViewModel;
	//
	private PullToRefreshScrollView mPullToRefreshScrollView;
	private ScrollView mScrollView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleBarEnabled(false);
	}

	@Override
	public void exceptionHandle(String str) {
		
	}

	@Override
	public void optToast(String str) {
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mBookCityRecommendViewModel.onStart();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mBookCityRecommendViewModel.onRelease();
		
	}
	
	@Override
	protected void onDestroy() {
		mBookCityRecommendViewModel.finish();
		super.onDestroy();
	}

	@Override
	public void loadSubjectOver(ArrayList<SubjectResultInfo> subjectList) {
		mSubjectSwitecherView.setData(subjectList);
	}
	

	@Override
	protected View newContentView(Bundle savedInstanceState) {
//		mBookCityRecommendViewModel = new BookCityRecommendViewModelLeyue(this, this,this);
//		View view = bindView(R.layout.bookcity_recommend_layout,this, mBookCityRecommendViewModel);
//		mPullToRefreshScrollView = (PullToRefreshScrollView)view;
//		mPullToRefreshScrollView.setPullDownRefreshEnabled(true);
//		mPullToRefreshScrollView.setPullUpRefreshEnabled(false);
//		ViewGroup subject = (ViewGroup) view.findViewById(R.id.recommend_container);
//		mSubjectSwitecherView = new SlidingViewSwitcher(this, null);
//		subject.addView(mSubjectSwitecherView);
		return super.newContentView(savedInstanceState);
	}
	
	@Override
	protected View getContentView() {
		mBookCityRecommendViewModel = new BookCityRecommendViewModelLeyue(this, this,this);
		View view = bindView(R.layout.bookcity_recommend_layout,this, mBookCityRecommendViewModel);
		ViewGroup subject = (ViewGroup) view.findViewById(R.id.recommend_container);
		mSubjectSwitecherView = new SlidingViewSwitcher(this, null);
		subject.addView(mSubjectSwitecherView);
		return view;
	}
	
	@Override
	public void onSnapToTop() {
		super.onSnapToTop();
		if(!mInLoading){
			mBookCityRecommendViewModel.pullRefershStart();
			mInLoading = true;
		}
		
	}

	@Override
	public void loadDataEnd() {
		dataLoadEnd();  //关闭下拉刷新
	}

	@Override
	public boolean consumeHorizontalSlide(float x, float y, float deltaX) {
//		if(deltaX > 0){
//			return false;
//		}
		return false;
	}

}
