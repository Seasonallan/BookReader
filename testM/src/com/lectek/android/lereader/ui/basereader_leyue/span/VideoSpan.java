package com.lectek.android.lereader.ui.basereader_leyue.span;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;

import com.lectek.android.LYReader.R;
import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.lib.utils.DimensionsUtil;
import com.lectek.android.lereader.ui.basereader_leyue.widgets.ReaderMediaPlayer;
import com.lectek.android.lereader.ui.basereader_leyue.widgets.ReaderMediaPlayer.Frame;
import com.lectek.lereader.core.text.style.ReplacementSpan;
import com.lectek.lereader.core.text.style.ResourceSpan;

public class VideoSpan extends ReplacementSpan implements ResourceSpan,IMediaSpan{
	private String mSrc;
	private int mWidth;
	private int mHeight;
	private int mBtuWidth;
	private Drawable mPlayDrawable;
	private Drawable mPauseDrawable;
	private Drawable mPrepareingDrawable;
	private TextPaint mTextPaint;
	
	public VideoSpan(String src,int width,int height){
		mSrc = src;
		mBtuWidth = -1;
		mWidth = width;
		mHeight = height;
		ReaderMediaPlayer.getInstance().addPlayerListener(this);
		mTextPaint = new TextPaint();
		mTextPaint.setTextAlign(Align.CENTER);
	}
	
	public void setSrc(String src){
		mSrc = src;
	}
	
	@Override
	public boolean isClickable() {
		return !TextUtils.isEmpty(mSrc);
	}

	@Override
	public void release() {
		mPlayDrawable = null;
		mPauseDrawable = null;
		mPrepareingDrawable = null;
	}
	
	@Override
	public String getVoiceSrc(){
		return mSrc;
	}
	
	@Override
	public void getSize(Paint paint, CharSequence text, int start, int end,
			int maxW, int maxH, Rect container) {
		mTextPaint.setTextSize(paint.getTextSize());
		if(mBtuWidth == -1){
			int unit = (int) paint.measureText("测");
			mBtuWidth = unit * 3;
			if(mWidth <= 0 || mHeight <= 0){
				mWidth = maxW;
				mHeight = (int) (maxW / 1.77f);//默认16:9宽高比现实
			}
			if(mWidth > maxW || mHeight > maxH){
	        	int gapW = mHeight * (mWidth - maxW) / mWidth;
				int gapH = mHeight - maxH;
				if(gapW > gapH){
					mHeight = mHeight * maxW / mWidth;
					mWidth = maxW;
				}else{
					mWidth = mWidth * maxH / mHeight;
					mHeight = maxH;
				}
			}
		}
		container.set(0, 0, mWidth, mHeight);
	}
	
