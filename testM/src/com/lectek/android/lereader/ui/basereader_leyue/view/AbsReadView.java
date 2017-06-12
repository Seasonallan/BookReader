package com.lectek.android.lereader.ui.basereader_leyue.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.lib.utils.DimensionsUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.ui.basereader_leyue.ReadSetting;
import com.lectek.android.lereader.ui.basereader_leyue.ReadSetting.SettingListener;
import com.lectek.android.lereader.ui.basereader_leyue.anim.AutoAnimController;
import com.lectek.android.lereader.ui.basereader_leyue.anim.PageAnimController;
import com.lectek.android.lereader.ui.basereader_leyue.anim.PageAnimController.PageCarver;

public abstract class AbsReadView  extends View implements PageCarver,SettingListener{
	protected static final String TAG = AbsReadView.class.getSimpleName();
	protected static final int PADDING_LEFT = DimensionsUtil.dip2px(15, MyAndroidApplication.getInstance());
	protected static final int PADDING_RIGHT = DimensionsUtil.dip2px(15, MyAndroidApplication.getInstance());
	protected static final int PADDING_TOP = DimensionsUtil.dip2px(10, MyAndroidApplication.getInstance());
	protected static final int PADDING_BOTTOM = DimensionsUtil.dip2px(8, MyAndroidApplication.getInstance());
	protected static final int PADDING_CONTENT_TOP = DimensionsUtil.dip2px(15, MyAndroidApplication.getInstance());
	protected static final int PADDING_CONTENT_BOTTOM = DimensionsUtil.dip2px(2, MyAndroidApplication.getInstance());
	/** 代表初始界面*/
	protected static final int INDEX_INITIAL_CONTENT = Integer.MIN_VALUE - 1;
	protected static final int REQUEST_INDEX_INITIAL_CONTENT = -Integer.MIN_VALUE;
	/** 初始画笔*/
	protected TextPaint mTextPaint;
	/** 临时画笔*/
	protected TextPaint mTempTextPaint;
	/** 当前页*/
	protected int mCurrentPageIndex;
	/** 请求要去的页为负数值从-1开始*/
	protected int mRequestPageIndex;
	/** 当前章*/
	protected int mCurrentChapterIndex;
	/** 请求要去的章*/
	protected int mRequestChapterIndex;
	/** 阅读界面设置管理*/
	protected ReadSetting mReadSetting;
	/** 头部间距*/
	protected int mHeadspaceTop;
	/** 当前状态*/
	protected int mHeadspaceBottom;
	/** 翻页动画控制者*/
	private PageAnimController mPageAnimController;
	/** 背景图片*/
	private Drawable mBGDrawable;
	/** 记录当前使用的动画类型，用于自动动画停止后恢复原动画*/
	private int mAnimType = PageAnimController.ANIM_TYPE_PAGE_TURNING;
	private Handler mHandler;
	private Runnable mOnPageChangeRun;

	public AbsReadView(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		mHandler = new Handler(Looper.getMainLooper());
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		setDrawingCacheEnabled(false);
		mCurrentPageIndex = INDEX_INITIAL_CONTENT;
		mRequestPageIndex = REQUEST_INDEX_INITIAL_CONTENT;
		mCurrentChapterIndex = INDEX_INITIAL_CONTENT;
		mRequestChapterIndex = INDEX_INITIAL_CONTENT;
		mTextPaint = new TextPaint();
		mTempTextPaint = new TextPaint();
		mTempTextPaint.setAntiAlias(true);
		mReadSetting = ReadSetting.getInstance(getContext());
		mReadSetting.addDataListeners(this);
		setAnimType(mReadSetting.getAnimType(),true);
		loadStyleSetting();
	}
	
	private void loadStyleSetting(){
		loadStyleSetting(true);
	}
	
	private void loadStyleSetting(boolean isReLayout){
		int bgRes = mReadSetting.getThemeBGImgRes();
		if(bgRes == -1){
			mBGDrawable = new ColorDrawable(mReadSetting.getThemeBGColor());
		}else{
			mBGDrawable = getResources().getDrawable(bgRes);
		}
		mTextPaint.setColor(mReadSetting.getThemeTextColor());
		mTextPaint.linkColor = Color.BLUE;
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(mReadSetting.getMinFontSize());
		mHeadspaceTop = (int) ((int)mTextPaint.getFontSpacing() + PADDING_TOP + PADDING_CONTENT_TOP);
		mHeadspaceBottom = (int) ((int)mTextPaint.getFontSpacing() + PADDING_BOTTOM  + PADDING_CONTENT_BOTTOM);
		mTextPaint.setTextSize(mReadSetting.getFontSize());
		onLoadStyleSetting(isReLayout);
		LogUtil.i(TAG, " loadStyleSetting");
	}
	
