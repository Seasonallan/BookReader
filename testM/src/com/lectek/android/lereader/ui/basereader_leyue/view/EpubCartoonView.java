package com.lectek.android.lereader.ui.basereader_leyue.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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
import com.lectek.android.lereader.lib.thread.ThreadFactory;
import com.lectek.android.lereader.lib.thread.ThreadPoolFactory;
import com.lectek.android.lereader.lib.utils.EncryptUtils;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.basereader_leyue.Book;
import com.lectek.android.lereader.ui.basereader_leyue.ReadSetting;
import com.lectek.android.lereader.ui.basereader_leyue.ReadSetting.SettingListener;
import com.lectek.android.lereader.ui.basereader_leyue.digests.TextSelectHandler;
import com.lectek.android.lereader.ui.basereader_leyue.span.ExpandTagHandler;
import com.lectek.android.lereader.ui.basereader_leyue.widgets.ReaderMediaPlayer;
import com.lectek.lereader.core.bookformats.BookInfo;
import com.lectek.lereader.core.bookformats.Catalog;
import com.lectek.lereader.core.bookformats.Chapter;
import com.lectek.lereader.core.bookformats.FormatPlugin;
import com.lectek.lereader.core.bookformats.PaserExceptionInfo;
import com.lectek.lereader.core.bookformats.PluginManager;
import com.lectek.lereader.core.bookformats.epub.Resource;
import com.lectek.lereader.core.text.html.CssProvider;
import com.lectek.lereader.core.text.html.CssProvider.ICssLoader;
import com.lectek.lereader.core.text.html.DataProvider;
import com.lectek.lereader.core.text.html.HtmlParser.TagHandler;
import com.lectek.lereader.core.text.html.ICssProvider;

/**
 * 本地漫画阅读器
 * @author laijp
 * @date 2014-3-5
 * @email 451360508@qq.com
 */
public class EpubCartoonView extends BaseCartoonView implements IReaderView, SettingListener{
	private PreferencesUtil mPreferencesUtil;
	private Handler mHandler;
	private String mSecretKey;
	private BookInfo mBookInfo;
	private FormatPlugin mPlugin; 

	protected IReadCallback mReadCallback;
	protected Book mBook;
	public EpubCartoonView(Context context, Book book, IReadCallback readCallback) {
		super(context);
		this.mBook = book;
		this.mReadCallback = readCallback;
		init();
	} 
	
	private ReadSetting mReadSetting;
	private void init() {
		ReaderMediaPlayer.init(getDataProvider());
		mHandler = new Handler(Looper.getMainLooper());
		mPreferencesUtil = PreferencesUtil.getInstance(getContext()); 
		mReadSetting  = ReadSetting.getInstance(getContext());
		mReadSetting.addDataListeners(this);
	}

    @Override
    public void onReloadEnd(){
        super.onReloadEnd();
        loadStyleSetting(false);
    }


    @Override
    protected boolean isLayoutAll() {
        return true;
    }

