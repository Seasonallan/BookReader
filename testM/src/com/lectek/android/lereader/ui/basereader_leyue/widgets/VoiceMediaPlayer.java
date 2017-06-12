package com.lectek.android.lereader.ui.basereader_leyue.widgets;

import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Handler;
import android.os.Looper;

public class VoiceMediaPlayer extends MediaPlayer implements OnErrorListener,OnCompletionListener,Runnable{
	public static final byte STATE_START = 0;
	public static final byte STATE_STOP = 1;
	public static final byte STATE_ERROR = 2;
	public static final byte STATE_COMPLETION = 3;
	public static final byte STATE_PAUSE = 4;
	private static final int DELAY_MILLIS = 300;
	private Timer mTimer = new Timer();
	private TimerTask mTimerTask;
	private int mStartTime = 0;
	private int mPauseTime = -1;
	private int mSeekToPosition = 0;
	private OnCompletionListener mOnCompletionListener;
	private OnErrorListener mOnErrorListener;
	private boolean isTimerTaskStart = false;
	private long lastSystemTime = 0;
	private Handler mHandler;
	
	public VoiceMediaPlayer(){
		super();
		super.setOnErrorListener(this);
		super.setOnCompletionListener(this);
		mHandler = new Handler(Looper.getMainLooper());
	}
	
	@Override
	public int getCurrentPosition() {
		return mStartTime;
	}
	
	@Override
	public void start() throws IllegalStateException {
		super.start();
		startTimer();
	}

	@Override
	public void stop() throws IllegalStateException {
		super.stop();
		stopTimer();
		onStopPlay(STATE_STOP);
	}

	@Override
	public void setOnErrorListener(OnErrorListener listener) {
		mOnErrorListener = listener;
	}

	@Override
	public void setOnCompletionListener(OnCompletionListener listener) {
		mOnCompletionListener = listener;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		boolean isHandle = false;
		stopTimer();
		onStopPlay(STATE_ERROR);
		if(mOnErrorListener != null){
			isHandle = mOnErrorListener.onError(mp, what, extra);
		}
		return isHandle;
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		stopTimer();
		if(mOnCompletionListener != null){
			mOnCompletionListener.onCompletion(mp);
		}
		onStopPlay(STATE_COMPLETION);
	}
	
	@Override
	public void seekTo(int msec) throws IllegalStateException {
		super.seekTo(msec);
		stopTimer();
		mSeekToPosition = msec;
	}

	@Override
	public void pause() throws IllegalStateException {
		super.pause();
		mPauseTime = mStartTime;
		pauseTimer();
		if(lastSystemTime != 0){
			long timeDifference = System.currentTimeMillis() - lastSystemTime;
			if(timeDifference > DELAY_MILLIS){
				timeDifference = DELAY_MILLIS;
			}
			if(timeDifference < 0){
				timeDifference = 0;
			}
			mPauseTime += timeDifference;
		}
		onStopPlay(STATE_PAUSE);
	}

	private void startTimer(){
		if(mPauseTime >= 0){
			mStartTime = mPauseTime;
		}else{
			mStartTime = mSeekToPosition;
		}
		mPauseTime = -1;
		if(mTimerTask != null){
			mTimerTask.cancel();
		}
		isTimerTaskStart = true;
		lastSystemTime = System.currentTimeMillis();
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				if(isTimerTaskStart && this.equals(mTimerTask)){
					lastSystemTime = 0;
					mStartTime += DELAY_MILLIS;
					mHandler.post(VoiceMediaPlayer.this);
				}
			}
		};
		mTimer.schedule(mTimerTask, DELAY_MILLIS,DELAY_MILLIS);
	}
	
	private void pauseTimer(){
		isTimerTaskStart = false;
		if(mTimerTask != null){
			mTimerTask.cancel();
			mTimerTask = null;
		}
	}
	
	private void stopTimer(){
		pauseTimer();
		mSeekToPosition = 0;
		mPauseTime = -1;
		lastSystemTime = 0;
	}
	
	protected void onStopPlay(int state){
	}

	@Override
	public final void run() {
		if(isTimerTaskStart){
			onProgressChange(this);
		}
	}
	
	protected void onProgressChange(VoiceMediaPlayer mediaPlayer){
		
	}
}
