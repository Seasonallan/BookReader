package com.lectek.android.lereader.ui.basereader_leyue.view;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import taobe.tec.jcc.JChineseConvertor;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.lectek.android.lereader.lib.utils.DimensionsUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsSpan;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.basereader_leyue.Book;
import com.lectek.android.lereader.ui.basereader_leyue.digests.AbsTextSelectHandler.ITouchEventDispatcher;
import com.lectek.android.lereader.ui.basereader_leyue.digests.AbsTextSelectHandler.IViewInformer;
import com.lectek.android.lereader.ui.basereader_leyue.digests.SelectorControlView;
import com.lectek.android.lereader.ui.basereader_leyue.digests.TextSelectHandler;
import com.lectek.android.lereader.ui.basereader_leyue.expand_audio.AudioPlayView;
import com.lectek.android.lereader.ui.basereader_leyue.span.ExpandTagHandler;
import com.lectek.android.lereader.ui.basereader_leyue.span.IMedia;
import com.lectek.android.lereader.ui.basereader_leyue.span.IMediaSpan;
import com.lectek.android.lereader.ui.basereader_leyue.span.NoteSpan;
import com.lectek.android.lereader.ui.basereader_leyue.span.VideoSpan;
import com.lectek.android.lereader.ui.basereader_leyue.span.VoicesSpan;
import com.lectek.android.lereader.ui.basereader_leyue.widgets.ImgViewerPopWin;
import com.lectek.android.lereader.ui.basereader_leyue.widgets.NotePopWin;
import com.lectek.android.lereader.ui.basereader_leyue.widgets.ReaderMediaPlayer;
import com.lectek.android.lereader.ui.basereader_leyue.widgets.ReaderMediaPlayer.PlayerListener;
import com.lectek.android.lereader.ui.basereader_leyue.widgets.VideoWindow;
import com.lectek.lereader.core.bookformats.Catalog;
import com.lectek.lereader.core.bookformats.PaserExceptionInfo;
import com.lectek.lereader.core.text.ClickSpanHandler;
import com.lectek.lereader.core.text.PageManager;
import com.lectek.lereader.core.text.PageManager.PageManagerCallback;
import com.lectek.lereader.core.text.SettingParam;
import com.lectek.lereader.core.text.html.HtmlParser;
import com.lectek.lereader.core.text.html.HtmlParser.TagHandler;
import com.lectek.lereader.core.text.style.AsyncDrawableSpan;
import com.lectek.lereader.core.text.style.ClickActionSpan;
import com.lectek.lereader.core.text.style.ClickAsyncDrawableSpan;
import com.lectek.lereader.core.text.style.UrlSpna;

public abstract class BaseHtmlReadView extends BaseReadView implements PlayerListener, PageManagerCallback, ClickSpanHandler {
	protected PreferencesUtil mPreferencesUtil;
	protected PageManager mPageManager;
	private int mRequestCharIndex;
	private Integer mChapterSize;
	private int mOrientation;
	private boolean isKeyguard;
	private ImgViewerPopWin mImgViewerPopWin;
	private NotePopWin mNotePopWin;
	private VideoWindow mVideoWindow;
	private AudioPlayView mAudioPlayDialog;
	private TextSelectHandler mTextSelectHandler;
	private Bitmap mCacheBitmap;
	private Canvas mCacheBitmapCanvas;
	private int mRequestDrawResult;
	private Integer mSelectTextsChapterIndex;
	
	public BaseHtmlReadView(Context context, Book book, IReadCallback readCallback) {
		super(context, book, readCallback);
        mTextSelectHandler = new TextSelectHandler(0, 0);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		ReaderMediaPlayer.init(getDataProvider());
		mPageManager = new PageManager(getContext(),this, isLayoutAll());
		mPreferencesUtil = PreferencesUtil.getInstance(getContext());
		setDrawingCacheEnabled(false);
		mCurrentPageIndex = INDEX_INITIAL_CONTENT;
		mRequestPageIndex = REQUEST_INDEX_INITIAL_CONTENT;
		mCurrentChapterIndex = INDEX_INITIAL_CONTENT;
		mRequestChapterIndex = INDEX_INITIAL_CONTENT;
		mRequestCharIndex = -1;
		ReaderMediaPlayer.getInstance().addPlayerListener(this);
        if(mVideoWindow == null && ReaderMediaPlayer.getInstance() != null){
            mVideoWindow = new VideoWindow((Activity) getContext());
        }
	}

