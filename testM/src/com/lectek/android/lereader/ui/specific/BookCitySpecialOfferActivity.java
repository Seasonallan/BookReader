package com.lectek.android.lereader.ui.specific;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.bookCityWelfare.BookCityWelfareViewModelLeyue;
import com.lectek.android.lereader.binding.model.bookCityWelfare.BookCityWelfareViewModelLeyue.BookCityWelfareViewUserAciton;
import com.lectek.android.lereader.net.response.SubjectResultInfo;
import com.lectek.android.lereader.widgets.SlidingView.ISlideGuestureHandler;
import com.lectek.android.lereader.widgets.SlidingViewSwitcher;
/**
 * 书城福利
 * @author gyz
 *
 */
public class BookCitySpecialOfferActivity extends BookCityBaseActivity implements
		BookCityWelfareViewUserAciton,ISlideGuestureHandler {
	public final String TAG = BookCitySpecialOfferActivity.class.getSimpleName();
	private BookCityWelfareViewModelLeyue mBookCityWelfareViewModelLeyue;
	private SlidingViewSwitcher mSubjectSwitecherView;
	
	@Override
	public void exceptionHandle(String str) {}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleBarEnabled(false);
	}

	@Override
	public void onResume() {
		super.onResume();
		mBookCityWelfareViewModelLeyue.onStart();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mBookCityWelfareViewModelLeyue.onRelease();
	}
	
	@Override
	protected void onDestroy() {
		mBookCityWelfareViewModelLeyue.finish();
		super.onDestroy();
	}

	@Override
	public void optToast(String str) {}

	@Override
	public void loadSubjectOver(ArrayList<SubjectResultInfo> subjectList) {
		mSubjectSwitecherView.setData(subjectList);
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		return super.newContentView(savedInstanceState);
	}
	
	@Override
	public void onSnapToTop() {
		// TODO Auto-generated method stub
		super.onSnapToTop();
		if(!mInLoading){
			mBookCityWelfareViewModelLeyue.pullRefershStart();
			mInLoading = true;
		}
	}

	@Override
	public void loadDataEnd() {
		// TODO Auto-generated method stub
		dataLoadEnd();  //关闭下拉刷新
	}

	@Override
	protected View getContentView() {
		// TODO Auto-generated method stub
		mBookCityWelfareViewModelLeyue = new BookCityWelfareViewModelLeyue(this,this,this);
		View view=bindView(R.layout.bookcity_welfare_layout, this, mBookCityWelfareViewModelLeyue);
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
