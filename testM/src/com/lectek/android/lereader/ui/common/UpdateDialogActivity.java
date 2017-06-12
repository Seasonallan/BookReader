package com.lectek.android.lereader.ui.common;

import java.io.Serializable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.app.BaseContextActivity;
import com.lectek.android.lereader.lib.utils.ClientInfoUtil;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.update.ClientInfo;

public class UpdateDialogActivity extends BaseContextActivity {
	public static final String EXTRA_ACTION_TYPE = "ACTION_TYPE";
	public static final String EXTRA_UPDATE_INFO = "EXTRA_UPDATE_INFO";
	public static final String EXTRA_UPDATE_DOWNLAOD_CURRENT_BYTES = "EXTRA_UPDATE_DOWNLAOD_CURRENT_BYTES";
	public static final String EXTRA_UPDATE_DOWNLAOD_TOTAL_BYTES = "EXTRA_UPDATE_DOWNLAOD_TOTAL_BYTES";
	public static final String EXTRA_HAS_REQUESTCODE = "EXTRA_HAS_REQUESTCODE";
	
	public static final int VALUE_ACTION_TYPE_UPDATE_INFO = 0;
	public static final int VALUE_ACTION_TYPE_UPDATE_DOWNLOAD_INFO = 1;
	public static final int VALUE_ACTION_TYPE_INSTALL_UPDATE = 2;
	public static final int VALUE_ACTION_TYPE_UPDATE_DOWNLOAD_FAIL = 3;
	
	public static final int RESULT_CODE_SUCCEED = 1;
	public static final int RESULT_CODE_CANCEL = 2;
	public static final int RESULT_CODE_EXIT = 3;
	
	public static final int HANDLER_MSG_DOWNLOAD_RUNNING = 1;
	public static final int HANDLER_MSG_DOWNLOAD_COMPLETE = 2;
	public static final int HANDLER_MSG_DOWNLOAD_FAILED = 3;
	
	private static Activity oldActivity;
	
	private Context this_;
	private ClientInfo mClientInfo;
	private int mActionType;
	
	private static UpdateSelfThread mUpdateSelfThread;
	
	private ProgressBar progressBar;
	private TextView progressTv;
	
