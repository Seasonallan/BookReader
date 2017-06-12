package com.lectek.android.lereader.widgets.drag;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;

/**
 * 拖动Gridview
 * @author ziv
 * @param <T>
 *
 */
public class DragGridView<T extends IDragDatas> extends GridView implements IDragListener{
	public static final int DURATION = 250;

	protected int dragItem;
	private int mCurrentPage = 0;
	Context mContext; 
 
	protected int mDropItem;
	
	private DragController mDragController;
//	private ItemMoveController mAnimThread;
//	private int[][] mOldAnimOffset;
	
	public DragGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DragGridView(Context context) {
		super(context);
		init(context);
	}
	

	public int getCurrentLayer(){
		return this.mCurrentPage;
	}
	
	/**
	 * 设置当前页面所属页码
	 * @param page
	 */
	public void setCurrentPageId(int page){
		this.mCurrentPage = page;
	}
	
	private void init(Context context) {
		mContext = context; 
		mDragController = DragController.getInstance();
		mDragController.registerDragListener(this);
	}
   
	public boolean setOnItemLongClickListener(final MotionEvent ev) {
		this.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
                if (arg2 != -1){
                    T item = adapter.getItem(arg2);
                    if (!item.isEmpty()){
                        if(mDragController.isDragReady()){
                        	mDropItem = -1;
                        	
                            dragItem = arg2;
                            adapter.resetGonePosition(dragItem);
                            IDragItemView itemView = adapter.getItemView(dragItem
                                    - getFirstVisiblePosition());
                            startDragThread();
                            adapter.notifyDataSetChanged();
                            mDragController.notifyDragCreated(mCurrentPage, itemView, ev);
                            requestDisallowInterceptTouchEvent(false);
                        }else{
                            adapter.setMovingState(false);
                            adapter.resetGonePosition(-1);
                            mDragController.notifyDragEnable();
                        }
                    }
                }
				return true;
			};
		});
		return super.onInterceptTouchEvent(ev);
	}
	
	public void startDragThread(){ 
		adapter.setMovingState(true);
//		mOldAnimOffset = new int[getCount()][2]; 
//		mAnimThread = new ItemMoveController(dragItem);   
	}
	
	public static int NUM_COLUMNS = 3;
	@Override
	public void setNumColumns(int numColumns) {
		super.setNumColumns(numColumns);
		NUM_COLUMNS = numColumns;
	}



	private DragAdapter<T> adapter;
	
	public void setAdapter(DragAdapter<T> adapter){
		super.setAdapter(adapter);
		this.adapter = adapter;
	}

	private void showDropAnimation(MotionEvent event){
		adapter.setMovingState(false); 
//		int dropPosition = dragItem;
//		if (mAnimThread != null && mAnimThread.getDropPosition() >= 0) {
//			dropPosition = mAnimThread.getDropPosition();
//			if (dropPosition != dragItem) {
//                IDragItemView itemView = adapter.getItemView(dropPosition);
//                if (itemView.isFileOpen()) {
//                    //添加书籍/文件夹
//                    itemView.onItemAdded(adapter.getItem(dragItem), new ICallBack() {
//
//                        @Override
//                        public Object onResult(Object... params) {
//                            mDragController.notifyFileCreateOrUpdate(mAnimThread.getDropPosition(),
//                                    dragItem);
//                            showItemDeleteMoveAnim(dragItem, false);
//                            return null;
//                        }
//                    });
//                    return;
//                }
//            }
//		}
		final int dropItem = mDropItem;
		if(dropItem >= 0 && dropItem != dragItem) {
			IDragItemView itemView = adapter.getItemView(dropItem);
			if (itemView.isFileOpen()) {
              //添加书籍/文件夹
              itemView.onItemAdded(adapter.getItem(dragItem), new ICallBack() {

                  @Override
                  public Object onResult(Object... params) {
                      mDragController.notifyFileCreateOrUpdate(dropItem,dragItem);
                      showItemDeleteMoveAnim(dragItem, false);
                      return null;
                  }
              });
              return;
			}
		}
		
        adapter.notifyDataSetChanged();
	}	 
	
//	private void notifyItemReplace(int lastPosition){
//        adapter.onItemReplace(dragItem, lastPosition);
//		T oldData = adapter.getItem(dragItem);
//		adapter.resetGonePosition(lastPosition);
//		adapter.removeItem(dragItem);
//		adapter.addItem(lastPosition, oldData);
//        adapter.notifyDataSetChanged();
//	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			return setOnItemLongClickListener(ev);
		}
		return super.onInterceptTouchEvent(ev);
	}

    private int mTop = -1;
