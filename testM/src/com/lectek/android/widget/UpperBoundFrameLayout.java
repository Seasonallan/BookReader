package com.lectek.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class UpperBoundFrameLayout extends FrameLayout {
	private int mMaxHeight = Integer.MAX_VALUE;
	private int mMeasureMaxHeight = -1;
	public UpperBoundFrameLayout(Context context) {
		super(context);
	}
	
	public UpperBoundFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public UpperBoundFrameLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if(getMeasuredHeight() > mMaxHeight && mMaxHeight != mMeasureMaxHeight){
			ViewGroup.LayoutParams lp = getLayoutParams();
			if(lp != null){
				mMeasureMaxHeight = mMaxHeight;
				lp.height = mMaxHeight;
				requestLayout();
			}
		}
	}
	
	public void setMaxHeight(int maxHeight){
		mMaxHeight = maxHeight;
	}
}
