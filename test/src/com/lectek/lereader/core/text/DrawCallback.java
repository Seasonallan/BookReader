package com.lectek.lereader.core.text;

import android.graphics.Canvas;

public interface DrawCallback {
	/**
	 * 绘制内容页之后
	 * @param canvas 需要绘制内容的画布
	 * @param chapterIndex 章节下标
	 * @param pageIndex 页下标
	 */
	public void onPostDrawContent(Canvas canvas,boolean isFullScreen);
	/**
	 * 绘制内容页之前
	 * @param canvas 需要绘制内容的画布
	 * @param chapterIndex 章节下标
	 * @param pageIndex 页下标
	 */
	public void onPreDrawContent(Canvas canvas,boolean isFullScreen);
}
