package com.lectek.lereader.core.bookformats.epub;

/**
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-2-25
 */
public class NavPoint extends Resource{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6640902331520854018L;
	private final int order;
	private final int level;
	private String text = "";
	private String content = "";
	
	public NavPoint(String filePath, String href, int order, int level){
		super(filePath, href);
		this.order = order;
		this.level = level;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
		this.href = content;
	}

	/**
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

}