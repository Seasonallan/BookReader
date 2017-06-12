package com.lectek.android.lereader.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Scroller;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.data.BookShelfItem;
import com.lectek.android.lereader.lib.image.BaseImageLoader;
import com.lectek.android.lereader.lib.image.ImageLoader;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.utils.Constants;
import com.lectek.android.widget.AsyncImageView;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShelfFileView extends AsyncImageView {
    private final int MAX = 4;
    private final int MARGIN = 6;
    private Bitmap mDefaultBitmap;
    private ImageLoader mImageLoader;
    private HashMap<String, Bitmap> mHardBitmapCache;

	public ShelfFileView(Context context) {
		super(context);
		initState();
	}

	public ShelfFileView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initState();
	}

	private void initState() {
        mImageLoader = new ImageLoader(getContext());
        mDefaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.book_default);
        mHardBitmapCache = new HashMap<String, Bitmap>();
        setDefaultImgRes(R.drawable.book_default);
	}

    public BookShelfItem bookShelfItem;

    public void setShelfItem(BookShelfItem item){
        bookShelfItem = item;
        if (bookShelfItem != null && !bookShelfItem.isFile && bookShelfItem.mDownLoadInfo != null){
            setImageUrl(bookShelfItem.mDownLoadInfo.logoUrl);
        }else{
            invalidate();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LogUtil.i("OCD>> onAttachedToWindow");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtil.i("OCD>> onDetachedFromWindow");
    }

    public Bitmap getBitmap(){
        if (bookShelfItem != null){
            String url = bookShelfItem.mDownLoadInfo.logoUrl;
            if (!TextUtils.isEmpty(url)){
                if (mHardBitmapCache.containsKey(url)){
                    Bitmap bitmap = mHardBitmapCache.get(url);
                    if (bitmap != null && bitmap != null){
                        return bitmap;
                    }
                }
                return mImageLoader.loadImage(Constants.BOOKS_TEMP, Constants.BOOKS_TEMP_IMAGE,
                        url, String.valueOf(url.hashCode()), new BaseImageLoader.ImageCallback() {
                    @Override
                    public void imageLoaded(Bitmap bitmap, String url, String s2) {
                        if (bitmap != null){
                            mHardBitmapCache.put(url, bitmap);
                        }
                    }
                });
            }
        }
        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect rect = new Rect(MARGIN/4,MARGIN/4, getWidth() - MARGIN/4, getHeight() - MARGIN/4);
        if (bookShelfItem == null){
           // canvas.drawBitmap(mDefaultBitmap, null, rect, new Paint());
        }else{
            List<BookShelfItem> items = bookShelfItem.mItems;
            if (bookShelfItem.isFile){
                if (items != null){
                    for (int i = 0;i < Math.min(MAX ,items.size()); i++){
                        if (i == 0){
                            rect = new Rect(MARGIN, MARGIN ,getWidth()/2 - MARGIN/2, getHeight()/2 - MARGIN/2);
                        }else if (i == 1){
                            rect = new Rect(getWidth()/2 + MARGIN/2, MARGIN ,getWidth()-MARGIN, getHeight()/2 - MARGIN/2);
                        }else if (i == 2){
                            rect = new Rect(MARGIN, getHeight()/2 + MARGIN/2 ,getWidth()/2 - MARGIN/2, getHeight()-MARGIN);
                        }else if (i == 3){
                            rect = new Rect(getWidth()/2 + MARGIN/2, getHeight()/2 + MARGIN/2 ,getWidth()-MARGIN, getHeight()-MARGIN);
                        }
                        String url = items.get(i).mDownLoadInfo.logoUrl;
                        boolean res = draw(canvas, url, rect);
                    }
                }
            }else{
                super.onDraw(canvas);
                //String url = bookShelfItem.mDownLoadInfo.logoUrl;
                //draw(canvas, url, rect);
            }
        }
    }

    private boolean draw(Canvas canvas, String url, Rect rect){
        if (url != null){
            if (mHardBitmapCache.containsKey(url)){
                Bitmap bitmap = mHardBitmapCache.get(url);
                if (bitmap != null && bitmap != null){
                    canvas.drawBitmap(bitmap, null, rect, new Paint());
                    return true;
                }
            }
            Bitmap bitmap = mImageLoader.loadImage(Constants.BOOKS_TEMP, Constants.BOOKS_TEMP_IMAGE,
                    url, String.valueOf(url.hashCode()), new BaseImageLoader.ImageCallback() {
                @Override
                public void imageLoaded(Bitmap bitmap, String url, String s2) {
                    if (bitmap != null){
                        mHardBitmapCache.put(url, bitmap);
                        invalidate();
                    }
                }
            });
            if (bitmap != null){
                canvas.drawBitmap(bitmap, null, rect, new Paint());
                return true;
            }
        }
        canvas.drawBitmap(mDefaultBitmap, null, rect, new Paint());
        return false;
    }


}




