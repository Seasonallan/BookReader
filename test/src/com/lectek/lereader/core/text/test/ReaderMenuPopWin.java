package com.lectek.lereader.core.text.test;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.lectek.bookformats.R;
import com.lectek.lereader.core.text.test.CheckedGridView.OnItemCheckedStateChangeListener;
import com.lectek.lereader.core.text.test.ReaderMediaPlayer.PlayerListener;

public class ReaderMenuPopWin extends BasePopupWindow implements PlayerListener{
	private static final int FONT_INCREASE_UNIT = 1;
	private static final int MAX_MENU_SIZE = 5;
	private Activity mActivity;
	private Handler mHandler;
	private Book mBook;
	private IActionCallback mActionCallback;
	private CheckedGridView mGridView;
	private View addBookmarkIB;
	private ViewGroup mChildMenuLayout;
	private TextView mTitleTV;
	private View mBuyBut;
	private boolean mHasBookmark;
	private ArrayList<MenuItem> mMoreMenuItems;
	private View mJumpPageView;
	private int mTotalPageNums;
	private TextView mJumpPageTip;
	private SeekBar mJumpSeekBar;
	private View mJumpPreBut;
	private View mJumpNextBut;
	private View mBrightessSettingView;
	private View mMoreView;
	private View mThemeView;
	private View mFontSettingView;
	private View mCutFontSizeBut;
	private View mAddFontSizeBut;
	private ReadSetting mReadSetting;
	private RadioGroup mLineSpacingRG;
	private View mVoiceLayout;
	private SeekBar mVoiceSeekBar;
	private TextView mVoiceMaxProgressTV;
	private TextView mVoiceProgressTV;
	private ImageButton mVoiceCloseBut;
	private ImageButton mVoiceStateBut;
	private boolean isVoicePlay;
	private AudioManager mAudioManager;
	public ReaderMenuPopWin(View parent,Activity activity,Book book,IActionCallback callback) {
		super(parent, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mActivity = activity;
		mReadSetting = ReadSetting.getInstance(mActivity);
		mHandler = new Handler(Looper.getMainLooper());
		mBook = book;
		mActionCallback = callback;
		//设置屏幕亮度
		setScreenBrightess(mReadSetting.getBrightessLevel());
		ReaderMediaPlayer.getInstance().addPlayerListener(this);
		isVoicePlay = ReaderMediaPlayer.getInstance().isPlaying();
		mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
	}
	
	public void validateBookmarkState(boolean hasBookmark) {
		mHasBookmark = hasBookmark;
		if(addBookmarkIB == null) {
			return;
		}
//		if(!hasBookmark) {
//			addBookmarkIB.setBackgroundResource(R.drawable.bookmark_icon);
//		}else {
//			addBookmarkIB.setBackgroundResource(R.drawable.bookmark_delete_icon);
//		}
	}
	
	@Override
	protected View onCreateContentView() {
		View contentView = getLayoutInflater().inflate(R.layout.reader_menu_leyue, null);
		mVoiceLayout = contentView.findViewById(R.id.menu_reader_voice_layout);
		mVoiceStateBut = (ImageButton)contentView.findViewById(R.id.menu_reader_voice_state_but);
		mVoiceCloseBut = (ImageButton)contentView.findViewById(R.id.menu_reader_voice_close_but);
		mVoiceProgressTV = (TextView)contentView.findViewById(R.id.menu_reader_voice_progress_tv);
		mVoiceMaxProgressTV = (TextView)contentView.findViewById(R.id.menu_reader_voice_max_progress_tv);
		mVoiceSeekBar = (SeekBar)contentView.findViewById(R.id.menu_reader_voice_seek);
		final ReaderMediaPlayer voicePlayer = ReaderMediaPlayer.getInstance();
		onProgressChange(voicePlayer.getCurrentPosition(), voicePlayer.getDuration(), null);
		if(isVoicePlay){
			mVoiceStateBut.setImageResource(R.drawable.ic_menu_reader_voice_play);
		}else{
			mVoiceStateBut.setImageResource(R.drawable.ic_menu_reader_voice_pause);
		}
		mVoiceCloseBut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				voicePlayer.stop();
				Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left_hide);
				animation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {}
					
					@Override
					public void onAnimationRepeat(Animation animation) {}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						mVoiceLayout.setVisibility(View.GONE);
					}
				});
				mVoiceLayout.startAnimation(animation);
			}
		});
		mVoiceStateBut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isVoicePlay = !isVoicePlay;
				voicePlayer.setPlayState(isVoicePlay);
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
			}
		});
		
		mChildMenuLayout = (ViewGroup) contentView.findViewById(R.id.menu_child_layout);
		contentView.findViewById(R.id.transparent_view).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		addBookmarkIB = contentView.findViewById(R.id.menu_add_bookmark_ib);
		addBookmarkIB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!mHasBookmark){
					mActionCallback.onSaveUserBookmark();
				}else{
					mActionCallback.onDeleteUserBookmark();
				}
			}
		});
		if (mActionCallback.canAddUserBookmark()) {
			addBookmarkIB.setVisibility(View.VISIBLE);
		}
		
		mTitleTV = (TextView) contentView.findViewById(R.id.menu_body_title);
		mBuyBut = contentView.findViewById(R.id.menu_buy_but);
		ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
		menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_CATALOG, R.drawable.menu_icon_mark, getString(R.string.reader_menu_item_catalog_tip)));
		menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_FONT, R.drawable.menu_icon_font, getString(R.string.reader_menu_item_font_tip)));
		menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_THEME, R.drawable.menu_icon_background, getString(R.string.reader_menu_item_theme_tip)));
		menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_BRIGHTNESS, R.drawable.menu_icon_brightness, getString(R.string.reader_menu_item_brightness_tip)));
