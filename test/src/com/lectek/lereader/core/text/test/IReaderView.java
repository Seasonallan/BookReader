package com.lectek.lereader.core.text.test;

public interface IReaderView {
	/**
	 * 跳章（点击目录）
	 * @param requestChapterIndex 章节数
	 * @param isStartAnim 是否播放动画
	 */
	public void gotoChapter(int requestChapterIndex,boolean isStartAnim);
	/**
	 * 跳转下一页
	 */
	public void gotoNextPage();
	/**
	 * 跳转上一页
	 */
	public void gotoPrePage();
	/**
	 * 跳转具体页
	 * @param requestChapterIndex 章节数
	 * @param requestIndex 具体页数或者百分比
	 * @param isStartAnim 是否播放动画
	 */
	public void gotoPage(int requestChapterIndex,int requestIndex,boolean isStartAnim);
	
	
}
