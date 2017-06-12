package com.lectek.android.lereader.lib.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


/**
 * Apn工具类
 * 
 * @author mingkg21
 * @date 2010-4-12
 * @email mingkg21@gmail.com
 */
public class ApnUtil {


	//WIFI
	public static String APN_WIFI = "WIFI";
	//CDMA
	public static String APN_CDMA = "CDMA";
	public static String APN_CTWAP = "ctwap";
	public static String APN_CTNET = "ctnet";
	//GSM
	public static String APN_CMWAP = "cmwap";
	public static String APN_CMNET = "CMNET";
	//WCDMA
	public static String APN_UNIWAP = "uniwap";
	public static String APN_UNINET = "UNINET";
	public static String APN_3GWAP = "3GWAP";
	public static String APN_3GNET = "3GNET";

	private static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
	private static final Uri PREFERRED_35_APN_URI = Uri.parse("content://telephony/carriers-preferapn-cdma");
	private static final Uri CURRENT_APNS = Uri.parse("content://telephony/carriers/current");
	private static final Uri PREFERRED2_APN_URI = Uri.parse("content://telephony/carriers/preferapn2");

	public static boolean isCtwap(Context context) {
		if (!ClientInfoUtil.canAutoChangeCtwap(context) || ClientInfoUtil.isHTCSpecial(context)) {
			if(!ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_LG_CS600)){
				ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = cm.getActiveNetworkInfo();
				if (networkInfo != null) {
					String str = networkInfo.toString();
					if (!TextUtils.isEmpty(str) && str.toLowerCase().indexOf(APN_CTWAP) > -1) {
//						LogUtil.i("isCtwap", "isCtwap: " + str);
					//	return str.toLowerCase().indexOf(APN_CTWAP) > -1;
						return true;
					}
					
					/*begin add by xzz 2012-05-30*/
					//如果是HTC_X720D,由于类型判断出错，导致APN接入点显示的是CTNET，而实际的底层网络类型是CTWAP的时候，这个时候就要返回false
					if(ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_HTC_X720D))
					{
						if(APN_CTWAP.equalsIgnoreCase(getApnType(context))){
							return false;
						}
					}
					if(ClientInfoUtil.isHTCSpecial(context)){
						return false;
					}
					/*end by xzz 2012-05-30*/
					
					//当前网络不是CTWAP，如果是金立C600则返回失败（通过获取选中APN，CTNET状态获取出来的是CTWAP）
					if (ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_GIO_C600) ||
							ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_GIO_C500)) {
						return false;
					}
				}
			}
		}
		return APN_CTWAP.equalsIgnoreCase(getApnType(context));
	}

	public static boolean isCmwap(Context context) {
		return judgeNetworkType(context, APN_CMWAP);
	}
	
	public static boolean isCmnet(Context context) {
		return judgeNetworkType(context, APN_CMNET);
	}

	public static boolean isUniwap(Context context) {
		return judgeNetworkType(context, APN_UNIWAP);
	}
	
	public static boolean isUninet(Context context) {
		return judgeNetworkType(context, APN_UNINET);
	}

	private static boolean judgeNetworkType(Context context, String type) {
		NetworkInfo info = getActiveNetworkInfo(context);
		if (info != null) {
			String extraInfo = info.getExtraInfo();
			if (!TextUtils.isEmpty(extraInfo) && extraInfo.equalsIgnoreCase(type)) {
				return true;
			}
		}
		return false;
	}

	private static Uri getApnUri(Context context) {
		Uri uri = PREFERRED_APN_URI;
		if (ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_35_Q68)) {
			uri = PREFERRED_35_APN_URI;
		}
		return uri;
	}
	
	public static String getCurrentNetType(Context context) {
		NetworkInfo networkInfo = getActiveNetworkInfo(context);
		String type = "nomatch";
		if (networkInfo != null) {
			String typeName = networkInfo.toString();
			if (!TextUtils.isEmpty(typeName)) {
				String temp = typeName.toLowerCase();
				if (temp.indexOf(APN_CTNET) > -1) {// ctnet
					type = APN_CTNET;
				} else if (temp.indexOf(APN_CTWAP) > -1) {// ctwap
					type = APN_CTWAP;
				}
			}
		}

		return type;
	}

	public static String getApnType(Context context) {
		if(Build.VERSION.SDK_INT >= 17){
			return getCurrentNetType(context);
		}
		String apntype = "nomatch";
		Uri uri = getApnUri(context);
		Cursor c = context.getContentResolver().query(uri, null, null, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				String user = c.getString(c.getColumnIndex("user"));
//				 int columneCount = c.getColumnCount();
//				 LogUtil.i("count " + columneCount);
//				 for(int i = 0; i < columneCount; ++i){
//					 LogUtil.i(c.getColumnName(i) + " : " + c.getString(i));
//				 }
//				 LogUtil.i("user " + user);
//				 LogUtil.i("apn_id " + c.getLong(c.getColumnIndex("_id")));
				if (!TextUtils.isEmpty(user)) {
					if (user.startsWith(APN_CTNET)) {
						apntype = APN_CTNET;
						DataCache.getInstance().ctnetID = c.getLong(c.getColumnIndex("_id"));
					} else if (user.startsWith(APN_CTWAP)) {
						apntype = APN_CTWAP;
					}
				}
			}
			c.close();
			c = null;
		}
		return apntype;
	}

