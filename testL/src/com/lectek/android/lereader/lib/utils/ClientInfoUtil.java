package com.lectek.android.lereader.lib.utils;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;


/**
 * 一些客户端信息
 * 
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2010-10-22
 */
public final class ClientInfoUtil {

	public static String CLIENT_VERSION;

	// 通用版本号
	public static final String CLIENT_VERSION_COMMON = "TYYD_Android_JAVA_2_9_9";// 2011.06.27
	// public static final String CLIENT_VERSION_COMMON =
	// "TYYD_Android_AZ001_JAVA_2_0_0";//安卓市场 2011.07.25
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_AZ001_JAVA_2_9_9";//安卓市场 2011.08.26
	// public static final String CLIENT_VERSION_COMMON =
	// "TYYD_Android_JF002_JAVA_2_0_0";//机锋市场 2011.07.25
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_JF002_JAVA_2_9_9";//机锋市场 2011.08.26
	// public static final String CLIENT_VERSION_COMMON =
	// "TYYD_Android_ZS003_JAVA_2_0_0";//掌上应用汇 2011.07.25
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_ZS003_JAVA_2_9_9";//掌上应用汇 2011.08.26
	// public static final String CLIENT_VERSION_COMMON =
	// "TYYD_Android_AZ004_JAVA_2_0_0";//安智市场 2011.07.25
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_AZ004_JAVA_2_9_9";//安智市场 2011.08.26
	// public static final String CLIENT_VERSION_COMMON =
	// "TYYD_Android_ND005_JAVA_2_0_0";//N多市场 2011.07.25
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_ND005_JAVA_2_9_9";//N多市场 2011.08.26
	// public static final String CLIENT_VERSION_COMMON =
	// "TYYD_Android_91006_JAVA_2_0_0";//91手机助手 2011.07.25
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_91006_JAVA_2_9_9";//91手机助手 2011.08.26
	// public static final String CLIENT_VERSION_COMMON =
	// "TYYD_Android_YY007_JAVA_2_0_0";//优亿Market 2011.07.25
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_YY007_JAVA_2_9_9";//优亿Market 2011.08.26
	// public static final String CLIENT_VERSION_COMMON =
	// "TYYD_Android_AM008_JAVA_2_0_0";//Android Market 2011.07.25
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_AM008_JAVA_2_9_9";//Android Market 2011.08.26
	// public static final String CLIENT_VERSION_COMMON =
	// "TYYD_Android_HW009_JAVA_2_0_0";//华为智汇云 2011.07.25
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_HW009_JAVA_2_9_9";//华为智汇云 2011.08.26
	// public static final String CLIENT_VERSION_COMMON =
	// "TYYD_Android_KP010_JAVA_2_0_0";//酷派COOLMART 2011.07.25
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_KP010_JAVA_2_9_9";//酷派COOLMART 2011.08.26
	// public static final String CLIENT_VERSION_COMMON =
	// "TYYD_Android_MT011_JAVA_2_0_0";//MOTO智件园 2011.07.25
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_MT011_JAVA_2_9_9";//MOTO智件园 2011.08.26
	// public static final String CLIENT_VERSION_COMMON =
	// "TYYD_Android_SX012_JAVA_2_0_0";//三星APPS 2011.07.25
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_SX012_JAVA_2_9_9";//三星APPS 2011.08.26
	// public static final String CLIENT_VERSION_COMMON =
	// "TYYD_Android_LX013_JAVA_2_0_0";//联想开发社区 2011.07.28
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_LX013_JAVA_2_9_9";//联想开发社区 2011.08.26
	// public static final String CLIENT_VERSION_COMMON =
	// "TYYD_Android_QT014_JAVA_2_0_0";//其他应用商店 2011.07.28
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_QT014_JAVA_2_9_9";//其他应用商店 2011.08.26
	// public static final String CLIENT_VERSION_COMMON =
	// "TYYD_Android_HT015_JAVA_2_0_0";//HTC商城 2011.08.02
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_HT015_JAVA_2_9_9";//HTC商城 2011.08.26
	// public static final String CLIENT_VERSION_COMMON =
	// "TYYD_Android_TY016_JAVA_2_0_0";//天翼圈 2011.08.03
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_TY016_JAVA_2_9_9";//天翼圈 2011.08.26
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_WAP017_JAVA_2_9_9";//自有门户 2011.09.16
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_WWW018_JAVA_2_9_9";//自有门户 2011.09.16
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_BD019_JAVA_2_9_9";//百度 2011.10.14
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_UC020_JAVA_2_9_9";//UC 2011.11.18
	// public static final String CLIENT_VERSION_COMMON =
	// "TYYD_Android_JF003_JAVA_2_0_0";//机锋网 2011.07.25
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_JF003_JAVA_2_9_9";//机锋网 2011.08.26
	// public static final String CLIENT_VERSION_COMMON =
	// "TYYD_Android_DIY004_JAVA_2_0_0";//DiyPDA 2011.07.25
//	 public static final String CLIENT_VERSION_COMMON = "TYYD_Android_DIY004_JAVA_2_9_9";//DiyPDA 2011.08.26
	//威盛芯片
//	public static final String CLIENT_VERSION_COMMON = "TYYD_Android_VIA_JAVA_2_9_9";// 2012.05.29
	// public static final String CLIENT_VERSION = "wm5.0-06000800-1.0.0.jar";
	// 实验室用的
	// public static final String CLIENT_VERSION =
	// "TYYD_Android_2_0_480_854_JAVA_1_2";
	// for XT800
	// public static final String CLIENT_VERSION_XT800 =
	// "TYYD_Android_2_0_480_854_XT800_JAVA_1_1_2";
	// public static final String CLIENT_VERSION_XT800 =
	// "TYYD_Android_2_0_480_854_XT800_JAVA_1_1_3";
	// public static final String CLIENT_VERSION_XT800 =
	// "TYYD_Android_2_0_480_854_XT800_JAVA_1_2_0";//20110104
	// public static final String CLIENT_VERSION_XT800 =
	// "TYYD_Android_2_0_480_854_XT800_JAVA_1_3_0";//2011.01.21
	// public static final String CLIENT_VERSION_XT800 =
	// "TYYD_Android_2_0_480_854_MOT_XT800_JAVA_2_0_0";//2011.06.27
	public static final String CLIENT_VERSION_XT800 = "TYYD_Android_2_0_480_854_MOT_XT800_JAVA_2_9_9";// 2011.08.02
	// 贺岁版测试平台上的版本
	// public static final String CLIENT_VERSION_XT800 =
	// "TYYD_Android_2_0_480_854_XT800_JAVA_1_1";
	// for XT806
	// public static final String CLIENT_VERSION_XT806 =
	// "TYYD_Android_2_1_480_854_XT806_JAVA_2_9_9";
	// public static final String CLIENT_VERSION_XT806 =
	// "TYYD_Android_2_1_480_854_XT806_JAVA_1_0_1";//2010/12/24
	// public static final String CLIENT_VERSION_XT806 =
	// "TYYD_Android_2_1_480_854_XT806_JAVA_1_1_0";//2011/01/04
	// public static final String CLIENT_VERSION_XT806 =
	// "TYYD_Android_2_1_480_854_MOT_XT806_JAVA_2_0_0";//2011/07/12
	public static final String CLIENT_VERSION_XT806 = "TYYD_Android_2_1_480_854_MOT_XT806_JAVA_2_9_9";// 2011/08/02
	// for XT800+
	// public static final String CLIENT_VERSION_XT800PLUS =
	// "TYYD_Android_2_2_480_854_XT800p_JAVA_2_9_9";
	// public static final String CLIENT_VERSION_XT800PLUS =
	// "TYYD_Android_2_2_480_854_MOT_XT800p_JAVA_2_0_0"; //2011/07/07
	public static final String CLIENT_VERSION_XT800PLUS = "TYYD_Android_2_2_480_854_MOT_XT800p_JAVA_2_9_9"; // 2011/08/02
	// for ME811
	// public static final String CLIENT_VERSION_ME811 =
	// "TYYD_Android_2_2_480_854_ME811_JAVA_2_9_9";
	// public static final String CLIENT_VERSION_ME811 =
	// "TYYD_Android_2_2_480_854_ME811_JAVA_1_0_1";//2010/12/24
	// public static final String CLIENT_VERSION_ME811 =
	// "TYYD_Android_2_2_480_854_ME811_JAVA_1_1_0";//2011/01/04
	// public static final String CLIENT_VERSION_ME811 =
	// "TYYD_Android_2_2_480_854_ME811_JAVA_1_1_2";//2011/01/26
	// public static final String CLIENT_VERSION_ME811 =
	// "TYYD_Android_2_2_480_854_ME811_JAVA_1_3_0";//2011/01/28
	// public static final String CLIENT_VERSION_ME811 =
	// "TYYD_Android_2_2_480_854_MOT_ME811_JAVA_2_0_0";//2011/06/27
	public static final String CLIENT_VERSION_ME811 = "TYYD_Android_2_2_480_854_MOT_ME811_JAVA_2_9_9";// 2011/08/02
	//华为C8860E
	public static final String CLIENT_VERSION_HUAWEI_C8860E = "TYYD_Android_2_3_480_854_HW_C8860E_JAVA_2_9_9";// 2011.11.25
	//小米MI1
	public static final String CLIENT_VERSION_XIAOMI_C1 = "TYYD_Android_2_3_480_854_XIAOMI_C1_JAVA_2_9_9";// 2012.02.14
	//联想S850E
	public static final String CLIENT_VERSION_LNV_S850E = "TYYD_Android_2_3_480_728_LNV_S850E_JAVA_2_9_9";// 2012.02.29
	// 摩托XT681
	public static final String CLIENT_VERSION_MOT_XT681 = "TYYD_Android_2_3_480_854_MOT_XT681_JAVA_2_9_9";//2011.12.30
	// 优思  US910
	public static final String CLIENT_VERSION_XD_US910 = "TYYD_Android_2_3_480_854_XD_US910_JAVA_2_9_9";//2012.06.14
	//优思US920
	public static final String CLIENT_VERSION_XD_US920 = "TYYD_Android_2_3_480_854_XD_US920_JAVA_2_9_9";// 2012.07.04
	//联想A765E
	public static final String CLIENT_VERSION_LNV_A765E = "TYYD_Android_4_0_480_854_LNV_A765E_JAVA_2_9_9";// 2012.09.14
	//蓝天 S910D
	public static final String CLIENT_VERSION_TLT_S910D = "TYYD_Android_2_3_480_854_TLT_S910D_JAVA_2_9_9";// 2012.11.07
	// 新邮通信 E606
	public static final String CLIENT_VERSION_XYT_E606 = "TYYD_Android_4_0_480_854_XYT_E606_JAVA_2_9_9";// 2012.12.10
	// 博瑞世纪 S3
	public static final String CLIENT_VERSION_BR_S3 = "TYYD_Android_4_0_480_854_BR_S3_JAVA_2_9_9";// 2012.12.10
	// 锋达通 E9
	public static final String CLIENT_VERSION_FDT_E9 = "TYYD_Android_2_3_480_854_FDT_E9_JAVA_2_9_9";// 2012.12.10

	// for lephone 1.6
	// public static final String CLIENT_VERSION_LEPHONE_4 =
	// "TYYD_Android_2_1_480_800_3GC101_JAVA_1_2_1";//2011.01.26
	// public static final String CLIENT_VERSION_LEPHONE_4 =
	// "TYYD_Android_2_1_480_800_3GC101_JAVA_1_3_0";//2011.04.12
	// public static final String CLIENT_VERSION_LEPHONE_4 =
	// "TYYD_Android_2_1_480_800_LNV_3GC101_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_LEPHONE_4 = "TYYD_Android_2_1_480_800_LNV_3GC101_JAVA_2_9_9";// 2011.08.02
	// for lephone 2.2
	// public static final String CLIENT_VERSION_LEPHONE =
	// "TYYD_Android_2_2_480_800_3GC101_JAVA_1_1_0";
	public static final String CLIENT_VERSION_LEPHONE_7 = "TYYD_Android_2_2_480_800_3GC101_JAVA_1_3_0";// 2011.04.12
	// //三星I909 2.2
	// // public static final String CLIENT_VERSION_I909 =
	// "TYYD_Android_2_2_480_800_I909_JAVA_1_1_0";
	// public static final String CLIENT_VERSION_I909_8 =
	// "TYYD_Android_2_2_480_800_I909_JAVA_1_1_1";//2010/12/24
	// SAMSUNG I909 2.1
	// public static final String CLIENT_VERSION_I909_7 =
	// "TYYD_Android_2_1_480_800_I909_JAVA_1_2_0";//2011/01/04
	// public static final String CLIENT_VERSION_I909_7 =
	// "TYYD_Android_2_1_480_800_I909_JAVA_1_3_0";//2011/01/21
	// public static final String CLIENT_VERSION_I909_7 =
	// "TYYD_Android_2_1_480_800_SCH_I909_JAVA_2_0_0";//2011/06/27
	public static final String CLIENT_VERSION_I909_7 = "TYYD_Android_2_1_480_800_SCH_I909_JAVA_2_9_9";// 2011/08/02
	// 三星W899
	// public static final String CLIENT_VERSION_W899 =
	// "TYYD_Android_2_1_480_800_W899_JAVA_1_1_0";
	// public static final String CLIENT_VERSION_W899 =
	// "TYYD_Android_2_1_480_800_W899_JAVA_1_1_1";
	// public static final String CLIENT_VERSION_W899 =
	// "TYYD_Android_2_1_480_800_W899_JAVA_1_2_0";//2011.01.05
	// public static final String CLIENT_VERSION_W899 =
	// "TYYD_Android_2_1_480_800_W899_JAVA_1_2_1";//2011.01.26
	// public static final String CLIENT_VERSION_W899 =
	// "TYYD_Android_2_1_480_800_SCH_W899_JAVA_2_0_0";//2011.06.27
	public static final String CLIENT_VERSION_W899 = "TYYD_Android_2_1_480_800_SCH_W899_JAVA_2_9_9";// 2011.08.02
	// 酷派N930
	// public static final String CLIENT_VERSION_COOLPAD_N930 =
	// "TYYD_Android_2_1_480_800_N930_JAVA_1_3_0";//2011.04.11
	// public static final String CLIENT_VERSION_COOLPAD_N930 =
	// "TYYD_Android_2_1_480_800_YL_N930_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_COOLPAD_N930 = "TYYD_Android_2_1_480_800_YL_N930_JAVA_2_9_9";// 2011.08.02
	// 易明MID E6
	// public static final String CLIENT_VERSION_E6 =
	// "TYYD_Android_2_1_480_800_E6_JAVA_2_9_9";
	// public static final String CLIENT_VERSION_E6 =
	// "TYYD_Android_2_1_480_800_E6_JAVA_1_1_0";//2011.01.06
	// public static final String CLIENT_VERSION_E6 =
	// "TYYD_Android_2_1_480_800_E6_JAVA_1_1_1";//2011.01.26
	// public static final String CLIENT_VERSION_E6 =
	// "TYYD_Android_2_1_480_800_IFREE_E6_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_E6 = "TYYD_Android_2_1_480_800_IFREE_E6_JAVA_2_9_9";// 2011.08.02
	// for HTC VIVO
	// public static final String CLIENT_VERSION_HTC_VIVO =
	// "TYYD_Android_2_2_480_800_VIVO_JAVA_2_9_9";//20101224
	// public static final String CLIENT_VERSION_HTC_VIVO =
	// "TYYD_Android_2_2_480_800_HTC_VIVO_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_HTC_VIVO = "TYYD_Android_2_2_480_800_HTC_VIVO_JAVA_2_9_9";// 2011.08.02
	// for HTC EVO
	// public static final String CLIENT_VERSION_HTC_EVO =
	// "TYYD_Android_2_1_480_800_EVO4G_JAVA_2_9_9";//20101229
	// public static final String CLIENT_VERSION_HTC_EVO =
	// "TYYD_Android_2_1_480_800_HTC_EVO4G_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_HTC_EVO = "TYYD_Android_2_1_480_800_HTC_EVO4G_JAVA_2_9_9";// 2011.08.02
	// for HTC INCREDIBLE
	// public static final String CLIENT_VERSION_HTC_INCREDIBLE =
	// "TYYD_Android_2_2_480_800_INCREDIBLE_JAVA_2_9_9";//2011.02.15
	// public static final String CLIENT_VERSION_HTC_INCREDIBLE =
	// "TYYD_Android_2_2_480_800_HTC_INCREDIBLE_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_HTC_INCREDIBLE = "TYYD_Android_2_2_480_800_HTC_INCREDIBLE_JAVA_2_9_9";// 2011.08.02
	// for ZTE V9E
	// public static final String CLIENT_VERSION_ZTE_V9E =
	// "TYYD_Android_2_1_480_800_V9E_JAVA_2_9_9";//2011.02.16
	// public static final String CLIENT_VERSION_ZTE_V9E =
	// "TYYD_Android_2_1_480_800_ZTE_V9E_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_ZTE_V9E = "TYYD_Android_2_1_480_800_ZTE_V9E_JAVA_2_9_9";// 2011.08.02
	// for HUAWEI C8800
	// public static final String CLIENT_VERSION_HUAWEI_C8800 =
	// "TYYD_Android_2_3_480_800_C8800_JAVA_2_9_9";//2011.02.25
	// public static final String CLIENT_VERSION_HUAWEI_C8800 =
	// "TYYD_Android_2_3_480_800_HW_C8800_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_HUAWEI_C8800 = "TYYD_Android_2_3_480_800_HW_C8800_JAVA_2_9_9";// 2011.08.02
	// for TIANYU E800
	// public static final String CLIENT_VERSION_TIANYU_E800 =
	// "TYYD_Android_2_2_480_800_TYE800_JAVA_2_9_9";//2011.03.07
	// public static final String CLIENT_VERSION_TIANYU_E800 =
	// "TYYD_Android_2_2_480_800_TY_E800_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_TIANYU_E800 = "TYYD_Android_2_2_480_800_TY_E800_JAVA_2_9_9";// 2011.08.02
	// for AIGO N700
	// public static final String CLIENT_VERSION_AIGO_N700 =
	// "TYYD_Android_2_1_480_800_N700_JAVA_2_9_9";//2011.03.07
	// public static final String CLIENT_VERSION_AIGO_N700 =
	// "TYYD_Android_2_1_480_800_AIG_N700_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_AIGO_N700 = "TYYD_Android_2_1_480_800_AIG_N700_JAVA_2_9_9";// 2011.08.02
	// for ZTE N880
	// public static final String CLIENT_VERSION_ZTE_N880 =
	// "TYYD_Android_2_2_480_800_N880_JAVA_2_9_9";//2011.03.25
	// public static final String CLIENT_VERSION_ZTE_N880 =
	// "TYYD_Android_2_2_480_800_ZTE_N880_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_ZTE_N880 = "TYYD_Android_2_2_480_800_ZTE_N880_JAVA_2_9_9";// 2011.08.02
	// for COOLPAD 9930
	// public static final String CLIENT_VERSION_COOLPAD_9930 =
	// "TYYD_Android_2_2_480_960_9930_JAVA_2_9_9";//2011.03.28
	// public static final String CLIENT_VERSION_COOLPAD_9930 =
	// "TYYD_Android_2_2_480_960_YL_9930_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_COOLPAD_9930 = "TYYD_Android_2_2_480_960_YL_9930_JAVA_2_9_9";// 2011.08.02
	// for 三星I919
	// public static final String CLIENT_VERSION_SAMSUNG_I919 =
	// "TYYD_Android_2_3_480_800_I919_JAVA_2_9_9";//2011.06.08
	// public static final String CLIENT_VERSION_SAMSUNG_I919 =
	// "TYYD_Android_2_3_480_800_SCH_I919_JAVA_2_0_0";//2011.07.25
	public static final String CLIENT_VERSION_SAMSUNG_I919 = "TYYD_Android_2_3_480_800_SCH_I919_JAVA_2_9_9";// 2011.08.02
	// for 35 Q68
	// public static final String CLIENT_VERSION_35_Q68 =
	// "TYYD_Android_2_2_480_800_SW_Q68_JAVA_2_0_0";//2011.07.19
	public static final String CLIENT_VERSION_35_Q68 = "TYYD_Android_2_2_480_800_SW_Q68_JAVA_2_9_9";// 2011.08.02
	// for 酷派N916
	// public static final String CLIENT_VERSION_COOLPAD_N916 =
	// "TYYD_Android_2_2_480_800_N916_JAVA_2_9_9";//2011.07.11
	public static final String CLIENT_VERSION_COOLPAD_N916 = "TYYD_Android_2_2_480_800_YL_N916_JAVA_2_9_9";// 2011.08.02
	// for 三星W999
	// public static final String CLIENT_VERSION_SAMSUNG_W999 =
	// "TYYD_Android_2_3_480_800_W999_JAVA_2_9_9";//2011.07.15
	public static final String CLIENT_VERSION_SAMSUNG_W999 = "TYYD_Android_2_3_480_800_SCH_W999_JAVA_2_9_9";// 2011.09.19
	// for 首亿 E9
	public static final String CLIENT_VERSION_SOAYE_E9 = "TYYD_Android_2_2_480_800_SSY_E9_JAVA_2_9_9";// 2011.08.02
	// for HTC Z510d
	public static final String CLIENT_VERSION_HTC_Z510D = "TYYD_Android_2_3_480_800_HTC_Z510D_JAVA_2_9_9";// 2011.08.02
	// for ZTE N880S
	public static final String CLIENT_VERSION_ZTE_N880S = "TYYD_Android_2_2_480_800_ZTE_N880S_JAVA_2_9_9";// 2011.08.03
	// for 酷派9100
	public static final String CLIENT_VERSION_COOLPAD_9100 = "TYYD_Android_2_2_480_800_YL_9100_JAVA_2_9_9";// 2011.08.09
	// for HUAWEI S8600
	public static final String CLIENT_VERSION_HUAWEI_S8600 = "TYYD_Android_2_3_480_800_HW_S8600_JAVA_2_9_9";// 2011.08.09
	// for 海信 ET919
	public static final String CLIENT_VERSION_HS_ET919 = "TYYD_Android_2_3_480_800_HS_ET919_JAVA_2_9_9";// 2011.08.18
	// for 铂酷 PM700
	public static final String CLIENT_VERSION_BOK_PM700 = "TYYD_Android_2_3_480_800_BOK_PM700_JAVA_2_9_9";// 2011.08.22
	// for 三星I929
	// public static final String CLIENT_VERSION_SAMSUNG_I929 =
	// "TYYD_Android_2_3_480_800_SCH_I929_JAVA_1_3_0";//2011.08.23
	public static final String CLIENT_VERSION_SAMSUNG_I929 = "TYYD_Android_2_3_480_800_SCH_I929_JAVA_2_9_9";// 2011.09.19
	// for 京瓷M9300
	public static final String CLIENT_VERSION_KYOCERA_M9300 = "TYYD_Android_2_2_480_800_KZ_M9300_JAVA_2_9_9";// 2011.08.31
	// for 京瓷KSP8000
 	public static final String CLIENT_VERSION_KYOCERA_KSP8000 = "TYYD_Android_2_3_480_800_KZ_KSP8000_JAVA_2_9_9";// 2011.11.30
	// for 酷派9900
	public static final String CLIENT_VERSION_COOLPAD_9900 = "TYYD_Android_2_3_480_960_YL_9900_JAVA_2_9_9";// 2011.08.31
	// for 35 U705
	public static final String CLIENT_VERSION_35_U705 = "TYYD_Android_2_2_800_480_SW_U705_JAVA_2_9_9";// 2011.09.08
	// for 夏普SH7218T
	public static final String CLIENT_VERSION_SHARP_SH7218T = "TYYD_Android_2_3_480_800_SHP_SH7218T_JAVA_2_9_9";// 2011.09.15
	// for 华为C8810
	public static final String CLIENT_VERSION_HUAWEI_C8810 = "TYYD_Android_2_3_480_800_HW_C8810_JAVA_2_9_9";// 2011.09.19
	// for 酷派5860
	public static final String CLIENT_VERSION_COOLPAD_5860 = "TYYD_Android_2_2_480_800_YL_5860_JAVA_2_9_9";// 2011.09.26
	// for 华为C8850
	public static final String CLIENT_VERSION_HUAWEI_C8850 = "TYYD_Android_2_3_480_800_HW_C8850_JAVA_2_9_9";// 2011.10.08
	// for 大唐DeTron-1
	public static final String CLIENT_VERSION_DETRON_1 = "TYYD_Android_2_2_480_800_DT_DETRON1_JAVA_2_9_9";// 2011.10.24
	// for 华宇能AF1000
	public static final String CLIENT_VERSION_HYN_AF1000 = "TYYD_Android_2_2_480_800_HYN_AF1000_JAVA_2_9_9";// 2011.11.09
	// for 海信E910
	public static final String CLIENT_VERSION_HS_E910 = "TYYD_Android_2_3_480_800_HS_E910_JAVA_2_9_9";// 2011.11.14
	// for 天元-Q9
	public static final String CLIENT_VERSION_TE_TE800 = "TYYD_Android_2_3_480_800_YCT_TE800_JAVA_2_9_9";// 2011.12.20
	//易丰A5
	public static final String CLIENT_VERSION_EPHONE_A5 = "TYYD_Android_2_3_480_800_YFZ_A5_JAVA_2_9_9";// 2011.11.25
	//波导N760
	public static final String CLIENT_VERSION_BRID_N760 = "TYYD_Android_2_2_480_800_BD_N760_JAVA_2_9_9";// 2011.12.02
	//波导N760
	public static final String CLIENT_VERSION_XYT_D656 = "TYYD_Android_2_3_480_800_XYT_D656_JAVA_2_9_9";// 2011.12.06
	// for 联想A790E
	public static final String CLIENT_VERSION_LENOVO_A790E = "TYYD_Android_2_3_480_800_LNV_A790E_JAVA_2_9_9";// 2011.12.09
	// for 海信EG900
	public static final String CLIENT_VERSION_HS_EG900 = "TYYD_Android_2_3_480_800_HS_EG900_JAVA_2_9_9";// 2011.12.22
	// for 长虹C100
	public static final String CLIENT_VERSION_CH_C100 = "TYYD_Android_2_3_480_800_CH_C100_JAVA_2_9_9";// 2011.12.26
	// for 同威S8210
	public static final String CLIENT_VERSION_TW_S8210 = "TYYD_Android_2_3_480_800_TW_S8210_JAVA_2_9_9";// 2011.12.26
	// for TCL-C995
	public static final String CLIENT_VERSION_TCL_C995 = "TYYD_Android_2_3_480_800_TCL_C995_JAVA_2_9_9"; //2011.12.30
	// for 酷诺E188
	public static final String CLIENT_VERSION_KUNUO_E188 = "TYYD_Android_2_3_480_800_KUNUO_E188_JAVA_2_9_9"; //2012.01.31
	// for 海尔N86E
	public static final String CLIENT_VERSION_HC_N86E = "TYYD_Android_2_3_480_800_HC_N86E_JAVA_2_9_9"; //2012.01.31
	// for ZTE N880E
	public static final String CLIENT_VERSION_ZTE_N880E = "TYYD_Android_2_3_480_800_ZTE_N880E_JAVA_2_9_9"; //2012.01.31
	// for 华录S9000
	public static final String CLIENT_VERSION_CHL_S9000 = "TYYD_Android_2_2_480_800_CHL_S9000_JAVA_2_9_9"; //2012.02.13
	// for 华录S9100
	public static final String CLIENT_VERSION_CHL_S9100 = "TYYD_Android_2_3_480_800_CHL_S9100_JAVA_2_9_9"; //2012.02.13
	// for 三星I779
	public static final String CLIENT_VERSION_SCH_I779 = "TYYD_Android_2_3_480_800_SCH_I779_JAVA_2_9_9"; //2012.02.14
	// for 大唐S22
	public static final String CLIENT_VERSION_DT_S22 = "TYYD_Android_2_3_480_800_DT_S22_JAVA_2_9_9"; //2012.02.14
	// for 桑菲D633
	public static final String CLIENT_VERSION_SAF_D633 = "TYYD_Android_2_3_480_800_SAF_D633_JAVA_2_9_9"; //2012.02.21
	// for 优派Q8
	public static final String CLIENT_VERSION_YHY_Q8 = "TYYD_Android_2_3_480_800_YHY_Q8_JAVA_2_9_9"; //2012.03.08
	// for 海尔E760
	public static final String CLIENT_VERSION_HC_E760 = "TYYD_Android_2_3_480_800_HC_E760_JAVA_2_9_9"; //2012.03.15
	// for 酷派5860+
	public static final String CLIENT_VERSION_YL_5860A = "TYYD_Android_2_3_480_800_YL_5860A_JAVA_2_9_9"; //2012.03.16
	// for 华为C8812
	public static final String CLIENT_VERSION_HW_C8812 = "TYYD_Android_4_0_480_800_HW_C8812_JAVA_2_9_9"; //2012.03.29
	// for 语信E96
	public static final String CLIENT_VERSION_YX_E96 = "TYYD_Android_2_3_480_800_YX_E96_JAVA_2_9_9"; //2012.03.29
	// for 海信E920
	public static final String CLIENT_VERSION_HS_E920 = "TYYD_Android_2_3_480_800_HS_E920_JAVA_2_9_9"; //2012.04.05
	// for 三星I719
	public static final String CLIENT_VERSION_SCH_I719 = "TYYD_Android_2_3_480_800_SCH_I719_JAVA_2_9_9"; //2012.04.06
	// for 中兴N882E
	public static final String CLIENT_VERSION_ZTE_N882E = "TYYD_Android_4_0_480_800_ZTE_N882E_JAVA_2_9_9"; //2012.04.09
	// for 酷派——5880
	public static final String CLIENT_VERSION_YL_5880 = "TYYD_Android_2_3_480_800_YL_5880_JAVA_2_9_9"; //2012.04.26
	// for 海信EG906
	public static final String CLIENT_VERSION_HS_EG906 = "TYYD_Android_2_3_480_800_HS_EG906_JAVA_2_9_9"; //2012.04.26
	// for 美翼E999
	public static final String CLIENT_VERSION_MYJ_E999 = "TYYD_Android_2_3_480_800_MYJ_E999_JAVA_2_9_9"; //2012.04.26
	// for 葳朗 VE600
	public static final String CLIENT_VERSION_BLW_VE600 = "TYYD_Android_2_3_480_800_BLW_VE600_JAVA_2_9_9"; //2012.04.26
	// for 中兴N881D
	public static final String CLIENT_VERSION_ZTE_N881D = "TYYD_Android_4_0_480_800_ZTE_N881D_JAVA_2_9_9"; //2012.04.26
	// for 和信N719
	public static final String CLIENT_VERSION_HX_N719 = "TYYD_Android_2_3_480_800_HX_N719_JAVA_2_9_9"; //2012.05.07
	// for 和信N819
	public static final String CLIENT_VERSION_HX_N819 = "TYYD_Android_2_3_480_800_HX_N819_JAVA_2_9_9"; //2012.05.07
	// for 酷派5866
	public static final String CLIENT_VERSION_YL_5866 = "TYYD_Android_4_0_480_800_YL_5866_JAVA_2_9_9"; //2012.05.07
	// for 酷派5870
	public static final String CLIENT_VERSION_YL_5870 = "TYYD_Android_4_0_480_800_YL_5870_JAVA_2_9_9"; //2012.05.07
	// for 联想A710e
	public static final String CLIENT_VERSION_LNV_A710E = "TYYD_Android_4_0_480_800_LNV_A710E_JAVA_2_9_9"; //2012.05.07
	// for 创维SKY-E8
	public static final String CLIENT_VERSION_CW_SKYE8 = "TYYD_Android_2_3_480_800_CW_SKYE8_JAVA_2_9_9"; //2012.05.17
	// for 联想A700e
	public static final String CLIENT_VERSION_LNV_A700E = "TYYD_Android_4_0_480_800_LNV_A700E_JAVA_2_9_9"; //2012.05.22
	// for 天迈D09S
	public static final String CLIENT_VERSION_TM_D09S = "TYYD_Android_2_3_480_800_TM_D09S_JAVA_2_9_9"; //2012.05.23
	// for 蓝天 S980D
	public static final String CLIENT_VERSION_TLT_S980D = "TYYD_Android_2_3_480_800_TLT_S980D_JAVA_2_9_9"; //2012.05.28
	// for 易丰4S
	public static final String CLIENT_VERSION_YFZ_4S = "TYYD_Android_2_3_480_800_YFZ_4S_JAVA_2_9_9"; //2012.05.30
	// for 华为C8825D
	public static final String CLIENT_VERSION_HW_C8825D = "TYYD_Android_4_0_480_800_HW_C8825D_JAVA_2_9_9"; //2012.06.04
	// for 优思  US900
	public static final String CLIENT_VERSION_XD_US900 = "TYYD_Android_2_3_480_800_XD_US900_JAVA_2_9_9"; //2012.06.18
	// for 展翼N9
	public static final String CLIENT_VERSION_ZY_N9 = "TYYD_Android_2_3_480_800_ZY_N9_JAVA_2_9_9"; //2012.06.26
	// for 广信 EF930
	public static final String CLIENT_VERSION_GX_EF930 = "TYYD_Android_2_3_480_800_GX_EF930_JAVA_2_9_9"; //2012.07.02
	// for 中兴N880F
	public static final String CLIENT_VERSION_ZTE_N880F = "TYYD_Android_4_0_480_800_ZTE_N880F_JAVA_2_9_9"; //2012.07.02
	// for TCL-D662
	public static final String CLIENT_VERSION_TCL_D662 = "TYYD_Android_2_3_480_800_TCL_D662_JAVA_2_9_9"; //2012.07.02
	// for 酷派5216
	public static final String CLIENT_VERSION_YL_5216 = "TYYD_Android_2_3_480_800_YL_5216_JAVA_2_9_9"; //2012.07.05
	// for 金立C700
	public static final String CLIENT_VERSION_GIO_C700 = "TYYD_Android_4_0_480_800_GIO_C700_JAVA_2_9_9"; //2012.07.09
	// for 三星W2013
	public static final String CLIENT_VERSION_SCH_W889 = "TYYD_Android_4_0_480_800_SCH_W889_JAVA_2_9_9"; //2012.07.09
	// for 酷派5910
	public static final String CLIENT_VERSION_YL_5910 = "TYYD_Android_4_0_480_800_YL_5910_JAVA_2_9_9"; //2012.07.19
	// for 斐讯i330v
	public static final String CLIENT_VERSION_PXX_I330V = "TYYD_Android_4_0_480_800_PXX_I330V_JAVA_2_9_9"; //2012.07.19
	// for LNV A600E
	public static final String CLIENT_VERSION_LNV_A600E = "TYYD_Android_4_0_480_800_LNV_A600E_JAVA_2_9_9"; //2012.07.20
	// for 中兴N8010
	public static final String CLIENT_VERSION_ZTE_N8010 = "TYYD_Android_4_0_480_800_ZTE_N8010_JAVA_2_9_9"; //2012.07.20
	// for 金立C610
	public static final String CLIENT_VERSION_GIO_C610 = "TYYD_Android_4_0_480_800_GIO_C610_JAVA_2_9_9"; //2012.07.30
	// for 酷派9120
	public static final String CLIENT_VERSION_YL_9120 = "TYYD_Android_4_0_480_800_YL_9120_JAVA_2_9_9"; //2012.08.02
	// for 海信EG909
	public static final String CLIENT_VERSION_HS_EG909 = "TYYD_Android_4_0_480_800_HS_EG909_JAVA_2_9_9"; //2012.08.20
	// for 海信E926
	public static final String CLIENT_VERSION_HS_E926 = "TYYD_Android_4_0_480_800_HS_E926_JAVA_2_9_9"; //2012.08.20
	// for 中兴N878
	public static final String CLIENT_VERSION_ZTE_N878 = "TYYD_Android_4_0_480_800_ZTE_N878_JAVA_2_9_9"; //2012.08.21
	// for 酷派5860S
	public static final String CLIENT_VERSION_YL_5860S = "TYYD_Android_4_0_480_800_YL_5860S_JAVA_2_9_9"; //2012.08.24
	// for 酷诺 EG189
	public static final String CLIENT_VERSION_KUNUO_EG189 = "TYYD_Android_2_3_480_800_KUNUO_EG189_JAVA_2_9_9"; //2012.08.31
	// for 中兴 N881E
	public static final String CLIENT_VERSION_ZTE_N881E = "TYYD_Android_4_0_480_800_ZTE_N881E_JAVA_2_9_9"; //2012.08.31
	// for 桑菲D833
	public static final String CLIENT_VERSION_SAF_D833 = "TYYD_Android_4_0_480_800_SAF_D833_JAVA_2_9_9"; //2012.09.03
	// for HTC-T329d
	public static final String CLIENT_VERSION_HTC_T329D = "TYYD_Android_4_0_480_800_HTC_T329D_JAVA_2_9_9"; //2012.09.17
	// for HUAWEI C8833D
	public static final String CLIENT_VERSION_HW_C8833D = "TYYD_Android_4_1_480_800_HW_C8833D_JAVA_2_9_9"; //2012.09.24
	// for TCL-D668
	public static final String CLIENT_VERSION_TCL_D668 = "TYYD_Android_4_0_480_800_TCL_D668_JAVA_2_9_9";// 2012.09.24
	// for 华为 C8826D
	public static final String CLIENT_VERSION_HW_C8826D = "TYYD_Android_4_0_480_800_HW_C8826D_JAVA_2_9_9";// 2012.09.24
	// for 三星W2013
	public static final String CLIENT_VERSION_SCH_W2013 = "TYYD_Android_4_0_480_800_SCH_W2013_JAVA_2_9_9"; //2012.09.28
	// for 三星I829
	public static final String CLIENT_VERSION_SCH_I829 = "TYYD_Android_4_1_480_800_SCH_I829_JAVA_2_9_9"; //2012.10.08
	// for 三星I759
	public static final String CLIENT_VERSION_SCH_I759 = "TYYD_Android_4_1_480_800_SCH_I759_JAVA_2_9_9"; //2012.10.11
	// for HTC T327d
	public static final String CLIENT_VERSION_HTC_T327D = "TYYD_Android_4_0_480_800_HTC_T327D_JAVA_2_9_9"; //2012.10.23
	// for 海尔E700
	public static final String CLIENT_VERSION_HC_E700 = "TYYD_Android_4_0_480_800_HC_E700_JAVA_2_9_9"; //2012.11.01
	// for 中兴N807
	public static final String CLIENT_VERSION_ZTE_N807 = "TYYD_Android_4_1_480_800_ZTE_N807_JAVA_2_9_9"; //2012.11.01
	// for 金立C605
	public static final String CLIENT_VERSION_GIO_C605 = "TYYD_Android_4_0_480_800_GIO_C605_JAVA_2_9_9"; //2012.11.19
	// for 三星I739
	public static final String CLIENT_VERSION_SCH_I739 = "TYYD_Android_4_1_480_800_SCH_I739_JAVA_2_9_9"; //2012.11.27
	// for TCL D920
	public static final String CLIENT_VERSION_TCL_D920 = "TYYD_Android_4_0_480_800_TCL_D920_JAVA_2_9_9"; //2012.11.28
	// for 华录 S9500
	public static final String CLIENT_VERSION_CHL_S9500 = "TYYD_Android_2_3_480_800_CHL_S9500_JAVA_2_9_9"; //2012.12.07
	// for 海信 E930
	public static final String CLIENT_VERSION_HS_E930 = "TYYD_Android_2_3_480_800_HS_E930_JAVA_2_9_9"; //2012.12.07
	// for lephone 2900
	public static final String CLIENT_VERSION_BLF_2900 = "TYYD_Android_2_3_480_800_BLF_2900_JAVA_2_9_9"; //2012.12.07
	// for 锋达通 E8
	public static final String CLIENT_VERSION_FDT_E8 = "TYYD_Android_2_3_480_800_FDT_E8_JAVA_2_9_9"; //2012.12.07
	// for 至尊宝CD801
	public static final String CLIENT_VERSION_HST_CD801 = "TYYD_Android_2_3_480_800_HST_CD801_JAVA_2_9_9"; //2012.12.07
	// for 国信通 E880
	public static final String CLIENT_VERSION_GXT_E880 = "TYYD_Android_2_3_480_800_GXT_E880_JAVA_2_9_9"; //2012.12.07
	// for 大唐S26
	public static final String CLIENT_VERSION_DT_S26 = "TYYD_Android_4_0_480_800_DT_S26_JAVA_2_9_9"; //2012.12.10
	// for 广信 EF88
	public static final String CLIENT_VERSION_GX_EF88 = "TYYD_Android_2_3_480_800_GX_EF88_JAVA_2_9_9"; //2012.12.11
	// for 飓畅 JC-A8
	public static final String CLIENT_VERSION_JCT_A8 = "TYYD_Android_4_0_480_800_JCT_A8_JAVA_2_9_9"; //2012.12.12
	// for 天迈  D99SW
	public static final String CLIENT_VERSION_TM_D99SW = "TYYD_Android_2_3_480_800_TM_D99SW_JAVA_2_9_9"; //2012.12.14
	// for 展翼 N9X
	public static final String CLIENT_VERSION_ZY_N9X = "TYYD_Android_2_3_480_800_ZY_N9X_JAVA_2_9_9"; //2012.12.18
	// for 博瑞世纪 W69
	public static final String CLIENT_VERSION_BR_W69 = "TYYD_Android_4_0_480_800_BR_W69_JAVA_2_9_9"; //2012.12.19
	// for 百灵 VE63
	public static final String CLIENT_VERSION_BLW_VE63 = "TYYD_Android_4_0_480_800_BLW_VE63_JAVA_2_9_9"; //2012.12.21
	// for 三星 I879
	public static final String CLIENT_VERSION_SCH_I879 = "TYYD_Android_4_1_480_800_SCH_I879_JAVA_2_9_9"; //2012.12.24
	// for 微网通信 H200
	public static final String CLIENT_VERSION_WW_H200 = "TYYD_Android_2_3_480_800_WW_H200_JAVA_2_9_9"; //2013.01.08

