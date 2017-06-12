package com.lectek.android.lereader.ui.specific;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.lectek.android.LYReader.R;
import com.lectek.android.app.BaseContextActivity;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.share.entity.UmengShareInfo;
import com.lectek.android.lereader.lib.utils.FileUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.SubjectResultInfo;
import com.lectek.android.lereader.permanent.ShareConfig;
import com.lectek.android.lereader.share.ShareWeiXin;
import com.lectek.android.lereader.share.ShareYiXin;
import com.lectek.android.lereader.share.entity.MutilMediaInfo;
import com.lectek.android.lereader.share.util.UmengShareUtils;
import com.lectek.android.lereader.share.util.UmengShareUtils.YXHanlder;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.CoverImageLoader;
import com.lectek.android.lereader.utils.ToastUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.UMImage;

/**
 * 封面页面
 * 
 * @author gyz
 * 
 */
//public class CoverActivity extends BaseContextActivity implements ILoadDataCallBack, YXHanlder {
public class CoverActivity extends BaseContextActivity implements YXHanlder {
	private static final String Tag = CoverActivity.class.getSimpleName();
	
	private UmengShareUtils utils = null;
	private PopupWindow popupWindow;
	protected View view;

	private ImageSwitcher imageSwitcher;
	private ImageView iv_goMain;
	private ImageButton cover_share_ib;
	@SuppressWarnings("deprecation")
	private GestureDetector detector = new GestureDetector(
			new OnGestureListener() {

				@Override
				public boolean onSingleTapUp(MotionEvent e) {return false;}

				@Override
				public void onShowPress(MotionEvent e) {}

				@Override
				public boolean onScroll(MotionEvent e1, MotionEvent e2,
						float distanceX, float distanceY) {
					return false;
				}

				@Override
				public void onLongPress(MotionEvent e) {}

				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2,
						float velocityX, float velocityY) {
					float x1 = 0;
					float x2 = 0;
					float y1 = 0;
					float y2 = 0;
					try {
						x1 = e1.getX();
						x2 = e2.getX();
						y1 = e1.getY();
						y2 = e2.getY();
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (x1 - x2 > 50 && Math.abs(y1 - y2) < 100) {
						this_.startActivity(new Intent(this_,
								SlideActivityGroup.class));
						finish();
						overridePendingTransition(R.anim.cover_retain_anim, R.anim.cover_out_anim);
					}
					return true;
				}

				@Override
				public boolean onDown(MotionEvent e) {
					return false;
				}
			});

	int switchTime;// 进入下个图片所需时间
	int position = 0;// 图片位置
	private ArrayList<Uri> uris = new ArrayList<Uri>();// 图片uri的list
	private ArrayList<Integer> res = new ArrayList<Integer>();// 图片本地资源的list，第一次进入用到
	public static final int MSG_SWITCIMAGE = 1;
//	private BookCityCoverSubjectModelLeyue mBookCityRecommendModelLeyue;

//	Handler handler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			if (msg.what == MSG_SWITCIMAGE) {
//				switchImage();
//				removeMessages(MSG_SWITCIMAGE);
//				handler.sendEmptyMessageDelayed(MSG_SWITCIMAGE, 5000);
//			} 
//		}
//	};

	private boolean switchImage() {
		if(isFinishing()) {
			return false;
		}
		if(uris.size() > 0){
			imageSwitcher.setImageURI(uris.get(position));
			position++;
			if (position > uris.size() - 1) {
				position = 0;
			}
		}else{
			imageSwitcher.setImageResource(res.get(position));
			position++;
			if (position > res.size() - 1) {
				position = 0;
			}
		}
		return true;
	}

	 @Override
	 public boolean onTouchEvent(MotionEvent event) {
		 return detector.onTouchEvent(event);
	 }

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		view = LayoutInflater.from(this).inflate(R.layout.guider_layout, null);
		setContentView(view);
		init();
		// 加载封面数据,取缓存显示
		if (CoverImageLoader.checkIfFileFolderExist()) {// 如果有，那么加载缓存文件
			// 获取缓存uri
			uris = CoverImageLoader.getCacheUris();
		} else {// 如果没有就加载int资源
			res.add(R.drawable.cover_init_res1);
		}
//		if(handler != null){
//			handler.sendEmptyMessage(MSG_SWITCIMAGE);
//		}

