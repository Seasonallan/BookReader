package com.lectek.android.lereader.ui.basereader_leyue.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.net.ResponseResultCode;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.net.http.AbsConnect;
import com.lectek.android.lereader.lib.net.http.HttpUtil;
import com.lectek.android.lereader.lib.thread.ThreadFactory;
import com.lectek.android.lereader.lib.thread.ThreadPoolFactory;
import com.lectek.android.lereader.lib.utils.BitmapUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.IHttpRequest4Leyue;
import com.lectek.android.lereader.net.openapi.ApiProcess4TianYi;
import com.lectek.android.lereader.net.openapi.ResponseCode;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.net.response.tianyi.Chapter;
import com.lectek.android.lereader.net.response.tianyi.ContentInfo;
import com.lectek.android.lereader.net.response.tianyi.OrderedResult;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.basereader_leyue.Book;
import com.lectek.android.lereader.utils.UserManager;
import com.lectek.lereader.core.bookformats.BookInfo;
import com.lectek.lereader.core.bookformats.Catalog;
import com.lectek.lereader.core.text.html.CssProvider;
import com.lectek.lereader.core.text.html.CssProvider.ICssLoader;
import com.lectek.lereader.core.text.html.DataProvider;
import com.lectek.lereader.core.text.html.ICssProvider;

public class NetCebReadView extends BaseHtmlReadView implements DataProvider{
	private static long LAST_PRIORITY = 0;
	private ArrayList<Catalog> mCatalog;
	private ArrayList<Chapter> mBookCatalogs;
	private BookInfo mBookInfo;
	private ContentInfo mContentInfoLeyue;
	private IHttpRequest4Leyue mApi;
    private String surfingBookId;
	public NetCebReadView(Context context, Book book, IReadCallback readCallback) {
		super(context, book, readCallback);
		mApi = ApiProcess4Leyue.getInstance(getContext());
		mCatalog = new ArrayList<Catalog>();
		mBookCatalogs = new ArrayList<Chapter>();
		mBookInfo = new BookInfo();
		mBookInfo.id = book.getBookId();
		mBookInfo.author = book.getAuthor();
		mBookInfo.title = book.getBookName();
	}

