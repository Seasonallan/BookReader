package com.lectek.android.lereader.lib.image;

import java.lang.ref.SoftReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Process;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lectek.android.lereader.lib.cache.image.ImageMemoryCache;
import com.lectek.android.lereader.lib.utils.BitmapUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.lib.utils.StringUtil;

/**
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-9-15
 */
public class ImageLoader extends BaseImageLoader {
	private static final String TEMP_URL_SCHEME = "temp";
	public static final String TEMP_URL_PRE = TEMP_URL_SCHEME + "://";
//	private static final long IMG_MAX_SIZE = 1024 * 256;
	
	private static final String TAG = ImageLoader.class.getSimpleName();
	private ImageMemoryCache imageMemoryCache;
	private Context context;

	public ImageLoader(Context context) {
		imageMemoryCache = ImageMemoryCache.getInstance();
		this.context = context;
	}

	public void setImageViewBitmap( String bookTempPath, String bookTempImagePath,String imageUrl, String imageId,
			final ImageView view, final int defualtResId) {
		setImageViewBitmap(bookTempPath, bookTempImagePath,imageUrl, imageId, view, null, null, defualtResId);
	}
	
	public void setImageViewBitmap(String bookTempPath, String bookTempImagePath, String imageUrl, String imageId,
			final ImageView view,final TextView tv,final String tipStr, final int defualtResId) {
		setImageViewBitmap(bookTempPath, bookTempImagePath,imageUrl, imageId, view,tv,tipStr, null, defualtResId);
	}
	
	public void setImageViewBitmap(String bookTempPath,String bookTempImagePath, String imageUrl, String imageId,
			ImageView view, final View contentView, final int defualtResId) {
		setImageViewBitmap(bookTempPath, bookTempImagePath, imageUrl, imageId, view,null,null, contentView, defualtResId);
	}
	
	public void setImageViewBitmap(String bookTempPath,String bookTempImagePath,String imageUrl, String imageId,
			final ImageView imageView,final TextView tv,final String tipStr, final View contentView, final int defualtResId) {
		
		imageUrl = repairUrl(imageUrl,imageId);
		
		LogUtil.v(TAG, "imageID: " + imageId + "  imageUrl: " + imageUrl);
		String imageName = getImageName(imageUrl,imageId);
		LogUtil.v(TAG, "imageName: " + imageName);
		
		
		imageView.setTag(imageName);//通过设置setTag标记但是这个imageView需要显示的图片是哪一张
		Bitmap bitmap = loadImage(bookTempPath,bookTempImagePath,imageUrl, imageName, new ImageCallback() {//TODO：不是传imageId 吗？通过imageName更具唯一性？
			//图片加载完成后的回调
			@Override
			public void imageLoaded(Bitmap bitmap, String imageUrl, String imageId) {
				if (bitmap == null) {
					LogUtil.v(TAG, "imageLoaded bitmap is null");
				} else {
					LogUtil.v(TAG, "imageLoaded set bitmap");
				}
				View view = null;
				if(imageId.equals(imageView.getTag())){
					view = imageView;
				}
				if (view != null) {
					setImageViewBitmap((ImageView) view, bitmap, defualtResId, tv, tipStr);
				} 
			}
		});
		setImageViewBitmap(imageView, bitmap, defualtResId,tv,tipStr);
	}
	
	private void setImageViewBitmap(ImageView view, Bitmap bitmap,
			int defualtResId,TextView tv, String tipStr) {
		if (bitmap != null) {
			LogUtil.v(TAG, "setImageViewBitmap bitmap is not null");
			view.setImageBitmap(bitmap);
			if(tv != null){
				tv.setVisibility(View.GONE);
			}
		} else {
			LogUtil.v(TAG, "setImageViewBitmap default bitmap");
			view.setImageResource(defualtResId);
			if(tipStr == null){
				tipStr = "";
			}
			if(tv != null){
				tv.setVisibility(View.VISIBLE);
				tv.setText(tipStr);
			}
		}
	}

	@Override
	protected boolean isInCache(String imageId) {
		boolean result = imageMemoryCache.isContains(imageId);
//		LogUtil.v(TAG, "is in cache: " + result);
		return result;
	}

	@Override
	protected Bitmap loadImageFromNetwork(String bookTempPath, String bookTempImagePath,String imageUrl, String imageId) {//TODO:这里的imageId其实是imageName
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		Bitmap bitmap = null;
//		URI uri = null;
//		if(!ApnUtil.isConnected(context)){
//			return bitmap;
//		}
//		
//		try{
//			uri = URI.create(imageUrl);
//		}catch (Exception e) {
//			LogUtil.e(TAG,e);
//		}
//		if(uri == null){
//			return bitmap;
//		}
//
//		if(bitmap == null && imageUrl != null){//TODO：普通的url地址，从网络下载
//			bitmap = BitmapUtil.downloadBitmap(imageUrl, IMG_MAX_SIZE, context);//TODO:OOM问题
//			if (bitmap != null) {
//				LogUtil.e("--imageId--"+imageId);
//				BitmapUtil.saveImage(bookTempPath,bookTempImagePath,imageId, bitmap);
//			}
//		}
		
		if(!StringUtil.isEmpty(imageUrl)){
			bitmap = BitmapUtil.downloadBitmapToLocal(context, bookTempPath, bookTempImagePath, imageUrl, imageId);
		}
		
		return bitmap;
	}

	@Override
	protected Bitmap loadImageFromSdcard(String bookTempImagePath,String imageId) {
//		return BitmapUtil.getImageInSdcard(context, bookTempImagePath,imageId, true);
		return BitmapUtil.getImageInSdcardAutoScale(context, bookTempImagePath, imageId);
	}

	@Override
	protected SoftReference<Bitmap> loadImageInCache(String imageId) {
		Bitmap bitmap = imageMemoryCache.get(imageId);
		if (bitmap != null) {
			return new SoftReference<Bitmap>(bitmap);
		}
		return null;
	}

	@Override
	protected void saveImageToCache(Bitmap bitmap, String imageId) {
		if (bitmap == null || TextUtils.isEmpty(imageId)) {
			return;
		}
		imageMemoryCache.put(imageId, bitmap);
	}
	
	protected String repairUrl(String url,String contentId){
		if(TextUtils.isEmpty(url)){
			url = ImageLoader.TEMP_URL_PRE + contentId;
		}
		return url;
	}
	
	public static String getImageName(String imageUrl, String imageId){
		String imageName = StringUtil.subEndString(imageUrl, "/", ".");
		if (TextUtils.isEmpty(imageName)) {
			imageName = imageId;
		}
		return imageName;
	}
}
