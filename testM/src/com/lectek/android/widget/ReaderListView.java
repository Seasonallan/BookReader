package com.lectek.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/** 在ScrollView中能显示的ListdView
 * @author mingkg21
 * @date 2011-7-31
 * @email mingkg21@gmail.com
 */
public class ReaderListView extends ListView {
	
	public ReaderListView(Context context) {
		super(context);
		setFocusable(false);
	}
	
	public ReaderListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(false);
	}

	public ReaderListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setFocusable(false);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
