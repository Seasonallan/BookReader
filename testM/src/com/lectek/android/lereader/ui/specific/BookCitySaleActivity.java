package com.lectek.android.lereader.ui.specific;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.bookCitySale.BookCitySaleTwoViewModelLeyue;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.widgets.MyViewPager;
import com.lectek.android.lereader.widgets.SlidingView.ISlideGuestureHandler;
/**
 * 书城排行 
 * @author gyz
 *
 */
public class BookCitySaleActivity extends BaseActivity implements ISlideGuestureHandler  {
	private final String TAG = BookCitySaleActivity.class.getSimpleName();
	
	private MyViewPager viewPager;// 页卡内容
	private ImageView mImageView;// 动画图片
	private TextView sale_totle_tv, sale_best_tv, sale_newbook_tv,
			sale_free_tv;
	public List<TextView> textViewList;
	private List<View> views;// Tab页面列表
//	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int mImageViewWidth = 0;// 动画图片宽度
	// private View ;//各个页卡
	private BookCitySaleView totleSaleView, bestSaleView, newBookSaleView,
			freeSaleView;
	public Animation animation;

	private boolean mPause = true;
	
	@Override
	protected View newContentView(Bundle savedInstanceState) {
		View saleView = LayoutInflater.from(this).inflate(
				R.layout.bookcity_sale_layout, null);
		return saleView;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleBarEnabled(false);
		InitImageView();
		InitTextView();
		InitViewPager();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mPause = true;
		BookCitySaleView model = getCurrentModel(viewPager.getCurrentItem());
		if(model != null) {
			model.onRelease();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mPause = false;
		BookCitySaleView model = getCurrentModel(viewPager.getCurrentItem());
		if(model != null) {
			model.onStart();
		}
//		LogUtil.e("currentIndex>>>>>>>>>>>>>" + currIndex);
		if(animation != null && mImageView != null){
			animation.setDuration(0);
			mImageView.startAnimation(animation);
		}
	}
	
	private BookCitySaleView getCurrentModel(int position) {
		BookCitySaleView result = null;
		if(position >= 0) {
			switch(position) {
			case 0:
				result = totleSaleView;
				break;
			case 1:
				result = bestSaleView;
				break;
			case 2:
				result = newBookSaleView;
				break;
			case 3:
				result = freeSaleView;
			}
		}
		
		return result;
	}
	
	@Override
	protected void onDestroy() {
		totleSaleView.finish();
		bestSaleView.finish();
		newBookSaleView.finish();
		freeSaleView.finish();
		
		viewPager.setAdapter(null);
		super.onDestroy();
	}
	
	@Override
	public void showLoadView() {
		showLoadDialog();
	}
	
	@Override
	public void hideLoadView() {
		hideLoadDialog();
	}

	/**
	 * 初始化选项卡底部动画
	 */
	private void InitImageView() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		mImageViewWidth = screenW / 4;
		//
		mImageView = (ImageView) findViewById(R.id.cursor);
		LayoutParams mLayoutParams = mImageView.getLayoutParams();
		mLayoutParams.width = mImageViewWidth;
		
//		offset = (screenW / 4 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(mImageViewWidth, 0);
		mImageView.setImageMatrix(matrix);// 设置动画初始位置
	}

	/**
	 * 初始化头标
	 */
	private void InitTextView() {
		sale_totle_tv = (TextView) findViewById(R.id.sale_totle_tv);
		sale_best_tv = (TextView) findViewById(R.id.sale_best_tv);
		sale_newbook_tv = (TextView) findViewById(R.id.sale_newbook_tv);
		sale_free_tv = (TextView) findViewById(R.id.sale_free_tv);
		textViewList = new ArrayList<TextView>();
		textViewList.add(sale_totle_tv);
		textViewList.add(sale_best_tv);
		textViewList.add(sale_newbook_tv);
		textViewList.add(sale_free_tv);
		sale_totle_tv.setOnClickListener(new MyOnClickListener(0));
		sale_best_tv.setOnClickListener(new MyOnClickListener(1));
		sale_newbook_tv.setOnClickListener(new MyOnClickListener(2));
		sale_free_tv.setOnClickListener(new MyOnClickListener(3));
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		
		LayoutParams lp = sale_free_tv.getLayoutParams();
		lp.width = screenW / 4;
		sale_free_tv.setLayoutParams(lp);
		
		lp = sale_best_tv.getLayoutParams();
		lp.width = screenW / 4;
		sale_best_tv.setLayoutParams(lp);
		
		lp = sale_newbook_tv.getLayoutParams();
		lp.width = screenW / 4;
		sale_newbook_tv.setLayoutParams(lp);
		
		lp = sale_totle_tv.getLayoutParams();
		lp.width = screenW / 4;
		sale_totle_tv.setLayoutParams(lp);
	}

