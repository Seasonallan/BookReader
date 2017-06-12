package com.lectek.android.lereader.account;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.account.UseAccount.IUserAccountResult;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.binding.model.contentinfo.ScoreUploadModel;
import com.lectek.android.lereader.lib.cache.reference.SoftReferenceQueue;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.thread.ThreadFactory;
import com.lectek.android.lereader.lib.thread.internal.ITerminableThread;
import com.lectek.android.lereader.lib.utils.StringUtil;
import com.lectek.android.lereader.lib.utils.UniqueIdUtils;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.openapi.ApiProcess4TianYi;
import com.lectek.android.lereader.net.response.RegisterInfo;
import com.lectek.android.lereader.net.response.UserThirdLeyueInfo;
import com.lectek.android.lereader.net.response.UserThridInfo;
import com.lectek.android.lereader.permanent.AccountConfig;
import com.lectek.android.lereader.permanent.ApiConfig;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.permanent.ThirdPartLoginConfig;
import com.lectek.android.lereader.presenter.SyncPresenter;
import com.lectek.android.lereader.storage.dbase.TianYiUserInfo;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.storage.dbase.UserScoreInfo;
import com.lectek.android.lereader.storage.dbase.util.TianYiUserInfoDB;
import com.lectek.android.lereader.storage.dbase.util.UserInfoLeYueDB;
import com.lectek.android.lereader.storage.dbase.util.UserScoreRecordDB;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.login_leyue.ThirdPartyLoginActivity;
import com.lectek.android.lereader.ui.login_leyue.UserLoginLeYueNewActivity;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.UserManager;


/**
 * 账号管理(应用相关)
 * @author wuwq
 * @date 2014-04-28
 */
public class AccountManager{
	/** 乐阅后台基本账号bean. */
//	private static LeYueBaseAccount leYueBaseAccount = new LeYueBaseAccount();
	
	/** 天翼账号基本属性bean. */
//	private static TYBaseAccount tyBaseAccount = new TYBaseAccount();
	/** 微信账号信息 */
	private static WXBaseAccount wXBaseAccount = new WXBaseAccount();
	
	/** 账号类型. */
	private static int accountType;
	
	private static AccountManager mInstance;
	
//	private AppLoginReceiver mAppLoginReceiver;
	
	private ILoginInterface mLoginInterface;
	
	private Context this_ = MyAndroidApplication.getInstance().getBaseContext();
	
	private Context payContext = null;
	
	private SoftReferenceQueue<IaccountObserver> mObservers = new SoftReferenceQueue<IaccountObserver>();
	private UserInfoLeyue mUserInfo;
	private TianYiUserInfo mTYAccountInfo;
	
	public static AccountManager getInstance(){
		if(mInstance == null){
			syncInit();
		}
		return mInstance;
	}
	
	public synchronized static AccountManager syncInit(){
		if(mInstance == null){
			mInstance = new AccountManager(MyAndroidApplication.getInstance());
		}
		return mInstance;
	}
	
	private AccountManager(Context context) {
//		leYueBaseAccount.setmAccountLoginResult(mLeyueAccountLoginResult);
//		tyBaseAccount.setmAccountLoginResult(mLeyueAccountLoginResult);
		wXBaseAccount.setmAccountLoginResult(mLeyueAccountLoginResult);
	}
	
	/**
	 * 注册帐号观察者
	 * @param observer
	 */
	public void registerObserver(IaccountObserver observer) {
		mObservers.addReference(observer);
	}
	
	public void unRegisterObserver(IaccountObserver observer) {
		mObservers.removeReference(observer);
	}
	
