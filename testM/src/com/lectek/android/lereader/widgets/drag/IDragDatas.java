package com.lectek.android.lereader.widgets.drag;
/** 
 *  拖动数据基类
 * @author laijp
 * @date 2014-7-7
 * @email 451360508@qq.com
 */
public interface IDragDatas {
	/**
	 * 本身是否是文件夹
	 * @return
	 */
	public boolean isFile();
	/**
	 * 是否是文件夹里面的item
	 * @return
	 */
	public boolean isInFile();

    /**
     * 是否是添加按钮
     * @return
     */
    public boolean isEmpty();
}
