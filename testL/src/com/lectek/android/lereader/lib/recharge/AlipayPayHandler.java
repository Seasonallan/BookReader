package com.lectek.android.lereader.lib.recharge;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.lectek.alipay.android.AliPay;
import com.lectek.android.lereader.lib.api.response.ResponsePlayStatusInfo;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.recharge.alipay.AlixId;
import com.lectek.android.lereader.lib.recharge.alipay.BaseHelper;
import com.lectek.android.lereader.lib.recharge.alipay.MobileSecurePayer;
import com.lectek.android.lereader.lib.recharge.alipay.Rsa;
import com.lectek.android.lereader.lib.thread.ThreadFactory;
import com.lectek.android.lereader.lib.thread.internal.ITerminableThread;
import com.lectek.android.lereader.lib.utils.LogUtil;

/**
 * 处理支付宝流程的类
 * 
 * @author yangwq
 * @date 2014年6月18日
 * @email 57890940@qq.com
 */
public class AlipayPayHandler implements IPayHandler{
	private static final String TAG = AlipayPayHandler.class.getSimpleName();
		
	private IDealPayRunnable mDealPayRunnable;
	private PayOrder mPayOrder;

	private static String mOrderId;
	private static String mBookDes;
	private static String mBookName;
	private static String mRechargeAmount;
	private Handler mHandler;
	private ITerminableThread mTerminableThread;
	
	private MobileSecurePayer mSecurePayer;
	
	private boolean mIsAbort;
	
	public AlipayPayHandler(Context context){
		createHandler(context);
		mSecurePayer = new MobileSecurePayer();
	}

	@Override
	public void abort() {
		mIsAbort = true;
		if(mTerminableThread != null) {
			mTerminableThread.cancel();
			mTerminableThread = null;
		}
		
		mSecurePayer.abort();
	}
	
