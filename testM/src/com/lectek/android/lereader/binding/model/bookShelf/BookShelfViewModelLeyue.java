package com.lectek.android.lereader.binding.model.bookShelf;

import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.binding.command.OnItemClickCommand;
import com.lectek.android.binding.command.OnItemLongClickedCommand;
import com.lectek.android.lereader.animation.OpenBookAnimManagement;
import com.lectek.android.lereader.binding.model.BaseLoadDataViewModel;
import com.lectek.android.lereader.download.DownloadHttpEngine;
import com.lectek.android.lereader.lib.utils.FileUtil;
import com.lectek.android.lereader.net.response.DownloadInfo;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.permanent.DownloadAPI;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue.DeleteDownloadsUIRunnadle;
import com.lectek.android.lereader.storage.dbase.mark.BookMarkDB;
import com.lectek.android.lereader.ui.ILoadView;
import com.lectek.android.lereader.ui.basereader_leyue.BaseReaderActivityLeyue;
import com.lectek.android.lereader.ui.basereader_leyue.Book;
import com.lectek.android.lereader.ui.specific.MainActivity;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.DialogUtil.ConfirmListener;
import com.lectek.android.lereader.utils.ToastUtil;

public class BookShelfViewModelLeyue extends BaseLoadDataViewModel {

    private Activity mActivity;
	private boolean mIsSelectAll;
	
	private BookShelfModelLeyue mBookShelfModel;
	
	private TitleViewHandler mTitleViewHandler;
	
	private static final int MSG_WHAT_ON_PROGRESS_CHANGE = 0 ;
	private static final int MSG_WHAT_ON_STATE_CHANGE = 1 ;
	
	private static final int CLEAR_TIP = 0 ;
	private static final int DELETE_TIP = 1 ;
	
	private boolean isEditMode;
	private DonwloadListener mDonwloadListener;
	public final ArrayListObservable<ItemViewModel> bItems = new ArrayListObservable<ItemViewModel>(ItemViewModel.class);
	public final IntegerObservable bNoDateVisibility = new IntegerObservable(View.VISIBLE);
	public final IntegerObservable bLastestReadBookVisibility = new IntegerObservable(View.GONE);
	public final IntegerObservable bContentVisibility = new IntegerObservable(View.GONE);
	private DownloadInfo mDownloadInfo;
	
