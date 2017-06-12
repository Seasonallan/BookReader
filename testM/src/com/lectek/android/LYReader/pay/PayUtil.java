package com.lectek.android.LYReader.pay;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.LYReader.pay.alipay.AliConfig;
import com.lectek.android.LYReader.pay.alipay.MobileSecurePayHelper;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.lib.recharge.AlipayPayHandler;
import com.lectek.android.lereader.lib.recharge.CTCMessageOrderInfo;
import com.lectek.android.lereader.lib.recharge.CTCMessagePayHandler;
import com.lectek.android.lereader.lib.recharge.IPayHandler;
import com.lectek.android.lereader.lib.recharge.PayConst;
import com.lectek.android.lereader.lib.recharge.PayOrder;
import com.lectek.android.lereader.lib.utils.ApnUtil;
import com.lectek.android.lereader.lib.utils.IProguardFilterOnlyPublic;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.lib.utils.PkgManagerUtil;
import com.lectek.android.lereader.lib.utils.StringUtil;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.openapi.ApiProcess4TianYi;
import com.lectek.android.lereader.permanent.ApiConfig;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.permanent.OrderConfig;
import com.lectek.android.lereader.ui.specific.SlideActivityGroup;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.IMSIUtil;

/**
 * 购买工具类
 * 
 * @author zhouxinghua
 * @edited by chends@lectek.com 2014/07/14
 * 
 */
public class PayUtil {

	private static final String Tag = PayUtil.class.getSimpleName();
	
