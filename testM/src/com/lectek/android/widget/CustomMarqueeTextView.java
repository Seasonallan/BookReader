package com.lectek.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 自定义TEXTVIEW类
 * 用于监听Marquee滚动完成事件
 * @author Administrator
 *
 */
public class CustomMarqueeTextView extends TextView {

	private TimeCountRunnable mTimeCount;
	private OnMarqueeFinishedListener mOnMarqueeFinishedListener;
	private long elapseTime;

	public CustomMarqueeTextView(Context context, AttributeSet attrs,
                                 int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomMarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomMarqueeTextView(Context context) {
		super(context);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		elapseTime = 0;
	}
	
	public void startRun(){
		System.out.println("startRun");
		mTimeCount = new TimeCountRunnable();
		mTimeCount.isRunning = true;
		elapseTime = 0;
		mTimeCount.start();
	}
	
	private class TimeCountRunnable extends Thread {
		
		public boolean isRunning;
		
		@Override
		public void run() {
			while(isRunning){
				if(elapseTime == 20){
					isRunning = false;
					if(mOnMarqueeFinishedListener != null){
						mOnMarqueeFinishedListener.marqueeFinished();
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				elapseTime++;
			}
			
		}
	}
	
	public void setOnMarqueeFinishedListener(OnMarqueeFinishedListener listener){
		mOnMarqueeFinishedListener = listener;
	}
	
	public static interface OnMarqueeFinishedListener{
		public void marqueeFinished();
	}

}
