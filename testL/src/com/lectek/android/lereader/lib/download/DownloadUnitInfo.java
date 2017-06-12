package com.lectek.android.lereader.lib.download;


/**
 * 数据库表实例化对象
 * @author linyiwei
 * @email 21551594@qq.com
 * @date 2011-11-1
 */
public class DownloadUnitInfo {
	public long mID;
	/**
	 * 本地文件存放路径
	 */
	public String mFilePath;
	/**
	 * 文件网络地址
	 */
	public String mDownloadUrl;
	/**
	 * 文件下载或上传状态
	 */
	public int mState;
	/**
	 * 文件总大小
	 */
	public long mFileByteSize;
	/**
	 * 文件当前大小
	 */
	public long mFileByteCurrentSize;
	/**
	 * 文件名
	 */
	public String mFileName;
	/**
	 * 文件动作类型，（如上传或下载  预留）
	 */
	public int mActionType;
	public boolean isDelete = false;
	public DownloadUnitInfo(long mID, String mFilePath, String mDownloadUrl,
			int mState, long mFileByteSize, long mFileByteCurrentSize,
			String mFileName, int actionType,int isDelete) {
		super();
		this.mID = mID;
		this.mFilePath = mFilePath;
		this.mDownloadUrl = mDownloadUrl;
		this.mState = mState;
		this.mFileByteSize = mFileByteSize;
		this.mFileByteCurrentSize = mFileByteCurrentSize;
		this.mFileName = mFileName;
		this.mActionType = actionType;
		if(isDelete == 0)this.isDelete = false;
		if(isDelete == 1)this.isDelete = true;
	}
	public void update(DownloadUnitInfo newData){
		this.mID = newData.mID;
		this.mFilePath = newData.mFilePath;
		this.mDownloadUrl = newData.mDownloadUrl;
		this.mState = newData.mState;
		this.mFileByteSize = newData.mFileByteSize;
		this.mFileByteCurrentSize = newData.mFileByteCurrentSize;
		this.mFileName = newData.mFileName;
		this.mActionType = newData.mActionType;
		this.isDelete = newData.isDelete;
	}
	@Override
	public boolean equals(Object o) {
		if(o instanceof Long){
			if((Long)o == this.mID){
				return true;
			}
		}
		if(o instanceof DownloadUnitInfo){
			DownloadUnitInfo downloadUnit = (DownloadUnitInfo) o;
			if(downloadUnit.mID == this.mID){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
}
