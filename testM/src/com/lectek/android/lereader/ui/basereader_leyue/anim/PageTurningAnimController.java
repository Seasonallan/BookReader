package com.lectek.android.lereader.ui.basereader_leyue.anim;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.widget.Scroller;

public class PageTurningAnimController extends AbsHorGestureAnimController {
	private Integer mFromIndex;
	private Integer mToIndex;
	private int mCornerX = 0; // 拖拽点对应的页脚
	private int mCornerY = 0;
	private Path mPath0;
	private Path mPath1;
	private PointF mTouch = new PointF(); // 拖拽点
	/**
	 * 贝塞尔曲线起始点
	 */
	private PointF mBezierStart1 = new PointF(); // 
	/**
	 * 贝塞尔曲线控制点
	 */
	private PointF mBezierControl1 = new PointF(); // 
	/**
	 * 贝塞尔曲线顶点
	 */
	private PointF mBeziervertex1 = new PointF(); // 
	/**
	 * 贝塞尔曲线结束点
	 */
	private PointF mBezierEnd1 = new PointF(); // 
	/**
	 * 贝塞尔曲线起始点2
	 */
	private PointF mBezierStart2 = new PointF(); // 
	/**
	 * 贝塞尔曲线控制点2
	 */
	private PointF mBezierControl2 = new PointF();
	/**
	 * 贝塞尔曲线顶点2
	 */
	private PointF mBeziervertex2 = new PointF();
	/**
	 * 贝塞尔曲线结束点2
	 */
	private PointF mBezierEnd2 = new PointF();

	private float mMiddleX;
	private float mMiddleY;
	private float mDegrees;
	private float mTouchToCornerDis;
	private Matrix mMatrix;
	private float[] mMatrixArray = { 0, 0, 0, 0, 0, 0, 0, 0, 1.0f };

	private boolean mIsRTandLB = false; // 是否属于右上左下
	private float mMaxLength;
	private int[] mBackShadowColors;
	private int[] mFrontShadowColors;
	private GradientDrawable mBackShadowDrawableLR;
	private GradientDrawable mBackShadowDrawableRL;
	private GradientDrawable mFolderShadowDrawableLR;
	private GradientDrawable mFolderShadowDrawableRL;

	private GradientDrawable mFrontShadowDrawableHBT;
	private GradientDrawable mFrontShadowDrawableHTB;
	private GradientDrawable mFrontShadowDrawableVLR;
	private GradientDrawable mFrontShadowDrawableVRL;
	private boolean isLandscape = false;
	private int maxShadow = 30;
	private float mRightPageStartX = 0;
	private boolean isCenterTouchAnim;
	
	PageTurningAnimController(Context context) {
		super(context);
		createDrawable();
		mPath0 = new Path();
		mPath1 = new Path();
		mMatrix = new Matrix();
	}

	@Override
	protected void setScroller(Scroller scroller, boolean isRequestNext,
			boolean isCancelAnim, PageCarver pageCarver) {
		int dx, dy;
		// dx 水平方向滑动的距离，负值会使滚动向左滚动
		// dy 垂直方向滑动的距离，负值会使滚动向上滚动
		mTouch.set(mLastTouchPoint);
		calcPoints(!isRequestNext);
		int duration = DURATION;
		if(isCancelAnim){
			if(isLandscape){
				if(isRequestNext){
					dx = (int) (mScreenWidth - mTouch.x);
				}else{
					dx = (int) - mTouch.x;
				}
				if (mCornerY > 0) {
					dy = (int) ((mScreenHeight - mTouch.y) * 2f);
				} else {
					dy = (int) ((0.01f - mTouch.y) * 2f); // 防止mTouch.y最终变为0
				}
			}else{
				if(isRequestNext){
					dx = (int) (mScreenWidth - mTouch.x);
				}else{
					dx = (int) - mTouch.x - mScreenWidth;
					duration = DURATION * 2;
				}
				if (mCornerY > 0) {
					dy = (int) (mScreenHeight - mTouch.y);
				} else {
					dy = (int) (1 - mTouch.y); // 防止mTouch.y最终变为0
				}
			}
			scroller.startScroll((int) mTouch.x, (int) mTouch.y, dx, dy,duration);
		}else{
			duration = DURATION * 2;
			if(isLandscape){
				if(!isRequestNext){
					dx = (int) (mScreenWidth - mTouch.x);
				}else{
					dx = (int) - mTouch.x;
				}
				if (mCornerY > 0) {
					dy = (int) ((mScreenHeight - mTouch.y) * 2f);
				} else {
					dy = (int) ((0.01f - mTouch.y) * 2f); // 防止mTouch.y最终变为0
				}
			}else{
				if (isRequestNext) {
					dx = -(int) (mScreenWidth + mTouch.x);
				} else {
					dx = (int) (mScreenWidth - mTouch.x + mScreenWidth);
				}
				if (mCornerY > 0) {
					dy = (int) (mScreenHeight - mTouch.y);
				} else {
					dy = (int) (1 - mTouch.y); // 防止mTouch.y最终变为0
				}
			}
			scroller.startScroll((int) mTouch.x, (int) mTouch.y, dx, dy,duration);
		}
	}

