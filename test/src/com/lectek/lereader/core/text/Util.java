package com.lectek.lereader.core.text;

import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;

import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.MetricAffectingSpan;
import android.util.SparseArray;

import com.lectek.lereader.core.text.style.ReplacementSpan;

/**
 * 工具类包含杂七杂八的独立方法
 * @author lyw
 *
 */
public final class Util {
	private static final String DEFAULT_CN_CHAR = "测";
	private static final int MAX_WIDTH_MAP_SIZE = 10000;
	private static SparseArray<Short> mWidthMap = new SparseArray<Short>();
	/**
	 * 获取最后一个制定类型的span
	 * @param start
	 * @param end
	 * @param type
	 * @return
	 */
	public static <T> T findLastSpans(Object[] arr, Class<T> type){
		if(arr == null){
			return null;
		}
		for(int i = arr.length - 1;i >= 0;i--){
			if(type.isInstance(arr[i])){
				return (T) arr[i];
			}
		}
		return null;
	}
	
	public static <T> T findFirstSpans(Object[] arr, Class<T> type){
		if(arr == null){
			return null;
		}
		int length = arr.length;
		for(int i = 0;i < length;i++){
			if(type.isInstance(arr[i])){
				return (T) arr[i];
			}
		}
		return null;
	}
	
	public static void measureText(StyleText styleText
			,TextPaint paint,int index, int maxW,int maxH,Rect container){
		char c = styleText.charAt(index);
		ReplacementSpan replacementSpan = null;
		boolean isEN = c <= 127 && c >= 0;
		boolean isEnter = false;
		if(!isEN){
			if(styleText.hasCNRect()){
				container.set(0,0,styleText.getCNWidth(),styleText.getCNHeight());
				return;
			}
		}else{
			isEnter = c == '\n' || c == '\u2029';
		}
		CharacterStyle[] characterStyles = styleText.getTotalSpans();
		for(CharacterStyle characterStyle : characterStyles){
			if(Constant.REPLACEMENT_SPAN_CHAR == c){
				if(characterStyle instanceof ReplacementSpan){
					replacementSpan = (ReplacementSpan) characterStyle;
					break;
				}
			}else{
				if(characterStyle instanceof MetricAffectingSpan){
					((MetricAffectingSpan) characterStyle).updateMeasureState(paint);
				}
			}
		}
		if(replacementSpan != null){
			replacementSpan.getSize(paint, styleText.getDataSource(), index, index + 1,maxW,maxH,container);
			return;
		}
		short charWidth = 0;
		short charHeight = (short) paint.getFontSpacing();
		float textSize = paint.getTextSize();
		if(isEnter){
			charWidth = 0;
		}else {
			if(isEN){
				int key = (int) (c * 1000 + textSize);
				Short charWidthTemp = mWidthMap.get(key);
				if(charWidthTemp == null){
					charWidth = (short) paint.measureText(String.valueOf(c));
					if(mWidthMap.size() >= MAX_WIDTH_MAP_SIZE){
						mWidthMap.clear();
					}
					mWidthMap.put(key, charWidth);
				}else{
					charWidth = charWidthTemp;
				}
			}else{
				charWidth = (short) paint.measureText(DEFAULT_CN_CHAR);
				styleText.setCNRect(charWidth, charHeight);
			}
		}
		container.set(0,0,charWidth,charHeight);
	}
	
	public static Integer parseHtmlColor(String color) {
		if (null == color){
			return null;
		}
		Integer i = COLORS.get(color.toLowerCase());
		if (i != null) {
			return i;
		} else if(color.indexOf("rgb") != -1){
			try {
				i = convertRGBAToInt(color);
			} catch (Exception nfe) {
				nfe.printStackTrace();
			}
		} else {
			try {
				i = convertValueToInt(color);
			} catch (Exception nfe) {
				nfe.printStackTrace();
			}
		}
		return i;
	}
	
