package com.lectek.android.lereader.ui.basereader_leyue.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.text.TextPaint;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.lib.utils.DimensionsUtil;
import com.lectek.android.lereader.ui.basereader_leyue.Book;
import com.lectek.android.lereader.ui.basereader_leyue.widgets.ReaderClockDrawable;

public abstract class BaseReadView extends AbsReadView implements IReaderView{
	protected IReadCallback mReadCallback;
	protected Book mBook;
	
	private int mLoadingPointSize;
	private long mLastDrawWaitTime;
	private Drawable mInitBGDrawable;
    private Drawable mBookMarkTip;
	
	public BaseReadView(Context context,Book book,IReadCallback readCallback) {
		super(context);
		mBook = book;
		mReadCallback = readCallback;
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

    @Override
    public void onDestroy() {
        mInitBGDrawable = null;
        mBookMarkTip = null;
    }

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}
	
	@Override
	public void invalidateDrawable(Drawable drawable) {
		super.invalidateDrawable(drawable);
	}
	
	@Override
	protected void onLoadStyleSetting(boolean isReLayout) {

	}

    /**
     * 获取某个页面是否被标记为书签
     */
    protected boolean isPageMarked(int chapterIndex, int pageIndex){
        return false;
    }

    @Override
    protected void drawBookMarkTip(Canvas canvas, int chapterIndex, int pageIndex){
        if(isPageMarked(chapterIndex, pageIndex)){
            if(mBookMarkTip == null){
                mBookMarkTip = getResources().getDrawable(R.drawable.icon_shuqian_chang);
                int bookMarkW = mBookMarkTip.getIntrinsicWidth();
                int bookMarkH = mBookMarkTip.getIntrinsicHeight();
                Rect bounds = new Rect(getWidth() - PADDING_RIGHT - PADDING_LEFT - bookMarkW, 0, getWidth() - PADDING_RIGHT, bookMarkH);
                mBookMarkTip.setBounds(bounds);
            }
            mBookMarkTip.draw(canvas);
        }
    }

