package com.lectek.lereader.core.text.layout;

import java.util.List;

import android.graphics.Rect;
import android.text.TextPaint;

import com.lectek.lereader.core.text.Constant;
import com.lectek.lereader.core.text.LinkedList;
import com.lectek.lereader.core.text.PageManager.TaskListener;
import com.lectek.lereader.core.text.SettingParam;
import com.lectek.lereader.core.text.StyleText;
import com.lectek.lereader.core.text.Util;
import com.lectek.lereader.core.text.style.BlockquoteSpan;
import com.lectek.lereader.core.text.style.FloatSpan;
import com.lectek.lereader.core.util.ArrayMap;
import com.lectek.lereader.core.util.LogUtil;

/**
 * 负责排版布局
 * @author linyiwei
 */
public class Layout{
	private static final String TAG = Layout.class.getSimpleName();
	private SettingParam mSettingParam;
	private StyleText mStyleText;
	private LayoutCallback mCallback;
	/** 表示预先申请的后面几页，在构造新的page时会优先使用这些page，没有的情况才重新生成新的page*/
	private LinkedList<Page> mNextPages;
	/** 如果当前行没有足够的空间放浮动区域，先临时保存在这个列表里面，在换行之后重新计算*/
	private LinkedList<Line> mNextFloatLine;
	/** 表示当前也*/
	private Page mCurrentPage;
	/** 代表当前正在计算的行，如果为空说明是行的开始位置。*/
	private Line mCurrentLine;
	/** 是否需要等待数据解析*/
	private volatile boolean isWaitingParser;
	/** 是否开始布局*/
	private volatile boolean isStartLayout;
	/** Char大小缓存*/
	private Rect mCharRect;
	/** Rect对象缓存,为了重复利用Rect对象 */
	private Rect[] mRectCaches;
	/** Rect对象缓存大小*/
	private int mRectCacheSize;
	/** 画笔*/
	private TextPaint mTextPaint;
	/** 页内容区域*/
	private Rect mPageRect;
	/** 临时区域对象用于获取Rect值*/
	private Rect mTempEnabledRect;
	/** 用来判断任务是否已经终止*/
	private TaskListener mTask;
	/** 排版产生的页数*/
	private int mLayoutPageSize;
	
	private ArrayMap<Long> mPanleTops;
	
	private int mCurrentPageSize;
	
//	private Line[] mLineCaches;
	
//	private int mLineCacheSize;
	
	public Layout(StyleText styleText,SettingParam settingParam,LayoutCallback callback,TaskListener task){
		mSettingParam = settingParam;
		mPageRect = mSettingParam.getPageRect();
		mCallback = callback;
		mTask = task;
		mTextPaint = new TextPaint();
		mTempEnabledRect = new Rect();
		mCharRect = new Rect();
		mRectCaches = new Rect[10];
		mRectCacheSize = 0;
//		mLineCaches = new Line[2];
		mPanleTops = new ArrayMap<Long>();
//		mLineCacheSize = 0;
		mCurrentPageSize = 0;
		init(styleText);
	}
	
//	public Layout(SettingParam settingParam){
//		mCurrentPageSize = 0;
//		mSettingParam = settingParam;
//		mPageRect = mSettingParam.getPageRect();
//		mTextPaint = new TextPaint();
//		mTempEnabledRect = new Rect();
//		mCharRect = new Rect();
//		mRectCaches = new Rect[10];
//		mPanleTops = new ArrayMap<Long>();
//		mRectCacheSize = 0;
//		mTask = new TaskListener(null,0){
//			@Override
//			public boolean isStop() {
//				return false;
//			}
//		};
//	}

	public void setCallback(LayoutCallback callback){
		mCallback = callback;
	}
	
