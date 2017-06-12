package com.lectek.android.lereader.binding.model.common;

import java.util.ArrayList;

import android.text.TextUtils;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.lib.cache.DataCacheManage;
import com.lectek.android.lereader.lib.cache.ValidPeriodCache;
import com.lectek.android.lereader.lib.utils.LogUtil;

public abstract class PagingLoadModel<Result> extends BaseLoadNetDataModel<ArrayList<Result>> {
	/**
	 * 请求页越界
	 */
	public static final String START_RESULT_OUT_BOUNDS = "START_RESULT_OUT_OF_BOUNDS";
	private int mCurrentPage;
	private int mLastPage;
	private int mStartPage;
	private ArrayList<Result> mDatas;
	private Object[] mParams;
	
	public PagingLoadModel(Object... params){
		this(new ArrayList<Result>(), params);
	}
	
	public PagingLoadModel(ArrayList<Result> dataContainer,Object... params){
		mParams = params;
		mDatas = dataContainer;
		mLastPage = mStartPage = mCurrentPage = 1;
	}
	
	public ArrayList<Result> getData(){
		return mDatas;
	}
	/**
	 * 位置计数从1开始
	 * @return
	 */
	public int getCurrentPage(){
		return mCurrentPage;
	}
	/**
	 * 位置计数从1开始
	 * @return
	 */
	public int getLastPage(){
		return mLastPage;
	}
	/**
	 * 位置计数从1开始
	 * @return
	 */
	public int getStartPage(){
		return mStartPage;
	}
	
	public boolean loadNextPage(){
		if(mLastPage < getPageSize()){//如果当前最后一页不是最后一页
			//向下翻页
			int loadPageIndex = mLastPage + 1;
			return loadPage(loadPageIndex,false,true,false);
		}
		return false;
	}
	
	public boolean loadPrevPage(){
		if(mStartPage > 1){//如果当前开始不是第一页
			//向上翻页
			int loadPageIndex = mStartPage - 1;
			return loadPage(loadPageIndex,false,false,false);
		}
		return false;
	}
	
	public final boolean loadPage() {
		return loadPage(1,true,true,true);
	}
	
	public final boolean loadPage(boolean isShowLoadView) {
		return loadPage(1,true,true,isShowLoadView);
	}
	
	public boolean loadPage(int loadPage) {
		return loadPage(loadPage,true,true,false);
	}
	
	private boolean loadPage(int loadPage,boolean isClearData,boolean isNext,boolean isShowLoadView) {
		String startResult = startLoadPage(loadPage, isClearData, isNext, isShowLoadView);
		if(START_RESULT_SUCCEED.equals(startResult)){
			return true;
		}
		return false;
	}
	
	private String startLoadPage(int loadPage,boolean isClearData,boolean isNext,boolean isShowLoadView) {
		return super.start(loadPage,isClearData,isNext,isShowLoadView,mParams);
	}

	@Override
	protected String onStartPreCheck(Object... params) {
		int loadPage = (Integer) params[0];
		boolean isClearData = (Boolean) params[1];
		if(!isClearData && (getPageSize() == null || loadPage < 0 || loadPage > getPageSize())){
			return START_RESULT_OUT_BOUNDS;
		}
		return super.onStartPreCheck(params);
	}
	
	@Override
	protected final ArrayList<Result> onLoad(Object... params) throws Exception {
		return onLoadCurrentPageData((Integer)params[0] - 1, getPageItemSize(), params[3]);
	}
	
	@Override
	protected void dispatchOnPostLoad(ArrayList<Result> result, String tag,
			boolean isSucceed, boolean isCancel, Object... params) {
		if(result != null && isSucceed && !isCancel){
			boolean isClearData = (Boolean) params[1];
			boolean isNext = (Boolean) params[2];
			mCurrentPage = (Integer) params[0];
			if(isClearData){//如果跳页，最开始和最后的页数都是一样的
				mStartPage = mLastPage = mCurrentPage;
				mDatas.clear();
			}
			if(isNext){
				mLastPage = mCurrentPage;
				mDatas.addAll(result);
			}else{
				mStartPage = mCurrentPage;
				mDatas.addAll(0, result);
			}
			if(mCurrentPage == 1 && mDatas.size() > 0){				
				saveDataCache(isValidDataCacheEnabled());
			}
		}
		super.dispatchOnPostLoad(result, tag, isSucceed, isCancel, params);
	}

