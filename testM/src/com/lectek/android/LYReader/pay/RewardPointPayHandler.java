package com.lectek.android.LYReader.pay;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.lectek.android.lereader.lib.api.response.ScoreExchangeBookResultInfo;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.recharge.IDealPayRunnable;
import com.lectek.android.lereader.lib.recharge.IPayHandler;
import com.lectek.android.lereader.lib.recharge.PayConst;
import com.lectek.android.lereader.lib.thread.ThreadFactory;
import com.lectek.android.lereader.lib.thread.internal.ITerminableThread;

/**
 * 乐阅积分兑换书籍
 * @author chends@lectek.com
 * @date 2014/07/17
 */
public class RewardPointPayHandler implements IPayHandler {

	private static final int MSG_FIRE_RESULT_SUCCESS = 0;
	private static final int MSG_FIRE_RESULT_FAIL = MSG_FIRE_RESULT_SUCCESS + 1;
	
	private ITerminableThread mTerminableThread;
	private IDealPayRunnable mDealPayRunnable;
	private Handler mHandler;
	private boolean mIsAbort;
	
	public RewardPointPayHandler(Context context) {
		mHandler = new Handler(context.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == MSG_FIRE_RESULT_SUCCESS) {
					mDealPayRunnable.onPayComplete(true, msg.arg1, null, msg.obj);
				}else if(msg.what == MSG_FIRE_RESULT_FAIL) {
					mDealPayRunnable.onPayComplete(false, msg.arg1, null, msg.obj);
				}
			}
		};
	}
	
	@Override
	public void execute(IDealPayRunnable dealPayRunnable) {
		mDealPayRunnable = dealPayRunnable;
		
		if(mTerminableThread != null) {
			mTerminableThread.cancel();
		}
		
		mIsAbort = false;
		
		mTerminableThread = ThreadFactory.createTerminableThread(new Runnable() {
			
			@Override
			public void run() {
				RewardPointOrderInfo orderInfo = (RewardPointOrderInfo)mDealPayRunnable.onGetOrder(getPayType());
				
				ScoreExchangeBookResultInfo info = null;
				try {
					if(orderInfo != null) {
						info = orderInfo.apiHandler.getScoreExchangeBook(orderInfo.bookID, 1, 
															orderInfo.bookID, (int)orderInfo.fee, orderInfo.purchaseType, orderInfo.bookName);
						
						if(info != null) {
							if(info.isSuccess) {
								firePayResult(true, info.getCode(), info);
								return;
							}
						}
					}
					firePayResult(false, info.getCode(), info);
					
				}catch(GsonResultException e) {
					e.printStackTrace();
					firePayResult(false, -1, null);
				}finally {
					mTerminableThread = null;
				}
				
				
			}
		});
		
		mTerminableThread.start();
	}

	private void firePayResult(boolean success, int code, Object resultData) {
		Message msg = mHandler.obtainMessage(success ? MSG_FIRE_RESULT_SUCCESS : MSG_FIRE_RESULT_FAIL);
		msg.obj = resultData;
		msg.arg1 = code;
		mHandler.sendMessage(msg);
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
	public int getPayType() {
		return PayConst.PAY_TYPE_LEREADER_READ_POINT;
	}
	
	@Override
	public boolean isAbort() {
		return mIsAbort;
	}
}
