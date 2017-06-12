package com.lectek.android.lereader.widgets;

import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.PopupWindow;

/**
 * 悬浮弹窗通用类
 * 
 * @author yangwq
 * @date 2014年7月4日
 * @email 57890940@qq.com
 */
public class CustomPopupWindow extends PopupWindow{
	
	private View mPopView;	//悬浮窗的View
	
	public CustomPopupWindow(View view, int width, int height) {
		super(view, width, height);
		mPopView = view;
		init();
	}
	
	public CustomPopupWindow(View view) {
		super(view);
		mPopView = view;
		init();
	}
	
	/**
	 * 初始化悬浮弹窗
	 */
	private void init(){
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		this.setBackgroundDrawable(new BitmapDrawable());
	}
	
	/**
	 * 显示悬浮窗
	 */
	public void showView(View anchor){
		this.showAsDropDown(anchor, 0, 0);	//设置弹出的位置
	}
	
	/**
	 * 显示悬浮窗在Anchor视图的中间
	 * @param anchor
	 */
	public void showViewAtAnchorCenter(View anchor){
		int width = anchor.getWidth() / 2 - mPopView.getWidth() / 2;
		int height = mPopView.getHeight() / 2 - anchor.getHeight() / 2;
		this.showAsDropDown(anchor, width, height);
	}
	
	/**
	 * 显示悬浮窗在Anchor视图的正上方
	 * @param anchor
	 */
	public void showViewAtAnchorTopCenter(View anchor){
		int width = anchor.getWidth() / 2 - mPopView.getWidth() / 2;
		int height = -anchor.getHeight() - mPopView.getHeight();
		this.showAsDropDown(anchor, width, height);
	}
	
	/**
	 * 显示悬浮窗的上边缘在Anchor视图的正上方
	 * @param anchor
	 */
	public void showViewAtAnchorAlignTopCenter(View anchor){
		int width = anchor.getWidth() / 2 - mPopView.getWidth() / 2;
		int height = -anchor.getHeight();
		this.showAsDropDown(anchor, width, height);
	}
	
	
	/**
	 * 关闭悬浮窗
	 */
	public void dismissView(){
		if(this.isShowing()){
			this.dismiss();
		}
	}
	
	
	
	

}