	// *******************************************************************************************
	// ***************************************540*960版本号***************************************
	// *******************************************************************************************
	// for MOTO XT882
	// public static final String CLIENT_VERSION_MOTO_XT882 =
	// "TYYD_Android_2_2_540_960_XT882_JAVA_2_9_9";//2011.01.27
	// public static final String CLIENT_VERSION_MOTO_XT882 =
	// "TYYD_Android_2_3_540_960_MOT_XT882_JAVA_2_0_0";//2011.07.11
	public static final String CLIENT_VERSION_MOTO_XT882 = "TYYD_Android_2_3_540_960_MOT_XT882_JAVA_2_9_9";// 2011.08.02
	// for MOTO XT883
	// public static final String CLIENT_VERSION_MOTO_XT883 =
	// "TYYD_Android_2_3_540_960_XT883_JAVA_2_9_9";//2011.04.27
	// public static final String CLIENT_VERSION_MOTO_XT883 =
	// "TYYD_Android_2_3_540_960_MOT_XT883_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_MOTO_XT883 = "TYYD_Android_2_3_540_960_MOT_XT883_JAVA_2_9_9";// 2011.08.02
	// for HTC X515D
	public static final String CLIENT_VERSION_HTC_X515D = "TYYD_Android_2_3_540_960_HTC_X515D_JAVA_2_9_9";// 2011.08.02
	// for 摩托XT885
//	public static final String CLIENT_VERSION_MOTO_XT885 = "TYYD_Android_2_3_720_1280_MOT_XT885_JAVA_2_9_9";// 2011.08.31
	public static final String CLIENT_VERSION_MOTO_XT928 = "TYYD_Android_2_3_720_1280_MOT_XT928_JAVA_2_9_9";//2011.10.24
	// for 夏普 SH831T
	public static final String CLIENT_VERSION_SHARP_SH831T = "TYYD_Android_2_3_540_960_SHP_SH831T_JAVA_2_9_9";//2011.12.06
	// for 中兴 N970
	public static final String CLIENT_VERSION_ZTE_N970 = "TYYD_Android_4_0_540_960_ZTE_N970_JAVA_2_9_9";//2012.05.23
	// for 华为 C8950D
	public static final String CLIENT_VERSION_HW_C8950D = "TYYD_Android_4_0_540_960_HW_C8950D_JAVA_2_9_9";//2012.07.02
	// for 华为 C8951D
	public static final String CLIENT_VERSION_HW_C8951D = "TYYD_Android_4_1_480_854_HW_C8951D_JAVA_2_9_9";//2012.09.24
	//for 华为C8813
	public static final String CLIENT_VERSION_HW_C8813 = "TYYD_Android_4_1_480_854_HW_C8813_JAVA_2_9_9";//2012.11.05
	//for 华录S9388
	public static final String CLIENT_VERSION_CHL_S9388 = "TYYD_Android_4_0_480_854_CHL_S9388_JAVA_2_9_9";//2012.11.22
	//for 斐讯 i700v
	public static final String CLIENT_VERSION_PXX_I700V = "TYYD_Android_4_0_540_960_PXX_I700V_JAVA_2_9_9";//2012.11.28
	//for 赛鸿  X5
	public static final String CLIENT_VERSION_SH_X5 = "TYYD_Android_4_0_480_854_SH_X5_JAVA_2_9_9";//2012.12.07
	//for 联想 A630E
	public static final String CLIENT_VERSION_LNV_A630E = "TYYD_Android_4_1_480_854_LNV_A630E_JAVA_2_9_9";//2012.12.26
	//for 海信 EG956
	public static final String CLIENT_VERSION_HS_E956 = "TYYD_Android_4_0_480_854_HS_E956_JAVA_2_9_9";//2012.12.26
	//for 灵威朗 VE65
	public static final String CLIENT_VERSION_BLW_VE65 = "TYYD_Android_4_0_480_854_BLW_VE65_JAVA_2_9_9";//2013.01.09

