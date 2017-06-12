package com.lectek.android.lereader.binding.model.user;

import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.binding.model.contentinfo.ScoreUploadModel;
import com.lectek.android.lereader.net.response.RuleLimitInfo;
import com.lectek.android.lereader.net.response.ScoreUploadResponseInfo;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.permanent.ThirdPartLoginConfig;
import com.lectek.android.lereader.presenter.SyncPresenter;
import com.lectek.android.lereader.share.util.UmengShareUtils;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.storage.dbase.UserScoreInfo;
import com.lectek.android.lereader.storage.dbase.util.UserScoreRecordDB;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.IBaseUserAction;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.login_leyue.ThirdPartyLoginActivity;
import com.lectek.android.lereader.ui.login_leyue.UserLoginLeYueNewActivity;
import com.lectek.android.lereader.ui.person.PersonInfoActivity;
import com.lectek.android.lereader.ui.specific.ActivityChannels;
import com.lectek.android.lereader.ui.specific.BookCityActivityGroup;
import com.lectek.android.lereader.ui.specific.CoverActivity;
import com.lectek.android.lereader.ui.specific.MainActivity;
import com.lectek.android.lereader.ui.specific.MyDigestActivity;
import com.lectek.android.lereader.ui.specific.MyMessagesActivity;
import com.lectek.android.lereader.ui.specific.VisitorEditUserPasswordActivity;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.ToastUtil;
import com.lectek.android.lereader.utils.UserManager;

/**
 * @description

 * @author chendt
 * @date 2013-12-27
 * @Version 1.0
 * @SEE UserInfoActivity
 */
public class UserInfoViewModelLeyue extends BaseLoadNetDataViewModel implements INetAsyncTask{
	
	public static final int REQUESTCODE = 54321;
	public static final int REQUEST_CODE_ACCOUNT_CHANGE = REQUESTCODE + 1;
	public static final int REQUEST_CODE_LOGOUT = REQUESTCODE + 2;
	
	public final StringObservable bUserName = new StringObservable();
	public final StringObservable bUserScore = new StringObservable();
	public final StringObservable bHeadImageUrl = new StringObservable();
	public final IntegerObservable bLoginBtnVisibility = new IntegerObservable(View.VISIBLE);
	public final IntegerObservable bShowUserInfoVisibility = new IntegerObservable(View.GONE);
	public final IntegerObservable bLogoutBtnVisibility = new IntegerObservable(View.GONE);
	public final IntegerObservable bThirdPartyVisibility = new IntegerObservable(View.INVISIBLE);
	public final IntegerObservable bMyPersonInfoClickVisibility = new IntegerObservable(View.VISIBLE);
	public final IntegerObservable bUserInfoHelperVisibility = new IntegerObservable(View.GONE);
	public final IntegerObservable bThirdPartyBackground = new IntegerObservable(R.drawable.account_sina_weibo_icon);
	public final BooleanObservable bLayoutVisible = new BooleanObservable(false);
	public final IntegerObservable bEditPsdVisibility = new IntegerObservable(View.GONE);
	public final IntegerObservable bShowNotLoginViewVisibility = new IntegerObservable(View.VISIBLE);
	private UserInfoLeyue mDataSource;
	private UserInfoUserAction mUserAction;
	private Activity mActivity;
	public final OnClickCommand bShareClick = new OnClickCommand() {

		@Override
		public void onClick(View v) {
			mUserAction.shareClick();
		}
	};
	
