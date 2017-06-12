package com.lectek.android.lereader.binding.model.bookCityClassify;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.lib.cache.DataCacheManage;
import com.lectek.android.lereader.lib.cache.ValidPeriodCache;
import com.lectek.android.lereader.net.ApiProcess4Leyue;

public class BookCityClassifyDataModelLeyue extends BaseLoadNetDataModel<BookCityClassifyData> {

	public static final String BOOKCITY_CLASSIFY_DATA_CACHE_GROUP_KEY = "BOOKCITY_CLASSIFY_DATA_CACHE_GROUP_KEY";
	public static final String BOOKCITY_CLASSIFY_DATA_CACHE_KEY = "BOOKCITY_CLASSIFY_DATA_CACHE_KEY";

	@Override
	protected BookCityClassifyData onLoad(Object... params) throws Exception {
		BookCityClassifyData mBookCityClassifyData;
		mBookCityClassifyData = getCachedData();
		if(mBookCityClassifyData == null || (Boolean)params[0] == true){
			if(mBookCityClassifyData == null)
				mBookCityClassifyData = new BookCityClassifyData();
			mBookCityClassifyData.mClassifyInfoList = ApiProcess4Leyue.getInstance(getContext()).getBookCityClassify(100000);
			mBookCityClassifyData.mClassifySubjectList= ApiProcess4Leyue.getInstance(getContext()).getBookCitySubjectInfo(2, 0, 10);
			saveDataCache(mBookCityClassifyData);
		}
		return mBookCityClassifyData;
	}

	public BookCityClassifyData getCachedData(){
		return getDataCache() != null? getDataCache().data : null;
	}
	/**
	 * 获取缓存
	 * @return
	 */
	public DataCache getDataCache() {
		Object data = DataCacheManage.getInstance().getData(BOOKCITY_CLASSIFY_DATA_CACHE_GROUP_KEY,BOOKCITY_CLASSIFY_DATA_CACHE_KEY);
		if(data instanceof ValidPeriodCache<?>){
			return ((ValidPeriodCache<DataCache>)data).getData();
		}
		return null;
	}
	
	/**
	 * 缓存数据
	 * @param data
	 */
	public void saveDataCache(BookCityClassifyData data) {
		DataCache dataCache = getDataCache();
		
		if(dataCache == null) {
			dataCache = new DataCache();
			dataCache.data = new BookCityClassifyData();
			DataCacheManage.getInstance().setData(BOOKCITY_CLASSIFY_DATA_CACHE_GROUP_KEY,BOOKCITY_CLASSIFY_DATA_CACHE_KEY,
														new ValidPeriodCache<DataCache>(dataCache));
		}
		dataCache.data = data;
	}
	
	private class DataCache {
		BookCityClassifyData data;
	}
}