	private void init(StyleText styleText){
		mStyleText = styleText;
		mCurrentPageSize = 0;
		if(mNextPages == null){
			mNextPages = new LinkedList<Page>();
		}else{
			mNextPages.clear();
		}
		if(mNextFloatLine == null){
			mNextFloatLine = new LinkedList<Line>();
		}else{
			mNextFloatLine.clear();
		}
		if(mPanleTops == null){
			mPanleTops = new ArrayMap<Long>();
		}else{
			mPanleTops.clear();
		}
		if(mCharRect == null){
			mCharRect = new Rect();
		}
		if(mTempEnabledRect == null){
			mTempEnabledRect = new Rect();
		}
		if(mRectCaches == null){
			mRectCaches = new Rect[10];
			mRectCacheSize = 0;
		}
	}
	
//	private void clearUp(){
//		isBindLayout = false;
//		mStyleText = null;
//		mNextPages = null;
//		mNextFloatLine = null;
//		mCharRect = null;
//		mTempEnabledRect = null;
//		mCurrentPage = null;
//		mCurrentLine = null;
//		mRectCaches = null;
//		mPanleTops = null;
//	}
	
//	public synchronized void layoutPage(StyleText styleText,Page page){
//		init(styleText);
//		isBindLayout = true;
//		mCurrentPage = page;
//		page.initLayoutData();
//		layout(page.getStart(), page.getEnd());
//		clearUp();
//	}
	
	public void startLayout(int start,int end){
		if(!isStartLayout){
			isWaitingParser = false;
			layout(start,end);
		}
	}
	
	public void startLayoutAndWaitingParser(){
		if(!isStartLayout){
			isWaitingParser = true;
			layout(0, -1);
		}
	}
	
	public void finishParser(){
		isWaitingParser = false;
	}
	
