package com.lectek.android.lereader.widgets.drag;

import android.graphics.Bitmap;

/**
 *  
 * @author laijp
 * @date 2014-7-7
 * @email 451360508@qq.com
 */
public interface IDragItemView {
	
	/**
	 * 判斷位置是否移动到item中心点
	 * @param x
	 * @param y
	 * @return true则不是中心点，
	 */
//	public boolean point2Position(int x, int y);
	
	
	/**
	 * 背景图片展开
	 * @return
	 */
	public boolean openFile();
	
	/**
	 * 背景图片关闭
	 * @return
	 */
	public boolean closeFile();
	
	/**
	 * 背景图片是否为展开，表示要创建或添加书籍
	 * @return
	 */
	public boolean isFileOpen();
	
	/**
	 * 创建文件夹或添加书籍
	 * @param item
	 */
	public <T extends IDragDatas> void onItemAdded(T item, ICallBack callBack);


    public void setVisibility(int visible);

    public Bitmap createBitmap();
}













