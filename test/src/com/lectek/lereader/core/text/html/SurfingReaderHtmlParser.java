package com.lectek.lereader.core.text.html;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;

import com.lectek.lereader.core.text.Constant;
import com.lectek.lereader.core.text.LinkedList;
import com.lectek.lereader.core.text.PageManager.TaskListener;
import com.lectek.lereader.core.text.StyleText;
import com.lectek.lereader.core.text.StyleText.Intervalo;
import com.lectek.lereader.core.text.Util;
import com.lectek.lereader.core.text.html.HtmlParser.HtmlContentHandler;
import com.lectek.lereader.core.text.html.HtmlParser.SizeInfo;
import com.lectek.lereader.core.text.html.HtmlParser.TagHandler;
import com.lectek.lereader.core.text.html.HtmlParser.TagInfo;
import com.lectek.lereader.core.text.html.css.PropertyValue;
import com.lectek.lereader.core.text.style.AlignSpan;
import com.lectek.lereader.core.text.style.AsyncDrawableSpan;
import com.lectek.lereader.core.text.style.BaseAsyncDrawableSpan;
import com.lectek.lereader.core.text.style.BlockquoteSpan;
import com.lectek.lereader.core.text.style.Border;
import com.lectek.lereader.core.text.style.ClickAsyncDrawableSpan;
import com.lectek.lereader.core.text.style.FloatSpan;
import com.lectek.lereader.core.text.style.GroupsAsyncDrawableSpan;
import com.lectek.lereader.core.text.style.ImgPanelBGDrawableSpan;
import com.lectek.lereader.core.text.style.SpannableStringBuilder;
import com.lectek.lereader.core.text.style.StyleSpan;
import com.lectek.lereader.core.text.style.UrlSpna;
import com.lectek.lereader.core.util.LogUtil;

public final class SurfingReaderHtmlParser extends HtmlParser{
	SurfingReaderHtmlParser(ICssProvider cssProvider,
			DataProvider imageGetter, TaskListener task, TagHandler tagHandler,SizeInfo sizeInfo) {
		super(cssProvider, imageGetter, task, tagHandler,sizeInfo);
	}

	@Override
	protected HtmlContentHandler createHandler(ICssProvider cssProvider,
			DataProvider imageGetter, XMLReader parser, TaskListener task,
			TagHandler tagHandler,SizeInfo sizeInfo) {
		return new SurfingHtmlToSpannedConverter(cssProvider, imageGetter, parser, task, tagHandler,sizeInfo);
	}
}

final class SurfingHtmlToSpannedConverter implements HtmlContentHandler {
	private static final String TAG = SurfingReaderHtmlParser.TAG;
	private static final float[] HEADER_SIZES = {1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1f};
	private XMLReader mReader;
	private SpannableStringBuilder mSpannableStringBuilder;
	private DataProvider mImageGetter;
	private ICssProvider mCssProvider;
	private LinkedList<TagInfo> mReadTagInfos;
	private boolean isReadingBody;
	private ArrayList<String> mCssPaths;
	private StyleText mRootStyleText;
	private LinkedList<TagInfo> mCacheTagInfos;
	private StringBuilder mCharContainer;
	private TaskListener mTask;
	private TagHandler mTagHandler;
	private boolean isScript;
	private SizeInfo mSizeInfo;
	public SurfingHtmlToSpannedConverter(ICssProvider cssProvider,DataProvider imageGetter
			, XMLReader parser,TaskListener task,TagHandler tagHandler,SizeInfo sizeInfo) {
		mRootStyleText = StyleText.createRoot();
		mSpannableStringBuilder = mRootStyleText.getDataSource();
		mImageGetter = imageGetter;
		mReader = parser;
		mReadTagInfos = new LinkedList<TagInfo>();
		mCacheTagInfos = new LinkedList<TagInfo>();
		mCssProvider = cssProvider;
		mCssPaths = new ArrayList<String>();
		mCharContainer = new StringBuilder(100);
		mTask = task;
		mTagHandler = tagHandler;
		mSizeInfo = sizeInfo;
	}
	
	@Override
	public StyleText getStyleText(){
		return mRootStyleText;
	}
	
	@Override
	public void convert(InputSource source) throws RuntimeException{
		long startTime = System.currentTimeMillis();
		mSpannableStringBuilder.clear();
		mReader.setContentHandler(this);
		Exception e = null;
		try {
			mReader.parse(source);
		} catch (IOException e1) {
			e = e1;
			e1.printStackTrace();
		} catch (SAXException e2) {
			e = e2;
			e2.printStackTrace();
		} catch (Exception e3) {
			e = e3;
			e3.printStackTrace();
		}
		if(e != null){
			throw new RuntimeException(e);
		}
		LogUtil.i(TAG, "convert finish time="+(System.currentTimeMillis() - startTime) +" size="+mSpannableStringBuilder.length() +" otherTime="+time);
	}
	
	private boolean handleStartTag(TagInfo tagInfo) {
		String tag = tagInfo.getTag();
		int start = mSpannableStringBuilder.length();
		handleStartTagCss(tagInfo,mSpannableStringBuilder);
		if (mTagHandler != null
				&& mTagHandler.handleTag(tagInfo,mSpannableStringBuilder,true)){
		}else {
			if (tag.length() == 2
					&& tag.charAt(0) == 'h'
					&& tag.charAt(1) >= '1' && tag.charAt(1) <= '6') {
				startHeader(tagInfo,mSpannableStringBuilder,tag.charAt(1) - '1');
			}else{
				HtmlHandler htmlHandler = mHtmlHandlerMap.get(tag);
				if(htmlHandler != null){
					htmlHandler.handle(tagInfo, this);
				}
			}
		}
		if(start != mSpannableStringBuilder.length()){
			tagInfo.mStyleText.putIntervalo(new Intervalo(start,mSpannableStringBuilder.length() - 1));
		}
		return isReadingBody;
	}

