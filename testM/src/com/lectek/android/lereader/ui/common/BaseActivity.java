package com.lectek.android.lereader.ui.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.app.BaseContextActivity;
import com.lectek.android.app.ITitleBar;
import com.lectek.android.app.SimpleMenuItem;
import com.lectek.android.lereader.animation.ViewAnimDecorator;
import com.lectek.android.lereader.binding.model.BaseViewModel;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.binding.model.common.FrameNetDataViewModel;
import com.lectek.android.lereader.lib.utils.ApnUtil;
import com.lectek.android.lereader.lib.utils.ClientInfoUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.ui.ILoadDialog;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.IRequestResultViewNotify;
import com.lectek.android.lereader.ui.IRetryClickListener;
import com.lectek.android.lereader.ui.common.dialog.CommonDialog;
import com.lectek.android.lereader.ui.specific.ActivityChannels;
import com.lectek.android.lereader.ui.specific.ChildViewExitGestureDetectorListener;
import com.lectek.android.lereader.ui.specific.ChildViewExitGestureDetectorListener.ChildViewSwitcher;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.DialogUtil.CancelListener;
import com.lectek.android.lereader.utils.DialogUtil.ConfirmListener;
import com.lectek.android.lereader.utils.PathRecordUtil;

/**
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-11-22
 */
public abstract class BaseActivity extends BaseContextActivity implements INetLoadView, ILoadDialog, IRequestResultViewNotify{
	protected View mLoadingView;
	protected Dialog mLoadingDialog;
	private TextView mLoadingViewTipTV;
	private Button userOprButton;
	private LinearLayout userSettingLay,loadingProgressLay;
	private ImageView mTipImageView;
	private View mProgressBar;
//	protected FrameLayout contentFrameLayout;
	protected FrameLayout contentFrameLayout;
	private ViewGroup mRootView;
	private ViewGroup mFullScreenLay;
	private ViewGroup mTitleCenterLay;
	private View mLeftBut;
	private View mRightBut;
	private TextView mLeftButTV;
	private TextView mRightButTV;
	private ImageView mLeftButIV;
	private ImageView mRightButIV;
	private View mLeftLine;
	private View mRightLine;
	private TextView mTitleTV;
	private FrameNetDataViewModel mFrameNetDataViewModel;
	private Dialog mNetSettingDialog;
	private IRetryClickListener mRetryClickListener;
	//
	/*private Animation mRotateUpAnimation;
	private Animation mRotateDownAnimation;
	private TextView mActionText;
	private TextView mTimeText;
	private View mProgress;
	private View mActionImage;
	public boolean mInLoading = false;*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initVar();
		resetView(savedInstanceState);
		PathRecordUtil.getInstance().restoreInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		PathRecordUtil.getInstance().saveInstanceState(outState);
	}
	
	@Override
	public void onUserLoginStateChange(boolean isLogin) {
		super.onUserLoginStateChange(isLogin);
	}

	protected boolean isTitleBackBtnEnabled(){
		return true;
	}
	
	protected boolean needLoadView(){
		return true;
	}

	/*protected void initPullView() {
		mRotateUpAnimation = AnimationUtils.loadAnimation(this_,
				R.anim.rotate_up);
		mRotateDownAnimation = AnimationUtils.loadAnimation(this_,
				R.anim.rotate_down);
		contentFrameLayout = (PullRefreshLayout) findViewById(R.id.activity_content_lay);
//		contentFrameLayout.setOnActionPullListener(this);
//		contentFrameLayout.setOnPullStateChangeListener(this);
		
		mProgress = findViewById(android.R.id.progress);
		mActionImage = findViewById(android.R.id.icon);
		mActionText = (TextView) findViewById(R.id.pull_note);
		mTimeText = (TextView) findViewById(R.id.refresh_time);

		mTimeText.setText(R.string.note_not_update);
		mActionText.setText(R.string.note_pull_down);
	}*/
	
