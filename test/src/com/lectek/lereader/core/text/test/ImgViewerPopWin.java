package com.lectek.lereader.core.text.test;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.lectek.lereader.core.text.test.ImgViewer.ActionCallback;

public class ImgViewerPopWin extends PopupWindow implements ActionCallback{
	private ImgViewer mImgViewer;
	private FrameLayout mBackgroundView;
	private Context mContext;
	public ImgViewerPopWin(Context context) {
		this(context,"#E0000000");
	}
	
	public ImgViewerPopWin(Context context,String background) {
		super(context);
		mContext = context.getApplicationContext();
		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.FILL_PARENT);
		setBackgroundDrawable(null);
		setFocusable(true);
		mImgViewer = new ImgViewer(context);
		mImgViewer.setActionCallback(this);
		mBackgroundView = new FrameLayout(context);
		mBackgroundView.setBackgroundDrawable(new ColorDrawable(Color.parseColor(background)));
		mBackgroundView.setAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
		BackgroundLayout contentView = new BackgroundLayout(context);
		contentView.addView(mBackgroundView, new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		contentView.addView(mImgViewer,new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		setContentView(contentView);
	}
	
	public void showImgViewer(final Drawable d,final Rect location,final View parent){
		if(!isShowing()){
			setWidth(WindowManager.LayoutParams.FILL_PARENT);
			setHeight(WindowManager.LayoutParams.FILL_PARENT);
			showAtLocation(parent, Gravity.LEFT | Gravity.TOP,0,0);
		}
		mImgViewer.setDrawable(d,location);
		mBackgroundView.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
	}
	
	public void hideImgViewer(){
		mImgViewer.setDrawable(null);
		if(isShowing()){
			dismiss();
		}
	}

	@Override
	public void onEndTransitionAnim(boolean isEnter) {
		if(!isEnter){
			mImgViewer.post(new Runnable() {
				@Override
				public void run() {
					hideImgViewer();
				}
			});
		}
	}

	@Override
	public void onStartTransitionAnim(boolean isEnter) {
		if(!isEnter){
			Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_out);
			animation.setFillAfter(true);
			mBackgroundView.startAnimation(animation);
		}
	}
	
	private class BackgroundLayout extends RelativeLayout{
		public BackgroundLayout(Context context) {
			super(context);
		}

		@Override
		public boolean dispatchKeyEvent(KeyEvent event) {
			if(event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK){
				if(isShowing()){
					if(mImgViewer.canExit()){
						mImgViewer.exit();
					}else{
						hideImgViewer();
					}
				}
			}
			return true;
		}
	}
}
