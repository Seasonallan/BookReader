package com.lectek.android.lereader.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.utils.DateUtil;

/**
 * 可以下拉刷新的listview
 * @author zhouxinghua
 *
 */
public class PullToRefreshListView extends ListView implements OnScrollListener{
	private static final int STATE_PULL_TO_REFRESH = 10;//下拉刷新
	private static final int STATE_RELEASE_TO_REFRESH = STATE_PULL_TO_REFRESH + 1;//松开刷新
	private static final int STATE_REFRESHING = STATE_RELEASE_TO_REFRESH + 1;//正在刷新
	private static final int STATE_DONE = STATE_REFRESHING + 1;//刷新完成
	private static final int RATIO = 5;// 实际拉动距离和 headview距离上边距 距离之比
	private int state;//用来记录当前下拉刷新状态
	private boolean isRecord;//起始点坐标是否被记录
	private float startX;
	private float startY;
	private float tempX;
	private float tempY;
	private boolean isBack; //是否由 松开刷新  回到的 下拉刷新
	private int firstVisibleIndex;//ListView中 第一个看见的Item的下标    当下标为0的时候 再下拉的话  下拉刷新的控件显示
	private int headContentHeight;	//head view的高度
	private Animation animation;
	private Animation reverseAnimation;
	
	private OnScrollListener mOnScrollListener;
	private OnRefreshListener mOnRefreshListener;
	
	private View headView;
	private ImageView arrow;
	private ProgressBar progress;
	private TextView refreshTV;
	private TextView refreshTime;
	/**
	 * 最小滑动距离
	 */
	private int mTouchSlop;
	/**
	 * 下拉刷新是否有效
	 */
	private boolean isPullToRefreshEnable = false;
	
	public boolean isPullToRefreshEnable() {
		return isPullToRefreshEnable;
	}

	public void setPullToRefreshEnable(boolean isPullToRefreshEnable) {
		this.isPullToRefreshEnable = isPullToRefreshEnable;
	}

	public PullToRefreshListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PullToRefreshListView(Context context) {
		super(context);
		init(context);
	}
	
	/**
	 * 初始化操作
	 * @param context
	 */
	private void init(Context context){
		mTouchSlop = ViewConfiguration.getTouchSlop();
		
		headView = View.inflate(context, R.layout.pull_to_refresh_header, null);  //  //refresh_layout
		arrow = (ImageView) headView.findViewById(R.id.pull_to_refresh_image);
		progress = (ProgressBar) headView.findViewById(R.id.pull_to_refresh_progress);
		refreshTV = (TextView) headView.findViewById(R.id.pull_to_refresh_text);
		refreshTime = (TextView) headView.findViewById(R.id.refresh_time);
		
		arrow.setMinimumHeight(50);
		
		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();//重绘
		addHeaderView(headView);
		super.setOnScrollListener(this);
		
		animation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(250);
		animation.setFillAfter(true);
		animation.setInterpolator(new LinearInterpolator());//指定动画的运行速度的方式
		
		reverseAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);
		reverseAnimation.setInterpolator(new LinearInterpolator());//指定动画的运行速度的方式
		
