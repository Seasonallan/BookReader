package com.lectek.android.lereader.ui.basereader_leyue.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.net.http.AbsConnect;
import com.lectek.android.lereader.lib.net.http.HttpUtil;
import com.lectek.android.lereader.lib.thread.ThreadFactory;
import com.lectek.android.lereader.lib.thread.ThreadPoolFactory;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.IHttpRequest4Leyue;
import com.lectek.android.lereader.net.response.BookCatalog;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.net.response.OnlineReadContentInfo;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.basereader_leyue.Book;
import com.lectek.android.lereader.ui.basereader_leyue.ReadSetting;
import com.lectek.android.lereader.ui.basereader_leyue.ReadSetting.SettingListener;
import com.lectek.android.lereader.ui.basereader_leyue.digests.TextSelectHandler;
import com.lectek.android.lereader.ui.basereader_leyue.span.ExpandTagHandler;
import com.lectek.android.lereader.ui.basereader_leyue.widgets.ReaderMediaPlayer;
import com.lectek.android.lereader.utils.ToastUtil;
import com.lectek.lereader.core.bookformats.BookInfo;
import com.lectek.lereader.core.bookformats.Catalog;
import com.lectek.lereader.core.bookformats.PaserExceptionInfo;
import com.lectek.lereader.core.text.html.CssProvider;
import com.lectek.lereader.core.text.html.CssProvider.ICssLoader;
import com.lectek.lereader.core.text.html.DataProvider;
import com.lectek.lereader.core.text.html.HtmlParser.TagHandler;
import com.lectek.lereader.core.text.html.ICssProvider;

/**
 * 在线漫画阅读器
 * @deprecated  该阅读器只适用于 书籍中一个章节只有一张图片的情况，</br>
 * 原因：无法根据chapterId跟page获取到当前阅读之前的总页数。
 * @author laijp
 * @date 2014-3-5
 * @email 451360508@qq.com
 */
public class NetEpubCartoonView extends BaseCartoonView implements IReaderView, SettingListener {
	private PreferencesUtil mPreferencesUtil;
	private Handler mHandler;
	private BookInfo mBookInfo;
    private ContentInfoLeyue mContentInfoLeyue;
    private ArrayList<Catalog> mCatalog;
    private ArrayList<BookCatalog> mBookCatalogs;

    private IHttpRequest4Leyue mApi;
	protected IReadCallback mReadCallback;
	protected Book mBook;
    private Activity mActivity;
	public NetEpubCartoonView(Context context, Book book, IReadCallback readCallback) {
		super(context);
        mActivity = (Activity) context;
        this.mApi = ApiProcess4Leyue.getInstance(getContext());
		this.mBook = book;
		this.mReadCallback = readCallback;
        mCatalog = new ArrayList<Catalog>();
        mBookInfo = new BookInfo();
        mBookCatalogs = new ArrayList<BookCatalog>();
		init();
	}


    @Override
    public void onReloadEnd(){
        super.onReloadEnd();
        loadStyleSetting(false);
    }

    @Override
    protected boolean isLayoutAll() {
        return false;
    }

	private ReadSetting mReadSetting;
	private void init() {
		ReaderMediaPlayer.init(getDataProvider());
		mHandler = new Handler(Looper.getMainLooper());
		mPreferencesUtil = PreferencesUtil.getInstance(getContext());
		mReadSetting  = ReadSetting.getInstance(getContext());
		mReadSetting.addDataListeners(this);
		loadStyleSetting(false);
	}

	private void loadStyleSetting(boolean isReLayout){
        setBackgroundColor(Color.BLACK);
		/*int bgRes = mReadSetting.getThemeBGImgRes();
		if(bgRes == -1){
			mBGDrawable = new ColorDrawable(mReadSetting.getThemeBGColor());
		}else{
			mBGDrawable = getResources().getDrawable(bgRes);
		}
		setBackgroundDrawable(mBGDrawable);*/
	}

	@Override
	protected boolean checkNeedBuy(int requestChapterIndex) {
		if (mReadCallback != null && mReadCallback.checkNeedBuy(requestChapterIndex, false)) {
			return true;
		}
		return false;
	}

