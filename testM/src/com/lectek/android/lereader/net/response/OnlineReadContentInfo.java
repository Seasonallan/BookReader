package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 * 在线阅读内容
 * @author Administrator
 *
 */
public class OnlineReadContentInfo extends BaseDao{

	@Json(name = "bookContents")
	public String bookContents;// 内容为html, 例如<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n <head>

	/**
	 * @return the bookContents
	 */
	public String getBookContents() {
		return bookContents;
	}

	/**
	 * @param bookContents the bookContents to set
	 */
	public void setBookContents(String bookContents) {
		this.bookContents = bookContents;
	}
}