	// *******************************************************************************************
	// ***************************************320*480版本号***************************************
	// *******************************************************************************************
	// 中兴 R750
	// public static final String CLIENT_VERSION_R750 =
	// "TYYD_Android_2_1_320_480_R750_JAVA_1_1_0";
	// public static final String CLIENT_VERSION_R750 =
	// "TYYD_Android_2_1_320_480_R750_JAVA_1_1_1";//20101202
	// public static final String CLIENT_VERSION_R750 =
	// "TYYD_Android_2_1_320_480_R750_JAVA_1_1_2";//20101209
	// public static final String CLIENT_VERSION_R750 =
	// "TYYD_Android_2_1_320_480_R750_JAVA_1_2_0";//20110104
	// public static final String CLIENT_VERSION_R750 =
	// "TYYD_Android_2_1_320_480_R750_JAVA_1_2_1";//2011.01.26
	// public static final String CLIENT_VERSION_R750 =
	// "TYYD_Android_2_1_320_480_R750_JAVA_1_3_0";//2011.04.11
	// public static final String CLIENT_VERSION_R750 =
	// "TYYD_Android_2_1_320_480_ZTE_R750_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_R750 = "TYYD_Android_2_1_320_480_ZTE_R750_JAVA_2_9_9";// 2011.07.12
	// for HUAWEI C8600
	// public static final String CLIENT_VERSION_C8600 =
	// "TYYD_Android_2_1_320_480_C8600_JAVA_2_9_9";
	// public static final String CLIENT_VERSION_C8600 =
	// "TYYD_Android_2_1_320_480_C8600_JAVA_1_0_1";//20101220
	// public static final String CLIENT_VERSION_C8600 =
	// "TYYD_Android_2_1_320_480_C8600_JAVA_1_1_0";//20110104
	// public static final String CLIENT_VERSION_C8600 =
	// "TYYD_Android_2_1_320_480_C8600_JAVA_1_1_1";//2011.01.26
	// public static final String CLIENT_VERSION_C8600 =
	// "TYYD_Android_2_1_320_480_C8600_JAVA_1_3_0";//2011.04.01
	// public static final String CLIENT_VERSION_C8600 =
	// "TYYD_Android_2_1_320_480_HW_C8600_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_C8600 = "TYYD_Android_2_1_320_480_HW_C8600_JAVA_2_9_9";// 2011.08.02
	// for 宇龙D530
	// public static final String CLIENT_VERSION_D530 =
	// "TYYD_Android_2_1_320_480_D530_JAVA_2_9_9";
	// public static final String CLIENT_VERSION_D530 =
	// "TYYD_Android_2_1_320_480_D530_JAVA_1_3_0";//2011.03.18
	// public static final String CLIENT_VERSION_D530 =
	// "TYYD_Android_2_1_240_320_ZTE_N600p_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_D530 = "TYYD_Android_2_1_320_480_YL_D530_JAVA_2_9_9";// 2011.08.02
	// for 海尔E600
	// public static final String CLIENT_VERSION_E600 =
	// "TYYD_Android_2_1_320_480_E600_JAVA_2_9_9";
	// public static final String CLIENT_VERSION_E600 =
	// "TYYD_Android_2_1_320_480_E600_JAVA_1_1_0";//20110104
	// public static final String CLIENT_VERSION_E600 =
	// "TYYD_Android_2_1_320_480_E600_JAVA_1_1_1";//2011.01.26
	// public static final String CLIENT_VERSION_E600 =
	// "TYYD_Android_2_1_320_480_HC_E600_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_E600 = "TYYD_Android_2_1_320_480_HC_E600_JAVA_2_9_9";// 2011.08.02
	// for HTC HERO200
	// public static final String CLIENT_VERSION_HTC_HERO200 =
	// "TYYD_Android_2_1_320_480_Hero200_JAVA_2_9_9";//2011.03.10
	// public static final String CLIENT_VERSION_HTC_HERO200 =
	// "TYYD_Android_2_1_320_480_HTC_Hero200_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_HTC_HERO200 = "TYYD_Android_2_1_320_480_HTC_Hero200_JAVA_2_9_9";// 2011.08.02
	// for COOLPAD D539
	// public static final String CLIENT_VERSION_COOLPAD_D539 =
	// "TYYD_Android_2_1_320_480_D539_JAVA_2_9_9";//20101220
	// public static final String CLIENT_VERSION_COOLPAD_D539 =
	// "TYYD_Android_2_1_320_480_YL_D539_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_COOLPAD_D539 = "TYYD_Android_2_1_320_480_YL_D539_JAVA_2_9_9";// 2011.08.02
	// for COOLPAD D5800
	// public static final String CLIENT_VERSION_COOLPAD_D5800 =
	// "TYYD_Android_2_2_320_480_D5800_JAVA_2_9_9";//20101220
	// public static final String CLIENT_VERSION_COOLPAD_D5800 =
	// "TYYD_Android_2_2_320_480_YL_D5800_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_COOLPAD_D5800 = "TYYD_Android_2_2_320_480_YL_D5800_JAVA_2_9_9";// 2011.08.02
	// for COOLPAD N950
	// public static final String CLIENT_VERSION_COOLPAD_N950 =
	// "TYYD_Android_2_2_320_480_N950_JAVA_2_9_9";//20101220
	// public static final String CLIENT_VERSION_COOLPAD_N950 =
	// "TYYD_Android_2_2_320_480_YL_N950_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_COOLPAD_N950 = "TYYD_Android_2_2_320_480_YL_N950_JAVA_2_9_9";// 2011.08.02
	// for SAMSUNG I579
	// public static final String CLIENT_VERSION_SAMSUNG_I579 =
	// "TYYD_Android_2_2_320_480_I579_JAVA_2_9_9";//20101222
	// public static final String CLIENT_VERSION_SAMSUNG_I579 =
	// "TYYD_Android_2_2_320_480_SCH_I579_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_SAMSUNG_I579 = "TYYD_Android_2_2_320_480_SCH_I579_JAVA_2_9_9";// 2011.08.02
	// for HAIER N6E
	// public static final String CLIENT_VERSION_HAIER_N6E =
	// "TYYD_Android_2_2_320_480_N6E_JAVA_2_9_9";//20101229
	// public static final String CLIENT_VERSION_HAIER_N6E =
	// "TYYD_Android_2_2_320_480_N6E_JAVA_2_9_9";//2011.01.26
	// public static final String CLIENT_VERSION_HAIER_N6E =
	// "TYYD_Android_2_2_320_480_HC_N6E_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_HAIER_N6E = "TYYD_Android_2_2_320_480_HC_N6E_JAVA_2_9_9";// 2011.08.02
	// for 天语 E600
	// public static final String CLIENT_VERSION_TIANYU_E600 =
	// "TYYD_Android_2_2_320_480_TYE600_JAVA_2_9_9";//2011.01.19
	// public static final String CLIENT_VERSION_TIANYU_E600 =
	// "TYYD_Android_2_2_320_480_TY_E600_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_TIANYU_E600 = "TYYD_Android_2_2_320_480_TY_E600_JAVA_2_9_9";// 2011.08.02
	// for COOLPAD E239
	// public static final String CLIENT_VERSION_COOLPAD_E239 =
	// "TYYD_Android_2_1_320_480_E239_JAVA_2_9_9";//2011.03.07
	// public static final String CLIENT_VERSION_COOLPAD_E239 =
	// "TYYD_Android_2_1_320_480_YL_E239_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_COOLPAD_E239 = "TYYD_Android_2_1_320_480_YL_E239_JAVA_2_9_9";// 2011.08.02
	// for SAMSUNG I569
	// public static final String CLIENT_VERSION_SAMSUNG_I569 =
	// "TYYD_Android_2_2_320_480_I569_JAVA_2_9_9";//2011.03.10
	// public static final String CLIENT_VERSION_SAMSUNG_I569 =
	// "TYYD_Android_2_2_320_480_SCH_I569_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_SAMSUNG_I569 = "TYYD_Android_2_2_320_480_SCH_I569_JAVA_2_9_9";// 2011.08.02
	// for ZTE R750+
	// public static final String CLIENT_VERSION_ZTE_R750PLUS =
	// "TYYD_Android_2_2_320_480_R750p_JAVA_2_9_9";//2011.03.10
	// public static final String CLIENT_VERSION_ZTE_R750PLUS =
	// "TYYD_Android_2_2_320_480_ZTE_R750p_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_ZTE_R750PLUS = "TYYD_Android_2_2_320_480_ZTE_R750p_JAVA_2_9_9";// 2011.08.02
	// for 三美奇 EA6000
	// public static final String CLIENT_VERSION_SANMEIQI_EA6000 =
	// "TYYD_Android_2_2_320_480_EA6000_JAVA_2_9_9";//2011.03.18
	// public static final String CLIENT_VERSION_SANMEIQI_EA6000 =
	// "TYYD_Android_2_2_320_480_SMQ_EA6000_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_SANMEIQI_EA6000 = "TYYD_Android_2_2_320_480_SMQ_EA6000_JAVA_2_9_9";// 2011.08.02
	// for HUAWEI C8650
	// public static final String CLIENT_VERSION_HUAWEI_C8650 =
	// "TYYD_Android_2_3_320_480_C8650_JAVA_2_9_9";//2011.03.29
	// public static final String CLIENT_VERSION_HUAWEI_C8650 =
	// "TYYD_Android_2_3_320_480_HW_C8650_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_HUAWEI_C8650 = "TYYD_Android_2_3_320_480_HW_C8650_JAVA_2_9_9";// 2011.08.02
	// for 高新奇ES608
	// public static final String CLIENT_VERSION_GXQ_ES608 =
	// "TYYD_Android_2_1_320_480_ES608_JAVA_2_9_9";//2011.04.06
	// public static final String CLIENT_VERSION_GXQ_ES608 =
	// "TYYD_Android_2_1_320_480_GXQ_ES608_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_GXQ_ES608 = "TYYD_Android_2_1_320_480_GXQ_ES608_JAVA_2_9_9";// 2011.08.02
	// for 创维PE10
	// public static final String CLIENT_VERSION_SKYWOTH_PE10 =
	// "TYYD_Android_2_1_320_480_PE10_JAVA_2_9_9";//2011.04.14
	// public static final String CLIENT_VERSION_SKYWOTH_PE10 =
	// "TYYD_Android_2_1_320_480_CW_PE10_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_SKYWOTH_PE10 = "TYYD_Android_2_1_320_480_CW_PE10_JAVA_2_9_9";// 2011.08.02
	// for 和信N200
	// public static final String CLIENT_VERSION_HESENS_N200 =
	// "TYYD_Android_2_1_320_480_N200_JAVA_2_9_9";//2011.04.14
	// public static final String CLIENT_VERSION_HESENS_N200 =
	// "TYYD_Android_2_1_320_480_HX_N200_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_HESENS_N200 = "TYYD_Android_2_1_320_480_HX_N200_JAVA_2_9_9";// 2011.08.02
	// for HTC MARVEL
	// public static final String CLIENT_VERSION_HTC_MARVEL =
	// "TYYD_Android_2_3_320_480_MARVEL_JAVA_2_9_9";//2011.04.15
	// public static final String CLIENT_VERSION_HTC_MARVEL =
	// "TYYD_Android_2_3_320_480_HTC_MARVEL_JAVA_2_0_0";//2011.07.25
	public static final String CLIENT_VERSION_HTC_MARVEL = "TYYD_Android_2_3_320_480_HTC_MARVEL_JAVA_2_9_9";// 2011.08.02
	// for 彤霖 T90
	// public static final String CLIENT_VERSION_TONGLIN_T90 =
	// "TYYD_Android_2_1_320_480_T90_JAVA_2_9_9";//2011.05.03
	// public static final String CLIENT_VERSION_TONGLIN_T90 =
	// "TYYD_Android_2_1_320_480_TL_T90_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_TONGLIN_T90 = "TYYD_Android_2_1_320_480_TL_T90_JAVA_2_9_9";// 2011.08.02
	// for 中兴N760
	// public static final String CLIENT_VERSION_ZTE_N760 =
	// "TYYD_Android_2_3_320_480_N760_JAVA_2_9_9";//2011.05.09
	// public static final String CLIENT_VERSION_ZTE_N760 =
	// "TYYD_Android_2_3_320_480_ZTE_N760_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_ZTE_N760 = "TYYD_Android_2_3_320_480_ZTE_N760_JAVA_2_9_9";// 2011.08.02
	// for 海信EG968B
	// public static final String CLIENT_VERSION_HS_EG968B =
	// "TYYD_Android_2_1_320_480_EG968B_JAVA_2_9_9";//2011.05.30
	// public static final String CLIENT_VERSION_HS_EG968B =
	// "TYYD_Android_2_1_320_480_HS_EG968B_JAVA_2_0_0";//2011.07.25
	public static final String CLIENT_VERSION_HS_EG968B = "TYYD_Android_2_1_320_480_HS_EG968B_JAVA_2_9_9";// 2011.08.02
	// for 三和新 S06
	// public static final String CLIENT_VERSION_SHX_S06 =
	// "TYYD_Android_2_2_320_480_S06_JAVA_2_9_9";//2011.06.15
	// public static final String CLIENT_VERSION_SHX_S06 =
	// "TYYD_Android_2_2_320_480_SHX_S06_JAVA_2_0_0";//2011.07.25
	public static final String CLIENT_VERSION_SHX_S06 = "TYYD_Android_2_2_320_480_SHX_S06_JAVA_2_9_9";// 2011.08.02
	// for 海信 E86
	// public static final String CLIENT_VERSION_HS_E86 =
	// "TYYD_Android_2_1_320_480_E86_JAVA_2_9_9";//2011.06.15
	// public static final String CLIENT_VERSION_HS_E86 =
	// "TYYD_Android_2_1_320_480_HS_E86_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_HS_E86 = "TYYD_Android_2_1_320_480_HS_E86_JAVA_2_9_9";// 2011.08.02
	// for EPHONE A6
	// public static final String CLIENT_VERSION_EPHONE_A6 =
	// "TYYD_Android_2_1_320_480_A6_JAVA_2_9_9";//2011.06.22
	public static final String CLIENT_VERSION_EPHONE_A6 = "TYYD_Android_2_1_320_480_YFZ_A6_JAVA_2_9_9";// 2011.08.02
	// for 联想 A68E
	// public static final String CLIENT_VERSION_LENOVO_A68E =
	// "TYYD_Android_2_3_320_480_A68E_JAVA_2_9_9";//2011.06.23
	public static final String CLIENT_VERSION_LENOVO_A68E = "TYYD_Android_2_3_320_480_LNV_A68E_JAVA_2_9_9";// 2011.08.02
	// for 展翼 N8
	// public static final String CLIENT_VERSION_FLY_N8 =
	// "TYYD_Android_2_1_320_480_N8_JAVA_2_9_9";//2011.06.24
	// public static final String CLIENT_VERSION_FLY_N8 =
	// "TYYD_Android_2_1_320_480_ZY_N8_JAVA_2_0_0";//2011.07.25
	public static final String CLIENT_VERSION_FLY_N8 = "TYYD_Android_2_1_320_480_ZY_N8_JAVA_2_9_9";// 2011.08.02
	// for 威睿 P2
	// public static final String CLIENT_VERSION_VIA_P2 =
	// "TYYD_Android_2_1_320_480_P2_JAVA_2_9_9";//2011.06.24
	// public static final String CLIENT_VERSION_VIA_P2 =
	// "TYYD_Android_2_1_320_480_VIA_P2_JAVA_2_0_0";//2011.07.25
	public static final String CLIENT_VERSION_VIA_P2 = "TYYD_Android_2_1_320_480_VIA_P2_JAVA_2_9_9";// 2011.08.02
	// for 金派 E239
	// public static final String CLIENT_VERSION_KINGPAD_E239 =
	// "TYYD_Android_2_1_320_480_KINGPAD_E239_JAVA_2_9_9";//2011.06.27
	// public static final String CLIENT_VERSION_KINGPAD_E239 =
	// "TYYD_Android_2_1_320_480_SM_E239_JAVA_2_0_0";//2011.07.25
	public static final String CLIENT_VERSION_KINGPAD_E239 = "TYYD_Android_2_1_320_480_SM_E239_JAVA_2_9_9";// 2011.08.02
	// for LENOVO A1-32AJO
	// public static final String CLIENT_VERSION_LENOVO_AMAZON =
	// "TYYD_Android_2_3_320_480_LENOVOA1_JAVA_2_9_9";//2011.07.05
	// public static final String CLIENT_VERSION_LENOVO_A1 =
	// "TYYD_Android_2_3_320_480_LNV_A132AJ0_JAVA_2_0_0";//2011.07.21
	public static final String CLIENT_VERSION_LENOVO_A1 = "TYYD_Android_2_3_320_480_LNV_A132AJ0_JAVA_2_9_9";// 2011.08.02
	// for 南极星 D99
	public static final String CLIENT_VERSION_EXUN_D99 = "TYYD_Android_2_2_320_480_D99_JAVA_2_9_9";// 2011.07.05
	// for LG CS600
	// public static final String CLIENT_VERSION_LG_CS600 =
	// "TYYD_Android_2_3_320_480_CS600_JAVA_2_9_9";//2011.07.08
	public static final String CLIENT_VERSION_LG_CS600 = "TYYD_Android_2_3_320_480_LG_CS600_JAVA_2_9_9";// 2011.08.02
	// for 中辰 ZC600
	// public static final String CLIENT_VERSION_ZC_600 =
	// "TYYD_Android_2_1_320_480_ZC_ZC600_JAVA_2_0_0";//2011.07.19
	public static final String CLIENT_VERSION_ZC_600 = "TYYD_Android_2_1_320_480_ZC_ZC600_JAVA_2_9_9";// 2011.02.02
	// for COOLPAD 5820
	// public static final String CLIENT_VERSION_COOLPAD_5820 =
	// "TYYD_Android_2_2_320_480_YL_5820_JAVA_2_0_0";//2011.07.25
	public static final String CLIENT_VERSION_COOLPAD_5820 = "TYYD_Android_2_2_320_480_YL_5820_JAVA_2_9_9";// 2011.08.02
	// for 海尔EG-E600
	// public static final String CLIENT_VERSION_HC_EGE600 =
	// "TYYD_Android_2_1_320_480_HC_EGE600_JAVA_2_0_0";//2011.07.25
	public static final String CLIENT_VERSION_HC_EGE600 = "TYYD_Android_2_1_320_480_HC_EGE600_JAVA_2_9_9";// 2011.08.02
	// for 彤霖 T98
	// public static final String CLIENT_VERSION_TONGLIN_T98 =
	// "TYYD_Android_2_2_320_480_TL_T98_JAVA_2_0_0";//2011.07.25
	public static final String CLIENT_VERSION_TONGLIN_T98 = "TYYD_Android_2_2_320_480_TL_T98_JAVA_2_9_9";// 2011.08.02
	// for 波导 AE750
	// public static final String CLIENT_VERSION_BIRD_AE750 =
	// "TYYD_Android_2_2_320_480_BD_AE750_JAVA_2_0_0";//2011.07.26
	public static final String CLIENT_VERSION_BIRD_AE750 = "TYYD_Android_2_2_320_480_BD_AE750_JAVA_2_9_9";// 2011.08.02
	// for 大成 E366
	// public static final String CLIENT_VERSION_DC_E366 =
	// "TYYD_Android_2_1_320_480_DC_E366_JAVA_2_0_0";//2011.07.28
	public static final String CLIENT_VERSION_DC_E366 = "TYYD_Android_2_1_320_480_DC_E366_JAVA_2_9_9";// 2011.08.02
	// for 奇乐 A709
	public static final String CLIENT_VERSION_QL_A709 = "TYYD_Android_2_3_320_480_QL_A709_JAVA_2_9_9";// 2011.08.02
	// for 华为 S8520
	public static final String CLIENT_VERSION_HW_S8520 = "TYYD_Android_2_2_320_480_HW_S8520_JAVA_2_9_9";// 2011.08.02
	// for 语信-E80
	public static final String CLIENT_VERSION_YUXIN_E80 = "TYYD_Android_2_2_320_480_YX_E80_JAVA_2_9_9";// 2011.08.15
	// for 赛鸿I90
	public static final String CLIENT_VERSION_SAIHONG_I90 = "TYYD_Android_2_1_320_480_SH_I90_JAVA_2_9_9";// 2011.08.15
	// for 和信N300
	public static final String CLIENT_VERSION_HEXIN_N300 = "TYYD_Android_2_1_320_480_HX_N300_JAVA_2_9_9";// 2011.08.15
	// for 海尔N710E
	public static final String CLIENT_VERSION_HC_N710E = "TYYD_Android_2_1_320_480_HC_N710E_JAVA_2_9_9";// 2011.08.15
	// for COOLPAD 5855
	public static final String CLIENT_VERSION_COOLPAD_5855 = "TYYD_Android_2_2_320_480_YL_5855_JAVA_2_9_9";// 2011.08.23
	// for 海尔E899
	public static final String CLIENT_VERSION_HC_E899 = "TYYD_Android_2_3_320_480_HC_E899_JAVA_2_9_9";// 2011.08.23
	// for 大众EC600
	public static final String CLIENT_VERSION_DZD_EC600 = "TYYD_Android_2_2_320_480_DZD_EC600_JAVA_2_9_9";// 2011.08.23
	// for 中兴N780
	public static final String CLIENT_VERSION_ZTE_N780 = "TYYD_Android_2_2_320_480_ZTE_N780_JAVA_2_9_9";// 2011.08.31
	// for 本为5100
	public static final String CLIENT_VERSION_BENWEI_5100 = "TYYD_Android_2_2_320_480_BW_5100_JAVA_2_9_9";// 2011.08.31
	// for 海尔N720E
	public static final String CLIENT_VERSION_HC_N720E = "TYYD_Android_2_2_320_480_HC_N720E_JAVA_2_9_9";// 2011.08.31
	// for 三星I579I
	public static final String CLIENT_VERSION_SAMSUNG_I579I = "TYYD_Android_2_3_320_480_SCH_I579I_JAVA_2_9_9";// 2011.09.08
	// for 首亿E6
	public static final String CLIENT_VERSION_SOAYE_E6 = "TYYD_Android_2_2_320_480_SSY_E6_JAVA_2_9_9";// 2011.09.14
	// for TCL-C990
	public static final String CLIENT_VERSION_TCL_C990 = "TYYD_Android_2_3_320_480_TCL_C990_JAVA_2_9_9";// 2011.09.14
	// for 联想A390E
	public static final String CLIENT_VERSION_LENOVO_A390E = "TYYD_Android_2_2_320_480_LNV_A390E_JAVA_2_9_9";// 2011.09.15
	// for 创维PE90
	public static final String CLIENT_VERSION_SKYWORTH_PE90 = "TYYD_Android_2_2_320_480_CW_PE90_JAVA_2_9_9";// 2011.09.15
	// for 华录S3000
	public static final String CLIENT_VERSION_CHL_S3000 = "TYYD_Android_2_3_320_480_CHL_S3000_JAVA_2_9_9";// 2011.09.26
	// for 展翼N7
	public static final String CLIENT_VERSION_ZY_N7 = "TYYD_Android_2_1_320_480_ZY_N7_JAVA_2_9_9";// 2011.09.26
	// for 桑达S508EG
	public static final String CLIENT_VERSION_SED_S508EG = "TYYD_Android_2_2_320_480_SED_S508EG_JAVA_2_9_9";// 2011.09.29
	// for 语信-E88
	public static final String CLIENT_VERSION_YUSUN_E88 = "TYYD_Android_2_2_320_480_YX_E88_JAVA_2_9_9";// 2011.10.08
	// for 语信-E70
	public static final String CLIENT_VERSION_YUSUN_E70 = "TYYD_Android_2_1_320_480_YX_E70_JAVA_2_9_9";// 2011.10.19
	// for COOLPAD 5899
	public static final String CLIENT_VERSION_COOLPAD_5899 = "TYYD_Android_2_2_320_480_YL_5899_JAVA_2_9_9";// 2011.10.24
	// for 天元TE690
	public static final String CLIENT_VERSION_YCT_TE690 = "TYYD_Android_2_3_320_480_YCT_TE690_JAVA_2_9_9";// 2011.10.24
	// for 夏普SH320T
	public static final String CLIENT_VERSION_SHARP_SH320T = "TYYD_Android_2_3_320_480_SHP_SH320T_JAVA_2_9_9";// 2011.11.01
	// for ZTE X500
	public static final String CLIENT_VERSION_ZTE_X500 = "TYYD_Android_2_3_320_480_ZTE_X500_JAVA_2_9_9";// 2011.11.07
	// for HUAWEI C8650E
	//public static final String CLIENT_VERSION_HUAWEI_C8650E = "TYYD_Android_2_3_320_480_HW_C8650E_JAVA_2_9_9";// 2011.11.08
	public static final String CLIENT_VERSION_HUAWEI_C8650E = "TYYD_Android_2_3_320_480_HW_C8650P_JAVA_2_9_9";// 2011.12.13
	// for 和信N800
	public static final String CLIENT_VERSION_HEXIN_N800 = "TYYD_Android_2_3_320_480_HX_N800_JAVA_2_9_9";// 2011.11.15
	// for 和信N700
	public static final String CLIENT_VERSION_HEXIN_N700 = "TYYD_Android_2_2_320_480_HX_N700_JAVA_2_9_9";// 2011.11.15
	// for 海信E860
	public static final String CLIENT_VERSION_HS_E860 = "TYYD_Android_2_3_320_480_HS_E860_JAVA_2_9_9"; //2011.11.25
	// for 谷派E68
	public static final String CLIENT_VERSION_EBEST_E68 = "TYYD_Android_2_1_320_480_SGP_E68_JAVA_2_9_9"; //2011.11.25
	// for 赛鸿I91
	public static final String CLIENT_VERSION_SAIHONG_I91 = "TYYD_Android_2_2_320_480_SH_I91_JAVA_2_9_9";// 2011.11.25
	// for 基伍-E86
	public static final String CLIENT_VERSION_JWG_E86 = "TYYD_Android_2_3_320_480_JWG_E86_JAVA_2_9_9";// 2011.12.13
	// for 三星-i619
	public static final String CLIENT_VERSION_SCH_I619 = "TYYD_Android_2_3_320_480_SCH_I619_JAVA_2_9_9";// 2011.12.20
	// for 博瑞S9
	public static final String CLIENT_VERSION_BR_S9 = "TYYD_Android_2_3_320_480_BR_S9_JAVA_2_9_9";// 2011.12.22
	// for 广信E920
	public static final String CLIENT_VERSION_GX_E920 = "TYYD_Android_2_1_320_480_GX_E920_JAVA_2_9_9";// 2011.12.27
	// for 海尔N620E
	public static final String CLIENT_VERSION_HC_N620E = "TYYD_Android_2_1_320_480_HC_N620E_JAVA_2_9_9";// 2011.12.30
	// for  蓝天S600D
	public static final String CLIENT_VERSION_TLT_S600D= "TYYD_Android_2_2_320_480_TLT_S600D_JAVA_2_9_9";// 2012.1.11
	// for  赛鸿I98
	public static final String CLIENT_VERSION_SAIHONG_I98 = "TYYD_Android_2_2_320_480_SH_I98_JAVA_2_9_9";// 2012.01.31
	// for  语信E89
	public static final String CLIENT_VERSION_YUSUN_E89 = "TYYD_Android_2_3_320_480_YX_E89_JAVA_2_9_9";// 2012.01.31
	// for  夏普SH330T
	public static final String CLIENT_VERSION_SHARP_SH330T = "TYYD_Android_2_3_320_480_SHP_SH330T_JAVA_2_9_9";// 2012.01.31
	// for  蓝天S800
	public static final String CLIENT_VERSION_TLT_S800 = "TYYD_Android_2_3_320_480_TLT_S800_JAVA_2_9_9";// 2012.01.31
	// for 华为C8655
	public static final String CLIENT_VERSION_HUAWEI_C8655 = "TYYD_Android_2_3_320_480_HW_C8655_JAVA_2_9_9";// 2012.02.04
	// for 易丰A10
	public static final String CLIENT_VERSION_YFZ_A10 = "TYYD_Android_2_3_320_480_YFZ_A10_JAVA_2_9_9";// 2012.02.10
	// for 华录S3500
	public static final String CLIENT_VERSION_CHL_S3500 = "TYYD_Android_2_3_320_480_CHL_S3500_JAVA_2_9_9";// 2012.02.15
	// for 海尔 N610E
	public static final String CLIENT_VERSION_HC_N610E = "TYYD_Android_2_3_320_480_HC_N610E_JAVA_2_9_9";// 2012.02.27
	// for 海尔E617
	public static final String CLIENT_VERSION_HC_E617 = "TYYD_Android_2_3_320_480_HC_E617_JAVA_2_9_9";// 2012.02.27
	// for 博瑞W60
	public static final String CLIENT_VERSION_BR_W60 = "TYYD_Android_2_2_320_480_BR_W60_JAVA_2_9_9";// 2012.02.27
	// for 赛鸿I901
	public static final String CLIENT_VERSION_SAIHONG_I901 = "TYYD_Android_2_2_320_480_SH_I901_JAVA_2_9_9";// 2012.02.29
	// 佳斯特 J868
	public static final String CLIENT_VERSION_JST_J868 = "TYYD_Android_2_2_320_480_JST_J868_JAVA_2_9_9";// 2012.03.05
	// 海尔N620E
	public static final String CLIENT_VERSION_HE_N620E = "TYYD_Android_2_3_320_480_HE_N620E_JAVA_2_9_9";// 2012.03.05
	// 35Phone—Q3510
	public static final String CLIENT_VERSION_SW_Q3510 = "TYYD_Android_2_3_320_480_SW_Q3510_JAVA_2_9_9";// 2012.03.08
	// 摩托罗拉 XT553
	public static final String CLIENT_VERSION_MOT_XT553 = "TYYD_Android_2_3_320_480_MOT_XT553_JAVA_2_9_9";// 2012.03.08
	// 世纪星宇S11
	public static final String CLIENT_VERSION_SJX_S11 = "TYYD_Android_2_3_320_480_SJX_S11_JAVA_2_9_9";// 2012.03.14
	// 联想—A560E
	public static final String CLIENT_VERSION_LNV_A560E = "TYYD_Android_2_3_320_480_LNV_A560E_JAVA_2_9_9";// 2012.03.15
	// 中兴N855
	public static final String CLIENT_VERSION_ZTE_N855 = "TYYD_Android_2_3_320_480_ZTE_N855_JAVA_2_9_9";// 2012.03.16
	// 酷派—5210
	public static final String CLIENT_VERSION_YL_5210 = "TYYD_Android_2_3_320_480_YL_5210_JAVA_2_9_9";// 2012.03.16
	// 同威—S8510
	public static final String CLIENT_VERSION_TW_S8510 = "TYYD_Android_2_3_320_480_TW_S8510_JAVA_2_9_9";// 2012.03.16
	// 中兴—V6700
	public static final String CLIENT_VERSION_ZTE_V6700 = "TYYD_Android_2_3_320_480_ZTE_V6700_JAVA_2_9_9";// 2012.03.26
	// 金立C500
	public static final String CLIENT_VERSION_GIO_C500 = "TYYD_Android_2_3_320_480_GIO_C500_JAVA_2_9_9";// 2012.03.29
	// 金立C600
	public static final String CLIENT_VERSION_GIO_C600 = "TYYD_Android_2_3_320_480_GIO_C600_JAVA_2_9_9";// 2012.03.29
	// 华录 S8300
	public static final String CLIENT_VERSION_CHL_S8300 = "TYYD_Android_2_3_320_480_CHL_S8300_JAVA_2_9_9";// 2012.04.06
	// 三星 I919U
	public static final String CLIENT_VERSION_SCH_I919U = "TYYD_Android_2_3_480_800_SCH_I919U_JAVA_2_9_9";// 2012.04.26
	// 酷派——5110
	public static final String CLIENT_VERSION_YL_5110 = "TYYD_Android_2_3_320_480_YL_5110_JAVA_2_9_9";// 2012.04.26
	// 海信——EG870
	public static final String CLIENT_VERSION_HS_EG870 = "TYYD_Android_2_3_320_480_HS_EG870_JAVA_2_9_9";// 2012.04.26
	// 赛鸿I97
	public static final String CLIENT_VERSION_SH_I97 = "TYYD_Android_2_3_320_480_SH_I97_JAVA_2_9_9";// 2012.04.26
	// 华录—S3000B
	public static final String CLIENT_VERSION_CHL_S3000B = "TYYD_Android_2_3_320_480_CHL_S3000B_JAVA_2_9_9";// 2012.04.26
	// 中兴N885D
	public static final String CLIENT_VERSION_ZTE_N855D = "TYYD_Android_2_3_320_480_ZTE_N855D_JAVA_2_9_9";// 2012.05.07
	// 金立C900
	public static final String CLIENT_VERSION_GIO_C900 = "TYYD_Android_2_3_320_480_GIO_C900_JAVA_2_9_9";// 2012.05.07
	// 同威S8310
	public static final String CLIENT_VERSION_TW_S8310 = "TYYD_Android_2_3_320_480_TW_S8310_JAVA_2_9_9";// 2012.05.07
	// 和信N739
	public static final String CLIENT_VERSION_HX_N739 = "TYYD_Android_2_3_320_480_HX_N739_JAVA_2_9_9";// 2012.05.07
	// 三星I659
	public static final String CLIENT_VERSION_SCH_I659 = "TYYD_Android_2_3_320_480_SCH_I659_JAVA_2_9_9";// 2012.05.14
	// 优思S6000
	public static final String CLIENT_VERSION_XD_S6000 = "TYYD_Android_2_3_320_480_XD_S6000_JAVA_2_9_9";// 2012.05.16
	// 同威S8800
	public static final String CLIENT_VERSION_TW_S8800 = "TYYD_Android_2_3_320_480_TW_S8800_JAVA_2_9_9";// 2012.05.16
	// 蓝天S800D
	public static final String CLIENT_VERSION_TLT_S800D = "TYYD_Android_2_3_320_480_TLT_S800D_JAVA_2_9_9";// 2012.05.22
	// 天迈D99S
	public static final String CLIENT_VERSION_TM_D99S = "TYYD_Android_2_3_320_480_TM_D99S_JAVA_2_9_9";// 2012.05.23
	// 百灵威 VE509
	public static final String CLIENT_VERSION_BLW_VE509 = "TYYD_Android_2_3_320_480_BLW_VE509_JAVA_2_9_9";// 2012.05.28
	// 泰丰C800
	public static final String CLIENT_VERSION_TF_C800 = "TYYD_Android_2_3_320_480_TF_C800_JAVA_2_9_9";// 2012.06.04
	// 易丰E6A
	public static final String CLIENT_VERSION_YFZ_E6A = "TYYD_Android_2_3_320_480_YFZ_E6A_JAVA_2_9_9";// 2012.06.04
	// TCL C990+
	public static final String CLIENT_VERSION_TCL_C990P = "TYYD_Android_2_3_320_480_TCL_C990p_JAVA_2_9_9";// 2012.06.08
	// 天语  E619
	public static final String CLIENT_VERSION_TY_E619 = "TYYD_Android_2_3_320_480_TY_E619_JAVA_2_9_9";// 2012.06.18
	// 比酷 X903
	public static final String CLIENT_VERSION_BK_X903 = "TYYD_Android_2_3_320_480_BK_X903_JAVA_2_9_9";// 2012.06.26
	// 金派EG800
	public static final String CLIENT_VERSION_SM_EG800 = "TYYD_Android_2_3_320_480_SM_EG800_JAVA_2_9_9";// 2012.07.23
	// 斐讯K210V
	public static final String CLIENT_VERSION_PXX_K210V = "TYYD_Android_2_3_320_480_PXX_K210V_JAVA_2_9_9";// 2012.08.01
	// 中兴N855D+
	public static final String CLIENT_VERSION_ZTE_N855DP = "TYYD_Android_2_3_320_480_ZTE_N855Dp_JAVA_2_9_9";// 2012.08.21
	// 飓畅A107
	public static final String CLIENT_VERSION_JCT_A107 = "TYYD_Android_2_3_320_480_JCT_A107_JAVA_2_9_9";// 2012.08.23
	// 华为C8685D
	public static final String CLIENT_VERSION_HW_C8685D = "TYYD_Android_2_3_320_480_HW_C8685D_JAVA_2_9_9";// 2012.09.06
	// 海信E830
	public static final String CLIENT_VERSION_HS_E830 = "TYYD_Android_2_3_320_480_HS_E830_JAVA_2_9_9";// 2012.10.31
	// 语信E66
	public static final String CLIENT_VERSION_YX_E66 = "TYYD_Android_2_3_320_480_YX_E66_JAVA_2_9_9";// 2012.11.01
	// 蓝天 S600+
	public static final String CLIENT_VERSION_TLT_S600P = "TYYD_Android_2_3_320_480_TLT_S600P_JAVA_2_9_9";// 2012.11.05
	// 酷派5210S
	public static final String CLIENT_VERSION_YL_5210S = "TYYD_Android_2_3_320_480_YL_5210S_JAVA_2_9_9";// 2012.11.30
	// TCL D510
	public static final String CLIENT_VERSION_TCL_D510 = "TYYD_Android_2_3_320_480_TCL_D510_JAVA_2_9_9";// 2012.12.03
	// 广信 EF78
	public static final String CLIENT_VERSION_GX_EF78 = "TYYD_Android_2_3_320_480_GX_EF78_JAVA_2_9_9";// 2012.12.04
	// lephone 1800
	public static final String CLIENT_VERSION_BLF_1800 = "TYYD_Android_2_3_320_480_BLF_1800_JAVA_2_9_9";// 2012.12.07
	// 创维 EG6188
	public static final String CLIENT_VERSION_CW_EG6188 = "TYYD_Android_2_2_320_480_CW_EG6188_JAVA_2_9_9";// 2012.12.10
	// 佳斯特 J699
	public static final String CLIENT_VERSION_JST_J699 = "TYYD_Android_2_3_320_480_JST_J699_JAVA_2_9_9";// 2012.12.10
	// 美翼E86
	public static final String CLIENT_VERSION_MYJ_E86 = "TYYD_Android_2_3_320_480_MYJ_E86_JAVA_2_9_9";// 2012.12.10
	// 博瑞-W68
	public static final String CLIENT_VERSION_BR_W68 = "TYYD_Android_2_3_320_480_BR_W68_JAVA_2_9_9";// 2012.12.10
	// Lephone 2800
	public static final String CLIENT_VERSION_BLF_2800 = "TYYD_Android_2_3_320_480_BLF_2800_JAVA_2_9_9";// 2012.12.10
	// 三美奇 EA6800
	public static final String CLIENT_VERSION_SMQ_EA6800 = "TYYD_Android_2_2_320_480_SMQ_EA6800_JAVA_2_9_9";// 2012.12.10
	// 同威 X1
	public static final String CLIENT_VERSION_TW_X1 = "TYYD_Android_2_1_320_480_TW_X1_JAVA_2_9_9";// 2012.12.11
	// 广信 EF68
	public static final String CLIENT_VERSION_GX_EF68 = "TYYD_Android_2_3_320_480_GX_EF68_JAVA_2_9_9";// 2012.12.11
	// 飓畅 JC-A109
	public static final String CLIENT_VERSION_JCT_A109 = "TYYD_Android_2_3_320_480_JCT_A109_JAVA_2_9_9";// 2012.12.12
	// 易丰 8s
	public static final String CLIENT_VERSION_YFZ_8S = "TYYD_Android_2_3_320_480_YFZ_8S_JAVA_2_9_9";// 2012.12.26
	// 天迈 G19
	public static final String CLIENT_VERSION_TM_G19 = "TYYD_Android_2_3_320_480_TM_G19_JAVA_2_9_9";// 2012.12.26

