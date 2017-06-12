package com.lectek.android.lereader.ui.basereader_leyue.digests;

import java.util.ArrayList;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsSpan;
/**
 * @author linyiwei
 * @date 2012-03-23
 * @email 21551594@qq.com
 *
 */
public abstract class AbsTextSelectHandler{
	private static final String TAG = "TextSelect";
	
	private static final float ZOOM_FACTOR = 1.5f;
	
	private static final float ZOOM_RECT_W_FACTOR = 0.4f;
	
	private static final float ZOOM__RECT_H_FACTOR = 0.10f;
	
	private static final float EFFECTIVE_RECT = ViewConfiguration.getTouchSlop();
	
	protected SelectData mSelectData;
	
	private String mContentID;
	
	private int mChaptersId;
	
	private float [] mOffset;
	
	private boolean isSelect;
	
	private boolean isPressInvalid;
	
	private ISelectorListener mSelectorLocationListener;
	
	private IViewInformer mViewInformer;
	
	private Handler mHandler = new Handler(Looper.getMainLooper());
		
	private boolean mHasLongPress;
	
	private LongPressRunnable mLongPressRunnable;
	
	private boolean mHasConsume = false;
	
	private PointF mTouchDownPoint;
	
	private PointF mCurrentTouchPoint;
	
	private boolean isFirstDraw = true;
	
	private boolean isFirstTouchPeriod = true;
	
	private boolean isDown = false;
	
	private Paint mPaint;
		
	private Rect mTopSelectCursor = new Rect();
	
	private Rect mBottomSelectCursor = new Rect();
	
	private Rect mSelectCursorSize = new Rect();
	
	private Rect mSetZoomRect = new Rect();
	
	private boolean isSelfInvalidate = false;
	
	private TouchSelectRect mCurrentTouchSelectCursor = null;
		
	private int mHeight;
	
	private int mWidth;
	
	private Vibrator mVibrator;
	
	private boolean isEdit = false;
	
	private String mChaptersName;

	private String mContentName;
	
	private int mFromType;
	
	private String author;
	
	private ArrayList<BookDigests> mShowBookDigestsList = new ArrayList<BookDigests>();

	private MotionEvent mTempMotionEvent;
	
