package com.lectek.lereader.core.text.style;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.text.TextUtils;

import com.lectek.lereader.core.text.StyleText;
import com.lectek.lereader.core.util.ContextUtil;

public class Border {
	/** 定义无边框*/
	public static final int TYPE_NONE = 0;
	/** 定义点状边框。在大多数浏览器中呈现为实线*/
	public static final int TYPE_DOTTED = 1;
	/** 定义虚线。在大多数浏览器中呈现为实线*/
	public static final int TYPE_DASHED = 2;
	/** 定义实线*/
	public static final int TYPE_SOLID = 3;
	private int leftCorol;
	private int rightCorol;
	private int topCorol;
	private int bottomCorol;
	private int leftWidth;
	private int rightWidth;
	private int topWidth;
	private int bottomWidth;
	private int leftType;
	private int rightType;
	private int topType;
	private int bottomType;
	private DashPathEffect mDashedEffect;
	private DashPathEffect mDottedEffect;
	
	public Border(){
		this(TYPE_NONE);
		setWidth(ContextUtil.DIPToPX(2));
		setCorol(Color.BLACK);
	}
	
	public Border(int topType){
		this(topType, topType, topType, topType);
	}
	
	public Border(int topType,int rightType){
		this(topType, rightType, topType, rightType);
	}
	
	public Border(int topType,int rightType,int bottomType){
		this(topType, rightType, bottomType, rightType);
	}
	
	public Border(int topType,int rightType,int bottomType,int leftType){
		setLeftType(leftType);
		setRightType(rightType);
		setTopType(topType);
		setBottomType(bottomType);
		setWidth(ContextUtil.DIPToPX(2));
		setCorol(Color.BLACK);
	}
	
	public void setWidth(int topWidth){
		setWidth(topWidth, topWidth, topWidth, topWidth);
	}
	
	public void setWidth(int topWidth,int rightWidth){
		setWidth(topWidth, rightWidth, topWidth, rightWidth);
	}
	
	public void setWidth(int topWidth,int rightWidth,int bottomWidth){
		setWidth(topWidth, rightWidth, bottomWidth, rightWidth);
	}
	
	public void setWidth(int topWidth,int rightWidth,int bottomWidth,int leftWidth){
		setLeftWidth(leftWidth);
		setRightWidth(rightWidth);
		setTopWidth(topWidth);
		setBottomWidth(bottomWidth);
	}
	
	public void setCorol(int topCorol){
		setCorol(topCorol, topCorol, topCorol, topCorol);
	}
	
	public void setCorol(int topCorol,int rightCorol){
		setCorol(topCorol, rightCorol, topCorol, rightCorol);
	}
	
	public void setCorol(int topCorol,int rightCorol,int bottomCorol){
		setCorol(topCorol, rightCorol, bottomCorol, rightCorol);
	}
	
	public void setCorol(int topCorol,int rightCorol,int bottomCorol,int leftCorol){
		setLeftCorol(leftCorol);
		setRightCorol(rightCorol);
		setTopCorol(topCorol);
		setBottomCorol(bottomCorol);
	}
	
	public final void draw(Canvas canvas, final StyleText styleText, final int start,
			final int end, int left, int top, int right, int bottom, Paint paint){
		int leftX = left + leftWidth / 2;
		draw(canvas, leftX, top, leftX, bottom, leftCorol, leftType,leftWidth, paint);
		int rightX = right - rightWidth / 2;
		draw(canvas, rightX, top, rightX, bottom, rightCorol, rightType,rightWidth,paint);
		if(styleText.getStart() == start){
			int topY = top + topWidth/ 2;
			draw(canvas, left, topY, right, topY, topCorol, topType,topWidth, paint);
		}
		if(styleText.getEnd() == end){
			int bottomY = bottom - bottomWidth/ 2;
			draw(canvas, left, bottomY, right, bottomY, bottomCorol, bottomType,bottomWidth, paint);
		}
	}
	
