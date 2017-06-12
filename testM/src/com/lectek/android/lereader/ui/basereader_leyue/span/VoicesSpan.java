package com.lectek.android.lereader.ui.basereader_leyue.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.lectek.android.LYReader.R;
import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.ui.basereader_leyue.widgets.ReaderMediaPlayer;
import com.lectek.lereader.core.text.style.AsyncDrawableSpan;
import com.lectek.lereader.core.text.style.ReplacementSpan;
import com.lectek.lereader.core.text.style.ResourceSpan;

public class VoicesSpan extends ReplacementSpan implements ResourceSpan,IMediaSpan{
	private String mSrc;
	private boolean isPlay;
	private float mSize;
	private int mWidth;
	private Drawable mPlayDrawable;
	private Drawable mPauseDrawable;
	private AsyncDrawableSpan mAsyncDrawableSpan;
	
	public VoicesSpan(String src){
		this(src, null);
	}
	
	public VoicesSpan(String src,AsyncDrawableSpan asyncDrawableSpan){
		mSrc = src;
		mSize = -1;
		ReaderMediaPlayer.getInstance().addPlayerListener(this);
		isPlay = ReaderMediaPlayer.getInstance().getPlayState(src);
		mAsyncDrawableSpan = asyncDrawableSpan;
	}
	
	@Override
	public boolean isClickable() {
		return true;
	}

	@Override
	public void release() {
		mPlayDrawable = null;
		mPauseDrawable = null;
	}
	
	@Override
	public String getVoiceSrc(){
		return mSrc;
	}

	@Override
	public void getSize(Paint paint, CharSequence text, int start, int end,
			int maxW, int maxH, Rect container) {
		if(mAsyncDrawableSpan != null){
			mAsyncDrawableSpan.getSize(paint, text, start, end, maxW, maxH, container);
		}else{
			if(mSize != paint.getTextSize()){
				mWidth = (int) paint.measureText("测") * 3;
				mSize = paint.getTextSize();
			}
			container.set(0, 0, mWidth, mWidth);
		}
	}
	
	@Override
	public void draw(Canvas canvas, CharSequence text, int start, int end,
			int left, int top, int right, int bottom, int maxW, int maxH,
			Paint paint) {
		if(mAsyncDrawableSpan != null){
			mAsyncDrawableSpan.draw(canvas, text, start, end, left, top, right, bottom, maxW, maxH, paint);
		}else{
			if(isPlay){
				if(mPlayDrawable == null){
					mPlayDrawable = BaseApplication.getInstance().getResources().getDrawable(R.drawable.ic_reader_voice_play);
				}
				mPlayDrawable.setBounds(left, top, right, bottom);
				mPlayDrawable.draw(canvas);
			}else{
				if(mPauseDrawable == null){
					mPauseDrawable = BaseApplication.getInstance().getResources().getDrawable(R.drawable.ic_reader_voice_pause);
				}
				mPauseDrawable.setBounds(left, top, right, bottom);
				mPauseDrawable.draw(canvas);
			}
		}
	}
	
	@Override
	public boolean isPlay(){
		return isPlay;
	}
	
	@Override
	public boolean contains(long position){
		return true;
	}
	
	@Override
	public void onPlayStateChange(int playState, String voiceSrc) {
		if(mSrc.equals(voiceSrc)){
			isPlay = playState == ReaderMediaPlayer.STATE_START;
		}else {
			isPlay = false;
		}
	}

	@Override
	public void onProgressChange(long currentPosition, long maxPosition, String voiceSrc) {
		
	}

	@Override
	public long getStartPosition() {
		return 0;
	}

	@Override
	public long computePositionByLocal(int x, int y) {
		return 0;
	}

	@Override
	public void checkContentRect(RectF rect) {
	}
}