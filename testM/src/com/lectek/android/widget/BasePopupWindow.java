package com.lectek.android.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

public abstract class BasePopupWindow {
	private InteriorPopupWindow mPopupWindow;
	protected View mParentView;
	private Context mContext;
	private OnShowListener mOnShowListener;
	private OnDismissListener mOnDismissListener;
	private View mContentView;

	public BasePopupWindow(View parent) {
		this(parent, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
	}

	public BasePopupWindow(View parent, int lpWidth, int lpHeight) {
		mParentView = parent;
		mContext = parent.getContext();
		mPopupWindow = new InteriorPopupWindow(mContext);
		Drawable defaultBackgroundDrawable = getDefaultBackgroundDrawable();
		if (defaultBackgroundDrawable != null) {
			mPopupWindow.setBackgroundDrawable(defaultBackgroundDrawable);
		}

		int defaultAnimationStyle = getDefaultAnimationStyle();
		if (defaultAnimationStyle != 0) {
			mPopupWindow.setAnimationStyle(defaultAnimationStyle);
		}

		mPopupWindow.setWidth(lpWidth);
		mPopupWindow.setHeight(lpHeight);
		mPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				dispatchOnDismiss();
			}
		});

		onInitPopupWindow(mPopupWindow);
		InteriorContentView interiorContentView = new InteriorContentView(
				getContext());
		mPopupWindow.setContentView(interiorContentView);
	}

	public Context getContext() {
		return mContext;
	}

	public Resources getResources() {
		return mContext.getResources();
	}

	public String getString(int id) {
		return mContext.getResources().getString(id);
	}

	public LayoutInflater getLayoutInflater() {
		return LayoutInflater.from(getContext());
	}

	public String getString(int id, Object... formatArgs) {
		return mContext.getResources().getString(id, formatArgs);
	}

	public View getParentView() {
		return mParentView;
	}

	public View getContentView() {
		return mContentView;
	}

	public PopupWindow getPopupWindow() {
		return mPopupWindow;
	}
	
	public void setSoftInputMode(int mode) {
		mPopupWindow.setSoftInputMode(mode);
	}

	public boolean isShowing() {
		return mPopupWindow.isShowing();
	}

	public void showAsDropDown() {
		showAsDropDown(0, 0);
	}

	public void showAsDropDown(int xoff, int yoff) {
		int x, y;
		int gravity;
		int[] mDrawingLocation = new int[2];
		int[] mScreenLocation = new int[2];

		mParentView.getLocationInWindow(mDrawingLocation);
		x = mDrawingLocation[0] + xoff;
		y = mDrawingLocation[1] + mParentView.getHeight() + yoff;

		boolean onTop = false;

		gravity = Gravity.LEFT | Gravity.TOP;

		mParentView.getLocationOnScreen(mScreenLocation);
		final Rect displayFrame = new Rect();
		mParentView.getWindowVisibleDisplayFrame(displayFrame);

		final View root = mParentView.getRootView();
		onTop = (displayFrame.bottom - mScreenLocation[1]
				- mParentView.getHeight() - yoff) < (mScreenLocation[1] - yoff - displayFrame.top);
		if (onTop) {
			gravity = Gravity.LEFT | Gravity.BOTTOM;
			y = root.getHeight() - mDrawingLocation[1] + yoff;
		} else {
			y = mDrawingLocation[1] + mParentView.getHeight() + yoff;
		}
		gravity |= Gravity.DISPLAY_CLIP_VERTICAL;
		showAtLocation(root, gravity, x, y);
	}

	public void showAtLocation() {
		showAtLocation(mParentView, Gravity.NO_GRAVITY, 0, 0);
	}

	public void showAtLocation(int gravity) {
		showAtLocation(mParentView, gravity, 0, 0);
	}

	public void showAtLocation(int x, int y) {
		showAtLocation(mParentView, Gravity.NO_GRAVITY, x, y);
	}

	public void showAtLocation(int gravity, int x, int y) {
		showAtLocation(mParentView, gravity, x, y);
	}

	public void showAtLocation(View parent, int gravity, int x, int y) {
		if (isShowing()) {
			return;
		}
		if (mContentView == null) {
			mContentView = onCreateContentView();
			if (mContentView != null) {
				((ViewGroup) mPopupWindow.getContentView()).removeAllViews();
				((ViewGroup) mPopupWindow.getContentView())
						.addView(mContentView);
			}
		}
		onPreShow();
		mPopupWindow.showAtLocation(parent, gravity, x, y);
		dispatchOnShow();
	}

	public void dismiss() {
		if (!isShowing()) {
			return;
		}
		mPopupWindow.dismiss();
	}

	public void setOnDismissListener(OnDismissListener onDismissListener) {
		mOnDismissListener = onDismissListener;
	}

	public void setOnShowListener(OnShowListener onShowListener) {
		mOnShowListener = onShowListener;
	}

	private void dispatchOnShow() {
		onShow();
		if (mOnShowListener != null) {
			mOnShowListener.onShow();
		}
	}

	private void dispatchOnDismiss() {
		onDismiss();
		if (mOnDismissListener != null) {
			mOnDismissListener.onDismiss();
		}
	}

	protected void onPreShow() {

	}

	protected void onShow() {

	}

	protected void onDismiss() {
	}

	protected boolean dispatchKeyEvent(KeyEvent event){
		return false;
	}

	protected Drawable getDefaultBackgroundDrawable() {
		return new ColorDrawable(Color.TRANSPARENT);
	}

	protected int getDefaultAnimationStyle() {
		return android.R.style.Animation_Toast;
	}

	protected void onInitPopupWindow(PopupWindow pop) {
		pop.setTouchable(true);
		pop.setFocusable(true);
	}

	protected abstract View onCreateContentView();

	public interface OnShowListener {
		public void onShow();
	}

	private class InteriorPopupWindow extends PopupWindow {
		private InteriorPopupWindow(Context context) {
			super(context);
		}
	}

	private class InteriorContentView extends FrameLayout {
		public InteriorContentView(Context context) {
			super(context);
		}

		@Override
		public boolean dispatchKeyEvent(KeyEvent event) {
			if (BasePopupWindow.this.dispatchKeyEvent(event)) {
				return true;
			}
			return super.dispatchKeyEvent(event);
		}
	}
}