	public void drawContent(Canvas canvas,int left, int top, int right, int bottom){
		int maxW = right - left;
		int maxH = bottom - top;
		canvas.save();
		int strokeWidth = DimensionsUtil.dip2px(1, MyAndroidApplication.getInstance());
		int strokeDW = strokeWidth * 2;
		canvas.clipRect(left - strokeDW, top - strokeDW, right + strokeDW, bottom + strokeDW);
		canvas.drawColor(Color.BLACK);
		mTextPaint.setStrokeWidth(strokeDW);
		mTextPaint.setColor(0xffb3b3b3);
		canvas.drawLine(left, top - strokeWidth, right, top - strokeWidth, mTextPaint);
		canvas.drawLine(right + strokeWidth, top - strokeDW, right + strokeWidth, bottom + strokeDW, mTextPaint);
		canvas.drawLine(left, bottom + strokeWidth, right, bottom + strokeWidth, mTextPaint);
		canvas.drawLine(left - strokeWidth, top - strokeDW, left - strokeWidth, bottom + strokeDW, mTextPaint);
		canvas.restore();
		Frame lastFrame = ReaderMediaPlayer.getInstance().getLastFrame(mSrc);
		if(lastFrame != null){
			int w = lastFrame.getWidth();
	        int h = lastFrame.getHeight();
			int transT = 0;
	        int transL = 0;
	        int transR = 0;
	        int transB = 0;
			int gapW = h * (w - maxW) / w;
			int gapH = h - maxH;
			if(gapW > gapH){
				h = h * maxW / w;
				w = maxW;
			}else{
				w = w * maxH / h;
				h = maxH;
			}
			transL = (maxW - w) / 2 + left;
	        transR = transL + w;
			transT = (maxH - h) / 2 + top;
            transB = transT + h;
			lastFrame.getDrawable().setBounds(transL, transT, transR, transB);
			lastFrame.getDrawable().draw(canvas);
		}
		if(maxW > maxH){
			if(maxH > mBtuWidth){
				left += (maxW - mBtuWidth) / 2;
				right -= (maxW - mBtuWidth) / 2;
				top += (maxH - mBtuWidth) / 2;
				bottom -= (maxH - mBtuWidth) / 2;
			}else{
				left += (mBtuWidth - maxW) / 2;
				right -= (mBtuWidth - maxW) / 2;
			}
		}else{
			if(maxW > mBtuWidth){
				left += (maxW - mBtuWidth) / 2;
				right -= (maxW - mBtuWidth) / 2;
				top += (maxH - mBtuWidth) / 2;
				bottom -= (maxH - mBtuWidth) / 2;
			}else{
				top += (mBtuWidth - maxH) / 2;
				bottom -= (mBtuWidth - maxH) / 2;
			}
		}
		int state = ReaderMediaPlayer.getInstance().getState(mSrc);
		if(state == ReaderMediaPlayer.STATE_START){
			if(mPlayDrawable == null){
				mPlayDrawable = BaseApplication.getInstance().getResources().getDrawable(R.drawable.ic_reader_voice_play);
			}
			mPlayDrawable.setBounds(left, top, right, bottom);
			mPlayDrawable.draw(canvas);
		}else{
			if(state != ReaderMediaPlayer.STATE_PREPAREING){
				if(mPauseDrawable == null){
					mPauseDrawable = BaseApplication.getInstance().getResources().getDrawable(R.drawable.ic_reader_voice_pause);
				}
				mPauseDrawable.setBounds(left, top, right, bottom);
				mPauseDrawable.draw(canvas);
			}else{
//				if(mPrepareingDrawable == null){
//					mPrepareingDrawable = MyAndroidApplication.getInstance().getResources().getDrawable(R.drawable.ic_reader_voice_prepareing);
//				}
//				mPrepareingDrawable.setBounds(left, top, right, bottom);
//				mPrepareingDrawable.draw(canvas);
				mTextPaint.setColor(Color.WHITE);
				canvas.drawText("loading...", left + (right - left) / 2,top + (bottom - top) / 2, mTextPaint);
			}
		}
	}
	
	@Override
	public void draw(Canvas canvas, CharSequence text, int start, int end,
			int left, int top, int right, int bottom, int maxW,int maxH,Paint paint) {
		drawContent(canvas, left, top, right, bottom);
	}
	
	@Override
	public boolean isPlay(){
		return ReaderMediaPlayer.getInstance().getState(mSrc) == ReaderMediaPlayer.STATE_START;
	}
	
	@Override
	public boolean contains(long position){
		return true;
	}
	
	@Override
	public void onPlayStateChange(int playState, String voiceSrc) {
		
	}

	@Override
	public void onProgressChange(long currentPosition, long maxPosition, String voiceSrc) {
		
	}

	@Override
	public long getStartPosition() {
		return ReaderMediaPlayer.getInstance().getLastPlayPosition(mSrc);
	}

	@Override
	public long computePositionByLocal(int x, int y) {
		return getStartPosition();
	}

	@Override
	public void checkContentRect(RectF rect) {
	}
}