package com.lectek.android.lereader.data;

/**
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-11-26
 */
public class ReadStyleItem {
	
	public int id;
	public int resId;
	public boolean isSelected;
	
	public ReadStyleItem(int id, int resId, boolean isSelected){
		this.id = id;
		this.resId = resId;
		this.isSelected = isSelected;
	}

}
