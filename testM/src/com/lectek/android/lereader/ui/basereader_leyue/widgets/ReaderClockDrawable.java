package com.lectek.android.lereader.ui.basereader_leyue.widgets;

import java.util.Calendar;

import com.lectek.android.lereader.lib.utils.DimensionsUtil;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextPaint;
import android.text.format.DateFormat;


public class ReaderClockDrawable extends Drawable{
	private Handler mHandler;
	private Calendar mCalendar;
	private final static String m12 = "h:mm aa";
	private final static String m24 = "k:mm";
	private FormatChangeObserver mFormatChangeObserver;
	private Runnable mTicker;
	private boolean mTickerStopped = false;
	private String mFormat;
	private Context mContext;
	private String mText;
	private TextPaint mTextPaint;
	
	public ReaderClockDrawable(Context context){
		mHandler = new Handler(Looper.getMainLooper());
		mContext = context;
		if (mCalendar == null) {
			mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(System.currentTimeMillis());
		}
		setFormat();
		mTextPaint = new TextPaint();
		mTextPaint.setTextSize(DimensionsUtil.dip2px(12, mContext));
		mTextPaint.setColor(Color.BLACK);
		mTextPaint.setAntiAlias(true);
	}

	public void start() {
		mTickerStopped = false;
		if(mFormatChangeObserver == null){
			 mFormatChangeObserver = new FormatChangeObserver();
			 mContext.getContentResolver().registerContentObserver(
			 Settings.System.CONTENT_URI, true, mFormatChangeObserver);
		}
		mHandler = new Handler();
		/**
		 * requests a tick on the next hard-second boundary
		 */
		mTicker = new Runnable() {
			@Override
			public void run() {
				if (mTickerStopped)
					return;
				mCalendar.setTimeInMillis(System.currentTimeMillis());
				if(mCalendar.getTime().getSeconds() < 2 || mText == null ||  mText.length() == 0){
					invalidateTime();
				}
				long now = SystemClock.uptimeMillis();
				long next = now + (1000 - now % 1000);
				mHandler.postAtTime(mTicker, next);
			}
		};
		mTicker.run();
	}

	public void release(){
		if(mFormatChangeObserver != null){
			mContext.getContentResolver().unregisterContentObserver(mFormatChangeObserver);
			mFormatChangeObserver = null;
		}
		mTickerStopped = true;
	}
	
	public void setTextSize(int textSize){
		mTextPaint.setTextSize(textSize);
	}
	
	public void setTextColor(int color){
		mTextPaint.setColor(color);
	}
	
	private void invalidateTime(){
		setText(DateFormat.format(mFormat, mCalendar).toString()
				.toUpperCase());
		invalidateSelf();
	}
	
	private void setText(String str){
		mText = str;
	}
	
	/**
	 * Pulls 12/24 mode from system settings
	 */
	private boolean get24HourMode() {
		return android.text.format.DateFormat.is24HourFormat(mContext);
	}

	private void setFormat() {
		if (get24HourMode()) {
			mFormat = m24;
		} else {
			mFormat = m12;
		}
	}

	private class FormatChangeObserver extends ContentObserver {
		public FormatChangeObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			setFormat();
			invalidateTime();
		}
	}
	
	@Override
	public void draw(Canvas canvas) {
		Rect rect = getBounds();
		if(rect == null){
			rect = new Rect();
		}
		FontMetricsInt fm = mTextPaint.getFontMetricsInt();
		rect.top = (int) (rect.top - fm.bottom + mTextPaint.getFontSpacing());
		if(mText != null){
			canvas.drawText(mText, rect.left, rect.top, mTextPaint);
		}
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
	}

	@Override
	public int getOpacity() {
		return 0;
	}
}