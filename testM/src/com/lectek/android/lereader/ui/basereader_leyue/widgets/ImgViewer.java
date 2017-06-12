package com.lectek.android.lereader.ui.basereader_leyue.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

public class ImgViewer extends View implements OnGestureListener{
	private static final int TRANSITION_ANIM_TYPE_ENTER = 0;
	private static final int TRANSITION_ANIM_TYPE_EXIT = 1;
	private static final int TRANSITION_ANIM_TYPE_NOTHING = -1;
	private static final int TRANSITION_ANIM_DURATION = 700;
	private static final int RESTORE_ANIM_DURATION = 700;
	private static final int FLING_ANIM_DURATION = 1000;
	private static final int ANGLE_ANIM_DURATION = 700;
	private static final float MAX_SIZE = 2.5f;
	private static final float MIN_ZOOM_RESTORE = 0.6f;
	private static final float MIN_ZOOM_EXIT = 0.5f;
	private static final float MAX_ZOOM = 3.0f;
	private Drawable mDrawable;
	private RectF mIntrinsicDrawableRect;
	private RectF mDrawableRect;
	private GestureDetector mGestureDetector;
	private Scroller mRestoreScroller;
	private Scroller mFlingScroller;
	private Scroller mAngleScroller;
	private float mLastDistance;
	private int mLastPointerCount;
	private float mRotateDegrees;
	private float mLastAngle;
	private ActionCallback mActionCallback;
	private PaintFlagsDrawFilter mPaintFlagsDrawFilter;
	private Rect mLocationRect;
	private int mTransitionAnimType = TRANSITION_ANIM_TYPE_NOTHING;
	private boolean isTransitionAnimStarting;
	public ImgViewer(Context context) {
		super(context);
		init();
	}
	
