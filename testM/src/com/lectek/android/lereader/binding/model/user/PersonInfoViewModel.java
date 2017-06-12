package com.lectek.android.lereader.binding.model.user;

import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.ObjectObservable;
import gueei.binding.observables.StringObservable;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.lereader.account.AccountBindingManager;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.binding.model.BaseLoadDataViewModel;
import com.lectek.android.lereader.binding.model.account.AccountBindingInfoListModel;
import com.lectek.android.lereader.binding.model.account.SaveAccountBindingModel;
import com.lectek.android.lereader.binding.model.account.UnbindAccountModel;
import com.lectek.android.lereader.binding.model.feedback.FastDfsClient;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.response.AccountBindingInfo;
import com.lectek.android.lereader.net.response.CommonResultInfo;
import com.lectek.android.lereader.permanent.ApiConfig;
import com.lectek.android.lereader.permanent.ThirdPartLoginConfig;
import com.lectek.android.lereader.presenter.SyncPresenter;
import com.lectek.android.lereader.storage.cprovider.UriUtil;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.login_leyue.ThirdPartyLoginActivity;
import com.lectek.android.lereader.ui.login_leyue.UserLoginLeYueNewActivity;
import com.lectek.android.lereader.ui.person.PersonInfoActivity;
import com.lectek.android.lereader.ui.person.PersonInfoActivity.IPersonInfoAction;
import com.lectek.android.lereader.ui.person.PersonInfoNickNameActivity;
import com.lectek.android.lereader.ui.person.PersonInfoSexActivity;
import com.lectek.android.lereader.ui.person.PersonInfoSignatureActivity;
import com.lectek.android.lereader.ui.person.SelectSexActivity;
import com.lectek.android.lereader.ui.specific.EditUserPasswordActivity;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.ToastUtil;
import com.lectek.android.lereader.utils.DialogUtil.ConfirmListener;

public class PersonInfoViewModel extends BaseLoadDataViewModel {
	
	public static final int RESULT_CODE_MODIFIED = 65321;
	public static final int REQUEST_CODE = 12345;
	
	public final StringObservable bNickName = new StringObservable("");
	public final StringObservable bSignatureText = new StringObservable("");
	public final StringObservable bSex = new StringObservable("");
	public final StringObservable bTencentBindingText = new StringObservable("");
	public final StringObservable bSinaBindingText = new StringObservable("");
	public final StringObservable bTianYiBindingText = new StringObservable("");
	public final IntegerObservable bBindingTencentImage = new IntegerObservable(R.drawable.icon_bangding);
	public final IntegerObservable bBindingSinaImage = new IntegerObservable(R.drawable.icon_bangding);
	public final IntegerObservable bBindingTianYiImage = new IntegerObservable(R.drawable.icon_bangding);
	public final StringObservable bHeadImageUrl = new StringObservable("");
	
	private UserInfoModelLeyue mUserInfoModel;
	private UserInfoLeyue mDataSource;
	private AccountBindingInfoListModel mAccountBindingInfoListModel;
	private SaveAccountBindingModel mSaveAccountBindingModel;
	private UnbindAccountModel mUnbindAccountModel;
	private UploadHeadModel mUploadHeadModel;
	private PersonInfoEditModel mPersonInfoEditModel;
	
	
	private String mUserId;
	private String mAccount;
	private String mPassword;
	private PersonInfoActivity this_;
//	private SelectPicPopupWindow menuWindow;
	private String unBindSource;
	
