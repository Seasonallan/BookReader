package com.lectek.android.lereader.ui.basereader_leyue.anim;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.lectek.android.lereader.lib.utils.LogUtil;

/**
 * 水分平滑翻页动画
 * @author lyw
 */
public abstract class AbsHorGestureAnimController extends PageAnimController {
	protected static final String TAG = AbsHorGestureAnimController.class.getSimpleName();
	protected static final int DURATION = 800;
	private Scroller mScroller;
	private boolean isCancelAnim;
	private Boolean isRequestNextPage;
	private int mLastMoveX;
	protected int mHalfScreenWidth;
	protected int mHalfScreenHeight;
	protected int mHalfContentWidth;
	protected int mHalfContentHeight;
	protected int mScreenWidth;
	protected int mScreenHeight;
	protected int mContentWidth;
	protected int mContentHeight;
	protected PointF mLastTouchPoint;
	protected PointF mDownTouchPoint;
	protected boolean isAnimStart;
	protected boolean isTouchStart;
	protected boolean isTouchRequestPage;
	private int mTouchSlopSquare;

	AbsHorGestureAnimController(Context context){
		super(context);
		mScroller = new Scroller(context);
		mLastTouchPoint = new PointF();
		mDownTouchPoint = new PointF();
		mContentWidth = -1;
	    mTouchSlopSquare = ViewConfiguration.getTouchSlop();
	}
	
	private void checkInit(PageCarver pageCarver){
		if(mContentWidth == -1 
//				|| mScreenWidth != pageCarver.getScreenWidth() 
//				|| mScreenHeight != pageCarver.getScreenHeight()
				){
			onMeasure(pageCarver);
		}
	}
	
	protected void onMeasure(PageCarver pageCarver) {
		mContentWidth = pageCarver.getContentWidth();
		mContentHeight = pageCarver.getContentHeight();
		mHalfContentWidth = mContentWidth >> 1;
		mHalfContentHeight = mContentHeight >> 1;
		mScreenWidth = pageCarver.getScreenWidth();
		mScreenHeight = pageCarver.getScreenHeight();
		mHalfScreenWidth = mScreenWidth >> 1;
		mHalfScreenHeight = mScreenHeight >> 1;
	}
	
