package com.lectek.lereader.core.text.test;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.BatteryManager;
import android.provider.Browser;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.lectek.bookformats.R;
import com.lectek.lereader.core.bookformats.PaserExceptionInfo;
import com.lectek.lereader.core.text.ClickSpanHandler;
import com.lectek.lereader.core.text.PageManager;
import com.lectek.lereader.core.text.PageManager.PageManagerCallback;
import com.lectek.lereader.core.text.SettingParam;
import com.lectek.lereader.core.text.html.DataProvider;
import com.lectek.lereader.core.text.html.HtmlParser;
import com.lectek.lereader.core.text.html.HtmlParser.TagHandler;
import com.lectek.lereader.core.text.html.ICssProvider;
import com.lectek.lereader.core.text.style.AsyncDrawableSpan;
import com.lectek.lereader.core.text.style.ClickActionSpan;
import com.lectek.lereader.core.text.style.ClickAsyncDrawableSpan;
import com.lectek.lereader.core.text.style.UrlSpna;
import com.lectek.lereader.core.text.test.PageAnimController.PageCarver;
import com.lectek.lereader.core.text.test.ReadSetting.SettingListener;
import com.lectek.lereader.core.text.test.ReaderMediaPlayer.PlayerListener;
import com.lectek.lereader.core.util.ContextUtil;
import com.lectek.lereader.core.util.LogUtil;
/**
 * 1.管理控制翻页动画.
 * 2.管理控制PageManager.
 * @author lyw
 */
