package com.lectek.android.lereader.widgets;


import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

/**
 * 上拉下拉基类
 * @param <T> 需要添加view
 */
public abstract class PullToRefreshBase<T extends View> extends LinearLayout {

	final class SmoothScrollRunnable implements Runnable {

		static final int ANIMATION_DURATION_MS = 190;
		static final int ANIMATION_FPS = 1000 / 60;

		private final Interpolator interpolator;
		private final int scrollToY;
		private final int scrollFromY;
		private final Handler handler;

		private boolean continueRunning = true;
		private long startTime = -1;
		private int currentY = -1;

		public SmoothScrollRunnable(Handler handler, int fromY, int toY) {
			this.handler = handler;
			this.scrollFromY = fromY;
			this.scrollToY = toY;
			this.interpolator = new AccelerateDecelerateInterpolator();
		}

		@Override
		public void run() {

			if (startTime == -1) {
				startTime = System.currentTimeMillis();
			} else {
				long normalizedTime = (1000 * (System.currentTimeMillis() - startTime)) / ANIMATION_DURATION_MS;
				normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

				final int deltaY = Math.round((scrollFromY - scrollToY) * interpolator.getInterpolation(normalizedTime / 1000f));
				this.currentY = scrollFromY - deltaY;
				setHeaderScroll(currentY);
			}

			if (continueRunning && scrollToY != currentY) {
				handler.postDelayed(this, ANIMATION_FPS);
			}
		}

		public void stop() {
			this.continueRunning = false;
			this.handler.removeCallbacks(this);
		}
	};

	private static final float FRICTION = 5.0f;
	private static final int PULL_TO_REFRESH = 0x0;
	private static final int RELEASE_TO_REFRESH = 0x1;
	private static final int REFRESHING = 0x2;
	private static final int MANUAL_REFRESHING = 0x3;
	
	public static final int MODE_PULL_DOWN_TO_REFRESH = 0x1;
	public static final int MODE_PULL_UP_TO_REFRESH = 0x2;
	public static final int MODE_BOTH = 0x3;

	private int touchSlop;
	private float initialMotionY;
	private float lastMotionX;
	private float lastMotionY;
	private boolean isBeingDragged = false;

	private int state = PULL_TO_REFRESH;
	private int mode = MODE_BOTH;
	private int currentMode;

	public T refreshableView;
	
	private boolean disableScrollingWhileRefreshing = true;
	private boolean isPullToRefreshEnabled = true;
	private boolean isPullUpRefreshEnabled = true;
	private boolean isPullDownRefreshEnabled = true;
	
	private LoadingLayout headerLayout;
	private LoadingLayout footerLayout;
	private int headerHeight;

	private final Handler handler = new Handler();

	private SmoothScrollRunnable currentSmoothScrollRunnable;

	public PullToRefreshBase(Context context) {
		super(context);
		init(context);
	}
	
