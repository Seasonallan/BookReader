package com.lectek.android.lereader.binding.model.user;

import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.lectek.android.binding.command.OnItemClickCommand;
import com.lectek.android.binding.command.OnItemLongClickedCommand;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.binding.model.common.PagingLoadViewModel;
import com.lectek.android.lereader.lib.utils.IProguardFilter;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.specific.ActivityChannels;

public class MyOrderViewModelLeyue extends PagingLoadViewModel implements INetAsyncTask {
	
	public final ArrayListObservable<ItemViewModel> bItems = new ArrayListObservable<ItemViewModel>(ItemViewModel.class);
	public final IntegerObservable bNoDateVisibility = new IntegerObservable(View.VISIBLE);
	public final IntegerObservable bContentVisibility = new IntegerObservable(View.GONE);
	
	private MyOrderModelLeyue mMyOrderModel;
	private ArrayList<ContentInfoLeyue> mDataSource;
	
	public final FooterViewModel bFooterViewModel = new FooterViewModel();
	
	public final OnItemClickCommand bOnItemClickCommand = new OnItemClickCommand() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			ItemViewModel item = (ItemViewModel) parent.getItemAtPosition(position);
			if(item != null && item.contentInfo != null){
				//天翼阅读书籍
				if(!TextUtils.isEmpty(item.contentInfo.getOutBookId())){
					ActivityChannels.gotoLeyueBookDetail(getContext(), item.contentInfo.getOutBookId(),
							LeyueConst.EXTRA_BOOLEAN_IS_SURFINGREADER, true,
							LeyueConst.EXTRA_LE_BOOKID, item.contentInfo.getBookId()
							);
				}else{
					//乐阅书籍
					ActivityChannels.gotoLeyueBookDetail(getContext(), item.contentInfo.getBookId());
				}
			}
		}
	};
	
	public final OnItemLongClickedCommand bOnItemLongClickedCommand = new OnItemLongClickedCommand() {
		
		@Override
		public void onItemLongClick(AdapterView<?> parent, View view, int position,
				long id) {
			ItemViewModel item = (ItemViewModel) parent.getItemAtPosition(position);
			if(item != null && item.contentInfo != null){
				//天翼阅读书籍
				if(!TextUtils.isEmpty(item.contentInfo.getOutBookId())){
					ActivityChannels.gotoLeyueBookDetail(getContext(), item.contentInfo.getOutBookId(),
							LeyueConst.EXTRA_BOOLEAN_IS_SURFINGREADER, true,
							LeyueConst.EXTRA_LE_BOOKID, item.contentInfo.getBookId()
							);
				}else{
					//乐阅书籍
					ActivityChannels.gotoLeyueBookDetail(getContext(), item.contentInfo.getBookId());
				}
			}
			
		}
	};

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if(result != null && !isCancel){
//			if(mDataSource == null){
//				mDataSource = mMyOrderModel.getData();
//			}
			mDataSource = (ArrayList<ContentInfoLeyue>)result;
			ArrayListObservable<ItemViewModel> mTempItems = new ArrayListObservable<ItemViewModel>(ItemViewModel.class);
			for (ContentInfoLeyue contentInfo : mDataSource) {
				if(contentInfo != null){
					ItemViewModel itemViewModel = newItemViewModel(contentInfo);
					mTempItems.add(itemViewModel);
				}
			}
			
			if(mTempItems.size() > 0){
				bItems.addAll(mTempItems);
			}else{
				setLoadPageCompleted(true);
			}

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

//	@Override
//	public boolean onFail(Exception e, String tag, Object... params) {
//		super.onFail(e, tag, params);
//		return false;
//	}

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

	public MyOrderViewModelLeyue(Context context, INetLoadView loadView) {
		super(context, loadView);
		mMyOrderModel = new MyOrderModelLeyue();
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
	
	private ItemViewModel newItemViewModel(ContentInfoLeyue contentInfo){
		ItemViewModel itemViewModel = new ItemViewModel();
		itemViewModel.contentInfo = contentInfo;
		itemViewModel.bCoverUrl.set(contentInfo.getCoverPath());
		itemViewModel.bBookName.set(contentInfo.getBookName());
//		itemViewModel.bAuthorName.set(getContext().getString(R.string.content_info_author, contentInfo.getAuthor()));
		itemViewModel.bAuthorName.set(contentInfo.getAuthor());
		itemViewModel.bIntroduce.set(contentInfo.getIntroduce());
		return itemViewModel;
	}
	
	public class ItemViewModel implements IProguardFilter{
		public ContentInfoLeyue contentInfo;
		public final StringObservable bCoverUrl = new StringObservable();
		public final StringObservable bBookName = new StringObservable();
		public final StringObservable bAuthorName = new StringObservable();
		public final StringObservable bIntroduce = new StringObservable();
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
	
	private class FooterViewModel implements IProguardFilter{
		public final BooleanObservable bFooterLoadingViewVisibility = MyOrderViewModelLeyue.this.bFootLoadingVisibility;
		public final BooleanObservable bFooterLoadingCompletedVisibility = MyOrderViewModelLeyue.this.bFootLoadCompletedVisibility;
	}

}