	/**
	 * 显示购买对话框
	 * 
	 * @param activity
	 * @param contentInfo
	 * @param orderInfo
	 */
	public static Dialog showBuyDialog(Activity activity, final BuyInfo buyInfo, final OnPayCallback buyCallback) {
		
//		Activity activity = SlideActivityGroup.getInstance();
		
		final Dialog dialog = DialogUtil.customDialog(activity);
		Display display = activity.getWindowManager().getDefaultDisplay();
		LinearLayout.LayoutParams lp = new LayoutParams(display.getWidth() * 4 / 5, LayoutParams.WRAP_CONTENT);
		dialog.setContentView(LayoutInflater.from(activity).inflate(R.layout.dialog_buy_layout, null), lp);
		dialog.setCanceledOnTouchOutside(true);

		TextView nameTV = (TextView) dialog.findViewById(R.id.book_name_tv);
		TextView originalTag = (TextView) dialog.findViewById(R.id.original_price_tag);
		TextView originalPrice = (TextView) dialog.findViewById(R.id.original_price_tv);
		TextView preferentialPrice = (TextView) dialog.findViewById(R.id.preferential_price_tv);
		
		//支付按钮设置
		Button alipayBtn = (Button) dialog.findViewById(R.id.alipay_btn);
		Button chinatelecomPhoneMoneyPayBtn = (Button) dialog.findViewById(R.id.chinatelecom_phone_money_pay_btn);
		Button chinatelecomMessagePayBtn = (Button) dialog.findViewById(R.id.chinatelecom_message_pay_btn);
		Button surfingReadPointPayBtn = (Button) dialog.findViewById(R.id.surfing_read_point);
		
		alipayBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buyCallback.onBuyBtnClick(PayConst.PAY_TYPE_ALIPAY);
				dialog.dismiss();
			}
		});

		chinatelecomPhoneMoneyPayBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buyCallback.onBuyBtnClick(PayConst.PAY_TYPE_CHINATELECOM_FEE);
				dialog.dismiss();
			}
		});
		
		surfingReadPointPayBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buyCallback.onBuyBtnClick(PayConst.PAY_TYPE_TY_READ_POINT);
				dialog.dismiss();
			}
		});
		
		chinatelecomMessagePayBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buyCallback.onBuyBtnClick(PayConst.PAY_TYPE_CHINATELECOM_MESSAGE_PAY);
				dialog.dismiss();
			}
		});
		
		//支付详情设置
		nameTV.setText(buyInfo.bookName);

		String originalStr = activity.getString(R.string.original_price, buyInfo.price);
		originalPrice.setText(originalStr);
		// 有优惠价
		if (!TextUtils.isEmpty(buyInfo.discountPrice)) {
			preferentialPrice.setVisibility(View.VISIBLE);
			originalTag.setText(R.string.original_price_tag);
			String promotionStr = activity.getString(R.string.promotion_price, buyInfo.discountPrice);
			SpannableStringBuilder style = new SpannableStringBuilder(promotionStr);
			style.setSpan(new ForegroundColorSpan(Color.RED), 4, promotionStr.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			preferentialPrice.setText(style);

			SpannableStringBuilder style2 = new SpannableStringBuilder(originalStr);
			style2.setSpan(new ForegroundColorSpan(Color.GRAY), 0, originalStr.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			originalPrice.setText(style2);
			originalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		}
		
		//设置支付方式
		//如果是乐阅书籍支持支付宝支付
		if(!buyInfo.isSurfingBook) {
			alipayBtn.setVisibility(View.VISIBLE);
//			if(ApnUtil.isCDMA(activity) || ApnUtil.isCtwap(activity) 
//					|| ApnUtil.isCmnet(activity) 
//					|| IMSIUtil.getMobileType(activity) == IMSIUtil.MOBILE_TYPE_CHINA_TELECOM) {
//				
//				chinatelecomMessagePayBtn.setVisibility(View.VISIBLE);
//			}
			
		}else {
			surfingReadPointPayBtn.setVisibility(View.VISIBLE);
			//电信ctwap网络支持话费支付
			if(ApnUtil.isCtwap(activity)) {
				chinatelecomPhoneMoneyPayBtn.setVisibility(View.VISIBLE);
			}
		}
		
		dialog.show();
		return dialog;
	}
	
	/**
	 * 处理支付流程
	 * @param payType 支付类型
	 * @param contentInfoLeyue	内容信息
	 * @param dealPayRunnable	反馈回调接口
	 */
	public static IPayHandler dealPay(Activity context,int payType){
		context = CommonUtil.getRealActivity(context);
		IPayHandler handler = null;
		switch (payType) {
		case PayConst.PAY_TYPE_ALIPAY:
			handler = new AlipayPayHandler(context);
			break;
		case PayConst.PAY_TYPE_CHINATELECOM_MESSAGE_PAY:
			handler = new CTCMessagePayHandler(context);
			break;
		case PayConst.PAY_TYPE_LEREADER_READ_POINT:
			handler = new RewardPointPayHandler(context);
			break;
		case PayConst.PAY_TYPE_TY_READ_POINT:
			handler = new TYReadPointPayHandler(context);	
		default:
			break;
		}
		
		return handler;
	}
	

//	/**
//	 * 获取应支付的钱数
//	 * @param contentInfo
//	 * @return
//	 */
//	private Double getCharge(ContentInfoLeyue contentInfo){
//		if(contentInfo.getLimitType() != null && contentInfo.getLimitType() == OrderConfig.LIMIT_TYPE_PRICE  && contentInfo.getLimitPrice() > 0){
//			return contentInfo.getLimitPrice();
//		}else if (TextUtils.isEmpty(contentInfo.getPromotionPrice())) {
//			return Double.parseDouble(contentInfo.getPrice());
//		}else {
//			return Double.parseDouble(contentInfo.getPromotionPrice());
//		}
//	}
//	
//	/**
//	 * 初始化订单信息
//	 * @param contentInfo
//	 * @return
//	 */
//	private PayOrder initPayOrder(ContentInfoLeyue contentInfo){
//		PayOrder order = new PayOrder();
//		String bookId = contentInfo.getBookId();
//		String bookName = contentInfo.getBookName();
//		String charge = getCharge(contentInfo).toString();
//		order.setBookId(bookId);
//		order.setBookName(bookName);
//		order.setCharge(charge);
//		order.setCalType(OrderConfig.ORDER_TYPE_BOOK + "");
//		return order;
//	}
	
	/**
	 * 生成电信短代订单信息
	 * @param context
	 * @param bookID
	 * @param fee
	 * @param bookName
	 * @return
	 */
	public static CTCMessageOrderInfo getCTCMessageOrderInfo(Context context, String bookID, Double fee, String bookName) {
		CTCMessageOrderInfo result = new CTCMessageOrderInfo();
		
		result.setUserId(AccountManager.getInstance().getUserID());
		
		if(TextUtils.isEmpty(bookID)) {
			LogUtil.e(Tag, "getCTCMessageOrderInfo 书籍ID为空");
			return null;
		}
		result.setBookId(bookID);
		
		result.setCalType(PayConst.ORDER_TYPE_BOOK);
		result.setCalObj(bookID);
		result.setCharge(fee);
		result.setPurchaser(AccountManager.getInstance().getUserID());
		result.setSourceType(Integer.parseInt(LeyueConst.SOURCE_TYPE));
		result.setVersion(PkgManagerUtil.getApkVersion(context));
		result.setAccount(AccountManager.getInstance().getUserInfo().getAccount());
		result.setCalObjName(bookName);
		result.setReleaseChannel(ApiConfig.PUBLISH_CHANNEL);
		result.setSalesChannel(ApiConfig.SALE_CHANNEL);
		result.apiHandler = ApiProcess4Leyue.getInstance(context);
		result.apiTYHandler = ApiProcess4TianYi.getInstance(context);
		
		return result;
	}
	
	/**
	 * 生成支付宝订单
	 * @param context
	 * @return
	 */
	public static PayOrder getAliPayOrderInfo(Activity context, String bookID, String charge, String bookName) {
		context = CommonUtil.getRealActivity(context);
		//检查是否有支付宝安全控件
		MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(context);
		boolean isMobile_spExist = mspHelper.detectMobile_sp();
		if (!isMobile_spExist){
			LogUtil.e(Tag, "未检测到支付宝安全控件");
			return null;
		}
		
		PayOrder result = new PayOrder();
		
		result.setBookId(bookID);
		result.setCalType(String.valueOf(PayConst.ORDER_TYPE_BOOK));
		result.setBookName(bookName);
		result.setCalObj(bookID);
		result.setCharge(charge);
		
		result.sourceType =  LeyueConst.SOURCE_TYPE;
		result.appVersionCode = PkgManagerUtil.getApkInfo(context).versionCode;
		result.setKey(AliConfig.RSA_PRIVATE);
		result.apiHandler = ApiProcess4Leyue.getInstance(context);
		
		
		return result;
	}
	
	/**
	 * 生成乐阅积分兑换书籍订单
	 * @param context
	 * @param bookID
	 * @param fee
	 * @param bookName
	 * @return
	 */
	public static RewardPointOrderInfo getRewardPayOrderInfo(Context context, String bookID, int fee, String bookName) {
		RewardPointOrderInfo result = new RewardPointOrderInfo();
		result.purchaseType = ApiConfig.PURCHASE_TYPE;
		result.bookID = bookID;
		result.bookName = bookName;
		result.fee = fee + 0f;
		result.apiHandler = ApiProcess4Leyue.getInstance(context);
		return result;
	}
	
	
	public static TYReadPointPayOrderInfo getTYReadPointPayOrderInfo(Context context, String tyBookID, String bookName, String fee,
							String leBookID) {
		TYReadPointPayOrderInfo result = new TYReadPointPayOrderInfo();
		result.bookID = tyBookID;
		result.bookName = bookName;
		result.fee = fee;
		result.leBookID = leBookID;
		result.orderType = PayConst.ORDER_TYPE_BOOK;
		result.payMode = "none";
		result.purchaseType = OrderConfig.PURCHASE_TYPE_TY_READ_POINT;
		result.sourceType = Integer.valueOf(LeyueConst.SOURCE_TYPE);
		result.userID = AccountManager.getInstance().getUserID();
		result.apiHandler = ApiProcess4Leyue.getInstance(context);
		result.apiHandlerTY = ApiProcess4TianYi.getInstance(context);
		return result;
	}
	
	public interface OnPayCallback {
		public void onBuyBtnClick(int payType);
	}

	public static class BuyInfo implements IProguardFilterOnlyPublic{
		public String bookName; 
		public String price;
		public String discountPrice;
		
		public boolean isSurfingBook;
	}
}
