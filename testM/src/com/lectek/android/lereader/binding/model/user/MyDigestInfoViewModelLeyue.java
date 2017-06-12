package com.lectek.android.lereader.binding.model.user;

import gueei.binding.Observable;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.binding.command.OnItemClickCommand;
import com.lectek.android.binding.command.OnItemLongClickedCommand;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.binding.model.common.PagingLoadViewModel;
import com.lectek.android.lereader.download.DownloadHttpEngine;
import com.lectek.android.lereader.net.response.DownloadInfo;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.basereader_leyue.BaseReaderActivityLeyue;
import com.lectek.android.lereader.ui.basereader_leyue.Book;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.ToastUtil;
import com.lectek.android.lereader.widgets.CustomPopupWindow;
import com.lectek.lereader.core.text.Constant;

public class MyDigestInfoViewModelLeyue extends PagingLoadViewModel implements INetAsyncTask {
	
	public final ArrayListObservable<ItemViewModel> bItems = new ArrayListObservable<ItemViewModel>(ItemViewModel.class);
	public final IntegerObservable bNoDateVisibility = new IntegerObservable(View.VISIBLE);
	public final IntegerObservable bContentVisibility = new IntegerObservable(View.GONE);
	
	public final StringObservable bBookName = new StringObservable();
	public final StringObservable bDigestTime = new StringObservable();
	public final StringObservable bDigestNum = new StringObservable();
	public final StringObservable bCoverUrl = new StringObservable();
	
	private MyDigestInfoModelLeyue mMyOrderModel;
	private ArrayList<BookDigests> mDataSource;
	private CustomPopupWindow popupWindow;
	private View popupWindowView;