	public final OnItemClickCommand bOnItemClickCommand = new OnItemClickCommand() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {



            final ItemViewModel itemViewModel = bItems.get(position);
            if(isEditMode){
                return;
            }

            ImageView bookCover = (ImageView)view.findViewById(R.id.book_img_iv);

            OpenBookAnimManagement.getInstance().setOpenBookAnimVIew(bookCover);
            if (itemViewModel == null) {
                return;
            }
            mDownloadInfo = newDownloadInfo(itemViewModel); //mDataSource.get(position);
            //TODO:暂时处理，到时候详情界面，广播通知修改。
            DownloadInfo dbInfo = DownloadPresenterLeyue.getDownloadUnitById(mDownloadInfo.contentID);
            if (dbInfo!=null) {
                mDownloadInfo.isOrderChapterNum = dbInfo.isOrderChapterNum;
                mDownloadInfo.isOrder = dbInfo.isOrder;
                mDownloadInfo.price = dbInfo.price;
                mDownloadInfo.promotionPrice = dbInfo.promotionPrice;
                mDownloadInfo.contentType = dbInfo.contentType;
                mDownloadInfo.isDownloadFullVersonBook = dbInfo.isDownloadFullVersonBook;
            }

            if(itemViewModel.state != DownloadAPI.STATE_ONLINE && itemViewModel.downloadBook()){
                return;
            }

            if(!TextUtils.isEmpty(dbInfo.filePathLocation)){
                File file = new File(dbInfo.filePathLocation);
                if (!file.exists()) {
                    gotoReadBook(true);
                    return;
                }
                gotoReadBook(false);
            }else{//不大可能发生
                gotoReadBook(true);
                return;
            }

        }
    };

    private void gotoReadBook(boolean isOnline){
        if(!isOnline){
            if(!FileUtil.isSDcardExist()){
                ToastUtil.showToast(getContext(), R.string.sdcard_no_exist_download_tip);
                return;
            }
        }else{
            if(!DialogUtil.isDeviceNetword((Activity) getContext())){
                return;
            }
        }
        if (mDownloadInfo == null) {
            return;
        }
        Book book = new Book();
        book.setPath(mDownloadInfo.filePathLocation);
        book.setBookId(mDownloadInfo.contentID);
        book.setFeeStart(mDownloadInfo.isOrderChapterNum);
        //书籍阅读界面，将根据isOrder字段来判断需要排版的章节数，所以免费书籍也要在进入阅读前，设置为isOrder。
        if (TextUtils.isEmpty(mDownloadInfo.price) && TextUtils.isEmpty(mDownloadInfo.promotionPrice)) {
            book.setOrder(true);
        }else {
            book.setOrder(mDownloadInfo.isOrder);
        }
        book.setPrice(mDownloadInfo.price);
        book.setPromotionPrice(mDownloadInfo.promotionPrice);
        book.setBookType(mDownloadInfo.contentType);
        book.setBookName(mDownloadInfo.contentName);
        book.setAuthor(mDownloadInfo.authorName);
        book.setCoverPath(mDownloadInfo.logoUrl);
        book.setBookFormatType(mDownloadInfo.downloadType);
        book.setDownloadFullVersonBook(mDownloadInfo.isDownloadFullVersonBook);
        book.setOnline(isOnline);
        if(!isOnline){
            if(book.getBookFormatType().equals(DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_BOOK)
                    && verifyLeyueEpubBook(mDownloadInfo.contentID)){
                BaseReaderActivityLeyue.openActivity(getContext(), book,true);
            }
        }
        BaseReaderActivityLeyue.openActivity(getContext(), book,true);
    }

    /**
     * 判断是否为乐阅EPUB书籍，是的话可以通过ID去取Secretkey
     * @param contentID
     * @return
     */
    private boolean verifyLeyueEpubBook(String contentID){
        if (!TextUtils.isEmpty(contentID)){
            try{
                Long.parseLong(contentID);
                return true;
            }catch (Exception e){}
        }
        return false;
    }

	public final OnItemLongClickedCommand bOnItemLongClickedCommand = new OnItemLongClickedCommand() {
		
		@Override
		public void onItemLongClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			if(mTitleViewHandler != null){
				mTitleViewHandler.setTitleView(new ActionbarHandler(){

					@Override
					public void cancelEditMode() {
						isEditMode = false;
						mTitleViewHandler.resetTitleView();
						setItemCheckBoxVisibility(View.GONE);
					}

					@Override
					public void selectAllItem() {
						if(!mIsSelectAll){
							mIsSelectAll = true;
							setItemSelected(true);
						}else{
							mIsSelectAll = false;
							setItemSelected(false);
						}
					}

					@Override
					public void remove() {
						if(removeItems()){
							isEditMode = false;
							mTitleViewHandler.resetTitleView();
							setItemCheckBoxVisibility(View.GONE);
						}
						
					}
					
				});
			}
			
			isEditMode = true;
			
			setItemSelected(false);
			bItems.get(position).bItemCheck.set(true);
			setItemCheckBoxVisibility(View.VISIBLE);
		}
	};
	
//	private boolean isNativeBook(String contentId, PerformClick performClick){
//		boolean isNativeBook = false;
//		String ids = PreferencesUtil.getInstance(getContext()).getNativeBookIdsRecord();
//		String contentIds[] = null;
//		if(!TextUtils.isEmpty(ids)){
//			contentIds = ids.split(",");
//		}
//		for(int i=0; contentIds != null && i < contentIds.length; i++){
//			if(contentId.equals(contentIds[i])){
//				isNativeBook = true;
//				break;
//			}
//		}
//		if(isNativeBook){
//			writeNativeBook(contentId,performClick);
//			return true;
//		}
//		
//		return false;
//	}
	