//	private boolean resetParam = false;
	/**
	 * 多item移动
	 * @param event
	 */
	public void onItemsMove(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY() - mTop;
		int dropPosition = pointToPosition(x, y);

//		if (mAnimThread == null){
//            startDragThread();
//        }
//		int orgPosition = mAnimThread.getDropPosition();
		int orgPosition = mDropItem;
		if (orgPosition != -1 && orgPosition != dropPosition) {
            IDragItemView itemView = adapter.getItemView(orgPosition);
            if (itemView.isFileOpen()) {
//                mAnimThread.setDropPosition(-1);
            	mDropItem = -1;
                itemView.closeFile();
            }
		}
		if (dropPosition != AdapterView.INVALID_POSITION && dropPosition != dragItem){
            if (adapter.getItem(dropPosition).isEmpty()){
                return;
            }
			T dragData = adapter.getItem(dragItem);
//			if (dragData.isFile() || dragData.isInFile()) {//文件夹形式
//				autoDrag(dropPosition);
//			}else{
//                IDragItemView itemView = adapter.getItemView(dropPosition);
//                if (itemView.point2Position(x, y)) {
//                    if (itemView.closeFile()) {
//                        autoDrag(dropPosition);
//                    }
//                } else {
//                    itemView.openFile();
//                    mAnimThread.setDropPosition(dropPosition);
//                }
//			}
			if (dragData.isFile() || dragData.isInFile()) {//文件夹形式
				mDropItem = dropPosition;
			}else {
				IDragItemView itemView = adapter.getItemView(dropPosition);
				itemView.openFile();
//				mAnimThread.setDropPosition(dropPosition);
				mDropItem = dropPosition;
			}
		} 
	}  
	
