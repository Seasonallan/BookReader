package com.lectek.android.lereader.binding.model.user;

import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.StringObservable;

import java.util.Date;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.binding.model.BaseLoadDataViewModel;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.utils.DateUtil;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.person.PersonInfoBirthdayActivity;
import com.lectek.android.lereader.utils.ToastUtil;

/**
 * 个人中心-修改生日日期ViewModel
 * 
 * @author yangwq
 * @date 2014年4月28日
 * @email 57890940@qq.com
 */
public class PersonInfoBirthdayViewModel extends BaseLoadDataViewModel {
	
	public static final int RESULT_CODE_MODIFIED = 65321;
	public static final int REQUEST_CODE = 12345;
	
	public final StringObservable bYearText = new StringObservable("");
	public final StringObservable bMonthText = new StringObservable("");
	public final StringObservable bAttributeTip = new StringObservable("");
	
	public BooleanObservable bClearBtnVisible = new BooleanObservable(false);
	
	private UserInfoModelLeyue mUserInfoModel;
	private UserInfoLeyue mDataSource;
	
	private UserAction mUserAction;
	
	private String mUserId;
	private String mAccount;
	private String mPassword;
	private PersonInfoBirthdayActivity this_;
	private DatePicker mDatePicker;
	private Date mBirthDate;
	
	public final OnClickCommand bSaveClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			showLoadView();
			final String date = DateUtil.getNowDayYMDHMS(mBirthDate);
			saveUserInfo(mUserId, date, mAccount, mPassword, mPassword, new ISaveUserInfoHandler() {
				
				@Override
				public void saveUserInfoSuccess() {
					ToastUtil.showLongToast(getContext(), R.string.save_success);
					if(mUserAction != null){
						mUserAction.modifyUserInfo();
						UserInfoLeyue userInfo = AccountManager.getInstance().getUserInfo();
						userInfo.setBirthday(date);
						AccountManager.getInstance().updateUserInfo(userInfo, null);
					}
				}
				
				@Override
				public void saveUserInfoFail() {
					ToastUtil.showLongToast(getContext(), R.string.fail_save);
				}
			});
		}
	};
	

	public PersonInfoBirthdayViewModel(PersonInfoBirthdayActivity context, INetLoadView loadView) {
		super(context, loadView);
		this_ = context;
		mUserInfoModel = new UserInfoModelLeyue();
		mUserInfoModel.addCallBack(this);
		bAttributeTip.set(getString(R.string.dialog_person_email));
	}
	
	public void setDatePicker(DatePicker datePicker){
		mDatePicker = datePicker;
	}
	
	public void windowFocus(){
//		refreshView();
	}

	@Override
	public void onStart() {
		super.onStart();
		boolean isLogin = PreferencesUtil.getInstance(getContext()).getIsLogin();
		if(isLogin){
			
			UserInfoLeyue userInfo = AccountManager.getInstance().getUserInfo();
			mUserId = PreferencesUtil.getInstance(getContext()).getUserId();
			mAccount = PreferencesUtil.getInstance(getContext()).getUserName();
			mPassword = PreferencesUtil.getInstance(getContext()).getUserPSW();
			if(userInfo == null){
				mUserInfoModel.start(mUserId, mAccount, mPassword);
				return;
			}
			mDataSource = userInfo;
			refreshView();
//			changeLoginState(true);
		}else{
//			changeLoginState(false);
		}
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		showLoadView();
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		hideLoadView();
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		// TODO Auto-generated method stub
		mDataSource = (UserInfoLeyue)result;
		refreshView();
		hideLoadView();
		return false;
	}
	
	private void refreshView(){
		if(mDataSource != null){
			String date = mDataSource.getBirthday();
			if(!TextUtils.isEmpty(date)){
				mBirthDate = DateUtil.parseDate(date);
				int year = mBirthDate.getYear();
				int monthOfYear = mBirthDate.getMonth();
				int dayOfMonth = mBirthDate.getDate();
				initDatePicker(year, monthOfYear, dayOfMonth);
			}else{
				//用户还没有设置出生日期，设置默认日期
				//TODO:
				initDate();
			}
		}
	}
	
	/**
	 * 初始化时间控件
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 */
	private void initDatePicker(int year, int monthOfYear, int dayOfMonth){
		int tempYear = year + 1900;	//year从1900开始计算
		mDatePicker.init(tempYear, monthOfYear, dayOfMonth, mDateChangedListener);
	}
	
	/**
	 * 初始化控件时间
	 * 若用户资料里没有birthday值，则初始化时间
	 */
	private void initDate(){
		int year = 1980;
		int monthOfYear = 6;
		int dayOfMonth = 1;
		mBirthDate = new Date();
		setBirthDate(year, monthOfYear, dayOfMonth);
		mDatePicker.init(year, monthOfYear, dayOfMonth, mDateChangedListener);;
	}
	
	private void saveUserInfo(final String userId, final String nickName, final String userName, final String password, final String newPassword, final ISaveUserInfoHandler saveUserInfoHandler){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean result = false;
				try {
					result = ApiProcess4Leyue.getInstance(getContext()).updateUserInfo(userId, null, userName, password, newPassword, null, null, null, nickName, null, null);
				} catch (GsonResultException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				final boolean r = result;
				if(saveUserInfoHandler != null){
					((Activity)getContext()).runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							hideLoadView();
							if(r){
								saveUserInfoHandler.saveUserInfoSuccess();
							}else{
								saveUserInfoHandler.saveUserInfoFail();
							}
						}
					});
				}
			}
		}).start();
	}
	
	private OnDateChangedListener mDateChangedListener = new OnDateChangedListener() {
		
		@Override
		public void onDateChanged(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			setBirthDate(year, monthOfYear, dayOfMonth);
			
		}
	};
	
	/**
	 * 设置时间实例
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 */
	private void setBirthDate(int year, int monthOfYear, int dayOfMonth){
		int tempYear = year - 1900;
		mBirthDate.setYear(tempYear);
		mBirthDate.setMonth(monthOfYear);
		mBirthDate.setDate(dayOfMonth);
	}
	
	public void setUserAction(UserAction userAction){
		mUserAction = userAction;
	}
	
	public interface UserAction{
		public void modifyUserInfo();
	}
	
	public static interface ISaveUserInfoHandler{
		void saveUserInfoSuccess();
		void saveUserInfoFail();
	}

}
