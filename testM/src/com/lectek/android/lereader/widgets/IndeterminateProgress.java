package com.lectek.android.lereader.widgets;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.lectek.android.LYReader.R;
/**
 * 统一控制加载过渡进度样式
 * @author linyiwei
 */
public class IndeterminateProgress extends FrameLayout {
	private Drawable mIndeterminateDrawable;
	public IndeterminateProgress(Context context) {
		super(context);
		init();
	}
	
	public IndeterminateProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public IndeterminateProgress(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		//R.drawable.firecracker_anim_list
		//R.drawable.progress_medium_white
		//R.drawable.header_img
//		long currentTimeMillis = System.currentTimeMillis();
//		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+08:00")); 
//		cal.set(2013, 1, 8, 0, 0, 0); //注意月份的表示，一月是0，二月是1，以此类推，是12月是11
//		long min = cal.getTime().getTime();
//		cal.set(2013, 1, 25, 0, 0, 0);
//		long max = cal.getTime().getTime();
//		boolean isAnniversary = min <= currentTimeMillis && max >= currentTimeMillis;
		int indeterminateRes = 0;
		int backgroundRes = 0;
//		if(isAnniversary){
//			indeterminateRes = R.drawable.firecracker_anim_list;
//			backgroundRes = 0;
//		}else{
			indeterminateRes = R.drawable.progress_medium_white;
//			backgroundRes = R.drawable.header_img;
//		}
		setIndeterminateProgress(indeterminateRes,backgroundRes);
	}
	/**
	 * 0 代表为空
	 * @param indeterminateRes
	 * @param backgroundRes
	 */
	private void setIndeterminateProgress(int indeterminateRes,int backgroundRes){
		LayoutParams lp = null;
		if(backgroundRes != 0){
			lp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			ImageView imageView = new ImageView(getContext());
			imageView.setBackgroundResource(backgroundRes);
			lp.gravity = Gravity.CENTER;
			addView(imageView,lp);
		}
		if(indeterminateRes != 0){
			lp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			lp.gravity = Gravity.CENTER;
			ProgressBar progressBar = (ProgressBar) LayoutInflater.from(getContext()).inflate(R.layout.indeterminate_progress_bar, null);
			mIndeterminateDrawable = getContext().getResources().getDrawable(indeterminateRes);
			progressBar.setIndeterminateDrawable(mIndeterminateDrawable);
			addView(progressBar,lp);
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if(mIndeterminateDrawable instanceof AnimationDrawable){
			((AnimationDrawable) mIndeterminateDrawable).setVisible(true, true);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		if(mIndeterminateDrawable instanceof AnimationDrawable){
			((AnimationDrawable) mIndeterminateDrawable).stop();
		}
		super.onDetachedFromWindow();
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if(mIndeterminateDrawable instanceof AnimationDrawable){
			if(visibility != VISIBLE){
				((AnimationDrawable) mIndeterminateDrawable).stop();
			}else{
				((AnimationDrawable) mIndeterminateDrawable).setVisible(true, true);
			}
		}
	}
}
