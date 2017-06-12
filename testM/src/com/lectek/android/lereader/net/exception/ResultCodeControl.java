package com.lectek.android.lereader.net.exception;

import android.content.Context;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.net.ResponseResultCode;

/** 响应结果的状态码处理
 *    乐阅
 */
public final class ResultCodeControl {
	
	public static String getResultString(Context context, String resultCode) {
		int strIndex = -1;
		if (resultCode.equals(ResponseResultCode.STATUS_SYSTEM_ERROR)) {
			strIndex = R.string.result_content_system_error;
		} else if (resultCode.equals(ResponseResultCode.STATUS_DATA_BASE_ERROR)) {
			strIndex = R.string.result_content_database_error;
		} else if (resultCode.equals(ResponseResultCode.STATUS_UNAUTHORRIZED)) {
			strIndex = R.string.result_content_unauthorrized;
		}else if (resultCode.equals(ResponseResultCode.STATUS_PARAM_EMPTY)) {
			strIndex = R.string.result_content_param_empty;
		}else if (resultCode.equals(ResponseResultCode.STATUS_UPDATE_FAILD)) {
			strIndex = R.string.result_content_update_unsuccess;
		}else if (resultCode.equals(ResponseResultCode.STATUS_DELETE_FAILD)) {
			strIndex = R.string.result_content_delete_unsuccess;
		}else if (resultCode.equals(ResponseResultCode.STATUS_USER_UNAVAILABLE)) {
			strIndex = R.string.result_content_user_no_exist;
		}else if (resultCode.equals(ResponseResultCode.STATUS_USER_AVAILABLE)) {
			strIndex = R.string.result_content_user_exist;
		}else if (resultCode.equals(ResponseResultCode.STATUS_ACCOUNT_EMPTY)) {
			strIndex = R.string.result_content_account_empty;
		}else if (resultCode.equals(ResponseResultCode.STATUS_PSW_ERROR)) {
			strIndex = R.string.result_content_psw_error;
		}else if (resultCode.equals(ResponseResultCode.STATUS_PSW_EMPTY)) {
			strIndex = R.string.result_content_psw_empty;
		}else if (resultCode.equals(ResponseResultCode.STATUS_RECHARGE_FAILD)) {
			strIndex = R.string.result_content_recharge_faild;
		}else if (resultCode.equals(ResponseResultCode.STATUS_USER_NO_ENOUGH_MONEY)) {
			strIndex = R.string.result_content_no_enough_money;
		}else if (resultCode.equals(ResponseResultCode.STATUS_USER_ORDER_FAILD)) {
			strIndex = R.string.result_content_user_order_faild;
		}else if (resultCode.equals(ResponseResultCode.STATUS_USER_NO_ORDER_BOOK)) {
			strIndex = R.string.result_content_user_no_order_book;
		}else if (resultCode.equals(ResponseResultCode.STATUS_NO_AVAILABLE_BOOK)) {
			strIndex = R.string.result_content_user_no_available_book;
		}else if (resultCode.equals(ResponseResultCode.STATUS_NO_AVAILABLE_ASSORTMENT)) {
			strIndex = R.string.result_content_user_no_subclassification_book;
		}else if (resultCode.equals(ResponseResultCode.STATUS_NO_KIND_BOOK)) {
			strIndex = R.string.result_content_user_no_kind_book;
		}else if (resultCode.equals(ResponseResultCode.STATUS_NO_CHANNEL_BOOK)) {
			strIndex = R.string.result_content_user_no_channel_book;
		}else if (resultCode.equals(ResponseResultCode.STATUS_NO_SPECIAL_BOOK)) {
			strIndex = R.string.result_content_user_no_special_book;
		}else if (resultCode.equals(ResponseResultCode.STATUS_NO_RANK_BOOK)) {
			strIndex = R.string.result_content_user_no_rank_book;
		}else if (resultCode.equals(ResponseResultCode.STATUS_NO_FIND_REAL_KEY_BOOK)) {
			strIndex = R.string.result_content_user_unfind_real_key_book;
		}else if (resultCode.equals(ResponseResultCode.STATUS_USER_UNREGISTER)) {
			strIndex = R.string.result_content_user_disable;
		}else if (resultCode.equals(ResponseResultCode.STATUS_NO_FIND_BOOK_OFF_LINE)) {
			strIndex = R.string.result_find_book_offline;
		}
		if (strIndex == -1) {
			strIndex = R.string.tip_no_content;
		}
		return context.getString(strIndex);
	}

	
	public static final String getServerErrStr(Context context){
		return context.getString(R.string.tip_no_content);
	}
	
	/** 返回添加收藏夹上限提示
	 * @param context
	 * @return
	 */
	public static final String getAddFavoriteOverLimitTip(Context context){
//		return context.getString(R.string.result_code_add_favorite_over_limit);
		return null;
	}

}
