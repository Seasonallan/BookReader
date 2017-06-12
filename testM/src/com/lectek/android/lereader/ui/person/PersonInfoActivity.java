package com.lectek.android.lereader.ui.person;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.app.ITitleBar;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.binding.model.user.PersonInfoViewModel;
import com.lectek.android.lereader.lib.LeyueConst;
import com.lectek.android.lereader.lib.utils.BitmapUtil;
import com.lectek.android.lereader.permanent.ApiConfig;
import com.lectek.android.lereader.permanent.ThirdPartLoginConfig;
import com.lectek.android.lereader.storage.cprovider.UriUtil;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.ui.login_leyue.ThirdPartyLoginActivity;
import com.lectek.android.lereader.ui.login_leyue.UserLoginLeYueNewActivity;
import com.lectek.android.lereader.utils.Constants;
import com.lectek.android.lereader.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 账户管理
 * 
 * @author yangwq
 * @date 2014年7月11日
 * @email 57890940@qq.com
 */
public class PersonInfoActivity extends BaseActivity{
	
	private final String PAGE_NAME = "账户管理界面";
	
	private PersonInfoViewModel mPersonInfoViewModel;
	
	private IPersonInfoAction mPersonInfoAction;
	
	public static final int RESULT_CODE_MODIFIED = 65321;
	public static final int RESULT_CODE_LOG_OUT = RESULT_CODE_MODIFIED + 1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleContent((getString(R.string.my_info,"")));
		setRightButtonEnabled(true);
		setRightButton(getResources().getString(R.string.title_finish_sort_book_shelf), -1);
		
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_CODE_MODIFIED){
			//个人资料已被更新
			setResult(RESULT_CODE_MODIFIED);
			if(mPersonInfoAction != null){
				mPersonInfoAction.refreshLocalUserInfo();
			}
		}else if(resultCode == SelectPicPopupWindow.RESULT_CODE_SELECT_OK){
			if (data != null) {
				//取得返回的Uri,基本上选择照片的时候返回的是以Uri形式，但是在拍照中有得机子呢Uri是空的，所以要特别注意
				Uri mImageCaptureUri = data.getData();
				//返回的Uri不为空时，那么图片信息数据都会在Uri中获得。如果为空，那么我们就进行下面的方式获取
				if (mImageCaptureUri != null) {
					mPersonInfoAction.updateHeadImg(mImageCaptureUri);
				}

			}	
		}else if(resultCode == SelectPicPopupWindow.RESULT_CODE_TAKE_PHOTO_OK){
			if (data != null) {
				//取得返回的Uri,基本上选择照片的时候返回的是以Uri形式，但是在拍照中有得机子呢Uri是空的，所以要特别注意
				Uri mImageCaptureUri = data.getData();
				//返回的Uri不为空时，那么图片信息数据都会在Uri中获得。如果为空，那么我们就进行下面的方式获取
				if (mImageCaptureUri != null) {
					String filePath = UriUtil.getFilePathFromUri(mImageCaptureUri, this_);
					int rotate = BitmapUtil.readPictureDegree(filePath);
					BitmapFactory.Options opts = new BitmapFactory.Options();
					opts.inJustDecodeBounds = true;
					opts.inSampleSize = BitmapUtil.computeSampleSize(opts, -1, (int)124*165);//84-112
					opts.inJustDecodeBounds = false;
					Bitmap notRoateBitmap = BitmapFactory.decodeFile(filePath, opts);
					Bitmap rotateBitmap = BitmapUtil.rotaingImageView(rotate, notRoateBitmap);
					File file = new File(filePath);
					String fileName = "photo_" + file.getName();
					BitmapUtil.saveImage(Constants.BOOKS_TEMP_IMAGE, Constants.BOOKS_TEMP_IMAGE, fileName, rotateBitmap);
					String imgFilePath = Constants.BOOKS_TEMP_IMAGE + fileName;
					mPersonInfoAction.updateHeadImg(imgFilePath);
				}

			}	
		}else if(resultCode == SelectSexActivity.RESULT_CODE_SELECT_OK){
			if(mPersonInfoAction != null){
				String sex = data.getStringExtra(SelectSexActivity.EXTRA_SEX);
				mPersonInfoAction.onUpdateSex(sex);
			}
		}else if(resultCode == UserLoginLeYueNewActivity.RESULT_CODE_SUCCESS){
			if(mPersonInfoAction != null){
				mPersonInfoAction.reGetUserInfo();
			}
		}else{
			if(ThirdPartyLoginActivity.ACTIVITY_REQUEST_CODE == requestCode && data != null) {
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
							mPersonInfoAction.saveBindAccount(resultData.getString(ThirdPartLoginConfig.TYConfig.Extra_UserID),
									resultData.getString(ThirdPartLoginConfig.TYConfig.Extra_AccessToken), ApiConfig.TIAN_YI_REGISTER);
							
							break;
						case ThirdPartLoginConfig.TYPE_QQ:
							mPersonInfoAction.saveBindAccount(resultData.getString(ThirdPartLoginConfig.QQConfig.Extra_OpenID),
									resultData.getString(ThirdPartLoginConfig.QQConfig.Extra_OpenID), ApiConfig.QQ_REGISTER);
							break;
						case ThirdPartLoginConfig.TYPE_SINA:
							mPersonInfoAction.saveBindAccount(
									resultData.getString(ThirdPartLoginConfig.SinaConfig.Extra_UID),
									resultData.getString(ThirdPartLoginConfig.SinaConfig.Extra_UID), ApiConfig.SINA_REGISTER);
							break;
						}
					
					}
					
					break;
				default:
					break;
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public IPersonInfoAction getmPersonInfoAction() {
		return mPersonInfoAction;
	}

	public void setmPersonInfoAction(IPersonInfoAction mPersonInfoAction) {
		this.mPersonInfoAction = mPersonInfoAction;
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mPersonInfoViewModel = new PersonInfoViewModel(this, this);
		mPersonInfoViewModel.onStart();
		return bindView(R.layout.personal_info_layout, mPersonInfoViewModel);
	}
	
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
	}
	
	
	
	public interface IPersonInfoAction{
		public void refreshLocalUserInfo();
		public void saveBindAccount(String account, String password, String source);
		public void updateHeadImg(Uri data);
		public void updateHeadImg(String filePath);
		public void onSaveInfoClick();
		public void onUpdateSex(String sex);
		public void reGetUserInfo();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == ITitleBar.MENU_ITEM_ID_RIGHT_BUTTON){
			//点击右键保存
			if(mPersonInfoAction != null){
				mPersonInfoAction.onSaveInfoClick();
			}
		}
		return super.onOptionsItemSelected(item);
	}
	

}