	protected void initView(int chapterSize,int requestChapterIndex,int requestCharIndex){
		LogUtil.e(TAG,"initView");
		mChapterSize = chapterSize;
        requestCharIndex = requestCharIndex < 0 ? 0:requestCharIndex;
        requestChapterIndex = requestChapterIndex < 0 ? 0:requestChapterIndex;
        requestChapterIndex =  requestChapterIndex >= chapterSize ? chapterSize -1:requestChapterIndex;
		mCurrentChapterIndex = requestChapterIndex;
		mRequestChapterIndex = requestChapterIndex;
		mRequestCharIndex = requestCharIndex;
		mCurrentPageIndex = INDEX_INITIAL_CONTENT;
		mRequestPageIndex = REQUEST_INDEX_INITIAL_CONTENT;
		setUnInit();
		requestLayout();
	}

    @Override
    protected boolean isPageMarked(int chapterIndex, int pageIndex){
        int pageStart = getPageStartIndex(chapterIndex, pageIndex);
        int pageEnd = mPageManager.findPageLastIndex(chapterIndex, pageIndex);
        pageEnd = pageEnd < pageStart? pageStart: pageEnd;
        return mReadCallback.hasShowBookMark(chapterIndex, pageStart, pageEnd);
    }

    private void setUnInit(){
        if (mPageManager != null){
            mPageManager.setUnInit();
        }
		mRequestDrawResult = PageManager.RESULT_UN_INIT;
	}
	
	@Override
	public void onDestroy() {
        super.onDestroy();
		ReaderMediaPlayer player = ReaderMediaPlayer.getInstance();
		if(player != null){
			player.release();
		}
        if (mCacheBitmap != null){
            if (!mCacheBitmap.isRecycled()){
                mCacheBitmap.recycle();
                mCacheBitmap = null;
            }
        }
        if(mPageManager != null){
        	mPageManager.release();
        	mPageManager = null;
        }
	}

	@Override
	protected void onLoadStyleSetting(boolean isReLayout) {
		super.onLoadStyleSetting(isReLayout);
		if(mPageManager == null){
			return;
		}
		if(isReLayout){
			int requestCharIndex = mPageManager.findPageFirstIndex(mCurrentChapterIndex,mCurrentPageIndex);
			if(requestCharIndex < 0){
				if(mRequestCharIndex == -1){
					mRequestCharIndex = 0;
				}
			}else{
				mRequestCharIndex = requestCharIndex;
			}
			setUnInit();
			mCurrentPageIndex = INDEX_INITIAL_CONTENT;
			mRequestPageIndex = REQUEST_INDEX_INITIAL_CONTENT;
			requestLayout();
		}else{
			mPageManager.invalidateCachePage();
		}
	}

	@Override
	public void onActivityResume() {
	}

	@Override
	public void onActivityPause() {
	}

	@Override
	public void onActivitySaveInstanceState(Bundle outState) {
	}

	@Override
	public Object onActivityRetainNonConfigurationInstance() {
		return null;
	}

	@Override
	public void onActivityRestoreInstanceState(Bundle savedInstanceState) {
	}

	@Override
	public boolean onActivityDispatchTouchEvent(MotionEvent ev) {
		if (!mPageManager.isFirstDraw()) {
			return true;
		}
		if(dispatchVideoTouchEvent(ev)){
			return true;
		}
		return false;
	}

