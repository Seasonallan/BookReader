package com.lectek.lereader.core.text.test;


/**
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-11-2
 */
public class MenuItem {
	public static final int MENU_ITEM_ID_CATALOG = 1;
	public static final int MENU_ITEM_ID_FONT = 2;
	public static final int MENU_ITEM_ID_THEME = 3;
	public static final int MENU_ITEM_ID_BRIGHTNESS = 4;
	public static final int MENU_ITEM_ID_SETTING = 5;
	public static final int MENU_ITEM_ID_MORE = 6;
	
	public int id;
	public int iconResId;
	public String name;
	
	public MenuItem(int id, int iconResId, String name){
		this.id = id;
		this.iconResId = iconResId;
		this.name = name;
	}

}