	private void handleEndTag(TagInfo tagInfo) {
		String tag = tagInfo.getTag();
		boolean isHandleEnd = false;
		int start = mSpannableStringBuilder.length();
		if (mTagHandler != null
				&& mTagHandler.handleTag(tagInfo,mSpannableStringBuilder,false)){
			if(!isHandleEnd){
				end(tagInfo,mSpannableStringBuilder);
			}
		}else{
			if (tag.equals("br")) {
				handleBr(mSpannableStringBuilder);
				isHandleEnd = true;
			}else if (tag.equals("img")) {
				isHandleEnd = true;
			}
			if(!isHandleEnd){
				end(tagInfo,mSpannableStringBuilder);
			}
		}
		if(start != mSpannableStringBuilder.length()){
			tagInfo.mStyleText.putIntervalo(new Intervalo(start,mSpannableStringBuilder.length() - 1));
		}
	}

	private void handlerInlineCss(TagInfo tagInfo){
		PropertyValue propertyValue = null;
		ArrayList<String[]> css = Util.handlerInlineCss(tagInfo.mAttributes);
		if(css != null){
			for (String[] values : css) {
				if(!TextUtils.isEmpty(values[0]) && !TextUtils.isEmpty(values[1])){
					propertyValue = new PropertyValue(values[0], values[1]);
					tagInfo.mClassInfos.add(propertyValue);
				}
			}
		}
	}
	
	private void handleStartTagCss(TagInfo tagInfo,SpannableStringBuilder text) {
		if(mCssProvider == null){
			return;
		}
		List<PropertyValue> classInfos = mCssProvider.getClassInfo(mReadTagInfos);
		if(classInfos != null){
			tagInfo.mClassInfos.addAll(classInfos);
		}
		handlerInlineCss(tagInfo);
		for(int i = 0;i < tagInfo.mClassInfos.size();i++){
			PropertyValue propertyValue = tagInfo.mClassInfos.get(i);
			String tag = propertyValue.getProperty();
			if(TextUtils.isEmpty(tag)){
				continue;
			}
			CssHandler cssHandler = mCssHandlerMap.get(tag);
			if(cssHandler != null){
				cssHandler.handle(tagInfo, propertyValue);
			}
		}
		
		ImgPanelBGDrawableSpan mBGDrawableSpan = tagInfo.mStyleText.getBGDrawable();
		if(mBGDrawableSpan != null){
			Integer size = null;
			float width = 0;
			float height = 0;
			size = tagInfo.handleSize(tagInfo.mBGWidthValue);
			if(size != null){
				width = size;
			}
			size = tagInfo.handleSize(tagInfo.mBGHeightValue);
			if(size != null){
				height = size;
			}
			BaseAsyncDrawableSpan drawableSpan = new BaseAsyncDrawableSpan(mBGDrawableSpan.getSrc(), width, height,mImageGetter);
			mBGDrawableSpan.setDrawableSpan(drawableSpan);
		}
		
		Border border = tagInfo.getBorder();
		if(border != null){
			tagInfo.mStyleText.setPaddingLeft(border.getLeftWidth());
			tagInfo.mStyleText.setPaddingRight(border.getRightWidth());
			tagInfo.mStyleText.setPaddingTop(border.getTopWidth());
			tagInfo.mStyleText.setPaddingBottom(border.getBottomWidth());
		}
	}
	
	private static RelativeSizeSpan handleFontSize(String size){
		if(size != null && size.length() > 2){
			String unit = (String) size.subSequence(size.length() - 2, size.length());
			if(unit.equalsIgnoreCase("em")){
				size = size.substring(0, size.length() - 2);
				try {
					return new RelativeSizeSpan(Float.valueOf(size));		
				} catch (Exception e) {}
			}
		}
		return null;
	}
	
	private static TagInfo getLastTagInfo(LinkedList<TagInfo> tagInfos){
		if(tagInfos != null && !tagInfos.isEmpty()){
			return tagInfos.peekLast();
		}
		return null;
	}

	private static void handleBr(SpannableStringBuilder text) {
		text.append("\n");
	}
	
	private static void start(TagInfo tagInfo,SpannableStringBuilder text, CharacterStyle repl) {
		tagInfo.addStyle(repl);
	}

	private static void end(TagInfo tagInfo,SpannableStringBuilder text) {
		int end = text.length();
		int start = tagInfo.mStart;
		handleSpan(tagInfo, text, start, end);
	}

	private static void handleSpan(TagInfo tagInfo,SpannableStringBuilder text,int start,int end){
		tagInfo.mStyleText.finishFillData(start,end - start);
	}
	
	private static void startImg(TagInfo tagInfo, SurfingHtmlToSpannedConverter converter) {
		SpannableStringBuilder text = converter.mSpannableStringBuilder;
		DataProvider img = converter.mImageGetter;
		String src = tagInfo.mAttributes.getValue( "src");
		String title = tagInfo.mAttributes.getValue( "title");
		String imgs = tagInfo.mAttributes.getValue( "imgs");
		String dataScale = tagInfo.mAttributes.getValue("data-scale");
		String cli = tagInfo.mAttributes.getValue("cli");
		String qp = tagInfo.mAttributes.getValue( "qp");
		boolean isqp = "true".equals(qp) || tagInfo.isCover();
		String widthStr = tagInfo.mAttributes.getValue( "width");
		String heightStr = tagInfo.mAttributes.getValue( "height");
		if(TextUtils.isEmpty(widthStr)){
			widthStr = tagInfo.mWidthValue;
		}
		if(TextUtils.isEmpty(heightStr)){
			heightStr = tagInfo.mHeightValue;
		}
		Integer size = null;
		float width = 0;
		float height = 0;
		size = tagInfo.handleSize(widthStr);
		if(size != null){
			width = size;
		}
		size = tagInfo.handleSize(heightStr);
		if(size != null){
			height = size;
		}
		text.append(Constant.REPLACEMENT_SPAN_CHAR);
		AsyncDrawableSpan asyncDrawableSpan = null;
		if(!TextUtils.isEmpty(imgs)){
			asyncDrawableSpan = new GroupsAsyncDrawableSpan(src,title,imgs,isqp,width,height, img);
		}else{
			if("true".equals(cli) || "true".equals(dataScale)){
				asyncDrawableSpan = new ClickAsyncDrawableSpan(src,title,isqp,width,height, img);
			}else{
				asyncDrawableSpan = new AsyncDrawableSpan(src,title,isqp,width,height, img);
			}
		}
		tagInfo.addStyle(asyncDrawableSpan);
		asyncDrawableSpan.setPaddingLeft(tagInfo.getPaddingLeft() + tagInfo.getMarginLeft());
		asyncDrawableSpan.setPaddingTop(tagInfo.getPaddingTop() + tagInfo.getMarginTop());
		asyncDrawableSpan.setPaddingRight(tagInfo.getPaddingRight() + tagInfo.getMarginRight());
		asyncDrawableSpan.setPaddingBottom(tagInfo.getPaddingBottom() + tagInfo.getMarginBottom());
		handleSpan(tagInfo, text, tagInfo.mStart , text.length());
	}
	
