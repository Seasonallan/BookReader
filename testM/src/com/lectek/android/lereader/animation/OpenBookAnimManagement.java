package com.lectek.android.lereader.animation;

import android.R.color;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.utils.BitmapUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
/**	打开书本切换动画管理类
 * @author linyiwei
 * @email 21551594@qq.com
 * @date 2011-12-01
 */
public class OpenBookAnimManagement {
	private static OpenBookAnimManagement mOpenBookAnimManagement;
	private PopupWindow mPopupWindow;
	private OpenBookAnim mTopAnim;
	private OpenBookAnim mBottomAnim;
	private View mView;
	private Bitmap mBookCover;
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private boolean isOpen = false;
	private int canBackLocation [];
	private int mLocation [] = new int[2];
	private OpenBookAnimManagement(){
		
	}
	
	public static OpenBookAnimManagement getInstance(){
		if(mOpenBookAnimManagement == null){
			mOpenBookAnimManagement = new OpenBookAnimManagement();
		}
		return mOpenBookAnimManagement;
	}

    public void setOpenBookAnimVIew(ImageView v){
        setOpenBookAnimVIew(v, ((BitmapDrawable) v.getDrawable()).getBitmap());
    }

	public void setOpenBookAnimVIew(View v, Bitmap bitmap){
		setOpenBookAnimVIew(v,bitmap,null);
	}
	
	public void setOpenBookAnimVIew(View v, Bitmap bitmap,int canBackLocation[]){
        /*if (mBookCover != null){
            if (!mBookCover.isRecycled()){
                mBookCover.recycle();
                mBookCover = null;
            }
        }
        if (mView != null){
            mView = null;
        }*/
		mView = v;
		if(mView == null)return;
		mView.getLocationInWindow(mLocation);
		mBookCover = bitmap;
		this.canBackLocation = canBackLocation;
	}
	
	public void starOpenBookAnim(final PreRunnable animOverRun){
		isOpen = false;
		dismissPopWin();
		if(mView == null || !mView.isShown()){
			if(animOverRun != null)animOverRun.run();
			return;
		}
		isOpen = true;
		mPopupWindow = new PopupWindow(mView.getContext());
		mPopupWindow.setWidth(android.view.ViewGroup.LayoutParams.FILL_PARENT);
		mPopupWindow.setHeight(android.view.ViewGroup.LayoutParams.FILL_PARENT);
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(color.transparent));
		mPopupWindow.setTouchable(true);
		mPopupWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(mPopupWindow == null || !mPopupWindow.getContentView().isShown()){
					stopBookAnim();
					return false;
				}
				return true;
			}
		});
		final View mainView = LayoutInflater.from(mView.getContext()).inflate(R.layout.open_book_anim_lay, null);
		mainView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
		final Runnable runnable = new Runnable() {
			boolean isStart = false;
			@Override
			public void run() {
				if(!isStart){
					isStart = true;
					if(animOverRun != null){
						if(!animOverRun.run()){
							OnComeBack();
						}
					}
					releaseRes();
				}
			}
		};
		initAnimView(mainView,mView).setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				mHandler.post(runnable);
			}
		});
		mPopupWindow.setContentView(mainView);
		mPopupWindow.showAtLocation(mView, Gravity.NO_GRAVITY,0,0);
		mHandler.postDelayed(runnable, mTopAnim.getDuration() + 100);
	}
	
	private void dismissPopWin(){
		if( mPopupWindow != null && mPopupWindow.isShowing()){
//			View view = mPopupWindow.getContentView();
//			if(view.isShown()){
//				mPopupWindow.dismiss();
//			}
			mPopupWindow.dismiss();
		}
	}
	
	public void stopBookAnim(){
		dismissPopWin();
		mPopupWindow = null;
		mView = null;
		mTopAnim = null;
		mBottomAnim = null;
		canBackLocation = null;
		mBookCover = null;
		isOpen = false;
		Runtime.getRuntime().gc();
	}
	
	private void releaseRes(){
		if(mPopupWindow != null && mPopupWindow.getContentView() != null){
			mPopupWindow.getContentView().destroyDrawingCache();
			mPopupWindow.getContentView().findViewById(R.id.anim_view_top).destroyDrawingCache();
			mPopupWindow.getContentView().findViewById(R.id.anim_view_bottom).destroyDrawingCache();
		}
		Runtime.getRuntime().gc();
	}
	
	public void starCloseBookAnim(final PreRunnable animOverRun){
		//TODO 需要添加一些防止极端情况出现造成屏幕无法点击的问题
		if(mTopAnim == null || mBottomAnim == null ||  mPopupWindow == null || !isOpen){
			LogUtil.e("OpenBookAnimManagement", " invalid close");
			stopBookAnim();
			if(animOverRun != null)animOverRun.run();
			return;
		}
		mTopAnim.initialize(OpenBookAnim.START_TYPE_INVERSE, canBackLocation);
		mBottomAnim.initialize(OpenBookAnim.START_TYPE_INVERSE, canBackLocation);
		mTopAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				stopBookAnim();
			}
		});
		ImageView mTopView = (ImageView) mPopupWindow.getContentView().findViewById(R.id.anim_view_top);
		ImageView mBottomView = (ImageView) mPopupWindow.getContentView().findViewById(R.id.anim_view_bottom);
        mTopView.clearAnimation();
        mBottomView.clearAnimation();
		mTopView.startAnimation(mTopAnim);
		mBottomView.startAnimation(mBottomAnim);
	}
	
	public boolean isAnimOpen(){
		if(mPopupWindow != null && mPopupWindow.isShowing()){
			stopBookAnim();
			return false;
		}
		return isOpen;
	}
	
	private OpenBookAnim initAnimView(View mainView ,View v){
		DisplayMetrics  mDisplayMetrics  = v.getContext().getResources().getDisplayMetrics();
		ImageView mTopView = (ImageView) mainView.findViewById(R.id.anim_view_top);
		ImageView mBottomView = (ImageView) mainView.findViewById(R.id.anim_view_bottom);
		int location[] = mLocation;
		int mWH[] = new int[]{v.getMeasuredWidth(),v.getMeasuredHeight()};
		
		LayoutParams lp = (LayoutParams) mTopView.getLayoutParams();
		lp.leftMargin = location[0];
		lp.topMargin = location[1];
		lp.width = mWH[0];
		lp.height = mWH[1];
		mTopView.setLayoutParams(lp);
		mTopAnim = new OpenBookAnim(location[0], location[1], mWH[0], mWH[1],
				 mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels, true);
		mTopAnim.setFillAfter(true);
		mTopAnim.setDuration(700);
		mTopView.setAnimation(mTopAnim);
		mTopView.setImageBitmap(mBookCover);
		mBottomView.setLayoutParams(lp);
		mBottomAnim = new OpenBookAnim(location[0], location[1], mWH[0], mWH[1],
				 mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels, false);
		mBottomAnim.setFillAfter(true);
		mBottomAnim.setDuration(700);
//		mBottomView.setImageResource(R.drawable.book_second_cover);
		mBottomView.setImageBitmap(BitmapUtil.getBitmap(mBottomView.getContext(), R.drawable.book_second_cover));
		mBottomView.setAnimation(mBottomAnim);
		return mTopAnim;
	}
	
	public void OnComeBack(){
		starCloseBookAnim(null);
	}
	public interface OnComeBackListener{
		public void onComeBack(ImageView mImageView);
	}
	public interface PreRunnable{
		public boolean run();
	}
}
