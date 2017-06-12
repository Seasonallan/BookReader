package com.lectek.android.update;

import com.lectek.android.lereader.net.response.UpdateInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
/**
 * 软件升级管理类
 * 使用升级模块的几个必要条件
 * 1.必须在Application的onCreate()执行init(Context context,UpdateSetting updateSetting)方法;
 * 2.UpdateSetting的任何属性都是不能为空的;
 * 3.必须在显示升级界面的activity的onCreate()方法调用dispatchActivityCreate(IUpdateActivity updateActivity);
 * 4.必须在显示升级界面的activity的finish()方法调用dispatchActivityFinish(Activity activity);
 * 5.外部在获取完自己的更新信息之后，必须转换成UpdateInfo对象并通过
 * startUpdateInfo(Context context , UpdateInfo updateInfo,Integer requestCode)方法启动升级界面，具体见该方法。
 * 
 * @author lyw
 */
public class UpdateManager {
	/**
	 * 返回更新成功
	 */
	public static final int RESULT_CODE_SUCCEED = 1;
	/**
	 * 返回取消更新
	 */
	public static final int RESULT_CODE_CANCEL = 2;
	/**
	 * 返回取消强制更新，通知退出应用
	 */
	public static final int RESULT_CODE_EXIT = 3;

	static final String EXTRA_ACTION_TYPE = "ACTION_TYPE";
	static final String EXTRA_HAS_REQUEST_CODE = "EXTRA_HAS_REQUEST_CODE";
	static final String EXTRA_UPDATE_INFO = "EXTRA_UPDATE_INFO";
	static final String EXTRA_UPDATE_DOWNLAOD_CURRENT_BYTES = "EXTRA_UPDATE_DOWNLAOD_CURRENT_BYTES";
	static final String EXTRA_UPDATE_DOWNLAOD_TOTAL_BYTES = "EXTRA_UPDATE_DOWNLAOD_TOTAL_BYTES";
	
	static final int VALUE_ACTION_TYPE_UPDATE_INFO = 0;
	static final int VALUE_ACTION_TYPE_UPDATE_DOWNLOAD_INFO = 1;
	static final int VALUE_ACTION_TYPE_INSTALL_UPDATE = 2;
	static final int VALUE_ACTION_TYPE_UPDATE_DOWNLOAD_FAIL = 3;
	
	private static UpdateManager mInstance;
	private static UpdateSetting mUpdateSetting;
	private static boolean isInit;
	
	private Context mContext;
	private ActivityParams mActivityParams;
	private Handler mHandler;
	private UpdateSelfThread mUpdateSelfThread;
	
	private static UpdateInfo updateInfo;
	
	private UpdateManager(Context context){
		mContext = context.getApplicationContext();
		mHandler = new Handler(Looper.getMainLooper()){
			@Override
			public void handleMessage(Message msg) {
				int what = msg.what;
				if (what == UpdateSelfThread.HANDLER_MSG_DOWNLOAD_RUNNING) {
					int currentBytes = msg.arg1;
					int totalBytes = msg.arg2;
					setProgressBar(currentBytes , totalBytes);
				} else if (what == UpdateSelfThread.HANDLER_MSG_DOWNLOAD_COMPLETE) {
					onShowInstallUpdate(false);
				} else if (what == UpdateSelfThread.HANDLER_MSG_DOWNLOAD_FAILED) {
					onShowDownloadFail();
				} else if (what == UpdateSelfThread.HANDLER_MSG_THREAD_STOP) {
					mUpdateSelfThread = null;
				}
			}
		};
	}

	public static UpdateSetting getUpdateSetting(){
		return mUpdateSetting;
	} 
	
	public synchronized static void init(Context context,UpdateSetting updateSetting){
		if(isInit){
			return;
		}
		isInit = true;
		mInstance = new UpdateManager(context);
		mUpdateSetting = updateSetting;
		if(mUpdateSetting.mApkSavePath == null 
				|| mUpdateSetting.mHttpFactory == null
				|| mUpdateSetting.mNotification == null
				|| mUpdateSetting.mUpdateActivityCls == null){
			throw new RuntimeException("参数不能为空");
		}
	}
	
	public synchronized static UpdateManager getInstance(){
		if(!isInit){
			throw new RuntimeException("必须在Application的onCreate()执行init()方法");
		}
		return mInstance;
	}
	
