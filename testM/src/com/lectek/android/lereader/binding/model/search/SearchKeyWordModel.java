package com.lectek.android.lereader.binding.model.search;

import java.util.ArrayList;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.lib.cache.DataCacheManage;
import com.lectek.android.lereader.lib.cache.ValidPeriodCache;
import com.lectek.android.lereader.lib.storage.dbase.JsonArrayList;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.KeyWord;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;

/**
 * 搜索界面关键字
 */
public class SearchKeyWordModel extends BaseLoadNetDataModel<ArrayList<KeyWord>> {
		
	public static final String BOOKCITY_SEARCH_KEY_DATA_CACHE_GROUP_KEY = "BOOKCITY_SEARCH_KEY_DATA_CACHE_GROUP_KEY";
	public static final String BOOKCITY_SEARCH_KEY_DATA_CACHE_KEY = "BOOKCITY_SEARCH_KEY_DATA_CACHE_KEY";

	@Override
	protected ArrayList<KeyWord> onLoad(Object... params) throws Exception {
		ArrayList<KeyWord> listCached = getCachedData();
		if(listCached != null && listCached.size() > 0){
			return listCached;
		}else{
			JsonArrayList<KeyWord> list = (JsonArrayList<KeyWord>) ApiProcess4Leyue.getInstance(getContext()).getSearchKeyWords("0", "0", 0, 50);
			String gsonSrt = list.toJsonArray().toString();
			PreferencesUtil.getInstance(getContext()).setSearchKeywords(gsonSrt);
			saveDataCache((ArrayList<KeyWord>)list);
			return list;
		}
	}
	
	public ArrayList<KeyWord> getCachedData(){
		ArrayList<KeyWord> cacheData = null; 
		if(getDataCache() != null && getDataCache().data != null){
			cacheData = new ArrayList<KeyWord>();
			cacheData.addAll(getDataCache().data);
		}
		return cacheData;
	}
	
	/**
	 * 获取缓存
	 * @return
	 */
	public DataCache getDataCache() {
		Object data = DataCacheManage.getInstance().getData(BOOKCITY_SEARCH_KEY_DATA_CACHE_GROUP_KEY,BOOKCITY_SEARCH_KEY_DATA_CACHE_KEY);
		if(data instanceof ValidPeriodCache<?>){
			return ((ValidPeriodCache<DataCache>)data).getData();
		}
		return null;
	}
	
	/**
	 * 缓存数据
	 * @param data
	 */
	public void saveDataCache(ArrayList<KeyWord> data) {
		DataCache dataCache = getDataCache();
		
		if(dataCache == null) {
			dataCache = new DataCache();
			dataCache.data = new ArrayList<KeyWord>();
			DataCacheManage.getInstance().setData(BOOKCITY_SEARCH_KEY_DATA_CACHE_GROUP_KEY,BOOKCITY_SEARCH_KEY_DATA_CACHE_KEY,
														new ValidPeriodCache<DataCache>(dataCache));
		}
		dataCache.data.clear();
		dataCache.data.addAll(data);
	}
	
	private class DataCache{
		ArrayList<KeyWord> data;
	}
}
