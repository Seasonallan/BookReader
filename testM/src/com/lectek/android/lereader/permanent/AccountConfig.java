package com.lectek.android.lereader.permanent;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.account.AccountType;

public class AccountConfig {
	
	public static final AccountType ACCOUNT_TYPE_VISITOR = AccountType.VISITOR;
	public static final AccountType ACCOUNT_TYPE_LEYUE = AccountType.LEYUE;
	public static final AccountType ACCOUNT_TYPE_SINA = AccountType.SINA;
	public static final AccountType ACCOUNT_TYPE_QQ = AccountType.QQ;
	public static final AccountType ACCOUNT_TYPE_TIANYI = AccountType.TIANYI;
	public static final AccountType ACCOUNT_TYPE_WEIXIN = AccountType.WEIXIN;
	
	public static final int RES_ID_STRING_SWITCH_ACCOUNT_SUCCESS = R.string.account_change_success; 
	public static final int RES_ID_STRING_SWITCH_ACCOUNT_FAIL = R.string.account_change_fail;
	public static final int RES_ID_STRING_REGISTER_FAIL = R.string.regist_faild_tip;
	public static final int RES_ID_STRING_REGISTER_SUCCESS = R.string.regist_success_tip;
}