	private void InitViewPager() {
		viewPager = (MyViewPager) findViewById(R.id.sale_pager);
		views = new ArrayList<View>();
		totleSaleView = new BookCitySaleView(this, this, BookCitySaleTwoViewModelLeyue.TOTLE_BOOK_RANKID);
		bestSaleView = new BookCitySaleView(this, this, BookCitySaleTwoViewModelLeyue.BEST_BOOK_RANKID); // ,
		newBookSaleView = new BookCitySaleView(this, this, BookCitySaleTwoViewModelLeyue.NEW_BOOK_RANKID); // ,
		freeSaleView = new BookCitySaleView(this, this, BookCitySaleTwoViewModelLeyue.FREE_BOOK_RANKID); // ,
		views.add(totleSaleView.getRootView());
		views.add(bestSaleView.getRootView());
		views.add(newBookSaleView.getRootView());
		views.add(freeSaleView.getRootView());
		MyViewPagerAdapter pagerAdapter = new MyViewPagerAdapter(views);
		pagerAdapter.setLiftCycleListener(mViewPageLiftCycleListener);
		pagerAdapter.setLoadTag(false);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(1);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
//		viewPager.onInterceptTouchEvent(arg0);
	}
	
	private ILiftCycle mViewPageLiftCycleListener = new ILiftCycle() {
		
		@Override
		public void onResume(View view, int position) {
			LogUtil.i(TAG, "onResume position:" + String.valueOf(position));
			
			BookCitySaleView model = getCurrentModel(position);
			if(model != null) {
				model.onStart();
			}
//			if(view.equals(totleSaleView.getRootView())) {
//				totleSaleView.onStart();
//			}else if(view.equals(bestSaleView.getRootView())) {
//				bestSaleView.onStart();
//			}else if(view.equals(newBookSaleView.getRootView())) {
//				newBookSaleView.onStart();
//			}else if(view.equals(freeSaleView.getRootView())) {
//				freeSaleView.onStart();
//			}
		}
		
		@Override
		public void onPause(View view, int position) {
			LogUtil.i(TAG, "onPause position:" + String.valueOf(position));
			BookCitySaleView model = getCurrentModel(position);
			if(model != null) {
				model.onRelease();
			}
//			if(view.equals(totleSaleView.getRootView())) {
//				totleSaleView.onRelease();
//			}else if(view.equals(bestSaleView.getRootView())) {
//				bestSaleView.onRelease();
//			}else if(view.equals(newBookSaleView.getRootView())) {
//				newBookSaleView.onRelease();
//			}else if(view.equals(freeSaleView.getRootView())) {
//				freeSaleView.onRelease();
//			}
		}
	};
	
//	public void creatView(int mIndex){
//		if(mIndex == 0)
//			totleSaleView.onStart();
//		else if(mIndex == 1)
//			bestSaleView.onStart();
//		else if(mIndex == 2)
//			newBookSaleView.onStart();
//		else if(mIndex == 3)
//			freeSaleView.onStart();
//	}

	private class MyOnClickListener implements OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			viewPager.setCurrentItem(index);
		}
	}

	public class MyViewPagerAdapter extends PagerAdapter {
		private List<View> mListViews;
		public HashMap<View, Boolean> loadTag;
		private int mCurrentPosition = -1;
		private ILiftCycle mLiftCycleListener;
		
		public MyViewPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
			loadTag = new HashMap<View, Boolean>();
		}

		public void setLoadTag(boolean tag) {
			if (mListViews != null && mListViews.size() > 0) {
				int size = mListViews.size();
				for (View views : mListViews) {
					loadTag.put(views, tag);
				}
			}
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void setPrimaryItem(View container, int position, Object object) {
			super.setPrimaryItem(container, position, object);
			LogUtil.i(TAG, ">>>>>>>>>>>>>setPrimaryItem position:" + String.valueOf(position));
			if (loadTag.get(mListViews.get(position)) == false) {
//				creatView(position);
				loadTag.put(mListViews.get(position), true);
			}
			
			if(mCurrentPosition != position ) {
				if(mLiftCycleListener != null) {
					if(mCurrentPosition >= 0) {
						mLiftCycleListener.onPause(mListViews.get(mCurrentPosition), mCurrentPosition);
					}
					if(!mPause) {	//当前activity没显示时禁止刷新
						mLiftCycleListener.onResume(mListViews.get(position), position);
					}
				}
				
				mCurrentPosition = position;
			}
		}
		
		public void setLiftCycleListener(ILiftCycle listener) {
			mLiftCycleListener = listener;
		}
	}

	public interface ILiftCycle {
		public void onResume(View view, int position);
		public void onPause(View view, int position);
	}
	
	public class MyOnPageChangeListener implements OnPageChangeListener {

//		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
//		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		public void onPageScrollStateChanged(int arg0) {
			
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
			viewPager.requestDisallowInterceptTouchEvent(true);
		}

		public void onPageSelected(int position) {
			animation = new TranslateAnimation(mImageViewWidth * currIndex, mImageViewWidth
					* position, 0, 0);// 显然这个比较简洁，只有一行代码。
			currIndex = position;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			mImageView.startAnimation(animation);
			selectTextColor(position);
		}
	}
	
	public void selectTextColor(int currIndex){
		for (int i = 0; i < textViewList.size(); i++) {
			if(i == currIndex){
				textViewList.get(i).setTextColor(getResources().getColor(R.color.common_14));
			}else{
				textViewList.get(i).setTextColor(getResources().getColor(R.color.color_414141));
			}
		}
	}
	
	@Override
	public boolean consumeHorizontalSlide(float x, float y, float deltaX) {
//		LogUtil.e(Tag,">>>>>>>>>>>consumeHorizontalSlide>>>>>>>");
		return true;
	}

}
