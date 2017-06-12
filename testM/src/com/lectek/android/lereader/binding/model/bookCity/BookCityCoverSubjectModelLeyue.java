package com.lectek.android.lereader.binding.model.bookCity;

import java.util.ArrayList;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.lib.cache.DataCacheManage;
import com.lectek.android.lereader.lib.cache.ValidPeriodCache;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.SubjectResultInfo;

public class BookCityCoverSubjectModelLeyue extends	BaseLoadNetDataModel<ArrayList<SubjectResultInfo>>  {

	public static final String BOOKCITY_COVER_SUBJECT_DATA_CACHE_GROUP_KEY = "BOOKCITY_COVER_SUBJECT_DATA_CACHE_GROUP_KEY";
	public static final String BOOKCITY_COVER_SUBJECT_KEY = "BOOKCITY_COVER_SUBJECT_KEY";

	@Override
	protected ArrayList<SubjectResultInfo> onLoad(Object... params) throws Exception {
		ArrayList<SubjectResultInfo> bookCityClassify= ApiProcess4Leyue.getInstance(getContext()).getBookCitySubjectInfo(7, 0, 10);
		saveDataCache(bookCityClassify);
		return bookCityClassify;
	}

	/**
	 * 获取缓存
	 * @return
	 */
	public DataCache getDataCache() {
		Object data = DataCacheManage.getInstance().getData(BOOKCITY_COVER_SUBJECT_DATA_CACHE_GROUP_KEY,BOOKCITY_COVER_SUBJECT_KEY);
		if(data instanceof ValidPeriodCache<?>){
			return ((ValidPeriodCache<DataCache>)data).getData();
		}
		return null;
	}
	
	/**
	 * 缓存数据
	 * @param data
	 */
	public void saveDataCache(ArrayList<SubjectResultInfo> data) {
		DataCache dataCache = getDataCache();
		
		if(dataCache == null) {
			dataCache = new DataCache();
			DataCacheManage.getInstance().setData(BOOKCITY_COVER_SUBJECT_DATA_CACHE_GROUP_KEY,BOOKCITY_COVER_SUBJECT_KEY,
														new ValidPeriodCache<DataCache>(dataCache));
		}
		dataCache.data.clear();
		dataCache.data.addAll(data);
	}
	
	private class DataCache {
		ArrayList<SubjectResultInfo> data;
		
		public DataCache(){
			data = new ArrayList<SubjectResultInfo>();
		}
	}
}
