package com.lectek.lereader.core.text.test;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lectek.bookformats.R;

public class NotePopWin extends BasePopupWindow{
	private TextView mTextView;
	private Drawable mContentDrawable;
	private RelativeLayout mContentLayout;
	private ImageView mTopIV;
	private ImageView mBottomIV;
	private Rect mPadding;
	public NotePopWin(View parent) {
		super(parent);
		mPadding = new Rect();
		RelativeLayout.LayoutParams lp = null;
		
		mContentDrawable = getResources().getDrawable(R.drawable.note_arrow_content);
		mContentDrawable.getPadding(mPadding);
		mContentLayout = new RelativeLayout(getContext());
		mContentLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		
		ImageView tempIV = new ImageView(getContext());
		tempIV.setId(tempIV.hashCode());
		tempIV.setImageResource(R.drawable.note_arrow_top);
		tempIV.setVisibility(View.INVISIBLE);
		lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		tempIV.setLayoutParams(lp);
		mContentLayout.addView(tempIV);
		
		mTextView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.scroll_text_view, null);
		mTextView.setId(mTextView.hashCode());
		mTextView.setBackgroundDrawable(mContentDrawable);
		mTextView.setTextColor(Color.WHITE);
		mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
		mTextView.setGravity(Gravity.CENTER_VERTICAL);
		lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		lp.topMargin = -mPadding.top;
		lp.addRule(RelativeLayout.BELOW, tempIV.getId());
		mTextView.setLayoutParams(lp);
		mContentLayout.addView(mTextView);

		mTopIV = new ImageView(getContext());
		mTopIV.setId(mTopIV.hashCode());
		mTopIV.setImageResource(R.drawable.note_arrow_top);
		mTopIV.setVisibility(View.INVISIBLE);
		lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_TOP, tempIV.getId());
		lp.addRule(RelativeLayout.ALIGN_BOTTOM, tempIV.getId());
		mTopIV.setLayoutParams(lp);
		mContentLayout.addView(mTopIV);
		
		mBottomIV = new ImageView(getContext());
		mBottomIV.setId(mBottomIV.hashCode());
		mBottomIV.setImageResource(R.drawable.note_arrow_bottom);
		mBottomIV.setVisibility(View.INVISIBLE);
		lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		lp.topMargin = -mPadding.bottom;
		lp.addRule(RelativeLayout.BELOW, mTextView.getId());
		mBottomIV.setLayoutParams(lp);
		mContentLayout.addView(mBottomIV);
	}

	@Override
	protected View onCreateContentView() {
		return mContentLayout;
	}

	public void showNote(String note, RectF localRect,float size){
		if(note == null){
			note = "";
		}
		localRect = new RectF(localRect);
		mTextView.setText(note);
		mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
		int width = mParentView.getWidth();
		int height = mParentView.getHeight();
		mTextView.setMaxWidth(width);
		mTextView.setMaxHeight(height);
		mContentLayout.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int[] mScreenLocation = new int[2];
		mParentView.getLocationOnScreen(mScreenLocation);
		mTextView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		getPopupWindow().setWidth(mContentLayout.getMeasuredWidth());
		getPopupWindow().setHeight(mContentLayout.getMeasuredHeight());
		int contentHeight = mTextView.getMeasuredHeight();
		int contentWidth = mTextView.getMeasuredWidth();
		int popX = (int) (localRect.centerX() - contentWidth / 2);
		if(popX + contentWidth > mScreenLocation[0] + mParentView.getWidth()){
			popX = mScreenLocation[0] + mParentView.getWidth() - contentWidth;
		}
		if(popX < mScreenLocation[0]){
			popX = mScreenLocation[0];
		}
		int leftMargin = (int) (localRect.centerX() - popX - mTopIV.getMeasuredWidth() / 2);
		if(leftMargin < mPadding.left){
			leftMargin = mPadding.left;
		}
		if(contentWidth - leftMargin - mTopIV.getMeasuredWidth() < mPadding.right){
			leftMargin = contentWidth - mTopIV.getMeasuredWidth() - mPadding.right;
		}
		if(localRect.top - mScreenLocation[1] >= contentHeight){
			mBottomIV.setVisibility(View.VISIBLE);
			mTopIV.setVisibility(View.INVISIBLE);
			RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) mBottomIV.getLayoutParams();
			lp.leftMargin = leftMargin;
			mBottomIV.setLayoutParams(lp);
			showAtLocation(popX, (int) (localRect.top - contentHeight));
		}else if(mScreenLocation[1] + height - localRect.bottom >= contentHeight){
			mBottomIV.setVisibility(View.INVISIBLE);
			mTopIV.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) mTopIV.getLayoutParams();
			lp.leftMargin = leftMargin;
			mTopIV.setLayoutParams(lp);
			showAtLocation(popX, (int) localRect.bottom);
		}else{
			mBottomIV.setVisibility(View.INVISIBLE);
			mTopIV.setVisibility(View.INVISIBLE);
			showAtLocation(mScreenLocation[0] + (width - contentWidth) / 2
					,mScreenLocation[1] + (height - contentHeight) / 2);
		}
		mContentLayout.requestLayout();
	}
}
