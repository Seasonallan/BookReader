package com.lectek.android.lereader.lib.utils;

/** 中文简体繁体转换
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2012-9-16
 */
public final class ChineseTranslate {
	
	/** 简体转繁体
	 * @param src
	 * @return
	 */
	public static final String sim2Tra(String src){
		return translate(src, true);
	}
	
	/** 繁体转简体
	 * @param src
	 * @return
	 */
	public static final String tra2Sim(String src){
		return translate(src, false);
	}
	
	private static final String translate(String src, boolean toTraditional){
		if(src == null || src.length() == 0){
			return src;
		}
		
		StringBuilder det = new StringBuilder(src);
		Character c;
		Character desChar;
		int len = src.length();
		for(int i = 0; i < len; i++){
			c = det.charAt(i);
			if(toTraditional){
				desChar = ChineseChar.sim2TraMap.get(c);
			}else{
				desChar = ChineseChar.tra2SimMap.get(c);
			}
			if(desChar != null){
				det.setCharAt(i, desChar);
			}
		}
		
		return det.toString();
	}

}