	@Override
	protected void onMeasure(PageCarver pageCarver) {
		super.onMeasure(pageCarver);
		mMaxLength = (float) Math.hypot(mContentWidth, mContentHeight);
		mRightPageStartX = mScreenWidth;
	}

	@Override
	protected void onAnimStart(boolean isCancelAnim) {
	}

	@Override
	protected void onAnimEnd(boolean isCancelAnim) {
		mFromIndex = null;
		mToIndex = null;
		isCenterTouchAnim = false;
	}

	@Override
	protected void setDefaultTouchPoint(boolean isNext) {
		if(isNext){
			mDownTouchPoint.x = mContentWidth / 4 * 3;
		}else{
			mDownTouchPoint.x = mContentWidth / 4;
		}
		mDownTouchPoint.y = mContentHeight / 8 * 7;
		mLastTouchPoint.set(mDownTouchPoint);
	}

	@Override
	protected void onRequestPage(boolean isRequestNext,int fromIndex,int toIndex,float x,float y) {
		if(y > mScreenHeight / 3f && y < mScreenHeight / 3f * 2 || !isRequestNext){
			isCenterTouchAnim = true;
			y = mScreenHeight;
		}
		mFromIndex = fromIndex;
		mToIndex = toIndex;
		calcCornerXY(y, false);
	}
	
	@Override
	public void dispatchTouchEvent(MotionEvent event, PageCarver pageCarver) {
		if(isCenterTouchAnim && event.getAction() != MotionEvent.ACTION_DOWN){
			event.setLocation(event.getX(), mScreenHeight);
		}
		super.dispatchTouchEvent(event, pageCarver);
	}

