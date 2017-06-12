package com.lectek.android.lereader.lib.cache;

/**
 * 超时机制数据缓存
 * @author linyiwei
 * @param <Data>
 */
public class ValidPeriodCache<Data> {
	private static final long DEFAULT_EFFECTIVE_TIME = 1000 * 60 * 20;
	private Data data;
	private long creationTime;
	private long validPeriodTime;
	private boolean isNeedRefresh;
	
	public ValidPeriodCache(Data data){
		this(data,DEFAULT_EFFECTIVE_TIME,false);
	}
	
	public ValidPeriodCache(Data data, boolean isNeedRefresh){
		this(data,DEFAULT_EFFECTIVE_TIME,isNeedRefresh);
	}
	
	public ValidPeriodCache(Data data,long validPeriodTime){
		this(data,validPeriodTime,false);
	}
	/**
	 * 
	 * @param data 数据
	 * @param validPeriodTime 有效期限
	 */
	public ValidPeriodCache(Data data,long validPeriodTime, boolean isNeedRefresh){
		setData(data);
		setValidPeriodTime(validPeriodTime);
		setCreationTime(System.currentTimeMillis());
		setNeedRefresh(isNeedRefresh);
	}
	/**
	 * 是否过期,如果发现当前时间比创建时间还早，认为用户修改时间直接视为失效
	 * @return
	 */
	public boolean isPastDue(){
		long validPeriod = getCreationTime() + getValidPeriodTime();
		return validPeriod < System.currentTimeMillis() || getCreationTime() > System.currentTimeMillis();
	}
	/**
	 * 获取有效数据
	 * @return the data
	 */
	public Data getValidData() {
		return getValidData(false);
	}
	/**
	 * 获取有效数据
	 * @param isClearUnValidData 如果数据过期是否自动清除
	 * @return
	 */
	public Data getValidData(boolean isClearUnValidData) {
		if(data != null && !isPastDue()){
			return data;
		}else{
			if(isClearUnValidData){
				data = null;
			}
		}
		return null;
	}
	/**
	 * 获取保存过的数据
	 * @return the data
	 */
	public Data getData() {
		return data;
	}
	/**
	 * 设置数据、并自动保存当前创建时间
	 * @param data the data to set
	 */
	public void setData(Data data) {
		setCreationTime(System.currentTimeMillis());
		this.data = data;
	}
	/**
	 * 获取创建时间
	 * @return the creationTime
	 */
	public long getCreationTime() {
		return creationTime;
	}
	/**
	 * 设置创建时间
	 * @param creationTime the creationTime to set
	 */
	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}
	/**
	 * 获取有效期时长
	 * @return the effectiveTime
	 */
	public long getValidPeriodTime() {
		return validPeriodTime;
	}
	/**
	 * 设置有效期时长
	 * @param effectiveTime the effectiveTime to set
	 */
	public void setValidPeriodTime(long validPeriod) {
		this.validPeriodTime = validPeriod;
	}
	
	public boolean isNeedRefresh() {
		return isNeedRefresh;
	}
	
	public void setNeedRefresh(boolean isNeedRefresh) {
		this.isNeedRefresh = isNeedRefresh;
	}
	
}
