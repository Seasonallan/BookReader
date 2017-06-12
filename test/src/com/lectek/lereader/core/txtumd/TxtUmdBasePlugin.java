package com.lectek.lereader.core.txtumd;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.style.CharacterStyle;

import com.lectek.lereader.core.util.LogUtil;

/**
 * txt umd解码基类
 * 
 * @author laijp
 * @date 2014-1-14
 * @email 451360508@qq.com
 */
public abstract class TxtUmdBasePlugin {
	private boolean DEBUG = false;
	protected void log(String... logs) {
		if(DEBUG){ 
			String logStr = "";
			for (String string : logs) {
				logStr += (string+"...");
			}
			LogUtil.i("TXT-UMD",logStr);
		}
	}
	public void setDebug(boolean debug){
		DEBUG = debug;
	}

	/**
	 * 打开本地书籍，读取相关信息操作
	 */
	public abstract void openBook(String filePath, String chapterPath) throws IOException;

	/**
	 * 获取某个位置的字节值
	 */
	public abstract byte getContentByte(int position);

	/**
	 * 获取当前章节名
	 */
	public abstract String getCurrentChapterName();

	/**
	 * 设置目录缓存地址
	 * @param path
	 */
	public void setCacheChapterPath(String path){
		
	}
	/**
	 * 获取当前章节位置
	 * 
	 * @param realChapter
	 *            用于txt获取章节位置， 实际章节位置都为1
	 */
	public abstract int getCurrentChapterIndex(boolean realChapter);

	/**
	 * 获取所有章节信息
	 * 
	 * @return
	 */
	public abstract ArrayList<String> getChapterList();

	/**
	 * 释放资源
	 */
	public void release(){
		mListener = null;
		mCurrentPageStringVectorMap.clear();
		log("release");
	}

	public static interface IScreenParam {
        /**
         * 刷新界面
         */
        public void invalidateView(String text);
		/**
		 * 计算当前字符串能绘制在当前行的数量
		 */
		public int calculateLineSize(String text);

		/**
		 * 计算当前屏幕能容纳的行数
		 */
		public int calculateLineCount();

		/**
		 * 解码获取到新的章节，回调添加到目录视图中
		 */
		public void onChapterFounded(ArrayList<String> chapter);
	}

	private int mRequestChapterIndex = -1;

	protected String mEncode = "UTF-8";
	protected int mBufferLength = 0;

	public int mPageStart = 0, mPageEnd = 0;
	private int _recordStart = 0, _recordEnd = 0;
	public HashMap<String, Vector<String>> mCurrentPageStringVectorMap;

	protected IScreenParam mListener;

	public TxtUmdBasePlugin(IScreenParam listener) {
		mListener = listener;
		MAX_PARAGRAPH = mListener.calculateLineCount() * 30 * 2;
		mCurrentPageStringVectorMap = new HashMap<String, Vector<String>>();
	}

	private void addCache(Vector<String> resVector, int requestChapterId, int startId, int endId){ 
		log("addCache", "requestChapterId:"+requestChapterId, "startId:"+startId," endId:"+endId );
		if(resVector.size() > 0){
			mCurrentPageStringVectorMap.put(buildKey(requestChapterId, startId, endId), resVector);
		}
	}
	
	private String buildKey(int chapterId, int startId, int endId) {
		return chapterId + "_" + startId + "_" + endId;
	} 

	private String getCurrentKey() {
		return buildKey(getCurrentChapterIndex(false), mPageStart, mPageEnd);
	}

    /**
     * 获取某页面开始结束
     * @param pageIndex
     * @return
     */
    public int[] getPageStartEndIndex(int chapterIndex, int pageIndex){
        Set<String> keyVec = mCurrentPageStringVectorMap.keySet();
        for (String key : keyVec) {
            String[] keySpe = key.split("_");
            if(keySpe != null && keySpe.length == 3 &&
                    keySpe[1].equals(pageIndex+"") && keySpe[0].equals(chapterIndex+"")){
                return new int[]{Integer.parseInt(keySpe[1]), Integer.parseInt(keySpe[2])};
            }
        }
        return null;
    }

