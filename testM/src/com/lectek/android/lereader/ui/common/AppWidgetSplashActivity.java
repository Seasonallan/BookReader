package com.lectek.android.lereader.ui.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.lectek.android.LYReader.R;
import com.lectek.android.app.AbsContextActivity;
import com.lectek.android.lereader.animation.OpenBookAnimManagement;
import com.lectek.android.lereader.net.response.NotifyCustomInfo;
import com.lectek.android.lereader.storage.dbase.util.NotifyCustomInfoDB;
import com.lectek.android.lereader.ui.specific.ActivityChannels;

/**
 * @description
	接收桌面插件，通知栏等外部start进来的Intent，进行登录判断、登录流程等一些逻辑之后在跳转到具体界面
	相当于一个透明的过渡界面
 * @author chendt
 * @date 2014-2-27
 * @Version 1.0
 */
public class AppWidgetSplashActivity extends AbsContextActivity {
	public static final String TYPE_VALUE_CUSTOM_NOTIFY = "type_value_custom_notify";
	public static final String EXTRA_NOTIFY_INFO = "extra_notify_info";
	
	
	/*-------------------------下面的是旧数据-------start------------------------*/
	public static final String EXTRA_ENTRANCE = "entrance";
	public static final String EXTRA_EXTERNAL_APP_PKG = "external_app_pkg";
	public static final String EXTRA_TYPE = "ExtraType";
	public static final String TYPE_VALUE_OPEN_BOOK = "TypeValueOpenBook";
	public static final String TYPE_VALUE_GOTO_BOOK_INFO = "TypeValueGotoBookInfo";
	public static final String TYPE_VALUE_GOTO_MSG = "TYPE_VALUE_GOTO_MSG";
	public static final String TYPE_VALUE_GOTO_SPECIAL_SUBJECT = "TYPE_VALUE_GOTO_SPECIAL_SUBJECT";
	public static final String TYPE_VALUE_GOTO_RECOMMEND = "TYPE_VALUE_GOTO_RECOMMEND";
	public static final String TYPE_VALUE_GOTO_AREA = "TYPE_VALUE_GOTO_AREA";
	public static final String TYPE_VALUE_GOTO_SOFTWARE = "type_value_goto_software";
	public static final String TYPE_VALUE_GOTO_DOWNLOAD = "TYPE_VALUE_GOTO_DOWNLOAD";
	
	public static final String ACTION_EXTERNAL_LAUNCH = "com.lectek.android.sfreader.action.main.EXTERNAL_LAUNCH";
	public static final String ACTION_EXTERNAL_GOTO_BOOKINFO = "com.lectek.android.sfreader.action.main.EXTERNAL_OPEN_BOOKINFO";
	public static final String ACTION_EXTERNAL_READ_BOOK = "com.lectek.android.sfreader.action.main.EXTERNAL_READ_BOOK";
	public static final String CONTENT_TYPE_VOICE = "contentTypeVoice";
	
	public static final String EXTRA_CONTENT_TYPE = "content_type";
	public static final String EXTRA_CONTENT_ID = "content_ID";
	public static final String EXTRA_CONTENT_NAME = "content_name";
	public static final String EXTRA_CHAPTER_ID = "chapter_ID";

	public static final String EXTRA_NAME_SPECIAL_SUBJECT_ID = "EXTRA_NAME_SPECIAL_SUBJECT_ID";
	public static final String EXTRA_NAME_SPECIAL_SUBJECT_TYPE = "EXTRA_NAME_SPECIAL_SUBJECT_TYPE";
	public static final String EXTRA_NAME_SPECIAL_SUBJECT_NAME = "EXTRA_NAME_SPECIAL_SUBJECT_NAME";
	public static final String EXTRA_NAME_IS_VOICE_READ = "EXTRA_NAME_IS_VOICE_READ";
	
	public static final String EXTRA_NAME_AREA_CATALOG_ID = "EXTRA_NAME_AREA_CATALOG_ID";
	public static final String EXTRA_NAME_AREA_CATALOG_NAME = "EXTRA_NAME_AREA_CATALOG_NAME";