//	private void autoDrag(int dropPosition){
//		mAnimThread.startMove(dropPosition);
//		postInvalidate(); 
//	}
 
 
	private void adapterDataSetChangedNotify(){
		adapter.notifyDataSetChanged(); 
	}
 
	@Override
	public void onDragViewCreate(int page, IDragItemView itemView, MotionEvent event) {
        mTop = getTop();
        ViewParent parent = getParent();
        int max = 7, i=0;
        while (true){
            if (parent == null || parent instanceof DragLayer || i > max){
                return;
            }
            mTop += ((View)parent).getTop();
            i++;
            parent = parent.getParent();
        }
	}

	@Override
	public void onDragViewDestroy(int page, MotionEvent event) {
		if(page == mCurrentPage)
			showDropAnimation(event); 
	}

	@Override
	public void onItemMove(int page, MotionEvent event) {
		if(page == mCurrentPage){
			onItemsMove(event);
		}
	}
	
	@Override
	public void onPageChange(int lastPage, int currentPage) {
		if(lastPage == mCurrentPage){//添加数据到该页尾部 
			adapter.setMovingState(false); 
			//stopDragThread();
			mDragController.notifyPageChangeRemoveDragItem(lastPage, currentPage, adapter.remove(dragItem));
			adapterDataSetChangedNotify();
		}else if(currentPage == mCurrentPage){//移除该页第一项到上一个页面
			adapter.setMovingState(true);  
			dragItem = (lastPage < currentPage? 0:adapter.getCount()-1); 
			adapter.resetGonePosition(lastPage < currentPage? 0:adapter.getCount()-1);
			adapterDataSetChangedNotify();
		}
	}
 
	@SuppressWarnings("hiding")
	@Override
	public <T> void onPageChangeRemoveDragItem(int lastPage, int currentPage,
			T object) {
		// TODO Auto-generated method stub
		if(lastPage == mCurrentPage){//添加数据到该页尾部
			 
		}else if(currentPage == mCurrentPage){//移除该页第一【或最后一个】项到上一个页面
			mDragController.notifyPageSnapeReplaceFirstItem(lastPage, currentPage, 
					adapter.replace((lastPage < currentPage? 0:adapter.getCount()-1), object));
			adapterDataSetChangedNotify();
		}
	}

	@SuppressWarnings("hiding")
	@Override
	public <T> void onPageChangeReplaceFirstItem(int lastPage,
			int currentPage, T object) {
		if(lastPage == mCurrentPage){//添加数据到该页尾部 
			adapter.add((lastPage < currentPage? adapter.getCount() :0), object);
			adapterDataSetChangedNotify();
			mDragController.notifyPageChangeFinish();
		}else if(currentPage == mCurrentPage){//移除该页第一项到上一个页面
			startDragThread();
		}
	}

	@Override
	public void onPageChangeFinish() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDragEnable() {
		// TODO Auto-generated method stub
        adapter.enableDrag();
	}

	@Override
	public void onDragDisable() {
		// TODO Auto-generated method stub
		adapter.disableDrag();
	}

	@Override
	public void onItemDelete(int page, int position) {
		// TODO Auto-generated method stub 
	}
 
 
	private void showItemDeleteMoveAnim(int position, boolean showDeleteAnim){
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(adapter.getCount() == 0){
					mDragController.notifyDeleteItemInPage(-1, 1, -1, -1 , null);
				}else{
					adapterDataSetChangedNotify();
				}
			}
		}, DURATION + 50);
		int MoveNum = adapter.getCount()- 1 - position;
		int dragPosition = position;
		if (MoveNum != 0) { 
			int itemMoveNum = Math.abs(MoveNum);
			boolean finalItem = false;
			for (int i = 0; i < itemMoveNum; i++) {
				int holdPosition = dragPosition + 1; 
				ViewGroup moveView = (ViewGroup) getChildAt(holdPosition);
				if(moveView == null){
					continue;
				}
				Rect rect = new Rect();
				moveView.getLocalVisibleRect(rect); 
				if (rect.top == 0 || finalItem) { 
					Animation animation = getMoveAnimation(moveView.getLeft(), moveView.getTop(),
							getChildAt(dragPosition).getLeft(),getChildAt(dragPosition).getTop(),moveView.getLeft(), moveView.getTop()); 
					dragPosition = holdPosition;
					moveView.startAnimation(animation);
				} 
				finalItem = rect.top == 0; 
			}
		} 
		if (!showDeleteAnim) {
			return;
		}
		ViewGroup itemView = (ViewGroup) getChildAt(position);
		Animation removeAnimation = getRemoveAnimation(); 
		itemView.startAnimation(removeAnimation);
	}

	@SuppressWarnings("hiding")
	@Override
	public <T> void onItemDelete(int totalPage, int page, int removePage,  int position, T object) {
		// TODO Auto-generated method stub
		if(totalPage >= 0){ 
			if(mCurrentPage == page){
				if(page == removePage){//当前页面 执行移位操作
					showItemDeleteMoveAnim(position, true);
					adapter.remove(position);
					if(object != null){
						adapter.add(adapter.getCount(), object);
					}else{
					}
				} else {// 删除页面以后的页面
					if (totalPage == page) {
						mDragController.notifyDeleteItemInPage(totalPage, page - 1,
								removePage, position, adapter.remove(0));
						if (adapter.getCount() == 0) {
							mDragController.notifyDeleteItemInPage(-1, -1, removePage, -1,null);
						}
					} else {
						mDragController.notifyDeleteItemInPage(totalPage, page - 1,
								removePage, position, adapter.remove(0));
						adapter.add(adapter.getCount(), object);
					}
					adapterDataSetChangedNotify();
				}
			}
		}
		
	} 
	


	/**
	 * 获取移动动画
	 * @param x
	 * @param y
	 * @param toX
	 * @param toY
	 * @return
	 */
	public Animation getMoveAnimation(float x, float y,float toX,float toY, float fromX, float fromY) {
		//TranslateAnimation go = new TranslateAnimation(0,toX - x, 0,toY -y);  
		TranslateAnimation go = new TranslateAnimation(x-fromX,toX - fromX, y -fromY,toY -fromY); 
		go.setFillAfter(true);
		go.setDuration(DURATION);
		go.setInterpolator(new AccelerateInterpolator());
		return go;
	}

	/**
	 * 获取移动动画
	 * @return
	 */
	public Animation getRemoveAnimation() {
		ScaleAnimation removeAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		removeAnimation.setFillAfter(true);
		removeAnimation.setDuration(DURATION);
		return removeAnimation;
	}
	

	private Rect mTmpRect = new Rect();
	//为了能够搜索到隐藏的项，重载
	@Override
	public int pointToPosition(int x, int y) {
        Rect frame = mTmpRect;
        
        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i); 
            
            child.getHitRect(frame);
            if (frame.contains(x, y)) {
                return getFirstVisiblePosition() + i;
            }
        }
        return INVALID_POSITION;
    }
	
