package com.lectek.android.lereader.binding.model.collect;

import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.binding.command.OnItemClickCommand;
import com.lectek.android.binding.command.OnItemLongClickedCommand;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.response.CollectCancelListResultItem;
import com.lectek.android.lereader.net.response.CollectResultInfo;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.IRequestResultViewNotify;
import com.lectek.android.lereader.ui.specific.ActivityChannels;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.ToastUtil;

/**
 * 收藏页面 Model
 * @author donghz
 * @date 2014-3-27
 */
public class CollectBookViewModel extends BaseLoadNetDataViewModel implements INetAsyncTask {

	private CollectListInfoModel mCollectListInfoModel;
	private CollectDelModel mCollectDelModel;
	private CollectDelListModel mCollectDelListModel;
	private ArrayList<CollectResultInfo> mCollectResultInfos;
	private ArrayList<CollectCancelListResultItem> mCancelList;
	public final ArrayListObservable<CollectAdapterInfo> bCollectItems = new ArrayListObservable<CollectAdapterInfo>(CollectAdapterInfo.class);
	public final IntegerObservable bNoDateVisibility = new IntegerObservable(View.VISIBLE);
	private UserActionListener mUserActionListener;
	private String collectionIds="";
	private Context mContext;
	private boolean mIsEdit;
	private Integer mDelItemId;
	
	
	public CollectBookViewModel(Context context, INetLoadView loadView) {
		super(context, loadView);
		mContext = context;
		mCollectListInfoModel = new CollectListInfoModel();
		mCollectListInfoModel.addCallBack(this);
		mCollectDelModel = new CollectDelModel();
		mCollectDelModel.addCallBack(this);
		mCollectDelListModel = new CollectDelListModel();
		mCollectDelListModel.addCallBack(this);
		mCollectResultInfos = new ArrayList<CollectResultInfo>();
		mCancelList = new ArrayList<CollectCancelListResultItem>();
	}

	/**
	 * 获取收藏列表数据
	 */
	private void getColletBookInfo(){
		String userId = PreferencesUtil.getInstance(mContext).getUserId();
		Integer start=0;
		Integer count =100;
		if(!TextUtils.isEmpty(userId)){
			mCollectListInfoModel.start(userId,start,count);
			showLoadView();
		}
	}
	
	@Override
	public boolean onPreLoad(String tag, Object... params) {
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		LogUtil.e("dhz", "onFail   " + e.toString());
		if(mCollectListInfoModel.getTag().equals(tag)){
            hideLoadView();
            mUserActionListener.showRetryImgView();
            showRetryView();
		}
		if(mCollectDelListModel.getTag().equals(tag)){
			hideLoadView();
			LogUtil.e("dhz", "批量删除 fail");
		}
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if(result != null && !isCancel){
			if(mCollectListInfoModel.getTag().equals(tag)){//获取列表数据
				mCollectResultInfos.clear();
				bCollectItems.clear();
				mCollectResultInfos = (ArrayList<CollectResultInfo>)result;
				buildCollectAdapterInfo(mCollectResultInfos);
				if(mCollectResultInfos.size()==0){
					hideLoadView();
//					mUserActionListener.setNoDataView(IRequestResultViewNotify.tipImg.no_info,false);
					bNoDateVisibility.set(View.VISIBLE);
				}else{
					bNoDateVisibility.set(View.GONE);
					hideLoadView();
				}
			}
			
			if(mCollectDelModel.getTag().equals(tag)){//取消收藏成功
				int size = bCollectItems.size();
				for (int i = 0; i < size; i++) {
					Integer itemId = bCollectItems.get(i).collectionId;
					if(itemId == mDelItemId){
						String title = bCollectItems.get(i).bCollectTitleText.toString();
						ToastUtil.showToast(getContext(), getContext().getResources()
								.getString(R.string.collect_cancel_str_success, title));
						bCollectItems.remove(i);
						bCollectItems.notifyChanged();
						if(bCollectItems.size()==0){
//							mUserActionListener.setNoDataView(IRequestResultViewNotify.tipImg.no_info,false);
							bNoDateVisibility.set(View.VISIBLE);
						}
						break;
					}
				}
				
			}
			
			if(mCollectDelListModel.getTag().equals(tag)){//批量取消收藏
				mCancelList.clear();
				ArrayList<CollectCancelListResultItem> cancelList =(ArrayList<CollectCancelListResultItem>)result;
				if(cancelList!=null){
					mCancelList.addAll(cancelList);
				}
				for(CollectCancelListResultItem item:mCancelList){
					CollectAdapterInfo tempInfo = null;
					for(CollectAdapterInfo info :bCollectItems){
						if(item != null && item.getResult() != null && item.getResult()==true){//取消收藏成功
							if(info.collectionId.equals(item.getCollectionId())){
								tempInfo = info;
							}
						}else{
							if(item != null && info.collectionId.equals(item.getCollectionId())){//取消收藏失败
								String bookName =info.bCollectTitleText.get();
								String toastStr = mContext.getResources().getString(R.string.collect_cancel_str,bookName);
								ToastUtil.showToast(mContext, toastStr);
							}
						}
					}
					if(tempInfo!=null){
						bCollectItems.remove(tempInfo);
					}
					LogUtil.e("dhz", "collectId" + item.getCollectionId() + " result: " + item.getResult());
				}
				hideLoadView();
				bCollectItems.notifyChanged();
				if(bCollectItems.size()==0){
//					mUserActionListener.setNoDataView(IRequestResultViewNotify.tipImg.no_info,false);
					bNoDateVisibility.set(View.VISIBLE);
				}
				LogUtil.e("dhz", "批量删除 ok");
			}
		}else{
			//hideLoadView();
		}
		return false;
	}

