/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package com.lectek.android.lereader.lib.recharge.alipay;

public final class AlixId
{
	public static final int BASE_ID 			= 0;
	public static final int RQF_PAY 			= BASE_ID + 1;
	public static final int RQF_INSTALL_CHECK = RQF_PAY + 1;
	public static final int RQF_DOWNLOAD_FAILED = RQF_PAY + 2;
	public static final int RQF_INSTALL_WITHOUT_DOWNLOAD = RQF_PAY + 3;
}
