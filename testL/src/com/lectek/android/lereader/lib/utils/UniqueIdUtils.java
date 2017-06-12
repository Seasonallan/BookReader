package com.lectek.android.lereader.lib.utils;
 

import java.util.UUID;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 获取设备唯一标识工具类
 * @author wuwq
 *
 */
public class UniqueIdUtils {
	/**
	 * 获取唯一设备标识
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context){
		try{
			//phone
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String phone = tm.getLine1Number();
			if(!StringUtil.isEmpty(phone)){
				return phone;
			}
			//imsi
			String imsi = tm.getSubscriberId();
			if(!StringUtil.isEmpty(imsi)){
				return imsi;
			}
			//imei
			String imei = tm.getDeviceId();
			if(!StringUtil.isEmpty(imei)){
				return imei;
			}
			//mac
			WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			String wifiMac = info.getMacAddress();
			if(!StringUtil.isEmpty(wifiMac)){
				return wifiMac;
			}
			//uuid
			String uuid = getUUID(context);
			if(!StringUtil.isEmpty(uuid)){
				return uuid;
			}
		}catch(Exception e){
			UUID deviceUuid = UUID.randomUUID();
			return deviceUuid.toString();
		}
		return null;
	}
	/**
	 * 获取唯一uuid
	 * @param context
	 * @return
	 */
	public static String getUUID(Context context){
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice,tmSerial,androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(),((long)tmDevice.hashCode()<<32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();
		return uniqueId;
	}
	/**
	 * 获取终端型号
	 * @param context
	 * @return
	 */
	public static String getTerminalBrand(Context context){
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

    /**
     * 获取设备信息
     * @param context
     * @return
     */
    public static String getDeviceInfo(Context context){
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if(tm != null){
            String imei = tm.getDeviceId();
            String imsi = tm.getSubscriberId();
            String phoneType = android.os.Build.MODEL;//手机型号
            String phoneBrand = android.os.Build.BRAND;//手机品牌
            String phoneNumber = tm.getLine1Number();//手机号码，有的可得，有的不可得

            StringBuilder sb = new StringBuilder();

            if(!TextUtils.isEmpty(imei)){
                sb.append("imei:"+imei);
                sb.append(",");
            }
            if(!TextUtils.isEmpty(imsi)){
                sb.append("imsi:"+imsi);
                sb.append(",");
            }
            if(!TextUtils.isEmpty(phoneType)){
                sb.append("phoneType:"+phoneType);
                sb.append(",");
            }
            if(!TextUtils.isEmpty(phoneBrand)){
                sb.append("phoneBrand:"+phoneBrand);
                sb.append(",");
            }
            if(!TextUtils.isEmpty(phoneNumber)){
                sb.append("phoneNumber:"+phoneNumber);
                sb.append(",");
            }

            return sb.toString();
        }
        return null;
    }
}