public class BaseReadView extends View implements PageManagerCallback,PageCarver,SettingListener
	,ClickSpanHandler,PlayerListener{
	private static final String TAG = BaseReadView.class.getSimpleName();
	private static final int PADDING_LEFT = ContextUtil.DIPToPX(25);
	private static final int PADDING_RIGHT = ContextUtil.DIPToPX(25);
	private static final int PADDING_TOP = ContextUtil.DIPToPX(10);
	private static final int PADDING_BOTTOM = ContextUtil.DIPToPX(10);
	private static final int PADDING_CONTENT_TOP = ContextUtil.DIPToPX(10);
	private static final int PADDING_CONTENT_BOTTOM = ContextUtil.DIPToPX(10);
	/** 代表初始界面*/
	private static final int INDEX_INITIAL_CONTENT = Integer.MIN_VALUE - 1;
	private static final int REQUEST_INDEX_INITIAL_CONTENT = -Integer.MIN_VALUE;
	/** 页管理器*/
	private PageManager mPageManager;
	/** 初始画笔*/
	private TextPaint mTextPaint;
	/** 临时画笔*/
	private TextPaint mTempTextPaint;
	/** 当前页*/
	private int mCurrentPage;
	/** 请求要去的页为负数值从-1开始*/
	private int mRequestPage;
	/** 当前章*/
	private int mCurrentChapterIndex;
	/** 请求要去的章*/
	private int mRequestChapterIndex;
	/** 请求跳转的指定字符所在位置的页，-1代表未指定*/
	private int mRequestCharIndex;
	/** 页跳转动作监听*/
	private ReadViewCallback mCallback;
	/** 翻页动画控制者*/
	private PageAnimController mPageAnimController;
	/** 当前状态*/
	private int mHeadspaceTop;
	/** 当前状态*/
	private int mHeadspaceBottom;
	/** 阅读界面设置管理*/
	private ReadSetting mReadSetting;
	/** 背景图片*/
	private Drawable mBGDrawable;
	/** 章节数*/
	private Integer mChapterSize;
	/** 书籍ID*/
	private String mContentId;
	private int mLoadingPointSize;
	private long mLastDrawWaitTime;
	private ImgViewerPopWin mImgViewerPopWin;
	private NotePopWin mNotePopWin;
	private Drawable mBatteryDrawable;
	private Rect mMaxBatteryProgress;
	private int mBatteryProgress;
	private BroadcastReceiver mBatteryReceiver;
	private ReaderClockDrawable mReaderClockDrawable;
//	private Drawable mBookMarkTip;
//	private VoiceProgressSpan[] mVoiceProgressSpans;
//	private VoiceProgressSpan mLastVoiceProgressSpan;
	private Runnable mOnPageChangeRun;
	private Drawable mInitBGDrawable;
	private VideoWindow mVideoWindow;
	private int mOrientation;
	private boolean isKeyguard;

	public BaseReadView(Context context) {
		super(context);
		init();
	}

	public BaseReadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if(mBatteryReceiver == null){
			mBatteryReceiver = new BroadcastReceiver(){
				@Override
				public void onReceive(Context context, Intent intent) {
					int current = intent.getExtras().getInt(BatteryManager.EXTRA_LEVEL);
					int total = intent.getExtras().getInt(BatteryManager.EXTRA_SCALE);
					if(total == 0){
						mBatteryProgress = 0;
					}else{
						mBatteryProgress = current * 100 / total;
					}
					invalidate();
				}
		    };
		    getContext().registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		}
		mReaderClockDrawable.start();
		if(mVideoWindow == null){
			mVideoWindow = new VideoWindow((Activity) getContext());
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(mBatteryReceiver != null){
			getContext().unregisterReceiver(mBatteryReceiver);
			mBatteryReceiver = null;
		}
		mReaderClockDrawable.release();
		closeVideo();
	}
	
	@Override
	public void invalidateDrawable(Drawable drawable) {
		super.invalidateDrawable(drawable);
		if(drawable.equals(mReaderClockDrawable)){
			invalidate();
		}
	}
    
	private void init(){
		setDrawingCacheEnabled(false);
		mPageManager = new PageManager(getContext(),this);
		mBatteryProgress = 100;
		mCurrentPage = INDEX_INITIAL_CONTENT;
		mRequestPage = REQUEST_INDEX_INITIAL_CONTENT;
		mCurrentChapterIndex = INDEX_INITIAL_CONTENT;
		mRequestChapterIndex = INDEX_INITIAL_CONTENT;
		mRequestCharIndex = -1;
		mTextPaint = new TextPaint();
		mTempTextPaint = new TextPaint();
		mTempTextPaint.setAntiAlias(true);
		mReadSetting = ReadSetting.getInstance(getContext());
		mReadSetting.addDataListeners(this);
		setPageAnimController(PageAnimController.create(getContext(), PageAnimController.ANIM_TYPE_TRANSLATION));
		ReaderMediaPlayer.getInstance().addPlayerListener(this);
		mReaderClockDrawable = new ReaderClockDrawable(getContext());
		mReaderClockDrawable.setCallback(this);
		loadStyleSetting();
	}
	
	public void initView(String contentId,ReadViewCallback listener,int chapterSize,int requestChapterIndex,int requestCharIndex){
		LogUtil.e(TAG,"initView");
		mCallback = listener;
		mChapterSize = chapterSize;
		mContentId = contentId;
		mCurrentChapterIndex = requestChapterIndex;
		mRequestChapterIndex = requestChapterIndex;
		mRequestCharIndex = requestCharIndex;
		mCurrentPage = INDEX_INITIAL_CONTENT;
		mRequestPage = REQUEST_INDEX_INITIAL_CONTENT;
		mPageManager.setUnInit();
		requestLayout();
	}
	
	private void loadStyleSetting(){
		loadStyleSetting(true);
	}
	
	private void loadStyleSetting(boolean isReLayout){
		mMaxBatteryProgress = null;
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
		if(isReLayout){
			int requestCharIndex = mPageManager.findPageFirstIndex(mCurrentChapterIndex,mCurrentPage);
			if(requestCharIndex < 0){
				if(mRequestCharIndex == -1){
					mRequestCharIndex = 0;
				}
			}else{
				mRequestCharIndex = requestCharIndex;
			}
			mPageManager.setUnInit();
			mCurrentPage = INDEX_INITIAL_CONTENT;
			mRequestPage = REQUEST_INDEX_INITIAL_CONTENT;
			requestLayout();
		}else{
			mPageManager.clearPageCache();
			invalidate();
		}
		LogUtil.i(TAG, " loadStyleSetting");
	}
	
	public int findCurrentPageFirstIndex(){
		int requestPageIndex = mPageManager.findPageFirstIndex(mCurrentChapterIndex,mCurrentPage);
		if(requestPageIndex >= 0){
			return requestPageIndex;
		}
		return 0;
	}
	
	private Rect newPageContenRect(){
		return new Rect(getLeft() + PADDING_LEFT
				,getTop() + mHeadspaceTop
				,getRight() - PADDING_RIGHT
				,getBottom() - mHeadspaceBottom);
	}
	
	public void gotoChapter(int requestChapterIndex,boolean isStartAnim){
		gotoPageCharIndex(requestChapterIndex, 0, isStartAnim);
	}
	
	public void gotoPageCharIndex(int requestChapterIndex,int requestPageCharIndex,boolean isStartAnim){
		if(mCallback.checkNeedBuy(requestChapterIndex)){
			setOnPageChange();
			return;
		}
		int requestPage = mPageManager.findPageIndex(requestChapterIndex, requestPageCharIndex);
		if(requestPage >= 0){
			gotoPage(requestChapterIndex, requestPage, isStartAnim);
		}else{
			mRequestCharIndex = requestPageCharIndex;
			gotoPage(requestChapterIndex,INDEX_INITIAL_CONTENT, isStartAnim);
		}
	}
	
	public void gotoTotalPage(int requestTotalPage,boolean isStartAnim){
		int[] locals = mPageManager.findPageIndexByTotal(requestTotalPage);
		if(locals != null){
			gotoPage(locals[0], locals[1], isStartAnim);
		}
	}
	
	private void gotoPage(int requestChapterIndex,int requestPage,boolean isStartAnim){
		if(mCallback.checkNeedBuy(requestChapterIndex)){
			setOnPageChange();
			return;
		}
		if(isStartAnim && !mPageAnimController.isAnimStop()){
			mPageAnimController.stopAnim(this);
		}
		mRequestChapterIndex = requestChapterIndex;
		mRequestPage = -(requestPage + 1);
		if(!isStartAnim){
			mCurrentChapterIndex = requestChapterIndex;
			mCurrentPage = requestPage;
			setOnPageChange();
			invalidate();
		}else if(isStartAnim){
			boolean isNext = false;
			if(mCurrentChapterIndex == requestChapterIndex){
				isNext = requestPage > mCurrentPage;
			}else{
				isNext = requestChapterIndex > mCurrentChapterIndex;
			}
			setOnPageChange(requestChapterIndex, requestPage);
			mPageAnimController.startAnim(mCurrentPage,mRequestPage, isNext, this);
		}
	}
	
	private void setOnPageChange(final int chapterIndex,final int pageIndex){
		closeVideo();
		if(mOnPageChangeRun != null){
			removeCallbacks(mOnPageChangeRun);
		}
		mOnPageChangeRun = new Runnable() {
			@Override
			public void run() {
				int totalPageSize = mPageManager.getTotalPageSize();
				if(totalPageSize > 0){
					mCallback.onPageChange(mPageManager.getTotalPageIndex(chapterIndex,pageIndex),totalPageSize);
				}
			}
		};
		postDelayed(mOnPageChangeRun, 100);
	}
	
	private void setOnPageChange(){
		setOnPageChange(mCurrentChapterIndex, mCurrentPage);
	}
	
	public void setPageAnimController(PageAnimController pageAnimController){
		if(pageAnimController == null){
			return;
		}
		mPageAnimController = pageAnimController;
	}
	
	public boolean handlerTouchEvent(MotionEvent event) {
		if(mCurrentPage != INDEX_INITIAL_CONTENT && mPageManager.isFirstDraw()){
			mPageAnimController.dispatchTouchEvent(event, this);
		}
		return true;
	}
	
	public void refreshView(){
//		if(!getHolder().isCreating()){
//			return;
//		}
//		Canvas canvas = getHolder().lockCanvas();
//		if(canvas != null){
//			refreshView(canvas);
//			getHolder().unlockCanvasAndPost(canvas);
//		}
	}
	
	public void refreshView(Canvas canvas){
		if(!mPageAnimController.dispatchDrawPage(canvas, BaseReadView.this)){
			drawPage(canvas, mCurrentPage);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		refreshView(canvas);
//		refreshView();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		refreshView();
	}

	@Override
	public void invalidateView(Rect dirty) {
		invalidate();
	}
	/**
	 * 绘制等待画面
	 * @param canvas
	 */
	private void drawWaitPage(Canvas canvas,boolean isFirstDraw){
		if(isFirstDraw){
			mBGDrawable.setBounds(getLeft(), getTop(), getRight(), getBottom());
			mBGDrawable.draw(canvas);
			mInitBGDrawable = null;
			mTempTextPaint.setTextSize((float) (mReadSetting.getFontSize()));
			mTempTextPaint.setColor(mReadSetting.getThemeTextColor());
		}else{
			if(mInitBGDrawable == null){
				Options opts = new Options();
				opts.inPreferredConfig = Config.RGB_565;
				mInitBGDrawable = new BitmapDrawable(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.book_vers, opts));
			}
			mInitBGDrawable.setBounds(getLeft(), getTop(), getRight(), getBottom());
			mInitBGDrawable.draw(canvas);
			mTempTextPaint.setTextSize((float) (mReadSetting.getFontSize()));
			mTempTextPaint.setColor(Color.BLACK);
		}
		long timeDifference = System.currentTimeMillis() - mLastDrawWaitTime;
		if(timeDifference  > 200 || timeDifference < 0){
			mLastDrawWaitTime = System.currentTimeMillis();
			mLoadingPointSize++;
			if(mLoadingPointSize > 3){
				mLoadingPointSize = 1;
			}
		}
		mTempTextPaint.setTextAlign(Align.CENTER);
		String str = getResources().getString(R.string.reader_transition_tip);
		for (int i = 0; i < mLoadingPointSize; i++) {
			str = " " + str + ".";
		}
		canvas.drawText(str, getWidth()/2, getHeight()/2, mTempTextPaint);
		postInvalidateDelayed(500);
	}
	
	@Override
	public boolean onClickSpan(ClickActionSpan clickableSpan, RectF localRect,int x, int y) {
		if(!mPageAnimController.isAnimStop() || !clickableSpan.isClickable()){
			return false;
		}
		try {
			if(clickableSpan instanceof ClickAsyncDrawableSpan){
				if(mImgViewerPopWin == null){
					mImgViewerPopWin = new ImgViewerPopWin(getContext());
				}
				mImgViewerPopWin.showImgViewer(((AsyncDrawableSpan)clickableSpan).getDrawable(), new Rect(
						(int)localRect.left,
						(int)localRect.top, 
						(int)localRect.right, 
						(int)localRect.bottom),this);
				return true;
			}else if(clickableSpan instanceof NoteSpan){
				if(mNotePopWin == null){
					mNotePopWin = new NotePopWin(this);
				}
				mNotePopWin.showNote(((NoteSpan) clickableSpan).getNote(),localRect,mReadSetting.getMinFontSize());
				return true;
			}else if(clickableSpan instanceof UrlSpna){
				String urlStr = ((UrlSpna) clickableSpan).getUrl();
				if(!TextUtils.isEmpty(urlStr)){
					Uri uri = Uri.parse(urlStr);
			        Context context = getContext();
			        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
			        context.startActivity(intent);
			        return true;
				}
			}else if(clickableSpan instanceof IMediaSpan){
				if(clickableSpan instanceof VideoSpan){
					if(mVideoWindow != null){
						mVideoWindow.handlerPlayVideo((VideoSpan) clickableSpan, localRect);
					}
				}else{
					if(!((IMediaSpan) clickableSpan).isPlay()){
						long startPosition = ((IMediaSpan) clickableSpan).computePositionByLocal(x, y);
						ReaderMediaPlayer.getInstance().startVioce((IMediaSpan) clickableSpan,startPosition);
					}else{
						ReaderMediaPlayer.getInstance().setPlayState(false);
					}
				}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}
	
	public void closeVideo(){
		if(mVideoWindow != null){
			mVideoWindow.dismiss();
		}
	}
	
	public boolean dispatchVideoKeyEvent(KeyEvent event){
		return mVideoWindow != null && mVideoWindow.dispatchKeyEvent(event);
	}
	
	public boolean dispatchVideoTouchEvent(MotionEvent ev){
		return mVideoWindow != null && mVideoWindow.dispatchTouchEvent(ev);
	}
	
	public boolean onClickCallBack(MotionEvent event) {
		int x = (int)event.getX();
		int y = (int)event.getY();
		if(mPageManager != null 
				&& mPageManager.dispatchClick(this,x,y,mCurrentChapterIndex, mCurrentPage)){
			return true;
		}
		return false;
	}

	@Override
	public void drawPage(Canvas canvas,int requestPage) {
		handleRequestCharIndex();
		int chapterIndex = 0;
		if(requestPage < 0){
			requestPage = mRequestPage;
			chapterIndex = mRequestChapterIndex;
		}else{
			requestPage = mCurrentPage;
			chapterIndex = mCurrentChapterIndex;
		}
		if(requestPage < 0){
			requestPage = -requestPage - 1;
		}
		mPageManager.requestDrawPage(canvas, chapterIndex,requestPage,mRequestChapterIndex, -mRequestPage - 1);
	}
	
	@Override
	public boolean handRequestIndex(Canvas canvas,int chapterIndex,int pageIndex,int bindChapterIndex,int bindPageIndex){
		if(pageIndex == INDEX_INITIAL_CONTENT){
			drawWaitPage(canvas,mPageManager.isFirstDraw());
			return true;
		}
		return false;
	}
	
	private void drawFullPageBackground(Canvas canvas){
		canvas.drawColor(Color.BLACK);
	}
	
	private void drawFooter(Canvas canvas,int chapterIndex, int pageIndex){
		int totalPageSize = mPageManager.getTotalPageSize();
		String pageSizeStr = "-/-";
		if(totalPageSize > 0){
			pageSizeStr = (mPageManager.getTotalPageIndex(chapterIndex, pageIndex) + 1) + "/" + totalPageSize;
		}
		mTempTextPaint.setTextSize((float) (mReadSetting.getMinFontSize()));
		mTempTextPaint.setTextAlign(Align.RIGHT);
		mTempTextPaint.setColor(mReadSetting.getThemeDecorateTextColor());
		FontMetricsInt fm = mTempTextPaint.getFontMetricsInt();
		int x = getWidth() - PADDING_LEFT;
		int y = getHeight() - PADDING_BOTTOM - fm.bottom;
		canvas.drawText(pageSizeStr, x, y, mTempTextPaint);
		if(mBatteryDrawable == null){
			mBatteryDrawable = getResources().getDrawable(R.drawable.battery);
		}
		if(mMaxBatteryProgress == null){
			int maxBatteryW = (int) mTempTextPaint.measureText("9/9");
			int maxBatteryH = (int)(mTempTextPaint.getFontSpacing() * 0.8);
			Rect bounds = new Rect(PADDING_LEFT, getHeight() - PADDING_BOTTOM - maxBatteryH, PADDING_LEFT + maxBatteryW, getHeight() - PADDING_BOTTOM);
			bounds.offset(0, -(int)(mTempTextPaint.getFontSpacing() * 0.1));
			mBatteryDrawable.setBounds(bounds);
			Rect padding = new Rect();
			mBatteryDrawable.getPadding(padding);
			mMaxBatteryProgress = new Rect(bounds.left + padding.left + 1,
					bounds.top + padding.top,
					bounds.right - padding.right,
					bounds.bottom - padding.bottom);
		}
		mBatteryDrawable.setColorFilter(mReadSetting.getThemeDecorateTextColor(), Mode.SRC_ATOP);
		mBatteryDrawable.draw(canvas);
		if(mBatteryProgress > 66){
			mTempTextPaint.setColor(mReadSetting.getThemeDecorateTextColor());
		}else if(mBatteryProgress > 33){
			mTempTextPaint.setColor(mReadSetting.getThemeDecorateTextColor());
		}else{
			mTempTextPaint.setColor(mReadSetting.getThemeDecorateTextColor());
		}
		canvas.drawRect(mMaxBatteryProgress.left + (1 - mBatteryProgress / 100f) * mMaxBatteryProgress.width(),
				mMaxBatteryProgress.top,
				mMaxBatteryProgress.right,
				mMaxBatteryProgress.bottom, mTempTextPaint);
		Rect batteryRect = mBatteryDrawable.getBounds();
		int clockX = batteryRect.right + ContextUtil.DIPToPX(5);
		mReaderClockDrawable.setTextSize(mReadSetting.getMinFontSize());
		mReaderClockDrawable.setTextColor(mReadSetting.getThemeDecorateTextColor());
		mReaderClockDrawable.setBounds(clockX
				, batteryRect.top
				, clockX
				, batteryRect.bottom);
		mReaderClockDrawable.draw(canvas);
	}
	
	private void drawChapterName(Canvas canvas,int chapterIndex){
		String title = mCallback.getChapterName(chapterIndex);
		if(title == null){
			title = "";
		}
		mTempTextPaint.setTextSize((float) (mReadSetting.getMinFontSize()));
		mTempTextPaint.setTextAlign(Align.LEFT);
		mTempTextPaint.setColor(mReadSetting.getThemeDecorateTextColor());
		FontMetricsInt fm = mTempTextPaint.getFontMetricsInt();
		int x = PADDING_LEFT;
		int y = PADDING_TOP - fm.top;
		canvas.drawText(title, x, y, mTempTextPaint);
	}
	
	private void drawBookName(Canvas canvas){
		String title = mCallback.getBookName();
		if(title == null){
			title = "";
		}
		mTempTextPaint.setTextSize((float) (mReadSetting.getMinFontSize()));
		mTempTextPaint.setTextAlign(Align.LEFT);
		mTempTextPaint.setColor(mReadSetting.getThemeDecorateTextColor());
		FontMetricsInt fm = mTempTextPaint.getFontMetricsInt();
		int x = PADDING_LEFT;
		int y = PADDING_TOP - fm.top;
		canvas.drawText(title, x, y, mTempTextPaint);
	}
	
	@Override
	public Integer requestPrePage() {
		//由于界面没有布局好不可能会调用此方法所以不用担心页跳转问题
		int[] locals = mPageManager.requestPretPage(mCurrentChapterIndex, mCurrentPage);
		if(locals != null){
			if(locals[0] >= 0){
				int requestChapterIndex = locals[0];
				int requestPage = -(locals[1] + 1);
				if(!mCallback.checkNeedBuy(requestChapterIndex)){
					mRequestChapterIndex = requestChapterIndex;
					mRequestPage = requestPage;
					setOnPageChange(requestChapterIndex, -requestPage - 1);
					return mRequestPage;
				}else{
					setOnPageChange();
				}
			}
		}else{
			mCallback.onNotPreContent();
		}
		return null;
	}

	@Override
	public Integer requestNextPage() {
		//由于界面没有布局好不可能会调用此方法所以不用担心页跳转问题
		int[] locals = mPageManager.requestNextPage(mCurrentChapterIndex, mCurrentPage);
		if(locals != null){
			if(locals[0] >= 0){
				int requestChapterIndex = locals[0];
				int requestPage = -(locals[1] + 1);
				if(!mCallback.checkNeedBuy(requestChapterIndex)){
					mRequestChapterIndex = requestChapterIndex;
					mRequestPage = requestPage;
					setOnPageChange(requestChapterIndex, -requestPage - 1);
					return mRequestPage;
				}else{
					setOnPageChange();
				}
			}
		}else{
			mCallback.onNotNextContent();
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
				|| type == ReadSetting.SETTING_TYPE_FONT_SIZE){
			loadStyleSetting(type != ReadSetting.SETTING_TYPE_THEME);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		KeyguardManager mKeyguardManager = (KeyguardManager) getContext().getSystemService(Context.KEYGUARD_SERVICE);   
	    if (mKeyguardManager.inKeyguardRestrictedInputMode()) {
	    	isKeyguard = true;
	        return;
	    }
		boolean isChanged = changed;
		if(isKeyguard){
		    isKeyguard = false;
			isChanged = false;
		}
		int orientation = ((Activity) getContext()).getRequestedOrientation();
		if(mOrientation != orientation){
			isChanged = false;
		}
		mOrientation = orientation;
		super.onLayout(changed, left, top, right, bottom);
		if(isChanged){
			mPageManager.setUnInit();
		}
		createPageManager();
	}

	private void createPageManager(){
		if(mChapterSize != null && !mPageManager.isInit()){
			Rect fullPageRect = new Rect(getLeft(), getTop(), getRight(), getBottom());
			SettingParam settingParam = new SettingParam(HtmlParser.PARSER_TYPE_SURFINGREADER,mContentId,mTextPaint
					,newPageContenRect()
					,fullPageRect,mReadSetting.getLineSpaceSize(),mReadSetting.getParagraphSpaceSize(),this);
			mPageManager.init(settingParam, mChapterSize, mCurrentChapterIndex);
			invalidate();
		}
	}
	
	/**
	 * @see android.view.View#measure(int, int)
	 */
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
			if(mCurrentPage == -mRequestPage - 1
					&& mCurrentChapterIndex == mRequestChapterIndex){
				return;
			}
			LogUtil.i(TAG,"onAnimEnd mRequestPage="+mRequestPage);
			mCurrentPage = -mRequestPage - 1;
			mCurrentChapterIndex = mRequestChapterIndex;
			LogUtil.e("pagepage");
			setOnPageChange();
		}else{
			mRequestPage = -(mCurrentPage + 1);
			mRequestChapterIndex = mCurrentChapterIndex;
		}
	}

	@Override
	public void onPlayStateChange(int playState, String voiceSrc) {
		post(new Runnable() {
			@Override
			public void run() {
				if(mPageManager != null){
					mPageManager.invalidateCachePage();
				}
			}
		});
	}

	@Override
	public void onProgressChange(final long currentPosition, long maxPosition, String voiceSrc) {
//		post(new Runnable() {
//			@Override
//			public void run() {
//				if(mPageManager != null){
//					if(mVoiceProgressSpans != null){
//						for (int i = 0; i < mVoiceProgressSpans.length; i++) {
//							VoiceProgressSpan span = mVoiceProgressSpans[i];
//							if(!span.equals(mLastVoiceProgressSpan) && span.contains(currentPosition)){
//								mLastVoiceProgressSpan = span;
//								int pageIndex = mPageManager.findPageByIndex(mContentText.getSpanStart(span));
//								if(pageIndex - mCurrentPage == 1){
//									gotoPage(pageIndex, false);
//								}
//								break;
//							}
//						}
//					}
//					mPageManager.invalidateCachePage();
//				}
//			}
//		});
	}

	public int getCurrentChapterIndex(){
		return mCurrentChapterIndex;
	}
	
	@Override
	public int getCurrentPageIndex() {
		return mCurrentPage;
	}

	@Override
	public String getChapterInputStream(int chapterIndex) {
		return mCallback.getChapterInputStream(chapterIndex);
	}

	@Override
	public ICssProvider getCssProvider() {
		return mCallback.getCssProvider();
	}

	@Override
	public DataProvider getDataProvider() {
		return mCallback.getDataProvider();
	}

	@Override
	public TagHandler getTagHandler() {
		return mCallback.getTagHandler();
	}
	
	@Override
	public void drawWaitingContent(Canvas canvas, int chapterIndex,boolean isFirstDraw) {
		drawWaitPage(canvas,isFirstDraw);
	}

	@Override
	public void onPostDrawContent(Canvas canvas, int chapterIndex, int pageIndex,boolean isFullScreen) {
		if(!isFullScreen){
			if(pageIndex == 0){
				drawBookName(canvas);
			}else{
				drawChapterName(canvas,chapterIndex);
			}
			drawFooter(canvas,chapterIndex,pageIndex);
		}
	}

	@Override
	public void onPreDrawContent(Canvas canvas, int chapterIndex, int pageIndex,boolean isFullScreen) {
		mInitBGDrawable = null;
		mBGDrawable.setBounds(getLeft(), getTop(), getRight(), getBottom());
		mBGDrawable.draw(canvas);
		if(isFullScreen){
			drawFullPageBackground(canvas);
		}
	}
	
	@Override
	public void onLayoutPageFinish(int chapterIndex, int pageIndex,int curChar, int maxChar) {
		if(mCurrentChapterIndex == chapterIndex){
			handleRequestCharIndex();
		}
	}

	@Override
	public void onLayoutChapterFinish(int chapterIndex,int progress, int max) {
		if(progress == max){
			mPageManager.invalidateCachePage();
		}
		mCallback.onLayoutChapterFinish(progress, max);
	}
	
	@Override
	public void saveDataDB(String contentId, String key, String data) {
//		ReaderLayoutDB.getInstance(getContext()).saveData(contentId, key, data);
	}

	@Override
	public String getDataDB(String contentId, String key) {
		return null;
//		return ReaderLayoutDB.getInstance(getContext()).getData(contentId, key);
	}

	@Override
	public boolean hasDataDB(String contentId, String key) {
//		return ReaderLayoutDB.getInstance(getContext()).hasData(contentId, key);
		return false;
	}
	
	public int getLayoutChapterProgress(){
		return mPageManager.getLayoutChapterProgress();
	}
	
	public int getLayoutChapterMax(){
		return mPageManager.getLayoutChapterMax();
	}
	
	private void handleRequestCharIndex(){
		if(mRequestCharIndex != -1){
			int newPageIndex = mPageManager.findPageIndex(mCurrentChapterIndex,mRequestCharIndex);
			if(newPageIndex >= 0){
				LogUtil.e(TAG,"handleRequestCharIndex");
				if(mCurrentPage == INDEX_INITIAL_CONTENT){
					mCurrentPage = newPageIndex;
				}
				if(mRequestPage == REQUEST_INDEX_INITIAL_CONTENT){
					mRequestPage = -(newPageIndex + 1);
				}
				mRequestCharIndex = -1;
				mPageManager.invalidateCachePage();
			}
		}
	}

	public int getTotalPageSize() {
		return mPageManager.getTotalPageSize();
	}

	public int getCurTotalPageIndex() {
		return getTotalPageIndex(mCurrentChapterIndex, mCurrentPage);
	}

	public int getTotalPageIndex(int chapterIndex,int pageIndex){
		return mPageManager.getTotalPageIndex(chapterIndex, pageIndex);
	}
	
	public void gotoNextPage() {
		if(!mPageAnimController.isAnimStop()){
			mPageAnimController.stopAnim(this);
		}
		int[] locals = mPageManager.requestNextPage(mCurrentChapterIndex, mCurrentPage);
		if(locals != null){
			if(locals[0] >= 0){
				gotoPage(locals[0], locals[1], true);
			}
		}
	}

	public void gotoPrePage() {
		if(!mPageAnimController.isAnimStop()){
			mPageAnimController.stopAnim(this);
		}
		int[] locals = mPageManager.requestPretPage(mCurrentChapterIndex, mCurrentPage);
		if(locals != null){
			if(locals[0] >= 0){
				gotoPage(locals[0], locals[1], true);
			}
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
		if(mChapterSize == null){
			return false;
		}
		return mCurrentChapterIndex > 0;
	}

	public boolean hasNextChapter() {
		if(mChapterSize == null){
			return false;
		}
		return mCurrentChapterIndex < mChapterSize - 1;
	}

	public boolean isFirstDraw(){
		return mPageManager.isFirstDraw();
	}
	/**
	 * 释放资源
	 */
	public void release() {
		mPageManager.release();
	}
	/**
	 * 对外接口
	 * @author lyw
	 */
	public interface ReadViewCallback{
		/** 
		 * 当前页变化
		 * */
		public void onPageChange(int totalPageIndex,int max);
		/**
		 * 当前页是否有书签
		 */
		public boolean hasBookMark();
		/**
		 * 获取章节数据
		 * @param chapterIndex
		 * @return
		 */
		public String getChapterInputStream(int chapterIndex);
		/**
		 * 获取html扩展者
		 * @return
		 */
		public TagHandler getTagHandler();
		/**
		 * 获取Css加载器
		 * @return
		 */
		public ICssProvider getCssProvider();
		/**
		 * 获取数据加载器
		 * @return
		 */
		public DataProvider getDataProvider();
		/**
		 * 获取书名
		 * @return
		 */
		public String getBookName();
		/**
		 * 获取章节名
		 * @param chapterIndex
		 * @return
		 */
		public String getChapterName(int chapterIndex);
		/**
		 * 章节布局进度
		 * @param progress
		 * @param max
		 */
		public void onLayoutChapterFinish(int progress, int max);
		/**
		 * 没有上一页内容
		 */
		public void onNotPreContent();
		/**
		 * 没有下一页内容
		 */
		public void onNotNextContent();
		/**
		 * 检测是否需要购买
		 * @param catalogIndex
		 * @param requestPageIndex
		 * @return
		 */
		public boolean checkNeedBuy(int catalogIndex);
	}
	
	@Override
	public PaserExceptionInfo getPaserExceptionInfo() {
		return new PaserExceptionInfo(mContentId, mCallback.getBookName(), mChapterSize);
	}
}