	public static void setUpdateInfo(UpdateInfo info) {
		updateInfo = info;
	}
	
	public static UpdateInfo getUpdateInfo() {
		return updateInfo;
	}
	
	public boolean isStartUpdateSelfThread(){
		return mUpdateSelfThread != null && !mUpdateSelfThread.isStop();
	}
	/**
	 * 启动升级界面
	 * @param updateInfo
	 * 重载至#startUpdateInfo(Context, UpdateInfo, Integer)
	 * @see {@link #startUpdateInfo(Context, com.lectek.android.lereader.net.response.UpdateInfo, Integer)}
	 */
	public void startUpdateInfo(UpdateInfo updateInfo){
		startUpdateInfo(mContext,updateInfo,null);
	}
	/**
	 * 启动升级界面
	 * @param context
	 * @param updateInfo
	 * 重载至#startUpdateInfo(Context, UpdateInfo, Integer)
	 * @see {@link #startUpdateInfo(Context, UpdateInfo, Integer)}
	 */
	public void startUpdateInfo(Context context , UpdateInfo updateInfo){
		startUpdateInfo(context,updateInfo,null);
	}
	/**
	 * 启动升级界面
	 * <p>如果已经下载完成会显示安装提示界面</p>
	 * <p>如果正在下载会显示下载界面</p>
	 * <p>如果未开始下载显示更新提示界面</p>
	 * @param context
	 * @param updateInfo 更新信息
	 * @param requestCode 如果没有设置requestCode,
	 * 取消强制升级的时候会调用UpdateSetting.INotification.notifyExitApp方法通知退出应用
	 */
	public void startUpdateInfo(Context context , UpdateInfo updateInfo,Integer requestCode){
		if(updateInfo == null || context == null){
			return;
		}
		Intent intent = new Intent(context,mUpdateSetting.mUpdateActivityCls);
		intent.putExtra(EXTRA_ACTION_TYPE, VALUE_ACTION_TYPE_UPDATE_INFO);
		intent.putExtra(EXTRA_UPDATE_INFO, updateInfo);
		if(!( context instanceof Activity )){
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}else{
			if(requestCode != null){
				intent.putExtra(EXTRA_HAS_REQUEST_CODE, true);
				((Activity) context).startActivityForResult(intent, requestCode);
			}else{
				context.startActivity(intent);
			}
		}
	}
	
	/**
	 * 启动下载界面
	 * @param context
	 * @param updateInfo 暂时使用该实体 设置应用名，大小
	 * @param requestCode 如果没有设置requestCode,
	 * 取消强制升级的时候会调用UpdateSetting.INotification.notifyExitApp方法通知退出应用
	 */
	public void startDownloadInfo(Context context , UpdateInfo updateInfo,Integer requestCode){
		if(updateInfo == null || context == null){
			return;
		}
		Intent intent = new Intent(context,mUpdateSetting.mUpdateActivityCls);
		intent.putExtra(EXTRA_ACTION_TYPE, VALUE_ACTION_TYPE_UPDATE_INFO);
		intent.putExtra(EXTRA_UPDATE_INFO, updateInfo);
		intent.putExtra(UpdateActivity.NOT_UPDATE_DOWNLOAD, true);
		if(!( context instanceof Activity )){
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}else{
			if(requestCode != null){
				intent.putExtra(EXTRA_HAS_REQUEST_CODE, true);
				((Activity) context).startActivityForResult(intent, requestCode);
			}else{
				context.startActivity(intent);
			}
		}
	}
	
	void startInstallUpdate(UpdateInfo updateInfo){
		Intent intent = new Intent(mContext, mUpdateSetting.mUpdateActivityCls);
		intent.putExtra(EXTRA_ACTION_TYPE, VALUE_ACTION_TYPE_INSTALL_UPDATE);
		intent.putExtra(EXTRA_UPDATE_INFO, updateInfo);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}

	private void startUpdateSelfThread(Context context, UpdateInfo updateInfo){
		if(mUpdateSelfThread != null && !mUpdateSelfThread.isStop()){
			return;
		}
		mUpdateSelfThread = new UpdateSelfThread(mContext,mActivityParams.mUpdateInfo,mHandler);
		mUpdateSelfThread.start();
	}
	