//	private void writeNativeBook(final String contentId, final PerformClick performClick){
//		showLoadView();
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				int[] built_in_books = CommonUtil.getIntArray((Activity)getContext(), R.array.built_in_books);
//				if (built_in_books!=null && built_in_books.length>0) {
//					//1984
//					if(contentId.equals("1000000126")){
//						WelcomeActivity.copyFileToSDCard(getContext(),built_in_books[0],DownloadConstants.BOOKS_DOWNLOAD + "1000000126.epub");
//					}else if(contentId.equals("1000000068")){
//						//催眠师手记
//						WelcomeActivity.copyFileToSDCard(getContext(),built_in_books[1],DownloadConstants.BOOKS_DOWNLOAD + "1000000068.epub");
//					}
//					
//					((Activity)getContext()).runOnUiThread(new Runnable() {
//						
//						@Override
//						public void run() {
//							performClick.click();
//						}
//					});
//				}
//			}
//		}).start();
//	}
	
	private interface PerformClick{
		void click();
	}
	
	private void setItemCheckBoxVisibility(int visibility){
		if(bItems == null)
			return;
		
		for(ItemViewModel item : bItems){
			item.bItemCheckBoxVisible.set(visibility);
		}
	}
	
	private void setItemSelected(boolean isChecked){
		if(bItems == null)
			return;
		
		for(ItemViewModel item : bItems){
			item.bItemCheck.set(isChecked);
		}
	}
	
	private boolean removeItems(){
		if(bItems == null)
			return false;
		
		return deleteBookShelfInfo(DELETE_TIP);
		
//		mBookShelfModel.removeBookmark(getContext(), contentIds.split(","));
	}
	
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			if(what == MSG_WHAT_ON_PROGRESS_CHANGE){//下载进度改变
				Intent intent = (Intent) msg.obj;
				long id = intent.getLongExtra(DownloadAPI.BROAD_CAST_DATA_KEY_ID, -1);
				long currentSize  = intent.getLongExtra(DownloadAPI.BROAD_CAST_DATA_KEY_FILE_BYTE_CURRENT_SIZE, -1);
				long size = intent.getLongExtra(DownloadAPI.BROAD_CAST_DATA_KEY_FILE_BYTE_SIZE, -1);
				
				System.out.println("id:"+id+" currentSize:"+currentSize+" size:"+size);
				
				for(int i = 0; i < bItems.size(); i++){
					ItemViewModel itemViewModel = bItems.get(i);
					if(itemViewModel != null && itemViewModel.id == id){
						itemViewModel.currentSize = currentSize;
						itemViewModel.size = size;
						setDownloadProgress(itemViewModel, currentSize, size);
						break;
					}
				}
			}else if(what == MSG_WHAT_ON_STATE_CHANGE){//下载状态改变
				Intent intent = (Intent) msg.obj;
				long id = intent.getLongExtra(DownloadAPI.BROAD_CAST_DATA_KEY_ID, -1);
				int state = intent.getIntExtra(DownloadAPI.BROAD_CAST_DATA_KEY_STATE, -1);
				System.out.println("id:"+id+" state:"+state);
				boolean isNewDate = true;
				for(int i = 0; i < bItems.size(); i++){
					ItemViewModel itemViewModel = bItems.get(i);
					if(itemViewModel != null && itemViewModel.id == id){
						itemViewModel.state = state;
						setDownloadBtnState(itemViewModel, state);
						isNewDate = false;
						break;
					}
				}
				if(isNewDate){
					onStart();
				}
			}
		}
	};
	
	private void setDownloadProgress(ItemViewModel itemViewModel, long currentSize, long size){
		itemViewModel.bItemDownloadStateBtnVisible.set(View.VISIBLE);
		if (itemViewModel.state == DownloadAPI.STATE_START
				|| itemViewModel.state == DownloadAPI.STATE_STARTING) {
			itemViewModel.bDownloadStateIV.set(R.drawable.download_pause);
		} else {
			itemViewModel.bDownloadStateIV.set(R.drawable.download_start);
		}
		long totalBytes = size;
		long currentBytes = 0;
		int progressAmount = 0;
		currentBytes = currentSize;
		if (totalBytes == 0) {
			progressAmount = 0;
		} else {
			progressAmount = (int) (currentBytes * 100 / totalBytes);
		}
		
		itemViewModel.bDownloadStateValue.set("已下载 "+progressAmount+" %");
	}
	
	private void setDownloadBtnState(ItemViewModel itemViewModel, int state){
		if (state == DownloadAPI.STATE_FINISH) {// 已下载完成
			// downloadControlIV.setOnClickListener(null);
			System.out.println("STATE_FINISH");
			itemViewModel.bItemDownloadStateBtnVisible.set(View.GONE);
			//用户在书架，下载完成，直接打开。
			if (getContext() instanceof MainActivity && ((MainActivity) getContext()).isBookShelf) {
                mDownloadInfo = newDownloadInfo(itemViewModel); //mDataSource.get(position);
				gotoReadBook(false);
			}
		} else {
			itemViewModel.bItemDownloadStateBtnVisible.set(View.VISIBLE);
			if (state == DownloadAPI.STATE_START
					|| state == DownloadAPI.STATE_STARTING) {
				itemViewModel.bDownloadStateIV.set(R.drawable.download_pause);
			} else {
				itemViewModel.bDownloadStateIV.set(R.drawable.download_start);
			}
			if (state == DownloadAPI.STATE_FAIL
					|| state == DownloadAPI.STATE_FAIL_OUT_MEMORY) {
				itemViewModel.bDownloadStateIV.set(R.drawable.download_start);
			}
//			downloadControlIV.setOnClickListener(new DownloadButClickListener(
//					position));
//			downloadControlIV.setOnLongClickListener(null);
		}
	}

	public BookShelfViewModelLeyue(Context context, ILoadView loadView) {
		super(context, loadView);
		mBookShelfModel = new BookShelfModelLeyue();
		mBookShelfModel.addCallBack(this);
	}
	
	public void setTitleViewHandler(TitleViewHandler titleViewHandler){
		mTitleViewHandler = titleViewHandler;
	}
	
	/**
	 * 注册广播接收者
	 */
	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(DownloadAPI.ACTION_ON_DOWNLOAD_PROGRESS_CHANGE);
		filter.addAction(DownloadAPI.ACTION_ON_DOWNLOAD_STATE_CHANGE);
		filter.addAction(AppBroadcast.ACTION_UPDATE_BOOKSHELF);
		if (mDonwloadListener == null) {
			mDonwloadListener = new DonwloadListener();
		}
		getContext().registerReceiver(mDonwloadListener, filter);
	}
	
	/**
	 * 注销广播接收者
	 */
	private void unRegisterReceiver() {
		if(mDonwloadListener != null) {
			getContext().unregisterReceiver(mDonwloadListener);
			mDonwloadListener = null;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		registerReceiver();
		mBookShelfModel.start();
	}
	
	@Override
	public void onRelease() {
		super.onRelease();
		unRegisterReceiver();
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if(result != null && !isCancel){
			if (mBookShelfModel.getTag().equals(tag)) {
				ArrayList<DownloadInfo> downloadInfos = (ArrayList<DownloadInfo>)result;
				if(downloadInfos != null){
					bLastestReadBookVisibility.set(View.VISIBLE);
				}
                ArrayListObservable<ItemViewModel> mTempItems = new ArrayListObservable<ItemViewModel>(ItemViewModel.class);
                for(DownloadInfo downloadInfo : downloadInfos){
                    ItemViewModel itemViewModel = newItemViewModel(downloadInfo);
                    mTempItems.add(itemViewModel);
                }
                bItems.setAll(mTempItems);
				checkIsItemsEmpty();
			}
		}
//		hideLoadView();
		return false;
	}
	
	/**
	 * 如果当前书架没有书籍显示无书架提示
	 */
	private void checkIsItemsEmpty(){
		if(bItems != null && bItems.size() > 0){
			bNoDateVisibility.set(View.GONE);
			bContentVisibility.set(View.VISIBLE);
		}else{
			bNoDateVisibility.set(View.VISIBLE);
			bContentVisibility.set(View.GONE);
		}
	}
	
	
	protected boolean deleteBookShelfInfo(final int operationTip) {
		if(bItems == null || bItems.size() <= 0){
			return false;
		}
		final ArrayList<DownloadInfo> datas = new ArrayList<DownloadInfo>();
		
		for(int i = 0; i < bItems.size(); i++){
			ItemViewModel itemViewModel = bItems.get(i);
			if(itemViewModel.bItemCheck.get()){
				datas.add(newDownloadInfo(itemViewModel));
			}
		}
		
		String contentString = "";
		if(datas.size() ==1){
			contentString = getContext().getString(R.string.book_delete_select_single_tip, datas.get(0).contentName);
		}else if(datas.size() > 1){
			contentString = getContext().getString(R.string.book_delete_select_multiple_tip, datas.size());
		}else{
			ToastUtil.showToast(getContext(), R.string.book_no_selected_item);
			return false;
		}
		
		int lResId = -1, rResId = -1;
		if(operationTip == CLEAR_TIP){
//			contentString = getContext().getString(R.string.book_clear_all_tip);
			lResId = R.string.btn_text_clear;
		}else if(operationTip == DELETE_TIP){
//			contentString = getContext().getString(R.string.book_delete_select_tip);
			lResId = R.string.btn_text_delete;
		}
		DialogUtil.commonConfirmDialog((Activity)getContext(), null, contentString, lResId, rResId, new ConfirmListener() {
			@Override
			public void onClick(View v) {
				
				DownloadPresenterLeyue.runDeleteInUI(datas, new DeleteDownloadsUIRunnadle() {
					@Override
					public boolean onPreRun() {
//						showWaittingDialog(operationTip);
						showLoadView();
						return true;
					}
					
					@Override
					public void onPostRun(int successSize) {
//						dismissWaittingDialog();
						hideLoadView();
						if(successSize > 0){
							
							for(int i = 0; i < bItems.size(); i++){
								ItemViewModel itemViewModel = bItems.get(i);
								if(itemViewModel.bItemCheck.get()){
									bItems.remove(i);
									i--;
								}
							}
                            BookMarkDB.getInstance().softDeleteSystemBookmark(datas);
//							if(operationTip == CLEAR_TIP){
//								ToastUtil.showToast(getContext(), R.string.clear_success);
//							}else if(operationTip == DELETE_TIP){
								ToastUtil.showToast(getContext(), R.string.delete_success);
//							}
						}else{
//							if(operationTip == CLEAR_TIP){
//								ToastUtil.showToast(getContext(), R.string.clear_fault);
//							}else if(operationTip == DELETE_TIP){
								ToastUtil.showToast(getContext(), R.string.delete_fault);
//							}
							
						}
						
						checkIsItemsEmpty();
						
					}
				});
			}
		},null);
		return true;
	}
	
	private class DonwloadListener extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(DownloadAPI.ACTION_ON_DOWNLOAD_STATE_CHANGE)){
				Message msg = new Message();
				msg.what = MSG_WHAT_ON_STATE_CHANGE;
				msg.obj = intent;
				mHandler.sendMessage(msg);
			}else if(intent.getAction().equals(DownloadAPI.ACTION_ON_DOWNLOAD_PROGRESS_CHANGE)){
				Message msg = new Message();
				msg.what = MSG_WHAT_ON_PROGRESS_CHANGE;
				msg.obj = intent;
				mHandler.sendMessage(msg);
			}else if(intent.getAction().equals(AppBroadcast.ACTION_UPDATE_BOOKSHELF)){
				onStart();
			}
		}
	}
	
	private ItemViewModel newItemViewModel(DownloadInfo downloadInfo){
		ItemViewModel itemViewModel = new ItemViewModel();
		itemViewModel.id = downloadInfo.id;
		itemViewModel.state = downloadInfo.state;
		itemViewModel.contentId = downloadInfo.contentID;
		itemViewModel.filePathLocation = downloadInfo.filePathLocation;
		itemViewModel.bCoverUrl.set(downloadInfo.logoUrl);
		itemViewModel.bTitle.set(downloadInfo.contentName);
		itemViewModel.bReaderProgress.set(downloadInfo.authorName);
		itemViewModel.isOrder = downloadInfo.isOrder;
		itemViewModel.isOrderNum = downloadInfo.isOrderChapterNum;
		itemViewModel.price = downloadInfo.price;
		itemViewModel.promotionPrice = downloadInfo.promotionPrice;
		itemViewModel.secretKey = downloadInfo.secret_key;
		itemViewModel.size = downloadInfo.size;
		itemViewModel.downloadType = downloadInfo.downloadType;
		if(downloadInfo.state == DownloadAPI.STATE_PAUSE){
			itemViewModel.bItemDownloadStateBtnVisible.set(View.VISIBLE);
			setDownloadProgress(itemViewModel, downloadInfo.current_size, downloadInfo.size);
		}else{
			itemViewModel.bItemDownloadStateBtnVisible.set(View.GONE);
		}
        //在线书籍
        if(itemViewModel.state == DownloadAPI.STATE_ONLINE){
            itemViewModel.bOnlineBookVisibility.set(true);
        }else{
            //本地书籍
            itemViewModel.bOnlineBookVisibility.set(false);
        }
		return itemViewModel;
	}
	
	private DownloadInfo newDownloadInfo(ItemViewModel itemViewModel){
		DownloadInfo downloadInfo = new DownloadInfo();
		downloadInfo.id = itemViewModel.id;
		downloadInfo.state = itemViewModel.state;
		downloadInfo.contentID = itemViewModel.contentId;
		downloadInfo.filePathLocation = itemViewModel.filePathLocation;
		downloadInfo.logoUrl = itemViewModel.bCoverUrl.get();
		downloadInfo.contentName = itemViewModel.bTitle.get();
		downloadInfo.isOrder = itemViewModel.isOrder;
		downloadInfo.isOrderChapterNum = itemViewModel.isOrderNum;
		downloadInfo.price = itemViewModel.price;
		downloadInfo.promotionPrice = itemViewModel.promotionPrice;
		downloadInfo.secret_key = itemViewModel.secretKey;
		downloadInfo.size = itemViewModel.size;
		downloadInfo.current_size = itemViewModel.currentSize;
		downloadInfo.downloadType = itemViewModel.downloadType;
		return downloadInfo;
	}
	
	public class ItemViewModel{
		public String downloadType;
		public long id;
		public int state;
		public String contentId;
		public String filePathLocation;
		long currentSize;
		long size;
		public boolean isOrder;
		public String isOrderNum;
		public String price;
		public String promotionPrice;
		public String secretKey;
		public final StringObservable bCoverUrl = new StringObservable();
		public final StringObservable bTitle = new StringObservable();
		public final StringObservable bDownloadStateValue = new StringObservable();
		public final StringObservable bReaderProgress = new StringObservable();
		public final IntegerObservable bItemCheckBoxVisible = new IntegerObservable(View.GONE);
		public final IntegerObservable bDownloadStateIV = new IntegerObservable(R.drawable.download_start);
		public final IntegerObservable bItemDownloadStateBtnVisible = new IntegerObservable(View.GONE);
		public final BooleanObservable bItemCheck = new BooleanObservable(false);
        public final BooleanObservable bOnlineBookVisibility = new BooleanObservable(false);
		
		public final OnClickCommand bCheckBoxBlock = new OnClickCommand() {
			
			@Override
			public void onClick(View v) {
				if(bItemCheck.get()){
					bItemCheck.set(false);
				}else{
					bItemCheck.set(true);
				}
			}
		};

        /**
         * 下载书籍，已经下载完成返回false
         * @return
         */
		public boolean downloadBook(){
			if (state == DownloadAPI.STATE_FINISH) {
				return false;
			}
			if (mDownloadInfo!=null && mDownloadInfo.size > FileUtil.getStorageSize()) {
				ToastUtil.showToast(getContext(), R.string.sdcard_no_exist_Free_Not_Enough_tip);
			}
			ContentValues values = new ContentValues();
			int mState = -1;
			if (state == DownloadAPI.STATE_PAUSE
					|| state == DownloadAPI.STATE_FAIL
					|| state == DownloadAPI.STATE_FAIL_OUT_MEMORY) {
				values.put(DownloadAPI.STATE, DownloadAPI.STATE_START);
				mState = DownloadAPI.STATE_START;
			} else {
				values.put(DownloadAPI.STATE, DownloadAPI.STATE_PAUSE);
				mState = DownloadAPI.STATE_PAUSE;
			}
			int successSize = getContext().getContentResolver().update(
					DownloadAPI.CONTENT_URI,
					values,
					DownloadHttpEngine.CONTENT_ID + " ='"
							+ contentId + "'", null);
			if (successSize > 0) {
				state = mState;
				setDownloadBtnState(ItemViewModel.this, state);
				setDownloadProgress(ItemViewModel.this, currentSize, size);
			}
		    return true;
		}
	}
	
	public static interface TitleViewHandler{
		public void setTitleView(ActionbarHandler actionbarHandler);
		public void resetTitleView();
	}
	
	public static interface ActionbarHandler{
		public void cancelEditMode();
		public void selectAllItem();
		public void remove();
	}

}
