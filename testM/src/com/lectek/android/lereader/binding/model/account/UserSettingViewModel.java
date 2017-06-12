package com.lectek.android.lereader.binding.model.account;

import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.StringObservable;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.binding.model.user.PersonInfoNickNameViewModel;
import com.lectek.android.lereader.lib.utils.PkgManagerUtil;
import com.lectek.android.lereader.storage.LocalDataCacheManage;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.person.PersonInfoActivity;
import com.lectek.android.lereader.ui.specific.ActivityChannels;
import com.lectek.android.lereader.ui.specific.GuiderActivity;
import com.lectek.android.lereader.utils.ToastUtil;

/**
 * 用户设置ViewModel
 * 
 * @author yangwq
 * @date 2014年7月8日
 * @email 57890940@qq.com
 */
public class UserSettingViewModel extends BaseLoadNetDataViewModel implements INetAsyncTask{
	
	
	public final BooleanObservable bReceiveMessageChecked = new BooleanObservable();
	public final BooleanObservable bWifiDownloadChecked = new BooleanObservable();
	public final StringObservable bCacheSizeText = new StringObservable();
	private static final long KB_SIZE = 1024;
	private static final long MB_SIZE = 1024 * 1024;
	
	public final OnClickCommand bAboutClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			ActivityChannels.gotoAboutUsActivity(getContext());
		}
	};
	
	public final OnClickCommand bCommentLeyueClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			String pkgName = PkgManagerUtil.getApkInfo(getContext()).packageName;
			Uri uri = Uri.parse("market://details?id=" + pkgName);
			Intent intent = new Intent(Intent.ACTION_VIEW,uri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			((Activity)getContext()).startActivity(intent);
			
		}
	};
	
	public final OnClickCommand bVisitorLeadClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			((Activity)getContext()).startActivity(new Intent(getContext(), GuiderActivity.class));
			
		}
	};
	
	/**
	 * 个人中心帮助界面(在用户为登陆情况下出现，引导用户登陆领取积分)
	 */
	public final OnClickCommand bUserInfoHelperClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			
		}
	};
	
	public final OnClickCommand bWifiDownloadClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			if(bWifiDownloadChecked.get()){
				PreferencesUtil.getInstance(getContext()).setWifiDownloadBoolean(true);
			}else{
				PreferencesUtil.getInstance(getContext()).setWifiDownloadBoolean(false);
			}
			
		}
	};
	
	public final OnClickCommand bReceiveMessageClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			if(bReceiveMessageChecked.get()){
				PreferencesUtil.getInstance(getContext()).setReceiveSendMsgBoolean(true);
			}else{
				PreferencesUtil.getInstance(getContext()).setReceiveSendMsgBoolean(false);
			}
			
		}
	};
	
	public final OnClickCommand bClearCacheClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			String cacheSize = bCacheSizeText.get();
			LocalDataCacheManage.getInstance().deleteCache();
			updateCacheValue();
			ToastUtil.showToast(getContext(), getContext().getString(R.string.account_setting_cache_clear_toast, cacheSize));
		}
	};
	
	public final OnClickCommand bBindingAccount = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			boolean isLogin = PreferencesUtil.getInstance(getContext()).getIsLogin();
			if(isLogin){
				((Activity)getContext()).startActivityForResult(new Intent(getContext(), PersonInfoActivity.class), PersonInfoNickNameViewModel.REQUEST_CODE);
			}
			
		}
	};
	

	public UserSettingViewModel(Context context, INetLoadView loadView) {
		super(context, loadView);
		initData();
	}
	
	private void initData(){
		bReceiveMessageChecked.set(PreferencesUtil.getInstance(getContext()).getReceiveSendMsgBoolean());
		bWifiDownloadChecked.set(PreferencesUtil.getInstance(getContext()).getWifiDownloadBoolean());
		updateCacheValue();
	}
	
	private void updateCacheValue(){
		long fileSize = LocalDataCacheManage.getInstance().getCurrentCacheSize(getContext());
		String fileSizeTemple = getFileSizeTemple(fileSize);
		String cacheText = getContext().getString(R.string.account_setting_cache_size, fileSizeTemple);
		bCacheSizeText.set(cacheText);
	}
	
	public String getFileSizeTemple(long size){
		if(size > MB_SIZE){
			long mbSize = size / MB_SIZE;
			return mbSize + "MB";
		}else{
			long kbSize = size / KB_SIZE;
			return kbSize + "KB";
		}
	}
	
	

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean hasLoadedData() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNeedReStart() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStop() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}
	

}