	/**
	 * 绘制等待画面
	 * @param canvas
	 */
	@Override
	protected void drawWaitPage(Canvas canvas,boolean isFirstDraw){
		if(isFirstDraw){
			drawBackground(canvas);
			mTempTextPaint.setTextSize((float) (mReadSetting.getFontSize()));
			mTempTextPaint.setColor(mReadSetting.getThemeTextColor());
		}else{
			if(mInitBGDrawable == null){
				Options opts = new Options();
				opts.inPreferredConfig = Config.RGB_565;
				mInitBGDrawable = new BitmapDrawable(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.book_second_cover, opts));
			}
			mInitBGDrawable.setBounds(getLeft(), getTop(), getRight(), getBottom());
			mInitBGDrawable.draw(canvas);
			mTempTextPaint.setTextSize((float) (mReadSetting.getFontSize()));
			mTempTextPaint.setColor(Color.BLACK);
		}
		long timeDifference = System.currentTimeMillis() - mLastDrawWaitTime;
		if(timeDifference  > 200 || timeDifference < 0){
			mLastDrawWaitTime = System.currentTimeMillis();
			mLoadingPointSize++;
			if(mLoadingPointSize > 3){
				mLoadingPointSize = 1;
			}
		}
		mTempTextPaint.setTextAlign(Align.CENTER);
		String str = getResources().getString(R.string.reader_transition_tip);
		for (int i = 0; i < mLoadingPointSize; i++) {
			str = " " + str + ".";
		}
		canvas.drawText(str, getWidth()/2, getHeight()/2, mTempTextPaint);
		postInvalidateDelayed(500);
	
	}

    protected void drawReadProgress(Canvas canvas, float progress, float max){
        int height = 8;
        int y = getHeight() - height;
        mTempTextPaint.setColor(0xffa5a5a5);
        canvas.drawRect(0 , y, getWidth(), getHeight(), mTempTextPaint);
        mTempTextPaint.setColor(0xff40ac7a);
        canvas.drawRect(0 , y, getWidth() * progress/ max, getHeight(), mTempTextPaint);
    }

	protected void drawReadPercent(Canvas canvas, String pageSizeStr){
		mTempTextPaint.setTextSize((float) (mReadSetting.getMinFontSize()));
		mTempTextPaint.setTextAlign(Align.RIGHT);
		mTempTextPaint.setColor(mReadSetting.getThemeDecorateTextColor());

        if(mBookMarkTip == null){
            mBookMarkTip = getResources().getDrawable(R.drawable.icon_shuqian_chang);
            int bookMarkW = mBookMarkTip.getIntrinsicWidth();
            int bookMarkH = mBookMarkTip.getIntrinsicHeight();
            Rect bounds = new Rect(getWidth() - PADDING_CONTENT_BOTTOM - bookMarkW, 0, getWidth() - PADDING_CONTENT_BOTTOM, bookMarkH);
            mBookMarkTip.setBounds(bounds);
        }
        int bookMarkW = mBookMarkTip.getIntrinsicWidth();

		FontMetricsInt fm = mTempTextPaint.getFontMetricsInt();
		int x = getWidth() - PADDING_LEFT - bookMarkW;
		int y = PADDING_TOP - fm.top;
		canvas.drawText(pageSizeStr, x, y, mTempTextPaint);
	}
	
	protected void drawChapterName(Canvas canvas,String title){
		if(title == null){
			title = "";
		}
		mTempTextPaint.setTextSize((float) (mReadSetting.getMinFontSize()));
		mTempTextPaint.setTextAlign(Align.LEFT);
		mTempTextPaint.setColor(mReadSetting.getThemeDecorateTextColor());
		FontMetricsInt fm = mTempTextPaint.getFontMetricsInt();
		int x = PADDING_LEFT;
		int y = PADDING_TOP - fm.top;
        int w = (int) mTempTextPaint.measureText(title);
        int titleMaxWidth = (getWidth() - (PADDING_LEFT + PADDING_RIGHT)) * 8 / 10;//标题宽度最宽不能超过去掉padding后屏幕宽度的80%
        if(w > titleMaxWidth){
            title = validateTextWidth(mTempTextPaint, title, titleMaxWidth) + " ";
            w = (int) mTempTextPaint.measureText(title);
        }
		canvas.drawText(title, x, y, mTempTextPaint);
	}
	
	protected void drawBookName(Canvas canvas,String title){
		if(title == null){
			title = "";
		}
		mTempTextPaint.setTextSize((float) (mReadSetting.getMinFontSize()));
		mTempTextPaint.setTextAlign(Align.LEFT);
		mTempTextPaint.setColor(mReadSetting.getThemeDecorateTextColor());
		FontMetricsInt fm = mTempTextPaint.getFontMetricsInt();
		int x = PADDING_LEFT;
		int y = PADDING_TOP - fm.top;
		int w = (int) mTempTextPaint.measureText(title);
		int titleMaxWidth = (getWidth() - (PADDING_LEFT + PADDING_RIGHT)) * 8 / 10;//标题宽度最宽不能超过去掉padding后屏幕宽度的80%
		if(w > titleMaxWidth){
			title = validateTextWidth(mTempTextPaint, title, titleMaxWidth) + " ";
			w = (int) mTempTextPaint.measureText(title);
		}
		canvas.drawText(title, x, y, mTempTextPaint);
	}
	
	/**
	 * 获取文本根据指定宽省略后文本(带省略号)
	 * @param textPaint
	 * @param text
	 * @param totalWidth 指定最大宽度
	 * @return
	 */
	private String validateTextWidth(TextPaint textPaint, String text, int totalWidth){
		text = text.substring(0, text.length()-2);
		int textWidth = (int)textPaint.measureText(text);
		if(textWidth > totalWidth){
			return validateTextWidth(textPaint, text, totalWidth);
		}
		return text+"...";
	}
	
	@Override
	protected void drawBackground(Canvas canvas){
		super.drawBackground(canvas);
		mInitBGDrawable = null;
	}
}