	public int containStartId(int startId){
		Set<String> keyVec = mCurrentPageStringVectorMap.keySet();
		for (String key : keyVec) {
			String[] keySpe = key.split("_");
			if(keySpe != null && keySpe.length == 3 && 
			   keySpe[1].equals(startId+"") && keySpe[0].equals(getCurrentChapterIndex(false)+"")){
				return Integer.parseInt(keySpe[2]);
			}
		}
		return -1;
	}
	public int containEndId(int endId){
		Set<String> keyVec = mCurrentPageStringVectorMap.keySet();
		for (String key : keyVec) {
			String[] keySpe = key.split("_");
			if(keySpe != null && keySpe.length == 3 && 
			   keySpe[2].equals(endId+"") && keySpe[0].equals(getCurrentChapterIndex(false)+"")){
				return Integer.parseInt(keySpe[1]);
			}
		}
		return -1;
	}

	/**
	 * 获取该页第一行，用于保存书签
	 * 
	 * @return
	 */
	public String getFirstLine() {
		Vector<String> currentPageStringVector = getCurrentPageStringVector();
		if (currentPageStringVector == null
				|| currentPageStringVector.size() == 0) {
			return "";
		} else {
			return currentPageStringVector.get(0);
		}
	}

	/**
	 * 获取当前页码内容
	 * @return
	 */
	public Vector<String> getCurrentPageStringVector(){
		return mCurrentPageStringVectorMap
				.get(getCurrentKey());
	}
	
	/**
	 * 绘制内容
	 * 
	 * @param lineHeight
	 *            每一行的高度，包括行间距
	 */
	public boolean onDraw(Canvas canvas, float x, float y, Paint paint,
			float lineHeight, float lineSpacing,  float lineWidth) {
		long c = System.currentTimeMillis();
		Vector<String> currentPageStringVector = getCurrentPageStringVector();
		if (currentPageStringVector == null
				|| currentPageStringVector.size() == 0) {
			return false;
		}
        digestEnd = -1;
        mRectMaps.clear();
        int lineStart = mPageStart;
		for (int i = 0; i < currentPageStringVector.size(); ++i) {
			String str = currentPageStringVector.get(i);
            lineStart = drawLine(canvas, x, y, (TextPaint) paint, str, lineWidth, lineHeight, lineSpacing, lineStart);
			y += (lineHeight+lineSpacing);
		}
		log("onDraw cost :"+(System.currentTimeMillis() - c));
		return true;
	}

   private HashMap<Integer, RectF> mRectMaps = new HashMap<Integer, RectF>();