	@Override
	public void dispatchTouchEvent(MotionEvent event, PageCarver pageCarver) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			isTouchStart = false;
		}else if(!isTouchStart || isRequestNextPage != null && !isTouchRequestPage){
			isTouchStart = false;
			return;
		}
		if(!mScroller.isFinished()){
			LogUtil.i(TAG,"dispatchTouchEvent isAnimStop");
			stopAnim(pageCarver);
		}
		checkInit(pageCarver);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isTouchStart = true;
			isTouchRequestPage = false;
			mDownTouchPoint.set(event.getX(), event.getY());
			mLastTouchPoint.set(event.getX(), event.getY());
			mLastMoveX = 0;
			break;
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) (mDownTouchPoint.x - event.getX());
			if(isRequestNextPage == null){
				if(Math.abs(moveX) > mTouchSlopSquare){
					Integer requestPageIndex = null;
					if(moveX > 0){
						requestPageIndex = pageCarver.requestNextPage();
						if(requestPageIndex != null){
							isRequestNextPage = true;
							int currentPageIndex = pageCarver.getCurrentPageIndex();
							onRequestPage(isRequestNextPage,currentPageIndex,requestPageIndex,mLastTouchPoint.x,mLastTouchPoint.y);
							isTouchRequestPage = true;
						}else{
							isTouchStart = false;
						}
					}else{
						requestPageIndex = pageCarver.requestPrePage();
						if(requestPageIndex != null){
							isRequestNextPage = false;
							int currentPageIndex = pageCarver.getCurrentPageIndex();
							onRequestPage(isRequestNextPage,currentPageIndex,requestPageIndex,mLastTouchPoint.x,mLastTouchPoint.y);
							isTouchRequestPage = true;
						}else{
							isTouchStart = false;
						}
					}
				}
			}else{
				if(isRequestNextPage){
					if(moveX < 0){
						mDownTouchPoint.set(mDownTouchPoint.x - moveX, mDownTouchPoint.y);
					}
				}else{
					if(moveX > 0){
						mDownTouchPoint.set(mDownTouchPoint.x - moveX, mDownTouchPoint.y);
					}
				}
			}
			mLastMoveX = (int) (mLastTouchPoint.x - event.getX());
			break;
		case MotionEvent.ACTION_UP:
			if(isRequestNextPage != null){
				if(isRequestNextPage && mLastMoveX < 0){
					startCancelAnim(isRequestNextPage, pageCarver);
				}else if(!isRequestNextPage && mLastMoveX > 0){
					startCancelAnim(isRequestNextPage, pageCarver);
				}else{
					startAnim(isRequestNextPage, pageCarver);
				}
			}else{
				Integer requestPageIndex = null;
				int contentWidth = pageCarver.getContentWidth();
				int contentHeight = pageCarver.getContentHeight();
				if( (mDownTouchPoint.y<contentHeight/2 && mDownTouchPoint.x > contentWidth*2/3) || (mDownTouchPoint.y>contentHeight/2 && mDownTouchPoint.x>contentWidth/3)){
					stopAnim(pageCarver);
					requestPageIndex = pageCarver.requestNextPage();
					if(requestPageIndex != null){
						isRequestNextPage = true;
						int currentPageIndex = pageCarver.getCurrentPageIndex();
						startAnim(currentPageIndex,requestPageIndex,isRequestNextPage,pageCarver);
					}
				}else{
					stopAnim(pageCarver);
					requestPageIndex = pageCarver.requestPrePage();
					if(requestPageIndex != null){
						isRequestNextPage = false;
						int currentPageIndex = pageCarver.getCurrentPageIndex();
						startAnim(currentPageIndex,requestPageIndex,isRequestNextPage,pageCarver);
					}
				}
				if(isRequestNextPage != null){
					startAnim(isRequestNextPage,pageCarver);
				}
			}
			isTouchStart = false;
			break;
		}
		mLastTouchPoint.set(event.getX(), event.getY());
		pageCarver.requestInvalidate();
	}

	@Override
	public boolean dispatchDrawPage(Canvas canvas, PageCarver pageCarver) {
		if(isRequestNextPage == null){
			return false;
		}
		checkInit(pageCarver);
		boolean isUnFinished = !mScroller.isFinished() && mScroller.computeScrollOffset();
		if(isUnFinished){
			mLastTouchPoint.set(mScroller.getCurrX(), mScroller.getCurrY());
		}
		if(!isUnFinished && !isTouchStart){
			dispatchAnimEnd(pageCarver);
			return false;
		}else{
			onDrawAnim(canvas,isCancelAnim,isRequestNextPage, pageCarver);
			pageCarver.requestInvalidate();
			return true;
		}
	}
	
	private void dispatchAnimStart(PageCarver pageCarver){
		isAnimStart = true;
		onAnimStart(isCancelAnim);
		pageCarver.onStartAnim(isCancelAnim);
		pageCarver.requestInvalidate();
	}
	
	private void dispatchAnimEnd(PageCarver pageCarver){
		isAnimStart = false;
		isRequestNextPage = null;
		onAnimEnd(isCancelAnim);
		pageCarver.onStopAnim(isCancelAnim);
		isCancelAnim = false;
		pageCarver.requestInvalidate();
	}
	
	/**
	 * onRequestPage() -> onAnimStart() -> onAnimEnd()
	 */
	protected abstract void onAnimStart(boolean isCancelAnim);
	/**
	 * onRequestPage() -> onAnimStart() -> onAnimEnd()
	 */
	protected abstract void onAnimEnd(boolean isCancelAnim);
	/**
	 * onRequestPage() -> onAnimStart() -> onAnimEnd()
	 */
	protected abstract void onRequestPage(boolean isRequestNext,int fromIndex,int toIndex,float x,float y);
	
	protected abstract void onDrawAnim(Canvas canvas,boolean isCancelAnim,boolean isNext,PageCarver pageCarver);
	
	protected void setScroller(Scroller scroller,boolean isRequestNext,boolean isCancelAnim, PageCarver pageCarver){
		int dx = 0;
		int dy = 0;
		if(isCancelAnim){
			if(isRequestNext){
				dx = (int)(mDownTouchPoint.x - mLastTouchPoint.x);
			}else{
				dx = (int)-(mLastTouchPoint.x - mDownTouchPoint.x);
			}
//			LogUtil.i(Tag,"startCancelAnim dx="+dx+" isDown="+isTouchStart+" mLastTouchPoint.x="+mLastTouchPoint.x);
			dy = (int) mLastTouchPoint.y;
			scroller.startScroll((int)mLastTouchPoint.x, (int)mLastTouchPoint.y, dx, dy, DURATION);
		}else{
			if(isRequestNext){
				dx = (int) -(pageCarver.getContentWidth() - (mDownTouchPoint.x - mLastTouchPoint.x));
			}else{
				dx = (int) (pageCarver.getContentWidth() - (mLastTouchPoint.x - mDownTouchPoint.x));
			}
//			LogUtil.i(Tag,"startAnim startX="+mLastTouchPoint.x+" dx="+dx+" isDown="+isTouchStart+" mLastTouchPoint.x="+mLastTouchPoint.x);
			dy = (int) mLastTouchPoint.y;
			scroller.startScroll((int)mLastTouchPoint.x, (int)mLastTouchPoint.y, dx, dy, DURATION);
		}
	}
	
	protected void startCancelAnim(boolean isRequestNext, PageCarver pageCarver){
		isCancelAnim = true;
		setScroller(mScroller, isRequestNext, isCancelAnim,pageCarver);
		dispatchAnimStart(pageCarver);
	}
	
	protected void setDefaultTouchPoint(boolean isNext){
		if(isNext){
			mDownTouchPoint.set(mContentWidth,0);
		}else{
			mDownTouchPoint.set(0,0);
		}
		mLastTouchPoint.set(mDownTouchPoint);
	}
	
	@Override
	public void startAnim(int fromIndex,int toIndex,boolean isNext, PageCarver pageCarver) {
		stopAnim(pageCarver);
		checkInit(pageCarver);
		isRequestNextPage = isNext;
		setDefaultTouchPoint(isNext);
		onRequestPage(isRequestNextPage,fromIndex, toIndex,mLastTouchPoint.x,mLastTouchPoint.y);
		startAnim(isRequestNextPage, pageCarver);
	}
	
	protected void startAnim(boolean isRequestNext, PageCarver pageCarver) {
		isCancelAnim = false;
		setScroller(mScroller, isRequestNext, isCancelAnim,pageCarver);
		dispatchAnimStart(pageCarver);
	}

	@Override
	public void stopAnim(PageCarver pageCarver) {
		if(!mScroller.isFinished()){
			mScroller.abortAnimation();
			mLastTouchPoint.set(mScroller.getFinalX(), mScroller.getFinalY());
			dispatchAnimEnd(pageCarver);
		}
	}

	@Override
	public boolean isAnimStop() {
		return isRequestNextPage == null;
	}
}
