package com.lectek.android.lereader.ui.basereader_leyue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.common.BaseActivity;

public class ReaderSettingActivity extends BaseActivity {
	private RadioGroup mAnimationRG;
	private RadioGroup mRestReminderRG;
	private RadioGroup mScreensaverRG;
	private ToggleButton mBrightnessTB;
	private PreferencesUtil mPreferencesUtil;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleContent(getString(R.string.reader_menu_item_setting_tip));
		mPreferencesUtil = PreferencesUtil.getInstance(this_);
		mAnimationRG = (RadioGroup) findViewById(R.id.menu_settings_anim_rg);
		mRestReminderRG = (RadioGroup) findViewById(R.id.menu_settings_reminder_rg);
		mScreensaverRG = (RadioGroup) findViewById(R.id.menu_settings_screensaver_rg);
		mBrightnessTB = (ToggleButton) findViewById(R.id.brightness_auto_but);
		mRestReminderRG.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int reminder = 0;
				switch (checkedId) {
				case R.id.menu_settings_reminder_but_1:
					reminder = 0;
					break;
				case R.id.menu_settings_reminder_but_2:
					reminder = 1;
					break;
				case R.id.menu_settings_reminder_but_3:
					reminder = 2;
					break;
				}
				mPreferencesUtil.setRestReminder(reminder);
			}
		});
		
		mScreensaverRG.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int screensaver = 0;
				switch (checkedId) {
				case R.id.menu_settings_screensaver_but_1:
					screensaver = 0;
					break;
				case R.id.menu_settings_screensaver_but_2:
					screensaver = 1;
					break;
				case R.id.menu_settings_screensaver_but_3:
					screensaver = 2;
					break;
				case R.id.menu_settings_screensaver_but_4:
					screensaver = 3;
					break;
				}
				mPreferencesUtil.setScreensaver(screensaver);
				
			}
		});
		mBrightnessTB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mPreferencesUtil.setVolumnTurnPage(isChecked);
			}
		});
		initView();
	}
	
	private void initView() {
		mAnimationRG.check(R.id.menu_settings_anim_but_1);
		mBrightnessTB.setChecked(mPreferencesUtil.isVolumnTurnPage());
		
		int restReminderType = mPreferencesUtil.getRestReminder();
		if(restReminderType == 0){
			mRestReminderRG.check(R.id.menu_settings_reminder_but_1);
		}else if(restReminderType == 1){
			mRestReminderRG.check(R.id.menu_settings_reminder_but_2);
		}else if(restReminderType == 2){
			mRestReminderRG.check(R.id.menu_settings_reminder_but_3);
		}
		
		int screensaverType = mPreferencesUtil.getScreensaver();
		if(screensaverType == 0){
			mScreensaverRG.check(R.id.menu_settings_screensaver_but_1);
		}else if(screensaverType == 1){
			mScreensaverRG.check(R.id.menu_settings_screensaver_but_2);
		}else if(screensaverType == 2){
			mScreensaverRG.check(R.id.menu_settings_screensaver_but_3);
		}else if(screensaverType == 3){
			mScreensaverRG.check(R.id.menu_settings_screensaver_but_4);
		}
		LogUtil.e(restReminderType + "," + screensaverType);
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		return LayoutInflater.from(this_).inflate(R.layout.book_read_setting, null);
	}


}
