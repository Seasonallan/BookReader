package com.lectek.lereader.core.text;

import com.lectek.lereader.core.text.layout.Page;

import android.graphics.Canvas;

public interface IPagePicture {
	public void init(int chapterIndex,int index,Page page);
	
	public boolean equals(int chapterIndex,int index);

	public boolean equals(Page page);
	
	public Canvas getCanvas(int width, int height);

	public void onDraw(Canvas canvas);
	
	public void release();
}

