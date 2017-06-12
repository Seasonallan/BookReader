package com.lectek.lereader.core.cartoon;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lectek.lereader.core.bookformats.PaserExceptionInfo;
import com.lectek.lereader.core.cartoon.photoview.PageDrawable;
import com.lectek.lereader.core.cartoon.photoview.PhotoView;
import com.lectek.lereader.core.cartoon.photoview.PhotoViewAttacher.OnViewTapListener;
import com.lectek.lereader.core.text.ClickSpanHandler;
import com.lectek.lereader.core.text.PageManager;
import com.lectek.lereader.core.text.PageManager.PageManagerCallback;
import com.lectek.lereader.core.text.SettingParam;
import com.lectek.lereader.core.text.html.HtmlParser;
import com.lectek.lereader.core.text.style.ClickActionSpan;
import com.lectek.lereader.core.text.test.ReaderMediaPlayer.PlayerListener;

/**
 * 
 * @author laijp
 * @date 2014-3-5
 * @email 451360508@qq.com
 */
public abstract class CartoonView extends FrameLayout implements PageManagerCallback,
		ClickSpanHandler, PlayerListener, OnPageChangeListener {
	public static final String TAG = CartoonView.class.getSimpleName();
	/** 代表初始界面 */
	protected static final int INDEX_INITIAL_CONTENT = -1;
	/** 页管理器 */
	protected PageManager mPageManager;
	/** 请求跳转的指定字符所在位置的页，-1代表未指定 */
	protected int mRequestCharIndex;
	/** 当前页 */
	protected int mCurrentPage;
	/** 当前章 */
	protected int mCurrentChapterIndex; 
	/** 章节数 */
	protected Integer mChapterSize;
	/** 书籍ID */
	protected String mContentId;  
	protected Runnable mOnPageChangeRun;
	protected int mOrientation;
	protected boolean isKeyguard;

	private IPageView mPageView; 
	private ViewPagerAdapter mPageViewAdapter; 

	public CartoonView(Context context) {
		super(context);
		init();
	}

	public CartoonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CartoonView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	} 
	
	private void init() {
		setDrawingCacheEnabled(false);
		mPageManager = new PageManager(getContext(), this);
		mCurrentPage = INDEX_INITIAL_CONTENT;
		mCurrentChapterIndex = INDEX_INITIAL_CONTENT;
		//ReaderMediaPlayer.getInstance().addPlayerListener(this);
	}

	public void initView(String contentId, int chapterSize, int requestChapterIndex, int requestCharIndex) {
		mChapterSize = chapterSize;
		mContentId = contentId;
		mCurrentChapterIndex = requestChapterIndex;
		mRequestCharIndex = requestCharIndex;
		mPageManager.setUnInit(); 
		
		addChildView(getResources().getConfiguration().orientation); 
	} 
	
	private float mBitmapWidth, mBitmapHeight; 

	/**
	 * 获取当前页图片高度
	 * @return
	 */
	private float getDrawableHeight(){
		if(mBitmapHeight <= 0){
			Rect rect = mPageManager.getFullScreenContentRect(mCurrentChapterIndex, mCurrentPage);
			if(rect != null){
				mBitmapHeight = rect.height();
			}
		}
		return mBitmapHeight;
	}
	
	/**
	 * 获取当前页图片宽度
	 * @return
	 */
	private float getDrawableWidth(){
		if(mBitmapWidth <= 0){
			Rect rect = mPageManager.getFullScreenContentRect(mCurrentChapterIndex, mCurrentPage);
			if(rect != null){
				mBitmapWidth = rect.width();
			}
		}
		return mBitmapWidth;
	}

	/**
	 * 开始加载
	 */
	public void onReLoadStart(){

	}
	
	/**
	 * 加载结束
	 */
	public void onReloadEnd(){ 
		setBackgroundColor(0x0000);
	}
	
	private void addChildView(int orientation){
		onReLoadStart();
		removeAllViews();
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            mPageView = new HackyViewPager(getContext()); 
        }else{
        	mPageView = new HorPageView(getContext()){ 
    			 
    			public float getBitmapWidth() {
    				return  getDrawableWidth();
    			} 
    			
    			public float getBitmapHeight() {
    				return  getDrawableHeight();
    			}
    		}.setOnViewTapListener(new ViewTapListener());
        }
		mPageViewAdapter = new ViewPagerAdapter(mPageView.isPhotoView());
		mPageView.setAdapter(mPageViewAdapter);
		mPageView.setPageMargin(10); 
		mPageView.setOnPageChangeListener(this);
		addView((View) mPageView);
	}
	
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPageManager.onConfigurationChanged();
		mPageManager.setUnInit();
		createPageManager();  
        addChildView(newConfig.orientation); 
    }
	
	public int findCurrentPageFirstIndex() {
		int requestPageIndex = mPageManager.findPageFirstIndex(
				mCurrentChapterIndex, mCurrentPage);
		if (requestPageIndex >= 0) {
			return requestPageIndex;
		}
		return 0;
	}

	public void gotoChapter(int requestChapterIndex, boolean isStartAnim) {
		gotoPageCharIndex(requestChapterIndex, 0, isStartAnim);
	}

	public void gotoPageCharIndex(int requestChapterIndex,
			int requestPageCharIndex, boolean isStartAnim) { 
		int requestPage = mPageManager.findPageIndex(requestChapterIndex,
				requestPageCharIndex);
		if (requestPage >= 0) {
			gotoPage(requestChapterIndex, requestPage, isStartAnim);
		} else {
			mRequestCharIndex = requestPageCharIndex;
			gotoPage(requestChapterIndex, INDEX_INITIAL_CONTENT, isStartAnim);
		}
	}

	public void gotoTotalPage(int requestTotalPage, boolean isStartAnim) {
		int[] locals = mPageManager.findPageIndexByTotal(requestTotalPage);
		if (locals != null) {
			gotoPage(locals[0], locals[1], isStartAnim);
		}
	}
	
	/**
	 * 是否需要购买
	 * @param requestChapterIndex
	 * @return
	 */
	protected abstract boolean checkNeedBuy(int requestChapterIndex);
	protected abstract boolean onPageChange(int totalPageIndex,int max);

	protected void gotoPage(int requestChapterIndex, int requestPage,
			boolean isStartAnim) {
		if (checkNeedBuy(requestChapterIndex)) {
			setOnPageChange();
			return;
		}
		mCurrentChapterIndex = requestChapterIndex;
		mCurrentPage = requestPage;
		setCurrentSelection();
	}

	private void setCurrentSelection(){
		int index = mPageManager.getPageIndex(
				mCurrentChapterIndex, mCurrentPage);
		mPageView.setCurrentItem(index); 
		invalidate();
		
	}
	
	private void setOnPageChange(final int chapterIndex, final int pageIndex) {
		closeVideo();
		if (mOnPageChangeRun != null) {
			removeCallbacks(mOnPageChangeRun);
		}
		mOnPageChangeRun = new Runnable() {
			@Override
			public void run() {
				int totalPageSize = mPageManager.getTotalPageSize();
				if (totalPageSize > 0) {
					onPageChange(mPageManager.getTotalPageIndex(
							chapterIndex, pageIndex), totalPageSize); 
				}
			}
		};
		postDelayed(mOnPageChangeRun, 100);
	}

	private void setOnPageChange() {
		setOnPageChange(mCurrentChapterIndex, mCurrentPage);
	}

	public boolean handlerTouchEvent(MotionEvent event) {
		return true;
	}
	 
	@Override
	public boolean onClickSpan(ClickActionSpan clickableSpan, RectF localRect,
			int x, int y) {
		return false;
	}

	public void closeVideo() {
	}

	public boolean dispatchVideoKeyEvent(KeyEvent event) {
		return false;
	}

	public boolean dispatchVideoTouchEvent(MotionEvent ev) {
		return false;
	}

	public boolean onClickCallBack(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		if (mPageManager != null
				&& mPageManager.dispatchClick(this, x, y, mCurrentChapterIndex,
						mCurrentPage)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean handRequestIndex(Canvas canvas, int chapterIndex,
			int pageIndex, int bindChapterIndex, int bindPageIndex) {
		return false;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {  
		super.onLayout(changed, left, top, right, bottom);
		if(changed){
			mPageManager.setUnInit();
		}
		createPageManager();
	}

	private void createPageManager() {
		if (mChapterSize != null && !mPageManager.isInit()) {
			Rect fullPageRect = new Rect(getLeft(), getTop(), getRight(),
					getBottom());
			SettingParam settingParam = new SettingParam(
					HtmlParser.PARSER_TYPE_SURFINGREADER, mContentId,
					new TextPaint(), fullPageRect, fullPageRect, 0, 0, this);
			mPageManager.init(settingParam, mChapterSize, mCurrentChapterIndex);
			invalidate();
		}
	}

	@Override
	public void onPlayStateChange(int playState, String voiceSrc) {
		post(new Runnable() {
			@Override
			public void run() {
				if (mPageManager != null) {
					mPageManager.invalidateCachePage();
					invalidate();
				}
			}
		});
	}

	@Override
	public void onProgressChange(final long currentPosition, long maxPosition,
			String voiceSrc) {

	}

	public int getCurrentChapterIndex() {
		return mCurrentChapterIndex;
	} 
	
	@Override
	public void drawWaitingContent(Canvas canvas, int chapterIndex,
			boolean isFirstDraw) { 
	}

	@Override
	public void onPostDrawContent(Canvas canvas, int chapterIndex,
			int pageIndex, boolean isFullScreen) {
	}

	@Override
	public void onPreDrawContent(Canvas canvas, int chapterIndex,
			int pageIndex, boolean isFullScreen) {
	}

	private void handleRequestCharIndex() {
		if (mRequestCharIndex != -1) {
			int newPageIndex = mPageManager.findPageIndex(mCurrentChapterIndex,
					mRequestCharIndex);
			if (newPageIndex >= 0) { 
				mCurrentPage = newPageIndex;
				mRequestCharIndex = -1;
				mPageManager.invalidateCachePage();  
			}
		}
	}

	@Override
	public void onLayoutPageFinish( int chapterIndex, int pageIndex,int curChar, int maxChar) {  
	}

	@Override
	public void onLayoutChapterFinish(int chapterIndex, int progress, int max) { 
		if (progress == max) {
			onReloadEnd();
			mPageManager.invalidateCachePage();
			mPageViewAdapter.notifyDataSetChanged(); 
			invalidate();
			handleRequestCharIndex();
			gotoPage(mCurrentChapterIndex, mCurrentPage, false);
		} 

	}

	@Override
	public void invalidateView(Rect dirty) {
		invalidate();
	} 
	
	@Override
	public void saveDataDB(String contentId, String key, String data) {
		// ReaderLayoutDB.getInstance(getContext()).saveData(contentId, key,
		// data);
	}

	@Override
	public String getDataDB(String contentId, String key) {
		return null;
		// return ReaderLayoutDB.getInstance(getContext()).getData(contentId,
		// key);
	}

	@Override
	public boolean hasDataDB(String contentId, String key) {
		// return ReaderLayoutDB.getInstance(getContext()).hasData(contentId,
		// key);
		return false;
	}

	public int getLayoutChapterProgress() {
		return mPageManager.getLayoutChapterProgress();
	}

	public int getLayoutChapterMax() {
		return mPageManager.getLayoutChapterMax();
	}

	public int getTotalPageSize() {
		return mPageManager.getTotalPageSize();
	}

	public int getCurTotalPageIndex() {
		return getTotalPageIndex(mCurrentChapterIndex, mCurrentPage);
	}

	public int getTotalPageIndex(int chapterIndex, int pageIndex) {
		return mPageManager.getTotalPageIndex(chapterIndex, pageIndex);
	}

	public void gotoNextPage() {
		int[] locals = mPageManager.requestNextPage(mCurrentChapterIndex,
				mCurrentPage);
		if (locals != null) {
			if (locals[0] >= 0) {
				if (locals[0] >= 0) {
					gotoPage(locals[0], locals[1], true);
				}
			}
		}
	}

	public void gotoPrePage() {
		int[] locals = mPageManager.requestPretPage(mCurrentChapterIndex,
				mCurrentPage);
		if (locals != null) {
			if (locals[0] >= 0) {
				gotoPage(locals[0], locals[1], true);
			}
		}
	}

	public void gotoPreChapter() {
		if (hasPreChapter()) {
			gotoChapter(mCurrentChapterIndex - 1, true);
		}
	}

	public void gotoNextChapter() {
		if (hasNextChapter()) {
			gotoChapter(mCurrentChapterIndex + 1, true);
		}
	}

	public boolean hasPreChapter() {
		if (mChapterSize == null) {
			return false;
		}
		return mCurrentChapterIndex > 0;
	}

	public boolean hasNextChapter() {
		if (mChapterSize == null) {
			return false;
		}
		return mCurrentChapterIndex < mChapterSize - 1;
	}

	public boolean isFirstDraw() {
		return true;
	}

	/**
	 * 释放资源
	 */
	public void release() {
		mPageManager.release();
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		int[] page = mPageManager.getPage(arg0);
		if (page != null) {
			mCurrentChapterIndex = page[0];
			mCurrentPage = page[1];
			setOnPageChange();
		} 
	}
 
	private View instantChildView(Context context , int position , ImageView imageView){ 
		FrameLayout frameLayout = new FrameLayout(context);
		//frameLayout.setBackgroundColor(0xffc9c9c9);
		TextView textView = new TextView(context);
		textView.setText((position + 1)+"");
		textView.setTextColor(0xffffffff);
		textView.setTextSize(64);
		LayoutParams param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		param.gravity = Gravity.CENTER;
		frameLayout.addView(textView, param);
		
		int[] page = mPageManager.getPage(position); 
		imageView.setImageDrawable(new PageDrawable(mPageManager, page));
		frameLayout.addView(imageView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return frameLayout;
	}

	public boolean onSingleTapUp(float x, float y) { 
		int width = getWidth();
		int height = getHeight();
		
		int toolTouchAreaW = width >> 2;
		int toolTouchTouchH = height >> 2;
		int toolLP = (width - toolTouchAreaW) >> 1;
		int toolRP = width - toolLP;
		int toolTP = (height - toolTouchTouchH) >> 1;
		int toolBP = height - toolTP;
		
		if(x > toolLP && x < toolRP && y > toolTP && y < toolBP) {	//弹出菜单
//			if(mReaderCallback != null) {
//				mReaderCallback.showReaderMenu();
//			}
		} else if( (y < height/2 && x > width*2/3) || (y > height/2 && x > width/3)) {	//下一页
			mPageView.moveToNext();
		} else if ((y < height/2 && x < width*2/3) || (y > height/2 && x < width/3)) {	//上一页
			mPageView.moveToPrevious();
		}
		return false;
	}
	
	class ViewTapListener implements OnViewTapListener{ 
		@Override
		public void onViewTap(View view, float x, float y) {
			onSingleTapUp(x, y);
		}
	}
	
	/**
	 * ViewPager的适配器
	 */
	class ViewPagerAdapter extends PagerAdapter { 
		
		private boolean mInnerPhotoView;
		public ViewPagerAdapter(boolean photoView) {
			mInnerPhotoView = photoView;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {  
			final ImageView imageView ;
			if(mInnerPhotoView){
				imageView = new PhotoView(getContext()){ 
	        		@Override
	        		public float getBitmapWidth() {
	        			return getDrawableWidth();
	        		}

	        		@Override
	        		public float getBitmapHeight() {
	        			return getDrawableHeight();
	        		}
				};  
				((PhotoView) imageView).setOnViewTapListener(new ViewTapListener());
			}else{
				imageView = new ImageView(getContext());
			}
			View frameLayout = instantChildView(getContext(), position, imageView);
			if(container != null){
				container.addView(frameLayout, LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);
			}
			return frameLayout;
		}

		@Override
		public int getCount() {
			int count = mPageManager.getTotalPageSize();
			count = count <= 0 ? 0 : count;
			return count;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = (View) object; 
			container.removeView(view);
		}
	} 
 
	@Override
	public PaserExceptionInfo getPaserExceptionInfo() {
		// TODO Auto-generated method stub
		return null;
	}
    boolean isLast = true;
    @Override
    public void onPageScrollStateChanged(int i) {
        if(i == 2) {
            isLast = false;
        } else if(i == 0 && isLast) {
            if (mPageView.getCurrentItem() == 0){
                onNotPreContent();
            }else{
                onNotNextContent();
            }
        } else {
            isLast = true;
        }
    }

    /**
	 * 第一页
	 */
    protected abstract void onNotPreContent();

    /**
     * 最后一页
     */
    protected abstract void onNotNextContent();

}