	private synchronized void layout(int start,int end){
		long startTime = System.currentTimeMillis();
		if(mStyleText == null){
			return;
		}
		mLayoutPageSize = 0;
		isStartLayout = true;
		int i = start;
		if(mCurrentPage == null){
			mCurrentPage = createPage();
		}
		for(;end == -1 || i <= end;){
			if(mTask.isStop()){
				return;
			}
			if(isWaitingParser){
				if(i < mStyleText.getTotalLength() - 500){
					i = handleChar(i);
				}else{
					sleep();
				}
			}else{
				if(i < mStyleText.getTotalLength()){
					i = handleChar(i);
				}else{
					break;
				}
			}
		}
		if(mCurrentLine != null && mCurrentPage != null){
			StyleText styleText = mStyleText.findStyleText(mCurrentLine.getEnd());
			Rect enabledRect = getEnabledRect(mCurrentLine.getWidth(), mCurrentLine.getHeight(), mTempEnabledRect,mCurrentLine,styleText);
			addPatch(mCurrentLine, enabledRect,styleText);
			mCurrentLine = null;
		}
		//最后一个字符了，需要清理未处理的东西
		handlerNextFloatLine();
		onLayoutFinishPage(mCurrentPage);
		for (Page page : mNextPages) {
			onLayoutFinishPage(page);
		}
		isStartLayout = false;
//		if(!isBindLayout){
			LogUtil.i(TAG, "layout finish time="+(System.currentTimeMillis() - startTime) +" size="+mLayoutPageSize);
//			LogUtil.i(TAG, "layout finish floatTiem="+floatTiem+
//					" addCharTiem="+addCharTiem+
//					" newLineTiem="+newLineTiem+
//					" measureTiem="+measureTiem);
//		}
	}
//	long floatTiem = 0;
//	long addCharTiem = 0;
//	long newLineTiem = 0;
//	long measureTiem = 0;
	private final int handleChar(int index){
//		long startTime = System.currentTimeMillis();16
		char c = mStyleText.charAt(index);
		if(c == 0){
			return ++index;
		}
		boolean isOutOfBounds = false;
		StyleText styleText = mStyleText.findStyleText(index);
		Rect charRect = measureText(index,styleText);
		FloatSpan floatSpan = styleText.getFloatSpan();
		boolean isPanleStart = styleText.isPanleStart(index);
		boolean isPanleEnd = styleText.isPanleEnd(index);
		boolean isNewlineChar = c == '\n' || c == '\u2029';
		Rect enabledRect = null;
//		measureTiem += System.currentTimeMillis() - startTime;
//		startTime = System.currentTimeMillis();
		if(floatSpan != null){
			// start 处理浮动区域
			//1.只处理最顶层的浮动区域，被当前浮动样式包含的直接过滤掉
			//2.直接获取浮动样区域作用的字符范围，交个FloatLine对象计算需要的空间
//			LogUtil.i(TAG, "处理浮动区域 开始位置："+i+" 内容："+c);
			//生成浮动区域，向页请求可分配空间并进行定位
			Line floatLine = createLine();
			floatLine.setLayoutType(floatSpan.getType() == FloatSpan.LEFT_FLOAT 
					? Constant.LAYOUT_TYPE_LEFT_FLOAT : Constant.LAYOUT_TYPE_RIGHT_FLOAT);
			floatLine.setContent(styleText.getStart(), styleText.getEnd());
			if(styleText.isPanleStart(index)){
				floatLine.setPanleStart(styleText.getPanleStart(),styleText.getPanleType());
			}
			int newLineWidth = charRect.right;
			int newLineHeight = charRect.bottom;
			for(int j = index + 1;j <= floatLine.getEnd();j++){
				StyleText floatStyleText = mStyleText.findStyleText(j);
				charRect = measureText(index,floatStyleText);
				newLineWidth += charRect.right;
				newLineHeight = Math.max(newLineHeight, charRect.bottom);
			}
			floatLine.setBoundary(newLineWidth, newLineHeight);
			enabledRect = getEnabledRect(floatLine.getWidth(),floatLine.getHeight(),mTempEnabledRect,floatLine,styleText);
			if(enabledRect != null){
				//XXX 按页还原排版会导致重复内容
//				LogUtil.i(TAG, "处理浮动区域 获得到的可用空间:"+enabledRect+" 当前行未处理的浮动："+nextFloatLine.size());
				//当前行没有足够的空间放浮动区域,在换行之后重新计算
				if(mNextFloatLine.size() > 0 || mCurrentLine != null 
						&& enabledRect.width() - mCurrentLine.getWidth() < floatLine.getWidth()){
//					LogUtil.i(TAG, "处理浮动区域  当前行没有足够的空间  " +
//							(currentLine != null ? ("剩余宽度:"+(currentLine.getMaxWidth() - currentLine.getWidth())) : "")+
//							" 浮动宽度："+floatLine.getWidth());
					mNextFloatLine.add(floatLine);
				}else{
					addPatch(floatLine, enabledRect,styleText);
				}
			}else{
//				addFloatLineToNextPages(floatLine,styleText);
				onLayoutFinishPage(mCurrentPage);
				mCurrentPage = createPage();
				int gotoStart = 0;
				if(mCurrentLine != null){
					gotoStart = mCurrentLine.getStart();
					mCurrentLine = null;
				}else{
					gotoStart = floatLine.getStart();
				}
//				addCharTiem += System.currentTimeMillis() - startTime;
//				startTime = System.currentTimeMillis();
				return gotoStart;
			}
			//跳转浮动区域结束的位置继续往下计算
//			LogUtil.i(TAG, "处理浮动区域 结束位置是："+i+" 内容："+charSequence.charAt(i));
//			floatTiem += System.currentTimeMillis() - startTime;
//			startTime = System.currentTimeMillis();
			return floatLine.getEnd() + 1;
			// end 处理区域
		}
		if(mCurrentLine != null){
			int oldWidth = mCurrentLine.getWidth();
			enabledRect = getEnabledRect(oldWidth,mCurrentLine.getHeight(),mTempEnabledRect,mCurrentLine,styleText);
			if(isNewlineChar || isPanleStart){
				addPatch(mCurrentLine,enabledRect,styleText);
				mCurrentLine = null;
				handlerNextFloatLine();
			}else{
				int newLineContentWidth = mCurrentLine.getContentWidth() + charRect.right;
				int newLineWidth = oldWidth + charRect.right;
				int newLineContentHeight = Math.max(mCurrentLine.getContentHeight(), charRect.bottom);
				int newLineHeight = newLineContentHeight + mCurrentLine.getTopPadding() + mCurrentLine.getBottomPadding();
				//如果高已经超过了当前页折换页，并调整到当前行的第一个字符重新开始计算，因为无法知道下一页可以的宽是多少
				if(enabledRect == null || enabledRect.height() < newLineHeight){
					onLayoutFinishPage(mCurrentPage);
					mCurrentPage = createPage();
//					LogUtil.i(TAG, "处理行区域 处理行区域发现高已经超过了当前页换页 重新从第"+i+"个字符开始计算");
					int gotoStart = mCurrentLine.getStart();
					mCurrentLine = null;
//					addCharTiem += System.currentTimeMillis() - startTime;
//					startTime = System.currentTimeMillis();
					return gotoStart;
				}else{
					isOutOfBounds = enabledRect.width() < newLineWidth;
					//需要换行
					if(isOutOfBounds){
//						LogUtil.i(TAG, "处理行区域 发现需要换行 isOutOfBounds="+isOutOfBounds+" isNewlineChar"+isNewlineChar);
						addPatch(mCurrentLine,enabledRect,styleText);
						mCurrentLine = null;
						handlerNextFloatLine();
					}else{
						//添加新的字符
						mCurrentLine.setContent(mCurrentLine.getStart(), index);
						mCurrentLine.setBoundary(newLineContentWidth, newLineContentHeight);
					}
				}
//				addCharTiem += System.currentTimeMillis() - startTime;
//				startTime = System.currentTimeMillis();
			}
		}
		if(mCurrentLine == null){
//			LogUtil.i(TAG, "处理行区域 申请新的行 outOfBoundsTextRect="+outOfBoundsTextRect+" isLineFirst"+isLineFirst);
			mCurrentLine = createLine();
			mCurrentLine.setContent(index, index);
			mCurrentLine.setPanleStart(styleText.getPanleStart(),styleText.getPanleType());
			mCurrentLine.setBoundary(charRect.right, charRect.bottom);
			if(isPanleStart){
				BlockquoteSpan blockquoteSpan = Util.findFirstSpans(styleText.getTotalSpans(), BlockquoteSpan.class);
				if("　".indexOf(c) != -1 || blockquoteSpan != null){
					mCurrentLine.setIndentSize(0);
				}
			}
			int oldWidth = mCurrentLine.getWidth();
			int oldHeight = mCurrentLine.getHeight();
			enabledRect = getEnabledRect(oldWidth,oldHeight,mTempEnabledRect,mCurrentLine,styleText);
			//如果当前页不够放添加到下一页，也许是下下下下页，只到找到可以放的页位置
			if(enabledRect == null){
//				LogUtil.i(TAG, "处理行区域 当前页无法放下新的行，进行换页处理");
				do {
					onLayoutFinishPage(mCurrentPage);
					mCurrentPage = createPage();
					enabledRect = getEnabledRect(oldWidth,oldHeight,mTempEnabledRect,mCurrentLine,styleText);
					if(enabledRect != null){
						mCurrentLine.setLocation(enabledRect.left, enabledRect.top);
					}
				} while (enabledRect == null);
//				LogUtil.i(TAG, "处理行区域 当前页无法放下新的行 ==》 找到了新的页  当前页数为："+(pageList.size() + 1));
			}
			if(isNewlineChar){
				addPatch(mCurrentLine,enabledRect,styleText);
				handlerNextFloatLine();
				mCurrentLine = null;
			}
//			newLineTiem += System.currentTimeMillis() - startTime;
		}
		if(isPanleEnd && mCurrentLine != null){
			addPatch(mCurrentLine,enabledRect,styleText);
			handlerNextFloatLine();
			mCurrentLine = null;
		}
		return ++index;
	}
	