	//分发帐号改变事件
	private void dispatchAccountChanged() {
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				Iterator<SoftReference<IaccountObserver>> collections = mObservers.getSoftReferencesIterator();
				while(collections.hasNext()) {
					SoftReference<IaccountObserver> item = collections.next();
					if(item != null && item.get() != null) {
						item.get().onAccountChanged();
					}
				}
			}
		};
		
		if(Thread.currentThread() != Looper.getMainLooper().getThread()){
			MyAndroidApplication.getHandler().post(runnable);
		}else {
			runnable.run();
		}
	}
	
	private void dispatchLoginComplete(final boolean success, final String msg, final Object...params) {
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				Iterator<SoftReference<IaccountObserver>> collections = mObservers.getSoftReferencesIterator();
				while(collections.hasNext()) {
					SoftReference<IaccountObserver> item = collections.next();
					if(item != null && item.get() != null) {
						item.get().onLoginComplete(success, msg, params);
					}
				}
			}
		};
		
		if(Thread.currentThread() != Looper.getMainLooper().getThread()){
			MyAndroidApplication.getHandler().post(runnable);
		}else {
			runnable.run();
		}
	}
	
	private void dispatchOnGetUserInfo(final UserInfoLeyue userInfo) {
		Runnable runnable = new Runnable() {
			
				@Override
				public void run() {
					Iterator<SoftReference<IaccountObserver>> collections = mObservers.getSoftReferencesIterator();
					while(collections.hasNext()) {
						SoftReference<IaccountObserver> item = collections.next();
						if(item != null && item.get() != null) {
							item.get().onGetUserInfo(userInfo);
						}
					}
			}
		};
		
		if(Thread.currentThread() != Looper.getMainLooper().getThread()){
			MyAndroidApplication.getHandler().post(runnable);
		}else {
			runnable.run();
		}
	}
	
	/**
	 * 按用户类型登录
	 * @return
	 */
	public void login(AccountType accountType, LoginType loginType, ILoginInterface loginInterface, Object... params){
		mLoginInterface = loginInterface;
		if(mLoginInterface != null){
			mLoginInterface.showLoading();
		}
		switch(loginType){
//			case AUTO_LOGIN:
//				autoLogin(accountType,params);
//				break;
			case BASE_LOGIN:
				baseLogin(accountType, params);
				break;
			case PAY_LOGIN:
				payLogin(accountType, params);
				break;
			case PAGE_LOGIN:
				toLoginPage(params);
				break;
			default:
				break;
		}
	}
	/**
	 * 自动登录
	 * @return
	 */
//	private void autoLogin(AccountType accountType,Object... params) {
//		if(params != null){
//			switch (accountType) {
//				case WEIXIN:
//					String leYueUserId = (String) params[0];
//					String wxUserId = (String) params[1];
//					wXBaseAccount.loginByUserId(this_, wxUserId, leYueUserId,leYueBaseAccount);
//					//leYueBaseAccount.loginByUserId(this_, wxUserId, wxUserId, leYueUserId);
//					break;
//				case LEYUE:
//					UserInfoLeyue infoLeyue = getLeYueAccount();
//					if(infoLeyue != null){
//						leYueBaseAccount.loginByUserId(this_, infoLeyue.getAccount(), infoLeyue.getPassword(), infoLeyue.getUserId());
//					}else{
//						//使用唯一码登录
//						String uniKey = getUniqueId(this_);
//						leYueBaseAccount.uniKeyLogin(uniKey);
//					}
//					break;
//				default:
//					break;
//			}
//		}else{
//			UserInfoLeyue infoLeyue = getLeYueAccount();
//			if(infoLeyue != null){
//				//本地获取成功直接登录
//				leYueBaseAccount.login(null, infoLeyue.getAccount(), infoLeyue.getPassword());
//			}else{
//				//使用唯一码登录
//				String uniKey = getUniqueId(this_);
//				leYueBaseAccount.uniKeyLogin(uniKey);
//			}
//		}
//	}
	
	/**
	 * 自动登录
	 */
	public void autoLogin() {
		UserInfoLeyue userInfo = getUserInfo();
		if(userInfo != null) {
			saveUserInfo(userInfo);
			PreferencesUtil.getInstance(this_).setIsLogin(true);
			dispatchLoginComplete(true, null);
		}else {
			String uniKey = getUniqueId(this_);
			registerByDeviceId(uniKey);
		}
	}
	
	/**
	 * 自动注册
	 */
	private void registerByDeviceId(final String deviceId) {
		
		ITerminableThread thread = ThreadFactory.createTerminableThread(new Runnable() {
			
			@Override
			public void run() {
				try {
					
					UserThridInfo thirdInfo = ApiProcess4Leyue.getInstance(this_).registByDeviceId(deviceId,ApiConfig.DEVICEID_REGISTER);
					if(thirdInfo != null) {
						UserInfoLeyue infoLeyue = ApiProcess4Leyue.getInstance(this_).getUserInfo(thirdInfo.getUserId());
//						UserInfoLeyue infoLeyue = new UserInfoLeyue();
//						infoLeyue.setUserId(thirdInfo.getUserId());
//						thirdInfo.
						
						infoLeyue.setPassword(deviceId);
						saveUserInfo(infoLeyue);
						PreferencesUtil.getInstance(this_).setIsLogin(true);
						dispatchAccountChanged();
					}
					
				}catch(GsonResultException ge) {}
			}
		});
		
		thread.start();
	}
	
	/**
	 * 获取乐阅帐号信息
	 * @param userId
	 * @return
	 */
	public ITerminableThread requestUserInfo(final String userId) {
		ITerminableThread thread = ThreadFactory.createTerminableThread(new Runnable() {
			
			@Override
			public void run() {
				try {
					UserInfoLeyue userInfo = ApiProcess4Leyue.getInstance(this_).getUserInfo(userId);
					if(userInfo != null) {
						saveUserInfo(userInfo);
						
						new ScoreUploadModel().start(UserScoreInfo.ANDROID_LOGIN,CommonUtil.getMyUUID(this_));
						
						dispatchOnGetUserInfo(userInfo);
					}
					
				}catch(GsonResultException ge) {}
			}
		});
		thread.start();
		return thread;
	}
	