	/////////////////////////////////////前端Activity控制  开始////////////////////////////////////////////////////
	/**
	 * 派遣Activity onCreate事件
	 * @param updateActivity
	 */
	public void dispatchActivityCreate(IUpdateActivity updateActivity){
		Activity mActivity = updateActivity.getActivity();
		if(mActivityParams != null){
			mActivity.finish();
			return;
		}
		Intent intent = mActivity.getIntent();
		if(intent == null){
			mActivity.finish();
			return;
		}
		int actionType = intent.getIntExtra(EXTRA_ACTION_TYPE, -1);
		UpdateInfo updateInfo = (UpdateInfo) intent.getSerializableExtra(EXTRA_UPDATE_INFO);
		if(updateInfo == null){
			mActivity.finish();
			return;
		}
		mActivityParams = new ActivityParams();
		mActivityParams.mUpdateActivity = updateActivity;
		mActivityParams.mUpdateInfo = updateInfo;
		mActivityParams.hasRequestCode = intent.getBooleanExtra(EXTRA_HAS_REQUEST_CODE, false);
		UpdateSelfThread.cancelNotification(mContext);
		if(UpdateUtil.isDownloadUpdateApk(mContext,updateInfo)){
			onShowInstallUpdate(true);
			return;
		}
		if(isStartUpdateSelfThread()){
			onShowUpdateDownlaodInfo(mUpdateSelfThread.getCurrentBytes() , mUpdateSelfThread.getTotalBytes());
			return;
		}
		if(actionType == VALUE_ACTION_TYPE_UPDATE_INFO){
			onShowUpdateInfo();
		}
		else if(actionType == VALUE_ACTION_TYPE_UPDATE_DOWNLOAD_INFO){
			long currentBytes = intent.getLongExtra(EXTRA_UPDATE_DOWNLAOD_CURRENT_BYTES, 0);
			long totalBytes = intent.getLongExtra(EXTRA_UPDATE_DOWNLAOD_TOTAL_BYTES,0);
			onShowUpdateDownlaodInfo(currentBytes , totalBytes);
		}
		else if(actionType == VALUE_ACTION_TYPE_INSTALL_UPDATE){
			onShowInstallUpdate(false);
		}else if(actionType == VALUE_ACTION_TYPE_UPDATE_DOWNLOAD_FAIL){
			onShowDownloadFail();
		}else{
			mActivity.finish();
			return;
		}
	}
	
	/**
	 * 派遣Activity onFinish事件
	 */
	public void dispatchActivityFinish(Activity activity){
		if(mActivityParams == null){
			return;
		}
		Activity mActivity = mActivityParams.mUpdateActivity.getActivity();
		if(!activity.equals(mActivity)){
			return;
		}
		if(mActivityParams.isCancel){
			mActivity.setResult(RESULT_CODE_CANCEL);
		}else{
			mActivity.setResult(RESULT_CODE_SUCCEED);
		}
		UpdateInfo updateInfo = mActivityParams.mUpdateInfo;
		if(updateInfo != null && updateInfo.isMustUpdate() && mActivityParams.isCancel){
			if(mActivityParams.hasRequestCode){
				mActivity.setResult(RESULT_CODE_EXIT);
			}else{
				mUpdateSetting.mNotification.notifyExitApp(mContext);
			}
		}
		if(mUpdateSelfThread != null){
			if(mActivityParams.isCancel){
				mUpdateSelfThread.stopDownlaod();
			}
			mUpdateSelfThread.setNotifyView(false);
			if(mUpdateSelfThread.isStop()){
				mUpdateSelfThread.cancelNotification();
				mUpdateSelfThread = null;
			}else{
				mUpdateSelfThread.recoverNotification();
			}
		}
		mActivityParams = null;
	}
	
