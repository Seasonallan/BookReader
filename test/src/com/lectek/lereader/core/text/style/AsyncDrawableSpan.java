package com.lectek.lereader.core.text.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.lectek.lereader.core.text.html.DataProvider;

/**
 * 异步图片样式
 * @author lyw
 */
public class AsyncDrawableSpan extends BaseAsyncDrawableSpan{
    @SuppressWarnings("unused")
	private static final String TAG = AsyncDrawableSpan.class.getSimpleName();
    private String mTitle;
    private String mDrawTitleStr;
    private boolean isFullScreen;
    private int mPaddingLeft;
	private int mPaddingRight;
	private int mPaddingTop;
	private int mPaddingBottom;
    protected final int mVerticalAlignment;
    private FontMetrics mFontMetrics;
	private float mEllipsizeWidth;
    /**
     * A constant indicating that the bottom of this span should be aligned
     * with the bottom of the surrounding text, i.e., at the same level as the
     * lowest descender in the text.
     */
    public static final int ALIGN_BOTTOM = 0;
    
    /**
     * A constant indicating that the bottom of this span should be aligned
     * with the baseline of the surrounding text.
     */
    public static final int ALIGN_BASELINE = 1;

    public AsyncDrawableSpan(String src,String title,boolean isFullScreen,float presetWidth,float presetHeight,DataProvider dataProvider){
        this(src,title,isFullScreen,presetWidth,presetHeight,dataProvider,ALIGN_BOTTOM);
    }
    /**
     * @param verticalAlignment one of {@link #ALIGN_BOTTOM} or {@link #ALIGN_BASELINE}.
     */
    protected AsyncDrawableSpan(String src,String title,boolean isFullScreen,float presetWidth,float presetHeight,DataProvider dataProvider,int verticalAlignment) {
        super(src, presetWidth, presetHeight, dataProvider);
        mFontMetrics = new FontMetrics();
    	this.isFullScreen = isFullScreen;
        mVerticalAlignment = verticalAlignment;
        mDataProvider = dataProvider;
        mTitle = title;
        mPaddingLeft = 0;
		mPaddingRight = 0;
		mPaddingBottom = 0;
		mPaddingTop = 0;
		mEllipsizeWidth = -1;
    }
    
    public void setTitle(String title){
    	mTitle = title;
    }
    /**
     * Returns the vertical alignment of this span, one of {@link #ALIGN_BOTTOM} or
     * {@link #ALIGN_BASELINE}.
     */
    public int getVerticalAlignment() {
        return mVerticalAlignment;
    }
    
    public boolean isFullScreen(){
		return isFullScreen;
    }
	/**
	 * @return the mPaddingLeft
	 */
	public int getPaddingLeft() {
		return mPaddingLeft;
	}
	/**
	 * @param mPaddingLeft the mPaddingLeft to set
	 */
	public void setPaddingLeft(int mPaddingLeft) {
		this.mPaddingLeft = mPaddingLeft;
	}
	/**
	 * @return the mPaddingRight
	 */
	public int getPaddingRight() {
		return mPaddingRight;
	}
	/**
	 * @param mPaddingRight the mPaddingRight to set
	 */
	public void setPaddingRight(int mPaddingRight) {
		this.mPaddingRight = mPaddingRight;
	}
	/**
	 * @return the mPaddingTop
	 */
	public int getPaddingTop() {
		return mPaddingTop;
	}
	/**
	 * @param mPaddingTop the mPaddingTop to set
	 */
	public void setPaddingTop(int mPaddingTop) {
		this.mPaddingTop = mPaddingTop;
	}
	/**
	 * @return the mPaddingBottom
	 */
	public int getPaddingBottom() {
		return mPaddingBottom;
	}
	/**
	 * @param mPaddingBottom the mPaddingBottom to set
	 */
	public void setPaddingBottom(int mPaddingBottom) {
		this.mPaddingBottom = mPaddingBottom;
	}
	
