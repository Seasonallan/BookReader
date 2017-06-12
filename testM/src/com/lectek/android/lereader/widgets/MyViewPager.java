package com.lectek.android.lereader.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.lectek.android.lereader.lib.utils.LogUtil;

public class MyViewPager extends ViewPager {
	private final String tag = this.getClass().getSimpleName();
	private float curr_x;

	public MyViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
//		LogUtil.e(tag, "onInterceptTouchEvent");
		return super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
//		LogUtil.e(tag, "onTouchEvent");
		return super.onTouchEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
//		LogUtil.e(tag, "dispatchTouchEvent");
//		gestureDetector.onTouchEvent(event); 
		/*switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			curr_x = event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			if ((event.getX() - curr_x) > 0 && getCurrentItem() == 0) {
				LogUtil.e(tag, "move right");
				getParent().requestDisallowInterceptTouchEvent(false);
			} else if ((event.getX() - curr_x) < 0 && getCurrentItem() == 3) {
				LogUtil.e(tag, "move left");
				getParent().requestDisallowInterceptTouchEvent(false);
			} else {
				LogUtil.e(tag, "move viewpager");
				getParent().requestDisallowInterceptTouchEvent(true);
			}
			break;
		default:
			break;
		}*/
		return super.dispatchTouchEvent(event);
	}

}
