package com.lectek.android.LYReader.pay;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.lectek.android.lereader.lib.recharge.IDealPayRunnable;
import com.lectek.android.lereader.lib.recharge.IPayHandler;
import com.lectek.android.lereader.lib.recharge.PayConst;
import com.lectek.android.lereader.lib.thread.ThreadFactory;
import com.lectek.android.lereader.lib.thread.internal.ITerminableThread;

/**
 * 天翼书籍话费支付，只有ctwap网络下支持
 * @author chends@lectek.com
 * @date 2014/07/17
 */
public class TYSSOPayHandler implements IPayHandler{

	private static final int MSG_WHAT_FIRE_RESULT = 0;
	
	private IDealPayRunnable mDealPayRunnable;
	private ITerminableThread mTerminableThread;
	private boolean mIsAbort;

	private Handler mHandler;
	
	public TYSSOPayHandler(Context context) {
		mHandler = new Handler(context.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == MSG_WHAT_FIRE_RESULT) {
					mDealPayRunnable.onPayComplete(((Boolean)msg.obj).booleanValue(), msg.arg1, null, null);
				}
			}
		};
	}

	@Override
	public void abort() {
		mIsAbort = true;
		if(mTerminableThread != null) {
			mTerminableThread.cancel();
			mTerminableThread = null;
		}
	}

	@Override
	public void execute(IDealPayRunnable arg0) {
		mDealPayRunnable = arg0;
		
		if(mTerminableThread != null) {
			mTerminableThread.cancel();
		}
		
		mIsAbort = false;
		
		mTerminableThread = ThreadFactory.createTerminableThread(new Runnable() {
			
			@Override
			public void run() {
//				PayMonthlyInfo info = new PayMonthlyInfo();
//				String type = params[0].toString();
//				String bookId = params[1].toString();
//				String payMode = "none";
//				if(params.length>=3){
//					payMode = params[2].toString();
//				}
//				info.payMode = payMode;
//				info.type = type;
//				
//				String userId = AccountManager.getInstance().getTYAccountInfo(PreferencesUtil.getInstance(getContext()).getUserId()).getUserId();
//				
//				if (type.equals(OrderDialogBuildUtil.EXTRA_NAME_SSO_ORDER)) {
//					try {
//						if(payMode.equals(MONTHLY_PAY)){
//							info.sso1PayResult = ApiProcess4TianYi.getInstance(getContext()).payConfirm(bookId, 1, null, null, bookId,userId);
//						}else{
//							info.sso1PayResult = ApiProcess4TianYi.getInstance(getContext()).payConfirm(bookId, 2, bookId, null, null,userId);
//						}
//					} catch (GsonResultException e) {
//						info.sso1PayResult = new PayResult();
//						info.sso1PayResult.setSurfingCode(e.getResponseInfo().getSurfingCode());
//						info.sso1PayResult.setSurfingMsg(e.getResponseInfo().getSurfingMsg());
//					}
//					if (info.sso1PayResult!=null && !TextUtils.isEmpty(info.sso1PayResult.getContent())) {
//						try {
//							info.sso2PayAfterResult = ApiProcess4TianYi.getInstance(getContext()).pay(info.sso1PayResult.getContent());
//						} catch (GsonResultException e) {
//							info.sso2PayAfterResult = new PayAfterResult();
//							info.sso2PayAfterResult.setSurfingCode(e.getResponseInfo().getSurfingCode());
//							info.sso2PayAfterResult.setSurfingMsg(e.getResponseInfo().getSurfingMsg());
//						}
//					}
//
//				} else if (type.equals(OrderDialogBuildUtil.EXTRA_NAME_READ_POINT_ORDER)) {
//					//注意：阅读支付没有try，需要在外部接收处理。
//					try {
//						if(payMode.equals(MONTHLY_PAY)){
//							info.pointPayOrderResult = ApiProcess4TianYi.getInstance(getContext()).subscribeMonthProductReadpoint(bookId);
//						}else{
//							info.pointPayOrderResult = ApiProcess4TianYi.getInstance(getContext()).subscribeContentReadpoint(bookId, null);
//						}
//					} catch (GsonResultException e) {
//						info.pointPayOrderResult = new OrderedResult();
//						info.pointPayOrderResult.setMessage(e.getResponseInfo().getSurfingMsg());
//						info.pointPayOrderResult.setSurfingCode((e.getResponseInfo().getSurfingCode()));
//					}
//				}
				mTerminableThread = null;
			}
		});
		
		mTerminableThread.start();
	}

	private void firePayResult(boolean success, int resultCode, String resultMsg, Object resultData) {
		Message msg = mHandler.obtainMessage(MSG_WHAT_FIRE_RESULT);
		msg.obj = success;
		msg.arg1 = resultCode;
		msg.setTarget(mHandler);
	}
	
	@Override
	public int getPayType() {
		return PayConst.PAY_TYPE_CHINATELECOM_FEE;
	}
	
	@Override
	public boolean isAbort() {
		return mIsAbort;
	}
}