	private Rect getEnabledRect(int width,int height,Rect rectContainer,Line line,StyleText styleText){
		return getEnabledRect(mCurrentPage,width, height, rectContainer, line, styleText);
	}
	
	private Rect getEnabledRect(Page page,int width,int height,Rect rectContainer,Line line,StyleText styleText){
		Rect enabledRect = page.getEnabledRect(width, height, rectContainer, line);
		StyleText panleStyleText = styleText.getPanleStyleText();
		if(panleStyleText != null && enabledRect != null){
			int pLeft = 0;
			int pTop = 0;
			int pRight = 0;
			int mLeft = 0;
			int mRight = 0;
			int mTop = 0;
			Long top = mPanleTops.get(panleStyleText.hashCode());
			long newTop = enabledRect.top + page.getPageIndex() * 1000;
			if(panleStyleText.getStart() == line.getStart() || top == null || top - newTop > -1){
				mPanleTops.put(panleStyleText.hashCode(), newTop);
				pTop = panleStyleText.getPaddingTop();
				mTop = panleStyleText.getMarginTop();
			}
			if(enabledRect.left == mPageRect.left){
				pLeft = panleStyleText.getPaddingLeft();
				mLeft = panleStyleText.getMarginLeft();
			}
			if(enabledRect.right == mPageRect.right){
				pRight = panleStyleText.getPaddingRight();
				mRight = panleStyleText.getMarginRight();
			}
			line.setPadding(pLeft, pTop, pRight,line.getBottomPadding());
			if(mTop != 0 || mLeft != 0 || mRight != 0){
				height += mTop;
				width += mLeft + mRight;
				enabledRect = page.getEnabledRect(width, height, rectContainer, line);
				enabledRect.left += mLeft;
				enabledRect.right -= mRight;
				enabledRect.top += mTop;
			}
		}
		return enabledRect;
	}
	/**
	 * 处理之前计算当前行时发现不够放的浮动区域
	 * @param nextFloatLine
	 * @param nextPages
	 * @param currentPage
	 */
	private final void handlerNextFloatLine(){
		for(Line floatLine : mNextFloatLine){
			StyleText styleText = mStyleText.findStyleText(floatLine.getEnd());
			Rect enabledRect = getEnabledRect(floatLine.getWidth(),floatLine.getHeight(),mTempEnabledRect,floatLine,styleText);
			if(enabledRect != null){
				addPatch(floatLine,enabledRect,styleText);
			}else{
				addFloatLineToNextPages(floatLine,styleText);
			}
		}
		mNextFloatLine.clear();
	}
	/**
	 * 添加FloatLine对象到下一页或者下下下下下页
	 * 1.当前也空间已经 不足，尝试添加到后面的页中
	 * 2.如果后面的页已经有预先生成并且空间足够就添加进去，否则新生成一个页添加
	 * @param floatLine
	 * @param nextPages
	 */
	private final void addFloatLineToNextPages(Line floatLine,StyleText styleText){
		Rect rect = null;
		boolean isHandleFloatLine = false;
		for(Page page : mNextPages){
			rect = getEnabledRect(page,floatLine.getWidth(),floatLine.getHeight(),mTempEnabledRect,floatLine,styleText);
			if(rect != null){
				addPatch(page,floatLine, rect,styleText);
				isHandleFloatLine = true;
				break;
			}
		}
		if(!isHandleFloatLine){
			Page page = new Page(mSettingParam,mCurrentPageSize++);
			rect = getEnabledRect(page,floatLine.getWidth(),floatLine.getHeight(),mTempEnabledRect,floatLine,styleText);
			addPatch(page,floatLine, rect,styleText);
			mNextPages.add(page);
		}
	}
	
