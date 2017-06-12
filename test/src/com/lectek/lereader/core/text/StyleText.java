package com.lectek.lereader.core.text;

import java.util.TreeMap;

import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;

import com.lectek.lereader.core.text.style.Border;
import com.lectek.lereader.core.text.style.FloatSpan;
import com.lectek.lereader.core.text.style.ImgPanelBGDrawableSpan;
import com.lectek.lereader.core.text.style.SpannableStringBuilder;
import com.lectek.lereader.core.util.ArrayMap;

public final class StyleText{
	public static final int PANLE_TYPE_P = 0;
	public static final int PANLE_TYPE_H = 1;
	public static final int PANLE_TYPE_TITLE = 2;
	public static final int PANLE_TYPE_DIV = 3;
	public static final int PANLE_TYPE_BLOCKQUOTE = 4;
	private String mId;
	private String mTag;
	private int mStart;
	private int mLength;
	private int mEnd;
	private int mChildSize;
	private StyleText[] mChilds;
	private CharacterStyle[] mSelfSpans;
	private CharacterStyle[] mTotalSpans;
	private SpannableStringBuilder mDataSource;
	private ArrayMap<StyleText> mStyleMap;
	private TreeMap<Comparable<Intervalo>, StyleText> mStyleTreeMap;
	private StyleText mParent;
	private StyleText mRootParent;
	private StyleText mPanleStyleText;
	private StyleMapKey mStyleMapKey;
	private FloatSpan mFloatSpan;
	private int mPanleType;
	private int mCNWidth;
	private int mCNHeight;
	private ImgPanelBGDrawableSpan mBGDrawable;
	private BackgroundColorSpan mBGColor;
	private int mPaddingLeft;
	private int mPaddingRight;
	private int mPaddingTop;
	private int mPaddingBottom;
	private int mMarginLeft;
	private int mMarginRight;
	private int mMarginTop;
	private int mMarginBottom;
	private Border mBorder;
	private boolean isCover;
	private boolean hasTitle;

	private StyleText(StyleText rootParent){
		mChildSize = 0;
		mEnd = -1;
		mSelfSpans = new CharacterStyle[0];
		mTotalSpans = new CharacterStyle[0];
		mStyleMapKey = new StyleMapKey(0);
		if(rootParent == null){
			mRootParent = this;
			mDataSource = new SpannableStringBuilder();
			mStyleTreeMap = new TreeMap<Comparable<Intervalo>, StyleText>();
			mStyleMap = new ArrayMap<StyleText>();
		}else{
			mRootParent = rootParent;
			mDataSource = rootParent.mDataSource;
			mStyleTreeMap = rootParent.mStyleTreeMap;
			mStyleMap = rootParent.mStyleMap;
			isCover = rootParent.isCover;
		}
		mCNWidth = -1;
		mCNHeight = -1;
		mPaddingTop = 0;
		mPaddingLeft = 0;
		mPaddingRight = 0;
		mPaddingBottom = 0;
		mMarginTop = 0;
		mMarginLeft = 0;
		mMarginRight = 0;
		mMarginBottom = 0;
	}
	
	public void setCover(boolean isCover) {
		mRootParent.isCover = isCover;
	}
	
	public boolean isCover() {
		return mRootParent.isCover;
	}
	
	/**
	 * @return the mBorder
	 */
	public Border getBorder() {
		return mBorder;
	}
	/**
	 * @param mBorder the mBorder to set
	 */
	public void setBorder(Border border) {
		this.mBorder = border;
	}

	public final String getTag() {
		return mTag;
	}

	public void setTag(String tag) {
		if(this.equals(mRootParent)){
			mId = tag;
		}
		this.mTag = tag;
	}
	
	public final SpannableStringBuilder getDataSource(){
		return mDataSource;
	}
	
	public final int getStart() {
		return mStart;
	}

	public final void setStart(int start){
		mStart = start;
	}
	
	public final void finishFillData(int start,int length){
		this.mStart = start;
		this.mLength = length;
		mEnd = mStart + mLength - 1;
		mStyleMap.put(getId(),this);
		if(length != 0 && isSelfPanle()){
			for (int i = 0; i < mChildSize; i++) {
				StyleText styleText = mChilds[i];
				if(styleText.isSelfPanle() && styleText.mEnd == mEnd){
					styleText.setPaddingBottom(mPaddingBottom);
					styleText.setMarginBottom(mMarginBottom);
				}
			}
		}
	}

	public final int getLength() {
		return mLength;
	}

	public final int getId(){
		return mId.hashCode() + mStart;
	}
	
	public final int getEnd() {
		return mEnd;
	}

	public final int getTotalLength(){
		return mDataSource.length();
	}
	
	public final char charAt(int index){
		return mDataSource.charAt(index);
	}
	
