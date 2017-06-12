package com.lectek.android.lereader.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TabWidget;

import com.lectek.android.lereader.lib.utils.DimensionsUtil;

public class SlideTabWidget extends TabWidget {
	private Context mContext;
	private Drawable mDrawable = null;
	private int mPaddingWidth = -1;
	private int mCotentId = 0;
	private int mDrawY = -1;
	private int mDrawX = -1;
	private int mDrawWidth = -1;
	private int mDrawHeight = -1;
	private int mCurrentIndex = -1;
	private int mPaddingLeft = 0;
	private int mOffSetX = -1;
	private int mOffSetY = -1;
	private boolean isInit = false;
	private boolean showAtTop = false;//需求要显示在顶部，默认情况下显示在TabWidget底部
	
	public SlideTabWidget(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	public SlideTabWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	public SlideTabWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	private void init(){
		mCotentId = android.R.id.title;
		mDrawHeight = DimensionsUtil.dip2px(3, mContext);
		mPaddingWidth = DimensionsUtil.dip2px(10, mContext);
		mOffSetY = 0;
		mOffSetX = 0;
		onInit();
	}
	
	protected void onInit(){
		
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if(changed){
			isInit = false;
		}
	}

	@Override
	public void setCurrentTab(int index) {
		mCurrentIndex = index;
		super.setCurrentTab(index);
		if(!isInit){
			postInvalidate();
		}
	}

	public void initialize(Drawable indicatorDrawable){
		initialize(0, DimensionsUtil.dip2px(3, mContext), indicatorDrawable);
	}
	
	public void initialize(int indicatorHeight,Drawable indicatorDrawable){
		initialize(0, indicatorHeight, indicatorDrawable);
	}
	
	public void initialize(int cotentId,int indicatorHeight,Drawable indicatorDrawable){
		if(indicatorHeight < 0){
			indicatorHeight = 0;
		}
		mDrawable = indicatorDrawable;
		mCotentId = cotentId;
		mDrawHeight = indicatorHeight;
		isInit = false;
		postInvalidate();
	}
	
	public void dispatchPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		if(!isInit){
			postInvalidate();
			return;
		}
		View currentTabView = getChildTabViewAt(position);
		if(currentTabView == null){
			return;
		}
		int currentTabViewWidth = currentTabView.getWidth();       	
    	int startLeftX = currentTabView.getLeft() + mPaddingLeft;
    	int moveX = (int) Math.ceil(positionOffset * currentTabViewWidth);
    	mDrawX = startLeftX + moveX;
		postInvalidate();
	}
	
	public void setIndicatorPaddingWidth(int padding){
		mPaddingWidth = padding;
		isInit = false;
		postInvalidate();
	}
	
	public void setIndicatorOffsetX(int offsetX){
		mOffSetX = offsetX;
		isInit = false;
		postInvalidate();
	}
	
	public void setIndicatorOffsetY(int offsetY){
		mOffSetY = offsetY;
		isInit = false;
		postInvalidate();
	}

	private boolean initDraw(){
		if(isInit){
			return true;
		}
		if(mDrawable == null){
			mDrawable = new ColorDrawable(Color.BLACK);
		}
		int childCount = this.getChildCount();
		int minCotentWidth = Integer.MAX_VALUE;
		int maxCotentHeight = -1;
		View currentView = null;
		for(int i = 0; i < childCount; i++){
			View view = this.getChildTabViewAt(i).findViewById(mCotentId);
			if(view == null){
				view = this.getChildTabViewAt(i);
			}
			if(minCotentWidth > view.getWidth()){
				minCotentWidth = view.getWidth();
			}
			if(maxCotentHeight < view.getHeight()){
				maxCotentHeight = view.getHeight();
			}
			if(mCurrentIndex == i){
				currentView = getChildTabViewAt(mCurrentIndex);
			}
		}
		if(currentView != null){
			if(mDrawable.getIntrinsicWidth() > 0 && mDrawable.getIntrinsicHeight() > 0){
				mDrawWidth = mDrawable.getIntrinsicWidth();
				mDrawHeight = mDrawable.getIntrinsicHeight();
			}else{
				mDrawWidth = minCotentWidth + mPaddingWidth * 2;
			}
			if(mDrawWidth > minCotentWidth){
				mDrawWidth = minCotentWidth;
			}
			if(mDrawHeight > maxCotentHeight){
				mDrawHeight = maxCotentHeight;
			}
			mPaddingLeft = ( currentView.getWidth() - mDrawWidth ) / 2;
			mDrawX = currentView.getLeft() + mPaddingLeft;
			if(isShowAtTop()){
				mDrawY = currentView.getTop();
			}else {
				mDrawY = currentView.getBottom() - mDrawHeight;
			}	
		}else{
			return false;
		}
		if(mDrawWidth <= 0 || mDrawHeight <= 0){
			return false;
		}
		isInit = true;
		return true;
	}
	
	@Override
	public void dispatchDraw(Canvas canvas) {
		if(initDraw()){
			canvas.save();
			mDrawable.setBounds(mDrawX + mOffSetX
					, mDrawY + mOffSetY
					, mDrawX + mDrawWidth + mOffSetX
					, mDrawY + mDrawHeight + mOffSetY);
			canvas.clipRect(mDrawable.getBounds());
			mDrawable.draw(canvas);
			canvas.restore();
		}
		super.dispatchDraw(canvas);
	}

	public boolean isShowAtTop() {
		return showAtTop;
	}

	public void setShowAtTop(boolean showAtTop) {
		this.showAtTop = showAtTop;
	}
	
}