	private final void addPatch(Line line,Rect enabledRect,StyleText styleText) {
		addPatch(mCurrentPage,line, enabledRect,styleText);
	}
	/**
	 * 添加区域到页
	 * @param line
	 */
	private final void addPatch(Page page,Line line,Rect enabledRect,StyleText styleText) {
		if(styleText.isPanleEnd(line.getEnd())){
			line.setPanleEnd(styleText.getPanleEnd());
			line.setPadding(line.getLeftPadding(), line.getTopPadding(), line.getRightPadding(), styleText.getPanleStyleText().getPaddingBottom());
		}
		if(line.getLayoutType() == Constant.LAYOUT_TYPE_RIGHT_FLOAT){
			line.setLocation(enabledRect.right - line.getWidth(), enabledRect.top);
			line.setMaxBoundary(line.getWidth(), line.getHeight());
		}else if(line.getLayoutType() == Constant.LAYOUT_TYPE_LEFT_FLOAT){
			line.setLocation(enabledRect.left, enabledRect.top);
			line.setMaxBoundary(line.getWidth(), line.getHeight());
		}else{
			line.setLocation(enabledRect.left, enabledRect.top);
			line.setMaxBoundary(enabledRect.width(), enabledRect.height());
		}
		page.addPatch(line,this,styleText);
//		page.addPatch(line,this,isBindLayout,styleText);
//		if(!isBindLayout){
//			recycleLine(line);
//		}
//		recycleCharRect(line.getStart(),line.getEnd());
	}
	/**
	 * 布局完成一个页的回调
	 * @param page
	 */
	private void onLayoutFinishPage(Page page){
		if(mTask.isStop()){
			return;
		}
		page.releaseLayoutData(this);
//		if(!isBindLayout){
			if(page.getStart() < 0 || page.getEnd() < 0){
				return;
			}
			if(page.getEnd() == page.getStart()){
				char c = mStyleText.charAt(page.getStart());
				if(c == ' ' || c == '\n' || c == '\t'){
					return;
				}
			}else if(page.getEnd() - page.getStart() == 1){
				char c = mStyleText.charAt(page.getStart());
				if(c == ' ' || c == '\n' || c == '\t'){
					c = mStyleText.charAt(page.getEnd());
					if(c == ' ' || c == '\n' || c == '\t'){
						return;
					}
				}
			}
//		}
		page.clearUpCoverLayout(mStyleText);
		mLayoutPageSize++;
		if(mCallback != null){
			mCallback.onLayoutFinishPage(mTask,page,mStyleText.getTotalLength());
		}
	}
	/**
	 * 在构造新的page时会优先使用这些page，没有的情况才重新生成新的page
	 * @param nextPages 表示预先申请的后面几页
	 * @return
	 */
	private final Page createPage(){
		if(mNextPages.size() > 0){
			return mNextPages.pollFirst();
		}
		return new Page(mSettingParam,mCurrentPageSize++);
	}
	