	/**
	 * @return the mPaddingLeft
	 */
	public int getPaddingLeft() {
		return mPaddingLeft;
	}
	/**
	 * @param mPaddingLeft the mPaddingLeft to set
	 */
	public void setPaddingLeft(int mPaddingLeft) {
		if(mPaddingLeft > 0){
			this.mPaddingLeft += mPaddingLeft;
		}
	}
	/**
	 * @return the mPaddingRight
	 */
	public int getPaddingRight() {
		return mPaddingRight;
	}

	/**
	 * @param mPaddingRight the mPaddingRight to set
	 */
	public void setPaddingRight(int mPaddingRight) {
		if(mPaddingRight > 0){
			this.mPaddingRight += mPaddingRight;
		}
	}
	/**
	 * @return the mPaddingTop
	 */
	public int getPaddingTop() {
		return mPaddingTop;
	}
	/**
	 * @param mPaddingTop the mPaddingTop to set
	 */
	public void setPaddingTop(int mPaddingTop) {
		if(mPaddingTop > 0){
			this.mPaddingTop += mPaddingTop;
		}
	}
	/**
	 * @return the mPaddingBottom
	 */
	public int getPaddingBottom() {
		return mPaddingBottom;
	}
	/**
	 * @param mPaddingBottom the mPaddingBottom to set
	 */
	public void setPaddingBottom(int mPaddingBottom) {
		if(mPaddingBottom > 0){
			this.mPaddingBottom += mPaddingBottom;
		}
	}

	/**
	 * @return the mMarginLeft
	 */
	public int getMarginLeft() {
		return mMarginLeft;
	}
	/**
	 * @param mMarginLeft the mMarginLeft to set
	 */
	public void setMarginLeft(int mMarginLeft) {
		if(mMarginLeft > 0){
			this.mMarginLeft += mMarginLeft;
		}
	}
	/**
	 * @return the mMarginRight
	 */
	public int getMarginRight() {
		return mMarginRight;
	}

	/**
	 * @param mMarginRight the mMarginRight to set
	 */
	public void setMarginRight(int mMarginRight) {
		if(mMarginRight > 0){
			this.mMarginRight += mMarginRight;
		}
	}
	/**
	 * @return the mMarginTop
	 */
	public int getMarginTop() {
		return mMarginTop;
	}
	/**
	 * @param mMarginTop the mMarginTop to set
	 */
	public void setMarginTop(int mMarginTop) {
		if(mMarginTop > 0){
			this.mMarginTop += mMarginTop;
		}
	}
	/**
	 * @return the mMarginBottom
	 */
	public int getMarginBottom() {
		return mMarginBottom;
	}
	/**
	 * @param mMarginBottom the mMarginBottom to set
	 */
	public void setMarginBottom(int mMarginBottom) {
		if(mMarginBottom > 0){
			this.mMarginBottom += mMarginBottom;
		}
	}
	
	public ImgPanelBGDrawableSpan getBGDrawable() {
		return mBGDrawable;
	}

	public void setBGDrawable(ImgPanelBGDrawableSpan mBGDrawable) {
		this.mBGDrawable = mBGDrawable;
	}

	/**
	 * @return the mBGColor
	 */
	public BackgroundColorSpan getBGColor() {
		return mBGColor;
	}

	/**
	 * @param mBGColor the mBGColor to set
	 */
	public void setBGColor(BackgroundColorSpan mBGColor) {
		this.mBGColor = mBGColor;
	}

	public final CharacterStyle[] getTotalSpans(){
		return mTotalSpans;
	}
	
	public final CharacterStyle[] getSelfSpans(){
		return mSelfSpans;
	}
	
	public StyleText addChild(StyleText styleText){
		if(!mRootParent.hasTitle && getPanleType(styleText.mTag) == PANLE_TYPE_H){
			mRootParent.hasTitle = true;
			styleText.hasTitle = true;
		}
		styleText.mParent = this;
		styleText.mId = styleText.mTag + mId;
		if(mChilds == null){
			mChilds = new StyleText[10];
		}
		if(mChilds.length - mChildSize < 1){
			StyleText[] tempArr = new StyleText[mChilds.length + 10];
			System.arraycopy(mChilds, 0, tempArr, 0, mChilds.length);
			mChilds = tempArr;
		}
		mChilds[mChildSize] = styleText;
		mChildSize++;
		if(styleText.isSelfPanle() && isSelfPanle()){
			styleText.setPaddingLeft(mPaddingLeft);
			styleText.setPaddingRight(mPaddingRight);
			styleText.setMarginLeft(mMarginLeft);
			styleText.setMarginRight(mMarginRight);
			if(styleText.mStart == mStart){
				styleText.setPaddingTop(mPaddingTop);
				styleText.setMarginTop(mMarginTop);
			}
		}
		return styleText;
	}
	
	public void setSpans(CharacterStyle[] spans,int length){
		CharacterStyle[] parentSpans = null;
		if(mParent != null){
			parentSpans = mParent.getTotalSpans();
		}
		mTotalSpans = new CharacterStyle[length + (parentSpans != null ? parentSpans.length : 0)];
		mSelfSpans = new CharacterStyle[length];
		length = mTotalSpans.length - length;
		for (int i = 0,j = 0; i < mTotalSpans.length; i++,j++) {
			if(i < length){
				mTotalSpans[i] = parentSpans[j];
				if(i == length - 1){
					j = -1;
				}
			}else{
				mTotalSpans[i] = spans[j];
				mSelfSpans[j] = spans[j];
			}
		}
	}
	
