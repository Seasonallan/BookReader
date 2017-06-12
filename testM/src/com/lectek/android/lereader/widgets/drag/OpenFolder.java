/*
 * Created Date: 2012-9-23 上午10:34:48
 * 
 */
package com.lectek.android.lereader.widgets.drag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 模拟弹窗
 */
public class OpenFolder {

	public static String TAG = "OpenFolder";
	/** folder animaltion execution time */
	private static int ANIMALTION_TIME = 400;

	private static float SIZE = 4 / 5f;
	private Context mContext;
	private boolean mWindowIsAdd = false;
	private int mWindowLayoutType = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;

	private RelativeLayout container;
	//private View mBackgroundView;
	private View mFolderView;  

	private int[] mAnchorLocation = new int[2];
	private int mFolderheigh;
	private int mFolderUpY;
 
	private boolean mIsOpened = false;

	/**
	 * Listener that is called when this OpenFolder window is closed.
	 */
	public interface OnFolderClosedListener {
		public void onClosed();
	}

	private OnFolderClosedListener mOnFolderClosedListener;

	/**
	 * Sets the listener to be called when the openFolder is colsed.
	 */
	public void setmOnFolderClosedListener(
			OnFolderClosedListener onFolderClosedListener) {
		this.mOnFolderClosedListener = onFolderClosedListener;
	}

	private DragLayer mContainerView;

    public static OpenFolder sInstance;

	public OpenFolder(Context context, DragLayer layer) {
		this.mContext = context;
		this.mContainerView = layer;
		this.container = new RelativeLayout(mContext);
		this.container.setBackgroundColor(0x77000000);
        sInstance = this;
	}

    public int getTopEdge(){
        return (int) (mContext.getResources().getDisplayMetrics().heightPixels * (1 - SIZE));
    }

    public int position;
    public void setItemPosition(int position){
        this.position = position;
    }
    private boolean wrapContent;
	/**
	 * @param folderView
	 *            folder页面
	 */
	public void openFolderView(View folderView, boolean wrapContent) {
		//mBackgroundView = backgroundView;
		mFolderheigh = (int) (mContext.getResources().getDisplayMetrics().heightPixels * SIZE);
		mFolderView = folderView;
        this.wrapContent = wrapContent;
		// 获取参照控件的位置
		//anchor.getLocationOnScreen(mAnchorLocation);
 
		mFolderUpY = (int) (mContext.getResources().getDisplayMetrics().heightPixels * (1 - SIZE));
/*		container.setFolderParams(mFolderUpY);
		container.setOnDismissListener(new ICallBack() {

			@Override
			public Object onResult(Object... params) {
				//mContainerView.removeView(container);
				//mWindowIsAdd = false;
				dismiss();
				return null;
			}
		});*/
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
		prepareLayout();
	    startOpenAnimation();
	}

	private void prepareLayout() {

		if (mWindowIsAdd) {
			Log.e(TAG,
					"container view has already been added to the window manager!!!");
			return;
		}
		container.removeAllViews();
		// add folderview
		RelativeLayout.LayoutParams fp = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, wrapContent? LayoutParams.WRAP_CONTENT: mFolderheigh);
		fp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		container.addView(mFolderView, fp);

		/*// 截当前view背景图，用于分割 topview bottomview
		mBackgroundView.setDrawingCacheEnabled(true);
		Bitmap srceen = mBackgroundView.getDrawingCache();
		// add topview
		// 截图控件以上部分
		Bitmap top = Bitmap.createBitmap(srceen, 0, 36, mSrceenwidth,
				offsety - 36);
		mTopView = new ImageView(mContext);
		mTopView.setId(1000);
		mTopView.setBackgroundDrawable(new BitmapDrawable(mContext
				.getResources(), top));
		RelativeLayout.LayoutParams ft = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, offsety);
		ft.addRule(RelativeLayout.ABOVE, mFolderView.getId());
		//container.addView(mTopView, ft);

		// add bottomview
		// 截图控件以下部分
		Bitmap bottom = Bitmap.createBitmap(srceen, 0, offsety, mSrceenwidth,
				mSrceenheigh - offsety);
		mBottomView = new ImageView(mContext);
		mBottomView.setBackgroundDrawable(new BitmapDrawable(mContext
				.getResources(), bottom));
		RelativeLayout.LayoutParams fb = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, mSrceenheigh - offsety);
		fb.addRule(RelativeLayout.BELOW, mFolderView.getId());
		//container.addView(mBottomView, fb);
*/
		if (!mWindowIsAdd) {
			mContainerView.addView(container, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			mWindowIsAdd = true;
		}

	}

	private void startOpenAnimation() {
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1f);
		alphaAnimation.setDuration(ANIMALTION_TIME);
//		alphaAnimation.setFillAfter(true);
		container.startAnimation(alphaAnimation);

		TranslateAnimation ta = new TranslateAnimation(0, 0, mFolderheigh, 0);
        if (wrapContent){
            ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        }
		ta.setDuration(ANIMALTION_TIME);
		ta.setFillAfter(true);
		ta.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mIsOpened = true;
			}
		});
		mFolderView.startAnimation(ta);
	}

	/*private WindowManager.LayoutParams createPopupLayout(IBinder token) {
		WindowManager.LayoutParams p = new WindowManager.LayoutParams();
		p.gravity = Gravity.LEFT | Gravity.TOP;
		p.width = mSrceenwidth;
		p.height = mSrceenheigh;
		p.format = PixelFormat.OPAQUE;
		p.token = token;
		p.type = mWindowLayoutType;
		p.setTitle("OpenFolder:" + Integer.toHexString(hashCode()));

		return p;
	}
*/
	/**
	 * @return true if the folder is showing, false otherwise
	 */
	public boolean isOpened() {
		return mIsOpened;
	}

	/**
	 * colse the folder
	 */
	public void dismiss() {
		if (!mIsOpened) {
			return;
		}

		AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.0f);
		alphaAnimation.setDuration(ANIMALTION_TIME);
//		alphaAnimation.setFillAfter(true);
		container.startAnimation(alphaAnimation);

		TranslateAnimation tra = new TranslateAnimation(0, 0, 0,
				mFolderheigh);
        if (wrapContent){
            tra = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        }
		tra.setDuration(ANIMALTION_TIME);
		tra.setFillAfter(true);
		tra.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				container.post(new Runnable() {

					@Override
					public void run() {
						container.removeAllViews();
					}
				});

				mContainerView.removeView(container);
				mWindowIsAdd = false;
				// 清空画图缓存区，否则获取的还是原来的图像
				//mBackgroundView.setDrawingCacheEnabled(false);
				mIsOpened = false;
				if (mOnFolderClosedListener != null) {
					mOnFolderClosedListener.onClosed();
				}
			}
		});
		mFolderView.startAnimation(tra);
	}

}