	protected Rect newPageContenRect(){
		return new Rect(getLeft() + PADDING_LEFT
				,getTop() + mHeadspaceTop
				,getRight() - PADDING_RIGHT
				,getBottom() - mHeadspaceBottom); 
	}
	
	public void gotoChapter(int requestChapterIndex,boolean isStartAnim){
		gotoPage(requestChapterIndex, 0, isStartAnim);
	}
	
	public boolean gotoPage(int requestChapterIndex,int requestPage,boolean isStartAnim){
		if(interceptGotoPage(requestChapterIndex,requestPage)){
			setOnPageChange();
			return false;
		}
		if(mCurrentPageIndex == requestPage && mCurrentChapterIndex == requestChapterIndex){
			isStartAnim = false;
		}
		if(isStartAnim && !mPageAnimController.isAnimStop()){
			mPageAnimController.stopAnim(this);
		}
		mRequestChapterIndex = requestChapterIndex;
		mRequestPageIndex = -(requestPage + 1);
		if(!isStartAnim){
			onGotoPage(requestChapterIndex,requestPage,isStartAnim);
			mCurrentChapterIndex = requestChapterIndex;
			mCurrentPageIndex = requestPage;
			setOnPageChange();
			invalidate();
		}else if(isStartAnim){
			boolean isNext = false;
			if(mCurrentChapterIndex == INDEX_INITIAL_CONTENT){
				isNext = true;
			}else{
				if(mCurrentChapterIndex == requestChapterIndex){
					isNext = requestPage > mCurrentPageIndex;
				}else{
					isNext = requestChapterIndex > mCurrentChapterIndex;
				}
			}
			setPageProgress(requestChapterIndex, requestPage);
			onGotoPage(requestChapterIndex,requestPage,isStartAnim);
			mPageAnimController.startAnim(mCurrentPageIndex,mRequestPageIndex, isNext, this);
		}
		return true;
	}
	
	protected void setPageProgress(final int chapterIndex,final int pageIndex){
		if(mOnPageChangeRun != null){
			removeCallbacks(mOnPageChangeRun);
		}
		mOnPageChangeRun = new Runnable() {
			@Override
			public void run() {
				onSetPageProgress(chapterIndex, pageIndex);
			}
		};
		postDelayed(mOnPageChangeRun, 100);
	}
	
	private void setOnPageChange(){
		setPageProgress(mCurrentChapterIndex, mCurrentPageIndex);
	}

    /**
     * 触屏事件派遣
     */
	public boolean handlerTouchEvent(MotionEvent event) {
		if(mCurrentPageIndex != INDEX_INITIAL_CONTENT){
			mPageAnimController.dispatchTouchEvent(event, this);
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);  
		if(!mPageAnimController.dispatchDrawPage(canvas, AbsReadView.this)){
			drawPage(canvas, mCurrentPageIndex);
		}
	}

	@Override
	public void drawPage(Canvas canvas,int requestPage) { 
		int chapterIndex = 0;
		boolean isCurrentPage = false;
		if(requestPage < 0){
			requestPage = mRequestPageIndex;
			chapterIndex = mRequestChapterIndex;
		}else{
			requestPage = mCurrentPageIndex;
			chapterIndex = mCurrentChapterIndex;
			isCurrentPage = true;
		}
		if(requestPage < 0){
			requestPage = -requestPage - 1;
		}
		if(chapterIndex == INDEX_INITIAL_CONTENT || !onDrawPage(canvas,isCurrentPage, chapterIndex, requestPage)){
			drawWaitPage(canvas, false);
		}else{
            drawBookMarkTip(canvas, chapterIndex, requestPage);
        }
	}

    /**
     * 绘制书签
     */
    protected abstract void drawBookMarkTip(Canvas canvas, int chapterIndex, int pageIndex);

	protected void drawBackground(Canvas canvas){
		mBGDrawable.setBounds(getLeft(), getTop(), getRight(), getBottom());
		mBGDrawable.draw(canvas);
	}
	
	@Override
	public Integer requestPrePage() {
		//由于界面没有布局好不可能会调用此方法所以不用担心页跳转问题
		int[] locals = requestPrePage(mCurrentChapterIndex, mCurrentPageIndex);
		if(locals != null){
			if(locals[0] >= 0){
				if(!interceptGotoPage(locals[0],locals[1])){
					mRequestChapterIndex = locals[0];
					mRequestPageIndex = -(locals[1] + 1);
					setPageProgress(locals[0],locals[1]);
					return mRequestPageIndex;
				}else{
					setOnPageChange();
				}
			}else{
                gotoPreChapter();
            }
		}else{
			onNotPreContent();
		}
		return null;
	}

