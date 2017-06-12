package com.lectek.android.lereader.ui.basereader_leyue.view;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.lectek.android.LYReader.R;
import com.lectek.android.LYReader.dialog.LeYueDialog;
import com.lectek.android.lereader.lib.utils.FileUtil;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.plugin.ExPluginPdfSo;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.dbase.mark.BookMarkDB;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.basereader_leyue.Book;
import com.lectek.android.lereader.ui.basereader_leyue.ReadSetting;
import com.lectek.android.lereader.ui.basereader_leyue.ReadSetting.SettingListener;
import com.lectek.android.lereader.ui.basereader_leyue.digests.AbsTextSelectHandler.ITouchEventDispatcher;
import com.lectek.android.lereader.ui.basereader_leyue.digests.TextSelectHandler;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.DialogUtil.CancelListener;
import com.lectek.android.lereader.utils.DialogUtil.ConfirmListener;
import com.lectek.android.lereader.utils.ToastUtil;
import com.lectek.lereader.core.bookformats.BookInfo;
import com.lectek.lereader.core.bookformats.Catalog;
import com.lectek.lereader.core.pdf.PageView;
import com.lectek.lereader.core.pdf.PageView.PageViewCallback;
import com.lectek.lereader.core.pdf.PdfLog;
import com.lectek.lereader.core.pdf.PdfPageAdapter;
import com.lectek.lereader.core.pdf.PdfReaderView;
import com.lectek.lereader.core.pdf.jni.MuPDFCore;
import com.lectek.lereader.core.pdf.jni.OutlineItem;
/**
 * PDF阅读界面
 * @author zhouxinghua
 *
 */
public class PdfReadView extends PdfReaderView implements IReaderView {
	private MuPDFCore mCore;
	private Activity mActivity;
	private Book mBook;
	private IReadCallback mReadCallback;
	private AsyncTask<Integer,Integer,SearchTaskResult> mSearchTask;
	private PreferencesUtil mPreferencesUtil;

	public PdfReadView(Activity context, Book book, IReadCallback readCallback) {
		super(context);
		mActivity = context;
		mBook = book;
		mReadCallback = readCallback;
	}
	
	@Override
	public void onChildSetup(int i, View v) {
		if(v != null && v instanceof PageView) {
			if (SearchTaskResult.get() != null && SearchTaskResult.get().pageNumber == i) {
				((PageView)v).setSearchBoxes(SearchTaskResult.get().searchBoxes);
			} else {
				((PageView)v).setSearchBoxes(null);
			}
		}
	}

	@Override
	public void onMoveToChild(int i) {
		if(mCore == null) {
			return;
		}
		if (SearchTaskResult.get() != null && SearchTaskResult.get().pageNumber != i) {
			SearchTaskResult.set(null);
			resetupChildren();
		}
	}
	
