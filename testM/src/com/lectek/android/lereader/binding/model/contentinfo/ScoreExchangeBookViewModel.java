package com.lectek.android.lereader.binding.model.contentinfo;

import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.SpanObservable;
import gueei.binding.observables.StringObservable;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.lectek.android.LYReader.R;
import com.lectek.android.LYReader.pay.PayUtil;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.lereader.analysis.MobclickAgentUtil;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.binding.model.user.UserInfoModelLeyue;
import com.lectek.android.lereader.lib.api.response.ScoreExchangeBookResultInfo;
import com.lectek.android.lereader.lib.recharge.IDealPayRunnable;
import com.lectek.android.lereader.lib.recharge.IPayHandler;
import com.lectek.android.lereader.lib.recharge.PayConst;
import com.lectek.android.lereader.lib.share.entity.UmengShareInfo;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.openapi.ResponseCode;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.net.response.ScoreUploadResponseInfo;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.permanent.ShareConfig;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.share.ShareWeiXin;
import com.lectek.android.lereader.share.ShareYiXin;
import com.lectek.android.lereader.share.entity.MutilMediaInfo;
import com.lectek.android.lereader.share.util.UmengShareUtils;
import com.lectek.android.lereader.share.util.UmengShareUtils.YXHanlder;
import com.lectek.android.lereader.storage.dbase.UserInfoLeyue;
import com.lectek.android.lereader.storage.dbase.UserScoreInfo;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.IBaseUserAction;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.ToastUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.sso.UMSsoHandler;

/**
 * @description
	积分兑书
 * @author chendt
 * @date 2013-12-27
 * @Version 1.0
 * @SEE ScoreExchangeBookActivity
 */
public class ScoreExchangeBookViewModel extends BaseLoadNetDataViewModel implements YXHanlder,INetAsyncTask {
	
	private UserInfoModelLeyue mUserInfoModel;
//	private ScoreExchangeBookModel mScoreExchangeBookModel;
	private ScoreUploadModel mUploadModel;
	
	private final String EVENT_POINT_EXCHANGE_BOOK_ID = "PointExchangeBook";
	
	private UmengShareUtils utils = null;
	private boolean isShareQZone = false;
	
	private String mUserId;
	private String mAccount;
	private String mPassword;
	private String mPrice;
	private UserInfoLeyue mDataSource;
	private ContentInfoLeyue mContentInfo;
	public final StringObservable bCurrentPointText = new StringObservable();
	public final StringObservable bConsumePointText = new StringObservable();
	public final StringObservable bGainPointText = new StringObservable(getContext().getString(R.string.text_share_can_gain_more_point));
	public final StringObservable bPointExchangeBookText = new StringObservable(getContext().getString(R.string.btn_point_not_enough));
	public final BooleanObservable bPointExchangeBookEnable = new BooleanObservable(false);
	public final SpanObservable bGainPointSpan = new SpanObservable(new UnderlineSpan());
	private ScoreExchangeBookResultInfo resultInfo;
	private RechargeUserAction mUserAction;
	private PopupWindow popupWindow;
	private float currentScore = 0;
	private int bookScore = 0;
	
	private IPayHandler mPayHandler;
	
