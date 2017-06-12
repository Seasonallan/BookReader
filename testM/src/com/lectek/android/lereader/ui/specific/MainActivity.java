package com.lectek.android.lereader.ui.specific;

import gueei.binding.observables.BooleanObservable;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.binding.widget.BViewPagerTabHost;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.account.IaccountObserver;
import com.lectek.android.lereader.lib.utils.FileUtil;
import com.lectek.android.lereader.lib.utils.IProguardFilterOnlyPublic;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.lib.utils.UniqueIdUtils;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.UpdateInfo;
import com.lectek.android.lereader.presenter.SyncPresenter;
import com.lectek.android.lereader.share.util.UmengShareUtils;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.IPanelView;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.ToastUtil;
import com.lectek.android.update.UpdateManager;
import com.lectek.android.widget.BaseViewPagerTabHostAdapter;
import com.lectek.android.widget.BaseViewPagerTabHostAdapter.ItemLifeCycleListener;
import com.umeng.analytics.MobclickAgent;
/**
 * 主界面
 * @author linyiwei
 */
public class MainActivity extends BaseActivity implements IProguardFilterOnlyPublic{
	
	public static final String EXTRA_NAME_GO_TO_VIEW = "go_to_view";
	public static final int RESULT_CODE_GOTO_DOWNLOAD = 2010;
	
	
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final String TAG_BOOK_SHELF = "TAG_BOOK_SHELF";
	private static final String TAG_BOOK_CITY = "TAG_BOOK_CITY";
	
	private ViewGroup mCustomTitle;
	private View mTabTitle;
	private ImageView iv_shelf_more;
	private BookShelfViewNotBinding mBookShelfView;
	private PopupWindow mImportPopupWin;	// 导入本地图书和wifi传书popupwindow
	
	public boolean isBookShelf;
	
	private BViewPagerTabHost mainTabHost;

	public final ViewPagerAdapter bViewPagerTabHostAdapter = new ViewPagerAdapter();
	public BooleanObservable bShelfLBtnVisible = new BooleanObservable(true);
	public BooleanObservable bShelfLImportBtnVisible = new BooleanObservable(true);
	public BooleanObservable bShelfRBtnVisible = new BooleanObservable(false);
	public BooleanObservable bCitySearchBtnVisible = new BooleanObservable(false);
	public BooleanObservable bPersonalTipVisible = new BooleanObservable(false);
	public BooleanObservable bBookShelfTipVisible = new BooleanObservable(false);
	public BooleanObservable bSearchTipVisible = new BooleanObservable(false);
	public BooleanObservable bBookCityTipVisible = new BooleanObservable(false);
	public BooleanObservable bLayoutTipVisible = new BooleanObservable(false);
	public BooleanObservable bIsUnLoginPointVisibility = new BooleanObservable(false);

	public final OnClickCommand bLeftButClick = new OnClickCommand(){
		@Override
		public void onClick(View v) {
//            if (getParent() != null && getParent() instanceof SlideActivityGroup){
//                ((SlideActivityGroup)getParent()).showLeft();
//            }else{
//                ActivityChannels.gotoUserInfoActivity(this_);
//            }
		}
	};
	