	protected void resetView(Bundle savedInstanceState){
		setContentView(R.layout.common_activity_layout);
//		drawTopBarBottomLine();
		mFrameNetDataViewModel = new FrameNetDataViewModel(this,this);
		mFullScreenLay = (ViewGroup) findViewById(R.id.full_screen_lay);
		mRootView = (ViewGroup) findViewById(R.id.root_view);
//		initPullView();
		contentFrameLayout = (FrameLayout) findViewById(R.id.activity_content_lay);
		mTitleCenterLay = (ViewGroup) findViewById(R.id.body_title_center_lay);
		mLeftBut = findViewById(R.id.body_title_left_but);
		mLeftButTV = (TextView) findViewById(R.id.body_title_left_but_tv);
		mLeftButIV = (ImageView) findViewById(R.id.body_title_left_but_iv);
		mLeftLine = findViewById(R.id.body_title_left_line);
		mRightBut = findViewById(R.id.body_title_right_but);
		mRightButTV = (TextView) findViewById(R.id.body_title_right_but_tv);
		mRightButIV = (ImageView) findViewById(R.id.body_title_right_but_iv);
		mRightLine = findViewById(R.id.body_title_right_line);
		mTitleTV = (TextView) findViewById(R.id.body_title);
		mLeftBut.setOnClickListener(new OnClickListener() {
			MenuItem item;
			@Override
			public void onClick(View v) {
				if(item == null){
					item = new SimpleMenuItem(ITitleBar.MENU_ITEM_ID_LEFT_BUTTON);
				}
				onOptionsItemSelected(item);
			}
		});
		mRightBut.setOnClickListener(new OnClickListener() {
			MenuItem item;
			@Override
			public void onClick(View v) {
				if(item == null){
					item = new SimpleMenuItem(ITitleBar.MENU_ITEM_ID_RIGHT_BUTTON);
				}
				onOptionsItemSelected(item);
			}
		});
		String titleStr = getContentTitle();
		if(!TextUtils.isEmpty(titleStr)){
			mTitleTV.setText(titleStr);
		}
		
		setThemeStyle();
		mLoadingView = getLayoutInflater().inflate(R.layout.load_and_retry_lay, null);
		if(needLoadView()){
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.BELOW, R.id.header_lay);
			mRootView.addView(mLoadingView, params);
		}
		getHandleView();
		resetTitleBar();
		View newView = newContentView(savedInstanceState);
		if (newView != null) {
			contentFrameLayout.addView(newView);
		}
		initLoadingDialog();
//		initFlingHandle();
	}

	private ChildViewExitGestureDetectorListener mDetectorListener;
	private GestureDetector mDetector;
	private void initFlingHandle() {
		mDetectorListener = new ChildViewExitGestureDetectorListener(switcher);
		mDetector = new GestureDetector(this, mDetectorListener);
		if (mRootView!=null) {
			mRootView.requestFocus();
			mRootView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return mDetector.onTouchEvent(event);
				}
			});
		}
	}
	
	private ChildViewSwitcher switcher = new ChildViewSwitcher() {
		
		@Override
		public void flingHandle(boolean isLeft) {
			if (isLeft) {
				flingExitHandle();
			}
		}
	};
	
	protected void flingExitHandle() {
		// 子类覆盖实现
		LogUtil.e("---flingExitHandle---");
	}
	
	protected void getHandleView() {
		mLoadingViewTipTV = getLoadingViewTipTV();
		mProgressBar = getLoadingProgress();
		loadingProgressLay = getLoadingProgressView();
		userOprButton = getUserOprBut();
		userSettingLay = getUserSettingLay();
		mTipImageView = getTipImgView();
	}

	private void drawTopBarBottomLine() {
		CommonUtil.getSpecialRepeatLine(findViewById(R.id.header_bottom_line), this_);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(!super.onOptionsItemSelected(item) 
				&& ITitleBar.MENU_ITEM_ID_LEFT_BUTTON == item.getItemId()
				&& Integer.valueOf(R.drawable.back_btn).equals(mLeftButIV.getTag())){
			if(!onClickBackBtn()){
				finish();
			}
		}
		return false;
	}

	protected boolean onClickBackBtn(){
		return false;
	}
	
	protected void addFullScreenView(View view){
		if(view != null && view.getParent() == null){
			mFullScreenLay.addView(view,new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		}
	}
	
	protected void removeFullScreenView(View view){
		if(view != null && mFullScreenLay.equals(view.getParent())){
			mFullScreenLay.removeView(view);
		}
	}
	
	@Override
	public void setTitleBarEnabled(boolean isEnabled){
		if(isEnabled){
			findViewById(R.id.header_lay).setVisibility(View.VISIBLE);
			findViewById(R.id.header_bottom_line).setVisibility(View.VISIBLE);
			
		}else{
			findViewById(R.id.header_lay).setVisibility(View.GONE);
			findViewById(R.id.header_bottom_line).setVisibility(View.GONE);
		}
	}
	
	@Override
	public void setTitleView(View view) {
		super.setTitleView(view);
		if(view != null && view.getParent() == null){
			mTitleCenterLay.removeAllViews();
			mTitleCenterLay.addView(view);
		}
		
	}

	@Override
	public void setTitleContent(String titleStr){
		super.setTitleContent(titleStr);
		if(!TextUtils.isEmpty(titleStr)){
			mTitleTV.setText(titleStr);
		}
	}
	
	@Override
	public void setLeftButton(String tip, int icon) {
		super.setLeftButton(tip,icon);
		if(tip == null){
			tip = "";
		}
		mLeftButTV.setText(tip);
		if(icon > 0){
			mLeftButIV.setImageResource(icon);
		}else{
			mLeftButIV.setImageBitmap(null);
		}
		mLeftButIV.setTag(null);
	}
	
	@Override
	public void setRightButton(String tip, int icon) {
		super.setRightButton(tip,icon);
		if(tip == null){
			tip = "";
		}
		mRightButTV.setText(tip);
		if(icon > 0){
			mRightButIV.setImageResource(icon);
		}else{
			mRightButIV.setImageBitmap(null);
		}
	}
	
	@Override
	public void setLeftButtonEnabled(boolean isEnabled) {
		super.setLeftButtonEnabled(isEnabled);
		if(isEnabled){
			mLeftBut.setVisibility(View.VISIBLE);	//2.0.0版没有分割线
			mLeftLine.setVisibility(View.INVISIBLE);
		}else{
			mLeftBut.setVisibility(View.INVISIBLE);
			mLeftLine.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public void setRightButtonEnabled(boolean isEnabled) {
		super.setRightButtonEnabled(isEnabled);
		if(isEnabled){
			mRightBut.setVisibility(View.VISIBLE);
			mRightLine.setVisibility(View.INVISIBLE);	//2.0.0版没有分割线
		}else{
			mRightBut.setVisibility(View.INVISIBLE);
			mRightLine.setVisibility(View.INVISIBLE);
		}
	}
	
	public View getLeftBtnView(){
		return mLeftBut;
	}
	
	public View getRightBtnView(){
		return mRightBut;
	}
	
	@Override
	public void resetTitleBar() {
		super.resetTitleBar();
		mTitleCenterLay.removeAllViews();
		mTitleCenterLay.addView(mTitleTV);
		setRightButtonEnabled(false);
		setRightButton("", 0);
		if(isTitleBackBtnEnabled()){
			setLeftButtonEnabled(true);
			setLeftButton(null, R.drawable.back_btn);
			mLeftButIV.setTag(R.drawable.back_btn);
		}else{
			setLeftButton("", 0);
			setLeftButtonEnabled(false);
			mLeftButIV.setTag(null);
		}
	}
	
	@Override
	public void setHeadView(View view) {
		super.setHeadView(view);
		mLeftBut.setVisibility(View.GONE);
		mLeftLine.setVisibility(View.GONE);
		mRightBut.setVisibility(View.GONE);
		mRightLine.setVisibility(View.GONE);
		setTitleView(view);
	}
	
	protected abstract View newContentView(Bundle savedInstanceState);
	
	protected String getContentTitle(){
		return null;
	};
	
	protected void initVar(){
		
	}
	
	private void setThemeStyle() {
		getWindow().setBackgroundDrawableResource(R.drawable.window_bg);
		((TextView) findViewById(R.id.body_title)).setTextColor(Color.WHITE);
	}

	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		if (repeatCount >= 1) {
			return true;
		}
		return super.onKeyMultiple(keyCode, repeatCount, event);
	}
	
	/**
	 * 获得加载界面文字提示TextView
	 * @return
	 */
	protected TextView getLoadingViewTipTV(){
		return (TextView) mLoadingView.findViewById(R.id.load_tip_tv);
	}
	
	/**
	 * 获取网络设置按钮
	 * @return
	 * */
	protected Button getUserOprBut(){
		return (Button) mLoadingView.findViewById(R.id.user_opr_but_new);
	}
	
	/**
	 * 用户操作
	 * @return
	 * */
	protected LinearLayout getUserSettingLay(){
		return (LinearLayout) mLoadingView.findViewById(R.id.opr_setting_lay_new);
	}
	
	/**
	 * 获得加载过渡界面
	 * @return
	 */
	protected LinearLayout getLoadingProgressView(){
		return (LinearLayout) mLoadingView.findViewById(R.id.progress_view);
	}
	
	/**
	 * 获得提示图片 ImageView
	 * @return
	 */
	protected ImageView getTipImgView(){
		return (ImageView) mLoadingView.findViewById(R.id.tip_img);
	}
	
	protected View getLoadingProgress(){
		return mLoadingView.findViewById(R.id.loading_v);
	}
	
	public boolean checkNetWrok() {
		if(!ApnUtil.isNetAvailable(this_)){
			showNetSettingView();
			return false;
		}
		return true;
	}
	
	public boolean tryStartNetTack(INetAsyncTask lastTask) {
		return mFrameNetDataViewModel.tryStartNetTack(lastTask);
	}
	
	@Override
	public void registerNetworkChange(NetworkChangeListener l) {
		mFrameNetDataViewModel.registerNetworkChange(l);
	}
	
	public void dispatchNetworkChange(final boolean isAvailable) {
		mFrameNetDataViewModel.dispatchNetworkChange(isAvailable);
	}
	
	public void showRetryView() {
		mFrameNetDataViewModel.showRetryView();
	}
	
	/**
	 * 设置提示界面
	 * @param atipImg 提示的图标
	 * @param isNeedOprBtn 是否需要按钮
	 * @param btnText 按钮名字
	 * @param clickListener 按钮事件监听器
	 */
	protected void showTipView(tipImg atipImg, boolean isNeedOprBtn,String btnText,OnClickListener clickListener){
		setTipView(atipImg, isNeedOprBtn);
		showProgressLayOrNot(false);
		userOprButton.setOnClickListener(clickListener);
		userOprButton.setText(btnText);
		ViewAnimDecorator.showView(mLoadingView,false);
	}
	
	@Override
	public void showNetSettingView() {
		onShowNetSettingView(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ClientInfoUtil.gotoSettingActivity(this_);
			}
		}, new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityChannels.gotoMainActivityDownload(this_);
			}
		},false);
	}
	
	@Override
	public void showNetSettingDialog() {
		onShowNetSettingView(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ClientInfoUtil.gotoSettingActivity(this_);
			}
		}, new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityChannels.gotoMainActivityDownload(this_);
			}
		},true);
	}

	@Override
	public void showRetryView(final Runnable task) {
		onShowRetryView(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(task != null){
					if (mRetryClickListener!=null) {//新增界面需要对重试时 对请求作变更。提供处理接口
						mRetryClickListener.onRetryClick();
					}
					task.run();
				}
			}
		});
	}
	
	@Override
	public void showLoadView() {
		onShowLoadingView();
	}

	@Override
	public void hideLoadView() {
		onHideLoadAndRetryView();
	}
	
	@Override
	public void hideNetSettingView() {
		onHiedNetSettingView();
	}
	
	@Override
	public void hideRetryView() {
		onHideLoadAndRetryView();
	}

	@Override
	public void onNetworkChange(boolean isAvailable) {
		super.onNetworkChange(isAvailable);
		dispatchNetworkChange(isAvailable);
	}
	
	/**
	 * 提示界面 区域显示控制
	 * @param falseWillShowNetSettingLay 如果为false 则显示网络设置部分
	 */
	private void showProgressLayOrNot(boolean falseWillShowNetSettingLay){
		if (falseWillShowNetSettingLay) {
			loadingProgressLay.setVisibility(View.VISIBLE);
			userSettingLay.setVisibility(View.GONE);
		}else {
			loadingProgressLay.setVisibility(View.GONE);
			userSettingLay.setVisibility(View.VISIBLE);
		}
	}
	
	protected void onShowRetryView(OnClickListener retryListener) {
		showProgressLayOrNot(false);
		userOprButton.setOnClickListener(retryListener);
		userOprButton.setText(R.string.btn_text_retry);
		ViewAnimDecorator.showView(mLoadingView,false);
	}
	
	protected void onShowLoadingView() {
		showProgressLayOrNot(true);
		mLoadingViewTipTV.setText(R.string.waitting_dialog_load_tip);
//		contentFrameLayout.setVisibility(View.GONE);
		ViewAnimDecorator.showView(mLoadingView,false);
	}
	
	protected void onShowNetSettingView(final OnClickListener netSettingListener,
			OnClickListener gotoBookShelfListener,boolean isDialog) {
		if(isDialog){
			if (isFinishing()) {
				return;
			}
			if (mNetSettingDialog != null) {
				if (!mNetSettingDialog.isShowing()) {
					mNetSettingDialog.show();
				}
				return;
			}
			ConfirmListener sureListener = new ConfirmListener() {
				@Override
				public void onClick(View v) {
					netSettingListener.onClick(v);
				}
			};
			CancelListener cancelListener = new CancelListener() {
				@Override
				public void onClick(View v) {
				}
			};
			mNetSettingDialog = DialogUtil.createSpecialConfirmDialog2(this_,
					R.string.conection_unavailable, sureListener, cancelListener,
					R.string.btn_text_now_setting, R.string.btn_text_next_setting);
			mNetSettingDialog.show();
		}else{
			showProgressLayOrNot(false);
			mTipImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_check_net_tip));
			userOprButton.setText(R.string.btn_text_now_setting);
			userOprButton.setOnClickListener(netSettingListener);
			ViewAnimDecorator.showView(mLoadingView,false);
		}
	}
	
	protected void onHiedNetSettingView() {
		if (isFinishing()) {
			return;
		}
		if(mNetSettingDialog != null && mNetSettingDialog.isShowing()){
			mNetSettingDialog.dismiss();
		}
	}
	
	protected void onHideLoadAndRetryView() {
		onHiedNetSettingView();
		contentFrameLayout.setVisibility(View.VISIBLE);
		if(mLoadingView.getVisibility() == View.VISIBLE){
			ViewAnimDecorator.hideView(mLoadingView,isViewAnimEnabled());
		}
	}
	
	protected boolean isViewAnimEnabled(){
		return false;
	}

	@Override
	public boolean bindDialogViewModel(Context context, BaseViewModel baseViewModel) {
		return CommonDialog.bindViewModel(context, baseViewModel);
	}

	@Override
	public int getRes(String type) {
		return 0;
	}
	
	/**
	 * 设置提示图片
	 * @param drawable
	 */
	protected void setTipImageResource(int drawable){
//		mTipImageView.setBackgroundDrawable(getResources().getDrawable(drawable));
	}
	
	/**隐藏操作按钮*/
	protected void setOprBtnGone(){
		userOprButton.setVisibility(View.GONE);
	}

	@Override
	public void setTipView(tipImg tipImg, Boolean isNeedOprBtn) {
		switch (tipImg) {
		case request_fail:
			setTipImageResource(R.drawable.icon_request_fail_tip);
			break;
		case no_info:
			setTipImageResource(R.drawable.icon_no_info_tip);
			break;
		case goto_book_city:
			setTipImageResource(R.drawable.icon_goto_bookcity_tip);
			break;
		case no_monthly_service:
			setTipImageResource(R.drawable.icon_no_monthly_service_tip);
			break;
		case no_target_book:
			setTipImageResource(R.drawable.icon_no_target_book_tip);
			break;
		case non_purchased_book:
			setTipImageResource(R.drawable.icon_non_purchased_book_tip);
			break;
		case no_book:
			setTipImageResource(R.drawable.icon_no_book_tip);
			break;

		default:
			break;
		}
		if(!isNeedOprBtn){
			setOprBtnGone();
		}else{
			userOprButton.setVisibility(View.VISIBLE);
		}
	}
	
	public void setmRetryClickListener(IRetryClickListener mRetryClickListener) {
		this.mRetryClickListener = mRetryClickListener;
	}
	
	public void initLoadingDialog(){
//		View loadView = LayoutInflater.from(this_).inflate(R.layout.loading_data_lay, null);
//		mLoadingDialog = CommonUtil.getTransparentDialog(this_, loadView);
		
		View loadView = LayoutInflater.from(this_).inflate(R.layout.loading_data_lay, null);
		mLoadingDialog = DialogUtil.getLoadingDialog(CommonUtil.getRealActivity(this_), loadView);
	}
	
	@Override
	public void showLoadDialog() {
		if(mLoadingDialog != null && !mLoadingDialog.isShowing()){
			mLoadingDialog.show();
		}
	}
	
	@Override
	public void hideLoadDialog() {
		if(mLoadingDialog != null && mLoadingDialog.isShowing()){
			mLoadingDialog.dismiss();
		}
		
	}
}
