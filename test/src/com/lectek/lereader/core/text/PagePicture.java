package com.lectek.lereader.core.text;

import com.lectek.lereader.core.text.layout.Page;

import android.graphics.Canvas;
import android.graphics.Picture;

public class PagePicture extends Picture implements IPagePicture{
	private int mPageIndex;
	private int mChapterIndex;
	private Page mPage;

	public PagePicture(int chapterIndex,int index,Page page){
		init(chapterIndex, index,page);
	}
	
	@Override
	public void init(int chapterIndex,int index,Page page){
		mChapterIndex = chapterIndex;
		mPageIndex = index;
		mPage = page;
	}
	
	@Override
	public void release() {
	}
	
	@Override
	public boolean equals(int chapterIndex, int index) {
		return mPageIndex == index && mChapterIndex == chapterIndex;
	}

	@Override
	public boolean equals(Page page) {
		return page.equals(mPage);
	}

	@Override
	public Canvas getCanvas(int width, int height) {
		return super.beginRecording(width, height);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.draw(canvas);
	}
}