	/**
	 * 绘制一行文字
	 */
	private int drawLine(Canvas canvas, float x, float y, TextPaint paint,
			String str, float lineWidth, float lineHeight, float lineSpacing, int lineStart_) {
		float paddingLeft = x;
        int lineStart = lineStart_;
		float width = paint.measureText(str);
		float remindWidth = lineWidth - width;
		float everyRemindWidth = remindWidth > paint.measureText("WW")? 0: (remindWidth/str.length());
        Paint.FontMetrics metrics = paint.getFontMetrics();
        float top = metrics.descent - metrics.top;
		for (int i = 0; i < str.length(); i++) { 
			String c = str.substring(i, i+1);
            int byteLength = 1;
            try {
                byteLength = c.getBytes(mEncode).length; 
                if (isUmdDecode()) {
					byteLength /= 2;
				}
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            RectF rectF =  new RectF(paddingLeft , y - top *3/4, paddingLeft + (paint.measureText(c) + everyRemindWidth)  , y - top *3/4- lineSpacing + lineHeight);
            mRectMaps.put(lineStart, rectF);
            if (digestStartIdMaps.containsKey(lineStart)){
                digestEnd = digestStartEndMaps.get(lineStart);
                digestCharacter = digestStartIdMaps.get(lineStart);
            }else if (lineStart >= digestEnd){
                digestEnd = -1;
            }
            if (digestEnd >= 0){
                digestCharacter.updateDrawState(paint);
                drawBgColor(canvas, rectF, paint, lineHeight+ lineSpacing);
            }
            canvas.drawText(c, paddingLeft, y, paint);
            paddingLeft += (paint.measureText(c) + everyRemindWidth);
            lineStart += byteLength;
		}
        return lineStart;
	}
    private int digestEnd = -1;
    private CharacterStyle digestCharacter = null;

	private void drawBgColor(Canvas canvas,RectF rect,TextPaint paint, float lineHeight){
		int oldColor = paint.getColor();
		paint.setColor(paint.bgColor); 
		RectF mTempBgRect = new RectF(); 
		mTempBgRect.set(rect.left, rect.top, rect.right, rect.bottom); 
		canvas.drawRect(mTempBgRect, paint);
		paint.setColor(oldColor);
	}
	
	
    /**
     * 验证是否初始化
     * @return
     */
    public boolean isInit(){
        return true;
    }
    private HashMap<Integer, CharacterStyle> digestStartIdMaps = new HashMap<Integer, CharacterStyle>();
    private HashMap<Integer, Integer> digestStartEndMaps = new HashMap<Integer, Integer>();
    /**
     * 动态设置样式
     * @param chapterIndex
     * @param what
     * @param start
     * @param end
     * @param flags
     */
    public void setSpan(int chapterIndex,Object what, int start, int end, int flags){
        if(!isInit()){
            return;
        }
        if (digestStartIdMaps.containsValue(what)){
            Set<Integer> set = digestStartIdMaps.keySet();
            for (Integer s : set){
                if (digestStartIdMaps.get(s) == what){
                    int old_start = s;
                    digestStartIdMaps.remove(old_start);
                    digestStartEndMaps.remove(old_start);
                    break;
                }
            }
        } 
        digestStartIdMaps.put(start, (CharacterStyle) what);
        digestStartEndMaps.put(start, end);
        invalidateCachePage();
    }
    /**
     * 删除动态设置的样式
     * @param chapterIndex
     * @param what
     */
    public void removeSpan(int chapterIndex,Object what) {
        if(!isInit()){
            return;
        }
        if (digestStartIdMaps.containsValue(what)){
            Set<Integer> set = digestStartIdMaps.keySet();
            for (Integer start : set){
                if (digestStartIdMaps.get(start) == what){
                    digestStartIdMaps.remove(start);
                    digestStartEndMaps.remove(start);
                    break;
                }
            }
        }
        invalidateCachePage();
    }

    /**
     * 3.截取数据源
     * @param chapterIndex
     * @param start
     * @param end
     * @return
     */
    public String subSequence(int chapterIndex,int start, int end) {
        if(!isInit()){
            return null;
        }
        Vector<String> currentPageStringVector = getCurrentPageStringVector();
        if (currentPageStringVector == null
                || currentPageStringVector.size() == 0) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        int lineStart = mPageStart;
        for (int i = 0; i < currentPageStringVector.size(); ++i) {
            String str = currentPageStringVector.get(i);
            buffer.append(str);
        }
        String currenPageString = buffer.toString();
        int startE = 0, endE = 0;
        for (int i = 0; i < currenPageString.length(); i++) {
            String c = currenPageString.substring(i, i + 1);
            int byteLength = 1;
            try {
                byteLength = c.getBytes(mEncode).length;
                if (isUmdDecode()) {
					byteLength /= 2;
				}
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (lineStart == start){
                startE = i;
            }
            if (lineStart >= end){
                endE = i;
                break;
            }
            lineStart += byteLength;
        }
        if (endE > startE){
            return currenPageString.substring(startE, endE);
        }
        return "";
    }
    /**
     * 1.找到坐标所在的绘制元素，在数据源中的位置（相当于文字位置，也可以是图片等）
     * @param pageIndex
     * @param x
     * @param y
     * @param isAccurate
     * @return
     */
    public int findIndexByLocation(int chapterIndex,int pageIndex,int x, int y, boolean isAccurate) {
        if(!isInit()){
            return -1;
        }
        Set<Map.Entry<Integer, RectF>> set = mRectMaps.entrySet();
        for (Map.Entry<Integer, RectF> entry: set){
            RectF rectF = entry.getValue();
            if (rectF.left <= x && rectF.right >= x){
                if (rectF.top <= y && rectF.bottom >= y){
                    return entry.getKey();
                }
            }
        }
        return -1;
    }
    /**
     * 2.返回绘制元素的宽高等信息
     */
    public RectF findRectByPosition(int chapterIndex,int pageIndex, int position) {
        if(!isInit()){
            return null;
        }
        return mRectMaps.get(position);
    }

    /**
     * 刷新绑定页
     * @param canvas
     * @param pageIndex
     * @return
     */
    public void invalidateCachePage() {
    	mListener.invalidateView(null);
    }

	/**
	 * 获取当前章节字节数量
	 * 
	 * @return
	 */
	public int getContentLength() {
		return mBufferLength;
	} 

	/**
	 * 动画结束参数重置
	 * 
	 * @param isCancel
	 *            是否成功翻页
	 */
	public void onAnimEnd(boolean isCancel) {
		if (!isCancel) {// 成功
			_recordStart = mPageStart;
			_recordEnd = mPageEnd;
			mRequestChapterIndex = getCurrentChapterIndex(false);
		} else {
			resetChapterIndex(mRequestChapterIndex);
			mPageStart = _recordStart;
			mPageEnd = _recordEnd; 
		}
	}
	/**
	 * 重置章节信息【length 和 index】
	 * @param chapterIndex
	 */
	protected void resetChapterIndex(int chapterIndex) {
        log("deleteBookDigestsSpan resetChapterIndex>> " + chapterIndex);
        digestStartIdMaps.clear();
        digestStartEndMaps.clear();
	}

	/**
	 * 读取某章节某百分比位置
	 */
	public void readChapterData(int chapterIndex, double wordPercent) {
		if (mRequestChapterIndex == -1) {
			mRequestChapterIndex = chapterIndex;
		} 
		resetChapterIndex(chapterIndex); 
		jumpPage(getPageIndexFromPercent(wordPercent), false);  
	}

	/**
	 * 读取某章节某集体字节位置
	 */
	public void readChapterData(int chapterIndex, int wordPosition) {
		if (mRequestChapterIndex == -1) {
			mRequestChapterIndex = chapterIndex;
		} 
		resetChapterIndex(chapterIndex);
		jumpPage(wordPosition, false); 
	}

	/**
	 * 获取章节总数
	 */
	public int getChapterCount() {
		return 1;
	}
 
	/**
	 * 获取上一页 章节信息，页码信息
	 * 
	 * @return
	 */
	public int[] getPrePageIndex() {
		int chapterIndex = getCurrentChapterIndex(false);
		if (mPageStart <= 0) {
			mPageStart = 0;
			if (chapterIndex <= 0) {
				return null;
			} else {
				int requestChapterIndex = chapterIndex - 1;
				readChapterData(requestChapterIndex, 1f);
				return new int[] {requestChapterIndex, mPageStart };
			}
		} else {
			mPageEnd = mPageStart;
			int start = containEndId(mPageEnd);
			if(start >= 0){//缓存存在
				mPageStart = start;
				return new int[] { chapterIndex, mPageStart};
			}
			int pageStart = pageUp(chapterIndex, mPageEnd);
			if (pageStart < mPageStart) {
				mPageStart = pageStart;
			} else {// 第二页翻到第一页
				mPageEnd = pageStart;
				mPageStart = 0;
			}
			return new int[] { chapterIndex, mPageStart };
		}
	}

	/**
	 * 获取下一页 章节信息，页码信息
	 * 
	 * @return
	 */
	public int[] getNextPageIndex() {
		int chapterIndex = getCurrentChapterIndex(false);
		if (mPageEnd >= mBufferLength) { 
			if (chapterIndex >= getChapterCount() -1) {
				return null;
			} else {
				int requestChapterIndex = chapterIndex + 1;
				readChapterData(requestChapterIndex, 0);
				return new int[] { requestChapterIndex, 0};
			}
		} else {
			mPageStart = mPageEnd;
			int end = containStartId(mPageStart);
			if(end >= 0){//缓存存在
				mPageEnd = end;
				return new int[] { chapterIndex, mPageStart };
			}
			mPageEnd = pageDown(chapterIndex, mPageStart);
			return new int[] { chapterIndex, mPageStart };
		}
	}

	/**
	 * 清空缓存
	 */
	public void clearCache() {
        _lastEndPosition = -1;
        _lastStartPosition = -1;
		mCurrentPageStringVectorMap.clear();
	}

	/**
	 * 跳转到指定字节位置
	 * 
	 * @param position
	 * @param mand
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public void jumpPage(int position, boolean mand) {
		log("jumpPage", "position:"+position+",mBufferLength:"+mBufferLength);
		position = position < 4 ? 0 : position;
		position = position > mBufferLength - 1 ? mBufferLength : position;

		mPageStart = position;
		if (mPageStart % 2 != 0 && isUnicode()) {
			mPageStart += (position == mBufferLength) ? -1 : 1;
		}
		mPageEnd = mPageStart; 
		if (position == mBufferLength) {
			int start = containEndId(mPageEnd);
			if(start >= 0){//缓存存在
				mPageStart = start;
				return;
			} 
			int pageStart = pageUp(getCurrentChapterIndex(false), mPageStart);
			if (pageStart < mPageStart) {
				mPageStart = pageStart;
			} else {// 第二页翻到第一页
				mPageEnd = pageStart;
				mPageStart = 0;
			}
		} else {
			int end = containStartId(mPageStart);
			if(end >= 0){//缓存存在
				mPageEnd = end;
				return;
			} 
			if (isUmdDecode() || mand) {
				mPageEnd = pageDown(getCurrentChapterIndex(false), mPageEnd);
			} else {
				byte[] paraBuf = readParagraphBack(mPageStart);
				mPageStart -= paraBuf.length;
				// byte[] paraBuf = readParagraphForward(mPageEnd);
				// mPageEnd += paraBuf.length;
				mPageEnd = mPageStart;
				mPageEnd = pageDown(getCurrentChapterIndex(false), mPageEnd);
			}
		}
	}

	/**
	 * 跳转到百分比
	 * 
	 * @param percent
	 *            百分比
	 * @param mand
	 *            true者定位到当前百分比，否则定位到当前百分比所在的段落
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public int getPageIndexFromPercent(double percent) {
		if (percent < 0.0 || percent > 1.0) {
			throw new IndexOutOfBoundsException();
		}
		mPageStart = (int) (mBufferLength * percent);
		return mPageStart;
	}

	/**
	 * 读取下一页文字数组
	 */
	private int pageDown(int requestChapterId, int startId) {
		log("pageDown", "startId:"+startId);
		int endId = startId;
		String strParagraph = "";
		Vector<String> resVector = new Vector<String>();
		int maxLine = mListener.calculateLineCount();
		while (resVector.size() < maxLine && endId < mBufferLength) {
			byte[] paraBuf = readParagraphForward(endId);
			endId += paraBuf.length;
			int changeLineLength = 0;
			try {
				strParagraph = new String(paraBuf, mEncode);
				if (strParagraph.indexOf("\r\n") != -1) {
					changeLineLength = "\r\n".getBytes(mEncode).length;
					strParagraph = strParagraph.replaceAll("\r\n", "");
				} else if (strParagraph.indexOf("\n") != -1) {
					changeLineLength = "\n".getBytes(mEncode).length;
					strParagraph = strParagraph.replaceAll("\n", "");
				} else if (strParagraph.indexOf("\r") != -1) {
					changeLineLength = "\r".getBytes(mEncode).length;
					strParagraph = strParagraph.replaceAll("\r", "");
				} else if (isUnicode()) {
					changeLineLength = -2;
				}

			} catch (UnsupportedEncodingException e) {
				break;
			}
            log("pageDown", endId+  ">>> changeLineLength:"+changeLineLength);

			if (strParagraph.length() == 0) {
				// lines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				int nSize = mListener.calculateLineSize(strParagraph);
				resVector.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
				if (resVector.size() >= maxLine) {
					break;
				}
			}
			if (strParagraph.length() != 0) {
				try {
					endId -= (strParagraph.getBytes(mEncode).length + changeLineLength);
				} catch (UnsupportedEncodingException e) {
					break;
				}
			}
		}
		addCache(resVector, requestChapterId, startId, endId);
		return endId;
	}

	/**
	 * 定位到上一页第一个字符
	 * 
	 * @return
	 */
	private int pageUp(int requestChapterId, int endId) {
		log("pageUp", "endId:"+endId);
		int startId = endId;
		Vector<String> resVector = new Vector<String>();
		String strParagraph = "";
		while (resVector.size() < mListener.calculateLineCount()
				&& startId > 0) {
			Vector<String> paraLines = new Vector<String>();
			byte[] paraBuf = readParagraphBack(startId);
			startId -= paraBuf.length;
			if (startId <= 0) {
				return pageDown(requestChapterId, 0);
			}
			try {
				strParagraph = new String(paraBuf, mEncode);
			} catch (UnsupportedEncodingException e) {
				break;
			}
			strParagraph = strParagraph.replaceAll("\r\n", "");
			strParagraph = strParagraph.replaceAll("\n", "");
			strParagraph = strParagraph.replaceAll("\r", "");

			if (strParagraph.length() == 0) {
				// paraLines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				int nSize = mListener.calculateLineSize(strParagraph);
				paraLines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
			}
			resVector.addAll(0, paraLines);
		}
		while (resVector.size() > mListener.calculateLineCount()) {
			try {
				startId += resVector.get(0).getBytes(mEncode).length;
				resVector.remove(0);
			} catch (UnsupportedEncodingException e) {
				break;
			}
		} 
		addCache(resVector, requestChapterId, startId, endId);
		return startId;
	}

	private int _lastEndPosition = -1;
	private int _lastStartPosition = -1;
	private int MAX_PARAGRAPH = 40 * 200;

	/**
	 * 读取下一个段落
	 */
	protected byte[] readParagraphForward(int nFromPos) {
		int nStart = nFromPos;
		int i = nStart;
		byte b0, b1;
		int byteCount = 0;
		if (nStart > _lastStartPosition && nStart < _lastEndPosition) {
			i = _lastEndPosition;
		} else {
			if (isUnicode()) {
				while (i < mBufferLength - 1) {
					b0 = getContentByte(i++);
					b1 = getContentByte(i++);
					if (isEnterKey(b0, b1)) { // 回车
						break;
					} else if (byteCount > MAX_PARAGRAPH) {// 空格
						if (b0 == 0x30 && b1 == 0x00) {
							break;
						}
					}
					byteCount++;
				}
			} else {
				while (i < mBufferLength) {
					b0 = getContentByte(i++);
					if (b0 == 0x0a) { // 回车
						break;
					}
				}
			}
			_lastStartPosition = nStart;
		}
		_lastEndPosition = i;
		int nParaSize = i - nStart;
		byte[] buf = new byte[nParaSize];
		// buf = getContentByte(nFromPos, nFromPos + i);
		for (i = 0; i < nParaSize; i++) {
			buf[i] = getContentByte(nFromPos + i);
		}
		return buf;
	}

	/**
	 * 是否是回车字节
	 * 
	 * @param b0
	 * @param b1
	 * @return
	 */
	private boolean isEnterKey(byte b0, byte b1) {
		if (isUmdDecode()) {
			return b0 == 0x20 && b1 == 0x29;
		}
		return (b0 == 0x20 && b1 == 0x29) || (b0 == 0x00 && b1 == 0x0a)
				|| (b0 == 0x0a && b1 == 0x00);
	}

	/**
	 * 读取上一段落
	 */
	protected byte[] readParagraphBack(int nFromPos) {
		int nEnd = nFromPos;
		int i;
		byte b0, b1;
		int byteCount = 0;
		if (isUnicode()) {
			i = nEnd - 2;
			while (i > 0) {
				b1 = getContentByte(i + 1);
				b0 = getContentByte(i);
				if (isEnterKey(b0, b1) && i != nEnd - 2) {
					i += 2;
					break;
				} else if (byteCount > MAX_PARAGRAPH) {
					if (b0 == 0x30 && b1 == 0x00 && i != nEnd - 2) {
						i += 2;
						break;
					}
				}
				byteCount++;
				i--;
			}

		} else {
			i = nEnd - 1;
			while (i > 0) {
				b0 = getContentByte(i);
				if (b0 == 0x0a && i != nEnd - 1) {
					i++;
					break;
				}
				i--;
			}
		}
		if (i < 0)
			i = 0;
		int nParaSize = nEnd - i;
		int j;
		byte[] buf = new byte[nParaSize];
		for (j = 0; j < nParaSize; j++) {
			buf[j] = getContentByte(i + j);
		}
		return buf;
	}

	private boolean isUnicode() {
		return mEncode.toUpperCase().equals("UNICODE");
	}

	protected boolean isUmdDecode() {
		return false;
	}

}
