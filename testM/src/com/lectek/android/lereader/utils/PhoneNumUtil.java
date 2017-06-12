package com.lectek.android.lereader.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 电话号码的工具类
 * 
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2012-9-7
 */
public final class PhoneNumUtil {

	/**
	 * 判断是否电信的号码
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isChinaTelecomNumber(String number) {
		Pattern pattern = Pattern.compile("^(133|153|180|181|189)[0-9]{8}");
		Matcher matcher = pattern.matcher(number);
		if (matcher.matches()) {
			return true;
		}
		return false;
	}
	
	/** 判断是否非电信手机号码
	 * @param number
	 * @return
	 */
	public static boolean isNoChinaTelecomNumber(String number) {
		Pattern pattern = Pattern
				.compile("^(134|135|136|137|138|139|147|150|151|152|157|158|159|182|183|187|188|130|131|132|155|156|185|186|145)[0-9]{8}");
		Matcher matcher = pattern.matcher(number);
		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	/** 判断是否手机号码
	 * 移动的号段：134(0-8)、135、136、137、138、139、147（预计用于TD上网卡）、150、151、152、157（TD专用）、
	 * 158、159、187（未启用）、182、183、188（TD专用）
	 * 联通的号段：130、131、132、155、156（世界风专用）、185（未启用）、186（3g）、145
	 * 电信的号段：133、153、180、181、189 网通在并入联通之前只经营固话和小灵通，没有手机号段。
	 * 中国卫通的卫星电话使用1349号段，现在卫通的基础电信服务并入了电信、不知这号段有没有移交给电信。
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isPhoneNumber(String number) {
		Pattern pattern = Pattern
				.compile("^(134|135|136|137|138|139|147|150|151|152|157|158|159|182|183|187|188|130|131|132|155|156|185|186|145|133|153|180|181|189)[0-9]{8}");
		Matcher matcher = pattern.matcher(number);
		if (matcher.matches()) {
			return true;
		}
		return false;
	}

}
