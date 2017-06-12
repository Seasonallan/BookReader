package com.lectek.android.lereader.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.widgets.drag.DragController;

/**
 * 顶部可埋藏布局的FrameLayout,向下可拉出隐藏的布局，如果子view包含可滚动的控件需实现OverBoundDetector接口
 * 
 * @author chends@lectek.com 2013-06-20
 */
public class GapView extends FrameLayout {

	private static final int SCROLL_IDLE = 0;
	private static final int SCROLL_LEFT = SCROLL_IDLE + 1;
	private static final int SCROLL_RIGHT = SCROLL_LEFT + 1;
	public static final int SCROLL_UP = SCROLL_RIGHT + 1;
	public static final int SCROLL_DOWN = SCROLL_UP + 1;

	private static final int OVER_NONE = 0;
	private static final int OVER_TOP = OVER_NONE + 1;
	private static final int OVER_BOTTOM = OVER_TOP + 1;
	private static final int OVER_BOTH = OVER_BOTTOM + 1;

	public enum GapState {
		SHOW_HEAD, SHOW_FOOTER, HIDE
	}

	public enum GapType {
		HEAD, FOOTER, BOTH, NONE
	}

	public OnGapStateChangedListener mGapStateChangedListener;

	// private static final float FACTOR = 0.2f;

	private boolean mMeasureAll = false;
	private View mIconView;
	private View mHeaderView;
	private View mFooterView;
	private View mContentView;

	private Rect mTmpRect = new Rect();

	private float mDownXfloat;
	private float mDownYfloat;
	private float mLastYfloat;
 
	private int mIconHeight = 0;
	private int mHeaderHeight = 0;
	private int mFooterHeight = 0;
	private int mScrollDirection = -1;
	private int mOverType = OVER_NONE;

	private Scroller mScroller;
	private GapState mGapState = GapState.SHOW_HEAD; // 初始为true是第一次点击时切换为false;

	private Paint mPaint = new Paint();
	private boolean mDrawing = false;
	private boolean mAutoDrawing = false;
	private boolean mReBuildSnapShot = false;

	private Bitmap mSnapshot;

	private GapType mEnableGap = GapType.NONE;
	private boolean mEnableSnapshot = false;

	private boolean mScrolling;

	private boolean mFixGapHeight;

	public GapView(Context context) {
		super(context);
		initState();
	}