	public static final String EXTRA_BOOK_INFO = "ExtraAppWidgetBookInfo";
	public static final String EXTRA_MESSAGE_ID = "extra_message_id";
	public static final String EXTRA_HAS_MSG = "EXTRA_HAS_MSG";
	public static final String VALUE_EXTRA_HAS_MSG_TRUE = "VALUE_EXTRA_HAS_TRUE";
	public static final String VALUE_EXTRA_HAS_MSG_FALSE = "VALUE_EXTRA_HAS_FALSE";
	/*-------------------------旧数据-------end------------------------*/
	private ImageView mImageView;
	private Handler mHandler = new Handler();
	private Activity this_ = this;
	private boolean isStop = false;
	private Dialog mWaittingDialog;
	boolean isLoginSuccess = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(0, 0);
		setContentView(R.layout.appwidget_handler_activity_lay);
		mImageView = (ImageView) findViewById(R.id.appwidget_handler_iv);
		isStop = false;
	}
	
	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
		if(!isFinishing()){
			finish();
		}
		isStop = true;
	}

	@Override
	public void onBackPressed() {
		
	}

	@Override
	protected void onDestroy() {
		OpenBookAnimManagement.getInstance().stopBookAnim();
		if(mWaittingDialog != null && mWaittingDialog.isShowing()){
			mWaittingDialog.dismiss();
		}
		super.onDestroy();
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		try{
			Intent intent = getIntent();
			if(intent == null){
				exit();
				return;
			}
			
			String actionString = intent.getAction();
//			String externalAppPkg = "";
//			if(!TextUtils.isEmpty(actionString)){
//				externalAppPkg = intent.getStringExtra(EXTRA_EXTERNAL_APP_PKG);
//			}
//			
//			String entrance = intent.getStringExtra(EXTRA_ENTRANCE);
			if(TYPE_VALUE_CUSTOM_NOTIFY.equalsIgnoreCase(actionString)){
				NotifyCustomInfo info = (NotifyCustomInfo) intent.getSerializableExtra(EXTRA_NOTIFY_INFO);
				//设置为已读
				NotifyCustomInfoDB.getInstance().updateMsgStatus(info.msgId, true);
				Intent notifyIntent = ActivityChannels.getNotifyInfoIntent(this, info);
				startActivity(notifyIntent);
				finish();
			} else {
				exit();
				return;
			}
//			if(ACTION_EXTERNAL_GOTO_BOOKINFO.equalsIgnoreCase(actionString)){
//				if(TextUtils.isEmpty(entrance)){
//					PathRecordUtil.getInstance().createPath(PathRecordUtil.TAG_PACKAGE_NAME + externalAppPkg);
//				}else{
//					PathRecordUtil.getInstance().createPath(entrance);
//					PathManager.getInstance().setPageRecord(entrance);
//				}
//				onThridPartyGotoBookInfo(intent);
//			} else if(ACTION_EXTERNAL_READ_BOOK.equalsIgnoreCase(actionString)){
//				if(TextUtils.isEmpty(entrance)){
//					PathRecordUtil.getInstance().createPath(PathRecordUtil.TAG_PACKAGE_NAME + externalAppPkg);
//				}else{
//					PathRecordUtil.getInstance().createPath(entrance);
//					PathManager.getInstance().setPageRecord(entrance);
//				}
//				onThridPartyOpenBook(intent);
//			} else if(ACTION_EXTERNAL_LAUNCH.equalsIgnoreCase(actionString)) {
//				onGotoSoftware(intent);
//			} else{
//				String type = intent.getStringExtra(EXTRA_TYPE);
//				AppWidgetBookInfo bookInfo = (AppWidgetBookInfo) intent.getSerializableExtra(EXTRA_BOOK_INFO);
//				if (TYPE_VALUE_OPEN_BOOK.equals(type)) {
//					if (bookInfo == null) {
//						exit();
//						return;
//					}
//					if(TextUtils.isEmpty(entrance)){
//						PathRecordUtil.getInstance().createPath(PathRecordUtil.TAG_WIDGET);
//					}else{
//						PathRecordUtil.getInstance().createPath(entrance);
//						PathManager.getInstance().setPageRecord(entrance);
//					}
//					onGotoReadBook(intent, bookInfo, false);
//				} else if (TYPE_VALUE_GOTO_BOOK_INFO.equals(type)) {
//					if (bookInfo == null) {
//						exit();
//						return;
//					}
//					if(TextUtils.isEmpty(entrance)){
//						PathRecordUtil.getInstance().createPath(PathRecordUtil.TAG_WIDGET);
//					}else{
//						PathRecordUtil.getInstance().createPath(entrance);
//						PathManager.getInstance().setPageRecord(entrance);
//					}
//					onGotoBookInfo(intent, bookInfo);
//				} else if (TYPE_VALUE_GOTO_MSG.equals(type)) {
//					onGotoMessage(intent);
//				} else if (TYPE_VALUE_GOTO_SPECIAL_SUBJECT.equals(type)) {
//					if(TextUtils.isEmpty(entrance)){
//						PathRecordUtil.getInstance().createPath(PathRecordUtil.TAG_WIDGET);
//					}else{
//						PathRecordUtil.getInstance().createPath(entrance);
//						PathManager.getInstance().setPageRecord(entrance);
//					}
//					onGotoSpecialSubject(intent);
//				} else if (TYPE_VALUE_GOTO_AREA.equals(type)) {
//					if(TextUtils.isEmpty(entrance)){
//						PathRecordUtil.getInstance().createPath(PathRecordUtil.TAG_WIDGET);
//					}else{
//						PathRecordUtil.getInstance().createPath(entrance);
//						PathManager.getInstance().setPageRecord(entrance);
//					}
//					onGotoArea(intent);
//				} else if (TYPE_VALUE_GOTO_RECOMMEND.equals(type)) {
//					onGotoRecommend(intent);
//				} else if (TYPE_VALUE_GOTO_SOFTWARE.equals(type)) {
//					onGotoSoftware(intent);
//				} else if (TYPE_VALUE_GOTO_DOWNLOAD.equals(type)) {
//					onGotoDownload(intent);
//				} else {
//					exit();
//					return;
//				}
//			}
		}catch (Exception e) {
			exit();
		}
	}

	private void exit(){
		setIntent(null);
		finish();
	}
	
	// TODO:----------@Deprecated--------------
