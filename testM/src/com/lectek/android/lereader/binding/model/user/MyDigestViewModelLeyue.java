package com.lectek.android.lereader.binding.model.user;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnItemClickCommand;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.binding.model.common.PagingLoadViewModel;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.specific.MyDigestInfoActivity;
import com.lectek.android.lereader.utils.CommonUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;

public class MyDigestViewModelLeyue extends PagingLoadViewModel implements INetAsyncTask {
	
	public final ArrayListObservable<ItemViewModel> bItems = new ArrayListObservable<ItemViewModel>(ItemViewModel.class);
	public final IntegerObservable bNoDateVisibility = new IntegerObservable(View.VISIBLE);
	public final IntegerObservable bContentVisibility = new IntegerObservable(View.GONE);
	
	private MyDigestModelLeyue mMyOrderModel;
	private ArrayList<BookDigests> mDataSource;
	
	
	public final OnItemClickCommand bOnItemClickCommand = new OnItemClickCommand() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			ItemViewModel item = (ItemViewModel) parent.getItemAtPosition(position);
			if(item != null && item.contentInfo != null){
				BookDigests digest = item.contentInfo;
				/*MyDigestInfoActivity.openActivity(getContext(),
						digest.getContentId(), digest.getContentName()
						,digest.getDate(), digest.getCount());*/
				MyDigestInfoActivity.openActivity(getContext(),
                        digest);
			}
		}
	};

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		bItems.clear();
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

	public MyDigestViewModelLeyue(Context context, INetLoadView loadView) {
		super(context, loadView);
		mMyOrderModel = new MyDigestModelLeyue();
		mMyOrderModel.addCallBack(this);
	}

	@Override
	protected PagingLoadModel<?> getPagingLoadModel() {
		return mMyOrderModel;
	}

	@Override
	protected boolean hasLoadedData() {
		return mDataSource != null;
	}
	
	private ItemViewModel newItemViewModel(BookDigests contentInfo){
		ItemViewModel itemViewModel = new ItemViewModel();
		itemViewModel.contentInfo = contentInfo;
		itemViewModel.bCoverUrl.set(contentInfo.getAuthor());
        String content = contentInfo.getContentName();
        if(TextUtils.isEmpty(content)){
            content = "未知书名";
        }
		itemViewModel.bBookName.set(content);
//		itemViewModel.bAuthorName.set(getContext().getString(R.string.content_info_author, contentInfo.getAuthor()));
		itemViewModel.bAuthorName.set(getTime(contentInfo.getDate()));
//		itemViewModel.bIntroduce.set(getContext().getString(R.string.my_digest_item_count, contentInfo.getCount()));
		itemViewModel.bDigestNum.set(contentInfo.getCount() + "");
		return itemViewModel;
	}

	private String getTime(Long datelong){
	  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  Date date = new Date();
	  date.setTime(datelong);
	  return CommonUtil.getNowDay(sdf.format(date));
	}
	
	public class ItemViewModel{
		public BookDigests contentInfo;
		public final StringObservable bCoverUrl = new StringObservable();
		public final StringObservable bBookName = new StringObservable();
		public final StringObservable bAuthorName = new StringObservable();
		public final StringObservable bIntroduce = new StringObservable();
		public final StringObservable bDigestNum = new StringObservable();
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
