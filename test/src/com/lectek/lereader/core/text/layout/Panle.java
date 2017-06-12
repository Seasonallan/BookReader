package com.lectek.lereader.core.text.layout;

import android.graphics.Canvas;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;

import com.lectek.lereader.core.text.StyleText;
import com.lectek.lereader.core.text.style.Border;
import com.lectek.lereader.core.text.style.ImgPanelBGDrawableSpan;


public class Panle{
	protected int mId;
	/** 内容起始点从（代表该下标所对应的字符）*/
	protected int mStart;
	/** 内容结束点（代表该下标所对应的字符）*/
	protected int mEnd;
	/** 开始y位置*/
	protected int mTop;
	/** 结束y位置*/
	protected int mBottom;
	/** 开始x位置*/
	protected int mLeft;
	/** 结束x位置*/
	protected int mRight;
	
	public Panle(int id){
		mId = id;
	}
	
	/**
	 * @return the mId
	 */
	public int getId() {
		return mId;
	}

	/**
	 * 设置内容对应的字符下标
	 * @param start （代表该下标所对应的字符）
	 * @param end （代表该下标所对应的字符）
	 */
	public void setContent(int start,int end){
		this.mStart = start;
		if(start <= end){
			this.mEnd = end;
		}
	}
	/**
	 * 获取内容对应的字符开始下标
	 * @return the mStart （代表该下标所对应的字符）
	 */
	public final int getStart() {
		return mStart;
	}
	/**
	 * 获取内容对应的字符结束下标
	 * @return the mEnd （代表该下标所对应的字符）
	 */
	public final int getEnd() {
		return mEnd;
	}
	/**
	 * @return the mTop
	 */
	public int getTop() {
		return mTop;
	}
	/**
	 * @param mTop the mTop to set
	 */
	public void setTop(int mTop) {
		this.mTop = mTop;
	}
	/**
	 * @return the mBottom
	 */
	public int getBottom() {
		return mBottom;
	}
	/**
	 * @param mBottom the mBottom to set
	 */
	public void setBottom(int mBottom) {
		this.mBottom = mBottom;
	}
	
	/**
	 * @return the mLeft
	 */
	public int getLeft() {
		return mLeft;
	}

	/**
	 * @param mLeft the mLeft to set
	 */
	public void setLeft(int mLeft) {
		this.mLeft = mLeft;
	}

	/**
	 * @return the mRight
	 */
	public int getRight() {
		return mRight;
	}

	/**
	 * @param mRight the mRight to set
	 */
	public void setRight(int mRight) {
		this.mRight = mRight;
	}

	public void drawBG(Canvas canvas,StyleText mStyleText,TextPaint mTextPaint){
		StyleText styleText = mStyleText.findStyleTextById(mId);
		if(styleText != null){
			BackgroundColorSpan bgColorSpan = styleText.getBGColor();
			ImgPanelBGDrawableSpan bgDrawable = styleText.getBGDrawable();
			Border border = styleText.getBorder();
			StyleText parentStyleText = styleText.getParentPanleStyleText();
			int left = mLeft + (parentStyleText != null ? parentStyleText.getPaddingLeft() : 0);
			int right = mRight - (parentStyleText != null ? parentStyleText.getPaddingRight() : 0);;
			if(bgColorSpan != null){
				mTextPaint.setColor(bgColorSpan.getBackgroundColor());
				canvas.drawRect(left,mTop, right, mBottom,mTextPaint);
			}
			if(bgDrawable != null){
				bgDrawable.drawBG(canvas, mStyleText.getDataSource(), mStart, mEnd,
						left,mTop, right, mBottom,0,mTextPaint);
			}
			if(border != null){
				border.draw(canvas,styleText, mStart, mEnd,
						left,mTop, right, mBottom,mTextPaint);
			}
		}
	}
}
