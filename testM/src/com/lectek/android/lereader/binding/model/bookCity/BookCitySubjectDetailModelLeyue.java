package com.lectek.android.lereader.binding.model.bookCity;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.lib.cache.DataCacheManage;
import com.lectek.android.lereader.lib.cache.ValidPeriodCache;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.SubjectDetailResultInfo;

public class BookCitySubjectDetailModelLeyue extends BaseLoadNetDataModel<SubjectDetailResultInfo> {

	private int mSubjectId;
	public static final String BOOKCITY_SUBJECT_DERAIL_DATA_CACHE_GROUP_KEY = "BOOKCITY_SUBJECT_DERAIL_DATA_CACHE_GROUP_KEY";
	
	public BookCitySubjectDetailModelLeyue(int subjectid) {
		super();
		this.mSubjectId = subjectid;
	}

	@Override
	protected SubjectDetailResultInfo onLoad(Object... params)
			throws Exception {
		SubjectDetailResultInfo info = (SubjectDetailResultInfo)ApiProcess4Leyue.getInstance(getContext()).getBookCitySubjectClassifyDetail(mSubjectId, 0, 1000);
//		CommonEntiyPaser<ContentInfoLeyue> paser = new CommonEntiyPaser<ContentInfoLeyue>();
//		ArrayList<ContentInfoLeyue> list = paser.parseLeyueListEntity(info.bookList, ContentInfoLeyue.class);
		saveDataCache(info,mSubjectId);
		return info;
	}

	private DataCache getDataCache(int subjectId) {
		Object data = DataCacheManage.getInstance().getData(BOOKCITY_SUBJECT_DERAIL_DATA_CACHE_GROUP_KEY, String.format("%s", subjectId));
		if(data instanceof ValidPeriodCache<?>){
			return ((ValidPeriodCache<DataCache>)data).getData();
		}
		return null;
	}
	
	private void saveDataCache(SubjectDetailResultInfo data, int subjectId) {
		DataCache dataCache = getDataCache(subjectId);
		
		if(dataCache == null) {
			dataCache = new DataCache();
			dataCache.data = new SubjectDetailResultInfo();
			DataCacheManage.getInstance().setData(BOOKCITY_SUBJECT_DERAIL_DATA_CACHE_GROUP_KEY, String.format("%s", subjectId),
														new ValidPeriodCache<DataCache>(dataCache));
		}
		dataCache.data = data;
	}
	
	private class DataCache {
		SubjectDetailResultInfo data;
	}
}