	// *******************************************************************************************
	// ***************************************240*320版本号***************************************
	// *******************************************************************************************
	// MOTO XT301
	// public static final String CLIENT_VERSION_XT301 =
	// "TYYD_Android_2_1_240_320_XT301_JAVA_1_1_0";
	// public static final String CLIENT_VERSION_XT301 =
	// "TYYD_Android_2_1_240_320_XT301_JAVA_1_1_1";//20101202
	// public static final String CLIENT_VERSION_XT301 =
	// "TYYD_Android_2_1_240_320_XT301_JAVA_1_1_2";//20101209
	// public static final String CLIENT_VERSION_XT301 =
	// "TYYD_Android_2_1_240_320_XT301_JAVA_1_2_0";//2011.01.04
	// public static final String CLIENT_VERSION_XT301 =
	// "TYYD_Android_2_1_240_320_XT301_JAVA_1_2_1";//2011.01.18
	// public static final String CLIENT_VERSION_XT301 =
	// "TYYD_Android_2_1_240_320_XT301_JAVA_1_2_2";//2011.01.26
	// public static final String CLIENT_VERSION_XT301 =
	// "TYYD_Android_2_1_240_320_XT301_JAVA_1_3_0";//2011.03.25
	// public static final String CLIENT_VERSION_XT301 =
	// "TYYD_Android_2_1_240_320_MOT_XT301_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_XT301 = "TYYD_Android_2_1_240_320_MOT_XT301_JAVA_2_9_9";// 2011.08.02
	// for ZTE N600
	// public static final String CLIENT_VERSION_N600 =
	// "TYYD_Android_2_1_240_320_N600_JAVA_2_9_9";
	// public static final String CLIENT_VERSION_N600 =
	// "TYYD_Android_2_1_240_320_N600_JAVA_1_1_0";//2011.01.06
	// public static final String CLIENT_VERSION_N600 =
	// "TYYD_Android_2_1_240_320_N600_JAVA_1_3_0";//2011.04.01
	// public static final String CLIENT_VERSION_N600 =
	// "TYYD_Android_2_1_240_320_ZTE_N600_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_N600 = "TYYD_Android_2_1_240_320_ZTE_N600_JAVA_2_9_9";// 2011.08.02
	// for HUAWEI C8500
	// public static final String CLIENT_VERSION_C8500 =
	// "TYYD_Android_2_1_240_320_C8500_JAVA_2_9_9";
	// public static final String CLIENT_VERSION_C8500 =
	// "TYYD_Android_2_1_240_320_C8500_JAVA_1_0_1";//20101223
	// public static final String CLIENT_VERSION_C8500 =
	// "TYYD_Android_2_1_240_320_C8500_JAVA_1_1_0";//20110104
	// public static final String CLIENT_VERSION_C8500 =
	// "TYYD_Android_2_1_240_320_C8500_JAVA_1_1_1";//2011.01.26
	// public static final String CLIENT_VERSION_C8500 =
	// "TYYD_Android_2_1_240_320_C8500_JAVA_1_3_0";//2011.03.18
	// public static final String CLIENT_VERSION_C8500 =
	// "TYYD_Android_2_1_240_320_HW_C8500_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_HW_C8500 = "TYYD_Android_2_1_240_320_HW_C8500_JAVA_2_9_9";// 2011.08.02
	// for 宇龙 E230A
	// public static final String CLIENT_VERSION_E230A =
	// "TYYD_Android_2_1_240_320_E230A_JAVA_2_9_9";
	// public static final String CLIENT_VERSION_E230A =
	// "TYYD_Android_2_1_240_320_E230A_JAVA_1_3_0";//2011.04.11
	// public static final String CLIENT_VERSION_E230A =
	// "TYYD_Android_2_1_240_320_YL_E230A_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_E230A = "TYYD_Android_2_1_240_320_YL_E230A_JAVA_2_9_9";// 2011.08.02
	// for ZTE N606
	// public static final String CLIENT_VERSION_N606 =
	// "TYYD_Android_2_2_240_320_N606_JAVA_2_9_9";
	// public static final String CLIENT_VERSION_N606 =
	// "TYYD_Android_2_2_240_320_N606_JAVA_1_1_0";//20110104
	// public static final String CLIENT_VERSION_N606 =
	// "TYYD_Android_2_2_240_320_N606_JAVA_1_1_1";//20110118
	// public static final String CLIENT_VERSION_N606 =
	// "TYYD_Android_2_2_240_320_N606_JAVA_1_1_2";//2011.01.26
	// public static final String CLIENT_VERSION_N606 =
	// "TYYD_Android_2_2_240_320_N606_JAVA_1_3_0";//2011.05.13
	// public static final String CLIENT_VERSION_N606 =
	// "TYYD_Android_2_2_240_320_ZTE_N606_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_N606 = "TYYD_Android_2_2_240_320_ZTE_N606_JAVA_2_9_9";// 2011.08.02
	// for SAMSUNG I559
	// public static final String CLIENT_VERSION_I559 =
	// "TYYD_Android_2_2_240_320_I559_JAVA_2_9_9";
	// public static final String CLIENT_VERSION_I559 =
	// "TYYD_Android_2_2_240_320_SCH_I559_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_I559 = "TYYD_Android_2_2_240_320_SCH_I559_JAVA_2_9_9";// 2011.08.02
	// for EPHONE A9
	// public static final String CLIENT_VERSION_EPHONE_A9 =
	// "TYYD_Android_2_1_240_320_A9_JAVA_2_9_9";
	// public static final String CLIENT_VERSION_EPHONE_A9 =
	// "TYYD_Android_2_1_240_320_YFZ_A9_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_EPHONE_A9 = "TYYD_Android_2_1_240_320_YFZ_A9_JAVA_2_9_9";// 2011.08.02
	// for ZTE N600+ 2.1
	// public static final String CLIENT_VERSION_N600_PLUS =
	// "TYYD_Android_2_1_240_320_N600p_JAVA_2_9_9";//20101209
	// public static final String CLIENT_VERSION_N600_PLUS =
	// "TYYD_Android_2_1_240_320_N600p_JAVA_1_1_0";//20110104
	// public static final String CLIENT_VERSION_N600_PLUS_7 =
	// "TYYD_Android_2_1_240_320_N600p_JAVA_1_1_1";//20110118
	// public static final String CLIENT_VERSION_N600_PLUS_7 =
	// "TYYD_Android_2_1_240_320_N600p_JAVA_1_1_2";//2011.01.26
	// public static final String CLIENT_VERSION_N600_PLUS_7 =
	// "TYYD_Android_2_1_240_320_N600p_JAVA_1_3_0";//2011.04.11
	// public static final String CLIENT_VERSION_N600_PLUS_7 =
	// "TYYD_Android_2_1_240_320_ZTE_N600p_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_N600_PLUS_7 = "TYYD_Android_2_1_240_320_ZTE_N600p_JAVA_2_9_9";// 2011.08.02
	// for ZTE N600+ 2.2
	public static final String CLIENT_VERSION_N600_PLUS_8 = "TYYD_Android_2_2_240_320_N600p_JAVA_2_9_9";// 20110118
	// for 和信 N100
	// public static final String CLIENT_VERSION_HESENS_N100 =
	// "TYYD_Android_2_1_240_320_N100_JAVA_2_9_9";//20101215
	// public static final String CLIENT_VERSION_HESENS_N100 =
	// "TYYD_Android_2_1_240_320_HX_N100_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_HESENS_N100 = "TYYD_Android_2_1_240_320_HX_N100_JAVA_2_9_9";// 2011.08.02
	// for HTC BEE
	// public static final String CLIENT_VERSION_HTC_BEE =
	// "TYYD_Android_2_2_240_320_BEE_JAVA_2_9_9";//20101221
	// public static final String CLIENT_VERSION_HTC_BEE =
	// "TYYD_Android_2_2_240_320_HTC_BEE_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_HTC_BEE = "TYYD_Android_2_2_240_320_HTC_BEE_JAVA_2_9_9";// 2011.08.02
	// for 海信 E89
	// public static final String CLIENT_VERSION_HS_E89 =
	// "TYYD_Android_2_1_240_320_E89_JAVA_2_9_9";//20101221
	// public static final String CLIENT_VERSION_HS_E89 =
	// "TYYD_Android_2_1_240_320_E89_JAVA_1_0_1";//2011.01.18
	// public static final String CLIENT_VERSION_HS_E89 =
	// "TYYD_Android_2_1_240_320_E89_JAVA_1_0_2";//2011.01.26
	// public static final String CLIENT_VERSION_HS_E89 =
	// "TYYD_Android_2_1_240_320_HS_E89_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_HS_E89 = "TYYD_Android_2_1_240_320_HS_E89_JAVA_2_9_9";// 2011.07.12
	// for ZTE X920
	// public static final String CLIENT_VERSION_ZTE_X920 =
	// "TYYD_Android_2_1_240_320_X920_JAVA_2_9_9";//20110105
	// public static final String CLIENT_VERSION_ZTE_X920 =
	// "TYYD_Android_2_1_240_320_ZTE_X920_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_ZTE_X920 = "TYYD_Android_2_1_240_320_ZTE_X920_JAVA_2_9_9";// 2011.08.02
	// for TE(天元) TE600
	// public static final String CLIENT_VERSION_TE_TE600 =
	// "TYYD_Android_2_1_240_320_TE600_JAVA_2_9_9";//2011.02.10
	// public static final String CLIENT_VERSION_TE_TE600 =
	// "TYYD_Android_2_1_240_320_YCT_TE600_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_TE_TE600 = "TYYD_Android_2_1_240_320_YCT_TE600_JAVA_2_9_9";// 2011.08.02
	// for 华录 S800
	// public static final String CLIENT_VERSION_HUALU_S800 =
	// "TYYD_Android_2_1_240_320_S800_JAVA_2_9_9";//2011.02.10
	// public static final String CLIENT_VERSION_HUALU_S800 =
	// "TYYD_Android_2_1_240_320_CHL_S800_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_HUALU_S800 = "TYYD_Android_2_1_240_320_CHL_S800_JAVA_2_9_9";// 2011.08.02
	// for ZTE N700
	// public static final String CLIENT_VERSION_ZTE_N700 =
	// "TYYD_Android_2_2_240_400_N700_JAVA_2_9_9";//2011.02.24
	// public static final String CLIENT_VERSION_ZTE_N700 =
	// "TYYD_Android_2_2_240_400_ZTE_N700_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_ZTE_N700 = "TYYD_Android_2_2_240_400_ZTE_N700_JAVA_2_9_9";// 2011.08.02
	// for 齐尔 A4
	// public static final String CLIENT_VERSION_CHAR_A4 =
	// "TYYD_Android_2_1_240_320_A4_JAVA_2_9_9";//2011.02.10
	// public static final String CLIENT_VERSION_CHAR_A4 =
	// "TYYD_Android_2_1_240_320_QL_A4_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_CHAR_A4 = "TYYD_Android_2_1_240_320_QL_A4_JAVA_2_9_9";// 2011.08.02
	// for 同威 X2011
	// public static final String CLIENT_VERSION_TONGWEI_X2011 =
	// "TYYD_Android_2_1_240_320_X2011_JAVA_2_9_9";//2011.02.24
	// public static final String CLIENT_VERSION_TONGWEI_X2011 =
	// "TYYD_Android_2_1_240_320_TW_X2011_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_TONGWEI_X2011 = "TYYD_Android_2_1_240_320_TW_X2011_JAVA_2_9_9";// 2011.08.02
	// for EPHONE(易丰) A8
	// public static final String CLIENT_VERSION_EPHONE_A8 =
	// "TYYD_Android_2_1_240_320_A8_JAVA_2_9_9";//2011.03.23
	// public static final String CLIENT_VERSION_EPHONE_A8 =
	// "TYYD_Android_2_1_240_320_YFZ_A8_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_EPHONE_A8 = "TYYD_Android_2_1_240_320_YFZ_A8_JAVA_2_9_9";// 2011.08.02
	// for FLY(展翼) N6
	// public static final String CLIENT_VERSION_FLY_N6 =
	// "TYYD_Android_2_1_240_320_N6_JAVA_2_9_9";//2011.03.30
	// public static final String CLIENT_VERSION_FLY_N6 =
	// "TYYD_Android_2_1_240_320_ZY_N6_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_FLY_N6 = "TYYD_Android_2_1_240_320_ZY_N6_JAVA_2_9_9";// 2011.08.02
	// for 广信E900
	// public static final String CLIENT_VERSION_GUANGXIN_TE600 =
	// "TYYD_Android_2_1_240_320_E900_JAVA_2_9_9";//2011.04.06
	public static final String CLIENT_VERSION_GUANGXIN_TE600 = "TYYD_Android_2_1_240_320_GX_E900_JAVA_2_9_9";// 2011.08.26
	// for 天语E610
	// public static final String CLIENT_VERSION_TIANYU_E610 =
	// "TYYD_Android_2_2_240_400_E610_JAVA_2_9_9";//2011.04.19
	// public static final String CLIENT_VERSION_TIANYU_E610 =
	// "TYYD_Android_2_2_240_400_TY_E610_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_TIANYU_E610 = "TYYD_Android_2_2_240_400_TY_E610_JAVA_2_9_9";// 2011.08.02
	// for 海信E839
	// public static final String CLIENT_VERSION_HS_E839 =
	// "TYYD_Android_2_1_240_320_E839_JAVA_2_9_9";//2011.04.26
	// public static final String CLIENT_VERSION_HS_E839 =
	// "TYYD_Android_2_1_240_320_HS_E839_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_HS_E839 = "TYYD_Android_2_1_240_320_HS_E839_JAVA_2_9_9";// 2011.08.02
	// for 波导AE710
	// public static final String CLIENT_VERSION_BIRD_AE710 =
	// "TYYD_Android_2_1_240_320_AE710_JAVA_2_9_9";//2011.05.04
	// public static final String CLIENT_VERSION_BIRD_AE710 =
	// "TYYD_Android_2_1_240_320_BD_AE710_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_BIRD_AE710 = "TYYD_Android_2_1_240_320_BD_AE710_JAVA_2_9_9";// 2011.08.02
	// for 创维PE89
	// public static final String CLIENT_VERSION_SKYWORTH_PE89 =
	// "TYYD_Android_2_1_240_320_PE89_JAVA_2_9_9";//2011.05.04
	// public static final String CLIENT_VERSION_SKYWORTH_PE89 =
	// "TYYD_Android_2_1_240_320_CW_PE89_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_SKYWORTH_PE89 = "TYYD_Android_2_1_240_320_CW_PE89_JAVA_2_9_9";// 2011.08.02
	// for 海信E87
	// public static final String CLIENT_VERSION_HS_E87 =
	// "TYYD_Android_2_1_240_320_E87_JAVA_2_9_9";//2011.05.04
	// public static final String CLIENT_VERSION_HS_E87 =
	// "TYYD_Android_2_1_240_320_HS_E87_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_HS_E87 = "TYYD_Android_2_1_240_320_HS_E87_JAVA_2_9_9";// 2011.08.02
	// for 华录ES2000
	// public static final String CLIENT_VERSION_HUALU_ES2000 =
	// "TYYD_Android_2_2_240_400_ES2000_JAVA_2_9_9";//2011.05.11
	// public static final String CLIENT_VERSION_HUALU_ES2000 =
	// "TYYD_Android_2_2_240_400_CHL_ES2000_JAVA_2_0_0";//2011.07.25
	public static final String CLIENT_VERSION_HUALU_ES2000 = "TYYD_Android_2_2_240_400_CHL_ES2000_JAVA_2_9_9";// 2011.08.02
	// for 蓝天 S100
	// public static final String CLIENT_VERSION_LANTIAN_S100 =
	// "TYYD_Android_2_1_240_320_S100_JAVA_2_9_9";//2011.06.15
	// public static final String CLIENT_VERSION_LANTIAN_S100 =
	// "TYYD_Android_2_1_240_320_TLT_S100_JAVA_2_0_0";//2011.07.25
	public static final String CLIENT_VERSION_LANTIAN_S100 = "TYYD_Android_2_1_240_320_TLT_S100_JAVA_2_9_9";// 2011.08.02
	// for 金派 N600
	// public static final String CLIENT_VERSION_KINGPAD_N600 =
	// "TYYD_Android_2_1_240_320_KINGPAD_N600_JAVA_2_9_9";//2011.06.24
	// public static final String CLIENT_VERSION_KINGPAD_N600 =
	// "TYYD_Android_2_1_240_320_SM_N600_JAVA_2_0_0";//2011.07.25
	public static final String CLIENT_VERSION_KINGPAD_N600 = "TYYD_Android_2_1_240_320_SM_N600_JAVA_2_9_9";// 2011.08.02
	// for 华录 S850
	// public static final String CLIENT_VERSION_CHL_S850 =
	// "TYYD_Android_2_1_240_320_CHL_S850_JAVA_2_0_0";//2011.07.18
	public static final String CLIENT_VERSION_CHL_S850 = "TYYD_Android_2_1_240_320_CHL_S850_JAVA_2_9_9";// 2011.08.02
	// for HUAWEI C8500SR
	// public static final String CLIENT_VERSION_HUAWEI_C8500SR =
	// "TYYD_Android_2_2_240_320_HW_C8500S_JAVA_2_0_0";//2011.07.28
	public static final String CLIENT_VERSION_HUAWEI_C8500SR = "TYYD_Android_2_2_240_320_HW_C8500S_JAVA_2_9_9";// 2011.08.02
	// 博瑞-S06
	public static final String CLIENT_VERSION_BROR_S06 = "TYYD_Android_2_1_240_320_BR_S6_JAVA_2_9_9";// 2011.08.10
	// 语信-E60
	public static final String CLIENT_VERSION_YUXIN_E60 = "TYYD_Android_2_1_240_320_YX_E60_JAVA_2_9_9";// 2011.08.15
	// 中兴 N601
	public static final String CLIENT_VERSION_ZTE_N601 = "TYYD_Android_2_2_240_320_ZTE_N601_JAVA_2_9_9";// 2011.08.19
	// for 创维ES2000
	public static final String CLIENT_VERSION_SKYWORTH_ES2000 = "TYYD_Android_2_2_240_320_CW_ES2000_JAVA_2_9_9";// 2011.08.26
	// for 展翼N5：
	public static final String CLIENT_VERSION_FLY_N5 = "TYYD_Android_2_1_240_320_ZY_N5_JAVA_2_9_9";// 2011.09.15
	// for 华为C8550
	public static final String CLIENT_VERSION_HW_C8550 = "TYYD_Android_2_2_240_320_HW_C8550_JAVA_2_9_9";// 2011.12.30
	// for SAMSUNG I339
	public static final String CLIENT_VERSION_SAMSUNG_I339 = "TYYD_Android_2_3_240_320_SCH_I339_JAVA_2_9_9";// 2012.01.31
	// for 世纪星宇S7
	public static final String CLIENT_VERSION_SJX_S7 = "TYYD_Android_2_1_240_320_SJX_S7_JAVA_2_9_9";// 2012.02.29
	// for 三星I519
	public static final String CLIENT_VERSION_SCH_I519 = "TYYD_Android_2_3_240_320_SCH_I519_JAVA_2_9_9";// 2012.03.15
	// for 三星I509U
	public static final String CLIENT_VERSION_SCH_I509U = "TYYD_Android_2_3_240_320_SCH_I509U_JAVA_2_9_9";// 2012.03.20
	// for 三星I639
	public static final String CLIENT_VERSION_SCH_I639 = "TYYD_Android_2_3_240_320_SCH_I639_JAVA_2_9_9";// 2012.03.20
	// for 百灵威朗—VE200
	public static final String CLIENT_VERSION_BLW_VE200 = "TYYD_Android_2_2_240_320_BLW_VE200_JAVA_2_9_9";// 2012.03.26
	// for 酷派5010
	public static final String CLIENT_VERSION_YL_5010 = "TYYD_Android_2_3_240_320_YL_5010_JAVA_2_9_9";// 2012.05.07
	// for 百利丰2600
	public static final String CLIENT_VERSION_BLF_2600 = "TYYD_Android_2_3_240_320_BLF_2600_JAVA_2_9_9";// 2012.07.02
	// for 百利丰1600
	public static final String CLIENT_VERSION_BLF_1600 = "TYYD_Android_2_3_240_320_BLF_1600_JAVA_2_9_9";// 2012.07.02
	// for 广信 EF55
	public static final String CLIENT_VERSION_GX_EF55 = "TYYD_Android_2_3_240_400_GX_EF55_JAVA_2_9_9";// 2012.12.04
	// for 百灵威 VE360
	public static final String CLIENT_VERSION_BLW_VE360 = "TYYD_Android_2_2_240_320_BLW_VE360_JAVA_2_9_9";// 2012.12.06
	// for 赛鸿 I60
	public static final String CLIENT_VERSION_SH_I60 = "TYYD_Android_2_3_240_320_SH_I60_JAVA_2_9_9";// 2012.12.10

	// *********************************************************************************************
	// ****************************************其它分辨率版本号**************************************
	// *********************************************************************************************
	// public static final String CLIENT_VERSION_LIFEPAD =
	// "TYYD_Android_2_0_800_600_lifepad_1_1";
	// public static final String CLIENT_VERSION_LIFEPAD =
	// "TYYD_Android_2_1_800_600_lifepad_JAVA_1_1_0";
	// public static final String CLIENT_VERSION_LIFEPAD =
	// "TYYD_Android_2_0_800_600_lifepad_JAVA_1_2_0";//20110104
	// public static final String CLIENT_VERSION_LIFEPAD =
	// "TYYD_Android_2_0_800_600_lifepad_JAVA_1_2_1";//2011.01.26
	// public static final String CLIENT_VERSION_LIFEPAD =
	// "TYYD_Android_2_0_800_600_WST_lifepad_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_LIFEPAD = "TYYD_Android_2_0_800_600_WST_lifepad_JAVA_2_9_9";// 2011.08.02
	// for AUSCLOUD V8
	// public static final String CLIENT_VERSION_AUSCLOUD_V8 =
	// "TYYD_Android_2_2_1024_600_AUSCLOUDV8_JAVA_2_9_9";//2011.03.16
	// public static final String CLIENT_VERSION_AUSCLOUD_V8 =
	// "TYYD_Android_2_2_1024_600_AUSCLOUDV8_JAVA_2_0_0";//2011.07.12
	public static final String CLIENT_VERSION_AUSCLOUD_V8 = "TYYD_Android_2_2_1024_600_AUSCLOUDV8_JAVA_2_9_9";// 2011.08.02
	// for LION PAD I600
	public static final String CLIENT_VERSION_LION_PAD_I600 = "TYYD_Android_2_2_600_1024_I600_JAVA_2_9_9";// 2011.05.25
	// for LION PAD A1 鲁科特 LKT-A1
	// public static final String CLIENT_VERSION_LION_PAD_A1 = "TYYD_Android_2_2_600_1024_A1_JAVA_2_9_9";//2011.06.08
	// public static final String CLIENT_VERSION_LION_PAD_A1 = "TYYD_Android_2_2_600_1024_LKT_A1_JAVA_2_0_0";//2011.07.28
	public static final String CLIENT_VERSION_LION_PAD_A1 = "TYYD_Android_2_2_600_1024_LKT_A1_JAVA_2_9_9";// 2011.08.02
	// for 万利达 T8
	// public static final String CLIENT_VERSION_MALATA_T8 =
	// "TYYD_Android_2_2_1024_768_WLD_T8_JAVA_2_0_0";//2011.07.19
	public static final String CLIENT_VERSION_MALATA_T8 = "TYYD_Android_2_2_1024_768_WLD_T8_JAVA_2_9_9";// 2011.08.02
	// for 万事达 A700
	// public static final String CLIENT_VERSION_WST_A700 =
	// "TYYD_Android_2_2_600_1024_WST_A700_JAVA_2_0_0";//2011.07.18
	public static final String CLIENT_VERSION_WST_A700 = "TYYD_Android_2_2_600_1024_WST_A700_JAVA_2_9_9";// 2011.08.02
	// for SAMSUNG P739
	public static final String CLIENT_VERSION_SAMSUNG_P739 = "TYYD_Android_3_1_1280_800_SCH_P739_JAVA_2_9_9";// 2011.08.02
	// for E人E本 T3
	public static final String CLIENT_VERSION_EBEN_T3 = "TYYD_Android_2_2_600_800_EB_T3_JAVA_2_9_9";// 2011.08.12
	// for UT斯达康P200E
	public static final String CLIENT_VERSION_UT_P200E = "TYYD_Android_2_2_768_1024_UT_P200E_JAVA_2_9_9";// 2011.09.15
	//for 三星I889
	public static final String CLIENT_VERSION_SCH_I889 = "TYYD_Android_2_3_800_1280_SCH_I889_JAVA_2_9_9";//2011.12.06
	//for 三星I9520
	public static final String CLIENT_VERSION_SCH_I9520 = "TYYD_Android_4_0_720_1184_SCH_I9520_JAVA_2_9_9";//2012.02.27
	//for 摩托罗拉 XT889
	public static final String CLIENT_VERSION_MOT_XT889 = "TYYD_Android_4_0_540_888_MOT_XT889_JAVA_2_9_9";//2012.03.08
	//for HTC—T328d
	public static final String CLIENT_VERSION_HTC_T328D = "TYYD_Android_4_0_480_728_HTC_T328D_JAVA_2_9_9";//2012.03.09
	//for 三星I939
	public static final String CLIENT_VERSION_SCH_I939 = "TYYD_Android_4_0_720_1280_SCH_I939_JAVA_2_9_9";//2012.04.23
	//for HTC—X720d
	public static final String CLIENT_VERSION_HTC_X720D = "TYYD_Android_4_0_720_1184_HTC_X720D_JAVA_2_9_9";//2012.03.09
	//for 斐讯 FPAD
	public static final String CLIENT_VERSION_PXX_FPAD = "TYYD_Android_2_3_600_1024_PXX_FPAD_JAVA_2_9_9";//2012.06.08
	//for 索尼LT25c
	public static final String CLIENT_VERSION_SND_LT25C = "TYYD_Android_4_0_720_1184_SND_LT25C_JAVA_2_9_9";//2012.07.05
	//for MOT-XT785
	public static final String CLIENT_VERSION_MOT_XT785 = "TYYD_Android_4_0_540_894_MOT_XT785_JAVA_2_9_9";//2012.07.17
	//for HTC-T528d
	public static final String CLIENT_VERSION_HTC_T528D = "TYYD_Android_4_0_480_728_HTC_T528D_JAVA_2_9_9";//2012.07.23
	//for 酷派9960
	public static final String CLIENT_VERSION_YL_9960 = "TYYD_Android_4_0_720_1280_YL_9960_JAVA_2_9_9";//2012.07.30
	//for 联想S870e
	public static final String CLIENT_VERSION_LNV_S870E = "TYYD_Android_4_0_540_888_LNV_S870E_JAVA_2_9_9";//2012.07.30
	//for 三星I959
	public static final String CLIENT_VERSION_SCH_I959 = "TYYD_Android_4_1_720_1280_SCH_I959_JAVA_2_9_9";//2012.08.09
	//for MOT-XT788
	public static final String CLIENT_VERSION_MOT_XT788 = "TYYD_Android_4_0_540_894_MOT_XT788_JAVA_2_9_9";//2012.08.09
	//for 金立C800
	public static final String CLIENT_VERSION_GIO_C800 = "TYYD_Android_4_0_540_960_GIO_C800_JAVA_2_9_9";//2012.08.10
	//for 夏普SH630T
	public static final String CLIENT_VERSION_SHP_SH630T = "TYYD_Android_4_0_540_888_SHP_SH630T_JAVA_2_9_9";//2012.08.14
	//for 三星I939D
	public static final String CLIENT_VERSION_SCH_I939D = "TYYD_Android_4_1_720_1280_SCH_I939D_JAVA_2_9_9";//2012.08.15
	//for 海信 EG950
	public static final String CLIENT_VERSION_HS_EG950 = "TYYD_Android_4_0_540_960_HS_EG950_JAVA_2_9_9";//2012.08.30
	//for 三星N719
	public static final String CLIENT_VERSION_SCH_N719 = "TYYD_Android_4_1_720_1280_SCH_N719_JAVA_2_9_9";//2012.09.11
	//for 海尔E80
	public static final String CLIENT_VERSION_HE_E80 = "TYYD_Android_4_0_540_960_HC_E80_JAVA_2_9_9";//2012.10.11
	//for 中兴N880G
	public static final String CLIENT_VERSION_ZTE_N880G = "TYYD_Android_4_0_480_782_ZTE_N880G_JAVA_2_9_9";//2012.10.19
	//for 酷派5876
	public static final String CLIENT_VERSION_YL_5876 = "TYYD_Android_4_0_540_960_YL_5876_JAVA_2_9_9";//2012.11.01
	//for 中兴N983
	public static final String CLIENT_VERSION_ZTE_N983 = "TYYD_Android_4_0_720_1280_ZTE_N983_JAVA_2_9_9";//2012.11.01
	//for 优思U1203
	public static final String CLIENT_VERSION_XD_U1203 = "TYYD_Android_4_0_540_960_XD_U1203_JAVA_2_9_9";//2012.11.06
	//for 华为 D2
	public static final String CLIENT_VERSION_HW_D2 = "TYYD_Android_4_1_1080_1785_HW_D2_JAVA_2_9_9";//2012.11.08
	//for TCL D706
	public static final String CLIENT_VERSION_TCL_D706 = "TYYD_Android_4_0_540_960_TCL_D706_JAVA_2_9_9";//2012.12.03
	//for 酷派 5890
	public static final String CLIENT_VERSION_YL_5890 = "TYYD_Android_4_1_540_960_YL_5890_JAVA_2_9_9";//2012.12.04
	//for 蓝天 S939D
	public static final String CLIENT_VERSION_TLT_S939D = "TYYD_Android_4_0_540_960_TLT_S939D_JAVA_2_9_9";//2012.12.05
	//for 百灵威 E2013
	public static final String CLIENT_VERSION_BLW_E2013 = "TYYD_Android_4_0_540_960_BLW_E2013_JAVA_2_9_9";//2012.12.05
	//for 中信 N881F
	public static final String CLIENT_VERSION_ZTE_N881F = "TYYD_Android_4_1_540_960_ZTE_N881F_JAVA_2_9_9";//2012.12.06
	//for 基思瑞 D9C
	public static final String CLIENT_VERSION_JSR_D9C = "TYYD_Android_4_0_540_960_JSR_D9C_JAVA_2_9_9";//2012.12.06
	//for 联想 A820e
	public static final String CLIENT_VERSION_LVN_A820E = "TYYD_Android_4_1_540_960_LVN_A820E_JAVA_2_9_9";//2012.12.06
	//for 酷派 5930
	public static final String CLIENT_VERSION_YL_5930 = "TYYD_Android_4_1_540_960_YL_5930_JAVA_2_9_9";//2012.12.10
	//for TF-C820
	public static final String CLIENT_VERSION_TF_C820 = "TYYD_Android_4_0_540_960_TF_C820_JAVA_2_9_9";//2012.12.14
	//for 酷派 9070
	public static final String CLIENT_VERSION_YL_9070 = "TYYD_Android_4_1_720_1280_YL_9070_JAVA_2_9_9";//2012.12.24
	//for 基思瑞 I6C
	public static final String CLIENT_VERSION_JSR_I6C = "TYYD_Android_4_0_540_960_JSR_I6C_JAVA_2_9_9";//2013.01.11
	//for Sony M35h
	public static final String CLIENT_VERSION_SND_M35H = "TYYD_Android_4_1_720_1280_SND_M35H_JAVA_2_9_9";//2013.01.11

	// 我们自己的
	public static String CLIENT_AGENT;
	public static final String CLIENT_RESOLUTION_720_1280 = "720*1280";
	public static final String CLIENT_RESOLUTION_480_854 = "480*854";
	public static final String CLIENT_RESOLUTION_480_800 = "480*800";
	public static final String CLIENT_RESOLUTION_320_480 = "320*480";
	public static final String CLIENT_RESOLUTION_240_320 = "240*320";
	public static final String CLIENT_RESOLUTION_540_960 = "540*960";
	public static String clientResolution = CLIENT_RESOLUTION_480_854;
	public static final String CLIENT_SECITE = "Kt^&kj%$#k.l;iyu";

	public static final int SDK_VERSION_4 = 4;
	public static final int SDK_VERSION_7 = 7;
	public static final int SDK_VERSION_8 = 8;
	public static final int SDK_VERSION_HONEYCOMB = 11;
	private static int sdkVersion;

	private static Boolean isConnect = null;

	private static Boolean isCtwapLogin = null;

	private static Boolean isVoiceLogin = null;
	private static Boolean isSmsBuyEnabled = null;
	/*begin add by xzz 2012-04-13*/
	//厂商测试开关，true的话表示是提供给三星的,HTC,摩托，否则就提供给基地测试的版本
	public static boolean isSAMSUNGOpen = false;
	//TTS开关，true的话表示是打开TTS,否则就是关闭TTS
	public static boolean isTTSOpen = true;  
	//三星应用商场渠道控制开关，true表示是三星应用商场渠道，false则表示不是
	public static boolean isSamSungAppOpen = false;
	/*end by xzz 2012-04-13*/

	private static String orderModel;
	private static String manufacturer;
	private static final String ORDER_MODEL_COMMON = "COMMON_unknow";
	// 指定的机器型号；三星I909（SCH-i909），三星W899（SCH-W899）, MOTO XT800(XT800), MOTO
	// XT806(XT806), 中兴 R750（ZTE-C R750），宇龙N930（N930）
	private static final String ORDER_MODEL_XT800 = "XT800";
	private static final String ORDER_MODEL_XT806 = "XT806";
	// 三星I909
	public static final String ORDER_MODEL_I909 = "SCH-i909";
	// 三星W899
	public static final String ORDER_MODEL_W899 = "SCH-W899";
	// 易明电子E6
	public static final String ORDER_MODEL_TCC8900 = "Android 2.x for HD601 Board(US)";
	public static final String ORDER_MODEL_XT800PLUS = "XT800+";
	public static final String ORDER_MODEL_ME811 = "ME811";
	//联想S850E 2012.03.29
	public static final String ORDER_MODEL_LNV_S850E = "sichuan";
	public static final String ORDER_MODEL_LNV_S850E_2 = "Lenovo S850e";
	// 摩托XT681 2011.12.30
//	public static final String ORDER_MODEL_MOT_XT681 = "XT681";
	public static final String ORDER_MODEL_MOT_XT681 = "Motorola MOT-XT681";//出厂的XT681的MODEL改了这个
	// 优思  US910 2012.06.14
	public static final String ORDER_MODEL_XD_US910 = "XD-US910"; 
	//优思US920 2012.07.04
	public static final String ORDER_MODEL_XD_US920 = "XD-US920";
	//联想A765E 2012.09.14
	public static final String ORDER_MODEL_LNV_A765E = "Lenovo A765e";
	// 蓝天 S910D 2012.11.07
	public static final String ORDER_MODEL_TLT_S910D = "LT S910D";
	// 新邮通信 E606 2012.12.10
	public static final String ORDER_MODEL_XYT_E606 = "E606";
	// 博瑞世纪 S3 2012.12.10
	public static final String ORDER_MODEL_BR_S3 = "BROR S3";
	// 锋达通 E9 2012.12.10
	public static final String ORDER_MODEL_FDT_E9 = "FDT E9";
	
