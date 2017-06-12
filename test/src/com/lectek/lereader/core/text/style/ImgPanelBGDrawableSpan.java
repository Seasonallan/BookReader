package com.lectek.lereader.core.text.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class ImgPanelBGDrawableSpan{
	public static final int GRAVITY_HORIZONTAL_LEFT = -1;
	public static final int GRAVITY_HORIZONTAL_RIGHT = -2;
	public static final int GRAVITY_VERTICAL_TOP = -1;
	public static final int GRAVITY_VERTICAL_BOTTOM = -2;
	public static final int GRAVITY_CENTER = -3;

	private int mMarginHorizontal;
	private int mMarginVertical;
	private String mSrc;
	private BaseAsyncDrawableSpan mDrawableSpan;
	
	public static Integer getGravity(String str){
		if("left".equals(str)){
			return GRAVITY_HORIZONTAL_LEFT;
		}else if("right".equals(str)){
			return GRAVITY_HORIZONTAL_RIGHT;
		}else if("top".equals(str)){
			return GRAVITY_VERTICAL_TOP;
		}else if("bottom".equals(str)){
			return GRAVITY_VERTICAL_BOTTOM;
		}else if("center".equals(str)){
			return GRAVITY_CENTER;
		}
		return Integer.MIN_VALUE;
	}
	
	public ImgPanelBGDrawableSpan(int marginHorizontal,int marginVertical,String src) {
		mMarginHorizontal = marginHorizontal;
		mMarginVertical = marginVertical;
		mSrc = src;
	}
	
	public String getSrc(){
		return mSrc;
	}
	
	public void setDrawableSpan(BaseAsyncDrawableSpan drawableSpan){
		mDrawableSpan = drawableSpan;
	}
	
	public final void drawBG(Canvas canvas, final CharSequence text, final int start,
			final int end, int left, int top, int right, int bottom,int translateDy, Paint paint){
		canvas.save();
        canvas.clipRect(left, top, right, bottom);
		int maxW = right - left;
		int maxH = bottom - top;
		Rect rect = mDrawableSpan.getImageRect();
		int imgW = rect.width();
		int imgH = rect.height();
		if(mMarginHorizontal >= 0){
			left += mMarginHorizontal;
		}else if(mMarginHorizontal == GRAVITY_HORIZONTAL_RIGHT){
			left = right - imgW;
		}else if(mMarginHorizontal == GRAVITY_CENTER){
			if(maxW > imgW){
				left += (maxW - imgW) / 2;
				right -= (maxW - imgW) / 2;
			}else{
				left -= (imgW - maxW) / 2;
				right += (imgW - maxW) / 2;
			}
		}
		if(mMarginVertical >= 0){
			top += mMarginVertical;
		}else if(mMarginVertical == GRAVITY_VERTICAL_BOTTOM){
			top = bottom - imgH;
		}else if(mMarginVertical == GRAVITY_CENTER){
			if(maxH > imgH){
				top += (maxH - imgH) / 2;
				bottom -= (maxH - imgH) / 2;
			}else{
				top -= (imgH - maxH) / 2;
				bottom += (imgH - maxH) / 2;
			}
		}
		canvas.translate(0, translateDy);
		mDrawableSpan.draw(canvas, text, start, end, left, top, right, bottom,maxW,maxH, paint);
		canvas.restore();
	}
}
