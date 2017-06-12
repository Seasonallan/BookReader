package com.lectek.android.lereader.widgets.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.lectek.android.lereader.widgets.GapView;


public class DragScrollView extends ScrollView implements GapView.OverBoundDetector {
  
 
	
	public DragScrollView(Context context, AttributeSet attrs) {
		super(context, attrs); 
	} 

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			smoothScrollTo(0, 0);
		}
	}
  
	@Override
	public boolean overTop(){
		return  getScrollY() <= 0 ;
	}
	 
	@Override
	public boolean overBottom(){return false;}
 
}