	@Override
	public boolean onActivityDispatchKeyEvent(KeyEvent event) {
		if (!mPageManager.isFirstDraw()) {
			return false;
		}
		if(mTextSelectHandler != null && mTextSelectHandler.isSelect()){
			mTextSelectHandler.setSelect(false);
			return true;
		}
		if(dispatchVideoKeyEvent(event)){
			return true;
		}
		return false;
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		closeVideo();
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
		if(isChanged){
			setUnInit();
		}
		createTextSelectHandler();
		createPageManager();
	}

	private void createPageManager(){
		if(mChapterSize != null && !mPageManager.isInit()){
			Rect fullPageRect = new Rect(getLeft(), getTop(), getRight(), getBottom());
			SettingParam settingParam = new SettingParam(HtmlParser.PARSER_TYPE_SURFINGREADER,mBook.getBookId(),mTextPaint
					,newPageContenRect()
					,fullPageRect,mReadSetting.getLineSpaceSize(),mReadSetting.getParagraphSpaceSize(),this);
			mPageManager.init(settingParam, mChapterSize, mCurrentChapterIndex);
			invalidate();
		}
	}
	
	private void createTextSelectHandler(){
		if(mTextSelectHandler == null || mTextSelectHandler.isChangeSize(getMeasuredWidth(),getMeasuredHeight())){
			if(mTextSelectHandler != null){
				mTextSelectHandler.releaseSpan();
			}
			mSelectTextsChapterIndex = null;
			mTextSelectHandler = new TextSelectHandler(getMeasuredWidth(),getMeasuredHeight());
			mTextSelectHandler.setSelectorLocationListener(new SelectorControlView(this,(Activity) getContext()));
			mTextSelectHandler.setViewInformer(new IViewInformer(){
				@Override
				public int getCurrentPage() {
					return mCurrentPageIndex;
				}

				@Override
				public String getData(int start, int end) {
					return mPageManager.subSequence(mCurrentChapterIndex, start, end);
				}
				
				@Override
				public void onInvalidate() {
					mPageManager.invalidateCachePage();
				}

				@Override
				public int findIndexByLocation(int pageIndex, int x, int y) {
					if(mPageManager == null){
						return -1;
					}
					return mPageManager.findIndexByLocation(mCurrentChapterIndex,pageIndex, x, y, false);
				}

				@Override
				public Rect findRectByPosition(int pageIndex, int position) {
					if(mPageManager == null){
						return null;
					}
					return mPageManager.findRectByPosition(mCurrentChapterIndex,pageIndex, position);
				}

				@Override
				public void setBookDigestsSpan(BookDigestsSpan span,int start, int end) {
					mPageManager.setSpan(mCurrentChapterIndex,span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

				@Override
				public void deleteBookDigestsSpan(BookDigestsSpan span) {
					mPageManager.removeSpan(mCurrentChapterIndex,span);
				}

				@Override
				public void stopAinm() {
					BaseHtmlReadView.this.stopAnim();
				}

                @Override
                public BookDigests newBookDigest(BookDigests digest) {
                    digest.setPosition4Txt(-1);
                    return digest;
                }
            });
		}
		if(mChapterSize != null){
			updateSelectTexts(mCurrentChapterIndex);
		}
        Rect displayFrame = new Rect();
        getWindowVisibleDisplayFrame(displayFrame);
        mTextSelectHandler.setRectOffset(0, - displayFrame.top);
	}
	
	@Override
	public TextSelectHandler getTextSelectHandler() {
		return mTextSelectHandler;
	}

	@Override
	public String getJumpProgressStr(int curPage, int pageNums) {
		String pageSizeStr = "-/-";
		if(pageNums > 0){
			pageSizeStr = (curPage + 1) + "/" + pageNums;
		}
		return pageSizeStr;
	}

	@Override
	public void gotoChapter(Catalog catalog, boolean isStartAnim) {
		gotoChapter(getChapterIndex(catalog), isStartAnim);
	}
	
	@Override
	public void gotoPage(int requestProgress, boolean isStartAnim) {
		if(mPageManager.isLayoutAll()){
			int[] locals = mPageManager.findPageIndexByTotal(requestProgress);
			if(locals != null){
				gotoPage(locals[0], locals[1], isStartAnim);
			}
		}else{
			gotoPage(mCurrentChapterIndex,requestProgress, isStartAnim);
		}
	}

	@Override
	public void gotoChar(int requestChapterIndex, int requestCharIndex,boolean isStartAnim) {
		int requestPage = mPageManager.findPageIndex(requestChapterIndex, requestCharIndex);
		if(requestPage >= 0){
			gotoPage(requestChapterIndex, requestPage, isStartAnim);
		}else{
			mRequestCharIndex = requestCharIndex;
			gotoPage(requestChapterIndex,INDEX_INITIAL_CONTENT, isStartAnim);
		}
	}

	@Override
	public void gotoBookmark(BookMark bookmark, boolean isStartAnim) {
		int requestCatalogIndex = 0;
		int requestPageCharIndex = 0;
		if (bookmark != null) {
			requestCatalogIndex = bookmark.getChapterID();
			requestPageCharIndex = bookmark.getPosition();
			try {
				gotoChar(requestCatalogIndex, requestPageCharIndex,isStartAnim);
			} catch (Exception e) {
				LogUtil.e(TAG, e);
			}
		} 
	}

	@Override
	public boolean hasPreSet() {
		return false;
	}

	@Override
	public boolean hasNextSet() {
		return false;
	}

	@Override
	public void gotoPreSet() {
	}

	@Override
	public void gotoNextSet() {
	}

	private BookMark newBookMark() {
		BookMark bookMark = null;
		if(mChapterSize != null && mBook != null && mCurrentChapterIndex >= 0 && mCurrentChapterIndex <= mChapterSize - 1){
			bookMark = new BookMark();
			bookMark.setAuthor(mBook.getAuthor());
			bookMark.setContentID(mBook.getBookId());
			bookMark.setBookmarkName(mBook.getBookName());
			bookMark.setUserID(mPreferencesUtil.getUserId());
			bookMark.setSoftDelete(DBConfig.BOOKMARK_STATUS_SOFT_DELETE_NO);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            bookMark.setCreateTime(sdf.format(date));
		}
		return bookMark;
	}
	
	@Override
	public BookMark newSysBookmark() {
		BookMark bookMark = newBookMark();
		if(bookMark != null){
            bookMark.max = mChapterSize;
			bookMark.setBookmarkType(DBConfig.BOOKMARK_TYPE_SYSTEM);
			bookMark.setChapterID(mCurrentChapterIndex);
			bookMark.setPosition(getCurPageStartIndex());
		}
		return bookMark;
	}

	@Override
	public BookMark newUserBookmark() {
        BookMark bookMark = newBookMark();
        if(bookMark != null){
            int chapterIndex = mCurrentChapterIndex;
            int start = getCurPageStartIndex();
            int end = start + 30;
            bookMark.setBookmarkType(DBConfig.BOOKMARK_TYPE_USER);
            bookMark.setChapterID(getCurChapterIndex());
            bookMark.setChapterName(getChapterName(getCurChapterIndex()));
            bookMark.setPosition(start);
            bookMark.setContentID(mBook.getBookId());
            String subString = mPageManager.subSequence(chapterIndex, start, end);
            bookMark.setBookmarkName(subString);
        }
        return bookMark;
	}

	@Override
	public int getMaxReadProgress() {
		if(mPageManager.isLayoutAll()){
			return mPageManager.getTotalPageSize();
		}else{
			return mPageManager.getChapterPageSize(mCurrentChapterIndex);
		}
	}

	@Override
	public int getCurReadProgress() {
		if(mPageManager.isLayoutAll()){
			return mPageManager.getTotalPageIndex(mCurrentChapterIndex, mCurrentPageIndex);
		}else{
			return mCurrentPageIndex;
		}
	}

	@Override
	public int getLayoutChapterProgress() {
		return mPageManager.getLayoutChapterProgress();
	}

	@Override
	public int getLayoutChapterMax() {
		return mPageManager.getLayoutChapterMax();
	}

	@Override
	public Catalog getCurrentCatalog() {
		return getCatalogByIndex(mCurrentChapterIndex);
	}

	@Override
	public int getCurChapterIndex() {
		return mCurrentChapterIndex;
	}

    protected int getCurPageStartIndex(){
        return getPageStartIndex(mCurrentChapterIndex, mCurrentPageIndex);
    }

	@Override
	public int getPageStartIndex(int chapterIndex, int pageIndex) {
		int requestPageIndex = mPageManager.findPageFirstIndex(chapterIndex, pageIndex);
		if(requestPageIndex >= 0){
			return requestPageIndex;
		}
		return 0;
	}

	@Override
	public boolean handlerSelectTouchEvent(MotionEvent event,ITouchEventDispatcher touchEventDispatcher) {
		if(mPageManager == null || mCurrentPageIndex == INDEX_INITIAL_CONTENT || mRequestDrawResult != PageManager.RESULT_SUCCESS){
			return false;
		}
		if(mTextSelectHandler != null && mTextSelectHandler.handlerTouch(event,touchEventDispatcher)){
			return true;
		}
		return false;
	}

	@Override
	public boolean dispatchClickEvent(MotionEvent event) {
		if(!isAnimStop()){
			stopAnim();
		}
		final Rect displayFrame = new Rect();
		getWindowVisibleDisplayFrame(displayFrame);
		int x = (int)event.getX();
		int y = (int)event.getY() - displayFrame.top;
		if(mPageManager != null 
				&& mPageManager.dispatchClick(this,x,y,mCurrentChapterIndex, mCurrentPageIndex)){
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

	@Override
	public boolean onClickSpan(ClickActionSpan clickableSpan, RectF localRect,
			int x, int y) {
		if(!clickableSpan.isClickable()){
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
					if(clickableSpan instanceof VoicesSpan){
						if(mAudioPlayDialog != null && mAudioPlayDialog.isShowing()){
							mAudioPlayDialog.dismiss();
						}
						DisplayMetrics realDM = DimensionsUtil.getRealDisplayMetrics((Activity) getContext());
						LogUtil.e("---realDMHeight()-"+realDM.heightPixels+",--- realDMWidth()="+ realDM.widthPixels);
//						if (!CommonUtil.isTablet(getContext())) {
							((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
//						}
						mAudioPlayDialog = new AudioPlayView(getContext(), getDataProvider(), ((IMedia) clickableSpan).getVoiceSrc());
						LogUtil.e("---getHeight()-"+getHeight()+",--- getWidth()="+ getWidth());
						LogUtil.e("---heightPixels()-"+getContext().getResources().getDisplayMetrics().heightPixels+
								",--- widthPixels()="+ getContext().getResources().getDisplayMetrics().widthPixels);
						//针对虚拟按键的 设备，需要计算虚拟按键的占用空间。
						int heightAbs = Math.abs(realDM.heightPixels - getContext().getResources().getDisplayMetrics().heightPixels);
						int widthAbs = Math.abs(realDM.widthPixels - getContext().getResources().getDisplayMetrics().widthPixels);
					
						mAudioPlayDialog.setOritationChange(realDM.widthPixels-heightAbs, realDM.heightPixels-widthAbs);
						mAudioPlayDialog.show();
					}else{
						if(!((IMediaSpan) clickableSpan).isPlay()){
							long startPosition = ((IMediaSpan) clickableSpan).computePositionByLocal(x, y);
							ReaderMediaPlayer.getInstance().startVioce((IMediaSpan) clickableSpan,startPosition);
						}else{
							ReaderMediaPlayer.getInstance().setPlayState(false);
						}
					}
				}
				return true;
			}
		} catch (Exception e) {}
		return false;
	}
	
	@Override
	public boolean canAddUserBookmark() {
		return false;
	}

	@Override
	public View getContentView() {
		return this;
	}

	@Override
	public void dealBuyResult(int chapterId) {
	}

	@Override
	public boolean onLongPress() {
		return false;
	}

	@Override
	public void search(int direction, String keyWord) {
	}

	@Override
	protected boolean interceptGotoPage(int chapterIndex, int pageIndex) {
		return mReadCallback.checkNeedBuy(chapterIndex, false);
	}

	@Override
	public void drawPage(Canvas canvas, int requestPage) {
		handleRequestCharIndex();
		super.drawPage(canvas, requestPage);
	}

	@Override
	protected boolean onDrawPage(Canvas canvas, boolean isCurrentPage,int chapterIndex, int pageIndex) {
		if(mTextSelectHandler != null && mTextSelectHandler.isSelect()){
			if(mCacheBitmap == null){
				mCacheBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.RGB_565);
				mCacheBitmapCanvas = new Canvas(mCacheBitmap);
			}
			mCacheBitmapCanvas.drawColor(0,Mode.CLEAR);
			mTextSelectHandler.handlerDrawPre();
			mPageManager.requestDrawPage(mCacheBitmapCanvas, chapterIndex,pageIndex,mRequestChapterIndex, -mRequestPageIndex - 1);
			canvas.drawBitmap(mCacheBitmap, 0, 0, null);
			mTextSelectHandler.handlerDrawPost(canvas,mCacheBitmap);
		}else{
			int result = PageManager.RESULT_UN_INIT;
			mCacheBitmap = null;
			mCacheBitmapCanvas = null;
			result = mPageManager.requestDrawPage(canvas, chapterIndex,pageIndex,mRequestChapterIndex, -mRequestPageIndex - 1);
			if(mRequestDrawResult != PageManager.RESULT_SUCCESS && result == PageManager.RESULT_SUCCESS && mTextSelectHandler != null){
				mTextSelectHandler.reLoadView();
			}
			mRequestDrawResult = result;
		}
		return true;
	}
	
	@Override
	protected void onSetPageProgress(int chapterIndex, int pageIndex) {
        if (mPageManager == null){//该方法为延迟调用，可能出现资源已回收导致的空指针异常
            return;
        }
		if(mPageManager.isLayoutAll()){
			int totalPageSize = mPageManager.getTotalPageSize();
			if(totalPageSize > 0){
				mReadCallback.onPageChange(mPageManager.getTotalPageIndex(chapterIndex,pageIndex),totalPageSize);
			}
		}else{
			int totalPageSize = mPageManager.getChapterPageSize(chapterIndex);
			if(totalPageSize > 0){
				mReadCallback.onPageChange(pageIndex,totalPageSize);
			}
		}
	}

	@Override
	protected void setPageProgress(int chapterIndex,int pageIndex){
		super.setPageProgress(chapterIndex, pageIndex);
		closeVideo();
	}
	
	private void updateSelectTexts(int requestChapterIndex){
		if(mTextSelectHandler == null){
			return;
		}
		if(mSelectTextsChapterIndex == null || mSelectTextsChapterIndex != requestChapterIndex){
			mTextSelectHandler.updateSelectTexts(mBook.getBookId(), mRequestChapterIndex, mBook.getBookName(), getChapterName(mRequestChapterIndex), 0, mBook.getAuthor());
			mTextSelectHandler.reLoadView();
			mSelectTextsChapterIndex = requestChapterIndex;
		}
	}

	@Override
	public void onStopAnim(boolean isCancel) {
		super.onStopAnim(isCancel);
		if(!isCancel){
			updateSelectTexts(mCurrentChapterIndex);
		}
	}
	
	@Override
	protected void onGotoPage(int chapterIndex,int pageIndex,boolean isStartAnim) {
		if(!isStartAnim){
			updateSelectTexts(chapterIndex);
		}
		if(!mPageManager.isLayoutAll()){
			if(mCurrentChapterIndex != chapterIndex){
				int totalPageSize = mPageManager.getChapterPageSize(chapterIndex);
				if(totalPageSize > 0){
					mReadCallback.onLayoutProgressChange(totalPageSize,totalPageSize);
				}else{
					mReadCallback.onLayoutProgressChange(0, 1);
				}
			}
		}
	}

	@Override
	protected int[] requestNextPage(int chapterIndex, int pageIndex) {
		return mPageManager.requestNextPage(chapterIndex, pageIndex);
	}

	@Override
	protected int[] requestPrePage(int chapterIndex, int pageIndex) {
		return mPageManager.requestPretPage(chapterIndex, pageIndex);
	}

	@Override
	protected void onNotPreContent() {
		mReadCallback.onNotPreContent();
	}

	@Override
	protected void onNotNextContent() {
		mReadCallback.onNotNextContent();
	}

	@Override
	protected Integer getChapterSize() {
		return mChapterSize;
	}

	@Override
	public void onPlayStateChange(int state, String voiceSrc) {
		if(state == ReaderMediaPlayer.STATE_COMPLETION){
			String newPlaySrc = null;
			if(!TextUtils.isEmpty(voiceSrc)){
				try {
					int start = voiceSrc.lastIndexOf("-") + 1;
					int end = voiceSrc.lastIndexOf(".");
					String indexStr = voiceSrc.substring(start, end);
					if(TextUtils.isDigitsOnly(indexStr)){
						int voiceIndex = Integer.valueOf(indexStr);
						if(voiceIndex >= 0){
							newPlaySrc = voiceSrc.substring(0, start) 
									+ (voiceIndex + 1)
									+ voiceSrc.substring(end, voiceSrc.length());
							if(!getDataProvider().hasData(newPlaySrc)){
								newPlaySrc = null;
							}
						}
					}
				} catch (Exception e) {}
			}
			if(newPlaySrc != null){
				ReaderMediaPlayer.getInstance().startVioce(newPlaySrc);
			}else{
				ReaderMediaPlayer.getInstance().stop();
			}
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(mPageManager != null){
					mPageManager.invalidateCachePage();
				}
			}
		});
	}

	@Override
	public void onProgressChange(long currentPosition, long maxPosition,
			String voiceSrc) {
	}
	

	@Override
	public void invalidateView(Rect dirty) {
		invalidate();
	}

	@Override
	public void onLayoutPageFinish(int chapterIndex, int pageIndex,int curChar, int maxChar) {
		if(mCurrentChapterIndex == chapterIndex){
			handleRequestCharIndex();
		}
		if(!mPageManager.isLayoutAll() && mRequestChapterIndex == chapterIndex){
			mReadCallback.onLayoutProgressChange(curChar, maxChar);
		}
	}

	private void handleRequestCharIndex(){
		if(mRequestCharIndex != -1){
			int newPageIndex = mPageManager.findPageIndex(mCurrentChapterIndex,mRequestCharIndex);
			if(newPageIndex >= 0){
                if (mRequestCharIndex == 0){
                    newPageIndex = 0;//修正 《爸爸去哪儿》等类似书籍 由于空白字符引起的起始位置非0错误
                }
                interceptGotoPage(mCurrentChapterIndex, newPageIndex);
				if(mCurrentPageIndex == INDEX_INITIAL_CONTENT){
					mCurrentPageIndex = newPageIndex;
				}
				if(mRequestPageIndex == REQUEST_INDEX_INITIAL_CONTENT){
					mRequestPageIndex = -(newPageIndex + 1);
				}
				mRequestCharIndex = -1;
				mPageManager.invalidateCachePage();
			}
		}
	}
	
	@Override
	public void onLayoutChapterFinish(int chapterIndex,int progress,int max) {
		if(progress == 0){
			ArrayList<Catalog> catalogs = getChapterList();
			for (Catalog catalog : catalogs) {
				catalog.setPageIndex(null);
			}
			mReadCallback.onChapterChange(catalogs);
		}else if(progress == max){
			ArrayList<Catalog> catalogs = getChapterList();
			for (Catalog catalog : catalogs) {
				int index = mPageManager.getTotalPageIndex(getChapterIndex(catalog), 0);
				if(index >= 0){
					catalog.setPageIndex(++index);
				}
			}
			mReadCallback.onChapterChange(catalogs);
		}
		if(mPageManager.isLayoutAll()){
			mReadCallback.onLayoutProgressChange(progress, max);
			if(progress == max){
				mPageManager.invalidateCachePage();
			}
		}else{
			if(chapterIndex == mRequestChapterIndex){
				mReadCallback.onLayoutProgressChange(progress, progress);
				mPageManager.invalidateCachePage();
			}
		} 
	}

	@Override
	public TagHandler getTagHandler() {
		return new ExpandTagHandler(getDataProvider());
	}

	@Override
	public void drawWaitingContent(Canvas canvas, int chapterIndex,boolean isFirstDraw) {
		drawWaitPage(canvas,isFirstDraw);
	}

	@Override
	public void onPostDrawContent(Canvas canvas, int chapterIndex,int pageIndex, boolean isFullScreen) {
		if(!isFullScreen){
			if(pageIndex == 0){
				drawBookName(canvas,mBook.getBookName());
			}else{
				drawChapterName(canvas,getChapterName(chapterIndex));
			}
			String pageSizeStr = "-/-";
            float progress = 0, max = 1;
			if(mPageManager.isLayoutAll()){
				int totalPageSize = mPageManager.getTotalPageSize();
				if(totalPageSize > 0){
					pageSizeStr = (mPageManager.getTotalPageIndex(chapterIndex, pageIndex) + 1) + "/" + totalPageSize;
                    progress = mPageManager.getTotalPageIndex(chapterIndex, pageIndex) + 1;
                    max = totalPageSize;
				}
			}else{
				int totalPageSize = mPageManager.getChapterPageSize(chapterIndex);
				if(totalPageSize > 0){
					pageSizeStr = (pageIndex + 1) + "/" + totalPageSize;
                    progress = pageIndex + 1;
                    max = totalPageSize;
				}
			}
			drawReadPercent(canvas, pageSizeStr);
            drawReadProgress(canvas, progress, max);
		}
	}

	@Override
	public void onPreDrawContent(Canvas canvas, int chapterIndex,
			int pageIndex, boolean isFullScreen) {
		drawBackground(canvas);
		if(isFullScreen){
			canvas.drawColor(Color.BLACK);
		}
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
	
	@Override
	public boolean handRequestIndex(Canvas canvas, int chapterIndex,int pageIndex, int bindChapterIndex, int bindPageIndex) {
		if(pageIndex == INDEX_INITIAL_CONTENT){
			drawWaitPage(canvas,mPageManager.isFirstDraw());
			return true;
		}
		return false;
	}

	@Override
	public PaserExceptionInfo getPaserExceptionInfo() {
		return new PaserExceptionInfo(mBook.getBookId(), mBook.getBookName(), mCurrentChapterIndex);
	}
	
	@Override
	public void onShowContentView() {
	}

	@Override
	public void onHideContentView() {
		closeVideo();
	}

    protected boolean isLayoutAll(){
        return true;
    }

    /**
     * 获取内容[简繁体切换]
     * @param chapterIndex
     * @return
     */
    @Override
    public String getChapterInputStream(int chapterIndex) {
        String content = getChapterInputStream_(chapterIndex);
        if (!mReadSetting.isSimplified()){
            try {
                return JChineseConvertor.getInstance().s2t(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    /**
     * 获取内容[简体]
     * @param chapterIndex
     * @return
     */
    protected abstract String getChapterInputStream_(int chapterIndex);

    protected abstract int getChapterIndex(Catalog catalog);
	
	protected abstract Catalog getCatalogByIndex(int chapterIndex);
	
	protected abstract String getChapterName(int chapterIndex);
	
	protected abstract int loadCatalogID(String chapterID);
}
