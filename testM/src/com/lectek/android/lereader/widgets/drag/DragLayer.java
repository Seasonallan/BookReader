package com.lectek.android.lereader.widgets.drag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.lectek.android.lereader.lib.utils.LogUtil;

/**
 * 拖动容器
 */
public class DragLayer extends RelativeLayout implements IDragListener{


	private PageEdgeController mCountController;
	private DragController mDragController;

    public DragLayer(Context context) {
        super(context);
        init(context);
    }

    public DragLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DragLayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){
		mDragController = DragController.getInstance(); 
		mCountController = new PageEdgeController(context.getResources().getDisplayMetrics().heightPixels
				, (int) (8 * context.getResources().getDisplayMetrics().density));
		DragController.getInstance().registerDragListener(this);
	}

    public void setEdgeControllerEdge(int top){
        mCountController = new PageEdgeController(getContext().getResources().getDisplayMetrics().heightPixels + top
                , top);
    }

	private ScrollView mScrollView;
	public void setScrollView(ScrollView scrollView){
		this.mScrollView = scrollView;
	}
	
	private int mCurrentPage = 0;
	public void setOnDragPage(int page){
		mCurrentPage = page;
	}


	private float mLastMotionX; 
	private float mLastMotionY; 
	private long lastItemMoveTime = System.currentTimeMillis();
	@Override
	public boolean onTouchEvent(MotionEvent event) { 

		final int action = event.getAction();
		final float x = event.getRawX();
		final float y = event.getRawY();

		switch (action) {
		case MotionEvent.ACTION_DOWN: 
			super.onTouchEvent(event); 
		case MotionEvent.ACTION_MOVE:
			if (dragImageView != null) {  
				if(true){ 
					if (System.currentTimeMillis() - lastItemMoveTime > 200) {
						lastItemMoveTime = System.currentTimeMillis();
						final int yDiff = (int) Math.abs(mLastMotionY - y);
						final int xDiff = (int) Math.abs(mLastMotionX - x); 
						if (yDiff <= 8 && xDiff <= 8) {
							if (mScrollView != null) {
								event.setLocation(event.getX(), event.getY() + mScrollView.getScrollY());
							}else{
								event.setLocation(event.getX(), event.getY());
							}
							mDragController.notifyDrag(mCurrentPage, event);
						} 
						mLastMotionY = y;
						mLastMotionX  =x;
					}
					onDrag((int)x, (int)y); 
				}
			}else{ 
				super.onTouchEvent(event); 
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			drop(event);
			break; 
		}
		return super.onTouchEvent(event);
	}

    private void drop(MotionEvent event){
        if(mDragController.isDraging())
            mDragController.notifyDragDrop(mCurrentPage, event);
        if (dragImageView != null) {
            if (dragImageView != null) {
                dragImageView.setAlpha(60);
                windowManager.removeView(dragImageView);
                dragImageView = null;
            }
        }else{
        }
    }

    public void onDestory(){
        DragController.getInstance().unRegisterDragListener(this);
    }
	
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (dragImageView != null) {
            if (ev.getAction() == MotionEvent.ACTION_UP){
                drop(ev);
            }
			return true;
		}
		
		return super.onInterceptTouchEvent(ev);
	}

	private int bitmapWidth, bitmapHeight;
	private ImageView dragImageView = null;
	private IBinder miIBinder;
	private WindowManager windowManager = null;
	private WindowManager.LayoutParams windowParams = null;
	/**
	 * 创建图片
	 * @param bm
	 * @param x
	 * @param y
	 */
	private void createBitmapInWindow(Bitmap bm, int x, int y) { 
		windowManager = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		windowParams = new WindowManager.LayoutParams();   /**   
         *以下都是WindowManager.LayoutParams的相关属性   
         * 具体用途请参考SDK文档   
         */
		windowParams.type=2002;   //这里是关键，你也可以试试2003   
		windowParams.format=1;   
         /**   
         *这里的flags也很关键   
         *代码实际是wmParams.flags |= FLAG_NOT_FOCUSABLE;   
         *40的由来是wmParams的默认属性（32）+ FLAG_NOT_FOCUSABLE（8）   
         */
		windowParams.flags=40;   
		//windowParams.format = PixelFormat.TRANSLUCENT  
        //        | WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW;  
		windowParams.gravity = Gravity.TOP | Gravity.LEFT; 
		windowParams.x = x - bitmapWidth /2;//+ dragOffsetX - dragStartPointX;
		windowParams.y = y - bitmapHeight/2;//+ dragOffsetY - dragStartPointY - getScrollY();
		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.format= PixelFormat.RGBA_8888; //设置图片格式，效果为背景透明
		windowParams.alpha = 0.8f;
		windowParams.token = miIBinder;
		ImageView iv = new ImageView(getContext());
		iv.setImageBitmap(bm);
		  
		if (dragImageView != null) {
			windowManager.removeView(dragImageView);
		}
		windowManager.addView(iv, windowParams);
		dragImageView = iv;
	}


	/**
	 * 生成图片过程
	 * @param itemView
	 */
	private void showCreateDragImageAnimation(final IDragItemView itemView, int x, int y){
        Bitmap bm =  itemView.createBitmap();
		Bitmap orignalBitmp = Bitmap.createBitmap(bm, 0,0, bm.getWidth(), bm.getHeight());
		final Bitmap bitmap = scaleBitmpa(orignalBitmp, 1.0f); 
		bitmapWidth = bitmap.getWidth();
		bitmapHeight = bitmap.getHeight();
		if(mDragController.isDraging()){
			createBitmapInWindow(bitmap,x ,y);
		} 
	}


	/**
	 * 拖动图片
	 * @param x
	 * @param y
	 */
	private void onDrag(int x, int y) {
		mCountController.addCount(y);
		if(mCountController.isAllow2Snap2Next()){
			 if (mScrollView != null) {
				mScrollView.smoothScrollTo(0, mScrollView.getScrollY() + mCountController.mFullWidth);
			}
		}else if(mCountController.isAllow2Snap2Last()){
			 if (mScrollView != null) {
				mScrollView.smoothScrollTo(0, mScrollView.getScrollY() - mCountController.mFullWidth);
			}
		}
		if (dragImageView != null) {  
			int[] location = new int[2];
			getLocationOnScreen(location);
			
			windowParams.alpha = 0.8f;
			windowParams.x = x - bitmapWidth /2;//+ dragOffsetX - dragStartPointX;
			windowParams.y = y - bitmapHeight/2;//+ dragOffsetY - dragStartPointY - getScrollY();

			windowManager.updateViewLayout(dragImageView, windowParams);
		}
	}
	
	@Override
	public void onDragViewCreate(int page, IDragItemView itemView , MotionEvent event) {
		int x = (int)event.getRawX();
		int y = (int)event.getRawY(); 
 
		showCreateDragImageAnimation(itemView, x, y);
	}

	@Override
	public void onDragViewDestroy(int page, MotionEvent event) {
	}

	@Override
	public void onItemMove(int page, MotionEvent event) {
		
		
	}

	@Override
	public void onPageChange(int lastPage, int currentPage) {
		
	}

	@Override
	public <T> void onPageChangeRemoveDragItem(int lastPage, int currentPage,
			T object) {
		
		
	}
 
	@Override
	public <T> void onPageChangeReplaceFirstItem(int lastPage,
			int currentPage, T object) {
		
		
	}

	@Override
	public void onPageChangeFinish() {
	}

	@Override
	public void onDragEnable() {
		
		
	}

	@Override
	public void onDragDisable() {
        if (dragImageView != null) {
            windowManager.removeView(dragImageView);
            dragImageView = null;
        }
	}

	@Override
	public void onItemDelete(int page, int position) {
		mDragController.notifyDeleteItemInPage(getChildCount() -1, getChildCount() -1, page, position, null);
	}

	@Override
	public <T> void onItemDelete(int totalPage, int page, int removePage,  int position, T object) {
		 
	}
	 
	/**
	 * 图片放大缩小
	 * @param bitmap
	 * @param scale
	 * @return
	 */
	public static Bitmap scaleBitmpa(Bitmap bitmap, float scale) {
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	} 

	public void setWindowManager(IBinder binder) {
		this.miIBinder = binder;
	}


	@Override
	public void onFileCreateOrUpdate(int fileData,
			int itemData) {
		
	}
 
}


