package com.lectek.lereader.core.text.html;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.graphics.Rect;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;

import com.lectek.lereader.core.text.PageManager.TaskListener;
import com.lectek.lereader.core.text.StyleText;
import com.lectek.lereader.core.text.html.css.PropertyValue;
import com.lectek.lereader.core.text.style.Border;
import com.lectek.lereader.core.util.ContextUtil;

public abstract class HtmlParser {
	public static EmptyStyle EMPTY_STYLE = new EmptyStyle();
	public static final int PARSER_TYPE_LEREADER = 0;
	public static final int PARSER_TYPE_SURFINGREADER = 1;
	public static final String TAG = HtmlParser.class.getSimpleName();
	private HtmlContentHandler mConverter;
	public static HtmlParser create(int type,ICssProvider cssProvider, DataProvider imageGetter
			,TaskListener task,TagHandler tagHandler,SizeInfo sizeInfo){
		switch (type) {
		case PARSER_TYPE_LEREADER:
			return new LeReaderHtmlParser(cssProvider, imageGetter, task, tagHandler,sizeInfo);
		case PARSER_TYPE_SURFINGREADER:
			return new SurfingReaderHtmlParser(cssProvider, imageGetter, task, tagHandler,sizeInfo);
		}
		return null;
	}
	
	protected HtmlParser(ICssProvider cssProvider, DataProvider imageGetter,TaskListener task
			,TagHandler tagHandler,SizeInfo sizeInfo) {
		XMLReader parser = null;
		try {
			//tagsoup 的解析方式
//			parser = new Parser();
//			parser.setProperty(Parser.schemaProperty,new HTMLSchema());
			//原始的SAX解析方式 
			SAXParserFactory factory = SAXParserFactory.newInstance();
			parser = factory.newSAXParser().getXMLReader();
		} catch (org.xml.sax.SAXNotRecognizedException e) {
			// Should not happen.
			throw new RuntimeException(e);
		} catch (org.xml.sax.SAXNotSupportedException e) {
			// Should not happen.
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		mConverter = createHandler(cssProvider,imageGetter, parser,task,tagHandler,sizeInfo);
	}
	
	public StyleText getStyleText(){
		return mConverter.getStyleText();
	}
	
	public final void start(InputStream byteStream) throws RuntimeException{
		start(new InputSource(byteStream));
	}
	
	public final void start(String source) throws RuntimeException{
		start(new InputSource(new StringReader(source)));
	}
	
	public final void start(InputSource source) throws RuntimeException{
		mConverter.convert(source);
	}
	
	protected abstract HtmlContentHandler createHandler(ICssProvider cssProvider,DataProvider imageGetter
			, XMLReader parser,TaskListener task,TagHandler tagHandler,SizeInfo sizeInfo);
	
	public static class SizeInfo{
		int mEmUnit;
		int mPageWidth;
		int mPageHeight;
		Rect mPageRect;
		public SizeInfo(int emUnit, Rect pageRect) {
			mEmUnit = emUnit;
			mPageRect = pageRect;
			mPageWidth = pageRect.width();
			mPageHeight = pageRect.height();
		}
	}
	
	public static class TagInfo{
		String mTag;
		String mClass;
		String mId;
		Attributes mAttributes;
		int mStart;
		CharacterStyle[] mSpanArr;
		int mSpanSize;
		StyleText mStyleText;
		StyleText mParentStyleText;
		String mWidthValue;
		String mHeightValue;
		String mBGWidthValue;
		String mBGHeightValue;
		ArrayList<PropertyValue> mClassInfos;
		SizeInfo mSizeInfo;
		TagInfo(String tag,int start,Attributes attributes,SizeInfo sizeInfo){
			mClassInfos = new ArrayList<PropertyValue>();
			mSizeInfo = sizeInfo;
			init(tag, start, attributes);
		}
		
		public boolean isCover(){
			return mStyleText.isCover();
		}
		
		public Integer handleBorderSize(String string){
			if(TextUtils.isEmpty(string)){
				return null;
			}
			Integer s = handleSize(string);
			if(s == null){
				if("thin".equals(string)){
					s = ContextUtil.DIPToPX(1);
				}else if("medium".equals(string)){
					s = ContextUtil.DIPToPX(2);
				}else if("thick".equals(string)){
					s = ContextUtil.DIPToPX(3);
				}
			}
			return s;
		}
		
		public Integer handleSize(String value){
			if(!TextUtils.isEmpty(value)){
				try {
					if(value.length() > 2 && value.indexOf("em") != -1){
						return (int) (Float.valueOf(value.substring(0, value.length() - 2)) * mSizeInfo.mEmUnit);
					}else if(value.length() > 2 && value.indexOf("px") != -1){
						return (int) (Float.valueOf(value.substring(0, value.length() - 2)) * 1f);
					}else if(value.length() > 1 && value.indexOf("%") != -1){
						return (int) (Float.valueOf(value.substring(0, value.length() - 1)) * mSizeInfo.mPageWidth / 100);
					}else if(TextUtils.isDigitsOnly(value)){
						return (int) (Float.valueOf(value) * 1f);
					}
				} catch (Exception e) {}
			}
			return null;
		}
		
		void init(String tag,int start,Attributes attributes){
			mTag = tag.toLowerCase();
			mClass = attributes.getValue("class");
			mId = attributes.getValue("id");
			mStart = start;
			mAttributes = attributes;
			mClassInfos.clear();
			mStyleText = null;
			mParentStyleText = null;
			mWidthValue = null;
			mHeightValue = null;
			mBGWidthValue = null;
			mBGHeightValue = null;
			mSpanSize = 0;
		}
		
		public void removeStyle(Class<?> clazz){
			for (int i = 0; i < mSpanSize; i++) {
				CharacterStyle object = mSpanArr[i];
				if(clazz.isInstance(object)){
					mSpanArr[i] = EMPTY_STYLE;
				}
			}
		}
		
		public <T>T findStyle(Class<T> clazz){
			for (int i = 0; i < mSpanSize; i++) {
				CharacterStyle object = mSpanArr[i];
				if(clazz.isInstance(object)){
					return (T) object;
				}
			}
			return null;
		}
		
		public void addStyle(CharacterStyle object){
			if(mSpanArr == null){
				mSpanArr = new CharacterStyle[10];
			}
			if(mSpanArr.length - mSpanSize < 1){
				CharacterStyle[] tempArr = new CharacterStyle[mSpanArr.length + 5];
				System.arraycopy(mSpanArr, 0, tempArr, 0, mSpanArr.length);
				mSpanArr = tempArr;
			}
			mSpanArr[mSpanSize] = object;
			mSpanSize++;
		}
		
		/**
		 * @return the mClass
		 */
		public String getClazz() {
			return mClass;
		}

		public String getId(){
			return mId;
		}
		/**
		 * @return the mWidthValue
		 */
		public String getWidthValue() {
			return mWidthValue;
		}

		/**
		 * @return the mHeightValue
		 */
		public String getHeightValue() {
			return mHeightValue;
		}

		/**
		 * @return the mTag
		 */
		public String getTag() {
			return mTag;
		}
		
		/**
		 * @return the mTag
		 */
		public Attributes getAttributes() {
			return mAttributes;
		}
		/**
		 * @return the mPaddingLeft
		 */
		public int getMarginLeft() {
			return mStyleText.getMarginLeft();
		}
		/**
		 * @return the mMarginRight
		 */
		public int getMarginRight() {
			return mStyleText.getMarginRight();
		}
		/**
		 * @return the mMarginTop
		 */
		public int getMarginTop() {
			return mStyleText.getMarginTop();
		}
		/**
		 * @return the mMarginBottom
		 */
		public int getMarginBottom() {
			return mStyleText.getMarginBottom();
		}
		/**
		 * @return the mPaddingLeft
		 */
		public int getPaddingLeft() {
			return mStyleText.getPaddingLeft();
		}
		/**
		 * @return the mPaddingRight
		 */
		public int getPaddingRight() {
			return mStyleText.getPaddingRight();
		}
		/**
		 * @return the mPaddingTop
		 */
		public int getPaddingTop() {
			return mStyleText.getPaddingTop();
		}
		/**
		 * @return the mPaddingBottom
		 */
		public int getPaddingBottom() {
			return mStyleText.getPaddingBottom();
		}
		/**
		 * @return the mBorder
		 */
		public Border getBorder() {
			return mStyleText.getBorder();
		}
		/**
		 * @param mBorder the mBorder to set
		 */
		public void setBorder(Border border) {
			mStyleText.setBorder(border);
		}
	}
	
	public static interface TagHandler {
		/**
		 * This method will be called whenn the HTML parser encounters a tag
		 * that it does not know how to interpret.
		 * @return 已处理放回true
		 */
		public boolean handleTag(TagInfo tagInfo,Editable editable,boolean isStart);
		
		public boolean isFilter(TagInfo tagInfo,boolean isStar);

		public boolean handleCharacters(TagInfo tagInfo,Editable editable,StringBuilder charContainer);
	}
	
	protected interface HtmlContentHandler extends ContentHandler{
		public void convert(InputSource source) throws RuntimeException;
		public StyleText getStyleText();
	}
	
	private static class EmptyStyle extends CharacterStyle{
		@Override
		public void updateDrawState(TextPaint tp) {}
	}
}