	@Override
	public Integer requestNextPage() {
		//由于界面没有布局好不可能会调用此方法所以不用担心页跳转问题 
		int[] locals = requestNextPage(mCurrentChapterIndex, mCurrentPageIndex); 
		if(locals != null){
			if(locals[0] >= 0){
				if(!interceptGotoPage(locals[0],locals[1])){
					mRequestChapterIndex = locals[0];
					mRequestPageIndex = -(locals[1] + 1);
					setPageProgress(locals[0],locals[1]);  
					return mRequestPageIndex;
				}else{
					setOnPageChange();
				}
			}else{
                gotoNextChapter();
            }
		}else{
			onNotNextContent();
		} 
		return null;
	}

	@Override
	public void requestInvalidate() {
		invalidate();
	}
	
	@Override
	public void onSettingChange(ReadSetting readSetting, String type) {
		if(type == ReadSetting.SETTING_TYPE_THEME
				|| type == ReadSetting.SETTING_TYPE_FONT_LINE_SPACE_TYPE
				|| type == ReadSetting.SETTING_TYPE_FONT_SIZE
                || type == ReadSetting.SETTING_TYPE_FONT_SIM){
			loadStyleSetting(type != ReadSetting.SETTING_TYPE_THEME);
		}
		if (type.equals(ReadSetting.SETTING_TYPE_ANIM)) {
			setAnimType(readSetting.getAnimType());
		} else if (type.equals(ReadSetting.SETTING_TYPE_AUTO)) {
			if(readSetting.getAutoType() == ReadSetting.AUTO_TYPE_UD){
				setAutoStart(readSetting.getAutoDelayedUD(),readSetting.isAutoStart(), readSetting.isPause());
			}else{
				setAutoStart(readSetting.getAutoDelayedUD(),false, true);
			}
		}
	}

	public void setAnimType(int animType) {
		setAnimType(animType, false);
	}

	public void setAnimType(int animType, boolean isForce) {
		if (mPageAnimController == null || mAnimType != animType || isForce) {
			mAnimType = animType;
			if (mPageAnimController != null) {
				mPageAnimController.stopAnim(this);
			}
			mPageAnimController = PageAnimController.create(getContext(),
					animType);
		}
	}

	protected boolean isAnimStop(){
		return mPageAnimController.isAnimStop();
	}
	
	protected void stopAnim(){
		mPageAnimController.stopAnim(this);
	}
	
