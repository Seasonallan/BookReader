package com.lectek.lereader.core.bookformats;

import java.io.IOException;
import java.io.InputStream;

/** 章节信息
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-3-14
 */
public class Chapter {
	
	/** id */
	protected String id;
	/** TITLE */
	protected String title;
	/** 内容 */
	protected byte[] content;
	
	public Chapter(String id, String title, byte[] content){
		this.id = id;
		this.title = title;
		this.content = content;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @return the content
	 */
	public byte[] getContent() {
		return content;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "id: " + id + "; title: " + title;
	}

}
