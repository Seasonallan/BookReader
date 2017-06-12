package com.lectek.lereader.core.text.style;

import android.graphics.RectF;
import android.text.TextPaint;
import android.text.style.CharacterStyle;

public class UrlSpna extends CharacterStyle implements ClickActionSpan{
	private final String mURL;
    public UrlSpna(String url) {
        mURL = url;
    }
    
    public String getUrl(){
		return mURL;
    }
    
	@Override
	public boolean isClickable() {
		return true;
	}
	
	@Override
	public void updateDrawState(TextPaint tp) {
		tp.setColor(tp.linkColor);
		tp.setUnderlineText(true);
	}

	@Override
	public void checkContentRect(RectF rect) {
	}
}