	private static CharacterStyle handleColor(String color) {
		CharacterStyle span = null;
		if (!TextUtils.isEmpty(color)) {
			if (color.startsWith("@")) {
				Resources res = Resources.getSystem();
				String name = color.substring(1);
				int colorRes = res.getIdentifier(name, "color", "android");
				if (colorRes != 0) {
					ColorStateList colors = res.getColorStateList(colorRes);
					span = new TextAppearanceSpan(null, 0, 0, colors,null);
				}
			} else {
				Integer c = Util.parseHtmlColor(color);
				if (c != null) {
					span = new ForegroundColorSpan(c);
				}
			}
		}
		return span;
	}
	
	private static void startFont(TagInfo tagInfo,SpannableStringBuilder text) {
		String color = tagInfo.mAttributes.getValue( "color");
		String face = tagInfo.mAttributes.getValue( "face");
		CharacterStyle span = handleColor(color);
		if(span != null){
			tagInfo.addStyle(span);
		}
		if (face != null) {
			span = new TypefaceSpan(face);
			tagInfo.addStyle(span);
		}
	}

	private static void startA(TagInfo tagInfo,SpannableStringBuilder text) {
		String href = tagInfo.mAttributes.getValue( "href");
		if (href != null) {
			CharacterStyle span = new UrlSpna(href);
			tagInfo.addStyle(span);
		}
	}

	private static void startHeader(TagInfo tagInfo,SpannableStringBuilder text,int level) {
		CharacterStyle span = new RelativeSizeSpan(HEADER_SIZES[level]);
		tagInfo.addStyle(span);
		span = new StyleSpan(Typeface.BOLD);
		tagInfo.addStyle(span);
	}
	
	@Override
	public void setDocumentLocator(Locator locator) {
	}
	
	@Override
	public void startDocument() throws SAXException {
	}
	
	@Override
	public void endDocument() throws SAXException {
	}
	
	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
	}
	
	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
	}
	
	long time = 0;