//	private class ItemMoveController{
//		
//		private int mDropPosition = -1;
//		private int mDragPosition = -1;  
//		public ItemMoveController(int emptyPosition) {
//			mDragPosition = emptyPosition; 
//		} 
//		
//		public void startMove(final int dropPosition) {
//			if(dropPosition == mDragPosition) {
//				return;
//			}
//			LogUtil.e("DrawGridView", "start move");
//			mDropPosition = dropPosition;
//			int direction = (int)Math.signum(mDropPosition - mDragPosition);
//			while(true) { 
//				if(mDragPosition != mDropPosition && mDropPosition >= 0 && mDropPosition <= getCount() -1) {
//					LogUtil.e("DrawGridView", "Thread hasCode:" + String.valueOf(this.hashCode()));
//					final int toPosition = mDragPosition;
//					mDragPosition += direction;
//					
//					if(mDragPosition < 0 || mDragPosition > getCount() - 1) {
//						direction = -direction;
//					}else {
//						setItemMoveAnim(mDragPosition, toPosition);
//					}
//					if(direction != 0) {
//						continue;
//					}
//				} 
//				break;
//			}
//			new Handler().postDelayed(new Runnable() {
//				
//				@Override
//				public void run() {
//					notifyItemReplace(dropPosition);
//					dragItem = dropPosition;
//					startDragThread();
//				}
//			}, DURATION + 50);
//		}
//		
//		public void setDropPosition(int dropPosition){
//			this.mDropPosition = dropPosition;
//		}
//		
//		public int getDropPosition() {
//			return mDropPosition;
//		} 
//		 
//	} 

	//fromPosition 要移动的项的位置
	//toPosition 目标位置
//	private void setItemMoveAnim(int fromPosition, int toPosition){
//		
//		if(mAnimThread == null) {
//			return;
//		}
//		
//		int oldFromPosition = fromPosition;
//		
//		//调整动画对象,用存储的已移动x,y计算被哪个位置占用
//		if(mOldAnimOffset[fromPosition][0] != 0) {	//位置被其它项占用，计算占用的项
//			if(mOldAnimOffset[fromPosition][1] != 0) {	//有换行
//				fromPosition -= (mOldAnimOffset[fromPosition][1] * (mOldAnimOffset[fromPosition][0] / mOldAnimOffset[fromPosition][0]));
//			}else {		//无换行
//				fromPosition -= mOldAnimOffset[fromPosition][0];
//			}
//		}
//		
//		View moveView = getChildAt(fromPosition - getFirstVisiblePosition());
//		if(moveView != null){
//			
//			int fromY = mOldAnimOffset[fromPosition][1];
//			int toY = toPosition / NUM_COLUMNS - fromPosition / NUM_COLUMNS;
//			int fromX = mOldAnimOffset[fromPosition][0];
//			int toX = toPosition  - fromPosition + -toY * NUM_COLUMNS;
//			
//			mOldAnimOffset[fromPosition][0] = toX;
//			mOldAnimOffset[fromPosition][1] = toY;
//			
//			//如果目标位置是拖动项的话
//			if(toPosition == dragItem) {
//				mOldAnimOffset[toPosition][0] = toX;
//				mOldAnimOffset[toPosition][1] = toY;
//			}else if(oldFromPosition == dragItem) {
//				mOldAnimOffset[oldFromPosition][0] = 0;
//				mOldAnimOffset[oldFromPosition][1] = 0;
//			} 
//			
//			Animation anim = new TranslateAnimation(Animation.ABSOLUTE, fromX * mAnimDistanceX
//					, Animation.ABSOLUTE, toX * mAnimDistanceX
//					, Animation.ABSOLUTE, fromY * mAnimDistanceY
//					, Animation.ABSOLUTE, toY * mAnimDistanceY);
//			
//			anim.setDuration(DURATION);
//			anim.setFillAfter(true);
//			
//			moveView.startAnimation(anim);
//			
//		}
//	}

	private int mAnimDistanceX;
	private int mAnimDistanceY;
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if(mAnimDistanceX <= 0) {
			mAnimDistanceX = getChildCount() > 1 ? getChildAt(1).getLeft() - getChildAt(0).getLeft() : 0;
		}
		
		if(mAnimDistanceY <= 0) {
			mAnimDistanceY = getChildCount() > NUM_COLUMNS ? getChildAt(NUM_COLUMNS).getTop() - getChildAt(0).getTop() : 0;
		}
	}

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);   
		super.onMeasure(widthMeasureSpec, expandSpec); 
	}

	@Override
	public void onFileCreateOrUpdate(int fileData,
			int itemData) {
		
	}
	
/*
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		System.out.println("dispatchKeyEvent event GRID");
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (getKeyDispatcherState() == null) {
				return super.dispatchKeyEvent(event);
			}
			System.out.println("dispatchKeyEvent GRID");

			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getRepeatCount() == 0) {
				KeyEvent.DispatcherState state = getKeyDispatcherState();
				if (state != null) {
					state.startTracking(event, this);
				}
				return super.dispatchKeyEvent(event);
			} else if (event.getAction() == KeyEvent.ACTION_UP) {
				KeyEvent.DispatcherState state = getKeyDispatcherState();
				if (state != null && state.isTracking(event)
						&& !event.isCanceled() && DragController.getInstance().isDragReady()) {
					DragController.getInstance().notifyDragDisable();
					return true;
				}
			}
			return super.dispatchKeyEvent(event);
		} else {
			return super.dispatchKeyEvent(event);
		}
	}*/

}