	private boolean isCancel = true;
	private boolean hasRequestCode = false;
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			if (what == HANDLER_MSG_DOWNLOAD_RUNNING) {
				int currentBytes = msg.arg1;
				int totalBytes = msg.arg2;
				setProgressBar(currentBytes , totalBytes);
			} else if (what == HANDLER_MSG_DOWNLOAD_COMPLETE) {
				onShowInstallUpdate(false);
			} else if (what == HANDLER_MSG_DOWNLOAD_FAILED) {
				onShowDownloadFail();
			}
		}
	};
	
	public static boolean isStartUpdateSelfThread(){
		return mUpdateSelfThread != null && !mUpdateSelfThread.isStop();
	}
	
	public static void startInstallUpdate(Context context , ClientInfo clientInfo){
		Intent intent = new Intent(context, UpdateDialogActivity.class);
		intent.putExtra(EXTRA_ACTION_TYPE, VALUE_ACTION_TYPE_INSTALL_UPDATE);
		intent.putExtra(EXTRA_UPDATE_INFO, new UpdateInfo(clientInfo));
		if(!( context instanceof Activity )){
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(intent);
	}
	
	public static void startUpdateInfo(Context context , ClientInfo clientInfo){
		startUpdateInfo(context,clientInfo,null);
	}
	
	public static void startUpdateInfo(Context context , ClientInfo clientInfo,Integer requestCode){
		if(clientInfo == null || context == null){
			return;
		}
		Intent intent = new Intent(context, UpdateDialogActivity.class);
		intent.putExtra(EXTRA_ACTION_TYPE, VALUE_ACTION_TYPE_UPDATE_INFO);
		intent.putExtra(EXTRA_UPDATE_INFO, new UpdateInfo(clientInfo));
		if(!( context instanceof Activity )){
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}else{
			if(requestCode != null){
				intent.putExtra(EXTRA_HAS_REQUESTCODE, true);
				((Activity) context).startActivityForResult(intent, requestCode);
			}else{
				context.startActivity(intent);
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(oldActivity != null){
			oldActivity.finish();
		}
		oldActivity = this;
		Intent intent = getIntent();
		if(intent == null){
			finish();
			return;
		}
		this_ = this;
		mActionType = getIntent().getIntExtra(EXTRA_ACTION_TYPE, -1);
		UpdateInfo updateInfo = (UpdateInfo) getIntent().getSerializableExtra(EXTRA_UPDATE_INFO);
		if(updateInfo == null || updateInfo.getClientInfo() == null){
			finish();
			return;
		}
		hasRequestCode = getIntent().getBooleanExtra(EXTRA_HAS_REQUESTCODE,false);
		mClientInfo = updateInfo.getClientInfo();
//		mClientInfo.mustUpdate = true;//TODO 测试数据
		UpdateSelfThread.cancelNotification(this);
		
		if(UpdateSelfThread.isDownloadUpdateApk(mClientInfo)){
			onShowInstallUpdate(true);
			return;
		}
		if(mActionType == VALUE_ACTION_TYPE_UPDATE_INFO){
			onShowUpdateInfo();
		}
		else if(mActionType == VALUE_ACTION_TYPE_UPDATE_DOWNLOAD_INFO){
			long currentBytes = intent.getLongExtra(EXTRA_UPDATE_DOWNLAOD_CURRENT_BYTES, 0);
			long totalBytes = intent.getLongExtra(EXTRA_UPDATE_DOWNLAOD_TOTAL_BYTES,0);
			onShowUpdateDownlaodInfo(currentBytes , totalBytes);
		}
		else if(mActionType == VALUE_ACTION_TYPE_INSTALL_UPDATE){
			onShowInstallUpdate(false);
		}else if(mActionType == VALUE_ACTION_TYPE_UPDATE_DOWNLOAD_FAIL){
			onShowDownloadFail();
		}else{
			finish();
			return;
		}
	}
	
	private String getVersion(String source){
		try{
			if(!TextUtils.isEmpty(source)){
				String [] data = source.split("_");
				if(data != null && data.length >= 3){
					String result = "";
					for(int i = data.length - 3;i < data.length;i++){
						result += data[i];
						Integer.valueOf(data[i]);
						if(i != data.length - 1){
							result += ".";
						}
					}
					return result;
				}
			}
		}catch (Exception e) {}
		return source;
	}
	
	protected void onShowUpdateInfo(){
		
		final PreferencesUtil preferencesUtil = PreferencesUtil.getInstance(this_);
		
		isCancel = true;
		setContentView(R.layout.dialog_update);
		((TextView) findViewById(R.id.current_version)).setText(ClientInfoUtil.CLIENT_VERSION);
		TextView sizeTV = ((TextView) findViewById(R.id.update_size));
		if (mClientInfo.updateSize > 0) {
			sizeTV.setText(this_.getString(R.string.check_client_update_size, Formatter.formatFileSize(this_, mClientInfo.updateSize)));
		} else {
			sizeTV.setVisibility(View.GONE);
		}
		((TextView) findViewById(R.id.update_version))
			.setText(getString(R.string.check_client_update_version_tip, getVersion(mClientInfo.updateVersion)));
		
		TextView updateMessageTV = ((TextView) findViewById(R.id.update_message));
		updateMessageTV.setText(mClientInfo.getUpdateMessage());
		updateMessageTV.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		final CheckBox cBox = (CheckBox)findViewById(R.id.no_ask_cb);
		
		Button okBtn = (Button) findViewById(R.id.dialog_update_now);
		Button cancelBtn = (Button) findViewById(R.id.dialog_update_later);
		
		if(!mClientInfo.mustUpdate){
			okBtn.setText(R.string.update_text);
			cancelBtn.setText(R.string.skip_text);
			cBox.setVisibility(View.VISIBLE);
			cBox.setChecked(true);
		}
		
		// 现在更新
		okBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mUpdateSelfThread = new UpdateSelfThread(mClientInfo);
				mUpdateSelfThread.start();
				isCancel = false;
				if(!mClientInfo.mustUpdate){
					if(cBox.isChecked()){
						String updateVersion = mClientInfo.updateVersion;
						preferencesUtil.setShowUpdateAgain(updateVersion, true);
					}else{
						preferencesUtil.setShowUpdateAgain("", false);
					}
				}
				if (mClientInfo.mustUpdate) {
					onShowUpdateDownlaodInfo(0,0);
				} else {
					finish();
				}
			}
		});
		// 稍后更新
		if (mClientInfo.mustUpdate) {
			cancelBtn.setVisibility(View.GONE);
		} else {
			cancelBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if(cBox.isChecked()){
						String updateVersion = mClientInfo.updateVersion;
						preferencesUtil.setShowUpdateAgain(updateVersion, true);
					}else{
						preferencesUtil.setShowUpdateAgain("", false);
					}
					finish();
				}

			});
		}
	}
	
	@Override
	public void finish() {
		if(isCancel){
			setResult(RESULT_CODE_CANCEL);
		}else{
			setResult(RESULT_CODE_SUCCEED);
		}
		if(mClientInfo != null && mClientInfo.mustUpdate && isCancel){
			setResult(RESULT_CODE_EXIT);
		}
		if(mUpdateSelfThread != null){
			if(isCancel){
				mUpdateSelfThread.stopDownlaod();
			}
			mUpdateSelfThread.setHandler(null);
			if(mUpdateSelfThread.isStop()){
				mUpdateSelfThread.cancelNotification();
				mUpdateSelfThread = null;
			}else{
				mUpdateSelfThread.recoverNotification();
			}
		}
		oldActivity = null;
		super.finish();
	}

	@Override
	protected void onDestroy() {
		if(mClientInfo != null && mClientInfo.mustUpdate && isCancel && !hasRequestCode){
			sendBroadcast(new Intent(AppBroadcast.ACTION_CLOSE_APP));
		}
		super.onDestroy();
	}

	private void setProgressBar(int currentBytes,int totalBytes){
		if(currentBytes < 0 || totalBytes < 0){
			return;
		}
		int progressAmount = 0;
		if(totalBytes > 0){
			progressAmount = (currentBytes * 100 / totalBytes);
			if (progressBar != null) {
				progressBar.setProgress(progressAmount);
			}
			progressBar.setVisibility(View.VISIBLE);
		}else{
			progressBar.setVisibility(View.GONE);
		}
		if (progressTv != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(Formatter.formatFileSize(this_, currentBytes));
			if(totalBytes > 0){
				sb.append("/");
				sb.append(Formatter.formatFileSize(this_, totalBytes));
			}
			progressTv.setText(sb);
		}
	}
	
	protected void onShowUpdateDownlaodInfo(long currentBytes,long totalBytes){
		if(mUpdateSelfThread == null){
			finish();
			return;
		}
		mUpdateSelfThread.setHandler(mHandler);
		setContentView(R.layout.update_waitting);
		progressBar = (ProgressBar) findViewById(R.id.update_pb);
		progressTv = (TextView) findViewById(R.id.update_tv);
		
		setProgressBar((int)currentBytes , (int)totalBytes);
		
		TextView updateContentTV = (TextView) findViewById(R.id.update_content_tv);
		updateContentTV.setText(mClientInfo.getUpdateMessage());
		updateContentTV.setMovementMethod(ScrollingMovementMethod.getInstance());
		if(mClientInfo.mustUpdate){
			findViewById(R.id.update_cancel_btn).setVisibility(View.GONE);
			findViewById(R.id.update_back_btn).setVisibility(View.GONE);
			isCancel = true;
		}else{
			findViewById(R.id.update_cancel_btn).setVisibility(View.VISIBLE);
			findViewById(R.id.update_back_btn).setVisibility(View.VISIBLE);
			isCancel = false;
		}
		findViewById(R.id.update_cancel_btn).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mUpdateSelfThread.stopDownlaod();
					isCancel = true;
					finish();
				}
		});
		findViewById(R.id.update_back_btn).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
		});
	}
	
	protected void onShowDownloadFail(){
		isCancel = true;
		setContentView(R.layout.update_waitting);
		findViewById(R.id.update_pb).setVisibility(View.GONE);
		findViewById(R.id.update_tv).setVisibility(View.GONE);
		((TextView) findViewById(R.id.update_client_tip)).setText(R.string.update_download_fail_tip);
		TextView updateContentTV = (TextView) findViewById(R.id.update_content_tv);
		updateContentTV.setText(mClientInfo.getUpdateMessage());
		updateContentTV.setMovementMethod(ScrollingMovementMethod.getInstance());
		Button okBtn = (Button) findViewById(R.id.update_cancel_btn);
		okBtn.setText(R.string.update_download_now_redownload);
		okBtn.setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mUpdateSelfThread = new UpdateSelfThread(mClientInfo);
					mUpdateSelfThread.start();
					isCancel = false;
					if (mClientInfo.mustUpdate) {
						onShowUpdateDownlaodInfo(0,0);
					} else {
						finish();
					}
				}
		});
		Button cancelBtn = (Button) findViewById(R.id.update_back_btn);
		if(mClientInfo.mustUpdate){
			cancelBtn.setVisibility(View.GONE);
		}else{
			cancelBtn.setVisibility(View.VISIBLE);
		}
		cancelBtn.setText(R.string.update_download_back_redownload);
		cancelBtn.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
			});
	}
	
	protected void onShowInstallUpdate(boolean isOldApk){
		if(!UpdateSelfThread.isDownloadUpdateApk(mClientInfo)){
			finish();
			return;
		}
		isCancel = true;
		setContentView(R.layout.update_waitting);
		final PreferencesUtil preferencesUtil = PreferencesUtil.getInstance(this_);
		findViewById(R.id.update_pb).setVisibility(View.GONE);
		findViewById(R.id.update_tv).setVisibility(View.GONE);
		final CheckBox cBox = (CheckBox)findViewById(R.id.no_ask_cb);
		((TextView) findViewById(R.id.update_client_tip)).setText(R.string.update_download_succeed_tip);
		TextView updateContentTV = (TextView) findViewById(R.id.update_content_tv);
		updateContentTV.setText(mClientInfo.getUpdateMessage());
		updateContentTV.setMovementMethod(ScrollingMovementMethod.getInstance());
		Button okBtn = (Button) findViewById(R.id.update_cancel_btn);
		okBtn.setText(R.string.update_download_now_install);
		okBtn.setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(!mClientInfo.mustUpdate){
						if(cBox.isChecked()){
							String updateVersion = mClientInfo.updateVersion;
							preferencesUtil.setShowUpdateAgain(updateVersion, true);
						}else{
							preferencesUtil.setShowUpdateAgain("", false);
						}
					}
					UpdateSelfThread.installUpdate(mClientInfo);
//					isCancel = false;
//					finish();
				}
		});
		Button cancelBtn = (Button) findViewById(R.id.update_back_btn);
		if(mClientInfo.mustUpdate){
			cancelBtn.setVisibility(View.GONE);
		}else{
			cBox.setVisibility(View.VISIBLE);
			cBox.setChecked(true);
			cancelBtn.setVisibility(View.VISIBLE);
		}
		cancelBtn.setText(R.string.update_download_back_install);
		cancelBtn.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(cBox.isChecked()){
							String updateVersion = mClientInfo.updateVersion;
							preferencesUtil.setShowUpdateAgain(updateVersion, true);
						}else{
							preferencesUtil.setShowUpdateAgain("", false);
						}
						finish();
					}
			});
	}
	
	public static class UpdateInfo implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -2720858918286368453L;
		private boolean mustUpdate;
		private boolean silenceUpdate;
		private String updateMessage;
		private int updateSize;
		private String updateURL;
		private String updateVersion;
		public UpdateInfo(ClientInfo clientInfo){
			mustUpdate = clientInfo.mustUpdate;
			silenceUpdate = clientInfo.silenceUpdate;
			updateMessage = clientInfo.updateMessage;
			updateSize = clientInfo.updateSize;
			updateURL = clientInfo.updateURL;
			updateVersion = clientInfo.updateVersion;
		}
		public ClientInfo getClientInfo(){
			ClientInfo clientInfo = new ClientInfo();
			clientInfo.mustUpdate = mustUpdate;
			clientInfo.silenceUpdate = silenceUpdate;
			clientInfo.updateMessage = updateMessage;
			clientInfo.updateSize = updateSize;
			clientInfo.updateURL = updateURL;
			clientInfo.updateVersion = updateVersion;
			return clientInfo;
		}
	}
}
