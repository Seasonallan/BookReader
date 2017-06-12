package com.lectek.android.lereader.animation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;
/**	打开书本切换动画
 * @author linyiwei
 * @email 21551594@qq.com
 * @date 2011-12-01
 */
public class OpenBookAnim extends Animation {
	public static final int START_TYPE_DEFAULT = 0;
	public static final int START_TYPE_INVERSE = 1;
	private static final float MAX_ROTATE_Y = 135;
	private int mStartType = START_TYPE_DEFAULT;
	private boolean isTop = false;
	private float mScaleCenterX;
	private float mScaleCenterY;
	private float mScaleX;
	private float mScaleY;
	private int mLocationX;
	private int mLocationY;
	private int[] mCanBackLocation;
	private int mWidth;
	private int mHeight;
	private int mMaxWidth;
	private int mMaxHeight;
	private Camera mCamera = new Camera();
	public OpenBookAnim(int x,int y,int width , int height,int maxWidth,int maxHeigh,boolean istop){
		isTop = istop;
		mLocationX = x;
		mLocationY = y;
		mCanBackLocation = new int []{0,0};
		initialize( x, y, width ,  height, maxWidth, maxHeigh);
	}
	public void setAnimStartType(int startType){
		mStartType = startType;
	}
	public void initialize(int startType,int location[] ){
		mStartType = startType;
		if(location != null){
			mCanBackLocation[0] = location[0] - mLocationX;
			mCanBackLocation[1] = location[1] - mLocationY;
		}
	};
	private void initialize(int x,int y,int width , int height,int maxWidth,int maxHeigh){
		if( y + height > maxHeigh){
			height -=  y + height - maxHeigh;
		}
		mWidth = width;
		mHeight = height;
		mMaxWidth = maxWidth;
		mMaxHeight = maxHeigh;
		mScaleX = maxWidth  * 1f / width;
		mScaleY = maxHeigh * 1f / height;
		mScaleCenterX = x / ( maxWidth - width - 0f) * width;
		mScaleCenterY = y / ( maxHeigh - height - 0f) * height;
	}
	public int getAnimStartType(){
		return mStartType;
	}
	/**
	 * 这个方法会不断循环回调
	 * float interpolatedTime 0 ~ 1 的数字代表了当前要绘制的画面对应的进度是百分多少。
	 */
	@Override
	protected void applyTransformation(float interpolatedTime,
			Transformation t) {
		final Matrix matrix = t.getMatrix();
		float progress;
		
		progress = computeProgress(interpolatedTime);
		mCamera.save();
		if(isTop){
			//执行旋转
			mCamera.rotateY(computeRotate(progress));
		}
		mCamera.getMatrix(matrix);
		//执行缩放
		setScale(progress,matrix);
		
		matrix.preTranslate(0 , -mHeight/2);   
	    matrix.postTranslate(0, mHeight/2);   
	    matrix.postTranslate(interpolatedTime * mCanBackLocation[0],interpolatedTime * mCanBackLocation[1]);
		mCamera.restore();
	}
	private float computeProgress(float interpolatedTime) {
		float progress = interpolatedTime;
		if(mStartType == START_TYPE_INVERSE){
			progress = (1 - interpolatedTime);
		}
		return progress;
	}
	private float computeRotate(float progress){
		return -MAX_ROTATE_Y * progress;
	}
	private void setScale(float progress,Matrix matrix){
		
		matrix.postScale(1 + ( mScaleX - 1 )* progress, 1 + ( mScaleY - 1 ) * progress, mScaleCenterX, mScaleCenterY - mHeight/2 );
	}
}
