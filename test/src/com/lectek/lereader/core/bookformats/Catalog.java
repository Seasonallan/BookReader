package com.lectek.lereader.core.bookformats;

/** 书籍的目录
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-2-19
 */
public class Catalog {
	/** 目录的href */
	private String href;
	/** 目录的TITLE */
	private String text;
	/** 目录的背景颜色 */
	private Integer bgColor;
	/** 目录的字体颜色 */
	private Integer textColor;
	/** 目录的所在页 */
	private Integer pageIndex;
	/** 目录的层 */
	private int layer;
	/** 目录顺序**/
	private int index;
	/** 父节点**/
	private Catalog parent;
	/** 是否有标题**/
	private boolean hasLabel;
	
	public Catalog(Catalog parent,int index){
		setParent(parent);
		setIndex(index);
		setLayer(parent == null ? 1 : parent.getLayer() + 1);
	}
	
	/**
	 * @return the hasLabel
	 */
	public boolean hasLabel() {
		return hasLabel;
	}

	/**
	 * @param hasLabel the hasLabel to set
	 */
	public void setHasLabel(boolean hasLabel) {
		this.hasLabel = hasLabel;
	}

	/**
	 * @return the parent
	 */
	public Catalog getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Catalog parent) {
		this.parent = parent;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the id
	 */
	public String getHref() {
		return href;
	}
	/**
	 * @param id the id to set
	 */
	public void setHref(String href) {
		this.href = href;
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
	 * @return the bgColor
	 */
	public Integer getBgColor() {
		return bgColor;
	}

	/**
	 * @param bgColor the bgColor to set
	 */
	public void setBgColor(Integer bgColor) {
		this.bgColor = bgColor;
	}

	/**
	 * @return the textColor
	 */
	public Integer getTextColor() {
		return textColor;
	}

	/**
	 * @param textColor the textColor to set
	 */
	public void setTextColor(Integer textColor) {
		this.textColor = textColor;
	}

	/**
	 * @return the layer
	 */
	public int getLayer() {
		return layer;
	}

	/**
	 * @param layer the layer to set
	 */
	public void setLayer(int layer) {
		this.layer = layer;
	}

	/**
	 * @return the pageIndex
	 */
	public Integer getPageIndex() {
		return pageIndex;
	}

	/**
	 * @param pageIndex the pageIndex to set
	 */
	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}
}