    private BookDigests mClickBookDigest;
	public final OnItemClickCommand bOnItemClickCommand = new OnItemClickCommand() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			ItemViewModel item = (ItemViewModel) parent.getItemAtPosition(position);
			if(item != null && item.contentInfo != null){
                mClickBookDigest = item.contentInfo;
				mDownloadInfo = DownloadPresenterLeyue.getDownloadUnitById(mClickBookDigest.getContentId());
				gotoReadBook(); 
			}
		}
	};
	
	public final OnItemLongClickedCommand bOnItemLongClickCommand = new OnItemLongClickedCommand() {
		
		@Override
		public void onItemLongClick(final AdapterView<?> parent, View view, final int position,
				long id) {
			final ItemViewModel item = (ItemViewModel) parent.getItemAtPosition(position);
			
			popupWindowView.findViewById(R.id.tv_digest_copy).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					popupWindow.dismissView();
					String str = item.bDigestMsg.toString();
					ClipboardManager cm =(ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
					cm.setText(str);
					ToastUtil.showToast(getContext(), R.string.copy_sucess);
					
				}
			});;
			
			
			popupWindowView.findViewById(R.id.tv_digest_delete).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					popupWindow.dismissView();
					showDelDialog(item, mDataSource.get(position));
				}
			});;
			
			popupWindowView.findViewById(R.id.tv_digest_share).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					popupWindow.dismissView();
					
				}
			});;
			
			popupWindow = new CustomPopupWindow(popupWindowView, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			popupWindow.showViewAtAnchorAlignTopCenter(view);
			
		}
	};

	private DownloadInfo mDownloadInfo;
	private void gotoReadBook(){
		if (mDownloadInfo == null) {
            ToastUtil.showToast(getContext(), "该书籍未添加到书架,无法打开!");
			return;
		}
		Book book = new Book();
		book.setPath(mDownloadInfo.filePathLocation);
		book.setBookId(mDownloadInfo.contentID);
		book.setFeeStart(mDownloadInfo.isOrderChapterNum);
        book.setBookFormatType(mDownloadInfo.downloadType == null? DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_BOOK: mDownloadInfo.downloadType);
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
		book.setDownloadFullVersonBook(mDownloadInfo.isDownloadFullVersonBook);
		boolean isOnline = false;
		if(!TextUtils.isEmpty(mDownloadInfo.filePathLocation)){
			File file = new File(mDownloadInfo.filePathLocation);
			if (!file.exists()) {
				isOnline = true;
			}
		}else{
			isOnline = true;
		}
		book.setOnline(isOnline);
        int p = mClickBookDigest.getPosition4Txt();
        if (p == -1){
            p =  mClickBookDigest.getPosition();
        }
		BaseReaderActivityLeyue.openActivity(getContext(), book,
                mClickBookDigest.getChaptersId(), p);

	}

    @Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if(result != null && !isCancel){
			if(mDataSource == null){
				mDataSource = mMyOrderModel.getData();
			}
			ArrayListObservable<ItemViewModel> mTempItems = new ArrayListObservable<ItemViewModel>(ItemViewModel.class);
			for (BookDigests contentInfo : mDataSource) {
				ItemViewModel itemViewModel = newItemViewModel(contentInfo);
				mTempItems.add(itemViewModel);
			}
			bItems.setAll(mTempItems);
		}
		
		checkIsItemsEmpty();
		
		hideLoadView();
		
		if(bHeadLoadingVisibility.get()){
			bHeadLoadingVisibility.set(false);
		}
		
		if(bFootLoadingVisibility.get()){
			bFootLoadingVisibility.set(false);
		}
		return false;
	}
	
	private void checkIsItemsEmpty(){
		if(bItems != null && bItems.size() > 0){
			bNoDateVisibility.set(View.GONE);
			bContentVisibility.set(View.VISIBLE);
		}else{
			bNoDateVisibility.set(View.VISIBLE);
			bContentVisibility.set(View.GONE);
		}
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		super.onFail(e, tag, params);
		return false;
	}

	@Override
	public void onStart() {
		super.onStart();
		tryStartNetTack(this);
	}

	@Override
	public void onRelease() {
		super.onRelease();
		mMyOrderModel.cancel();
	}

	private int mDigestCount = 0;
	public MyDigestInfoViewModelLeyue(Context context, BookDigests digest, INetLoadView loadView) {
		super(context, loadView);
		mDigestCount = digest.getCount();
        String content = digest.getContentName();
        if(TextUtils.isEmpty(content)){
            content = "未知书名";
        }
		bBookName.set(content);
		bDigestTime.set(getContext().getString(R.string.my_digest_item_date, getTime(digest.getDate())));
		bDigestNum.set(mDigestCount + "");
		bCoverUrl.set(digest.getAuthor());
		mMyOrderModel = new MyDigestInfoModelLeyue(digest);
		mMyOrderModel.addCallBack(this);
		initDigestPopupWindow();
		
	}

	@Override
	protected PagingLoadModel<?> getPagingLoadModel() {
		return mMyOrderModel;
	}

	@Override
	protected boolean hasLoadedData() {
		return mDataSource != null;
	}
	
	private ItemViewModel newItemViewModel(final BookDigests contentInfo){
		final ItemViewModel itemViewModel = new ItemViewModel();
		itemViewModel.contentInfo = contentInfo;
        String chapterName = contentInfo.getChaptersName();
        if(TextUtils.isEmpty(chapterName)){
            chapterName = "第"+ (contentInfo.getChaptersId() + 1) +"章";
        }
//		itemViewModel.bChapterName.set(chapterName);
        String content = contentInfo.getContent();
        SpannableString spanStr = new SpannableString(content);
        int index = content.indexOf(Constant.REPLACEMENT_SPAN_CHAR);
        while (index >= 0){
            ImageSpan span = new ImageSpan(getContext(), R.drawable.tp_icon);
            spanStr.setSpan(span, index, index + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            index = content.indexOf(Constant.REPLACEMENT_SPAN_CHAR, index +1);
        }
        itemViewModel.bDigestContent.set(spanStr);
		String msg = contentInfo.getMsg();
		if(TextUtils.isEmpty(msg)){
			msg = getContext().getString(R.string.reader_bookdigest_msg_none);
		}
		itemViewModel.bDigestMsg.set(getContext().getString(R.string.reader_bookdigest_msg_part1)+""+ msg);
		itemViewModel.bDigestTime.set(getTime(contentInfo.getDate())); 
		return itemViewModel;
	}

    public class SpannableStringObservable extends Observable<SpannableString> {
        public SpannableStringObservable() {
            super(SpannableString.class);
        }

        public SpannableStringObservable(SpannableString value){
            super(SpannableString.class, value);
        }
    }


    private void showDelDialog(final ItemViewModel itemViewModel, final BookDigests contentInfo){
		DialogUtil.commonConfirmDialog((Activity) getContext(), null,
                getContext().getString(R.string.book_digest_delete_confirm)
                , R.string.alert_dialog_ok, -1, new DialogUtil.ConfirmListener() {
            @Override
            public void onClick(View v) {
                mDigestCount--;
                BookDigestsDB.getInstance().deleteBookDigest(contentInfo);
                bDigestNum.set(mDigestCount + "");
                bItems.remove(itemViewModel);
                bItems.notifyChanged();
                Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
            }
        }, null);
	}
    
    private void initDigestPopupWindow(){
    	popupWindowView = LayoutInflater.from(getContext()).inflate(R.layout.digest_popup_window_layout, null);
		
    }
	
	private String getTime(Long datelong){
	  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  Date date = new Date();
	  date.setTime(datelong);
	  return CommonUtil.getNowDay(sdf.format(date));
	}
	
	public class ItemViewModel{
		public BookDigests contentInfo;
//		public final StringObservable bChapterName = new StringObservable();
		public final SpannableStringObservable bDigestContent = new SpannableStringObservable();
		public final StringObservable bDigestMsg = new StringObservable();
		public final StringObservable bDigestTime = new StringObservable();
		public final OnClickCommand bOnDelClickCommand = new OnClickCommand() {
			@Override
			public void onClick(View v) { 
				showDelDialog(ItemViewModel.this, contentInfo);
			} 
		};
		public final OnClickCommand bOnContainerClickCommand = new OnClickCommand() {
			@Override
			public void onClick(View v) {  
				mDownloadInfo = DownloadPresenterLeyue.getDownloadUnitById(contentInfo.getContentId());
				gotoReadBook(); 
			} 
		};
	}

	@Override
	public boolean isNeedReStart() {
		return !hasLoadedData();
	}

	@Override
	public boolean isStop() {
		return !mMyOrderModel.isStart();
	}

	@Override
	public void start() {
		mMyOrderModel.loadPage();
	}

}