	public ScoreExchangeBookViewModel(Context context, INetLoadView loadView,RechargeUserAction mUserAction,
			ContentInfoLeyue info) {
		super(context, loadView);
		this.mUserAction = mUserAction;
		mContentInfo = info;
		if(mContentInfo != null)
			UmengShareUtils.contentUrl = LeyueConst.WX_YYB_PATH+mContentInfo.getBookId();
		mUserInfoModel = new UserInfoModelLeyue();
		mUserInfoModel.addCallBack(this);
//		mScoreExchangeBookModel = new ScoreExchangeBookModel();
//		mScoreExchangeBookModel.addCallBack(this);
		mUploadModel = new ScoreUploadModel();
		mUploadModel.addCallBack(this);
	}
	
	
	public final OnClickCommand bPointExchangeBookClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			if (currentScore<bookScore) {
				ToastUtil.showToast(getContext(), "积分不足，分享可获得更多积分！");
			}else {
//				mScoreExchangeBookModel.start(mContentInfo.getBookId(),
//						bookScore,mContentInfo.getBookName());
				
				mPayHandler = PayUtil.dealPay((Activity)getContext(), PayConst.PAY_TYPE_LEREADER_READ_POINT);
				showLoadView();
				mPayHandler.execute(new IDealPayRunnable() {
					
					@Override
					public void unbindService(ServiceConnection conn) {}
					
					@Override
					public void startActivity(Intent intent) {}
					
					@Override
					public void onUnregisterSmsReceiver(BroadcastReceiver receiver) {}
					
					@Override
					public void onRegisterSmsReceiver(String action, BroadcastReceiver receiver) {}
					
					@Override
					public void onPayComplete(boolean success, int resultCode,String resultMsg, Object resultData) {
						
						hideLoadView();
						
						if(mPayHandler == null || mPayHandler.isAbort()) {
							return;
						}
						
						mPayHandler.abort();
						mPayHandler = null;
						resultInfo = (ScoreExchangeBookResultInfo) resultData;
						if(resultInfo != null) {
							if (resultInfo.isSuccess()) {
								ToastUtil.showToast(getContext(), "兑换成功！");
								updateViewByScore(resultInfo.getUsableScore());
								mUserAction.exchangeSuccess();
								//修改书架当前书籍为已订购书籍
								DownloadPresenterLeyue.updateDownloadinfoFullVersionState(true, mContentInfo.getBookId());
							}else {
								switch (resultInfo.getCode()) {
								case ResponseCode.SCORE_FORMAT_ERROR:
									throw new NullPointerException(getContext().getString(R.string.reward_point_pay_result_invalid_params));
								case ResponseCode.SCORE_HAS_CHANGE:
									ToastUtil.showToast(getContext(), getContext().getString(R.string.reward_point_pay_result_invalid_price));
									mUserAction.reloadBookInfo();
									break;
								case ResponseCode.SCORE_NOT_ENOUGH:
									ToastUtil.showToast(getContext(), getContext().getString(R.string.reward_point_pay_result_not_enough_pint));
									break;
								case ResponseCode.BOOK_HAS_BOUGHT:
									ToastUtil.showToast(getContext(), getContext().getString(R.string.reward_point_pay_result_has_perchase));
									mUserAction.exchangeSuccess();
									break;
								default:
									ToastUtil.showToast(getContext(), getContext().getString(R.string.reward_point_pay_result_fail));
								}
							}
							return;
						}
						ToastUtil.showToast(getContext(), getContext().getString(R.string.reward_point_pay_result_fail));
					}
					
					@Override
					public Object onGetOrder(int payType) {
						return PayUtil.getRewardPayOrderInfo(getContext(), mContentInfo.getBookId(),
														bookScore,mContentInfo.getBookName());
					}
					
					@Override
					public void bindService(Intent service, ServiceConnection conn, int flags) {}
				});
				
                MobclickAgentUtil.uploadUmengMsg(EVENT_POINT_EXCHANGE_BOOK_ID,
                        "book_name", mContentInfo.getBookName(),
                        "book_type", mContentInfo.getBookType(),
                        "book_price", !TextUtils.isEmpty(mContentInfo.getPromotionPrice()) ? mContentInfo.getPromotionPrice() : !TextUtils.isEmpty(mContentInfo.getPrice()) ? mContentInfo.getPrice() : "0");
			}
		}
	};
	

	public final OnClickCommand bShareClick = new OnClickCommand() {

		@Override
		public void onClick(View v) {
			if (mContentInfo!=null) {
				utils = new UmengShareUtils();
				utils.baseInit((Activity)getContext());
				utils.setMailSubjectTitle("书籍分享");
				String imagePath = null;
				if (!TextUtils.isEmpty(mContentInfo.getCoverPath())) {
					imagePath = String.valueOf(mContentInfo.getCoverPath().hashCode());
				}
				Bitmap bitmap = null;
				if (!TextUtils.isEmpty(imagePath)) {
					bitmap = CommonUtil.getImageInSdcard(imagePath);
				}
				utils.setShareInfo((Activity)getContext(), new UmengShareInfo(
						((Activity)getContext()).getString(R.string.share_for_book, mContentInfo.getBookName(),UmengShareUtils.contentUrl), ""),bitmap);
				if (popupWindow == null) {
					popupWindow = utils.showPopupWindow((Activity)getContext(), v,ScoreExchangeBookViewModel.this,snsListener);
				}else {
					if (popupWindow.isShowing()) {
						popupWindow.dismiss();
					}
					UmengShareUtils.popWindowShow(v, popupWindow);
				}
			}else {
				ToastUtil.showToast(getContext(), "暂时无法分享，请重新进入该界面！");
			}
		}
	};
	
	public void setContentInfo(ContentInfoLeyue info){
		if (!TextUtils.isEmpty(info.getPromotionPrice())) {
			mPrice = info.getPromotionPrice();
		}else {
			mPrice = info.getPrice();
		}
		mContentInfo = info;
	}

	@Override
	public void onStart() {
		super.onStart();
		tryStartNetTack(this);
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		showLoadView();
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		if (tag.equals(mUploadModel.getTag())) {
			ToastUtil.showToast(getContext(), R.string.share_record_fail_tip);
			hideLoadView();
		}
		if (tag.equals(mUserInfoModel.getTag())) {
			hideLoadView();
			mUserAction.showRetryImgView();
			showRetryView();
		}
//		if (mScoreExchangeBookModel.getTag().equals(tag)) {
//			if (e instanceof GsonResultException) {
//				GsonResultException exception = (GsonResultException) e;
//				ToastUtil.showToast(getContext(), exception.getResponseInfo().getErrorDescription());
//			}else {
//				ToastUtil.showToast(getContext(), "兑书失败，请重试！");
//			}
//			hideLoadView();
//		}
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if (!isCancel && result!=null) {
			if (mUserInfoModel.getTag().equals(tag)) {
				mDataSource = (UserInfoLeyue)result;
				if(mDataSource != null){
					if (!TextUtils.isEmpty(mDataSource.getScore())) {
						currentScore = Float.valueOf(mDataSource.getScore());
					}
					calculateConsumePoint();
					updateViewByScore(mDataSource.getScore());
				}
			}
//			else if (mScoreExchangeBookModel.getTag().equals(tag)) {
//				resultInfo = (ScoreExchangeBookResultInfo) result;
//				if (resultInfo.isSuccess()) {
//					ToastUtil.showToast(getContext(), "兑换成功！");
//					updateViewByScore(resultInfo.getUsableScore());
//					mUserAction.exchangeSuccess();
//					//修改书架当前书籍为已订购书籍
//					DownloadPresenterLeyue.updateDownloadinfoFullVersionState(true, mContentInfo.getBookId());
//					return false;
//				}else {
//					switch (resultInfo.getCode()) {
//					case ResponseCode.SCORE_FORMAT_ERROR:
//						throw new NullPointerException(getContext().getString(R.string.reward_point_pay_result_invalid_params));
//					case ResponseCode.SCORE_HAS_CHANGE:
//						ToastUtil.showToast(getContext(), getContext().getString(R.string.reward_point_pay_result_invalid_price));
//						mUserAction.reloadBookInfo();
//						break;
//					case ResponseCode.SCORE_NOT_ENOUGH:
//						ToastUtil.showToast(getContext(), getContext().getString(R.string.reward_point_pay_result_not_enough_pint));
//						break;
//					case ResponseCode.BOOK_HAS_BOUGHT:
//						ToastUtil.showToast(getContext(), getContext().getString(R.string.reward_point_pay_result_has_perchase));
//						mUserAction.exchangeSuccess();
//						break;
//
//					default:
//						ToastUtil.showToast(getContext(), getContext().getString(R.string.book_recharge_fail));
//						break;
//					}
//				}
//			}
			if (mUploadModel.getTag().equals(tag)) {
				ScoreUploadResponseInfo info = (ScoreUploadResponseInfo) result;
				CommonUtil.handleForShareTip(getContext(), info);
				if (info!=null && info.getAllAvailableScore()!=null) {
					updateViewByScore(info.getAllAvailableScore());
				}
			}
			hideLoadView();
		}
		return false;
	}
	
	private void updateBtn(int currentCore){
		if(currentCore >= bookScore){
			bPointExchangeBookText.set(getContext().getString(R.string.btn_exchange_immediately));
			bPointExchangeBookEnable.set(true);
		}else{
			//积分不足
			bPointExchangeBookText.set(getContext().getString(R.string.btn_point_not_enough));
			bPointExchangeBookEnable.set(false);
		}
	}
	
	private void calculateConsumePoint(){
		float price = 0;
		if (!TextUtils.isEmpty(mContentInfo.getPromotionPrice())) {
			mPrice = mContentInfo.getPromotionPrice();
		}else {
			mPrice = mContentInfo.getPrice();
		}
		if(!TextUtils.isEmpty(mPrice)){
			price = Float.parseFloat(mPrice);
		}
		//书籍价格转化为积分兑换的规则是1元等于10积分
		bookScore = (int)(price*10);
		bConsumePointText.set(getContext().getResources().getString(R.string.text_consume_point, bookScore));
	}
	
	public static interface RechargeUserAction extends IBaseUserAction{
		/**兑换成功*/
		public void exchangeSuccess();
		
		/**书籍价格调整，重新加载*/
		public void reloadBookInfo();
		
		public void showRetryImgView(); 
	}

	@Override
	public void handleForYiXin(int type) {
		ShareYiXin shareYiXin = new ShareYiXin(getContext());
		if (shareYiXin.isYxInstall()) {
			if (!TextUtils.isEmpty(mContentInfo.getCoverPath())) {
				String imagePath = String.valueOf(mContentInfo.getCoverPath().hashCode());
				LogUtil.e("-----cover-localName=="+imagePath);
				shareYiXin.sendTextWithPic(new MutilMediaInfo("",((Activity)getContext()).getString(R.string.share_for_book_wx, mContentInfo.getBookName()), imagePath,type,UmengShareUtils.contentUrl));
			}else {
				shareYiXin.sendTextWithPic(new MutilMediaInfo("",((Activity)getContext()).getString(R.string.share_for_book_wx, mContentInfo.getBookName()), "",type,UmengShareUtils.contentUrl));
			}
		}else {
			Toast.makeText(getContext(), "你还没有安装易信！", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void handleForWeiXin(int type) {
		ShareWeiXin shareWeiXin = new ShareWeiXin(getContext());
		if (shareWeiXin.isWxInstall()) {
			if (shareWeiXin.isSupportVersion()) {
				if (!TextUtils.isEmpty(mContentInfo.getCoverPath())) {
					String imagePath = String.valueOf(mContentInfo.getCoverPath().hashCode());
					switch (type) {
					case MutilMediaInfo.WX_FRIEND:
						shareWeiXin.sendTextWithPic(new MutilMediaInfo("",((Activity)getContext()).getString(R.string.share_for_book_wx, mContentInfo.getBookName()), imagePath,type,UmengShareUtils.contentUrl));
						break;
					case MutilMediaInfo.WX_FRIEND_ZONE:
						shareWeiXin.sendTextWithPic(new MutilMediaInfo(((Activity)getContext()).getString(R.string.share_for_book_wx, mContentInfo.getBookName()),"", imagePath,type,UmengShareUtils.contentUrl));
						break;
						
					default:
						break;
					}
				}else {
					shareWeiXin.sendTextWithPic(new MutilMediaInfo(((Activity)getContext()).getString(R.string.share_for_book_title),((Activity)getContext()).getString(R.string.share_for_book_wx, mContentInfo.getBookName()), "",type,LeyueConst.WX_YYB_PATH));
				}
			}else {
				Toast.makeText(getContext(), "请更新微信到最新版本！", Toast.LENGTH_SHORT).show();
			}
		}else {
			Toast.makeText(getContext(), "你还没有安装微信！", Toast.LENGTH_SHORT).show();
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
					utils.setShareInfo((Activity)getContext(), new UmengShareInfo(
							((Activity)getContext()).getString(R.string.share_for_book, mContentInfo.getBookName(),UmengShareUtils.contentUrl), mContentInfo.getCoverPath()),bitmap);
				}
	}

	@Override
	public void handleForQQZONE() {
		//Qzone 使用自定义分享接口
				if (utils!=null && !TextUtils.isEmpty(mContentInfo.getCoverPath())) {
					utils.setShareInfo((Activity)getContext(), new UmengShareInfo(
							((Activity)getContext()).getString(R.string.share_for_book, mContentInfo.getBookName(),UmengShareUtils.contentUrl), mContentInfo.getCoverPath()),null);
				}
				utils.shareToQzone((Activity)getContext());
				isShareQZone = true;
	}

	@Override
	public void handleForSMS() {
		//短信不带图片
		if (utils!=null) {
			utils.setShareInfo((Activity)getContext(), new UmengShareInfo(
					((Activity)getContext()).getString(R.string.share_for_book, mContentInfo.getBookName(),UmengShareUtils.contentUrl), ""),null);
		}
	}

	@Override
	public void saveSourceId() {
		UmengShareUtils.LAST_SHARE_SOURCEID = mContentInfo.getBookId();
		UmengShareUtils.shareContext = getContext();
	}
	
	public void activityResult(int requestCode, int resultCode, Intent data){
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
	}
	
	public void uploadShareInfo(){
		if (UserScoreInfo.SINA.equals(UmengShareUtils.LAST_SHARE_TYPE) || 
				UserScoreInfo.QQ_FRIEND.equals(UmengShareUtils.LAST_SHARE_TYPE)) {
			mUploadModel.start(UmengShareUtils.LAST_SHARE_TYPE,UmengShareUtils.LAST_SHARE_SOURCEID);
		}
	}

	@Override
	public boolean isNeedReStart() {
		return !hasLoadedData();
	}

	@Override
	public boolean isStop() {
		return !mUserInfoModel.isStart() && !mUploadModel.isStart();
	}

	@Override
	public void start() {
		if (!CommonUtil.isGuest()) {
			mUserId = PreferencesUtil.getInstance(getContext()).getUserId();
			mAccount = PreferencesUtil.getInstance(getContext()).getUserName();
			mPassword = PreferencesUtil.getInstance(getContext()).getUserPSW();
			mUserInfoModel.start(mUserId, mAccount, mPassword);
		}
	}

	@Override
	protected boolean hasLoadedData() {
		return mDataSource!=null;
	}
	
	/**
	 * 获取到最新积分，刷新界面
	 * @param currentTotalScore
	 */
	public void updateViewByScore(String currentTotalScore){
		bCurrentPointText.set(getContext().getString(R.string.text_current_point, currentTotalScore));
		currentScore = Float.valueOf(currentTotalScore);
		updateBtn(Integer.valueOf(currentTotalScore));
	}
	
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
					uploadShareInfo();
				}else {
					ToastUtil.showToast(getContext(), "分享失败");
				}
				break;
			case SINA:
				if (eCode == ShareConfig.SNS_SUCEESS_CODE) {
					uploadShareInfo();
				}else if(eCode == 5016){
					ToastUtil.showToast(getContext(), "分享内容重复");
				}else {
					ToastUtil.showToast(getContext(), "分享失败");
				}
				break;
			default:
				break;
			}
			
		}
	};
	
	@Override
	public void finish() {
		if(mPayHandler != null) {
			mPayHandler.abort();
			mPayHandler = null;
		}
	};
}
