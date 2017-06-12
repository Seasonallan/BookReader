package com.lectek.android.lereader.ui.pay;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.ui.specific.ActivityChannels;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.FontUtil;
import com.lectek.android.lereader.utils.IMSIUtil;
import com.lectek.android.lereader.utils.ToastUtil;
import com.lectek.android.lereader.widgets.adapter.RechargeSmsAdapter;
import com.lectek.android.widget.ReaderGridView;


public class RechargeSmsActivity extends BaseActivity {
	public static final String TAG = RechargeSmsActivity.class.getSimpleName();
	private ReaderGridView mReadticketGrid;
	private RechargeSmsAdapter mAdapter;
	private ArrayList<RechargeSms> mRechargeSmsList;
	private View mContentView;
	private Dialog mDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mContentView = LayoutInflater.from(this).inflate(R.layout.recharge_sms_layout, null);
		mReadticketGrid = (ReaderGridView) mContentView.findViewById(R.id.recharge_sms_grid);
		mRechargeSmsList = new ArrayList<RechargeSms>();
		mAdapter = new RechargeSmsAdapter(this, mRechargeSmsList);
		mReadticketGrid.setAdapter(mAdapter);
		mReadticketGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(IMSIUtil.MOBILE_TYPE_CHINA_TELECOM == IMSIUtil.getMobileType(getApplicationContext())){
					RechargeSms item = (RechargeSms) parent.getItemAtPosition(position);
					showSmsOrderDialog(item);
				}else{
					ToastUtil.showToast(getApplicationContext(), R.string.account_sms_not_support_recharge);
				}
			}
		});
		netHttp();
		return mContentView;
	}

	protected void showSmsOrderDialog(final RechargeSms item) {
		String forNum ;
		String fromNum = getString(R.string.account_recharge_sms_my_phone);
		final Dialog dialog = newRechargeDialog(this, R.layout.dialog_confirmation_of_recharge_layout, getString(R.string.account_confirmation_of_recharge));
//		final Dialog dialog = CommonUtil.getCommonDialog(this, R.layout.dialog_confirmation_of_recharge_layout,
//				R.string.account_confirmation_of_recharge);
		final LinearLayout dialogTitleLay = (LinearLayout)dialog.findViewById(R.id.title_lay);
		final LinearLayout rechargeInfoLay = (LinearLayout)dialog.findViewById(R.id.recharge_info_lay);
		final LinearLayout rechargeLoadingLay = (LinearLayout)dialog.findViewById(R.id.recharge_loading_lay);
		final LinearLayout rechargeBtnLay = (LinearLayout)dialog.findViewById(R.id.recharge_btn_lay);
		final TextView rechargeErrorTipTV = (TextView)dialog.findViewById(R.id.recharge_error_tip_tv);
		final TextView rechargeLoadingTipTV = (TextView)dialog.findViewById(R.id.recharge_loading_tip);

		TextView forNumTV = (TextView) dialog.findViewById(R.id.recharge_for_number);
		TextView fromNumTV = (TextView) dialog.findViewById(R.id.recharge_from_number);
		TextView readpointTV = (TextView) dialog.findViewById(R.id.recharge_readpoint_count);
		TextView priceTV = (TextView) dialog.findViewById(R.id.recharge_sms_price);
		
		forNum = PreferencesUtil.getInstance(MyAndroidApplication.getInstance()).getPhoneNum();;
		if(forNum == null){
			forNum = "";
		}
		final String tempForNum = forNum;
		forNumTV.setText(getString(R.string.account_recharge_for_number, forNum));
		fromNumTV.setText(getString(R.string.account_recharge_from_number, fromNum));
		if(TextUtils.isEmpty(item.mReadticketCount)){
			readpointTV.setVisibility(View.GONE);
		}else{
			String countStr = getString(R.string.account_recharge_readpoint_count, item.mReadticketCount);
			SpannableStringBuilder ssBuilder = FontUtil.textSpannableStringBuilder(false, countStr, getResources().getColor(R.color.red), 6, countStr.length() - 2);
			readpointTV.setText(ssBuilder);
		}
		if(TextUtils.isEmpty(item.mSmsPrice)){
			priceTV.setVisibility(View.GONE);
		}else{
			String countStr = getString(R.string.account_recharge_sms_price, item.mSmsPrice);
			SpannableStringBuilder ssBuilder = FontUtil.textSpannableStringBuilder(false, countStr, getResources().getColor(R.color.red), 3, countStr.length() - 1);
			priceTV.setText(ssBuilder);
		}
		OnClickListener rOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		};
		
		final Runnable action = new Runnable() {
			Runnable actionRunnable = this;
			@Override
			public void run() {
				if(dialog != null && !dialog.isShowing()){
					dialog.show();
				}
				dialogTitleLay.setVisibility(View.GONE);
				rechargeInfoLay.setVisibility(View.GONE);
				rechargeBtnLay.setVisibility(View.GONE);
				rechargeErrorTipTV.setVisibility(View.GONE);
				rechargeLoadingLay.setVisibility(View.VISIBLE);
				//还原加载提示
				rechargeLoadingTipTV.setText(getString(R.string.loading_sms_buy_send_sms_tip));
				//TODO:短代购买
				/*final ITerminableThread terminableThread = ThreadFactory.createTerminableThread(new Runnable() {
					@Override
					public void run() {
						//处理订购完书籍的UI更新
						HandlerRunnable handlerRunnable = new HandlerRunnable() {

							@Override
							public boolean run(String resultCode) {
								boolean isSuccess = false;
								if(resultCode.equals(ResponseResultCodeTY.STATUS_OK)){
									isSuccess = true;
									UserInfoCacheManage.getInstance().setCacheState(true);
									sendBroadcast(new Intent(AppBroadcast.ACTION_RECHARGE_SUCCEED));
									DataCache.getInstance().setPayAccount(
											tempForNum);
									DataCache.getInstance().setPayMoney(
											item.mSmsPrice);
									DataCache.getInstance().setPayWay(DataCache.TAG_PAY_WAY_MESSAGE_PAY);
								}
								
								if(!isFinishing()){
									if (dialog != null && dialog.isShowing()) {
										LogUtil.e("RechargeSms-->", "dialog has close");
										dialog.dismiss();
									}
									showResultDialog(isSuccess, tempForNum, item.mReadticketCount, actionRunnable);
								}
								return false;
							}

							@Override
							public void excuteReadPointNoEnough() {
							}

							@Override
							public void excutePayError(String resultCode) {
								
							}
						};
						ProcessOrderUtil.dealSmsRechargeContent(this_, item.mReadticketCount, handlerRunnable, Tag);
					}
				});
				terminableThread.start();*/
			}
		};
		
		OnClickListener lOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				action.run();
			} 
		};
		
		DialogUtil.dealDialogBtnWithPrimarySecondary((Button)(dialog.findViewById(R.id.dialog_comment_ok)), R.string.btn_text_recharge, lOnClickListener, 
				(Button)(dialog.findViewById(R.id.dialog_comment_cancel)), R.string.btn_text_cancel, rOnClickListener);
		dialog.show();
	}

	public static Dialog newRechargeDialog(final Context context, final int layoutRes,final String title) {
		View view = LayoutInflater.from(context).inflate(layoutRes, null);
		Dialog dialog = new PayDialog(context, view);
		FrameLayout contentLayout = (FrameLayout) dialog.findViewById(R.id.dialog_content_layout);
		contentLayout.addView(view);
		TextView titleTV = (TextView) dialog.findViewById(R.id.dialog_title);
		titleTV.setText(title);
		return dialog;
	}
	
	private static class PayDialog extends Dialog{
		private Activity mActivity;
		private Dialog this_ = this;
		private OnDismissListener mExternalOnDismissListener;
		private OnShowListener mExternalOnShowListener;
		private BroadcastReceiver mLoadingStateBroadcast;
		private View dialogContentChildView;
		private TextView rechargeLoadingTipTV;
		
		public PayDialog(Context context, View dialogContentChildView) {
			super(context, R.style.CustomDialog);
			if(context instanceof Activity){
				mActivity = (Activity) context;
			}
			this.dialogContentChildView = dialogContentChildView;
			setContentView(R.layout.dialog_common_layout);
			initView();
		}
		
		public void initView(){
			rechargeLoadingTipTV = (TextView)dialogContentChildView.findViewById(R.id.recharge_loading_tip);
		}
		
		private OnDismissListener mOnDismissListener = new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (mExternalOnDismissListener != null) {
					mExternalOnDismissListener.onDismiss(this_);
				}
				if(mLoadingStateBroadcast != null){
					getContext().unregisterReceiver(mLoadingStateBroadcast);
					mLoadingStateBroadcast = null;
				}
			}
		};

		private OnShowListener mOnShowListener = new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				if (mExternalOnShowListener != null) {
					mExternalOnShowListener.onShow(this_);
				}
				if(mLoadingStateBroadcast == null){
					//XXX 使用接口将实现部分移动外面
					mLoadingStateBroadcast = new BroadcastReceiver(){
						@Override
						public void onReceive(Context context, Intent intent) {
							if(intent != null){
								if(AppBroadcast.ACTION_SMS_BUY_LOADING_MESSAGE.equals(intent.getAction())){
									long tempTimeTag3 = intent.getExtras().getLong(SMS_BUY_RESULT_LOADING);
									if(tempTimeTag3 > 0 && tempTimeTag3 == TAG_TIME){//短信发送成功，加载结果中
										rechargeLoadingTipTV.setText(context.getString(R.string.loading_sms_receive_buy_result_tip));
									}
								}
							}
						}
					};
					getContext().registerReceiver(mLoadingStateBroadcast,new IntentFilter(AppBroadcast.ACTION_SMS_BUY_LOADING_MESSAGE));
				}
			}
		};
        public static final long TAG_TIME = System.currentTimeMillis();
        public static final String SMS_BUY_RESULT_LOADING = "sms_buy_result";

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			super.setOnDismissListener(mOnDismissListener);
			super.setOnShowListener(mOnShowListener);
		}

		@Override
		public void setOnDismissListener(OnDismissListener listener) {
			mExternalOnDismissListener = listener;
		}

		@Override
		public void setOnShowListener(OnShowListener listener) {
			mExternalOnShowListener = listener;
		}

		@Override
		public void show() {
			if(mActivity != null){
				if(!mActivity.isFinishing()){
					super.show();
				}
			}else{
				super.show();
			}
		}

		@Override
		public void dismiss() {
			if(mActivity != null){
				if(!mActivity.isFinishing()){
					super.dismiss();
				}
			}else{
				super.dismiss();
			}
		}
	}
	
	protected void showResultDialog(final boolean isSucess, String forNum, String readticketCount, final Runnable rechargeRunnable) {
		int titleId;
		if(isSucess){
			titleId = R.string.account_recharge_sms_result_ok_title;
		}else{
			titleId = R.string.account_recharge_sms_result_failure_title;
		}
		mDialog = CommonUtil.getCommonDialog(this, R.layout.dialog_recharge_result_layout,
				titleId);
		TextView resultTv = (TextView) mDialog.findViewById(R.id.result_tv);
		Button confirmBtn = (Button) mDialog.findViewById(R.id.i_see_btn);
		Button goToBookCityBtn = (Button)mDialog.findViewById(R.id.go_to_book_city_btn);
		if(isSucess){
			resultTv.setText(getString(R.string.account_recharge_sms_result_ok, forNum, readticketCount));
		}else{
			resultTv.setText(R.string.account_recharge_sms_result_failure);
		}
		
		OnClickListener lOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mDialog != null && mDialog.isShowing()){
					mDialog.dismiss();
					LogUtil.e("RechargeSmsActivity", "mDialog has cloase");
				}
			}
		};
		
		OnClickListener rOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mDialog != null && mDialog.isShowing()){
					mDialog.dismiss();
					if(isSucess){
						ActivityChannels.gotoMainActivityBookCity(this_);
						finish();
						LogUtil.e("RechargeSmsActivity", "go to book city");
					}else{
						//重试方法
						if(rechargeRunnable != null){
							rechargeRunnable.run();
						}
					}
				}
			}
		};
		
		if(isSucess){
			DialogUtil.dealDialogBtn(confirmBtn, R.string.btn_text_i_see, lOnClickListener, 
					goToBookCityBtn, R.string.btn_text_go_to_book_city, rOnClickListener);
		}else{
			DialogUtil.dealDialogBtn(confirmBtn, R.string.btn_text_cancel, lOnClickListener, 
					goToBookCityBtn, R.string.btn_text_retry, rOnClickListener);
		}
		
		if(!isFinishing() && mDialog != null && !mDialog.isShowing()){
			mDialog.show();
		}
	}

	@Override
	protected void onDestroy() {
		if(mDialog != null && mDialog.isShowing()){
			mDialog.dismiss();
		}
		super.onDestroy();
	}
	
	private void netHttp() {
		mRechargeSmsList.add(new RechargeSms("3.00", "300",
				R.drawable.read_point_green_bg));
		mRechargeSmsList.add(new RechargeSms("5.00", "500",
				R.drawable.read_point_brown_bg));
		mRechargeSmsList.add(new RechargeSms("10.00", "1000",
				R.drawable.read_point_gold_bg));
		mRechargeSmsList.add(new RechargeSms("20.00", "2000",
				R.drawable.read_point_red_bg));
		if (null != mRechargeSmsList && 0 < mRechargeSmsList.size()) {
			mAdapter.notifyDataSetChanged();
			mReadticketGrid.setVisibility(View.VISIBLE);
		} 
	}
	
	public class RechargeSms {
		public String mSmsPrice;
		public String mReadticketCount;
		public int mReadtickBg;
		public RechargeSms(String smsPrice, String readticketCount, int readTickBg) {
			this.mReadticketCount = readticketCount;
			this.mSmsPrice = smsPrice;
			this.mReadtickBg = readTickBg;
		}
	}

	@Override
	protected String getContentTitle() {
		return getString(R.string.account_recharge_sms_title);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