	/**
	 * 计算字符宽高
	 * @param index
	 * @param charRect
	 */
	private final Rect measureText(int index,StyleText styleText){
		mTextPaint.set(mSettingParam.getSourcePaint());
		Util.measureText(styleText, mTextPaint, index, mPageRect.width(), mPageRect.height(), mCharRect);
		return mCharRect;
	}
	
	final void recycleRects(List<Rect> rects){
		for (Rect rect : rects) {
			recycleRect(rect);
		}
		rects.clear();
	}
	
	final void recycleRect(Rect rect){
		if(mRectCacheSize < mRectCaches.length){
			mRectCaches[mRectCacheSize] = rect;
			mRectCacheSize++;
		}
	}
	
	final Rect createRect(){
		Rect rect;
		if(mRectCacheSize > 0){
			mRectCacheSize--;
			rect = mRectCaches[mRectCacheSize];
		}else{
			rect = new Rect();
		}
		return rect;
	}
	
//	final void recycleLine(Line line){
//		if(mLineCacheSize < mLineCaches.length){
//			line.init();
//			mLineCaches[mLineCacheSize] = line;
//			mLineCacheSize++;
//		}
//	}
	
	private Line createLine(){
		Line line;
//		if(mLineCacheSize > 0 && !isBindLayout){
//			mLineCacheSize--;
//			line = mLineCaches[mLineCacheSize];
//		}else{
			line = new Line(mSettingParam);
//		}
		return line;
	}
	
	private final void sleep(){
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {}
	}
	
	public interface LayoutCallback{
		public void onLayoutFinishPage(TaskListener taskListener,Page page,int totalLength);
	}
}
