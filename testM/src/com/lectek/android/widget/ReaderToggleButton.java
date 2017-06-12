package com.lectek.android.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import com.lectek.android.LYReader.R;

public class ReaderToggleButton extends ToggleButton{
	public ReaderToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);	
	}
	
	public ReaderToggleButton(Context context) {
		super(context, null);
	}
	
	@Override
	public void setChecked(boolean checked) {
		super.setChecked(checked);
		syncTextAndBackGroundState(checked);
	}
	
	private void syncTextAndBackGroundState(boolean checked) {
		if(checked){
			setTextColor(getResources().getColor(R.color.white));
			setBackgroundResource(R.drawable.toggle_btn_open);
		}else{
			setTextColor(getResources().getColor(R.color.gray));
			setBackgroundResource(R.drawable.toggle_btn_close);
		}
	}
	
	
}
