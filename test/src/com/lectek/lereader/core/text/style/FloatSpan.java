package com.lectek.lereader.core.text.style;

import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateLayout;
/**
 * 浮动样式
 * @author lyw
 *
 */
public class FloatSpan extends CharacterStyle implements UpdateLayout{
	/**
	 * 浮动类型：左悬浮
	 */
	public static final int LEFT_FLOAT = 0;
	/**
	 * 浮动类型：右悬浮
	 */
	public static final int RIGHT_FLOAT = 1;
	/**
	 * 浮动类型
	 */
	private int mType;
	
	public FloatSpan(int type){
		mType = type;
	}
	/**
	 * 获得浮动类型
	 * @return
	 */
	public int getType(){
		return mType;
	}
	
	@Override
	public void updateDrawState(TextPaint tp) {
		
	}
}