	public final OnClickCommand bNickNameClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(this_, PersonInfoNickNameActivity.class);
			this_.startActivityForResult(intent, REQUEST_CODE);
		}
	}; 
	
	public final OnClickCommand bSignatureClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(this_, PersonInfoSignatureActivity.class);
			this_.startActivityForResult(intent, REQUEST_CODE);
		}
	};
	
	public final OnClickCommand bSexClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(this_, SelectSexActivity.class);
			intent.putExtra(SelectSexActivity.EXTRA_SEX, bSex.get());
			this_.startActivityForResult(intent, REQUEST_CODE);
			
		}
	}; 
	
	public final OnClickCommand bEditPasswordClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(this_, EditUserPasswordActivity.class);
			this_.startActivityForResult(intent, REQUEST_CODE);
			
		}
	};
	
	public final OnClickCommand bOnBindTencentClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			boolean isBindTencent = AccountBindingManager.getInstance().getHasBindingTencent();
			if(isBindTencent){
				//已绑定需要解绑
				String id = AccountBindingManager.getInstance().getmTencentAccountInfo().getId();
				unBindSource = ApiConfig.QQ_REGISTER;
				mUnbindAccountModel.start(id);
			}else{
				Intent intent = new Intent(getContext(), ThirdPartyLoginActivity.class);
				intent.putExtra(ThirdPartLoginConfig.EXTRA_TYPE, ThirdPartLoginConfig.TYPE_QQ);
				this_.startActivityForResult(intent, ThirdPartyLoginActivity.ACTIVITY_REQUEST_CODE);
			}
			
			
		}
	};
	
	public final OnClickCommand bOnBindSinaClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			boolean isBind = AccountBindingManager.getInstance().getHasBindingSina();
			if(isBind){
				//已绑定需要解绑
				String id = AccountBindingManager.getInstance().getmSinaAccountInfo().getId();
				unBindSource = ApiConfig.SINA_REGISTER;
				mUnbindAccountModel.start(id);
			}else{
				Intent intent = new Intent(getContext(), ThirdPartyLoginActivity.class);
				intent.putExtra(ThirdPartLoginConfig.EXTRA_TYPE, ThirdPartLoginConfig.TYPE_SINA);
				this_.startActivityForResult(intent, ThirdPartyLoginActivity.ACTIVITY_REQUEST_CODE);
			}
			
			
		}
	};
	
	public final OnClickCommand bOnBindTianYiClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			boolean isBind = AccountBindingManager.getInstance().getHasBindingTianYi();
			if(isBind){
				//已绑定需要解绑
				String id = AccountBindingManager.getInstance().getmTianYiAccountInfo().getId();
				unBindSource = ApiConfig.TIAN_YI_REGISTER;
				mUnbindAccountModel.start(id);
			}else{
				Intent intent = new Intent(getContext(), ThirdPartyLoginActivity.class);
				intent.putExtra(ThirdPartLoginConfig.EXTRA_TYPE, ThirdPartLoginConfig.TYPE_TY);
				this_.startActivityForResult(intent, ThirdPartyLoginActivity.ACTIVITY_REQUEST_CODE);
			}
			
			
		}
	};
	
	public final OnClickCommand bHeadPicClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