//	public static String getCurrentApnName(Context context, Uri uri) {
//		String apnName = null;
//		Cursor c = context.getContentResolver().query(uri, null, null, null,
//				null);
//		if (c != null) {
//			if (c.moveToFirst()) {
//				apnName = c.getString(c.getColumnIndex("name"));
//			}
//			c.close();
//			c = null;
//		}
//		return apnName;
//	}

	/**
	 * 添加新的CTWAP的APN
	 * 
	 * @param context
	 * @return
	 */
	public static long newCtwapApn(Context context) {
		Uri uri = Uri.parse("content://telephony/carriers");
		ContentValues values = new ContentValues();
		values.put("name", "ctwap");
		values.put("apn", "#777");
		values.put("type", "default,mms");
		values.put("user", "ctwap@mycdma.cn");
		values.put("numeric", "46003");
		values.put("mcc", "460");
		values.put("mnc", "03");
		values.put("port", "80");
		values.put("mmsproxy", "10.0.0.200");
		values.put("mmsport", "80");
		values.put("mmsc", "http://mmsc.vnet.mobi");
		values.put("authtype", "-1");
		values.put("password", "vnet.mobi");
		values.put("proxy", "10.0.0.200");
		Uri insertUri = context.getContentResolver().insert(uri, values);
		if (insertUri == null) {
			return -1;
		}
		return ContentUris.parseId(insertUri);
	}

	/**
	 * 获取CTWAP的APN的ID
	 * 
	 * @param context
	 * @return
	 */
	public static long getCtwapAPN(Context context) {

		long id = -1;
		LogUtil.v("ApnUtil", "getCtwapAPN 11");
		Uri uri = null;
		
		/*begin add by xzz 2012-08-23*/
		//新的赛鸿I91机型数据库获取方式有变化，		
//		ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_SAIHONG_I91)
//		ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_SAIHON_I91)		
		/*end by xzz 2012-08-23*/
		if (ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_SAIHONG_I98)
			||ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_BR_W60)
			|| ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_BLW_VE200)
			|| ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_SAIHONG_I901)
			|| ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_SH_I97)
			) {
			uri = Uri.parse("content://telephony/carriers/21");
		} else {
			uri = Uri.parse("content://telephony/carriers");//获取所有apn
		}
		
		Cursor cr = context.getContentResolver().query(uri, null, null, null, null);

		while (cr != null && cr.moveToNext()) {
			String user = cr.getString(cr.getColumnIndex("user"));
			String numeric = cr.getString(cr.getColumnIndex("numeric"));
			String mmsproxy = null;
			mmsproxy = cr.getString(cr.getColumnIndex("proxy"));
			LogUtil.i("APN Count: " + cr.getCount());
			int columneCount = cr.getColumnCount();
			LogUtil.i("count " + columneCount);
			for(int i = 0; i < columneCount; ++i){
				LogUtil.i(cr.getColumnName(i) + " : " +  cr.getString(i));
			}
			if (TextUtils.isEmpty(user) || TextUtils.isEmpty(numeric) || TextUtils.isEmpty(mmsproxy)) {
				continue;
			}
			if (user.startsWith(APN_CTWAP) && numeric.equals("46003") && mmsproxy.equals("10.0.0.200")) {
				id = cr.getLong(cr.getColumnIndex("_id"));
				break;
			}
		}
		if (cr != null) {
			cr.close();
		}

		if (id == -1) {
			id = newCtwapApn(context);
		}

		return id;
	}

	public static long getCtnetAPN(Context context) {

		long id = -1;
		Uri uri = Uri.parse("content://telephony/carriers");
		Cursor cr = context.getContentResolver().query(uri, null, null, null,
				null);
		while (cr != null && cr.moveToNext()) {
			String user = cr.getString(cr.getColumnIndex("user"));
			String numeric = cr.getString(cr.getColumnIndex("numeric"));
			if (TextUtils.isEmpty(user) || TextUtils.isEmpty(numeric)) {
				continue;
			}
			if (user.startsWith(APN_CTNET) && numeric.equals("46003")) {
				id = cr.getLong(cr.getColumnIndex("_id"));
				break;
			}
		}
		if (cr != null) {
			cr.close();
		}

		return id;
	}