//	private TianYiUserInfo requestTYUserInfo(String lereadUserId) {
//		
//		if(StringUtil.isEmpty(lereadUserId)) {
//			//根据用户id获取后台绑定第三方账号信息
//			try {
//				TianYiUserInfo tianYiInfo = null;
//		        ArrayList<UserThirdLeyueInfo> infos = ApiProcess4Leyue.getInstance(this_).getThirdIdByLeYueId(lereadUserId);
//				if(infos != null && infos.size()>0){
//					for(UserThirdLeyueInfo info : infos){
//						if(!TextUtils.isEmpty(info.getSource()) && info.getSource().equals(UserInfoLeyue.TYPE_TIANYI)){
//							//获取用户信息返回
//							tianYiInfo = new TianYiUserInfo();
//							tianYiInfo.setAccessToken(info.getAccessToken());
//							tianYiInfo.setUserId(info.getThirdId());
//							tianYiInfo.setLeyueUserId(lereadUserId);
//							tianYiInfo.setRefreshToken(info.getRefreshToken());
//							if(!TextUtils.isEmpty(info.getAccessToken())){
//								UserManager.getInstance(this_).setCurrentAccessToken(info.getAccessToken());
//								//获取天翼账号信息更新乐阅后台账号信息
//								TianYiUserInfo userInfo = ApiProcess4TianYi.getInstance(this_).queryUserInfo();
//								userInfo.setLeyueUserId(lereadUserId);
//								userInfo.setAccessToken(info.getAccessToken());
//								userInfo.setRefreshToken(info.getRefreshToken());
//								userInfo.setUserId(info.getThirdId());
//								if(userInfo!=null){
//									return userInfo;
//								}
//							}
//						}
//					}
//					return tianYiInfo;
//				}
//			}catch(GsonResultException ge) {}
//		}
//		return null;
//	}
	
	private void saveUserInfo(UserInfoLeyue userInfo) {
		mUserInfo = userInfo;
		UserInfoLeYueDB.getInstance(this_).setUserLeYueInfo(userInfo);
		AccountManager.getInstance().setUserInfo(userInfo);
//		PreferencesUtil.getInstance(this_).setIsLogin(true);
		PreferencesUtil.getInstance(this_).setUserId(userInfo.getUserId());
		PreferencesUtil.getInstance(this_).setUserNickName(userInfo.getNickName());
		if(!TextUtils.isEmpty(userInfo.getEmail()))
			PreferencesUtil.getInstance(this_).setBindEmail(userInfo.getEmail());
		if(!TextUtils.isEmpty(userInfo.getMobile()))
			PreferencesUtil.getInstance(this_).setBindPhoneNum(userInfo.getMobile());
		if(!TextUtils.isEmpty(userInfo.getSource()))
			PreferencesUtil.getInstance(this_).setUserSource(userInfo.getSource());
		PreferencesUtil.getInstance(this_).setUserName(userInfo.getAccount());
		PreferencesUtil.getInstance(this_).setUserPSW(userInfo.getPassword());

		UserScoreRecordDB.getInstance(this_).updateGuestRecordToUser(userInfo.getUserId());
        SyncPresenter.startSyncTask();
	}
	
	/**
	 * 普通登录
	 * @return
	 */
	private boolean baseLogin(AccountType accountType,  Object... params){
		
		if(params != null){
//			Context context = null;
//			Intent intent = null;
//			switch (accountType) {
//				case QQ:
//					context = (Context) params[0];
//					//只传Context进来，第三方登录
//					intent = new Intent(context, ThirdPartyLoginActivity.class);
//					intent.putExtra(ThirdPartLoginConfig.EXTRA_TYPE, ThirdPartLoginConfig.TYPE_QQ);
////					context.startActivity(intent);
//					if(context instanceof UserLoginLeYueNewActivity) {
//						((Activity)context).startActivityForResult(intent, ThirdPartyLoginActivity.ACTIVITY_REQUEST_CODE);
//					}else {
//						context.startActivity(intent);
//					}
//					break;
//				case SINA:
//					context = (Context) params[0];
//					registerAppLoginReceiver(MyAndroidApplication.getInstance().getBaseContext());
//					intent = new Intent(context, ThirdPartyLoginActivity.class);
//					intent.putExtra(ThirdPartLoginConfig.EXTRA_TYPE, ThirdPartLoginConfig.TYPE_SINA);
////					context.startActivity(intent);
//					if(context instanceof UserLoginLeYueNewActivity) {
//						((Activity)context).startActivityForResult(intent, ThirdPartyLoginActivity.ACTIVITY_REQUEST_CODE);
//					}else {
//						context.startActivity(intent);
//					}
//					break;
//				case TIANYI:
//					context = (Context) params[0];
//					registerAppLoginReceiver(MyAndroidApplication.getInstance().getBaseContext());
//					intent = new Intent(context, ThirdPartyLoginActivity.class);
//					intent.putExtra(ThirdPartLoginConfig.EXTRA_TYPE, ThirdPartLoginConfig.TYPE_TY);
//					intent.putExtra("logintype", LoginType.BASE_LOGIN.getCode());
//					if(context instanceof UserLoginLeYueNewActivity) {
//						((Activity)context).startActivityForResult(intent, ThirdPartyLoginActivity.ACTIVITY_REQUEST_CODE);
//					}else {
//						context.startActivity(intent);
//					}
//						
////					context = (Context) params[0];
////					registerAppLoginReceiver(MyAndroidApplication.getInstance().getBaseContext());
////					intent = new Intent(context, UserLoginTianYiActivity.class);
////					intent.putExtra("logintype", LoginType.BASE_LOGIN.getCode());
////					//如果是登录界面发起的需要处理用户按返回键取消登录的情况
////					if(context instanceof UserLoginLeYueNewActivity) {
////						intent.putExtra(UserLoginTianYiActivity.EXTRA_FROM_USER_LOGIN_ACTIVITY, true);
////						((Activity)context).startActivityForResult(intent, UserLoginLeYueNewActivity.EXTRA_REQUEST_CODE_AUTO_LOGIN);
////					}else {
////						context.startActivity(intent);
////					}
//					break;
//				case LEYUE:
//					String account = (String) params[0];
//					String password = (String) params[1];
//					//乐阅登录
//					leYueBaseAccount.login(null, account, password);
//					break;
//				default:
//					break;
//			}
		}
		return true;
	}
	
	/**
	 * 购买时登录天翼
	 * @return
	 */
	public boolean payLogin(AccountType accountType,  Object... params){
		
		if(params != null){
			if(params.length == 1){
				Context context = (Context) params[0];
				payContext = context;
				Intent intent = null;
				switch (accountType) {
				case TIANYI:
					intent = new Intent(context, ThirdPartyLoginActivity.class);
					intent.putExtra(ThirdPartLoginConfig.EXTRA_TYPE, ThirdPartLoginConfig.TYPE_TY);
					intent.putExtra("logintype", LoginType.PAY_LOGIN.getCode());
					context.startActivity(intent);
					break;
				default:
					break;
				}
			}
		}
		return true;
	}
	/**
	 * 跳转到页面登录
	 * @param params
	 */
	private void toLoginPage(Object... params){
		if(params != null){
			if(params.length == 1){
				Context context = (Context) params[0];
				Intent intent = new Intent(context, UserLoginLeYueNewActivity.class);
				context.startActivity(intent);
			}
		}
	}
	
	/**
	 * 注册第三方登录广播
	 * @param context
	 */
