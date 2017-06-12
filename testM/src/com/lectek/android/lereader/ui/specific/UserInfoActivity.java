package com.lectek.android.lereader.ui.specific;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.account.IaccountObserver;
import com.lectek.android.lereader.binding.model.user.PersonInfoNickNameViewModel;
import com.lectek.android.lereader.binding.model.user.UserInfoViewModelLeyue;
import com.lectek.android.lereader.binding.model.user.UserInfoViewModelLeyue.UserInfoUserAction;
import com.lectek.android.lereader.lib.share.entity.UmengShareInfo;
import com.lectek.android.lereader.lib.thread.internal.ITerminableThread;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.permanent.ShareConfig;
import com.lectek.android.lereader.permanent.ThirdPartLoginConfig;
import com.lectek.android.lereader.share.ShareWeiXin;
import com.lectek.android.lereader.share.ShareYiXin;
import com.lectek.android.lereader.share.entity.MutilMediaInfo;
import com.lectek.android.lereader.share.util.UmengShareUtils;
import com.lectek.android.lereader.share.util.UmengShareUtils.YXHanlder;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.ui.login_leyue.ThirdPartyLoginActivity;
import com.lectek.android.lereader.ui.person.PersonInfoActivity;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;

/**
 * @description
	 个人中心
 * @author chendt
 * @date 2013-12-29
 * @Version 1.0
 * @SEE UserInfoViewModelLeyue
 */
public class UserInfoActivity extends BaseActivity implements UserInfoUserAction,YXHanlder{
	
	private final String PAGE_NAME = "个人中心";
	
	private UserInfoViewModelLeyue mUserInfoViewModel;
	private PopupWindow popupWindow;
	private ITerminableThread mTerminableThread;
	private View view;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleBarEnabled(false);
		findViewById(R.id.header_lay_divider).setVisibility(View.GONE);
		UmengShareUtils.contentUrl = "http://www.leread.com/";
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ContentInfoActivityLeyue.ACTION_SHOW_TIP_DIALOG_AFTER_SHARE);
		intentFilter.addAction(ContentInfoActivityLeyue.ACTION_SHARE_OK_UPDATE_VIEW);
		registerReceiver(mTipDialogBroadcastReceiver, intentFilter);
		
		AccountManager.getInstance().registerObserver(mAccountObserver);
		
		LogUtil.e(this.getClass().getSimpleName(), this.getClass().getSimpleName() + " onCreate");
	}
	
	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mUserInfoViewModel = new UserInfoViewModelLeyue(this, this,this);