		MyAndroidApplication.getHandler().post(mSwitchRunnable);
		
		//从网络上获取数据，缓存下次加载
//		mBookCityRecommendModelLeyue = new BookCityCoverSubjectModelLeyue();
//		mBookCityRecommendModelLeyue.addCallBack(this);
//		mBookCityRecommendModelLeyue.start();
		MyAndroidApplication.addBackgroundTask(mDownloadImageRunnable, true);

	}

	private void init() {

		iv_goMain = (ImageView) findViewById(R.id.iv_goMain);
		iv_goMain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				this_.startActivity(new Intent(this_, SlideActivityGroup.class));
				finish();
				overridePendingTransition(R.anim.cover_retain_anim, R.anim.cover_out_anim);
			}
		});
		imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
		imageSwitcher.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				ImageView imageView = new ImageView(this_);
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

				return imageView;
			}
		});
		cover_share_ib = (ImageButton) findViewById(R.id.cover_share_ib);
		cover_share_ib.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toShare();
			}
		});
		AlphaAnimation inanimation = new AlphaAnimation(0.1f, 1.0f);
		inanimation.setDuration(1000);
		imageSwitcher.setInAnimation(inanimation);
		AlphaAnimation outanimation = new AlphaAnimation(1.0f, 0.1f);
		outanimation.setDuration(1000);
		imageSwitcher.setOutAnimation(outanimation);
	}

	private Runnable mSwitchRunnable = new Runnable() {
		
		@Override
		public void run() {
			if(switchImage()) {
//			handler.sendEmptyMessageDelayed(MSG_SWITCIMAGE, 5000);
				MyAndroidApplication.getHandler().postDelayed(mSwitchRunnable, 5000);
			}
		}
	};
	
//	@Override
//	public boolean onStartFail(String tag, String state, Object... params) {
//		return false;
//	}
//
//	@Override
//	public boolean onPreLoad(String tag, Object... params) {
//		return false;
//	}
//
//	@Override
//	public boolean onFail(Exception e, String tag, Object... params) {
//
//		return false;
//	}

//	@Override
//	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
//			boolean isCancel, Object... params) {
//		if (tag.equals(mBookCityRecommendModelLeyue.getTag())) {
//			final ArrayList<String> list = new ArrayList<String>();
//			ArrayList<SubjectResultInfo> subjectInfoList = (ArrayList<SubjectResultInfo>) result;
//			if (subjectInfoList != null && subjectInfoList.size() > 0) {
//
//				for (int i = 0; i < subjectInfoList.size(); i++) {
//					String imgUri = subjectInfoList.get(i).getSubjectPic();
//					list.add(imgUri);
//				}
//				// 判断缓存文件夹有没有建起来
//				if (!CoverImageLoader.checkIfFileFolderExist()) {// 如果已经有了，那么
//					CoverImageLoader.createFileFolder();
//				} 
//				// 后台下载
//				downloadBackGround(list);
//			}
//		}
//		return false;
//	}

	private Runnable mDownloadImageRunnable = new Runnable() {
		
		@Override
		public void run() {
			try {
				ArrayList<SubjectResultInfo> bookCityClassify= ApiProcess4Leyue.getInstance(MyAndroidApplication.getInstance()).getBookCitySubjectInfo(7, 0, 10);
				
				if (bookCityClassify != null && bookCityClassify.size() > 0) {
					// 判断缓存文件夹有没有建起来
					FileUtil.deleteDir(CoverImageLoader.getCacheFolderName(), true);
					CoverImageLoader.createFileFolder();
					
					for (int i = 0; i < bookCityClassify.size(); i++) {
						CoverImageLoader.downloadCoverImage(bookCityClassify.get(i).getSubjectPic());
					}
				}
			}catch(GsonResultException e) {
				e.printStackTrace();
			}
			
		}
	};
	