	@Override
	public String getChapterInputStream_(int chapterIndex) {
		String defaultString = "<html><body>无法阅读此章节.原因：<p>1.该章节未购买</p><p>2.书籍格式错误!</p></body></html>";
		String content = null;
		try {
            String chapterID = getChapterId(chapterIndex);
            Chapter chapter = ApiProcess4TianYi.getInstance(getContext()).getChapterInfo(surfingBookId, chapterID);
			content = chapter.getContent();
		} catch (Exception e) {
			LogUtil.e(TAG, e);
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

	@Override
	public DataProvider getDataProvider() {
		return this;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public int onInitReaderInBackground(final int fRequestCatalogIndex,final int fRequestPageCharIndex, String secretKey) {

        try {
            ContentInfoLeyue contentInfoLeyue = ApiProcess4Leyue.getInstance(getContext()).getContentInfo(mBook.getBookId()
                    , PreferencesUtil.getInstance(getContext()).getUserId());
            if(contentInfoLeyue != null){
                surfingBookId = contentInfoLeyue.getOutBookId();
                if(!TextUtils.isEmpty(surfingBookId)){
                	 ContentInfo contentInfo = ApiProcess4TianYi.getInstance(getContext()).getBaseContent(surfingBookId);
                     if(contentInfo != null && !UserManager.getInstance(getContext()).isGuset()){
                         OrderedResult orderedResult = ApiProcess4TianYi.getInstance(getContext()).isContentOrdered(surfingBookId);
                         if(orderedResult != null && orderedResult.getCode() == ResponseCode.HAD_EXIST_ORDERED){
                             contentInfo.setOrdered(true);
                         }
                     }
                     mContentInfoLeyue = contentInfo;
//                    mContentInfoLeyue = ContentInfoModel.getContentInfo(getContext(), surfingBookId);
                }
            }
        } catch (GsonResultException exception) {
            if (exception != null && ResponseResultCode.STATUS_NO_FIND_BOOK_OFF_LINE.equals(exception.getResponseInfo().getErrorCode())){
                return ERROR_BOOK_OFFLINE;
            }
            return ERROR_GET_CONTENT_INFO;
        }
		if(mContentInfoLeyue == null){
            return ERROR_GET_CONTENT_INFO;
		}
		mBook.setOrder(mContentInfoLeyue.isOrdered());
        mBook.setBookName(mContentInfoLeyue.getBookName());
        mBook.setPrice(mContentInfoLeyue.getFeePrice());

		mBookInfo.id = mContentInfoLeyue.getBookId();
		mBookInfo.author = mContentInfoLeyue.getAuthorName();
		mBookInfo.title = mContentInfoLeyue.getBookName();
        mReadCallback.setCebBookId(surfingBookId);
        mBookCatalogs = ApiProcess4TianYi.getInstance(getContext()).
                getBookChapterListNew(surfingBookId, 0, Integer.MAX_VALUE);
        if (mBookCatalogs == null || mBookCatalogs.size() == 0){
            return ERROR_GET_CATALOG_INFO;
        }

        int feeStart = mBook.isOrder() ? Integer.MAX_VALUE : -1;
        if(mBook.isOrder()){
            mReadCallback.setFreeStart_Order_Price(feeStart, mBook.isOrder(), mBook.getPrice(), null);
        }
        for(int i = 0; i < mBookCatalogs.size(); i++){
            Chapter chapter = mBookCatalogs.get(i);
            Catalog catalog = new Catalog(null, i);
            if(chapter.getIsFree() == 1 && feeStart < 0){
                feeStart = i;
                mReadCallback.setFreeStart_Order_Price(feeStart, mBook.isOrder(), mBook.getPromotionPrice(), null);
            }

            catalog.setText(chapter.getChapterName());
            catalog.setHref(chapter.getChpaterId());
            mCatalog.add(catalog);
        }

		runOnUiThread(new Runnable() {
						@Override
						public void run() {
							initView(mBookCatalogs.size(), fRequestCatalogIndex, fRequestPageCharIndex);
						}
					});
		return SUCCESS;
	}

	@Override
	protected boolean interceptGotoPage(int chapterIndex, int pageIndex) {
		boolean isNeedBuy = false;
        if(1 == mBookCatalogs.get(chapterIndex).getIsFree()){
            if(!"0".equals(mContentInfoLeyue.getFeeType())){
                if(!mBook.isOrder()){
                    isNeedBuy = true;
                }
            }
        }
		return mReadCallback.checkNeedBuy(chapterIndex, isNeedBuy);
	}
	
	@Override
	public ArrayList<Catalog> getChapterList() {
		return mCatalog;
	}

	@Override
	public BookInfo getBookInfo() {
		return mBookInfo;
	}

	@Override
	public String getChapterId(int chapterIndex) {
		return mCatalog.get(chapterIndex).getHref();
	}

	@Override
	protected int getChapterIndex(Catalog catalog) {
		return mCatalog.indexOf(catalog);
	}

	@Override
	protected Catalog getCatalogByIndex(int chapterIndex) {
		if(mCatalog == null || mCatalog.size() == 0 || chapterIndex >= mCatalog.size()){
			Catalog catalog = new Catalog(null, 0);
			catalog.setText("正文");
			return catalog;
		}
		return mCatalog.get(chapterIndex);
	}

	@Override
	protected String getChapterName(int chapterIndex) {
		return getCatalogByIndex(chapterIndex).getText();
	}

	@Override
	protected int loadCatalogID(String chapterID) {
		int index = -1;
		for (Catalog catalog : mCatalog) {
			index++;
			if(catalog.getHref().equals(chapterID)){
				return index;
			}
		}
		return index;
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
			LogUtil.e(TAG, e);
		}
		return is;
	}

	@Override
	public Drawable getDrawable(final String source,final DrawableContainer drawableContainer) {
		Drawable drawable = getResources().getDrawable(R.drawable.manh);
        ThreadFactory.createTerminableThreadInPool(new Runnable() {
            Bitmap bitmap = null;

            @Override
            public void run() {
                if (drawableContainer.isInvalid()) {
                    return;
                }
                try {
                    bitmap = BitmapUtil.clipScreenBoundsBitmap(getResources(), getDataStream(source));
                } catch (Exception e) {
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BitmapDrawable bitmapDrawable = null;
                        if (bitmap != null) {
                            bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                        }
                        drawableContainer.setDrawable(bitmapDrawable);
                    }
                });
            }
        }, ThreadPoolFactory.getReaderImageDownloaderPool()).start();
		return drawable;
	}

	@Override
	public boolean hasData(String arg0) {
		return false;
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
				LogUtil.e(TAG, e);
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
	protected boolean isLayoutAll() {
		return false;
	}
}