	@Override
	protected void onDrawAnim(Canvas canvas,boolean isCancelAnim, boolean isNext, PageCarver pageCarver) {
		if(isCenterTouchAnim){
			mLastTouchPoint.y = mScreenHeight;
		}
		mTouch.set(mLastTouchPoint);
		int fromIndex = mFromIndex;
		int toIndex = mToIndex;
		if(!isNext){
			fromIndex = mToIndex;
			toIndex = mFromIndex;
			mTouch.x -= mScreenWidth / 4;
		}
		calcPoints(!isNext);
		mPath0.reset();
		mPath0.moveTo(filterBezierStartBound(mBezierStart1,!isNext), mBezierStart1.y);
		mPath0.quadTo(mBezierControl1.x, mBezierControl1.y, mBezierEnd1.x,
				mBezierEnd1.y);
		mPath0.lineTo(mTouch.x, mTouch.y);
		mPath0.lineTo(mBezierEnd2.x, mBezierEnd2.y);
		mPath0.quadTo(mBezierControl2.x, mBezierControl2.y, mBezierStart2.x,
				mBezierStart2.y);
		mPath0.lineTo(mCornerX, mCornerY);
		mPath0.close();
		drawCurrentPageArea(canvas, fromIndex,true,pageCarver);
//		drawCurrentPageArea(canvas, rightPageCurBitmap, false);
		mDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl1.x
				- mCornerX, mBezierControl2.y - mCornerY));
		// 绘制翻起页背面//第三个参数表示是否绘制在左边
		drawCurrentBackArea(canvas, fromIndex,!isNext,pageCarver);
		// 绘制下一页//第三个参数表示是否绘制在左边
		drawNextPageAreaAndShadow(canvas, toIndex,!isNext,pageCarver);
		// 绘制翻起页的阴影
		drawCurrentPageShadow(canvas,!isNext);
		// //自动播放的动画结束时，进行的操作
	}
	
	/**
	 * 绘制翻起页背面
	 */
	private final void drawCurrentBackArea(Canvas canvas,int pageIndex,boolean isLeftPage, PageCarver pageCarver) {
		int i = (int) (filterBezierStartBound(mBezierStart1,isLeftPage) + mBezierControl1.x) / 2;
		float f1 = Math.abs(i - mBezierControl1.x);
		int i1 = (int) (mBezierStart2.y + mBezierControl2.y) / 2;
		float f2 = Math.abs(i1 - mBezierControl2.y);
		float f3 = Math.min(f1, f2);
		mPath1.reset();
		mPath1.moveTo(mBeziervertex2.x, mBeziervertex2.y);
		
		if(isBezierStartOut(isLeftPage) && isLandscape)
		mPath1.lineTo(filterBezierControlBound(mBezierControl1,isLeftPage), mBezierControl1.y);
		else
		mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
		
		mPath1.lineTo(mBezierEnd1.x, mBezierEnd1.y);
		mPath1.lineTo(mTouch.x, mTouch.y);
		mPath1.lineTo(mBezierEnd2.x, mBezierEnd2.y);
		
		mPath1.close();
		GradientDrawable mFolderShadowDrawable;
		int left;
		int right;
		if (mIsRTandLB) {
			left = (int) (filterBezierStartBound(mBezierStart1,isLeftPage) - maxShadow);
			right = (int) (filterBezierStartBound(mBezierStart1,isLeftPage) + f3 + 2);
			mFolderShadowDrawable = mFolderShadowDrawableLR;
		} else {
			left = (int) (filterBezierStartBound(mBezierStart1,isLeftPage) - f3 - 2);
			right =(int) (filterBezierStartBound(mBezierStart1,isLeftPage) + maxShadow + 2);
			mFolderShadowDrawable = mFolderShadowDrawableRL;
		}
		canvas.save();
		canvas.clipPath(mPath0);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
//		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
		float dis = (float) Math.hypot(mCornerX - mBezierControl1.x,
				mBezierControl2.y - mCornerY);
		float f8 = (mCornerX - mBezierControl1.x) / dis;
		float f9 = (mBezierControl2.y - mCornerY) / dis;
		mMatrixArray[0] = 1 - 2 * f9 * f9;
		mMatrixArray[1] = 2 * f8 * f9;
		mMatrixArray[3] = mMatrixArray[1];
		mMatrixArray[4] = 1 - 2 * f8 * f8;
		mMatrix.reset();
		mMatrix.setValues(mMatrixArray);
		mMatrix.preTranslate(-mBezierControl1.x, -mBezierControl1.y);
		mMatrix.postTranslate(mBezierControl1.x, mBezierControl1.y);
		
		if(!isLeftPage && isLandscape){
			float x = Math.abs(mTouch.x - mBezierControl1.x);
			float y = Math.abs(mTouch.y - mBezierControl1.y);
			float z = (float) Math.hypot(x,y);
			if( mTouch.x <= mBezierControl1.x && mTouch.y >= mBezierControl2.y){
				mMatrix.postTranslate(- (mRightPageStartX)*(x/z), - (mRightPageStartX)*(y/z));
			}else if(mTouch.x >= mBezierControl1.x && mTouch.y >= mBezierControl2.y ){
				mMatrix.postTranslate( (mRightPageStartX)*(x/z), (mRightPageStartX)*(y/z));
			}else if(mTouch.x < mBezierControl1.x && mTouch.y < mBezierControl2.y){
				mMatrix.postTranslate( -(mRightPageStartX)*(x/z), (mRightPageStartX)*(y/z));
			}else if(mTouch.x > mBezierControl1.x && mTouch.y < mBezierControl2.y){
				mMatrix.postTranslate( (mRightPageStartX)*(x/z), -(mRightPageStartX)*(y/z));
			}
		}
		int bgColor = pageCarver.getPageBackgroundColor();
		if(!isLandscape){
			canvas.drawColor(bgColor);
		}
		canvas.save();
		canvas.concat(mMatrix);
		if(isLandscape){
			canvas.scale(-1, 1,mHalfContentWidth,mHalfContentHeight);
		}
		pageCarver.drawPage(canvas, pageIndex);
		canvas.restore();
		if(!isLandscape){
			canvas.drawColor(bgColor + 0xaa000000);
		}
		canvas.rotate(mDegrees, filterBezierStartBound(mBezierStart1,isLeftPage), mBezierStart1.y);
		mFolderShadowDrawable.setBounds(left, (int) mBezierStart1.y, right,
				(int) (mBezierStart1.y + mMaxLength));
		mFolderShadowDrawable.draw(canvas);
		canvas.restore();
	}
	
	private final void drawNextPageAreaAndShadow(Canvas canvas,int pageIndex,boolean isLeftPage, PageCarver pageCarver) {
		if(!isLandscape)isLeftPage = true;
		int leftx;
		int rightx;
		GradientDrawable mBackShadowDrawable;
		GradientDrawable mTempShadowDrawableLR = null;
		GradientDrawable mTempShadowDrawableRL = null;
		float touchToCornerDis = 0;
		if(isBezierStartOut(isLeftPage) && isLandscape){
			touchToCornerDis = maxShadow;
			mTempShadowDrawableLR = mFolderShadowDrawableRL;
			mTempShadowDrawableRL = mFolderShadowDrawableLR;
		}else{
			touchToCornerDis = mTouchToCornerDis / 4;
			mTempShadowDrawableLR = mBackShadowDrawableLR;
			mTempShadowDrawableRL = mBackShadowDrawableRL;
		}
		
		if (mIsRTandLB) {
			leftx = (int) (filterBezierStartBound(mBezierStart1,isLeftPage));
			rightx = (int) (filterBezierStartBound(mBezierStart1,isLeftPage) + touchToCornerDis);
			mBackShadowDrawable = mTempShadowDrawableLR;
		} else {
			leftx = (int) (filterBezierStartBound(mBezierStart1,isLeftPage) - touchToCornerDis);
			rightx = (int) filterBezierStartBound(mBezierStart1,isLeftPage);
			mBackShadowDrawable = mTempShadowDrawableRL;
		}
		
		canvas.save();
		canvas.clipPath(mPath0);
		canvas.clipPath(mPath1, Region.Op.DIFFERENCE);
//		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
		if(isLeftPage){
			pageCarver.drawPage(canvas, pageIndex);
		}else{
			canvas.save();
			canvas.translate(-mRightPageStartX, 0);
			pageCarver.drawPage(canvas, pageIndex);
			canvas.restore();
		}
		canvas.rotate(mDegrees, filterBezierStartBound(mBezierStart1,isLeftPage), mBezierStart1.y);
		mBackShadowDrawable.setBounds(leftx, (int) mBezierStart1.y, rightx,
				(int) (mMaxLength + mBezierStart1.y));
		mBackShadowDrawable.draw(canvas);
		canvas.restore();
	}
	/**
	 * 绘制翻起页的阴影
	 */
	private final void drawCurrentPageShadow(Canvas canvas,boolean isLeftPage) {
		double degree;
		if (mIsRTandLB) {
			degree = Math.PI
					/ 4
					- Math.atan2(mBezierControl1.y - mTouch.y, mTouch.x
							- mBezierControl1.x);
		} else {
			degree = Math.PI
					/ 4
					- Math.atan2(mTouch.y - mBezierControl1.y, mTouch.x
							- mBezierControl1.x);
		}
		// 翻起页阴影顶点与touch点的距离
		double d1 = 25 * 1.414 * Math.cos(degree);
		double d2 = 25 * 1.414 * Math.sin(degree);
		float x = (float) (mTouch.x + d1);
		float y;
		if (mIsRTandLB) {
			y = (float) (mTouch.y + d2);
		} else {
			y = (float) (mTouch.y - d2);
		}
		mPath1.reset();
		mPath1.moveTo(x, y);
		mPath1.lineTo(mTouch.x, mTouch.y);
		mPath1.lineTo(mBezierControl1.x, mBezierControl1.y);
		mPath1.lineTo(filterBezierStartBound(mBezierStart1,isLeftPage), mBezierStart1.y);
		mPath1.close();
		float rotateDegrees;
		canvas.save();

		canvas.clipPath(mPath0, Region.Op.DIFFERENCE);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
		int leftx;
		int rightx;
		GradientDrawable mCurrentPageShadow;
		//全屏翻页，边缘阴影宽度渐变
//		int shadowWidth = (int) (25 * (isLeftPage 
//				? ( mTouch.x / mContentWidth) : ( (mContentWidth - mTouch.x) / mContentWidth) ) );
		int shadowWidth = (int) (25 * (false 
				? ( mTouch.x / mContentWidth) : ( (mContentWidth - mTouch.x) / mContentWidth) ) );
		if (mIsRTandLB) {
			leftx = (int) (mBezierControl1.x - 1);
			rightx = (int) mBezierControl1.x + shadowWidth;
			mCurrentPageShadow = mFrontShadowDrawableVLR;
		} else {
			leftx = (int) (mBezierControl1.x - shadowWidth);
			rightx = (int) mBezierControl1.x + 1;
			mCurrentPageShadow = mFrontShadowDrawableVRL;
		}

		rotateDegrees = (float) Math.toDegrees(Math.atan2(mTouch.x
				- mBezierControl1.x, mBezierControl1.y - mTouch.y));
		canvas.rotate(rotateDegrees, mBezierControl1.x, mBezierControl1.y);
		mCurrentPageShadow.setBounds(leftx,
				(int) (mBezierControl1.y - mMaxLength), rightx,
				(int) (mBezierControl1.y) +  mContentHeight );
		mCurrentPageShadow.draw(canvas);
		canvas.restore();

		mPath1.reset();
		mPath1.moveTo(x, y);
		mPath1.lineTo(mTouch.x, mTouch.y);
		mPath1.lineTo(mBezierControl2.x, mBezierControl2.y);
		mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
		mPath1.close();
		canvas.save();
		canvas.clipPath(mPath0, Region.Op.DIFFERENCE);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
		if (mIsRTandLB) {
			leftx = (int) (mBezierControl2.y - 1);
			rightx = (int) (mBezierControl2.y + shadowWidth);
			mCurrentPageShadow = mFrontShadowDrawableHTB;
		} else {
			leftx = (int) (mBezierControl2.y - shadowWidth);
			rightx = (int) (mBezierControl2.y + 1);
			mCurrentPageShadow = mFrontShadowDrawableHBT;
		}
		rotateDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl2.y
				- mTouch.y, mBezierControl2.x - mTouch.x));
		canvas.rotate(rotateDegrees, mBezierControl2.x, mBezierControl2.y);
		float temp;
		if (mBezierControl2.y < 0)
			temp = mBezierControl2.y - mContentHeight;
		else
			temp = mBezierControl2.y;

		int hmg = (int) Math.hypot(mBezierControl2.x, temp);
		if (hmg > mMaxLength)
			mCurrentPageShadow
					.setBounds((int) (mBezierControl2.x - 25) - hmg, leftx,
							(int) (mBezierControl2.x + mMaxLength) - hmg,
							rightx);
		else
			mCurrentPageShadow.setBounds(
					(int) (mBezierControl2.x - mMaxLength), leftx,
					(int) (mBezierControl2.x  +  mContentHeight ), rightx );

		mCurrentPageShadow.draw(canvas);
		canvas.restore();
	}
	
	private final void drawCurrentPageArea(Canvas canvas,int pageIndex,boolean isLeftPage, PageCarver pageCarver) {
		canvas.save();
		if(isLeftPage){
			pageCarver.drawPage(canvas, pageIndex);
		}else{
			canvas.translate(-mRightPageStartX, 0);
			pageCarver.drawPage(canvas, pageIndex);
		}
		canvas.restore();
	}
	
	private final float filterBezierStartBound(PointF pointF,boolean isLeftPage){
		if(( mBezierStart1.x > mRightPageStartX && isLeftPage || mBezierStart1.x < mRightPageStartX && !isLeftPage) && isAnimStart && isLandscape){
			return mRightPageStartX;
		}
		return pointF.x;
	}
	
	private final float filterBezierControlBound(PointF pointF,boolean isLeftPage){
		if(!isLandscape)return pointF.x;
		int i = (int) (filterBezierStartBound(mBezierStart1,isLeftPage) + mBezierControl1.x) / 2;
		float f1 = Math.abs(i - mBezierControl1.x);
		if(isLeftPage){
			return pointF.x + f1;
		}else{
			return pointF.x - f1;
		}
	}
	/**
	 * 约束触屏点可移动范围
	 * @param pointF
	 */
	private final void filterTouchBound(PointF pointF){
		if(pointF.y == pointF.x){
			pointF.y -= 0.1f;
		}else if(mScreenWidth - pointF.y == pointF.x){
			pointF.y -= 0.1f;
		}else if(mScreenWidth - pointF.x == pointF.y){
			pointF.y -= 0.1f;
		}else if(mScreenWidth - pointF.x == mScreenHeight - pointF.y){
			pointF.y -= 0.1f;
		}
		if(pointF.y <= 0){
			if(!isAnimStart){
				pointF.y = 0.25f;
			}else{
				pointF.y = 0.25f;
			}
		}
		if(pointF.y >= mScreenHeight){			
			if(!isAnimStart){
				pointF.y = mScreenHeight - 0.25f;
			}else{
				pointF.y = mScreenHeight - 0.25f;
			}
		}
		if(isLandscape){
			if(pointF.x >= mScreenWidth){
				pointF.x = mScreenWidth - 0.1f;
			}
			if(pointF.x <= 0){
				pointF.x = 0.1f;
			}
			float length = 0;
			
			if(mCornerY > mScreenHeight/2){
				length = PointF.length(pointF.x - mRightPageStartX, pointF.y - mScreenHeight);
				if(length > mRightPageStartX + 1){
					pointF.x = pointF.x - mRightPageStartX;
					pointF.y = pointF.y - mScreenHeight;
					pointF.x = (mRightPageStartX)/length*pointF.x -1;
					pointF.y = (mRightPageStartX)/length*pointF.y -1 ;
					pointF.x = pointF.x + mRightPageStartX;
					pointF.y = pointF.y + mScreenHeight;
				}
			}else{
				length = PointF.length(pointF.x - mRightPageStartX, pointF.y);
				if(length > mRightPageStartX + 1){
					pointF.x = pointF.x - mRightPageStartX;
					pointF.x = (mRightPageStartX)/length*pointF.x - 1;
					pointF.y = (mRightPageStartX)/length*pointF.y - 1;
					pointF.x = pointF.x + mRightPageStartX;
				}
			}
		}
	}
	/**
	 * 各个关键点的计算 Author : hmg25 Version: 1.0 Description :
	 */
	private final void calcPoints(boolean isLeftPage) {
		// 约束触屏点的可移动范围
		filterTouchBound(mTouch);
		mMiddleX = (mTouch.x + mCornerX) / 2;
		mMiddleY = (mTouch.y + mCornerY) / 2;

		mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
				* (mCornerY - mMiddleY) / (mCornerX - mMiddleX);

		mBezierControl1.y = mCornerY;
		mBezierControl2.x = mCornerX;
		mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
				* (mCornerX - mMiddleX) / (mCornerY - mMiddleY);

		mBezierStart1.x = mBezierControl1.x - (mCornerX - mBezierControl1.x)
				/ 2;
		mBezierStart1.y = mCornerY;
		// 在触屏移动时做此约束
		if (!isAnimStart || (!isLandscape && mTouch.x > 0 && mTouch.x < mScreenWidth)) {
			if (isBezierStartOut(isLeftPage)) {
				if (!isLeftPage && isLandscape) {
					mBezierStart1.x -= mRightPageStartX;
				}
				if (mBezierStart1.x < 0)
					mBezierStart1.x = mRightPageStartX - mBezierStart1.x;
				float f1 = Math.abs(mCornerX - mTouch.x);
				float f2 = mRightPageStartX * f1 / mBezierStart1.x;
				mTouch.x = Math.abs(mCornerX - f2);

				float f3 = Math.abs(mCornerX - mTouch.x)
						* Math.abs(mCornerY - mTouch.y) / f1;
				mTouch.y = Math.abs(mCornerY - f3);

				mMiddleX = (mTouch.x + mCornerX) / 2;
				mMiddleY = (mTouch.y + mCornerY) / 2;

				mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
						* (mCornerY - mMiddleY) / (mCornerX - mMiddleX);
				mBezierControl1.y = mCornerY;

				mBezierControl2.x = mCornerX;
				mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
						* (mCornerX - mMiddleX) / (mCornerY - mMiddleY);
				mBezierStart1.x = mBezierControl1.x
						- (mCornerX - mBezierControl1.x) / 2;
			}
		}
		mBezierStart2.y = mBezierControl2.y - (mCornerY - mBezierControl2.y)
				/ 2;
		mBezierStart2.x = mCornerX;
		calcTouchToCornerDis();
		mBezierEnd1 = getCross(mTouch, mBezierControl1, mBezierStart1,
				mBezierStart2);
		mBezierEnd2 = getCross(mTouch, mBezierControl2, mBezierStart1,
				mBezierStart2);
		mBeziervertex1.x = (mBezierStart1.x + 2 * mBezierControl1.x + mBezierEnd1.x) / 4;
		mBeziervertex1.y = (2 * mBezierControl1.y + mBezierStart1.y + mBezierEnd1.y) / 4;
		mBeziervertex2.x = (mBezierStart2.x + 2 * mBezierControl2.x + mBezierEnd2.x) / 4;
		mBeziervertex2.y = (2 * mBezierControl2.y + mBezierStart2.y + mBezierEnd2.y) / 4;
	}

	private final void calcTouchToCornerDis() {
		mTouchToCornerDis = (float) Math.hypot((mTouch.x - mCornerX),
				(mTouch.y - mCornerY));
	}

	/**
	 * 求解直线P1P2和直线P3P4的交点坐标
	 */
	private final PointF getCross(PointF P1, PointF P2, PointF P3, PointF P4) {
		PointF CrossP = new PointF();
		// 二元函数通式： y=ax+b
		float a1 = (P2.y - P1.y) / (P2.x - P1.x);
		float b1 = ((P1.x * P2.y) - (P2.x * P1.y)) / (P1.x - P2.x);

		float a2 = (P4.y - P3.y) / (P4.x - P3.x);
		float b2 = ((P3.x * P4.y) - (P4.x * P3.y)) / (P3.x - P4.x);
		CrossP.x = (b2 - b1) / (a1 - a2);
		CrossP.y = a1 * CrossP.x + b1;
		return CrossP;
	}

	private final boolean isBezierStartOut(boolean isLeftPage) {
		if(isLandscape){
			if ( mBezierStart1.x <= mRightPageStartX - 2&& !isLeftPage 
					|| mBezierStart1.x >= mRightPageStartX + 2&& isLeftPage ) {
				return true;
			}
			return false;
		}else{
			if (mBezierStart1.x < 0 || mBezierStart1.x > mScreenWidth) {
				return true;
			}
			return false;
		}
	}
	
	/**
	 *  计算拖拽点对应的拖拽脚 (以处理完)
	 */
	private final void calcCornerXY(float y,boolean isLeftPage) {
		//在左边翻页
		if (isLeftPage){
				mCornerX = 0;
		}else{//在右边翻页
			mCornerX = mScreenWidth;
		}
		if (y <= mScreenHeight / 2)
			mCornerY = 0;
		else
			mCornerY = mScreenHeight;
		if ((mCornerX == 0 && mCornerY == mScreenHeight)
				|| (mCornerX == mScreenWidth && mCornerY == 0))
			mIsRTandLB = true;
		else
			mIsRTandLB = false;
	}
	
	/**
	 *创建阴影的GradientDrawable
	 */
	private final void createDrawable() {
		int[] color = { 0x333333, 0x80333333 };
		mFolderShadowDrawableRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, color);
		mFolderShadowDrawableRL
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFolderShadowDrawableLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, color);
		mFolderShadowDrawableLR
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mBackShadowColors = new int[] { 0xd0111111, 0x111111 };
		mBackShadowDrawableRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, mBackShadowColors);
		mBackShadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mBackShadowDrawableLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors);
		mBackShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFrontShadowColors = new int[] { 0x80111111, 0x111111 };
		mFrontShadowDrawableVLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors);
		mFrontShadowDrawableVLR
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		mFrontShadowDrawableVRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, mFrontShadowColors);
		mFrontShadowDrawableVRL
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFrontShadowDrawableHTB = new GradientDrawable(
				GradientDrawable.Orientation.TOP_BOTTOM, mFrontShadowColors);
		mFrontShadowDrawableHTB
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFrontShadowDrawableHBT = new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP, mFrontShadowColors);
		mFrontShadowDrawableHBT
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);
	}
}
