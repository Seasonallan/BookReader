package com.lectek.lereader.core.text.style;

import android.text.TextPaint;
import android.text.style.CharacterStyle;
/**
 * 对齐样式
 * @author lyw
 *
 */
public class AlignSpan extends CharacterStyle{
	public static final int LEFT_ALIGN = 0;
	public static final int RIGHT_ALIGN = 1;
	public static final int CENTER_ALIGN = 2;
	private int mType = LEFT_ALIGN;

	public AlignSpan(int type){
		mType = type;
	}
	
	public int getType(){
		return mType;
	}
	
	@Override
	public void updateDrawState(TextPaint tp) {
		
	}
}
