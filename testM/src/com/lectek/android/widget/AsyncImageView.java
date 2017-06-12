package com.lectek.android.widget;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.lectek.android.lereader.lib.image.BaseImageLoader.ImageCallback;
import com.lectek.android.lereader.lib.image.ImageLoader;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.utils.Constants;
/**
 * 异步图片加载ImageView
 * @author linyiwei
 */
public class AsyncImageView extends QuickImageView{
	
	private String mUrl;
	private int mDefaultImgRes;
	private ImageLoader mImageLoader;
	private InteriorImageCallback mInteriorImageCallback;
	private boolean hasLoadNetImg = false;
	
	public AsyncImageView(Context context) {
		super(context);
	}
	
	public AsyncImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void init(Context context) {
		mImageLoader = new ImageLoader(getContext());
		mInteriorImageCallback = new InteriorImageCallback(this, getContext());
	}
	
	public void setDefaultImgRes(int defaultImgRes){
		mDefaultImgRes = defaultImgRes;
		if(!hasLoadNetImg){
			setImageResource(mDefaultImgRes);
		}
	}
	
//	@Override
//	protected void onAttachedToWindow() {
//		super.onAttachedToWindow();
//		
//		if(!TextUtils.isEmpty(mUrl)) {
//			setImageUrl(mUrl);
//		}
//	}
//	
//	@Override
//	protected void onDetachedFromWindow() {
//		super.onDetachedFromWindow();
//		if(!TextUtils.isEmpty(mUrl)) {
//			clearResource();
//		}
//	}
	
	public void setImageUrl(String url){
		mUrl = url;
		Bitmap bitmap = null;
		hasLoadNetImg = false;
		if(mUrl != null){
			bitmap = mImageLoader.loadImage(Constants.BOOKS_TEMP, Constants.BOOKS_TEMP_IMAGE,mUrl, String.valueOf(mUrl.hashCode()),mInteriorImageCallback);
		}
		if(bitmap == null){
			if(mDefaultImgRes > 0){
				setImageResource(mDefaultImgRes);
			}else{
				setImageBitmap(null);
			}
		}else{
			hasLoadNetImg = true;
			setImageBitmap(bitmap);
		}
	}
	
	private static class InteriorImageCallback implements ImageCallback{
		private WeakReference<AsyncImageView> mImageView;
		private InteriorImageCallback(AsyncImageView imageView, Context aContext){
			mImageView = new WeakReference<AsyncImageView>(imageView);
		}
		
		@Override
		public void imageLoaded(Bitmap bitmap, String imageUrl, String imageId) {
			AsyncImageView imageView = mImageView.get();
			if(imageView == null || imageUrl == null){
				return;
			}
			if(imageUrl.equals(imageView.mUrl) && bitmap != null){
				imageView.setImageBitmap(bitmap);
				imageView.hasLoadNetImg = true;
			}
		}
	}
}