//	private void registerAppLoginReceiver(Context context){
//		mAppLoginReceiver = new AppLoginReceiver();
//		mAppLoginReceiver.setThirdBindingInterface(this);
//		IntentFilter filter = new IntentFilter(AppLoginReceiver.ACTION_THIRD_LOGIN);
//		context.registerReceiver(mAppLoginReceiver, filter);
//	}
	
	/**
	 * 注销第三方登录广播
	 * @param context
	 */
//	private void unregisterAppLoginReceiver(Context context){
//		if(mAppLoginReceiver != null){
//			context.unregisterReceiver(mAppLoginReceiver);
//			mAppLoginReceiver = null;
//		}
//		
//	}
	
	
	/**
	 * 获取当天登录用户乐阅账号信息 
	 * @return
	 */
//	public UserInfoLeyue getLeYueAccount(){
//		return leYueBaseAccount.getLeYueAccount();
//	}
	/**
	 * 获取当前登录用户天翼账户信息
	 * @return
	 */
//	public TianYiUserInfo getTianYiAccount(String userId){
//		return tyBaseAccount.getTianYiAccount(userId);
//	}
	
	public boolean isLoginTianYiAccount(){
//		String userId = PreferencesUtil.getInstance(this_).getUserId();
//		TianYiUserInfo info = getTianYiAccount(userId);
//		if(info == null){
//			return false;
//		}else{
//			return true;
//		}
		UserInfoLeyue userInfo = getUserInfo();
		if(userInfo != null) {
			return getTYAccountInfo(userInfo.getUserId()) != null;
		}
		return false;
	}
	
	/**
	 * 获取当前用户是否是游客
	 * @return
	 */
	public boolean isVisitor(){
		boolean flag = false;
		UserInfoLeyue userInfo = getUserInfo();
		if(userInfo != null){
			flag = LeyueConst.TOURIST_USER_ID.equals(userInfo.getUserId());
			if(userInfo != null 
					&& !TextUtils.isEmpty(userInfo.getAccount()) && userInfo.getAccount().equals(userInfo.getPassword())
					&& UserInfoLeyue.TYPE_DEVICEID.equals(userInfo.getSource())) {
				flag = getTYAccountInfo(userInfo.getUserId()) == null;
			}
		}
		
		return flag;
	}
	
