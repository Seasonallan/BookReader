package com.lectek.android.lereader.ui.specific;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.bookCityClassify.BookCityClassifyViewModelLeyue;
import com.lectek.android.lereader.binding.model.bookCityClassify.BookCityClassifyViewModelLeyue.BookCityClassifyViewUserAciton;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.response.SubjectResultInfo;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.widgets.SlidingView.ISlideGuestureHandler;
import com.lectek.android.lereader.widgets.SlidingViewSwitcher;
/**
 * 书城分类
 * @author Administrator
 *
 */
public class BookCityClassifyActivity extends BookCityBaseActivity implements
		BookCityClassifyViewUserAciton,ISlideGuestureHandler {

	public static final String Tag = BookCityClassifyView.class.getSimpleName();
	private BookCityClassifyViewModelLeyue mBookCityClassifyViewModelLeyue;
	private SlidingViewSwitcher mSubjectSwitecherView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleBarEnabled(false);
	}

	@Override
	public void onResume() {
		super.onResume();
		mBookCityClassifyViewModelLeyue.onStart();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		mBookCityClassifyViewModelLeyue.onRelease();
	}
	
	@Override
	protected void onDestroy() {
		mBookCityClassifyViewModelLeyue.finish();
		super.onDestroy();
	}

	@Override
	public void exceptionHandle(String str) {
		setTipImageResource(R.drawable.icon_request_fail_tip);
	}

	@Override
	public void optToast(String str) {
		
	}

	@Override
	public void loadSubjectOver(ArrayList<SubjectResultInfo> subjectList) {
		mSubjectSwitecherView.setData(new ArrayList<SubjectResultInfo>(subjectList.subList(0, Math.min(4, subjectList.size()))));
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		return super.newContentView(savedInstanceState);
	}
	
	@Override
	public void onSnapToTop() {
		super.onSnapToTop();
		if(!mInLoading){
			mBookCityClassifyViewModelLeyue.pullRefershStart();
			mInLoading = true;
		}
	}

	@Override
	public void loadDataEnd() {
		dataLoadEnd();  //关闭下拉刷新
	}

	@Override
	protected View getContentView() {
		// TODO Auto-generated method stub
		mBookCityClassifyViewModelLeyue = new BookCityClassifyViewModelLeyue(this, this,this);
		View view = bindView(R.layout.bookcity_classify_layout, this, mBookCityClassifyViewModelLeyue);
		ViewGroup subject = (ViewGroup) view.findViewById(R.id.subject_container);
		mSubjectSwitecherView = new SlidingViewSwitcher(this, null);
		subject.addView(mSubjectSwitecherView);
		return view;
	}

	@Override
	public boolean consumeHorizontalSlide(float x, float y, float deltaX) {
		// TODO Auto-generated method stub
//		LogUtil.e(Tag, "consumeHorizontalSlide");
		return false;
	}

}