	private void setAutoStart(int autoDelayed ,boolean autoStart,boolean isPause){
		if(!autoStart){
			if(mPageAnimController instanceof AutoAnimController){ 
				setKeepScreenOn(false);
				setAnimType(mAnimType,true);
			}
			return;
		}
		AutoAnimController autoAnimController = null;
		if(mPageAnimController instanceof AutoAnimController){
			autoAnimController = (AutoAnimController) mPageAnimController;
		}else{
			if(mPageAnimController != null){
				mPageAnimController.stopAnim(this);
			}
			mPageAnimController = PageAnimController.create(getContext(), PageAnimController.ANIM_TYPE_AUTO);
			autoAnimController = (AutoAnimController) mPageAnimController;
		}
		
		if(autoAnimController.isAnimStop()){
			autoAnimController.setAutoStartDelayedType(autoDelayed);
			int[] locals = requestNextPage(mCurrentChapterIndex, mCurrentPageIndex);
			if(!isPause){
				if(locals != null){
					if(locals[0] >= 0){
						gotoPage(locals[0], locals[1], true);
					}
				}else{
					onNotNextContent();
				}
			}
		}else{
			autoAnimController.updateState(autoDelayed,isPause,this);
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		onLoadStyleSetting(true);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}
	/**
	 * Determines the mWidth of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The mWidth of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = getSuggestedMinimumWidth();
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}

		return result;
	}

	/**
	 * Determines the mHeight of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The mHeight of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = getSuggestedMinimumHeight();
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	@Override
	public int getContentWidth() {
		return getWidth();
	}

	@Override
	public int getContentHeight() {
		return getHeight();
	}

	@Override
	public int getScreenWidth() {
		return getWidth();
	}

	@Override
	public int getScreenHeight() {
		return getHeight();
	}

	@Override
	public int getPageBackgroundColor() {
		return mReadSetting.getThemeBGColor();
	}

	@Override
	public void onStartAnim(boolean isCancel) {
	}

	@Override
	public void onStopAnim(boolean isCancel) {
		if(!isCancel){
			if(mCurrentPageIndex == -mRequestPageIndex - 1
					&& mCurrentChapterIndex == mRequestChapterIndex){
				return;
			}
			LogUtil.i(TAG,"onAnimEnd mRequestPage="+mRequestPageIndex);
			mCurrentPageIndex = -mRequestPageIndex - 1;
			mCurrentChapterIndex = mRequestChapterIndex;
			LogUtil.e("pagepage");
			setOnPageChange();
		}else{
			mRequestPageIndex = -(mCurrentPageIndex + 1);
			mRequestChapterIndex = mCurrentChapterIndex;
		}
	}

	@Override
	public int getCurrentPageIndex() {
		return mCurrentPageIndex;
	}
	
	public void gotoNextPage() {
		if(!mPageAnimController.isAnimStop()){
			mPageAnimController.stopAnim(this);
		}
		int[] locals = requestNextPage(mCurrentChapterIndex, mCurrentPageIndex);
		if(locals != null){
			if(locals[0] >= 0){
				gotoPage(locals[0], locals[1], true);
			}else{
                //TODO:下一章节数据未加载完全
            }
		}else{
			onNotNextContent();
		}
	}

	public void gotoPrePage() {
		if(!mPageAnimController.isAnimStop()){
			mPageAnimController.stopAnim(this);
		}
		int[] locals = requestPrePage(mCurrentChapterIndex, mCurrentPageIndex);
		if(locals != null){
			if(locals[0] >= 0){
				gotoPage(locals[0], locals[1], true);
			}
		}else{
			onNotPreContent();
		}
	}

	public void gotoPreChapter() {
		if(!mPageAnimController.isAnimStop()){
			mPageAnimController.stopAnim(this);
		}
		if(hasPreChapter()){
			gotoChapter(mCurrentChapterIndex - 1, true);
		}
	}

	public void gotoNextChapter() {
		if(!mPageAnimController.isAnimStop()){
			mPageAnimController.stopAnim(this);
		}
		if(hasNextChapter()){
			gotoChapter(mCurrentChapterIndex + 1, true);
		}
	}

	public boolean hasPreChapter() {
		if(getChapterSize() == null){
			return false;
		}
		return mCurrentChapterIndex > 0;
	}

	public boolean hasNextChapter() {
		if(getChapterSize() == null){
			return false;
		}
		return mCurrentChapterIndex < getChapterSize() - 1;
	}
	
	public final void runOnUiThread(Runnable action) {
        if (Thread.currentThread() != mHandler.getLooper().getThread()) {
        	mHandler.post(action);
        } else {
            action.run();
        }
    }

	/** 章节数*/
	protected abstract Integer getChapterSize();
	/**
	 * 拦截跳转动作
	 * @param chapterIndex
	 * @param pageIndex
	 * @return
	 */
	protected abstract boolean interceptGotoPage(int chapterIndex,int pageIndex);
	/**
	 * 绘制内容
	 * @param canvas
	 * @param chapterIndex
	 * @param pageIndex
	 */
	protected abstract boolean onDrawPage(Canvas canvas,boolean isCurrentPage,int chapterIndex,int pageIndex);
	/**
	 * 绘制等待界面
	 * @param canvas
	 * @param isFirstDraw
	 */
	protected abstract void drawWaitPage(Canvas canvas,boolean isFirstDraw);
	/**
	 * 设置变化回调
	 * @param isReLayout
	 */
	protected abstract void onLoadStyleSetting(boolean isReLayout);
	/**
	 * 页码变化回调
	 * @param chapterIndex
	 * @param pageIndex
	 */
	protected abstract void onSetPageProgress(int chapterIndex,int pageIndex);
	/**
	 * 跳转页回调
	 * @param chapterIndex
	 * @param pageIndex
	 */
	protected abstract void onGotoPage(int chapterIndex,int pageIndex,boolean isStartAnim);
	/**
	 * 请求下一页
	 * @param pageIndex
	 * @return 第一个位置是章节下标，第二个位置是页下标，如果返回空说明没有内容或者出错，如果放回-1说明还在等待排版
	 */
	protected abstract int[] requestNextPage(int chapterIndex, int pageIndex);
	/**
	 * 请求上一页
	 * @param pageIndex
	 * @return 第一个位置是章节下标，第二个位置是页下标，如果返回空说明没有内容或者出错，如果放回-1说明还在等待排版
	 */
	protected abstract int[] requestPrePage(int chapterIndex, int pageIndex);
	/**
	 * 提示没有上一页内容
	 */
	protected abstract void onNotPreContent();
	/**
	 * 提示没有下一页内容
	 */
	protected abstract void onNotNextContent();
}
