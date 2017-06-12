package com.lectek.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabHost;

public class BaseTabHost extends TabHost{
	private OnClickTabListener mOnClickTabListener;
	private OnTabChangeListener mOnTabChangeListener;
	public BaseTabHost(Context context) {
		super(context);
		init();
	}

	public BaseTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init(){
		super.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				dispatchTabChanged(tabId);
			}
		});
		onInit();
	}
	
	protected void onInit(){
		
	}
	
	@Override
	public void setCurrentTab(int index) {
		boolean isChange = index != getCurrentTab();
		super.setCurrentTab(index);
		if(mOnClickTabListener != null){
			mOnClickTabListener.onClickTab(index,isChange);
		}
	}
	
	public void setOnClickTabListener(OnClickTabListener onClickTabListener){
		mOnClickTabListener = onClickTabListener;
	}
	/**
	 * 生成没有内容的站位View设置代码为：
	 * <p>contentView.setTag(tag);</p>contentView.setId(getCurrentTab());
	 * @return
	 */
	public TabContentFactory newTabContentFactory(){
		return new TabContentFactory() {

			@Override
			public View createTabContent(String tag) {
				FrameLayout contentView = new FrameLayout(getContext());
				contentView.setTag(tag);
				contentView.setId(getCurrentTab());
				return contentView;
			}
		};
	}
	
	@Override
	public void setOnTabChangedListener(OnTabChangeListener l) {
		mOnTabChangeListener = l;
	}

	protected void dispatchTabChanged(String tabId) {
		if(mOnTabChangeListener != null){
			mOnTabChangeListener.onTabChanged(tabId);
		}
	}
	
	public interface OnClickTabListener{
		public void onClickTab(int index,boolean isChange);
	}
}