	/**
	 * 显示下载失败界面
	 */
	private void onShowDownloadFail() {
		if(mActivityParams == null){
			return;
		}
		mActivityParams.isCancel = true;
		OnClickListener retryListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mActivityParams != null){
					startUpdateSelfThread(mContext,mActivityParams.mUpdateInfo);
					mActivityParams.isCancel = false;
					if (mActivityParams.mUpdateInfo.isMustUpdate()) {
						onShowUpdateDownlaodInfo(0,0);
					} else {
						mActivityParams.mUpdateActivity.getActivity().finish();
					}
				}
			}
		};
		OnClickListener cancelListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mActivityParams != null){
					mActivityParams.mUpdateActivity.getActivity().finish();
				}
			}
		};
		mActivityParams.mUpdateActivity.onShowDownloadFail(mActivityParams.mUpdateInfo,retryListener, cancelListener);
	}
	/**
	 * 显示现在进度界面
	 * @param currentBytes
	 * @param totalBytes
	 */
	private void onShowUpdateDownlaodInfo(long currentBytes, long totalBytes) {
		if(mActivityParams == null){
			return;
		}
		if(mUpdateSelfThread == null){
			mActivityParams.mUpdateActivity.getActivity().finish();
			return;
		}
		mUpdateSelfThread.setNotifyView(true);
		mActivityParams.mUpdateActivity.onUpdateProgressBar(mActivityParams.mUpdateInfo,currentBytes , totalBytes);
		if(mActivityParams.mUpdateInfo.isMustUpdate()){
			mActivityParams.isCancel = true;
		}else{
			mActivityParams.isCancel = false;
		}

		OnClickListener backgrounderListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mActivityParams != null){
					mActivityParams.mUpdateActivity.getActivity().finish();
				}
			}
		};

		OnClickListener cancelListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mUpdateSelfThread != null){
					mUpdateSelfThread.stopDownlaod();
				}
				if(mActivityParams != null){
					mActivityParams.isCancel = true;
					mActivityParams.mUpdateActivity.getActivity().finish();
				}
			}
		};
		mActivityParams.mUpdateActivity.onShowUpdateDownlaodInfo(mActivityParams.mUpdateInfo
				, backgrounderListener, cancelListener, currentBytes, totalBytes);
	}
	
	private void setProgressBar(int currentBytes,int totalBytes){
		if(mActivityParams == null){
			return;
		}
		mActivityParams.mUpdateActivity.onUpdateProgressBar(mActivityParams.mUpdateInfo, currentBytes, totalBytes);
	}
	
	/**
	 * 显示升级信息
	 */
	private void onShowUpdateInfo() {
		if(mActivityParams == null){
			return;
		}
		mActivityParams.isCancel = true;
		// 现在更新
		OnClickListener confirmListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mActivityParams != null){
					startUpdateSelfThread(mContext,mActivityParams.mUpdateInfo);
					mActivityParams.isCancel = false;
					if (mActivityParams.mUpdateInfo.isMustUpdate()) {
						onShowUpdateDownlaodInfo(0,0);
					} else {
						mActivityParams.mUpdateActivity.getActivity().finish();
					}
				}
			}
		};
		// 稍后更新
		OnClickListener cancelListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mActivityParams != null){
					mActivityParams.mUpdateActivity.getActivity().finish();
				}
			}
		};
		mActivityParams.mUpdateActivity.onShowUpdateInfo(mActivityParams.mUpdateInfo, confirmListener, cancelListener);
	}
	/**
	 * 显示安装新版本确认提示
	 * @param isOldApk 是否是之前已经下载完成的APK
	 */
	private void onShowInstallUpdate(boolean isOldApk) {
		if(mActivityParams == null){
			return;
		}
		if(!UpdateUtil.isDownloadUpdateApk(mContext,mActivityParams.mUpdateInfo)){
			mActivityParams.mUpdateActivity.getActivity().finish();
			return;
		}
		mActivityParams.isCancel = true;
		OnClickListener confirmListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mActivityParams != null){
					UpdateUtil.installUpdate(mContext,mActivityParams.mUpdateInfo);
				}
			}
		};

		OnClickListener cancelListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mActivityParams != null){
					mActivityParams.mUpdateActivity.getActivity().finish();
				}
			}
		};
		mActivityParams.mUpdateActivity.onShowInstallUpdate(mActivityParams.mUpdateInfo, confirmListener, cancelListener, isOldApk);
	}
	/////////////////////////////////////前端Activity控制  结束////////////////////////////////////////////////////
	
	private class ActivityParams{
		private IUpdateActivity mUpdateActivity;
		private UpdateInfo mUpdateInfo;
		private boolean isCancel = true;
		private boolean hasRequestCode = false;
	}
}