//	private void downloadBackGround(final ArrayList<String> list) {
//		new AsyncTask<Void, Void, Void>() {
//			@Override
//			protected Void doInBackground(Void... params) {
//				for (int i = 0; i < list.size(); i++) {
//					CoverImageLoader.downloadCoverImage(list.get(i));
//				}
//				return null;
//			}
//
//			@Override
//			protected void onPreExecute() {
//				super.onPreExecute();
//			}
//
//			@Override
//			protected void onPostExecute(Void result) {
//				LogUtil.i("yyl", "后台下载完成");
//			}
//		}.execute();
//	}


	/**
	 * 分享功能
	 * 
	 * @param v
	 */

	public void toShare() {
		utils = new UmengShareUtils();
		utils.baseInit(this_);
		utils.setShareInfo(
				this_,
				new UmengShareInfo(getResources().getString(
						R.string.share_for_software), ""));
		utils.setMailSubjectTitle("软件分享");
		if (popupWindow == null) {
			popupWindow = utils.showPopupWindow(this, view, this, snsListener);
		} else {
			if (popupWindow.isShowing()) {
				popupWindow.dismiss();
			}
			UmengShareUtils.popWindowShow(view, popupWindow);
		}
	}

	private SnsPostListener snsListener = new SnsPostListener() {

		@Override
		public void onStart() {
		}

		@Override
		public void onComplete(SHARE_MEDIA arg0, int eCode, SocializeEntity arg2) {
			LogUtil.e("--- eCode--" + eCode);
			switch (arg0) {
			case QQ:
				if (eCode == ShareConfig.SNS_SUCEESS_CODE) {
					// mUserInfoViewModel.uploadShareInfo();
					ToastUtil.showToast(this_, "分享成功");
				} else {
					ToastUtil.showToast(this_, "分享失败");
				}
				break;
			case SINA:
				if (eCode == ShareConfig.SNS_SUCEESS_CODE) {
					// mUserInfoViewModel.uploadShareInfo();
					ToastUtil.showToast(this_, "分享成功");
				} else if (eCode == 5016) {
					ToastUtil.showToast(this_, "分享内容重复");
				} else {
					ToastUtil.showToast(this_, "分享失败");
				}
				break;
			default:
				break;
			}

		}
	};

	@Override
	public void handleForYiXin(int type) {
		ShareYiXin shareYiXin = new ShareYiXin(this_);
		if (shareYiXin.isYxInstall()) {
			// Bitmap bitmap =
			// CommonUtil.drawableToBitmap(this_.getResources().getDrawable(R.drawable.share_icon));
			// shareYiXin.sendTextWithBtimap(new
			// MutilMediaInfo("",getString(R.string.share_for_software),
			// "",type,UmengShareUtils.contentUrl),bitmap);
			shareYiXin.sendText(new MutilMediaInfo(this_.getResources()
					.getString(R.string.share_for_software), "", type));
		} else {
			Toast.makeText(this_, "您还没有安装易信！", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void handleForWeiXin(int type) {
		ShareWeiXin shareWeiXin = new ShareWeiXin(this_);
		if (shareWeiXin.isWxInstall()) {
			if (shareWeiXin.isSupportVersion()) {
				// Bitmap bitmap =
				// CommonUtil.drawableToBitmap(this_.getResources().getDrawable(R.drawable.share_icon));
				// shareWeiXin.sendTextWithBtimap(new MutilMediaInfo("",
				// "",getString(R.string.share_for_software),type,UmengShareUtils.contentUrl),bitmap);
				shareWeiXin.sendText(new MutilMediaInfo(this_.getResources()
						.getString(R.string.share_for_software), "", type));
			} else {
				Toast.makeText(this_, "请更新微信到最新版本！", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this_, "您还没有安装微信！", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void handleForQQ() {
		Bitmap bitmap = CommonUtil.drawableToBitmap(this_.getResources()
				.getDrawable(R.drawable.share_icon));
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
	public void saveSourceId() {
		UmengShareUtils.LAST_SHARE_SOURCEID = CommonUtil.getMyUUID(this_);
		UmengShareUtils.shareContext = CoverActivity.this;
	}
	
	@Override
	protected void onDestroy() {
//		handler.removeMessages(MSG_SWITCIMAGE);
		MyAndroidApplication.getHandler().removeCallbacks(mSwitchRunnable);
//		if(mBookCityRecommendModelLeyue != null)
//			mBookCityRecommendModelLeyue.release();
		super.onDestroy();
	}
}
