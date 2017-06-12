package com.lectek.lereader.core.text.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.lectek.lereader.core.text.html.DataProvider;
import com.lectek.lereader.core.util.FileUtil;

public class ReaderMediaPlayer extends BaseMediaPlayer{
	private static HashMap<String, Integer> mLastPlayPositionMap = new HashMap<String, Integer>();
	private static final long PLAYSIZE = 1024 * 1024 * 50;
	private static final int BUFFER_SIZE = 1024 * 50;
	private static ReaderMediaPlayer mInstance;
	private ArrayList<WeakReference<PlayerListener>> mPlayerListeners;
	private String mCurrentPlaySrc;
	private boolean isUserPlay;
	private boolean isPrepare;
	private Thread mLoadDataTask;
	private Thread mOldLoadDataTask;
	private long mUserSeekPosition;
	private int mState;
	private PlayerSetDataListener mPlayerSetDataListener;
	private Frame mLastFrame;
	private String mLastFrameSrc;
	private DataProvider mDataProvider;
	private String mCurrentFilePath;
	
	public static synchronized void init(DataProvider dataProvider){
		if(mInstance == null){
			mInstance = new ReaderMediaPlayer(dataProvider);
		}
	} 
	
	public static synchronized ReaderMediaPlayer getInstance(){
		return mInstance;
	}
	
	private ReaderMediaPlayer(DataProvider dataProvider){
		mDataProvider = dataProvider;
		mPlayerListeners = new ArrayList<WeakReference<PlayerListener>>();
		isUserPlay = true;
		mUserSeekPosition = 0;
		mState = STATE_STOP;
	}
	
	public int getLastPlayPosition(String voiceSrc){
		Integer position = mLastPlayPositionMap.get(voiceSrc);
		return position == null ? 0 : position;
	}
	
	public Frame getLastFrame(String voiceSrc){
		if(!voiceSrc.equals(mLastFrameSrc)){
			return null;
		}
		return mLastFrame;
	}
	
	public void createLastFrame(String voiceSrc,int w,int h){
		if(!voiceSrc.equals(mCurrentPlaySrc) || mCurrentFilePath == null){
			return;
		}
		mLastFrameSrc = mCurrentPlaySrc;
		MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
		mediaMetadataRetriever.setDataSource(mCurrentFilePath);
		Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(getCurrentPosition() * 1000);
		if(bitmap != null){
			mLastFrame = new Frame(w,h,new BitmapDrawable(bitmap));
		}
		mediaMetadataRetriever.release();
	}
	
	@Override
	public void release() {
		check();
		stop();
		super.release();
		mInstance = null;
	}
	/**
	 * 查询指定音频的播放状态
	 * @param voiceSpan
	 * @return 是否正在播放
	 */
	public boolean getPlayState(String voiceSrc){
		if(mCurrentPlaySrc != null && mCurrentPlaySrc.equals(voiceSrc) && isPlaying()){
			return true;
		}
		return false;
	}
	/**
	 * 是否需要调节音量键
	 * @return
	 */
	public boolean isNeedControlVolume(){
		return isPlaying();
	}
	/**
	 * 播放控制界面是否需要显示
	 * @return
	 */
	public boolean isPlayerStop(){
		if(mCurrentPlaySrc != null && 
				(mState == STATE_START || mState == STATE_PAUSE)){
			return true;
		}
		return false;
	}
	/**
	 * 播放器是否就绪
	 * @return
	 */
	public boolean isPrepare(){
		return isPrepare;
	}
	
	public void startVioce(IMedia voiceSpan){
		startVioce(voiceSpan,voiceSpan.getStartPosition());
	}
	
	public void startVioce(IMedia voiceSpan,long startPosition){
		if(voiceSpan.getVoiceSrc().equals(mCurrentPlaySrc) && isPrepare){
			if(!voiceSpan.contains(getCurrentPosition())){
				if(startPosition == 0){
					startPosition = voiceSpan.getStartPosition();
				}
			}
		}else{
			if(startPosition == 0){
				startPosition = voiceSpan.getStartPosition();
			}
		}
		startVioce(voiceSpan.getVoiceSrc(),startPosition);
	}
	
	public void startVioce(String voiceSrc){
		startVioce(voiceSrc, 0);
	}
	
	public void startVioce(String voiceSrc,long startPosition){
		check();
		if(TextUtils.isEmpty(voiceSrc)){
			return;
		}
		if(!voiceSrc.equals(mCurrentPlaySrc)){
			startLoadVioce(voiceSrc,startPosition);
		}else{
			if(!isPrepare){
				startLoadVioce(voiceSrc,startPosition);
			}else{
				isUserPlay = true;
				if(startPosition != 0){
					seekTo((int) startPosition);
				}
				start();
			}
		}
	}
	