	public ImgViewer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public ImgViewer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		mDrawableRect = new RectF();
		mGestureDetector = new GestureDetector(this);
		mRestoreScroller = new Scroller(getContext());
		mFlingScroller = new Scroller(getContext());
		mAngleScroller = new Scroller(getContext());
		mLastDistance = -1;
		mLastAngle = -1;
		mLastPointerCount = -1;
		mRotateDegrees = 0;
		mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
	}
	
	public void setActionCallback(ActionCallback actionCallback){
		mActionCallback = actionCallback;
	}
	
	public void setDrawable(Drawable d){
		setDrawable(d, null);
	}
	
	public RectF getDrawableRect(){
		return new RectF(mIntrinsicDrawableRect);
	}
	
	public void setDrawable(Drawable d,Rect locationRect) {
		if(mDrawable != d){
			mDrawable = d;
			isTransitionAnimStarting = false;
			if(mDrawable != null){
				mLocationRect = locationRect;
				mTransitionAnimType = TRANSITION_ANIM_TYPE_ENTER;
			}else{
				mLocationRect = null;
			}
			mIntrinsicDrawableRect = null;
		}
		requestLayout();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int w;
		int h;
		if (mDrawable == null) {
			// If no drawable, its intrinsic size is 0.
			w = h = 0;
		} else {
			w = mDrawable.getIntrinsicWidth();
			h = mDrawable.getIntrinsicHeight();
			if (w <= 0)
				w = 1;
			if (h <= 0)
				h = 1;
		}
//		int pleft = getPaddingLeft();
//		int pright = getPaddingRight();
//		int ptop = getPaddingTop();
//		int pbottom = getPaddingBottom();
//		w += pleft + pright;
//		h += ptop + pbottom;
		int widthSize;
		int heightSize;
		
		w = Math.max(w, getSuggestedMinimumWidth());//720
		h = Math.max(h, getSuggestedMinimumHeight());//1011

		widthSize = resolveAdjustedSize(w, widthMeasureSpec);//1080 - 480
		heightSize = resolveAdjustedSize(h, heightMeasureSpec);//1845 - 762
		
		setMeasuredDimension(widthSize, heightSize);
		
		if(mIntrinsicDrawableRect == null && mDrawable != null){
			int intrinsicWidth = mDrawable.getIntrinsicWidth();
			int intrinsicHeight = mDrawable.getIntrinsicHeight();
			if(intrinsicWidth > widthSize || intrinsicHeight > heightSize){
				int gapW = intrinsicWidth - widthSize;
				int gapH = intrinsicHeight - heightSize;
				if(gapW > gapH){
					intrinsicHeight = intrinsicHeight * widthSize / intrinsicWidth;
					intrinsicWidth = widthSize;
				}else{
					intrinsicWidth = intrinsicWidth * heightSize / intrinsicHeight;
					intrinsicHeight = heightSize;
				}
			}
			mIntrinsicDrawableRect = new RectF(0,0,intrinsicWidth,intrinsicHeight);
			centerRect(widthSize, heightSize, mIntrinsicDrawableRect);
			if(mLocationRect != null && mTransitionAnimType == TRANSITION_ANIM_TYPE_ENTER){
				mDrawableRect.set(mLocationRect);
			}else{
				mDrawableRect.set(mIntrinsicDrawableRect);
			}
		}
	}
	
	private int resolveAdjustedSize(int desiredSize,int measureSpec) {
		int result = desiredSize;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize =  MeasureSpec.getSize(measureSpec);
		switch (specMode) {
			case MeasureSpec.UNSPECIFIED:
				result = desiredSize;
			break;
			case MeasureSpec.AT_MOST:
				// Parent says we can be as big as we want, up to specSize. 
				// Don't be larger than specSize, and don't be larger than 
				// the max size imposed on ourselves.
				result = Math.min(desiredSize, specSize);
			break;
				case MeasureSpec.EXACTLY:
				// No choice. Do what we are told.
				result = specSize;
			break;
		}
		return result;
	}
	
	private void centerRect(int maxW,int maxH,RectF rect){
		float w = rect.width();//720
		float h = rect.height();//1011
		float gapW = maxW - w;//1080-720
		float gapH = maxH - h;//1845-1011
		rect.offsetTo(0, 0);
		rect.offset(gapW / 2, gapH / 2);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(mDrawable == null){
			return;
		}
		if(mTransitionAnimType != TRANSITION_ANIM_TYPE_NOTHING && !isTransitionAnimStarting){
			startTransitionAnim(mTransitionAnimType == TRANSITION_ANIM_TYPE_ENTER);
		}
		boolean isNeedInvalidate = false;
		if(mRestoreScroller.computeScrollOffset()){
			isNeedInvalidate = true; 
			computeRestoreAnim(mRestoreScroller.getCurrX(),mRestoreScroller.getCurrY());
		}else{
			onTransitionAnimEnd();
		}
		if(mDrawable == null){
			return;
		}
		if(mFlingScroller.computeScrollOffset()){
			isNeedInvalidate = true; 
			computeFlingAnim(mFlingScroller.getCurrX(),mFlingScroller.getCurrY());
		}
		if(mAngleScroller.computeScrollOffset()){
			isNeedInvalidate = true; 
			computeAngleAnim(mAngleScroller.getCurrX(),mAngleScroller.getCurrY());
		}
		canvas.save();
		canvas.clipRect(getLeft(),getTop(),getRight(),getBottom());
		canvas.setDrawFilter(mPaintFlagsDrawFilter); 
		canvas.rotate(mRotateDegrees, mDrawableRect.centerX(), mDrawableRect.centerY());
		mDrawable.setBounds((int)mDrawableRect.left,
				(int)mDrawableRect.top,
				(int)mDrawableRect.right,
				(int)mDrawableRect.bottom);
		mDrawable.draw(canvas);
		canvas.restore();
		if(isNeedInvalidate){
			invalidate();
		}
	}
	
	private void onTransitionAnimEnd(){
		if(isTransitionAnimStarting){
			isTransitionAnimStarting = false;
			if(mActionCallback != null){
				mActionCallback.onEndTransitionAnim(mTransitionAnimType == TRANSITION_ANIM_TYPE_ENTER);
			}
			mTransitionAnimType = TRANSITION_ANIM_TYPE_NOTHING;
		}
	}
	
	public boolean canExit(){
		return mDrawable != null && mLocationRect != null;
	}
	
	public void exit(){
		abortAnim();
		mTransitionAnimType = TRANSITION_ANIM_TYPE_EXIT;
		invalidate();
	}
	
	private float computeAngle(float x1,float y1,float x2,float y2){
		float x = x1 - x2;
		float y = - y1 + y2;
		if(y == 0){
			if(x < 0){
				return 180;
			}else{
				return 0;
			}
		}
		if(x == 0){
			if(y < 0){
				return 270;
			}else{
				return 90;
			}
		}
		float angle = (float) (Math.atan(Math.abs(y / x)) / Math.PI * 180);
		if(y > 0){
			if(x < 0){
				angle = 180 - angle;
			}
		}else{
			if(x < 0){
				angle = 180 + angle;
			}else{
				angle = 360 - angle;
			}
		}
		return angle;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mDrawable == null){
			return false;
		}
		abortAnim();
		boolean isHandle = false;
		isHandle = mGestureDetector.onTouchEvent(event);
		if(!isHandle){
			if(event.getAction() == MotionEvent.ACTION_MOVE && event.getPointerCount() == 2){
				PointF centrePoint = new PointF();
				centrePoint.set(Math.abs((event.getX(1) - event.getX(0)) / 2 + event.getX(0)),
						Math.abs((event.getY(1) - event.getY(0)) / 2 + event.getY(0)));
				
				float newDistance = PointF.length(event.getX(1) - event.getX(0), event.getY(1) - event.getY(0));
				float newAngle = computeAngle(event.getX(1),event.getY(1),event.getX(0),event.getY(0));
				if(mLastDistance == -1){
					mLastDistance = newDistance;
				}
				if(mLastAngle == -1){
					mLastAngle = newAngle;
				}
				if(mLastPointerCount == event.getPointerCount()){
					RectF tempRect = new RectF();
					tempRect.set(mDrawableRect);
					float distance = (mLastDistance - newDistance) / getWidth();
					float dx = distance * mDrawableRect.width() / 2;
					float dy = distance * mDrawableRect.height() / 2;
					tempRect.inset(dx, dy);
					float minWidth = mIntrinsicDrawableRect.width() * MIN_ZOOM_EXIT;
					float minHeight = mIntrinsicDrawableRect.height() * MIN_ZOOM_EXIT;
					float maxWidth = mIntrinsicDrawableRect.width() * MAX_ZOOM;
					float maxHeight = mIntrinsicDrawableRect.height() * MAX_ZOOM;
					if(tempRect.width() < minWidth){
						mDrawableRect.set(0,0,minWidth,minHeight);
						mDrawableRect.offsetTo((getWidth() - mDrawableRect.width()) / 2,
								(getHeight() - mDrawableRect.height()) / 2);
					}else if(tempRect.width() > maxWidth){
						mDrawableRect.set(mDrawableRect.left,
								mDrawableRect.top,
								mDrawableRect.left + maxWidth,
								mDrawableRect.top + maxHeight);
					}else{
						float offsetX = (1 - (centrePoint.x - mDrawableRect.left) / (mDrawableRect.width() / 2)) * dx;
						float offsetY = (1 - (centrePoint.y - mDrawableRect.top) / (mDrawableRect.height() / 2)) * dy;
						mDrawableRect.set(tempRect);
						mDrawableRect.offset(-offsetX, -offsetY);
					}
					float tempDegrees = mLastAngle - newAngle;
					mRotateDegrees += Math.abs(tempDegrees) > 1.5 ? tempDegrees : 0;
					isHandle = true;
				}
				mLastDistance = newDistance;
				mLastAngle = newAngle;
			}else{
				mLastDistance = -1;
				mLastAngle = -1;
			}
		}
		if(isHandle){
			rectifyBoundary();
			invalidate();
		}
		mLastPointerCount = event.getPointerCount();
		if(event.getAction() == MotionEvent.ACTION_UP){
			if(mDrawableRect.width() < mIntrinsicDrawableRect.width() * MIN_ZOOM_RESTORE){
				startTransitionAnim(false);
			}else{
				startRestoreAnim();
			}
			startAngleAnim();
		}
		return true;
	}

	private void computeAngleAnim(int curX, int curY){
		mRotateDegrees = curX;
	}
	
	private void startAngleAnim(){
		if(mDrawable == null){
			return;
		}
		int angle = (int)mRotateDegrees % 360;
		int angleD = -angle;
		if(angle != 0){
			if(angle > 180){
				angleD = 360 - angle;
			}
			if(angle < -180){
				angleD = - 360 - angle;
			}
			mAngleScroller.startScroll(angle, angle, angleD, angleD, ANGLE_ANIM_DURATION);
			mAngleScroller.computeScrollOffset();
			invalidate();
		}
	}
	
	private void startFlingAnim(int velocityX,int velocityY){
		if(mDrawable == null){
			return;
		}
		if(mDrawableRect.width() > getWidth() || mDrawableRect.height() > getHeight()){
			mFlingScroller.startScroll((int)mDrawableRect.centerX(),
					(int)mDrawableRect.centerY(),
					velocityX / 4,
					velocityY / 4,
					FLING_ANIM_DURATION);
			mFlingScroller.computeScrollOffset();
			invalidate();
		}
	}
	
	private void computeFlingAnim(int curX, int curY){
		mDrawableRect.offset(curX - mDrawableRect.centerX(), curY - mDrawableRect.centerY());
		rectifyBoundary();
	}
	
	private void computeRestoreAnim(int curX, int curY){
		float dx = (mDrawableRect.width() - curX) / 2;
		float dy = (mDrawableRect.height() - curY) / 2;
		mDrawableRect.inset(dx,dy);
		float offsetX = (1 - (getWidth() / 2 - mDrawableRect.left) / (mDrawableRect.width() / 2)) * dx;
		float offsetY = (1 - (getHeight() / 2 - mDrawableRect.top) / (mDrawableRect.height() / 2)) * dy;
		mDrawableRect.offset(-offsetX, -offsetY);
		rectifyBoundary();
	}
	
	private void abortAnim(){
		if(!mRestoreScroller.isFinished()){
			mRestoreScroller.abortAnimation();
			computeRestoreAnim(mRestoreScroller.getCurrX(),mRestoreScroller.getCurrY());
		}
		if(!mFlingScroller.isFinished()){
			mFlingScroller.abortAnimation();
			computeFlingAnim(mFlingScroller.getCurrX(),mFlingScroller.getCurrY());
		}
		if(!mAngleScroller.isFinished()){
			mAngleScroller.abortAnimation();
			computeAngleAnim(mAngleScroller.getCurrX(),mAngleScroller.getCurrY());
		}
		onTransitionAnimEnd();
	}
	
	private void startTransitionAnim(boolean isEnter){
		RectF sourceRect = mDrawableRect;
		RectF targetRect = isEnter ? mIntrinsicDrawableRect : new RectF(mLocationRect);
		isTransitionAnimStarting = true;
		mRestoreScroller.startScroll(
				(int)sourceRect.width(), 
				(int)sourceRect.height(),
				(int)(targetRect.width() - sourceRect.width()), 
				(int)(targetRect.height() - sourceRect.height()),
				TRANSITION_ANIM_DURATION);
		mRestoreScroller.computeScrollOffset();
		mFlingScroller.startScroll((int)sourceRect.centerX(),
				(int)sourceRect.centerY(),
				(int)(targetRect.centerX() - sourceRect.centerX()),
				(int)(targetRect.centerY() - sourceRect.centerY()),
				TRANSITION_ANIM_DURATION);
		mFlingScroller.computeScrollOffset();
		if(mActionCallback != null){
			mActionCallback.onStartTransitionAnim(isEnter);
		}
		invalidate();
	}
	
	private void startRestoreAnim(){
		if(mDrawable == null){
			return;
		}
		float intrinsicWidth = mIntrinsicDrawableRect.width();
		if(mDrawableRect.width() < intrinsicWidth){
			mRestoreScroller.startScroll(
					(int)mDrawableRect.width(), 
					(int)mDrawableRect.height(),
					(int)(intrinsicWidth - mDrawableRect.width()), 
					(int)(mIntrinsicDrawableRect.height() - mDrawableRect.height()),
					RESTORE_ANIM_DURATION);
			mRestoreScroller.computeScrollOffset();
			invalidate();
		}
		float maxWidth = mIntrinsicDrawableRect.width() * MAX_SIZE;
		float maxHeight = mIntrinsicDrawableRect.height() * MAX_SIZE;
		if(mDrawableRect.width() > maxWidth){
			mRestoreScroller.startScroll(
					(int)mDrawableRect.width(), 
					(int)mDrawableRect.height(),
					(int)(- mDrawableRect.width() + maxWidth), 
					(int)(- mDrawableRect.height() + maxHeight),
					RESTORE_ANIM_DURATION);
			mRestoreScroller.computeScrollOffset();
			invalidate();
		}
	}
	
	private void rectifyBoundary(){
		if(isTransitionAnimStarting){
			return;
		}
		if(mDrawableRect.width() > getWidth()){
			if(mDrawableRect.left > 0){
				mDrawableRect.offsetTo(0, mDrawableRect.top);
			}else if(mDrawableRect.right < getWidth()){
				mDrawableRect.offsetTo(getWidth() - mDrawableRect.width(), mDrawableRect.top);
			}
		}else{
			mDrawableRect.offsetTo((getWidth() - mDrawableRect.width()) / 2, mDrawableRect.top);
		}
		
		if(mDrawableRect.height() > getHeight()){
			if(mDrawableRect.top > 0){
				mDrawableRect.offsetTo(mDrawableRect.left,0);
			}else if(mDrawableRect.bottom < getHeight()){
				mDrawableRect.offsetTo(mDrawableRect.left,getHeight() - mDrawableRect.height());
			}
		}else{
			mDrawableRect.offsetTo(mDrawableRect.left,(getHeight() - mDrawableRect.height()) / 2);
		}
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		mLastDistance = -1;
		mLastPointerCount = e.getPointerCount();
		mLastAngle = -1;
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		exit();
		return false;
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		boolean isHandle = false;
		if(e2.getPointerCount() == 1 && mLastPointerCount == e2.getPointerCount()){
			mDrawableRect.offset(-distanceX,-distanceY);
			isHandle = true;
		}
		return isHandle;
	}
	
	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		startFlingAnim((int)velocityX,(int)velocityY);
		return true;
	}
	
	public interface ActionCallback{
		public void onEndTransitionAnim(boolean isEnter);
		public void onStartTransitionAnim(boolean isEnter);
	}
}