//	/**
//	 * 当前是否为隐式登录
//	 * @return
//	 */
//	public boolean isStealthLogin(){
//		boolean flag = false;
//		UserInfoLeyue userLeyueInfo = getLeYueAccount();
//		if(userLeyueInfo!=null){
//			if(!userLeyueInfo.getAccount().equals(userLeyueInfo.getPassword()) && userLeyueInfo.getSource().equals(UserInfoLeyue.TYPE_DEVICEID)){
//				TianYiUserInfo tianYiUserInfo = getTianYiAccount(userLeyueInfo.getUserId());
//				if(tianYiUserInfo==null){
//					flag = true;
//				}
//			}
//		}
//		return flag;
//	}
	
	/**
	 * 获取唯一标识
	 * @return
	 */
	public String getUniqueId(Context context){
		String uniqueId = UniqueIdUtils.getDeviceId(context);
		return uniqueId;
	}
	
	/**
	 * Gets the 乐阅后台基本账号bean.
	 *
	 * @return the 乐阅后台基本账号bean
	 */
//	public static LeYueBaseAccount getLeYueBaseAccount() {
//		return leYueBaseAccount;
//	}

	/**
	 * Sets the 乐阅后台基本账号bean.
	 *
	 * @param leYueBaseAccount the new 乐阅后台基本账号bean
	 */
//	public static void setLeYueBaseAccount(LeYueBaseAccount leYueBaseAccount) {
//		AccountManager.leYueBaseAccount = leYueBaseAccount;
//	}

	/**
	 * Gets the 天翼账号基本属性bean.
	 *
	 * @return the 天翼账号基本属性bean
	 */
//	public static TYBaseAccount getTyBaseAccount() {
//		return tyBaseAccount;
//	}

	/**
	 * Sets the 天翼账号基本属性bean.
	 *
	 * @param tyBaseAccount the new 天翼账号基本属性bean
	 */
//	public static void setTyBaseAccount(TYBaseAccount tyBaseAccount) {
//		AccountManager.tyBaseAccount = tyBaseAccount;
//	}

	/**
	 * Gets the 账号类型.
	 *
	 * @return the 账号类型
	 */
	public static int getAccountType() {
		return accountType;
	}

	/**
	 * Sets the 账号类型.
	 *
	 * @param accountType the new 账号类型
	 */
	public static void setAccountType(int accountType) {
		AccountManager.accountType = accountType;
	}

	public interface ILoginInterface{
		public void loginSuccess();
		public void loginFail();
		public void showLoading();
		public void hideLoading();
	}

