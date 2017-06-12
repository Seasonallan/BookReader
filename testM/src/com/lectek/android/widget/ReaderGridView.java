package com.lectek.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

/** 在ScrollView中能显示的GridView
 * @author mingkg21
 * @date 2011-7-31
 * @email mingkg21@gmail.com
 */
public class ReaderGridView extends GridView {
	
	public ReaderGridView(Context context) {
		super(context);
		setFocusable(false);
	}
	
	public ReaderGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(false);
	}

	public ReaderGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setFocusable(false);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
	
	public void releaseRes(){
		this.setOnItemClickListener(null);
		this.setAdapter(null);
		this.destroyDrawingCache();
		if(this.getParent() instanceof ViewGroup){
			((ViewGroup)this.getParent()).removeView(this);
		}
	}
}
