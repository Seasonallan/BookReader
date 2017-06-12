package com.lectek.android.lereader.ui.specific;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.user.ShareDialogViewModel;
import com.lectek.android.lereader.lib.share.entity.UmengShareInfo;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.share.ShareWeiXin;
import com.lectek.android.lereader.share.ShareYiXin;
import com.lectek.android.lereader.share.entity.MutilMediaInfo;
import com.lectek.android.lereader.share.util.UmengShareUtils;
import com.lectek.android.lereader.share.util.UmengShareUtils.YXHanlder;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.ToastUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;

public class ShareDialogActivity extends Activity implements YXHanlder {
	
	public static final String EXTRA_INTENTINFO = "extra_intentinfo";
	
	private ShareDialogActivity this_ = this;
	private PopupWindow popupWindow;
	private UmengShareUtils utils = null;
	private boolean isShareQZone = false;
	private boolean isGoBackOpt;
	private ContentInfoLeyue mContentInfo;

	private ShareDialogViewModel mShareDialogViewModel;
	
	public static void openActivity(Context context, ContentInfoLeyue contentInfo){
		Intent intent = new Intent(context, ShareDialogActivity.class);
		intent.putExtra(EXTRA_INTENTINFO, contentInfo);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_dialog_layout);
		Intent intent = getIntent();
		ContentInfoLeyue contentInfo = (ContentInfoLeyue)intent.getSerializableExtra(EXTRA_INTENTINFO);
		mContentInfo = contentInfo;
	}
	private boolean isFirstInit = true;
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (isFirstInit) {
			initShareDialog(findViewById(R.id.hide_btn));
			isFirstInit = false;
		}
	}
	
	private void initShareDialog(View v){
		if (mContentInfo!=null) {
			utils = new UmengShareUtils();
			utils.baseInit(this_);
			utils.setMailSubjectTitle("书籍分享");
			String imagePath = String.valueOf(mContentInfo.getCoverPath().hashCode());
			Bitmap bitmap = null;
			if (!TextUtils.isEmpty(imagePath)) {
				bitmap = CommonUtil.getImageInSdcard(imagePath);
			}
			utils.setShareInfo(this_, new UmengShareInfo(
					getString(R.string.share_for_book, mContentInfo.getBookName(),LeyueConst.WX_YYB_PATH+mContentInfo.getBookId()), ""),bitmap);
			if (popupWindow == null) {
				popupWindow = utils.showPopupWindow(this_, v,this_,snsListener);
			}else {
				if (popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
				UmengShareUtils.popWindowShow(v, popupWindow);
			}
			
			popupWindow.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss() {
					if (isGoBackOpt) {
						finish();
					}
				}
			});
		}else {
			ToastUtil.showToast(this_, "暂时无法分享，请重新进入该界面！");
		}
	}

	@Override
	public void handleForYiXin(int type) {
		ShareYiXin shareYiXin = new ShareYiXin(this_);
		if (shareYiXin.isYxInstall()) {
			if (!TextUtils.isEmpty(mContentInfo.getCoverPath())) {
				String imagePath = String.valueOf(mContentInfo.getCoverPath().hashCode());
				LogUtil.e("-----cover-localName=="+imagePath);
				shareYiXin.sendTextWithPic(new MutilMediaInfo("",getString(R.string.share_for_book_wx, mContentInfo.getBookName()), imagePath,type,LeyueConst.WX_YYB_PATH+mContentInfo.getBookId()));
			}else {
				shareYiXin.sendTextWithPic(new MutilMediaInfo("",getString(R.string.share_for_book_wx, mContentInfo.getBookName()), "",type,LeyueConst.WX_YYB_PATH+mContentInfo.getBookId()));
			}
		}else {
			Toast.makeText(this_, "你还没有安装易信！", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void handleForWeiXin(int type) {
		ShareWeiXin shareWeiXin = new ShareWeiXin(this_);
		if (shareWeiXin.isWxInstall()) {
			if (shareWeiXin.isSupportVersion()) {
				if (!TextUtils.isEmpty(mContentInfo.getCoverPath())) {
					String imagePath = String.valueOf(mContentInfo.getCoverPath().hashCode());
					switch (type) {
					case MutilMediaInfo.WX_FRIEND:
						shareWeiXin.sendTextWithPic(new MutilMediaInfo("",getString(R.string.share_for_book_wx, mContentInfo.getBookName()), imagePath,type,LeyueConst.WX_YYB_PATH+mContentInfo.getBookId()));
						break;
					case MutilMediaInfo.WX_FRIEND_ZONE:
						shareWeiXin.sendTextWithPic(new MutilMediaInfo(getString(R.string.share_for_book_wx, mContentInfo.getBookName()),"", imagePath,type,LeyueConst.WX_YYB_PATH+mContentInfo.getBookId()));
						break;
						
					default:
						break;
					}
				}else {
					shareWeiXin.sendTextWithPic(new MutilMediaInfo(getString(R.string.share_for_book_title),getString(R.string.share_for_book_wx, mContentInfo.getBookName()), "",type,LeyueConst.WX_YYB_PATH));
				}
			}else {
				Toast.makeText(this_, "请更新微信到最新版本！", Toast.LENGTH_SHORT).show();
			}
		}else {
			Toast.makeText(this_, "你还没有安装微信！", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void handleForQQ() {
		//友盟3.2 sdk 支持本地图片分享 —— 【QQ分享内容为音乐，视频的时候，其形式必须为url;图片支持url跟本地图片类型.】
				if (utils!=null && !TextUtils.isEmpty(mContentInfo.getCoverPath())) {
					String imagePath = String.valueOf(mContentInfo.getCoverPath().hashCode());
					Bitmap bitmap = null;
					if (!TextUtils.isEmpty(imagePath)) {
						bitmap = CommonUtil.getImageInSdcard(imagePath);
					}
					utils.setShareInfo(this_, new UmengShareInfo(
							getString(R.string.share_for_book, mContentInfo.getBookName(),LeyueConst.WX_YYB_PATH+mContentInfo.getBookId()), mContentInfo.getCoverPath()),bitmap);
				}
	}

	@Override
	public void handleForQQZONE() {
		//Qzone 使用自定义分享接口
				if (utils!=null && !TextUtils.isEmpty(mContentInfo.getCoverPath())) {
					utils.setShareInfo(this_, new UmengShareInfo(
							getString(R.string.share_for_book, mContentInfo.getBookName(),LeyueConst.WX_YYB_PATH+mContentInfo.getBookId()), mContentInfo.getCoverPath()),null);
				}
				utils.shareToQzone(this_);
				isShareQZone = true;
	}

	@Override
	public void handleForSMS() {
		//短信不带图片
				if (utils!=null) {
					utils.setShareInfo(this_, new UmengShareInfo(
							getString(R.string.share_for_book, mContentInfo.getBookName(),LeyueConst.WX_YYB_PATH+mContentInfo.getBookId()), ""),null);
				}
	}

	@Override
	public void saveSourceId() {
		UmengShareUtils.LAST_SHARE_SOURCEID = mContentInfo.getBookId();
	}
	
	private SnsPostListener snsListener = new SnsPostListener() {
		
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
			// TODO Auto-generated method stub
			
		}
	};
}
