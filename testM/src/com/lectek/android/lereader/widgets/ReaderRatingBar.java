package com.lectek.android.lereader.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RatingBar;

public class ReaderRatingBar extends RatingBar{
	
	private float mMinProgress;
	private float mSingleValue;
	private OnRatingBarChangingListener mOnRatingBarChangingListener;
	public ReaderRatingBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		mMinProgress = 0;
	}

	public ReaderRatingBar(Context context) {
        this(context, null);
        init();
    }
	
	@Override
	public synchronized void setSecondaryProgress(int secondaryProgress) {
		if(secondaryProgress < mMinProgress){
			setRating(mMinProgress);
			return;
		}
		if(mOnRatingBarChangingListener != null){
			mOnRatingBarChangingListener.OnRatingBarChanging(this, secondaryProgress);
		}
		super.setSecondaryProgress(secondaryProgress);
	}
	
	@Override
	public synchronized void setProgress(int progress) {
		super.setProgress(progress);
	}

	public float getmMinProgress() {
		return mMinProgress;
	}

	public void setmMinProgress(float mMinProgress) {
		this.mMinProgress = mMinProgress;
	}
	
	public void setOnRatingBarChangingListener(OnRatingBarChangingListener onRatingBarChangingListener){
		this.mOnRatingBarChangingListener = onRatingBarChangingListener;
	}
	
	public float getmSingleValue() {
		return mSingleValue;
	}

	public void setmSingleValue(float mSingleValue) {
		this.mSingleValue = mSingleValue;
	}
	
	public float getCurValue(){
		return getRating() * mSingleValue;
	}

	public interface OnRatingBarChangingListener {
		public void OnRatingBarChanging(ReaderRatingBar ratingBar, float rating);
	}
	
	public void setFloatRating(float rating){
		float a ;
		setMax(getMax() * 10);
		if (getNumStars() > 0) {
            a= 1f * getMax() / getNumStars();
        } else {
            a= 1;
        }
		setProgress((int) (rating * a));
	}
}
