package com.lectek.android.lereader.ui.specific;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug.HierarchyTraceType;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lectek.android.LYReader.R;
import com.lectek.android.app.ActivityManage;
import com.lectek.android.app.ITitleBar;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.account.IaccountObserver;
import com.lectek.android.lereader.animation.OpenBookAnimManagement;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.binding.model.contentinfo.ContentInfoViewModelLeyue;
import com.lectek.android.lereader.binding.model.contentinfo.ContentInfoViewModelLeyue.ContentInfoUserAciton;
import com.lectek.android.lereader.binding.model.contentinfo.ContentInfoViewModelLeyue.UserActionListener;
import com.lectek.android.lereader.lib.share.entity.UmengShareInfo;
import com.lectek.android.lereader.lib.thread.internal.ITerminableThread;
import com.lectek.android.lereader.lib.thread.internal.TerminableThread;
import com.lectek.android.lereader.lib.utils.DateUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.lib.utils.PkgManagerUtil;
import com.lectek.android.lereader.net.response.BookTagInfo;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.permanent.ShareConfig;
import com.lectek.android.lereader.permanent.ThirdPartLoginConfig;
import com.lectek.android.lereader.permanent.ThirdPartLoginConfig.TYConfig;
import com.lectek.android.lereader.share.ShareWeiXin;
import com.lectek.android.lereader.share.ShareYiXin;
import com.lectek.android.lereader.share.entity.MutilMediaInfo;
import com.lectek.android.lereader.share.util.UmengShareUtils;
import com.lectek.android.lereader.share.util.UmengShareUtils.YXHanlder;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.ui.login_leyue.ThirdPartyLoginActivity;
import com.lectek.android.lereader.ui.login_leyue.UserLoginLeYueNewActivity;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.ToastUtil;
import com.lectek.android.lereader.widgets.SlidingView.ISlideGuestureHandler;
import com.lectek.android.widget.EllipsizingTextView;
import com.lectek.android.widget.GVViewPager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.view.CommentActivity;

/**
 * @description
	书籍详情
 * @author chendt
 * @date 2013-9-28
 * @Version 1.0
 * @SEE ContentInfoViewModelLeyue
 */
public class ContentInfoActivityLeyue extends BaseActivity implements ContentInfoUserAciton ,YXHanlder,ISlideGuestureHandler{
	private boolean isShareQZone = false;
	private final String PAGE_NAME = "书籍详情";
	private static final String TAG = ContentInfoActivityLeyue.class.getSimpleName();
	private ContentInfoViewModelLeyue mContenViewModelLeyue;
	private ContentInfoLeyue loadedInfo;
	private TextView priceTextView;
	private PopupWindow popupWindow;
	private EllipsizingTextView mEllipseTextView;
	private TextView moreDescIcon;
	private GVViewPager bottomViewPager;
//	public static final int REQUEST_CODE = 0x1;
	public static final int REQUEST_CODE_EXCHANGE_BOOK = 0x2;
	
	public static final int COLLECTREQUESTCODE = 12002;
	
	/**接收弹出分享重复的提示框*/
	public static final String ACTION_SHOW_TIP_DIALOG_AFTER_SHARE = "action_show_tip_dialog_after_share";
	/**分享成功刷新界面*/
	public static final String ACTION_SHARE_OK_UPDATE_VIEW = "action_share_ok_update_view";
	public static final String BUY_BOOK_FROM_READER_CODE = "buy_book_from_reader_code";
	public static final String DOWNLOAD_FULL_VERSION_FROM_READER_CODE = "download_full_version_from_reader_code";
	private UmengShareUtils utils = null;
	private AnimationDrawable newAnimation;
	public static final String INTENT_FILTER_TAG = "leyue://bookinfo/";
	public static final String GET_SUBJECT_FROM_MY_MESSAGE_TAG = "get_subject_from_my_message_tag";//从我的消息进入专题详情
	private Bundle bundle;
	private Uri uri;
	private boolean isTuiSongScene = false;
	private boolean isMyMessageScene;
	private String mBookId;
	private boolean isSurfingReader = false;
	LinearLayout ll_container;
	public static final int REQUEST_CODE_GOTOCATALOGLIST = 0x12003;
	public static final int RESULT_CODE_CATALOG_NEED_BUY = 0x12004;