	public AbsTextSelectHandler(int width , int height){
		mChaptersId  = -1;
		mContentID = "";
		isSelect = false;
		mOffset = new float [2];
		mTouchDownPoint = new PointF();
		mCurrentTouchPoint = new PointF();
		mSelectData = new SelectData();
		mPaint = new Paint();
		mPaint.setTextAlign(Align.CENTER);
		mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);   
		mSelectCursorSize.set(0,0,getTopSelectCursorBitmap().getWidth(),getTopSelectCursorBitmap().getHeight());
		mWidth = width;
		mHeight = height;
		mSetZoomRect.set(0, 0,(int)( mWidth * ZOOM_RECT_W_FACTOR ),  (int)( mHeight * ZOOM__RECT_H_FACTOR ));
	}

    /**
     * 是否初始化成功
     * @return
     */
    public boolean isInit(){
        return mWidth >0 && mHeight > 0;
    }

	public boolean isChangeSize(int width , int height){
		return width != mWidth || height != mHeight;
	}
	
	public boolean isSelect() {
		return isSelect;
	}

	
	public void setRectOffset(float x, float y) {
		mOffset[0] = x;
		mOffset[1] = y;
	}

	
	public float[] getRectOffset() {
        float [] temp = new float[2];
		System.arraycopy(mOffset, 0, temp, 0, 2);
		return temp;
	}

	
	public Rect getSelectCursorRect(boolean isTop) {
		if(!isSelect()){
			return null;
			
		}
		Rect rect = null;
		if(isTop){
			if(mTopSelectCursor != null){
				rect = new Rect(mTopSelectCursor);
			}
		}else{
			if(mBottomSelectCursor != null){
				rect = new Rect(mBottomSelectCursor);
			}
		}
		return rect;
	}
	
	public void setSelectorLocationListener(ISelectorListener selectorLocationListener) {
		mSelectorLocationListener = selectorLocationListener;
	}
	
	public void setViewInformer(IViewInformer viewInformer) {
		mViewInformer = viewInformer;
	}
	
	public void setSelect(boolean isSelect) {
		if(this.isSelect != isSelect){
			this.isSelect = isSelect;
			onSelectChangeCallBack(isSelect);
		}
	}
	
	public void handlerDrawPre() {
		if(isSelect()){
			BookDigests currentBookDigests = mSelectData.getCurrentBookDigests();
			int start = currentBookDigests.getPosition();
			if(isFirstDraw){
				start = findIndexByLocation((int)mCurrentTouchPoint.x,(int)mCurrentTouchPoint.y);
				if(start != -1){
					mTopSelectCursor.set(newSelectCursor(true,start));				
					mBottomSelectCursor.set(newSelectCursor(false,start));
					mCurrentTouchSelectCursor = new TouchSelectRect(true,mTopSelectCursor);
					currentBookDigests.setPosition(start);
					currentBookDigests.setCount(1);
				}
			}
			if(currentBookDigests != null && start != -1){
				if(isSelfInvalidate){
					int end = start + currentBookDigests.getCount();
					mViewInformer.setBookDigestsSpan(currentBookDigests.mBookDigestsSpan, start, end);
				}
			}
		}
	}
	
	public void handlerDrawPost(Canvas canvas,Bitmap bitmap) {
		LogUtil.i(TAG, "postDrawSelectText isFirstDraw="+isFirstDraw);
		if(isSelect()){
			if(isFirstDraw){
				if(mCurrentTouchSelectCursor != null){
					onInit(mCurrentTouchSelectCursor.getX(),mCurrentTouchSelectCursor.getY(), 
								getScaleBitmap(bitmap,mCurrentTouchSelectCursor), this);
					vibrateStartingSelectorSuccess();
				}else{
					vibrateStartingFail();
					setSelect(false);
					return;
				}
				isFirstDraw = false;
			}else{
				if(isSelfInvalidate){
					if(!isDown){
						onPause(mCurrentTouchPoint.x,mCurrentTouchPoint.y, this);
					}else{
						if(mCurrentTouchSelectCursor != null){
							onChange(mCurrentTouchSelectCursor.getX(),mCurrentTouchSelectCursor.getY(), 
									getScaleBitmap(bitmap,mCurrentTouchSelectCursor), this);
						}
					}
				}
			}
			drawSelectCursor(canvas);
		}
		if(isSelfInvalidate){
			isSelfInvalidate = false;
		}
	}
	
	public boolean handlerTouch(final MotionEvent ev,ITouchEventDispatcher touchEventDispatcher) {
        if(mTempMotionEvent == ev){
            return false;
        }
        int key = ev.getAction();
        final float[] offset = getRectOffset();
        mCurrentTouchPoint.set(ev.getX() + offset[0], ev.getY() + offset[1]);

        switch (key) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.v(TAG, "ACTION_DOWN"+ev.toString());
                isPressInvalid = false;
                mCurrentTouchSelectCursor = null;
                mHasConsume = true;
                mHasLongPress = false;
                isDown = true;
                mTouchDownPoint.set(ev.getX(), ev.getY());

                if(!isSelect()){
                    postCheckForLongClick(0);
                }else{
                    checkCurrentTouchSelectCursor((int)(ev.getX()+ offset[0]), (int)(ev.getY() + offset[1]));
                }
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtil.v(TAG, "ACTION_MOVE");
                if(!mHasConsume || !isDown){
                    mHasConsume = false;
                    break;
                }
                float move = PointF.length(ev.getX() - mTouchDownPoint.x, ev.getY() - mTouchDownPoint.y);

                if(move > EFFECTIVE_RECT){
                    isPressInvalid = true;
                }
                LogUtil.v(TAG, "isPressInvalid move="+move);
                if(isPressInvalid){
                    removeLongPressCallback();
                    if( !isSelect() ){
                        if( !mHasLongPress ){
                            mHasConsume = false;
                            mTempMotionEvent = MotionEvent.obtain(ev);
                            mTempMotionEvent.setAction(MotionEvent.ACTION_DOWN);
                            mTempMotionEvent.setLocation(mTouchDownPoint.x, mTouchDownPoint.y);
                            touchEventDispatcher.dispatchTouchEventCallBack(mTempMotionEvent);
                            mTempMotionEvent = null;
                            if(isEdit){
                                closeEdit();
                            }
                        }
                    }else{
                        //防止当此事件和长按回调同时发生时，导致书摘生命周期紊乱问题
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                onMoveCallBack(ev.getX() + offset[0], ev.getY() + offset[1]);
                            }
                        };
                        if(!isFirstDraw){
                            runnable.run();
                        }else{
                            mHandler.post(runnable);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                LogUtil.v(TAG, " ACTION_UP");
                isFirstTouchPeriod = false;
                if(!mHasConsume || !isDown){
                    mHasConsume = false;
                    break;
                }
                isDown = false;
                if(!mHasLongPress && !isPressInvalid){
                    removeLongPressCallback();
                    onPressCallBack(ev,touchEventDispatcher);
                }else if(isSelect()){
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                LogUtil.v(TAG, " ACTION_CANCEL");
                if(!mHasConsume || !isDown){
                    mHasConsume = false;
                    break;
                }
                removeLongPressCallback();
                isFirstTouchPeriod = false;
                isDown = false;
                if(isSelect()){
                    invalidate();
                }
                break;
            default:
                break;
        }
        return mHasConsume || isSelect();
    }
	
	public void updateSelectTexts(String contentId, int chaptersId,String contentName ,String chaptersName,int fromType, String author) {
		releaseSpan();
		mChaptersId = chaptersId;
		mChaptersName = chaptersName;
		mFromType = fromType;
		this.author = author;
		if(mContentID != null && mContentID.equals(contentId)){
			return;
		}
		mSelectData.clear();
		mContentID = contentId;
		mContentName = contentName;
		loadData(contentId);
	}
	
	public ArrayList<BookDigests> getBookDigestsData(){
		return mSelectData.getAllBookDigestsList();
	}
	
	public void noticeDataChanges() {
		reLoadView();
	}
	
	
	public BookDigests getCurrentBookDigests() {
		return mSelectData.getCurrentBookDigests();
	}

	
	public String getData(BookDigests bookDigests) {
		String data = null;
		if(bookDigests != null){
			int start = bookDigests.getPosition();
			int end = bookDigests.getPosition() + bookDigests.getCount();
			data = getData(start, end);
		}
		if(data == null){
			data = "";
		}
		return data;
	}
	
	protected void onInit(float x, float y,Bitmap bitmap,AbsTextSelectHandler textSelectHandler){
		LogUtil.v(TAG, "onInit");
		if(mSelectorLocationListener != null){
			mSelectorLocationListener.onInit(x, y, bitmap, textSelectHandler);
		}
	}
	
	protected void onChange(float x, float y,Bitmap bitmap,AbsTextSelectHandler textSelectHandler){
		LogUtil.v(TAG, "onChange");
		if(mSelectorLocationListener != null){
			mSelectorLocationListener.onChange(x, y, bitmap, textSelectHandler);
		}
	}
	
	protected void onPause(float x, float y,AbsTextSelectHandler textSelectHandler){
		LogUtil.v(TAG, "onPause");
		if(mSelectorLocationListener != null){
			mSelectorLocationListener.onPause(x, y, textSelectHandler);
		}
	}
	
	protected void onStop(AbsTextSelectHandler textSelectHandler){
		LogUtil.v(TAG, "onStop");
		if(mSelectorLocationListener != null){
			mSelectorLocationListener.onStop(textSelectHandler);
		}
	}
	
	protected void onOpenEditView(float x, float y,BookDigests bookDigests,AbsTextSelectHandler textSelectHandler){
		LogUtil.i(TAG, "onOpenEditView");
		if(mSelectorLocationListener != null){
			mSelectorLocationListener.onOpenEditView(x, y, bookDigests, textSelectHandler);
		}
	}
	
	protected void onCloseEditView(AbsTextSelectHandler textSelectHandler){
		LogUtil.i(TAG, "onCloseEditView");
		if(mSelectorLocationListener != null){
			mSelectorLocationListener.onCloseEditView(textSelectHandler);
		}
	}
	
	protected void checkCurrentTouchSelectCursor(int x,int y){
		Rect tempTopSelectCursor = new Rect(mTopSelectCursor);
		Rect tempBottomSelectCursor = new Rect(mBottomSelectCursor);
		tempTopSelectCursor.inset(-10, -10);
		tempBottomSelectCursor.inset(-10, -10);
		if(tempTopSelectCursor.contains(x , y)){
			mCurrentTouchSelectCursor = new TouchSelectRect(true, mTopSelectCursor);
			LogUtil.i(TAG, "set mCurrentTouchSelectCursor isLeft " +
					"x="+mCurrentTouchSelectCursor.getX()+" y="+mCurrentTouchSelectCursor.getY());
		}else if(tempBottomSelectCursor.contains(x , y)){
			mCurrentTouchSelectCursor = new TouchSelectRect(false, mBottomSelectCursor);
			LogUtil.i(TAG, "set mCurrentTouchSelectRect isRight " +
					"x="+mCurrentTouchSelectCursor.getX()+" y="+mCurrentTouchSelectCursor.getY());
		}else{
			LogUtil.i(TAG, "set mCurrentTouchSelectRect is null");
		}
	}
	
	protected void drawSelectCursor(Canvas canvas){
		canvas.save();
		mPaint.setColor(Color.BLACK);
        canvas.drawBitmap(getTopSelectCursorBitmap(), mTopSelectCursor.left, mTopSelectCursor.top + 32, mPaint);
        canvas.drawBitmap(getBottomSelectCursorBitmap(), mBottomSelectCursor.left, mBottomSelectCursor.top - 32, mPaint);
//        mPaint.setColor(0x99ffffff);
//        canvas.drawRect(new Rect(
//        		 (int) ( mCurrentTouchPoint.x - 20 )
//        		, (int) ( mCurrentTouchPoint.y - 20 )
//        		, (int) ( mCurrentTouchPoint.x + 20 )
//        		,  (int) ( mCurrentTouchPoint.y + 20 ) ), mPaint);
        canvas.restore();
	}
	
	protected void setSelectCursor(int tempPosition , int position){
		if(tempPosition == position){
			return;
		}
		if(mCurrentTouchSelectCursor.isTop){
			int count = mSelectData.getCurrentBookDigests().getCount();
			if(tempPosition > position + count - 1){
				tempPosition = position + count - 1;
			}
			mTopSelectCursor.set(newSelectCursor(true,tempPosition));
			mSelectData.getCurrentBookDigests().setPosition(tempPosition);
			mSelectData.getCurrentBookDigests().setCount(count + position - tempPosition);
			LogUtil.i(TAG, "set mLeftSelectCursor "+mTopSelectCursor.toString());
		}else{
			if(tempPosition < position){
				tempPosition = position;
			}
			mBottomSelectCursor.set(newSelectCursor(false,tempPosition));
			mSelectData.getCurrentBookDigests().setCount(tempPosition - position + 1);
			LogUtil.i(TAG, "set mRightSelectCursor "+mTopSelectCursor.toString());
		}
	}
	
	protected Rect newSelectCursor(boolean isLeft,int position){
		Rect rectCursor = new Rect(mSelectCursorSize);
		Rect rect = findRectByPosition(position);
		if(rect != null){
			if(isLeft){
				rectCursor.offset(rect.left - rectCursor.width()/2 , rect.top - rectCursor.height());
	//			rectCursor.set(rectCursor.left, rectCursor.top, rectCursor.right, rectCursor.bottom);
			}else{
				rectCursor.offset(rect.right - rectCursor.width()/2, rect.top + rect.height());
	//			rectCursor.set(rectCursor.left, rectCursor.top, rectCursor.right, rectCursor.bottom);
			}
		}
		return rectCursor;
	}
	
	protected Bitmap getScaleBitmap(Bitmap bitmap,TouchSelectRect touchSelectRect){
		if(touchSelectRect == null || bitmap == null){
			return null; 
		}
		Rect setZoomRect = new Rect(mSetZoomRect);
		Rect src = new Rect(0,0,(int)(setZoomRect.width() / ZOOM_FACTOR ),(int) (setZoomRect.height() / ZOOM_FACTOR));
		Rect selectrect = null;
		float x = 0;
		
		BookDigests currentBookDigests = mSelectData.getCurrentBookDigests();
		int position = 0;
		if(touchSelectRect.isTop){
			position = currentBookDigests.getPosition();
			selectrect = new Rect(findRectByPosition(position));
			x = selectrect.left;
		}else{
			position = currentBookDigests.getPosition() + currentBookDigests.getCount() - 1;
			selectrect = new Rect(findRectByPosition(position));
			x = selectrect.right;
		}
		src.offset((int) ( x - src.width() / 2 ), selectrect.centerY() - src.height() / 2);
		if(src.left < 0){
			src.offset(-src.left, 0);
		}
		if(src.right > mWidth){
			src.offset( - ( src.right - mWidth ), 0);
		}
		if(src.top < 0){
			src.offset( 0 , -src.top);
		}
		if(src.bottom > mHeight){
			src.offset(0 , - ( src.bottom - mHeight ));
		}
		Bitmap tempBitmap = Bitmap.createBitmap(setZoomRect.width(),setZoomRect.height(),Bitmap.Config.ARGB_8888);
		mPaint.setColor(Color.BLACK);
		Canvas canvas = new Canvas(tempBitmap);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
		canvas.save();
		canvas.drawBitmap(bitmap, src, setZoomRect, mPaint);
		canvas.restore();
		return tempBitmap;
	}
	
	
	protected ArrayList<BookDigests> getBookDigests(int start, int end){
		ArrayList<BookDigests> dataArrayList = new ArrayList<BookDigests>();
		ArrayList<BookDigests> targetArrayList = mSelectData.getBookDigestsList();
		if(targetArrayList == null){
			return dataArrayList;
		}
		int position;
		int count;
		for(BookDigests bookDigests : targetArrayList){
			position = bookDigests.getPosition();
			count = bookDigests.getCount();
			if(computeIntersect(position,position + count - 1,start,end) != null){
				dataArrayList.add(bookDigests);
				continue;
			}
			
		}
		return dataArrayList;
	}
	
	protected int[] computeIntersect(BookDigests bookDigests,int targetStar , int targetEnd){
		int position = bookDigests.getPosition();
		int count = bookDigests.getCount();
		if(position == -1){
			return null;
		}
		return computeIntersect(position,position + count - 1,targetStar,targetEnd);
	}
	
	
	protected int[] computeIntersect(int star,int end,int targetStar , int targetEnd){
		int [] positions = null;
		if(star < targetStar && end > targetEnd){
			positions = new int [2];
			positions[0] = targetStar;
			positions[1] = targetEnd;
		}else if(star >= targetStar && star <= targetEnd){
			positions = new int [2];
			positions[0] = star;
			if(end > targetEnd){
				positions[1] = targetEnd;
			}else{
				positions[1] = end;
			}
		}else if(end >= targetStar && end <= targetEnd){
			positions = new int [2];
			positions[1] = end;
			if(star < targetStar){
				positions[0] = targetStar;
			}else{
				positions[0] = star;
			}
		}
		return positions;
	}
	
	public void closeEdit(){
		if(this.isEdit){
			onCloseEditView(this);
			isEdit = false;
		}
	}
	
	public boolean isEdit(){
		return isEdit;
	}
	
	protected void onSelectChangeCallBack(boolean isSelect){
		LogUtil.i(TAG, " onSelectChangeCallBack isSelect="+isSelect);
		if(isSelect){
			isFirstDraw = true;
			isFirstTouchPeriod = true;
            BookDigests digest = newCurrentBookDigests();
            if (mViewInformer != null){
                digest = mViewInformer.newBookDigest(digest);
            }
			mSelectData.setCurrentBookDigests(digest);
		}else{
			onStop(this);
//			ArrayList<BookDigests> bookDigestsList =  mSelectData.getBookDigestsList();
//			boolean hasBookDigests = false;
//			if(bookDigestsList != null){
//				for(BookDigests data : bookDigestsList){
//					if(data.equals(mSelectData.getCurrentBookDigests())){
//						hasBookDigests = true;
//					}
//				}
//			}
			if(mSelectData.getCurrentBookDigests() != null 
//					&& !hasBookDigests
					){
				mViewInformer.deleteBookDigestsSpan(mSelectData.getCurrentBookDigests().mBookDigestsSpan);
			}
		}
		invalidate();
	}
	
	protected void onMoveCallBack(float x, float y){
		LogUtil.i(TAG, "onMoveCallBack");
		if(mCurrentTouchSelectCursor == null){
			onChange(mCurrentTouchPoint.x,mCurrentTouchPoint.y,null, this);
			return;
		}
		BookDigests currentBookDigests = mSelectData.getCurrentBookDigests();
		int oldPosition = currentBookDigests.getPosition();
		int oldCount = currentBookDigests.getCount();
		if(oldPosition != -1){
			int tempX = (int)x;
			int tempY = (int)y;
			if(!isFirstTouchPeriod){
				if(mCurrentTouchSelectCursor.isTop){
					tempY +=  mSelectCursorSize.height() * 1.5;
				}else{
					tempY -=  mSelectCursorSize.height();
				}
			}
			int tempPosition = findIndexByLocation(tempX,tempY);
			if(tempPosition != -1){
				if(!isFirstTouchPeriod){
					LogUtil.i(TAG, "onMoveCallBack computePosition="+tempPosition);
					setSelectCursor(tempPosition,oldPosition);
					LogUtil.i(TAG, "onMoveCallBack newPosition="+currentBookDigests.getPosition());
				}else{
					mTopSelectCursor.set(newSelectCursor(true,tempPosition));
					mBottomSelectCursor.set(newSelectCursor(false,tempPosition));
					currentBookDigests.setPosition(tempPosition);
				}
			}
		}
		if(currentBookDigests.isBoundChange(oldPosition, oldCount) || isFirstTouchPeriod){
//		if(true){
			invalidate();
		}
	}
	
	private Rect findRectByPosition(int position) {
		if(mViewInformer != null){
			return mViewInformer.findRectByPosition(getCurrentPage(),position);
		}
		return new Rect();
	}
	
	private int findIndexByLocation(int x, int y) {
		return findIndexByLocation(getCurrentPage(),x, y);
	}
	
	private int findIndexByLocation(int pageIndex,int x, int y) {
		if(mViewInformer != null){
			return mViewInformer.findIndexByLocation(pageIndex, x, y);
		}
		return -1;
	}

	protected void onLongPressCallBack(float x, float y){
		LogUtil.i(TAG, "onLongPressCallBack");
		mViewInformer.stopAinm();
		mLongPressRunnable = null;
		int currentPage = getCurrentPage();
		BookDigests bookDigests = null;
		if(currentPage != -1){
			bookDigests = findBookDigestsByLocal((int)x,(int)y,currentPage);				
		}
		if(bookDigests != null){
			if(!isEdit){
				onOpenEditView((int)x,(int)y,bookDigests, this);
				vibrateStartingBookDigests();
				isEdit = true;
			}
		}else{
			setSelect(true);
		}
	}
	
	protected void invalidate(){
		isSelfInvalidate = true;
		if(mViewInformer != null){
			mViewInformer.onInvalidate();
		}
	}
	
	protected boolean onPressCallBack(MotionEvent ev,ITouchEventDispatcher touchEventDispatcher){
		LogUtil.i(TAG, " onPressCallBack isSelect="+isSelect());
		if(isSelect()){
			setSelect(false);
		}else{
			if( isEdit ){
				closeEdit();
			}else if(mViewInformer != null){
				LogUtil.i(TAG, " mHasConsume="+mHasConsume);
				mTempMotionEvent = MotionEvent.obtain(ev);
				mTempMotionEvent.setAction(MotionEvent.ACTION_DOWN);
				touchEventDispatcher.dispatchTouchEventCallBack(mTempMotionEvent);
				mTempMotionEvent = MotionEvent.obtain(ev);
				mTempMotionEvent.setAction(MotionEvent.ACTION_UP);
				touchEventDispatcher.dispatchTouchEventCallBack(mTempMotionEvent);
				mTempMotionEvent = null;
			}
		}
		return true;
	}
	
	protected BookDigests findBookDigestsByLocal(int x ,int y, int currentPage){
		BookDigests bookDigests  = null;
		int position = findIndexByLocation(currentPage,x, y);
		if(position != -1){
			ArrayList<BookDigests> bookDigestsList = getBookDigests(position, position);	
			if(bookDigestsList.size() > 0){
				bookDigests = bookDigestsList.get(0);
			}
		}
		return bookDigests;
	}

	protected BookDigests newCurrentBookDigests(){
		BookDigests dataBookDigests = new BookDigests(-1, -1, "", bookDigestDefaultColor(), mChaptersId, mContentID,mFromType, author);
		dataBookDigests.setChaptersName(mChaptersName);
		dataBookDigests.setContentName(mContentName);
		return dataBookDigests;
	}
	
	private void removeLongPressCallback(){
		if(mLongPressRunnable == null){
			return;
		}
		mHandler.removeCallbacks(mLongPressRunnable);
	}
	
	private void postCheckForLongClick(int delayOffset) {
		LogUtil.i(TAG, "postCheckForLongClick");
		removeLongPressCallback();
		mLongPressRunnable = new LongPressRunnable();
        postDelayed(mLongPressRunnable,
                ViewConfiguration.getLongPressTimeout() - delayOffset);
    }
	
	private void postDelayed(Runnable run,long delay){
		mHandler.postDelayed(run, delay);
	}
	
	public void reLoadView(){
		ArrayList<BookDigests> bookDigestsList = mSelectData.getBookDigestsList();
		ArrayList<BookDigests> deletBookDigestsList = new ArrayList<BookDigests>();
		for(BookDigests oldData : mShowBookDigestsList){
			boolean isNeedDelet = true;
			if(bookDigestsList != null){
				for(BookDigests data : bookDigestsList){
					if(data.equals(oldData)){
						isNeedDelet = false;
					}
				}
			}
			if(isNeedDelet){
				deletBookDigestsList.add(oldData);
			}
		}
		mShowBookDigestsList.clear();
		if(bookDigestsList != null){
			mShowBookDigestsList = new ArrayList<BookDigests>();
            for(BookDigests data : deletBookDigestsList){
                mViewInformer.deleteBookDigestsSpan(data.mBookDigestsSpan);
            }
			for(BookDigests data : bookDigestsList){
                BookDigests digests = new BookDigests();
                digests.set(data);
                mShowBookDigestsList.add(digests);
				int start = data.getPosition();
				int end = start + data.getCount();
				mViewInformer.setBookDigestsSpan(data.mBookDigestsSpan, start, end);
			}
		}
		invalidate();
	}
	
	public void releaseSpan(){
		for (BookDigests data : mShowBookDigestsList) {
			mViewInformer.deleteBookDigestsSpan(data.mBookDigestsSpan);
		}
		mShowBookDigestsList.clear();
	}
	
	protected String getData(int start, int end){
		if(mViewInformer != null){
			return mViewInformer.getData(start,end);
		}
		return "";
	}
	
	protected int getCurrentPage(){
		int currentPage = -1;
		if(mViewInformer != null){
			currentPage = mViewInformer.getCurrentPage();
		}
		return currentPage;
	}
	
	protected void vibrateStartingSelectorSuccess(){
		long[] pattern = {0, 25, 100, 50}; 
		mVibrator.vibrate(pattern, -1);
	}
	
	protected void vibrateStartingFail(){
		long[] pattern = {0, 25};  
		mVibrator.vibrate(pattern, -1);
	}
	
	protected void vibrateStartingBookDigests(){
		long[] pattern = {0 , 50}; 
		mVibrator.vibrate(pattern, -1);
	}
	
	public TouchSelectRect getTouchSelectCursor(){
		return mCurrentTouchSelectCursor;
	}
	
	protected abstract int bookDigestDefaultColor();
	
	protected abstract Bitmap getTopSelectCursorBitmap();
	
	protected abstract Bitmap getBottomSelectCursorBitmap();
	
	protected abstract Context getContext();
		
	protected abstract void loadData(String contentId);
	
	public abstract void createOrUpdateBookDigests(BookDigests bookDigests); 
	
	public abstract void deleteBookDigests(BookDigests bookDigests);
	
	public abstract void deleteBookDigestsAll(ArrayList<BookDigests> bookDigestsList);
	
	private class LongPressRunnable implements Runnable{
		@Override
		public void run() {
			mHasLongPress = true;
			onLongPressCallBack(mCurrentTouchPoint.x,mCurrentTouchPoint.y);
		}
	}
	
	public class TouchSelectRect{
		private boolean isTop;
		private Rect rect;
		
		public TouchSelectRect(boolean isLeft,Rect rect){
			this.isTop = isLeft;
			this.rect = rect;
		}
		
		public Rect getRect() {
			return rect;
		}
		
		public void setRect(Rect rect) {
			this.rect = rect;
		}
		
		public boolean isTop(){
			return isTop;
		}
		
		public int getX(){
			return rect.centerX();
		}
		
		public int getY(){
			return rect.centerY();
		}
	}
	
	public class SelectData{
		/**
		 * 当前选择的文本
		 */
		private BookDigests mCurrentBookDigests;
		/**
		 * 已经被选择的文本
		 */
		private TreeMap<Integer, ArrayList<BookDigests>> mBookDigestsMap = new TreeMap<Integer, ArrayList<BookDigests>>();		
		
		
		public BookDigests getCurrentBookDigests() {
			return mCurrentBookDigests;
		}
		
		public void setCurrentBookDigests(BookDigests mCurrentBookDigests) {
			this.mCurrentBookDigests = mCurrentBookDigests;
		}
		
		public void updateBookDigests(BookDigests bookDigests){
			if(bookDigests == null){
				return;
			}
			int chaptersId = bookDigests.getChaptersId();
			
			if(chaptersId == -1){
				return;
			}
			
			ArrayList<BookDigests> bookDigestsList = getBookDigestsList( chaptersId );
			BookDigests tempBookDigests = findBookDigests(bookDigestsList ,bookDigests);
			if(tempBookDigests != null){
				tempBookDigests.set(bookDigests);
			}
		}
		
		public void removeBookDigestsAll(ArrayList<BookDigests> mBookDigestsList){
			if(mBookDigestsList != null){
				for(BookDigests bookDigests: mBookDigestsList){
					removeBookDigests(bookDigests);
				}
			}
		}
		
		public void removeBookDigests(BookDigests bookDigests){
			if(bookDigests == null){
				return;
			}
			int chaptersId = bookDigests.getChaptersId();
			
			if(chaptersId == -1){
				return;
			}
			
			ArrayList<BookDigests> bookDigestsList = getBookDigestsList( chaptersId );
			BookDigests tempBookDigests = findBookDigests( bookDigestsList ,bookDigests);
			if(tempBookDigests != null){
				bookDigestsList.remove(tempBookDigests);
			}
		}
		
		public boolean containsBookDigests(BookDigests bookDigests){
			for(ArrayList<BookDigests> bookDigestsList : mBookDigestsMap.values()){
				if(findBookDigests(bookDigestsList , bookDigests) != null){
					return true;
				}
			}
			return false;
		}
		
		public boolean containsChapters(int chaptersid){
			return mBookDigestsMap.containsKey(chaptersid);
		}
		
		public BookDigests findBookDigests(ArrayList<BookDigests> bookDigestsList,BookDigests bookDigests){
			if(bookDigestsList == null){
				return null;
			}
			for(BookDigests tempBookDigests: bookDigestsList){
				if(tempBookDigests.equals(bookDigests)){
					return tempBookDigests;
				}
			}
			return null;
		}
		
		public void addBookDigests(BookDigests bookDigests){
			int chaptersId = bookDigests.getChaptersId();
			if(chaptersId == -1){
				return;
			}
			addBookDigests(chaptersId,bookDigests);
		}
		
		public void addBookDigests(int chaptersId , BookDigests bookDigests){
			ArrayList<BookDigests> bookDigestsList = getBookDigestsList(chaptersId);
			if(bookDigestsList == null){
				bookDigestsList = new ArrayList<BookDigests>();
				setBookDigestsList(chaptersId,bookDigestsList);
			}
			bookDigestsList.add(bookDigests);
		}
		
		public ArrayList<BookDigests> getAllBookDigestsList() {
			ArrayList<BookDigests> allBookDigestsList = new ArrayList<BookDigests>();
			ArrayList<BookDigests> tempData;
			for(Integer integer : mBookDigestsMap.keySet()){
				tempData = mBookDigestsMap.get(integer);
				if(tempData != null){
					allBookDigestsList.addAll(tempData);
				}
			}
			return allBookDigestsList;
		}
		
		public ArrayList<BookDigests> getBookDigestsList() {
			return getBookDigestsList(mChaptersId);
		}
		
		public ArrayList<BookDigests> getBookDigestsList(int chaptersId) {
			ArrayList<BookDigests> bookDigestsList = mBookDigestsMap.get(chaptersId);
			
			return bookDigestsList;
		}
		
		public void setBookDigestsList(ArrayList<BookDigests> mBookDigestsList) {
			setBookDigestsList(mChaptersId, mBookDigestsList);
		}
		
		public void setBookDigestsList(int chaptersId,ArrayList<BookDigests> mBookDigestsList) {
			mBookDigestsMap.put(chaptersId, mBookDigestsList);
		}
		
		public void clear(){
			mBookDigestsMap.clear();
		}
	}
	
	public interface ISelectorListener{
		
		public void onInit(float x, float y,Bitmap bitmap,AbsTextSelectHandler textSelectHandler);
		
		public void onChange(float x, float y,Bitmap bitmap,AbsTextSelectHandler textSelectHandler);
		
		public void onPause(float x, float y,AbsTextSelectHandler textSelectHandler);
		
		public void onStop(AbsTextSelectHandler textSelectHandler);
		
		public void onOpenEditView(float x, float y,BookDigests bookDigests,AbsTextSelectHandler textSelectHandler);
		
		public void onCloseEditView(AbsTextSelectHandler textSelectHandler);
	}
	
	public interface IViewInformer{
		public void onInvalidate();
		
		public int getCurrentPage();
		
		public String getData(int start, int end);
		
		public void setBookDigestsSpan(BookDigestsSpan span,int start, int end);
		
		public void deleteBookDigestsSpan(BookDigestsSpan span);
		
		public int findIndexByLocation(int pageIndex,int x, int y);
		
		public Rect findRectByPosition(int pageIndex,int position);
		
		public void stopAinm();

        /**
         * 装饰digest
         * @param digest
         * @return
         */
        public BookDigests newBookDigest(BookDigests digest);
	}
	
	public interface ITouchEventDispatcher{
		public void dispatchTouchEventCallBack(MotionEvent ev);
	}
}
