package com.lectek.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.lectek.android.lereader.lib.utils.LogUtil;

/**
 * @author linyiwei 2013/1/18
 */
public class FacilityExpandableListView extends ExpandableListView implements
		OnScrollListener{
	private static final String TAG = FacilityExpandableListView.class.getSimpleName();
	/**
	 * 通过装饰者模式实现由当前类控制OnScrollListener，而不是父类。
	 */
	private OnScrollListener mOnScrollListener;
	/**
	 * 悬浮窗口显示的GroupItemView缓存
	 */
	private View mSuspensionGroupView;
	/**
	 * 悬浮窗口显示的GroupItemView的Layout载体，用于实现隐藏GroupItemView超出FacilityExpandableListView边缘部分。
	 */
	private InteriorRelativeLayout mSuspensionGroupLayout;
	/**
	 * 当前固定的GroupItemView位置
	 */
	private int currentSuspensionGroupPos = -1;
	/**
	 * 当前悬浮窗口显示GroupItemView位置，不一定是固定的，因为有的时候只是做过渡。
	 */
	private int showSuspensionGroupPos = -1;
	/**
	 * 当前悬浮窗口显示GroupItemView偏移量
	 */
	private int showSuspensionViewOffsetY = 0;
	/**
	 * 悬浮窗口
	 */
	private PopupWindow mSuspensionWindow;
	
	public FacilityExpandableListView(Context context) {
		super(context);
		inti();
	}

	public FacilityExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		inti();
	}

	public FacilityExpandableListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		inti();
	}

	private void inti() {
		mSuspensionGroupLayout = new InteriorRelativeLayout(getContext());
		mSuspensionGroupLayout.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT
				, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		mSuspensionGroupLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(showSuspensionGroupPos >= 0 && mSuspensionGroupView != null){
					int position = getFlatListPosition(getPackedPositionForGroup(showSuspensionGroupPos));
					performItemClick(mSuspensionGroupView, position, getAdapter().getItemId(position));
					updateSuspensionGroupView(showSuspensionGroupPos, true);
					setSelectedGroup(showSuspensionGroupPos);
				}
			}
		});
		super.setOnScrollListener(this);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		//为了实现悬浮窗口只悬浮在控制之上，而不是Activity，首先残废PopupWindow的绘制能力，由控件负责绘制悬浮窗口内容。
		if(mSuspensionGroupLayout.getVisibility() == View.VISIBLE){
			canvas.save();
			mSuspensionGroupLayout.superDraw(canvas);
			Drawable drawable = getDivider();
			if(drawable != null){
				drawable.setBounds(mSuspensionGroupLayout.getLeft()
						, mSuspensionGroupLayout.getBottom() + showSuspensionViewOffsetY
						, mSuspensionGroupLayout.getRight()
						, mSuspensionGroupLayout.getBottom() + getDividerHeight() + showSuspensionViewOffsetY);
				drawable.draw(canvas);
			}
			canvas.restore();
		}
	}
		
	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mOnScrollListener = l;
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mOnScrollListener != null) {
			mOnScrollListener.onScrollStateChanged(view, scrollState);
		}
		if(getAdapter() == null){
			return;
		}
		LogUtil.i(TAG, " onScrollStateChanged scrollState="+scrollState);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mOnScrollListener != null) {
			mOnScrollListener.onScroll(view, firstVisibleItem,
					visibleItemCount, totalItemCount);
		}
		computeSuspensionShowLocation();
	}
	
	private void computeSuspensionShowLocation(){
		if(getAdapter() == null || getChildCount() < 2 || Build.VERSION.SDK_INT < 8){//2.2以下的系统不支持
			return;
		}
		int firstVisibleItem = getFirstVisiblePosition();
		//计算当前第一个显示的Item的是否是GroupView以及它的GroupPosition。
		long packedPos = getExpandableListPosition(firstVisibleItem);
		int groupPos = getPackedPositionGroup(packedPos);
		int type = getPackedPositionType(packedPos);
		LogUtil.i(TAG, "onCompute type="+type+" groupPos="+groupPos+" packedPos="+packedPos+" firstVisibleItem="+firstVisibleItem);
		//标识当前第一个显示的Item的是ChildView。
		boolean isFirstVisibleChild = false;
		if (type == PACKED_POSITION_TYPE_GROUP) {
			//如果是GroupView显示悬浮的副本GroupView
			updateSuspensionGroupView(groupPos);
			showNewSuspensionView(groupPos);
		}else if(type == PACKED_POSITION_TYPE_NULL) {
			//如果既不是GroupView又不是ChildView，则隐藏悬浮View
			mSuspensionGroupLayout.setVisibility(View.INVISIBLE);
		}else if(type == PACKED_POSITION_TYPE_CHILD){
			//标识当前第一个显示的Item的是ChildView。
			isFirstVisibleChild = true;
			//如果这个时候悬浮窗口还未显示说明已经由于滚动的太快而错过了前面的监听。重新显示窗口矫正这个漏洞
			if(!isSuspensionWindowShowing() || mSuspensionGroupLayout.getVisibility() != View.VISIBLE){
				updateSuspensionGroupView(groupPos);
				showNewSuspensionView(groupPos);
			}
		}
		//判断当前显示的第二条Item显示的内容是什么
		int nextVisibleItem = firstVisibleItem + 1;
		if(nextVisibleItem < getCount() && currentSuspensionGroupPos != -1){
			long nextPackedPos = getExpandableListPosition(nextVisibleItem);
			int nextGroupPos = getPackedPositionGroup(nextPackedPos);
			int nextType = getPackedPositionType(nextPackedPos);
			LogUtil.i(TAG, "onCompute nextType="+nextType);
			LogUtil.i(TAG, "onCompute currentSuspensionGroupPos="+currentSuspensionGroupPos+" nextGroupPos="+nextGroupPos);
			//如果是GroupView推动原来的悬浮窗口
			if (nextType == PACKED_POSITION_TYPE_GROUP) {
				View sourcePackedView = getChildAt(1);
				if(currentSuspensionGroupPos != nextGroupPos){
					pushSuspensionView(sourcePackedView);
				}else{
					int preGroupPos = nextGroupPos - 1;
					if(preGroupPos >= 0){
						updateSuspensionGroupView(preGroupPos);
						pushSuspensionView(sourcePackedView);
					}
				}
			}else if(isFirstVisibleChild){
				//当代码执行到进入这里就说明这个时候悬浮窗口是处于静止状态
				//重新计算悬浮窗口位置，已矫正由于滚动过快时并不是每滚动一个像素都会进入这个回调而导致的位置显示偏差。
				updateSuspensionGroupView(groupPos);
				showNewSuspensionView(groupPos);
			}
		}
	}
	
	/**
	 * 更新悬浮窗口内显示的GroupItem
	 * @param groupPosition
	 * @return
	 */
	private View updateSuspensionGroupView(int groupPosition) {
		return updateSuspensionGroupView(groupPosition, false);
	}
	
	/**
	 * 更新悬浮窗口内显示的GroupItem
	 * @param groupPosition
	 * @param isForceUpdate 强制刷新
	 * @return
	 */
	private View updateSuspensionGroupView(int groupPosition,boolean isForceUpdate){
		boolean isGroupExpanded = isGroupExpanded(groupPosition);
		int groupExpandedInt = mSuspensionGroupView != null && mSuspensionGroupView.getId() > 0 ? mSuspensionGroupView.getId() : isGroupExpanded ? 2: 1;
		boolean oldGroupExpanded = groupExpandedInt == 2 ? true : false;
		if(showSuspensionGroupPos == groupPosition 
				&& mSuspensionGroupView != null
				&& isGroupExpanded == oldGroupExpanded
				&& !isForceUpdate){
			return mSuspensionGroupView;
		}
		showSuspensionGroupPos = groupPosition;
		RelativeLayout.LayoutParams lp = null;
		if(mSuspensionGroupView == null){
			lp = new RelativeLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		mSuspensionGroupView = getExpandableListAdapter().getGroupView(groupPosition, isGroupExpanded, mSuspensionGroupView, this);
		mSuspensionGroupView.setId(isGroupExpanded == true ? 2 : 1);
		if(lp != null){
			mSuspensionGroupView.setLayoutParams(lp);
		}
		LogUtil.i(TAG, "updateSuspensionGroupView showSuspensionGroupPos="+showSuspensionGroupPos+" groupPosition="+groupPosition);
		return mSuspensionGroupView;
	}
	/**
	 * 显示新的GroupItem的悬浮窗口
	 */
	private void showNewSuspensionView(int groupPos){
		LogUtil.i(TAG, "showNewSuspensionView groupPos="+groupPos);
		currentSuspensionGroupPos = groupPos;
		if(mSuspensionGroupLayout.getChildCount() == 0){
			getSuspensionWindow().setWidth(getWidth());
			getSuspensionWindow().setHeight(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			getSuspensionWindow().setContentView(mSuspensionGroupLayout);
			mSuspensionGroupLayout.addView(mSuspensionGroupView);
		}
		if(mSuspensionGroupLayout.getVisibility() != View.VISIBLE){
			mSuspensionGroupLayout.setVisibility(View.VISIBLE);
		}
		if(!isSuspensionWindowShowing()){
			Point point = getLocationPoint(this);
			getSuspensionWindow().showAtLocation(this, 0, 0, point.y);
		}
		offsetAnimSuspensionGroupView(0);
	}
	/**
	 * 隐藏悬浮窗口
	 */
	private void hideSuspensionView(){
		currentSuspensionGroupPos = -1;
		if(mSuspensionWindow != null && mSuspensionWindow.isShowing()){
			mSuspensionWindow.dismiss();
		}
	}
	/**
	 * 根据提供的View偏移悬浮窗口，实现推的动画效果，偏移的方式是Animation
	 * @param source
	 */
	private void pushSuspensionView(View source){
		if(mSuspensionGroupView == null 
				|| !isSuspensionWindowShowing() 
				|| mSuspensionGroupLayout.getVisibility() != View.VISIBLE){
			return;
		}
		int difference = mSuspensionGroupView.getHeight() - source.getTop();
		if(difference < 0){
			difference = 0;
		}
		if(difference > mSuspensionGroupView.getHeight()){
			difference = mSuspensionGroupView.getHeight();
		}
		offsetAnimSuspensionGroupView(-difference);
		LogUtil.i(TAG, "pushSuspensionView source.getTop="+source.getTop());
	}

	/**
	 * 通过Animation方式偏移的悬浮窗口
	 * @param offsetY
	 */
	private void offsetAnimSuspensionGroupView(int offsetY){
		if(offsetY == 0){
			mSuspensionGroupView.setAnimation(null);
			showSuspensionViewOffsetY = 0;
		}else{
			showSuspensionViewOffsetY = offsetY;
			TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0
					, Animation.RELATIVE_TO_SELF, 0
					, Animation.ABSOLUTE,offsetY
					, Animation.ABSOLUTE,offsetY);
			anim.setDuration(0);
			anim.setFillAfter(true); 
			mSuspensionGroupView.setAnimation(anim);
		}
		invalidate();
		LogUtil.i(TAG, "offsetAnimSuspensionGroupView offsetY="+offsetY);
	}
	/**
	 * 判断悬浮窗口是否显示
	 * @return
	 */
	private boolean isSuspensionWindowShowing(){
		return mSuspensionWindow != null && mSuspensionWindow.isShowing();
	}

	private PopupWindow getSuspensionWindow(){
		if(mSuspensionWindow == null){
			mSuspensionWindow = new PopupWindow(getContext());
			mSuspensionWindow.setBackgroundDrawable(null);
			mSuspensionWindow.setFocusable(false);
		}
		return mSuspensionWindow;
	}
	
	@Override
	protected void onDetachedFromWindow() {
		//当ExpandableListView脱离Window关闭悬浮窗口
		hideSuspensionView();
		super.onDetachedFromWindow();
	}
	
	private Point getLocationPoint(View view){
		int[] location = new int[2];
		getLocationInWindow(location);
		return new Point(location[0], location[1]);
	}
	/**
	 * 用于残废PopupWindow绘制能力，由控件自己控制绘制。
	 * @author linyiwei
	 *
	 */
	private class InteriorRelativeLayout extends RelativeLayout{
		boolean canDraw = false;
		public InteriorRelativeLayout(Context context) {
			super(context);
		}
		
		@Override
		public void draw(Canvas canvas) {
			
		}
		
		@Override
		protected void dispatchDraw(Canvas canvas) {
			if(canDraw){
				super.dispatchDraw(canvas);
			}
		}

		public void superDraw(Canvas canvas){
			canDraw = true;
			super.draw(canvas);
			canDraw = false;
		}
		
		@Override
		public void addView(View child, int index, ViewGroup.LayoutParams params) {
			addViewInLayout(child, index, params, true);
		}
		
		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			if(getHeight() < ev.getY() - showSuspensionViewOffsetY){
				FacilityExpandableListView.this.dispatchTouchEvent(ev);
				return true;
			}
			ev.setLocation(ev.getX(), ev.getY() - showSuspensionViewOffsetY);
			return super.dispatchTouchEvent(ev);
		}

	}
}