	public final OnClickCommand bEditPsdClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			boolean isLogin = PreferencesUtil.getInstance(getContext()).getIsLogin();
			if(isLogin){
				((Activity)getContext()).startActivityForResult(new Intent(getContext(), VisitorEditUserPasswordActivity.class), PersonInfoNickNameViewModel.REQUEST_CODE);
			}else{
				Intent intent = new Intent(getContext(), UserLoginLeYueNewActivity.class);
				((Activity)getContext()).startActivityForResult(intent, REQUESTCODE);
			}
			
		}
	};
	public final OnClickCommand bMyPersonInfoClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			boolean isLogin = PreferencesUtil.getInstance(getContext()).getIsLogin();
			if(isLogin){
				((Activity)getContext()).startActivityForResult(new Intent(getContext(), PersonInfoActivity.class), REQUESTCODE);
			}else{
				Intent intent = new Intent(getContext(), UserLoginLeYueNewActivity.class);
				((Activity)getContext()).startActivityForResult(intent, REQUESTCODE);
			}
		}
	};
    public final OnClickCommand bMyCollectClick = new OnClickCommand() {
        @Override
        public void onClick(View v) {
            boolean isLogin = PreferencesUtil.getInstance(getContext()).getIsLogin();
            if(isLogin){
                ActivityChannels.gotoCollectBookActivity(getContext());
            }else{
                Intent intent = new Intent(getContext(), UserLoginLeYueNewActivity.class);
                ((Activity)getContext()).startActivityForResult(intent, REQUESTCODE);
            }
        }
    };
    public final OnClickCommand bMyDigestClick = new OnClickCommand() {

        @Override
        public void onClick(View v) {
            MyDigestActivity.open(mActivity);
        }
    };
	
	public final OnClickCommand bMyMessageClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getContext(), MyMessagesActivity.class);
			getContext().startActivity(intent);
		}
	};
	
	public final OnClickCommand bMyOrderClick = new OnClickCommand(){
		@Override
		public void onClick(View v) {
			boolean isLogin = PreferencesUtil.getInstance(getContext()).getIsLogin();
			if(isLogin){
				ActivityChannels.gotoMyOrderActivity(getContext());
			}else{
				Intent intent = new Intent(getContext(), UserLoginLeYueNewActivity.class);
				((Activity)getContext()).startActivityForResult(intent, REQUESTCODE);
			}
		}
	};
	
	public final OnClickCommand bPointManagerClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			//阅点积分
			boolean isLogin = PreferencesUtil.getInstance(getContext()).getIsLogin();
			if(isLogin){
				ActivityChannels.gotoPointManagerActivity(getContext());
			}else{
				Intent intent = new Intent(getContext(), UserLoginLeYueNewActivity.class);
				((Activity)getContext()).startActivityForResult(intent, REQUESTCODE);
			}
			
		}
	};
	
	public final OnClickCommand bLoginClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			//注释原来登录逻辑，调用新的融合天翼用户登录逻辑 wuwq 2014-04-18
			//userLogin();
			tianyiUserLogin();
		}
	};
	
	public final OnClickCommand bLogoutClick = new OnClickCommand(){
		@Override
		public void onClick(View v) {
//			DialogUtil.commonConfirmDialog((Activity)getContext(), null, getContext().getString(R.string.logout_app_tip), R.string.main_menu_exit, R.string.btn_text_cancel, new ConfirmListener(){
//
//				@Override
//				public void onClick(View v) {
//					logoutUser();
//				
//				}},null);
            SyncPresenter.setSwitchTagAction(true);
			Intent intent = new Intent(mActivity, UserLoginLeYueNewActivity.class);
			mActivity.startActivityForResult(intent, UserInfoViewModelLeyue.REQUEST_CODE_ACCOUNT_CHANGE);
			
		}
	};
	
	public final OnClickCommand bFeedbackClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
            ActivityChannels.gotoFeedbackActivity(getContext());
		}
	};
	
	public final OnClickCommand bAboutClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			ActivityChannels.gotoAboutUsActivity(getContext());
		}
	};
	
	/**
	 * 个人中心帮助界面(在用户为登陆情况下出现，引导用户登陆领取积分)
	 */
	public final OnClickCommand bUserInfoHelperClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			bUserInfoHelperVisibility.set(View.GONE);
		}
	};
	
	/**
	 * 立即领取(跳转到登陆界面)
	 */
	public final OnClickCommand bGainPointClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			//userLogin();
			//注释原来登录逻辑，调用新的融合天翼用户登录逻辑 wuwq 2014-04-18
			tianyiUserLogin();
		}
	};
	
	public final OnClickCommand bUserSettingClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			ActivityChannels.gotoUserSettingActivity(getContext());
			
		}
	};
	
	public final OnClickCommand bArrowClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getContext(), CoverActivity.class);
			getContext().startActivity(intent);
			
		}
	};
	
	public final OnClickCommand bQQLoginClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mActivity, ThirdPartyLoginActivity.class);
			intent.putExtra(ThirdPartLoginConfig.EXTRA_TYPE, ThirdPartLoginConfig.TYPE_QQ);
			mActivity.startActivityForResult(intent, ThirdPartyLoginActivity.ACTIVITY_REQUEST_CODE);
			
		}
	};
	
	public final OnClickCommand bSinaLoginClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mActivity, ThirdPartyLoginActivity.class);
			intent.putExtra(ThirdPartLoginConfig.EXTRA_TYPE, ThirdPartLoginConfig.TYPE_SINA);
			mActivity.startActivityForResult(intent, ThirdPartyLoginActivity.ACTIVITY_REQUEST_CODE);
			
		}
	};
	
	public final OnClickCommand bLeyueLoginClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mActivity, UserLoginLeYueNewActivity.class);
			mActivity.startActivityForResult(intent, UserInfoViewModelLeyue.REQUEST_CODE_ACCOUNT_CHANGE);
			
		}
	};
	
	public UserInfoViewModelLeyue(Context context, INetLoadView loadView,UserInfoUserAction userAction) {
		super(context, loadView);
		mUserAction = userAction;
		mActivity = (Activity) context;
	}
	
	
	private void userLogin(){
		Intent intent = new Intent(getContext(), UserLoginLeYueNewActivity.class);
		((Activity)getContext()).startActivityForResult(intent, REQUESTCODE);
	}
	/**
	 * 调用新的融合天翼用户登录逻辑 wuwq 2014-04-18
	 */
	private void tianyiUserLogin(){
//		AccountManager.getInstance().login(AccountType.LEYUE, LoginType.PAGE_LOGIN, new ILoginInterface() {
//			
//			@Override
//			public void showLoading() {
//				UserInfoViewModelLeyue.this.showLoadView();
//				
//			}
//			
//			@Override
//			public void loginSuccess() {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void loginFail() {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void hideLoading() {
//				// TODO Auto-generated method stub
//				
//			}
//		}, getContext());
		Intent intent = new Intent(getContext(), UserLoginLeYueNewActivity.class);
		((Activity)getContext()).startActivityForResult(intent, REQUESTCODE);
	}
	
	/**
	 * 改变当前的登录状态，true：显示用户信息；false：显示登录按钮
	 * @param isLogin 
	 */
	public void changeLoginState(boolean isLogin){
		if(isLogin){
			bLoginBtnVisibility.set(View.GONE);
			bShowNotLoginViewVisibility.set(View.GONE);
			bShowUserInfoVisibility.set(View.VISIBLE);
			bLogoutBtnVisibility.set(View.VISIBLE);
			bUserInfoHelperVisibility.set(View.GONE);
			mDataSource = AccountManager.getInstance().getUserInfo();
			if(mDataSource != null){
				String photoUrl = mDataSource.getPhotoUrl();
				if(!TextUtils.isEmpty(photoUrl)){
					bHeadImageUrl.set(photoUrl);
				}
			}
			setNickName();
		}else{
			bLoginBtnVisibility.set(View.VISIBLE);
			bShowNotLoginViewVisibility.set(View.VISIBLE);
			bShowUserInfoVisibility.set(View.GONE);
			bLogoutBtnVisibility.set(View.GONE);
			if(!LeyueConst.isShowUserInfoHelper){
				bUserInfoHelperVisibility.set(View.VISIBLE);
				LeyueConst.isShowUserInfoHelper = true;
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
//		tryStartNetTack(this);
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		//2
		return false;
	}

	@Override
	protected boolean hasLoadedData() {
		return true;
	}

	@Override
	public void onRelease() {
		super.onRelease();
	}
	
	public static interface UserInfoUserAction extends IBaseUserAction{
		public void shareClick();
		public void showRetryImgView(); 
	}
	
	/**正在进行分享操作*/
	private boolean isShareHandle = false;
//	public void uploadShareInfo(){
//		if (UserScoreInfo.SINA.equals(UmengShareUtils.LAST_SHARE_TYPE) || 
//				UserScoreInfo.QQ_FRIEND.equals(UmengShareUtils.LAST_SHARE_TYPE)) {
//			isShareHandle = true;
//			mUploadModel.start(UmengShareUtils.LAST_SHARE_TYPE,UmengShareUtils.LAST_SHARE_SOURCEID);
//		}
//	}


	@Override
	public boolean isNeedReStart() {
		return !hasLoadedData();
	}


	@Override
	public boolean isStop() {
		return true;
	}


	@Override
	public void start() {
		boolean isLogin = PreferencesUtil.getInstance(getContext()).getIsLogin();
		if(isLogin){
			changeLoginState(true);
		}else{
			bLayoutVisible.set(true);
			changeLoginState(false);
		}
	}
	
	public void updateViewByScoreChange(String totalScore){
		bUserScore.set(getContext().getResources().getString(R.string.menu_consume_score, totalScore));
	}
	
	//设置刷新昵称
	private void setNickName(){
		//判断是否是游客
		boolean isVisitor = AccountManager.getInstance().isVisitor();
		if(mDataSource != null){
			if(!TextUtils.isEmpty(mDataSource.getNickName())){
				bUserName.set(mDataSource.getNickName());
			}else{
				bUserName.set(mDataSource.getAccount());
			}
		}
		
	}
}
