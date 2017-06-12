package com.lectek.lereader.core.text.style;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class BlockquoteSpan extends MetricAffectingSpan{
	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setTextSize(ds.getTextSize() * 0.8f);
		ds.setTextSkewX(-0.25f);
	}

	@Override
	public void updateMeasureState(TextPaint ds) {
		ds.setTextSize(ds.getTextSize() * 0.8f);
		ds.setTextSkewX(-0.25f);
	}
}