    private void loadStyleSetting(boolean isReLayout){
		setBackgroundColor(Color.BLACK);
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
		if(mReadCallback != null){
			mReadCallback.onLayoutProgressChange(progress, max);
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
					if(drawableContainer.isInvalid()){
						return;
					}
					try {
						InputStream is = getDataStream(source);
						if (is != null) {
							Options opts = new BitmapFactory.Options();
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
			return EpubCartoonView.this.getContext().getApplicationContext();
		}

		@Override
		public InputStream getDataStream(String source) throws IOException {
			Resource resource = mPlugin.findResource(source);
			if(resource != null){
				//目前media文件不加密。音视频
				if (source.endsWith(".mp3")||source.endsWith(".mp4")) {//服务器对此后缀不加密
					if (mBookInfo.isMediaDecode) {
						if (TextUtils.isEmpty(mSecretKey)) {
							return resource.getDataStream();
						}else {
							return EncryptUtils.decryptByAES(resource.getDataStream(),mSecretKey);
						}
					}else {
						return resource.getDataStream();
					}
				}else {
					if (TextUtils.isEmpty(mSecretKey)) {
						return resource.getDataStream();
					}else {
						return EncryptUtils.decryptByAES(resource.getDataStream(),mSecretKey);
					}
				}
			}
			return null;
			
		}

		@Override
		public boolean hasData(String source) {
			Resource resource = mPlugin.findResource(source);
			return resource != null;
		}
	}; 
       

	@Override
	public String getChapterInputStream(int chapterIndex) {
		String defaultString = "<html><body>无法阅读此章节.原因：<p>1.该章节未购买</p><p>2.书籍格式错误-请到书架删除后重新下载！</p></body></html>";
		//用于触发购买点
		String content = defaultString;
		Chapter chapter = null;
		try {
			chapter = mPlugin.getChapter(mPlugin.getChapterIds().get(chapterIndex));
		} catch (Exception e) {
			LogUtil.e(TAG, e);
		}
		if (chapter != null) {
			byte[] contentByte = null;
			try {
				if (TextUtils.isEmpty(mSecretKey)) {
					contentByte = chapter.getContent();
				}else {
					contentByte = EncryptUtils.decryptByAES(chapter.getContent(),mSecretKey);
				}
				if(contentByte != null){
					content = new String(contentByte);
				}
			} catch (Exception e) {
				if(contentByte != null){
					content = new String(contentByte);
				}
			}
		}
		if(TextUtils.isEmpty(content)){
			content = defaultString;
		}else if(content.indexOf("<html") == -1){
			StringBuffer temp = new StringBuffer();
			temp.append("<html><body>");
			temp.append(content);
			temp.append("</body></html>");
			content = temp.toString();
		}
		return content;
	}

	@Override
	public ICssProvider getCssProvider() {
		return mCssProvider;
	}

	private CssProvider mCssProvider = new CssProvider(new ICssLoader() {
		@Override
		public String load(String path) {
			try {
				Resource resource = mPlugin.findResource(path);
				byte[] data = null;
				if(resource != null){
					if (TextUtils.isEmpty(mSecretKey)) {
						data = resource.getData();
					}else {
						data = EncryptUtils.decryptByAES(resource.getData(),mSecretKey);
					}
				}
				if (data != null) {
					return new String(data);
				}
			} catch (Exception e) {
			}
			return null;
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
	public int onInitReaderInBackground(final int fRequestCatalogIndex,final int fRequestPageCharIndex , String secretKey) {
        mSecretKey = secretKey;
		try {
            try {
                mPlugin = PluginManager.instance().getPlugin(mBook.getPath(),mSecretKey);
            }catch (Exception e){
            }
            mBookInfo = mPlugin.getBookInfo();
			mBookInfo.id = mBook.getBookId();
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(!mBook.isOrder() && getBuyIndex() != -1){
						if (fRequestCatalogIndex >= getBuyIndex()) {
							initView(mBook.getBookId(), getBuyIndex() + 1, 0, fRequestPageCharIndex);
						}else {
							initView(mBook.getBookId(), getBuyIndex() + 1, fRequestCatalogIndex, fRequestPageCharIndex);
						}
					}else{
						initView(mBook.getBookId(), mPlugin.getChapterIds().size(), fRequestCatalogIndex, fRequestPageCharIndex);
					}
				}
			});
		} catch (Exception e) {
			LogUtil.e(TAG, e);
            return ERROR_GET_CONTENT_INFO;
		}
		return SUCCESS;
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
		int index = mPlugin.getChapterIds().indexOf(chapterID);
		return index < 0 ? 0 : index;
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
		return mPlugin.getCatalog();
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
		gotoChapter(mPlugin.getChapterIndex(catalog), isStartAnim);
	}
	
	@Override
	public void gotoPage(int requestProgress, boolean isStartAnim) {
		int[] locals = mPageManager.findPageIndexByTotal(requestProgress);
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
		if(mBookInfo != null && mPlugin != null && mCurrentChapterIndex >= 0 && mCurrentChapterIndex <= mPlugin.getChapterIds().size() - 1){
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
		return mPageManager.getTotalPageSize();
	}

	@Override
	public int getCurReadProgress() {
		return mPageManager.getTotalPageIndex(mCurrentChapterIndex, mCurrentPage);
	} 

	@Override
	public BookInfo getBookInfo() {
		return mBookInfo;
	}

	@Override
	public Catalog getCurrentCatalog() {
		return mPlugin.getCatalogByIndex(mCurrentChapterIndex);
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
        return mPlugin.getChapterIds().get(chaptersId);
    }

    @Override
	public void onDestroy() {
	    super.onDestroy();
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

}
