package com.lectek.android.lereader.ui.basereader_leyue.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
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

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.ui.basereader_leyue.ReadSetting;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.lereader.core.bookformats.PaserExceptionInfo;
import com.lectek.lereader.core.cartoon.HackyViewPager;
import com.lectek.lereader.core.cartoon.HorPageView;
import com.lectek.lereader.core.cartoon.IPageView;
import com.lectek.lereader.core.cartoon.photoview.PageDrawable;
import com.lectek.lereader.core.cartoon.photoview.PhotoView;
import com.lectek.lereader.core.cartoon.photoview.PhotoViewAttacher.OnViewTapListener;
import com.lectek.lereader.core.text.ClickSpanHandler;
import com.lectek.lereader.core.text.PageManager;
import com.lectek.lereader.core.text.PageManager.PageManagerCallback;
import com.lectek.lereader.core.text.SettingParam;
import com.lectek.lereader.core.text.html.HtmlParser;
import com.lectek.lereader.core.text.style.ClickActionSpan;
//import com.lectek.lereader.core.text.test.ReaderMediaPlayer.PlayerListener;

import java.util.HashMap;

/**
 * 
 * @author laijp
 * @date 2014-3-5
 * @email 451360508@qq.com
 */
public abstract class BaseCartoonView extends FrameLayout implements PageManagerCallback,
		ClickSpanHandler,OnPageChangeListener {
    public static final String TAG = BaseCartoonView.class.getSimpleName();
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

    private Activity mActivity;
    public BaseCartoonView(Context context) {
        super(context);
        mActivity = (Activity) context;
        init();
    }

    public BaseCartoonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseCartoonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setDrawingCacheEnabled(false);
        mPageManager = new PageManager(getContext(), this, isLayoutAll());
        mCurrentPage = INDEX_INITIAL_CONTENT;
        mCurrentChapterIndex = INDEX_INITIAL_CONTENT;
    }

    /**
     * 是否预加载所有页面
     * @return
     */
    protected abstract boolean isLayoutAll();


    public void initView(String contentId, int chapterSize, int requestChapterIndex, int requestCharIndex) {
        mChapterSize = chapterSize;
        mContentId = contentId;
        mCurrentChapterIndex = requestChapterIndex;
        mRequestCharIndex = requestCharIndex;
        if (mPageManager != null){
            mPageManager.setUnInit();
        }

        addChildView(getResources().getConfiguration().orientation);
    }

    private float mBitmapWidth, mBitmapHeight;

    /**
     * 获取当前页图片高度
     * @return
     */
    private float getDrawableHeight(){
        if(mBitmapHeight <= 0){
            try {
                Rect rect = mPageManager.getFullScreenContentRect(mCurrentChapterIndex, mCurrentPage);
                if(rect != null){
                    mBitmapHeight = rect.height();
                }
            }catch (Exception e){

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
            try {
                Rect rect = mPageManager.getFullScreenContentRect(mCurrentChapterIndex, mCurrentPage);
                if(rect != null){
                    mBitmapWidth = rect.width();
                }
            }catch (Exception e){

            }
        }
        return mBitmapWidth;
    }

    public void onDestroy() {
        release();
    }



    FrameLayout mLoadingDialog;
    public void onReLoadStart(){
        if (mLoadingDialog == null){
            mLoadingDialog = new FrameLayout(getContext());
            mLoadingDialog.setBackgroundResource(R.drawable.book_second_cover);
            TextView textView = new TextView(getContext());
            textView.setText(getResources().getString(R.string.reader_transition_tip));
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(18);
            LayoutParams param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            param.gravity = Gravity.CENTER;
            mLoadingDialog.addView(textView, param);
        }
        addView(mLoadingDialog);
    }

    public void onReloadEnd(){
        removeView(mLoadingDialog);
    }

    private void addChildView(int orientation){
        onReLoadStart();
        if (mPageView != null){
            removeView((View) mPageView);
        }
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
        mPageView.setCurrentItem(getPageIndex(
                mCurrentChapterIndex, mCurrentPage));
        invalidate();

    }

    private void setOnPageChange(final int chapterIndex, final int pageIndex) {
        invalidate();
        closeVideo();
        if (mOnPageChangeRun != null) {
            removeCallbacks(mOnPageChangeRun);
        }
        mOnPageChangeRun = new Runnable() {
            @Override
            public void run() {
                //int totalPageSize = mPageManager.getTotalPageSize();
                int totalPageSize = mPageManager.getLayoutChapterMax();
                if (totalPageSize > 0) {
                    onPageChange(chapterIndex, totalPageSize);
                }
            }
        };
        postDelayed(mOnPageChangeRun, 100);
    }

    private void setOnPageChange() {
        setOnPageChange(mCurrentChapterIndex, mCurrentPage);
    }

    @Override
    public boolean onClickSpan(ClickActionSpan clickableSpan, RectF localRect,
                               int x, int y) {
        return false;
    }

    public void closeVideo() {
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

//    @Override
//    public void onPlayStateChange(int playState, String voiceSrc) {
//        post(new Runnable() {
//            @Override
//            public void run() {
//                if (mPageManager != null) {
//                    mPageManager.invalidateCachePage();
//                    invalidate();
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onProgressChange(final long currentPosition, long maxPosition,
//                                 String voiceSrc) {
//
//    }

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
            int newPageIndex = getPageIndex(mCurrentChapterIndex, mRequestCharIndex);
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
        if (chapterIndex == mCurrentChapterIndex) {
            onReloadEnd();
            ViewPagerAdapter pageViewAdapter = new ViewPagerAdapter(mPageView.isPhotoView());
            mPageView.setAdapter(pageViewAdapter);
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


    /**
     * 释放资源
     */
    public void release() {
        mPageManager.release();
        mPageManager = null;
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    protected int getPageCount(){
        return mPageManager.getLayoutChapterMax();
        // return mPageManager.getTotalPageSize();
    }

    protected int getPageIndex(int chapterIndex, int page){
        return chapterIndex;
        /// return  mPageManager.getPageIndex(
        //        chapterIndex, page);
    }

    protected int[] getPage(int page){
        return new int[]{page, 0};
        //return mPageManager.getPage(page);
    }

    @Override
    public void onPageSelected(int arg0) {
        int[] page = getPage(arg0);
        if (page != null) {
            if(checkNeedBuy(page[0])){
                mPageView.setCurrentItem(getPageIndex(mCurrentChapterIndex, mCurrentPage));
                return;
            }
            mCurrentChapterIndex = page[0];
            mCurrentPage = page[1];
            setOnPageChange();
            if (mUnInitViewMap.containsKey(arg0)){
                ImageView imageView = mUnInitViewMap.get(arg0);
                imageView.setImageDrawable(new PageDrawable(mPageManager, page));
                mUnInitViewMap.remove(arg0);
            }
        }
    }

    private HashMap<Integer, ImageView> mUnInitViewMap = new HashMap<Integer, ImageView>();
    private View instantChildView(Context context , int position , ImageView imageView){
        FrameLayout frameLayout = new FrameLayout(context);
        //frameLayout.setBackgroundColor(0xffc9c9c9);
        TextView textView = new TextView(context);
        textView.setText((position + 1)+"");
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(64);
        LayoutParams param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        param.gravity = Gravity.CENTER;
        frameLayout.addView(textView, param);


        int[] page = getPage(position);
        if (page == null){
            mUnInitViewMap.put(position ,imageView);
        }
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
            int count = getPageCount();
            count = count <= 0 ? 0 : count;
            return count;//Integer.MAX_VALUE;
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
            }else if (mPageView.getCurrentItem() >= getPageCount() - 1){
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



    public int getLayoutChapterProgress() {
        return mPageManager.getLayoutChapterProgress();
    }

    public int getLayoutChapterMax() {
        return mPageManager.getLayoutChapterMax();
    }

    public int getTotalPageSize() {
        return mPageManager.getLayoutChapterProgress();
    }

    public int getCurTotalPageIndex() {
        return getTotalPageIndex(mCurrentChapterIndex, mCurrentPage);
    }

    public int getTotalPageIndex(int chapterIndex, int pageIndex) {
        return chapterIndex;//mPageManager.getTotalPageIndex(chapterIndex, pageIndex);
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

    public boolean handlerTouchEvent(MotionEvent event) {
        return true;
    }
}