	private SettingListener mSettingListener = new SettingListener() {
		@Override
		public void onSettingChange(ReadSetting readSetting, String type) {
			if(ReadSetting.SETTING_TYPE_PDF_LAYOUT_TYPE.equals(type)) {
				int layoutType = readSetting.getPdfLayoutType();
				if(layoutType == PdfReaderView.LAYOUT_TYPE_FITSCREEN) {
					fitScreen();
				} else if (layoutType == PdfReaderView.LAYOUT_TYPE_FITWIDTH) {
					fitWidth();
				}
			} else if (ReadSetting.SETTING_TYPE_ORIENTATION.equals(type)) {
				mActivity.setRequestedOrientation(readSetting.getOrientationType());
				postDelayed(new Runnable() {
					@Override
					public void run() {
                        setDisplayedViewIndex(getDisplayedViewIndex());
					}
				}, 200);
			}
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState) {
 
        ExPluginPdfSo pluginPdfSo = new ExPluginPdfSo(getContext());
        String soPath = pluginPdfSo.getPath()+"/"+pluginPdfSo.getName();
        if (FileUtil.isFileExists(soPath)){
            System.load(soPath);
        }else{
            System.loadLibrary("pdf");
        }


        ReadSetting readSetting = ReadSetting.getInstance(getContext());
        readSetting.addDataListeners(mSettingListener);

        mLayoutType = readSetting.getPdfLayoutType();

        mPreferencesUtil = PreferencesUtil.getInstance(getContext());

        boolean soCheck = true;
        if(mBook != null && !TextUtils.isEmpty(mBook.getPath())) {
            long openTime = System.currentTimeMillis();
/*            try {
                File cacheFile = getContext().getCacheDir();
                File dirFile = cacheFile.getParentFile();
                for (File file : dirFile.listFiles()){
                    if (file.getName().contains("lib")){
                        for (File libFile: file.listFiles()){
                            if (libFile.getName().equals("libpdf.so")){
                                soCheck = true;
                            }
                        }
                    }
                }
            }catch (Exception e){
            }*/
            if (soCheck){
                mCore = openFile(mBook.getPath());
                openTime = System.currentTimeMillis() - openTime;
                PdfLog.d("打开书籍的时间： " + openTime);
            }
        }

        if(mCore == null) {
            // 打开失败
            Dialog dialog = DialogUtil.commonConfirmDialog(mActivity, null, mActivity.getString(
                    !soCheck?R.string.pdf_lib_error:R.string.pdf_reading_open_fail),
                    -1, -1, new ConfirmListener() {
                        @Override
                        public void onClick(View v) {
                            mActivity.finish();
                        }
                    }, new CancelListener() {
                        @Override
                        public void onClick(View v) {
                            mActivity.finish();
                        }
                    });
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            return;
        }

        // 密码验证
        if(mCore != null && mCore.needsPassword()) {
            showPasswordDialog(savedInstanceState);
            return;
        }

        createUI(savedInstanceState);
	}
	
	private void createUI(Bundle savedInstanceState) {
		if(mCore == null) {
			return;
		}
		setAdapter(new PdfPageAdapter(getContext(), mCore, new PageViewCallback() {
			@Override
			public void setLoadingBg(ProgressBar progressBar) {
				progressBar.setBackgroundResource(R.drawable.pdf_reading_loading_bg);
			}
		}));
		// 跳转到上次看的地方
		BookMark bookMark = BookMarkDB.getInstance().getSystemBookmark(mBook.getBookId(),
				PreferencesUtil.getInstance(mActivity).getUserId());
		if(bookMark != null) {
			setDisplayedViewIndex(bookMark.getPosition());
		} else {
			setDisplayedViewIndex(0);
		}
		//设置横竖屏
		mActivity.setRequestedOrientation(ReadSetting.getInstance(mActivity).getOrientationType());
	}
	
	/**
	 * 打开pdf文件
	 * @param path
	 * @return
	 */
	private MuPDFCore openFile(String path)
	{
		MuPDFCore core = null;
		PdfLog.d("Trying to open "+path);
		try
		{
			core = new MuPDFCore(path);
		}
		catch (Exception e)
		{
			PdfLog.e(e.getMessage());
			return core;
		}
		return core;
	}
	
	/**
	 * 显示密码验证对话框
	 * @param savedInstanceState
	 */
	private void showPasswordDialog(final Bundle savedInstanceState) {
		final LeYueDialog dialog = new LeYueDialog(mActivity);
		dialog.setCancelable(false);
		dialog.setTitle(R.string.pdf_reading_input_password);
		View view = View.inflate(mActivity, R.layout.pdf_verify_password_lay, null);
		dialog.setContentLay(view);
		
		final EditText et = (EditText) view.findViewById(R.id.password_et);
		et.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					verifyPassword(savedInstanceState, dialog, et.getText().toString().trim());
				}
				return false;
			}
		});
		
		// TODO 确认按钮按下后dialog会不会消失
		
		dialog.dealDialogBtn(-1, new ConfirmListener() {
			@Override
			public void onClick(View v) {
				verifyPassword(savedInstanceState, dialog, et.getText().toString().trim());
			}
		}, -1, new CancelListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				mActivity.finish();
			}
		}, false);
		
		dialog.show();
	}
	
	/**
	 * 手动验证密码
	 * @param savedInstanceState
	 * @param password
	 */
	private void verifyPassword(Bundle savedInstanceState, Dialog dialog, String password) {
		if(TextUtils.isEmpty(password)) {
			ToastUtil.showToast(mActivity, R.string.pdf_reading_input_password);
			return;
		}
		if(mCore != null && mCore.authenticatePassword(password)) {
			dialog.dismiss();
			createUI(savedInstanceState);
		} else {
			ToastUtil.showToast(mActivity, R.string.pdf_reading_password_error);
		}
	}

	@Override
	public int onInitReaderInBackground(final int fRequestCatalogIndex,final int fRequestPageCharIndex, String secretKey) {
		// TODO Auto-generated method stub
		return SUCCESS;
	}

	@Override
	public void onDestroy() {
		if(mCore != null) {
			mCore.onDestroy();
			mCore = null;
		}
		cancelSearch();
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
		return false;
	}

	@Override
	public boolean onActivityDispatchKeyEvent(KeyEvent event) {
		return false;
	}

	@Override
	public ArrayList<Catalog> getChapterList() {
		if(mCore != null) {
			OutlineItem[] items = mCore.getOutline();
			if(items == null || items.length <= 0) {
				return null;
			}
			ArrayList<Catalog> catalogs = new ArrayList<Catalog>();
			for(int i = 0; i < items.length; i++) {
				Catalog catalog = changeOutlineItem(items[i], i);
				if(catalog != null) {
					catalogs.add(catalog);
				}
			}
			return catalogs;
		}
		return null;
	}
	
	private Catalog changeOutlineItem(OutlineItem item, int index) {
		if(item != null) {
			int level = item.level;
			if(level > 8) {
				level = 8;
			} else if (level < 0) {
				level = 0;
			}
			String space = "";
			for(int i = 0; i < level; i++) {
				space += "\t";
			}
			Catalog catalog = new Catalog(null, index);
			catalog.setText(space + item.title);
			catalog.setPageIndex(item.page);
			return catalog;
		}
		return null;
	}
	
	private OutlineItem getOutlineItem(int index) {
		if(mCore != null) {
			OutlineItem[] items = mCore.getOutline();
			if(index >= 0 && index < items.length) {
				return items[index];
			}
		}
		return null;
	}
	
	private OutlineItem[] getOutlineItems() {
		if(mCore != null) {
			return mCore.getOutline();
		}
		return null;
	}

	@Override
	public String getJumpProgressStr(int curPage, int pageNums) {
		return null;
	}

	@Override
	public void gotoChapter(Catalog catalog, boolean isStartAnim) {
		// TODO Auto-generated method stub
		int page = catalog.getPageIndex();
		setDisplayedViewIndex(page);
	}

	@Override
	public void gotoChapter(int requestChapterIndex, boolean isStartAnim) {
		OutlineItem outlineItem = getOutlineItem(requestChapterIndex);
		if(outlineItem != null) {
			setDisplayedViewIndex(outlineItem.page);
		}
	}

	@Override
	public boolean hasPreChapter() {
		return false;
	}

	@Override
	public boolean hasNextChapter() {
		return false;
	}

	@Override
	public void gotoNextPage() {
		setDisplayedViewIndex(mCurrent + 1);
	}

	@Override
	public void gotoPrePage() {
		setDisplayedViewIndex(mCurrent - 1);
	}

	@Override
	public void gotoPreChapter() {
	}

	@Override
	public void gotoNextChapter() {
	}

	@Override
	public void gotoPage(int requestProgress, boolean isStartAnim) {
		setDisplayedViewIndex(requestProgress);
	}

	@Override
	public void gotoChar(int requestChapterIndex, int requestCharIndex,
			boolean isStartAnim) {
	}

	@Override
	public void gotoBookmark(BookMark bookmark, boolean isStartAnim) {
		if(bookmark != null) {
			setDisplayedViewIndex(bookmark.getPosition());
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

	@Override
	public BookMark newSysBookmark() {
		BookMark bookMark = null;
		if(mBook != null) {
			bookMark = new BookMark();
			bookMark.setAuthor(mBook.getAuthor());
			bookMark.setContentID(mBook.getBookId());
			bookMark.setBookmarkName(mBook.getBookName());
			bookMark.setUserID(mPreferencesUtil.getUserId());
			bookMark.setSoftDelete(DBConfig.BOOKMARK_STATUS_SOFT_DELETE_NO);
			bookMark.setBookmarkType(DBConfig.BOOKMARK_TYPE_SYSTEM);
			bookMark.setPosition(mCurrent);
		}
		return bookMark;
	}

	@Override
	public BookMark newUserBookmark() {
		return null;
	}

	@Override
	public int getMaxReadProgress() {
		if(mCore != null) {
			return mCore.countPages() - 1;
		}
		return 0;
	}

	@Override
	public int getCurReadProgress() {
		return mCurrent;
	}

	@Override
	public int getLayoutChapterProgress() {
		return 0;
	}

	@Override
	public int getLayoutChapterMax() {
		return 0;
	}

	@Override
	public void onShowContentView() {
	}

	@Override
	public void onHideContentView() {
	}

	@Override
	public BookInfo getBookInfo() {
		BookInfo bookInfo = new BookInfo();
		if(mBook != null) {
			bookInfo.id = mBook.getBookId();
			bookInfo.author = mBook.getAuthor();
			bookInfo.title = mBook.getBookName();
		}
		return bookInfo;
	}

	@Override
	public Catalog getCurrentCatalog() {
		ArrayList<Catalog> catalogs = getChapterList();
		if(catalogs == null || catalogs.size() <= 0) {
			return null;
		}
		int positon = getCurChapterIndex();
		if(positon >= 0 && positon < catalogs.size()) {
			return catalogs.get(positon);
		}
		return null;
	}

	@Override
	public int getCurChapterIndex() {
		return countPosition(mCurrent);
	}
	
	/**
	 * 根据当前页计算item在list中的位置
	 * @param page
	 * @return
	 */
	private int countPosition(int page) {
		OutlineItem[] items = getOutlineItems();
		if(getOutlineItems() == null || items.length <= 0) {
			return -1;
		}
		int position = -1;
		for(int i = 0; i < items.length; i++) {
			OutlineItem item = items[i];
			if(item.page == page) {
				position = i;
				break;
			} else if (item.page > page) {
				position = i - 1;
				break;
			}
		}
		return position;
	}
	
	@Override
    public int getPageStartIndex(int chapterIndex, int pageIndex) {
		return -1;
	}

	@Override
	public boolean handlerSelectTouchEvent(MotionEvent event,
			ITouchEventDispatcher touchEventDispatcher) {
		return false;
	}

	@Override
	public boolean handlerTouchEvent(MotionEvent event) {
		return onTouchEvent(event);
	}

	@Override
	public boolean dispatchClickEvent(MotionEvent event) {
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
	public void search(int direction, final String keyWord) {
		cancelSearch();
		if(TextUtils.isEmpty(keyWord)) {
			ToastUtil.showToast(mActivity, R.string.pdf_reading_search_empty);
			return;
		}
		
		mSearchTask = new AsyncTask<Integer,Integer,SearchTaskResult>() {

			@Override
			protected SearchTaskResult doInBackground(Integer... params) {
				int index;
				if (SearchTaskResult.get() == null)
					index = getDisplayedViewIndex();
				else
					index = SearchTaskResult.get().pageNumber + params[0].intValue();

				while (0 <= index && index < mCore.countPages() && !isCancelled()) {
//					publishProgress(index);
					RectF searchHits[] = mCore.searchPage(index, keyWord);

					if (searchHits != null && searchHits.length > 0)
						return new SearchTaskResult(keyWord, index, searchHits);

					index += params[0].intValue();
				}
				return null;
			}
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showLoadingDialog();
			}
			
			@Override
			protected void onPostExecute(SearchTaskResult result) {
				hideLoadingDialog();
				if (result != null) {
					// Ask the ReaderView to move to the resulting page
					setDisplayedViewIndex(result.pageNumber);
				    SearchTaskResult.set(result);
					// Make the ReaderView act on the change to mSearchTaskResult
					// via overridden onChildSetup method.
				    resetupChildren();
				} else {
					ToastUtil.showToast(mActivity, R.string.pdf_reading_search_result_none);
				}
			}
			
			@Override
			protected void onCancelled() {
				super.onCancelled();
				hideLoadingDialog();
			}
			
			@Override
			protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
			}
			
		};
		mSearchTask.execute(direction);
	}

    @Override
    public String getChapterId(int chaptersId) {
        return chaptersId+"";
    }

    private void showLoadingDialog() {
		if(mReadCallback != null) {
			mReadCallback.showLoadingDialog(R.string.pdf_reading_search_ing);
		}
	}
	
	private void hideLoadingDialog() {
		if(mReadCallback != null) {
			mReadCallback.hideLoadingDialog();
		}
	}
	
	/**
	 * 取消搜索任务
	 */
	private void cancelSearch() {
		if (mSearchTask != null) {
			mSearchTask.cancel(true);
			mSearchTask = null;
		}
	}

	@Override
	public TextSelectHandler getTextSelectHandler() {
		return null;
	}
}

class SearchTaskResult {
	public final String txt;
	public final int   pageNumber;
	public final RectF searchBoxes[];
	static private SearchTaskResult singleton;

	SearchTaskResult(String _txt, int _pageNumber, RectF _searchBoxes[]) {
		txt = _txt;
		pageNumber = _pageNumber;
		searchBoxes = _searchBoxes;
	}

	static public SearchTaskResult get() {
		return singleton;
	}

	static public void set(SearchTaskResult r) {
		singleton = r;
	}
}
