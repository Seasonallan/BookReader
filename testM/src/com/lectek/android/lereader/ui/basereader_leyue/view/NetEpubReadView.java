package com.lectek.android.lereader.ui.basereader_leyue.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.contentinfo.ContentInfoViewModelLeyue;
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
import com.lectek.android.lereader.net.response.BookCatalog;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.net.response.OnlineReadContentInfo;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.basereader_leyue.Book;
import com.lectek.android.lereader.utils.PriceUtil;
import com.lectek.lereader.core.bookformats.BookInfo;
import com.lectek.lereader.core.bookformats.Catalog;
import com.lectek.lereader.core.text.html.CssProvider;
import com.lectek.lereader.core.text.html.CssProvider.ICssLoader;
import com.lectek.lereader.core.text.html.DataProvider;
import com.lectek.lereader.core.text.html.ICssProvider;

public class NetEpubReadView extends BaseHtmlReadView implements DataProvider{
	private static long LAST_PRIORITY = 0;
	private ArrayList<Catalog> mCatalog;
	private ArrayList<BookCatalog> mBookCatalogs;
	private BookInfo mBookInfo;
	private ContentInfoLeyue mContentInfoLeyue;
	private IHttpRequest4Leyue mApi;
	public NetEpubReadView(Context context, Book book,IReadCallback readCallback) {
		super(context, book, readCallback);
		mApi = ApiProcess4Leyue.getInstance(getContext());
		mCatalog = new ArrayList<Catalog>();
		mBookCatalogs = new ArrayList<BookCatalog>();
		mBookInfo = new BookInfo();
		mBookInfo.id = book.getBookId();
		mBookInfo.author = book.getAuthor();
		mBookInfo.title = book.getBookName();
	}

	@Override
	public String getChapterInputStream_(int chapterIndex) {
        String defaultString = "<html><body>无法阅读此章节.原因：<p>1.该章节未购买</p><p>2.书籍格式错误!</p></body></html>";
        String filePath = getContext().getCacheDir() +"/"+ mBook.getBookId() +"/"+ chapterIndex;
        if(new File(filePath).exists()){
            try {
                FileInputStream fin = new FileInputStream(filePath);
                // FileInputStream fin = openFileInput(fileName);
                // 用这个就不行了，必须用FileInputStream
                int length = fin.available();
                byte[] buffer = new byte[length];
                fin.read(buffer);
                String res = EncodingUtils.getString(buffer, "UTF-8");////依Y.txt的编码类型选择合适的编码，如果不调整会乱码
                fin.close();//关闭资源
                return res;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return defaultString;
        }else{
            String content = null;
            try {
                OnlineReadContentInfo onlineReadContentInfo = mApi.getOnlineReadContent(mBook.getBookId(),
                        mBookCatalogs.get(chapterIndex).getSequence()+"", getWidth(), getHeight());
                content = onlineReadContentInfo.getBookContents();
            } catch (Exception e) {
                LogUtil.e(TAG, e);
            }
            if(TextUtils.isEmpty(content)){
                content = defaultString;
            }else{
                File cacheDir = getContext().getCacheDir();
                File dir = new File(cacheDir, mBook.getBookId());
                if (!dir.exists()){
                    dir.mkdir();
                }
                File chapter = new File(dir, chapterIndex+"");
                try {
                    chapter.createNewFile();
                        FileWriter fw = new FileWriter(filePath, true);
                        fw.write(content);
                        fw.flush();
                        fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return content;
        }
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
	public int onInitReaderInBackground(final int fRequestCatalogIndex,final int fRequestPageCharIndex , String secretKey) {
		try {
			mContentInfoLeyue = mApi.getContentInfo(mBook.getBookId()
					, PreferencesUtil.getInstance(getContext()).getUserId());
        } catch (GsonResultException exception) {
            if (exception != null && ResponseResultCode.STATUS_NO_FIND_BOOK_OFF_LINE.equals(exception.getResponseInfo().getErrorCode())){
                return ERROR_BOOK_OFFLINE;
            }
            return ERROR_GET_CONTENT_INFO;
        }

        try {
            mBookCatalogs = mApi.getBookCatalogList(mBook.getBookId(),0,10000);
        } catch (Exception e) {
            return ERROR_GET_CATALOG_INFO;
        }
        if(mContentInfoLeyue == null){
            return ERROR_GET_CONTENT_INFO;
        }else if(mBookCatalogs == null || mBookCatalogs.size() == 0){
            return ERROR_GET_CATALOG_INFO;
        }
        String price = mContentInfoLeyue.getPromotionPrice();
        String limitPrice = null;
		mBook.setOrder(mContentInfoLeyue.isOrder());
        mBook.setPrice(mContentInfoLeyue.getPrice());
        if(mContentInfoLeyue.getLimitType() != null){
            if(mContentInfoLeyue.getLimitType() == ContentInfoViewModelLeyue.LIMIT_TYPE_FREE){
                mBook.setOrder(true);
            }else if(mContentInfoLeyue.getLimitType() == ContentInfoViewModelLeyue.LIMIT_TYPE_PRICE
                    && mContentInfoLeyue.getLimitPrice() > 0){
            	limitPrice = PriceUtil.formatPrice(mContentInfoLeyue.getLimitPrice() + "");
            }
        }
        mBook.setFeeStart(mContentInfoLeyue.getFeeStart());

		mBookInfo.id = mContentInfoLeyue.getBookId();
		mBookInfo.author = mContentInfoLeyue.getAuthor();
		mBookInfo.title = mContentInfoLeyue.getBookName();
		mCatalog.clear();
		Catalog catalog = null;
		int index = 0;
        int feeStart = mBook.isOrder() ? Integer.MAX_VALUE : -1;
        if(mBook.isOrder()){
            mReadCallback.setFreeStart_Order_Price(feeStart, mBook.isOrder(), price, limitPrice);
        }
		for (BookCatalog bookCatalog : mBookCatalogs) {
			catalog = new Catalog(null, index);
            if("1".equals(bookCatalog.getCalpoint()) && feeStart < 0){
                feeStart = index;
                mReadCallback.setFreeStart_Order_Price(feeStart, mBook.isOrder(), price, limitPrice);
            }
			catalog.setText(bookCatalog.getName());
			catalog.setHref(bookCatalog.getPath());
			mCatalog.add(catalog);
			index++;
		}
		runOnUiThread(new Runnable() {
					@Override
					public void run() {
				initView(mCatalog.size(), fRequestCatalogIndex, fRequestPageCharIndex);
			}
		});
		return SUCCESS;
	}

	@Override
	protected boolean interceptGotoPage(int chapterIndex, int pageIndex) {
		boolean isNeedBuy = false;
		if("1".equals(mBookCatalogs.get(chapterIndex).getCalpoint()) && !mBook.isOrder() && !"0".equals(mContentInfoLeyue.getIsFee())){
			isNeedBuy = true;
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
		String href = mCatalog.get(chapterIndex).getHref();
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
			if(catalog.getHref().indexOf(chapterID) != -1){
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
