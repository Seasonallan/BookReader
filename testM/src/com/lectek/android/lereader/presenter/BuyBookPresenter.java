package com.lectek.android.lereader.presenter;
//package com.lectek.android.sfreader.presenter;
//
//import java.util.ArrayList;
//
//import android.content.Context;
//import android.text.TextUtils;
//
//import com.lectek.android.sfreader.data.ChapterCartInfo;
//import com.lectek.android.sfreader.data.ChapterInfo;
//import com.lectek.android.sfreader.data.ContentInfo;
//import com.lectek.android.sfreader.data.ContentProductInfo;
//import com.lectek.android.sfreader.data.ShoppingCartInfo;
//import com.lectek.android.sfreader.data.SubscribeContent;
//import com.lectek.android.sfreader.data.VolumnInfo;
//import com.lectek.android.sfreader.net.DRMSaxParser;
//import com.lectek.android.sfreader.net.DataSaxParser;
//import com.lectek.android.sfreader.net.data.ResponseResultCode;
//import com.lectek.android.sfreader.net.exception.ResultCodeException;
//import com.lectek.android.sfreader.net.exception.ServerErrException;
//import com.lectek.android.util.LogUtil;
///**
// * @author linyiwei
// * @date 2012-02-29
// * @email 21551594@qq.com.com
// */
//public class BuyBookPresenter extends BasePresenter{
//	/** 显示有更新章节的提示（可执行在UI线程）
//	 * @param contentInfo
//	 */
//	public static void showChapterList(final ContentInfo contentInfo
//			,final LoadChapterListRunnadle mLoadChapterListUIRunnadle){
//
//		if(mLoadChapterListUIRunnadle != null){
//			mLoadChapterListUIRunnadle.onPreRunUI();
//		}
//		new Thread() {
//			@Override
//			public void run() {
//				// ChapterListData chapterListData =
//				// DataSaxParser.getInstance(mContext).getChapterList(contentInfo.contentID);
//				if (TextUtils.isEmpty(contentInfo.price) || contentInfo.volumnInfoList == null) {
//					ContentInfo ci;
//					try {
//						ci = DataSaxParser.getInstance(getContext()).getContentInfo(contentInfo.contentID);
//						if (ci != null) {
//							contentInfo.price = ci.price;
//							contentInfo.offersPrice = ci.offersPrice;
//							contentInfo.readPointPrice = ci.readPointPrice;
//							contentInfo.chargeDesc = ci.chargeDesc;
//							contentInfo.volumnInfoList = ci.volumnInfoList;
//							contentInfo.contentType = ci.contentType;
//						}
//					} catch (ResultCodeException e) {
//						LogUtil.e("BuyBookPresenter", e);
//					} catch (ServerErrException e) {
//						LogUtil.e("BuyBookPresenter", e);
//					}
//				}
//				// if(chapterListData != null && chapterListData.volumnInfoList
//				// != null){
//				final ArrayList<ChapterInfo> tempList;
//				if (contentInfo != null && contentInfo.volumnInfoList != null) {
//					tempList = new ArrayList<ChapterInfo>();
//					// for(VolumnInfo volumnInfo :
//					// chapterListData.volumnInfoList){
//					for (VolumnInfo volumnInfo : contentInfo.volumnInfoList) {
//						if (volumnInfo != null && volumnInfo.chapterInfoList != null) {
//							tempList.addAll(volumnInfo.chapterInfoList);
//						}
//					}
//				}else{
//					tempList = null;
//				}
//				runInUI(new Runnable() {
//
//					@Override
//					public void run() {
//						if(mLoadChapterListUIRunnadle != null){
//							mLoadChapterListUIRunnadle.onPostRunUI(tempList);
//						}
//					}
//				});
//			}
//
//		}.start();
//	
//	}
//	/** 处理购买流程（需要执行在子线程）
//	 * @param contentId 内容ID
//	 * @param chapterInfo 章节ID；如果不为空按章购买，否则按整本购买
//	 * @param giftAccount 赠送帐号；如果为空是购买，否则按赠送购买
//	 * @param isCtwap 购买方式；TRUE为话费购买，FALSE为阅点购买
//	 * @return
//	 */
//	public static boolean dealOrderContent(final String contentId,final ChapterInfo chapterInfo
//			,final String giftAccount, String blessingstr,final boolean isCtwap,final DealOrderRunnadle mDealOrderRunnadle){
//		Context context = getContext();
//		runInUI(new Runnable() {
//			
//			@Override
//			public void run() {
//				if(mDealOrderRunnadle != null){
//					mDealOrderRunnadle.onPreRunUI();
//				}
//			}
//		});
//		String tempID = null;
//		String resultCode = ResponseResultCodeTY.STATUS_FAULT;
//		
//		if (chapterInfo != null) {
//			tempID = chapterInfo.chapterID;
//		}
//		final String chapterID = tempID;
//		boolean isSuccess = false;
//		// 获取产品信息
//		ContentProductInfo info = DataSaxParser.getInstance(context).getContentProductInfo(contentId, chapterID);
//		LogUtil.v("giftAccount: " + giftAccount);
//		if (info != null) {
//			//TODO test ctwap
//			if (isCtwap) {// CTWAP
//				// 订阅内容
//				SubscribeContent subscribeContent;
//				try {
////					subscribeContent = DataSaxParser.getInstance(context)
////							.subscribeContent(info.productInfo.productID, contentId, chapterID);
//					if(TextUtils.isEmpty(giftAccount)){
//						subscribeContent = DataSaxParser.getInstance(context).subscribeContentSSO(info.productInfo.productID,
//								contentId, chapterID, giftAccount);
//					}else{
//						subscribeContent = DataSaxParser.getInstance(context).giftContentBySSO(info.productInfo.productID,
//								contentId, giftAccount, blessingstr);
//					}
//					if (subscribeContent != null) {
//						if (!TextUtils.isEmpty(subscribeContent.requestURL)) {
////							/* isSuccess = */DataSaxParser.getInstance(context).orderArea(subscribeContent.requestURL);
//							/* isSuccess = */DataSaxParser.getInstance(context).orderSSOMessage(subscribeContent.requestURL);
//						}
//						if (/* isSuccess && */!TextUtils.isEmpty(subscribeContent.billingURL)) {
////							isSuccess = DataSaxParser.getInstance(context).orderArea(subscribeContent.billingURL);
//							isSuccess = DataSaxParser.getInstance(context).orderSSOMessage(subscribeContent.billingURL);
//						}
//					}
//				} catch (final ResultCodeException e) {
//					resultCode = e.getResultCode();
//					runInUI(new Runnable() {
//						
//						@Override
//						public void run() {
//							if(mDealOrderRunnadle != null){
//								mDealOrderRunnadle.onFailUI( e.getMessage());
//							}
//						}
//					});
//				} catch (final Exception e) {
//					LogUtil.e("subscribeContent", e);
//					runInUI(new Runnable() {
//						
//						@Override
//						public void run() {
//							if(mDealOrderRunnadle != null){
//								mDealOrderRunnadle.onFailUI( e.getMessage());
//							}
//						}
//					});
//				}
//			} else {// WIFI or other network
//				try {
//					if(!TextUtils.isEmpty(giftAccount)){//如果不为空则按赠送方式购买
//						isSuccess = DataSaxParser.getInstance(context).giftContentByReadPoint(info.productInfo.productID, 
//								contentId, giftAccount, blessingstr);
//					}else{
//						isSuccess = DataSaxParser.getInstance(context)
//								.subscribeContentByReadPoint(info.productInfo.productID, contentId, chapterID);
//					}
//				} catch (final ResultCodeException e) {
//					resultCode = e.getResultCode();
//					runInUI(new Runnable() {
//						
//						@Override
//						public void run() {
//							if(mDealOrderRunnadle != null){
//								mDealOrderRunnadle.onResultCodeFailUI(e.getResultCode(), e.getMessage());
//							}
//						}
//					});
//				} catch (final Exception e) {
//					runInUI(new Runnable() {
//						
//						@Override
//						public void run() {
//							if(mDealOrderRunnadle != null){
//								mDealOrderRunnadle.onFailUI( e.getMessage());
//							}
//						}
//					});
//				}
//			}
//		} else {
//			runInUI(new Runnable() {
//				
//				@Override
//				public void run() {
//					if(mDealOrderRunnadle != null){
//						mDealOrderRunnadle.onFailUI( null);
//					}
//				}
//			});
//		}
//		if (isSuccess && chapterInfo == null && TextUtils.isEmpty(giftAccount)) {//下载ticket（许可）
//			try {
//				DRMSaxParser.getInstance(context).findContentTicket(contentId);
//			} catch (Exception e) {
//			}
//		}
//		if (isSuccess) {
//			resultCode = ResponseResultCodeTY.STATUS_OK;
//			if(mDealOrderRunnadle != null){
//				mDealOrderRunnadle.onSucceed();
//			}
//		}
//		if (isSuccess) {
//			boolean isConfirm = DataSaxParser.getInstance(context).orderConfirm("1", info.productInfo.productID, chapterID, contentId);
//			if(mDealOrderRunnadle != null){
//				mDealOrderRunnadle.onOrderConfirm(isConfirm);
//			}
//		}
//		final boolean temp = isSuccess;
//		final String tempCode = resultCode;
//		runInUI(new Runnable() {
//			
//			@Override
//			public void run() {
//				if(mDealOrderRunnadle != null){
//					mDealOrderRunnadle.onPostRunUI(tempCode);
//				}
//			}
//		});
//		return isSuccess;
//	}
//	/**
//	 * 处理订购包月
//	 * 
//	 * @param context
//	 * @param catalogID
//	 * @param handlerRunnable
//	 * @return
//	 */
//	public static boolean dealOrderArea(String catalogID,boolean isCtwap,final DealOrderRunnadle mDealOrderRunnadle) {
//		Context context = getContext();
//		runInUI(new Runnable() {
//			
//			@Override
//			public void run() {
//				if(mDealOrderRunnadle != null){
//					mDealOrderRunnadle.onPreRunUI();
//				}
//			}
//		});
//		boolean isSuccess = false;
//		String resultCode = ResponseResultCodeTY.STATUS_FAULT;
//		// if(!ApnUtil.isWifiWork(context) && ApnUtil.isCtwap(context)){//CTWAP
//		if (isCtwap) {// CTWAP
////			String requestURL = DataSaxParser.getInstance(context).subscribepackproduct(catalogID);
//			//SSO
//			String requestURL = DataSaxParser.getInstance(context).subscribepackproductBySSO(catalogID);
//			if (!TextUtils.isEmpty(requestURL)) {
//				try {
////					isSuccess = DataSaxParser.getInstance(context).orderArea(requestURL);
//					isSuccess = DataSaxParser.getInstance(context).orderSSOMessage(requestURL);
//				} catch (final ResultCodeException e) {
//					resultCode = e.getResultCode();
//					runInUI(new Runnable() {
//						
//						@Override
//						public void run() {
//							if(mDealOrderRunnadle != null){
//								mDealOrderRunnadle.onFailUI( e.getMessage());
//							}
//						}
//					});
//				} catch (final ServerErrException e) {
//					runInUI(new Runnable() {
//						
//						@Override
//						public void run() {
//							if(mDealOrderRunnadle != null){
//								mDealOrderRunnadle.onFailUI( e.getMessage());
//							}
//						}
//					});
//				}
//			}
//		} else {
//			try {
//				isSuccess = DataSaxParser.getInstance(context).subscribepackproductByReadpoint(catalogID);
//			} catch (final ResultCodeException e) {
//				resultCode = e.getResultCode();
//				runInUI(new Runnable() {
//					
//					@Override
//					public void run() {
//						if(mDealOrderRunnadle != null){
//							mDealOrderRunnadle.onResultCodeFailUI(e.getResultCode(), e.getMessage());
//						}
//					}
//				});
//			} catch (final ServerErrException e) {
//				runInUI(new Runnable() {
//					
//					@Override
//					public void run() {
//						if(mDealOrderRunnadle != null){
//							mDealOrderRunnadle.onFailUI( e.getMessage());
//						}
//					}
//				});
//			}
//		}
//		if (isSuccess) {
//			resultCode = ResponseResultCodeTY.STATUS_OK;
//			if(mDealOrderRunnadle != null){
//				mDealOrderRunnadle.onSucceed();
//			}
//		}
//		if (isSuccess) {
//			boolean isConfirm = DataSaxParser.getInstance(context).orderConfirm("2", catalogID, null, null);
//			if(mDealOrderRunnadle != null){
//				mDealOrderRunnadle.onOrderConfirm(isConfirm);
//			}
//		}
//		final boolean temp = isSuccess;
//		final String tempCode = resultCode;
//		runInUI(new Runnable() {
//			
//			@Override
//			public void run() {
//				if(mDealOrderRunnadle != null){
//					mDealOrderRunnadle.onPostRunUI(tempCode);
//				}
//			}
//		});
//		return isSuccess;
//	
//	}
//	
//	/**
//	 * 处理连载书籍批量章节订购
//	 * 
//	 * @param totalPrice		本次订单共计费用
//	 * @param chapterCartInfo
//	 * @param mDealOrderRunnadle
//	 * @return
//	 */
//	public static boolean dealMultiChapterOrder(String totalPrice, ChapterCartInfo chapterCartInfo, final DealOrderRunnadle mDealOrderRunnadle) {
//		Context context = getContext();
//		runInUI(new Runnable() {
//			
//			@Override
//			public void run() {
//				if(mDealOrderRunnadle != null){
//					mDealOrderRunnadle.onPreRunUI();
//				}
//			}
//		});
//		boolean isSuccess = false;
//		String resultCode = ResponseResultCodeTY.STATUS_FAULT;
//		// 获取产品信息
//		if (chapterCartInfo != null && chapterCartInfo.chapterIds != null) {
//			ContentProductInfo info = null;
//			info = DataSaxParser.getInstance(context).getContentProductInfo(chapterCartInfo.contentId, chapterCartInfo.chapterIds.get(0));
//			if (info != null && info.productInfo != null) {
//				chapterCartInfo.productId = info.productInfo.productID;
//				ShoppingCartInfo shoppingCartInfo = new ShoppingCartInfo();
//				ArrayList<ChapterCartInfo> chapterCartInfos = new ArrayList<ChapterCartInfo>();
//				chapterCartInfos.add(chapterCartInfo);
//				shoppingCartInfo.chapterCartInfos = chapterCartInfos;
//				try {
//					isSuccess = DataSaxParser.getInstance(context).subscribeMultiProducts(totalPrice, shoppingCartInfo);
//				} catch (final ResultCodeException e) {
//					resultCode = e.getResultCode();
//					runInUI(new Runnable() {
//						
//						@Override
//						public void run() {
//							if(mDealOrderRunnadle != null){
//								mDealOrderRunnadle.onResultCodeFailUI( e.getResultCode(), e.getMessage());
//							}
//						}
//					});
//				} catch (final ServerErrException e) {
//					runInUI(new Runnable() {
//						
//						@Override
//						public void run() {
//							if(mDealOrderRunnadle != null){
//								mDealOrderRunnadle.onFailUI( e.getMessage());
//							}
//						}
//					});
//				}
//			}
//		}
//				
//		if (isSuccess) {
//			resultCode = ResponseResultCodeTY.STATUS_OK;
//			if(mDealOrderRunnadle != null){
//				mDealOrderRunnadle.onSucceed();
//			}
//		}
//		final String tempCode = resultCode;
//		runInUI(new Runnable() {
//			
//			@Override
//			public void run() {
//				if(mDealOrderRunnadle != null){
//					mDealOrderRunnadle.onPostRunUI(tempCode);
//				}
//			}
//		});
//
//		return isSuccess;
//	
//	}
//	
//	public interface DealOrderRunnadle{
//		/**
//		 * 流程开始前执行的回调
//		 * @return
//		 */
//		public boolean onPreRunUI();
//		/**
//		 * 流程执行结束时的回调
//		 * @param resultCode
//		 */
//		public void onPostRunUI(String resultCode);
//		/**
//		 * 结果出错的回调
//		 * */
//		public void onResultCodeFailUI(String resultCode, String msg);
//		/**
//		 * 其他出错的回调
//		 * */
//		public void onFailUI(String msg);
//		/**
//		 * 流程执行成功时补充操作（可选）
//		 * @param msg
//		 */
//		public void onSucceed();
//		/**
//		 * 计费确认
//		 * @param isConfirm
//		 */
//		public void onOrderConfirm(boolean isConfirm);
//	}
//	
//	public interface LoadChapterListRunnadle{
//		public boolean onPreRunUI();
//		public void onPostRunUI(ArrayList<ChapterInfo> chapterInfoList);
//	}
//}
