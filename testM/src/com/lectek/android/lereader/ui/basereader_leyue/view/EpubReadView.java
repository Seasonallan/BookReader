package com.lectek.android.lereader.ui.basereader_leyue.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.thread.ThreadFactory;
import com.lectek.android.lereader.lib.thread.ThreadPoolFactory;
import com.lectek.android.lereader.lib.utils.BitmapUtil;
import com.lectek.android.lereader.lib.utils.EncryptUtils;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.ui.basereader_leyue.Book;
import com.lectek.lereader.core.bookformats.BookInfo;
import com.lectek.lereader.core.bookformats.Catalog;
import com.lectek.lereader.core.bookformats.Chapter;
import com.lectek.lereader.core.bookformats.FormatPlugin;
import com.lectek.lereader.core.bookformats.PluginManager;
import com.lectek.lereader.core.bookformats.epub.Resource;
import com.lectek.lereader.core.text.html.CssProvider;
import com.lectek.lereader.core.text.html.CssProvider.ICssLoader;
import com.lectek.lereader.core.text.html.DataProvider;
import com.lectek.lereader.core.text.html.ICssProvider;

public class EpubReadView extends BaseHtmlReadView {
	private String mSecretKey;
	private FormatPlugin mPlugin;
	private BookInfo mBookInfo;

	public EpubReadView(Context context, Book book, IReadCallback readCallback) {
		super(context, book, readCallback);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}


	@Override
	public int onInitReaderInBackground(final int fRequestCatalogIndex,final int fRequestPageCharIndex, String secretKey) {
        mSecretKey = secretKey;
		try {
            try {
                mPlugin = PluginManager.instance().getPlugin(mBook.getPath(), secretKey);
            }catch (Exception e){
            }
			// 书籍信息
			mBookInfo = mPlugin.getBookInfo();
			mBookInfo.id = mBook.getBookId();
            mReadCallback.setFreeStart_Order_Price(Integer.MAX_VALUE , true, null, null);
			// 读章节信息
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(!mBook.isOrder() && getBuyIndex() != -1){
						if (fRequestCatalogIndex >= getBuyIndex()) {
							initView(getBuyIndex() + 1, 0, fRequestPageCharIndex);
						}else {
							initView(getBuyIndex() + 1, fRequestCatalogIndex, fRequestPageCharIndex);
						}
					}else{
						initView(mPlugin.getChapterIds().size(), fRequestCatalogIndex, fRequestPageCharIndex);
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
	
	@Override
	public String getChapterInputStream_(int chapterIndex) {
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

	@Override
	public DataProvider getDataProvider() {
		return mDataProvider;
	}

	@Override
	public ArrayList<Catalog> getChapterList() {
		return mPlugin.getCatalog();
	}

	@Override
	public BookInfo getBookInfo() {
		return mBookInfo;
	}

	@Override
	public String getChapterId(int chapterIndex) {
		return mPlugin.getChapterIds().get(chapterIndex);
	}

	@Override
	protected int getChapterIndex(Catalog catalog) {
		return mPlugin.getChapterIndex(catalog);
	}

	@Override
	protected Catalog getCatalogByIndex(int chapterIndex) {
        if (chapterIndex < 0){
            return null;
        }
		return mPlugin.getCatalogByIndex(chapterIndex);
	}

	@Override
	protected String getChapterName(int chapterIndex) {
		Catalog catalog = getCatalogByIndex(chapterIndex);
		if(catalog != null && !TextUtils.isEmpty(catalog.getText())){
			return catalog.getText();
		}
		return "";
	}

	@Override
	protected int loadCatalogID(String chapterID) {
		int index = mPlugin.getChapterIds().indexOf(chapterID);
		return index < 0 ? 0 : index;
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
	
	private DataProvider mDataProvider = new DataProvider() {
		@Override
		public Drawable getDrawable(final String source,final DrawableContainer drawableContainer) {
			Drawable drawable = getResources().getDrawable(R.drawable.manh);
            ThreadFactory.createTerminableThreadInPool(new Runnable() {
				Bitmap bitmap = null;
				@Override
				public void run() {
					if(drawableContainer.isInvalid()){
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
								bitmapDrawable = new BitmapDrawable(getResources(),bitmap);
							}
							drawableContainer.setDrawable(bitmapDrawable);
						}
					});
				}
            }, ThreadPoolFactory.getReaderImageDownloaderPool()).start();
			return drawable;
		}

		@Override
		public Context getContext() {
			return EpubReadView.this.getContext().getApplicationContext();
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
}
