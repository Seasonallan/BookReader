package com.lectek.android.lereader.lib.image;

import java.lang.ref.SoftReference;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.lectek.android.lereader.lib.thread.ThreadFactory;
import com.lectek.android.lereader.lib.thread.ThreadPoolFactory;
import com.lectek.android.lereader.lib.utils.LogUtil;

/**
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-9-15
 */
public abstract class BaseImageLoader {

	public Bitmap loadImage(final String bookTempPath, final String bookTempImagePath,final String imageUrl, final String imageId,
			final ImageCallback callback) {
		if (isInCache(imageId)) {
			SoftReference<Bitmap> softReference = loadImageInCache(imageId);
			if (softReference != null && softReference.get() != null) {
				return softReference.get();
			}
		}
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				LogUtil.i("AsyncImageLoader", "handleMessage loadImage bitmap handle");
				LogUtil.i("AsyncImageLoader", "handleMessage imageID: " + imageId + "  imageUrl: " + imageUrl);
				callback.imageLoaded((Bitmap) msg.obj, imageUrl, imageId);
			}
		};
        ThreadFactory.createTerminableThreadInPool(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = loadImageFromSdcard( bookTempImagePath,imageId);
                if (bitmap != null) {
                    saveImageToCache(bitmap, imageId);
                    handler.sendMessage(handler.obtainMessage(0, bitmap));
                } else{
                    LogUtil.i("AsyncImageLoader", "loadImage bitmap is null");
                    LogUtil.i("AsyncImageLoader", "loadImage bitmap from network");
                    LogUtil.i("AsyncImageLoader", "imageID: " + imageId + "  imageUrl: " + imageUrl);
                    bitmap = loadImageFromNetwork(bookTempPath, bookTempImagePath,imageUrl, imageId);
                    if (bitmap != null) {
                        LogUtil.i("AsyncImageLoader", "load bitmap success to send message");
                        saveImageToCache(bitmap, imageId);
                        handler.sendMessage(handler.obtainMessage(0, bitmap));
                    } else {
                        LogUtil.i("AsyncImageLoader", "loadImage bitmap failed");
                    }
                }
            }
        }, ThreadPoolFactory.getImageDownloaderPool()).start();
		return null;
	}

	protected abstract boolean isInCache(String imageId);

	protected abstract SoftReference<Bitmap> loadImageInCache(String imageId);

	protected abstract void saveImageToCache(Bitmap bitmap, String imageId);

	protected abstract Bitmap loadImageFromSdcard(String bookTempImagePath, String imageId);

	protected abstract Bitmap loadImageFromNetwork(String bookTempPath, String bookTempImagePath,String imageUrl,
			String imageId);

	public interface ImageCallback {
		public void imageLoaded(Bitmap bitmap, String imageUrl, String imageId);
	}

}