package com.lectek.android.lereader.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;


/** 
 *  
 * @author laijp
 * @date 2014-7-2
 * @email 451360508@qq.com
 */
public class GapScrollView extends ScrollView implements GapView.OverBoundDetector {


	public GapScrollView(Context context) {
		super(context);
	}
	public GapScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean overTop(){
		return  getScrollY() <= 0 ;
	}
	 
	@Override
	public boolean overBottom(){return false;}
	 
}
