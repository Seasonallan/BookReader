package com.lectek.android.lereader.widgets;
//package com.lectek.android.sfreader.widgets;
//
//import java.io.File;
//import java.util.ArrayList;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.res.Resources;
//import android.database.Cursor;
//import android.graphics.Color;
//import android.os.AsyncTask;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.CheckBox;
//import android.widget.ImageView;
//import android.widget.ResourceCursorAdapter;
//import android.widget.TextView;
//
//import com.lectek.android.sfreader.R;
//import com.lectek.android.sfreader.dao.DownloadDB;
//import com.lectek.android.sfreader.dao.DownloadProviderMetaData.DownloadTableMetaData;
//import com.lectek.android.sfreader.data.ContentInfo;
//import com.lectek.android.sfreader.data.DownloadUnit;
//import com.lectek.android.sfreader.model.Book;
//import com.lectek.android.sfreader.net.DownloadThread;
//import com.lectek.android.sfreader.net.DownloadThreadManager;
//import com.lectek.android.sfreader.ui.BookShelfView.ListSelecter;
//import com.lectek.android.sfreader.ui.DownloadView;
//import com.lectek.android.sfreader.util.CommonUtil;
//import com.lectek.android.sfreader.util.CommonUtil.ConfirmListener;
//import com.lectek.android.sfreader.util.Constants;
//import com.lectek.android.lereader.lib.image.ImageLoader;
//import com.lectek.android.sfreader.util.ToastUtil;
//import com.lectek.android.util.LogUtil;
//import com.lectek.bookformats.BookMetaInfo;
//import com.lectek.bookformats.FormatPlugin;
//import com.lectek.bookformats.PluginManager;
//
///**
// * @author mingkg21
// * @date 2010-4-17
// * @email mingkg21@gmail.com
// */
//public class DownloadAdapter extends ResourceCursorAdapter implements ListSelecter{
//
//	protected static final String Tag = DownloadAdapter.class.getSimpleName();
//	protected int idColumnId;
//	protected int contentIDColumnId;
//	protected int contentNameColumnId;
//	protected int bookTypeColumnId;
//	protected int authorNameColumnId;
//	protected int totalBytesColumnId;
//	protected int currentBytesColumnId;
//	protected int statusColumnId;
//	protected int dataColumnId;
//	protected int isOrderColumnId;
//	protected int priceColumnId;
//	protected Activity mContext;
//	protected Resources res;
//
//	protected DownloadView downloadView;
//	protected DownloadDB downloadDB;
//	protected ArrayList<Integer> mSelects = new ArrayList<Integer>();
//	protected boolean isSelectEnabled = false;
//	public DownloadAdapter(Activity context, Cursor c, DownloadView downloadView) {
//		super(context, R.layout.book_shelf_download_bookinfo_item, c);
//		init(context, c, downloadView);
//	}
//	public DownloadAdapter(Activity context, Cursor c, DownloadView downloadView ,int layRes) {
//		super(context,layRes, c);
//		init(context, c, downloadView);
//	}
//	protected void init(Activity context, Cursor c, DownloadView downloadView){
//		mContext = context;
//		downloadDB = new DownloadDB(context);
//		idColumnId = c.getColumnIndexOrThrow(DownloadTableMetaData._ID);
//		contentIDColumnId = c
//				.getColumnIndexOrThrow(DownloadTableMetaData.CONTENT_ID);
//		contentNameColumnId = c
//				.getColumnIndexOrThrow(DownloadTableMetaData.TITLE);
//		authorNameColumnId = c
//				.getColumnIndexOrThrow(DownloadTableMetaData.AUTHOR);
//		totalBytesColumnId = c
//				.getColumnIndexOrThrow(DownloadTableMetaData.TOTAL_BYTES);
//		currentBytesColumnId = c
//				.getColumnIndexOrThrow(DownloadTableMetaData.CURRENT_BYTES);
//		statusColumnId = c.getColumnIndexOrThrow(DownloadTableMetaData.STATUS);
//		dataColumnId = c.getColumnIndexOrThrow(DownloadTableMetaData._DATA);
//		bookTypeColumnId = c
//				.getColumnIndexOrThrow(DownloadTableMetaData.BOOK_TYPE);
//		isOrderColumnId = c
//				.getColumnIndexOrThrow(DownloadTableMetaData.IS_ORDER);
//		priceColumnId = c.getColumnIndexOrThrow(DownloadTableMetaData.PRICE);
//
//		res = context.getResources();
//
//		this.downloadView = downloadView;
//	}
//	@Override
//	public void bindView(View view, final Context context, final Cursor cursor) {
//
////		TextView bookNameTV = (TextView) view.findViewById(R.id.book_name);
////		TextView authorNameTV = (TextView) view.findViewById(R.id.author_name);
////		View downloadContainer = view.findViewById(R.id.download_container);
////		ImageView logoIV = (ImageView) view.findViewById(R.id.logo);
////		final ImageView downloadControlIV = (ImageView) view.findViewById(R.id.download_status);
////		TextView tipView = (TextView) view.findViewById(R.id.book_download_tip);
//
//		ItemInfo itemInfo = new ItemInfo(cursor);
//		view.setTag(itemInfo);
//		setLogo(view, itemInfo);
//		setContentInfo( view, itemInfo);
//		setDowloadBut(view, itemInfo,cursor);
//		setProgress(view, itemInfo);
//		
//	}
//	protected void setLogo(View view,ItemInfo itemInfo){
//		ImageView logoIV = (ImageView) view.findViewById(R.id.logo);
//		if(logoIV == null)return;
//		ImageLoader imageLoader = new ImageLoader(mContext);
//		imageLoader.setImageViewBitmap(null, itemInfo.contentID,
//				logoIV, view, R.drawable.book_default);
//	}
//	protected void setContentInfo(View view,ItemInfo itemInfo){
//		TextView bookNameTV = (TextView) view.findViewById(R.id.book_name);
//		TextView authorNameTV = (TextView) view.findViewById(R.id.author_name);
//		if(bookNameTV == null){
//			return;
//		}
//		
//		bookNameTV.setText(itemInfo.contentName);
//		
//		if(authorNameTV == null){
//			return; 
//		}
//		if (!TextUtils.isEmpty(itemInfo.bookType)
//				&& (itemInfo.bookType.equals(ContentInfo.CONTENT_TYPE_MAGAZINE) || itemInfo.bookType
//						.equals(ContentInfo.CONTENT_TYPE_MAGAZINE_STREAM))) {
//			authorNameTV.setVisibility(View.GONE);
//		} else {
//			if (TextUtils.isEmpty(itemInfo.authorName)) {
//				authorNameTV.setText(mContext.getString(R.string.book_item_author, "无"));
//			} else {
//				authorNameTV.setText(mContext.getString(R.string.book_item_author, itemInfo.authorName));
//			}
//			authorNameTV.setVisibility(View.VISIBLE);
//		}
//
//	}
//	
//	protected void setDowloadBut(View view,ItemInfo itemInfo,Cursor cursor){
//		final ImageView downloadControlIV = (ImageView) view.findViewById(R.id.download_status);
//		if(downloadControlIV == null)return;
//		if (DownloadTableMetaData.isStatusCompleted(itemInfo.status)) {// 已下载完成
//			downloadControlIV.setClickable(false);
////			downloadControlIV.setOnClickListener(null);
//			downloadControlIV.setVisibility(View.INVISIBLE);
//		} else {// 未下载完�?
//			downloadControlIV.setVisibility(View.VISIBLE);
//			DownloadThreadManager dtm = DownloadThreadManager.getInstance();
//			LogUtil.v(Tag, "download thread num " + dtm.getThreadNumber());
//			if (!itemInfo.isRunning) {
//				downloadControlIV.setOnLongClickListener(null);
//				downloadControlIV.setImageResource(R.drawable.download_start);
//			} else {
//				downloadControlIV.setImageResource(R.drawable.download_pause);
//			}
//
//			// 下载失败的提�?
//			if (itemInfo.isError) {
//				downloadControlIV.setImageResource(R.drawable.download_start);
//			}
//			if(isSelectEnabled){
//				downloadControlIV.setClickable(false);
//			}else{
//				downloadControlIV.setClickable(true);
//				downloadControlIV.setOnClickListener(new DownloadButClickListener(itemInfo, cursor));
//			}
//		}
//	}
//	protected void setProgress(View view,ItemInfo itemInfo){
//		TextView tipView = (TextView) view.findViewById(R.id.book_download_tip);
//		if(tipView == null)return;
//		if (DownloadTableMetaData.isStatusCompleted(itemInfo.status)) {// 已下载完成
//			tipView.setVisibility(View.VISIBLE);
//			if (itemInfo.isOrder.equals(Book.unOrder)) {
//				tipView.setTextColor(mContext.getResources().getColor(R.color.search_normal));
//				tipView.setText(R.string.download_status_downloaded_try);
//			} else {
//				tipView.setTextColor(mContext.getResources().getColor(R.color.book_item_author));
//				tipView.setText(R.string.download_status_downloaded);
//			}
//		} else {// 未下载完�?
//			long totalBytes = itemInfo.totalBytes;
//			long currentBytes = 0;
//			int progressAmount = 0;
//			if (totalBytes > 0) {
//				currentBytes = itemInfo.currentBytes;
//				progressAmount = (int) (currentBytes * 100 / totalBytes);
//
//				StringBuilder sb = new StringBuilder();
//				sb.append(progressAmount);
//				sb.append("%");
//				tipView.setText(mContext.getString(R.string.download_status_downloading, sb));
//				tipView.setTextColor(mContext.getResources().getColor(R.color.search_normal));
//			}
//
//			DownloadThreadManager dtm = DownloadThreadManager.getInstance();
//			LogUtil.v(Tag, "download thread num " + dtm.getThreadNumber());
//			if (!itemInfo.isRunning) {
//				tipView.setText(R.string.download_status_pause);
//				tipView.setTextColor(Color.parseColor("#ee290a"));
//			} else {
//				if (totalBytes == 0) {
//					tipView.setText(R.string.download_status_ready);
//					tipView.setTextColor(mContext.getResources().getColor(R.color.search_normal));
//				}
//			}
//
//			// 下载失败的提�?
//			if (itemInfo.isError) {
//				tipView.setText(R.string.download_status_fault);
//				tipView.setTextColor(Color.parseColor("#ee290a"));
//			}
//		}
//	}
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		View view = super.getView(position, convertView, parent);
//		final int curPosition = position;
//		if(isSelectEnabled){
//			view.setClickable(true);
//			setSelectItemVisibility(view,View.VISIBLE);
//			if(canDelete(curPosition)){
//				setSelectItemEnabled(view,true);
//				if(mSelects.contains(curPosition)){
//					setSelectItemCheck(view,true);
//				}else{
//					setSelectItemCheck(view,false);
//				}
//				view.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						if(mSelects.contains(curPosition)){
//							mSelects.remove(new Integer(curPosition));
//							setSelectItemCheck(v,false);
//						}else{
//							mSelects.add(curPosition);
//							setSelectItemCheck(v,true);
//						}
//					}
//				});
//			}else{
//				if(mSelects.contains(curPosition)){
//					mSelects.remove(new Integer(curPosition));
//				}
//				setSelectItemEnabled(view,false);
//				view.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						ToastUtil.showToast(mContext,R.string.downlaod_tip_not_delete);
//					}
//				});
//			}
//		}else{
//			ItemInfo itemInfo= (ItemInfo) view.getTag();
//			view.setOnClickListener(null);
//			setSelectItemVisibility(view,View.INVISIBLE);
//			if (DownloadTableMetaData.isStatusCompleted(itemInfo.status)) {
//				view.setClickable(false);
//			}else{
//				view.setClickable(true);
//			}
//			
//		}
//		return view;
//	}
//	protected void setSelectItemCheck(View view,boolean isCheck){
//		CheckBox checkBox = (CheckBox) view.findViewById(R.id.book_download_select_cb);
//		if(checkBox == null)return;
//		checkBox.setChecked(isCheck);
//	}
//	protected void setSelectItemEnabled(View view,boolean isEnabled){
//		CheckBox checkBox = (CheckBox) view.findViewById(R.id.book_download_select_cb);
//		if(checkBox == null)return;
//		checkBox.setEnabled(isEnabled);
//	}
//	protected void setSelectItemVisibility(View view,int visibility){
//		CheckBox checkBox = (CheckBox) view.findViewById(R.id.book_download_select_cb);
//		if(checkBox == null)return;
//		checkBox.setVisibility(visibility);
//	}
//	/**
//	 * 删除书籍和下载数据库的数�?
//	 * 
//	 * @param context
//	 * @param contentID
//	 * @param contentName
//	 * @param bookPath
//	 */
//	private void deleteBook(final Context context, final String contentID,
//			final String contentName, final String bookPath) {
//		final ConfirmListener delCurListener = new ConfirmListener() {
//
//			@Override
//			public void onClick(final View v) {
//				final Dialog dialog = CommonUtil.getWaittingDialog(mContext,
//						R.string.waitting_dialog_delete_book);
//				dialog.show();
//				new AsyncTask<String, Integer, Long>() {
//
//					@Override
//					protected void onPostExecute(Long result) {
//						downloadView.flash();
//						dialog.dismiss();
//						CommonUtil.showToast(mContext, R.string.delete_success);
//					}
//
//					@Override
//					protected Long doInBackground(String... params) {
//						deleteFile(bookPath, contentID);
//						// BooksDao.instance().deleteBookByContentId(contentID);
//						// 删除下载数据库的数据
//						context.getContentResolver().delete(
//								DownloadTableMetaData.CONTENT_URI,
//								DownloadTableMetaData.CONTENT_ID + " = '"
//										+ contentID + "'", null);
//						return null;
//					}
//				}.execute();
//			}
//		};
//		CommonUtil.commonConfirmDialog(mContext, mContext.getString(
//				R.string.download_complete_delete_tip, contentName),
//				delCurListener);
//	}
//
//	public void readBook(int position) {
//		Cursor cursor = (Cursor) getItem(position);
//		if (cursor != null) {
//			Book mBook = null;
//			long bookId = cursor.getLong(cursor
//					.getColumnIndex(DownloadTableMetaData._ID));
//			String contentId = cursor.getString(cursor
//					.getColumnIndex(DownloadTableMetaData.CONTENT_ID));
//			String bookName = cursor.getString(cursor
//					.getColumnIndex(DownloadTableMetaData.TITLE));
//			String bookPath = cursor.getString(cursor
//					.getColumnIndex(DownloadTableMetaData._DATA));
//			String bookCover = DownloadConstants.BOOKS_TEMP_IMAGE + contentId + ".png";
//			String author = cursor.getString(cursor
//					.getColumnIndex(DownloadTableMetaData.AUTHOR));
//			long fileSize = cursor.getLong(cursor
//					.getColumnIndex(DownloadTableMetaData.TOTAL_BYTES));
//			int status = cursor.getInt(cursor
//					.getColumnIndex(DownloadTableMetaData.STATUS));
//			String isOrdered = cursor.getString(cursor
//					.getColumnIndex(DownloadTableMetaData.IS_ORDER));
//			String price = cursor.getString(cursor
//					.getColumnIndex(DownloadTableMetaData.PRICE));
//			String contentType = cursor.getString(cursor
//					.getColumnIndex(DownloadTableMetaData.BOOK_TYPE));
//			if (!DownloadTableMetaData.isStatusCompleted(status)
//					|| fileSize == 0) {// 未下载或还没下载完成
//				return;
//			}
//			boolean isError = false;
//			if (contentType.equals(ContentInfo.CONTENT_TYPE_MAGAZINE)) {// 如果是杂�?
//				try {
//					FormatPlugin fb = PluginManager.instance().getPlugin(bookPath);
//					BookMetaInfo bookMetaInfo = fb.getBookMetaInfo();
//					if (bookMetaInfo != null && bookMetaInfo.type != null && !TextUtils.isEmpty(bookMetaInfo.type)) {
//						if (bookMetaInfo.type.equals(BookMetaInfo.BOOK_META_INFO_STREAM)) {
//							contentType = ContentInfo.CONTENT_TYPE_MAGAZINE_STREAM;
//						}
//					}
//					fb.recyle();
//				} catch (Exception e) {
//					LogUtil.e("DownloadDB", "getBookData", e);
//					isError = true;
//				}
//			}
//			mBook = new Book(bookId, contentId, bookName, bookPath, null,
//					bookCover, author, fileSize, Book.FROM_TYPE_SELF, isOrdered,
//					price, contentType, DownloadTableMetaData
//							.isStatusCompleted(status));
//			if (isError) {
//				reDownload(mBook);
//			} else {
//				if (TextUtils.isEmpty(bookPath)) {// 其他异常
//					reDownload(mBook);
//					return;
//				}
//				File file = new File(bookPath);
//				if (file.exists() && mBook.isComplete) {// 如果本地�?
//					downloadDB.downLoadReader(mBook);
//				} else {//
//					reDownload(mBook);
//				}
//			}
//		}
//	}
//
//	private void reDownload(final Book mBook) {
//		final ConfirmListener delCurListener = new ConfirmListener() {
//
//			@Override
//			public void onClick(final View v) {
//				DownloadUnit downloadInfo = new DownloadUnit();
//				downloadInfo.contentID = mBook.contentID;
//				downloadInfo.contentName = mBook.name;
//				downloadInfo.authorName = mBook.author;
//				downloadInfo.contentType = mBook.type;
//				new DownloadThread(mContext, null, downloadInfo);
//			}
//		};
//		String title = mContext.getString(R.string.download_reload_tip,
//				mBook.name);
//		CommonUtil.commonConfirmDialog(mContext, title, delCurListener);
//	}
//
//	public void deleteBook(int position) {
//		Cursor cursor = (Cursor) getItem(position);
//		if (cursor != null) {
//			final String contentID = cursor.getString(contentIDColumnId);
//			final String contentName = cursor.getString(contentNameColumnId);
//			final String bookPath = cursor.getString(dataColumnId);
//			if(canDelete(position)){
//				deleteBook(mContext, contentID, contentName, bookPath);
//			}
//		}
//	}
//	public int deleteBooks(ArrayList<Integer> curSelect){
//		if(curSelect == null || curSelect.size() == 0){
//			return 0;
//		}
//		int successSize = 0;
//		String ids = "";
//		for(int i = 0 ;i < curSelect.size(); i ++){
//			Cursor cursor = (Cursor) getItem(curSelect.get(i));
//			if (cursor != null) {
//				final String contentID = cursor.getString(contentIDColumnId);
//				final String bookPath = cursor.getString(dataColumnId);
//				deleteFile(bookPath, contentID);
//				// BooksDao.instance().deleteBookByContentId(contentID);
//				// 删除下载数据库的数据
//				ids += contentID;
//				if(i != curSelect.size() - 1 && curSelect.size() != 1){
//					ids += ",";
//				}
//			}
//		}
//		successSize = mContext.getContentResolver().delete(
//				DownloadTableMetaData.CONTENT_URI,
//				DownloadTableMetaData.CONTENT_ID + " IN ("
//						+ ids + ")", null);
//		return successSize;
//	}
//	private boolean canDelete(int position){
//		Cursor cursor = (Cursor) getItem(position);
//		int status = cursor.getInt(statusColumnId);
//		if (DownloadTableMetaData.isStatusCompleted(status)) {// 下载完成
//			return true;
//		} else {
//			if (DownloadTableMetaData.isStatusSuspended(status)) {// 暂停下载
//				return true;
//			}
//		}
//		return false;
//	}
//	/**
//	 * 删除SDCARD中的文件
//	 * 
//	 * @param bookPath
//	 * @param contentID
//	 */
//	private void deleteFile(String bookPath, String contentID) {
//		if (TextUtils.isEmpty(bookPath)) {
//			bookPath = DownloadConstants.BOOKS_DOWNLOAD + contentID + ".ceb";
//		}
//		if (TextUtils.isEmpty(bookPath)) {
//			return;
//		}
//		File file = new File(bookPath);
//		if (file != null && file.exists()) {
//			file.delete();
//		}
//		String imagePath = DownloadConstants.BOOKS_DOWNLOAD + contentID + ".png";
//		file = null;
//		file = new File(imagePath);
//		if (file != null && file.exists()) {
//			file.delete();
//		}
//	}
//
//	@Override
//	public void setSelectEnabled(boolean isEnabled) {
//		if(isSelectEnabled != isEnabled){
//			isSelectEnabled = isEnabled;
//			mSelects.clear();
//			notifyDataSetChanged();
//		}
//	}
//
//	@Override
//	public ArrayList<Integer> getSelect() {
//		ArrayList<Integer> selects = new ArrayList<Integer>();
//		selects.addAll(mSelects);
//		return selects;
//	}
//
//	@Override
//	public void selectAll() {
//		mSelects.clear();
//		for(int i = 0 ;i < getCount();i++){
//			mSelects.add(i);
//		}
//		notifyDataSetChanged();
//	}
//	private class DownloadButClickListener implements OnClickListener{
//		private ItemInfo mItemInfo;
//		private Cursor mCursor;
//		private DownloadButClickListener( ItemInfo itemInfo,Cursor cursor){
//			mItemInfo = itemInfo;
//			mCursor = cursor;
//		}
//		@Override
//		public void onClick(View v) {
//			if (mItemInfo.isRunning) {
//				DownloadThreadManager dtm = DownloadThreadManager.getInstance();
//				DownloadUnit info = dtm.getThread(mItemInfo.contentID);
//				if (info != null) {
//					info.mHasActiveThread = false;
//				}
//			} else {
//				DownloadUnit downloadInfo = new DownloadUnit();
//				downloadInfo.contentID = mItemInfo.contentID;
//				downloadInfo.contentName = mItemInfo.contentName;
//				downloadInfo.authorName = mItemInfo.authorName;
//				downloadInfo.contentType = mItemInfo.bookType;
//				downloadInfo.url = mCursor.getString(mCursor.getColumnIndexOrThrow(DownloadTableMetaData.URI));
//				downloadInfo.ticketURL = mCursor.getString(mCursor.getColumnIndexOrThrow(DownloadTableMetaData.TICKET_URL));
//				downloadInfo.price = mCursor.getString(mCursor.getColumnIndexOrThrow(DownloadTableMetaData.PRICE));
//				if (mItemInfo.isOrder.equals(Book.unOrder)) {
//					downloadInfo.isOrdered = true;
//				} else {
//					downloadInfo.isOrdered = false;
//				}
//				new DownloadThread(mContext, null, downloadInfo);
//			}
//		}
//	}
//	protected class ItemInfo{
//		protected int id ;
//		protected String contentID;
//		protected String contentName;
//		protected String authorName ;
//		protected String bookPath;
//		protected String bookType ;
//		protected long fileSize ;
//		protected String isOrder ;
//		protected String price ;
//		protected int status;
//		protected boolean isRunning;
//		protected boolean isError;
//		protected long totalBytes; 
//		protected long currentBytes;
//		protected ItemInfo(Cursor cursor){
//			id = cursor.getInt(idColumnId);
//			contentID = cursor.getString(contentIDColumnId);
//			contentName = cursor.getString(contentNameColumnId);
//			authorName = cursor.getString(authorNameColumnId);
//			bookPath = cursor.getString(dataColumnId);
//			bookType = cursor.getString(bookTypeColumnId);
//			fileSize = cursor.getLong(totalBytesColumnId);
//			isOrder = cursor.getString(isOrderColumnId);
//			price = cursor.getString(priceColumnId);
//			status = cursor.getInt(statusColumnId);
//			isRunning = DownloadTableMetaData.isStatusRunning(status);
//			isError = DownloadTableMetaData.isStatusError(status);
//			totalBytes = cursor.getLong(totalBytesColumnId);
//			currentBytes = cursor.getLong(currentBytesColumnId);
//		}
//	}
//}