//			OnClickListener listenser = new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					
//					
//				}
//			};
//			SelectPicPopupWindow menuWindow = new SelectPicPopupWindow(this_, listenser);
//			menuWindow.showAtLocation(this_.findViewById(R.id.iv_head_pic), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
			Intent intent = new Intent(this_, com.lectek.android.lereader.ui.person.SelectPicPopupWindow.class);
			this_.startActivityForResult(intent, ThirdPartyLoginActivity.ACTIVITY_REQUEST_CODE);
			
		}
	};
	
	public final OnClickCommand bLogoutClick = new OnClickCommand(){
		@Override
		public void onClick(View v) {
			DialogUtil.commonConfirmDialog(this_, getContext().getString(R.string.main_menu_exit), R.string.logout_app_tip, new ConfirmListener(){

				@Override
				public void onClick(View v) {
					SyncPresenter.setSwitchTagAction(true);
					Intent intent = new Intent(this_, UserLoginLeYueNewActivity.class);
					this_.startActivityForResult(intent, UserInfoViewModelLeyue.REQUEST_CODE_ACCOUNT_CHANGE);
					
				}});
            
			
		}
	};
	
	
	
	public PersonInfoViewModel(PersonInfoActivity context, INetLoadView loadView) {
		super(context, loadView);
		this_ = context;
		mUserInfoModel = new UserInfoModelLeyue();
		mUserInfoModel.addCallBack(this);
		mAccountBindingInfoListModel = new AccountBindingInfoListModel();
		mAccountBindingInfoListModel.addCallBack(this);
		mSaveAccountBindingModel = new SaveAccountBindingModel();
		mSaveAccountBindingModel.addCallBack(this);
		mUnbindAccountModel = new UnbindAccountModel();
		mUnbindAccountModel.addCallBack(this);
		mUploadHeadModel = new UploadHeadModel();
		mUploadHeadModel.addCallBack(this);
		mPersonInfoEditModel = new PersonInfoEditModel();
		mPersonInfoEditModel.addCallBack(this);
		context.setmPersonInfoAction(mPersonInfoAction);
	}
	
	public void windowFocus(){
		refreshView();
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
			mUserInfoModel.start(mUserId, mAccount, mPassword);
			if(!AccountBindingManager.getInstance().getHasStartBindingListModel()){
				mAccountBindingInfoListModel.start(mUserId);
			}else{
				refreshBindingAccountView();
			}
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
		if(result != null){
			if(tag.equals(mUserInfoModel.getTag())){
				mDataSource = (UserInfoLeyue)result;
				refreshView();
			}else if(tag.equals(mAccountBindingInfoListModel.getTag())){
				AccountBindingManager.getInstance().setHasStartBindingListModel(true);
				ArrayList<AccountBindingInfo> list = null;
				if(result != null){
					list = (ArrayList<AccountBindingInfo>) result;
				}
				AccountBindingManager.getInstance().setBindingList(list);
				refreshBindingAccountView();
				hideLoadView();
			}else if(tag.equals(mSaveAccountBindingModel.getTag())){
				//绑定账号
				AccountBindingInfo object = (AccountBindingInfo) result;
				AccountBindingManager.getInstance().updateBindingAccount(object);
				refreshBindingAccountView();
			}else if(tag.equals(mUnbindAccountModel.getTag())){
				//解绑账号
				CommonResultInfo info = (CommonResultInfo) result;
				if(info.isSuccess()){
					AccountBindingManager.getInstance().onUnbindAccountBySource(unBindSource);
					refreshBindingAccountView();
				}
				
			}else if(tag.equals(mUploadHeadModel.getTag())){
				//上传头像结果
				UserInfoLeyue infoLeyue = (UserInfoLeyue) result;
				String photoId = infoLeyue.getPhotoId();
				String photoUrl = infoLeyue.getPhotoUrl();
				LogUtil.i("vickyLog", "photoUrl--- " + photoUrl);
				bHeadImageUrl.set(photoUrl);
				AccountManager.getInstance().getUserInfo().setPhotoId(photoId);
				AccountManager.getInstance().getUserInfo().setPhotoUrl(photoUrl);
			}else if(tag.equals(mPersonInfoEditModel.getTag())){
				//编辑个人信息结果
				CommonResultInfo info = (CommonResultInfo) result;
				boolean isSuccess = info.isSuccess();
				if(isSuccess){
					ToastUtil.showToast(getContext(), R.string.account_edit_info_success);
					//保存到本地
					UserInfoLeyue userInfo = AccountManager.getInstance().getUserInfo();
					userInfo.setNickName(bNickName.get());
					userInfo.setSignature(bSignatureText.get());
					userInfo.setSex(getSexIntegerValue(bSex.get()) + "");
					AccountManager.getInstance().updateUserInfo(userInfo, null);
					this_.setResult(RESULT_CODE_MODIFIED);
					finish();
				}else{
					ToastUtil.showToast(getContext(), R.string.account_edit_info_failure);
				}
			}
		}
		
		hideLoadView();
		return false;
	}
	
	private void refreshView(){
		if(mDataSource != null){
			String nick = mDataSource.getNickName();
			String phoneNum = mDataSource.getMobile();
			String email = mDataSource.getEmail();
			String birthday = mDataSource.getBirthday();
			String sex = mDataSource.getSex();
			String signature = mDataSource.getSignature();
			String headPhotoUrl = mDataSource.getPhotoUrl();
//			if(!TextUtils.isEmpty(nick)){
				bNickName.set(nick);
//			}
//			if(!TextUtils.isEmpty(signature)){
				bSignatureText.set(signature);
//			}
			bSex.set(getSexStringValue(sex));
			bHeadImageUrl.set(headPhotoUrl);
//			if(!TextUtils.isEmpty(headPhotoId)){
//				bHeadImageViewSource.set(R.drawable.icon_head_default);
//			}else{
//				
//			}
		}
	}
	
	private void refreshBindingAccountView(){
		boolean hasBindingSina = AccountBindingManager.getInstance().getHasBindingSina();
		boolean hasBindingTencent = AccountBindingManager.getInstance().getHasBindingTencent();
		boolean hasBindingTianYi = AccountBindingManager.getInstance().getHasBindingTianYi();
		String bindingText = getContext().getResources().getString(R.string.personal_account_manage_binding);
		String cancelBindingText = getContext().getResources().getString(R.string.personal_account_manage_cancel);
		if(hasBindingSina){
			bSinaBindingText.set(cancelBindingText);
			bBindingSinaImage.set(R.drawable.icon_qxbangding);
		}else{
			bSinaBindingText.set(bindingText);
			bBindingSinaImage.set(R.drawable.icon_bangding);
		}
		
		if(hasBindingTencent){
			bTencentBindingText.set(cancelBindingText);
			bBindingTencentImage.set(R.drawable.icon_qxbangding);
		}else{
			bTencentBindingText.set(bindingText);
			bBindingTencentImage.set(R.drawable.icon_bangding);
		}
		
		if(hasBindingTianYi){
			bTianYiBindingText.set(cancelBindingText);
			bBindingTianYiImage.set(R.drawable.icon_qxbangding);
		}else{
			bTianYiBindingText.set(bindingText);
			bBindingTianYiImage.set(R.drawable.icon_bangding);
		}
		
	}
	
	private String getSexStringValue(String sexValue){
		String sexStr = getString(R.string.person_info_sex_secert);
		if(!TextUtils.isEmpty(sexValue)){
			try {
				Integer sex = Integer.parseInt(sexValue);
				switch (sex) {
				case PersonInfoSexViewModel.PERSON_INFO_SEX_BOY_VALUE:
					sexStr = getString(R.string.person_info_sex_boy);
					break;
				case PersonInfoSexViewModel.PERSON_INFO_SEX_GIRL_VALUE:
					sexStr = getString(R.string.person_info_sex_girl);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		return sexStr;
	}
	
	private int getSexIntegerValue(String sexValue){
		int id = -1;
		if(sexValue.equals(getString(R.string.person_info_sex_boy))){
			id = PersonInfoSexViewModel.PERSON_INFO_SEX_BOY_VALUE;
		}else if(sexValue.equals(getString(R.string.person_info_sex_girl))){
			id = PersonInfoSexViewModel.PERSON_INFO_SEX_GIRL_VALUE;
		}else{
			id = PersonInfoSexViewModel.PERSON_INFO_SEX_SECRET_VALUE;
		}
		return id;
	}
	
	private boolean checkSaveNickNameInfo(){
		boolean checkResult = false;
        if (bNickName.get() == null){
            ToastUtil.showToast(getContext(), R.string.result_content_nickname_empty);
            return checkResult;
        }
		String nickName = bNickName.get().trim();
		if(TextUtils.isEmpty(nickName)){
			ToastUtil.showToast(getContext(), R.string.result_content_nickname_empty);
		}else if(nickName.length() > 16){
			ToastUtil.showToast(getContext(), R.string.dialog_person_nickname_hint);
		}else{
			checkResult = true;
		}
		return checkResult;
	}
	
	private boolean checkSaveSignatureInfo(){
		boolean checkResult = false;
		String signature = null;
		if(bSignatureText != null && bSignatureText.get() != null){
			signature =  bSignatureText.get().trim();
		}
		if(TextUtils.isEmpty(signature)){
			ToastUtil.showToast(getContext(), R.string.result_content_signature_empty);
		}else if(signature.length() > 16){
			ToastUtil.showToast(getContext(), R.string.dialog_person_nickname_hint);
		}else{
			checkResult = true;
		}
		return checkResult;
	}
	
	
	private IPersonInfoAction mPersonInfoAction = new IPersonInfoAction() {
		
		@Override
		public void refreshLocalUserInfo() {
			UserInfoLeyue account = AccountManager.getInstance().getUserInfo();
			mDataSource = account;
			refreshView();
		}

		@Override
		public void saveBindAccount(String account, String password, String source) {
			mSaveAccountBindingModel.start(mUserId, source, account, password);
			
		}

		@Override
		public void updateHeadImg(Uri data) {
//			Bitmap image;
//			try {
//				//这个方法是根据Uri获取Bitmap图片的静态方法
//				image = MediaStore.Images.Media.getBitmap(this_.getContentResolver(), data);
//				if (image != null) {
////					data.setImageBitmap(image);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			//开始上传头像
			String filePath = UriUtil.getFilePathFromUri(data, this_);
			String uid = mDataSource.getUserId();
			mUploadHeadModel.start(uid, filePath);
			
		}

		@Override
		public void onSaveInfoClick() {
			if(checkSaveNickNameInfo() && checkSaveSignatureInfo()){
				String nickName = bNickName.get().trim();
				String signature = bSignatureText.get().trim();
				String sexId = getSexIntegerValue(bSex.get().trim()) + "";
				mPersonInfoEditModel.start(nickName, signature, sexId);
			}
			
		}

		@Override
		public void onUpdateSex(String sex) {
			bSex.set(sex);
			
		}

		@Override
		public void updateHeadImg(String filePath) {
			String userId = "";
			if(mDataSource == null){
				userId = mDataSource.getUserId();
			}else{
				userId = PreferencesUtil.getInstance(getContext()).getUserId();
			}
			mUploadHeadModel.start(userId, filePath);
		}

		@Override
		public void reGetUserInfo() {
			mUserId = PreferencesUtil.getInstance(getContext()).getUserId();
			mAccount = PreferencesUtil.getInstance(getContext()).getUserName();
			mPassword = PreferencesUtil.getInstance(getContext()).getUserPSW();
			mUserInfoModel.start(mUserId, mAccount, mPassword);
		}
	};

}