//	long lastTiem = System.currentTimeMillis();
//	time += System.currentTimeMillis() - lastTiem;
	private TagInfo createTagInfo(String tag,int start,Attributes attributes){
		if(mCacheTagInfos.size() > 0){
			TagInfo tagInfo = mCacheTagInfos.pollLast();
			tagInfo.init(tag, start,attributes);
			return tagInfo;
		}
		return new TagInfo(tag,mSpannableStringBuilder.length(),attributes,mSizeInfo);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(mTask.isStop()){
			return;
		}
		TagInfo lastTagInfo = getLastTagInfo(mReadTagInfos);
		TagInfo tagInfo = createTagInfo(localName,mSpannableStringBuilder.length(),attributes);
		mReadTagInfos.add(tagInfo);
		if(tagInfo.mTag.equals("script")){
			isScript = true;
		}else if(!isScript){
			if(tagInfo.mTag.equals("body")){
				isReadingBody = true;
				String type = tagInfo.getAttributes().getValue("epub:type");
				if(!TextUtils.isEmpty(type) && "cover".equals(type)){
					mRootStyleText.setCover(true);
				}
			}else if(tagInfo.mTag.equals("link")){
				String type = attributes.getValue( "type");
				String path = attributes.getValue( "href");
				if(!TextUtils.isEmpty(type) && type.equalsIgnoreCase("text/css") && !TextUtils.isEmpty(path)){
					mCssPaths.add(path);
				}
			}
			if(isReadingBody){
				if (mTagHandler != null && mTagHandler.isFilter(tagInfo,true)){
					return;
				}
				if(lastTagInfo != null && lastTagInfo.mStyleText != null){
					tagInfo.mParentStyleText = lastTagInfo.mStyleText;
					tagInfo.mStyleText = StyleText.create(mRootStyleText);
				}else{
					tagInfo.mStyleText = mRootStyleText;
				}
				tagInfo.mStyleText.setStart(tagInfo.mStart);
				tagInfo.mStyleText.setTag(tagInfo.mTag);
				if(tagInfo.mParentStyleText != null){
					tagInfo.mParentStyleText.addChild(tagInfo.mStyleText);
				}
				handleStartTag(tagInfo);
				tagInfo.mStyleText.setSpans(tagInfo.mSpanArr,tagInfo.mSpanSize);
			}
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(mTask.isStop()){
			return;
		}
		TagInfo tagInfo = getLastTagInfo(mReadTagInfos);
		if(tagInfo != null){
			if(!isScript && isReadingBody){
				if (mTagHandler == null || !mTagHandler.isFilter(tagInfo,false)){
					if(tagInfo.mStyleText != null){
						handleEndTag(tagInfo);
					}
				}
			}
			if(tagInfo.mTag.equals("script")){
				isScript = false;
			}
			mCacheTagInfos.add(mReadTagInfos.pollLast());
			if(tagInfo.mTag.equals("head")){
				if(mCssPaths.size() > 0){
					mCssProvider.parse(mCssPaths);
				}
			}else if(tagInfo.mTag.equals("body")){
				isReadingBody = false;
			}
		}
	}
	
	@Override
	public void characters(char ch[], int start, int length)
			throws SAXException {
		if(mTask.isStop()){
			return;
		}
		if(!isReadingBody || isScript){
			return;
		}
		TagInfo lastTagInfo = getLastTagInfo(mReadTagInfos);
		mCharContainer.setLength(0);
//		boolean isBody = "body".equals(lastTagInfo.getTag());
		int noteStar = -1;
		char noteChar = '$';
		int index = 0;
		int strLength = start + length;
		for (int i = 0; i < length; i++) {
			index = i + start;
			char c = ch[index];
			if(i != noteStar){
				if(c == '/'){
					if(index + 3 < strLength && ch[++index] == '*'){
						noteChar = c;
						noteStar = i;
						continue;
					}else if(index + 1 < strLength && ch[++index] == '/'){
						noteChar = '@';
						continue;
					}
				}else if(c == '<'){
					if(index + 6 < strLength 
							&& ch[++index] == '!'
							&& ch[++index] == '-'
							&& ch[++index] == '-'){
						noteChar = c;
						noteStar = i;
						continue;
					}
				}
			}
			if(noteChar != '$'){
				if(noteChar == '/'){
					if(c == '*' && ++index < strLength && ch[index] == '/'){
						noteChar = '$';
						i++;
					}else if(i == length - 1){
						i = noteStar - 1;
						noteChar = '$';
					}
				}else if(noteChar == '<'){
					if(c == '-' && index + 2 < strLength 
							&& ch[++index] == '-'
							&& ch[++index] == '>'){
						noteChar = '$';
						i += 2;
					}else if(i == length - 1){
						i = noteStar - 1;
						noteChar = '$';
					}
				}else if(noteChar == '@'){
					if(c == '\n' || c == '\r'){
						noteChar = '$';
					}
				}
				continue;
			}
			if (c == ' ' ||c == '\n'  || c == '\t') {
//				if(!isBody && c == ' '){
//					char pred;
//					int len = mCharContainer.length();
//					if (len == 0) {
//						len = mSpannableStringBuilder.length();
//						if (len == 0) {
//							pred = '\n';
//						} else {
//							pred = mSpannableStringBuilder.charAt(len - 1);
//						}
//					} else {
//						pred = mCharContainer.charAt(len - 1);
//					}
//					if (pred != ' ' && pred != '\n' && pred != '\t') {
//						mCharContainer.append(' ');
//					}
//				}
			} else if(c != Constant.REPLACEMENT_SPAN_CHAR){
				mCharContainer.append(c);
			}
		}
		//添加样式
		if(mCharContainer.length() == 0){
			return;
		}
		start = mSpannableStringBuilder.length();
		if(mTagHandler == null || !mTagHandler.handleCharacters(lastTagInfo, mSpannableStringBuilder,mCharContainer)){
			mSpannableStringBuilder.append(mCharContainer);
		}
		if(start != mSpannableStringBuilder.length() && lastTagInfo != null){
			lastTagInfo.mStyleText.putIntervalo(new Intervalo(start,mSpannableStringBuilder.length() - 1));
		}
	}
	
	@Override
	public void ignorableWhitespace(char ch[], int start, int length)
			throws SAXException {
	}
	
	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
	}
	
	@Override
	public void skippedEntity(String name) throws SAXException {
	}
	
	private static HashMap<String,CssHandler> mCssHandlerMap = new HashMap<String, CssHandler>();
	
	static{
		mCssHandlerMap.put("color", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				CharacterStyle span = handleColor(propertyValue.getValue());
				if(span != null){
					tagInfo.addStyle(span);
				}
			}
		});
		mCssHandlerMap.put("font-weight", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				CharacterStyle span = null;
				if(propertyValue.getValue().equalsIgnoreCase("bold")){
					span = new StyleSpan(Typeface.BOLD);
				}
				if(span != null){
					tagInfo.addStyle(span);
				}
			}
		});
		mCssHandlerMap.put("font-style", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				CharacterStyle span = null;
				if(propertyValue.getValue().equalsIgnoreCase("italic")){
					span = new StyleSpan(Typeface.ITALIC);
				}
				if(span != null){
					tagInfo.addStyle(span);
				}
			}
		});
		mCssHandlerMap.put("float", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				CharacterStyle span = null;
				if(propertyValue.getValue().equalsIgnoreCase("left")){
					span = new FloatSpan(FloatSpan.LEFT_FLOAT);
				}else if(propertyValue.getValue().equalsIgnoreCase("right")){
					span = new FloatSpan(FloatSpan.RIGHT_FLOAT);
				}
				tagInfo.mStyleText.setFloatSpan((FloatSpan) span);
				if(span != null){
					tagInfo.addStyle(span);
				}
			}
		});
		mCssHandlerMap.put("background", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				boolean isParagraph = tagInfo.mTag.equals("body") || tagInfo.mTag.equals("div") || tagInfo.mTag.equals("p") || tagInfo.mTag.indexOf("h") != -1;
				String[] values = propertyValue.getValue().split(" ");
				String src = null;
				Integer marginHorizontal = null;
				Integer marginVertical = null;
				if(values != null){
					for (int i = 0;i < values.length;i++) {
						String string = values[i];
						if(!TextUtils.isEmpty(string)){
							Integer c = Util.parseHtmlColor(string);
							if(c != null){
								BackgroundColorSpan span = new BackgroundColorSpan(c);
								if(!isParagraph){
									tagInfo.addStyle(span);
								}else{
									tagInfo.mStyleText.setBGColor(span);
								}
							}else if(string.indexOf("url") != -1){
								if(isParagraph){
									int start = string.indexOf("(") + 1;
									int end = string.indexOf(")");
									if(0 <= start && start <= end && end <= string.length()){
										src = string.substring(start, end);
									}
								}
							}else if(string.indexOf("repeat") == -1){
								if(isParagraph){
									if(marginHorizontal == null){
										marginHorizontal = ImgPanelBGDrawableSpan.getGravity(string);
										if(marginHorizontal == null){
											marginHorizontal = tagInfo.handleSize(string);
										}
									}else{
										marginVertical = ImgPanelBGDrawableSpan.getGravity(string);
										if(marginVertical == null){
											marginVertical = tagInfo.handleSize(string);
										}
									}
								}
							}
						}
					}
					if(!TextUtils.isEmpty(src)){
						if(marginHorizontal == null){
							marginHorizontal = ImgPanelBGDrawableSpan.GRAVITY_HORIZONTAL_LEFT;
							marginVertical = ImgPanelBGDrawableSpan.GRAVITY_VERTICAL_TOP;
						}else if(marginVertical == null){
							marginVertical = ImgPanelBGDrawableSpan.GRAVITY_CENTER;
						}
						ImgPanelBGDrawableSpan mBGDrawable = new ImgPanelBGDrawableSpan(marginHorizontal, marginVertical, src);
						tagInfo.mStyleText.setBGDrawable(mBGDrawable);
					}
				}
			}
		});
		mCssHandlerMap.put("background-size", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				if(!tagInfo.mTag.equals("body") && !tagInfo.mTag.equals("div") && !tagInfo.mTag.equals("p") && tagInfo.mTag.indexOf("h") == -1){
					return;
				}
				String[] values = propertyValue.getValue().split(" ");
				String width = null;
				String height = null;
				for (int i = 0;i < values.length;i++) {
					String string = values[i];
					if(!TextUtils.isEmpty(string) && string.charAt(string.length() - 1) == '%'){
						if(width == null){
							width = string;
						}else{
							height = string;
						}
					}
				}
				if(width != null){
					tagInfo.mBGWidthValue = width;
					tagInfo.mBGHeightValue = height;
				}
			}
		});
		mCssHandlerMap.put("background-color", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Integer c = Util.parseHtmlColor(propertyValue.getValue());
				if(c != null){
					BackgroundColorSpan span = new BackgroundColorSpan(c);
					if(!tagInfo.mTag.equals("div") && !tagInfo.mTag.equals("p") && tagInfo.mTag.indexOf("h") == -1
							&& !tagInfo.mTag.equals("body")){
						tagInfo.addStyle(span);
					}else{
						tagInfo.mStyleText.setBGColor(span);
					}
				}
			}
		});
		mCssHandlerMap.put("margin", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				String[] values = propertyValue.getValue().split(" ");
				if(values != null && values.length > 0){
					int marginLeft = 0;
					int marginRight = 0;
					int marginTop = 0;
					int marginBottom = 0;
					Integer size = null;
					if(values.length == 1){
						size = tagInfo.handleSize(values[0]);
						if(size != null){
							marginLeft = size;
							marginRight = marginTop = marginBottom = marginLeft;
						}
					}else if(values.length == 2){
						size = tagInfo.handleSize(values[1]);
						if(size != null){
							marginLeft = size;
							marginRight = marginLeft;
						}
						size = tagInfo.handleSize(values[0]);
						if(size != null){
							marginTop = size;
							marginBottom = marginTop;
						}
					}else if(values.length == 3){
						size = tagInfo.handleSize(values[1]);
						if(size != null){
							marginLeft = size;
							marginRight = marginLeft;
						}
						size = tagInfo.handleSize(values[0]);
						if(size != null){
							marginTop = size;
						}
						size = tagInfo.handleSize(values[2]);
						if(size != null){
							marginBottom = size;
						}
					}else if(values.length == 4){
						size = tagInfo.handleSize(values[3]);
						if(size != null){
							marginLeft = size;
						}
						size = tagInfo.handleSize(values[0]);
						if(size != null){
							marginTop = size;
						}
						size = tagInfo.handleSize(values[1]);
						if(size != null){
							marginRight = size;
						}
						size = tagInfo.handleSize(values[2]);
						if(size != null){
							marginBottom = size;
						}
					}
					tagInfo.mStyleText.setMarginLeft(marginLeft);
					tagInfo.mStyleText.setMarginRight(marginRight);
					tagInfo.mStyleText.setMarginTop(marginTop);
					tagInfo.mStyleText.setMarginBottom(marginBottom);
				}
			}
		});
		
		mCssHandlerMap.put("margin-left", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Integer size = tagInfo.handleSize(propertyValue.getValue());
				if(size != null){
					tagInfo.mStyleText.setMarginLeft(size);
				}
			}
		});
		mCssHandlerMap.put("margin-right", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Integer size = tagInfo.handleSize(propertyValue.getValue());
				if(size != null){
					tagInfo.mStyleText.setMarginRight(size);
				}
			}
		});
		mCssHandlerMap.put("margin-top", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Integer size = tagInfo.handleSize(propertyValue.getValue());
				if(size != null){
					tagInfo.mStyleText.setMarginTop(size);
				}
			}
		});
		mCssHandlerMap.put("margin-bottom", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Integer size = tagInfo.handleSize(propertyValue.getValue());
				if(size != null){
					tagInfo.mStyleText.setMarginBottom(size);
				}
			}
		});
		mCssHandlerMap.put("padding", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				String[] values = propertyValue.getValue().split(" ");
				if(values != null && values.length > 0){
					int paddingLeft = 0;
					int paddingRight = 0;
					int paddingTop = 0;
					int paddingBottom = 0;
					Integer size = null;
					if(values.length == 1){
						size = tagInfo.handleSize(values[0]);
						if(size != null){
							paddingLeft = size;
							paddingRight = paddingTop = paddingBottom = paddingLeft;
						}
					}else if(values.length == 2){
						size = tagInfo.handleSize(values[1]);
						if(size != null){
							paddingLeft = size;
							paddingRight = paddingLeft;
						}
						size = tagInfo.handleSize(values[0]);
						if(size != null){
							paddingTop = size;
							paddingBottom = paddingTop;
						}
					}else if(values.length == 3){
						size = tagInfo.handleSize(values[1]);
						if(size != null){
							paddingLeft = size;
							paddingRight = paddingLeft;
						}
						size = tagInfo.handleSize(values[0]);
						if(size != null){
							paddingTop = size;
						}
						size = tagInfo.handleSize(values[2]);
						if(size != null){
							paddingBottom = size;
						}
					}else if(values.length == 4){
						size = tagInfo.handleSize(values[3]);
						if(size != null){
							paddingLeft = size;
						}
						size = tagInfo.handleSize(values[0]);
						if(size != null){
							paddingTop = size;
						}
						size = tagInfo.handleSize(values[1]);
						if(size != null){
							paddingRight = size;
						}
						size = tagInfo.handleSize(values[2]);
						if(size != null){
							paddingBottom = size;
						}
					}
					tagInfo.mStyleText.setPaddingLeft(paddingLeft);
					tagInfo.mStyleText.setPaddingRight(paddingRight);
					tagInfo.mStyleText.setPaddingTop(paddingTop);
					tagInfo.mStyleText.setPaddingBottom(paddingBottom);
				}
			}
		});
		
		mCssHandlerMap.put("padding-left", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Integer size = tagInfo.handleSize(propertyValue.getValue());
				if(size != null){
					tagInfo.mStyleText.setPaddingLeft(size);
				}
			}
		});
		mCssHandlerMap.put("padding-right", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Integer size = tagInfo.handleSize(propertyValue.getValue());
				if(size != null){
					tagInfo.mStyleText.setPaddingRight(size);
				}
			}
		});
		mCssHandlerMap.put("padding-top", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Integer size = tagInfo.handleSize(propertyValue.getValue());
				if(size != null){
					tagInfo.mStyleText.setPaddingTop(size);
				}
			}
		});
		mCssHandlerMap.put("padding-bottom", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Integer size = tagInfo.handleSize(propertyValue.getValue());
				if(size != null){
					tagInfo.mStyleText.setPaddingBottom(size);
				}
			}
		});
		
		mCssHandlerMap.put("border", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				String[] values = propertyValue.getValue().split(" ");
				if(values != null){
					Integer color = null;
					Integer size = null;
					int type = Border.TYPE_NONE;
					for (int i = 0;i < values.length;i++) {
						String string = values[i];
						if(!TextUtils.isEmpty(string)){
							Integer c = Util.parseHtmlColor(string);
							if(c == null){
								Integer s = tagInfo.handleBorderSize(string);
								if(s == null){
									type = Border.parseType(string);
								}else {
									size = s;
								}
							}else{
								color = c;
							}
						}
					}
					Border border = new Border(type);
					if(size != null){
						border.setWidth(size);
					}
					if(color != null){
						border.setCorol(color);
					}
					tagInfo.mStyleText.setBorder(border);
				}
			}
		});
		
		mCssHandlerMap.put("border-top", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				String[] values = propertyValue.getValue().split(" ");
				if(values != null){
					Integer color = null;
					Integer size = null;
					int type = Border.TYPE_NONE;
					for (int i = 0;i < values.length;i++) {
						String string = values[i];
						if(!TextUtils.isEmpty(string)){
							Integer c = Util.parseHtmlColor(string);
							if(c == null){
								Integer s = tagInfo.handleBorderSize(string);
								if(s == null){
									type = Border.parseType(string);
								}else {
									size = s;
								}
							}else{
								color = c;
							}
						}
					}
					Border border = tagInfo.mStyleText.getBorder();
					if(border == null){
						border = new Border();
						tagInfo.mStyleText.setBorder(border);
					}
					if(size != null){
						border.setTopWidth(size);
					}
					if(color != null){
						border.setTopCorol(color);
					}
					border.setTopType(type);
				}
			}
		});
		
		mCssHandlerMap.put("border-right", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				String[] values = propertyValue.getValue().split(" ");
				if(values != null){
					Integer color = null;
					Integer size = null;
					int type = Border.TYPE_NONE;
					for (int i = 0;i < values.length;i++) {
						String string = values[i];
						if(!TextUtils.isEmpty(string)){
							Integer c = Util.parseHtmlColor(string);
							if(c == null){
								Integer s = tagInfo.handleBorderSize(string);
								if(s == null){
									type = Border.parseType(string);
								}else {
									size = s;
								}
							}else{
								color = c;
							}
						}
					}
					Border border = tagInfo.mStyleText.getBorder();
					if(border == null){
						border = new Border();
						tagInfo.mStyleText.setBorder(border);
					}
					if(size != null){
						border.setRightWidth(size);
					}
					if(color != null){
						border.setRightCorol(color);
					}
					border.setRightType(type);
				}
			}
		});
		mCssHandlerMap.put("border-bottom", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				String[] values = propertyValue.getValue().split(" ");
				if(values != null){
					Integer color = null;
					Integer size = null;
					int type = Border.TYPE_NONE;
					for (int i = 0;i < values.length;i++) {
						String string = values[i];
						if(!TextUtils.isEmpty(string)){
							Integer c = Util.parseHtmlColor(string);
							if(c == null){
								Integer s = tagInfo.handleBorderSize(string);
								if(s == null){
									type = Border.parseType(string);
								}else {
									size = s;
								}
							}else{
								color = c;
							}
						}
					}
					Border border = tagInfo.mStyleText.getBorder();
					if(border == null){
						border = new Border();
						tagInfo.mStyleText.setBorder(border);
					}
					if(size != null){
						border.setBottomWidth(size);
					}
					if(color != null){
						border.setBottomCorol(color);
					}
					border.setBottomType(type);
				}
			}
		});
		
		mCssHandlerMap.put("border-left", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				String[] values = propertyValue.getValue().split(" ");
				if(values != null){
					Integer color = null;
					Integer size = null;
					int type = Border.TYPE_NONE;
					for (int i = 0;i < values.length;i++) {
						String string = values[i];
						if(!TextUtils.isEmpty(string)){
							Integer c = Util.parseHtmlColor(string);
							if(c == null){
								Integer s = tagInfo.handleBorderSize(string);
								if(s == null){
									type = Border.parseType(string);
								}else {
									size = s;
								}
							}else{
								color = c;
							}
						}
					}
					Border border = tagInfo.mStyleText.getBorder();
					if(border == null){
						border = new Border();
						tagInfo.mStyleText.setBorder(border);
					}
					if(size != null){
						border.setLeftWidth(size);
					}
					if(color != null){
						border.setLeftCorol(color);
					}
					border.setLeftType(type);
				}
			}
		});
		
		mCssHandlerMap.put("border-style", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				String[] values = propertyValue.getValue().split(" ");
				if(values != null && values.length > 0){
					Border border = null;
					if(values.length == 1){
						border = new Border(Border.parseType(values[0]));
					}else if(values.length == 2){
						border = new Border(Border.parseType(values[0]),Border.parseType(values[1]));
					}else if(values.length == 3){
						border = new Border(Border.parseType(values[0]),Border.parseType(values[1])
								,Border.parseType(values[2]));
					}else if(values.length == 4){
						border = new Border(Border.parseType(values[0]),Border.parseType(values[1])
								,Border.parseType(values[2]),Border.parseType(values[3]));
					}
					tagInfo.mStyleText.setBorder(border);
				}
			}
		});
		
		mCssHandlerMap.put("border-top-style", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Border border = tagInfo.mStyleText.getBorder();
				if(border == null){
					border = new Border();
					tagInfo.mStyleText.setBorder(border);
				}
				border.setTopType(Border.parseType(propertyValue.getValue()));
			}
		});
		
		mCssHandlerMap.put("border-right-style", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Border border = tagInfo.mStyleText.getBorder();
				if(border == null){
					border = new Border();
					tagInfo.mStyleText.setBorder(border);
				}
				border.setRightType(Border.parseType(propertyValue.getValue()));
			}
		});
		
		mCssHandlerMap.put("border-bottom-style", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Border border = tagInfo.mStyleText.getBorder();
				if(border == null){
					border = new Border();
					tagInfo.mStyleText.setBorder(border);
				}
				border.setBottomType(Border.parseType(propertyValue.getValue()));
			}
		});
		
		mCssHandlerMap.put("border-left-style", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Border border = tagInfo.mStyleText.getBorder();
				if(border == null){
					border = new Border();
					tagInfo.mStyleText.setBorder(border);
				}
				border.setLeftType(Border.parseType(propertyValue.getValue()));
			}
		});
		
		mCssHandlerMap.put("border-width", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Border border = tagInfo.mStyleText.getBorder();
				if(border == null){
					return;
				}
				String[] values = propertyValue.getValue().split(" ");
				if(values != null && values.length > 0){
					int leftWidth = 0;
					int rightWidth = 0;
					int topWidth = 0;
					int bottomWidth = 0;
					Integer size = null;
					if(values.length == 1){
						size = tagInfo.handleBorderSize(values[0]);
						if(size != null){
							leftWidth = size;
							rightWidth = topWidth = bottomWidth = leftWidth;
						}
					}else if(values.length == 2){
						size = tagInfo.handleBorderSize(values[1]);
						if(size != null){
							leftWidth = size;
							rightWidth = leftWidth;
						}
						size = tagInfo.handleBorderSize(values[0]);
						if(size != null){
							topWidth = size;
							bottomWidth = topWidth;
						}
					}else if(values.length == 3){
						size = tagInfo.handleBorderSize(values[1]);
						if(size != null){
							leftWidth = size;
							rightWidth = leftWidth;
						}
						size = tagInfo.handleBorderSize(values[0]);
						if(size != null){
							topWidth = size;
						}
						size = tagInfo.handleBorderSize(values[2]);
						if(size != null){
							bottomWidth = size;
						}
					}else if(values.length == 4){
						size = tagInfo.handleBorderSize(values[3]);
						if(size != null){
							leftWidth = size;
						}
						size = tagInfo.handleBorderSize(values[0]);
						if(size != null){
							topWidth = size;
						}
						size = tagInfo.handleBorderSize(values[1]);
						if(size != null){
							rightWidth = size;
						}
						size = tagInfo.handleBorderSize(values[2]);
						if(size != null){
							bottomWidth = size;
						}
					}
					border.setLeftWidth(leftWidth);
					border.setRightWidth(rightWidth);
					border.setTopWidth(topWidth);
					border.setBottomWidth(bottomWidth);
				}
			}
		});
		mCssHandlerMap.put("border-top-width", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Border border = tagInfo.mStyleText.getBorder();
				if(border == null){
					return;
				}
				Integer size = tagInfo.handleBorderSize(propertyValue.getValue());
				if(size != null){
					border.setTopWidth(size);
				}
			}
		});
		mCssHandlerMap.put("border-right-width", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Border border = tagInfo.mStyleText.getBorder();
				if(border == null){
					return;
				}
				Integer size = tagInfo.handleBorderSize(propertyValue.getValue());
				if(size != null){
					border.setRightWidth(size);
				}
			}
		});
		mCssHandlerMap.put("border-bottom-width", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Border border = tagInfo.mStyleText.getBorder();
				if(border == null){
					return;
				}
				Integer size = tagInfo.handleBorderSize(propertyValue.getValue());
				if(size != null){
					border.setBottomWidth(size);
				}
			}
		});
		mCssHandlerMap.put("border-left-width", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Border border = tagInfo.mStyleText.getBorder();
				if(border == null){
					return;
				}
				Integer size = tagInfo.handleBorderSize(propertyValue.getValue());
				if(size != null){
					border.setLeftWidth(size);
				}
			}
		});
		
		mCssHandlerMap.put("border-color", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Border border = tagInfo.mStyleText.getBorder();
				if(border == null){
					return;
				}
				String[] values = propertyValue.getValue().split(" ");
				if(values != null && values.length > 0){
					if(values.length == 1){
						border.setCorol(Util.getHtmlColor(values[0]));
					}else if(values.length == 2){
						border.setCorol(Util.getHtmlColor(values[0]),Util.getHtmlColor(values[1]));
					}else if(values.length == 3){
						border.setCorol(Util.getHtmlColor(values[0]),Util.getHtmlColor(values[1])
								,Util.getHtmlColor(values[2]));
					}else if(values.length == 4){
						border.setCorol(Util.getHtmlColor(values[0]),Util.getHtmlColor(values[1])
								,Util.getHtmlColor(values[2]),Util.getHtmlColor(values[3]));
					}
				}
			}
		});
		
		mCssHandlerMap.put("border-top-color", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Border border = tagInfo.mStyleText.getBorder();
				if(border == null){
					return;
				}
				border.setTopCorol(Util.getHtmlColor(propertyValue.getValue()));
			}
		});
		
		mCssHandlerMap.put("border-right-color", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Border border = tagInfo.mStyleText.getBorder();
				if(border == null){
					return;
				}
				border.setRightCorol(Util.getHtmlColor(propertyValue.getValue()));
			}
		});
		
		mCssHandlerMap.put("border-bottom-color", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Border border = tagInfo.mStyleText.getBorder();
				if(border == null){
					return;
				}
				border.setBottomCorol(Util.getHtmlColor(propertyValue.getValue()));
			}
		});
		
		mCssHandlerMap.put("border-left-color", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				Border border = tagInfo.mStyleText.getBorder();
				if(border == null){
					return;
				}
				border.setLeftCorol(Util.getHtmlColor(propertyValue.getValue()));
			}
		});
		
		mCssHandlerMap.put("font-size", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				CharacterStyle span = handleFontSize(propertyValue.getValue());
				if(span != null){
					tagInfo.addStyle(span);
				}
			}
		});
		mCssHandlerMap.put("text-align", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				CharacterStyle span = null;
				if(propertyValue.getValue().equalsIgnoreCase("left")){
					span = new AlignSpan(AlignSpan.LEFT_ALIGN);
				}else if(propertyValue.getValue().equalsIgnoreCase("right")){
					span = new AlignSpan(AlignSpan.RIGHT_ALIGN);
				}else if(propertyValue.getValue().equalsIgnoreCase("center")){
					span = new AlignSpan(AlignSpan.CENTER_ALIGN);
				}
				if(span != null){
					tagInfo.addStyle(span);
				}
			}
		});
		mCssHandlerMap.put("width", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				tagInfo.mWidthValue = propertyValue.getValue();
			}
		});
		mCssHandlerMap.put("height", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				tagInfo.mHeightValue = propertyValue.getValue();
			}
		});
		mCssHandlerMap.put("max-width", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				tagInfo.mWidthValue = propertyValue.getValue();
			}
		});
		mCssHandlerMap.put("max-height", new CssHandler() {
			@Override
			public void handle(TagInfo tagInfo, PropertyValue propertyValue) {
				tagInfo.mHeightValue = propertyValue.getValue();
			}
		});
	}
	
	private static HashMap<String,HtmlHandler> mHtmlHandlerMap = new HashMap<String, HtmlHandler>();

	static{
		mHtmlHandlerMap.put("em", new HtmlHandler() {
			@Override
			public void handle(TagInfo tagInfo, SurfingHtmlToSpannedConverter converter) {
				start(tagInfo,converter.mSpannableStringBuilder,new StyleSpan(Typeface.BOLD));
			}
		});
		mHtmlHandlerMap.put("b", new HtmlHandler() {
			@Override
			public void handle(TagInfo tagInfo, SurfingHtmlToSpannedConverter converter) {
				start(tagInfo,converter.mSpannableStringBuilder,new StyleSpan(Typeface.BOLD));
			}
		});
		mHtmlHandlerMap.put("strong", new HtmlHandler() {
			@Override
			public void handle(TagInfo tagInfo, SurfingHtmlToSpannedConverter converter) {
				start(tagInfo,converter.mSpannableStringBuilder, new StyleSpan(Typeface.ITALIC));
			}
		});
		mHtmlHandlerMap.put("cite", new HtmlHandler() {
			@Override
			public void handle(TagInfo tagInfo, SurfingHtmlToSpannedConverter converter) {
				start(tagInfo,converter.mSpannableStringBuilder, new StyleSpan(Typeface.ITALIC));
			}
		});
		mHtmlHandlerMap.put("dfn", new HtmlHandler() {
			@Override
			public void handle(TagInfo tagInfo, SurfingHtmlToSpannedConverter converter) {
				start(tagInfo,converter.mSpannableStringBuilder, new StyleSpan(Typeface.ITALIC));
			}
		});
		mHtmlHandlerMap.put("i", new HtmlHandler() {
			@Override
			public void handle(TagInfo tagInfo, SurfingHtmlToSpannedConverter converter) {
				start(tagInfo,converter.mSpannableStringBuilder, new StyleSpan(Typeface.ITALIC));
			}
		});
		mHtmlHandlerMap.put("big", new HtmlHandler() {
			@Override
			public void handle(TagInfo tagInfo, SurfingHtmlToSpannedConverter converter) {
				start(tagInfo,converter.mSpannableStringBuilder,new RelativeSizeSpan(1.25f));
			}
		});
		mHtmlHandlerMap.put("small", new HtmlHandler() {
			@Override
			public void handle(TagInfo tagInfo, SurfingHtmlToSpannedConverter converter) {
				start(tagInfo,converter.mSpannableStringBuilder,new RelativeSizeSpan(0.8f));
			}
		});
		mHtmlHandlerMap.put("font", new HtmlHandler() {
			@Override
			public void handle(TagInfo tagInfo, SurfingHtmlToSpannedConverter converter) {
				startFont(tagInfo,converter.mSpannableStringBuilder);
			}
		});
		mHtmlHandlerMap.put("tt", new HtmlHandler() {
			@Override
			public void handle(TagInfo tagInfo, SurfingHtmlToSpannedConverter converter) {
				start(tagInfo,converter.mSpannableStringBuilder, new TypefaceSpan("monospace"));
			}
		});
		mHtmlHandlerMap.put("blockquote", new HtmlHandler() {
			@Override
			public void handle(TagInfo tagInfo, SurfingHtmlToSpannedConverter converter) {
				tagInfo.mStyleText.setMarginLeft(tagInfo.mSizeInfo.mEmUnit * 2);
				tagInfo.mStyleText.setMarginRight(tagInfo.mSizeInfo.mEmUnit * 2);
				start(tagInfo,converter.mSpannableStringBuilder,new BlockquoteSpan());
			}
		});
		mHtmlHandlerMap.put("a", new HtmlHandler() {
			@Override
			public void handle(TagInfo tagInfo, SurfingHtmlToSpannedConverter converter) {
				startA(tagInfo,converter.mSpannableStringBuilder);
			}
		});
		mHtmlHandlerMap.put("u", new HtmlHandler() {
			@Override
			public void handle(TagInfo tagInfo, SurfingHtmlToSpannedConverter converter) {
				start(tagInfo,converter.mSpannableStringBuilder, new UnderlineSpan());
			}
		});
		mHtmlHandlerMap.put("sup", new HtmlHandler() {
			@Override
			public void handle(TagInfo tagInfo, SurfingHtmlToSpannedConverter converter) {
				start(tagInfo,converter.mSpannableStringBuilder, new SuperscriptSpan());
			}
		});
		mHtmlHandlerMap.put("sub", new HtmlHandler() {
			@Override
			public void handle(TagInfo tagInfo, SurfingHtmlToSpannedConverter converter) {
				start(tagInfo,converter.mSpannableStringBuilder, new SubscriptSpan());
			}
		});
		mHtmlHandlerMap.put("img", new HtmlHandler() {
			@Override
			public void handle(TagInfo tagInfo, SurfingHtmlToSpannedConverter converter) {
				startImg(tagInfo, converter);
			}
		});
	}
	
	private static interface CssHandler{
		public void handle(TagInfo tagInfo,PropertyValue propertyValue);
	}
	
	private static interface HtmlHandler{
		public void handle(TagInfo tagInfo,SurfingHtmlToSpannedConverter converter);
	}
}
