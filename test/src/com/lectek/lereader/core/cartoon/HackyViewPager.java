package com.lectek.lereader.core.cartoon;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 
 * @author laijp
 * @date 2014-3-5
 * @email 451360508@qq.com
 */
public class HackyViewPager extends ViewPager implements IPageView{

	public HackyViewPager(Context context) {
		super(context);
	}

	public HackyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (IllegalArgumentException e) {
		} catch (ArrayIndexOutOfBoundsException e) {

		}
		return false;
	}

	@Override
	public boolean isPhotoView() {
		return true;
	}

	@Override
	public void moveToNext() {
		int page = getCurrentItem();
		setCurrentItem(page + 1);
	}

	@Override
	public void moveToPrevious() {
		int page = getCurrentItem();
		setCurrentItem(page - 1);
	}
 
}