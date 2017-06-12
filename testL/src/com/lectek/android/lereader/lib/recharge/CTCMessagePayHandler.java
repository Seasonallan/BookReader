package com.lectek.android.lereader.lib.recharge;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;

import com.lectek.android.lereader.lib.api.response.OrderResponse;
import com.lectek.android.lereader.lib.api.response.OrderResponseToTYRead;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.thread.ThreadFactory;
import com.lectek.android.lereader.lib.thread.internal.ITerminableThread;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.lib.utils.StringUtil;

/**
 * 天翼短代支付流程
 * 
 * @author yangwq
 * @date 2014年6月18日
 * @email 57890940@qq.com
 */
public class CTCMessagePayHandler implements IPayHandler{
	
	private static final String Tag = CTCMessagePayHandler.class.getSimpleName();
	
	private static final String ACTION_SENT_SMS_MESSAGE = "ACTION_SENT_SMS_MESSAGE";
	private static final int MSG_WHAT_ON_CHECK_ORDER = 2;
	private static final int MSG_WHAT_ON_PAY_RESULT = MSG_WHAT_ON_CHECK_ORDER + 1;
	
	private Context mContext;
	private IDealPayRunnable mDealPayRunnable;
	
	private final long PERIOD = 6 * 1000L;	//检测周期
	private int mCheckOrderNum = 0;	//检测订单次数
	private static final int MAX_CHECK_ORDER_NUM = 4;	//最大检测次数
	private int mOrderId = -1;
	
	private ITerminableThread mTerminableThread;
	private CTCMessageOrderInfo mPayParams;
	
	private Handler mHandler = null;
	
	private boolean mIsAbort;
	