	private void draw(Canvas canvas,int left, int top, int right, int bottom,int color,int type,int width, Paint paint){
		if(type == TYPE_NONE){
			return;
		}
		float oldStrokeWidth = paint.getStrokeWidth();
		if(type == TYPE_SOLID){
			paint.setColor(color);
			paint.setStrokeWidth(width);
			canvas.drawLine(left, top, right, bottom, paint);
		}else if(type == TYPE_DASHED){
			if(mDashedEffect == null){
				mDashedEffect = new DashPathEffect(new float[]{10, 5, 5, 5}, 0);
			}
			paint.setPathEffect(mDashedEffect);
			paint.setColor(color);
			paint.setStrokeWidth(width);
			canvas.drawLine(left, top, right, bottom, paint);
		}else if(type == TYPE_DOTTED){
			if(mDottedEffect == null){
				mDottedEffect = new DashPathEffect(new float[]{5, 5}, 0);
			}
			paint.setPathEffect(mDottedEffect);
			paint.setColor(color);
			paint.setStrokeWidth(width);
			canvas.drawLine(left, top, right, bottom, paint);
		}
		paint.setPathEffect(null);
		paint.setStrokeWidth(oldStrokeWidth);
	}
	/**
	 * @return the leftWidth
	 */
	public int getLeftWidth() {
		if(leftType == TYPE_NONE){
			return 0;
		}
		return leftWidth;
	}
	/**
	 * @param leftWidth the leftWidth to set
	 */
	public void setLeftWidth(int leftWidth) {
		this.leftWidth = leftWidth;
	}
	/**
	 * @return the rightWidth
	 */
	public int getRightWidth() {
		if(rightType == TYPE_NONE){
			return 0;
		}
		return rightWidth;
	}
	/**
	 * @param rightWidth the rightWidth to set
	 */
	public void setRightWidth(int rightWidth) {
		this.rightWidth = rightWidth;
	}
	/**
	 * @return the topWidth
	 */
	public int getTopWidth() {
		if(topType == TYPE_NONE){
			return 0;
		}
		return topWidth;
	}
	/**
	 * @param topWidth the topWidth to set
	 */
	public void setTopWidth(int topWidth) {
		this.topWidth = topWidth;
	}
	/**
	 * @return the bottomWidth
	 */
	public int getBottomWidth() {
		if(bottomType == TYPE_NONE){
			return 0;
		}
		return bottomWidth;
	}
	/**
	 * @param bottomWidth the bottomWidth to set
	 */
	public void setBottomWidth(int bottomWidth) {
		this.bottomWidth = bottomWidth;
	}
	/**
	 * @return the leftType
	 */
	public int getLeftType() {
		return leftType;
	}
	/**
	 * @param leftType the leftType to set
	 */
	public void setLeftType(int leftType) {
		this.leftType = leftType;
	}
	/**
	 * @return the rightType
	 */
	public int getRightType() {
		return rightType;
	}
	/**
	 * @param rightType the rightType to set
	 */
	public void setRightType(int rightType) {
		this.rightType = rightType;
	}
	/**
	 * @return the topType
	 */
	public int getTopType() {
		return topType;
	}
	/**
	 * @param topType the topType to set
	 */
	public void setTopType(int topType) {
		this.topType = topType;
	}
	/**
	 * @return the bottomType
	 */
	public int getBottomType() {
		return bottomType;
	}
	/**
	 * @param bottomType the bottomType to set
	 */
	public void setBottomType(int bottomType) {
		this.bottomType = bottomType;
	}
	/**
	 * @return the leftType
	 */
	public int getLeftCorol() {
		return leftCorol;
	}
	/**
	 * @param leftCorol the leftCorol to set
	 */
	public void setLeftCorol(int leftCorol) {
		this.leftCorol = leftCorol;
	}
	/**
	 * @return the rightCorol
	 */
	public int getRightCorol() {
		return rightCorol;
	}
	/**
	 * @param rightCorol the rightCorol to set
	 */
	public void setRightCorol(int rightCorol) {
		this.rightCorol = rightCorol;
	}
	/**
	 * @return the topCorol
	 */
	public int getTopCorol() {
		return topCorol;
	}
	/**
	 * @param topCorol the topCorol to set
	 */
	public void setTopCorol(int topCorol) {
		this.topCorol = topCorol;
	}
	/**
	 * @return the bottomCorol
	 */
	public int getBottomCorol() {
		return bottomCorol;
	}
	/**
	 * @param bottomCorol the bottomCorol to set
	 */
	public void setBottomCorol(int bottomCorol) {
		this.bottomCorol = bottomCorol;
	}
	
	public static int parseType(String str){
		int type = TYPE_NONE;
		if(!TextUtils.isEmpty(str)){
			if(str.equalsIgnoreCase("dotted")){
				type = TYPE_DOTTED;
			}else if(str.equalsIgnoreCase("dashed")){
				type = TYPE_DASHED;
			}else if(str.equalsIgnoreCase("solid")){
				type = TYPE_SOLID;
			}
		}
		return type;
	}
}
