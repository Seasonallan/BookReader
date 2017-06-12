package com.lectek.android.widget;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class MiddleLineTextView extends TextView {

	public MiddleLineTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		System.out.println("tv");
		getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
	}


	 

}
