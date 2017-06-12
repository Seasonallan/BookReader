package com.lectek.lereader.core.pdf.jni;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * pdf核心类，包含本地接口
 * @author zhouxinghua
 *
 */
public class MuPDFCore {

	/* Readable members */
	/**
	 * 当前页
	 */
	private int pageNum  = -1;
	/**
	 * 总页数
	 */
	private int numPages = -1;
	public  float pageWidth;
	public  float pageHeight;

	/* The native functions */
	/**
	 * 打开pdf文件
	 * @param filename
	 * @return
	 */
	private static native int openFile(String filename);
	/**
	 * 计算总页数
	 * @return
	 */
	private static native int countPagesInternal();
	/**
	 * 跳转至哪一页
	 * @param localActionPageNum
	 */
	private static native void gotoPageInternal(int localActionPageNum);
	/**
	 * 获取页面宽度
	 * @return
	 */
	private static native float getPageWidth();
	/**
	 * 获取页面高度
	 * @return
	 */
	private static native float getPageHeight();
	/**
	 * 画页面内容
	 * @param bitmap
	 * @param pageW
	 * @param pageH
	 * @param patchX
	 * @param patchY
	 * @param patchW
	 * @param patchH
	 */
	public static native void drawPage(Bitmap bitmap,
			int pageW, int pageH,
			int patchX, int patchY,
			int patchW, int patchH);
	public static native RectF[] searchPage(String text);
	public static native int getPageLink(int page, float x, float y);
	public static native LinkInfo [] getPageLinksInternal(int page);
	/**
	 * 获取目录数据
	 * @return
	 */
	public static native OutlineItem [] getOutlineInternal();
	/**
	 * 是否有目录
	 * @return
	 */
	public static native boolean hasOutlineInternal();
	/**
	 * 是否需要密码
	 * @return
	 */
	public static native boolean needsPasswordInternal();
	/**
	 * 验证密码
	 * @param password
	 * @return
	 */
	public static native boolean authenticatePasswordInternal(String password);
	/**
	 * 销毁资源
	 */
	public static native void destroying();

	public MuPDFCore(String filename) throws Exception
	{
		if (openFile(filename) <= 0)
		{
			throw new Exception("Failed to open "+filename);
		}
	}

	/**
	 * 计算总页数
	 * @return
	 */
	public  int countPages()
	{
		if (numPages < 0)
			numPages = countPagesSynchronized();

		return numPages;
	}

	private synchronized int countPagesSynchronized() {
		return countPagesInternal();
	}

	/* Shim function */
	/**
	 * 跳转至哪一页
	 * @param page
	 */
	public void gotoPage(int page)
	{
		if (page > numPages-1)
			page = numPages-1;
		else if (page < 0)
			page = 0;
		if (this.pageNum == page)
			return;
		gotoPageInternal(page);
		this.pageNum = page;
		this.pageWidth = getPageWidth();
		this.pageHeight = getPageHeight();
	}

	/**
	 * 获取页面宽高
	 * @param page
	 * @return
	 */
	public synchronized PointF getPageSize(int page) {
		gotoPage(page);
		return new PointF(pageWidth, pageHeight);
	}

	/**
	 * 销毁释放资源
	 */
	public synchronized void onDestroy() {
		destroying();
	}

	/**
	 * 画页面内容
	 * @param page	第几页
	 * @param bitmap
	 * @param pageW
	 * @param pageH
	 * @param patchX
	 * @param patchY
	 * @param patchW
	 * @param patchH
	 */
	public synchronized void drawPage(int page, Bitmap bitmap,
			int pageW, int pageH,
			int patchX, int patchY,
			int patchW, int patchH) {
		gotoPage(page);
		drawPage(bitmap, pageW, pageH, patchX, patchY, patchW, patchH);
	}

	public synchronized int hitLinkPage(int page, float x, float y) {
		return getPageLink(page, x, y);
	}

	public synchronized LinkInfo [] getPageLinks(int page) {
		return getPageLinksInternal(page);
	}

	/**
	 * 搜索指定页面的文本
	 * @param page
	 * @param text
	 * @return
	 */
	public synchronized RectF [] searchPage(int page, String text) {
		gotoPage(page);
		return searchPage(text);
	}

	/**
	 * 是否有目录
	 * @return
	 */
	public synchronized boolean hasOutline() {
		return hasOutlineInternal();
	}

	/**
	 * 获取目录数据
	 * @return
	 */
	public synchronized OutlineItem [] getOutline() {
		return getOutlineInternal();
	}

	/**
	 * 打开文件是否需要密码
	 * @return
	 */
	public synchronized boolean needsPassword() {
		return needsPasswordInternal();
	}

	/**
	 * 验证密码是否正确
	 * @param password
	 * @return
	 */
	public synchronized boolean authenticatePassword(String password) {
		return authenticatePasswordInternal(password);
	}
}