	private static final Integer convertRGBAToInt(String color){
		int start = color.indexOf("(") + 1;
		int end = color.indexOf(")");
		String[] colors = null;
		if(0 <= start && start <= end && end <= color.length()){
			colors = color.substring(start, end).split(",");
		}
		if(colors != null){
			if(colors.length == 3){
				return Color.rgb(convertColorValue(colors[0]), convertColorValue(colors[1])
						, convertColorValue(colors[2]));
			}else if(colors.length == 4){
				String alpha = colors[3];
				if(!TextUtils.isEmpty(alpha) && alpha.charAt(0) == '.'){
					alpha = "0" + alpha;
				}
				return Color.argb((int) (255 * Float.valueOf(alpha)),convertColorValue(colors[0])
						, convertColorValue(colors[1]), convertColorValue(colors[2]));
			}
		}
		return null;
	}
	
	private static int convertColorValue(String value){
		if(value.length() > 1 && value.indexOf("%") != -1){
			return (int) (Float.valueOf(value.substring(0, value.length() - 1)) * 255 / 100);
		}else{
			return Integer.valueOf(value);
		}
	}
	
	public static int getHtmlColor(String color) {
		Integer i = parseHtmlColor(color);
		return i != null ? i : 0;
	}

	private static final Integer convertValueToInt(CharSequence charSeq) {
		StringBuffer nm = new StringBuffer(charSeq.toString());
		if ('#' == nm.charAt(0)) {
			if(nm.length() == 4){
				nm.insert(3, nm.charAt(3));
				nm.insert(2, nm.charAt(2));
				nm.insert(1, nm.charAt(1));
			}
		}
		try {
	        return Color.parseColor(nm.toString());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	private static HashMap<String, Integer> COLORS = buildColorMap();

	private static HashMap<String, Integer> buildColorMap(){
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("aqua", 0xFF00FFFF);
		map.put("black", 0xFF000000);
		map.put("blue", 0xFF0000FF);
		map.put("fuchsia", 0xFFFF00FF);
		map.put("green", 0xFF008000);
		map.put("grey", 0xFF808080);
		map.put("lime", 0xFF00FF00);
		map.put("maroon", 0xFF800000);
		map.put("navy", 0xFF000080);
		map.put("olive", 0xFF808000);
		map.put("purple", 0xFF800080);
		map.put("red", 0xFFFF0000);
		map.put("silver", 0xFFC0C0C0);
		map.put("teal", 0xFF008080);
		map.put("white", 0xFFFFFFFF);
		map.put("yellow", 0xFFFFFF00);
		map.put("transparent", 0);
		return map;
	}
	
	public static ArrayList<String[]> handlerInlineCss(Attributes attributes){
		if(attributes == null){
			return null;
		}
		String str = attributes.getValue("", "style");
		if(!TextUtils.isEmpty(str)){
			ArrayList<String[]> css = new ArrayList<String[]>();
			char[] tempChars = new char[str.length()];
			int tempLength = 0;
			String[] propertys = null;
			boolean space = false;
			boolean hasKH = false;
			for(int i = 0;i < str.length();i++){
				if(propertys == null){
					propertys = new String[2];
				}
				char c = str.charAt(i);
				if(c >= 33 && c <= 126 || c == ' '){
					if(c == ';'){
						propertys[1] = new String(tempChars, 0, tempLength);
						css.add(propertys);
						propertys = null;
						tempLength = 0;
					}else if(c == ':'){
						propertys[0] = new String(tempChars, 0, tempLength);
						tempLength = 0;
					}else{
						if(c == ' '){
							space = true;
							continue;
						}
						if(space){
							space = false;
							if(tempLength > 0 && !hasKH){
								tempChars[tempLength] = ' ';
								tempLength++;
							}
						}
						tempChars[tempLength] = c;
						tempLength++;
						if(c == '('){
							hasKH = true;
						}
						if(c == ')'){
							hasKH = false;
						}
					}
				}
			}
			if(tempLength > 0){
				propertys[1] = new String(tempChars, 0, tempLength);
			}
			if(propertys != null){
				css.add(propertys);
			}
			return css;
		}
		return null;
	}
}
