package com.lectek.android.lereader.storage.dbase.digest;

import android.text.TextPaint;
import android.text.style.CharacterStyle;

public class BookDigestsSpan extends CharacterStyle{
	private int mColor;
	public BookDigestsSpan(int color){
		mColor = color;
	}
	@Override
	public void updateDrawState(TextPaint tp) {
		tp.bgColor = mColor;
	}
}
