package com.lectek.lereader.core.bookformats;

/** 书籍信息
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-2-19
 */
public class BookInfo {
	public String title = "";
	public String id = "";
	public String author = "";
	public String publisher = "";
	public boolean isMediaDecode;
	public boolean isCartoon = false;
	
	@Override
	public String toString() {
		return "BookInfo [title=" + title + ", id=" + id + ", author=" + author
				+ ", publisher=" + publisher + ", isMediaDecode="
				+ isMediaDecode + "]";
	}
}