//	/**
//	 * 设置当前链接方式为CTWAP
//	 * 
//	 * @param context
//	 * @return
//	 */
//	public static boolean setCtwapMode(Context context) {
//		long id = getCtwapAPN(context);
//		LogUtil.v("ApnUtil", "setCtwapMode id " + id);
//		boolean isSuccess = setCDMAMode(context, id);
//		if (isSuccess) {
//			LogUtil.v("ApnUtil", "had change network successed");
//			DataCache.getInstance().hadChangeNetwork = true;
//		}
//		
////		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
////		connMgr.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, feature);
////		connMgr.requestRouteToHost(ConnectivityManager.TYPE_MOBILE, hostAddress);
//		return isSuccess;
//	}

	public static boolean setCtnetMode(Context context) {
		if (!DataCache.getInstance().hadChangeNetwork) {
			return false;
		}
		DataCache.getInstance().hadChangeNetwork = false;
		long id = DataCache.getInstance().ctnetID;
		DataCache.getInstance().ctnetID = -1;
		if (ClientInfoUtil.isHTCSpecial(context)) {
			id = getCtnetAPN(context);
		}
		if (id == -1) {
			return false;
		}
		return setCDMAMode(context, id);
	}

	public static boolean setCDMAMode(Context context, long id) {
		if (id == -1) {
			return false;
		}
		ContentValues cv = new ContentValues();
		cv.put("apn_id", id);

		int count = context.getContentResolver().update(getApnUri(context), cv,
				null, null);
		if (count > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否启动WIFI
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiWork(Context context) {
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wm == null) {// 有些手机阉割到WIFI，这里会为null。如：宇龙E230A
			return false;
		}
		if (wm.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if(cm != null){
				NetworkInfo networkInfo = cm.getActiveNetworkInfo();
				if (networkInfo != null) {
					String ei = networkInfo.getTypeName();
					if (!TextUtils.isEmpty(ei)) {
						int index = ei.toUpperCase().indexOf("WIFI");
						if (index > -1) {
							return true;
						}
					}
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 关闭WIFI连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean closeWifi(Context context) {
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wm.isWifiEnabled()) {
			return wm.setWifiEnabled(false);
		} else {
			return true;
		}
	}

	/**
	 * 判断是否有网络连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetAvailable(final Context context) {
//		if (ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_E600)) {
//			return true;
//		}
//		
//		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivity != null){
//        	
//            NetworkInfo[] info = connectivity.getAllNetworkInfo();
//            if (info != null) {
//                for (int i = 0; i < info.length; i++) {
//                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
//                        return true;
//                    }
//                }
//            }
//        }
        return isConnected(context);
	}

	public static boolean isCDMA(Context context) {
		if (ClientInfoUtil.getOrderModel(context).equals(
				ClientInfoUtil.ORDER_MODEL_TE_TE600)
				|| ClientInfoUtil.getOrderModel(context).equals(
						ClientInfoUtil.ORDER_MODEL_HUALU_S800)) {
			return true;
		}
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null) {
			// String ei = networkInfo.getExtraInfo();
			String ei = networkInfo.getSubtypeName();
			if (!TextUtils.isEmpty(ei)) {
				if (ClientInfoUtil.getOrderModel(context).equals(
						ClientInfoUtil.ORDER_MODEL_HTC_EVO)) {
					if (ei.equals("UNKNOWN")) {
						return true;
					}
				}
				int index = ei.toUpperCase().indexOf(APN_CDMA);
				if (index > -1) {
					return true;
				}
				ei = networkInfo.getExtraInfo();
				if (!TextUtils.isEmpty(ei)) {
					if (ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_BK_PM700)) {
						if (ei.equals("#777")) {
							return true;
						}
					}
					if (ei.equalsIgnoreCase(APN_CTWAP) || ei.equalsIgnoreCase(APN_CTNET)) {
						return true;
					}
				}
				// }
			}
			return false;
		}
		return false;
	}

	/**
	 * 判断网络是否连接上
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnected(Context context) {
		NetworkInfo networkInfo = getActiveNetworkInfo(context);
		if (networkInfo != null) {
			if (networkInfo.getState().compareTo(NetworkInfo.State.CONNECTED) == 0) {
				return true;
			}
		}
		return false;
	}

	private static NetworkInfo getActiveNetworkInfo(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		return networkInfo;
	}

	/**
	 * 关闭网络
	 * 
	 * @param context
	 */
	public static void closeNetwork(Context context) {
		// TODO
	}

	public static String getIMSI(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSubscriberId();
	}
	
//	/**
//	 * 判断当前网络类型。WIFI,NET,WAP
//	 * 
//	 * @param context
//	 * @return
//	 */
//	public static byte getCurrentNetType(Context context) {
//		NetworkInfo networkInfo = getActiveNetworkInfo(context);
//		byte type = CURRENT_NETWORK_TYPE_NONE;
//		if (networkInfo != null) {
//			String typeName = networkInfo.getExtraInfo();
//			if (TextUtils.isEmpty(typeName)) {
//				typeName = networkInfo.getTypeName();
//			}
//			if (!TextUtils.isEmpty(typeName)) {
//				String temp = typeName.toLowerCase();
//				if (temp.indexOf(APN_WIFI) > -1) {// wifi
//					type = CURRENT_NETWORK_TYPE_WIFI;
//				} else if (temp.indexOf(APN_CTNET) > -1) {// ctnet
//					type = CURRENT_NETWORK_TYPE_CTNET;
//				} else if (temp.indexOf(APN_CTWAP) > -1) {// ctwap
//					type = CURRENT_NETWORK_TYPE_CTWAP;
//				} else if (temp.indexOf(APN_CMNET) > -1) {// cmnet
//					type = CURRENT_NETWORK_TYPE_CMNET;
//				} else if (temp.indexOf(APN_CMWAP) > -1) {// cmwap
//					type = CURRENT_NETWORK_TYPE_CMWAP;
//				} else if (temp.indexOf(APN_UNIWAP) > -1) {// uniwap
//					type = CURRENT_NETWORK_TYPE_UNIWAP;
//				} else if (temp.indexOf(APN_3GWAP) > -1) {// 3gwap
//					type = CURRENT_NETWORK_TYPE_UNIWAP;
//				} else if (temp.indexOf(APN_UNINET) > -1) {// uninet
//					type = CURRENT_NETWORK_TYPE_UNIET;
//				} else if (temp.indexOf(APN_3GNET) > -1) {// 3gnet
//					type = CURRENT_NETWORK_TYPE_UNIET;
//				}
//			}
//		}
//		if (type == CURRENT_NETWORK_TYPE_NONE) {
//			String apnType = getApnType(context);
//			if (apnType != null && apnType.equals(APN_CTNET)) {// ctnet
//				type = CURRENT_NETWORK_TYPE_CTNET;
//			} else if (apnType != null && apnType.equals(APN_CTWAP)) {// ctwap
//				type = CURRENT_NETWORK_TYPE_CTWAP;
//			} else if (apnType != null && apnType.equals(APN_CMWAP)) {// cmwap
//				type = CURRENT_NETWORK_TYPE_CMWAP;
//			} else if (apnType != null && apnType.equals(APN_CMNET)) {// cmnet
//				type = CURRENT_NETWORK_TYPE_CMNET;
//			} else if (apnType != null && apnType.equals(APN_UNIWAP)) {// uniwap
//				type = CURRENT_NETWORK_TYPE_UNIWAP;
//			} else if (apnType != null && apnType.equals(APN_3GWAP)) {// 3gwap
//				type = CURRENT_NETWORK_TYPE_UNIWAP;
//			} else if (apnType != null && apnType.equals(APN_UNINET)) {// uninet
//				type = CURRENT_NETWORK_TYPE_UNIET;
//			} else if (apnType != null && apnType.equals(APN_3GNET)) {// 3gnet
//				type = CURRENT_NETWORK_TYPE_UNIET;
//			}
//		}
//
//		return type;
//	}
}