//		mUserInfoViewModel.onStart();
		view = bindView(R.layout.user_info_leyue_layout, mUserInfoViewModel);
		boolean isLogin = AccountManager.getInstance().getUserInfo() == null ? false : true;
		mUserInfoViewModel.changeLoginState(isLogin);
		return view;
	}

	private IaccountObserver mAccountObserver = new IaccountObserver() {
		
		@Override
		public void onLoginComplete(boolean success, String msg, Object... params) {
			if(success) {
				mUserInfoViewModel.changeLoginState(true);
			}
		}
		
		@Override
		public void onGetUserInfo(UserInfoLeyue userInfo) {
			hideLoadView();
			hideLoadDialog();
			mUserInfoViewModel.changeLoginState(true);
		}
		
		@Override
		public void onAccountChanged() {
//			mUserInfoViewModel.start();
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		if(requestCode == UserInfoViewModelLeyue.REQUESTCODE){
//			if(resultCode == UserLoginLeYueNewActivity.RESULT_CODE_SUCCESS){
//				mUserInfoViewModel.onStart();
//			}
//		}else
		if(requestCode == UserInfoViewModelLeyue.REQUESTCODE){
			if(resultCode == PersonInfoActivity.RESULT_CODE_MODIFIED){
				//修改个人资料或切换账号
				mUserInfoViewModel.changeLoginState(true);
			}else if(resultCode == PersonInfoActivity.RESULT_CODE_LOG_OUT){
				//退出登录
				mUserInfoViewModel.changeLoginState(false);
			}
		}else if(ThirdPartyLoginActivity.ACTIVITY_REQUEST_CODE == requestCode && data != null) {
			int type = data.getIntExtra(ThirdPartLoginConfig.EXTRA_TYPE, ThirdPartLoginConfig.TYPE_NORMAL);
			switch (resultCode) {
			case ThirdPartyLoginActivity.ACTIVITY_RESULT_CODE_CANCEL:
				hideLoadDialog();
				hideLoadView();
				break;
			case ThirdPartyLoginActivity.ACTIVITY_RESULT_CODE_FAIL:
				ToastUtil.showToast(this_, R.string.user_login_faild);
				
				hideLoadDialog();
				hideLoadView();
				break;
			case ThirdPartyLoginActivity.ACTIVITY_RESULT_CODE_SUCCESS:
				
				if(data.getExtras() != null) {
					
					Bundle resultData = data.getExtras();
					
					switch(type) {
					case ThirdPartLoginConfig.TYPE_TY:
						showLoadDialog();
						if(mTerminableThread != null && !mTerminableThread.isCancel()) {
							mTerminableThread.cancel();
						}
						mTerminableThread = AccountManager.getInstance().loginByTY(resultData.getString(ThirdPartLoginConfig.TYConfig.Extra_UserID),
								resultData.getString(ThirdPartLoginConfig.TYConfig.Extra_AccessToken), resultData.getString(ThirdPartLoginConfig.TYConfig.Extra_RefreshToken), false);
						
						break;
					case ThirdPartLoginConfig.TYPE_QQ:
						showLoadDialog();
						if(mTerminableThread != null && !mTerminableThread.isCancel()) {
							mTerminableThread.cancel();
						}
						mTerminableThread = AccountManager.getInstance().loginBySinaOrQQ(ThirdPartLoginConfig.TYPE_QQ,
														resultData.getString(ThirdPartLoginConfig.QQConfig.Extra_OpenID),
														resultData.getString(ThirdPartLoginConfig.QQConfig.Extra_NickName));
						break;
					case ThirdPartLoginConfig.TYPE_SINA:
						showLoadDialog();
						if(mTerminableThread != null && !mTerminableThread.isCancel()) {
							mTerminableThread.cancel();
						}
						mTerminableThread = AccountManager.getInstance().loginBySinaOrQQ(ThirdPartLoginConfig.TYPE_SINA,
													resultData.getString(ThirdPartLoginConfig.SinaConfig.Extra_UID),
														resultData.getString(ThirdPartLoginConfig.SinaConfig.Extra_Nick_Name));
						break;
					}
				
				}
				
				break;
			default:
				break;
			}
		}else{
			boolean isLogin = PreferencesUtil.getInstance(getApplicationContext()).getIsLogin();
			if(isLogin){
				mUserInfoViewModel.changeLoginState(true);
			}
		}
		
	    
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(PAGE_NAME);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(PAGE_NAME);
		MobclickAgent.onPause(this);
	}

	@Override
	public void exceptionHandle(String str) {}

	@Override
	public void optToast(String str) {}
	
	private UmengShareUtils utils;
	@Override
	public void shareClick() {
		utils = new UmengShareUtils();
		utils.baseInit(this_);
		utils.setShareInfo(this_, new UmengShareInfo(getResources().getString(R.string.share_for_software), ""));
		utils.setMailSubjectTitle("软件分享");
		if (popupWindow == null) {
			popupWindow = utils.showPopupWindow(this_, view,this,snsListener);
		}else {
			if (popupWindow.isShowing()) {
				popupWindow.dismiss();
			}
			UmengShareUtils.popWindowShow(view, popupWindow);
		}
	}

	@Override
	public void handleForYiXin(int type) {
		ShareYiXin shareYiXin = new ShareYiXin(this_);
		if (shareYiXin.isYxInstall()) {
//			Bitmap bitmap = CommonUtil.drawableToBitmap(this_.getResources().getDrawable(R.drawable.share_icon));
//			shareYiXin.sendTextWithBtimap(new MutilMediaInfo("",getString(R.string.share_for_software), "",type,UmengShareUtils.contentUrl),bitmap);
			shareYiXin.sendText(new MutilMediaInfo(
					this_.getResources().getString(R.string.share_for_software), "",type));
		}else {
			Toast.makeText(this_, "您还没有安装易信！", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void handleForWeiXin(int type) {
		ShareWeiXin shareWeiXin = new ShareWeiXin(this_);
		if (shareWeiXin.isWxInstall()) {
			if (shareWeiXin.isSupportVersion()) {
//				Bitmap bitmap = CommonUtil.drawableToBitmap(this_.getResources().getDrawable(R.drawable.share_icon));
//				shareWeiXin.sendTextWithBtimap(new MutilMediaInfo("", "",getString(R.string.share_for_software),type,UmengShareUtils.contentUrl),bitmap);
				shareWeiXin.sendText(new MutilMediaInfo(
						this_.getResources().getString(R.string.share_for_software), "",type));
			}else {
				Toast.makeText(this_, "请更新微信到最新版本！", Toast.LENGTH_SHORT).show();
			}
		}else {
			Toast.makeText(this_, "您还没有安装微信！", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void handleForQQ() {
		Bitmap bitmap = CommonUtil.drawableToBitmap(this_.getResources().getDrawable(R.drawable.share_icon));
		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent(getString(R.string.share_for_software));
		qqShareContent.setTitle("软件分享");
		qqShareContent.setShareImage(new UMImage(this, bitmap));
		qqShareContent.setTargetUrl(UmengShareUtils.contentUrl);
		utils.shareForQQ(qqShareContent);
	}

	@Override
	public void handleForQQZONE() {}

	@Override
	public void handleForSMS() {}

	@Override
	public void showRetryImgView() {
		setTipView(tipImg.request_fail, true);
	}

	@Override
	public void saveSourceId() {
		UmengShareUtils.LAST_SHARE_SOURCEID = CommonUtil.getMyUUID(this_);
		UmengShareUtils.shareContext = UserInfoActivity.this;
	}
	
	private BroadcastReceiver mTipDialogBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent == null){
				return;
			}
			final String soureName = intent.getStringExtra(ContentInfoActivityLeyue.ACTION_SHOW_TIP_DIALOG_AFTER_SHARE);
			if(ContentInfoActivityLeyue.ACTION_SHOW_TIP_DIALOG_AFTER_SHARE.equals(intent.getAction())
					&& CommonUtil.isOnCurrentActivityView(UserInfoActivity.this)){
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {	
						DialogUtil.commonConfirmDialog(UserInfoActivity.this, 
								UserInfoActivity.this.getString(R.string.share_tip), 
								UserInfoActivity.this.getString(R.string.share_software_repeat_tip,soureName), 
								R.string.share_enter_tip,R.string.share_exit_tip,new DialogUtil.ConfirmListener() {
							
									@Override
									public void onClick(View v) {
										ScoreRuleActivity.gotoScoreRuleActivity(UserInfoActivity.this);
									}
								},null).show();
					}
				});
			}else if (ContentInfoActivityLeyue.ACTION_SHARE_OK_UPDATE_VIEW.equals(intent.getAction())) {
				if (mUserInfoViewModel!=null) {
					String totalScore = intent.getStringExtra(ContentInfoActivityLeyue.ACTION_SHARE_OK_UPDATE_VIEW);
					mUserInfoViewModel.updateViewByScoreChange(totalScore);
				}
				
			}
		}
	};
	
	protected void onDestroy() {
		super.onDestroy();
		if (mTipDialogBroadcastReceiver!=null) {
			unregisterReceiver(mTipDialogBroadcastReceiver);
			mTipDialogBroadcastReceiver = null;
		}
		if(mTerminableThread != null) {
			mTerminableThread.cancel();
		}
	};
	
	private SnsPostListener snsListener = new SnsPostListener() {
		
		@Override
		public void onStart() {}
		
		@Override
		public void onComplete(SHARE_MEDIA arg0, int  eCode, SocializeEntity arg2) {
			LogUtil.e("--- eCode--"+ eCode);
			switch (arg0) {
			case QQ:
				if (eCode == ShareConfig.SNS_SUCEESS_CODE) {
//					mUserInfoViewModel.uploadShareInfo();
					ToastUtil.showToast(this_, "分享成功");
				}else {
					ToastUtil.showToast(this_, "分享失败");
				}
				break;
			case SINA:
				if (eCode == ShareConfig.SNS_SUCEESS_CODE) {
//					mUserInfoViewModel.uploadShareInfo();
					ToastUtil.showToast(this_, "分享成功");
				}else if(eCode == 5016){
					ToastUtil.showToast(this_, "分享内容重复");
				}else {
					ToastUtil.showToast(this_, "分享失败");
				}
				break;
			default:
				break;
			}
			
		}
	};
	/**
	 * 游客登录成功后融合数据，调用后台接口
	 */
	private void mergeData(){
		View topView = LayoutInflater.from(this_).inflate(R.layout.merge_data_dialog, null);
		DialogUtil.commonViewDialog(this_, topView).show();
	}
}