	//华为C8860E 2011.11.29
	public static final String ORDER_MODEL_HUAWEI_C8860E = "C8860E";	
	public static final String ORDER_MODEL_HUAWEI_C8860E_2 = "HUAWEI C8860E"; //2012.03.07
	// for 小米MI1 2012.02.14
	public static final String ORDER_MODEL_XIAOMI_C1 = "MI-ONE C1"; 
	public static final String ORDER_MODEL_LEPHONE = "3GC101";
	// public static final String ORDER_MODEL_HTC_VIVO = "ADR6350";
	public static final String ORDER_MODEL_HTC_VIVO = "HTC S710d";
	// public static final String ORDER_MODEL_HTC_VIVO_NEW = "S710d";//HTC
	// 最终确认的ROM的版本号
	// 2010.12.29
	public static final String ORDER_MODEL_HTC_EVO = "PC36100";
	// 2011.01.24
	public static final String ORDER_MODEL_COOLPAD_N916 = "N916";
	// 宇龙N930
	public static final String ORDER_MODEL_COOLPAD_N930 = "N930";
	// 2011.02.13
	public static final String ORDER_MODEL_HTC_INCREDIBLE = "ADR6300";
	// 2011.02.16
	public static final String ORDER_MODEL_ZTE_V9E = "V9E";
	// 2011.02.24
	public static final String ORDER_MODEL_HUAWEI_C8800 = "C8800";
	// 2011.03.05
	public static final String ORDER_MODEL_TIANYU_E800 = "5906";
	// 爱国者 N700 2011.03.11
	public static final String ORDER_MODEL_AIGO_N700 = "N700";
	// ZTE N880 2011.03.25
	public static final String ORDER_MODEL_ZTE_N880 = "ZTE-C N880";
	// COOLPAD 9930 2011.03.28
	public static final String ORDER_MODEL_COOLPAD_9930 = "9930";
	// 三星I919 2011.06.08
	public static final String ORDER_MODEL_SAMSUNG_I919 = "SCH-i919";
	// 35 Q68 2011.07.05
	public static final String ORDER_MODEL_35_Q68 = "Q68";
	// SAMSUNG W999 2011.07.15
	public static final String ORDER_MODEL_SAMSUNG_W999 = "SCH-W999";
	// 首亿 E9 2011.08.02
	public static final String ORDER_MODEL_SOAYE_E9 = "Soaye-E9";
	// HTC Z510d 2011.08.02
	public static final String ORDER_MODEL_HTC_Z510D = "HTC Z510d";
	// ZTE N880S 2011.08.03
	public static final String ORDER_MODEL_ZTE_N880S = "ZTE-C N880S";
	// COOLPAD 9100 2011.08.09
	public static final String ORDER_MODEL_COOLPAD_9100 = "9100";
	// 华为 S8600 2011.08.09
	public static final String ORDER_MODEL_HUAWEI_S8600 = "S8600";
	// HISENSE（海信） ET919 2011.08.18
	public static final String ORDER_MODEL_HS_ET919 = "HS-ET919";
	// HISENSE（海信） ET919 2011.08.18
	public static final String ORDER_MODEL_BK_PM700 = "Android for Telechips TCC8900 Evaluation Board";
	// 三星I919 2011.08.23
	public static final String ORDER_MODEL_SAMSUNG_I929 = "SCH-i929";
	// 京瓷M9300 2011.08.31
	public static final String ORDER_MODEL_KYOCERA_M9300 = "M9300";
	// 京瓷M9300 2011.11.30
	public static final String ORDER_MODEL_KYOCERA_KSP8000 = "KSP8000";
	// 酷派9900 2011.08.31
	public static final String ORDER_MODEL_COOLPAD_9900 = "9900";
	// 三五 U705 2011.09.08
	public static final String ORDER_MODEL_35_U705_UPAD = "UPAD";
	public static final String ORDER_MODEL_35_U705 = "U705";
	// 夏普SH7218T 2011.09.15
	public static final String ORDER_MODEL_SHARP_SH7218T = "SH7218T";
	// 华为 C8810 2011.09.19
	public static final String ORDER_MODEL_HUAWEI_C8810 = "Huawei-C8810";
	public static final String ORDER_MODEL_OTHER_HUAWEI_C8810 = "HUAWEI C8810";
	// 酷派5860 2011.09.26
	public static final String ORDER_MODEL_COOLPAD_5860 = "5860";
	// 华为 C8850 2011.10.08
	public static final String ORDER_MODEL_HUAWEI_C8850 = "HUAWEI-C8850";
	// 大唐DeTron-1 2011.10.24
	public static final String ORDER_MODEL_DETRON_1 = "DeTron-1";
	// 华宇能AF1000FR0 平板  2011.11.09
	public static final String ORDER_MODEL_HYN_AF1000FRO = "AF1000FRO";
	// HISENSE（海信） E910 2011.11.14
	public static final String ORDER_MODEL_HS_E910 = "HS-E910";
	// 天元-Q9 2011.12.20
	public static final String ORDER_MODEL_TE_TE800 = "TE800";
	// 易丰A5 2011.11.25
	public static final String ORDER_MODEL_EPHONE_A5 = "EPHONE A5";
	//  波导N760 2011.12.02
	public static final String ORDER_MODEL_BRID_N760 = "Bird N760";
	// 新邮通 D656 2011.12.07
	public static final String ORDER_MODEL_XYT_D656 = "D656";
	//联想A790E 2011.12.09
	public static final String ORDER_MODEL_LENOVO_A790E = "Lenovo A790e";
	//海信EG900 2011.12.22
	public static final String ORDER_MODEL_HS_EG900 = "HS-EG900";
	//长虹C100 2011.12.26  
	public static final String ORDER_MODEL_GH_CHANGHONGC100 = "GH-ChanghongC100";
	//同威S8210 2011.12.26  
	public static final String ORDER_MODEL_TW_S8210 = "S8210";
	//TCL-C995 2011.12.30 
	public static final String ORDER_MODEL_TCL_C995 = "TCL C995";
	//KUNUO E188 2012.01.31 
	public static final String ORDER_MODEL_KUNUO_E188 = "KN-E188";
	//海尔N86E 2012.01.31 
	public static final String ORDER_MODEL_HC_N86E = "N86E";
	public static final String ORDER_MODEL_HC_N86E_2 = "HE-N86E";
	//ZTE N880E 2012.01.31 
	public static final String ORDER_MODEL_ZTE_N880E = "ZTE N880E";
	public static final String ORDER_MODEL_ZTE_N880E_2 = "ZTE-C N880E";
	//华录S9000 2012.02.13
	public static final String ORDER_MODEL_CHL_S9000 = "S9000";
	//华录S9100 2012.02.13
	public static final String ORDER_MODEL_CHL_S9100 = "S9100";
	//三星I779 2012.02.14
	public static final String ORDER_MODEL_SCH_I779 = "SCH-I779";
	//大唐S22 2012.02.14
	public static final String ORDER_MODEL_DT_S22 = "S22";
	//桑菲D633 2012.02.21
	public static final String ORDER_MODEL_SAF_D633 = "SF-D633";
	//优派Q8 2012.03.08
	public static final String ORDER_MODEL_YHY_Q8 = "ViewSonic Q8";
	//海尔E760 2012.03.15
	public static final String ORDER_MODEL_HC_E760 = "HE-E760";
	// 酷派5860+ 2012.03.16
	public static final String ORDER_MODEL_YL_5860A = "5860A";
	// 华为C8812 2012.03.29
	public static final String ORDER_MODEL_HW_C8812 = "HUAWEI C8812";
	// 语信E96 2012.03.29
	public static final String ORDER_MODEL_YX_E96 = "YX-YUSUN E96";
	// 语信E96 2012.04.05
	public static final String ORDER_MODEL_HS_E920 = "HS-E920";
	// 三星I719 2012.04.06
	public static final String ORDER_MODEL_SCH_I719 = "SCH-I719";
	// 中兴N882E 2012.04.09
	public static final String ORDER_MODEL_ZTE_N882E = "ZTE N882E";
	// 酷派——5880 2012.04.26
	public static final String ORDER_MODEL_YL_5880 = "5880";
	// 海信EG906 2012.04.26
	public static final String ORDER_MODEL_HS_EG906 = "HS-EG906";
	// 美翼E999 2012.04.26
	public static final String ORDER_MODEL_MYJ_E999 = "E999";
	// 葳朗 VE600 2012.04.26
	public static final String ORDER_MODEL_BLW_VE600 = "VE600";
	// 中兴N881D 2012.04.26
	public static final String ORDER_MODEL_ZTE_N881D = "ZTE N881D";
	// 和信N719 2012.05.07
	public static final String ORDER_MODEL_HX_N719 = "HEG-N719";
	// 和信N819 2012.05.07
	public static final String ORDER_MODEL_HX_N819 = "HX-HE-N819";
	// 酷派5866 2012.05.07
	public static final String ORDER_MODEL_YL_5866 = "5866";
	// 酷派5870 2012.05.07
	public static final String ORDER_MODEL_YL_5870 = "5870";	
	// 联想A710e 2012.05.07
	public static final String ORDER_MODEL_LNV_A710E = "Lenovo A710e";
	// 创维SKY-E8 2012.05.17
	public static final String ORDER_MODEL_CW_SKYE8 = "SKY-E8";
	// 联想A700e 2012.05.22
	public static final String ORDER_MODEL_LNV_A700E = "Lenovo A700e";
	// 天迈D09S 2012.05.23
	public static final String ORDER_MODEL_TM_D09S = "T-smart D09S";
	// 蓝天 S980D 2012.05.28
	public static final String ORDER_MODEL_TLT_S980D = "LT S980D";
	// 易丰4S 2012.05.30
	public static final String ORDER_MODEL_YFZ_4S = "EPHONE 4S";
	// 华为C8825D 2012.06.04
	public static final String ORDER_MODEL_HW_C8825D = "HUAWEI C8825D";
	// 优思  US900 2012.06.18
	public static final String ORDER_MODEL_XD_US900 = "XD-US900";
	// 展翼N9 2012.06.26
	public static final String ORDER_MODEL_ZY_N9 = "FLY-N9";
	// 广信 EF930 2012.07.02
	public static final String ORDER_MODEL_GX_EF930 = "EF930";
	// 中兴N880F 2012.07.02
	public static final String ORDER_MODEL_ZTE_N880F = "ZTE N880F";
	// TCL-D662  2012.07.02
	public static final String ORDER_MODEL_TCL_D662 = "TCL D662";
	// 酷派5216  2012.07.05
	public static final String ORDER_MODEL_YL_5216 = "5216";
	// 金立C700  2012.07.09
	public static final String ORDER_MODEL_GIO_C700 = "C700";
	// 三星W9913   2012.07.09 
	public static final String ORDER_MODEL_SCH_W9913 = "SCH-W9913";  //三星W889
	// 酷派5910   2012.07.19
	public static final String ORDER_MODEL_YL_5910 = "5910";
	// 斐讯i330v   2012.07.19
	public static final String ORDER_MODEL_PXX_I330V = "i330v";
	// LNV A600E   2012.07.20
	public static final String ORDER_MODEL_LNV_A600E = "Lenovo A600e";
	public static final String ORDER_MODEL_LNV_A600E_2 = "LNV-Lenovo A600e";
	// 中兴N8010  2012.07.20
	public static final String ORDER_MODEL_ZTE_N8010 = "ZXY-ZTE_N8010"; 
	// 金立C610  2012.07.30
	public static final String ORDER_MODEL_GIO_C610 = "C610";
	// 酷派9120  2012.08.02
	public static final String ORDER_MODEL_YL_9120 = "9120";
	// HS-EG909  2012.08.20
	public static final String ORDER_MODEL_HS_EG909 = "HS-E909";
	// HS-E926  2012.08.20
	public static final String ORDER_MODEL_HS_E926 = "HS-E926";
	// 中兴N878  2012.08.21
	public static final String ORDER_MODEL_ZTE_N878 = "ZTE N878";
	// 酷派5860S  2012.08.24
	public static final String ORDER_MODEL_YL_5860S = "5860S";
	// 酷诺 EG189 2012.08.31
	public static final String ORDER_MODEL_KUNUO_EG189 = "CONOR EG189";
	//  中兴 N881E 2012.08.31
	public static final String ORDER_MODEL_ZTE_N881E = "ZTE N881E";
	//  桑菲D833 2012.09.03
	public static final String ORDER_MODEL_SAF_D833 = "Philips D833";
	//  HTC-T329d 2012.09.17
	public static final String ORDER_MODEL_HTC_T329D = "HTC T329d";
	//  华为  C8833D 2012.09.24
	public static final String ORDER_MODEL_HW_C8833D = "HUAWEI Y300-2010";
	// TCL-D668 2012.09.24	
	public static final String ORDER_MODEL_TCL_D668 = "TCL-D668";
	// 华为 C8826D 2012.09.24	
	public static final String ORDER_MODEL_HW_C8826D = "HUAWEI C8826D"; 
	// 三星W2013   2012.09.28
	public static final String ORDER_MODEL_SCH_W2013 = "SCH-W2013";
	// 三星I829   2012.10.08
	public static final String ORDER_MODEL_SCH_I829 = "SCH-I829";
	// 三星I759   2012.10.11
	public static final String ORDER_MODEL_SCH_I759 = "SCH-I759";
	// HTC T327d   2012.10.23
	public static final String ORDER_MODEL_HTC_T327D = "HTC T327d";
	// 海尔E700  2012.11.01
	public static final String ORDER_MODEL_HC_E700 = "HE-E700";
	// 中兴N807  2012.11.01
	public static final String ORDER_MODEL_ZTE_N807 = "ZTE N807";
	// 金立C605  2012.11.19
	public static final String ORDER_MODEL_GIO_C605 = "C605";
	// 三星I739  2012.11.27
	public static final String ORDER_MODEL_SCH_I739 = "SCH-I739";
	// TCL D920  2012.11.28
	public static final String ORDER_MODEL_TCL_D920 = "TCL D920";
	// 华录 S9500 2012.12.07
	public static final String ORDER_MODEL_CHL_S9500 = "S9500";
	// 海信 E930 2012.12.07
	public static final String ORDER_MODEL_HS_E930 = "HS-E930";
	// lephone 2900 2012.12.07
	public static final String ORDER_MODEL_BLF_2900 = "lephone 2900";
	// 锋达通 E8 2012.12.07
	public static final String ORDER_MODEL_FDT_E8 = "FDT E8";
	// 至尊宝 CD801  2012.12.07
	public static final String ORDER_MODEL_HST_CD801 = "HST-CD801";
	// 国信通 E880  2012.12.07
	public static final String ORDER_MODEL_GXT_E880 = "E880";
	// 大唐 S26  2012.12.10
	public static final String ORDER_MODEL_DT_S26 = "S26";
	// 广信 EF88  2012.12.11
	public static final String ORDER_MODEL_GX_EF88 = "EF88";
	// 飓畅 JC-A8  2012.12.12
	public static final String ORDER_MODEL_JCT_A8 = "JC-A8";
	// 天迈  D99SW 2012.12.14
	public static final String ORDER_MODEL_TM_D99SW = "T-smart D99SW";
	// 展翼 N9X 2012.12.18
	public static final String ORDER_MODEL_ZY_N9X = "N9X";
	// 博瑞世纪 W69 2012.12.19
	public static final String ORDER_MODEL_BR_W69 = "BROR W69";
	// 百灵 VE63 2012.12.21
	public static final String ORDER_MODEL_BLW_VE63 = "BLW-VE63";
	// 三星 I879 2012.12.24
	public static final String ORDER_MODEL_SCH_I879 = "SCH-I879";
	// 微网通信 H200 2013.01.08
	public static final String ORDER_MODEL_WW_H200 = "WayTone H200";
	
	// *********************************************************************************************
	// ******************************************540*960MODEL***************************************
	// *********************************************************************************************
	// 2011.01.26
	public static final String ORDER_MODEL_MOTO_XT882 = "XT882";
	// MOTO XT883 2011.04.27
	public static final String ORDER_MODEL_MOTO_XT883 = "XT883";
	// HTC X515D 2011.08.02
	public static final String ORDER_MODEL_HTC_X515D = "HTC X515d";
	// 摩托XT885 2011.08.31
	// public static final String ORDER_MODEL_MOTO_XT885 = "XT885";
	public static final String ORDER_MODEL_MOTO_XT928 = "XT928"; // 2011-10-24
	// 夏普SH831T 2011.12.06
	public static final String ORDER_MODEL_SHARP_SH831T = "SH831T";	   
	// 中兴N970 2012.05.23
	public static final String ORDER_MODEL_ZTE_N970 = "ZTE N970";  
	// 华为C8950D 2012.07.02
	public static final String ORDER_MODEL_HW_C8950D = "HUAWEI C8950D"; 
	// 华为 C8951D 2012.09.24	
	public static final String ORDER_MODEL_HW_C8951D = "HUAWEI C8951D"; 
	// 华为C8813 2012.11.05
	public static final String ORDER_MODEL_HW_C8813 = "HUAWEI C8813"; 
	// 华录S9388 2012.11.22
	public static final String ORDER_MODEL_CHL_S9388 = "S9388"; 
	// 斐讯 i700v 2012.11.28
	public static final String ORDER_MODEL_PXX_I700V = "i700v"; 
	// 赛鸿 X5 2012.12.08
	public static final String ORDER_MODEL_SH_X5 = "Saihon X5"; 
	// 联想 A630E 2012.12.26
	public static final String ORDER_MODEL_LNV_A630E = "A630e"; 
	public static final String ORDER_MODEL_LNV_A630E_2 = "Lenovo A630e"; 
	// 海信 EG956 2012.12.26
	public static final String ORDER_MODEL_HS_E956 = "HS-E956"; 
	// 灵威朗 VE65 2013.01.09
	public static final String ORDER_MODEL_BLW_VE65 = "VE65"; 
	
	// *********************************************************************************************
	// ******************************************320*480MODEL***************************************
	// *********************************************************************************************
	public static final String ORDER_MODEL_R750 = "ZTE-C R750";
	public static final String ORDER_MODEL_C8600 = "C8600";
	public static final String ORDER_MODEL_D530 = "D530";
	public static final String ORDER_MODEL_E600 = "E600";
	public static final String ORDER_MODEL_HTC_HERO200 = "HERO200";
	public static final String ORDER_MODEL_COOLPAD_D539 = "D539";
	public static final String ORDER_MODEL_COOLPAD_D5800 = "D5800";
	public static final String ORDER_MODEL_COOLPAD_N950 = "N950";
	// SAMSUNG I579 20101222
	public static final String ORDER_MODEL_SAMSUNG_I579 = "SCH-i579";
	// HAIER N6E 20101229
	public static final String ORDER_MODEL_HAIER_N6E = "HE-N6E";
	// TIANYU E600 2011.01.19
	public static final String ORDER_MODEL_TIANYU_E600 = "5902";
	// COOLPAD E239 2011.03.07
	public static final String ORDER_MODEL_COOLPAD_E239 = "E239";
	// SAMSUNG I569 2011.03.10
	public static final String ORDER_MODEL_SAMSUNG_I569 = "SCH-i569";
	// ZTE R750+ 2011.03.10
	public static final String ORDER_MODEL_ZTE_R750PLUS = "ZTE-C R750+";
	// 三美奇 EA6000 2011.03.18
	public static final String ORDER_MODEL_SANMEIQI_EA6000 = "EA6000";
	// HUAWEI C8650 2011.03.29
	public static final String ORDER_MODEL_HUAWEI_C8650 = "C8650";
	// 2011.04.06
	public static final String ORDER_MODEL_GAOXINQI_ES608 = "ES608";
	// 创维PE10 2011.04.14
	public static final String ORDER_MODEL_SKYWORTH_PE10 = "PE10";
	// 和信N200 2011.04.14
	public static final String ORDER_MODEL_HESENS_N200 = "N200";
	// HTC MARVEL 2011.04.15
	public static final String ORDER_MODEL_HTC_MARVEL = "HTC A510c";
	// 彤霖T90 2011.05.03
	public static final String ORDER_MODEL_TONGlIN_T90 = "T90";
	// 中兴N760 2011.05.09
	public static final String ORDER_MODEL_ZTE_N760 = "ZTE-C N760";
	// 海信EG968B 2011.05.30
	public static final String ORDER_MODEL_HS_EG968B = "EG968B";
	// 三和新 S06 2011.06.15
	public static final String ORDER_MODEL_SHX_S06 = "FC8912";
	// 海信 E86 2011.06.15
	public static final String ORDER_MODEL_HS_E86 = "E86";
	// EPHONE A6 2011.06.22
	public static final String ORDER_MODEL_EPHONE_A6 = "A6";
	// 联想 A68E 2011.06.23
	public static final String ORDER_MODEL_LENOVO_A68E = "Lenovo A68e";
	// 展翼 N8 2011.06.24
	public static final String ORDER_MODEL_FLY_N8 = "N8";
	// 威睿 P2 2011.06.24
	public static final String ORDER_MODEL_VIA_P2 = "ViaTelecom kunlun";
	// 金派 E239 2011.06.27
	public static final String ORDER_MODEL_KINGPAD_E239 = "KINGPAD E239";
	// LENOVO amazon e 2011.07.05
	// public static final String ORDER_MODEL_LENOVO_A1 = "amazon_e";
	public static final String ORDER_MODEL_LENOVO_A1 = "Lenovo A1-32AJ0"; // 2011.07.26
	// 南极星 D99 2011.07.05
	public static final String ORDER_MODEL_EXUN_D99 = "E·XUN-D99";
	// LG CS600 2011.07.08
	public static final String ORDER_MODEL_LG_CS600 = "LG-CS600";
	// 中辰 ZC600 2011.07.19
	public static final String ORDER_MODEL_ZC_ZC600 = "ZC600";
	// COOLPAD D5820 2011-07-25
	public static final String ORDER_MODEL_COOLPAD_5820 = "Coolpad 5820";
	// 海尔 EG-E600 2011-07-25
	public static final String ORDER_MODEL_HC_EGE600 = "EG-E600";
	// 彤霖T98 2011.07.25
	public static final String ORDER_MODEL_TONGlIN_T98 = "T98";
	// 波导 AE750 2011.07.26
	public static final String ORDER_MODEL_BIRD_AE750 = "Bird AE750";
	// 大成E366 2011.07.28
	public static final String ORDER_MODEL_DC_E366 = "E366";
	// 奇乐A709 2011.08.02
	public static final String ORDER_MODEL_QL_A709 = "CHER A709";
	// 奇乐A709 2011.08.02
	public static final String ORDER_MODEL_HW_S8520 = "S8520";
	// 语信E80 2011.08.15
	public static final String ORDER_MODEL_YUXIN_E80 = "E80";
	// 赛鸿I90 2011.08.15
	public static final String ORDER_MODEL_SAIHONG_I90 = "I90";
	// 和信N300 2011.08.15
	public static final String ORDER_MODEL_HEXIN_N300 = "HE-N300";
	// 海尔N710E 2011.08.15
	public static final String ORDER_MODEL_HC_N710E = "HE-N710E";
	// COOLPAD 5855 2011.08.23
	public static final String ORDER_MODEL_COOLPAD_5855 = "5855";
	// 海尔E899 2011.08.23
	public static final String ORDER_MODEL_HC_E899 = "HE-E899";
	// 大众EC600 2011.08.23
	public static final String ORDER_MODEL_DZD_EC600 = "EC600";
	// 中兴N780 2011.08.31
	public static final String ORDER_MODEL_ZTE_N780 = "ZTE-C N780";
	// 本为5100 2011.08.31
	public static final String ORDER_MODEL_BENWEI_5100 = "Q7";
	// 海尔N720E 2011.08.31
	public static final String ORDER_MODEL_HC_N720E = "HE-N720E";
	// 三星I579I 2011.08.31
	public static final String ORDER_MODEL_SAMSUNG_I579I = "SCH-i579i";
	// 首亿E6 2011.09.14
	public static final String ORDER_MODEL_SOAYE_E6 = "Soaye-E6";
	// TCL-C990 2011.09.14
	public static final String ORDER_MODEL_TCL_C990 = "msm7627_ffa";
	// 联想A390E 2011.09.15
	public static final String ORDER_MODEL_LENOVO_A390E = "Lenovo A390e";
	// 创维PE90 2011.09.15
	public static final String ORDER_MODEL_SKYWORTH_PE90 = "Skyworth PE90";
	// 展翼N7 2011.09.26
	public static final String ORDER_MODEL_ZY_N7 = "N7";
	// 华录S3000 2011.09.26
	public static final String ORDER_MODEL_CHL_S3000 = "S3000";
	// 桑达S508EG 2011.09.29
	public static final String ORDER_MODEL_SED_S508EG = "S508EG";
	// 语信-E88 2011.10.08
	public static final String ORDER_MODEL_YUSUN_E88 = "Yusun E88";
	// 语信-E70 2011.10.19
	public static final String ORDER_MODEL_YUSUN_E70 = "yusun E70";
	// COOLPAD 5899 2011.10.24
	public static final String ORDER_MODEL_COOLPAD_5899 = "5899";
	// 天元TE690 2011.10.24
	public static final String ORDER_MODEL_YCT_TE690 = "TE690";
	// 夏普SH320T 2011.11.01
	public static final String ORDER_MODEL_SHARP_SH320T = "SH320T";
	// ZTE X500 2011.11.07
	public static final String ORDER_MODEL_ZTE_X500 = "ZTE-C X500";
	// HUAWEI C8650E 2011.11.08
	public static final String ORDER_MODEL_HUAWEI_C8650E = "HUAWEI C8650+";
	// 和信N800 2011.11.15
	public static final String ORDER_MODEL_HEXIN_N800 = "HE-N800";
	// 和信N700 2011.11.15
	public static final String ORDER_MODEL_HEXIN_N700 = "HEG-N700";
	public static final String ORDER_MODEL_HEXIN_HE_N700 = "HE-N700";
	// HISENSE（海信） E860 2011.11.25
	public static final String ORDER_MODEL_HS_E860 = "HS-E860";	
	// 谷派E68 2011.11.25
	public static final String ORDER_MODEL_EBEST_E68 = "EBEST E68";
	// 赛鸿I91 2011.11.25
	public static final String ORDER_MODEL_SAIHONG_I91 = "SH_I91";
	public static final String ORDER_MODEL_SAIHON_I91 = "Saihon I91";
	// 基伍 E86 2011.12.13 
	public static final String ORDER_MODEL_JWG_E86 = "Gfive-E86";
	//三星I619 2011.12.20
	public static final String ORDER_MODEL_SCH_I619 = "SCH-i619";
	public static final String ORDER_MODEL_SCH_I619_2 = "SCH-I619";
	// 博瑞S9 2011.12.22
	public static final String ORDER_MODEL_BR_S9 = "S9";
	// 广信E920 2011.12.27
	public static final String ORDER_MODEL_GX_E920 = "e920";
	// 海尔N710E 2011.12.30
	public static final String ORDER_MODEL_HC_N620E = "HE-N710E";	
	// 蓝天S600D 2012.1.11
	public static final String ORDER_MODEL_TLT_S600D= "Generic";
	// 赛鸿I98 2012.01.31
	public static final String ORDER_MODEL_SAIHONG_I98 = "Saihon I98";
	// 语信E88 2012.01.31
	public static final String ORDER_MODEL_YUSUN_E89 = "E89";
	// 夏普SH330T 2012.01.31
	public static final String ORDER_MODEL_SHARP_SH330T = "SH330T";
	// 蓝天S800 2012.01.31
	public static final String ORDER_MODEL_TLT_S800 = "msm7627a_sku3_s800";
	public static final String ORDER_MODEL_TLT_S800_2 = "LT S800";
	// 华为C8655 2012.02.04 
	public static final String ORDER_MODEL_HUAWEI_C8655_1 = "C8655";
	public static final String ORDER_MODEL_HUAWEI_C8655_2 = "HUAWEI C8655";
	// 易丰A10 2012.02.10
	public static final String ORDER_MODEL_YFZ_A10 = "A10";
	// 华录S3500 2012.02.15
	public static final String ORDER_MODEL_CHL_S3500 = "S3500";
	// 海尔N610E 2012.02.27
	public static final String ORDER_MODEL_HC_N610E = "N610E";
	// 海尔E617 2012.02.27
	public static final String ORDER_MODEL_HC_E617 = "HE-E617";
	// 博瑞W60 2012.02.27
	public static final String ORDER_MODEL_BR_W60 = "W60";
	// 赛鸿I901 2012.02.29
	public static final String ORDER_MODEL_SAIHONG_I901 = "Saihon I901";
	// 佳斯特 J868 2012.03.05
	public static final String ORDER_MODEL_JST_J868 = "J868";
	// 海尔N620E 2012.03.05
	public static final String ORDER_MODEL_HE_N620E = "HE-N620E";
	// 35Phone Q3510 2012.03.08 
	public static final String ORDER_MODEL_SW_Q3510 = "35Phone-Q3510";
	// 摩托罗拉 XT553 2012.03.08
	public static final String ORDER_MODEL_MOT_XT553 = "Motorola MOT-XT553";
	// 世纪星宇S11 2012.03.14
	public static final String ORDER_MODEL_SJX_S11 = "CIYO-S11";
	//  联想—A560E 2012.03.15
	public static final String ORDER_MODEL_LNV_A560E = "Lenovo A560e";
	//  中信N885 2012.03.16
	public static final String ORDER_MODEL_ZTE_N855 = "ZTE N855";
	// 酷派—5210 2012.03.16
	public static final String ORDER_MODEL_YL_5210 = "Coolpad 5210";
	// 同威—S8510 2012.03.16
	public static final String ORDER_MODEL_TW_S8510 = "TONEWIN S8510";
	// 中兴—V6700 2012.03.26
	public static final String ORDER_MODEL_ZTE_V6700 = "ZTE V6700";
	public static final String ORDER_MODEL_ZTE_V6700_2 = "ZXY-ZTE_V6700";
	// 金立C500 2012.03.29
	public static final String ORDER_MODEL_GIO_C500 = "C500";
	// 金立C600 2012.03.29
	public static final String ORDER_MODEL_GIO_C600 = "C600";
	// 华录 S8300 2012.04.06
	public static final String ORDER_MODEL_CHL_S8300 = "S8300";
	// 三星 I919U 2012.04.26
	public static final String ORDER_MODEL_SCH_I919U = "SCH-I919U";
	// 酷派——5110 2012.04.26
	public static final String ORDER_MODEL_YL_5110 = "5110";
	// 海信——EG870 2012.04.26
	public static final String ORDER_MODEL_HS_EG870 = "HS-EG870";
	// 赛鸿I97 2012.04.26
	public static final String ORDER_MODEL_SH_I97 = "Saihon I97";
	// 华录—S3000B 2012.04.26
	public static final String ORDER_MODEL_CHL_S3000B = "msm7627a_sku1";
	// 中兴N885D 2012.05.07
	public static final String ORDER_MODEL_ZTE_N855D = "ZTE N855D";
	// 金立C900 2012.05.07
	public static final String ORDER_MODEL_GIO_C900 = "GiONEE C900";
	// 同威S8310 2012.05.07
	public static final String ORDER_MODEL_TW_S8310 = "TONEWIN S8310";
	// 和信N739 2012.05.07
	public static final String ORDER_MODEL_HX_N739 = "HEG-N739";
	// 三星I659 2012.05.14
	public static final String ORDER_MODEL_SCH_I659 = "SCH-I659";
	// 优思S6000 2012.05.16
	public static final String ORDER_MODEL_XD_S6000 = "A3";
	// 同威S8800  2012.05.16
	public static final String ORDER_MODEL_TW_S8800 = "TONEWIN S8800";
	// 蓝天S800D  2012.05.22	
	public static final String ORDER_MODEL_TLT_S800D = "LT S800D";
	// 天迈D99S  2012.05.23	
	public static final String ORDER_MODEL_TM_D99S = "T-smart D99S";
	// 百灵威 VE509  2012.05.28	
	public static final String ORDER_MODEL_BLW_VE509 = "BLW-VE509";
	// 泰丰C800  2012.06.04	
	public static final String ORDER_MODEL_TF_C800 = "TF-C800";	
	// 易丰E6A  2012.06.04	
	public static final String ORDER_MODEL_YFZ_E6A = "EPHONE E6A";	
	// TCL C990+  2012.06.08	
	public static final String ORDER_MODEL_TCL_C990P = "TCL C990+";
	// 天语  E619 2012.06.18	
	public static final String ORDER_MODEL_TY_E619 = "K-Touch E619";
	// 比酷 X903 2012.06.26	
	public static final String ORDER_MODEL_BK_X903 = "Coobe X903";
	// 金派EG800 2012.07.23	
	public static final String ORDER_MODEL_SM_EG800 = "Kingpad EG800";
	// 斐讯K210V 2012.08.01	
	public static final String ORDER_MODEL_PXX_K210V = "K210v";
	// 中兴N855D+ 2012.08.21	
	public static final String ORDER_MODEL_ZTE_N855DP = "ZTE N855D+";
	// 飓畅A107 2012.08.23	
	public static final String ORDER_MODEL_JCT_A107 = "JC-A107";
	// 华为C8685D 2012.09.06	
	public static final String ORDER_MODEL_HW_C8685D = "Y210-2010";
	public static final String ORDER_MODEL_HW_C8685D_2 = "HUAWEI C8685D";
	// 海信E830  2012.10.31	
	public static final String ORDER_MODEL_HS_E830 = "HS-E830";
	// 语信E66  2012.11.01	
	public static final String ORDER_MODEL_YX_E66 = "YUSUN E66";
	// 蓝天 S600+  2012.11.05	
	public static final String ORDER_MODEL_TLT_S600P = "LT S600+";
	// 酷派5210S  2012.11.30	
	public static final String ORDER_MODEL_YL_5210S = "YL-Coolpad 5210S"; 
	// TCL D510  2012.12.03	
	public static final String ORDER_MODEL_TCL_D510 = "TCL D510";  
	// 广信 EF78 2012.12.04	
	public static final String ORDER_MODEL_GX_EF78 = "EF78";  
	// Lephone 1800 2012.12.07	
	public static final String ORDER_MODEL_BLF_1800 = "lephone 1800";  
	// 创维 EG6188 2012.12.10	
	public static final String ORDER_MODEL_CW_EG6188 = "Skyworth EG6188";  
	// 佳斯特 J699 2012.12.10	
	public static final String ORDER_MODEL_JST_J699 = "J699";  
	// 美翼E86 2012.12.10	
	public static final String ORDER_MODEL_MYJ_E86 = "MY E86";  
	// 博瑞-W68 2012.12.10	
	public static final String ORDER_MODEL_BR_W68 = "BROR W68";  
	// Lephone 2800 2012.12.10	
	public static final String ORDER_MODEL_BLF_2800 = "lephone 2800";  
	// 三美奇 EA6800 2012.12.10	
	public static final String ORDER_MODEL_SMQ_EA6800 = "EA6800";  
	// 同威 X1 2012.12.11	
	public static final String ORDER_MODEL_TW_X1 = "X1";  
	// 广信 EF68 2012.12.11	
	public static final String ORDER_MODEL_GX_EF68 = "EF68";  
	// 飓畅 JC-A109 2012.12.12	
	public static final String ORDER_MODEL_JCT_A109 = "JC-A109";  
	// 易丰 8s 2012.12.26	
	public static final String ORDER_MODEL_YFZ_8S = "EPHONE 8S";  
	// 天迈 G19 2012.12.26	
	public static final String ORDER_MODEL_TM_G19 = "T-smart G19";  
		