	private ITerminableThread mTerminableThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		
		//积分兑换的动态图片
		ImageView newIv = (ImageView)findViewById(R.id.new_anim_iv);
		newIv.setBackgroundResource(R.anim.word_new_animation);
		newAnimation = (AnimationDrawable)newIv.getBackground();//获取动画
		setRightButtonEnabled(false);
		final ListView CommentListView = (ListView)findViewById(R.id.comment_list_lv);
		mContenViewModelLeyue.setUserActionListener(new UserActionListener() {
			
			//在列表填充完数据后计算评论列表的高度(在ScrollView中加入ListView必须要计算列表的高度)
			@Override
			public void calculateAndSetListViewHeight() {
				CommonUtil.setListViewHeightBasedOnChildren(CommentListView);
			}

			//为了避免当书籍详情滚动到底部后点击相关书籍推荐再次进入书籍详情界面内容还显示在底部，通过代码吧详情顶部显示
			@Override
			public void backToTop() {
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						ScrollView sv = (ScrollView)findViewById(R.id.content_info_scrollview);
						sv.scrollTo(0, 0);
					}
				}, 50);
			}

			@Override
			public void getLineCount() {
				MyAndroidApplication.getHandler().postDelayed(mSetExpendViewRunnable, 50);
//				new Handler().postDelayed(new Runnable() {
//					
//					@Override
//					public void run() {
//						int lines = mEllipseTextView.getLineCount();
//						
//						if (lines < 4) {
//							moreDescIcon.setVisibility(View.GONE);
//						} else {
//							moreDescIcon.setVisibility(View.VISIBLE);
//						}
//					}
//					
//				}, 50);
			}

			@Override
			public void share(View view) {
				toShare(view);
			}

			@Override
			public void getTags(ArrayList<BookTagInfo> tags) {
				//计算有几行
				int length=0;
				if(tags.size()%3==0){
					length=tags.size()/3;
				}else{
					length=tags.size()/3+1;
				}
				ll_container.removeAllViews();
				for (int i = 0; i < length; i++) {
					LinearLayout ll=new LinearLayout(this_);
					ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
					for (int j = 0; j < 3; j++) {
						TextView tv = (TextView) View.inflate(this_, R.layout.bookcity_hot_search_keywork_view, null);
						switch ((i*3+j)%5) {
						case 0:
							tv.setBackgroundResource(R.drawable.btn_biaoqian1);
							break;
						case 1:
							tv.setBackgroundResource(R.drawable.btn_biaoqian2);
							break;
						case 2:
							tv.setBackgroundResource(R.drawable.btn_biaoqian3);
							break;
						case 3:
							tv.setBackgroundResource(R.drawable.btn_biaoqian4);
							break;
						case 4:
							tv.setBackgroundResource(R.drawable.btn_biaoqian5);
							break;
						default:
							break;
						}
						if(i*3+j<tags.size()){
							tv.setText(tags.get(i*3+j).tagName);
							tv.setTextColor(getResources().getColor(R.color.white));
							tv.setPadding(20, 1, 20, 1);
							ll.addView(tv);
							LinearLayout spaceview=new LinearLayout(this_);
							spaceview.setLayoutParams(new LinearLayout.LayoutParams(20,LinearLayout.LayoutParams.WRAP_CONTENT));
							ll.addView(spaceview);
						}
					}
					ll_container.addView(ll);
					LinearLayout spaceview=new LinearLayout(this_);
					spaceview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,20));
					ll_container.addView(spaceview);
				}
			}

			@Override
			public void setBottonViewPager(ArrayList<ContentInfoLeyue> datas,
					Boolean visibility) {
				bottomViewPager.setDatas(datas,visibility);
			}

		});
		
		registerReceiver(mBroadcastReceiver, new IntentFilter(BookCommentDetailActivity.ACTION_REFREASH_DATA_BROADCAST));
		
		AccountManager.getInstance().registerObserver(mAccountObserver);
	}
	
	
	private Runnable mSetExpendViewRunnable = new Runnable() {
		
		@Override
		public void run() {
			int lines = mEllipseTextView.getLineCount();
			if(lines == 0){
				MyAndroidApplication.getHandler().postDelayed(mSetExpendViewRunnable, 50);
			}else{
				if (lines < 4) {
					moreDescIcon.setVisibility(View.GONE);
				} else {
					moreDescIcon.setVisibility(View.VISIBLE);
				}
				MyAndroidApplication.getHandler().removeCallbacks(mSetExpendViewRunnable);
			}
		}
	};
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		reEntry(intent);
	}
	
	public void reEntry(Intent intent) {
		setIntent(intent);
		LogUtil.e("onNewIntent");// singTask 走这里
		init();
	}
	
    private boolean mFromNotification = false;
	private static final String NOTI_TAG = "from_notification";
	public static void openActivity(Context context, String bookId, boolean fromOuntNotification){
		Intent intent = new Intent(context, ContentInfoActivityLeyue.class);
        intent.putExtra(LeyueConst.GOTO_LEYUE_BOOK_DETAIL_TAG, bookId);
        intent.putExtra(NOTI_TAG, fromOuntNotification);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

    public static void openActivity(Context context, String bookId){
        openActivity(context, bookId, false);
    }
	
	/**
	 * 初始化操作
	 */
	private void init(){
		bundle = getIntent().getExtras();
		uri = getIntent().getData();
		if (bundle == null && uri == null) {
			finish();
			return; 
		}
		String bookId = null;
		if (uri !=null) {
			String uriStr = uri.toString();
			if (uriStr.contains(INTENT_FILTER_TAG)) {
				bookId = uriStr.replace(INTENT_FILTER_TAG, "");
			}else if(uriStr.contains("http")){
				LogUtil.e("uriStr---"+uriStr);
				int startIndex = uriStr.indexOf("bookId=")+"bookId=".length();
				String containIdStr = uriStr.substring(startIndex, uriStr.length());
				String regex="^[0-9]\\d*";
			    Pattern p = Pattern.compile(regex);
			    Matcher m = p.matcher(containIdStr);
			    while (m.find()) {
			    	bookId = m.group(); 
				}
			}else {
				finish();
				return;
			}
		}else {
			bookId = bundle.getString(LeyueConst.GOTO_LEYUE_BOOK_DETAIL_TAG);
            mFromNotification = bundle.getBoolean(NOTI_TAG, false);
			if (bookId.contains(INTENT_FILTER_TAG)) {
				bookId = bookId.replace(INTENT_FILTER_TAG, "");
				isTuiSongScene = true;
				isMyMessageScene = bundle.getBoolean(GET_SUBJECT_FROM_MY_MESSAGE_TAG, false);
			}
			boolean needDownloadFullVersion = bundle.getBoolean(DOWNLOAD_FULL_VERSION_FROM_READER_CODE, false);
			if (mContenViewModelLeyue!=null) {
				mContenViewModelLeyue.setNeedDownloadFullVersion(needDownloadFullVersion);
			}
		}
		setTitleBarEnabled(!bundle.getBoolean(BookCityActivityGroup.Extra_Embed_Activity_Hide_Title_Bar, false));
		//获取该变量，如果返回TRUE证明是天翼阅读书籍
		isSurfingReader = bundle.getBoolean(LeyueConst.EXTRA_BOOLEAN_IS_SURFINGREADER, false);

        String leBookId = bundle.getString(LeyueConst.EXTRA_LE_BOOKID);
		
		mBookId = bookId;
		setTitleContent(getString(R.string.content_info));
		priceTextView = (TextView) findViewById(R.id.price);
		priceTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
		
		mEllipseTextView = (EllipsizingTextView)findViewById(R.id.ellipseTextView);
		moreDescIcon =(TextView)findViewById(R.id.more_des_icon);
		
		if (mContenViewModelLeyue!=null) {
			mContenViewModelLeyue.setSpan(getResources().getColor(R.color.common_5));
            mContenViewModelLeyue.setLeBookId(leBookId);
			mContenViewModelLeyue.onStart(mBookId, PreferencesUtil.getInstance(this_).getUserId(), isSurfingReader);
		}

//		setRightButton("", R.drawable.share_icon_white);
//		setRightButtonEnabled(false);
//		getRightBtnView().setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if (loadedInfo!=null) {
//					utils = new UmengShareUtils();
//					utils.baseInit(this_);
//					utils.setMailSubjectTitle("书籍分享");
//					Bitmap bitmap = null;
//					if (!TextUtils.isEmpty(loadedInfo.getCoverPath())) {
//						String imagePath = String.valueOf(loadedInfo.getCoverPath().hashCode());
//						if (!TextUtils.isEmpty(imagePath)) {
//							bitmap = CommonUtil.getImageInSdcard(imagePath);
//						}
//					}
//					utils.setShareInfo(this_, new UmengShareInfo(
//							getString(R.string.share_for_book, loadedInfo.getBookName(),UmengShareUtils.contentUrl), loadedInfo.getCoverPath()),bitmap);
//					if (popupWindow == null) {
//						popupWindow = utils.showPopupWindow((Activity)ContentInfoActivityLeyue.this, v,ContentInfoActivityLeyue.this,snsListener);
//					}else {
//						if (popupWindow.isShowing()) {
//							popupWindow.dismiss();
//						}
//						UmengShareUtils.popWindowShow(v, popupWindow);
//					}
//				}else {
//					ToastUtil.showToast(this_, "暂时无法分享，请重新进入该界面！");
//				}
//			}
//		});
		
		//注册接收弹出分享重复的提示框广播
		registerReceiver(mTipDialogBroadcastReceiver, new IntentFilter(ACTION_SHOW_TIP_DIALOG_AFTER_SHARE));
		//如果来自阅读界面购买操作后，弹出购买窗口
		if(mContenViewModelLeyue!=null && 
				bundle!=null && bundle.getBoolean(ContentInfoActivityLeyue.BUY_BOOK_FROM_READER_CODE, false)){
//			mContenViewModelLeyue.buyBook();
			mContenViewModelLeyue.setOnPostBuy(true);
		}
	}

	/**
	 * 分享功能
	 * @param v
	 */
	
	private void toShare(View v){
		if (loadedInfo!=null) {
			utils = new UmengShareUtils();
			utils.baseInit(this_);
			utils.setMailSubjectTitle("书籍分享");
			String imagePath = "";
			if(!TextUtils.isEmpty(loadedInfo.getCoverPath())){
				imagePath = String.valueOf(loadedInfo.getCoverPath().hashCode());
			}
			Bitmap bitmap = null;
			if (!TextUtils.isEmpty(imagePath)) {
				bitmap = CommonUtil.getImageInSdcard(imagePath);
			}
			utils.setShareInfo(this_, new UmengShareInfo(
					getString(R.string.share_for_book, loadedInfo.getBookName(),UmengShareUtils.contentUrl), ""),bitmap);
			if (popupWindow == null) {
				popupWindow = utils.showPopupWindow(CommonUtil.getRealActivity(this_), v,ContentInfoActivityLeyue.this,snsListener);
			}else {
				if (popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
				UmengShareUtils.popWindowShow(v, popupWindow);
			}
		}else {
			ToastUtil.showToast(this_, "暂时无法分享，请重新进入该界面！");
		}
	}
	
	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mContenViewModelLeyue = new ContentInfoViewModelLeyue(this_, this,this,ll_container);
		View view=bindView(R.layout.content_info_leyue,this, mContenViewModelLeyue);
		bottomViewPager=(GVViewPager) view.findViewById(R.id.gvvp);
		ll_container=(LinearLayout) view.findViewById(R.id.ll_container);
		return view;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		OpenBookAnimManagement.getInstance().starCloseBookAnim(null);
		//添加友盟统计
		MobclickAgent.onPageStart(PAGE_NAME);
		MobclickAgent.onResume(this);
	}

//	@Override
//	public void initLoadingDialog(){
//		View loadView = LayoutInflater.from(this_).inflate(R.layout.loading_data_lay, null);
//		mLoadingDialog = CommonUtil.getTransparentDialog(SlideActivityGroup.getInstance(), loadView);
//	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if(hasFocus && newAnimation!=null)
			newAnimation.start();
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	protected void onPause() {
		super.onPause();
		//添加友盟统计
		MobclickAgent.onPageEnd(PAGE_NAME);
		MobclickAgent.onPause(this);
	}

	@Override
	public void exceptionHandle(String str) {
//		ToastUtil.showToast(this_, str);
		setTipImageResource(R.drawable.icon_request_fail_tip);
	}

	@Override
	public void optToast(String str) {
		// TODO Auto-generated method stub
		
	}

	private Runnable mLoginSuccessRunnable;
	@Override
	public void gotoLoginAcitity(Runnable loginSuccessRunnable) {
		if(mLoginSuccessRunnable != null) {
			return;
		}
		
		mLoginSuccessRunnable = loginSuccessRunnable;
		if(isSurfingReader) {
			Bundle params = new Bundle();
			params.putBoolean(TYConfig.Extra_ShowSwitchTip, true);
			ThirdPartyLoginActivity.openActivityForResult(this_, ThirdPartLoginConfig.TYPE_TY, params);
			showLoadDialog();
		}else {
			Intent intent = new Intent(this, UserLoginLeYueNewActivity.class);
			startActivity(intent);
		}
	}
	
	@Override
	public void onBackPressed() {
		if(mContenViewModelLeyue.handlerOnBackPress()) {
			return;
		}else if(mTerminableThread != null) {
			mTerminableThread.cancel();
			mTerminableThread = null;
			hideLoadDialog();
		}
		super.onBackPressed();
	}
	
    @Override
	public void onDestroy() {
		super.onDestroy();
		
		AccountManager.getInstance().unRegisterObserver(mAccountObserver);
		MyAndroidApplication.getHandler().removeCallbacks(mSetExpendViewRunnable);
		
		if(mTerminableThread != null) {
			mTerminableThread.cancel();
			mTerminableThread = null;
		}
		
		if (mContenViewModelLeyue!=null) {
			mContenViewModelLeyue.onDestory();
		}else {
			LogUtil.e(TAG, "mContenViewModelLeyue = null.注销不了广播");
		}
		if (mTipDialogBroadcastReceiver!=null) {
			unregisterReceiver(mTipDialogBroadcastReceiver);
			mTipDialogBroadcastReceiver = null;
		}
		
		if(mBroadcastReceiver != null){
			unregisterReceiver(mBroadcastReceiver);
			mBroadcastReceiver = null;
		}
        if(mFromNotification){
            if(ActivityManage.getTopActivity() == null){
                startActivity(new Intent(this, WelcomeActivity.class));
            }
        }
	}
	@Override
	public void setPriceNoSpan(){
		priceTextView.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
	}
	
	@Override
	public void setPriceStrikeSpan() {
		priceTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
//		paperPriceTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
	}
	
	private IaccountObserver mAccountObserver = new IaccountObserver() {
		
		@Override
		public void onLoginComplete(boolean success, String msg, Object... params) {
			if(success){
				//登陆成功。1.本地判断该书籍是否已购买；2.网络判断书籍是否已购买
//				mContenViewModelLeyue.onLoginSuccessCheckInfo();
				if(mLoginSuccessRunnable != null) {
					Runnable runnable = mLoginSuccessRunnable;
					
					runOnUiThread(runnable);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							hideLoadDialog();
							hideLoadView();
						}
					});
				}
			}else{
				if(mLoginSuccessRunnable != null) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							hideLoadDialog();
							hideLoadView();
						}
					});
				}
				ContentInfoActivityLeyue.this.sendBroadcast(new Intent(AppBroadcast.ACTION_BUY_FAIL));
			}
			
			mLoginSuccessRunnable = null;
		}
		
		@Override
		public void onGetUserInfo(UserInfoLeyue userInfo) {}
		
		@Override
		public void onAccountChanged() {}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(ThirdPartyLoginActivity.ACTIVITY_REQUEST_CODE == requestCode) {
			int type = data.getIntExtra(ThirdPartLoginConfig.EXTRA_TYPE, ThirdPartLoginConfig.TYPE_NORMAL);
			switch(resultCode) {
			case ThirdPartyLoginActivity.ACTIVITY_RESULT_CODE_SUCCESS:
				if(data.getExtras() != null) {
					Bundle resultData = data.getExtras();
					if(ThirdPartLoginConfig.TYPE_TY == type) {
						if(mTerminableThread != null && !mTerminableThread.isCancel()) {
							mTerminableThread.cancel();
						}
						mTerminableThread = AccountManager.getInstance().loginByTY(resultData.getString(ThirdPartLoginConfig.TYConfig.Extra_UserID),
								resultData.getString(ThirdPartLoginConfig.TYConfig.Extra_AccessToken), resultData.getString(ThirdPartLoginConfig.TYConfig.Extra_RefreshToken), false);
					}
				}
				break;
			case ThirdPartyLoginActivity.ACTIVITY_RESULT_CODE_FAIL:
				ToastUtil.showToast(this_, R.string.user_login_faild);
				hideLoadDialog();
				hideLoadView();
				break;
			}
			return;
		}
		
		if (!isShareQZone) {
			/**使用SSO授权必须添加如下代码 */
			UmengShareUtils utils = new UmengShareUtils();
			UMSsoHandler ssoHandler = utils.getSsoHandler(requestCode);
			if(ssoHandler != null){
				ssoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
		}else {
			isShareQZone = false;
		}
		if (requestCode == REQUEST_CODE_EXCHANGE_BOOK) {
			if (resultCode == ScoreExchangeBookActivity.TAG_BUY_SUCCESS) {
				if(mContenViewModelLeyue!=null){
					mContenViewModelLeyue.buyBookSuccess();
				}
			}else if (resultCode == ScoreExchangeBookActivity.TAG_RELOAD) {
				if(mContenViewModelLeyue!=null){
					mContenViewModelLeyue.onStart(mBookId, PreferencesUtil.getInstance(this_).getUserId(), isSurfingReader);
				}
			}
		}
		
		
		//目录列表返回结果
		if(requestCode == REQUEST_CODE_GOTOCATALOGLIST){
			//目录点击了购买章节
			if(resultCode == RESULT_CODE_CATALOG_NEED_BUY){
				mContenViewModelLeyue.bBuyClick.onClick(null);
			}
		}
		
	}

	@Override
	public void finishActivity() {
		if(bundle.getBoolean(BookCityActivityGroup.Extra_Embed_Activity_Hide_Title_Bar, false)){
			ActivityChannels.removeEmbedActivityFromBookCity(this_, BookCityActivityGroup.TAB_Embed_CONTENTINFO);
		}else{			
			finish();
		}
	}

	@Override
	public void loadOver(ContentInfoLeyue info) {
//		setRightButtonEnabled(true);
		loadedInfo = info ;
		if(isSurfingReader){
			UmengShareUtils.contentUrl = LeyueConst.WX_YYB_PATH+loadedInfo.getBookId() + "&bookType=1";	//bookType=1代表是天翼阅读的书籍
		}else{
			UmengShareUtils.contentUrl = LeyueConst.WX_YYB_PATH+loadedInfo.getBookId() + "&bookType=0";	//bookType=1代表是乐阅的书籍
		}
		
	}

	@Override
	public void handleForYiXin(int type) {
		ShareYiXin shareYiXin = new ShareYiXin(this_);
		if (shareYiXin.isYxInstall()) {
			if (!TextUtils.isEmpty(loadedInfo.getCoverPath())) {
				String imagePath = String.valueOf(loadedInfo.getCoverPath().hashCode());
				LogUtil.e("-----cover-localName=="+imagePath);
				shareYiXin.sendTextWithPic(new MutilMediaInfo("",getString(R.string.share_for_book_wx, loadedInfo.getBookName()), imagePath,type,UmengShareUtils.contentUrl));
			}else {
				shareYiXin.sendTextWithPic(new MutilMediaInfo("",getString(R.string.share_for_book_wx, loadedInfo.getBookName()), "",type,UmengShareUtils.contentUrl));
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
				if (!TextUtils.isEmpty(loadedInfo.getCoverPath())) {
					String imagePath = String.valueOf(loadedInfo.getCoverPath().hashCode());
					switch (type) {
					case MutilMediaInfo.WX_FRIEND:
						shareWeiXin.sendTextWithPic(new MutilMediaInfo("",getString(R.string.share_for_book_wx, loadedInfo.getBookName()), imagePath,type,UmengShareUtils.contentUrl));
						break;
					case MutilMediaInfo.WX_FRIEND_ZONE:
						shareWeiXin.sendTextWithPic(new MutilMediaInfo(getString(R.string.share_for_book_wx, loadedInfo.getBookName()),"", imagePath,type,UmengShareUtils.contentUrl));
						break;
						
					default:
						break;
					}
				}else {
					shareWeiXin.sendTextWithPic(new MutilMediaInfo(getString(R.string.share_for_book_title),getString(R.string.share_for_book_wx, loadedInfo.getBookName()), "",type,LeyueConst.WX_YYB_PATH));
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
		//FIXME:改为在线图片
		if (utils!=null && !TextUtils.isEmpty(loadedInfo.getCoverPath())) {
			String imagePath = String.valueOf(loadedInfo.getCoverPath().hashCode());
			Bitmap bitmap = null;
			if (!TextUtils.isEmpty(imagePath)) {
				bitmap = CommonUtil.getImageInSdcard(imagePath);
			}
			QQShareContent qqShareContent = new QQShareContent();
			qqShareContent.setShareContent(getString(R.string.share_for_book, loadedInfo.getBookName(),UmengShareUtils.contentUrl));
			qqShareContent.setTitle("软件分享");
			qqShareContent.setShareImage(new UMImage(this, bitmap));
			qqShareContent.setTargetUrl(UmengShareUtils.contentUrl);
			utils.shareForQQ(qqShareContent);
		}
	}

	@Override
	public void handleForQQZONE() {
		//Qzone 使用自定义分享接口
		if (utils!=null && !TextUtils.isEmpty(loadedInfo.getCoverPath())) {
			utils.setShareInfo(this_, new UmengShareInfo(
					getString(R.string.share_for_book, loadedInfo.getBookName(),UmengShareUtils.contentUrl), loadedInfo.getCoverPath()),null);
		}
		utils.shareToQzone(this_);
		isShareQZone = true;
	}

	@Override
	public void handleForSMS() {
		//短信不带图片
		if (utils!=null) {
			utils.setShareInfo(this_, new UmengShareInfo(
					getString(R.string.share_for_book, loadedInfo.getBookName(),UmengShareUtils.contentUrl), ""),null);
		}
	}
	
	@Override
	public void finish() {
		if (!LeyueConst.isLeyueVersion) {
			if (LeyueConst.CHANNEL_360.equals(PkgManagerUtil.getUmengChannelId(this_))) {
				if (DateUtil.isAvoidAccessBookCity(this_,PreferencesUtil.getInstance(this_).getAccessBookCityDeadline())) {
					startActivity(new Intent(this, MainActivity.class));
				}
			}else {
				if (!PreferencesUtil.getInstance(getApplicationContext()).getIsFirstEnterApp(true)) {
					startActivity(new Intent(this, MainActivity.class));
				}
			}
			PreferencesUtil.getInstance(getApplicationContext()).setIsFirstEnterApp(false);
			super.finish();
		}else {
			if (uri!=null || isTuiSongScene && !isMyMessageScene) {
				startActivity(new Intent(this, MainActivity.class));
				super.finish();
			}else {
				super.finish();
			}
		}
	}

	@Override
	public void saveSourceId() {
		UmengShareUtils.LAST_SHARE_SOURCEID = loadedInfo.getBookId();
		UmengShareUtils.shareContext = ContentInfoActivityLeyue.this;
	}
	
	private BroadcastReceiver mTipDialogBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent == null){
				return;
			}
			final String soureName = intent.getStringExtra(ACTION_SHOW_TIP_DIALOG_AFTER_SHARE);
			if(ACTION_SHOW_TIP_DIALOG_AFTER_SHARE.equals(intent.getAction())
					&& CommonUtil.isOnCurrentActivityView(ContentInfoActivityLeyue.this)){
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {	
						//弹出分享同一本书，无积分奖励提示框
						DialogUtil.commonConfirmDialog(ContentInfoActivityLeyue.this, 
								ContentInfoActivityLeyue.this.getString(R.string.share_tip), 
								ContentInfoActivityLeyue.this.getString(R.string.share_book_repeat_tip,soureName), 
								 R.string.share_enter_tip,R.string.share_exit_tip,new DialogUtil.ConfirmListener() {
										
										@Override
										public void onClick(View v) {
											//进入积分规则说明界面
											ScoreRuleActivity.gotoScoreRuleActivity(ContentInfoActivityLeyue.this);
										}
									},null).show();
					}
				});
			}
		}
	};
	
	private SnsPostListener snsListener = new SnsPostListener() {
		
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onComplete(SHARE_MEDIA arg0, int eCode, SocializeEntity arg2) {
			LogUtil.e("--- eCode--"+ eCode);
			switch (arg0) {
			case QQ:
				if (eCode == ShareConfig.SNS_SUCEESS_CODE) {
					mContenViewModelLeyue.uploadShareInfo();
				}else {
					ToastUtil.showToast(this_, "分享失败");
				}
				break;
			case SINA:
				if (eCode == ShareConfig.SNS_SUCEESS_CODE) {
					mContenViewModelLeyue.uploadShareInfo();
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
	
	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			mContenViewModelLeyue.onStart(mBookId, PreferencesUtil.getInstance(this_).getUserId(), isSurfingReader);
		}
	};

	@Override
	public boolean consumeHorizontalSlide(float x, float y, float deltaX) {
		// TODO Auto-generated method stub
		return true;
	}

}
