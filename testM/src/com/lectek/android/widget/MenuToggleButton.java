package com.lectek.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import com.lectek.android.LYReader.R;

public class MenuToggleButton extends ToggleButton {
	public MenuToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);	
	}
	
	public MenuToggleButton(Context context) {
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
			setBackgroundResource(R.drawable.menu_toggle_btn_open);
		}else{
			setTextColor(getResources().getColor(R.color.white));
			setBackgroundResource(R.drawable.menu_toggle_btn_close);
		}
	}
	
	
}