	@Override
	protected boolean hasLoadedData() {
		return mCollectResultInfos !=null;
	}

	@Override
	public void onStart() {
		super.onStart();
		tryStartNetTack(this);
	}
	
	public final OnItemClickCommand bOnItemClickCommand = new OnItemClickCommand() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(bCollectItems.get(position).resourceId==null)
				return;
			
			String outBookId = bCollectItems.get(position).outBookId;
			String bookId =String.valueOf(bCollectItems.get(position).resourceId);
			//天翼阅读书籍
			if(!TextUtils.isEmpty(outBookId)){
				ActivityChannels.gotoLeyueBookDetail(getContext(), outBookId,
						LeyueConst.EXTRA_BOOLEAN_IS_SURFINGREADER, true,
						LeyueConst.EXTRA_LE_BOOKID, bookId
						);
			}else{
				//乐阅书籍
				ActivityChannels.gotoLeyueBookDetail(mContext, bookId);
			}
		}
	};
	
	
	public final OnItemLongClickedCommand bOnItemLongClickedCommand = new OnItemLongClickedCommand(){
		@Override
		public void onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			
			if(bCollectItems.get(position).resourceId==null)
				return;
			
			String outBookId = bCollectItems.get(position).outBookId;
			String bookId =String.valueOf(bCollectItems.get(position).resourceId);
			//天翼阅读书籍
			if(!TextUtils.isEmpty(outBookId)){
				ActivityChannels.gotoLeyueBookDetail(getContext(), outBookId,
						LeyueConst.EXTRA_BOOLEAN_IS_SURFINGREADER, true,
						LeyueConst.EXTRA_LE_BOOKID, bookId
						);
			}else{
				//乐阅书籍
				ActivityChannels.gotoLeyueBookDetail(mContext, bookId);
			}
		}
	};
	
	/**
	 * 删除收藏
	 */
	public void DeleteCollectBook(){
		boolean checkedDelItem = false;
		String titleStr ="";
		collectionIds="";
		for(int i=0;i<bCollectItems.size();i++){
			boolean ischeck = bCollectItems.get(i).bCollectItemCheck.get();
			if(ischeck){
				checkedDelItem = true;
				break;
			}
		}
		
		if(!checkedDelItem){//是否已经勾选
			ToastUtil.showToast(mContext, R.string.book_no_selected_item);
			return;
		}
		
		String tempStr ="";
		int removeCount =0;
		for(CollectAdapterInfo info :bCollectItems){
			if(info.bCollectItemCheck.get()){
				String collectionId = String.valueOf(info.collectionId)+",";
				collectionIds = collectionId + collectionIds;
				tempStr = info.bCollectTitleText.get().toString();
				removeCount++;
			}
		}
		LogUtil.e("dhz", "collectionIds " + collectionIds);
		
		if(removeCount>0){
			if(removeCount==1){
				titleStr = mContext.getResources().getString(R.string.collect_deleted_str,tempStr);
			}else{
				titleStr = mContext.getResources().getString(R.string.collect_delete_all_str,removeCount);
			}
		}
		
		DialogUtil.commonConfirmDialog((Activity) mContext, "", titleStr, R.string.btn_text_confirm, R.string.btn_text_cancel,
                new DialogUtil.ConfirmListener() {
                    @Override
                    public void onClick(View v) {
                        cancelListCollct(collectionIds);
                        if (mUserActionListener != null) {
                            mUserActionListener.cancelEditModel();
                        }
                        if (bCollectItems.size() == 0) {
//                            mUserActionListener.setNoDataView(IRequestResultViewNotify.tipImg.no_info, false);
                        	bNoDateVisibility.set(View.VISIBLE);
                        }
                    }
                }, null);
	}
	
	
	/**
	 * 取消收藏
	 */
	private void cancelCollect(Integer collectionId){
		mDelItemId = collectionId;
		String userId =PreferencesUtil.getInstance(mContext).getUserId();
		if(!TextUtils.isEmpty(userId)&&collectionId!=null){
			mCollectDelModel.start(collectionId,userId);
		}
	}
	
	/**
	 * 批量删除
	 */
	private void cancelListCollct(String collectionIds){
		String userId =PreferencesUtil.getInstance(mContext).getUserId();
		if(!TextUtils.isEmpty(userId)&&!TextUtils.isEmpty(collectionIds)){
			mCollectDelListModel.start(collectionIds,userId);
			showLoadView();
		}
	}
	
	/**
	 * 设置选中状态
	 * @param check
	 */
	public void setCollectCheckBoxVisible(boolean check){
		if(!check)
		 mIsEdit = false;
		for(int i=0;i<bCollectItems.size();i++){
			bCollectItems.get(i).bCollectItemCheckBoxVisible.set(check);
		}
	}
	
	/**
	 * 清除选中状态
	 * @param check
	 */
	public void clearCheckBoxVisible(boolean check){
		for(int i=0;i<bCollectItems.size();i++){
			bCollectItems.get(i).bCollectItemCheck.set(check);
		}
	}
	
	public boolean isEdit() {
		return mIsEdit;
	}
	
	/**
	 * CollectAdapterInfo
	 */
	private void buildCollectAdapterInfo(ArrayList<CollectResultInfo> collectResultInfos){
		for(CollectResultInfo item:collectResultInfos){
            if (item.getCollectBookInfo() != null){
                CollectAdapterInfo adapterItem = new CollectAdapterInfo();
                adapterItem.collectionId = item.getCollectionId();
                adapterItem.resourceId = item.getResourceId();
                adapterItem.outBookId = item.getCollectBookInfo().getOutBookId();
                adapterItem.bCollectCoverUrl.set(item.getCollectBookInfo().getCoverPath());
                adapterItem.bCollectTitleText.set(item.getCollectBookInfo().getBookName());
                adapterItem.bCollectAuthorText.set(getResources().
                        getString(R.string.collect_book_author_str,item.getCollectBookInfo().getAuthor()));
                adapterItem.bCollectDescripText.set(getResources().
                        getString(R.string.collect_book_desc_str,item.getCollectBookInfo().getIntroduce()));
                bCollectItems.add(adapterItem);
            }
		}
	}
	
	/**
	 * 收藏适配器数据
	 */
	public class CollectAdapterInfo{
		public Integer collectionId;
		public Integer resourceId;
		public String outBookId;
		public final BooleanObservable bCollectItemCheck = new BooleanObservable(false);
		public final BooleanObservable bCollectItemCheckBoxVisible = new BooleanObservable(false);
		public final IntegerObservable bCollectBookDefaultRes = new IntegerObservable(R.drawable.book_default);
		public final StringObservable bCollectCoverUrl = new StringObservable();
		public final StringObservable bCollectTitleText = new StringObservable();
		public final StringObservable bCollectAuthorText = new StringObservable();
		public final StringObservable bCollectDescripText = new StringObservable();
		public final OnClickCommand bCollectonClick = new OnClickCommand() {
			
			@Override
			public void onClick(View v) {
				cancelCollect(collectionId);
				
			}
		};
		
	}
	
	public void setUserActionListener(UserActionListener listener){
		mUserActionListener = listener;
	}
	
	public static interface UserActionListener {
		public void setButtonRightEnable(boolean isEnable);
		public void setNoDataView(IRequestResultViewNotify.tipImg tip, boolean bShow);
		public void cancelEditModel();
        public void showRetryImgView();
    }

	@Override
	public boolean isNeedReStart() {
		return !hasLoadedData();
	}

	@Override
	public boolean isStop() {
		return !mCollectListInfoModel.isStart();
	}

	@Override
	public void start() {
		getColletBookInfo();
	}

}
