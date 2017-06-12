package com.lectek.android.lereader.binding.model.bookCityWelfare;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.lib.cache.DataCacheManage;
import com.lectek.android.lereader.lib.cache.ValidPeriodCache;
import com.lectek.android.lereader.net.ApiProcess4Leyue;

public class BookCityWelfareDataModelLeyue extends BaseLoadNetDataModel<BookCityWelfareData> {

	public static final String BOOKCITY_WELFARE_DATA_CACHE_GROUP_KEY = "BOOKCITY_WELFARE_DATA_CACHE_GROUP_KEY";
	public static final String BOOKCITY_WELFARE_DATA_CACHE_KEY = "BOOKCITY_WELFARE_DATA_CACHE_KEY";

	@Override
	protected BookCityWelfareData onLoad(Object... params) throws Exception {
		BookCityWelfareData mBookCityWelfareData = getCachedData();
		
		if(mBookCityWelfareData == null || (Boolean)params[0] == true){
			
			if(mBookCityWelfareData == null)
				mBookCityWelfareData = new BookCityWelfareData();
			
			mBookCityWelfareData.mWelfareSubjectList = ApiProcess4Leyue.getInstance(getContext()).getBookCitySubjectInfo(3, 0, 10);
			
			mBookCityWelfareData.mFreeLimitBooksList = ApiProcess4Leyue.getInstance(getContext()).getBookCityFreeLimitSubject(0,1);
			
			mBookCityWelfareData.mFreeBooksList = ApiProcess4Leyue.getInstance(getContext()).getBookCityFreeSubject(0,9);
			
			mBookCityWelfareData.mLatestSpecialOfferList = ApiProcess4Leyue.getInstance(getContext()).getBookCityLatestSpecialOfferSubject(0,9);
			
			saveDataCache(mBookCityWelfareData);
		}
		return mBookCityWelfareData;
	}

	public BookCityWelfareData getCachedData(){
		return getDataCache() != null? getDataCache().data : null;
	}
	/**
	 * 获取缓存
	 * @return
	 */
	public DataCache getDataCache() {
		Object data = DataCacheManage.getInstance().getData(BOOKCITY_WELFARE_DATA_CACHE_GROUP_KEY,BOOKCITY_WELFARE_DATA_CACHE_KEY);
		if(data instanceof ValidPeriodCache<?>){
			return ((ValidPeriodCache<DataCache>)data).getData();
		}
		return null;
	}
	
	/**
	 * 缓存数据
	 * @param data
	 */
	public void saveDataCache(BookCityWelfareData data) {
		DataCache dataCache = getDataCache();
		
		if(dataCache == null) {
			dataCache = new DataCache();
			dataCache.data = new BookCityWelfareData();
			DataCacheManage.getInstance().setData(BOOKCITY_WELFARE_DATA_CACHE_GROUP_KEY,BOOKCITY_WELFARE_DATA_CACHE_KEY,
														new ValidPeriodCache<DataCache>(dataCache));
		}
		dataCache.data = data;
	}
	
	private class DataCache {
		BookCityWelfareData data;
	}
}
