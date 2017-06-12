package com.lectek.android.lereader.ui.basereader_leyue;


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
	public static final int MENU_ITEM_ID_PDF_LAYOUT_TYPE = 7;
	public static final int MENU_ITEM_ID_PDF_ORIENTATION = 8;

    //2014年6月30日17:43:44 ljp add
    public static final int MENU_ITEM_ID_N_SET = 11;
    public static final int MENU_ITEM_ID_N_NIGHT_DAY = 12;
    public static final int MENU_ITEM_ID_N_MORE = 13;
    public static final int MENU_ITEM_ID_N_MORE_DETAIL = 14;
    public static final int MENU_ITEM_ID_N_MORE_COMMENT = 15;
	public int id;
	public int iconResId;
	public String name;
	
	public MenuItem(int id, int iconResId, String name){
		this.id = id;
		this.iconResId = iconResId;
		this.name = name;
	}

}
