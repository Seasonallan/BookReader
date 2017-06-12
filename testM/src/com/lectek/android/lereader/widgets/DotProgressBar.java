package com.lectek.android.lereader.widgets;

import com.lectek.android.LYReader.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


/**
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-5-18
 */
public class DotProgressBar extends View {

	private int totalNum;
	private int curIndex;
	private int spaceLen = 10;
	private int radius;
	private int noramlColor;
	private int focusColor;
	private Paint mPaint;
	private Bitmap normalPointBmp;
	private Bitmap checkPointBmp;
	private int viewHeight;

	public DotProgressBar(Context context) {
		super(context);
		init();
	}

	public DotProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.DotProgressBar);

		noramlColor = a.getColor(R.styleable.DotProgressBar_normalColor,
				Color.GRAY);
		focusColor = a.getColor(R.styleable.DotProgressBar_focusColor, Color
				.parseColor("#1497ad"));
		radius = a.getDimensionPixelOffset(
				R.styleable.DotProgressBar_dotRadius, 5);

		a.recycle();
		init();
	}

	public DotProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mPaint = new Paint();
		normalPointBmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_point_normal);
		checkPointBmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_point_checked);
		if (normalPointBmp != null) {
			viewHeight = normalPointBmp.getHeight();
		}
	}

	public void setPointBmp(int normalRes,int checkRes){
		normalPointBmp = BitmapFactory.decodeResource(getResources(),normalRes);
		checkPointBmp = BitmapFactory.decodeResource(getResources(),checkRes);
		if (normalPointBmp != null) {
			viewHeight = normalPointBmp.getHeight();
		}
		requestLayout();
	}
	
	public void setDotNum(int num) {
		if(totalNum == num){
			return;
		}
		this.totalNum = num;
		this.curIndex = 0;
		invalidate();
	}

	public void setCurIndex(int index) {
		if (index < totalNum && index > -1) {
			this.curIndex = index;
			invalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		int spaceNum = totalNum - 1;
		if (spaceNum < 0) {
			spaceNum = 0;
		}
		// System.out.println("dot progress bar width " + width);
		// System.out.println("dot progress bar width 2 " + getWidth());
		// System.out.println("dot progress bar left " + getLeft());
		int dotsLen = totalNum * (radius << 1) + spaceNum * spaceLen;
		float posX = 0 + ((width - dotsLen) >> 1) + radius;
		int tempLen = (radius << 1) + spaceLen;
		float cx = 0f;
		// float cy = 0 + (height >> 1);
		float cy = 0 + ((height - viewHeight) >> 1);
		// for (int i = 0; i < totalNum; ++i) {
		// cx = posX + i * tempLen;
		// if (i == curIndex) {
		// mPaint.setColor(focusColor);
		// } else {
		// mPaint.setColor(noramlColor);
		// }
		// canvas.drawCircle(cx, cy, radius, mPaint);
		// }
		Bitmap drawBmp;
		for (int i = 0; i < totalNum; ++i) {
			cx = posX + i * tempLen;
			if (i == curIndex) {
				drawBmp = checkPointBmp;
			} else {
				drawBmp = normalPointBmp;
			}
			if (drawBmp != null && !drawBmp.isRecycled()) {
				canvas.drawBitmap(drawBmp, cx, cy, mPaint);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			int spaceNum = totalNum - 1;
			if (spaceNum < 0) {
				spaceNum = 0;
			}
			result = totalNum * (radius << 1) + spaceNum * spaceLen;
			;
		}

		return result;
	}

	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = Math.max(specMode, viewHeight);
		}
		return result;
	}

}
