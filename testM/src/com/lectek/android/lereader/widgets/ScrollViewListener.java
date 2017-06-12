package com.lectek.android.lereader.widgets;

import android.widget.HorizontalScrollView;

/**
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-5-5
 */
public interface ScrollViewListener {
	
	void onScrollChanged(HorizontalScrollView scrollView, int x, int y, int oldx, int oldy);
	
	void onLayout(boolean changed, int l, int t, int r, int b);

}