	public ArrayList<Result> loadCacheData(){
		if(TextUtils.isEmpty(getGroupKey()) || TextUtils.isEmpty(getKey())){
			return null;
		}
		Object dataObject = DataCacheManage.getInstance().getData(getGroupKey(), getKey());
		if(dataObject != null){
			ViewDataCache<Result> viewDataCache ;
			if(dataObject instanceof ValidPeriodCache<?>){
				viewDataCache  =  (ViewDataCache<Result>) ((ValidPeriodCache<?>) dataObject).getData();
			}else{
				viewDataCache = (ViewDataCache<Result>) dataObject;
			}
			if(viewDataCache == null){
				return null;
			}
			ArrayList<Result> cacheData = new ArrayList<Result>();
//			mDatas.clear();
			if(viewDataCache.mDatas != null){
				cacheData.addAll(viewDataCache.mDatas);
			}
			return cacheData;
		}
		return null;
	}
	
	private void saveDataCache(boolean isValidType){
		if(TextUtils.isEmpty(getGroupKey()) || TextUtils.isEmpty(getKey())){
			LogUtil.e("没有设置key，无法缓存");
			return;
		}
		Object dataTemp = DataCacheManage.getInstance().getData(getGroupKey(), getKey());
		ViewDataCache<Result> viewDataCache = null;
		if(dataTemp instanceof ValidPeriodCache<?>){
			viewDataCache = ((ValidPeriodCache<ViewDataCache<Result>>) dataTemp).getData();
		}if(dataTemp instanceof ViewDataCache){
			viewDataCache = (ViewDataCache<Result>) dataTemp;
		}
		if(viewDataCache == null){
			viewDataCache = new ViewDataCache<Result>();
			if(isValidType){
				Object dataCache = null;
				if(dataCacheTime() == -1){
					 dataCache = new ValidPeriodCache<ViewDataCache<Result>>(viewDataCache);
				}else{
					 dataCache = new ValidPeriodCache<ViewDataCache<Result>>(viewDataCache, dataCacheTime());
				}
				DataCacheManage.getInstance().setData(getGroupKey(), getKey(), new ValidPeriodCache<ViewDataCache<Result>>(viewDataCache));
			}else{
				DataCacheManage.getInstance().setData(getGroupKey(), getKey(), viewDataCache);
			}
		}
		if(viewDataCache.mDatas == null){
			viewDataCache.mDatas = new ArrayList<Result>();
		}
		viewDataCache.mDatas.clear();
		viewDataCache.mDatas.addAll(mDatas);
	}
	
	public void clearDataCache(){
		if(TextUtils.isEmpty(getGroupKey()) || TextUtils.isEmpty(getKey())){
			return;
		}
		Object dataObject = DataCacheManage.getInstance().getData(getGroupKey(), getKey());
		if(dataObject != null){
			ViewDataCache<Result> viewDataCache ;
			if(dataObject instanceof ValidPeriodCache<?>){
				viewDataCache  =  (ViewDataCache<Result>) ((ValidPeriodCache<?>) dataObject).getData();
			}else{
				viewDataCache = (ViewDataCache<Result>) dataObject;
			}
			if(viewDataCache == null){
				return;
			}
			viewDataCache.mDatas.clear();
		}
	}
	protected long dataCacheTime() {
		return -1;
	}
	
	
	protected abstract String getGroupKey();
	
	protected abstract String getKey();
	/**
	 * 是否设置缓存
	 * @return
	 */
	protected abstract boolean isDataCacheEnabled();
	
	/**
	 * 是否设置过期缓存
	 * @return
	 */
	protected abstract boolean isValidDataCacheEnabled();
	
	
	private static class ViewDataCache<Type>{
		private ArrayList<Type> mDatas = new ArrayList<Type>();
	}
	
	/**
	 * 加载当前页数据
	 * @param loadPage
	 * @param pageItemSize
	 * @param params
	 * @return
	 * @throws Exception 如果请求失败必须抛异常
	 */
	protected abstract ArrayList<Result> onLoadCurrentPageData(int loadPage,int pageItemSize,Object... params) throws Exception;
	/**
	 * 一页的项数
	 * @return
	 */
	protected abstract int getPageItemSize();
	/**
	 * 页数
	 * @return 放回空表示未初始化
	 */
	protected abstract Integer getPageSize();
	
	@Override
	@Deprecated
	public final String start(Object... params) {
		return START_RESULT_DEPRECATED;
	}
}
	