	@Override
	public void getSize(Paint paint, CharSequence text, int start, int end,
			int maxW, int maxH, Rect container) {
		super.getSize(paint, text, start, end, maxW, maxH, container);
		if(!isFullScreen){
			int bottom = 0;
			if(TextUtils.isEmpty(mTitle)){
				bottom = container.bottom + mPaddingTop + mPaddingBottom;
    		}else{
    			bottom = (int) (container.bottom + mPaddingTop + mPaddingBottom + paint.getFontSpacing() * 1.2f);
    		}
			container.set(0, 0, container.right + mPaddingLeft + mPaddingRight
					,bottom);
		}
	}
	
	@Override
	protected Rect onGetSize(Paint paint, CharSequence text,
            int start, int end, int maxW,int maxH,Rect rect, Rect container) {
		if(isFullScreen){
    		container.set(0, 0, maxW, maxH);
    		return null;
    	}else{
    		return onMeasureBounds(rect,maxW,maxH);
    	}
	}
    
	@Override
	public void draw(Canvas canvas, CharSequence text, int start, int end,
			int left, int top, int right, int bottom,int maxW,int maxH, Paint paint) {
        if(!isFullScreen){
        	left += mPaddingLeft;
            right -= mPaddingRight;
            top += mPaddingTop;
            bottom -= mPaddingBottom;
            if(!TextUtils.isEmpty(mTitle)){
            	if(mEllipsizeWidth == -1){
                    mEllipsizeWidth = paint.measureText("...");
            	}
            	paint.setTextSize(paint.getTextSize() * 0.8f);
    			paint.setTextAlign(Align.CENTER);
				paint.getFontMetrics(mFontMetrics);
				int width = right - left;
				float startX = width / 2 + left;
				float startY = bottom - mFontMetrics.bottom;
				if(mDrawTitleStr == null){
					int titleEnd = paint.breakText(mTitle, true, maxW - mEllipsizeWidth, null);
					mDrawTitleStr = mTitle.substring(0, titleEnd);
					if(titleEnd != mTitle.length()){
						mDrawTitleStr += "...";
					}
				}
				canvas.drawText(mDrawTitleStr,startX,startY, paint);
                bottom -= paint.getFontSpacing() * 1.2f;
    		}
        }
		super.draw(canvas, text, start, end, left, top, right, bottom, maxW, maxH, paint);
	}

	@Override
    protected void setMeasureBounds(int left, int top, int right, int bottom,int w,int h,Rect rect, Paint paint,Drawable b){
		rect = onMeasureBounds(rect,w,h);
    	int transT = 0;
        int transL = 0;
        int transR = 0;
        int transB = 0;
        int imgW = rect.width();
        int imgH = rect.height();
        if(isFullScreen){
        	if(imgW == 0){
        		imgW = w;
        	}
        	if(imgH == 0){
        		imgH = h;
        	}
        	int gapW = imgH * (imgW - w) / imgW;
			int gapH = imgH - h;
			if(gapW > gapH){
				imgH = imgH * w / imgW;
				imgW = w;
			}else{
				imgW = imgW * h / imgH;
				imgH = h;
			}
			transL = (w - imgW) / 2;
	        transR = transL + imgW;
			transT = (h - imgH) / 2;
            transB = transT + imgH;
        }else{
            transL = left + (w - imgW) / 2;
            transR = transL + imgW;
        	transT = bottom - imgH;
            if (mVerticalAlignment == ALIGN_BASELINE) {
                transT -= paint.getFontMetricsInt().descent;
            }
            transB = transT + imgH;
        }
    	super.setMeasureBounds(transL, transT, transR, transB, w, h, rect, paint, b);
    }
    
    protected Rect onMeasureBounds(Rect bounds,int w,int h){
    	if(bounds != null){
			int imgW = bounds.width();
			int imgH = bounds.height();
			if(imgW > w || imgH > h){
	        	int gapW = imgH * (imgW - w) / imgW;
				int gapH = imgH - h;
				if(gapW > gapH){
					imgH = imgH * w / imgW;
					imgW = w;
				}else{
					imgW = imgW * h / imgH;
					imgH = h;
				}
			}
			bounds.set(0, 0, imgW, imgH);
		}else{
			bounds = new Rect();
		}
		return bounds;
    }
}

