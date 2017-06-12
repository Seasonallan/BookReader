package com.lectek.lereader.core.text.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckedLinearLayout extends LinearLayout implements Checkable{

	private boolean mChecked;
	private static final int[] CHECKED_STATE_SET = {
        android.R.attr.state_checked
    };
	
	public CheckedLinearLayout(Context context) {
		super(context);
	}
	
	public CheckedLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void toggle() {
        setChecked(!mChecked);
    }

	@Override
    public boolean isChecked() {
        return mChecked;
    }
    
    /**
     * <p>Changes the checked state of this text view.</p>
     *
     * @param checked true to check the text, false to uncheck it
     */
	@Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            int count = getChildCount();
            for(int i = 0;i < count;i++){
            	View child = getChildAt(i);
            	if(child instanceof Checkable){
            		((Checkable) child).setChecked(checked);
            	}
            }
            refreshDrawableState();
        }
    }
    
    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        boolean populated = super.dispatchPopulateAccessibilityEvent(event);
        if (!populated) {
            event.setChecked(mChecked);
        }
        return populated;
    }
}