	public GapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScroller = new Scroller(context);
		initState();
	}

	public GapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initState();
	}

	private void initState() {
		enableGap(GapType.HEAD);
		setGapState(GapState.HIDE);
		setFixGapHeight(false);
	}

	private View getContentView() {
		if (mContentView == null) {
			mContentView = findViewById(R.id.contentView);
		}
		return mContentView;
	}

	private View getIconView() {
		if (mIconView == null) {
			mIconView = findViewById(R.id.iconView);
		}
		return mIconView;
	}
	
	private View getHeadView() {
		if (mHeaderView == null) {
			mHeaderView = findViewById(R.id.headView);
		}
		return mHeaderView;
	} 

	public void clearFastDrawCache() {
		destroySnapShot();
	}

	/**
	 * 設置滑動上下限是否開啟
	 * @param value
	 */
	public void setFixGapHeight(boolean value) {
		mFixGapHeight = value;
	}

	/**
	 * 设置内容视图
	 * 
	 * @param view
	 */
	public void addContentView(View view) {
		if (view == null) {
			return;
		}
		if (getContentView() != null) {
			removeView(getContentView());
		}
		mContentView = view;
		addView(view);

	}

	/**
	 * 设置顶部视图
	 * 
	 * @param view
	 */
	public void addHeader(View view) {
		if (view == null) {
			return;
		}
		if (getHeadView() != null) {
			removeView(getHeadView());
		}
		mHeaderView = view;
		addView(view, new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
	}

	/**
	 * 设置底部视图
	 * 
	 * @param view
	 */
	public void addFooter(View view) {
		if (view == null) {
			return;
		}

		if (mFooterView != null) {
			removeView(mFooterView);
		}
		mFooterView = view;
		addView(view, new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
	}

	public GapState getGapState() {
		return mGapState;
	}

	/**
	 * 設置顯示狀態，為顯示或隱藏
	 * @param state
	 */
	public void setGapState(GapState state) {
		mGapState = state;
	}

	/**
	 * 設置滑動類型，頂部、頂部、或無
	 * @param gap
	 */
	public void enableGap(GapType gap) {
		mEnableGap = gap;
	}

	public boolean isEnableGap() {
		return !GapType.NONE.equals(mEnableGap);
	}

	/**
	 * 是否允许使用快照
	 * @param value
	 */
	public void setEnableSnapshot(boolean value) {
		mEnableSnapshot = value;
	}

	public void hideGap(boolean needAnim) {
		if (!mScroller.isFinished()) {
			mScroller.abortAnimation();
		}
		mGapState = GapState.HIDE;
		if (!needAnim) {
			mDrawing = false;
			mAutoDrawing = false;

			if (mGapStateChangedListener != null) {
				post(new Runnable() {

					@Override
					public void run() {
						mGapStateChangedListener.onGapHided();
					}
				});
			}

		} else {
			mAutoDrawing = true;
			mDrawing = false;

			mScroller.startScroll(getScrollX(), getScrollY(), getScrollX(),
					0 - getScrollY(), 1000);
			invalidate();
		}
	}

	public boolean isGapShow() {
		return !mGapState.equals(GapState.HIDE);
	}

	/**
	 * 检测是否到达顶部
	 *
	 */
	private void checkOver() {
		if (mOverType != OVER_NONE) {
			return;
		}
		if (getContentView() != null) {
			if (mContentView instanceof OverBoundDetector) {
				if (((OverBoundDetector) mContentView).overTop()) {
					mOverType = OVER_TOP;
				}
				if (((OverBoundDetector) mContentView).overBottom()) {
					if (mOverType == OVER_TOP) {
						mOverType = OVER_BOTH;
					} else {
						mOverType = OVER_BOTTOM;
					}
				}
			}
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		if ((getHeadView() == null && mFooterView == null)
				|| GapType.NONE.equals(mEnableGap) || DragController.getInstance().isDragWorking()) {
			return false;
		}

		boolean result = false;

		float xf = ev.getX();
		float yf = ev.getY();

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:

			mDownXfloat = xf;
			mDownYfloat = yf;

			mOverType = OVER_NONE;
			mScrolling = false;
			mScrollDirection = SCROLL_IDLE;

			checkOver();
			break;

		case MotionEvent.ACTION_MOVE:

			// checkOver(ev);
			float deltaY = yf - mDownYfloat;
			float deltaX = xf - mDownXfloat;

			float absDeltaY = Math.abs(deltaY);

			if (absDeltaY > ViewConfiguration.getTouchSlop()
					&& absDeltaY - Math.abs(deltaX) > 0) {
				mScrollDirection = deltaY > 0 ? SCROLL_DOWN : SCROLL_UP;
			}

			if (mScrolling
					|| mOverType == OVER_BOTH
					|| (mOverType == OVER_TOP && mScrollDirection == SCROLL_DOWN)
					|| (mOverType == OVER_BOTTOM && mScrollDirection == SCROLL_UP)
					|| (GapState.SHOW_HEAD.equals(mGapState) && mScrollDirection == SCROLL_UP)
					|| (GapState.SHOW_FOOTER.equals(mGapState) && mScrollDirection == SCROLL_DOWN)) {

				if (!mScrolling) {

					ev.setAction(MotionEvent.ACTION_DOWN);
					ev.setLocation(mDownXfloat, mDownYfloat);
					onTouchEvent(ev);

					ev.setAction(MotionEvent.ACTION_MOVE);
					ev.setLocation(xf, yf);

					if (mGapStateChangedListener != null
							&& mGapState.equals(GapState.HIDE)) {
						mGapStateChangedListener.beforeShow(mScrollDirection);
					}

					mScrolling = true;
				}
				result = true;
			}

			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:

			if (mScrolling) {
				onTouchEvent(ev);
			}
			mOverType = OVER_NONE;
			mScrolling = false;
			break;
		}

		return result;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mEnableGap.equals(GapType.NONE)) {
			return false;
		}
		float yf = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			buildSnapShot();
			mDrawing = true;

			break;
		case MotionEvent.ACTION_MOVE:

			float scrollSize = 0;
			if (mScrollDirection == SCROLL_DOWN
					|| mScrollDirection == SCROLL_UP) {
				scrollSize = (mScrollDirection == SCROLL_LEFT || mScrollDirection == SCROLL_RIGHT) ? 0
						: yf - mLastYfloat;
			}

			if ((GapState.SHOW_HEAD.equals(mGapState)
					&& mScrollDirection == SCROLL_UP && getScrollY() > 0)
					|| (GapState.SHOW_FOOTER.equals(mGapState)
							&& mScrollDirection == SCROLL_DOWN && getScrollY() < 0)) {
				break;
			}

			if (mFixGapHeight) {

				int maxHeight = mHeaderHeight * 2;

				if ((mScrollDirection == SCROLL_DOWN
						&& (GapType.HEAD.equals(mEnableGap) || GapType.BOTH
								.equals(mEnableGap)) && (getScrollY() - (int) scrollSize) <= -maxHeight)) {
					scrollTo(0, -maxHeight - 2);
					break;
				} else if (mScrollDirection == SCROLL_UP
						&& (GapType.FOOTER.equals(mEnableGap) || GapType.BOTH
								.equals(mEnableGap))
						&& (getScrollY() - (int) scrollSize) >= maxHeight) {
					scrollTo(0, maxHeight + 2);
					break;
				}
			}

			scrollBy(0, -(int) (scrollSize/2));

			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:

			int distanceX = 0;
			int distanceY = 0;

			int height = Integer.MAX_VALUE;
			if (GapState.SHOW_HEAD.equals(mGapState)) {
				height = mHeaderHeight;
			} else if (GapState.SHOW_FOOTER.equals(mGapState)) {
				height = mFooterHeight;
			}

			float deltaY = Math.abs(getScrollY() - mDownYfloat);

			if (deltaY > height) {
				distanceY = -getScrollY();
				mGapState = GapState.HIDE;
			} else {

				if (height == Integer.MAX_VALUE) {

					if (mScrollDirection == SCROLL_DOWN
							&& getScrollY() < -mHeaderHeight) {
						distanceY = -mHeaderHeight - getScrollY();
						mGapState = GapState.SHOW_HEAD;
					} else if (mScrollDirection == SCROLL_UP
							&& getScrollY() > mFooterHeight) {
						distanceY = mFooterHeight - getScrollY();
						mGapState = GapState.SHOW_FOOTER;
					} else {
						mGapState = GapState.HIDE;
						distanceY = -getScrollY();
					}

				} else {
					distanceY = mGapState.equals(GapState.SHOW_HEAD) ? -mHeaderHeight
							- getScrollY()
							: mFooterHeight - getScrollY();
				}
			}

			mScroller.startScroll(getScrollX(), getScrollY(), distanceX,
					distanceY);
			mAutoDrawing = true;

			mOverType = OVER_NONE;
			mScrolling = false;

			invalidate();
		}

		mLastYfloat = yf;

		return true;
	}

	/**
	 * 用以移动画布以绘制超出边界的视图，左上右
	 */
	private void buildSnapShot() {
		if (mSnapshot != null && !mSnapshot.isRecycled()) {
			return;
		}

		int height = getMeasuredHeight();
		height += mHeaderHeight;
		height += mIconHeight;
		height += mFooterHeight; 

		mSnapshot = Bitmap.createBitmap(getMeasuredWidth(), height,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(mSnapshot);
		canvas.translate(0, mHeaderHeight);
		canvas.translate(0, mIconHeight); 

		Drawable drawable = getBackground();
		if (drawable != null) {
			drawable.draw(canvas);
		}

		dispatchDraw(canvas);
	}

	private void rebuildSnapShot() {
		mReBuildSnapShot = true;
		destroySnapShot();
		buildSnapShot();
		mReBuildSnapShot = false;
	}

	private void destroySnapShot() {
		if (mSnapshot != null && !mSnapshot.isRecycled()) {
			mSnapshot.recycle();
			mSnapshot = null;
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (!mEnableSnapshot || !mDrawing || mSnapshot == null
				|| mSnapshot.isRecycled() || mReBuildSnapShot) {
			super.dispatchDraw(canvas);
			return;
		} else {
			canvas.save();
			canvas.translate(0, -mHeaderHeight);
			canvas.translate(0, -mIconHeight); 
			canvas.drawBitmap(mSnapshot, 0, 0, mPaint);
			canvas.restore();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		destroySnapShot();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
			// LogUtil.e("GapView.computeScroll", "scroll Y:" +
			// String.valueOf(mScroller.getCurrY()));
		} else if (mAutoDrawing) {
			mDrawing = false;
			mAutoDrawing = false;
			// Log.e("GapView", "finish drawing...");

			rebuildSnapShot();

			if (mGapStateChangedListener != null) {

				post(new Runnable() {

					@Override
					public void run() {
						if (mGapState.equals(GapState.HIDE)) {
							mGapStateChangedListener.onGapHided();
						} else {
							mGapStateChangedListener.onGapShowed();
						}
					}
				});

			}
		}
	}

	@Override
	public void setMeasureAllChildren(boolean measureAll) {
		super.setMeasureAllChildren(measureAll);
		mMeasureAll = measureAll;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		final int count = getChildCount();

		final Drawable drawable = getForeground();
		int foregroundPaddingLeft = 0;
		int foregroundPaddingTop = 0;
		int foregroundPaddingRight = 0;
		int foregroundPaddingBottom = 0;
		
		if (drawable != null) {
			drawable.getPadding(mTmpRect);
			foregroundPaddingLeft = mTmpRect.left;
			foregroundPaddingTop = mTmpRect.top;
			foregroundPaddingRight = mTmpRect.right;
			foregroundPaddingBottom = mTmpRect.bottom;
		}

		final int parentLeft = getPaddingLeft() + foregroundPaddingLeft;
		final int parentRight = getWidth() - getPaddingRight()
				- foregroundPaddingRight;
		final int parentTop = getPaddingTop() + foregroundPaddingTop;
		final int parentBottom = getHeight() - getPaddingBottom()
				- foregroundPaddingBottom;

		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				final LayoutParams lp = (LayoutParams) child.getLayoutParams();
				final int width = child.getMeasuredWidth();
				final int height = child.getMeasuredHeight();
				int childLeft = parentLeft;
				int childTop = parentTop;
				final int gravity = lp.gravity;
	 
				if (getIconView() != null && getIconView().equals(child)) {
					childTop = childTop - mHeaderHeight - child.getMeasuredHeight();
					if (gravity == Gravity.CENTER_HORIZONTAL) {
						childLeft = parentLeft
								+ (parentRight - parentLeft - width) / 2 +
								lp.leftMargin - lp.rightMargin;
					}
				} else if (getHeadView() != null && getHeadView().equals(child)) {
					childTop = childTop - child.getMeasuredHeight();
				} else if (mFooterView != null && mFooterView.equals(child)) {
					childTop = parentBottom;
				} else if (gravity != -1) {
					final int horizontalGravity = gravity
							& Gravity.HORIZONTAL_GRAVITY_MASK;
					final int verticalGravity = gravity
							& Gravity.VERTICAL_GRAVITY_MASK;
					switch (horizontalGravity) {
					case Gravity.LEFT:
						childLeft = parentLeft + lp.leftMargin;
						break;
					case Gravity.CENTER_HORIZONTAL:
						childLeft = parentLeft
								+ (parentRight - parentLeft - width) / 2 +
								lp.leftMargin - lp.rightMargin;
						break;
					case Gravity.RIGHT:
						childLeft = parentRight - width - lp.rightMargin;
						break;
					default:
						childLeft = parentLeft + lp.leftMargin;
					}
					switch (verticalGravity) {
					case Gravity.TOP:
						childTop = parentTop + lp.topMargin;
						break;
					case Gravity.CENTER_VERTICAL:
						childTop = parentTop
								+ (parentBottom - parentTop - height) / 2 +
								lp.topMargin - lp.bottomMargin;
						break;
					case Gravity.BOTTOM:
						childTop = parentBottom - height - lp.bottomMargin;
						break;
					default:
						childTop = parentTop + lp.topMargin;
					}
				}
				child.layout(childLeft, childTop, childLeft + width, childTop
						+ height);
			}

		}

		if (!mScrolling || mEnableSnapshot) {
			if (getHeadView() != null && GapState.SHOW_HEAD.equals(mGapState)) {
				scrollTo(0, -mHeaderHeight);
			} else if (mFooterView != null
					&& GapState.SHOW_FOOTER.equals(mGapState)) {
				scrollTo(0, mFooterHeight);
			} else if (GapState.HIDE.equals(mGapState)) {
				scrollTo(0, 0);
			}
		}
 
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int count = getChildCount();
		int maxHeight = 0;
		int maxWidth = 0;
		
		for (int i = 0; i < count; i++) {

			final View child = getChildAt(i);
			if (mMeasureAll || child.getVisibility() != GONE) {
				measureChildWithMargins(child, widthMeasureSpec, 0,
						heightMeasureSpec, 0);
				if (getIconView() != null && getIconView().equals(child)) {
					mIconHeight = child.getMeasuredHeight();
					continue;

				} else if (getHeadView() != null && getHeadView().equals(child)) {
					mHeaderHeight = child.getMeasuredHeight();
					continue;

				} else if (mFooterView != null && mFooterView.equals(child)) {
					mFooterHeight = child.getMeasuredHeight();
					continue;
				}

				maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
				maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
			}

		}

		final Drawable drawable = getForeground();
		int foregroundPaddingLeft = 0;
		int foregroundPaddingTop = 0;
		int foregroundPaddingRight = 0;
		int foregroundPaddingBottom = 0;

		if (drawable != null) {
			drawable.getPadding(mTmpRect);
			foregroundPaddingLeft = mTmpRect.left;
			foregroundPaddingTop = mTmpRect.top;
			foregroundPaddingRight = mTmpRect.right;
			foregroundPaddingBottom = mTmpRect.bottom;
		}
		maxWidth += (getPaddingLeft() + getPaddingRight()
				+ foregroundPaddingLeft + foregroundPaddingRight);
		maxHeight += (getPaddingTop() + getPaddingBottom()
				+ foregroundPaddingTop + foregroundPaddingBottom);

		maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
		maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

		if (drawable != null) {

			maxHeight = Math.max(maxHeight, drawable.getMinimumHeight());
			maxWidth = Math.max(maxWidth, drawable.getMinimumWidth());

		}

		setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec),
				resolveSize(maxHeight, heightMeasureSpec));
	}

	public void setOnGapStateChangedListener(OnGapStateChangedListener listener) {
		mGapStateChangedListener = listener;
	}

	/**
	 * 滑动监听器
	 * @author Administrator
	 *
	 */
	public interface OnGapStateChangedListener {
		/**
		 * 头部搜索框隐藏触发事件
		 */
		public void onGapHided();
		/**
		 * 头部搜索框显示触发事件
		 */
		public void onGapShowed();

		/**
		 * 滑动开始触发
		 */
		public void beforeShow(int scrollDirection);
	}

	/**
	 * 用于内容视图继承该接口用以判断滑动到边界
	 * @author Administrator
	 *
	 */
	public interface OverBoundDetector {
		/**
		 * 是否滑动到顶部
		 * @return
		 */
		public boolean overTop();
		/**
		 * 是否滑动到底部
		 * @return
		 */
		public boolean overBottom();
	}
}




