/*
 * Copyright (C) 2012 yueyueniao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lectek.android.lereader.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.widgets.drag.DragController;


public class SlidingView extends RelativeLayout {

	private View mSlidingView;
	private View mLeftView;
	private View mRightView;
	private RelativeLayout bgShade;
	private int screenWidth;
	private int screenHeight;
	private Context mContext;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private int mTouchSlop;
	private float mLastMotionX;
	private float mLastMotionY;
	private static final int VELOCITY = 50;
	private boolean mIsBeingDragged = true;  

	private ISlideGuestureHandler mSlideGuestureListener;

	public SlidingView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		bgShade = new RelativeLayout(context);
		
		mScroller = new Scroller(getContext());
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		screenWidth = context.getResources().getDisplayMetrics().widthPixels;
		screenHeight = context.getResources().getDisplayMetrics().heightPixels;
		
		LayoutParams bgParams = new LayoutParams(screenWidth, screenHeight);
		bgParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		bgShade.setLayoutParams(bgParams); 
	}

	public SlidingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SlidingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public void addViews(View left, View center, View right) {
//		setLeftView(left);
//		setRightView(right);
//		setCenterView(center);
	}

	public void setLeftView(View view) {
		LayoutParams behindParams = new LayoutParams(screenWidth * 2/3,
				LayoutParams.MATCH_PARENT);
		addView(view, behindParams);
		mLeftView = view;
	}

	public void setRightView(View view) {
		LayoutParams behindParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);
		behindParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		addView(view, behindParams);
		mRightView = view;
	}

    private int getShadeResourceId(){
        return R.drawable.shade_bg;
    }

	public void setCenterView(View view) {
		LayoutParams aboveParams = new LayoutParams(screenWidth,
                screenHeight);
        aboveParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        int id = getShadeResourceId();
        if (id > 0){
            LayoutParams bgParams = new LayoutParams(screenWidth, screenHeight);
            bgParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            
            View bgShadeContent = new View(mContext);
            bgShadeContent.setBackgroundResource(id);
            bgShade.addView(bgShadeContent, bgParams);
            addView(bgShade, bgParams);
        }

		addView(view, aboveParams);
		mSlidingView = view;
		mSlidingView.bringToFront();
	}
 
	@Override
	public void computeScroll() {
		if (!mScroller.isFinished()) {
			if (mScroller.computeScrollOffset()) {
				
				int oldX = mSlidingView.getScrollX();
				int oldY = mSlidingView.getScrollY();
				int x = mScroller.getCurrX();
				int y = mScroller.getCurrY(); 
				if (oldX != x || oldY != y) {
					if (mSlidingView != null) {
						mSlidingView.scrollTo(x, y);
						scrollShadeTo(x, y);
					}
				}
				
				if(x > 0 && canSlideRight && mLeftView.getVisibility() == View.VISIBLE) {
					mLeftView.setVisibility(View.INVISIBLE);
					mRightView.setVisibility(View.VISIBLE);
				}else if(y < 0 && canSlideLeft && mRightView.getVisibility() == View.VISIBLE) {
					mRightView.setVisibility(View.INVISIBLE);
					mLeftView.setVisibility(View.VISIBLE);
				}
				
				invalidate();
			}
		} 
		
//		LogUtil.i(SlidingView.class.getSimpleName(), "computeScroll");
	}
	
	private void determine2Drag(MotionEvent ev){
		final float x = ev.getX();
		final float y = ev.getY();
		final float dx = x - mLastMotionX;
		final float xDiff = Math.abs(dx);
		final float yDiff = Math.abs(y - mLastMotionY);
		if (xDiff > mTouchSlop && xDiff > yDiff) {
			
			if(mSlideGuestureListener != null && mSlideGuestureListener.consumeHorizontalSlide(x, y, dx)) {
				return;
			}
			
			if(mQuickClick){
				mIsBeingDragged = true;
				mLastMotionX = x;
			}else{ 
				if(canSlideLeft && canSlideRight){
					if (dx > 0) {
						invisibleView(true, false);
					}else{
						invisibleView(false, true);
					}
					mIsBeingDragged = true;
					mLastMotionX = x;
				}else if (canSlideLeft) {
					float oldScrollX = mSlidingView.getScrollX();
					if (oldScrollX < 0) {
						mIsBeingDragged = true;
						mLastMotionX = x;
					} else {
						if (dx > 0) {
							mIsBeingDragged = true;
							mLastMotionX = x;
						}
					}
	
				} else if (canSlideRight) {
					float oldScrollX = mSlidingView.getScrollX();
					if (oldScrollX > 0) {
						mIsBeingDragged = true;
						mLastMotionX = x;
					} else {
						if (dx < 0) {
							mIsBeingDragged = true;
							mLastMotionX = x;
						}
					}
				}
			}
		}
	}

	private boolean canSlideLeft = true, mCanSlideLeft = true;
	private boolean canSlideRight = false, mCanSlideRight = false;

	
	public void setCanSliding(boolean left, boolean right) {
		mCanSlideLeft = left;
		mCanSlideRight = right;
        canSlideLeft = mCanSlideLeft;
        canSlideRight = mCanSlideRight;
	}

	private void invisibleView(boolean canLeft,boolean canRight){
		canSlideLeft = canLeft;
		canSlideRight = canRight;
		mLeftView.setVisibility(!canLeft? View.INVISIBLE: View.VISIBLE);
		if (mRightView != null) {
			mRightView.setVisibility(!canRight? View.INVISIBLE: View.VISIBLE);
		}
	}
	
	private boolean mQuickClick =false;
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (DragController.getInstance().isDraging()){
            return false;
        }
		if(!mScroller.isFinished()){
			return true;
		}
		
		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y; 
			
			if(mSlidingView.getScrollX() <= -mLeftView.getWidth() && x + mSlidingView.getScrollX() > getLeft()){
				mQuickClick = true;
				invisibleView(true, false);
			}else if(mRightView != null && mSlidingView.getScrollX() >= mRightView.getWidth() && x + mSlidingView.getScrollX() < getRight()){
				mQuickClick = true;
				invisibleView(false, true);
			}else{
				mQuickClick = false;
				if(mSlidingView.getScrollX() > -mLeftView.getWidth() && mRightView != null && mSlidingView.getScrollX() < mRightView.getWidth()){
					invisibleView(mCanSlideLeft, mCanSlideRight);
				}
			}
			
			mIsBeingDragged = false;
			super.onInterceptTouchEvent(ev);
			break;

		case MotionEvent.ACTION_MOVE:
			determine2Drag(ev);
			break;

		}
		
		return mIsBeingDragged || mQuickClick;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) { 
		if(!mScroller.isFinished()){
			return true;
		}
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			if (mSlidingView.getScrollX() == -getMenuViewWidth()
					&& mLastMotionX < getMenuViewWidth()) {
				return false;
			}

			if (mSlidingView.getScrollX() == getDetailViewWidth()
					&& mLastMotionX > getMenuViewWidth()) {
				return false;
			}

			break;
		case MotionEvent.ACTION_MOVE: 
			if(!mIsBeingDragged){
				determine2Drag(ev);
			}
			if (mIsBeingDragged) {
				final float deltaX = mLastMotionX - x;
				mLastMotionX = x;
				float oldScrollX = mSlidingView.getScrollX();
				float scrollX = oldScrollX + deltaX;
				if (canSlideLeft) {
					if (scrollX > 0)
						scrollX = 0;
				}
				if (canSlideRight) {
					if (scrollX < 0)
						scrollX = 0;
				}
				if (deltaX < 0 && oldScrollX < 0) { // left view
					final float leftBound = 0;
					final float rightBound = -getMenuViewWidth();
					if (scrollX > leftBound) {
						scrollX = leftBound;
					} else if (scrollX < rightBound) {
						scrollX = rightBound;
					}
				} else if (deltaX > 0 && oldScrollX > 0) { // right view
					final float rightBound = getDetailViewWidth();
					final float leftBound = 0;
					if (scrollX < leftBound) {
						scrollX = leftBound;
					} else if (scrollX > rightBound) {
						scrollX = rightBound;
					}
				}
				if (mSlidingView != null) {
					mSlidingView.scrollTo((int) scrollX,
							mSlidingView.getScrollY());
					scrollShadeTo((int) scrollX, mSlidingView.getScrollY());
				}

			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (mIsBeingDragged) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(100);
				float xVelocity = velocityTracker.getXVelocity();
				int oldScrollX = mSlidingView.getScrollX();
				int dx = 0;
				if (oldScrollX <= 0 && canSlideLeft) {// left view
					if (xVelocity > VELOCITY) {
						dx = -getMenuViewWidth() - oldScrollX;
					} else if (xVelocity < -VELOCITY) {
						dx = -oldScrollX; 
					} else if (oldScrollX < -getMenuViewWidth() / 2) {
						dx = -getMenuViewWidth() - oldScrollX;
					} else if (oldScrollX >= -getMenuViewWidth() / 2) {
						dx = -oldScrollX; 
					}

				}
				if (oldScrollX >= 0 && canSlideRight) {
					if (xVelocity < -VELOCITY) {
						dx = getDetailViewWidth() - oldScrollX;
					} else if (xVelocity > VELOCITY) {
						dx = -oldScrollX; 
					} else if (oldScrollX > getDetailViewWidth() / 2) {
						dx = getDetailViewWidth() - oldScrollX;
					} else if (oldScrollX <= getDetailViewWidth() / 2) {
						dx = -oldScrollX; 
					}
				}

				smoothScrollTo(dx);

			}else if (mQuickClick) {
				// close the menu
				mQuickClick =false; 
				if(mSlidingView.getScrollX() <= -mLeftView.getWidth() && x + mSlidingView.getScrollX() > getLeft()){
					smoothScrollTo(mLeftView.getWidth());
				}else if(mRightView != null && mSlidingView.getScrollX() >= mRightView.getWidth() && x + mSlidingView.getScrollX() < getRight()){
					smoothScrollTo(-mRightView.getWidth());
				} 
			}

			break;
		}

		return true;
	}

	private void scrollShadeTo(int x, int y) {
		if (x < 0)
			//左边暂时不需要隐去阴影
			bgShade.scrollTo(x + 20, y);
		else if(mRightView != null) {
			bgShade.scrollTo(x - Math.min(20, Math.abs(mRightView.getWidth() - x)), y);
		}
	}
	
	private int getMenuViewWidth() {
		if (mLeftView == null) {
			return 0;
		}
		return mLeftView.getWidth();
	}

	private int getDetailViewWidth() {
		if (mRightView == null) {
			return 0;
		}
		return mRightView.getWidth();
	}

	void smoothScrollTo(int dx) {  
		int duration = Math.abs(dx) * 2;
		int oldScrollX = mSlidingView.getScrollX();
		
		mScroller.startScroll(oldScrollX, mSlidingView.getScrollY(), dx,
				mSlidingView.getScrollY(), duration);
		invalidate();
	}

	public boolean showCenterView(){
		if (isLeftShow()) {
			int menuWidth = mLeftView.getWidth();
			smoothScrollTo(menuWidth); 
			return false; 
		}else if (isRightShow()) {
			int menuWidth = mRightView.getWidth();
			smoothScrollTo(-menuWidth); 
			return false;
		}
		return true;
	}
	
	public void showLeftView() {
		
		canSlideLeft = true;
		canSlideRight = false;
		
		int menuWidth = mLeftView.getWidth();
		int oldScrollX = mSlidingView.getScrollX();
		Log.e("SlidingMenu", "menuWidth: " + menuWidth);
		if (oldScrollX == 0) {
			mLeftView.setVisibility(View.VISIBLE);
            if (mRightView != null){
                mRightView.setVisibility(View.INVISIBLE);
            }
			smoothScrollTo(-menuWidth); 
		} else if (oldScrollX == -menuWidth) {
			smoothScrollTo(menuWidth); 
		}
	}

	public void showRightView() {
		if (mRightView == null) {
			return;
		}
		
		canSlideLeft = false;
		canSlideRight = true;
		
		int menuWidth = mRightView.getWidth();
		int oldScrollX = mSlidingView.getScrollX();
		if (oldScrollX == menuWidth) {
			smoothScrollTo(-menuWidth); 
		}else {
		
			if (oldScrollX == 0) {
				mLeftView.setVisibility(View.INVISIBLE);
				mRightView.setVisibility(View.VISIBLE);
			}
			smoothScrollTo(menuWidth - oldScrollX);
		}
	}

	public boolean isLeftShow(){
		int menuWidth = mLeftView.getWidth();
		int oldScrollX = mSlidingView.getScrollX();
		return oldScrollX == -menuWidth;
	}

	public boolean isRightShow(){
		if (mRightView == null) {
			return false;
		}
		int menuWidth = mRightView.getWidth();
		int oldScrollX = mSlidingView.getScrollX();
		return oldScrollX == menuWidth;
	}
	
	public void setSlideGuestureListener(ISlideGuestureHandler listener) {
		mSlideGuestureListener = listener;
	}
	
	public interface ISlideGuestureHandler {
		/**
		 * 子界面是否消费掉横向滑动事件
		 * @param x		横向滑动x坐标
		 * @param y		横向滑动y坐标
		 * @param deltaX	横向滑动本次与上次的滑动距离
		 * @return 如果消费掉返回ture，否则返回false
		 */
		public boolean consumeHorizontalSlide(float x, float y, float deltaX);
	}
}
