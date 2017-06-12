package com.lectek.lereader.core.cartoon;

import java.util.LinkedList;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Scroller;

import com.lectek.lereader.core.cartoon.photoview.PhotoViewAttacher.OnViewTapListener;
import com.lectek.lereader.core.util.LogUtil;

public class HorPageView extends ViewGroup implements
		GestureDetector.OnGestureListener,
		ScaleGestureDetector.OnScaleGestureListener, Runnable, IPageView{

	/**
	 * 页面之间的间隙
	 */
	private int mPageMargin = 10;
	/**
	 * 最小缩放比例
	 */
	private float MIN_SCALE = 1.0f;
	/**
	 * 最大缩放比例
	 */
	private float MAX_SCALE = 5.0f;
	private float mScale = MIN_SCALE;

	private PagerAdapter mAdapter;
	/**
	 * Adapter's index for the current view
	 */
	protected int mCurrent;
	private boolean mResetLayout;
	/**
	 * Shadows the children of the adapter view but with more sensible indexing
	 */
	private final SparseArray<View> mChildViews = new SparseArray<View>(3);
	private final LinkedList<View> mViewCache = new LinkedList<View>();
	private boolean mUserInteracting; // Whether the user is interacting
	private boolean mScaling; // Whether the user is currently pinch zooming
	private int mXScroll; // Scroll amounts recorded from events.
	private int mYScroll; // and then accounted for in onLayout
	private GestureDetector mGestureDetector;
	private ScaleGestureDetector mScaleGestureDetector;
	private Scroller mScroller;
	private int mScrollerLastX;
	private int mScrollerLastY;
	private boolean mScrollDisabled;

	private int mMinimumVelocity;
	private int mMaximumVelocity;
	private int mTouchSlop;
	private float mStartX;
	private float mStartY;
	private float mLastX;
	private float mLastY;
	private VelocityTracker mVelocityTracker;

	public HorPageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public HorPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public HorPageView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mGestureDetector = new GestureDetector(this);
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		mScroller = new Scroller(context);
		mGestureDetector
				.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {

					@Override
					public boolean onSingleTapConfirmed(MotionEvent e) {
						if(mViewTapListener != null){
							int x = (int) e.getX();
							int y = (int) e.getY();
							mViewTapListener.onViewTap(HorPageView.this, x, y);
						}
						return false;
					
					}

					@Override
					public boolean onDoubleTapEvent(MotionEvent e) {
						return false;
					}

					@Override
					public boolean onDoubleTap(MotionEvent ev) {
						LogUtil.i("HorPageView", "onDoubleTap");
						float x = ev.getX();
						float y = ev.getY();
						if (mScale > MIN_SCALE) {
							post(new AnimatedZoomRunnable(mScale,MIN_SCALE, x, y));
						} else {
							post(new AnimatedZoomRunnable(mScale,MAX_SCALE, x, y));
						}
						return true;
					}
				});
		ViewConfiguration vc = ViewConfiguration.get(context);
		mTouchSlop = vc.getScaledTouchSlop();
		mMinimumVelocity = vc.getScaledMinimumFlingVelocity();
		mMaximumVelocity = vc.getScaledMaximumFlingVelocity();
	}
	
	private boolean mAutoScaling = false;
    private class AnimatedZoomRunnable implements Runnable {
    	private final int ZOOM_DURATION = 300; 
        private  int SIXTY_FPS_INTERVAL = 1000 / 60;
    	private  Interpolator sInterpolator = new LinearInterpolator();
        private  float mFocalX, mFocalY;
        private  long mStartTime;
        private  float mZoomStart, mZoomEnd;  

        public AnimatedZoomRunnable(float currentZoom,  float targetZoom,
                                     float focalX,  float focalY) {  
            mFocalX = focalX;
            mFocalY = focalY;
            mStartTime = System.currentTimeMillis();
            mZoomStart = currentZoom;
            mZoomEnd = targetZoom;
        } 
        @Override
        public void run() {   
            float t = interpolate();
            float scale = mZoomStart + t * (mZoomEnd - mZoomStart); 
            	scale(scale, mFocalX, mFocalY);
           
          
            if (t < 1f) {
            	mAutoScaling = true;
            	post(this); 
            }else{
            	mAutoScaling = false;
            	new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						resetParams(); 
		            	checkAndDisplayEdge();
					}
				}, 30);
            }
        } 
        private float interpolate() { 
            float t = 1f * (System.currentTimeMillis() - mStartTime) / ZOOM_DURATION;
            t = Math.min(1f, t);
            t = sInterpolator.getInterpolation(t);
            return t;
        }
    }

	public View getDisplayedView() {
		return mChildViews.get(mCurrent);
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
 

	private Rect getScrollBounds(int left, int top, int right, int bottom) {
		int xmin = getWidth() - right;
		int xmax = -left;
		int ymin = getHeight() - bottom;
		int ymax = -top;
		// In either dimension, if view smaller than screen then
		// constrain it to be central
		if (xmin > xmax)
			xmin = xmax = (xmin + xmax) / 2;
		if (ymin > ymax)
			ymin = ymax = (ymin + ymax) / 2;

		return new Rect(xmin, ymin, xmax, ymax);
	}

	private Rect getScrollBounds(View v) {
		// There can be scroll amounts not yet accounted for in
		// onLayout, so add mXScroll and mYScroll to the current
		// positions when calculating the bounds.
		return getScrollBounds(v.getLeft() + mXScroll, v.getTop() + mYScroll,
				v.getLeft() + v.getMeasuredWidth() + mXScroll,
				v.getTop() + v.getMeasuredHeight() + mYScroll);
	}

	private Point getCorrection(Rect bounds) {
		return new Point(Math.min(Math.max(0, bounds.left), bounds.right),
				Math.min(Math.max(0, bounds.top), bounds.bottom));
	}

	/**
	 * 判断是否满足fling的条件
	 * 
	 * @param velocityY
	 * @return
	 */
	private boolean enableFling(float velocityY) {
		if (Math.abs(velocityY) > mMinimumVelocity) {
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
 
	public void setAdapter(PagerAdapter adapter) {
		mAdapter = adapter;
		mChildViews.clear();
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public void run() {// 平移动画
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
	}

	public float getBitmapWidth() {
		return Integer.MIN_VALUE;
	}

	public float getBitmapHeight() {
		return Integer.MIN_VALUE;
	}
 
	private void scale(float scale, float x, float y){
		float previousScale = mScale;
		mScale = scale;
		float factor = mScale / previousScale;

		View v = mChildViews.get(mCurrent);
		if (v != null) {
			// Work out the focus point relative to the view top left
			int viewFocusX = (int) x
					- (v.getLeft() + mXScroll);
			int viewFocusY = (int) y
					- (v.getTop() + mYScroll);
			// Scroll to maintain the focus point
			float scrollX = viewFocusX - viewFocusX * factor;
			float scrollY = viewFocusY - viewFocusY * factor;
			mXScroll += scrollX;
			mYScroll += scrollY;
			requestLayout(); 
		}
	}
	
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		scale(Math.min(
				Math.max(mScale * detector.getScaleFactor(), MIN_SCALE),
				MAX_SCALE), detector.getFocusX(), detector.getFocusY());
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
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (!mScrollDisabled) {
			View cv = mChildViews.get(mCurrent);
			if (cv != null) {
				float off = mOffX * mScale / MIN_SCALE;
				if (distanceX < 0) {
					float dis = cv.getLeft() - off;
					if (dis >= 0 || dis > distanceX) {
						distanceX = dis;
					}
				} else if (distanceX > 0) {
					float dis = getResources().getDisplayMetrics().widthPixels
							- off - (cv.getLeft() + cv.getMeasuredWidth());
					if (dis >= 0 || Math.abs(dis) < distanceX) {
						distanceX = -dis;
					}
				}
				mXScroll -= distanceX;
				mYScroll -= distanceY;
				requestLayout();
			}
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
		if (v == null) {
			return;
		}
		// See what size the view wants to be
		v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		// Work out a scale that will fit it to this view
		float scale = (float) getWidth() / (float) v.getMeasuredWidth();
		// Use the fitting values scaled by our current scale factor
		v.measure(View.MeasureSpec.EXACTLY
				| (int) (v.getMeasuredWidth() * scale * mScale),
				View.MeasureSpec.EXACTLY
						| (int) (v.getMeasuredHeight() * scale * mScale));
	}

	private View getOrCreateChild(int i) {
		View v = mChildViews.get(i);
		if (v == null) {
			if (mAdapter == null) {
				return null;
			}
			//v = mAdapter.getView(i, getCached(), this);
			v = (View) mAdapter.instantiateItem(null, i);//(i, getCached(), this);
			addAndMeasureChild(i, v);
		}
		if (v != null) {
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
			params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
		}
		addViewInLayout(v, 0, params, true);
		mChildViews.append(i, v); // Record the view against it's adapter index
		measureView(v);
	}

	/**
	 * 显示指定页
	 * 
	 * @param i
	 */
	public void setDisplayedViewIndex(int i) {
		if (0 <= i && i < mAdapter.getCount()) {
			mCurrent = i;
			onMoveToChild(i);
			handCacheScale();
		}
	}

	private void handCacheScale() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (!resetCurrentScale()) {
					handCacheScale();
				}else{
					reLayoutCount = 0;
				}
				mResetLayout = true;
				requestLayout();
			}
		}, 100);
	}

	private int reLayoutCount = 0;
	private int mOffX = Integer.MAX_VALUE;
	
	/**
	 * 设置最小缩放尺寸
	 * 
	 * @param scale
	 */
	public boolean resetCurrentScale() {
		if (getBitmapWidth() == Integer.MIN_VALUE
				&& getBitmapHeight() == Integer.MIN_VALUE) {
			return true;
		}
		if (mOffX == Integer.MAX_VALUE && getBitmapWidth() > 0
				&& getBitmapHeight() > 0) {
			float screenWidth = getResources().getDisplayMetrics().widthPixels;
			float screenHeight = getBottom();
			/*if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				float temp = screenHeight;
				screenHeight = screenWidth;
				screenWidth = temp;
			}*/
			float scale = screenWidth
					/ (screenHeight * getBitmapWidth() / getBitmapHeight());
			mOffX = (int) ((screenWidth - scale * screenWidth) / 2); 
			MIN_SCALE = mScale = scale;
			MAX_SCALE = mScale * 2;
			return true;
		}
		return reLayoutCount++ > 5;
	}
 
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) { 
		View cv = mChildViews.get(mCurrent); 
		if (!mResetLayout) {
			if (cv != null) {
				if (cv.getBottom() + mPageMargin / 2 + mYScroll < getHeight() / 2
						&& mCurrent + 1 < mAdapter.getCount()) {
					mCurrent++;
					onMoveToChild(mCurrent);
				}
				if (cv.getTop() - mPageMargin / 2 + mYScroll >= getHeight() / 2
						&& mCurrent > 0) {
					mCurrent--;
					onMoveToChild(mCurrent);
				}
			}

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
			mYScroll = 0; 
			mXScroll = (int) (mOffX * mScale / MIN_SCALE);
			// Remove all children and hold them for reuse
			int numChildren = mChildViews.size();
			for (int i = 0; i < numChildren; i++) {
				View v = mChildViews.valueAt(i);
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
		if (cv == null) {
			return;
		}

		if (notPresent) { 
			// Main item not already present. Just place it top left
			cvLeft = mXScroll;
			cvTop =  mYScroll;
		} else {
			// Main item already present. Adjust by scroll offsets
			cvLeft = cv.getLeft() + mXScroll;
			cvTop = cv.getTop() + mYScroll;
		}
		// Scroll values have been accounted for
		mXScroll = mYScroll = 0;
		cvRight = cvLeft + cv.getMeasuredWidth();
		cvBottom = cvTop + cv.getMeasuredHeight();

		if (!mUserInteracting && mScroller.isFinished()) {
			Point corr = getCorrection(getScrollBounds(cvLeft, cvTop, cvRight,
					cvBottom));
			cvRight += corr.x;
			cvLeft += corr.x;
		} else { // 垂直布局时，限制左右滚动的边界
			if (cv.getMeasuredWidth() <= getWidth()) {
				Point corr = getCorrection(getScrollBounds(cvLeft, cvTop,
						cvRight, cvBottom));
				cvLeft += corr.x;
				cvRight += corr.x;
			} else if (cv.getMeasuredWidth() > getWidth()) {
				if (cvLeft > 0) {
					cvLeft = 0;
					cvRight = cvLeft + cv.getMeasuredWidth();
				} else if (cvRight < getWidth()) {
					cvRight = getWidth();
					cvLeft = cvRight - cv.getMeasuredWidth();
				}
			}
		}

		if (mCurrent == 0) { // 垂直布局时,限制第一页和最后一页的边界
			if (cvTop > 0) {
				cvTop = 0;
				cvBottom = cvTop + cv.getMeasuredHeight();
			}
		}
		if (mCurrent == (mAdapter.getCount() - 1)) {
			if (cvBottom < getHeight()) {
				cvBottom = getHeight();
				cvTop = cvBottom - cv.getMeasuredHeight();
			}
		}

		cv.layout(cvLeft, cvTop, cvRight, cvBottom);

		if (mCurrent > 0) {
			View lv = getOrCreateChild(mCurrent - 1);
			lv.layout(cvLeft, cvTop - lv.getMeasuredHeight() - mPageMargin,
					cvLeft + lv.getMeasuredWidth(), cvTop - mPageMargin);
		}

		if (mCurrent + 1 < mAdapter.getCount()) {
			View rv = getOrCreateChild(mCurrent + 1);
			rv.layout(cvLeft, cvBottom + mPageMargin, cvLeft + rv.getMeasuredWidth(),
					cvBottom + mPageMargin + rv.getMeasuredHeight());
		}
		invalidate(); 
	}

	private void resetParams(){
		mXScroll = 0;
		mYScroll = 0;
		mScrollerLastX = 0;
		mScrollerLastY = 0;
	}
	
	private boolean checkAndDisplayEdge(){
		View v = mChildViews.get(mCurrent);
		if(v != null){ 
			if (mScroller.isFinished()) {  
				float off = mOffX * mScale / MIN_SCALE;
				float dis = v.getLeft() - off; 
				if (dis > 0) {
					mScroller.startScroll(0, 0, (int) -dis, 0);
					post(this);
					return true;
				} else {
					dis = getResources().getDisplayMetrics().widthPixels - off
							- (v.getLeft() + v.getMeasuredWidth()); 
					if (dis > 0) {
						mScroller.startScroll(0, 0, (int) dis, 0);
						post(this);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		initVelocityTrackerIfNotExists();
		mVelocityTracker.addMovement(event);

		//if (!mScaling) {
			mGestureDetector.onTouchEvent(event);
	//	}

		mScaleGestureDetector.onTouchEvent(event);
 
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(!mScroller.isFinished()) { 
				mScroller.abortAnimation();
			}
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
			mScaling = false;
			boolean scaleConsumed = false;
			if (!mAutoScaling) {
				scaleConsumed = checkAndDisplayEdge();
			}

			if (mVelocityTracker != null && !scaleConsumed) { 
				mLastX = event.getX();
				mLastY = event.getY();
				float length = PointF
						.length((mLastX - mStartX), (mLastY - mStartY));
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				float velocityY = velocityTracker.getYVelocity();
				float velocityX = velocityTracker.getXVelocity();
				if ((enableFling(velocityY) || enableFling(velocityX)) && Math.abs(length) > mTouchSlop) {

					int off =  (int) (mOffX * mScale / MIN_SCALE) ;
					View v1 = mChildViews.get(mCurrent);
					int minX = 0,maxX = 0;
					if (v1 != null) {
						if (velocityX < 0) {
							maxX = 0;
							minX = v1.getLeft()
									+ (v1.getMeasuredWidth() + off - getResources()
											.getDisplayMetrics().widthPixels);
							minX = -Math.abs(minX);
						} else {
							minX = 0;
							maxX = v1.getLeft() - off;
							maxX = Math.abs(maxX);
						} 
					}
					
					int minY = velocityY < 0 ? -Integer.MAX_VALUE : 0;
					int maxY = velocityY < 0 ? 0 : Integer.MAX_VALUE;

					resetParams(); 
					mScroller.fling(0, 0, (int) velocityX, (int) velocityY, minX, maxX, minY, maxY);  
					post(this);
				}

			}

			releaseVelocityTracker();
			break;

		default:
			break;
		}

		return true;
	}

	protected void onChildSetup(int i, View v) {
	}

	protected void onMoveToChild(int i) {
		if(mPageChangeListener != null){
			mPageChangeListener.onPageSelected(i);
		}
	}

	@Override
	public void moveToNext() {
		if (getBitmapHeight() != Integer.MIN_VALUE) {
			mScrollerLastX = mScrollerLastY = 0;
			mScroller.startScroll(0, 0, 0,
					-getBottom(), 400);
			post(this);
			return;
		}
		View v = mChildViews.get(mCurrent + 1);
		if (v != null)
			slideViewOntoScreen(v);
	}

	@Override
	public void moveToPrevious() {
		if (getBitmapHeight() != Integer.MIN_VALUE) {
			mScrollerLastX = mScrollerLastY = 0;
			mScroller.startScroll(0, 0, 0,
					getBottom(), 400);
			post(this);
			return;
		}
		View v = mChildViews.get(mCurrent - 1);
		if (v != null)
			slideViewOntoScreen(v);
	}

	/**
	 * 获取当前是第几页
	 * 
	 * @return
	 */
	@Override
	public int getCurrentItem() {
		return mCurrent;
	}

	@Override
	public void setCurrentItem(int position) {
		setDisplayedViewIndex(position);
	}
	
	@Override
	public void setPageMargin(int margin) {
		mPageMargin = margin;
	}

	private OnPageChangeListener mPageChangeListener;
	private OnViewTapListener mViewTapListener;
	@Override
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		mPageChangeListener = listener;
	}
 
    public IPageView setOnViewTapListener(OnViewTapListener listener) {
    	mViewTapListener = listener;
    	return this;
    }

	@Override
	public boolean isPhotoView() {
		return false;
	}
}