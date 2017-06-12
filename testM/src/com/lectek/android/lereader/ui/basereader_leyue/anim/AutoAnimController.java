package com.lectek.android.lereader.ui.basereader_leyue.anim;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;

import com.lectek.android.LYReader.R;

public class AutoAnimController extends PageAnimController{
	private Integer mFromIndex;
	private Integer mToIndex;
	private int mCurDelayed = 0;
	private long mToolbarAnimDelayed;
	private boolean isChangeSpeed = false;
	private long oldTime = 0;
	boolean isStop = false;
	boolean isPause = false;
	private float y;
	private Path mPath;
	private GradientDrawable mFolderShadowDrawableLR;
	private GradientDrawable mFolderShadowDrawableRL;

	AutoAnimController(Context context){
		super(context);
		int[] color = { 0x333333, 0x80333333 };
		mFolderShadowDrawableLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, color);
		mFolderShadowDrawableLR
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		mFolderShadowDrawableRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, color);
		mFolderShadowDrawableRL
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		mPath = new Path();
		isStop = true;
	}
	
	@Override
	public void dispatchTouchEvent(MotionEvent event, PageCarver pageCarver) {
		
	}

	@Override
	public boolean dispatchDrawPage(Canvas canvas, PageCarver pageCarver) {
		if(isStop){
			return false;
		}
		drawAutoStart(canvas, mFromIndex, true, pageCarver);
		drawAutoStart(canvas, mToIndex, false, pageCarver);
		if(isPause){
			return true;
		}
		int height = pageCarver.getContentHeight();
		y = height * computeProgress(height);
		if(mCurDelayed >= mToolbarAnimDelayed){
			pageCarver.onStopAnim(false);
			Integer requestPageIndex = pageCarver.requestNextPage();
			if(requestPageIndex != null){
				startAnim(pageCarver.getCurrentPageIndex(), requestPageIndex, true, pageCarver);
			}else{
				isStop = true;
				return false;
			}
		}else{
			pageCarver.requestInvalidate();
		}
		mCurDelayed += System.currentTimeMillis() - oldTime;
		oldTime = System.currentTimeMillis();
		return true;
	}

	@Override
	public void startAnim(int fromIndex, int toIndex, boolean isNext, PageCarver pageCarver) {
		mFromIndex = fromIndex;
		mToIndex = toIndex;
		isStop = false;
		mCurDelayed = 0;
		y = 0;
		oldTime = System.currentTimeMillis();
		pageCarver.onStartAnim(false);
		pageCarver.requestInvalidate();
	}
	
	private void drawAutoStart(Canvas canvas, int pageIndex,boolean isTop, PageCarver pageCarver){
		int width = pageCarver.getContentWidth();
		mPath.reset();
		mPath.moveTo( 0 , 0);
		mPath.lineTo( 0 , y);
		mPath.lineTo(width,y);
		mPath.lineTo(width,0);
		mPath.close();
		canvas.save();
		int left = 0;
		int right = 0;
		GradientDrawable mTempShadowDrawable = null;
		if(!isTop){
			mTempShadowDrawable = mFolderShadowDrawableLR;
			left = -15;
			right = 1;
			canvas.clipPath(mPath, Region.Op.INTERSECT);
		}else{
			mTempShadowDrawable = mFolderShadowDrawableRL;
			left = -1;
			right = 30;
			canvas.clipPath(mPath, Region.Op.DIFFERENCE);
		}
		pageCarver.drawPage(canvas, pageIndex);
		canvas.rotate(90,0,y);
		mTempShadowDrawable.setBounds(left, (int)(y - width - 2), right ,(int) y + 2);
		mTempShadowDrawable.draw(canvas);
		canvas.restore();
	}

	@Override
	public void stopAnim(PageCarver pageCarver) {
		if(!isStop){
			isStop = true;
			pageCarver.onStopAnim(false);
			pageCarver.requestInvalidate();
		}
	}

	@Override
	public boolean isAnimStop() {
		return isStop;
	}
	
	public void updateState(int autoStartDelayedType,boolean isPause,PageCarver pageCarver){
		if(this.isPause != isPause){
			oldTime = System.currentTimeMillis();
		}
		this.isPause = isPause;
		setAutoStartDelayedType(autoStartDelayedType);
		pageCarver.requestInvalidate();
	}
	
	public boolean isPause(){
		return isPause;
	}

	private void setToolbarAnimDelayed(long toolbarAnimDelayed){
		if(toolbarAnimDelayed != mToolbarAnimDelayed){
			isChangeSpeed = true;
		}
		this.mToolbarAnimDelayed = toolbarAnimDelayed;
	}
	
	public void setAutoStartDelayedType(int autoStartDelayedType){
		final String[] delayedArray = getResources().getStringArray(R.array.auto_delayed);
		if(autoStartDelayedType < 0 || autoStartDelayedType >= delayedArray.length){
			return;
		}
		setToolbarAnimDelayed(Integer.parseInt(delayedArray[autoStartDelayedType]));
	}
	
	private float computeProgress(int height){
		float progress = 0;
		if(mCurDelayed == 0 || mToolbarAnimDelayed == 0)return 0;
		if(isChangeSpeed){
			mCurDelayed = (int) (mToolbarAnimDelayed * (y / height));
			isChangeSpeed = !isChangeSpeed;
		}
		progress = mCurDelayed * 1f / mToolbarAnimDelayed;
		if(progress > 1) progress = 1;
		return progress;
	}
}