	private void startLoadVioce(String voiceSrc,final long startPosition){
		check();
		if(voiceSrc.equals(mCurrentPlaySrc)){
			if(mLoadDataTask != null){
				return;
			}else if(mCurrentFilePath != null){
				startPlay(mCurrentFilePath, startPosition);
				return;
			}
		}
		final String filePath = getVoiceFielPath();
		pause();
		mCurrentFilePath = null;
		isUserPlay = true;
		isPrepare = false;
		mCurrentPlaySrc = voiceSrc;
		final String finalVoiceSrc = voiceSrc;
		if(mLoadDataTask != null){
			mOldLoadDataTask = mLoadDataTask;
		}
		performOnPlayStateChange(STATE_PREPAREING, mCurrentPlaySrc);
		mLoadDataTask = new Thread(){
			Thread this_ = this;
			boolean isSucceed = false;
			@Override
			public void run() {
				while (mOldLoadDataTask != null) {
					try {sleep(100);} catch (InterruptedException e) {}
				}
				if(!isStop()){
					try {
						InputStream inputStream = mDataProvider.getDataStream(finalVoiceSrc);
						if(inputStream != null){
							if(filePath != null){
								int numread = 0;
								FileOutputStream outputStream = new FileOutputStream(filePath,false);
								byte buf[] = new byte[BUFFER_SIZE];
								while (-1 != (numread = inputStream.read(buf)) && !isStop()) {
									if (numread > 0) {
										outputStream.write(buf, 0, numread);
									}
								}
								outputStream.close();
								if(!isStop()){
									isSucceed = true;
								}
							}
							inputStream.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				getHandler().post(new Runnable() {
					@Override
					public void run() {
						if(!isStop()){
							if(isSucceed && mCurrentPlaySrc != null && mCurrentPlaySrc.equals(finalVoiceSrc)){
								mCurrentFilePath = filePath;
								startPlay(filePath, startPosition);
							}else{
								performOnPlayStateChange(STATE_STOP, mCurrentPlaySrc);
							}
						}
						mOldLoadDataTask = null;
						if(this_.equals(mLoadDataTask)){
							mLoadDataTask = null;
						}
					}
				});
			}
			
			private boolean isStop(){
				return !this.equals(mLoadDataTask);
			}
		};
		mLoadDataTask.start();
	}
	
	private void startPlay(String filePath,long startPosition){
		try {
			reset();
			File file = new File(filePath);
			if(mPlayerSetDataListener != null){
				if(mPlayerSetDataListener.onSetData(ReaderMediaPlayer.this, file)){
					setAudioStreamType(AudioManager.STREAM_MUSIC);
					ReaderMediaPlayer.this.prepare();
					isPrepare = true;
				}
			}else{
				FileInputStream fis = new FileInputStream(file);
				setDataSource(fis.getFD());
				setAudioStreamType(AudioManager.STREAM_MUSIC);
				ReaderMediaPlayer.this.prepare();
				isPrepare = true;
			}
			if(mUserSeekPosition > getDuration()){
				mUserSeekPosition = getDuration() - 1000;
			}
			if(mUserSeekPosition <= 0){
				mUserSeekPosition = startPosition;
			}
			if(isPrepare){
				ReaderMediaPlayer.super.seekTo((int)mUserSeekPosition);
				ReaderMediaPlayer.this.start();
			}else{
				performOnPlayStateChange(STATE_STOP, mCurrentPlaySrc);
			}
		} catch (Exception e) {
			performOnPlayStateChange(STATE_STOP, mCurrentPlaySrc);
		}
	}
	
	public void addPlayerListener(PlayerListener l){
		synchronized (mPlayerListeners) {
			if(!containsPlayerListeners(l)){
				mPlayerListeners.add(new WeakReference<PlayerListener>(l));
			}
		}
	}
	
	private boolean containsPlayerListeners(PlayerListener l){
		for (WeakReference<PlayerListener> playerListener : mPlayerListeners) {
			if(playerListener.get() != null && playerListener.get().equals(l)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void seekTo(int msec) throws IllegalStateException {
		check();
		isUserPlay = true;
		if(isPrepare){
			if(isPlaying()){
				pause();
			}
			super.seekTo(msec);
			start();
		}else{
			mUserSeekPosition = msec;
		}
	}

	@Override
	public void start() throws IllegalStateException {
		check();
		if(isUserPlay && isPrepare && !isPlaying()){
			mUserSeekPosition = 0;
			super.start();
			performOnPlayStateChange(STATE_START , mCurrentPlaySrc);
		}
	}

	@Override
	public void stop() throws IllegalStateException {
		check();
		super.stop();
		isPrepare = false;
		mLoadDataTask = null;
		mOldLoadDataTask = null;
	}

	@Override
	public void pause() throws IllegalStateException {
		check();
		if(isPlaying()){
			super.pause();
		}
	}

	@Override
	protected void onStopPlay(int state) {
		super.onStopPlay(state);
		performOnPlayStateChange(state,mCurrentPlaySrc);
	}

	public void setPlayerSetDataListener(PlayerSetDataListener listener){
		mPlayerSetDataListener = listener;
	}
	
	public void setPlayState(boolean isPlay){
		check();
		if(isUserPlay != isPlay){
			isUserPlay = isPlay;
			if(isPlaying() && !isPlay){
				pause();
			}else if(!isPlaying() && isPlay){
				if(isPrepare){
					start();
				}else if(mCurrentPlaySrc != null){
					startVioce(mCurrentPlaySrc);
				}
			}
		}
	}
	
	@Override
	protected final void onProgressChange(BaseMediaPlayer mediaPlayer) {
		super.onProgressChange(mediaPlayer);
		onProgressChange(getCurrentPosition(), getDuration(),mCurrentPlaySrc);
	}
	
	public int getState(String voiceSrc){
		if(mCurrentPlaySrc != null && mCurrentPlaySrc.equals(voiceSrc)){
			return mState;
		}
		return STATE_STOP;
	}
	
	private void performOnPlayStateChange(int state, String voiceSrc){
		if(mState == state){
			return;
		}
		int oldState = mState;
		if(state == STATE_STOP
				|| state == STATE_COMPLETION
				|| state == STATE_ERROR){
			if(oldState != STATE_START && oldState != STATE_PAUSE && oldState != STATE_PREPAREING){
				return;
			}
		}else if(state == STATE_PAUSE){
			if(oldState != STATE_START && oldState != STATE_PREPAREING){
				return;
			}
		}
		mState = state;
		if(state == STATE_COMPLETION){
			mLastPlayPositionMap.put(mCurrentPlaySrc,null);
		}else if(state != STATE_START){
			mLastPlayPositionMap.put(mCurrentPlaySrc, getCurrentPosition());
		}
		for (WeakReference<PlayerListener> l : mPlayerListeners) {
			if(l.get() != null){
				l.get().onPlayStateChange(state, voiceSrc);
			}
		}
	}
	
	private void onProgressChange(long currentPosition,long maxPosition, String voiceSrc){
		for (WeakReference<PlayerListener> l : mPlayerListeners) {
			if(l.get() != null){
				l.get().onProgressChange(currentPosition, maxPosition, voiceSrc);
			}
		}
	}
	
	private String getVoiceFielPath(){
		String path = null;
		String pathDir = null;
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)
			    && FileUtil.getStorageSize() > PLAYSIZE){
			// 有SD卡
			pathDir = Constants.BOOKS_TEMP;
			path = pathDir + "downloadingMedia.dat";
		} else if (getAvailMemory() > PLAYSIZE) {
			// 没有SD卡
			pathDir = getContext().getCacheDir() + File.separator;
			path = pathDir + "downloadingMedia.dat";
		}
		File fileDir = new File(pathDir);
		if(!fileDir.exists()){
			fileDir.mkdirs();
		}
		return path;
	}
	
	private long getAvailMemory() {// 获取android当前可用内存大小
		ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);// mi.availMem; 当前系统的可用内存
		return mi.availMem;// 将获取的内存大小规格化
	}
	
	private Context getContext(){
		return MyAndroidApplication.getInstance();
	}
	
	private Handler getHandler(){
		return MyAndroidApplication.getHandler();
	}
	
	private static void check(){
		if(Thread.currentThread() != Looper.getMainLooper().getThread()){
			new RuntimeException("Must be running on the UI thread");
		}
	}
	
	public static String getTimeStr(int seconds) {
		int min = seconds % 3600 / 60;
		int sec = seconds % 60;
		int hour = seconds / 3600;
		String timeStr = "";
		if(hour > 0){
			if(hour < 10){
				timeStr += "0" + hour + ":";
			}else{
				timeStr += hour + ":";
			}
		}
		if(min < 10){
			timeStr += "0" + min;
		}else{
			timeStr += min;
		}
		if (sec < 10){
			timeStr += ":0" + sec;// 把音乐播放的进度，转换成时间
		}else{
			timeStr += ":" + sec;
		}
		return timeStr;
	}
	
	public interface PlayerSetDataListener{
		public boolean onSetData(MediaPlayer mediaPlayer,File file);
	}
	
	public interface PlayerListener{
		public void onPlayStateChange(int state, String voiceSrc);
		public void onProgressChange(long currentPosition,long maxPosition, String voiceSrc);
	}
	
	public static class Frame{
		private BitmapDrawable mDrawable;
		private int mWidth;
		private int mHeight;
		private Frame(int width,int height,BitmapDrawable drawable){
			mWidth = width;
			mHeight = height;
			mDrawable = drawable;
		}
		/**
		 * @return the mDrawable
		 */
		public BitmapDrawable getDrawable() {
			return mDrawable;
		}
		/**
		 * @return the mWidth
		 */
		public int getWidth() {
			return mWidth;
		}
		/**
		 * @return the mHeight
		 */
		public int getHeight() {
			return mHeight;
		}
	}
}
