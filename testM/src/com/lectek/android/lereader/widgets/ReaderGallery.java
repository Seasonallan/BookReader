package com.lectek.android.lereader.widgets;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.SpinnerAdapter;

/**
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-5-10
 */
public class ReaderGallery extends Gallery {
	private boolean isTouchNow;
	private OnReStartListener mOnReStartListener;
	private AdapterView.OnItemSelectedListener mSelectedListener;
	private AdapterView.OnItemClickListener mItemClickListener;
	
	private int mRealCount;
	
	public ReaderGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setSoundEffectsEnabled(false);
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		float f = 3F * distanceX;
	    return super.onScroll(e1, e2, f, distanceY);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		setTouchNow(true);
		switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent(). requestDisallowInterceptTouchEvent(true);
                break;
		case MotionEvent.ACTION_UP:
			mOnReStartListener.reStart();
			setTouchNow(false);
			break;
		default:
			break;
		}
		
		return super.onTouchEvent(event);
	}

	public void releaseRes(){
		this.setOnItemClickListener(null);
		this.setAdapter(null);
		this.destroyDrawingCache();
		if(this.getParent() instanceof ViewGroup){
			((ViewGroup)this.getParent()).removeView(this);
		}
	}
	
	public boolean isTouchNow() {
		return isTouchNow;
	}

	public void setTouchNow(boolean isTouchNow) {
		this.isTouchNow = isTouchNow;
	}
	
	public void setOnReStartListener(OnReStartListener onReStartListener){
		this.mOnReStartListener = onReStartListener;
	}
	public interface OnReStartListener {
		public void reStart();
	}
	
	//add by chends 2012-11-23
	public void moveNext() {
		onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
	}
	
	public void movePrevious() {
		 onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
	}
	
	@Override
	public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
		mSelectedListener = listener;
		super.setOnItemSelectedListener(mSelectedListenerWrapper);
	}
	
	@Override
	public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
		mItemClickListener = listener;
		super.setOnItemClickListener(mItemClickWrapper);
	}
	
	private AdapterView.OnItemClickListener mItemClickWrapper = new AdapterView.OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> parent,View view,int position,long id) {

			if(mItemClickListener == null ){
				return;
			}
			
			int rePosition =  mRealCount > 1 ? position % mRealCount : 0;				
			mItemClickListener.onItemClick(parent, view, rePosition, getAdapter().getItemId(rePosition));
		}
	};
	
	@Override
	public void setSelection(int position) {
		
		int currentPosition = getSelectedItemPosition();
		if(currentPosition < 0) {
			currentPosition = Integer.MAX_VALUE / 2;
		}
		
		position = mRealCount > 1 ? (currentPosition / mRealCount) * mRealCount + position : Integer.MAX_VALUE / 2 + position;
		
		super.setSelection(position);
		
	}


    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        // 当拦截触摸事件到达此位置的时候，返回true，
        // 说明将onTouch拦截在此控件，进而执行此控件的onTouchEvent
        return true;
    }

	@Override
	public void setAdapter(SpinnerAdapter adapter) {
		super.setAdapter(new AdapterWrapper(adapter));
	}
	
	private AdapterView.OnItemSelectedListener mSelectedListenerWrapper = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			
			if(mSelectedListener == null ){
				return;
			}
			
			int position =  mRealCount > 1 ? arg2 % mRealCount : 0;				
			mSelectedListener.onItemSelected(arg0, arg1, position, arg3);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			if(mSelectedListener != null) {
				mSelectedListener.onNothingSelected(arg0);
			}
		}
		
	};
	
	private class AdapterWrapper extends BaseAdapter {
		
		private SpinnerAdapter mAdapter;
		
		public AdapterWrapper(SpinnerAdapter adapter) {
			mAdapter = adapter;
		}
		
		@Override
		public int getCount() {

			if(mAdapter != null) {		
				mRealCount = mAdapter.getCount();
				return mRealCount < 2 ? mRealCount : Integer.MAX_VALUE;
			}
			
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			return mAdapter == null ? null: mAdapter.getItem(arg0);
		}

		@Override
		public long getItemId(int position) {
			return mAdapter == null ? 0:mAdapter.getItemId(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			position = position % mAdapter.getCount();
			
			View result = mAdapter.getView(position, convertView, parent);
			
			LayoutParams lp = (Gallery.LayoutParams)result.getLayoutParams();
			if(lp == null) {
				lp = new LayoutParams(parent.getWidth()- parent.getPaddingLeft() - parent.getPaddingRight() - 2, 
											parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom());
			}
			
			lp.width = parent.getWidth() - 2;
			
			return result;
		}
		
		@Override
		public void registerDataSetObserver(DataSetObserver observer) {

			if (mAdapter != null) {

				mAdapter.registerDataSetObserver(observer);

			}

		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {

			if (mAdapter != null) {

				mAdapter.unregisterDataSetObserver(observer);

			}

		}

	}
}
