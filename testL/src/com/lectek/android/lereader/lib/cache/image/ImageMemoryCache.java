package com.lectek.android.lereader.lib.cache.image;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;

import com.lectek.android.lereader.lib.utils.LogUtil;

/**
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-9-15
 */
public class ImageMemoryCache {

	private static final int HARD_CACHE_CAPACITY = 15;

	private static ImageMemoryCache instance;
	private HashMap<String, SoftReference<Bitmap>> mHardBitmapCache;
	/**
	 *当mHardBitmapCache的key大于30的时候，会根据LRU算法把最近没有被使用的key放入到这个缓存中。
	 * Bitmap使用了SoftReference，当内存空间不足时，此cache中的bitmap会被垃圾回收掉
	 */
	private ConcurrentHashMap<String, SoftReference<Bitmap>> mSoftBitmapCache;

	private ImageMemoryCache() {
		mHardBitmapCache = new LinkedHashMap<String, SoftReference<Bitmap>>(
				HARD_CACHE_CAPACITY / 2, 0.75f, true) {
			private static final long serialVersionUID = 8873184667639123345L;
			@Override
			protected boolean removeEldestEntry(
					LinkedHashMap.Entry<String, SoftReference<Bitmap>> eldest) {
				if (size() > HARD_CACHE_CAPACITY) {
					// 当map的size大于30时，把最近不常用的key放到mSoftBitmapCache中，从而保证mHardBitmapCache的效率
					mSoftBitmapCache.put(eldest.getKey(), eldest.getValue());
					return true;
				} else
					return false;
			}
		};
		mSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
				HARD_CACHE_CAPACITY / 2);
	}

	public static ImageMemoryCache getInstance() {
		if (instance == null) {
			instance = new ImageMemoryCache();
		}
		return instance;
	}

	public boolean isContains(String id) {
		if (mHardBitmapCache.containsKey(id)) {
			return true;
		}
		return mSoftBitmapCache.containsKey(id);
	}

	/**
	 * 从缓存中获取图片
	 */
	public Bitmap get(String id) {
		// 先从mHardBitmapCache缓存中获取
		synchronized (mHardBitmapCache) {
			final SoftReference<Bitmap> bitmap = mHardBitmapCache.get(id);
			if (bitmap != null) {
                mHardBitmapCache.remove(id);
                if (bitmap.get() != null){
                    if (!bitmap.get().isRecycled()){
                        // 如果找到的话，把元素移到linkedhashmap的最前面，从而保证在LRU算法中是最后被删除
                        mHardBitmapCache.put(id, bitmap);
                        return bitmap.get();
                    }
                }
			}
		}
		// 如果mHardBitmapCache中找不到，到mSoftBitmapCache中找
		SoftReference<Bitmap> bitmapReference = mSoftBitmapCache.get(id);
		if (bitmapReference != null) {
			final Bitmap bitmap = bitmapReference.get();
			if (bitmap != null && !bitmap.isRecycled()) {
				return bitmap;
			} else {
				mSoftBitmapCache.remove(id);
			}
		}
		return null;
	}

	public void put(String id, Bitmap bitmap) {
		LogUtil.v("ImageMemoryCache", "id: " + id);
		synchronized (mHardBitmapCache) {
			mHardBitmapCache.put(id, new SoftReference<Bitmap>(bitmap));
		}
	}

	public void clear() {
		mHardBitmapCache.clear();
		mSoftBitmapCache.clear();
	}

}
