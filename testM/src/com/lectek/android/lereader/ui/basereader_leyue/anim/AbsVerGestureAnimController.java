package com.lectek.android.lereader.ui.basereader_leyue.anim;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * 水分平滑翻页动画
 * @author lyw
 */
public class AbsVerGestureAnimController {
	
	private MotionEvent mTempMotionEvent;
	private int mTouchSlopSquare;
	private int mLastMoveX;
	protected PointF mDownTouchPoint;
	private boolean mIsVertial = false;
	private boolean mHasConsume = false;
	private boolean isDown = false;
	private boolean twiceReceive = true;
	private boolean isPressInvalid;
	
	public AbsVerGestureAnimController() {
		mTouchSlopSquare = ViewConfiguration.getTouchSlop();
		mDownTouchPoint = new PointF();
	}
	
	public boolean handlerTouch(final MotionEvent ev,IVertialTouchEventDispatcher touchEventDispatcher) {
		if(mTempMotionEvent == ev){
			return false;
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mHasConsume = true;	
			isPressInvalid = false;
			isDown = true;
			mIsVertial = false;
			mDownTouchPoint.set(ev.getX(), ev.getY());
			touchEventDispatcher.verticalTouchEventCallBack(ev);
			return true;
		case MotionEvent.ACTION_MOVE:
			if(!mHasConsume || !isDown){
				mHasConsume = false;
				break;
			}
			int moveX = (int) (mDownTouchPoint.x - ev.getX());
			int moveY = (int) (mDownTouchPoint.y - ev.getY());
			float move = PointF.length(ev.getX() - mDownTouchPoint.x, ev.getY() - mDownTouchPoint.y);
			if(move > mTouchSlopSquare){
				isPressInvalid = true;
			}
			if(isPressInvalid){
				
				int distance = Math.abs(moveY) - Math.abs(moveX);
				if(distance > 0){
					//确认为纵向滑动
					touchEventDispatcher.verticalTouchEventCallBack(ev);
					return true;
				}else{
					//确认为横向滑动，还原现场
					//再触发一次Down事件
					mTempMotionEvent = MotionEvent.obtain(ev);
					mTempMotionEvent.setAction(MotionEvent.ACTION_DOWN);
					mTempMotionEvent.setLocation(mDownTouchPoint.x, mDownTouchPoint.y);
					touchEventDispatcher.unVerticalTouchEventCallBack(mTempMotionEvent);
					mTempMotionEvent = null;
					mHasConsume = false;
					
				}
			}
			break;
		case MotionEvent.ACTION_UP:
//			if(mIsVertial){
//				return true;
//			}
			if(!mHasConsume || !isDown){
				mHasConsume = false;
				break;
			}
			if(!isPressInvalid){
				onPressCallBack(ev,touchEventDispatcher);
			}else{
				touchEventDispatcher.verticalTouchEventCallBack(ev);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
//			if(mIsVertial){
//				return true;
//			}
			if(!mHasConsume || !isDown){
				mHasConsume = false;
				break;
			}
			touchEventDispatcher.verticalTouchEventCallBack(ev);
			break;
		default:
			break;
		}
		return mHasConsume;
		
	}
	
	protected boolean onPressCallBack(MotionEvent ev,IVertialTouchEventDispatcher touchEventDispatcher){
		mTempMotionEvent = MotionEvent.obtain(ev);
		mTempMotionEvent.setAction(MotionEvent.ACTION_DOWN);
		touchEventDispatcher.unVerticalTouchEventCallBack(mTempMotionEvent);
		mTempMotionEvent = MotionEvent.obtain(ev);
		mTempMotionEvent.setAction(MotionEvent.ACTION_UP);
		touchEventDispatcher.unVerticalTouchEventCallBack(mTempMotionEvent);
		mTempMotionEvent = null;
		return true;
	}
	
	public interface IVertialTouchEventDispatcher{
		public void unVerticalTouchEventCallBack(MotionEvent ev);
		public void verticalTouchEventCallBack(MotionEvent ev);
	}
}
