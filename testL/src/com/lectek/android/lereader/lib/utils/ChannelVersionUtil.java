package com.lectek.android.lereader.lib.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/** 渠道版本工具类
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2012-2-12
 */
public final class ChannelVersionUtil {
	
	private static final String TAG = ChannelVersionUtil.class.getSimpleName();
	
	/**
	 *  安卓市场	AZ001	TYYD_Android_AZ001_JAVA_2_9_5
		机锋市场	JF002	TYYD_Android_JF002_JAVA_2_9_5
		掌上应用汇	ZS003	TYYD_Android_ZS003_JAVA_2_9_5
		安智市场	AZ004	TYYD_Android_AZ004_JAVA_2_9_5
		N多市场	ND005	TYYD_Android_ND005_JAVA_2_9_5
		91手机助手	91006	TYYD_Android_91006_JAVA_2_9_5
		优亿Market	YY007	TYYD_Android_YY007_JAVA_2_9_5
		UC浏览器	UC020	TYYD_Android_UC020_JAVA_2_9_5
		翼起来大厅	YQ021	TYYD_Android_YQ021_JAVA_2_9_5
		Android Market	AM008	TYYD_Android_AM008_JAVA_2_9_5
		华为智汇云	HW009	TYYD_Android_HW009_JAVA_2_9_5
		酷派COOLMART	KP010	TYYD_Android_KP010_JAVA_2_9_5
		MOTO智件园	MT011	TYYD_Android_MT011_JAVA_2_9_5
		三星APPS	SX012	TYYD_Android_SX012_JAVA_2_9_5
		联想开发社区	LX013	TYYD_Android_LX013_JAVA_2_9_5
		其他应用商店	QT014	TYYD_Android_QT014_JAVA_2_9_5
		HTC商城	HT015	TYYD_Android_HT015_JAVA_2_9_5
		机锋网	JF003	TYYD_Android_JF003_JAVA_2_9_5
		DiyPDA	DIY004	TYYD_Android_DIY004_JAVA_2_9_5
		百度	BD019	TYYD_Android_BD019_JAVA_2_9_5
		天翼圈	TY016	TYYD_Android_TY016_JAVA_2_9_5
		WAP	WAP017	TYYD_Android_WAP017_JAVA_2_9_5
		WWW	WWW018	TYYD_Android_WWW018_JAVA_2_9_5
		威盛芯片		TYYD_Android_VIA_JAVA_2_9_5
	*/

	private static String CHANNEL_ID = "";
	
	/** 安智渠道标识 */
	private static final String CHANNLE_ID_ANZHI = "AZ004";
	/** 三星渠道标识 */
	private static final String CHANNLE_ID_SAMSUNG = "SX012";
	
	public static final boolean IS_CHANNEL = false;
	
	public static final void init(Context context){
		CHANNEL_ID = getChannelId(context);
		if(CHANNEL_ID == null){
			CHANNEL_ID = "";
		}
	}
	
	/** 获取渠道标识，如果不是渠道模式，返回空值（非NULL）
	 * @return
	 */
	public static final String getChannelID(){
		if(!IS_CHANNEL){
			return "";
		}
		return CHANNEL_ID;
	}
	
	public static final String getChannelId(Context context) {
		if(!IS_CHANNEL){
			return null;
		}
		String value = null;
		try {
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			value = appInfo.metaData.getString("CHANNEL_ID").trim();
		} catch (Exception e) {
			LogUtil.e(TAG, e);
		}
		return value;
	}
	
	/** 判断是否是安智的渠道
	 * @return
	 */
	public static final boolean isAnZhiChannel(){
		if(IS_CHANNEL && CHANNLE_ID_ANZHI.equals(CHANNEL_ID)){
			return true;
		}
		return false;
	}
	
	/** 判断是否是三星的渠道
	 * @return
	 */
	public static final boolean isSamsungChannel(){
		if(IS_CHANNEL && CHANNLE_ID_SAMSUNG.equals(CHANNEL_ID)){
			return true;
		}
		return false;
	}

}