//	private void onThridPartyGotoBookInfo(Intent intent) {
//		String contentType = intent.getStringExtra(EXTRA_CONTENT_TYPE);
//		String contentId = intent.getStringExtra(EXTRA_CONTENT_ID);
//		String contentName = intent.getStringExtra(EXTRA_CONTENT_NAME);
//		
//		if(TextUtils.isEmpty(contentId) || TextUtils.isEmpty(contentName)) {
//			exit();
//		}
//		
//		ContentInfo contentInfo = new ContentInfo();
//		contentInfo.contentType = CONTENT_TYPE_VOICE.equalsIgnoreCase(contentType)?ContentInfo.CONTENT_TYPE_VOICE:"";
//		contentInfo.contentID = contentId;
//		contentInfo.contentName = contentName;
//		AppWidgetBookInfo appWidgetBookInfo = new AppWidgetBookInfo(contentInfo);
//		onGotoBookInfo(intent, appWidgetBookInfo);
//	}
//	
//	private void onThridPartyOpenBook(Intent intent) {
//		String contentType = intent.getStringExtra(EXTRA_CONTENT_TYPE);
//		String contentId = intent.getStringExtra(EXTRA_CONTENT_ID);
//		String contentName = intent.getStringExtra(EXTRA_CONTENT_NAME);
//		String chapterID = intent.getStringExtra(EXTRA_CHAPTER_ID);
//		
//		if(TextUtils.isEmpty(contentType) || TextUtils.isEmpty(contentId)){
//			exit();
//		}
//		
//		ContentInfo contentInfo = new ContentInfo();
//		contentInfo.contentType = contentType;
//		contentInfo.contentID = contentId;
//		contentInfo.contentName = contentName;
//		contentInfo.chapterID = chapterID;
//		
//		AppWidgetBookInfo appWidgetBookInfo = new AppWidgetBookInfo(contentInfo);
//		onGotoReadBook(intent, appWidgetBookInfo,true);
//	}
//	
//	protected void onGotoReadBook(final Intent intent,final AppWidgetBookInfo appWidgetBookInfo, final boolean isThirdParty){
//		PostLoginRunnable postLoginRunnable = new PostLoginRunnable() {			
//			@Override
//			public void run() {
//				final ContentInfo contentInfo = appWidgetBookInfo.getContentInfo();
//				//跳转有声
//				if(ContentInfo.CONTENT_TYPE_VOICE.equals(contentInfo.contentType)){
//					gotoVoiceRead(contentInfo);
//				}else{//跳转图书
//					gotoReadBook(contentInfo);
//				}
//			}
//		};
//		if(!ApnUtil.isNetAvailable(this)){
//			postLoginRunnable.run();
//		}else{
//			onLogin(postLoginRunnable);
//		}
//	}
//	
//	private void gotoReadBook(ContentInfo contentInfo){
//		if (!ContentInfo.CONTENT_TYPE_SERIAL.equals(contentInfo.contentType)) {// 如果不是连载
//			if(openLocalBook(contentInfo)){
//				return;
//			}
//		}
//		openOnlineBook(contentInfo);
//	}
//	/**
//	 * 跳转有声阅读播放界面
//	 * @param contentInfo
//	 */
//	private void gotoVoiceRead(ContentInfo contentInfo){
//		boolean isOnlineRead = true;
//		String [] chapterInfo = VoicePlayerInterfaceControl.getChapterInfo(contentInfo.chapterID);
//		if(chapterInfo != null){
//			PlayInfo localPlayInfo = DownloadPresenter.loadFinishAudioPlayInfo(chapterInfo[1]);
//			if(localPlayInfo != null){
//				URI uri = URI.create(localPlayInfo.getAudioPlayInfo().getLinkurl());
//				if(!ContentResolver.SCHEME_FILE.equals(uri.getScheme()) 
//						|| !new File(uri).exists()){
//					DownloadPresenter.deleteVoiceByAudioId(chapterInfo[1]);
//				}else{
//					isOnlineRead = false;
//				}
//			}
//		}
//		if(!ApnUtil.isNetAvailable(this) && isOnlineRead){
//			ToastUtil.showToast(this, R.string.appwidget_not_net_work_read_book);
//			exit();
//			return;
//		}
//		ActivityChannels.gotoVoicePlayer(this_, contentInfo.contentID);
//		finish();
//	}
//	
//	private void setOnViewShowRunnable(View view ,final Runnable run){
//		Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
//		animation.setDuration(0);
//		animation.setAnimationListener(new AnimationListener() {
//			
//			@Override
//			public void onAnimationStart(Animation animation) {}
//			
//			@Override
//			public void onAnimationRepeat(Animation animation) {}
//			
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				if(run != null){
//					mHandler.post(run);
//				}
//			}
//		});
//		view.startAnimation(animation);
//	}
//	
//	private boolean openLocalBook(ContentInfo contentInfo){
//		return ReadBookPresenter.readLocalBook(contentInfo.contentID, new ReadBookCallBack() {
//			@Override
//			public void onFail() {
//				
//			}
//
//			@Override
//			public void onSucceed(Book book) {
//				onOpenBook(this_, book, null, true);
//			}
//		});
//	}
//	
//	protected void onOpenBook(final Context context, final Book book,final Bookmark bookmark,boolean isShowAnim){
//		//屏蔽不支持动画机型
//		if(ClientInfoUtil.ORDER_MODEL_ZY_N9.equals(Build.MODEL)){
//			isShowAnim = false;
//		}
//		if(isShowAnim){
//			OpenBookAnimManagement.getInstance().starOpenBookAnim(new PreRunnable() {
//				
//				@Override
//				public boolean run(){
//					if(!this_.isFinishing()){
//						boolean isSucceed = startBookActivity(context, book, bookmark);
//						finish();
//						return isSucceed;
//					}
//					return true;
//				}
//			});
//		}else{
//			startBookActivity(context, book, bookmark);
//			finish();
//		}
//	}
//	
//	private boolean startBookActivity(final Context context, final Book book,final Bookmark bookmark){
//		Intent intent = BaseReaderActivity.getReaderIntent(context, book, bookmark);
//		if(intent == null){
//			return false;
//		}
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		startActivity(intent);
//		return true;
//	}
//	
//	private void openOnlineBook(ContentInfo contentInfo){
//		if(!ApnUtil.isNetAvailable(this)){
//			ToastUtil.showToast(this, R.string.appwidget_not_net_work_read_book);
//			exit();
//			return;
//		}
//		ReadBookPresenter.readOnlineBook(contentInfo, new ReadBookCallBack() {
//			
//			@Override
//			public void onSucceed(Book book) {
//				onOpenBook(this_,book, null, true);
//			}
//			
//			@Override
//			public void onFail() {
//				exit();
//				return;
//			}
//		});
//	}
//	
//	protected void onGotoDownload (Intent intent){
//		ComponentName topActivity = ActivityTaskUtil.getTopActivity(this_, AppWidgetSplashActivity.class);
//		if(topActivity != null && topActivity.getPackageName().equals("com.alipay.android.app")){
//			Intent newIntent = ActivityChannels.getSplashActivityIntent(this);
//			superStartActivity(newIntent);
//			finish();
//			return;
//		}
//		ActivityManage.finishActivity(ActivityChannels.getVoicePlayerActivity());
//		Intent newIntent = ActivityChannels.getMainActivityDownloadIntent(this);
//		superStartActivity(newIntent);
//		finish();
//	}
//	
//	protected void onGotoSoftware (Intent intent){
//		Intent newIntent = new Intent();
//		ComponentName topActivity = ActivityTaskUtil.getTopActivity(this_, AppWidgetSplashActivity.class);
//		if(topActivity == null || topActivity.getPackageName().equals("com.alipay.android.app")){
//			newIntent = ActivityChannels.getSplashActivityIntent(this);
//		}else{
//			newIntent.setComponent(topActivity);
//			newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//		}
//		superStartActivity(newIntent);
//		finish();
//	}
//	
//	protected void onGotoRecommend(Intent intent){
//		Intent newIntent = null;
//		if(!ActivityTaskUtil.isBaseActivity(this,ActivityChannels.getMainActivityClass())){
//			newIntent = ActivityChannels.getSplashActivityIntent(this);
//			newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		}else{
//			newIntent = ActivityChannels.getMainActivityBookCityIntent(this);
//		}
//		startActivity(newIntent);
//		finish();
//	}
//	
//	@Override
//	public void startActivity(Intent intent) {
//		if(superStartActivity(intent)){
//			ActivityManage.finishBottomActivitys(ActivityChannels.getMainActivityClass());
//		}
//	}
//	
//	public boolean superStartActivity(Intent intent){
//		if(isFinishing()){
//			return false;
//		}
//		super.startActivity(intent);
//		return true;
//	}
//	
//	@Override
//	public boolean isFinishing() {
//		return super.isFinishing() || isStop;
//	}
//
//	protected void onGotoArea(final Intent intent){
//		if(!ApnUtil.isNetAvailable(this)){
//			ToastUtil.showToast(this, R.string.appwidget_not_net_work_book_info);
//			exit();
//			return;
//		}
//		PostLoginRunnable postLoginRunnable = new PostLoginRunnable() {
//			@Override
//			public void run() {
//				CatalogInfo info = new CatalogInfo();
//				info.catalogID = intent.getStringExtra(EXTRA_NAME_AREA_CATALOG_ID);
//				info.catalogName = intent.getStringExtra(EXTRA_NAME_AREA_CATALOG_NAME);
//				Intent areaIntent = ActivityChannels.getAreaContentActivityIntent(this_, info, intent.getBooleanExtra(EXTRA_NAME_IS_VOICE_READ, false));
//				areaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				areaIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//				areaIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(areaIntent);
//				finish();
//			}
//		};
//		onLogin(postLoginRunnable);
//	}
//	
//	protected void onGotoSpecialSubject(final Intent intent){
//		if(!ApnUtil.isNetAvailable(this)){
//			ToastUtil.showToast(this, R.string.appwidget_not_net_work_book_info);
//			exit();
//			return;
//		}
//		PostLoginRunnable postLoginRunnable = new PostLoginRunnable() {
//			
//			@Override
//			public void run() {
//				Intent subjectIntent = ActivityChannels.getSpecialSubjectActivityIntent(this_
//						, intent.getStringExtra(EXTRA_NAME_SPECIAL_SUBJECT_ID)
//						, intent.getStringExtra(EXTRA_NAME_SPECIAL_SUBJECT_TYPE)
//						, intent.getStringExtra(EXTRA_NAME_SPECIAL_SUBJECT_NAME)
//						, intent.getBooleanExtra(EXTRA_NAME_IS_VOICE_READ,false));
//				subjectIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				subjectIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//				subjectIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(subjectIntent);
//				finish();
//			}
//		};
//		onLogin(postLoginRunnable);
//	}
//	
//	protected void onGotoMessage(Intent intent){
//		String hasMsg = intent.getStringExtra(EXTRA_HAS_MSG);
//		final String msgId = intent.getStringExtra(EXTRA_MESSAGE_ID);
//		if(!VALUE_EXTRA_HAS_MSG_TRUE.equals(hasMsg)){
//			ToastUtil.showToast(this, R.string.appwidget_not_notice);
//			exit();
//			return;
//		}
//		if(!ApnUtil.isNetAvailable(this)){
//			ToastUtil.showToast(this, R.string.appwidget_not_net_work_msg);
//			exit();
//			return;
//		}
//		PostLoginRunnable postLoginRunnable = new PostLoginRunnable() {
//			
//			@Override
//			public void run() {
//				Intent newIntent = ActivityChannels.getNoticeActivityIntent(this_);
//				if(!ActivityTaskUtil.isPackageTaskStart(this_, AppWidgetSplashActivity.class)){
//					newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				}
//				newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				newIntent.putExtra(EXTRA_MESSAGE_ID, msgId);
//				startActivity(newIntent);
//				finish();
//			}
//		};
//		onLogin(postLoginRunnable);
//	}
//	
//	protected void onGotoBookInfo(final Intent intent,final AppWidgetBookInfo appWidgetBookInfo){
//		if(!ApnUtil.isNetAvailable(this)){
//			ToastUtil.showToast(this, R.string.appwidget_not_net_work_book_info);
//			exit();
//			return;
//		}
//		PostLoginRunnable postLoginRunnable = new PostLoginRunnable() {
//			@Override
//			public void run() {
//				ContentInfo contentInfo = appWidgetBookInfo.getContentInfo();
//				Intent bookInfoIntent = null;
//				if(ContentInfo.CONTENT_TYPE_VOICE.equals(contentInfo.contentType)){
//					bookInfoIntent = ActivityChannels.getVoiceBookInfoActivityIntent(this_, contentInfo.contentID, contentInfo.contentName);
//				}else{
//					bookInfoIntent = ActivityChannels.getBookInfoActivityIntent(this_, contentInfo.contentID, contentInfo.contentName);
//				}
//				bookInfoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				bookInfoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//				bookInfoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(bookInfoIntent);
//				finish();
//			}
//		};
//		onLogin(postLoginRunnable);
//	}
//	
//	
//	protected void onLogin(final PostLoginRunnable postLoginRunnable){
//		if(ActivityTaskUtil.isBaseActivity(this_, ActivityChannels.getMainActivityClass())){
//			if(postLoginRunnable != null){
//				postLoginRunnable.run();
//			}
//			return;
//		}
//		if(this_.isFinishing()){
//			return;
//		}
//		ClientInfoUtil.setUnConnectStatus(this);
//		if(ApnUtil.isConnected(getBaseContext())){
//			ClientInfoUtil.setConnect(this, true);
//			LoginManage.getInstance().startLogin();
//		}else{
//			ClientInfoUtil.setConnect(this, false);
//		}
//		if(postLoginRunnable != null){
//			postLoginRunnable.run();
//		}
//	}
//	
//	public static class AppWidgetBookInfo implements Serializable{
//		private static final long serialVersionUID = 8217652075607650392L;
//		private String contentName;
//		private String contentID;
//		private int position;
//		private String chapterID;
//		private String logoUrl;
//		private String contentType;
//		public AppWidgetBookInfo(ContentInfo contentInfo){
//			setContentInfo(contentInfo);
//		}
//
//		public ContentInfo getContentInfo() {
//			ContentInfo contentInfo = new ContentInfo();
//			contentInfo.contentName = contentName;
//			contentInfo.contentID = contentID;
//			contentInfo.position = position;
//			contentInfo.chapterID = chapterID;
//			contentInfo.logoUrl = logoUrl;
//			contentInfo.contentType = contentType;
//			return contentInfo;
//		}
//
//		public void setContentInfo(ContentInfo contentInfo) {
//			contentName = contentInfo.contentName;
//			contentID = contentInfo.contentID;
//			position = contentInfo.position;
//			chapterID = contentInfo.chapterID;
//			logoUrl = contentInfo.logoUrl;
//			contentType = contentInfo.contentType;
//		}
//		
//	}
//	
//	private interface PostLoginRunnable{
//		public void run();
//	}
}
