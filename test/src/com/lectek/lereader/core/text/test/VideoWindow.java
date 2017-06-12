package com.lectek.lereader.core.text.test;
import java.io.File;
import java.io.FileInputStream;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.lectek.bookformats.R;
import com.lectek.lereader.core.text.test.ReaderMediaPlayer.PlayerListener;
import com.lectek.lereader.core.text.test.ReaderMediaPlayer.PlayerSetDataListener;

/**
 * 视频窗口
 * @author linyiwei
 *
 */
public class VideoWindow implements Callback,OnPreparedListener,OnCompletionListener
	,PlayerSetDataListener,OnClickListener,PlayerListener{
	private RelativeLayout mContentView;
	private VideoView mVideoView;
	private VideoSpan mVideo;
	private SurfaceHolder mSurfaceHolder;
	private Window mWindow;
	private Activity mContext;
	private LayoutParams mLayoutParams;
	private Rect mLocalRect;
	private boolean isFullScreen;
	private VideoViewLayout mVideoLayout;
	private int mVideoWidth;
	private int mVideoHeight;
	private Handler mHandler;
	private View mVideoControlView;
	private SeekBar mVoiceSeekBar;
	private TextView mVoiceMaxProgressTV;
	private TextView mVoiceProgressTV;
	private ImageButton mVoiceStateBut;
	private boolean isVoicePlay;
	
	public VideoWindow(Activity context) {
		mContext = context;
		mWindow = context.getWindow();
		
		mContentView = new RelativeLayout(mContext);
		mContentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT
				,ViewGroup.LayoutParams.FILL_PARENT));
		mContentView.setOnClickListener(this);
		mContentView.setVisibility(View.GONE);
		
		mVideoLayout = new VideoViewLayout(mContext);
		mVideoLayout.setBackgroundColor(Color.BLACK);
		mContentView.addView(mVideoLayout);
		
		mVideoView = new VideoView(mContext);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
				,RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		mVideoLayout.addView(mVideoView,lp);
		mVideoView.getHolder().addCallback(this);
		
		ReaderMediaPlayer.getInstance().addPlayerListener(this);
		initVideoControlView();
		
		mLayoutParams = new LayoutParams();
		mLayoutParams.width = LayoutParams.FILL_PARENT;
		mLayoutParams.height = LayoutParams.FILL_PARENT;
		mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; 
		mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		mLayoutParams.format = PixelFormat.TRANSPARENT;
		mWindow.addContentView(mContentView, mLayoutParams);
		
		mLocalRect = new Rect();
		mHandler = new Handler(Looper.getMainLooper());
	}
	
	private void initVideoControlView(){
		mVideoControlView = mContext.getLayoutInflater().inflate(R.layout.reader_voice_control, null);
		mVideoControlView.setVisibility(View.INVISIBLE);
		mVideoControlView.setTag(false);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT
				,RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mContentView.addView(mVideoControlView,lp);
		mVoiceStateBut = (ImageButton)mVideoControlView.findViewById(R.id.menu_reader_voice_state_but);
		mVoiceProgressTV = (TextView)mVideoControlView.findViewById(R.id.menu_reader_voice_progress_tv);
		mVoiceMaxProgressTV = (TextView)mVideoControlView.findViewById(R.id.menu_reader_voice_max_progress_tv);
		mVoiceSeekBar = (SeekBar)mVideoControlView.findViewById(R.id.menu_reader_voice_seek);
		final ReaderMediaPlayer voicePlayer = ReaderMediaPlayer.getInstance();
		onProgressChange(voicePlayer.getCurrentPosition(), voicePlayer.getDuration(), null);
		if(isVoicePlay){
			mVoiceStateBut.setImageResource(R.drawable.ic_menu_reader_voice_play);
		}else{
			mVoiceStateBut.setImageResource(R.drawable.ic_menu_reader_voice_pause);
		}
		mVoiceStateBut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isVoicePlay = !isVoicePlay;
				voicePlayer.setPlayState(isVoicePlay);
				delayedHideVideoControlView();
			}
		});
		mVoiceSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				voicePlayer.seekTo(seekBar.getProgress());
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				voicePlayer.pause();
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
				onProgressChange(progress, seekBar.getMax(), null);
				if(fromUser){
					delayedHideVideoControlView();
				}
			}
		});
	}
	
	private boolean isControlViewShow(){
		return (Boolean) mVideoControlView.getTag();
	}
	
	private void showVideoControlView(){
		if(isControlViewShow()){
			return;
		}
		mVideoControlView.setVisibility(View.VISIBLE);
		Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.popup_show);
		mVideoControlView.startAnimation(animation);
		mVideoControlView.setTag(true);
		delayedHideVideoControlView();
	}
	
	private void delayedHideVideoControlView(){
		mHandler.removeCallbacks(mHideControlViewRunnable);
		mHandler.postDelayed(mHideControlViewRunnable, 3000);
	}
	
	private void hideVideoControlView(){
		mHandler.removeCallbacks(mHideControlViewRunnable);
		if(!isControlViewShow()){
			return;
		}
		Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.popup_hide);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				mVideoControlView.setVisibility(View.INVISIBLE);
			}
		});
		mVideoControlView.startAnimation(animation);
		mVideoControlView.setTag(false);
	}
	
	@Override
	public void onPlayStateChange(int playState, String voiceSrc) {
		if(mVideo != null && mVideo.getVoiceSrc().equals(voiceSrc)){
			isVoicePlay = playState == ReaderMediaPlayer.STATE_START;
			if(isVoicePlay){
				mVoiceStateBut.setImageResource(R.drawable.ic_menu_reader_voice_play);
			}else{
				mVoiceStateBut.setImageResource(R.drawable.ic_menu_reader_voice_pause);
			}
			mVoiceSeekBar.setEnabled(ReaderMediaPlayer.getInstance().isPrepare());
			if(playState == ReaderMediaPlayer.STATE_STOP
					|| playState == ReaderMediaPlayer.STATE_ERROR){
				dismiss();
			}
		}
	}

	@Override
	public void onProgressChange(long currentPosition, long maxPosition, String voiceSrc) {
		if(mVideo != null && mVideo.getVoiceSrc().equals(voiceSrc)){
			mVoiceSeekBar.setMax((int) maxPosition);
			mVoiceSeekBar.setProgress((int) currentPosition);
			mVoiceProgressTV.setText(ReaderMediaPlayer.getTimeStr((int) (currentPosition / 1000)));
			mVoiceMaxProgressTV.setText(ReaderMediaPlayer.getTimeStr((int) (maxPosition / 1000)));
		}
	}
	
	private void showFullScreen(){
		if(mContext.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  
		}
		DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
		int w = displayMetrics.widthPixels;
		int h = displayMetrics.heightPixels;
		setVideoLocation(-1,-1,h,w);
		mContentView.setBackgroundColor(Color.BLACK);
		if(!isShow()){
			show();
		}
		isFullScreen = true;
	}
	
	private void showInline(){
		if(mContext.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
			mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
		}
		setVideoLocation(mLocalRect.left,mLocalRect.top,mLocalRect.width(),mLocalRect.height());
		mContentView.setBackgroundColor(Color.TRANSPARENT);
		if(!isShow()){
			show();
		}
		isFullScreen = false;
		hideVideoControlView();
	}
	
	public void handlerPlayVideo(VideoSpan video, RectF localRect){
		if(ReaderMediaPlayer.getInstance() == null){
			return;
		}
		mVideo = video;
		mLocalRect.set((int)localRect.left, (int)localRect.top, (int)localRect.right,(int)localRect.bottom);
		showInline();
	}
	
	@Override
	public void onClick(View v) {
		if(!isFullScreen){
			showFullScreen();
		}else{
			if(isControlViewShow()){
				hideVideoControlView();
			}else{
				showVideoControlView();
			}
		}
	}
	
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(isShow()){
			if(isFullScreen){
				mContentView.dispatchTouchEvent(ev);
				return true;
			}else if(mLocalRect.contains((int)ev.getX(),(int)ev.getY())){
				mContentView.dispatchTouchEvent(ev);
				return true;
			}
		}
		return false;
	}
	
	public boolean dispatchKeyEvent(KeyEvent event){
		if(isShow()){
			if(isFullScreen){
				if(event.getAction() == KeyEvent.ACTION_UP){
					switch (event.getKeyCode()) {
					case KeyEvent.KEYCODE_BACK:
						showInline();
						break;
					case KeyEvent.KEYCODE_MENU:
						if(isControlViewShow()){
							hideVideoControlView();
						}else{
							showVideoControlView();
						}
						break;
					}
				}
				if(event.getKeyCode() == KeyEvent.KEYCODE_BACK
						|| event.getKeyCode() == KeyEvent.KEYCODE_MENU){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isShow(){
		return mContentView.isShown();
	}
	private void setVideoLocation(int x,int y,int w,int h){
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, h);
		if(x < 0 || y < 0){
			if(x >= 0){
				lp.leftMargin = x;
				lp.topMargin = 0;
				lp.addRule(RelativeLayout.CENTER_VERTICAL);
			}else if(y >= 0){
				lp.leftMargin = 0;
				lp.topMargin = y;
				lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			}else{
				lp.leftMargin = 0;
				lp.topMargin = 0;
				lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			}
		}else{
			lp.leftMargin = x;
			lp.topMargin = y;
		}
		mVideoLayout.setLayoutParams(lp);
	}
	
	private void show() {
		if(mVideo == null){
			return;
		}
		ReaderMediaPlayer.getInstance().setOnVideoSizeChangedListener(mVideoView);
		ReaderMediaPlayer.getInstance().setOnPreparedListener(this);
		ReaderMediaPlayer.getInstance().setPlayerSetDataListener(this);
		ReaderMediaPlayer.getInstance().setOnCompletionListener(this);
		ReaderMediaPlayer.getInstance().startVioce(mVideo);
		mVideoView.setBackgroundColor(Color.BLACK);
		mContentView.setVisibility(View.VISIBLE);
		mHandler.removeCallbacks(mHideVideoViewRunnable);
		mVideoView.setVisibility(View.VISIBLE);
	}

	public void dismiss() {
		if(!isShow() || ReaderMediaPlayer.getInstance() == null){
			return;
		}
		mContentView.setVisibility(View.GONE);
		ReaderMediaPlayer.getInstance().setDisplay(null);
		ReaderMediaPlayer.getInstance().setOnVideoSizeChangedListener(null);
		ReaderMediaPlayer.getInstance().setOnPreparedListener(null);
		ReaderMediaPlayer.getInstance().setPlayerSetDataListener(null);
		ReaderMediaPlayer.getInstance().setOnCompletionListener(null);
		ReaderMediaPlayer.getInstance().stop();
		ReaderMediaPlayer.getInstance().reset();
		if(mVideo != null){
			ReaderMediaPlayer.getInstance().createLastFrame(mVideo.getVoiceSrc(),mVideoView.getWidth(),mVideoView.getHeight());
		}
		if(mContext.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
			mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
		}
		mContentView.setBackgroundColor(Color.TRANSPARENT);
		mVideoWidth = 0;
		mVideoHeight = 0;
		mHandler.removeCallbacks(mShowVideoRunnable);
		hideVideoControlView();
		setVideoLocation(0, 0, 0, 0);
		mVideo = null;
		mHandler.post(mHideVideoViewRunnable);
	}

	@Override
	public boolean onSetData(MediaPlayer mediaPlayer, File file) {
		try {
			if(mSurfaceHolder != null && mVideo != null){
				FileInputStream fis = new FileInputStream(file);
				mediaPlayer.setDataSource(fis.getFD());
				mediaPlayer.setDisplay(mSurfaceHolder);
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		boolean needStart = mSurfaceHolder == null && mVideo != null;
        mSurfaceHolder = holder;
		if(needStart){
			ReaderMediaPlayer.getInstance().startVioce(mVideo);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceHolder = null;
        ReaderMediaPlayer.getInstance().stop();
	}
	
	@Override
	public void onPrepared(MediaPlayer mp) {
		mVideoView.onVideoSizeChanged(mp, mp.getVideoWidth(), mp.getVideoHeight());
		mHandler.postDelayed(mShowVideoRunnable, 500);
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		dismiss();
	}
	
	private Runnable mHideVideoViewRunnable = new Runnable() {
		@Override
		public void run() {
			mVideoView.setVisibility(View.GONE);
		}
	};
	
	private Runnable mShowVideoRunnable = new Runnable() {
		@Override
		public void run() {
			mVideoView.setBackgroundDrawable(null);
		}
	};
	
	private Runnable mHideControlViewRunnable = new Runnable() {
		@Override
		public void run() {
			hideVideoControlView();
		}
	};
	
	public class VideoViewLayout extends RelativeLayout{
		public VideoViewLayout(Context context) {
			super(context);
		}
		
		@Override
		protected void dispatchDraw(Canvas canvas) {
			super.dispatchDraw(canvas);
			if(mVideo != null && mVideoView.getBackground() != null){
				mVideo.drawContent(canvas,0,0,getWidth(),getHeight());
			}
		}
	}
	
	public class VideoView extends SurfaceView implements OnVideoSizeChangedListener{
		public VideoView(Context context) {
			super(context);
			mVideoWidth = 0;
	        mVideoHeight = 0;
	        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	        getHolder().setFormat(PixelFormat.TRANSPARENT);
		}

		@Override
		public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                requestLayout();
            }
        }
		    
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
			int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
	        if (mVideoWidth > 0 && mVideoHeight > 0) {
	            if ( mVideoWidth * height  > width * mVideoHeight ) {
	                height = width * mVideoHeight / mVideoWidth;
	            } else if ( mVideoWidth * height  < width * mVideoHeight ) {
	                width = height * mVideoWidth / mVideoHeight;
	            } else {
	                
	            }
	        }
	        setMeasuredDimension(width, height);
		}
	}
}
