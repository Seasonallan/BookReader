package com.lectek.lereader.core.text.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import com.lectek.lereader.core.text.html.DataProvider;

public class ClickAsyncDrawableSpan extends AsyncDrawableSpan implements ClickActionSpan{
    private static final int[] SHADOW_COLORS = new int[] { 0x40011111,0x111111 };

	private GradientDrawable mShadowDrawableL;
	private GradientDrawable mShadowDrawableR;
	private GradientDrawable mShadowDrawableT;
	private GradientDrawable mShadowDrawableB;

	public ClickAsyncDrawableSpan(String src,String title,boolean isFullScreen, float presetWidth, float presetHeight,
			DataProvider dataProvider) {
		super(src,title, isFullScreen, presetWidth, presetHeight, dataProvider);
		mShadowDrawableL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, SHADOW_COLORS);
		mShadowDrawableR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, SHADOW_COLORS);
		mShadowDrawableT = new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP, SHADOW_COLORS);
		mShadowDrawableB = new GradientDrawable(
				GradientDrawable.Orientation.TOP_BOTTOM, SHADOW_COLORS);
	}

	@Override
	protected void setMeasureBounds(int left, int top, int right, int bottom,int w,int h,Rect rect, Paint paint,Drawable b) {
		super.setMeasureBounds(left, top, right, bottom, w, h, rect, paint, b);
		if(b != null){
        	Rect drawRect = b.getBounds();
        	mShadowDrawableL.setBounds(drawRect.left - 2, drawRect.top, drawRect.left, drawRect.bottom + 3);
        	mShadowDrawableR.setBounds(drawRect.right, drawRect.top, drawRect.right + 5, drawRect.bottom + 3);
        	mShadowDrawableT.setBounds(drawRect.left, drawRect.top - 2, drawRect.right + 3, drawRect.top);
        	mShadowDrawableB.setBounds(drawRect.left, drawRect.bottom, drawRect.right, drawRect.bottom + 5);
        }
	}
	
	@Override
	public void draw(Canvas canvas, CharSequence text, int start, int end,
			int left, int top, int right, int bottom,int maxW,int maxH,Paint paint) {
		super.draw(canvas, text, start, end, left, top, right, bottom, maxW, maxH, paint);
		if(getDrawable() != null){
			mShadowDrawableL.draw(canvas);
	    	mShadowDrawableR.draw(canvas);
	    	mShadowDrawableT.draw(canvas);
	    	mShadowDrawableB.draw(canvas);
		}
	}
	
	@Override
	public boolean isClickable() {
		return getDrawable() != null && !isFullScreen();
	}

	@Override
	public void checkContentRect(RectF rect) {
		if(getDrawable() != null){
			Rect bounds = getDrawable().getBounds();
			if(bounds.width() > 0 && bounds.height() > 0){
				rect.set(bounds);
			}
		}
	}
}
