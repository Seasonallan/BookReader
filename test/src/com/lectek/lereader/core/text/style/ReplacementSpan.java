package com.lectek.lereader.core.text.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public abstract class ReplacementSpan extends MetricAffectingSpan {
	public abstract void getSize(Paint paint, CharSequence text, int start,
			int end, int maxW,int maxH,Rect container);

	public abstract void draw(Canvas canvas, CharSequence text, int start,
			int end, int left, int top, int right, int bottom,int maxW,int maxH, Paint paint);

	/**
	 * This method does nothing, since ReplacementSpans are measured explicitly
	 * instead of affecting Paint properties.
	 */
	public void updateMeasureState(TextPaint p) {
	}

	/**
	 * This method does nothing, since ReplacementSpans are drawn explicitly
	 * instead of affecting Paint properties.
	 */
	public void updateDrawState(TextPaint ds) {
	}
}
