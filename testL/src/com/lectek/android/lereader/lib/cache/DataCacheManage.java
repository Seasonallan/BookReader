package com.lectek.android.lereader.lib.cache;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class DataCacheManage {
	private static DataCacheManage this_;
	private HashMap<String, HashMap<String, Object>> mCacheData;
	private String mLockGroupKey;
	private String mLockKey;
	
	private DataCacheManage(){
		mCacheData = new HashMap<String, HashMap<String, Object>>();
	}
	
	public static DataCacheManage getInstance(){
		if(this_ == null){
			this_ = new DataCacheManage();
		}
		return this_;
	}
	
	public void lockDataCache(String groupKey,String key){
		if(checkLock(groupKey,key)){
			return;
		}
		if(mLockGroupKey != null || mLockKey != null){
			unlockDataCache(mLockGroupKey,mLockKey);
		}
		mLockGroupKey = groupKey;
		mLockKey = key;
		Object sourceData = getSourceData(mLockGroupKey,mLockKey);
		if(sourceData != null && sourceData instanceof SoftReference && ((SoftReference<Object>) sourceData).get() != null){
			setData(mLockGroupKey, mLockKey, ((SoftReference<Object>) sourceData).get());
		}
	}
	
	public void unlockDataCache(String groupKey,String key){
		mLockGroupKey = null;
		mLockKey = null;
		Object sourceData = getSourceData(groupKey,key);
		if(sourceData != null && !( sourceData instanceof SoftReference )){
			setData(groupKey, key, sourceData);
		}
	}
	
	private Object getSourceData(String groupKey,String key){
		Object sourceData = null;
		HashMap<String,Object> groupData = mCacheData.get(groupKey);
		if(groupData != null){
			sourceData = groupData.get(key);
		}
		return sourceData;
	}
	
	public Object getData(String groupKey,String key){
		Object sourceData = getSourceData(groupKey,key);
		Object data = getData(sourceData);
		return data;
	}
	
	private Object getData(Object sourceData){
		Object data = null;
		if(sourceData != null){
			if(sourceData instanceof SoftReference){
				SoftReference<Object> dataReference = (SoftReference<Object>) sourceData;
				data = dataReference.get();
			}else{
				data = sourceData;
			}
		}
		return data;
	}
	
	public ArrayList<Object> getGroupData(String groupKey){
		ArrayList<Object> datas = new ArrayList<Object>();
		HashMap<String, Object> groupData = mCacheData.get(groupKey);
		if(groupData != null){
			for(Object sourceData : groupData.values()){
				sourceData = getData(sourceData);
				if(sourceData != null){
					datas.add(sourceData);
				}
			}
		}
		return datas;
	}
	
	public void setData(String groupKey,String key,Object data){
		HashMap<String, Object> groupData = mCacheData.get(groupKey);
		if(groupData == null){
			groupData = new HashMap<String, Object>();
			mCacheData.put(groupKey, groupData);
		}
		if(checkLock(groupKey,key)){
			groupData.put(key, data);
		}else{
			groupData.put(key, new SoftReference<Object>(data));
		}
	}
	
	private boolean checkLock(String groupKey,String key){
//		if(groupKey.equals(mLockGroupKey) && key.equals(mLockKey)){
//			return true;
//		}
//		return false;
		return true;
	}
	
	private void clearData(Object sourceData){
		if(sourceData != null){
			if(sourceData instanceof SoftReference){
				SoftReference<Object> dataReference = (SoftReference<Object>) sourceData;
				dataReference.clear();
			}
		}
	}
	
	public void clearData(String groupKey,String key){
		HashMap<String, Object> groupData = mCacheData.get(groupKey);
		if(groupData != null){
			Object sourceData = groupData.remove(key);
			clearData(sourceData);
		}
	}
	/**
	 * 清除ValidPeriodCache类型过期数据
	 */
	public void clearPastDueData(String groupKey){
		HashMap<String,Object> groupData = mCacheData.get(groupKey);
		if(groupData != null){
			ArrayList<Object> tempData = new ArrayList<Object>();
			tempData.addAll(groupData.values());
			Object obj = null;
			for(Object sourceData : tempData){
				obj = getData(sourceData);
				if(obj instanceof ValidPeriodCache<?>){
					if( ((ValidPeriodCache<?>) obj).isPastDue() ){
						groupData.remove(sourceData);
					}
				}
			}
		}
	}
	
	public void clearData(String groupKey){
		HashMap<String,Object> groupData = mCacheData.get(groupKey);
		if(groupData != null){
			for(Entry<String,Object> data : groupData.entrySet()){
				Object sourceData = data.getValue();
				clearData(sourceData);
			}
			groupData.clear();
			mCacheData.remove(groupKey);
		}
	}
	
	public void clearAllData(){
		for(Entry<String, HashMap<String, Object>> groupDatas : mCacheData.entrySet()){
			HashMap<String,Object> groupData = groupDatas.getValue();
			if(groupData != null){
				for(Entry<String,Object> datas : groupData.entrySet()){
					Object sourceData = datas.getValue();
					clearData(sourceData);
				}
				groupData.clear();
			}
		}
		mCacheData.clear();
	}
	
	public boolean contains(String groupKey,String key){
		HashMap<String, Object> groupData = mCacheData.get(groupKey);
		if(groupData != null){
			Object sourceData = getSourceData(groupKey, key);
			if(sourceData != null){
				if(sourceData instanceof SoftReference){
					SoftReference<Object> dataReference = (SoftReference<Object>) sourceData;
					if(dataReference.get() != null){
						return true;
					}
				}else{
					return true;
				}
			}
		}
		return false;
	}
	
}