	// *********************************************************************************************
	// ******************************************240*320MODEL***************************************
	// *********************************************************************************************
	public static final String ORDER_MODEL_MOTO_XT301 = "XT301";
	public static final String ORDER_MODEL_ZTE_N600 = "ZTE-C N600";
	public static final String ORDER_MODEL_HW_C8500 = "C8500";
	// public static final String ORDER_MODEL_E230A = "E230A";
	public static final String ORDER_MODEL_E230A = "E230";
	// 2010.11.30
	public static final String ORDER_MODEL_ZTE_N606 = "ZTE-C N606";
	// 2010.11.30
	public static final String ORDER_MODEL_SAMSUNG_I559_1 = "SCH-i559";
	public static final String ORDER_MODEL_SAMSUNG_I559_2 = "SCH-I559";
	// 2010.12.01
	public static final String ORDER_MODEL_EPHONE_A9 = "CS7007";
	// 2010.12.09
	public static final String ORDER_MODEL_ZTE_N600_PLUS = "ZTE-C N600+";
	// 2010.12.15
	// public static final String ORDER_MODEL_HESENS_N100 = "CS7007HX";
	//和信N100
	public static final String ORDER_MODEL_HESENS_N100 = "N100";
	// 2010.12.21
	public static final String ORDER_MODEL_HTC_BEE = "HTC Bee";
	// 2010.12.21
	public static final String ORDER_MODEL_HS_E89 = "E89";
	// 2011.01.04
	public static final String ORDER_MODEL_ZTE_X920 = "ZTE-C X920";
	// 2011.02.10
	public static final String ORDER_MODEL_TE_TE600 = "TE600";
	// 2011.02.10
	public static final String ORDER_MODEL_HUALU_S800 = "S800";
	// 2011.02.24
	public static final String ORDER_MODEL_ZTE_N700 = "ZTE-C N700";
	// 2011.03.09
	public static final String ORDER_MODEL_CHER_A4 = "CHER A4";
	// 2011.03.09
	public static final String ORDER_MODEL_TONGWEI_X2011 = "X2011";
	// 2011.03.23
	public static final String ORDER_MODEL_EPHONE_A8 = "A8";
	// 2011.03.30
	public static final String ORDER_MODEL_FLY_N6 = "N6";
	// 2011.04.06
	public static final String ORDER_MODEL_GUANGXIN_E900 = "E900";
	// 2011.04.19 天语E610
	public static final String ORDER_MODEL_TIANYU_E610 = "K-Touch E610";
	// 2011.04.26 海信E839
	public static final String ORDER_MODEL_HS_E839 = "E839";
	// 波导AE710 2011.05.04
	public static final String ORDER_MODEL_BIRD_AE710 = "Bird AE710";
	// 创维PE89 2011.05.04
	public static final String ORDER_MODEL_SKYWORTH_PE89 = "Skyworth PE89";
	// 海信E87 2011.05.04
	public static final String ORDER_MODEL_HS_E87 = "E87";
	// 华录ES2000 2011.05.11
	public static final String ORDER_MODEL_HUALU_ES2000 = "ES2000";
	// 蓝天 S100 2011.06.15
	public static final String ORDER_MODEL_LANTIAN_S100 = "S100";
	// 金派 N600 2011.06.15
	public static final String ORDER_MODEL_KINGPAD_N600 = "N600";
	// 华录 S850 2011.07.18
	public static final String ORDER_MODEL_CHL_S850 = "S850";
	// 华为 C8500SR 2011.07.28
	public static final String ORDER_MODEL_HUAWEI_C8500SR = "C8500S";
	// 博瑞-S06 2011.08.10
	public static final String ORDER_MODEL_BROR_S06 = "S6";
	// 语信E60 2011.08.15
	public static final String ORDER_MODEL_YUXIN_E60 = "E60";
	public static final String ORDER_MODEL_YUXIN_E60_2 = "YUSUN E60";
	// 中兴 N601 2011.08.19
	public static final String ORDER_MODEL_ZTE_N601 = "ZTE-C N601";
	// 创维ES2000 2011.08.26
	public static final String ORDER_MODEL_SKYWORTH_ES2000 = "ES2000";
	// 展翼N5 2011.09.15
	public static final String ORDER_MODEL_FLY_N5 = "N5";
	// 华为C8550 2011.12.30
	public static final String ORDER_MODEL_HUAWEI_C8550 = "HUAWEI C8550";
	// SAMSUNG I339 2012.01.31
	public static final String ORDER_MODEL_SAMSUNG_I339 = "SCH-i339";
	// 世纪星宇 S7 2012.02.29
	public static final String ORDER_MODEL_SJX_S7 = "S7";
	// 三星I519 2012.03.15
	public static final String ORDER_MODEL_SCH_I519 = "SCH-I519";
	// 三星I509 2012.03.20
	public static final String ORDER_MODEL_SCH_I509U = "SCH-I509U";
	// 三星I639 2012.03.20
	public static final String ORDER_MODEL_SCH_I639 = "SCH-I639";
	// 百灵威朗—VE20 2012.03.26
	public static final String ORDER_MODEL_BLW_VE200 = "VE200";
	// 酷派5010 2012.05.07
	public static final String ORDER_MODEL_YL_5010 = "Coolpad 5010";
	// 百利丰2600 2012.07.02
	public static final String ORDER_MODEL_BLF_2600 = "lephone 2600";
	// 百利丰1600 2012.07.02
	public static final String ORDER_MODEL_BLF_1600 = "lephone 1600";
	// 广信 EF55 2012.12.04
	public static final String ORDER_MODEL_GX_EF55 = "EF55";
	// 百灵威 VE360 2012.12.06
	public static final String ORDER_MODEL_BLW_VE360 = "VE360";
	// 赛鸿 I60 2012.12.10
	public static final String ORDER_MODEL_SH_I60 = "Saihon I60";
	
	// *********************************************************************************************
	// *****************************************其它分辨率MODEL**************************************
	// *********************************************************************************************
	// public static final String ORDER_MODEL_LIFEPAD = "i850";
	public static final String ORDER_MODEL_LIFEPAD = "LifePad A800";
	// 2011.03.14
	public static final String ORDER_MODEL_AUSCLOUD_PAD = "Auscloud 10 inch";
	// 2011.05.25
	public static final String ORDER_MODEL_LION_PAD_I600 = "I600";
	// 2011.06.08
	public static final String ORDER_MODEL_LION_PAD_A1 = "LionPAD A1";
	// 万利达 T8 2011.07.08
	public static final String ORDER_MODEL_MALATA_T8 = "T8";
	// 万事通 A700 2011.07.14
	public static final String ORDER_MODEL_WST_A700 = "EF101";
	// SAMSUNG P739 2011.08.02
	public static final String ORDER_MODEL_SAMSUNG_P739 = "SCH-P739";
	// E人E本 T3 2011.08.12
	public static final String ORDER_MODEL_EBEN_T3 = "T3";
	// UT斯达康P200E 2011.09.15
	public static final String ORDER_MODEL_UT_P200E = "p800_l9_evdo";
	// 三星 i889 2011.12.06
	public static final String ORDER_MODEL_SAMSUNG_I889 = "SCH-i889";
	// 三星 I92502012.02.27
	public static final String ORDER_MODEL_SAMSUNG_I9250 = "Galaxy Nexus";
	// 摩托罗拉 XT889 2012.03.08
	public static final String ORDER_MODEL_MOT_XT889 = "XT889";
	// HTC—T328d 2012.03.09
	public static final String ORDER_MODEL_HTC_T328d = "HTC T328d";
	// 三星 I939 2012.04.23
	public static final String ORDER_MODEL_SAMSUNG_I939 = "SCH-I939";
	// HTC—X720d 2012.04.26
	public static final String ORDER_MODEL_HTC_X720D = "HTC X720d";
	// 斐讯 FPAD 2012.06.08
	public static final String ORDER_MODEL_PXX_FPAD = "FPAD";
	// 索尼LT25c 2012.07.05
	public static final String ORDER_MODEL_SND_LT25C = "LT25c";
	// MOT-XT785 2012.07.17
	public static final String ORDER_MODEL_MOT_XT785 = "MOT-XT785"; 
	// HTC-T528d  2012.07.23
	public static final String ORDER_MODEL_HTC_T528D = "HTC T528d";
	// 酷派9960  2012.07.30
	public static final String ORDER_MODEL_YL_9960 = "9960";
	public static final String ORDER_MODEL_YL_9960_2 = "YL-Coolpad 9960";
	// 联想S870e  2012.07.30
	public static final String ORDER_MODEL_LNV_S870E = "msm8x25";
	public static final String ORDER_MODEL_LNV_S870E_2 = "Lenovo S870e";
	public static final String ORDER_MODEL_LNV_S870E_3 = "LNV-Lenovo S870e";
	// 三星I959  2012.08.09
	public static final String ORDER_MODEL_SCH_I959 = "SCH-I959"; 
	// MOT-XT788 2012.08.09
	public static final String ORDER_MODEL_MOT_XT788 = "MOT-XT788";
	// 金立C800 2012.08.10
	public static final String ORDER_MODEL_GIO_C800 = "C800";
	// 夏普SH630T 2012.08.14
	public static final String ORDER_MODEL_SHP_SH630T = "SH630T";
	public static final String ORDER_MODEL_SHP_SH630T_2 = "SHP-SH630T";
	// 三星I939D 2012.08.15
	public static final String ORDER_MODEL_SCH_I939D = "SCH-i939D"; 
	public static final String ORDER_MODEL_SCH_I939D_2 = "SCH-I939D"; 
	// 海信 EG950 2012.08.30
	public static final String ORDER_MODEL_HS_EG950 = "HS-EG950"; 
	// 三星N719 2012.09.11
	public static final String ORDER_MODEL_SCH_N719 = "SCH-N719"; 
	// 海尔E80 2012.10.11
	public static final String ORDER_MODEL_HE_E80 = "HE_E80"; 
	// 中兴N880G 2012.10.19
	public static final String ORDER_MODEL_ZTE_N880G = "ZTE-N880G"; 
	// 酷派5876 2012.11.01
	public static final String ORDER_MODEL_YL_5876 = "5876"; 
	// 中兴N983 2012.11.01
	public static final String ORDER_MODEL_ZTE_N983 = "ZTE N983"; 
	// 优思U1203 2012.11.06
	public static final String ORDER_MODEL_XD_U1203 = "Uniscope_U1203"; 
	// 华为 D2 2012.11.08
	public static final String ORDER_MODEL_HW_D2 = "hwd2-2010"; 
	// TCL-D706 2012.12.03
	public static final String ORDER_MODEL_TCL_D706 = "TCL-D706"; 
	// 酷派 5890 2012.12.04
	public static final String ORDER_MODEL_YL_5890 = "Coolpad 5890"; 
	// 蓝天 S939D 2012.12.05
	public static final String ORDER_MODEL_TLT_S939D = "LT S939D"; 
	// 百灵威 E2013 2012.12.05
	public static final String ORDER_MODEL_BLW_E2013 = "E2013"; 
	public static final String ORDER_MODEL_BLW_E2013_2 = "VLAND E2013"; 
	// ZTE N881F 2012.12.06
	public static final String ORDER_MODEL_ZTE_N881F = "ZTE N881F"; 
	// 基思瑞 D9C 2012.12.06
	public static final String ORDER_MODEL_JSR_D9C = "innos D9C"; 
	// 联想 A820e 2012.12.06
	public static final String ORDER_MODEL_LVN_A820E = "Lenovo A820e"; 
	// Coolpad 5930 2012.12.10
	public static final String ORDER_MODEL_YL_5930 = "Coolpad 5930"; 
	// TF-C820 2012.12.14
	public static final String ORDER_MODEL_TF_C820 = "TF-C820"; 
	// Coolpad 9070 2012.12.24
	public static final String ORDER_MODEL_YL_9070 = "Coolpad 9070"; 
	// 基思瑞 I6C 2013.01.11
	public static final String ORDER_MODEL_JSR_I6C = "innos i6C"; 
	// Sony M35h 2013.01.11
	public static final String ORDER_MODEL_SND_M35H = "M35h"; 
	
	private static final String[] ORDER_MODEL = new String[] {
			ORDER_MODEL_XT800,
			ORDER_MODEL_XT806,
			ORDER_MODEL_I909, 
			ORDER_MODEL_HUAWEI_C8860E,
			ORDER_MODEL_HUAWEI_C8860E_2,			
			ORDER_MODEL_COOLPAD_N930,
			ORDER_MODEL_XT800PLUS,
			ORDER_MODEL_XIAOMI_C1,
			ORDER_MODEL_ME811,	
			ORDER_MODEL_LNV_S850E,
			ORDER_MODEL_LNV_S850E_2,
			ORDER_MODEL_MOT_XT681,
			ORDER_MODEL_XD_US910,
			ORDER_MODEL_XD_US920,
			ORDER_MODEL_LNV_A765E,
			ORDER_MODEL_TLT_S910D,
			ORDER_MODEL_XYT_E606,
			ORDER_MODEL_BR_S3,
			ORDER_MODEL_FDT_E9,
			ORDER_MODEL_W899,
			ORDER_MODEL_LEPHONE,
			ORDER_MODEL_HTC_VIVO,
			ORDER_MODEL_HTC_EVO,
			ORDER_MODEL_HTC_INCREDIBLE,
			ORDER_MODEL_ZTE_V9E,
			ORDER_MODEL_HUAWEI_C8800,
			ORDER_MODEL_TIANYU_E800,
			ORDER_MODEL_AIGO_N700,
			ORDER_MODEL_ZTE_N880,
			ORDER_MODEL_COOLPAD_9930,
			ORDER_MODEL_SAMSUNG_I919,
			ORDER_MODEL_TCC8900,
			ORDER_MODEL_35_Q68,
			ORDER_MODEL_COOLPAD_N916,
			ORDER_MODEL_SAMSUNG_W999,
			ORDER_MODEL_SOAYE_E9,
			ORDER_MODEL_HTC_Z510D,
			ORDER_MODEL_ZTE_N880S,
			ORDER_MODEL_COOLPAD_9100,
			ORDER_MODEL_HUAWEI_S8600,
			ORDER_MODEL_HS_ET919,
			ORDER_MODEL_BK_PM700,
			ORDER_MODEL_SAMSUNG_I929,
			ORDER_MODEL_KYOCERA_M9300,
			ORDER_MODEL_KYOCERA_KSP8000,
			ORDER_MODEL_COOLPAD_9900,
			ORDER_MODEL_35_U705,
			ORDER_MODEL_35_U705_UPAD,
			ORDER_MODEL_SHARP_SH7218T,
			ORDER_MODEL_HUAWEI_C8810, ORDER_MODEL_OTHER_HUAWEI_C8810,
			ORDER_MODEL_COOLPAD_5860,
			ORDER_MODEL_HUAWEI_C8850,
			ORDER_MODEL_DETRON_1,
			ORDER_MODEL_HYN_AF1000FRO,
			ORDER_MODEL_HS_E910,
			ORDER_MODEL_TE_TE800,
			ORDER_MODEL_EPHONE_A5,		
			ORDER_MODEL_BRID_N760,
			ORDER_MODEL_XYT_D656,
			ORDER_MODEL_LENOVO_A790E,
			ORDER_MODEL_HS_EG900,
			ORDER_MODEL_GH_CHANGHONGC100,
			ORDER_MODEL_TW_S8210,
			ORDER_MODEL_TCL_C995,
			ORDER_MODEL_KUNUO_E188,
			ORDER_MODEL_HC_N86E,
			ORDER_MODEL_HC_N86E_2,
			ORDER_MODEL_ZTE_N880E,
			ORDER_MODEL_ZTE_N880E_2,
			ORDER_MODEL_CHL_S9000,
			ORDER_MODEL_CHL_S9100,
			ORDER_MODEL_SCH_I779,
			ORDER_MODEL_DT_S22,
			ORDER_MODEL_SAF_D633,
			ORDER_MODEL_YHY_Q8,
			ORDER_MODEL_HC_E760,
			ORDER_MODEL_YL_5860A,
			ORDER_MODEL_HW_C8812,
			ORDER_MODEL_YX_E96,
			ORDER_MODEL_HS_E920,
			ORDER_MODEL_SCH_I719,
			ORDER_MODEL_ZTE_N882E,
			ORDER_MODEL_YL_5880,
			ORDER_MODEL_HS_EG906,
			ORDER_MODEL_MYJ_E999,
			ORDER_MODEL_BLW_VE600,
			ORDER_MODEL_ZTE_N881D,
			ORDER_MODEL_HX_N719,
			ORDER_MODEL_HX_N819,
			ORDER_MODEL_YL_5866,
			ORDER_MODEL_YL_5870,
			ORDER_MODEL_LNV_A710E,
			ORDER_MODEL_CW_SKYE8,
			ORDER_MODEL_LNV_A700E,
			ORDER_MODEL_TM_D09S,
			ORDER_MODEL_TLT_S980D,
			ORDER_MODEL_YFZ_4S,
			ORDER_MODEL_HW_C8825D,
			ORDER_MODEL_XD_US900,
			ORDER_MODEL_ZY_N9,
			ORDER_MODEL_GX_EF930,
			ORDER_MODEL_ZTE_N880F,
			ORDER_MODEL_TCL_D662,
			ORDER_MODEL_YL_5216,
			ORDER_MODEL_GIO_C700,
			ORDER_MODEL_SCH_W9913,
			ORDER_MODEL_YL_5910,
			ORDER_MODEL_PXX_I330V,
			ORDER_MODEL_LNV_A600E,
			ORDER_MODEL_LNV_A600E_2,
			ORDER_MODEL_ZTE_N8010,
			ORDER_MODEL_GIO_C610,
			ORDER_MODEL_YL_9120,
			ORDER_MODEL_HS_EG909,
			ORDER_MODEL_HS_E926,
			ORDER_MODEL_ZTE_N878,
			ORDER_MODEL_YL_5860S,
			ORDER_MODEL_KUNUO_EG189,
			ORDER_MODEL_ZTE_N881E,
			ORDER_MODEL_SAF_D833,
			ORDER_MODEL_HTC_T329D,
			ORDER_MODEL_HW_C8833D,
			ORDER_MODEL_TCL_D668,
			ORDER_MODEL_HW_C8826D,
			ORDER_MODEL_SCH_W2013,
			ORDER_MODEL_SCH_I829,
			ORDER_MODEL_SCH_I759,
			ORDER_MODEL_HTC_T327D,
			ORDER_MODEL_HC_E700,
			ORDER_MODEL_ZTE_N807,
			ORDER_MODEL_GIO_C605,
			ORDER_MODEL_SCH_I739,
			ORDER_MODEL_TCL_D920,
			ORDER_MODEL_CHL_S9500,
			ORDER_MODEL_HS_E930,
			ORDER_MODEL_BLF_2900,
			ORDER_MODEL_FDT_E8,
			ORDER_MODEL_HST_CD801,
			ORDER_MODEL_GXT_E880,
			ORDER_MODEL_DT_S26,
			ORDER_MODEL_GX_EF88,
			ORDER_MODEL_JCT_A8,
			ORDER_MODEL_TM_D99SW,
			ORDER_MODEL_ZY_N9X,
			ORDER_MODEL_BR_W69,
			ORDER_MODEL_BLW_VE63,
			ORDER_MODEL_SCH_I879,
			ORDER_MODEL_WW_H200,
			// 540*960
			ORDER_MODEL_MOTO_XT882,
			ORDER_MODEL_MOTO_XT883,
			ORDER_MODEL_HTC_X515D,
			ORDER_MODEL_MOTO_XT928,
			ORDER_MODEL_SHARP_SH831T, 
			ORDER_MODEL_ZTE_N970,
			ORDER_MODEL_HW_C8950D,
			ORDER_MODEL_HW_C8951D,
			ORDER_MODEL_HW_C8813,
			ORDER_MODEL_CHL_S9388,
			ORDER_MODEL_PXX_I700V,
			ORDER_MODEL_SH_X5,
			ORDER_MODEL_LNV_A630E,
			ORDER_MODEL_LNV_A630E_2,
			ORDER_MODEL_HS_E956,
			ORDER_MODEL_BLW_VE65,
			// 240*320
			ORDER_MODEL_MOTO_XT301,
			ORDER_MODEL_ZTE_N600,
			ORDER_MODEL_HW_C8500,
			ORDER_MODEL_E230A,
			ORDER_MODEL_ZTE_N606,
			ORDER_MODEL_SAMSUNG_I559_1,
			ORDER_MODEL_SAMSUNG_I559_2,
			ORDER_MODEL_EPHONE_A9,
			ORDER_MODEL_ZTE_N600_PLUS,
			ORDER_MODEL_HESENS_N100,
			ORDER_MODEL_HTC_BEE,
			ORDER_MODEL_HS_E89,
			ORDER_MODEL_ZTE_X920,
			ORDER_MODEL_TE_TE600,
			ORDER_MODEL_HUALU_S800,
			ORDER_MODEL_ZTE_N700,
			ORDER_MODEL_CHER_A4,
			ORDER_MODEL_TONGWEI_X2011,
			ORDER_MODEL_EPHONE_A8,
			ORDER_MODEL_TIANYU_E610,
			ORDER_MODEL_HS_E839,
			ORDER_MODEL_BIRD_AE710,
			ORDER_MODEL_SKYWORTH_PE89,
			ORDER_MODEL_HS_E87,
			ORDER_MODEL_HUALU_ES2000,
			ORDER_MODEL_LANTIAN_S100,
			ORDER_MODEL_KINGPAD_N600,
			ORDER_MODEL_CHL_S850,
			ORDER_MODEL_HUAWEI_C8500SR,
			ORDER_MODEL_BROR_S06,
			ORDER_MODEL_YUXIN_E60,
			ORDER_MODEL_YUXIN_E60_2,
			ORDER_MODEL_ZTE_N601,
			ORDER_MODEL_SKYWORTH_ES2000,
			ORDER_MODEL_FLY_N5,
			ORDER_MODEL_HUAWEI_C8550,
			ORDER_MODEL_SAMSUNG_I339,
			ORDER_MODEL_SJX_S7,
			ORDER_MODEL_SCH_I519,
			ORDER_MODEL_SCH_I509U,
			ORDER_MODEL_SCH_I639,
			ORDER_MODEL_BLW_VE200,
			ORDER_MODEL_YL_5010,
			ORDER_MODEL_BLF_2600,
			ORDER_MODEL_BLF_1600,
			ORDER_MODEL_GX_EF55,
			ORDER_MODEL_BLW_VE360,
			ORDER_MODEL_SH_I60,
			// 320*480
			ORDER_MODEL_R750, ORDER_MODEL_C8600, ORDER_MODEL_D530,
			ORDER_MODEL_E600, ORDER_MODEL_COOLPAD_D539,
			ORDER_MODEL_COOLPAD_D5800, ORDER_MODEL_COOLPAD_N950,
			ORDER_MODEL_SAMSUNG_I579, ORDER_MODEL_HAIER_N6E,
			ORDER_MODEL_TIANYU_E600, ORDER_MODEL_COOLPAD_E239,
			ORDER_MODEL_SAMSUNG_I569, ORDER_MODEL_ZTE_R750PLUS,
			ORDER_MODEL_HTC_HERO200, ORDER_MODEL_SANMEIQI_EA6000,
			ORDER_MODEL_HUAWEI_C8650, ORDER_MODEL_FLY_N6,
			ORDER_MODEL_GUANGXIN_E900, ORDER_MODEL_GAOXINQI_ES608,
			ORDER_MODEL_SKYWORTH_PE10, ORDER_MODEL_HESENS_N200,
			ORDER_MODEL_HTC_MARVEL, ORDER_MODEL_TONGlIN_T90,
			ORDER_MODEL_ZTE_N760, ORDER_MODEL_HS_EG968B, ORDER_MODEL_SHX_S06,
			ORDER_MODEL_HS_E86, ORDER_MODEL_EPHONE_A6, ORDER_MODEL_LENOVO_A68E,
			ORDER_MODEL_FLY_N8, ORDER_MODEL_VIA_P2, ORDER_MODEL_KINGPAD_E239,
			ORDER_MODEL_LENOVO_A1, ORDER_MODEL_EXUN_D99, ORDER_MODEL_LG_CS600,
			ORDER_MODEL_ZC_ZC600, ORDER_MODEL_COOLPAD_5820,
			ORDER_MODEL_HC_EGE600, ORDER_MODEL_TONGlIN_T98,
			ORDER_MODEL_BIRD_AE750, ORDER_MODEL_DC_E366, ORDER_MODEL_QL_A709,
			ORDER_MODEL_HW_S8520, ORDER_MODEL_YUXIN_E80,
			ORDER_MODEL_SAIHONG_I90, ORDER_MODEL_HEXIN_N300,
			ORDER_MODEL_HC_N710E, ORDER_MODEL_COOLPAD_5855,
			ORDER_MODEL_HC_E899, ORDER_MODEL_DZD_EC600, ORDER_MODEL_ZTE_N780,
			ORDER_MODEL_BENWEI_5100, ORDER_MODEL_HC_N720E,
			ORDER_MODEL_SAMSUNG_I579I, ORDER_MODEL_SOAYE_E6,
			ORDER_MODEL_TCL_C990, ORDER_MODEL_LENOVO_A390E,
			ORDER_MODEL_SKYWORTH_PE90,
			ORDER_MODEL_CHL_S3000,
			ORDER_MODEL_ZY_N7,
			ORDER_MODEL_SED_S508EG,
			ORDER_MODEL_YUSUN_E88,
			ORDER_MODEL_YUSUN_E70,
			ORDER_MODEL_COOLPAD_5899,
			ORDER_MODEL_YCT_TE690,
			ORDER_MODEL_SHARP_SH320T,
			ORDER_MODEL_ZTE_X500,
			ORDER_MODEL_HUAWEI_C8650E,
			ORDER_MODEL_HEXIN_N800,
			ORDER_MODEL_HEXIN_N700,
			ORDER_MODEL_HEXIN_HE_N700,
			ORDER_MODEL_HS_E860,
			ORDER_MODEL_SAIHONG_I91,
			ORDER_MODEL_SAIHON_I91,
			ORDER_MODEL_EBEST_E68,
			ORDER_MODEL_JWG_E86,
			ORDER_MODEL_SCH_I619,
			ORDER_MODEL_SCH_I619_2,
			ORDER_MODEL_BR_S9,
			ORDER_MODEL_GX_E920,
			ORDER_MODEL_HC_N620E,			
			ORDER_MODEL_TLT_S600D,
			ORDER_MODEL_SAIHONG_I98,
			ORDER_MODEL_YUSUN_E89,
			ORDER_MODEL_SHARP_SH330T,
			ORDER_MODEL_TLT_S800,
			ORDER_MODEL_TLT_S800_2,
			ORDER_MODEL_HUAWEI_C8655_1,
			ORDER_MODEL_HUAWEI_C8655_2,
			ORDER_MODEL_YFZ_A10,
			ORDER_MODEL_CHL_S3500,			
			ORDER_MODEL_HC_N610E,
			ORDER_MODEL_HC_E617,
			ORDER_MODEL_BR_W60,
			ORDER_MODEL_SAIHONG_I901,
			ORDER_MODEL_JST_J868,
			ORDER_MODEL_HE_N620E,
			ORDER_MODEL_SW_Q3510,
			ORDER_MODEL_MOT_XT553,
			ORDER_MODEL_SJX_S11,
			ORDER_MODEL_LNV_A560E,
			ORDER_MODEL_ZTE_N855,
			ORDER_MODEL_YL_5210,
			ORDER_MODEL_TW_S8510,
			ORDER_MODEL_ZTE_V6700,
			ORDER_MODEL_ZTE_V6700_2,
			ORDER_MODEL_GIO_C500,
			ORDER_MODEL_GIO_C600,
			ORDER_MODEL_CHL_S8300,
			ORDER_MODEL_SCH_I919U,
			ORDER_MODEL_YL_5110,
			ORDER_MODEL_HS_EG870,
			ORDER_MODEL_SH_I97,
			ORDER_MODEL_CHL_S3000B,
			ORDER_MODEL_ZTE_N855D,
			ORDER_MODEL_GIO_C900,
			ORDER_MODEL_TW_S8310,
			ORDER_MODEL_HX_N739,
			ORDER_MODEL_SCH_I659,
			ORDER_MODEL_XD_S6000,
			ORDER_MODEL_TW_S8800,
			ORDER_MODEL_TLT_S800D, 
			ORDER_MODEL_TM_D99S,
			ORDER_MODEL_BLW_VE509,
			ORDER_MODEL_TF_C800,
			ORDER_MODEL_YFZ_E6A,
			ORDER_MODEL_TCL_C990P,
			ORDER_MODEL_TY_E619,
			ORDER_MODEL_BK_X903,
			ORDER_MODEL_SM_EG800,
			ORDER_MODEL_PXX_K210V,
			ORDER_MODEL_ZTE_N855DP,
			ORDER_MODEL_JCT_A107,
			ORDER_MODEL_HW_C8685D,
			ORDER_MODEL_HW_C8685D_2,
			ORDER_MODEL_HS_E830,
			ORDER_MODEL_YX_E66,
			ORDER_MODEL_TLT_S600P,
			ORDER_MODEL_YL_5210S,
			ORDER_MODEL_TCL_D510,
			ORDER_MODEL_GX_EF78,
			ORDER_MODEL_BLF_1800,
			ORDER_MODEL_CW_EG6188,
			ORDER_MODEL_JST_J699,
			ORDER_MODEL_MYJ_E86,
			ORDER_MODEL_BR_W68,
			ORDER_MODEL_BLF_2800,
			ORDER_MODEL_SMQ_EA6800,
			ORDER_MODEL_TW_X1,
			ORDER_MODEL_GX_EF68,
			ORDER_MODEL_JCT_A109,
			ORDER_MODEL_YFZ_8S,
			ORDER_MODEL_TM_G19,
			// 其它分辨率
			ORDER_MODEL_LIFEPAD, ORDER_MODEL_AUSCLOUD_PAD,
			ORDER_MODEL_LION_PAD_I600, ORDER_MODEL_LION_PAD_A1,
			ORDER_MODEL_WST_A700, ORDER_MODEL_MALATA_T8,
			ORDER_MODEL_SAMSUNG_P739, ORDER_MODEL_EBEN_T3,
			ORDER_MODEL_UT_P200E, ORDER_MODEL_SAMSUNG_I889,
			ORDER_MODEL_SAMSUNG_I9250,
			ORDER_MODEL_MOT_XT889,
			ORDER_MODEL_HTC_T328d,
			ORDER_MODEL_SAMSUNG_I939,
			ORDER_MODEL_HTC_X720D,
			ORDER_MODEL_PXX_FPAD,
			ORDER_MODEL_SND_LT25C,
			ORDER_MODEL_MOT_XT785, 
			ORDER_MODEL_HTC_T528D,
			ORDER_MODEL_YL_9960,
			ORDER_MODEL_YL_9960_2,
			ORDER_MODEL_LNV_S870E,
			ORDER_MODEL_LNV_S870E_2,
			ORDER_MODEL_LNV_S870E_3,
			ORDER_MODEL_SCH_I959,
			ORDER_MODEL_MOT_XT788,
			ORDER_MODEL_GIO_C800,
			ORDER_MODEL_SHP_SH630T,
			ORDER_MODEL_SHP_SH630T_2,
			ORDER_MODEL_SCH_I939D,
			ORDER_MODEL_SCH_I939D_2,
			ORDER_MODEL_HS_EG950,
			ORDER_MODEL_SCH_N719,
			ORDER_MODEL_HE_E80,
			ORDER_MODEL_ZTE_N880G,
			ORDER_MODEL_YL_5876,
			ORDER_MODEL_ZTE_N983,
			ORDER_MODEL_XD_U1203,
			ORDER_MODEL_HW_D2,
			ORDER_MODEL_TCL_D706,
			ORDER_MODEL_YL_5890,
			ORDER_MODEL_TLT_S939D,
			ORDER_MODEL_BLW_E2013,
			ORDER_MODEL_BLW_E2013_2,
			ORDER_MODEL_ZTE_N881F,
			ORDER_MODEL_JSR_D9C,
			ORDER_MODEL_LVN_A820E,
			ORDER_MODEL_YL_5930,
			ORDER_MODEL_TF_C820,
			ORDER_MODEL_YL_9070,
			ORDER_MODEL_JSR_I6C,
			ORDER_MODEL_SND_M35H,
			// COMMON
			ORDER_MODEL_COMMON 
			};
 
