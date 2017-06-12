package com.lectek.android.lereader.lib.utils;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;

/** 切换语言
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2012-9-19
 */
public final class ChangeLanguageUtil {
	
	/** 判断是否繁体中文
	 * @param language
	 * @return
	 */
	public static boolean isTraditionalChinese(String language){
		if(Locale.TRADITIONAL_CHINESE.toString().equals(language)){
			return true;
		}
		return false;
	}
	
	/** 判断是否简体中文
	 * @param language
	 * @return
	 */
	public static boolean isSimplifiedChinese(String language){
		if(Locale.SIMPLIFIED_CHINESE.toString().equals(language)){
			return true;
		}
		return false;
	}
	
	/** 设置为简体中文
	 * @param context
	 */
	public static void changeSimplifiedChinese(Context context){
		changeLanguage(context, Locale.SIMPLIFIED_CHINESE);
	}
	
	/** 设置为繁体中文
	 * @param context
	 */
	public static void changeTraditionalChinese(Context context){
		changeLanguage(context, Locale.TRADITIONAL_CHINESE);
	}
	
	/** 设置语音
	 * @param locale
	 * @param _language
	 */
	public static void selectLanguage(Locale locale, String _language){
		String language = _language;
		String localeLanguage = locale.toString();
		if(localeLanguage.equals(language)){
			return;
		}
//		PreferencesUtil.getInstance(MyAndroidApplication.getInstance()).setSettingLanguage(localeLanguage);
	}
	 
	/** 改变语言
	 * @param context
	 * @param locale
	 */
	public static void changeLanguage(Context context, Locale locale){
		 Resources resources = context.getResources();// 获得res资源对象
         Configuration config = resources.getConfiguration();// 获得设置对象
         DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
         config.locale = locale;
         resources.updateConfiguration(config, dm);
	}
	
	/** 获取默认的语言
	 * @return
	 */
	public static String getDefaultLanguage(){
		String language = Locale.getDefault().toString();
		LogUtil.i("default language: " + language);
		return language;
	}
	
	public static void changeContextResource(Context context, String lan){
		String language = lan;
		if(language.equals("-1")){//如果没设置，用系统默认的
			return;
		}else{
//			String defaultLanguage = getDefaultLanguage();
//			if(!language.equals(defaultLanguage)){//如果设置的语言和手机上的不一致，则需要切换
//				if(isSimplifiedChinese(language)){//简体
//					changeSimplifiedChinese(context);
//				}else if(isTraditionalChinese(language)){//繁体
//					changeTraditionalChinese(context);
//				}
//			}
			if(isSimplifiedChinese(language)){//简体
				changeSimplifiedChinese(context);
			}else if(isTraditionalChinese(language)){//繁体
				changeTraditionalChinese(context);
			}
		}
	}
	
	/**
	 * 判断当前语言是否繁体
	 * @return
	 */
	public static boolean isTraditional(String _language){
		boolean isTraditional = false;
		String language = _language;
		if(language.equals("-1")){
			if(isTraditionalChinese(getDefaultLanguage())){
				isTraditional = true;
			}
		}else{
			if(isTraditionalChinese(language)){//繁体
				isTraditional = true;
			}
		}
		return isTraditional;
	}
	
	/** 网络字符转换，一般只有简体转换繁体
	 * @param src
	 * @return
	 */
	public static ByteArrayInputStream changeNetworkString(byte[] src, String language){
		if(isTraditional(language)){
			try {
				String str = new String(src, "UTF-8");
				str = ChineseTranslate.sim2Tra(str);
				return new ByteArrayInputStream(str.getBytes());
			} catch (UnsupportedEncodingException e) {
				
			}
			return new ByteArrayInputStream(src);
		}else{
			return new ByteArrayInputStream(src);
		}
	}
	
	/** 网络字符转换，一般只有简体转换繁体
	 * @param src
	 * @return
	 */
	public static String changeNetworkString(String src, String language){
		if(TextUtils.isEmpty(src)){
			return src;
		}
		if(isTraditional(language)){
			src = ChineseTranslate.sim2Tra(src);
		}
		return src;
	}
	
	/** 本地字符转换
	 * @param src
	 * @return
	 */
	public static String changeLocalString(String src, String language){
		if(TextUtils.isEmpty(src)){
			return src;
		}
		if(isTraditional(language)){
			src = ChineseTranslate.sim2Tra(src);
		}else{
			src = ChineseTranslate.tra2Sim(src);
		}
		return src;
	}
	
	/** 转换为简体
	 * @param src
	 * @return
	 */
	public static String changeSimplifiedString(String src){
		if(TextUtils.isEmpty(src)){
			return src;
		}
		src = ChineseTranslate.tra2Sim(src);
		return src;
	}

}