	public final OnClickCommand bShelfRightButClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			gotoBookCity();
			
		}
	};
	
	public final OnClickCommand bRightButClick = new OnClickCommand(){
		@Override
		public void onClick(View v) {}

	};
	public final OnClickCommand bTestWriteClick = new OnClickCommand(){
		@Override
		public void onClick(View v) {
			UmengShareUtils utils = new UmengShareUtils();
			utils.baseInit(this_);
		}
		
	};
	
	/**
	 * 导入本地图书或wifi传书
	 */
	public final OnClickCommand bImportButClick = new OnClickCommand() {
		@Override
		public void onClick(View v) {
			showImportPopWin();
		}
	};
	
	/**
	 * 显示本地导入和wifi传书的窗口
	 */
	private void showImportPopWin() {
		View view = bindView(R.layout.bookshelf_import_pop_lay, this);
		mImportPopupWin = new PopupWindow(view, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		mImportPopupWin.setFocusable(true);
		mImportPopupWin.setOutsideTouchable(true);
		mImportPopupWin.setBackgroundDrawable(new BitmapDrawable());
		mImportPopupWin.showAsDropDown(iv_shelf_more, 0, 0);	
	}
	
	/**
	 * 导入本地图书
	 */
	public final OnClickCommand bImportLocalBtnClick = new OnClickCommand() {
		@Override
		public void onClick(View v) {
			if(mImportPopupWin != null) {
				mImportPopupWin.dismiss();
			}

            if(!FileUtil.isSDcardExist()) {
                DialogUtil.oneConfirmBtnDialog(this_, null, getString(R.string.import_local_no_sdcard), -1, null);
                return;
            }
			ActivityChannels.gotoImportLocalActivity(this_);
		}
	};
	
	/**
	 * wifi传书
	 */
	public final OnClickCommand bWifiTransferBtnClick = new OnClickCommand() {
		@Override
		public void onClick(View v) {
			if(mImportPopupWin != null) {
				mImportPopupWin.dismiss();
			}
            if(!FileUtil.isSDcardExist()) {
                DialogUtil.oneConfirmBtnDialog(this_, null, getString(R.string.import_wifi_no_sdcard), -1, null);
                return;
            }
			ActivityChannels.gotoWifiTransferActivity(this_);
		}
	};
		
	@Override
	protected void onNewIntent(Intent intent) {
		int viewIndex = intent.getIntExtra(EXTRA_NAME_GO_TO_VIEW, -1);
		if (viewIndex == RESULT_CODE_GOTO_DOWNLOAD) {
			gotoBookShelf();
		}
		super.onNewIntent(intent);
	}
	
	private void gotoBookShelf(){
		mainTabHost.setCurrentTab(0);
	}
	
	private void gotoBookCity(){
		mainTabHost.setCurrentTab(1);
	}

	public final OnClickCommand bCitySearchBtnClick = new OnClickCommand(){
		@Override
		public void onClick(View v) {
			ActivityChannels.gotoSearchListActivity(this_);
		}
	};
	
	private String currentTag = null;
	
	/**
	 * 遮罩提示
	 
	public final OnClickCommand bhelplayoutCommand = new OnClickCommand(){
		@Override
		public void onClick(View v) {
			if(TAG_BOOK_SHELF.equals(currentTag)){
				PreferencesUtil.getInstance(this_).setShowBookShlefHelpTip(false);
				bPersonalTipVisible.set(false);
				bBookShelfTipVisible.set(false);
				bLayoutTipVisible.set(false);
			}else if(TAG_BOOK_CITY.equals(currentTag)){
				PreferencesUtil.getInstance(this_).setShowBookCityHelpTip(false);
				bSearchTipVisible.set(false);
				bBookCityTipVisible.set(false);
				bLayoutTipVisible.set(false);
			}
		}
	};
	
	*/
	
	@Override
	protected View newContentView(Bundle savedInstanceState) {
		return bindView(R.layout.main_layout, this);
	}
	
	@Override
	public void setTitleView(View view) {
		if(view != null && view.getParent() == null){
			mCustomTitle.removeAllViews();
			mCustomTitle.addView(view);
			mCustomTitle.setVisibility(View.VISIBLE);
			mTabTitle.setVisibility(View.GONE);
			findViewById(R.id.header_bottom_line_add).setVisibility(View.GONE);
		}else{
			resetTitleBar();
		}
	}

	@Override
	public void resetTitleBar() {
		
		if(mCustomTitle == null || mTabTitle == null)
			return;
		
		mCustomTitle.removeAllViews();
		mCustomTitle.setVisibility(View.GONE);
		mTabTitle.setVisibility(View.VISIBLE);
		findViewById(R.id.header_bottom_line_add).setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		super.setTitleBarEnabled(false);
		
//		CommonUtil.getSpecialRepeatLine(findViewById(R.id.header_bottom_line_add), this_);
		mCustomTitle = (ViewGroup)findViewById(R.id.custom_title);
		mTabTitle = findViewById(R.id.tab_title);
		iv_shelf_more = (ImageView) findViewById(R.id.iv_shelf_more);
		bViewPagerTabHostAdapter.setItemLifeCycleListener(new ItemLifeCycleListener() {
			@Override
			public void onDestroy(View view, int position) {
				if(view instanceof IPanelView){
					((IPanelView) view).onDestroy();
				}
			}
			
			@Override
			public boolean onCreate(View view, int position) {
				if(view instanceof IPanelView){
					((IPanelView) view).onCreate();
				}
				return true;
			}
		});
		mainTabHost = (BViewPagerTabHost) findViewById(R.id.main_view_pager);
		mainTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				
				currentTag = tabId;
				if(TAG_BOOK_SHELF.equals(tabId)){
					
					/**
					 * 隐藏帮助界面
					if (PreferencesUtil.getInstance(this_).isShowBookShlefHelpTip()) {
						bPersonalTipVisible.set(true);
						bBookShelfTipVisible.set(true);
						bLayoutTipVisible.set(true);
					}else {
						bPersonalTipVisible.set(false);
						bBookShelfTipVisible.set(false);
						bLayoutTipVisible.set(false);
					}
					
					*/
					
					isBookShelf = true;
					bShelfLBtnVisible.set(true);
					bShelfLImportBtnVisible.set(true);
					bShelfRBtnVisible.set(false);//TODO:
					bCitySearchBtnVisible.set(false);
					
					
					
					if(BookCityView.current_path_string.equals(BookCityView.PATH_BOOKCITY_RECOMMEND)){
						MobclickAgent.onPageEnd(BookCityView.PATH_BOOKCITY_RECOMMEND);
					}else if(BookCityView.current_path_string.equals(BookCityView.PATH_BOOKCITY_CLASSIFY)){
						MobclickAgent.onPageEnd(BookCityView.PATH_BOOKCITY_CLASSIFY);
					}else if(BookCityView.current_path_string.equals(BookCityView.PATH_BOOKCITY_SALE)){
						MobclickAgent.onPageEnd(BookCityView.PATH_BOOKCITY_SALE);
					}else if(BookCityView.current_path_string.equals(BookCityView.PATH_BOOKCITY_SPECIALOFFER)){
						MobclickAgent.onPageEnd(BookCityView.PATH_BOOKCITY_SPECIALOFFER);
					}else if(BookCityView.current_path_string.equals(BookCityView.PATH_BOOKCITY_SEARCH)){
						MobclickAgent.onPageEnd(BookCityView.PATH_BOOKCITY_SEARCH);
					}
//                    if (getParent() != null && getParent() instanceof SlideActivityGroup)
//                         ((SlideActivityGroup)getParent()).setCanSliding(true, false);
				}else if(TAG_BOOK_CITY.equals(tabId)){
//                    if (getParent() != null && getParent() instanceof SlideActivityGroup)
//                        ((SlideActivityGroup)getParent()).setCanSliding(false, false);
					/**
					 * 隐藏帮助界面
					
					
					if (PreferencesUtil.getInstance(this_).isShowBookCityHelpTip()) {
						bSearchTipVisible.set(true);
						bBookCityTipVisible.set(true);
						bLayoutTipVisible.set(true);
					}else {
						bSearchTipVisible.set(false);
						bBookCityTipVisible.set(false);
						bLayoutTipVisible.set(false);
					}
					 */
					
					isBookShelf = false;
					bShelfLBtnVisible.set(false);
					bShelfLImportBtnVisible.set(false);
					bShelfRBtnVisible.set(false);//TODO:
					bCitySearchBtnVisible.set(true);
					
					if(TextUtils.isEmpty(BookCityView.current_path_string))
						BookCityView.current_path_string = BookCityView.PATH_BOOKCITY_RECOMMEND;
						
					if(BookCityView.current_path_string.equals(BookCityView.PATH_BOOKCITY_RECOMMEND)){
						MobclickAgent.onPageStart(BookCityView.PATH_BOOKCITY_RECOMMEND);
						BookCityView.current_path_string = BookCityView.PATH_BOOKCITY_RECOMMEND;
					}else if(BookCityView.current_path_string.equals(BookCityView.PATH_BOOKCITY_CLASSIFY)){
						MobclickAgent.onPageStart(BookCityView.PATH_BOOKCITY_CLASSIFY);
						BookCityView.current_path_string = BookCityView.PATH_BOOKCITY_CLASSIFY;
					}else if(BookCityView.current_path_string.equals(BookCityView.PATH_BOOKCITY_SALE)){
						MobclickAgent.onPageStart(BookCityView.PATH_BOOKCITY_SALE);
						BookCityView.current_path_string = BookCityView.PATH_BOOKCITY_SALE;
					}else if(BookCityView.current_path_string.equals(BookCityView.PATH_BOOKCITY_SPECIALOFFER)){
						MobclickAgent.onPageStart(BookCityView.PATH_BOOKCITY_SPECIALOFFER);
						BookCityView.current_path_string = BookCityView.PATH_BOOKCITY_SPECIALOFFER;
					}else if(BookCityView.current_path_string.equals(BookCityView.PATH_BOOKCITY_SEARCH)){
						MobclickAgent.onPageStart(BookCityView.PATH_BOOKCITY_SEARCH);
						BookCityView.current_path_string = BookCityView.PATH_BOOKCITY_SEARCH;
					}
					
				}
			}
		});
		Drawable slideBottomLineDrawable = getResources().getDrawable(R.drawable.city_top_slide_bottom_shape);
		mainTabHost.slideTabWidgetinitialize(slideBottomLineDrawable);
		mainTabHost.setIndicatorOffsetX(getResources().getDimensionPixelOffset(R.dimen.a_top_slide_bg_offset));