//		menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_SETTING, R.drawable.menu_icon_settings, getString(R.string.reader_menu_item_setting_tip)));
		if(menuItems.size() > MAX_MENU_SIZE){
			mMoreMenuItems = new ArrayList<MenuItem>(menuItems.subList(MAX_MENU_SIZE - 1, menuItems.size()));
			menuItems = new ArrayList<MenuItem>(menuItems.subList(0, MAX_MENU_SIZE - 1));
			menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_MORE, R.drawable.menu_icon_more, getString(R.string.reader_menu_item_more_tip)));
		}
		MenuItemAdapter adapter = new MenuItemAdapter(getContext(), menuItems);
		mGridView = (CheckedGridView) contentView.findViewById(R.id.reader_menu_gv);
		mGridView.setChoiceMode(CheckedGridView.CHOICE_MODE_SINGLE);
		mGridView.setAdapter(adapter);
		mGridView.setNumColumns(menuItems.size());
		mGridView.setOnItemCheckedStateChangeListener(new OnItemCheckedStateChangeListener() {
			@Override
			public void onItemCheckedStateChange(AdapterView<?> parent, int position,boolean isChecked) {
			}
			@Override
			public boolean onPreItemCheckedStateChange(AdapterView<?> parent,
					int position, boolean isChecked) {
				MenuItem item = (MenuItem) parent.getItemAtPosition(position);
				if(isChecked){
					return handlerMenuItemAction(item);
				}else{
					showJumpPageView();
				}
				return false;
			}
		});
		return contentView;
	}
	
	private void updateState(){
		if(mActionCallback.isNeedBuy()){
			mBuyBut.setVisibility(View.VISIBLE);
			mBuyBut.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mActionCallback.onGotoBuyBook();
					dismiss();
				}
			});
		}else{
			mBuyBut.setVisibility(View.GONE);
		}
		mTitleTV.setVisibility(View.VISIBLE);
		mTitleTV.setText(mBook.getBookName());
		validateBookmarkState(mHasBookmark);
	}

	public void setLayoutChapterProgress(int progress,int max){
		if(mJumpPageView != null){
			if(progress == max){
				setJumpSeekBarProgress(mActionCallback.getCurPage(), mActionCallback.getPageNums());
				mJumpSeekBar.setSecondaryProgress(0);
				mJumpPageTip.setTextColor(getResources().getColor(R.color.common_white_2));
			}else{
				mJumpSeekBar.setProgress(0);
				mJumpSeekBar.setSecondaryProgress(progress);
				mJumpSeekBar.setMax(max);
				mJumpSeekBar.setEnabled(false);
				mJumpPreBut.setEnabled(mActionCallback.hasPreChapter());
				mJumpNextBut.setEnabled(mActionCallback.hasNextChapter());
				mJumpPageTip.setText(getString(R.string.reader_menu_item_seek_layouting_tip, (int)(progress * 1f / max * 100)+"%"));
				mJumpPageTip.setTextColor(Color.parseColor("#60ffffff"));
			}
		}
	}
	
	public void setJumpSeekBarProgress(int progress,int max){
		if(mJumpPageView != null){
			mTotalPageNums = max;
			updateJumpSeekBar(progress, mTotalPageNums);
		}
	}
	
	private void showPageNum(int curPage, int pageNums){
		if(pageNums > 1){
			curPage += 1;
		}
		if(mJumpPageTip != null){
			mJumpPageTip.setText(getString(R.string.reader_menu_item_seek_page_tip, curPage, pageNums));
		}
	}
	
	private void gotoPage(int page,int oldPage){
		mActionCallback.onGotoPage(page);
	}
	
	private void showJumpPageView(){
		int pageNums = mActionCallback.getPageNums();
		int curPage = mActionCallback.getCurPage();
		mTotalPageNums = pageNums;
		if(mJumpPageView == null){
			mJumpPageView = getLayoutInflater().inflate(R.layout.menu_jump_page_leyue, null);
			mJumpPageTip = (TextView) mJumpPageView.findViewById(R.id.page_text);
			mJumpSeekBar = ((SeekBar) mJumpPageView.findViewById(R.id.jump_page_seek));
			mJumpSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				int oldPage = 0;
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					showPageNum(seekBar.getProgress(), mTotalPageNums);
					gotoPage(seekBar.getProgress(),oldPage);
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					oldPage = seekBar.getProgress();
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					showPageNum(seekBar.getProgress(), mTotalPageNums);
				}
			});
			mJumpPreBut = mJumpPageView.findViewById(R.id.jump_pre_but);
			mJumpPreBut.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mActionCallback.gotoPreChapter();
					mJumpPreBut.setEnabled(mActionCallback.hasPreChapter());
					mJumpNextBut.setEnabled(mActionCallback.hasNextChapter());
				}
			});
			mJumpNextBut = mJumpPageView.findViewById(R.id.jump_next_but);
			mJumpNextBut.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mActionCallback.gotoNextChapter();
					mJumpPreBut.setEnabled(mActionCallback.hasPreChapter());
					mJumpNextBut.setEnabled(mActionCallback.hasNextChapter());
				}
			});
		}
		updateJumpSeekBar(curPage, mTotalPageNums);
		showChildMenu(mJumpPageView);
	}
	
	private void updateJumpSeekBar(int curPage,int max){
		if(max < 0){
			setLayoutChapterProgress(mActionCallback.getLayoutChapterProgress(), mActionCallback.getLayoutChapterMax());
		}else{
			if(max == 1){
				curPage = 1;
				max = 1;
				mJumpSeekBar.setMax(max);
				mJumpSeekBar.setProgress(curPage);
				mJumpSeekBar.setEnabled(false);
			}else{
				mJumpSeekBar.setMax(max - 1);
				mJumpSeekBar.setProgress(curPage);
				mJumpSeekBar.setEnabled(true);
			}
			mJumpPreBut.setEnabled(mActionCallback.hasPreChapter());
			mJumpNextBut.setEnabled(mActionCallback.hasNextChapter());
			showPageNum(curPage, max);
		}
	}
	
	private void showBrightessSettingView(){
		SeekBar seekBar = null;
		if(mBrightessSettingView == null){
			mBrightessSettingView = getLayoutInflater().inflate(R.layout.menu_brightness_setting_layout, null);
			seekBar = (SeekBar) mBrightessSettingView.findViewById(R.id.brightness_seek);
			seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					mReadSetting.setBrightessLevel(seekBar.getProgress());
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					setScreenBrightess(progress);
				}
			});
			mBrightessSettingView.findViewById(R.id.brightness_auto_but).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//TODO 亮度自动调整
				}
			});
		}
		seekBar = (SeekBar) mBrightessSettingView.findViewById(R.id.brightness_seek);
		WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
		float oldValue = lp.screenBrightness;
		if(oldValue < 0){
			seekBar.setProgress(50);
		}else{
			if(oldValue <= 0.17f){
				seekBar.setProgress(0);
			}else{
				seekBar.setProgress((int) (oldValue * 100));
			}
		}
		showChildMenu(mBrightessSettingView);
	}
	
	private void showThemeView(){
		if(mThemeView == null){
			mThemeView = getLayoutInflater().inflate(R.layout.reader_background, null);
		}
		GridView gridView  = (GridView) mThemeView;
		ArrayList<ReadStyleItem> readStyleItems = new ArrayList<ReadStyleItem>();
		int style = mReadSetting.getThemeType();
		readStyleItems.add(new ReadStyleItem(ReadSetting.THEME_TYPE_DAY, R.drawable.ic_read_style_day, 
				style == ReadSetting.THEME_TYPE_DAY));
		readStyleItems.add(new ReadStyleItem(ReadSetting.THEME_TYPE_OTHERS_1, R.drawable.ic_read_style_other_1, 
				style == ReadSetting.THEME_TYPE_OTHERS_1));
		readStyleItems.add(new ReadStyleItem(ReadSetting.THEME_TYPE_OTHERS_2, R.drawable.ic_read_style_other_2, 
				style == ReadSetting.THEME_TYPE_OTHERS_2));
		readStyleItems.add(new ReadStyleItem(ReadSetting.THEME_TYPE_OTHERS_3, R.drawable.ic_read_style_other_3, 
				style == ReadSetting.THEME_TYPE_OTHERS_3));
		readStyleItems.add(new ReadStyleItem(ReadSetting.THEME_TYPE_OTHERS_4, R.drawable.ic_read_style_other_4, 
				style == ReadSetting.THEME_TYPE_OTHERS_4));
		
		final ReadStytleItemAdapter adapter = new ReadStytleItemAdapter(getContext(), readStyleItems);
		gridView.setNumColumns(readStyleItems.size());
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.setSeleted(position);
				ReadStyleItem item = (ReadStyleItem) parent.getItemAtPosition(position);
				if(item != null){
					int styleId = item.id;
					mReadSetting.setThemeType(styleId);
//					updateLightState(styleId);
				}
			}
		});
		showChildMenu(mThemeView);
	}

	private void showFontSettingView(){
		if(mFontSettingView == null){
			//初始化字体大小设置逻辑
			mFontSettingView = getLayoutInflater().inflate(R.layout.reader_menu_font_settings, null);
			mCutFontSizeBut = mFontSettingView.findViewById(R.id.menu_settings_font_size_sut_but);
			mAddFontSizeBut = mFontSettingView.findViewById(R.id.menu_settings_font_size_add_but);
			mAddFontSizeBut.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int temtFontProgress = mReadSetting.getFontLevel();
					temtFontProgress += FONT_INCREASE_UNIT;
					if(temtFontProgress > 10){
						temtFontProgress = 10;
					}
					if(temtFontProgress == 10){
						ToastUtil.showToast(mActivity, R.string.reader_menu_item_font_size_max_tip);
						v.setEnabled(false);
					}
					mReadSetting.setFontLevel(temtFontProgress);
					if(!mCutFontSizeBut.isEnabled()){
						mCutFontSizeBut.setEnabled(true);
					}
				}
			});
			mCutFontSizeBut.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int temtFontProgress = mReadSetting.getFontLevel();
					temtFontProgress -= FONT_INCREASE_UNIT;
					if(temtFontProgress < 0){
						ToastUtil.showToast(mActivity, R.string.reader_menu_item_font_size_min_tip);
						temtFontProgress = 0;
					}
					if(temtFontProgress == 0){
						v.setEnabled(false);
					}
					mReadSetting.setFontLevel(temtFontProgress);
					if(!mAddFontSizeBut.isEnabled()){
						mAddFontSizeBut.setEnabled(true);
					}
				}
			});
			//初始化行距设置逻辑
			mLineSpacingRG = (RadioGroup)mFontSettingView.findViewById(R.id.menu_settings_line_spacing_rg);
			mLineSpacingRG.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					float lineSpaceType = ReadSetting.FONT_LINE_SPACE_TYPE_1;
					if(checkedId == R.id.menu_settings_line_spacing_but_1){
						lineSpaceType = ReadSetting.FONT_LINE_SPACE_TYPE_1;
					}else if(checkedId == R.id.menu_settings_line_spacing_but_2){
						lineSpaceType = ReadSetting.FONT_LINE_SPACE_TYPE_2;
					}else if(checkedId == R.id.menu_settings_line_spacing_but_3){
						lineSpaceType = ReadSetting.FONT_LINE_SPACE_TYPE_3;
					}
					mReadSetting.setLineSpaceType(lineSpaceType);
				}
			});
		}
		
		//同步字体大小设置状态
		int temtFontProgress = mReadSetting.getFontLevel();
		if(temtFontProgress == 10){
			mAddFontSizeBut.setEnabled(false);
		}else if(temtFontProgress == 0){
			mCutFontSizeBut.setEnabled(false);
		}
		//同步行距设置状态
		float lineSpaceType = mReadSetting.getLineSpaceType();
		if(lineSpaceType == ReadSetting.FONT_LINE_SPACE_TYPE_1){
			mLineSpacingRG.check(R.id.menu_settings_line_spacing_but_1);
		}else if(lineSpaceType == ReadSetting.FONT_LINE_SPACE_TYPE_2){
			mLineSpacingRG.check(R.id.menu_settings_line_spacing_but_2);
		}else if(lineSpaceType == ReadSetting.FONT_LINE_SPACE_TYPE_3){
			mLineSpacingRG.check(R.id.menu_settings_line_spacing_but_3);
		}
		//显示界面
		showChildMenu(mFontSettingView);
	}
	
	private void showMoreView(){
		if(mMoreView == null){
			mMoreView = getLayoutInflater().inflate(R.layout.reader_menu_more, null);
			if(mMoreMenuItems != null){
				MenuItemAdapter adapter = new MenuItemAdapter(getContext(), mMoreMenuItems);
				GridView gridView = (GridView) mMoreView;
				gridView.setAdapter(adapter);
				gridView.setNumColumns(mMoreMenuItems.size());
				gridView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						MenuItem item = (MenuItem) parent.getItemAtPosition(position);
						handlerMenuItemAction(item);
					}
				});
			}
		}
		showChildMenu(mMoreView);
	}
	
	private void showChildMenu(View childContentView){
		hideAllViews();
		childContentView.setAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
		if(childContentView.getParent() == null){
			mChildMenuLayout.addView(childContentView);
		}else{
			childContentView.setVisibility(View.VISIBLE);
		}
	}
	
	private void hideAllViews(){
		int count = mChildMenuLayout.getChildCount();
		View child = null;
		for(int i = 0;i < count;i++){
			child = mChildMenuLayout.getChildAt(i);
			child.setVisibility(View.GONE);
			child.setAnimation(null);
		}
	}
	
	/** 设置屏幕亮度
	 * @param value
	 */
	private void setScreenBrightess(int value){
		final WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
		lp.screenBrightness = value * 1.0f / 100.0f;
		if(lp.screenBrightness < 0.17){
			lp.screenBrightness = 0.17f;
		}
		mActivity.getWindow().setAttributes(lp);
	}
	
	@Override
	protected void onPreShow() {
		hideAllViews();
		mHandler.removeCallbacks(mFullScreenRunnable);
//		if(Build.MANUFACTURER.equals(Manufacturer.SAMSUNG)){
//			mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		}else{
//			mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//		}
		WindowManager.LayoutParams attrs = mActivity.getWindow().getAttributes();
		attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
		mActivity.getWindow().setAttributes(attrs);
		if(!ReaderMediaPlayer.getInstance().isPlayerStop()){
			mVoiceLayout.setVisibility(View.GONE);
		}else{
			mVoiceLayout.setVisibility(View.VISIBLE);
		}
		updateState();
		mGridView.clearChoices();
		showJumpPageView();
		super.onPreShow();
	}

	@Override
	protected void onDismiss() {
		super.onDismiss();
		mHandler.removeCallbacks(mFullScreenRunnable);
		mHandler.postDelayed(mFullScreenRunnable,500);
	}

	@Override
	protected boolean dispatchKeyEvent(KeyEvent event){
		//TODO:由于Reader界面不采用模拟系统menu 调用方式。故此处屏蔽取消
//		if(event.getKeyCode() == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP){
		
		//如果pop isShowing，点击menu事件 会被其拦截；所以用down来处理menu的消失。
		//如果pop dismiss，点击menu事件 会被界面的onKeyDown 拦截；用来处理调用pop，即menu的产生。
		if(event.getKeyCode() == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN){
			if (isShowing()) {
				dismiss();
			}
			return true;
		}
		if(ReaderMediaPlayer.getInstance().isNeedControlVolume()){
			int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP){
				int streamVolumeMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				streamVolume++;
				if(streamVolume > streamVolumeMax){
					streamVolume = streamVolumeMax;
				}
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,streamVolume, AudioManager.FLAG_SHOW_UI);
				return true;
			}
			if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN){
				streamVolume--;
				if(streamVolume < 0){
					streamVolume = 0;
				}
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, streamVolume, AudioManager.FLAG_SHOW_UI);
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	public void dismiss() {
		if(mActivity.isFinishing()){
			return;
		}
		super.dismiss();
	}
	
	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		if(mActivity.isFinishing()){
			return;
		}
		super.showAtLocation(parent, gravity, x, y);
	}

	private boolean handlerMenuItemAction(MenuItem item){
		if(item != null){
			ToastUtil.dismissToast();
			switch(item.id){
			case MenuItem.MENU_ITEM_ID_BRIGHTNESS:
				ToastUtil.showToast(getContext(), R.string.reader_menu_automatic_brightness_tip);
				showBrightessSettingView();
				break;
			case MenuItem.MENU_ITEM_ID_FONT:
				showFontSettingView();
				break;
			case MenuItem.MENU_ITEM_ID_SETTING:
				return true;
			case MenuItem.MENU_ITEM_ID_CATALOG:
				mActionCallback.onShowReaderCatalog();
				dismiss();
				return true;
			case MenuItem.MENU_ITEM_ID_MORE:
				showMoreView();
				break;
			case MenuItem.MENU_ITEM_ID_THEME:
				showThemeView();
				break;
			}
		}
		return false;
	}
	
	private Runnable mFullScreenRunnable = new Runnable() {
		@Override
		public void run() {
//			if(Build.MANUFACTURER.equals(Manufacturer.SAMSUNG)){
//				mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//			}else{
//				mActivity.getWindow().setFlags(~WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//			}
			WindowManager.LayoutParams attrs = mActivity.getWindow().getAttributes();
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			mActivity.getWindow().setAttributes(attrs);	
		}
	};
	
	public interface IActionCallback{
		public int getLayoutChapterProgress();
		public int getLayoutChapterMax();
		public int getPageNums();
		public int getCurPage();
		public void onGotoPage(int pageNum);
		public void onGotoBuyBook();
		public boolean isNeedBuy();
		public boolean canAddUserBookmark();
		public void onSaveUserBookmark();
		public void onDeleteUserBookmark();
		public void onShowReaderCatalog();
		public void gotoPreChapter();
		public void gotoNextChapter();
		public boolean hasPreChapter();
		public boolean hasNextChapter();
	}

	@Override
	public void onPlayStateChange(int playState, String voiceSrc) {
		isVoicePlay = playState == ReaderMediaPlayer.STATE_START;
		if(mVoiceLayout != null){
			if(isVoicePlay){
				mVoiceStateBut.setImageResource(R.drawable.ic_menu_reader_voice_play);
			}else{
				mVoiceStateBut.setImageResource(R.drawable.ic_menu_reader_voice_pause);
			}
			if(!ReaderMediaPlayer.getInstance().isPlayerStop()){
				mVoiceLayout.setVisibility(View.GONE);
			}else{
				mVoiceLayout.setVisibility(View.VISIBLE);
			}
			mVoiceSeekBar.setEnabled(ReaderMediaPlayer.getInstance().isPrepare());
		}
	}

	@Override
	public void onProgressChange(long currentPosition, long maxPosition, String voiceSrc) {
		if(mVoiceLayout != null){
			mVoiceSeekBar.setMax((int) maxPosition);
			mVoiceSeekBar.setProgress((int) currentPosition);
			mVoiceProgressTV.setText(ReaderMediaPlayer.getTimeStr((int) (currentPosition / 1000)));
			mVoiceMaxProgressTV.setText(ReaderMediaPlayer.getTimeStr((int) (maxPosition / 1000)));
		}
	}
	
	public class ReadStyleItem {
		public int id;
		public int resId;
		public boolean isSelected;
		
		public ReadStyleItem(int id, int resId, boolean isSelected){
			this.id = id;
			this.resId = resId;
			this.isSelected = isSelected;
		}
	}
	
	public class ReadStytleItemAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private ArrayList<ReadStyleItem> readStyleItems;
		private int oldSelect = -1;
		public ReadStytleItemAdapter(Context context, ArrayList<ReadStyleItem> readStyleItems){
			super();
			inflater = LayoutInflater.from(context);
			this.readStyleItems = readStyleItems;
		}

		@Override
		public int getCount() {
			if(readStyleItems != null){
				return readStyleItems.size();
			}
			return 0;
		}

		@Override
		public ReadStyleItem getItem(int position) {
			if(position < getCount()){
				return readStyleItems.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder viewHolder;
			if(convertView == null){
				convertView = newView();
				viewHolder = new ViewHolder();
				viewHolder.contentIV = (ImageView) convertView.findViewById(R.id.content_iv);
				viewHolder.selectTV = (ImageView) convertView.findViewById(R.id.select_iv);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			ReadStyleItem item = getItem(position);
			
			viewHolder.contentIV.setImageResource(item.resId);
			if(item.isSelected){
				oldSelect = position;
				viewHolder.selectTV.setVisibility(View.VISIBLE);
			}else{
				viewHolder.selectTV.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}
		
		private View newView(){
			return inflater.inflate(R.layout.reader_style_item, null);
		}
		
		public void setSeleted(int position){
			if(position != oldSelect){
				if(oldSelect != -1){
					getItem(oldSelect).isSelected = false;
				}
				getItem(position).isSelected = true;
				oldSelect = position;
				notifyDataSetChanged();
			}
		}
		
		private class ViewHolder {
			
			public ImageView contentIV;
			public ImageView selectTV;
			
		}
	}
}
