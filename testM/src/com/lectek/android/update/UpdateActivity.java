package com.lectek.android.update;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.app.AbsContextActivity;
import com.lectek.android.lereader.net.response.UpdateInfo;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;

public class UpdateActivity extends AbsContextActivity implements IUpdateActivity{
	/**非升级下载操作*/
	public static final String NOT_UPDATE_DOWNLOAD = "not_update_download";
	private ProgressBar progressBar;
	private TextView progressTv;
	private boolean notUpdateOpt = false;
	private CheckBox cBox;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		Bundle bundle = getIntent().getExtras();
		if (bundle!=null) {
			notUpdateOpt = bundle.getBoolean(NOT_UPDATE_DOWNLOAD, false);
		}
		UpdateManager.getInstance().dispatchActivityCreate(this);
	}

	@Override
	public void finish() {
		UpdateManager.getInstance().dispatchActivityFinish(this);
		super.finish();
	}

	@Override
	public void onShowUpdateInfo(final UpdateInfo updateInfo,
			final OnClickListener confirmListener, final OnClickListener cancelListener) {
		final PreferencesUtil preferencesUtil = PreferencesUtil.getInstance(this);
		Display display = getWindowManager().getDefaultDisplay();
		LinearLayout.LayoutParams lp = new LayoutParams(display.getWidth()*4/5, LayoutParams.WRAP_CONTENT);
		Button okBtn = null;
		Button cancelBtn = null;
		if (notUpdateOpt) {
			setContentView(LayoutInflater.from(this).inflate(R.layout.dialog_normal_download, null), lp);
			okBtn = (Button) findViewById(R.id.dialog_update_now);
			cancelBtn = (Button) findViewById(R.id.dialog_update_later);
			TextView sizeTV = ((TextView) findViewById(R.id.update_size));
			if (updateInfo.getUpdateSize() > 0) {
				sizeTV.setText(getString(R.string.check_client_update_size, Formatter.formatFileSize(this, updateInfo.getUpdateSize())));
			} else {
				sizeTV.setVisibility(View.GONE);
			}
			TextView updateMessageTV = ((TextView) findViewById(R.id.update_message));
			updateMessageTV.setText(updateInfo.getUpdateMessage());
			updateMessageTV.setMovementMethod(ScrollingMovementMethod.getInstance());
		}else {
			setContentView(LayoutInflater.from(this).inflate(R.layout.dialog_update, null), lp);
			((TextView) findViewById(R.id.current_version)).setText(updateInfo.getCurrentVersion());
			TextView sizeTV = ((TextView) findViewById(R.id.update_size));
			if (updateInfo.getUpdateSize() > 0) {
				sizeTV.setText(getString(R.string.check_client_update_size, Formatter.formatFileSize(this, updateInfo.getUpdateSize())));
			} else {
				sizeTV.setVisibility(View.GONE);
			}
			((TextView) findViewById(R.id.update_version))
			.setText(getString(R.string.check_client_update_version_tip, updateInfo.getUpdateVersion()));
			
			TextView updateMessageTV = ((TextView) findViewById(R.id.update_message));
			updateMessageTV.setText(updateInfo.getUpdateMessage());
			updateMessageTV.setMovementMethod(ScrollingMovementMethod.getInstance());
			
			cBox = (CheckBox)findViewById(R.id.no_ask_cb);
			okBtn = (Button) findViewById(R.id.dialog_update_now);
			cancelBtn = (Button) findViewById(R.id.dialog_update_later);
			if(!updateInfo.isMustUpdate()){
				okBtn.setText(R.string.update_text);
				cancelBtn.setText(R.string.skip_text);
				cBox.setVisibility(View.VISIBLE);
			}
		}
		// 现在更新
		okBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(confirmListener != null){
					confirmListener.onClick(v);
				}
				if (!notUpdateOpt) {
					if(cBox.isChecked()){
						preferencesUtil.setShowUpdateAgain(true);
					}else {
						preferencesUtil.setShowUpdateAgain(false);
					}
				}
			}
		});
		// 稍后更新
		if (updateInfo.isMustUpdate()) {
			cancelBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(cancelListener != null){
						cancelListener.onClick(v);
					}
				}
			});
		} else {
			cancelBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(cancelListener != null){
						cancelListener.onClick(v);
					}
					if (!notUpdateOpt) {
						if(cBox.isChecked()){
							preferencesUtil.setShowUpdateAgain(true);
						}else {
							preferencesUtil.setShowUpdateAgain(false);
						}
					}
				}
			});
		}
	}

	@Override
	public void onShowInstallUpdate(UpdateInfo updateInfo,
			OnClickListener confirmListener, OnClickListener cancelListener,
			boolean isOldApk) {
		if (notUpdateOpt) {
			setContentView(R.layout.dialog_download_waitting);
			findViewById(R.id.update_pb).setVisibility(View.GONE);
			findViewById(R.id.update_tv).setVisibility(View.GONE);
			((TextView) findViewById(R.id.update_client_tip)).setText(R.string.app_download_succeed_tip);
			TextView updateContentTV = (TextView) findViewById(R.id.update_content_tv);
			updateContentTV.setText(updateInfo.getUpdateMessage());
			updateContentTV.setMovementMethod(ScrollingMovementMethod.getInstance());
			Button okBtn = (Button) findViewById(R.id.update_cancel_btn);
			okBtn.setText(R.string.update_download_now_install);
			okBtn.setOnClickListener(confirmListener);
			Button cancelBtn = (Button) findViewById(R.id.update_back_btn);
			if(updateInfo.isMustUpdate()){
				cancelBtn.setVisibility(View.GONE);
			}else{
				cancelBtn.setVisibility(View.VISIBLE);
			}
			cancelBtn.setText(R.string.update_download_back_install);
			cancelBtn.setOnClickListener(cancelListener);
		}else {
			setContentView(R.layout.update_waitting);
			findViewById(R.id.update_pb).setVisibility(View.GONE);
			findViewById(R.id.update_tv).setVisibility(View.GONE);
			((TextView) findViewById(R.id.update_client_tip)).setText(R.string.update_download_succeed_tip);
			TextView updateContentTV = (TextView) findViewById(R.id.update_content_tv);
			updateContentTV.setText(updateInfo.getUpdateMessage());
			updateContentTV.setMovementMethod(ScrollingMovementMethod.getInstance());
			Button okBtn = (Button) findViewById(R.id.update_cancel_btn);
			okBtn.setText(R.string.update_download_now_install);
			okBtn.setOnClickListener(confirmListener);
			Button cancelBtn = (Button) findViewById(R.id.update_back_btn);
			if(updateInfo.isMustUpdate()){
				cancelBtn.setVisibility(View.GONE);
			}else{
				cancelBtn.setVisibility(View.VISIBLE);
			}
			cancelBtn.setText(R.string.update_download_back_install);
			cancelBtn.setOnClickListener(cancelListener);
		}
	
	}

	@Override
	public void onShowUpdateDownlaodInfo(UpdateInfo updateInfo,
			OnClickListener backgrounderListener,
			OnClickListener cancelListener, long currentBytes, long totalBytes) {
		if (notUpdateOpt) {
			setContentView(R.layout.dialog_download_waitting);
		}else {
			setContentView(R.layout.update_waitting);
		}
		progressBar = (ProgressBar) findViewById(R.id.update_pb);
		progressTv = (TextView) findViewById(R.id.update_tv);
		
		onUpdateProgressBar(updateInfo,(int)currentBytes , (int)totalBytes);
		
		TextView updateContentTV = (TextView) findViewById(R.id.update_content_tv);
		updateContentTV.setText(updateInfo.getUpdateMessage());
		updateContentTV.setMovementMethod(ScrollingMovementMethod.getInstance());
		if(updateInfo.isMustUpdate()){
			findViewById(R.id.update_cancel_btn).setVisibility(View.GONE);
			findViewById(R.id.update_back_btn).setVisibility(View.GONE);
		}else{
			findViewById(R.id.update_cancel_btn).setVisibility(View.VISIBLE);
			findViewById(R.id.update_back_btn).setVisibility(View.VISIBLE);
		}
		findViewById(R.id.update_cancel_btn).setOnClickListener(cancelListener);
		findViewById(R.id.update_back_btn).setOnClickListener(backgrounderListener);
	}

	@Override
	public void onUpdateProgressBar(UpdateInfo updateInfo, long currentBytes,
			long totalBytes) {
		if(currentBytes < 0 || totalBytes < 0 || progressBar == null || progressTv == null){
			return;
		}
		int progressAmount = 0;
		if(totalBytes > 0){
			progressAmount = (int) (currentBytes * 100 / totalBytes);
			if (progressBar != null) {
				progressBar.setProgress(progressAmount);
			}
			progressBar.setVisibility(View.VISIBLE);
		}else{
			progressBar.setVisibility(View.GONE);
		}
		if (progressTv != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(Formatter.formatFileSize(this, currentBytes));
			if(totalBytes > 0){
				sb.append("/");
				sb.append(Formatter.formatFileSize(this, totalBytes));
			}
			progressTv.setText(sb);
		}
		
	}

	@Override
	public void onShowDownloadFail(UpdateInfo updateInfo,
			OnClickListener retryListener, OnClickListener cancelListener) {
		if (notUpdateOpt) {
			setContentView(R.layout.dialog_download_waitting);
			((TextView) findViewById(R.id.update_client_tip)).setText(R.string.app_download_fail_tip);
		}else {
			setContentView(R.layout.update_waitting);
			((TextView) findViewById(R.id.update_client_tip)).setText(R.string.update_download_fail_tip);
		}
		findViewById(R.id.update_pb).setVisibility(View.GONE);
		findViewById(R.id.update_tv).setVisibility(View.GONE);
		TextView updateContentTV = (TextView) findViewById(R.id.update_content_tv);
		updateContentTV.setText(updateInfo.getUpdateMessage());
		updateContentTV.setMovementMethod(ScrollingMovementMethod.getInstance());
		Button okBtn = (Button) findViewById(R.id.update_cancel_btn);
		okBtn.setText(R.string.update_download_now_redownload);
		okBtn.setOnClickListener(retryListener);
		Button cancelBtn = (Button) findViewById(R.id.update_back_btn);
		if(updateInfo.isMustUpdate()){
			cancelBtn.setVisibility(View.GONE);
		}else{
			cancelBtn.setVisibility(View.VISIBLE);
		}
		cancelBtn.setText(R.string.update_download_back_redownload);
		cancelBtn.setOnClickListener(cancelListener);
	}

	@Override
	public Activity getActivity() {
		return this;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			return true;//TODO:强制升级时，直接退出应用。
		}
		return super.onKeyDown(keyCode, event);
	}
}