	public final void setFloatSpan(FloatSpan floatSpan){
		mFloatSpan = floatSpan;
	}
	
	public final FloatSpan getFloatSpan(){
		return mFloatSpan;
	}
	
	private int getPanleType(String tag){
		if("p".equals(tag)){
			if(isCover){
				return PANLE_TYPE_DIV;
			}
			return PANLE_TYPE_P;
		}else if("div".equals(tag)){
			return PANLE_TYPE_DIV;
		}else if("blockquote".equals(tag)){
			return PANLE_TYPE_BLOCKQUOTE;
		}else if(tag.lastIndexOf("h") != -1 && tag.length() == 2){
			if(isCover()){
				return PANLE_TYPE_DIV;
			}else if(hasTitle){
				return PANLE_TYPE_TITLE;
			}
			return PANLE_TYPE_H;
		}
		return -1;
	}
	
	public boolean isSelfPanle(){
		return equals(getPanleStyleText());
	}
	
	public StyleText getParentPanleStyleText(){
		if(isSelfPanle()){
			if(mParent != null){
				return mParent.getPanleStyleText();
			}
		}else{
			return getPanleStyleText();
		}
		return null;
	}
	
	public StyleText getPanleStyleText(){
		if(mPanleStyleText == null){
			mPanleStyleText = findParentParagraph(this);
			if(mPanleStyleText == null){
				mPanleStyleText = mRootParent;
			}else{
				mPanleType = getPanleType(mPanleStyleText.getTag());
			}
		}
		if(mPanleStyleText == mRootParent){
			return null;
		}
		return mPanleStyleText;
	}
	/**
	 * @return the isPanleStart
	 */
	public int getPanleStart() {
		StyleText styleText = getPanleStyleText();
		if(styleText != null){
			return styleText.getStart();
		}else{
			return -1;
		}
	}

	/**
	 * @return the isPanleEnd
	 */
	public int getPanleEnd() {
		StyleText styleText = getPanleStyleText();
		if(styleText != null){
			return styleText.getEnd();
		}else{
			return -1;
		}
	}

	/**
	 * @return the mPanleType
	 */
	public int getPanleType() {
		return mPanleType;
	}

	/**
	 * @return the isParagraphStart
	 */
	public boolean isPanleStart(int index) {
		return getPanleStart() == index;
	}
	/**
	 * @return the isParagraphStart
	 */
	public boolean isPanleEnd(int index) {
		return getPanleEnd() == index;
	}
	
	public void setCNRect(int width,int height){
		mCNWidth = width;
		mCNHeight = height;
	}
	
	public int getCNWidth(){
		return mCNWidth;
	}
	
	public int getCNHeight(){
		return mCNHeight;
	}
	
	public boolean hasCNRect(){
		return mCNWidth != -1;
	}
	
	private StyleText findParentParagraph(StyleText styleText){
		if(styleText == null){
			return null;
		}
		if(getPanleType(styleText.mTag) == -1){
			return findParentParagraph(styleText.getParent());
		}else{
			return styleText;
		}
	}

	public final StyleText getParent(){
		return mParent;
	}
	
	public final StyleText getRootParent(){
		return mRootParent;
	}
	
	public final void putIntervalo(Intervalo intervalo){
		mRootParent.mStyleTreeMap.put(intervalo, this);
	}
	
	public final int getIntervaloSzie(){
		return mRootParent.mStyleTreeMap.size();
	}
	
	public final synchronized StyleText findStyleTextById(int id){
		return mStyleMap.get(id);
	}
	
	public final synchronized StyleText findStyleText(int index){
		mStyleMapKey.mIndex = index;
		StyleText styleText = mStyleTreeMap.get(mStyleMapKey);
		return styleText != null ? styleText : mRootParent;
	}
	
	public static final StyleText createRoot(){
		return new StyleText(null);
	}
	
	public static final StyleText create(StyleText rootParent){
		return new StyleText(rootParent);
	}

	public static final class Intervalo implements Comparable<Intervalo>{
		private int mStart;
		private int mEnd;
		public Intervalo(int start,int end){
			mStart = start;
			mEnd = end;
		}
		
		@Override
		public int compareTo(Intervalo another) {
			if(mStart > another.mEnd){
				return 1;
			}
			if(mEnd < another.mStart){
				return -1;
			}
			return 0;
		}
	}
	
	private class StyleMapKey implements Comparable<Intervalo>{
		private int mIndex;
		StyleMapKey(int index){
			mIndex = index;
		}
		
		@Override
		public int compareTo(Intervalo another) {
			if(mIndex < another.mStart){
				return -1;
			}
			if(mIndex > another.mEnd){
				return 1;
			}
			return 0;
		}
	}
}