		state = STATE_DONE;
		isRecord = false;
	}
	
	/**
	 * 测量view的宽高
	 * @param child
	 */
	private void measureView(View child) {
		
		ViewGroup.LayoutParams lp = child.getLayoutParams();
		
		if(lp == null){
			lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); 		
		}
		
		int childMeasureWidth = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
		int childMeasureHeight;
		
		if(lp.height > 0){
			childMeasureHeight = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
		} else {
			childMeasureHeight = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		
		child.measure(childMeasureWidth, childMeasureHeight);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(firstVisibleIndex == 0 && !isRecord){
				startX = ev.getX();
				startY = ev.getY();
				isRecord = true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			tempX = ev.getX();
			tempY = ev.getY();
			if(firstVisibleIndex == 0 && !isRecord){
				startX = tempX;
				startY = tempY;
				isRecord = true;
			}
			float dX = Math.abs(tempX - startX);
			float dY = Math.abs(tempY - startY);
			if(firstVisibleIndex == 0 && state != STATE_REFRESHING && isPullToRefreshEnable && dY > dX && dY > mTouchSlop){
				if(state == STATE_PULL_TO_REFRESH){	//下拉刷新
					if((tempY - startY) / RATIO > headContentHeight){	//向下拉动将整个headview全部显示出来
						//由下拉刷新-->释放刷新
						state = STATE_RELEASE_TO_REFRESH;
						changeHeadViewState();
					}else if((tempY - startY) < 0){	//向上推
						//由下拉刷新-->完成
						state = STATE_DONE;
						changeHeadViewState();
					}
				}
				
				if(state == STATE_RELEASE_TO_REFRESH){	//释放刷新
					setSelection(0);
					if((tempY - startY) /RATIO  < headContentHeight && (tempY - startY) > 0){
						//向上推，松开刷新-->下拉刷新
						state = STATE_PULL_TO_REFRESH;
						isBack = true;
						changeHeadViewState();
					}else if((tempY - startY) < 0){
						//继续向上推至headview看不见
						state = STATE_DONE;
						changeHeadViewState();
					}
				}
				
				if(state == STATE_DONE){	//刷新结束
					if((tempY - startY) > 0){ 
						//刷新结束-->下拉刷新
						state = STATE_PULL_TO_REFRESH;
						changeHeadViewState();
					}
				}
				//根据滑动距离设置headview显示多少
				if(state == STATE_PULL_TO_REFRESH || state == STATE_RELEASE_TO_REFRESH){
					headView.setPadding(0, (int) ((tempY - startY) /RATIO  - headContentHeight), 0, 0);
				}
			}
//			LogUtil.i("something", "state: "+state + " tempY - startY: " + (tempY - startY) + " startY: " + startY + " tempY: " + tempY);
			break;
		case MotionEvent.ACTION_UP:
			if(state != STATE_REFRESHING){
				if(state == STATE_PULL_TO_REFRESH){	//下拉刷新
					state = STATE_DONE;
					changeHeadViewState();
				}
				if(state == STATE_RELEASE_TO_REFRESH){	//释放刷新
					state = STATE_REFRESHING;
					changeHeadViewState();
					onRefresh();
				}
			}
			isRecord = false;
			break;
		}
		
		return super.onTouchEvent(ev);
	}
	
	/**
	 * 改变headview的显示状态
	 */
	private void changeHeadViewState(){
		switch (state) {
		case STATE_PULL_TO_REFRESH:	//下拉刷新
			arrow.setVisibility(View.VISIBLE);
			progress.setVisibility(View.GONE);
			refreshTV.setVisibility(View.VISIBLE);
			arrow.clearAnimation();
			refreshTV.setText(getString(R.string.note_pull_down));
			refreshTime.setText(getContext().getString(R.string.note_update_at, DateUtil.getCurrentTimeByMDHM()));
			if(isBack){//由 松开刷新  回到的 下拉刷新
				arrow.startAnimation(animation);
				isBack = false;
			}
			break;
		case STATE_RELEASE_TO_REFRESH:	//释放刷新
			arrow.setVisibility(View.VISIBLE);
			progress.setVisibility(View.GONE);
			refreshTV.setVisibility(View.VISIBLE);
			arrow.clearAnimation();
			refreshTV.setText(getString(R.string.note_pull_refresh));
			arrow.startAnimation(reverseAnimation);
			break;
		case STATE_REFRESHING:	//刷新中
			arrow.setVisibility(View.GONE);
			progress.setVisibility(View.VISIBLE);
			refreshTV.setVisibility(View.VISIBLE);
			arrow.clearAnimation();
			refreshTV.setText(getString(R.string.note_pull_loading));
			headView.setPadding(0, 0, 0, 0);
			
//			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			break;
		case STATE_DONE:	//完成
			arrow.setVisibility(View.VISIBLE);
			progress.setVisibility(View.GONE);
			refreshTV.setVisibility(View.VISIBLE);
			arrow.clearAnimation();
			refreshTV.setText(getString(R.string.pull_to_refresh_pull_label));
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			break;
		default:
			break;
		}
	}
	
	private String getString(int resId){
		return getContext().getString(resId);
	}
	
	/**
	 * 刷新状态
	 */
	public void onRefreshState() {
		state = STATE_REFRESHING;
		changeHeadViewState();
	}
	
	/**
	 * 刷新完成
	 */
	public void onRefreshComplete(){
		state = STATE_DONE;
		changeHeadViewState();
	}
	
	/**
	 * 刷新数据
	 */
	private void onRefresh(){
		if(mOnRefreshListener != null){
			mOnRefreshListener.onRefresh();
		}
	}
	
	public void setOnRefreshListener(OnRefreshListener onRefreshListener){
		mOnRefreshListener = onRefreshListener;
	}
	
	/**
	 * 刷新监听
	 * @author zhouxinghua
	 *
	 */
	public interface OnRefreshListener{
		/**
		 * 刷新回调
		 */
		public void onRefresh();
	}
	
	@Override
	public void setOnScrollListener(AbsListView.OnScrollListener l) {
		mOnScrollListener = l;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(mOnScrollListener != null){
			mOnScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		firstVisibleIndex = firstVisibleItem;
		if(mOnScrollListener != null){
			mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}
}