	@Override
	public void execute(IDealPayRunnable dealPayRunnable) {
		mDealPayRunnable = dealPayRunnable;
		mIsAbort = false;
		mPayOrder = (PayOrder)dealPayRunnable.onGetOrder(getPayType());
		
		if(mPayOrder == null) {
			LogUtil.e(TAG, "订单为空");
			firePayResult(false, -1);
		}
		
		if(mTerminableThread != null) {
			mTerminableThread.cancel();
		}
		
		mTerminableThread = ThreadFactory.createTerminableThread(new Runnable() {
			
			@Override
			public void run() {
				try {
//					String strRet = "resultStatus={6001}";
//					String regex = "resultStatus=\\{(.\\d*)\\}";
//					Pattern pattern = Pattern.compile(regex);
//					Matcher matcher = pattern.matcher(strRet);
//					if(matcher.matches()) {
//						String num = matcher.group(1);
//						LogUtil.e(TAG, num);
//					}
//					
//					strRet = "resultStatus={sdsdasdas}";
//					regex = "resultStatus=\\{(.\\s*)\\}";
//					pattern = Pattern.compile(regex);
//					matcher = pattern.matcher(strRet);
//					if(matcher.matches()) {
//						String num = matcher.group(1);
//						num = null;
//					}
//					
//					firePayResult(false, PayResultCode.RESULT_CODE_PAY_FAIL);
//					return;
					
					OrderInfo info = null;
					
					String bookId = mPayOrder.getBookId();
					String charge = mPayOrder.getCharge();
					int calType = Integer.valueOf(mPayOrder.getCalType());
					String bookName = mPayOrder.getBookName();
					String calObj = mPayOrder.getCalObj();
					info = mPayOrder.apiHandler.getOrderInfoLeyue(bookId, calType, calObj, charge,mPayOrder.sourceType, mPayOrder.appVersionCode, 
																		bookName, PayConst.PAY_TYPE_ALIPAY);
					
					if(info != null) {
						// TODO:加入充值平台后，若getPayAmount为0且充值平台足够扣免
						// 记得要参考
						// 扣减金额getDeductionAmount()
						
						mOrderId = info.getOrderId();
						mBookName = mPayOrder.getBookName();
						mBookDes = mPayOrder.getBookDesc();
						mRechargeAmount = info.getPayAmount();
						
						String paramsError = null;
						//调试信息，写死文字
						if(TextUtils.isEmpty(mOrderId)) {
							paramsError = "订单id为空";
						}
						if (TextUtils.isEmpty(info.getPayAmount())) {
							paramsError += " 价格字段为空";
						}
						
						if (TextUtils.isEmpty(info.getNotifyURL())) {
							paramsError += " 无回调URL";
						}
						
						if (TextUtils.isEmpty(mOrderId)) {
							paramsError += " 无订单id";
						}
						
						if(paramsError != null) {
							LogUtil.e(TAG, paramsError);
							firePayResult(false, PayResultCode.RESULT_CODE_PAY_FAIL);
							return;
						}
						
						LogUtil.i(TAG, "----订单id为-----:"+mOrderId);
						LogUtil.i(TAG, "----订购价格-----:" + info.getPayAmount());
												
						try {
							String strInfo = AliPay.prepareParams(mOrderId, mBookName, mBookDes, info.getPayAmount(), info.getNotifyURL(), mPayOrder.getKey());
							mSecurePayer.pay(strInfo, mHandler, AlixId.RQF_PAY, mDealPayRunnable);
							return;
							
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					
				}catch(GsonResultException e) {
				}finally {
					mTerminableThread = null;
				}
				firePayResult(false, PayResultCode.RESULT_CODE_PAY_FAIL);
			}
		});
		
		mTerminableThread.start();
	}

	private void firePayResult(boolean success, int payResultCode) {
		Message msg = mHandler.obtainMessage(MSG_FIRE_RESULT);
		msg.obj = success;
		msg.arg1 = payResultCode;
		mHandler.sendMessage(msg);
	}
	
	private static final int MSG_FIRE_RESULT = 0;

	private void createHandler(Context context) {
		mHandler = new Handler(context.getMainLooper()) {

			@Override
			public void handleMessage(Message msg) {
				
				if(msg.what == MSG_FIRE_RESULT) {
					
					mDealPayRunnable.onPayComplete(((Boolean)msg.obj).booleanValue(), msg.arg1, null, null);
					
				}else if(AlixId.RQF_PAY == msg.what){
					
					String strRet = (String) msg.obj;//memo={操作已经取消。};resultStatus={6001};result={}
//					resultStatus={9000};memo={};result={partner="2088101636643891"&seller="2088101636643891"&out_trade_no="1254"&subject="无常七妙"&body="null"&total_fee="0.1"&notify_url="http://220.160.111.214:28082/lereader/alipay/consume?purchaseType=2"&success="true"&sign_type="RSA"&sign="pshCLzfzTiLbkH14aSpeeWtFDeSXj+kjAOJkMKv0ionhi6/pl9brfK3KUuGN24xLDkNy+JoF0AtsNu1dADiMFibjb9X8Oq4VA4eJ0jXvIIj4DcKKebs872o4EuQQ7F7CP+YYiomMGCKKA9g5dcKm3X8ovbutDv5Jd9+Tmeui578="}	
					if(!TextUtils.isEmpty(strRet)) {
						
						LogUtil.i(TAG, strRet);
						
						if (isPayOk(strRet)) {
							mTerminableThread = ThreadFactory.createTerminableThread(new Runnable() {
								
								@Override
								public void run() {
									try {
										ResponsePlayStatusInfo info = mPayOrder.apiHandler.responseClientPlayStatus(
												mOrderId, 
												PayConst.PAY_SOURCE, 
												PayConst.PAY_TYPE_PAYMENT, 
												PayConst.PAY_TRADE_TYPE_CONSUME,
												mBookName,mBookDes, 
												Double.valueOf(mRechargeAmount));
										
										if(info != null) {
											firePayResult(true, Integer.valueOf(info.code));
											return;
										}
										
									} catch (NumberFormatException e) {
										e.printStackTrace();
									} catch (GsonResultException e) {
										e.printStackTrace();
									}finally {
										mTerminableThread = null;
									}
									
									firePayResult(false, PayResultCode.RESULT_CODE_PAY_FAIL);
								}
							});
							mTerminableThread.start();
							return;
						}
						
//						String regex = "\\{(.\\d*)\\}";
//						Pattern pattern = Pattern.compile(regex);
//						Matcher matcher = pattern.matcher(strRet);
//						if(matcher.matches()) {
//							String num = matcher.group(1);
//							
//							LogUtil.e(TAG, "num = "+num);
//							if (PayResultCode.RESPONSE_CODE_9000 == num) {
//								
//								String memo = "memo={";
//
//								int imemoStart = strRet.indexOf(memo);
//								if (imemoStart < 0) {
//									return;
//								}
//								imemoStart += memo.length();
//								int imemoEnd = strRet.indexOf("};result=");
//								LogUtil.v(TAG, "find string end" + imemoEnd);
//								if (imemoEnd >= imemoStart && imemoEnd < strRet.length()) {
//									
//									if (isPayOk(strRet)) {
//										mTerminableThread = ThreadFactory.createTerminableThread(new Runnable() {
//											
//											@Override
//											public void run() {
//												try {
//													ResponsePlayStatusInfo info = mPayOrder.apiHandler.responseClientPlayStatus(
//															mOrderId, 
//															PayConst.PAY_SOURCE, 
//															PayConst.PAY_TYPE_PAYMENT, 
//															PayConst.PAY_TRADE_TYPE_CONSUME,
//															mBookName,mBookDes, 
//															Double.valueOf(mRechargeAmount));
//													
//													if(info != null) {
//														firePayResult(true, Integer.valueOf(info.code));
//														return;
//													}
//													
//												} catch (NumberFormatException e) {
//													e.printStackTrace();
//												} catch (GsonResultException e) {
//													e.printStackTrace();
//												}finally {
//													mTerminableThread = null;
//												}
//												
//												firePayResult(false, PayResultCode.RESULT_CODE_PAY_FAIL);
//											}
//										});
//										
//										return;
//									}
//								}
//								
//							}
//						}
		
						firePayResult(false, PayResultCode.RESULT_CODE_PAY_FAIL);
						
					}
				}
			}
		
		};
	}
	
	/**
	 * 需要通过resultStatus以及result字段的值来综合判断并确定支付结果。
	 * 在resultStatus=9000,并且success="true"以及sign="xxx"校验通过的情况下，
	 * 证明支付成功。其他情况归为失败。较低安全级别可以忽略sign="xxx"
	 * @return
	 */
	private boolean isPayOk(String content) {
		boolean isPayOk = false;

		String resStatus = getResultStatus(content);
		String success = getSuccess(content);
		if(PayResultCode.RESPONSE_CODE_9000.equals(resStatus) && "true".equalsIgnoreCase(success) && checkSign(content)) {
			isPayOk = true;
		}
		return isPayOk;
	}
	
	private String getSuccess(String content) {
		String success = null;
		
		try {
			JSONObject objContent = BaseHelper.string2JSON(content, ";");
			String result = objContent.getString("result");
			result = result.substring(1, result.length()-1);
			
			JSONObject objResult = BaseHelper.string2JSON(result, "&");
			success = objResult.getString("success");
			success = success.replace("\"", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return success;
	}
	
	private boolean checkSign(String content) {
		
		boolean retVal = true;
		
		try {
			JSONObject objContent = BaseHelper.string2JSON(content, ";");
			String result = objContent.getString("result");
			result = result.substring(1, result.length()-1);
			
			int iSignContentEnd = result.indexOf("&sign_type=");
			String signContent = result.substring(0, iSignContentEnd);
			
			JSONObject objResult = BaseHelper.string2JSON(result, "&");
			String signType = objResult.getString("sign_type");
			signType = signType.replace("\"", "");
			
			String sign = objResult.getString("sign");
			sign = sign.replace("\"", "");
			
			if( signType.equalsIgnoreCase("RSA") ){
				if (!Rsa.doCheck(signContent, sign, com.lectek.alipay.android.PartnerConfig.ALIPAY_RSA_PUBLIC)) {
					retVal = false;
				}
			}
		} catch (Exception e) {
			retVal = false;
		}

		return retVal;
	}
	
	private String getResultStatus(String content) {
		String resStatus = null;
		
		try {
			JSONObject objContent = BaseHelper.string2JSON(content, ";");
			String result = objContent.getString("resultStatus");
			resStatus = result.substring(1, result.length()-1);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resStatus;
	}
	
	@Override
	public int getPayType() {
		return PayConst.PAY_TYPE_ALIPAY;
	}
	
	@Override
	public boolean isAbort() {
		return mIsAbort;
	}
}
