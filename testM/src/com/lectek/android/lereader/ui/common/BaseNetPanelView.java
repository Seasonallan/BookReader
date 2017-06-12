package com.lectek.android.lereader.ui.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.animation.ViewAnimDecorator;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.binding.model.common.FrameNetDataViewModel;
import com.lectek.android.lereader.lib.utils.ApnUtil;
import com.lectek.android.lereader.lib.utils.ClientInfoUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.IRetryClickListener;
import com.lectek.android.lereader.ui.specific.ActivityChannels;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.DialogUtil.CancelListener;
import com.lectek.android.lereader.utils.DialogUtil.ConfirmListener;

public abstract class BaseNetPanelView extends BasePanelView implements INetLoadView{
	private static final String TAG = "BasePanelView";
	private View mLoadingView;
	private TextView mLoadingViewTipTV;
	private Button userOprButton;
	private LinearLayout userSettingLay,loadingProgressLay;
	private ImageView mTipImageView;
	private View mProgressBar;
	private FrameNetDataViewModel mFrameNetDataViewModel;
	private Dialog mNetSettingDialog;
	private IRetryClickListener mRetryClickListener;

	public BaseNetPanelView(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		LogUtil.i(TAG, "Start init");
		setBackgroundResource(R.drawable.window_background);
		mFrameNetDataViewModel = new FrameNetDataViewModel(getContext(),this);
		mLoadingView = newLoadingView();
		loadingProgressLay = getLoadingProgressView();
		mLoadingViewTipTV = getLoadingViewTipTV();
		mProgressBar = getLoadingProgress();
		mTipImageView = getTipImgView();
		userOprButton = getUserOprBut();
		userSettingLay = getUserSettingLay();
		addView(mLoadingView,new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT));
		LogUtil.i(TAG, "End init");
	}

	/**
	 * 获取加载界面
	 * @return
	 */
	protected View newLoadingView(){
		View view = LayoutInflater.from(getContext()).inflate(R.layout.load_and_retry_lay, null);
		return view;
	};
	
	/**
	 * 获得提示图片 ImageView
	 * @return
	 */
	protected ImageView getTipImgView(){
		return (ImageView) mLoadingView.findViewById(R.id.tip_img);
	}
	
	/**
	 * 获得加载过渡界面
	 * @return
	 */
	protected LinearLayout getLoadingProgressView(){
		return (LinearLayout) mLoadingView.findViewById(R.id.progress_view);
	}
	
	/**
	 * 获得加载界面文字提示TextView
	 * @return
	 */
	protected TextView getLoadingViewTipTV(){
		return (TextView) mLoadingView.findViewById(R.id.load_tip_tv);
	}
	
	/**
	 * 获取用户操作按钮（网络设置、重试）
	 * @return
	 * */
	protected Button getUserOprBut(){
		return (Button) mLoadingView.findViewById(R.id.user_opr_but_new);
	}
	
	/**
	 * 操作设置（用于网络设置/重试）
	 * @return
	 * */
	protected LinearLayout getUserSettingLay(){
		return (LinearLayout) mLoadingView.findViewById(R.id.opr_setting_lay_new);
	}
	
	/**
	 * 获得进度条
	 * @return
	 */
	protected View getLoadingProgress(){
		return mLoadingView.findViewById(R.id.loading_v);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(mLoadingView != null){
			mLoadingView.clearAnimation();
		}
	}
	
	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		if(getChildCount() > 0 && ( index == -1 || index == getChildCount() )){
			index = getChildCount() - 1;
		}
		super.addView(child, index, params);
	}
	
	public boolean checkNetWrok() {
		if(!ApnUtil.isNetAvailable(getContext())){
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
	
	@Override
	public void showNetSettingView() {
		onShowNetSettingView(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ClientInfoUtil.gotoSettingActivity(getContext());
			}
		}, new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityChannels.gotoMainActivityDownload(getContext());
			}
		},false);
	}
	
	@Override
	public void showNetSettingDialog() {
		onShowNetSettingView(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ClientInfoUtil.gotoSettingActivity(getContext());
			}
		}, new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityChannels.gotoMainActivityDownload(getContext());
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
	
	protected void onShowRetryView(OnClickListener retryListener) {
		showProgressLayOrNot(false);
		userOprButton.setOnClickListener(retryListener);
		userOprButton.setText(R.string.btn_text_retry);
		ViewAnimDecorator.showView(mLoadingView,false);
	}
	
	protected void onShowLoadingView() {
		showProgressLayOrNot(true);
		mLoadingViewTipTV.setText(R.string.waitting_dialog_load_tip);
		ViewAnimDecorator.showView(mLoadingView,false);
	}
	
	protected void onShowNetSettingView(final OnClickListener netSettingListener,
			OnClickListener gotoBookShelfListener,boolean isDialog) {
		if(isDialog){
			if(getContext() instanceof Activity){
				if(((Activity) getContext()).isFinishing()){
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
				mNetSettingDialog = DialogUtil.createSpecialConfirmDialog2((Activity) getContext(),
						R.string.conection_unavailable, sureListener, cancelListener,
						R.string.btn_text_now_setting, R.string.btn_text_next_setting);
				mNetSettingDialog.show();
			}
		}else{
			showProgressLayOrNot(false);
			mTipImageView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.icon_check_net_tip));
			userOprButton.setText(R.string.btn_text_now_setting);
			userOprButton.setOnClickListener(netSettingListener);
			ViewAnimDecorator.showView(mLoadingView,false);
		}
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
	
	protected void onHiedNetSettingView() {
		if(getContext() instanceof Activity){
			if(((Activity) getContext()).isFinishing()){
				return;
			}
			if(mNetSettingDialog != null && mNetSettingDialog.isShowing()){
				mNetSettingDialog.dismiss();
			}
		}
	}
	
	protected void onHideLoadAndRetryView() {
		onHiedNetSettingView();
		if(mLoadingView.getVisibility() == View.VISIBLE){
			ViewAnimDecorator.hideView(mLoadingView,isViewAnimEnabled());
		}
	}
	
	protected boolean isViewAnimEnabled(){
		return false;
	}
	
	/**
	 * 设置提示图片
	 * @param drawable
	 */
	protected void setTipImageResource(int drawable){
		mTipImageView.setBackgroundDrawable(getContext().getResources().getDrawable(drawable));
	}
	
	/**隐藏操作按钮*/
	protected void setOprBtnGone(){
		userOprButton.setVisibility(View.GONE);
	}

	public void setmRetryClickListener(IRetryClickListener mRetryClickListener) {
		this.mRetryClickListener = mRetryClickListener;
	}
	
}
