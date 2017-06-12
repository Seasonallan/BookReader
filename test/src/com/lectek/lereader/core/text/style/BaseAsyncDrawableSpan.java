package com.lectek.lereader.core.text.style;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.lectek.lereader.core.text.html.DataProvider;
import com.lectek.lereader.core.text.html.DataProvider.DrawableContainer;
import com.lectek.lereader.core.util.LogUtil;

/**
 * 异步图片样式
 * @author lyw
 */
public class BaseAsyncDrawableSpan extends ReplacementSpan implements ResourceSpan{
    private static final String TAG = BaseAsyncDrawableSpan.class.getSimpleName();
	private static HashMap<String,Rect> mCacheImgRect = new HashMap<String,Rect>();
	private static final int MAX_CACHE_IMG_RECT_SIZE = 500;

    protected DataProvider mDataProvider;
    private String mSrc;
    private Rect mImageRect;
    private Drawable mDrawable;
    private SoftReference<Drawable> mCacheDrawable;

    private int mPresetWidth;
    private int mPresetHeight;
	private InteriorDrawableContainer mDrawableContainer;

    /**
     * @param verticalAlignment one of {@link #ALIGN_BOTTOM} or {@link #ALIGN_BASELINE}.
     */
    public BaseAsyncDrawableSpan(String src,float presetWidth,float presetHeight,DataProvider dataProvider) {
        mDataProvider = dataProvider;
        mSrc = src;
        mPresetWidth = (int)presetWidth;
        mPresetHeight = (int)presetHeight;
		mCacheDrawable = new SoftReference<Drawable>(null);
    }
    /**
	 * @return the mPresetWidth
	 */
	public int getPresetWidth() {
		return mPresetWidth;
	}
	/**
	 * @param mPresetWidth the mPresetWidth to set
	 */
	public void setPresetWidth(int mPresetWidth) {
		this.mPresetWidth = mPresetWidth;
	}
	/**
	 * @return the mPresetHeight
	 */
	public int getPresetHeight() {
		return mPresetHeight;
	}
	/**
	 * @param mPresetHeight the mPresetHeight to set
	 */
	public void setPresetHeight(int mPresetHeight) {
		this.mPresetHeight = mPresetHeight;
	}

	public String getSrc(){
		return mSrc;
    }
    
    protected Rect onGetSize(Paint paint, CharSequence text,
            int start, int end, int maxW,int maxH,Rect rect,Rect container){
		return rect;
    }
    
    @Override
    public void getSize(Paint paint, CharSequence text,
                         int start, int end, int maxW,int maxH,Rect container) {
    	Rect rect = onGetSize(paint, text, start, end, maxW, maxH, getImageRect(), container);
    	if(rect != null){
            container.set(rect);
    	}
    }
    
    @Override
    public void draw(Canvas canvas, final CharSequence text, final int start,
			final int end, int left, int top, int right, int bottom,int maxW,int maxH, Paint paint) {
    	Drawable b = getCachedDrawable();
        if(b == null){
        	if(mDrawableContainer != null){
        		b = mDrawableContainer.getDefaultDrawable();
        	}else{
        		mDrawableContainer = new InteriorDrawableContainer(this, text, start, end);
            	b = mDataProvider.getDrawable(mSrc, mDrawableContainer);
            	mDrawableContainer.setDefaultDrawable(b);
        	}
        }
        int w = right - left;
        int h = bottom - top;
        setMeasureBounds(left, top, right, bottom, w, h, getImageRect(), paint, b);
        b.draw(canvas);
    }

    protected void onDraw(){
    	
    }
    
    protected void setMeasureBounds(int left, int top, int right, int bottom,int w,int h,Rect rect, Paint paint,Drawable b){
    	b.setBounds(left, top, right, bottom);
    }
    