	public static final String CLIENT_VERSION_DATE = "2013年10月24日";
	
	public static final String DEBUG_VERSION = "2.9.9 alpha 0719";
 
  
	
	// DEBUG联网时间输出
	public static final boolean IS_DEBUG_LOG = false;

	public static final boolean UNSUPPORT_CTWAP = false;
	
	public static int screenWidth;
	public static int screenHeight;
	public static int readerGalleryIndex;

	public static void init(Context context) {
//		//XXX 渠道版本号是写死的，2.9.8版本开始改为通用版本号为渠道版本号，在渠道安装的版本，
//		//    适配过的还是显示适配的版本号，没适配过的显示渠道版本号
//		if(ChannelVersionUtil.IS_CHANNEL) {
//			CLIENT_VERSION_COMMON = ChannelVersionUtil.VERSION;
//		}
		CLIENT_VERSION = macthClientVersion(orderModel,context);
//		//XXX 2.9.9版本开始不再使用渠道版本号，改为包头提交渠道标识。。by mingkg21
//		if(ChannelVersionUtil.IS_CHANNEL){
//			CLIENT_VERSION = ChannelVersionUtil.VERSION;
//		}
		//XXX 2.9.9版本开始处理
		ChannelVersionUtil.init(context);
//		if(LeyueConst.IS_DEBUG){
//			/*if(HttpConnect.TAG_READ_TEST_NET_SITE.equals(PreferencesUtil.getInstance(MyAndroidApplication.getInstance()).getNetSiteRead())){
//				CLIENT_VERSION = "TYYD_Android_JAVA_2_9_9";
//			}*/
//		}
//		CLIENT_VERSION = "TYYD_Android_JAVA_2_9_6";
//		CLIENT_VERSION = "TYYD_Android_2_3_540_960_MOT_XT882_JAVA_2_9_8";
//		CLIENT_VERSION = "TYYD_Android_2_2_480_800_HTC_VIVO_JAVA_2_6_0";
		clientResolution = matchDisplay(context, screenWidth, screenHeight);
		// clientResolution = CLIENT_RESOLUTION_240_320;
		// clientResolution = CLIENT_RESOLUTION_320_480;
		// CLIENT_VERSION = CLIENT_VERSION_XT800;
		
		/*begin add by xzz 2012-07-12*/
		//三星渠道版本开关，
  		checkIsSamSungFacturer();
		/*end by xzz 2012-07-12*/
		
 		/*begin add by xzz 2012-08-20*/
 		if(isHTCNonTTSSpecial(context)){
 			isTTSOpen = false;
 		}
 		/*end by xzz 2012-08-20*/
 		
		StringBuilder sb = new StringBuilder();
		sb.append(CLIENT_VERSION);
		sb.append("/");
		sb.append(clientResolution);
		sb.append("/");
		sb.append(manufacturer);
		sb.append("_");
		sb.append(Build.MODEL);
		CLIENT_AGENT = sb.toString();
		// isConnect = false;
		// isCtwapLogin = false;
		// setUnConnectStatus(context);
		readerGalleryIndex = 0;
		//CommonUtil.initLog();
	}

	 /*begin add by xzz 2012-07-12*/
	private static void checkIsSamSungFacturer(){
	//	if(Build.MANUFACTURER.equals(Manufacturer.SAMSUNG)){
//		if(ChannelVersionUtil.IS_CHANNEL &&
//				(ChannelVersionUtil.VERSION.indexOf("SX012") > -1)){
//			isSamSungAppOpen = true;
//		} else{
//			isSamSungAppOpen = false;
//		}
		if(ChannelVersionUtil.isSamsungChannel()){
			isSamSungAppOpen = true;
		} else{
			isSamSungAppOpen = false;
		}
	}
	/*end by xzz 2012-07-12*/

	public static boolean isOrder(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		sdkVersion = Build.VERSION.SDK_INT;
		manufacturer = Build.MANUFACTURER;
		// if(/*IS_DEBUG || */CommonUtil.isEmulator(context)){
		// orderModel = ORDER_MODEL_I909;
		// return true;
		// }
		String tempModel = matchOrder(Build.MODEL);
		if (TextUtils.isEmpty(tempModel)) {
			orderModel = ORDER_MODEL_COMMON;
			return true;
		}
		orderModel = tempModel;
//		 orderModel = ORDER_MODEL_COMMON;
//		 orderModel = ORDER_MODEL_XT800;
		// orderModel = ORDER_MODEL_HUAWEI_C8800;

		return true;
	}

