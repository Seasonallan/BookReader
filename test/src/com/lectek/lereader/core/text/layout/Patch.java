package com.lectek.lereader.core.text.layout;

import com.lectek.lereader.core.text.Constant;
import com.lectek.lereader.core.text.SettingParam;

/**
 * 代表一个矩形的内容区域
 * @author lyw
 *
 */
public abstract class Patch extends AbsPatch{
	/** 填充大小*/
	protected int mLeftPadding;
	/** 填充大小*/
	protected int mTopPadding;
	/** 填充大小*/
	protected int mRightPadding;
	/** 填充大小*/
	protected int mBottomPadding;
	/** 区域的起始坐标点x*/
	protected int mLeft;
	/** 区域的起始坐标点y*/
	protected int mTop;
	/** 区域的结束坐标点x*/
	protected int mRight;
	/** 区域的结束坐标点y*/
	protected int mBottom;
	/** 内容宽度*/
	protected int mContentWidth;
	/** 内容高度*/
	protected int mContentHeight;
	/** 宽度*/
	protected int mWidth;
	/** 高度*/
	protected int mHeight;
	/** 布局类型*/
	protected int mLayoutType;
	/** 最大允许使用的宽度*/
	protected int mMaxWidth;
	/**  最大允许使用的高度*/
	protected int mMaxHeight;
	/** 是否是块级元素的开始位置*/
	protected int mPanleStart;
	/** 是否是块级元素的结束位置*/
	protected int mPanleEnd;
	/** 块级元素类型*/
	protected int mPanleType;
	
	Patch(SettingParam settingParam){
		super(settingParam);
		mLayoutType = Constant.LAYOUT_TYPE_NOTHING;
	}
	
	public void init(){
		mPanleStart = -1;
		mPanleEnd = -1;
		mPanleType = -1;
		mLeftPadding = 0;
		mTopPadding = 0;
		mRightPadding = 0;
		mBottomPadding = 0;
		mLeft = 0;
		mTop = 0;
		mRight = 0;
		mBottom = 0;
		mContentWidth = 0;
		mContentHeight = 0;
		mWidth = 0;
		mHeight = 0;
		mLayoutType = 0;
		mMaxWidth = 0;
		mMaxHeight = 0;
	}
	
	public final void setPanleStart(int panleStart,int panleType){
		mPanleStart = panleStart;
		mPanleType = panleType;
	}
	
	public final void setPanleEnd(int panleEnd){
		mPanleEnd = panleEnd;
	}
	
	
	/**
	 * @return the mPanleStart
	 */
	public int getPanleStart() {
		return mPanleStart;
	}

	/**
	 * @return the mPanleEnd
	 */
	public int getPanleEnd() {
		return mPanleEnd;
	}

	/**
	 * @return the isPanleStart
	 */
	public boolean isPanleStart() {
		return mPanleStart == mStart;
	}

	/**
	 * @return the isPanleEnd
	 */
	public boolean isPanleEnd() {
		return mPanleEnd == mEnd;
	}

	/**
	 * @return the mLayoutType
	 */
	public int getLayoutType() {
		return mLayoutType;
	}
	/**
	 * @param mLayoutType the mLayoutType to set
	 */
	public void setLayoutType(int layoutType) {
		mLayoutType = layoutType;
	}
	/**
	 * 定位绘制单元起点位置
	 * @param offsetX
	 * @param offsetY
	 */
	void setLocation(int left,int top){
		mLeft = left;
		mTop = top;
		mRight = mLeft + mWidth;
		mBottom = mTop + mHeight;
	}
	/**
	 * 设置宽高
	 * @param maxWidth
	 * @param maxHeight
	 */
	void setBoundary(int width, int height) {
		mContentWidth = width;
		mContentHeight = height;
		mWidth = mContentWidth + mLeftPadding + mRightPadding;
		mHeight = mContentHeight + mTopPadding + mBottomPadding;
		mRight = mLeft + mWidth;
		mBottom = mTop + mHeight;
	}
	/**
	 * 设置允许绘制的最大范围
	 * @param maxWidth
	 * @param maxHeight
	 */
	void setMaxBoundary(int maxWidth, int maxHeight) {
		mMaxWidth = maxWidth;
		mMaxHeight = maxHeight;
	}
	/**
	 * 设置填充大小
	 * @return
	 */
	void setPadding(int left, int top, int right, int bottom){
		mLeftPadding = left;
		mRightPadding = right;
		mTopPadding = top;
		mBottomPadding = bottom;
		mWidth = mContentWidth + mLeftPadding + mRightPadding;
		mHeight = mContentHeight + mTopPadding + mBottomPadding;
		mRight = mLeft + mWidth;
		mBottom = mTop + mHeight;
	}
	/**
	 * @return the mLeftPadding
	 */
	public int getLeftPadding() {
		return mLeftPadding;
	}
	/**
	 * @return the mTopPadding
	 */
	public int getTopPadding() {
		return mTopPadding;
	}
	/**
	 * @return the mRightPadding
	 */
	public int getRightPadding() {
		return mRightPadding;
	}
	/**
	 * @return the mBottomPadding
	 */
	public int getBottomPadding() {
		return mBottomPadding;
	}
	/**
	 * @return the mLeft
	 */
	public int getLeft() {
		return mLeft;
	}
	/**
	 * @return the mTop
	 */
	public int getTop() {
		return mTop;
	}
	/**
	 * @return the mLeft
	 */
	public int getRight() {
		return mRight;
	}
	/**
	 * @return the mTop
	 */
	public int getBottom() {
		return mBottom;
	}
	/**
	 * 获取绘制单元内容宽度（包含内容区域）
	 * @return
	 */
	public int getContentWidth(){
		return mContentWidth;
	}
	/**
	 * 获取绘制单元内容高度（包含内容区域）
	 * @return
	 */
	public int getContentHeight(){
		return mContentHeight;
	}
	/**
	 * 获取绘制单元内容高度（只包含内容区域、padding）
	 * @return
	 */
	public int getWidth(){
		return mWidth;
	}
	/**
	 * 获取绘制单元内容高度（只包含内容区域、padding）
	 * @return
	 */
	public int getHeight(){
		return mHeight;
	}
	/**
	 * 获取最大允许使用的宽度
	 * @return 
	 */
	public final int getMaxWidth() {
		return mMaxWidth;
	}
	/**
	 * 获取最大允许使用的高度
	 * @return the mMaxHeight
	 */
	public final int getMaxHeight() {
		return mMaxHeight;
	}
}
