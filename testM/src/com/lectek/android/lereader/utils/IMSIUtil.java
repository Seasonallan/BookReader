package com.lectek.android.lereader.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * IMSI管理
 * 
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-6-20
 * 
 *       IMSI共有15位，其结构如下： MCC+MNC+MIN MCC：Mobile Country Code，移动国家码，共3位，中国为460;
 *       MNC:Mobile Network
 *       Code，移动网络码，共2位，联通CDMA系统使用03，一个典型的IMSI号码为460030912121001;
 *       MIN共有10位，其结构如下： 09+M0M1M2M3+ABCD
 *       其中的M0M1M2M3和MDN号码中的H0H1H2H3可存在对应关系，ABCD四位为自由分配。
 *       可以看出IMSI在MIN号码前加了MCC，可以区别出每个用户的来自的国家
 *       ，因此可以实现国际漫游。在同一个国家内，如果有多个CDMA运营商，可以通过MNC来进行区别.
 */
public final class IMSIUtil {

	private static final String IMSI_CHINA_MOBILE_1 = "46000";
	private static final String IMSI_CHINA_MOBILE_2 = "46002";
	private static final String IMSI_CHINA_MOBILE_3 = "46007";
	private static final String IMSI_CHINA_UNICOM = "46001";
	private static final String IMSI_CHINA_TELECOM = "46003";

	/** 运行商；未知 */
	public static final byte MOBILE_TYPE_UNKNOW = 0;
	/** 运行商；中国移动 */
	public static final byte MOBILE_TYPE_CHINA_MOBILE = 1;
	/** 运行商；中国联通 */
	public static final byte MOBILE_TYPE_CHINA_UNICOM = 2;
	/** 运行商；中国电信 */
	public static final byte MOBILE_TYPE_CHINA_TELECOM = 3;

	/**
	 * 通过IMSI判断运营商
	 * 
	 * @param context
	 * @return
	 */
	public static byte getMobileType(Context context) {
		String imsi = getIMSI(context);
		return getMobileType(context, imsi);
	}

	/**
	 * 通过IMSI判断运营商
	 * 
	 * @param context
	 * @param imsi
	 * @return
	 */
	public static byte getMobileType(Context context, String imsi) {
		byte type = MOBILE_TYPE_UNKNOW;
		if (TextUtils.isEmpty(imsi)) {
			return type;
		}
		if (imsi.startsWith(IMSI_CHINA_MOBILE_1)
				|| imsi.startsWith(IMSI_CHINA_MOBILE_2)
				|| imsi.startsWith(IMSI_CHINA_MOBILE_3)
				) {
			// 因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
			type = MOBILE_TYPE_CHINA_MOBILE;
		} else if (imsi.startsWith(IMSI_CHINA_UNICOM)) {
			type = MOBILE_TYPE_CHINA_UNICOM;
		} else if (imsi.startsWith(IMSI_CHINA_TELECOM)) {
			type = MOBILE_TYPE_CHINA_TELECOM;
		}
		return type;
	}

	/**
	 * 获取IMSI
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMSI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = null;
		imsi = tm.getSubscriberId();
//		if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {	//判断卡是否处于激活状态
//			imsi = tm.getSubscriberId();
//			if (imsi != null && !imsi.startsWith(tm.getSimOperator())) {	//判断所读IMSI是否与当前SIM卡信息吻合
//				return null;
//			}
//		}
		
//		LogUtil.i("获取设备编号: ", tm.getDeviceId());
//		LogUtil.i("获取SIM卡国别: ", tm.getSimCountryIso());
//		LogUtil.i("获取SIM卡序列号: ", tm.getSimSerialNumber());
//		LogUtil.i("getSubscriberId: ", tm.getSubscriberId());
//		LogUtil.i("获取SIM卡状态: ","" + tm.getSimState());
//		LogUtil.i("获取软件版本: ", tm.getDeviceSoftwareVersion());
//		LogUtil.i("获取网络运营商代号: ", tm.getNetworkOperator());
//		LogUtil.i("获取网络运营商名称: ", tm.getNetworkOperatorName());
//		LogUtil.i("获取手机制式: ", "" + tm.getPhoneType());
		
		return imsi;
	}

}
