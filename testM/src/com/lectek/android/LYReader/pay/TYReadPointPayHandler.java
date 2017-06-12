package com.lectek.android.LYReader.pay;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.lectek.android.lereader.lib.net.ResponseResultCode;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.recharge.IDealPayRunnable;
import com.lectek.android.lereader.lib.recharge.IPayHandler;
import com.lectek.android.lereader.lib.recharge.PayConst;
import com.lectek.android.lereader.lib.thread.ThreadFactory;
import com.lectek.android.lereader.lib.thread.internal.ITerminableThread;
import com.lectek.android.lereader.permanent.OrderConfig;

/**
 * 天翼书籍阅点支付
 * @author chends@lectek.com
 * @date 2014/07/17
 */
public class TYReadPointPayHandler implements IPayHandler{

	private static final int MSG_WHAT_FIRE_RESULT = 0;
	
	private IDealPayRunnable mDealPayRunnable;
	private ITerminableThread mTerminableThread;
	private boolean mIsAbort;
	private Handler mHandler;
	
	public TYReadPointPayHandler(Context context) {
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
				
				TYReadPointPayOrderInfo orderInfo = (TYReadPointPayOrderInfo)mDealPayRunnable.onGetOrder(getPayType());
				
				TYMonthlyPayResult info = new TYMonthlyPayResult();
				info.payMode = orderInfo.payMode;
				
				//注意：阅读支付没有try，需要在外部接收处理。
				try {
					if(OrderConfig.MONTHLY_PAY.equals(orderInfo.payMode)) {
						info.pointPayOrderResult = orderInfo.apiHandlerTY.subscribeMonthProductReadpoint(orderInfo.bookID);
					}else{
						info.pointPayOrderResult = orderInfo.apiHandlerTY.subscribeContentReadpoint(orderInfo.bookID, null);
					}
					
					if (info.pointPayOrderResult != null) {
						switch (info.pointPayOrderResult.getCode()) {
						case ResponseResultCode.ORDER_OK:
							
							String calObj ="";
							String bookId ="";
							if (orderInfo.orderType == PayConst.ORDER_TYPE_BOOK) {
								calObj = orderInfo.leBookID;
								bookId = orderInfo.leBookID;
							}else if(orderInfo.orderType == PayConst.ORDER_TYPE_MONTHLY){
								calObj = orderInfo.leBookID;
								bookId = "";
							}
							orderInfo.apiHandler.getAddOrderDetail(orderInfo.userID, bookId, orderInfo.bookName,orderInfo.orderType,calObj, 
											orderInfo.fee, orderInfo.purchaseType, orderInfo.sourceType);
							firePayResult(true, -1, null, null);
							break;
						case ResponseResultCode.NOT_SUFFICIENT_FUNDS:
							firePayResult(false, ResponseResultCode.NOT_SUFFICIENT_FUNDS, null, null);
							break;

						default:
							firePayResult(false, -1, null, null);
							break;
						}
						
						return;
					}
					
				} catch (GsonResultException e) {
					e.printStackTrace();
//					info.pointPayOrderResult = new OrderedResult();
//					info.pointPayOrderResult.setMessage(e.getResponseInfo().getSurfingMsg());
//					info.pointPayOrderResult.setSurfingCode((e.getResponseInfo().getSurfingCode()));
				}finally {
					mTerminableThread = null;
				}
				firePayResult(false, -1, null, null);
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
		return PayConst.PAY_TYPE_TY_READ_POINT;
	}
	
	@Override
	public boolean isAbort() {
		return mIsAbort;
	}
}
