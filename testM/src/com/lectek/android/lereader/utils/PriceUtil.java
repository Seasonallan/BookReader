package com.lectek.android.lereader.utils;

/** 价格工具类
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2012-2-13
 */
public final class PriceUtil {
	
	/** 格式或价格；price(单位分)->0.00元
	 * @param sb
	 * @return
	 */
	public static String formatPrice(StringBuilder sb) {
		return formatPrice(sb.toString());
	}
	
	/** 格式或价格；price(单位分)->0.00元
	 * @param price
	 * @return
	 */
	public static String formatPrice(String price) {
		try {
			int temp = Integer.valueOf(price);
			return formatPrice(temp);
		} catch (Exception e) {
			return price;
		}
	}

	/** 格式或价格；price(单位分)->0.00元
	 * @param price
	 * @return
	 */
	public static String formatPrice(int price) {
		int temp = price;
		int tc = temp / 100;
		int tb = temp % 100;
		StringBuilder s = new StringBuilder();
		if (tc > 0) {
			s.append(tc);
		} else {
			s.append("0");
		}
		if (tb > 0) {
			s.append(".");
			if (tb < 10) {
				s.append("0");
			}
			s.append(tb);
		} else {
			s.append(".00");
		}
		return s.toString();
	}
	
	/** 判断价格是否免费
	 * @param price
	 * @return
	 */
	public static boolean isFree(String price){
		if("0".equals(price)){
			return true;
		}
		return false;
	}

}