//	@Override
//	public void thirdLoginSuccess(AccountType accountType) {
//		Context context = MyAndroidApplication.getInstance().getBaseContext();
//		String tempAccount = PreferencesUtil.getInstance(context).getUserName();
//		String tempPassword = PreferencesUtil.getInstance(context).getUserPSW();
//		String userId = PreferencesUtil.getInstance(context).getUserId();
//		switch (accountType) {
//		case QQ:
//		case SINA:
//			leYueBaseAccount.login(context, tempAccount, tempPassword);
//			break;
////		case TIANYI:
////			leYueBaseAccount.loginByUserId(context, tempAccount, tempPassword,userId);
//			//TODO 保存账号天翼信息,目前在UserLoginTianYiActivity中调用数据库保存，待搬迁
////			break;
//		default:
//			break;
//		}
//	}
	
	public void setUserInfo(UserInfoLeyue userInfo) {
		mUserInfo = userInfo;
	}
	
	public UserInfoLeyue getUserInfo() {
		if(mUserInfo == null) {
			mUserInfo = UserInfoLeYueDB.getInstance(this_).getLastLoginUserLeYueInfo();
		}
		return mUserInfo;
	}
	
	public String getUserID() {
		UserInfoLeyue userInfo = getUserInfo();
		return userInfo == null ? "" : userInfo.getUserId();
	}
	
	public boolean isLogin() {
		return !TextUtils.isEmpty(getUserID()) && !LeyueConst.TOURIST_USER_ID.equals(getUserID());
	}
	
	public TianYiUserInfo getTYAccountInfo(String leyueId) {
		if(mTYAccountInfo == null && !TextUtils.isEmpty(leyueId)) {
			mTYAccountInfo = TianYiUserInfoDB.getInstance(this_).getTianYiUserInfo(leyueId);
		}
		
		return mTYAccountInfo;
	}
	
	/**
	 * 乐阅登录
	 * @return
	 */
	public ITerminableThread lereadLogin(final String account, final String psw) {
		ITerminableThread thread = ThreadFactory.createTerminableThread(new Runnable() {

			@Override
			public void run() {
				String errorMsg = this_.getString(AccountConfig.RES_ID_STRING_SWITCH_ACCOUNT_FAIL);
				try {
					UserInfoLeyue userInfo = ApiProcess4Leyue.getInstance(this_).login(account, psw);
					if(userInfo != null) {
						saveUserInfo(userInfo);
						PreferencesUtil.getInstance(this_).setIsLogin(true);
						
						dispatchLoginComplete(true, this_.getString(AccountConfig.RES_ID_STRING_SWITCH_ACCOUNT_SUCCESS), userInfo);
						
						dispatchAccountChanged();
						
						dispatchOnGetUserInfo(userInfo);
						return;
					}
					
				}catch(GsonResultException ge){
					if(ge.getResponseInfo() != null && !StringUtil.isEmpty(ge.getResponseInfo().errorDescription)) {
						errorMsg = ge.getResponseInfo().errorDescription;
					}
				}
				dispatchLoginComplete(false, errorMsg);
			}
		});
		thread.start();
		return thread;
	}
	
	/**
	 * 乐阅注册
	 * @param account email或其他帐号名称
	 * @param nick 用户昵称
	 * @param psw 密码
	 * @return
	 */
	public ITerminableThread lereadRegister(final String account, final String nick, final String psw) {
		ITerminableThread thread = ThreadFactory.createTerminableThread(new Runnable() {
			
			@Override
			public void run() {
				
				String errorMsg = this_.getString(AccountConfig.RES_ID_STRING_REGISTER_FAIL);
				
				try {
					RegisterInfo info = ApiProcess4Leyue.getInstance(this_).regist(account, psw, nick, ApiConfig.EMAIL_REGISTER);
					
					if(info != null) {
						PreferencesUtil.getInstance(this_).setUserId(info.getUserId());
						PreferencesUtil.getInstance(this_).setUserName(account);
						PreferencesUtil.getInstance(this_).setUserPSW(psw);
						
						dispatchLoginComplete(true, this_.getString(AccountConfig.RES_ID_STRING_REGISTER_SUCCESS), info);
						dispatchAccountChanged();
						return;
					}
					
				}catch(GsonResultException ge) {
					if(ge.getResponseInfo() != null && !StringUtil.isEmpty(ge.getResponseInfo().errorDescription)) {
						errorMsg = ge.getResponseInfo().errorDescription;
					}
				}
				
				dispatchLoginComplete(false, errorMsg);
			}
		});
		thread.start();
		return thread;
	}
	
	/**
	 * 天翼天翼帐号登录
	 * @param thirdId
	 * @param accessToken
	 * @param refreshToken
	 * @return
	 */
	public ITerminableThread loginByTY(final String thirdId,final String accessToken,final String refreshToken, final boolean showSwitchTip){
//		UserManager.getInstance(this_).setCurrentAccessToken(accessToken + "");
//		PreferencesUtil.getInsstance(this_).setIsLogin(true);
		
		ITerminableThread thread = ThreadFactory.createTerminableThread(new Runnable() {
			@Override
			public void run() {
				try {
					String leYueUserId = PreferencesUtil.getInstance(this_).getUserId();
					if(LeyueConst.TOURIST_USER_ID.equals(leYueUserId)) {
						leYueUserId = null;
					}
					TianYiUserInfo tyUserInfo = getTYAccountInfo(leYueUserId);
					UserThirdLeyueInfo refInfo = null;	//乐阅帐号绑定的第三帐号信息
					
					if(tyUserInfo == null ){	//若本地天翼账号信息为空，获取当前乐阅帐号绑定的天翼帐号
                        ArrayList<UserThirdLeyueInfo>  infos = ApiProcess4Leyue.getInstance(this_).getThirdIdByLeYueId(leYueUserId);
						if(infos != null && infos.size()>0){
							for(UserThirdLeyueInfo item : infos){
								if(!TextUtils.isEmpty(item.getSource()) && item.getSource().equals(UserInfoLeyue.TYPE_TIANYI)){
									refInfo = item;
									try {
										ApiProcess4Leyue.getInstance(this_).updateThirdAccessToken(refInfo.getId(), accessToken, refreshToken);
									}catch(GsonResultException ge){}
									
									break;
								}
							}
						}
					}else{
						refInfo = new UserThirdLeyueInfo();
						refInfo.setUserId(tyUserInfo.getLeyueUserId());
						refInfo.setThirdId(tyUserInfo.getUserId());
					}
			
					boolean diffTY = refInfo != null && !StringUtil.isEmpty(refInfo.getThirdId()) && !refInfo.getThirdId().equals(thirdId);
					
					//绑定的天翼id与当前登录的天翼Id不一致, 需要弹出确定切换对话框
					if (diffTY && showSwitchTip) {
						
						((Activity)payContext).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								DialogUtil.oneConfirmBtnDialog((Activity)payContext, payContext.getResources().getString(R.string.pay_tianyi_tip_title), payContext.getResources().getString(R.string.pay_tianyi_tip_content),R.string.btn_text_confirm, new DialogUtil.ConfirmListener() {
									@Override
									public void onClick(View v) {
										ITerminableThread thread = ThreadFactory.createTerminableThread(new Runnable() {
											
											@Override
											public void run() {
												//天翼帐号登录
												if(!doLoginByTY(thirdId, accessToken, refreshToken, null)) {
													dispatchLoginComplete(false, this_.getString(AccountConfig.RES_ID_STRING_SWITCH_ACCOUNT_FAIL));
													return;
												}
												
												UserManager.getInstance(this_).setCurrentAccessToken(accessToken);
												PreferencesUtil.getInstance(this_).setIsLogin(true);
								
												dispatchLoginComplete(true, this_.getString(AccountConfig.RES_ID_STRING_SWITCH_ACCOUNT_SUCCESS));
												dispatchAccountChanged();
											}
										});
										thread.start();
									}
								});
							}
						});
						
						return;
					}
	
					//天翼帐号登录
					if(!doLoginByTY(thirdId, accessToken, refreshToken, null)) {
						dispatchLoginComplete(false, this_.getString(AccountConfig.RES_ID_STRING_SWITCH_ACCOUNT_FAIL));
						return;
					}
					
					UserManager.getInstance(this_).setCurrentAccessToken(accessToken);
					PreferencesUtil.getInstance(this_).setIsLogin(true);
	
					dispatchLoginComplete(true, this_.getString(AccountConfig.RES_ID_STRING_SWITCH_ACCOUNT_SUCCESS));
					dispatchAccountChanged();
					
				} catch (Exception e) {
					e.printStackTrace();
					dispatchLoginComplete(false, this_.getString(AccountConfig.RES_ID_STRING_SWITCH_ACCOUNT_FAIL));
				}
			}
		});
		thread.start();
		return thread;
	}
	
	/**
	 * 必须运行在非主线程
	 * @param thirdId
	 * @param accessToken
	 * @param refreshToken
	 * @param leyueId
	 * @return
	 */
	private boolean doLoginByTY(String thirdId, String accessToken, String refreshToken, String leyueId){
		
		boolean result = false;
		
		try {
			UserThridInfo bindedLeyueInfo = ApiProcess4Leyue.getInstance(this_).recordThird(thirdId, accessToken, refreshToken, leyueId);
			
			if(bindedLeyueInfo != null) {
				
				//获取天翼帐号信息
				TianYiUserInfo userInfo = ApiProcess4TianYi.getInstance(this_).queryUserInfo(accessToken);
				if(userInfo!=null){
					userInfo.setAccessToken(accessToken);
					userInfo.setUserId(thirdId);
					userInfo.setLeyueUserId(bindedLeyueInfo.getUserId() + "");
					userInfo.setRefreshToken(refreshToken);
					TianYiUserInfoDB.getInstance(MyAndroidApplication.getInstance().getApplicationContext()).setTianYiUserInfo(userInfo);
					
					if(!bindedLeyueInfo.isUpdated) {	//如果未更新乐阅帐号信息
						try{
							ApiProcess4Leyue.getInstance(this_).updateUserInfo(bindedLeyueInfo.getUserId()+"", userInfo.getNickName(), thirdId, thirdId,
																		null, null, null, null, null, null, null);
						}catch(GsonResultException ge){}
					}
					
					UserInfoLeyue leUserInfo = new UserInfoLeyue();
					leUserInfo.setUserId(bindedLeyueInfo.getUserId() + "");
					setUserInfo(leUserInfo);
					
					//获取乐阅帐号信息
					requestUserInfo(bindedLeyueInfo.getUserId());
					
					result = true;
				}
			}
		}catch(GsonResultException ge) {
			ge.printStackTrace();
		}
		return result;
	}
	
	/**
	 * sina或qq帐号登录
	 * @param params
	 * @return
	 */
	public ITerminableThread loginBySinaOrQQ(final Object...params) {
		ITerminableThread thread = ThreadFactory.createTerminableThread(new Runnable() {
			
			@Override
			public void run() {
				
				RegisterInfo info = null;
				
				try {
					switch (((Integer)params[0]).intValue()) {
					case ThirdPartLoginConfig.TYPE_QQ:
						info = ApiProcess4Leyue.getInstance(this_).regist((String)params[1], (String)params[1], (String)params[2], ApiConfig.QQ_REGISTER);
						break;
	
					case ThirdPartLoginConfig.TYPE_SINA:
						info = ApiProcess4Leyue.getInstance(this_).regist((String)params[1], (String)params[1], (String)params[2], ApiConfig.SINA_REGISTER);
						break;
					}
					
					if(info != null) {
						
						PreferencesUtil.getInstance(this_).setUserId(info.getUserId());
						PreferencesUtil.getInstance(this_).setUserName((String)params[1]);
						PreferencesUtil.getInstance(this_).setUserPSW((String)params[1]);
						PreferencesUtil.getInstance(this_).setUserNickName((String)params[2]);
						PreferencesUtil.getInstance(this_).setIsLogin(true);
						
						requestUserInfo(info.getUserId());
						
						dispatchLoginComplete(true, this_.getString(AccountConfig.RES_ID_STRING_SWITCH_ACCOUNT_SUCCESS));
						dispatchAccountChanged();
					}else {
						dispatchLoginComplete(false, this_.getString(AccountConfig.RES_ID_STRING_SWITCH_ACCOUNT_FAIL));
					}
					
				}catch(GsonResultException ge) {}
			}
		});
		thread.start();
		return thread;
	}
	
	/**
	 * 更新帐号信息
	 * @param userInfoLeyue
	 * @param newPsw
	 * @return
	 */
	public ITerminableThread updateUserInfo(final UserInfoLeyue userInfoLeyue, final String newPsw){
//		leYueBaseAccount.updateUserInfo(userInfoLeyue);
		ITerminableThread thread = ThreadFactory.createTerminableThread(new Runnable() {
			
			@Override
			public void run() {
				try{
					boolean success = ApiProcess4Leyue.getInstance(this_).updateUserInfo(userInfoLeyue.getUserId() + "", userInfoLeyue.getNickName() + "", 
																null, userInfoLeyue.getPassword() + "", newPsw + "",
																userInfoLeyue.getMobile() + "", userInfoLeyue.getEmail() + "", 
																userInfoLeyue.getSex() + "", userInfoLeyue.getBirthday() + "", userInfoLeyue.getAccount() + "", userInfoLeyue.getSignature());
					if(success) {
						userInfoLeyue.setPassword(TextUtils.isEmpty(newPsw) ? userInfoLeyue.getPassword() + "" : newPsw + "");
						saveUserInfo(userInfoLeyue);
						
						dispatchOnGetUserInfo(userInfoLeyue);
					}
					
				}catch(GsonResultException ge){}
			}
		});
		thread.start();
		return thread;
	}
	
	private UseAccount.IUserAccountResult mLeyueAccountLoginResult = new IUserAccountResult() {
		
		@Override
		public void loginSuccess() {
			Context context = MyAndroidApplication.getInstance().getBaseContext();
			if(mLoginInterface != null){
				mLoginInterface.hideLoading();
				mLoginInterface.loginSuccess();
			}
			
			dispatchLoginComplete(true, context.getString(AccountConfig.RES_ID_STRING_SWITCH_ACCOUNT_SUCCESS));
			
			dispatchAccountChanged();
		}
		
		@Override
		public void loginFail() {
//			unregisterAppLoginReceiver(MyAndroidApplication.getInstance().getBaseContext());
			if(mLoginInterface != null){
				mLoginInterface.hideLoading();
				mLoginInterface.loginFail();
			}
			
			dispatchLoginComplete(false, this_.getString(AccountConfig.RES_ID_STRING_SWITCH_ACCOUNT_FAIL));
		}

	};

	public void setmLoginInterface(ILoginInterface mLoginInterface) {
		this.mLoginInterface = mLoginInterface;
	}

	public ILoginInterface getmLoginInterface() {
		return mLoginInterface;
	}

	public static WXBaseAccount getwXBaseAccount() {
		return wXBaseAccount;
	}

	public static void setwXBaseAccount(WXBaseAccount wXBaseAccount) {
		AccountManager.wXBaseAccount = wXBaseAccount;
	}
}
