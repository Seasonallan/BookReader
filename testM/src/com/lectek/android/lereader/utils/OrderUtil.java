package com.lectek.android.lereader.utils;
//package com.lectek.android.sfreader.util;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.drawable.Drawable;
//import android.os.Handler;
//import android.telephony.SmsManager;
//import android.text.TextUtils;
//import android.util.SparseBooleanArray;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.AbsListView;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.lectek.android.sfreader.R;
//import com.lectek.android.lereader.lib.utils.DataCache;
//import com.lectek.android.sfreader.data.ChapterCartInfo;
//import com.lectek.android.sfreader.data.ChapterInfo;
//import com.lectek.android.sfreader.data.ContentInfo;
//import com.lectek.android.sfreader.data.VolumnInfo;
//import com.lectek.android.sfreader.entity.ProductInfo;
//import com.lectek.android.sfreader.entity.SubscribeResult;
//import com.lectek.android.sfreader.net.DataSaxParser;
//import com.lectek.android.sfreader.net.data.ResponseResultCode;
//import com.lectek.android.sfreader.net.exception.ResultCodeException;
//import com.lectek.android.sfreader.net.exception.ServerErrException;
//import com.lectek.android.sfreader.net.exception.VoiceResultException;
//import com.lectek.android.sfreader.net.voice.VoiceConstants;
//import com.lectek.android.sfreader.net.voice.VoiceInfo;
//import com.lectek.android.sfreader.presenter.BuyBookPresenter;
//import com.lectek.android.sfreader.presenter.BuyBookPresenter.DealOrderRunnadle;
//import com.lectek.android.sfreader.widgets.CustomAlertDialog;
//import com.lectek.android.sfreader.widgets.CustomDialogInterface;
//import com.lectek.android.lereader.lib.utils.ApnUtil;
//import com.lectek.android.util.IMSIUtil;
//import com.lectek.android.util.LogUtil;
//import com.lectek.android.util.PhoneNumUtil;
//
///**
// * 购买流程处理
// * 
// * @author mingkg21
// * @email mingkg21@gmail.com
// * @date 2011-5-11
// */
//public final class OrderUtil {
//	
//	private final static String Tag = OrderUtil.class.getSimpleName();
//	public final static String ACTION_ORDER = "ACTION_ORDER";
//	public final static String EXTRA_CONTENT_ID = "EXTRA_CONTENT_ID";
//	public final static String EXTRA_TYPE = "EXTRA_TYPE";
//	
//	public final static String VALUE_TYPE_BOOK_BUY = "VALUE_TYPE_BOOK_BUY";
//	public final static String VALUE_TYPE_VOICE_BUY = "VALUE_TYPE_VOICE_BUY";
//	public final static String VALUE_TYPE_VOICE_MONTH = "VALUE_TYPE_VOICE_MONTH";
//	public final static String VALUE_TYPE_BOOK_MONTH = "VALUE_TYPE_BOOK_MONTH";
//	
//	public final static String EXTRA_NAME_BOOK_READ_POINT_NUM = "book_read_point_num";
//	public final static String EXTRA_NAME_READPOINT = "read_point";
//	public final static String EXTRA_NAME_POINTS = "point";
//	public final static String EXTRA_TYPE = "type";
//	
//	public final static int TAG_RECHARGE_READ_TICKET_BTN_ID = 1;
//	public final static int TAG_THRID_PARTY_RECHARGE_BTN_ID = 2;
//	/**
//	 * 处理订购包月
//	 * 
//	 * @param context
//	 * @param catalogID
//	 * @param handlerRunnable
//	 * @return
//	 */
//	public static void dealOrderArea(final Context context,
//			String catalogID, final String totalPrice, Handler handler,final HandlerRunnable handlerRunnable,
//			boolean isCtwap, final String type) {
//		BuyBookPresenter.dealOrderArea(catalogID, isCtwap, new DealOrderRunnadle() {
//			
//			@Override
//			public void onSucceed() {
//				
//			}
//			
//			@Override
//			public boolean onPreRunUI() {
//				return true;
//			}
//			
//			@Override
//			public void onPostRunUI(String resultCode) {
//				boolean isSuccess = false;
//				if (resultCode != null) {
//					if (resultCode.equals(ResponseResultCodeTY.STATUS_OK)) {
//						isSuccess = true;
//					} else {
//						isSuccess = false;
//					}
//				}
//				if (handlerRunnable != null) {
//					handlerRunnable.run(isSuccess);
//				}
//			}
//			
//			@Override
//			public void onOrderConfirm(boolean isConfirm) {
//				
//			}
//			
//			@Override
//			public void onFailUI(String msg) {
//				ToastUtil.showToast(context, msg);
//			}
//
//			@Override
//			public void onResultCodeFailUI(String resultCode, String msg) {
//				//TODO 读书书籍包月失败提示
//				if(!TextUtils.isEmpty(resultCode) && resultCode.equals(ResponseResultCodeTY.STATUS_NOT_ENOUGH_READ_POINT)){
//					if(!TextUtils.isEmpty(totalPrice)){
//						//TODO
//					}else{
//						if(!TextUtils.isEmpty(msg)){
//							ToastUtil.showToast(context, msg);
//						}else{
//							ToastUtil.showToast(context, R.string.book_content_buy_result_fault);
//						}
//					}
//				}else{
//					if(!TextUtils.isEmpty(msg)){
//						ToastUtil.showToast(context, msg);
//					}else{
//						ToastUtil.showToast(context, R.string.book_content_buy_result_fault);
//					}
//				}
//			}
//		});
//	}
//
//	public static void dealOrderContent(Activity context, String contentId,
//			ChapterInfo chapterInfo,final String totalPrice, Runnable actionRunnable, Handler handler,
//			final HandlerRunnableForAutoPay handlerRunnable, final String type) {
//		boolean isCtwap = !ApnUtil.isWifiWork(context) && ApnUtil.isCtwap(context);
//		dealOrderContent(context, contentId, chapterInfo,totalPrice,
//				actionRunnable, handler, handlerRunnable, isCtwap, type);
//	}
//
//	public static void dealOrderContent(final Activity context,
//			String contentId, ChapterInfo chapterInfo, final String totalPrice, Runnable actionRunnable,
//			Handler handler, final HandlerRunnableForAutoPay handlerRunnable,
//			boolean isCtwap , final String type) {
//		dealOrderContent(context, contentId, chapterInfo, null,totalPrice,
//				actionRunnable, handler, handlerRunnable, null, null,isCtwap, type);
//	}
//	
//	public static void dealGiftOrderContent(final Activity context,
//			String contentId, String giftAccount,final String totalPrice, String blessingstr,final HandlerRunnableForAutoPay handlerRunnable,
//			boolean isCtwap) {
//		dealOrderContent(context, contentId, null, giftAccount,totalPrice,
//				null, null, handlerRunnable, null,blessingstr, isCtwap, null);
//	}
//
//	/** 处理购买流程
//	 * @param context
//	 * @param contentId 内容ID
//	 * @param chapterInfo 章节ID；如果不为空按章购买，否则按整本购买
//	 * @param giftAccount 赠送帐号；如果为空是购买，否则按赠送购买
//	 * @param actionRunnable 购买成功后的逻辑处理
//	 * @param handler 可以为空，暂时没有用
//	 * @param handlerRunnable 购买成功后的UI处理
//	 * @param afterRunnable 暂时没有用
//	 * @param isCtwap 购买方式；TRUE为话费购买，FALSE为阅点购买
//	 * @return
//	 */
//	public static void dealOrderContent(final Activity context,
//			String contentId, ChapterInfo chapterInfo, final String giftAccount, final String totalPrice,
//			final Runnable actionRunnable, Handler handler, final HandlerRunnableForAutoPay handlerRunnable,
//			final Runnable afterRunnable, String blessingstr, boolean isCtwap, final String type) {
//		BuyBookPresenter.dealOrderContent(contentId, chapterInfo, giftAccount,blessingstr, isCtwap, new DealOrderRunnadle() {
//					// private Dialog waittingDialog;
//					@Override
//					public void onSucceed() {
//						if (actionRunnable != null) {
//							actionRunnable.run();
//						}
//					}
//
//					@Override
//					public boolean onPreRunUI() {
//						// waittingDialog = CommonUtil.getNetDialog(context);
//						// waittingDialog.show();
//						return true;
//					}
//
//					@Override
//					public void onPostRunUI(String resultCode) {
//						if (context.isFinishing()) {
//							return;
//						}
//						boolean isSuccess = false;
//						if (!TextUtils.isEmpty(resultCode)) {
//							if (resultCode.equals(ResponseResultCodeTY.STATUS_OK)) {
//								isSuccess = true;
//							} else if (resultCode.equals(ResponseResultCodeTY.STATUS_GIFT_NON_READER_USER)) {
//								giftNonReaderAccount(context, giftAccount);
//							} else {
//								isSuccess = false;
//							}
//						}
//						if (handlerRunnable != null) {
//							handlerRunnable.run(isSuccess);
//						}
//					}
//
//					@Override
//					public void onOrderConfirm(boolean isConfirm) {
//						if (afterRunnable != null) {
//							afterRunnable.run();
//						}
//					}
//
//					@Override
//					public void onFailUI(String msg) {
//						if (msg != null) {
//							ToastUtil.showToast(context, msg);
//						} else {
//							ToastUtil.showToast(context, R.string.book_content_buy_result_fault);
//						}
//					}
//
//					@Override
//					public void onResultCodeFailUI(String resultCode, String msg) {
//						//TODO 读书书籍包月失败提示
//						if(!TextUtils.isEmpty(resultCode) && resultCode.equals(ResponseResultCodeTY.STATUS_NOT_ENOUGH_READ_POINT)){
//							if(!TextUtils.isEmpty(totalPrice)){
//								//TODO
//							}else{
//								if(!TextUtils.isEmpty(msg)){
//									ToastUtil.showToast(context, msg);
//								}else{
//									ToastUtil.showToast(context, R.string.book_content_buy_result_fault);
//								}
//							}
//						}else{
//							if(!TextUtils.isEmpty(msg)){
//								ToastUtil.showToast(context, msg);
//							}else{
//								ToastUtil.showToast(context, R.string.book_content_buy_result_fault);
//							}
//						}
//					}
//				});
//	}
//	
//	public static void dealMultiChaptersOrder(final Activity activity, final String totalPrice, ChapterCartInfo chapterCartInfo,
//			final Runnable actionRunnable, Handler handler, final HandlerRunnable handlerRunnable, final Runnable afterRunnable,
//			final String type) {
//		
//		BuyBookPresenter.dealMultiChapterOrder(totalPrice, chapterCartInfo, new DealOrderRunnadle() {
//			
//			@Override
//			public void onSucceed() {
//				if (actionRunnable != null) {
//					actionRunnable.run();
//				}
//			}
//			
//			@Override
//			public boolean onPreRunUI() {
//				return true;
//			}
//			
//			@Override
//			public void onPostRunUI(String resultCode) {
//				if (activity.isFinishing()) {
//					return;
//				}
//				boolean isSuccess = false;
//				if (!TextUtils.isEmpty(resultCode)) {
//					if (resultCode.equals(ResponseResultCodeTY.STATUS_OK)) {
//						isSuccess = true;
//					} else {
//						isSuccess = false;
//					}
//				}
//				if (handlerRunnable != null) {
//					handlerRunnable.run(isSuccess);
//				}
//			}
//			
//			@Override
//			public void onOrderConfirm(boolean isConfirm) {
//				if (afterRunnable != null) {
//					afterRunnable.run();
//				}
//			}
//			
//			@Override
//			public void onFailUI(String msg) {
//				if (msg != null) {
//					ToastUtil.showToast(activity, msg);
//				} else {
//					ToastUtil.showToast(activity, R.string.book_content_buy_result_fault);
//				}
//			}
//
//			@Override
//			public void onResultCodeFailUI(String resultCode, String msg) {
//				//TODO 读书书籍包月失败提示
//				if(!TextUtils.isEmpty(resultCode) && resultCode.equals(ResponseResultCodeTY.STATUS_NOT_ENOUGH_READ_POINT)){
//					if(!TextUtils.isEmpty(totalPrice)){
//						//TODO
//					}else{
//						if(!TextUtils.isEmpty(msg)){
//							ToastUtil.showToast(activity, msg);
//						}else{
//							ToastUtil.showToast(activity, R.string.book_content_buy_result_fault);
//						}
//					}
//				}else{
//					if(!TextUtils.isEmpty(msg)){
//						ToastUtil.showToast(activity, msg);
//					}else{
//						ToastUtil.showToast(activity, R.string.book_content_buy_result_fault);
//					}
//				}
//			}
//		});
//	}
//	
//	public interface HandlerRunnable {
//
//		public void run(boolean isSuccess);
//
//	}
//	
//	private static void giftNonReaderAccount(final Activity context, final String giftAccount) {
//		if (!PhoneNumUtil.isChinaTelecomNumber(giftAccount)) {
//			Runnable runnable = new Runnable() {
//				
//				@Override
//				public void run() {
//					if (!TextUtils.isEmpty(giftAccount)) {
//						SmsManager smsManager = SmsManager.getDefault();
//						String msg = context.getString(R.string.recommend_app_content, DataCache.getInstance().getPhoneNumber());
//						ArrayList<String> messages = smsManager.divideMessage(msg);
//						smsManager.sendMultipartTextMessage(giftAccount, null, messages, null, null);
//					}
//
//				}
//			};
//			Dialog dialog = CommonUtil.confirmDialog(context, R.string.dialog_recommend_prompt_title,
//					R.string.dialog_recommend_prompt_content, runnable);
//			dialog.show();
//		} else {
//			new Thread() {
//
//				@Override
//				public void run() {
//					String contenStr = context.getString(R.string.recommend_app_content, DataCache.getInstance().getPhoneNumber());
//					try {
//						final boolean isSuccess = DataSaxParser.getInstance(context).recommendedService(giftAccount, contenStr);
//					} catch (ResultCodeException e) {
//					} catch (ServerErrException e) {
//					}
//				}
//				
//			}.start();
//		}
//		
//	}
//	
//	/** 处理购买流程的接口
//	 * @author mingkg21
//	 * @email mingkg21@gmail.com
//	 * @date 2011-12-6
//	 */
//	public interface DealBuyRunnable {
//		public void run(boolean isCtwap);
//	}
//	
//	public static void showDialog(Activity content, final DealBuyRunnable confirmRunnable, ContentInfo contentInfo, String chapterName){
//		showDialog(content, confirmRunnable, null, contentInfo.contentName, contentInfo.contentID, contentInfo.price,
//				contentInfo.readPointPrice, contentInfo.offersPrice, chapterName, contentInfo.logoUrl, null);
//	}
//	
//	public static void showDialog(Activity content, final DealBuyRunnable confirmRunnable, final Runnable cancelRunnable,
//			String name, String contentID, String price, String readPointPrice, String offersPrice, String chapterName,
//			String imageUrl, final DealBuyRunnable serialRunnable){
//		boolean isCtwapTmp;
//		if(ClientInfoUtil.isClientCtwap(content) && PreferencesUtil.getInstance(content).getPayMode() == PreferencesUtil.PAY_MOBILE_FEE){//CTWAP网络
//			isCtwapTmp = true;
//		}else{//非CTWAP网络
//			isCtwapTmp = false;
//		}
//		final boolean isCtwap = isCtwapTmp;
//		showDialog(content, confirmRunnable, cancelRunnable, name, contentID, price, readPointPrice, offersPrice, chapterName, imageUrl, serialRunnable, isCtwap);
//	}
//	
//	private static void sendBuySucceedBroadcast(Context context,String id,String type){
//		Intent intent = new Intent(ACTION_ORDER);
//		intent.putExtra(EXTRA_CONTENT_ID, id);
//		intent.putExtra(EXTRA_TYPE, type);
//		context.sendBroadcast(intent);
//	}
//	
//	public static boolean showVoiceBuyDialog(final Activity context, final Runnable successRunnable, final ProductInfo productInfo){
//		boolean isCtwap = false;
//		if(ClientInfoUtil.isClientCtwap(context) && PreferencesUtil.getInstance(context).getPayMode() == PreferencesUtil.PAY_MOBILE_FEE){
//			if (IMSIUtil.MOBILE_TYPE_CHINA_TELECOM == IMSIUtil.getMobileType(context)) {
//				ToastUtil.showLongToast(context, R.string.china_telecom_voice_buy_no_ctwap_tip);
//			} else {
//				ToastUtil.showLongToast(context, R.string.voice_buy_no_ctwap_tip);
//			}
//			isCtwap = true;
//		}
//		DealBuyRunnable confirmRunnable = new DealBuyRunnable() {
//			
//			@Override
//			public void run(final boolean isCtwap) {
//				final Dialog waittingDialog = CommonUtil.getNetDialog(context);
//				waittingDialog.show();
//				new Thread() {
//					@Override
//					public void run() {
//						boolean result = false;
//						String errStr = "";
//						SubscribeResult subscribeResult = null;
//						try {
//							if(isCtwap){
//								result = VoiceInfo.getInstence(context).productFee(productInfo.getId());
//							}else{
//								subscribeResult = VoiceInfo.getInstence(context).subscribeByReadPoint(VoiceConstants.ORDER_TYPE_VOICE_PRODUCT, productInfo.getId(), null);
//								if(subscribeResult != null && subscribeResult.statusCode != null ){
//									if(subscribeResult.statusCode.equals(SubscribeResult.STATUS_CODE_VALUE_SUCCESS)){
//										result = true;
//									}else{
//										errStr = SubscribeResult.getResultString(context, subscribeResult.statusCode);
//									}
//								}
//							}
//							
//						} catch (ServerErrException e) {
//							errStr = e.getMessage();
//							LogUtil.e("errStr", errStr+"");
//						} catch (VoiceResultException e) {
//							errStr = e.getMessage();
//							LogUtil.e("errStr", errStr+"");
//						}
//						refreshUI(result, errStr);
//					}
//					
//					private void refreshUI(final boolean result, final String errStr){
//						context.runOnUiThread(new Runnable() {
//							
//							@Override
//							public void run() {
//								if(waittingDialog != null){
//									waittingDialog.dismiss();
//								}
//								if(result){
//									sendBuySucceedBroadcast(context,productInfo.getId(),VALUE_TYPE_VOICE_BUY);
//									successRunnable.run();
//									ToastUtil.showToast(context, R.string.voice_buy_success);
//								}else{
//									if(TextUtils.isEmpty(errStr)){
//										ToastUtil.showToast(context, R.string.voice_buy_fail);
//									}else{
//										ToastUtil.showToast(context, errStr);
//									}
//								}
//							}
//						});
//					}
//					
//				}.start();
//			}
//		};
//		showDialog(context, confirmRunnable, null, productInfo.getTitle(), productInfo.getId(), productInfo.getListenPrice(), productInfo.getReadPointPrice(), null, null, productInfo.getCover(), null, isCtwap);
//		return true;
//	}
//	
//	public static void showOrderDialog(Activity context, final DealBuyRunnable confirmRunnable, int chapterNum, int readPointPrice){
//		final Dialog dialog = CommonUtil.getCommonDialog(context, R.layout.dialog_buy_chapter_book, context.getString(R.string.book_content_buy_ok));
//		TextView chapterNumTV = (TextView) dialog.findViewById(R.id.total_chapter_tv);
//		TextView totalReadpointPriceTV = (TextView) dialog.findViewById(R.id.total_price_tv);
//		TextView dialogHintTV = (TextView) dialog.findViewById(R.id.hint_tv);
//		
//		chapterNumTV.setText(context.getResources().getString(R.string.reader_charpter_total, chapterNum));
//		totalReadpointPriceTV.setText(context.getResources().getString(R.string.reader_charpter_total_price, readPointPrice * chapterNum));
//		dialogHintTV.setText(context.getResources().getString(R.string.reader_charpter_hint, readPointPrice * chapterNum));
//		
//		Button okBtn = (Button) dialog.findViewById(R.id.confirm_btn);
//		Button cancelBtn = (Button) dialog.findViewById(R.id.cancel_btn);
//		
//		boolean isCtwapTmp;
//		if(ClientInfoUtil.isClientCtwap(context) && PreferencesUtil.getInstance(context).getPayMode() == PreferencesUtil.PAY_MOBILE_FEE){//CTWAP网络
//			isCtwapTmp = true;
//		}else{//非CTWAP网络
//			isCtwapTmp = false;
//		}
//		final boolean isCtwap = isCtwapTmp;
//		
//		OnClickListener lOnClickListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				confirmRunnable.run(isCtwap);
//				dialog.dismiss();
//			}
//
//		};
//		OnClickListener rOnClickListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//			}
//
//		};
//		DialogUtil.dealDialogBtn(okBtn, R.string.btn_text_confirm, lOnClickListener, cancelBtn, R.string.btn_text_cancel, rOnClickListener);
//		
//		if (!context.isFinishing()) {
//			dialog.show();
//		}
//	}
//	
//	public static void showDialog(Activity content, final DealBuyRunnable confirmRunnable, final Runnable cancelRunnable,
//			String name, String contentID, String price, String readPointPrice, String offersPrice, String chapterName,
//			String imageUrl, final DealBuyRunnable serialRunnable, final boolean isCtwap){
//		final Dialog dialog = CommonUtil.getCommonDialog(content, R.layout.dialog_buy_book, content.getString(R.string.book_title,	name));
//		ImageView coverIV = (ImageView) dialog.findViewById(R.id.book_cover_iv);
//		ImageLoader imageLoader = new ImageLoader(content);
//		imageLoader.setImageViewBitmap(imageUrl, contentID, coverIV, R.drawable.book_default);
//		if(!TextUtils.isEmpty(chapterName)){
//			TextView chapterNameTV = (TextView) dialog.findViewById(R.id.book_chapter_name_tv);
//			chapterNameTV.setText(content.getString(R.string.dialog_buy_chapter_name, chapterName));
//			chapterNameTV.setVisibility(View.VISIBLE);
//		}
//		TextView priceTV = (TextView) dialog.findViewById(R.id.book_price);
//		TextView offersPriceTV = (TextView) dialog.findViewById(R.id.book_offers_price);
//		TextView readPointTV = (TextView) dialog.findViewById(R.id.read_point_price);
//		priceTV.setText((content.getString(R.string.dialog_buy_offers_price, price)));
//		readPointTV.setText(content.getString(R.string.book_content_read_point_price, readPointPrice));
//		readPointTV.setVisibility(View.VISIBLE);
//		if (!TextUtils.isEmpty(offersPrice)) {
//			priceTV.getPaint().setStrikeThruText(true);
//			offersPriceTV.setText(content.getString(R.string.book_content_offers_price_in_read, offersPrice));
//			offersPriceTV.setVisibility(View.VISIBLE);
//		} else {
//			offersPriceTV.setVisibility(View.GONE);
//		}
//		priceTV.setVisibility(View.VISIBLE);
//		
//		final CheckBox serialBuyCB = (CheckBox) dialog.findViewById(R.id.serial_book_auto_buy_cb);
//		if(serialRunnable != null){
//			serialBuyCB.setVisibility(View.VISIBLE);
//		}		
//		Button okBtn = (Button) dialog.findViewById(R.id.confirm_btn);
//		Button cancelBtn = (Button) dialog.findViewById(R.id.cancel_btn);
//		if (isCtwap) {// CTWAP
//			readPointTV.setVisibility(View.GONE);
//		} else {
//			priceTV.setVisibility(View.GONE);
//			offersPriceTV.setVisibility(View.GONE);
//		}
//		OnClickListener lOnClickListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if(serialRunnable != null){
//					serialRunnable.run(serialBuyCB.isChecked());
//				}
//				confirmRunnable.run(isCtwap);
//				dialog.dismiss();
//			}
//
//		};
//		OnClickListener rOnClickListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (null != cancelRunnable) {
//					cancelRunnable.run();
//				}
//				dialog.dismiss();
//			}
//
//		};
//		DialogUtil.dealDialogBtn(okBtn, R.string.btn_text_confirm, lOnClickListener, cancelBtn, R.string.btn_text_cancel, rOnClickListener);
//		
//		if (!content.isFinishing()) {
//			dialog.show();
//		}
//	}
//	
//	public interface HandlerRunnableForAutoPay{
//		public void run(boolean isSuccess);
//	}
//	
//	public static void showBuyByChapter(final Activity context
//			,final ContentInfo contentInfo,String chapterID, final OrderChapterCallBack orderChapterCallBack) {
//		if (contentInfo.volumnInfoList == null 
//				|| contentInfo.volumnInfoList.size() == 0 || orderChapterCallBack == null) {
//			return;
//		}
//		final ArrayList<ChapterInfo> tempList = new ArrayList<ChapterInfo>();
//		for (VolumnInfo volumnInfo : contentInfo.volumnInfoList) {
//			if (volumnInfo != null && volumnInfo.chapterInfoList != null) {
//				tempList.addAll(volumnInfo.chapterInfoList);
//			}
//		}
//		int itemPosition = 1;
//		final ArrayList<ChapterInfo> chapterInfoList = new ArrayList<ChapterInfo>();
//		int itemNum = 0;
//		for (ChapterInfo chapterInfo : tempList) {
//			if (chapterInfo.type == ChapterInfo.COST_TYPE_PAY) {
//				++itemNum;
//				if (!TextUtils.isEmpty(chapterID) && chapterInfo.chapterID.equals(chapterID)) {
//					itemPosition = itemNum;
//				}
//				chapterInfoList.add(chapterInfo);
//			}
//		}
//		if (chapterInfoList.size() == 0) {
//			ToastUtil.showToast(context, R.string.book_content_buy_no_chapter);
//			return;
//		}
//		final CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(context);
//		builder.setTitle(context.getString(R.string.book_title, contentInfo.contentName));
//		final ListView listView = new ListView(context);
//		listView.addHeaderView(LayoutInflater.from(context).inflate(R.layout.chapter_list_head_lay, null));
//
//		final boolean isMobileFee = ClientInfoUtil.isClientCtwap(context) && PreferencesUtil.getInstance(context).getPayMode() == PreferencesUtil.PAY_MOBILE_FEE;
//		ArrayAdapter<ChapterInfo> adapter = null;
//		if(isMobileFee) {
//			adapter = new ArrayAdapter<ChapterInfo>(context, R.layout.buy_chapter_item_single_choice,R.id.checked_tip_tv ,chapterInfoList);
//			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//		}else{//仅支持阅点批量订购，不支持话费批量订购，判断出是阅点支付的场景下才出现批量订购。
//			adapter = new ArrayAdapter<ChapterInfo>(context, R.layout.buy_chapter_item, R.id.checked_tip_tv,chapterInfoList);
//			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//		}
//		listView.setAdapter(adapter);
//
//		listView.setItemChecked(itemPosition, true);
//		if(itemPosition == 1){
//			itemPosition = 0;
//		}
//		listView.setSelection(itemPosition);
//		setListViewFastScroller(listView);
//		builder.setView(listView);
//		CustomDialogInterface.OnClickListener lOnClickListener = new CustomDialogInterface.OnClickListener() {
//			
//			@Override
//			public void onClick(View view, int which) {
//				int selectedCount = 0;
//				ArrayList<ChapterInfo> chapterInfos = new ArrayList<ChapterInfo>();
//				SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
//				
//				ChapterCartInfo cartInfo = new ChapterCartInfo();
//				cartInfo.contentId = contentInfo.contentID;
//				ArrayList<String> chapterIds = new ArrayList<String>();
//				for(int i = 0;i<sparseBooleanArray.size();i++){
//					if(sparseBooleanArray.valueAt(i)){
//						selectedCount++;
//						ChapterInfo chapterInfo = chapterInfoList.get(sparseBooleanArray.keyAt(i) - 1);
//						chapterInfos.add(chapterInfo);
//						chapterIds.add(chapterInfo.chapterID);
//					}
//				}
//				cartInfo.chapterIds = chapterIds;
//				if(selectedCount < 1){
//					view.setTag(true);
//					ToastUtil.showToast(context, R.string.please_select_chapter);
//				}else if(selectedCount > 20){
//					view.setTag(true);
//					ToastUtil.showToast(context, R.string.exceed_selected_num);
//				} else {
////					ChapterCartInfo cartInfo = new ChapterCartInfo();
////					cartInfo.contentId = contentInfo.contentID;
////					ArrayList<String> chapterIds = new ArrayList<String>();
////					for (int i = 0; i < selectedCount; i++) {
////						ChapterInfo chapterInfo = chapterInfoList.get(sparseBooleanArray.keyAt(i) - 1);
////						chapterInfos.add(chapterInfo);
////						chapterIds.add(chapterInfo.chapterID);
////					}
////					cartInfo.chapterIds = chapterIds;
//					if(isMobileFee) {
//						if(chapterInfos != null && chapterInfos.size() == 1){
//							orderChapterCallBack.showBuyBookDialog(chapterInfos.get(0));
//						}
//					}else{
//						orderChapterCallBack.showBuyChapterBookDialog(selectedCount, Integer.parseInt(contentInfo.readPointPrice), cartInfo, chapterInfos);
//					}
//				}
//				
//			}
//		};
//		
//		CustomDialogInterface.OnClickListener rOnClickListener = new CustomDialogInterface.OnClickListener(){
//			@Override
//			public void onClick(View view, int which) {
//
//			}
//		};
//		builder.setPositiveButton(R.string.btn_text_confirm, lOnClickListener);			 
//		builder.setNegativeButton(rOnClickListener);
//	
//		Dialog dialog = null;
//		if(!isMobileFee) {//仅支持阅点批量订购，不支持话费批量订购，判断出是阅点支付的场景下才出现批量订购。
//			View view = LayoutInflater.from(context).inflate(R.layout.chapter_bottom_lay, null);
//			builder.setBottomView(view);
//			final Dialog tempDialog = builder.create();
//			dialog = tempDialog;
//			final TextView choiseTV = ((TextView)view.findViewById(R.id.choised_tv));
//			final TextView totalPriceTV = ((TextView)view.findViewById(R.id.total_price_tv));
//			choiseTV.setText(context.getString(R.string.reader_charpter_choised, 1));
//			final int perReadPointPrice = Integer.parseInt(contentInfo.readPointPrice);
//			totalPriceTV.setText(context.getString(R.string.reader_charpter_total_price, perReadPointPrice));
//			
//			listView.setOnItemClickListener(new OnItemClickListener() {
//
//				@Override
//				public void onItemClick(AdapterView<?> parent, View view,
//						int position, long id) {
//					if(position == 0){
//						tempDialog.dismiss();
//						orderChapterCallBack.gotoRead();
//					}else{
//						SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
//						if(sparseBooleanArray != null){
//							int size = sparseBooleanArray.size();
//							int selectedCount = 0;
//							for(int i = 0; i < size; i++){
//								if(sparseBooleanArray.valueAt(i)){
//									selectedCount++;
//								}
//							}
//							if(selectedCount > 20){
//								listView.setItemChecked(position, false);
//								ToastUtil.showToast(context, R.string.exceed_selected_num);
//								return;
//							}
//							choiseTV.setText(context.getString(R.string.reader_charpter_choised, selectedCount));
//							totalPriceTV.setText(context.getString(R.string.reader_charpter_total_price, selectedCount*perReadPointPrice));
//						}
//					}
//				}
//			});
//		}else{
//			dialog = builder.create();
//		}
//		if (!context.isFinishing()) {
//			dialog.show();
//		}
//	}
//	
//	private static void setListViewFastScroller(ListView listView){
//		listView.setFastScrollEnabled(true);
//		try {
//			Field f = AbsListView.class.getDeclaredField("mFastScroller");
//			f.setAccessible(true);
//			Object object = f.get(listView);
//			f = f.getType().getDeclaredField("mThumbDrawable");
//			f.setAccessible(true);
//			Drawable drawable = (Drawable)f.get(object);
//			drawable = listView.getResources().getDrawable(R.drawable.fast_scroller);
//			f.set(object, drawable);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public static interface OrderChapterCallBack{
//		public void showBuyBookDialog(final ChapterInfo chapterInfo);
//		public void showBuyChapterBookDialog(int chapterNum, int readPointPrice, final ChapterCartInfo cartInfo, final ArrayList<ChapterInfo> chapterInfos);
//		public void gotoRead();
//	}
//}
