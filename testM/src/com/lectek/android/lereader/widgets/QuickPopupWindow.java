package com.lectek.android.lereader.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Quick
 * 
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2010-8-27
 */
public abstract class QuickPopupWindow {
	protected final View anchor;
	private final PopupWindow window;
	private View root;
	private Drawable background = null;
	private final WindowManager windowManager;
	private int popupWindowWidth;
	private int mGravity;
	public QuickPopupWindow(View anchor, int width) {
		this(anchor, width, Gravity.BOTTOM);
	}
	public QuickPopupWindow(View anchor, int width,int gravity) {
		this.mGravity = gravity;
		this.anchor = anchor;
		this.popupWindowWidth = width;
		this.window = new PopupWindow(anchor.getContext());
		this.window.setAnimationStyle(android.R.style.Animation_Toast);
		// when a touch even happens outside of the window
		// make the window go away
		this.window.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					QuickPopupWindow.this.window.dismiss();
					return true;
				}
				return false;
			}
		});

		this.windowManager = (WindowManager) this.anchor.getContext().getSystemService(Context.WINDOW_SERVICE);
		View view = onCreate();
		if(view != null){
			setContentView(view);
		}
	}
	
	protected abstract View onCreate();

	protected void onShow() {}
	
	public boolean isShowing(){
		return this.window.isShowing();
	}

	private void preShow() {
		if(this.root == null) {
			throw new IllegalStateException("setContentView was not called with a view to display.");
		}
		onShow();

		if(this.background == null) {
			this.window.setBackgroundDrawable(new BitmapDrawable());
		} else {
			this.window.setBackgroundDrawable(this.background);
		}

		// if using PopupWindow#setBackgroundDrawable this is the only values of the width and hight that make it work
		// otherwise you need to set the background of the root viewgroup
		// and set the popupwindow background to an empty BitmapDrawable
		if(popupWindowWidth != 0){
			this.window.setWidth(popupWindowWidth);
		}else{
			this.window.setWidth(LayoutParams.FILL_PARENT);
		}
		this.window.setHeight(LayoutParams.WRAP_CONTENT);
		this.window.setTouchable(true);
		this.window.setFocusable(true);
		this.window.setOutsideTouchable(true);

		this.window.setContentView(this.root);
	}
	public View findViewById(int id){
		if(window != null && window.getContentView() != null){
			return window.getContentView().findViewById(id);
		}
		return null;
	}
	public void setBackgroundDrawable(Drawable background) {
		this.background = background;
	}

	public void setContentView(View root) {
		this.root = root;
		this.window.setContentView(root);
	}

	public void setContentView(int layoutResID) {
		LayoutInflater inflator =
				(LayoutInflater) this.anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.setContentView(inflator.inflate(layoutResID, null));
	}

	public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
		this.window.setOnDismissListener(listener);
	}
	
	public void showAsMenu(boolean isFocusable){
		preShow();
		if(!isFocusable){
			this.window.setFocusable(false);
		}
		this.root.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int rootHeight = 0;
		if((mGravity & Gravity.BOTTOM) == Gravity.BOTTOM){
			rootHeight = this.root.getMeasuredHeight();
		}else if((mGravity & Gravity.TOP) == Gravity.TOP){
			int [] location = new int [2];
			this.anchor.getLocationInWindow(location);
			rootHeight = -location[1];
		}
		this.window.showAtLocation(this.anchor,mGravity, 0, -rootHeight);
	}

	public void showPopDownMenu() {
		this.showPopDownMenu(0, 0);
	}

	public void showPopDownMenu(int xOffset, int yOffset) {
		this.preShow();
		this.window.showAsDropDown(this.anchor, xOffset, yOffset);
	}

	public void showQuickAction() {

		this.showQuickAction(0, 0, Gravity.NO_GRAVITY);
	}

	public void showQuickAction(int gravity) {
		this.showQuickAction(0, 0, gravity);
	}

	public void showQuickAction(int xOffset, int yOffset, int gravity) {
		this.preShow();

		int[] location = new int[2];
		this.anchor.getLocationOnScreen(location);

		Rect anchorRect =
				new Rect(location[0], location[1], location[0] + this.anchor.getWidth(), location[1]
					+ this.anchor.getHeight());

		this.root.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootWidth = this.root.getMeasuredWidth();
		int rootHeight = this.root.getMeasuredHeight();

		int screenWidth = this.windowManager.getDefaultDisplay().getWidth();
		int screenHeight = this.windowManager.getDefaultDisplay().getHeight();

		int xPos = ((screenWidth - rootWidth) / 2) + xOffset;
		int yPos = anchorRect.top - rootHeight + yOffset;
		
		int tempWindowWidth = popupWindowWidth;
		if(popupWindowWidth == 0){
			tempWindowWidth = screenWidth;
		}
		if((gravity & Gravity.RIGHT) == Gravity.RIGHT && (gravity & Gravity.TOP) == Gravity.TOP){
			xPos = screenWidth - tempWindowWidth - xOffset;
		}else if((gravity & Gravity.LEFT) == Gravity.LEFT){
			xPos = screenWidth - tempWindowWidth - xOffset;
		}else if((gravity & Gravity.CENTER_HORIZONTAL) == Gravity.CENTER_HORIZONTAL){
			xPos = anchorRect.left + (anchorRect.width() >> 1) - (tempWindowWidth >> 1);
			if(xPos < 0){
				xPos = 0;
			}
			if((xPos + tempWindowWidth) > screenWidth){
				xPos = screenWidth - tempWindowWidth;
			}
		}

		// display on bottom
		if (rootHeight > anchorRect.top) {
			yPos = anchorRect.bottom + yOffset;
		}

		this.window.showAtLocation(this.anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}

	public void dismiss() {
		this.window.dismiss();
	}

}