//		if (NetworkUtil.isNetAvailable(this_)) {
//			mainTabHost.setCurrentTabByTag(TAG_BOOK_CITY);
//		}else {
//			mainTabHost.setCurrentTabByTag(TAG_BOOK_SHELF);
//		}
		final UpdateInfo updateInfo = UpdateManager.getUpdateInfo();
		if(updateInfo != null){
			PreferencesUtil preferencesUtil = PreferencesUtil.getInstance(this);
			if(preferencesUtil.isShowUpdateAgain() || updateInfo.isMustUpdate()){
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						UpdateManager.getInstance().startUpdateInfo(MainActivity.this, updateInfo, null);
					}
				});
			}
		}
		
		AccountManager.getInstance().registerObserver(mAccountObserver);
		updateLoginState();

	}
	
	private void updateLoginState(){
		if(PreferencesUtil.getInstance(this).getIsLogin()){
			bIsUnLoginPointVisibility.set(false);
		}else{
			bIsUnLoginPointVisibility.set(true);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("MainActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
        SyncPresenter.startSyncLocalSysBookMarkTask();
		if(PreferencesUtil.getInstance(this_).getStatusBarHeight() == 0){
			// 获取状态栏的高度
			Rect frame = new Rect();
			getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			int statusBarHeight = frame.top;
			LogUtil.i("something", "statusbar_height: "+statusBarHeight);
			PreferencesUtil.getInstance(this_).setStatusBarHeight(statusBarHeight);
		}
		
		MobclickAgent.onPageEnd("MainActivity");
		MobclickAgent.onPause(this);
	}
	
	private IaccountObserver mAccountObserver = new IaccountObserver() {
		
		@Override
		public void onLoginComplete(boolean success, String msg, Object... params) {
			updateLoginState();
		}
		
		@Override
		public void onGetUserInfo(UserInfoLeyue userInfo) {}
		
		@Override
		public void onAccountChanged() {
			updateLoginState();
		}
	};

	private class ViewPagerAdapter extends BaseViewPagerTabHostAdapter implements IProguardFilterOnlyPublic{
		public ArrayList<String> mTags;
		public ViewPagerAdapter(){
			mTags = new ArrayList<String>();
			mTags.add(TAG_BOOK_SHELF);
			mTags.add(TAG_BOOK_CITY);
		}
		
		@Override
		public View getIndicator(int position) {
			//字体描边效果。最土方式，使用层叠效果。
			View indicatorView = LayoutInflater.from(this_).inflate(R.layout.book_city_top_view, null);
			TextView textView1 = (TextView) indicatorView.findViewById(R.id.text);
			TextView textView2 = (TextView) indicatorView.findViewById(R.id.text_bg);
			TextPaint tp = textView1.getPaint(); 
		    tp.setStrokeWidth(1);
		    tp.setStyle(Style.FILL_AND_STROKE);
		    tp.setFakeBoldText(true);
		    
			if(TAG_BOOK_SHELF.equals(getTab(position))){
				textView1.setText("书架");
				textView2.setText("书架");
			}else if(TAG_BOOK_CITY.equals(getTab(position))){
				textView1.setText("书城");
				textView2.setText("书城");
			}
			return indicatorView;
		}

		@Override
		public String getTab(int position) {
			return mTags.get(position);
		}

		@Override
		public int getCount() {
			return mTags.size();
		}

		@Override
		public View getItemView(ViewGroup container, int position) {
			if(TAG_BOOK_SHELF.equals(getTab(position))){
//				container = mBookShelfView = new BookShelfViewNotBinding(this_);
			}else if(TAG_BOOK_CITY.equals(getTab(position))){
				container = new BookCityView(this_);
			}
			return container;
		}
	}
	
	private long lastExitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean canQuit = true;
		if(mBookShelfView != null && isBookShelf){
			canQuit = mBookShelfView.onKeyDown(keyCode, event);
		}
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && canQuit) {
			if(System.currentTimeMillis()-lastExitTime > 1500){
				lastExitTime = System.currentTimeMillis();
				ToastUtil.showToast(this_, "再按一次退出！");
			}else {
				new Thread(new Runnable() {
					@Override
					public void run() {
						//退出记录日志  20140507 -wuwq
						try {
							ApiProcess4Leyue.getInstance(this_).exitLog(PreferencesUtil.getInstance(this_).getUserId(), UniqueIdUtils.getDeviceId(this_), UniqueIdUtils.getTerminalBrand(this_));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
				this_.finish();
			}
			return true;
		}
		return false;
	}
	
	@Override
	protected void onDestroy() {
        mainTabHost.removeAllViews();
        mBookShelfView.onDestroy();
        mainTabHost = null;
        AccountManager.getInstance().unRegisterObserver(mAccountObserver);
		super.onDestroy();
		//System.exit(0);
	}
}
