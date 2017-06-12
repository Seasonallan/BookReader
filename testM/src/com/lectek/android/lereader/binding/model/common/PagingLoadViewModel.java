package com.lectek.android.lereader.binding.model.common;

import gueei.binding.observables.BooleanObservable;
import android.content.Context;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.lectek.android.binding.command.OnScrollStateChangedCommand;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.ui.INetLoadView;
/**
 * 
 * @author linyiwei
 *
 */
public abstract class PagingLoadViewModel extends BaseLoadNetDataViewModel implements OnScrollListener{
	public final BooleanObservable bHeadViewVisibility = new BooleanObservable(false);
	public final BooleanObservable bHeadLoadingVisibility = new BooleanObservable(false);
	public final BooleanObservable bFootLoadingVisibility = new BooleanObservable(false);
	public final BooleanObservable bFootLoadCompletedVisibility = new BooleanObservable(false);
	
	private boolean isLoadCompleted;
	
	public final OnScrollStateChangedCommand bOnScrollStateChangedCommand = new OnScrollStateChangedCommand() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			PagingLoadViewModel.this.onScrollStateChanged(view, scrollState);
		}
	};
	
	public PagingLoadViewModel(Context context, INetLoadView loadView) {
		super(context, loadView);
	}
	
	@Override
	public boolean onPreLoad(String tag, Object... params) {
		if(getPagingLoadModel().getTag().equals(tag)){
			boolean isClearData = (Boolean) params[1];
			boolean isNext = (Boolean) params[2];
			boolean isShowLoadView = (Boolean) params[3];
			if(isClearData && isShowLoadView){
				showLoadView();
			}else{
				if(isNext){
					bFootLoadingVisibility.set(true);
				}else{
					bHeadLoadingVisibility.set(true);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if(params!=null&&params.length >= 4){
			if(getPagingLoadModel().getTag().equals(tag)){
				boolean isClearData = (Boolean) params[1];
				boolean isNext = (Boolean) params[2];
				if(isClearData){
					hideLoadView();
				}else{
					if(isNext){
						bFootLoadingVisibility.set(false);
					}else{
						bHeadLoadingVisibility.set(false);
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		if(getPagingLoadModel().getTag().equals(tag)){
			boolean isClearData = (Boolean) params[1];
			if(isClearData){
				showRetryView();
			}
		}
		return false;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
		if(isLoadCompleted)
			return;
		
		int firstVisiblePosition = view.getFirstVisiblePosition();
		if(view.getLastVisiblePosition() == view.getCount() - 1){//向下滚动
			if(getPagingLoadModel().loadNextPage()){
				return;
			}
			return;
		}
		if(firstVisiblePosition == 0){//向上滚动
			if(getPagingLoadModel().loadPrevPage()){
				return;
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {}
	
	/**
	 * 这种当前列表是否已经完全加载完数据了
	 * @param isCompleted
	 */
	protected void setLoadPageCompleted(boolean isCompleted){
		isLoadCompleted = isCompleted;
		bFootLoadingVisibility.set(false);
		bFootLoadCompletedVisibility.set(true);
	}
	
	protected abstract PagingLoadModel<?> getPagingLoadModel();
}