    private Rect getImgSize(String src){
    	Rect rect = null;
    	rect = mCacheImgRect.get(src);
    	if(rect == null){
    		try {
    			InputStream is = mDataProvider.getDataStream(src);
    			if (is != null) {
    				rect = new Rect();
    				Options opts = new BitmapFactory.Options();
    				opts.inJustDecodeBounds = true;
    				opts.inScreenDensity = mDataProvider.getContext().getResources()
    						.getDisplayMetrics().densityDpi;
    				opts.inTargetDensity = mDataProvider.getContext().getResources()
    						.getDisplayMetrics().densityDpi;
    				opts.inDensity = mDataProvider.getContext().getResources()
    						.getDisplayMetrics().densityDpi;
    				BitmapFactory.decodeStream(is, null, opts);
    				is.close();
    				rect.set(0, 0, opts.outWidth, opts.outHeight);
    				if(mCacheImgRect.size() > MAX_CACHE_IMG_RECT_SIZE){
    					mCacheImgRect.clear();
    				}
    				mCacheImgRect.put(src, new Rect(rect));
    				return rect;
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
		return rect;
    }
    
    public Rect getImageRect(){
    	if(mImageRect != null){
    		return mImageRect;
    	}
    	mImageRect = getImgSize(mSrc);
    	if(mImageRect != null){
    		int width = mImageRect.width();
    		int height = mImageRect.height();
			if(mImageRect.width() > mImageRect.height()){
				if(mPresetWidth > 0){
					width = mPresetWidth;
					height = mPresetWidth * mImageRect.height() / mImageRect.width();
				}else if(mPresetHeight > 0){
					width = mPresetHeight * mImageRect.width() / mImageRect.height();
					height = mPresetHeight;
				}
			}else{
				if(mPresetHeight > 0){
					width = mPresetHeight * mImageRect.width() / mImageRect.height();
					height = mPresetHeight;
				}else if(mPresetWidth > 0){
					width = mPresetWidth;
					height = mPresetWidth * mImageRect.height() / mImageRect.width();
				}
			}
			mImageRect.set(0, 0, width, height);
    	}
		return mImageRect;
    }
    
    private void setCachedDrawable(Drawable drawable) {
    	mDrawable = drawable;
    }
    
    private Drawable getCachedDrawable() {
    	if(mDrawable == null){
    		mDrawable = mCacheDrawable.get();
    	}
        return mDrawable;
    }

	@Override
	public void release() {
		LogUtil.i(TAG, "release");
		if(mDrawable != null){
			mCacheDrawable = new SoftReference<Drawable>(mDrawable);
			mDrawable = null;
		}
		if(mDrawableContainer != null){
    		mDrawableContainer.release();
    		mDrawableContainer = null;
    	}
	}

	public Drawable getDrawable(){
		return mDrawable;
	}
	
	private static class InteriorDrawableContainer implements DrawableContainer{
		private BaseAsyncDrawableSpan mSpan;
		private CharSequence mText;
		private int mStart;
		private int mEnd;
		private Drawable mDefaultDrawable;
		private InteriorDrawableContainer(BaseAsyncDrawableSpan span,CharSequence text,int start,int end){
			mSpan = span;
			mText = text;
			mStart = start;
			mEnd = end;
		}
		
		@Override
		public void setDrawable(Drawable drawable) {
			if(mSpan == null){
				return;
			}
			if(drawable != null){
				mSpan.setCachedDrawable(drawable);
			}else {
				mSpan.setCachedDrawable(getDefaultDrawable());
			}
			
			if(mText instanceof SpannableStringBuilder){
				((SpannableStringBuilder) mText).sendSpanChanged(mSpan, mStart, mEnd,  mStart, mEnd);
			}
			mSpan.mDrawableContainer = null;
			release();
		}
		
		private void setDefaultDrawable(Drawable drawable){
			mDefaultDrawable = drawable;
		}
		
		private Drawable getDefaultDrawable(){
			return mDefaultDrawable;
		}

		private void release() {
			mSpan = null;
			mText = null;
			mStart = 0;
			mEnd = 0;
			mDefaultDrawable = null;
		}

		@Override
		public boolean isInvalid() {
			return mSpan == null;
		}
	}
}