	@Override
	protected boolean onPageChange(int current,int max){
		if(mReadCallback != null){
			mReadCallback.onPageChange(current, max);
		}
		return true;
	}

	@Override
	public void onLayoutChapterFinish(int chapterIndex, int progress, int max) {
		super.onLayoutChapterFinish(chapterIndex, progress, max);

        if(chapterIndex == mCurrentChapterIndex){
            mReadCallback.onLayoutProgressChange(progress, progress);
        }

	}

	@Override
	public TagHandler getTagHandler() {
		return new ExpandTagHandler(mDataProvider);
	}

	@Override
	public void onSettingChange(ReadSetting readSetting, String type) {
		if(type == ReadSetting.SETTING_TYPE_THEME){
			loadStyleSetting(true);
		}else if(type == ReadSetting.SETTING_TYPE_FONT_SIZE){

		}
	}

	/** 背景图片*/
	private Drawable mBGDrawable;

	private DataProvider mDataProvider = new DataProvider() {
		@Override
		public Drawable getDrawable(final String source,
				final DrawableContainer drawableContainer) {
			Drawable drawable = getResources().getDrawable(R.drawable.manh);
			ThreadFactory.createTerminableThreadInPool(new Runnable() {
                Bitmap bitmap = null;

                @Override
                public void run() {
                    if (drawableContainer.isInvalid()) {
                        return;
                    }
                    try {
                        InputStream is = getDataStream(source);
                        if (is != null) {
                            Options opts = new Options();
                            opts.inPreferredConfig = Bitmap.Config.RGB_565;
                            opts.inScaled = false;
                            opts.inPurgeable = true;
                            bitmap = BitmapFactory.decodeStream(is, null, opts);
                        }
                    } catch (Exception e) {
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BitmapDrawable bitmapDrawable = null;
                            if (bitmap != null) {
                                bitmapDrawable = new BitmapDrawable(
                                        bitmap);
                                bitmapDrawable
                                        .setTargetDensity(getResources()
                                                .getDisplayMetrics().densityDpi);
                            }
                            drawableContainer
                                    .setDrawable(bitmapDrawable);
                        }
                    });
                }
            }, ThreadPoolFactory.getReaderImageDownloaderPool()).start();
			return drawable;
		}

		@Override
		public Context getContext() {
			return NetEpubCartoonView.this.getContext().getApplicationContext();
		}

		@Override
		public InputStream getDataStream(String url) throws IOException {
            if (TextUtils.isEmpty(url)) {
                return null;
            }
            InputStream is = null;
            DefaultHttpClient httpClient = AbsConnect.getDefaultHttpClient(getContext());
            try {
                HttpGet httpGet = HttpUtil.getHttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntiry = httpResponse.getEntity();
                if (httpEntiry != null) {
                    is = httpEntiry.getContent();
                }
            } catch (Exception e) {
                com.lectek.lereader.core.util.LogUtil.e(TAG, e);
            }
            return is;
		}

		@Override
		public boolean hasData(String source) {
            return false;
		}
	}; 
       

	@Override
	public String getChapterInputStream(int chapterIndex) {
        String defaultString = "<html><body>无法阅读此章节.原因：<p>1.该章节未购买</p><p>2.书籍格式错误!</p></body></html>";
        String content = null;
        try {
            OnlineReadContentInfo onlineReadContentInfo = mApi.getOnlineReadContent(mBook.getBookId(),
                    mBookCatalogs.get(chapterIndex).getSequence() + "", getWidth(), getHeight());
            content = onlineReadContentInfo.getBookContents();
        } catch (Exception e) {
            com.lectek.lereader.core.util.LogUtil.e(TAG, e);
        }
        if(TextUtils.isEmpty(content)){
            content = defaultString;
        }
        return content;
	}

	@Override
	public ICssProvider getCssProvider() {
		return mCssProvider;
	}

	private CssProvider mCssProvider = new CssProvider(new ICssLoader() {
        private int BUFFER_SIZE = 1024;
		@Override
		public String load(String url) {
            String content = null;
            if (TextUtils.isEmpty(url)) {
                return content;
            }
            DefaultHttpClient httpClient = AbsConnect.getDefaultHttpClient(getContext());
            try {
                HttpGet httpGet = HttpUtil.getHttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntiry = httpResponse.getEntity();
                if (httpEntiry != null) {
                    InputStream is = httpEntiry.getContent();
                    byte[] responseByteArray = new byte[BUFFER_SIZE ];
                    ByteArrayBuffer bab = new ByteArrayBuffer(BUFFER_SIZE);
                    int line = -1;
                    while ((line = is.read(responseByteArray)) != -1) {
                        bab.append(responseByteArray, 0, line);
                        responseByteArray = new byte[BUFFER_SIZE];
                    }
                    content = new String(bab.toByteArray(), "utf-8");
                    bab.clear();
                    is.close();
                }
            } catch (Exception e) {
                com.lectek.lereader.core.util.LogUtil.e(TAG, e);
            } finally {
                if (httpClient != null) {
                    httpClient.getConnectionManager().shutdown();
                    httpClient = null;
                }
            }
            return content;
		}
	});
	
	@Override
	public DataProvider getDataProvider() {
		return mDataProvider;
	}
	@Override
	public PaserExceptionInfo getPaserExceptionInfo() {
		return new PaserExceptionInfo(mBook.getBookId(), mBook.getBookName(), mChapterSize);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	}

	@Override
	public int onInitReaderInBackground(final int fRequestCatalogIndex,final int fRequestPageCharIndex, String secretKey) {
        boolean isSucceed = false;
        try {
            mContentInfoLeyue = mApi.getContentInfo(mBook.getBookId()
                    , PreferencesUtil.getInstance(getContext()).getUserId());
            if(mContentInfoLeyue == null){
                isSucceed = false;
            }else{
                mBook.setOrder(mContentInfoLeyue.isOrder());
                mBookInfo.id = mContentInfoLeyue.getBookId();
                mBookInfo.author = mContentInfoLeyue.getAuthor();
                mBookInfo.title = mContentInfoLeyue.getBookName();
                mBookInfo.isCartoon = true;
                ArrayList<BookCatalog> bookCatalogs = mApi.getBookCatalogList(mBook.getBookId(),0,10000);
                if(bookCatalogs == null || bookCatalogs.size() == 0){
                    isSucceed = false;
                }else{
                    mCatalog.clear();
                    Catalog catalog = null;
                    int index = 0;
                    for (BookCatalog bookCatalog : bookCatalogs) {
                        catalog = new Catalog(null, index);
                        catalog.setText(bookCatalog.getName());
                       // catalog.setHref(bookCatalog.getPath());
                        mBookCatalogs.add(bookCatalog);
                        mCatalog.add(catalog);
                        index++;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initView(mBook.getBookId(), mCatalog.size(), fRequestCatalogIndex, fRequestPageCharIndex);
                        }
                    });
                    isSucceed = true;
                }
            }
        } catch (Exception e) {
            com.lectek.lereader.core.util.LogUtil.e(TAG, e);
            isSucceed = false;
        }
        if(!isSucceed){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showToast(getContext(), R.string.book_get_book_info_fault);
                }
            });
        }
        return isSucceed?SUCCESS:ERROR_GET_CONTENT_INFO;
    }


    /** 获取购买点*/
	private int getBuyIndex(){
		int feeIndex = -1;
		if (!TextUtils.isEmpty(mBook.getFeeStart())) {
			String feeStart = String.valueOf(mBook.getFeeStart());
			if(!TextUtils.isEmpty(feeStart) && !"null".equals(feeStart)){
				try {
					int start = feeStart.lastIndexOf("chapter") + 7;
					int end = feeStart.lastIndexOf(".");
					feeIndex = Integer.valueOf(feeStart.substring(start, end));
				} catch (Exception e) {
					LogUtil.e(e.getMessage());
				}
			}
		}else {
			feeIndex = -1;
		}
		return feeIndex;
	}
	// 将章节ID转换成章节列表索引
	private int loadCatalogID(String chapterID) {
        int index = -1;
        for (Catalog catalog : mCatalog) {
            index++;
            if(catalog.getHref().equals(chapterID)){
                return index;
            }
        }
        return index;
	}
	
	public final void runOnUiThread(Runnable action) {
        if (Thread.currentThread() != mHandler.getLooper().getThread()) {
        	mHandler.post(action);
        } else {
            action.run();
        }
    }
	@Override
	public ArrayList<Catalog> getChapterList() {
		return mCatalog;
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
		gotoChapter(mCatalog.indexOf(catalog), isStartAnim);
	}

	@Override
	public void gotoPage(int requestProgress, boolean isStartAnim) {
		int[] locals = new int[]{requestProgress, 0};// mPageManager.findPageIndexByTotal(requestProgress);
		if(locals != null){
			gotoPage(locals[0], locals[1], isStartAnim);
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasNextSet() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void gotoPreSet() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gotoNextSet() {
		// TODO Auto-generated method stub
		
	}
	private BookMark newBookMark() {
		BookMark bookMark = null;
		if(mBookInfo != null && mCurrentChapterIndex >= 0 && mCurrentChapterIndex <= mCatalog.size() - 1){
			bookMark = new BookMark();
			bookMark.setAuthor(mBookInfo.author);
			bookMark.setContentID(mBookInfo.id);
			bookMark.setBookmarkName(mBookInfo.title);
			bookMark.setUserID(mPreferencesUtil.getUserId());
			bookMark.setSoftDelete(DBConfig.BOOKMARK_STATUS_SOFT_DELETE_NO);
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
			bookMark.setPosition(getPageStartIndex(mCurrentChapterIndex, mCurrentPage));
		}
		return bookMark;
	}

	@Override
	public BookMark newUserBookmark() {
		return null;
	}

	@Override
	public int getMaxReadProgress() {
		return mPageManager.getLayoutChapterMax();
	}

	@Override
	public int getCurReadProgress() {
		return mCurrentChapterIndex;
	}

	@Override
	public BookInfo getBookInfo() {
		return mBookInfo;
	}

	@Override
	public Catalog getCurrentCatalog() {
		return mCatalog.get(mCurrentChapterIndex);
	}

	@Override
	public int getCurChapterIndex() {
		return mCurrentChapterIndex;
	}

	@Override
	public int getPageStartIndex(int chapterIndex, int pageIndex) {
		int requestPageIndex = mPageManager.findPageFirstIndex(chapterIndex,pageIndex);
		if(requestPageIndex >= 0){
			return requestPageIndex;
		}
		return 0;
	}
 
	@Override
	public void onShowContentView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHideContentView() {
		// TODO Auto-generated method stub
		
	}

 
	@Override
	public boolean dispatchClickEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canAddUserBookmark() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getContentView() {
		return this;
	}

	@Override
	public void dealBuyResult(int chapterId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onLongPress() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void search(int direction, String keyWord) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public String getChapterId(int chaptersId) {
        String href = mCatalog.get(chaptersId).getHref();
        int start = href.lastIndexOf('/');
        if(start == -1){
            start = 0;
        }else{
            start += 1;
        }
        int end = href.lastIndexOf('.');
        if(end == -1){
            return "";
        }
        return href.substring(start, end);
    }

	@Override
	public void onActivityResume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActivityPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActivitySaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object onActivityRetainNonConfigurationInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onActivityRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onActivityDispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onActivityDispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handlerSelectTouchEvent(
			MotionEvent event,
			com.lectek.android.lereader.ui.basereader_leyue.digests.AbsTextSelectHandler.ITouchEventDispatcher touchEventDispatcher) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TextSelectHandler getTextSelectHandler() {
		// TODO Auto-generated method stub
		return null;
	}

    protected void onNotPreContent() {
        mReadCallback.onNotPreContent();
    }

    protected void onNotNextContent() {
        mReadCallback.onNotNextContent();
    }
    @Override
    protected int[] getPage(int page){
        return new int[]{page, 0};
    }

    @Override
    protected int getPageIndex(int chapterIndex, int page){
        return chapterIndex;
    }
    @Override
    protected int getPageCount(){
        return mPageManager.getLayoutChapterMax();
    }
}