	public PullToRefreshBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context) {

		setOrientation(LinearLayout.VERTICAL);

		//最小能够滑动的检测
		touchSlop = ViewConfiguration.getTouchSlop();

		//滑动的内容view
		refreshableView = this.createRefreshableView(context);
		
		//添加内容view
		this.addRefreshableView(context, refreshableView);

		//添加上加载Views
		if (mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH) {
			headerLayout = new LoadingLayout(context, MODE_PULL_DOWN_TO_REFRESH);
			addView(headerLayout, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			headerHeight = headerLayout.getMeasuredHeight();
		}
		
		//添加下加载Views
		if (mode == MODE_PULL_UP_TO_REFRESH || mode == MODE_BOTH) {
			footerLayout = new LoadingLayout(context, MODE_PULL_UP_TO_REFRESH);
			addView(footerLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			headerHeight = footerLayout.getMeasuredHeight();
		}

		//加载Views的状态设置(0：上下都隐藏 1：上显示，下隐藏 2：上隐藏，下显示)
		switch (mode) {
			case MODE_BOTH:
				setPadding(0, -headerHeight, 0, -headerHeight);
				break;
			case MODE_PULL_UP_TO_REFRESH:
				setPadding(0, 0, 0, -headerHeight);
				break;
			case MODE_PULL_DOWN_TO_REFRESH:
			default:
				setPadding(0, -headerHeight, 0, 0);
				break;
		}

		if (mode != MODE_BOTH) {
			currentMode = mode;
		}
	}
	
	protected abstract T createRefreshableView(Context context);
	protected abstract boolean isReadyForPullDown();
	protected abstract boolean isReadyForPullUp();
	protected abstract void onRefresh(int orientation);
	
	public final T getRefreshableView() {
		return refreshableView;
	}
	
	protected void addRefreshableView(Context context, T refreshableView) {
		addView(refreshableView, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 1.0f));
	}

	/**回复原来状态*/
	public final void onRefreshComplete() {
		if (state != PULL_TO_REFRESH) {
			resetHeader();
		}
	}

	public final boolean isRefreshing() {
		return state == REFRESHING || state == MANUAL_REFRESHING;
	}

	/**
	 * 设置上下拉动的开关
	 * @param enable ture：可以上下拉 false：不允许上下拉（默认情况可以上下拉）
	 */
	public final void setPullToRefreshEnabled(boolean enable) {
		this.isPullToRefreshEnabled = enable;
	}

	/**
	 * 设置向上滑动的开关
	 * @param enable
	 */
	public final void setPullUpRefreshEnabled(boolean enable){
		this.isPullUpRefreshEnabled = enable;
	}
	
	/**
	 * 设置向下滑动的开关
	 * @param enable
	 */
	public final void setPullDownRefreshEnabled(boolean enable){
		this.isPullDownRefreshEnabled = enable;
	}
	
	/**
	 * 设置加载部分各个状态显示语
	 * @param releaseLabel 上
	 * @param pullLabel 上
	 * @param refreshingLabel 上
	 * @param firstNameLabel 上
	 * @param downReleaseLabel 下
	 * @param downPullLabel 下
	 * @param downRefreshingLabel 下
	 * @param downFirstNameLabel 下
	 */
	public void setLoadingLayoutTip(String pullLabel, String firstNameLabel, String downPullLabel, String downFirstNameLabel){
		if (null != headerLayout) {
//			headerLayout.setReleaseLabel(releaseLabel);
			headerLayout.setPullLabel(pullLabel);
//			headerLayout.setRefreshingLabel(refreshingLabel);
			headerLayout.setFirstNameLabel(firstNameLabel);
		}
		
		if (null != footerLayout) {
//			footerLayout.setReleaseLabel(downReleaseLabel);
			footerLayout.setPullLabel(downPullLabel);
//			footerLayout.setRefreshingLabel(downRefreshingLabel);
			footerLayout.setFirstNameLabel(downFirstNameLabel);
		}
	}

	@Override
	public final boolean onTouchEvent(MotionEvent event) {
		if (!isPullToRefreshEnabled || state == REFRESHING) {
			return false;
		}

//		if (isRefreshing() && disableScrollingWhileRefreshing) {
//			return true;
//		}

		if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
			return false;
		}

		switch (event.getAction()) {

			case MotionEvent.ACTION_MOVE: {
				if (isBeingDragged) {
					lastMotionY = event.getY();
					this.pullEvent();
					return true;
				}
				break;
			}

			case MotionEvent.ACTION_DOWN: {
				if (isReadyForPull()) {
					lastMotionY = initialMotionY = event.getY();
					return true;
				}
				break;
			}

			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP: {
				if (isBeingDragged) {
					isBeingDragged = false;
					if (state == RELEASE_TO_REFRESH) {
						setRefreshingInternal(true);
						onRefresh(currentMode);
					} else if(state == PULL_TO_REFRESH){
						smoothScrollTo(0);
					}
					return true;
				}
				break;
			}
		}

		return false;
	}

	@Override
	public final boolean onInterceptTouchEvent(MotionEvent event) {

		if (!isPullToRefreshEnabled || state == REFRESHING) {
			return false;
		}

//		if (isRefreshing() && disableScrollingWhileRefreshing) {
//			return true;
//		}

		final int action = event.getAction();

		//action-up或cancel的时候设置拉动状态：未拉动
		if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
			isBeingDragged = false;
			return false;
		}

		if (action != MotionEvent.ACTION_DOWN && isBeingDragged) {
			return true;
		}

		switch (action) {
			case MotionEvent.ACTION_MOVE: {
				if (isReadyForPull()) {

					//计算偏移量
					final float y = event.getY();
					final float dy = y - lastMotionY;
					final float x = event.getX();
					final float dx = x - lastMotionX;
					final float yDiff = Math.abs(dy);
					final float xDiff = Math.abs(dx);

					if (yDiff > touchSlop && yDiff > xDiff) {
						if ((mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH) 
								&& dy >= 0.0001f
								&& isReadyForPullDown()) {
							lastMotionY = y;
							isBeingDragged = true;
							if (mode == MODE_BOTH) {
								currentMode = MODE_PULL_DOWN_TO_REFRESH;
							}
						} else if ((mode == MODE_PULL_UP_TO_REFRESH || mode == MODE_BOTH) 
								&& dy <= 0.0001f
								&& isReadyForPullUp()) {
							lastMotionY = y;
							isBeingDragged = true;
							if (mode == MODE_BOTH) {
								currentMode = MODE_PULL_UP_TO_REFRESH;
							}
						}
					}
				}
				break;
			}
			case MotionEvent.ACTION_DOWN: {
				if (isReadyForPull()) {
					lastMotionY = initialMotionY = event.getY();
					lastMotionX = event.getX();
					isBeingDragged = false;
				}
				break;
			}
		}

		return isBeingDragged;
	}

	private boolean pullEvent() {

		int newHeight;
		final int oldHeight = this.getScrollY();

		switch (currentMode) {
			case MODE_PULL_UP_TO_REFRESH:
				newHeight = Math.round(Math.max(initialMotionY - lastMotionY, 0) / FRICTION);
				break;
			case MODE_PULL_DOWN_TO_REFRESH:
			default:
				newHeight = Math.round(Math.min(initialMotionY - lastMotionY, 0) / FRICTION);
				break;
		}
		
		if(state == REFRESHING && currentMode == MODE_PULL_DOWN_TO_REFRESH) {	//刷新状态下拉时
			if(newHeight <= -headerHeight) {
				newHeight = -headerHeight;
			}
		}

		setHeaderScroll(newHeight);

		if (newHeight != 0) {
			if (state == PULL_TO_REFRESH && headerHeight < Math.abs(newHeight)) {	//下拉刷新 --> 释放刷新
				state = RELEASE_TO_REFRESH;

				switch (currentMode) {
					case MODE_PULL_UP_TO_REFRESH:
						footerLayout.releaseToRefresh();
						break;
					case MODE_PULL_DOWN_TO_REFRESH:
						headerLayout.releaseToRefresh();
						break;
				}
				return true;

			} else if (state == RELEASE_TO_REFRESH && headerHeight >= Math.abs(newHeight)) {	//释放刷新 --> 下拉刷新
				state = PULL_TO_REFRESH;

				switch (currentMode) {
					case MODE_PULL_UP_TO_REFRESH:
						footerLayout.pullToRefresh();
						break;
					case MODE_PULL_DOWN_TO_REFRESH:
						headerLayout.pullToRefresh();
						break;
				}
				return true;
			}
		}
		return oldHeight != newHeight;
	}
	
	protected void resetHeader() {
		state = PULL_TO_REFRESH;
		isBeingDragged = false;

		if (null != headerLayout) {
			headerLayout.reset();
		}
		if (null != footerLayout) {
			footerLayout.reset();
		}
		smoothScrollTo(0);
	}

	protected void setRefreshingInternal(boolean doScroll) {
		state = REFRESHING;

//		if (null != headerLayout) {
//			headerLayout.refreshing();
//		}
		
		if (null != footerLayout) {
			footerLayout.refreshing();
		}

		if (doScroll) {
//			smoothScrollTo(currentMode == MODE_PULL_DOWN_TO_REFRESH ? -headerHeight : headerHeight);
//			smoothScrollTo(0);
			scrollTo(0, 0);
		}
	}

	protected final void setHeaderScroll(int y) {
		scrollTo(0, y);
	}

	protected final void smoothScrollTo(int y) {
		if (null != currentSmoothScrollRunnable) {
			currentSmoothScrollRunnable.stop();
		}

		if (this.getScrollY() != y) {
			this.currentSmoothScrollRunnable = new SmoothScrollRunnable(handler, getScrollY(), y);
			handler.post(currentSmoothScrollRunnable);
		}
	}

	private boolean isReadyForPull() {
		switch (mode) {
			case MODE_PULL_DOWN_TO_REFRESH:
				return isReadyForPullDown() && isPullDownRefreshEnabled;
			case MODE_PULL_UP_TO_REFRESH:
				return isReadyForPullUp() && isPullUpRefreshEnabled;
			case MODE_BOTH:
				return (isReadyForPullUp() && isPullUpRefreshEnabled)
						|| (isReadyForPullDown() && isPullDownRefreshEnabled);
		}
		return false;
	}
}