	public CTCMessagePayHandler(Context context){
		mContext = context;
		
		mHandler = new Handler(context.getMainLooper()){
			@Override
			public void handleMessage(Message msg) {
				int what = msg.what;
				
				switch(what) {
				case MSG_WHAT_ON_CHECK_ORDER:
					loopCheckPayResult();	
					break;
				case MSG_WHAT_ON_PAY_RESULT:
					boolean success = ((Boolean)msg.obj).booleanValue();
					int resultCode = msg.arg1;
					
					mDealPayRunnable.onPayComplete(success, resultCode, null, null);
					break;
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
		if(sendMessage != null){
			mDealPayRunnable.onUnregisterSmsReceiver(sendMessage);
		}
		
		mHandler.removeMessages(MSG_WHAT_ON_CHECK_ORDER);
	}
	
	@Override
	public void execute(IDealPayRunnable dealPayRunnable) {
		mDealPayRunnable = dealPayRunnable;
		mPayParams = (CTCMessageOrderInfo)mDealPayRunnable.onGetOrder(getPayType());
		
		if(mPayParams == null) {
			LogUtil.e(Tag, "订单为空");
			firePayResult(false, PayResultCode.RESULT_CODE_PAY_FAIL);
			return;
		}
		
		if(mTerminableThread != null) {
			mTerminableThread.cancel();
		}
		
		mIsAbort = false;
		
		mTerminableThread = ThreadFactory.createTerminableThread(new Runnable() {
			
			@Override
			public void run() {
				try {
					
					// 1.
					OrderResponse response = mPayParams.apiHandler.generateOrder(mPayParams);

					if (response != null) {
						// 2.
						OrderResponseToTYRead rsp = mPayParams.apiTYHandler.generateOrderToTYRead("",response.getProductId(), response.getNotifyURL());

						if (rsp != null) {
							// 3.
							OrderResponse updateOrderResponse = mPayParams.apiHandler.updateOrder(response.getOrderId(), rsp.getOrderToTYRead().getOrder_no());

							if (updateOrderResponse != null) {
								// 4
								String number = rsp.getOrderToTYRead().getSender_number();
								String shortMsg = rsp.getOrderToTYRead().getMessage_content();
								PendingIntent sentPI = null;
//								PendingIntent deliverPI = null;
								LogUtil.v("sms", "number = " + number + " msg = " + shortMsg);
								if (!StringUtil.isEmpty(number) && !StringUtil.isEmpty(shortMsg)) {
									sentPI = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_SENT_SMS_MESSAGE),  0);
//									deliverPI = PendingIntent.getBroadcast(mContext, 0,  new Intent(AppBroadcast.ACTION_DELIVERED_SMS_MESSAGE), 0);
									mDealPayRunnable.onRegisterSmsReceiver(ACTION_SENT_SMS_MESSAGE, sendMessage);
									SmsManager.getDefault().sendTextMessage(number, null, shortMsg, sentPI, null);
									mOrderId = response.getOrderId();	//预支付成功，orderId赋值
									return;
								}
							}
						}
					}
;
				} catch (GsonResultException exception) {
					exception.printStackTrace();
				}finally {
					mTerminableThread = null;
				}
				
				firePayResult(false, PayResultCode.RESULT_CODE_PAY_FAIL);
			}
		});		
		
		mTerminableThread.start();
	}
	
	private BroadcastReceiver sendMessage = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			//判断短信是否发送成功
			if (intent.getAction().equals(ACTION_SENT_SMS_MESSAGE)){
				try{
					boolean isSmsSendOK = false;
					if(getResultCode() == Activity.RESULT_OK){
						isSmsSendOK = true;
						LogUtil.v("SmsPayHandler", "短信发送成功!");
						mHandler.sendEmptyMessage(MSG_WHAT_ON_CHECK_ORDER);
					}
					if(!isSmsSendOK){
						if(getResultCode() == SmsManager.RESULT_ERROR_GENERIC_FAILURE){
							LogUtil.v("SmsPayHandler", "短信发送失败：Generic failure cause!");
						}else if(getResultCode() == SmsManager.RESULT_ERROR_NO_SERVICE){
							LogUtil.v("SmsPayHandler", "短信发送失败：Failed because service is currently unavailable!");
						}else if(getResultCode() == SmsManager.RESULT_ERROR_NULL_PDU){
							LogUtil.v("SmsPayHandler", "短信发送失败：Failed because no pdu provided!");
						}else if(getResultCode() == SmsManager.RESULT_ERROR_RADIO_OFF){
							LogUtil.v("SmsPayHandler", "短信发送失败：Failed because radio was explicitly turned off!");
						}
			
						firePayResult(false, PayResultCode.RESULT_CODE_PAY_FAIL);
					}
					
				}catch(Exception e){
					e.printStackTrace();
					firePayResult(false, PayResultCode.RESULT_CODE_PAY_FAIL);
				}finally{
					mDealPayRunnable.onUnregisterSmsReceiver(sendMessage);
					sendMessage = null;
				}
		    }
		}
	};

	private void loopCheckPayResult() {
		mTerminableThread = ThreadFactory.createTerminableThread(new Runnable() {
			
			@Override
			public void run() {
				try {
					OrderInfo info = mPayParams.apiHandler.getOrderInfoById(String.valueOf(mOrderId));
					
					
					if(info != null && info.getPayOrderStatus().equals(OrderInfo.PAY_ORDER_STATUS_SUCCESS)){
						firePayResult(true, PayResultCode.RESULT_CODE_PAY_SUCCESS);
						return;
					}
					
					if(mCheckOrderNum > MAX_CHECK_ORDER_NUM){
						firePayResult(false, PayResultCode.RESULT_CODE_PAY_FAIL);
					}else{
						mHandler.sendEmptyMessageDelayed(MSG_WHAT_ON_CHECK_ORDER, PERIOD);
					}
				}catch(GsonResultException e){
					e.printStackTrace();
				}finally {
					mTerminableThread = null;
					mCheckOrderNum++;
				}
			}
		});
		mTerminableThread.start();
	}
	
	private void firePayResult(boolean success, int resultCode) {
		Message msg = mHandler.obtainMessage(MSG_WHAT_ON_PAY_RESULT);
		msg.obj = success;
		msg.arg1 = resultCode;
		mHandler.sendMessage(msg);
	}
	
	@Override
	public int getPayType() {
		return PayConst.PAY_TYPE_CHINATELECOM_MESSAGE_PAY;
	}
	
	@Override
	public boolean isAbort() {
		return mIsAbort;
	}
}


