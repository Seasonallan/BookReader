package com.lectek.lereader.core.text;

import com.lectek.lereader.core.text.html.HtmlParser;

import android.graphics.Rect;
import android.text.TextPaint;

/**
 * 排版设置参数
 * @author lyw
 *
 */
public class SettingParam {
	private static final String VERSION = "1.0.0";
	/** 原始画笔*/
	private TextPaint sourcePaint;
	/** 页的n内容区域,包含坐标偏移*/
	private Rect pageRect;
	/** 行间距*/
	private int lineSpace;
	/** 段间距*/
	private int paragraphSpace;
	/**
	 * 全屏区域
	 */
	private Rect fullPageRect; 
	
	private ClickSpanHandler clickSpanHandler;
	
	private String key;
	
	private String contentId;
	
	private int mParserType;

	public SettingParam(String contentId,TextPaint sourcePaint, Rect pageRect,Rect fullPageRect
			, int lineSpace,int paragraphSpace,ClickSpanHandler clickSpanHandler) {
		this(HtmlParser.PARSER_TYPE_LEREADER, contentId, sourcePaint, pageRect, fullPageRect, lineSpace, paragraphSpace, clickSpanHandler);
	}
	
	public SettingParam(int parserType,String contentId,TextPaint sourcePaint, Rect pageRect,Rect fullPageRect
			, int lineSpace,int paragraphSpace,ClickSpanHandler clickSpanHandler) {
		this.mParserType = parserType;
		this.sourcePaint = sourcePaint;
		this.pageRect = pageRect;
		this.lineSpace = lineSpace;
		this.fullPageRect = fullPageRect;
		this.paragraphSpace = paragraphSpace;
		this.clickSpanHandler = clickSpanHandler;
		this.contentId = contentId;
		this.key = VERSION +  "-" + 
					lineSpace + "-" + 
					sourcePaint.getTextSize() + "-" +
					paragraphSpace + "-" +
					pageRect.width() + "-" +
					pageRect.height();
	}
	
	public int getParserType(){
		return mParserType;
	}
	
	public String getContentId(){
		return contentId;
	}
	
	public String getKey(){
		return key;
	}
	/**
	 * @return the paragraphSpace
	 */
	public int getParagraphSpace() {
		return paragraphSpace;
	}

	/**
	 * @return the fullPageRect
	 */
	public Rect getFullPageRect() {
		return fullPageRect;
	}

	/**
	 * 原始画笔
	 * @return the sourcePaint
	 */
	public TextPaint getSourcePaint() {
		return sourcePaint;
	}
	/**
	 * 原始画笔
	 * @param sourcePaint the sourcePaint to set
	 */
	public void setSourcePaint(TextPaint sourcePaint) {
		this.sourcePaint = sourcePaint;
	}
	/**
	 * 页的n内容区域,包含坐标偏移
	 * @return the pageRect
	 */
	public Rect getPageRect() {
		return pageRect;
	}
	/**
	 * 页的n内容区域,包含坐标偏移
	 * @param pageRect the pageRect to set
	 */
	public void setPageRect(Rect pageRect) {
		this.pageRect = pageRect;
	}
	/**
	 * 行间距
	 * @return the lineSpace
	 */
	public int getLineSpace() {
		return lineSpace;
	}
	/**
	 * 行间距
	 * @param lineSpace the lineSpace to set
	 */
	public void setLineSpace(int lineSpace) {
		this.lineSpace = lineSpace;
	}

	/**
	 * 获取可点击Span点击事件处理者
	 * @return the clickSpanHandler
	 */
	public ClickSpanHandler getClickSpanHandler() {
		return clickSpanHandler;
	}

	/**
	 * 设置可点击Span点击事件处理者
	 * @param clickSpanHandler the clickSpanHandler to set
	 */
	public void setClickSpanHandler(ClickSpanHandler clickSpanHandler) {
		this.clickSpanHandler = clickSpanHandler;
	}
}
