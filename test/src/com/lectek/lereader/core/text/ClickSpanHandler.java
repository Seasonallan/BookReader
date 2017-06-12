package com.lectek.lereader.core.text;

import android.graphics.RectF;

import com.lectek.lereader.core.text.style.ClickActionSpan;

public interface ClickSpanHandler {
	public boolean onClickSpan(ClickActionSpan clickableSpan,RectF localRect,int x, int y);
}
