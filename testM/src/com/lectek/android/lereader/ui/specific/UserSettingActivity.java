package com.lectek.android.lereader.ui.specific;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.account.UserSettingViewModel;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.common.BaseActivity;

public class UserSettingActivity extends BaseActivity{
	
	private View mContentView;
	
	private SeekBar screenLightSeekBar;
	
	private UserSettingViewModel viewModel;

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		viewModel = new UserSettingViewModel(this, this);
		mContentView = bindView(R.layout.account_setting_layout, viewModel);
		screenLightSeekBar = (SeekBar) mContentView.findViewById(R.id.sb_screen_light);
		int progress = PreferencesUtil.getInstance(this_).getScreenLightProgress();
		screenLightSeekBar.setProgress(progress);
		setScreenBrightess(progress);
		screenLightSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				PreferencesUtil.getInstance(getApplicationContext()).setScreenLightProgress(seekBar.getProgress());
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				setScreenBrightess(progress);
				
			}
		});
		return mContentView;
	}
	
	/** 设置屏幕亮度
     * @param value
     */
    private void setScreenBrightess(int value){
        final WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.screenBrightness = value * 1.0f / 100.0f;
        if(lp.screenBrightness < 0.17){
            lp.screenBrightness = 0.17f;
        }
        this.getWindow().setAttributes(lp);
    }
	
	

}
