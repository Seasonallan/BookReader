package com.lectek.android.lereader.widgets.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/** 
 *  
 * @author laijp
 * @date 2014-7-9
 * @email 451360508@qq.com
 */
public class OpenFolderContainer extends RelativeLayout{

	private static int ANIMALTION_TIME = 300;

	public OpenFolderContainer(Context context) {
		super(context);
	}
	public OpenFolderContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	public OpenFolderContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	long lasttime = 0;
	boolean isvalid = false;
	private int mTopEdge;
	public void setFolderParams(int topEdge){
		this.mTopEdge = topEdge;
	}
	
	ICallBack mCallBack;
	public void setOnDismissListener(ICallBack callBack){
		this.mCallBack =callBack;
	}

	
	
	@Override
	public boolean dispatchKeyEventPreIme(KeyEvent event) {
		return super.dispatchKeyEventPreIme(event);
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (getKeyDispatcherState() == null) {
				return super.dispatchKeyEvent(event);
			}
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getRepeatCount() == 0) {
				KeyEvent.DispatcherState state = getKeyDispatcherState();
				if (state != null) {
					state.startTracking(event, this);
				}
				return true;
			} else if (event.getAction() == KeyEvent.ACTION_UP) {
				KeyEvent.DispatcherState state = getKeyDispatcherState();
				if (state != null && state.isTracking(event)
						&& !event.isCanceled()) {
					if (mCallBack != null) {
						mCallBack.onResult();
					} 
					return true;
				}
			}
			return super.dispatchKeyEvent(event);
		} else {
			return super.dispatchKeyEvent(event);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int y = (int) event.getY();

		if (System.currentTimeMillis() - lasttime > ANIMALTION_TIME) {
			isvalid = true;
		} else {
			isvalid = false;
		}
		lasttime = System.currentTimeMillis();

		if ((event.getAction() == MotionEvent.ACTION_DOWN) && isvalid
				&& y < mTopEdge ) {
			if (mCallBack != null) {
				mCallBack.onResult();
			} 
			return true;
		} else {
			return super.onTouchEvent(event);
		}
	}

}
