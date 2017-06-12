package com.lectek.android.widget;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
/**
 * 一旦设置了图片之后出发设置null清空图片否则不会再重新计算布局，只会直接按照原来计算出来的大小来绘制新图片
 * @author linyiwei
 *
 */
public class QuickImageView extends ImageView {
	private static final String Tag = QuickImageView.class.getSimpleName();
	
	protected int mResource;
	protected Uri mUri;
	protected Drawable mResourceDrawable;
	protected float mFilletedDegree;
	
	public QuickImageView(Context context) {
		super(context);
		init(context);
	}
	
	public QuickImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public QuickImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	protected void init(Context context) {}
	
	/**
	 * 设置圆角角度
	 * @param degree
	 */
	public void setFilletedCornerDegree(float degree) {
		mFilletedDegree = degree;
	}
	
	/**
	 * 获取圆角角度
	 * @return
	 */
	public float getFilletedCornerDegree() {
		return mFilletedDegree;
	}
	
	@Override
	public void setImageResource(int resId) {
		if (mUri != null || mResource != resId) {
			 mResource = resId;
	         mUri = null;
	         resolveResource();
		}
	}

	@Override
	public void setImageURI(Uri uri) {
		if (mResource != 0 ||
                (mUri != uri &&
                 (uri == null || mUri == null || !uri.equals(mUri)))) {
            mResource = 0;
            mUri = uri;
            resolveResource();
        }
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(mResourceDrawable != null) {
			mResourceDrawable.setCallback(null);
		}
		if(getDrawable() != null) {
			getDrawable().setCallback(null);
		}
	}
	
	@Override
	public void setImageDrawable(Drawable drawable) {//2130837558
		if(mResourceDrawable != null && mResourceDrawable.equals(drawable)){
			mResource = 0;
	        mUri = null;
	        mResourceDrawable = null;
		}
		if(drawable != null){
			Drawable odlDrawable = super.getDrawable();
			if(odlDrawable == null){
				drawable = new QuickDrawable(drawable);
				super.setImageDrawable(drawable);
			}
			else{
				QuickDrawable quickDrawable = (QuickDrawable) odlDrawable;
				quickDrawable.replaceDrawable(drawable);
				invalidate();
			}
		}else{
			super.setImageDrawable(null);
		}
	}
	
	@Override
	public Drawable getDrawable() {
		Drawable drawable = super.getDrawable();
		if(drawable instanceof QuickDrawable){
			return ((QuickDrawable) drawable).getSourceDrawable();
		}
		return drawable;
	}
	
	protected void resolveResource() {
        Resources rsrc = getResources();
        if (rsrc == null) {
            return;
        }
        Drawable d = null;
        if (mResource != 0) {
            try {
                d = rsrc.getDrawable(mResource);
            } catch (Exception e) {
                Log.w("ImageView", "Unable to find resource: " + mResource, e);
                // Don't try again.
                mUri = null;
            }
        } else if (mUri != null) {
            String scheme = mUri.getScheme();
            if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)
            		||ContentResolver.SCHEME_CONTENT.equals(scheme)
                    || ContentResolver.SCHEME_FILE.equals(scheme)) {
            	InputStream is = null;
            	try {
            		is = getContext().getContentResolver().openInputStream(mUri);
                    d = Drawable.createFromStream(is, null);
                } catch (Exception e) {
                    Log.w("ImageView", "Unable to open content: " + mUri, e);
                }finally {
                	if(is != null) {
                		try {
                			is.close();
                		}catch(IOException ioe){}
                	}
                }
            } else {
                d = Drawable.createFromPath(mUri.toString());
            }
    
