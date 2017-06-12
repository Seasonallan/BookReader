package com.lectek.android.lereader.binding.model.user;

import gueei.binding.observables.StringObservable;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;

import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.binding.command.OnLongClickCommand;
import com.lectek.android.lereader.binding.model.BaseViewModel;
import com.lectek.android.lereader.lib.utils.PkgManagerUtil;
import com.lectek.android.lereader.net.response.UpdateInfo;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.IView;
import com.lectek.android.lereader.ui.specific.WelcomeActivity;
import com.lectek.android.lereader.utils.ToastUtil;
import com.lectek.android.update.UpdateManager;

/**
 * 
 * @author Shizq
 * @date 2013-9-16
 */
public class AboutUsViewModel extends BaseViewModel {
	
	private ShowAndHideLoadViewHandler mShowAndHideLoadViewHandler;
	
	public final StringObservable bVersionName = new StringObservable();
	
	public final OnClickCommand bUpdateClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			mShowAndHideLoadViewHandler.showLoad();
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					UpdateInfo updateInfo = UpdateManager.getUpdateInfo();
					if(updateInfo != null){
						updateHandle(updateInfo);
					}else{
						WelcomeActivity.checkClientUpdate(mActivity);
						UpdateInfo updateInfo2 = UpdateManager.getUpdateInfo();
						if(updateInfo2 != null){
							updateHandle(updateInfo2);
						}else {
							((Activity)getContext()).runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									ToastUtil.showToast(getContext(), "已是最新版本");
								}
							});
							
						}
					}
					
					((Activity)getContext()).runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							mShowAndHideLoadViewHandler.hideLoad();
						}
					});
					
				}
			}).start();
		}
	};
	
	private void updateHandle(final UpdateInfo updateInfo){
		PreferencesUtil preferencesUtil = PreferencesUtil.getInstance(mActivity);
		if(preferencesUtil.isShowUpdateAgain() || updateInfo.isMustUpdate()){
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					UpdateManager.getInstance().startUpdateInfo(mActivity, updateInfo, null);
				}
			});
		}
	}
	private Activity mActivity;

	private Object versionName;
	public AboutUsViewModel(Context context, IView loadView) {
		super(context, loadView);
		mActivity = (Activity) context;
		
		try {
			versionName = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		bVersionName.set("版本号:"+versionName);
	}
	
	public void setShowAndHideLoadViewHandler(ShowAndHideLoadViewHandler showAndHideLoadViewHandler){
		mShowAndHideLoadViewHandler = showAndHideLoadViewHandler;
	}
	
	public static interface ShowAndHideLoadViewHandler{
		public void showLoad();
		public void hideLoad();
	}
	
	public final OnLongClickCommand bGetUmengMetaData = new OnLongClickCommand() {

		@Override
		public boolean onLongClick(View v) {
			ToastUtil.showLongToast(mActivity, PkgManagerUtil.getUmengMetaDataValue(mActivity));
			return false;
		}
	};
}
