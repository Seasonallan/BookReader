package com.lectek.android.app;

import android.view.View;
/**
 * @author lyw
 */
public interface ITitleBar {
	public static final int MENU_ITEM_ID_LEFT_BUTTON = "MENU_ITEM_ID_LEFT_BUTTON".hashCode();
	public static final int MENU_ITEM_ID_RIGHT_BUTTON = "MENU_ITEM_ID_RIGHT_BUTTON".hashCode();
	/**
	 * @param isEnabled
	 */
	public void setTitleBarEnabled(boolean isEnabled);
	/**
	 * 设置一个View替换Title
	 * @param view
	 */
	public void setTitleView(View view);
	/**
	 * 设置title文本
	 * @param titleStr title文本
	 */
	public void setTitleContent(String titleStr);
	/**
	 * 设置左边按钮 
	 * <p>点击事件会触发一个 onOptionsItemSelected(MenuItem item)事件 item id 为 ITitleBar.MENU_ITEM_ID_LEFT_BUTTON</p>
	 * @param tip
	 * @param icon
	 */
	public void setLeftButton(String tip,int icon);
	/**
	 * 设置由变按钮边按钮
	 * <p>点击事件会触发一个 onOptionsItemSelected(MenuItem item)事件 item id 为 ITitleBar.MENU_ITEM_ID_RIGHT_BUTTON</p>
	 * @param tip
	 * @param icon
	 */
	public void setRightButton(String tip,int icon);
	/**
	 * 设置启用 LeftButton
	 * @param isEnabled
	 */
	public void setLeftButtonEnabled(boolean isEnabled);
	/**
	 * 设置启用 RightButton
	 * @param isEnabled
	 */
	public void setRightButtonEnabled(boolean isEnabled);
	/**
	 * 重置TitleBar
	 */
	public void resetTitleBar();
	/**
	 * 替换掉整个标题栏
	 * @param view
	 */
	public void setHeadView(View view);
}
