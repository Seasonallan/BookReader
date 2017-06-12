/*
 * ========================================================
 * ClassName:AudioPlayActivity.java* 
 * Description:
 * Copyright (C) 
 * ========================================================
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *===================================================================*
 * Revision History
 *
 * Modification                    Tracking
 * Date         Author          Number       Description of changes
 *____________________________________________________________________
 * 
 * 2013-12-12     chendt          #00000       create
 */
package com.lectek.android.lereader.ui.basereader_leyue.expand_audio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.utils.DateUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.ui.basereader_leyue.expand_audio.MusicView.TouchMusicScoreCallBack;
import com.lectek.android.lereader.ui.basereader_leyue.span.IMedia;
import com.lectek.android.lereader.ui.basereader_leyue.widgets.ReaderMediaPlayer;
import com.lectek.android.lereader.ui.basereader_leyue.widgets.ReaderMediaPlayer.PlayerListener;
import com.lectek.android.lereader.utils.ToastUtil;
import com.lectek.lereader.core.text.html.DataProvider;

/**
 * @description
	改造为Activity
 * @author chendt
 * @date 2013-12-14
 * @Version 1.0
 */
public class AudioPlayView extends Dialog implements PlayerListener,TouchMusicScoreCallBack{
	public static final String TAG = AudioPlayView.class.getSimpleName();
	public static final String GOTO_AUDIO_PLAY_TAG = "goto_audio_play_tag";
	public static final int SCROLL_DURATION = 2000;
	public static final int CONTROL_DURATION = 100;
	private static String CURRENT_TONE = "";
	private SeekBar mMusicSeekBar;
	private TextView currentTimeText,totalTimeText;
	private ArrayList<MusicView> musicViews;
	private ListView mListView;
	private int screen_W,screen_H;
//	private View playView;
	private ArrayList<MusicInfo> musicInfos;
	/**key_tone;value_mp3*/
	private static Map<String, String> tonesMap = new HashMap<String, String>();
	private MyAdapter adapter;
	private LinearLayout container;
	private boolean isStop;
	private String jsonStr;
	private DataProvider dataProvider;
	private Context mContext;
	private LinearLayout playPauseView;
	private ImageView playPauseViewImg;
	private Voice mVoice;
	private LinearLayout titleView;
	private RelativeLayout playLayout;
	private TextView mControlView,titleTextView;
	private View rootView;
	public AudioPlayView(Context context,DataProvider dataProvider,String jsonStr) {
		super(context,R.style.noTitleFullScreenDialogStyle);
		mContext = context;
		jsonStr = jsonStr.replace("\'", "\"");
		this.jsonStr = jsonStr;
		this.dataProvider = dataProvider;
		ReaderMediaPlayer.getInstance().addPlayerListener(this);
		((Activity) mContext).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,                
                 WindowManager.LayoutParams.FLAG_FULLSCREEN); 
	}
	
	@Override
	public void show() {
		if(isShowing()){
			return;
		}
		super.show();
		WindowManager.LayoutParams lp = getWindow().getAttributes();  
		lp.height = fullHeight ;
		lp.width = fullWidth;
		getWindow().setAttributes(lp);
	}
	
	private int fullWidth,fullHeight;
	public void setOritationChange(int heght,int width){
		fullWidth = width;
		fullHeight = heght;
	}
	
	
	@Override
	public void cancel() {
		super.cancel();
		if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){ 
			((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
			WindowManager.LayoutParams attrs = ((Activity) mContext).getWindow().getAttributes(); 
			attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN); 
			((Activity) mContext).getWindow().setAttributes(attrs); 
			//取消全屏设置
			((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		}
		if (wl!=null) {
			wl.release();  
		}
	}
	private PowerManager.WakeLock wl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
		//MyTag可以随便写,可以写应用名称等
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
		//在释放之前，屏幕一直亮着（有可能会变暗,但是还可以看到屏幕内容,换成PowerManager.SCREEN_BRIGHT_WAKE_LOCK不会变暗）
		wl.acquire();  
//		if (!CommonUtil.isTablet(mContext)) {
			setContentView(R.layout.audio_play_view);
//		}else {
//			setContentView(R.layout.audio_play_view_pad);
//		}
		screen_W = fullWidth;
		screen_H = fullHeight;
		String titleName = "乐谱";
		try {
			MusicInfoList list = new MusicInfoList();
			list.fromJsonObject(new JSONObject(jsonStr));
			musicInfos = list.getMusicInfo();
			titleName = list.getTitle();
		} catch (Exception e) {
			ToastUtil.showToast(mContext, "乐谱格式错误，无法播放！");
			((Activity) mContext).finish();
			return;
		}
		for (MusicInfo info : musicInfos) {
			info.screen_W = screen_W;
		}
		titleTextView = (TextView) findViewById(R.id.title);
		titleTextView.setText(titleName);
		mMusicSeekBar = (SeekBar) findViewById(R.id.music_seekbar);
		currentTimeText = (TextView) findViewById(R.id.current_time);
		playPauseViewImg = (ImageView) findViewById(R.id.play_pause_img);
		playPauseView = (LinearLayout) findViewById(R.id.play_pause);
		playPauseView.setOnClickListener(playPauseClickListener);
		totalTimeText = (TextView) findViewById(R.id.total_time);
		totalTimeText.setText(mContext.getString(R.string.music_original_total_time_display));
		mMusicSeekBar.setOnSeekBarChangeListener(mySeekBarChangeListener);
		mListView = (ListView) findViewById(R.id.listview);
		container = (LinearLayout) findViewById(R.id.grid_tone_layout);
        initMusicView();
        getDrawable();
//        if (!CommonUtil.isTablet(mContext)) {
        	mControlView = (TextView) findViewById(R.id.get_control);
        	titleView = (LinearLayout) findViewById(R.id.title_layout);
    		playLayout = (RelativeLayout) findViewById(R.id.play_zone);
    		rootView = findViewById(R.id.root_view);
        	rootView.setOnClickListener(rootClickListener);
        	playLayout.setOnClickListener(rootClickListener);
        	mControlView.setOnClickListener(controlClickListener);
//        }
        wakeUpControlView();
        initAudioData(tonesMap.get(CURRENT_TONE));
	}
	
	View.OnClickListener rootClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			wakeUpControlView();
		}
	};
	
	View.OnClickListener controlClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			handleControlView();
		}
	};
	
	/**
	 * 唤醒或隐藏控制界面
	 */
	private void handleControlView(){
		if (isControlViewShow()) {
			setControlView();
			if (mHandler!=null) {
				mHandler.removeMessages(COUNT_DOWN);
			}
			delayCount = ORIGINAL_COUNT;
		}else {
			wakeUpControlView();
		}
	}
	
	private void setControlView(){
//		if (!CommonUtil.isTablet(mContext)) {
			if (titleView.getVisibility() != View.GONE) {
				titleView.setVisibility(View.GONE);
				playLayout.setVisibility(View.GONE);
				mControlView.setSelected(false);
			} else {
				titleView.setVisibility(View.VISIBLE);
				playLayout.setVisibility(View.VISIBLE);
				mControlView.setSelected(true);
			}
//		}
	}
	
	/**控制布局是否显示*/
	private boolean isControlViewShow(){
		return titleView.getVisibility() != View.GONE;
	}
	
	private void getDrawable() {
		for (int i = 0; i < musicInfos.size(); i++) {
			final MusicInfo musicInfo = musicInfos.get(i);
			dataProvider.getDrawable(musicInfo.getImg(), new DataProvider.DrawableContainer() {
				
				@Override
				public void setDrawable(Drawable paramDrawable) {
					musicInfo.drawable = paramDrawable;
					//刷新指定view TODO：
//					for (int i=0;i<musicViews.size();i++) {
//						Log.e(Tag, "---MusicView---+"+i);
//						musicViews.get(i).refreshView(musicViews.get(i).getMusicInfo(),false);
//					}//刷新不了
					if (adapter!=null) {
						adapter.notifyDataSetChanged();
					}
				}
				
				@Override
				public boolean isInvalid() {
					if (isStop) {
						return true;
					}
					return false;
				}
			});
		}
	}

	private void initMusicView() {
		musicViews = new ArrayList<MusicView>();
		initCurrentTone(musicInfos);
		getTonesArray(musicInfos);
		initToneView();
		adapter = new MyAdapter(mContext, musicInfos);
		mListView.setAdapter(adapter);
//		initAudioData(tonesMap.get(CURRENT_TONE));
	}

	private void initCurrentTone(ArrayList<MusicInfo> musicInfos2) {
		CURRENT_TONE = musicInfos2.get(0).getTone();
	}

	private ArrayList<View> toneViews = new ArrayList<View>(); 
	/**gridView 图片状态不能再布局中设置，且排版不易调整，暂时使用此方式*/
	private void initToneView() {
		for (Iterator<Entry<String, String>> iterator = tonesMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, String>  entry = (Entry<String, String>) iterator.next();
			String tone = entry.getKey();
			View view = LayoutInflater.from(mContext).inflate(R.layout.music_tone_view, null);
			TextView textView = (TextView) view.findViewById(R.id.text);
			textView.setText(tone);
			view.setOnClickListener(toneClickListener);
			view.setTag(tone);
			if (CURRENT_TONE.equals(tone)) {
				textView.setSelected(true);
			}else {
				textView.setSelected(false);
			}
			toneViews.add(view);
			container.addView(view,container.getChildCount()-1);
		}
	}

	private void refreshToneView(){
		for (int i = 0; i < toneViews.size(); i++) {
			if (toneViews.get(i).getTag()!=null) {
				if (toneViews.get(i).getTag().equals(CURRENT_TONE)) {
					toneViews.get(i).findViewById(R.id.text).setSelected(true);
				}else {
					toneViews.get(i).findViewById(R.id.text).setSelected(false);
				}
			}
		}
	}
	
	private View.OnClickListener toneClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (v.getTag()!=null) {
				CURRENT_TONE = (String) v.getTag();
				refreshToneView();
				//切换音调。1.中断播放，更换音频。2.刷新Listview,刷新隐藏游标
				setProgressForView(-1);
				musicViews.clear();
				initAudioData(tonesMap.get(CURRENT_TONE));
				adapter = new MyAdapter(mContext, musicInfos);
				mListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				wakeUpControlView();
			}
		}
	};
	
	private void getTonesArray(ArrayList<MusicInfo> musicInfos){
		if (musicInfos == null || musicInfos.size()<1) {
			return;
		}
		tonesMap.clear();
		for (MusicInfo musicInfo : musicInfos) {
			if (!tonesMap.containsKey(musicInfo.getTone())) {
				tonesMap.put(musicInfo.getTone(), musicInfo.getSrc());
			}
		}
		
	}
	
	/**初始化音频数据*/
	private void initAudioData(final String sourcePath) {
		// 初始化音频数据
		if (ReaderMediaPlayer.getInstance().isPlaying()) {
			LogUtil.e("---isPlaying");
			ReaderMediaPlayer.getInstance().stop();
		}
		mVoice = new Voice(sourcePath);
		LogUtil.e("---startVioce");
		ReaderMediaPlayer.getInstance().startVioce(mVoice);
	}

	View.OnClickListener playPauseClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (!ReaderMediaPlayer.getInstance().isPlaying()) {
				ReaderMediaPlayer.getInstance().setPlayState(true);
			}else {
				//暂停
				ReaderMediaPlayer.getInstance().setPlayState(false);
				setPlayViewState(false);
			}
			wakeUpControlView();
		}
	};
	
	/**
	 * 唤醒控制界面显示，且倒计时
	 */
	private void wakeUpControlView(){
		if (mHandler!=null) {
			mHandler.sendEmptyMessage(HIDE_CONTROL_VIEW);
		}
	}
	
	/**
	 * @param isPlaying
	 * @param view
	 */
	private void setPlayViewState(boolean isPlaying){
		if (playPauseView!=null) {
			if (isPlaying) {//点击变成暂停状态
				if (playPauseViewImg!=null) 
					playPauseViewImg.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.audio_pause));
			}else {
				if (playPauseViewImg!=null) 
					playPauseViewImg.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.audio_start));
			}
		}
	}
	
	private void setProgressForView(long curPosition){
		//传入view 且刷新。
		//避免每次都在listview顶端。对播放完的进度条，要隐藏
		for (int i=0;i<musicViews.size();i++) {
			MusicView view = musicViews.get(i);
			view.getMusicInfo().curPosition = curPosition;
			view.refreshView(view.getMusicInfo(),false);
		}
		
	}
	
	private OnSeekBarChangeListener mySeekBarChangeListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (fromUser == true) {
				seekTo(progress);
				wakeUpControlView();
			}
		}
	};
	
	
	class MyAdapter extends BaseAdapter{
		private Context mContext;
		private ArrayList<MusicInfo> musicInfos;
		public MyAdapter(Context context,ArrayList<MusicInfo> infos){
			mContext = context;
			musicInfos = new ArrayList<MusicInfo>(); 
			for (MusicInfo musicInfo : infos) {
				if (CURRENT_TONE.equals(musicInfo.getTone())) {
					musicInfos.add(musicInfo);
				}
			}
		}
		
		@Override
		public int getCount() {
			return musicInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return musicInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MusicInfo musicInfo = musicInfos.get(position);
			if (convertView == null) {
				convertView = new MusicView(mContext);
			}
			if (!musicViews.contains(convertView)) {
				musicViews.add((MusicView)convertView);
			}
			convertView.setTag(position);
			((MusicView)convertView).setMusicViewCallBack(AudioPlayView.this);
			musicInfo.playIndex = position;
			((MusicView)convertView).refreshView(musicInfo,true);
			((MusicView)convertView).setScreen_WH(screen_W, screen_H);
			if (musicInfo.drawable!=null) {
				convertView.setBackgroundDrawable(musicInfo.drawable);
			}else {
				convertView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.default_music));
			}
			return convertView;
		}
	}
	
	private void seekTo(float duration){
		if(ReaderMediaPlayer.getInstance().isPrepare()){
			ReaderMediaPlayer.getInstance().seekTo((int) duration);
		}else if(mVoice != null){
			ReaderMediaPlayer.getInstance().startVioce(mVoice, (long) duration);
		}else{
			return;
		}
		setPlayViewState(true);
	}

	@Override
	public void dismiss() {
		super.dismiss();
		ReaderMediaPlayer.getInstance().stop();
		mVoice = null;
		isStop = true;
	}

	@Override
	public void scrollPartIndex(int index) {
		if (mListView!=null) {
			Log.d(TAG, "--index--"+index);
			mListView.smoothScrollBy(index, SCROLL_DURATION);
		}
	}
	
	@Override
	public void touchCallBack(float changeTime) {
		seekTo(changeTime);
	}
	
	@Override
	public void onPlayStateChange(int state, String voiceSrc) {
		if(state == ReaderMediaPlayer.STATE_ERROR
				|| state == ReaderMediaPlayer.STATE_STOP){
			if (playPauseViewImg!=null) {
				playPauseViewImg.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.audio_end));
			}
		}else{
			setPlayViewState(state == ReaderMediaPlayer.STATE_START);
		}
	}

	@Override
	public void onProgressChange(long currentPosition, long maxPosition,
			String voiceSrc) {
//		LogUtil.e("--currentPosition---"+currentPosition);
		mMusicSeekBar.setProgress((int)currentPosition);
		currentTimeText.setText(DateUtil.getTimeStr((int)currentPosition/1000));
		mMusicSeekBar.setMax((int) maxPosition);
		totalTimeText.setText(mContext.getString(R.string.music_total_time_display, DateUtil.getTimeStr((int) (maxPosition/1000))));
		setProgressForView(currentPosition);
	}

	private static class Voice implements IMedia{
		private String mVoice;
		private Voice(String voice){
			mVoice = voice;
		}
		@Override
		public String getVoiceSrc() {
			return mVoice;
		}

		@Override
		public long getStartPosition() {
			return 0;
		}

		@Override
		public boolean contains(long position) {
			return true;
		}
	}
	
	private int delayCount = COUNT_DOWN_COUNT;
	private static final int ORIGINAL_COUNT = -1;
	private static final int COUNT_DOWN_COUNT = 2000;
	private static final int HIDE_CONTROL_VIEW = 0x101;  
	private static final int COUNT_DOWN = 0x102;  
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HIDE_CONTROL_VIEW:
//				LogUtil.e("---HIDE_CONTROL_VIEW---delayCount "+delayCount);
				if (delayCount == 0) {
					if (isControlViewShow()) {//隐藏
						setControlView();
					}
					delayCount = ORIGINAL_COUNT;
				}else {
					if (delayCount == ORIGINAL_COUNT) {//显示
						setControlView();
					}
					removeMessages(COUNT_DOWN);
					sendEmptyMessageDelayed(COUNT_DOWN, CONTROL_DURATION);
					delayCount = COUNT_DOWN_COUNT;
				}
				break;
			case COUNT_DOWN:
				delayCount -= 100;
//				LogUtil.e("---COUNT_DOWN---delayCount "+delayCount);
				if (delayCount == 0) {
					sendEmptyMessage(HIDE_CONTROL_VIEW);
				}else {
					sendEmptyMessageDelayed(COUNT_DOWN, CONTROL_DURATION);
				}
				break;
			default:
				break;
			}
			
		}
		
	};
	
	public static interface MediaInitListener{
		public void mediaInit();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {//直接监听，不走模拟系统menu方式。由于不明原因，系统方式，onMenuOpen调不到
		   handleControlView();
		   return true;
		}  
		return super.onKeyDown(keyCode, event);
	};
}