	private static String matchDisplay(Context context, int width, int height) {
//		if (width == 480) {
//			if (height == 854) {
//				return CLIENT_RESOLUTION_480_854;
//			} else if (height == 800) {
//				return CLIENT_RESOLUTION_480_800;
//			}
//		} else if (width == 320) {
//			return CLIENT_RESOLUTION_320_480;
//		} else if (width == 240) {
//			return CLIENT_RESOLUTION_240_320;
//		}/* else {
//			StringBuilder sb = new StringBuilder();
//			if(width < height){
//				sb.append(width);
//				sb.append("*");
//				sb.append(height);
//			}else{
//				sb.append(height);
//				sb.append("*");
//				sb.append(width);
//			}
//			return sb.toString();
//		}*/
////		else if(width == 720){
////			return CLIENT_RESOLUTION_720_1280;
////		}
//		return CLIENT_RESOLUTION_480_854;
		/*begin add by xzz 2012-07-13*/
		if (getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_PXX_FPAD)){
			return CLIENT_RESOLUTION_540_960;
		} else{
			StringBuilder sb = new StringBuilder();
			if(width < height){
				sb.append(width);
				sb.append("*");
				sb.append(height);
			} else {
				sb.append(height);
				sb.append("*");
				sb.append(width);
			}
			return sb.toString();
		}
		/*end by xzz 2012-07-13*/
	}

	private static String matchOrder(String model) {
		// if(model.equals(ORDER_MODEL_HTC_VIVO_NEW)){
		// return ORDER_MODEL_HTC_VIVO;
		// }
		for (String str : ORDER_MODEL) {
			if (str.equals(model)) {
				return str;
			}
		}
		return null;
	}

	private static String macthClientVersion(String orderModel, Context context) {
		if (TextUtils.isEmpty(orderModel)) {
			return CLIENT_VERSION_COMMON;
		}
		if (orderModel.equals(ORDER_MODEL_XT800)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_XT800;
		} else if (orderModel.equals(ORDER_MODEL_XT806)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_XT806;
		} else if (orderModel.equals(ORDER_MODEL_I909)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			// if(sdkVersion == SDK_VERSION_8){
			// return CLIENT_VERSION_I909_8;
			// }else{
			return CLIENT_VERSION_I909_7;
			// }
		} else if (orderModel.equals(ORDER_MODEL_W899)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_W899;
		} else if (orderModel.equals(ORDER_MODEL_COOLPAD_N930)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_COOLPAD_N930;
		} else if (orderModel.equals(ORDER_MODEL_ME811)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_ME811;
		} else if (orderModel.equals(ORDER_MODEL_HUAWEI_C8860E) ||
						orderModel.equals(ORDER_MODEL_HUAWEI_C8860E_2)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_HUAWEI_C8860E;
		}
		else if (orderModel.equals(ORDER_MODEL_XT800PLUS)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_XT800PLUS;
		} else if (orderModel.equals(ORDER_MODEL_XIAOMI_C1)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_XIAOMI_C1;
		} else if (orderModel.equals(ORDER_MODEL_LNV_S850E)||
						orderModel.equals(ORDER_MODEL_LNV_S850E_2) ) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_LNV_S850E;
		}  else if (orderModel.equals(ORDER_MODEL_MOT_XT681)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_MOT_XT681;
		} else if (orderModel.equals(ORDER_MODEL_XD_US910)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_XD_US910;
		} else if (orderModel.equals(ORDER_MODEL_XD_US920)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_XD_US920;
		} else if (orderModel.equals(ORDER_MODEL_LNV_A765E)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_LNV_A765E;
		} else if (orderModel.equals(ORDER_MODEL_TLT_S910D)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_TLT_S910D;
		} else if (orderModel.equals(ORDER_MODEL_XYT_E606)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_XYT_E606;
		} else if (orderModel.equals(ORDER_MODEL_BR_S3)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_BR_S3;
		} else if (orderModel.equals(ORDER_MODEL_FDT_E9)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_FDT_E9;
		}
		//480*800
		else if (orderModel.equals(ORDER_MODEL_LEPHONE)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			// if(sdkVersion == SDK_VERSION_4){
			return CLIENT_VERSION_LEPHONE_4;
			// }else{
			// return CLIENT_VERSION_LEPHONE_7;
			// }
		} else if (orderModel.equals(ORDER_MODEL_HTC_VIVO)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HTC_VIVO;
		} else if (orderModel.equals(ORDER_MODEL_HTC_EVO)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HTC_EVO;
		} else if (orderModel.equals(ORDER_MODEL_HTC_INCREDIBLE)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HTC_INCREDIBLE;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_V9E)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_ZTE_V9E;
		} else if (orderModel.equals(ORDER_MODEL_HUAWEI_C8800)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HUAWEI_C8800;
		} else if (orderModel.equals(ORDER_MODEL_TIANYU_E800)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_TIANYU_E800;
		} else if (orderModel.equals(ORDER_MODEL_AIGO_N700)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_AIGO_N700;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N880)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_ZTE_N880;
		} else if (orderModel.equals(ORDER_MODEL_COOLPAD_9930)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_COOLPAD_9930;
		} else if (orderModel.equalsIgnoreCase(ORDER_MODEL_SAMSUNG_I919)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_SAMSUNG_I919;
		} else if (orderModel.equalsIgnoreCase(ORDER_MODEL_TCC8900)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_E6;
		} else if (orderModel.equalsIgnoreCase(ORDER_MODEL_35_Q68)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_35_Q68;
		} else if (orderModel.equalsIgnoreCase(ORDER_MODEL_COOLPAD_N916)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_COOLPAD_N916;
		} else if (orderModel.equalsIgnoreCase(ORDER_MODEL_SAMSUNG_W999)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_SAMSUNG_W999;
		} else if (orderModel.equalsIgnoreCase(ORDER_MODEL_SOAYE_E9)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_SOAYE_E9;
		} else if (orderModel.equalsIgnoreCase(ORDER_MODEL_HTC_Z510D)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HTC_Z510D;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N880S)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_ZTE_N880S;
		} else if (orderModel.equals(ORDER_MODEL_COOLPAD_9100)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_COOLPAD_9100;
		} else if (orderModel.equals(ORDER_MODEL_HUAWEI_S8600)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HUAWEI_S8600;
		} else if (orderModel.equals(ORDER_MODEL_HS_ET919)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HS_ET919;
		} else if (orderModel.equals(ORDER_MODEL_BK_PM700)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_BOK_PM700;
		} else if (orderModel.equals(ORDER_MODEL_SAMSUNG_I929)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_SAMSUNG_I929;
		} else if (orderModel.equals(ORDER_MODEL_KYOCERA_M9300)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_KYOCERA_M9300;
		} else if (orderModel.equals(ORDER_MODEL_KYOCERA_KSP8000)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_KYOCERA_KSP8000;
		} else if (orderModel.equals(ORDER_MODEL_COOLPAD_9900)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_COOLPAD_9900;
		} else if (orderModel.equals(ORDER_MODEL_35_U705) ||
				orderModel.equals(ORDER_MODEL_35_U705_UPAD)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_35_U705;
		} else if (orderModel.equals(ORDER_MODEL_SHARP_SH7218T)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_SHARP_SH7218T;
		} else if (orderModel.equals(ORDER_MODEL_HUAWEI_C8810) || orderModel.equals(ORDER_MODEL_OTHER_HUAWEI_C8810)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HUAWEI_C8810;
		} else if (orderModel.equals(ORDER_MODEL_COOLPAD_5860)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_COOLPAD_5860;
		} else if (orderModel.equals(ORDER_MODEL_HUAWEI_C8850)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HUAWEI_C8850;
		} else if (orderModel.equals(ORDER_MODEL_DETRON_1)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_DETRON_1;
		} else if (orderModel.equals(ORDER_MODEL_HYN_AF1000FRO)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HYN_AF1000;
		} else if (orderModel.equals(ORDER_MODEL_HS_E910)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HS_E910;
		} else if (orderModel.equals(ORDER_MODEL_TE_TE800)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_TE_TE800;
		} else if (orderModel.equals(ORDER_MODEL_EPHONE_A5)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_EPHONE_A5;
		} else if (orderModel.equals(ORDER_MODEL_BRID_N760)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_BRID_N760;
		} else if (orderModel.equals(ORDER_MODEL_XYT_D656)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_XYT_D656;
		}  else if (orderModel.equals(ORDER_MODEL_LENOVO_A790E)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_LENOVO_A790E;
		}  else if (orderModel.equals(ORDER_MODEL_HS_EG900)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HS_EG900;
		} else if (orderModel.equals(ORDER_MODEL_GH_CHANGHONGC100)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_CH_C100;
		} else if (orderModel.equals(ORDER_MODEL_TW_S8210)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_TW_S8210;
		} else if (orderModel.equals(ORDER_MODEL_TCL_C995)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_TCL_C995;
		} else if (orderModel.equals(ORDER_MODEL_KUNUO_E188)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_KUNUO_E188;
		} else if (orderModel.equals(ORDER_MODEL_HC_N86E) ||
						orderModel.equals(ORDER_MODEL_HC_N86E_2)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HC_N86E;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N880E) ||
						orderModel.equals(ORDER_MODEL_ZTE_N880E_2)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_ZTE_N880E;
		} else if (orderModel.equals(ORDER_MODEL_CHL_S9000)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_CHL_S9000;
		} else if (orderModel.equals(ORDER_MODEL_CHL_S9100)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_CHL_S9100;
		} else if (orderModel.equals(ORDER_MODEL_SCH_I779)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_SCH_I779;
		} else if (orderModel.equals(ORDER_MODEL_DT_S22)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_DT_S22;
		} else if (orderModel.equals(ORDER_MODEL_SAF_D633)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_SAF_D633;
		} else if (orderModel.equals(ORDER_MODEL_YHY_Q8)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_YHY_Q8;
		} else if (orderModel.equals(ORDER_MODEL_HC_E760)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HC_E760;
		} else if (orderModel.equals(ORDER_MODEL_YL_5860A)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_YL_5860A;
		} else if (orderModel.equals(ORDER_MODEL_HW_C8812)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HW_C8812;
		} else if (orderModel.equals(ORDER_MODEL_YX_E96)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_YX_E96;
		} else if (orderModel.equals(ORDER_MODEL_HS_E920)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HS_E920;
		} else if (orderModel.equals(ORDER_MODEL_SCH_I719)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_SCH_I719;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N882E)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_ZTE_N882E;
		} else if (orderModel.equals(ORDER_MODEL_YL_5880)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_YL_5880;
		} else if (orderModel.equals(ORDER_MODEL_HS_EG906)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HS_EG906;
		} else if (orderModel.equals(ORDER_MODEL_MYJ_E999)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_MYJ_E999;
		} else if (orderModel.equals(ORDER_MODEL_BLW_VE600)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_BLW_VE600;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N881D)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_ZTE_N881D;
		} else if (orderModel.equals(ORDER_MODEL_HX_N719)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HX_N719;
		} else if (orderModel.equals(ORDER_MODEL_HX_N819)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HX_N819;
		} else if (orderModel.equals(ORDER_MODEL_YL_5866)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_YL_5866;
		} else if (orderModel.equals(ORDER_MODEL_YL_5870)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_YL_5870;
		} else if (orderModel.equals(ORDER_MODEL_LNV_A710E)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_LNV_A710E;
		} else if (orderModel.equals(ORDER_MODEL_CW_SKYE8)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_CW_SKYE8;
		} else if (orderModel.equals(ORDER_MODEL_LNV_A700E)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_LNV_A700E;
		} else if (orderModel.equals(ORDER_MODEL_TM_D09S)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_TM_D09S;
		} else if (orderModel.equals(ORDER_MODEL_TLT_S980D)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_TLT_S980D;
		} else if (orderModel.equals(ORDER_MODEL_YFZ_4S)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_YFZ_4S;
		} else if (orderModel.equals(ORDER_MODEL_HW_C8825D)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HW_C8825D;
		} else if (orderModel.equals(ORDER_MODEL_XD_US900)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_XD_US900;
		} else if (orderModel.equals(ORDER_MODEL_ZY_N9)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_ZY_N9;
		} else if (orderModel.equals(ORDER_MODEL_GX_EF930)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_GX_EF930;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N880F)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_ZTE_N880F;
		} else if (orderModel.equals(ORDER_MODEL_TCL_D662)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_TCL_D662;
		} else if (orderModel.equals(ORDER_MODEL_YL_5216)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_YL_5216;
		} else if (orderModel.equals(ORDER_MODEL_GIO_C700)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_GIO_C700;
		} else if (orderModel.equals(ORDER_MODEL_SCH_W9913)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_SCH_W889;
		} else if (orderModel.equals(ORDER_MODEL_YL_5910)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_YL_5910;
		} else if (orderModel.equals(ORDER_MODEL_PXX_I330V)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_PXX_I330V;
		} else if (orderModel.equals(ORDER_MODEL_LNV_A600E) ||
						orderModel.equals(ORDER_MODEL_LNV_A600E_2)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_LNV_A600E;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N8010)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_ZTE_N8010;
		} else if (orderModel.equals(ORDER_MODEL_GIO_C610)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_GIO_C610;
		} else if (orderModel.equals(ORDER_MODEL_YL_9120)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_YL_9120;
		} else if (orderModel.equals(ORDER_MODEL_HS_EG909)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HS_EG909;
		} else if (orderModel.equals(ORDER_MODEL_HS_E926)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HS_E926;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N878)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_ZTE_N878;
		} else if (orderModel.equals(ORDER_MODEL_YL_5860S)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_YL_5860S;
		} else if (orderModel.equals(ORDER_MODEL_KUNUO_EG189)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_KUNUO_EG189;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N881E)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_ZTE_N881E;
		} else if (orderModel.equals(ORDER_MODEL_SAF_D833)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_SAF_D833;
		} else if (orderModel.equals(ORDER_MODEL_HTC_T329D)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HTC_T329D;
		} else if (orderModel.equals(ORDER_MODEL_HW_C8833D)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HW_C8833D;
		} else if (orderModel.equals(ORDER_MODEL_TCL_D668)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_TCL_D668;
		} else if (orderModel.equals(ORDER_MODEL_HW_C8826D)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HW_C8826D;
		} else if (orderModel.equals(ORDER_MODEL_SCH_W2013)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_SCH_W2013;
		} else if (orderModel.equals(ORDER_MODEL_SCH_I829)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_SCH_I829;
		} else if (orderModel.equals(ORDER_MODEL_SCH_I759)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_SCH_I759;
		} else if (orderModel.equals(ORDER_MODEL_HTC_T327D)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HTC_T327D;
		} else if (orderModel.equals(ORDER_MODEL_HC_E700)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HC_E700;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N807)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_ZTE_N807;
		} else if (orderModel.equals(ORDER_MODEL_GIO_C605)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_GIO_C605;
		} else if (orderModel.equals(ORDER_MODEL_SCH_I739)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_SCH_I739;
		} else if (orderModel.equals(ORDER_MODEL_TCL_D920)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_TCL_D920;
		} else if (orderModel.equals(ORDER_MODEL_CHL_S9500)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_CHL_S9500;
		} else if (orderModel.equals(ORDER_MODEL_HS_E930)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HS_E930;
		} else if (orderModel.equals(ORDER_MODEL_BLF_2900)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_BLF_2900;
		} else if (orderModel.equals(ORDER_MODEL_FDT_E8)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_FDT_E8;
		} else if (orderModel.equals(ORDER_MODEL_HST_CD801)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_HST_CD801;
		} else if (orderModel.equals(ORDER_MODEL_GXT_E880)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_GXT_E880;
		} else if (orderModel.equals(ORDER_MODEL_DT_S26)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_DT_S26;
		} else if (orderModel.equals(ORDER_MODEL_GX_EF88)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_GX_EF88;
		} else if (orderModel.equals(ORDER_MODEL_JCT_A8)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_JCT_A8;
		} else if (orderModel.equals(ORDER_MODEL_TM_D99SW)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_TM_D99SW;
		} else if (orderModel.equals(ORDER_MODEL_ZY_N9X)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_ZY_N9X;
		} else if (orderModel.equals(ORDER_MODEL_BR_W69)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_BR_W69;
		} else if (orderModel.equals(ORDER_MODEL_BLW_VE63)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_BLW_VE63;
		} else if (orderModel.equals(ORDER_MODEL_SCH_I879)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_SCH_I879;
		} else if (orderModel.equals(ORDER_MODEL_WW_H200)) {
			clientResolution = CLIENT_RESOLUTION_480_800;
			return CLIENT_VERSION_WW_H200;
		}
		// 540*960
		else if (orderModel.equals(ORDER_MODEL_MOTO_XT882)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_MOTO_XT882;
		} else if (orderModel.equals(ORDER_MODEL_MOTO_XT883)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_MOTO_XT883;
		} else if (orderModel.equals(ORDER_MODEL_HTC_X515D)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_HTC_X515D;
		} else if (orderModel.equals(ORDER_MODEL_MOTO_XT928)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_MOTO_XT928;
		} else if (orderModel.equals(ORDER_MODEL_SHARP_SH831T)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_SHARP_SH831T;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N970)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_ZTE_N970;
		} else if (orderModel.equals(ORDER_MODEL_HW_C8950D)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_HW_C8950D;
		} else if (orderModel.equals(ORDER_MODEL_HW_C8951D)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_HW_C8951D;
		} else if(orderModel.equals(ORDER_MODEL_HW_C8813)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_HW_C8813;
		} else if(orderModel.equals(ORDER_MODEL_CHL_S9388)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_CHL_S9388;
		} else if(orderModel.equals(ORDER_MODEL_PXX_I700V)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_PXX_I700V;
		} else if(orderModel.equals(ORDER_MODEL_SH_X5)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_SH_X5;
		} else if(orderModel.equals(ORDER_MODEL_LNV_A630E) ||
				   orderModel.equals(ORDER_MODEL_LNV_A630E_2)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_LNV_A630E;
		} else if(orderModel.equals(ORDER_MODEL_HS_E956)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_HS_E956;
		} else if(orderModel.equals(ORDER_MODEL_BLW_VE65)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_BLW_VE65;
		}
		// 320*480
		else if (orderModel.equals(ORDER_MODEL_R750)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_R750;
		} else if (orderModel.equals(ORDER_MODEL_C8600)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_C8600;
		} else if (orderModel.equals(ORDER_MODEL_D530)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_D530;
		} else if (orderModel.equals(ORDER_MODEL_E600)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_E600;
		} else if (orderModel.equals(ORDER_MODEL_HC_EGE600)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HC_EGE600;
		} else if (orderModel.equals(ORDER_MODEL_COOLPAD_D539)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_COOLPAD_D539;
		} else if (orderModel.equals(ORDER_MODEL_COOLPAD_D5800)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_COOLPAD_D5800;
		} else if (orderModel.equals(ORDER_MODEL_COOLPAD_5820)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_COOLPAD_5820;
		} else if (orderModel.equals(ORDER_MODEL_COOLPAD_N950)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_COOLPAD_N950;
		} else if (orderModel.equals(ORDER_MODEL_SAMSUNG_I579)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SAMSUNG_I579;
		} else if (orderModel.equals(ORDER_MODEL_HAIER_N6E)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HAIER_N6E;
		} else if (orderModel.equals(ORDER_MODEL_TIANYU_E600)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_TIANYU_E600;
		} else if (orderModel.equals(ORDER_MODEL_COOLPAD_E239)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_COOLPAD_E239;
		} else if (orderModel.equals(ORDER_MODEL_SAMSUNG_I569)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SAMSUNG_I569;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_R750PLUS)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_ZTE_R750PLUS;
		} else if (orderModel.equals(ORDER_MODEL_HTC_HERO200)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HTC_HERO200;
		} else if (orderModel.equals(ORDER_MODEL_SANMEIQI_EA6000)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SANMEIQI_EA6000;
		} else if (orderModel.equals(ORDER_MODEL_HUAWEI_C8650)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HUAWEI_C8650;
		} else if (orderModel.equals(ORDER_MODEL_GAOXINQI_ES608)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_GXQ_ES608;
		} else if (orderModel.equals(ORDER_MODEL_SKYWORTH_PE10)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SKYWOTH_PE10;
		} else if (orderModel.equals(ORDER_MODEL_HESENS_N200)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HESENS_N200;
		} else if (orderModel.equals(ORDER_MODEL_HTC_MARVEL)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HTC_MARVEL;
		} else if (orderModel.equals(ORDER_MODEL_TONGlIN_T90)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_TONGLIN_T90;
		} else if (orderModel.equals(ORDER_MODEL_TONGlIN_T98)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_TONGLIN_T98;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N760)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_ZTE_N760;
		} else if (orderModel.equals(ORDER_MODEL_HS_EG968B)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HS_EG968B;
		} else if (orderModel.equals(ORDER_MODEL_SHX_S06)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SHX_S06;
		} else if (orderModel.equals(ORDER_MODEL_HS_E86)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HS_E86;
		} else if (orderModel.equals(ORDER_MODEL_EPHONE_A6)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_EPHONE_A6;
		} else if (orderModel.equals(ORDER_MODEL_LENOVO_A68E)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_LENOVO_A68E;
		} else if (orderModel.equals(ORDER_MODEL_FLY_N8)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_FLY_N8;
		} else if (orderModel.equals(ORDER_MODEL_VIA_P2)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_VIA_P2;
		} else if (orderModel.equals(ORDER_MODEL_KINGPAD_E239)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_KINGPAD_E239;
		} else if (orderModel.equals(ORDER_MODEL_LENOVO_A1)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_LENOVO_A1;
		} else if (orderModel.equals(ORDER_MODEL_EXUN_D99)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_EXUN_D99;
		} else if (orderModel.equals(ORDER_MODEL_LG_CS600)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_LG_CS600;
		} else if (orderModel.equals(ORDER_MODEL_BIRD_AE750)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_BIRD_AE750;
		} else if (orderModel.equals(ORDER_MODEL_DC_E366)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_DC_E366;
		} else if (orderModel.equals(ORDER_MODEL_ZC_ZC600)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_ZC_600;
		} else if (orderModel.equals(ORDER_MODEL_QL_A709)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_QL_A709;
		} else if (orderModel.equals(ORDER_MODEL_HW_S8520)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HW_S8520;
		} else if (orderModel.equals(ORDER_MODEL_YUXIN_E80)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_YUXIN_E80;
		} else if (orderModel.equals(ORDER_MODEL_SAIHONG_I90)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SAIHONG_I90;
		} else if (orderModel.equals(ORDER_MODEL_HEXIN_N300)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HEXIN_N300;
		} else if (orderModel.equals(ORDER_MODEL_HC_N710E)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HC_N710E;
		} else if (orderModel.equals(ORDER_MODEL_COOLPAD_5855)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_COOLPAD_5855;
		} else if (orderModel.equals(ORDER_MODEL_HC_E899)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HC_E899;
		} else if (orderModel.equals(ORDER_MODEL_DZD_EC600)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_DZD_EC600;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N780)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_ZTE_N780;
		} else if (orderModel.equals(ORDER_MODEL_BENWEI_5100)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_BENWEI_5100;
		} else if (orderModel.equals(ORDER_MODEL_HC_N720E)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HC_N720E;
		} else if (orderModel.equals(ORDER_MODEL_SAMSUNG_I579I)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SAMSUNG_I579I;
		} else if (orderModel.equals(ORDER_MODEL_SOAYE_E6)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SOAYE_E6;
		} else if (orderModel.equals(ORDER_MODEL_TCL_C990)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_TCL_C990;
		} else if (orderModel.equals(ORDER_MODEL_LENOVO_A390E)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_LENOVO_A390E;
		} else if (orderModel.equals(ORDER_MODEL_SKYWORTH_PE90)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SKYWORTH_PE90;
		} else if (orderModel.equals(ORDER_MODEL_ZY_N7)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_ZY_N7;
		} else if (orderModel.equals(ORDER_MODEL_CHL_S3000)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_CHL_S3000;
		} else if (orderModel.equals(ORDER_MODEL_SED_S508EG)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SED_S508EG;
		} else if (orderModel.equals(ORDER_MODEL_YUSUN_E88)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_YUSUN_E88;
		} else if (orderModel.equals(ORDER_MODEL_YUSUN_E70)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_YUSUN_E70;
		} else if (orderModel.equals(ORDER_MODEL_COOLPAD_5899)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_COOLPAD_5899;
		} else if (orderModel.equals(ORDER_MODEL_YCT_TE690)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_YCT_TE690;
		} else if (orderModel.equals(ORDER_MODEL_SHARP_SH320T)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SHARP_SH320T;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_X500)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_ZTE_X500;
		} else if (orderModel.equals(ORDER_MODEL_HUAWEI_C8650E)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HUAWEI_C8650E;
		} else if (orderModel.equals(ORDER_MODEL_HEXIN_N800)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HEXIN_N800;
		} else if (orderModel.equals(ORDER_MODEL_HEXIN_N700) ||
				orderModel.equals(ORDER_MODEL_HEXIN_HE_N700)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HEXIN_N700;
		} else if(orderModel.equals(ORDER_MODEL_HS_E860)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HS_E860;
		} else if(orderModel.equals(ORDER_MODEL_EBEST_E68)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_EBEST_E68;
		} else if(orderModel.equals(ORDER_MODEL_SAIHONG_I91) ||
					orderModel.equals(ORDER_MODEL_SAIHON_I91)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SAIHONG_I91;
		} else if(orderModel.equals(ORDER_MODEL_JWG_E86)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_JWG_E86;
		} else if(orderModel.equals(ORDER_MODEL_SCH_I619) ||
					orderModel.equals(ORDER_MODEL_SCH_I619_2)){
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SCH_I619;
		} else if(orderModel.equals(ORDER_MODEL_BR_S9)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_BR_S9;
		} else if(orderModel.equals(ORDER_MODEL_GX_E920)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_GX_E920;
		} else if(orderModel.equals(ORDER_MODEL_HC_N620E)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HC_N620E;
		} else if(orderModel.equals(ORDER_MODEL_TLT_S600D)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_TLT_S600D;
		} else if(orderModel.equals(ORDER_MODEL_SAIHONG_I98)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SAIHONG_I98;
		} else if(orderModel.equals(ORDER_MODEL_YUSUN_E89) &&
				(ClientInfoUtil.getManufacturer(context.getApplicationContext()).equals(Manufacturer.YUSUN1) ||
				ClientInfoUtil.getManufacturer(context.getApplicationContext()).equals(Manufacturer.YUSUN))) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_YUSUN_E89;
		} else if(orderModel.equals(ORDER_MODEL_SHARP_SH330T)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SHARP_SH330T;
		} else if(orderModel.equals(ORDER_MODEL_TLT_S800) ||
				orderModel.equals(ORDER_MODEL_TLT_S800_2)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_TLT_S800;
		} else if (orderModel.equals(ORDER_MODEL_HUAWEI_C8655_1) ||
				orderModel.equals(ORDER_MODEL_HUAWEI_C8655_2)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HUAWEI_C8655;
		} else if (orderModel.equals(ORDER_MODEL_YFZ_A10)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_YFZ_A10;
		} else if (orderModel.equals(ORDER_MODEL_CHL_S3500)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_CHL_S3500;
		} else if (orderModel.equals(ORDER_MODEL_HC_N610E)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HC_N610E;
		} else if (orderModel.equals(ORDER_MODEL_HC_E617)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HC_E617;
		} else if (orderModel.equals(ORDER_MODEL_BR_W60)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_BR_W60;
		} else if (orderModel.equals(ORDER_MODEL_SAIHONG_I901)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SAIHONG_I901;
		} else if (orderModel.equals(ORDER_MODEL_JST_J868)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_JST_J868;
		} else if (orderModel.equals(ORDER_MODEL_HE_N620E)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HE_N620E;
		} else if (orderModel.equals(ORDER_MODEL_SW_Q3510)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SW_Q3510;
		} else if (orderModel.equals(ORDER_MODEL_MOT_XT553)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_MOT_XT553;
		} else if (orderModel.equals(ORDER_MODEL_SJX_S11)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SJX_S11;
		} else if (orderModel.equals(ORDER_MODEL_LNV_A560E)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_LNV_A560E;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N855)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_ZTE_N855;
		} else if (orderModel.equals(ORDER_MODEL_YL_5210)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_YL_5210;
		} else if (orderModel.equals(ORDER_MODEL_TW_S8510)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_TW_S8510;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_V6700)||
						orderModel.equals(ORDER_MODEL_ZTE_V6700_2)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_ZTE_V6700;
		} else if (orderModel.equals(ORDER_MODEL_GIO_C500)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_GIO_C500;
		} else if (orderModel.equals(ORDER_MODEL_GIO_C600)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_GIO_C600;
		} else if (orderModel.equals(ORDER_MODEL_CHL_S8300)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_CHL_S8300;
		} else if (orderModel.equals(ORDER_MODEL_SCH_I919U)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SCH_I919U;
		} else if (orderModel.equals(ORDER_MODEL_YL_5110)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_YL_5110;
		} else if (orderModel.equals(ORDER_MODEL_HS_EG870)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HS_EG870;
		} else if (orderModel.equals(ORDER_MODEL_SH_I97)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SH_I97;
		} else if (orderModel.equals(ORDER_MODEL_CHL_S3000B)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_CHL_S3000B;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N855D)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_ZTE_N855D;
		} else if (orderModel.equals(ORDER_MODEL_GIO_C900)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_GIO_C900;
		} else if (orderModel.equals(ORDER_MODEL_TW_S8310)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_TW_S8310;
		} else if (orderModel.equals(ORDER_MODEL_HX_N739)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HX_N739;
		} else if (orderModel.equals(ORDER_MODEL_SCH_I659)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SCH_I659;
		} else if (orderModel.equals(ORDER_MODEL_XD_S6000)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_XD_S6000;
		} else if (orderModel.equals(ORDER_MODEL_TW_S8800)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_TW_S8800;
		}  else if (orderModel.equals(ORDER_MODEL_TLT_S800D) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_TLT_S800D;
		} else if (orderModel.equals(ORDER_MODEL_TM_D99S) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_TM_D99S;
		} else if (orderModel.equals(ORDER_MODEL_BLW_VE509) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_BLW_VE509;
		} else if (orderModel.equals(ORDER_MODEL_TF_C800) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_TF_C800;
		} else if (orderModel.equals(ORDER_MODEL_YFZ_E6A) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_YFZ_E6A;
		} else if (orderModel.equals(ORDER_MODEL_TCL_C990P) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_TCL_C990P;
		} else if (orderModel.equals(ORDER_MODEL_TY_E619) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_TY_E619;
		} else if (orderModel.equals(ORDER_MODEL_BK_X903) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_BK_X903;
		} else if (orderModel.equals(ORDER_MODEL_SM_EG800) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SM_EG800;
		} else if (orderModel.equals(ORDER_MODEL_PXX_K210V) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_PXX_K210V;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N855DP) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_ZTE_N855DP;
		} else if (orderModel.equals(ORDER_MODEL_JCT_A107) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_JCT_A107;
		} else if (orderModel.equals(ORDER_MODEL_HW_C8685D)  ||
					orderModel.equals(ORDER_MODEL_HW_C8685D_2)) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HW_C8685D;
		} else if (orderModel.equals(ORDER_MODEL_HS_E830) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_HS_E830;
		} else if (orderModel.equals(ORDER_MODEL_YX_E66) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_YX_E66;
		} else if (orderModel.equals(ORDER_MODEL_TLT_S600P) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_TLT_S600P;
		} else if (orderModel.equals(ORDER_MODEL_YL_5210S) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_YL_5210S;
		} else if (orderModel.equals(ORDER_MODEL_TCL_D510) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_TCL_D510;
		} else if (orderModel.equals(ORDER_MODEL_GX_EF78) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_GX_EF78;
		} else if (orderModel.equals(ORDER_MODEL_BLF_1800) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_BLF_1800;
		} else if (orderModel.equals(ORDER_MODEL_CW_EG6188) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_CW_EG6188;
		} else if (orderModel.equals(ORDER_MODEL_JST_J699) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_JST_J699;
		} else if (orderModel.equals(ORDER_MODEL_MYJ_E86) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_MYJ_E86;
		} else if (orderModel.equals(ORDER_MODEL_BR_W68) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_BR_W68;
		} else if (orderModel.equals(ORDER_MODEL_BLF_2800) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_BLF_2800;
		} else if (orderModel.equals(ORDER_MODEL_SMQ_EA6800) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_SMQ_EA6800;
		} else if (orderModel.equals(ORDER_MODEL_TW_X1) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_TW_X1;
		} else if (orderModel.equals(ORDER_MODEL_GX_EF68) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_GX_EF68;
		} else if (orderModel.equals(ORDER_MODEL_JCT_A109) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_JCT_A109;
		} else if (orderModel.equals(ORDER_MODEL_YFZ_8S) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_YFZ_8S;
		} else if (orderModel.equals(ORDER_MODEL_TM_G19) ) {
			clientResolution = CLIENT_RESOLUTION_320_480;
			return CLIENT_VERSION_TM_G19;
		}
		// 240*320
		else if (orderModel.equals(ORDER_MODEL_MOTO_XT301)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_XT301;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N600)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_N600;
		} else if (orderModel.equals(ORDER_MODEL_HW_C8500)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_HW_C8500;
		} else if (orderModel.equals(ORDER_MODEL_E230A)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_E230A;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N606)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_N606;
		} else if (orderModel.equals(ORDER_MODEL_SAMSUNG_I559_1)
				|| orderModel.equals(ORDER_MODEL_SAMSUNG_I559_2)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_I559;
		} else if (orderModel.equals(ORDER_MODEL_EPHONE_A9)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_EPHONE_A9;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N600_PLUS)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			// if(sdkVersion == SDK_VERSION_8){
			// return CLIENT_VERSION_N600_PLUS_8;
			// }else{
			return CLIENT_VERSION_N600_PLUS_7;
			// }
		} else if (orderModel.equals(ORDER_MODEL_HESENS_N100)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_HESENS_N100;
		} else if (orderModel.equals(ORDER_MODEL_HTC_BEE)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_HTC_BEE;
		} else if (orderModel.equals(ORDER_MODEL_HS_E89) &&
				ClientInfoUtil.getManufacturer(context.getApplicationContext()).equals(Manufacturer.HISENSE)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_HS_E89;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_X920)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_ZTE_X920;
		} else if (orderModel.equals(ORDER_MODEL_TE_TE600)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_TE_TE600;
		} else if (orderModel.equals(ORDER_MODEL_HUALU_S800)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_HUALU_S800;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N700)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_ZTE_N700;
		} else if (orderModel.equals(ORDER_MODEL_CHER_A4)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_CHAR_A4;
		} else if (orderModel.equals(ORDER_MODEL_TONGWEI_X2011)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_TONGWEI_X2011;
		} else if (orderModel.equals(ORDER_MODEL_EPHONE_A8)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_EPHONE_A8;
		} else if (orderModel.equals(ORDER_MODEL_FLY_N6)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_FLY_N6;
		} else if (orderModel.equals(ORDER_MODEL_GUANGXIN_E900)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_GUANGXIN_TE600;
		} else if (orderModel.equals(ORDER_MODEL_TIANYU_E610)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_TIANYU_E610;
		} else if (orderModel.equals(ORDER_MODEL_HS_E839)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_HS_E839;
		} else if (orderModel.equals(ORDER_MODEL_BIRD_AE710)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_BIRD_AE710;
		} else if (orderModel.equals(ORDER_MODEL_SKYWORTH_PE89)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_SKYWORTH_PE89;
		} else if (orderModel.equals(ORDER_MODEL_HS_E87)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_HS_E87;
		} else if (orderModel.equals(ORDER_MODEL_HUALU_ES2000)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_HUALU_ES2000;
		} else if (orderModel.equals(ORDER_MODEL_LANTIAN_S100)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_LANTIAN_S100;
		} else if (orderModel.equals(ORDER_MODEL_KINGPAD_N600)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_KINGPAD_N600;
		} else if (orderModel.equals(ORDER_MODEL_CHL_S850)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_CHL_S850;
		} else if (orderModel.equals(ORDER_MODEL_HUAWEI_C8500SR)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_HUAWEI_C8500SR;
		} else if (orderModel.equals(ORDER_MODEL_BROR_S06)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_BROR_S06;
		} else if (orderModel.equals(ORDER_MODEL_YUXIN_E60) ||
					orderModel.equals(ORDER_MODEL_YUXIN_E60_2)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_YUXIN_E60;
		} else if (orderModel.equals(ORDER_MODEL_ZTE_N601)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_ZTE_N601;
		} else if (orderModel.equals(ORDER_MODEL_SKYWORTH_ES2000)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_SKYWORTH_ES2000;
		} else if (orderModel.equals(ORDER_MODEL_FLY_N5)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_FLY_N5;
		} else if (orderModel.equals(ORDER_MODEL_HUAWEI_C8550)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_HW_C8550;
		} else if (orderModel.equals(ORDER_MODEL_SAMSUNG_I339)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_SAMSUNG_I339;
		} else if (orderModel.equals(ORDER_MODEL_SJX_S7)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_SJX_S7;
		} else if (orderModel.equals(ORDER_MODEL_SCH_I519)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_SCH_I519;
		} else if (orderModel.equals(ORDER_MODEL_SCH_I509U)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_SCH_I509U;
		} else if (orderModel.equals(ORDER_MODEL_SCH_I639)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_SCH_I639;
		} else if (orderModel.equals(ORDER_MODEL_BLW_VE200)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_BLW_VE200;
		} else if (orderModel.equals(ORDER_MODEL_YL_5010)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_YL_5010;
		} else if (orderModel.equals(ORDER_MODEL_BLF_2600)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_BLF_2600;
		} else if (orderModel.equals(ORDER_MODEL_BLF_1600)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_BLF_1600;
		} else if (orderModel.equals(ORDER_MODEL_GX_EF55)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_GX_EF55;
		} else if (orderModel.equals(ORDER_MODEL_BLW_VE360)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_BLW_VE360;
		} else if (orderModel.equals(ORDER_MODEL_SH_I60)) {
			clientResolution = CLIENT_RESOLUTION_240_320;
			return CLIENT_VERSION_SH_I60;
		}
		// 其它分辨率
		else if (orderModel.equals(ORDER_MODEL_LIFEPAD)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_LIFEPAD;
		} else if (orderModel.equals(ORDER_MODEL_AUSCLOUD_PAD)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_AUSCLOUD_V8;
		} else if (orderModel.equals(ORDER_MODEL_LION_PAD_I600)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_LION_PAD_I600;
		} else if (orderModel.equals(ORDER_MODEL_LION_PAD_A1)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_LION_PAD_A1;
		} else if (orderModel.equals(ORDER_MODEL_MALATA_T8)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_MALATA_T8;
		} else if (orderModel.equals(ORDER_MODEL_WST_A700)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_WST_A700;
		} else if (orderModel.equals(ORDER_MODEL_SAMSUNG_P739)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_SAMSUNG_P739;
		} else if (orderModel.equals(ORDER_MODEL_EBEN_T3)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_EBEN_T3;
		} else if (orderModel.equals(ORDER_MODEL_UT_P200E)) {
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_UT_P200E;
		} else if(orderModel.equals(ORDER_MODEL_SAMSUNG_I889)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_SCH_I889;
		} else if(orderModel.equals(ORDER_MODEL_SAMSUNG_I9250)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_SCH_I9520;
		} else if(orderModel.equals(ORDER_MODEL_MOT_XT889)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_MOT_XT889;
		} else if(orderModel.equals(ORDER_MODEL_HTC_T328d)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_HTC_T328D;
		} else if(orderModel.equals(ORDER_MODEL_SAMSUNG_I939)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_SCH_I939;
		} else if(orderModel.equals(ORDER_MODEL_HTC_X720D)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_HTC_X720D;
		} else if(orderModel.equals(ORDER_MODEL_SND_LT25C)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_SND_LT25C;
		} else if(orderModel.equals(ORDER_MODEL_MOT_XT785)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_MOT_XT788;
		} else if(orderModel.equals(ORDER_MODEL_HTC_T528D)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_HTC_T528D;
		} else if(orderModel.equals(ORDER_MODEL_YL_9960)
				  || orderModel.equals(ORDER_MODEL_YL_9960_2)
					){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_YL_9960;
		} else if(orderModel.equals(ORDER_MODEL_LNV_S870E) ||
					orderModel.equals(ORDER_MODEL_LNV_S870E_2) ||
					orderModel.equals(ORDER_MODEL_LNV_S870E_3)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_LNV_S870E;
		} else if(orderModel.equals(ORDER_MODEL_SCH_I959)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_SCH_I959;
		} else if(orderModel.equals(ORDER_MODEL_MOT_XT788)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_MOT_XT788;
		} else if(orderModel.equals(ORDER_MODEL_GIO_C800)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_GIO_C800;
		} else if(orderModel.equals(ORDER_MODEL_SHP_SH630T) ||
				   orderModel.equals(ORDER_MODEL_SHP_SH630T_2)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_SHP_SH630T;
		} else if(orderModel.equals(ORDER_MODEL_SCH_I939D) ||
				    orderModel.equals(ORDER_MODEL_SCH_I939D_2)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_SCH_I939D;
		} else if(orderModel.equals(ORDER_MODEL_HS_EG950)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_HS_EG950;
		} else if(orderModel.equals(ORDER_MODEL_SCH_N719)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_SCH_N719;
		} else if(orderModel.equals(ORDER_MODEL_HE_E80)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_HE_E80;
		} else if(orderModel.equals(ORDER_MODEL_ZTE_N880G)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_ZTE_N880G;
		} else if(orderModel.equals(ORDER_MODEL_YL_5876)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_YL_5876;
		} else if(orderModel.equals(ORDER_MODEL_ZTE_N983)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_ZTE_N983;
		} else if(orderModel.equals(ORDER_MODEL_XD_U1203)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_XD_U1203;
		} else if(orderModel.equals(ORDER_MODEL_HW_D2)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_HW_D2;
		} else if(orderModel.equals(ORDER_MODEL_TCL_D706)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_TCL_D706;
		} else if(orderModel.equals(ORDER_MODEL_YL_5890)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_YL_5890;
		} else if(orderModel.equals(ORDER_MODEL_TLT_S939D)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_TLT_S939D;
		} else if(orderModel.equals(ORDER_MODEL_BLW_E2013) ||
				   orderModel.equals(ORDER_MODEL_BLW_E2013_2)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_BLW_E2013;
		} else if(orderModel.equals(ORDER_MODEL_ZTE_N881F)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_ZTE_N881F;
		} else if(orderModel.equals(ORDER_MODEL_JSR_D9C)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_JSR_D9C;
		} else if(orderModel.equals(ORDER_MODEL_LVN_A820E)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_LVN_A820E;
		} else if(orderModel.equals(ORDER_MODEL_YL_5930)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_YL_5930;
		} else if(orderModel.equals(ORDER_MODEL_TF_C820)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_TF_C820;
		} else if(orderModel.equals(ORDER_MODEL_YL_9070)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_YL_9070;
		} else if(orderModel.equals(ORDER_MODEL_JSR_I6C)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_JSR_I6C;
		} else if(orderModel.equals(ORDER_MODEL_SND_M35H)){
			clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_SND_M35H;
		}
		//CLIENT_RESOLUTION_540_960
		else if(orderModel.equals(ORDER_MODEL_PXX_FPAD)){
			clientResolution = CLIENT_RESOLUTION_540_960;
			return CLIENT_VERSION_PXX_FPAD;
		}
		// 通用版本
		else if (orderModel.equals(ORDER_MODEL_COMMON)) {
			// clientResolution = CLIENT_RESOLUTION_480_854;
			return CLIENT_VERSION_COMMON;
		}
		return CLIENT_VERSION_COMMON;
	}

	public static String getOrderModel(Context context) {
		if (TextUtils.isEmpty(orderModel)) {
			isOrder(context);
			init(context);
		}
		return orderModel;
	}

	public static String getManufacturer(Context context) {
		if (TextUtils.isEmpty(manufacturer)) {
			isOrder(context);
			init(context);
		}
		return manufacturer;
	}

	/*public static ComponentName getSettingComponentName() {
		ComponentName componentName = null;
		if (orderModel.equals(ORDER_MODEL_SAMSUNG_P739)) {
			componentName = new ComponentName("com.android.settings",
					"com.android.settings.Settings$WirelessSettingsActivity");
		}
		if (componentName == null) {
			componentName = new ComponentName("com.android.settings",
					"com.android.settings.WirelessSettings");
		}
		if(Build.VERSION.SDK_INT >= 14){//4.0的设置
			componentName = new ComponentName("com.android.settings",
					"com.android.settings.Settings");
		}
		return componentName;
	}*/
	
	/** 到系统的设置网络界面
	 * @param context
	 */
	public static void gotoSettingActivity(Context context){
		try {
			String action = android.provider.Settings.ACTION_WIRELESS_SETTINGS;
			if(Build.VERSION.SDK_INT >= 14){//4.0的设置
				action = android.provider.Settings.ACTION_SETTINGS;
			}
			Intent intent = new Intent(action);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
		}
	}

	/**
	 * 判断客户端是否可以自动切换CTWAP网络，目前只能手动添加过滤。
	 * 
	 * @param context
	 * @return
	 */
	public static boolean canAutoChangeCtwap(Context context) {
		String model = getOrderModel(context);
		if(Build.VERSION.SDK_INT >= 14){
			return false;
		} 
		
		if (model.equals(ClientInfoUtil.ORDER_MODEL_EPHONE_A9)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_HESENS_N100)
				|| (model.equals(ClientInfoUtil.ORDER_MODEL_HS_E89) &&
					ClientInfoUtil.getManufacturer(context.getApplicationContext()).equals(Manufacturer.HISENSE))
				|| model.equals(ClientInfoUtil.ORDER_MODEL_EPHONE_A8)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_HS_E87)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_AUSCLOUD_PAD)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_HS_EG968B)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_HS_E86)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_EPHONE_A6)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_FLY_N8)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_KINGPAD_N600)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_KINGPAD_E239)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_ZC_ZC600)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_TONGWEI_X2011)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_DC_E366)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_TONGlIN_T90)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_TONGlIN_T98)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_BROR_S06)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_YUXIN_E60)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_YUXIN_E80)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_SAIHONG_I90)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_LG_CS600)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_FLY_N5)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_ZY_N7)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_SJX_S7)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_SJX_S11)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_YHY_Q8)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_GIO_C600) 
				|| model.equals(ClientInfoUtil.ORDER_MODEL_TLT_S800) 
				|| model.equals(ClientInfoUtil.ORDER_MODEL_TLT_S800_2)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_TLT_S800D)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_GIO_C500) 
				|| model.equals(ClientInfoUtil.ORDER_MODEL_BLF_1600)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_BLF_2600)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_GX_EF930)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_SM_EG800)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_TCL_D662)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_PXX_K210V) 
				|| model.equals(ClientInfoUtil.ORDER_MODEL_JCT_A107)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_SAIHONG_I91)
				|| model.equals(ClientInfoUtil.ORDER_MODEL_SAIHON_I91)
				) {
			return false;
		}
		return true;
	}

	public static boolean isHTCSpecial(Context context) {
		String model = getOrderModel(context);
		if (model.equals(ORDER_MODEL_HTC_VIVO)
				|| model.equals(ORDER_MODEL_HTC_Z510D)
				|| model.equals(ORDER_MODEL_HTC_X515D)
				) {
			return true;
		}
		return false;
	}
	/*begin add by xzz 2012-07-11*/
	//三星I929跟I889由于升级到4.0，按钮位置要保持一致。ORDER_MODEL_LNV_A600E_2
	public static boolean isSamSungSpecical(Context context){ 
		if (getOrderModel(context).equals(ORDER_MODEL_SAMSUNG_I889) 			 				 
				|| getOrderModel(context).equals(ORDER_MODEL_SAMSUNG_I929)
				|| getOrderModel(context).equals(ORDER_MODEL_LNV_A600E)
				|| getOrderModel(context).equals(ORDER_MODEL_LNV_A600E_2)
				|| getOrderModel(context).equals(ORDER_MODEL_LNV_S870E)
				|| getOrderModel(context).equals(ORDER_MODEL_LNV_S870E_2)
				|| getOrderModel(context).equals(ORDER_MODEL_LNV_S870E_3)
				|| getOrderModel(context).equals(ORDER_MODEL_LNV_A630E)
				|| getOrderModel(context).equals(ORDER_MODEL_SCH_W9913)				
				) {
			return true;
		}
		return false; 
	}
	/*end by xzz 2012-07-11*/
	
	/*begin add by xzz 2012-08-20*/
	//HTC T528D 酷派5910 联想A700E 三款基地的版本不要TTS,以下机型均为不要TTS功能
	public static boolean isHTCNonTTSSpecial(Context context) {
		String model = getOrderModel(context);
		if (model.equals(ORDER_MODEL_HTC_T528D)
				|| model.equals(ORDER_MODEL_YL_5910)
				|| model.equals(ORDER_MODEL_LNV_A700E) 
				|| model.equals(ORDER_MODEL_HE_E80)
				|| model.equals(ORDER_MODEL_CHL_S9388)
				|| model.equals(ORDER_MODEL_YL_9120)) {
			return true;
		}
		return false;
	}
	/*end by xzz 2012-08-20*/
	
	/**
	 * 乐阅项目，请不要调用该方法。请使用 {@link CommonUtil #isGuest()}
	 * @return
	 */
//	@Deprecated
//	public static boolean isGuest() {
///*		if(LoginManage.getInstance().isForceGuest()){
//			return true;
//		}*/
//		String userID = DataCache.getInstance().getUserID();
//		if (TextUtils.isEmpty(userID)) {
//			return true;
//		}
//		if (!(LoginPresenter.LOGIN_TYPE_TY.equals(LoginPresenter.getLoginType())) 
//				&& !(LoginPresenter.LOGIN_TYPE_WEIBO.equals(LoginPresenter.getLoginType()))//第三方
//				&& !isCtwapLogin(MyAndroidApplication.getInstance())) {
//			String psw = DataCache.getInstance().getPsw();
//			if (TextUtils.isEmpty(psw)) {
//				return true;
//			}
//		}
//		return false;
//	}

}
