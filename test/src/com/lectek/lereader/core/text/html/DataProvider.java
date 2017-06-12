package com.lectek.lereader.core.text.html;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.drawable.Drawable;
/**
 * 图片提供者，负责获取图片
 * @author lyw
 *
 */
public interface DataProvider {
	/**
	 * 获取图片
	 * @param sourceUri 图片的Uri
	 * @param drawableContainer 异步加载图片时可以通过这个图片容器异步设置图片
	 * @return Drawable 如果是异步加载返回默认图片
	 */
	public Drawable getDrawable(String source,DrawableContainer drawableContainer);
	/**
	 * 获取上下文
	 * @return
	 */
	public Context getContext();
	/**
	 * 异步加载图片时可以通过这个图片容器异步设置图片
	 */
	public interface DrawableContainer{
		public void setDrawable(Drawable drawable);
		public boolean isInvalid();
	}
	
	public InputStream getDataStream(String source) throws IOException;
	
	public boolean hasData(String source);
}
