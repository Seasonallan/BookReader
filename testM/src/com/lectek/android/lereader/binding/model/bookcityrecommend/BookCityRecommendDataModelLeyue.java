package com.lectek.android.lereader.binding.model.bookcityrecommend;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.lib.cache.DataCacheManage;
import com.lectek.android.lereader.lib.cache.ValidPeriodCache;
import com.lectek.android.lereader.net.ApiProcess4Leyue;

public class BookCityRecommendDataModelLeyue extends BaseLoadNetDataModel<BookCityRecommendData> {

	public static final String BOOKCITY_RECOMMEND_DATA_CACHE_GROUP_KEY = "BOOKCITY_RECOMMEND_DATA_CACHE_GROUP_KEY";
	public static final String BOOKCITY_RECOMMEND_DATA_CACHE_KEY = "BOOKCITY_RECOMMEND_DATA_CACHE_KEY";

	@Override
	protected BookCityRecommendData onLoad(Object... params) throws Exception {
		BookCityRecommendData mBookCityRecommendData = getCachedData();
		if(mBookCityRecommendData == null || (Boolean)params[0] == true){
			
			if(mBookCityRecommendData == null)
				mBookCityRecommendData = new BookCityRecommendData();
			
			mBookCityRecommendData.mRecommendSubjectList = ApiProcess4Leyue.getInstance(getContext()).getBookCitySubjectInfo(6, 0, 20);
			
			mBookCityRecommendData.mEditorRecommendSubjectList = ApiProcess4Leyue.getInstance(getContext()).getBookCitySubjectInfo(1, 0, 8);
			
			mBookCityRecommendData.mHeavyRecommendList = ApiProcess4Leyue.getInstance(getContext()).getBookCitySubjectHeavyRecommend(100001,0,3);
			
			mBookCityRecommendData.mNewBookRecommendList = ApiProcess4Leyue
					.getInstance(getContext()).getBookCityNewBookRecommend(100002,0,9);
			
			mBookCityRecommendData.mAllLoveRecommendList = ApiProcess4Leyue.getInstance(getContext()).getBookCitySubjectHeavyRecommend(100009,0,9);
			saveDataCache(mBookCityRecommendData);
		}
		return mBookCityRecommendData;
	}

	public BookCityRecommendData getCachedData(){
		return getDataCache() != null? getDataCache().data : null;
	}
	/**
	 * 获取缓存
	 * @return
	 */
	public DataCache getDataCache() {
		Object data = DataCacheManage.getInstance().getData(BOOKCITY_RECOMMEND_DATA_CACHE_GROUP_KEY,BOOKCITY_RECOMMEND_DATA_CACHE_KEY);
		if(data instanceof ValidPeriodCache<?>){
			return ((ValidPeriodCache<DataCache>)data).getData();
		}
		return null;
	}
	
	/**
	 * 缓存数据
	 * @param data
	 */
	public void saveDataCache(BookCityRecommendData data) {
		DataCache dataCache = getDataCache();
		
		if(dataCache == null) {
			dataCache = new DataCache();
			dataCache.data = new BookCityRecommendData();
			DataCacheManage.getInstance().setData(BOOKCITY_RECOMMEND_DATA_CACHE_GROUP_KEY,BOOKCITY_RECOMMEND_DATA_CACHE_KEY,
														new ValidPeriodCache<DataCache>(dataCache));
		}
		dataCache.data = data;
	}
	
	private class DataCache {
		BookCityRecommendData data;
	}
}
