package com.lectek.lereader.core.text.test;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
/**
 * 单击、长按事件检测器
 * @author lyw
 *
 */
public class ClickDetector {
	private static final int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
	private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();

	private static final int LONG_PRESS = 1;
	private OnClickCallBack mOnClickCallBack;
	private int mTouchSlopSquare;
	private Handler mHandler;
	private boolean mHasConsume;
	private boolean mHasLongPress;
	private boolean isDown;
	private boolean isPressInvalid;
	private MotionEvent mCurrentDownEvent;
	private boolean mIsLongpressEnabled;

	public ClickDetector(OnClickCallBack l) {
		this(l, true);
	}
	
	public ClickDetector(OnClickCallBack l,boolean isLongpressEnabled) {
		mOnClickCallBack = l;
        int touchSlop = ViewConfiguration.getTouchSlop();
        mTouchSlopSquare = touchSlop * touchSlop;
        mHandler = new GestureHandler(Looper.getMainLooper());
        mIsLongpressEnabled = isLongpressEnabled;
	}

	public void setIsLongpressEnabled(boolean isLongpressEnabled) {
        mIsLongpressEnabled = isLongpressEnabled;
    }

    public boolean isLongpressEnabled() {
        return mIsLongpressEnabled;
    }
    
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
        final float y = ev.getY();
        final float x = ev.getX();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			isPressInvalid = false;
			mHasConsume = true;	
			mHasLongPress = false;
			isDown = true;
			mHandler.removeMessages(LONG_PRESS);
			if (mCurrentDownEvent != null) {
                mCurrentDownEvent.recycle();
            }
            mCurrentDownEvent = MotionEvent.obtain(ev);
            if(mIsLongpressEnabled){
                mHandler.sendEmptyMessageAtTime(LONG_PRESS, mCurrentDownEvent.getDownTime() + TAP_TIMEOUT + LONGPRESS_TIMEOUT);
            }
			break;
		case MotionEvent.ACTION_MOVE:
			if(!mHasConsume || !isDown){
				break;
			}
			if(!isPressInvalid){
				final int deltaX = (int) (x - mCurrentDownEvent.getX());
	            final int deltaY = (int) (y - mCurrentDownEvent.getY());
	            int distance = (deltaX * deltaX) + (deltaY * deltaY);
	            if (distance > mTouchSlopSquare) {
	            	isPressInvalid = true;
	            }
			}
			if(isPressInvalid){
                mHandler.removeMessages(LONG_PRESS);
                if(!mHasLongPress){
					mHasConsume = false;
					MotionEvent event = MotionEvent.obtain(ev);
					event.setLocation(mCurrentDownEvent.getX(), mCurrentDownEvent.getY());
					event.setAction(MotionEvent.ACTION_DOWN);
					mOnClickCallBack.dispatchTouchEventCallBack(event);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if(!mHasConsume || !isDown){
				break;
			}
			isDown = false;
			mHasConsume = false;
            mHandler.removeMessages(LONG_PRESS);
            if(!mHasLongPress){
            	dispatchShowPress(ev);
            }
			break;
		case MotionEvent.ACTION_CANCEL:
            mHandler.removeMessages(LONG_PRESS);
			isDown = false;
			break;
		default:
			break;
		}
		return mHasConsume;
	}

	private void dispatchLongPress(MotionEvent ev) {
		mHasLongPress = true;
		if(mOnClickCallBack != null && ev != null){
			mOnClickCallBack.onLongClickCallBack(ev);
		}
	}

	private void dispatchShowPress(final MotionEvent ev) {
		if(mOnClickCallBack != null && ev != null){
			boolean isHandle = mOnClickCallBack.onClickCallBack(ev);
			if(!isHandle){
				MotionEvent event = MotionEvent.obtain(ev);
				event.setLocation(mCurrentDownEvent.getX(), mCurrentDownEvent.getY());
				event.setAction(MotionEvent.ACTION_DOWN);
				mOnClickCallBack.dispatchTouchEventCallBack(event);
			}
		}
	}
	
	private class GestureHandler extends Handler {
		public GestureHandler(Looper arg0){
			super(arg0);
		}
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LONG_PRESS:
				dispatchLongPress(mCurrentDownEvent);
				break;
			default:
				throw new RuntimeException("Unknown message " + msg); // never
			}
		}
	}

	public interface OnClickCallBack {
		public boolean onClickCallBack(MotionEvent event);

		public boolean onLongClickCallBack(MotionEvent event);
		
		public void dispatchTouchEventCallBack(MotionEvent event);
	}

	public static class SimpleOnGestureListener implements OnClickCallBack {
		@Override
		public boolean onClickCallBack(MotionEvent event) {
			return false;
		}

		@Override
		public boolean onLongClickCallBack(MotionEvent event) {
			return false;
		}

		@Override
		public void dispatchTouchEventCallBack(MotionEvent event) {
		}
	}
}
