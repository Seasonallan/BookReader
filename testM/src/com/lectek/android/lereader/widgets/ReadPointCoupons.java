package com.lectek.android.lereader.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.lectek.android.LYReader.R;

public class ReadPointCoupons extends LinearLayout {
	private static final String NUMBER_RES_PRE = "read_point_";
	private LinearLayout mContentLayout;
	private int mReadPoint = -1;
	public ReadPointCoupons(Context context) {
		super(context);
		init();
	}
	
	public ReadPointCoupons(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init(){
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
		
		LayoutParams params = null;
		
		ImageView tempReadTipIV = new ImageView(getContext());
		tempReadTipIV.setImageResource(R.drawable.read_point_ic);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tempReadTipIV.setVisibility(View.INVISIBLE);
		super.addView(tempReadTipIV,-1, params);
		
		mContentLayout = new LinearLayout(getContext());
		mContentLayout.setId(mContentLayout.hashCode());
		mContentLayout.setOrientation(LinearLayout.HORIZONTAL);
		mContentLayout.setGravity(Gravity.CENTER);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		super.addView(mContentLayout,-1, params);
		
		ImageView readTipIV = new ImageView(getContext());
		readTipIV.setImageResource(R.drawable.read_point_ic);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		super.addView(readTipIV,-1, params);
	}
	
	@Deprecated
	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		
	}

	public void setReadPoint(int readPoint){
		if(readPoint == mReadPoint){
			return;
		}
		mContentLayout.removeAllViews();
		mReadPoint = readPoint;
		if(readPoint > 9){
			addNumbers(readPoint);
		}else{
			addNumber(readPoint);
		}
	}
	
	private void addNumbers(int number){
		int temp = 0;
		for(;number > 0;){
			temp = number % 10;
			number = number / 10;
			addNumber(temp);
		}
	}
	
	private void addNumber(int number){
		ImageView iv = new ImageView(getContext());
		iv.setScaleType(ScaleType.CENTER);
		iv.setImageResource(getNumberRes(number));
		mContentLayout.addView(iv, 0);
	}
	
	private int getNumberRes(int num){
		if(num < 0 || num > 9){
			return 0;
		}
		return getResources().getIdentifier(NUMBER_RES_PRE + num
				, getResources().getResourceTypeName(R.drawable.ic_launcher)
				, getResources().getResourcePackageName(R.drawable.ic_launcher));
	}
	
	public static int getNumberBGRes(int position){
		int temp = position / 2;
		switch (temp) {
		case 0:
			return R.drawable.read_point_default_bg;
		case 1:
			return R.drawable.read_point_default_bg;
		case 2:
			return R.drawable.read_point_default_bg;
		case 3:
			return R.drawable.read_point_default_bg;
		}
		return 0;
	}
}
