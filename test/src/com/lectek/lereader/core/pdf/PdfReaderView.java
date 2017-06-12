package com.lectek.lereader.core.pdf;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Scroller;

public class PdfReaderView extends AdapterView<Adapter> implements GestureDetector.OnGestureListener,
ScaleGestureDetector.OnScaleGestureListener, Runnable {
	
	/**
	 * 布局类型--适应屏幕
	 */
	public static final int LAYOUT_TYPE_FITSCREEN = 1;
	/**
	 * 布局类型--适应宽度
	 */
	public static final int LAYOUT_TYPE_FITWIDTH = 2;
	/**
	 * 布局类型
	 */
	protected int mLayoutType = LAYOUT_TYPE_FITWIDTH;
	
	/**
	 * 页面之间的间隙
	 */
	private static final int  GAP = 10;
	/**
	 * 最小缩放比例
	 */
	private static final float MIN_SCALE = 1.0f;
	/**
	 * 最大缩放比例
	 */
	private static final float MAX_SCALE = 5.0f;
	private float mScale = MIN_SCALE;
	
	private Adapter mAdapter;
	/**
	 * Adapter's index for the current view
	 */
	protected int mCurrent;
	private boolean mResetLayout;
	/**
	 * Shadows the children of the adapter view
	 * but with more sensible indexing
	 */
	private final SparseArray<View> mChildViews = new SparseArray<View>(3);
	private final LinkedList<View> mViewCache = new LinkedList<View>();
	private boolean           mUserInteracting;  // Whether the user is interacting
	private boolean           mScaling;    // Whether the user is currently pinch zooming
	private int               mXScroll;    // Scroll amounts recorded from events.
	private int               mYScroll;    // and then accounted for in onLayout
	private final GestureDetector mGestureDetector;
	private final ScaleGestureDetector mScaleGestureDetector;
	private final Scroller    mScroller;
	private int               mScrollerLastX;
	private int               mScrollerLastY;
	private boolean           mScrollDisabled;
	
	private int mMinimumVelocity;
	private int mMaximumVelocity;
	private int mTouchSlop;
	private float mStartX;
	private float mStartY;
	private float mLastX;
	private float mLastY;
	private VelocityTracker mVelocityTracker;
	
	public PdfReaderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mGestureDetector = new GestureDetector(this);
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		mScroller        = new Scroller(context);
		init(context);
	}

	public PdfReaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGestureDetector = new GestureDetector(this);
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		mScroller        = new Scroller(context);
		init(context);
	}

	public PdfReaderView(Context context) {
		super(context);
		mGestureDetector = new GestureDetector(this);
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		mScroller        = new Scroller(context);
		init(context);
	}
	
	private void init(Context context) {
		ViewConfiguration vc = ViewConfiguration.get(context);
		mTouchSlop = vc.getScaledTouchSlop();
		mMinimumVelocity = vc.getScaledMinimumFlingVelocity();
		mMaximumVelocity = vc.getScaledMaximumFlingVelocity();
	}
	
	/**
	 * 获取当前是第几页
	 * @return
	 */
	public int getDisplayedViewIndex() {
		return mCurrent;
	}
	
	public View getDisplayedView() {
		return mChildViews.get(mCurrent);
	}
	
	/**
	 * 显示指定页
	 * @param i
	 */
	public void setDisplayedViewIndex(int i) {
		if (0 <= i && i < mAdapter.getCount()) {
			mCurrent = i;
			onMoveToChild(i);
			mResetLayout = true;
			requestLayout();
		}
	}
	
	public void moveToNext() {
		View v = mChildViews.get(mCurrent+1);
		if (v != null)
			slideViewOntoScreen(v);
	}

	public void moveToPrevious() {
		View v = mChildViews.get(mCurrent-1);
		if (v != null)
			slideViewOntoScreen(v);
	}

	public void resetupChildren() {
		for (int i = 0; i < mChildViews.size(); i++) {
			onChildSetup(mChildViews.keyAt(i), mChildViews.valueAt(i));
		}
	}
	
	private void slideViewOntoScreen(View v) {
		Point corr = getCorrection(getScrollBounds(v));
		if (corr.x != 0 || corr.y != 0) {
			mScrollerLastX = mScrollerLastY = 0;
			mScroller.startScroll(0, 0, corr.x, corr.y, 400);
			post(this);
		}
	}
	
	/**
	 * onSettle and onUnsettle are posted so that the calls
	 * wont be executed until after the system has performed layout.
	 * @param v
	 */
	private void postSettle(final View v) {
		post (new Runnable() {
			public void run () {
				onSettle(v);
			}
		});
	}

	/**
	 * onSettle and onUnsettle are posted so that the calls
	 * wont be executed until after the system has performed layout.
	 * @param v
	 */
	private void postUnsettle(final View v) {
		post (new Runnable() {
			public void run () {
				onUnsettle(v);
			}
		});
	}
	
	private Point subScreenSizeOffset(View v) {
		if(mLayoutType == LAYOUT_TYPE_FITWIDTH) {
			return new Point(0, 0);
		}
		return new Point(Math.max((getWidth() - v.getMeasuredWidth())/2, 0),
				Math.max((getHeight() - v.getMeasuredHeight())/2, 0));
	}
	
	private Rect getScrollBounds(int left, int top, int right, int bottom) {
		int xmin = getWidth() - right;
		int xmax = -left;
		int ymin = getHeight() - bottom;
		int ymax = -top;
		// In either dimension, if view smaller than screen then
		// constrain it to be central
		if (xmin > xmax) xmin = xmax = (xmin + xmax)/2;
		if (ymin > ymax) ymin = ymax = (ymin + ymax)/2;
		
		return new Rect(xmin, ymin, xmax, ymax);
	}

	private Rect getScrollBounds(View v) {
		// There can be scroll amounts not yet accounted for in
		// onLayout, so add mXScroll and mYScroll to the current
		// positions when calculating the bounds.
		return getScrollBounds(v.getLeft() + mXScroll,
				               v.getTop() + mYScroll,
				               v.getLeft() + v.getMeasuredWidth() + mXScroll,
				               v.getTop() + v.getMeasuredHeight() + mYScroll);
	}

	private Point getCorrection(Rect bounds) {
		return new Point(Math.min(Math.max(0,bounds.left),bounds.right),
				         Math.min(Math.max(0,bounds.top),bounds.bottom));
	}
	
	/**
	 * 判断是否满足fling的条件
	 * @param velocityY
	 * @return
	 */
	private boolean enableFling(float velocityY) {
		if(Math.abs(velocityY) > mMinimumVelocity) {
			return true;
		}
		return false;
	}
	
	private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

	private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
	}

	@Override
	public Adapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void setAdapter(Adapter adapter) {
		mAdapter = adapter;
		mChildViews.clear();
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public View getSelectedView() {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public void setSelection(int position) {
		setDisplayedViewIndex(position);
	}

	@Override
	public void run() {
		if (!mScroller.isFinished()) {
			mScroller.computeScrollOffset();
			int x = mScroller.getCurrX();
			int y = mScroller.getCurrY();
			mXScroll += x - mScrollerLastX;
			mYScroll += y - mScrollerLastY;
			mScrollerLastX = x;
			mScrollerLastY = y;
			requestLayout();
			post(this);
		}
		else if (!mUserInteracting) {
			// End of an inertial scroll and the user is not interacting.
			// The layout is stable
			View v = mChildViews.get(mCurrent);
			postSettle(v);
		}
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float previousScale = mScale;
		mScale = Math.min(Math.max(mScale * detector.getScaleFactor(), MIN_SCALE), MAX_SCALE);
		float factor = mScale/previousScale;
		
		View v = mChildViews.get(mCurrent);
		if (v != null) {
			// Work out the focus point relative to the view top left
			int viewFocusX = (int)detector.getFocusX() - (v.getLeft() + mXScroll);
			int viewFocusY = (int)detector.getFocusY() - (v.getTop() + mYScroll);
			// Scroll to maintain the focus point
			mXScroll += viewFocusX - viewFocusX * factor;
			mYScroll += viewFocusY - viewFocusY * factor;
			requestLayout();
		}
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		mScaling = true;
		// Ignore any scroll amounts yet to be accounted for: the
		// screen is not showing the effect of them, so they can
		// only confuse the user
		mXScroll = mYScroll = 0;
		// Avoid jump at end of scaling by disabling scrolling
		// until the next start of gesture
		mScrollDisabled = true;
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		mScaling = false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		mScroller.forceFinished(true);
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		int x = (int) e.getX();
		int y = (int) e.getY();
		int width = getWidth();
		int height = getHeight();
		
		int toolTouchAreaW = width >> 2;
		int toolTouchTouchH = height >> 2;
		int toolLP = (width - toolTouchAreaW) >> 1;
		int toolRP = width - toolLP;
		int toolTP = (height - toolTouchTouchH) >> 1;
		int toolBP = height - toolTP;
		
		if(x > toolLP && x < toolRP && y > toolTP && y < toolBP) {	//弹出菜单
//			if(mReaderCallback != null) {
//				mReaderCallback.showReaderMenu();
//			}
		} else if( (y < height/2 && x > width*2/3) || (y > height/2 && x > width/3)) {	//下一页
			moveToNext();
		} else if ((y < height/2 && x < width*2/3) || (y > height/2 && x < width/3)) {	//上一页
			moveToPrevious();
		}
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (!mScrollDisabled) {
			mXScroll -= distanceX;
			mYScroll -= distanceY;
			requestLayout();
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int n = getChildCount();
		for (int i = 0; i < n; i++)
			measureView(getChildAt(i));
	}
	
	private void measureView(View v) {
		if(v == null) {
			return;
		}
		// See what size the view wants to be
		v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		// Work out a scale that will fit it to this view
		float scale = Math.min((float)getWidth()/(float)v.getMeasuredWidth(),
					(float)getHeight()/(float)v.getMeasuredHeight());
		if(mLayoutType == LAYOUT_TYPE_FITWIDTH) {	//垂直布局时，使页面默认宽度与屏幕宽度一致
			scale = (float)getWidth()/(float)v.getMeasuredWidth();
		}
		// Use the fitting values scaled by our current scale factor
		v.measure(View.MeasureSpec.EXACTLY | (int)(v.getMeasuredWidth()*scale*mScale),
				View.MeasureSpec.EXACTLY | (int)(v.getMeasuredHeight()*scale*mScale));
	}
	
	private View getOrCreateChild(int i) {
		View v = mChildViews.get(i);
		if (v == null) {
			if(mAdapter == null) {
				return null;
			}
			v = mAdapter.getView(i, getCached(), this);
			addAndMeasureChild(i, v);
		}
		if(v != null) {
			onChildSetup(i, v);
		}
		return v;
	}
	
	private View getCached() {
		if (mViewCache.size() == 0)
			return null;
		else
			return mViewCache.removeFirst();
	}

	private void addAndMeasureChild(int i, View v) {
		LayoutParams params = v.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}
		addViewInLayout(v, 0, params, true);
		mChildViews.append(i, v); // Record the view against it's adapter index
		measureView(v);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		View cv = mChildViews.get(mCurrent);
		Point cvOffset;

		if (!mResetLayout) {
			// Move to next or previous if current is sufficiently off center
			if (cv != null) {
//				Log.v("something", "cv--left: " + cv.getLeft() + " right: " + cv.getRight() + " top: " + cv.getTop() + " bottom: " + cv.getBottom());
//				Log.v("something", "mXScroll: " + mXScroll + " mYScroll: " + mYScroll);
				if(mLayoutType == LAYOUT_TYPE_FITSCREEN) {
					cvOffset = subScreenSizeOffset(cv);
					// cv.getRight() may be out of date with the current scale
					// so add left to the measured width for the correct position
					if (cv.getLeft() + cv.getMeasuredWidth() + cvOffset.x + GAP/2 + mXScroll < getWidth()/2
							&& mCurrent + 1 < mAdapter.getCount()) {
						postUnsettle(cv);
						// post to invoke test for end of animation
						// where we must set hq area for the new current view
						post(this);
						
						mCurrent++;
						onMoveToChild(mCurrent);
					}
					
					if (cv.getLeft() - cvOffset.x - GAP/2 + mXScroll >= getWidth()/2 && mCurrent > 0) {
						postUnsettle(cv);
						// post to invoke test for end of animation
						// where we must set hq area for the new current view
						post(this);
						
						mCurrent--;
						onMoveToChild(mCurrent);
					}
				} else if (mLayoutType == LAYOUT_TYPE_FITWIDTH) {
					if(cv.getBottom() + GAP/2 + mYScroll < getHeight()/2
							&& mCurrent + 1 < mAdapter.getCount()) {
						postUnsettle(cv);
//						post(this);
						mCurrent++;
						onMoveToChild(mCurrent);
					}
					if(cv.getTop() - GAP/2 + mYScroll >= getHeight()/2 && mCurrent > 0) {
						postUnsettle(cv);
//						post(this);
						mCurrent--;
						onMoveToChild(mCurrent);
					}
				}
			}

			// Remove not needed children and hold them for reuse
			int numChildren = mChildViews.size();
			int childIndices[] = new int[numChildren];
			for (int i = 0; i < numChildren; i++)
				childIndices[i] = mChildViews.keyAt(i);

			for (int i = 0; i < numChildren; i++) {
				int ai = childIndices[i];
				if (ai < mCurrent - 1 || ai > mCurrent + 1) {
					View v = mChildViews.get(ai);
					mViewCache.add(v);
					removeViewInLayout(v);
					mChildViews.remove(ai);
				}
			}
		} else {
			mResetLayout = false;
			mXScroll = mYScroll = 0;

			// Remove all children and hold them for reuse
			int numChildren = mChildViews.size();
			for (int i = 0; i < numChildren; i++) {
				View v = mChildViews.valueAt(i);
				postUnsettle(v);
				mViewCache.add(v);
				removeViewInLayout(v);
			}
			mChildViews.clear();
			// post to ensure generation of hq area
			post(this);
		}

		// Ensure current view is present
		int cvLeft, cvRight, cvTop, cvBottom;
		boolean notPresent = (mChildViews.get(mCurrent) == null);
		cv = getOrCreateChild(mCurrent);
		if(cv == null) {
			return;
		}
		// When the view is sub-screen-size in either dimension we
		// offset it to center within the screen area, and to keep
		// the views spaced out
		cvOffset = subScreenSizeOffset(cv);
		if (notPresent) {
			//Main item not already present. Just place it top left
			cvLeft = cvOffset.x;
			cvTop  = cvOffset.y;
		} else {
			// Main item already present. Adjust by scroll offsets
			cvLeft = cv.getLeft() + mXScroll;
			cvTop  = cv.getTop()  + mYScroll;
		}
		// Scroll values have been accounted for
		mXScroll = mYScroll = 0;
		cvRight  = cvLeft + cv.getMeasuredWidth();
		cvBottom = cvTop  + cv.getMeasuredHeight();

		if (!mUserInteracting && mScroller.isFinished()) {
			Point corr = getCorrection(getScrollBounds(cvLeft, cvTop, cvRight, cvBottom));
			if(mLayoutType == LAYOUT_TYPE_FITSCREEN) {
				cvRight  += corr.x;
				cvLeft   += corr.x;
				cvTop    += corr.y;
				cvBottom += corr.y;
			} else if (mLayoutType == LAYOUT_TYPE_FITWIDTH) {
				cvRight  += corr.x;
				cvLeft   += corr.x;
			}
		} else if (mLayoutType == LAYOUT_TYPE_FITSCREEN) {
			if (cv.getMeasuredHeight() <= getHeight()) {
				// When the current view is as small as the screen in height, clamp
				// it vertically
				Point corr = getCorrection(getScrollBounds(cvLeft, cvTop, cvRight, cvBottom));
				cvTop    += corr.y;
				cvBottom += corr.y;
			} else if (cv.getMeasuredHeight() > getHeight()) {	//高度大于屏幕高度时，限制上下滚动的边界
				if(cvTop > 0) {
					cvTop = 0;
					cvBottom = cvTop + cv.getMeasuredHeight();
				} else if (cvBottom < getHeight()) {
					cvBottom = getHeight();
					cvTop = cvBottom - cv.getMeasuredHeight();
				}
			}
		} else if (mLayoutType == LAYOUT_TYPE_FITWIDTH) {	//垂直布局时，限制左右滚动的边界
			if(cv.getMeasuredWidth() <= getWidth()) {
				Point corr = getCorrection(getScrollBounds(cvLeft, cvTop, cvRight, cvBottom));
				cvLeft += corr.x;
				cvRight += corr.x;
			} else if (cv.getMeasuredWidth() > getWidth()) {
				if(cvLeft > 0) {
					cvLeft = 0;
					cvRight = cvLeft + cv.getMeasuredWidth();
				} else if (cvRight < getWidth()) {
					cvRight = getWidth();
					cvLeft = cvRight - cv.getMeasuredWidth();
				}
			}
		}
		
		if(mLayoutType == LAYOUT_TYPE_FITWIDTH) {
			if(mCurrent == 0) {	//垂直布局时,限制第一页和最后一页的边界
				if(cvTop > 0) {
					cvTop = 0;
					cvBottom = cvTop + cv.getMeasuredHeight();
				}
			}
			if (mCurrent == (mAdapter.getCount() - 1)) {
				if(cvBottom < getHeight()) {
					cvBottom = getHeight();
					cvTop = cvBottom - cv.getMeasuredHeight();
				}
			}
		}
		
//		printLog("cv--left : " + cvLeft + " right: " + cvRight + " top: " + cvTop + " bottom: " + cvBottom);
		cv.layout(cvLeft, cvTop, cvRight, cvBottom);

		if(mLayoutType == LAYOUT_TYPE_FITSCREEN) {
			if (mCurrent > 0) {
				View lv = getOrCreateChild(mCurrent - 1);
				Point leftOffset = subScreenSizeOffset(lv);
				int gap = leftOffset.x + GAP + cvOffset.x;
				lv.layout(cvLeft - lv.getMeasuredWidth() - gap,
						(cvBottom + cvTop - lv.getMeasuredHeight())/2,
						cvLeft - gap,
						(cvBottom + cvTop + lv.getMeasuredHeight())/2);
			}
			
			if (mCurrent + 1 < mAdapter.getCount()) {
				View rv = getOrCreateChild(mCurrent + 1);
				Point rightOffset = subScreenSizeOffset(rv);
				int gap = cvOffset.x + GAP + rightOffset.x;
				rv.layout(cvRight + gap,
						(cvBottom + cvTop - rv.getMeasuredHeight())/2,
						cvRight + rv.getMeasuredWidth() + gap,
						(cvBottom + cvTop + rv.getMeasuredHeight())/2);
			}
		} else if (mLayoutType == LAYOUT_TYPE_FITWIDTH) {
			if (mCurrent > 0) {
				View lv = getOrCreateChild(mCurrent - 1);
				lv.layout(cvLeft,
						cvTop - lv.getMeasuredHeight() - GAP,
						cvLeft + lv.getMeasuredWidth(),
						cvTop - GAP);
			}
			
			if (mCurrent + 1 < mAdapter.getCount()) {
				View rv = getOrCreateChild(mCurrent + 1);
				rv.layout(cvLeft,
						cvBottom + GAP,
						cvLeft + rv.getMeasuredWidth(),
						cvBottom + GAP + rv.getMeasuredHeight());
			}
		}
		
		invalidate();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		initVelocityTrackerIfNotExists();
		mVelocityTracker.addMovement(event);
		
		mScaleGestureDetector.onTouchEvent(event);
		
		if(!mScaling) {
			mGestureDetector.onTouchEvent(event);
		}
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mUserInteracting = true;
			mStartX = event.getX();
			mStartY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mScrollDisabled = false;
			mUserInteracting = false;
			
			View v = mChildViews.get(mCurrent);
			if (v != null) {
				if (mScroller.isFinished()) {
					// XXX
					// If, at the end of user interaction, there is no
					// current inertial scroll in operation then animate
					// the view onto screen if necessary
//					slideViewOntoScreen(v);
				}

				if (mScroller.isFinished()) {
					// If still there is no inertial scroll in operation
					// then the layout is stable
					postSettle(v);
				}
			}
			
			mLastX = event.getX();
			mLastY = event.getY();
			float length = PointF.length((mLastX - mStartX), (mLastY - mStartY));
			
			if(mVelocityTracker != null) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				float velocityY = velocityTracker.getYVelocity();
				float velocityX = velocityTracker.getXVelocity();
				if(mLayoutType == LAYOUT_TYPE_FITWIDTH) {//处理垂直布局的惯性滑动
					if(enableFling(velocityY) && Math.abs(length) > mTouchSlop) {
						
						int minY = velocityY < 0 ? -Integer.MAX_VALUE : 0;
						int maxY = velocityY < 0 ? 0 : Integer.MAX_VALUE;
						
						mScroller.fling(0, 0, 0, (int)velocityY,
								0, Integer.MAX_VALUE, minY, maxY);
						mXScroll = 0;
						mYScroll = 0;
						mScrollerLastX = 0;
						mScrollerLastY = 0;
						post(this);
					}
				} else if (mLayoutType == LAYOUT_TYPE_FITSCREEN) {	//处理水平布局左右滑动翻页
					float dX = mLastX - mStartX;
					float dY = mLastY - mStartY;
					PdfLog.d("左右翻页： dx : " + dX + " dY： " + dY + " mtouchSlop: " + mTouchSlop
							+ " velocityX: " + velocityX + " mMinVelocity: " + mMinimumVelocity) ;
					if(Math.abs(dX) > mTouchSlop && Math.abs(dX) > Math.abs(dY) * 2) {
						if(dX > 0 && velocityX > mMinimumVelocity) {	//翻上一页
							setDisplayedViewIndex(mCurrent - 1);
						} else if (dX < 0 && velocityX < -mMinimumVelocity) {	//翻下一页
							setDisplayedViewIndex(mCurrent + 1);
						}
					}
				}
			}
			
			releaseVelocityTracker();
			break;

		default:
			break;
		}
		
		return true;
	}
	
	public void fitWidth() {
		if(mLayoutType != LAYOUT_TYPE_FITWIDTH) {
			mLayoutType = LAYOUT_TYPE_FITWIDTH;
			requestLayout();
		}
	}
	
	public void fitScreen() {
		if(mLayoutType != LAYOUT_TYPE_FITSCREEN) {
			mLayoutType = LAYOUT_TYPE_FITSCREEN;
			requestLayout();
		}
	}
	
	protected void onSettle(View v) {
		if(v == null) {
			return;
		}
		if(v instanceof PdfPageView) {
			((PdfPageView) v).addHq();
		}
	}
	
	protected void onUnsettle(View v) {
		if(v == null) {
			return;
		}
		if(v instanceof PdfPageView) {
			((PdfPageView) v).removeHq();
		}
	}
	
	protected void onChildSetup(int i, View v) {
	}
	
	protected void onMoveToChild(int i) {
	}
}