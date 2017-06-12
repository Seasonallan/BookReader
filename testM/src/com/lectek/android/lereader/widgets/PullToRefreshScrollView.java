package com.lectek.android.lereader.widgets;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.lectek.android.LYReader.R;

public class PullToRefreshScrollView extends PullToRefreshBase<ScrollView> {

	private IPostRefreshUI postRefreshUI;
	
	public PullToRefreshScrollView(Context context) {
		super(context);
	}

	public PullToRefreshScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected ScrollView createRefreshableView(Context context) {
		return new ScrollView(context);
	}

	@Override
	protected boolean isReadyForPullDown() {
		return refreshableView.getScrollY() == 0;
	}

	@Override
	protected boolean isReadyForPullUp() {
		ScrollView view = getRefreshableView();
		int off = view.getScrollY() + view.getHeight() - view.getChildAt(0).getHeight();
		if(off == 0){
			return true;
		}else{
			return false;
		}
	}

	@Override
	protected void onRefresh(int orientation) {
		
		if(postRefreshUI != null){
			postRefreshUI.postRefreshUI(orientation);
		}
	}
	
	public void setLoadingLayoutTip(String upFirstNameLabel, String downFirstNameLabel){
		//加载的提示语
		Context context = getContext();
		String pullUpLabel = context.getString(R.string.pull_to_refresh_pull_label);
		String pullDownLabel = context.getString(R.string.pull_to_refresh_pull_label_next);
		setLoadingLayoutTip(pullUpLabel, upFirstNameLabel,pullDownLabel, downFirstNameLabel);
	}
	
	public void setPostRefreshUI(IPostRefreshUI postRefreshUI){
		this.postRefreshUI = postRefreshUI;
	}
	
	public interface IPostRefreshUI{
		public void postRefreshUI(int orientation);
	}
}