            if (d == null) {
                System.out.println("resolveUri failed on bad bitmap uri: " + mUri);
                // Don't try again.
                mUri = null;
            }
        } else {
            return;
        }
        mResourceDrawable = d;
        setImageDrawable(d);
    }
	
	private class QuickDrawable extends Drawable implements Callback{
		private Drawable mDrawable;
		private Integer mAlpha = null;
		private ColorFilter mColorFilter = null;
		private Boolean isDither = null;
		private Boolean isFilter = null;
		
		public QuickDrawable(Drawable drawable){
			mDrawable = drawable;
			mDrawable.setCallback(this);
		}
				
		@Override
		public void draw(Canvas canvas) {
			if(mFilletedDegree != 0) {
				
				Bitmap result = Bitmap.createBitmap(getSourceDrawable().getBounds().width(),getSourceDrawable().getBounds().height(), Bitmap.Config.ARGB_8888);
				Canvas resultCanvas = new Canvas(result);
				getSourceDrawable().draw(resultCanvas);
				
				Bitmap mask = Bitmap.createBitmap(getSourceDrawable().getBounds().width(),getSourceDrawable().getBounds().height(), Bitmap.Config.ARGB_8888);
				Canvas maskCanvas = new Canvas(mask);
				Paint paint = new Paint();
				paint.setAntiAlias(true);
			    paint.setColor(0xff424242);
			    RectF rectF = new RectF(getSourceDrawable().getBounds());
			    maskCanvas.drawRoundRect(rectF, mFilletedDegree, mFilletedDegree, paint);
			    
			    paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
			    resultCanvas.drawBitmap(mask, 0, 0, paint);
			    
			    canvas.drawBitmap(result, 0, 0, null);
			    
			    paint.setXfermode(null);
			    
			    mask.recycle();
			    mask = null;
			    result.recycle();
			    result = null;
			    
			  }else {
				  getSourceDrawable().draw(canvas);
			  }
		}
	
		@Override
		public void setAlpha(int alpha) {
			mAlpha = alpha;
			getSourceDrawable().setAlpha(alpha);
		}
		
		@Override
		public void setColorFilter(ColorFilter cf) {
			mColorFilter = cf;
			getSourceDrawable().setColorFilter(cf);
		}

		@Override
		public int getOpacity() {
			return getSourceDrawable().getOpacity();
		}
		
		@Override
		public void setBounds(int left, int top, int right, int bottom) {
			getSourceDrawable().setBounds(left, top, right, bottom);
		}
		
		@Override
		public void setBounds(Rect bounds) {
			getSourceDrawable().setBounds(bounds);
		}

		@Override
		public void setChangingConfigurations(int configs) {
			getSourceDrawable().setChangingConfigurations(configs);
		}

		@Override
		public int getChangingConfigurations() {
			return getSourceDrawable().getChangingConfigurations();
		}

		@Override
		public void setDither(boolean dither) {
			isDither = dither;
			getSourceDrawable().setDither(dither);
		}

		@Override
		public void setFilterBitmap(boolean filter) {
			isFilter = filter;
			getSourceDrawable().setFilterBitmap(filter);
		}

		@Override
		public boolean isStateful() {
			return getSourceDrawable().isStateful();
		}

		@Override
		public boolean setState(int[] stateSet) {
			return getSourceDrawable().setState(stateSet);
		}

		@Override
		public int[] getState() {
			return getSourceDrawable().getState();
		}

		@Override
		public Drawable getCurrent() {
			return getSourceDrawable().getCurrent();
		}

		@Override
		public boolean setVisible(boolean visible, boolean restart) {
			return getSourceDrawable().setVisible(visible, restart);
		}

		@Override
		public Region getTransparentRegion() {
			return getSourceDrawable().getTransparentRegion();
		}

		@Override
		public int getIntrinsicWidth() {
			return getSourceDrawable().getIntrinsicWidth();
		}

		@Override
		public int getIntrinsicHeight() {
			return getSourceDrawable().getIntrinsicHeight();
		}

		@Override
		public int getMinimumWidth() {
			return getSourceDrawable().getMinimumWidth();
		}

		@Override
		public int getMinimumHeight() {
			return getSourceDrawable().getMinimumHeight();
		}

		@Override
		public boolean getPadding(Rect padding) {
			return getSourceDrawable().getPadding(padding);
		}

		@Override
		public Drawable mutate() {
			return getSourceDrawable().mutate();
		}

		@Override
		public void inflate(Resources r, XmlPullParser parser,
				AttributeSet attrs) throws XmlPullParserException, IOException {
			getSourceDrawable().inflate(r, parser, attrs);
		}

		@Override
		public ConstantState getConstantState() {
			return getSourceDrawable().getConstantState();
		}
		
		@Override
		public void invalidateSelf() {
			getSourceDrawable().invalidateSelf();
		}
		 
		@Override
		public void scheduleSelf(Runnable what, long when) {
			getSourceDrawable().scheduleSelf(what, when);
		}

		@Override
		public void unscheduleSelf(Runnable what) {
			getSourceDrawable().unscheduleSelf(what);
		}
		
		@Override
		public void invalidateDrawable(Drawable who) {
			super.invalidateSelf();
		}

		@Override
		public void scheduleDrawable(Drawable who, Runnable what, long when) {
			super.scheduleSelf(what, when);
		}

		@Override
		public void unscheduleDrawable(Drawable who, Runnable what) {
			super.unscheduleSelf(what);
		}

		private Drawable getSourceDrawable(){
			return mDrawable;
		}
		
		public void replaceDrawable(Drawable newDrawable){
			if(newDrawable == null){
				return;
			}
			Rect rect = mDrawable.copyBounds();
			int configs = mDrawable.getChangingConfigurations();
			int level = mDrawable.getLevel();
			int[] mStateSet = mDrawable.getState();
			boolean isVisible = mDrawable.isVisible();
			mDrawable.setCallback(null);
			mDrawable = newDrawable;
			mDrawable.setCallback(this);
			if(mAlpha != null){
				mDrawable.setAlpha(mAlpha);
			}
			if(isDither != null){
				mDrawable.setDither(isDither);
			}
			if(isFilter != null){
				mDrawable.setFilterBitmap(isFilter);
			}
			if(mColorFilter != null){
				mDrawable.setColorFilter(mColorFilter);
			}
			mDrawable.setBounds(rect);
			mDrawable.setChangingConfigurations(configs);
			mDrawable.setLevel(level);
			mDrawable.setState(mStateSet);
			mDrawable.setVisible(isVisible, false);
		}
	}
}
