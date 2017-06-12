package com.lectek.android.lereader.net.response;


import java.io.Serializable;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 *  版本更新HTTP数据结构
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public class UpdateInfo extends BaseDao implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2276583938696033951L;

	@Json(name = "clientName")
	public String clientName;//客服端名称
	
	@Json(name = "versionNum")
	public int versionCode;//升级版本号（1,2,3,4 ...递增）
	
	@Json(name = "deviceType")
	public String deviceType;//获取设备名，预留，目前不使用
	
	@Json(name = "versionIconUrl")
	public String versionIconUrl;//获取升级文件图标，目前没有使用
	
	@Json(name = "isForce")
	public boolean mustUpdate;//是否需要强制升级，true表达强制升级
	
	@Json(name = "versionMemo")
	public String updateMessage;//获取升级最新版本的说明
	
	public long updateSize;
	
	@Json(name = "versionUrl")
	public String updateURL;//获取升级文件，这里通常是apk文件
	@Json(name = "versionCode")
	public String updateVersion;//对应manifest中 的 versionName
	
	public String currentVersion;//当前  versionName
	
	public UpdateInfo(){
		
	}
	
	public UpdateInfo(boolean mustUpdate, String updateMessage, long updateSize,
			String updateURL, String updateVersion, String currentVersion,
			String clientName,int versionCode) {
		super();
		this.mustUpdate = mustUpdate;
		this.updateMessage = updateMessage;
		this.updateSize = updateSize;
		this.updateURL = updateURL;
		this.updateVersion = updateVersion;
		this.currentVersion = currentVersion;
		this.clientName = clientName;
		this.versionCode = versionCode;
	}
	/**
	 * @param mustUpdate the mustUpdate to set
	 */
	void setMustUpdate(boolean mustUpdate) {
		this.mustUpdate = mustUpdate;
	}

	/**
	 * @param updateMessage the updateMessage to set
	 */
	public void setUpdateMessage(String updateMessage) {
		this.updateMessage = updateMessage;
	}

	/**
	 * @param updateSize the updateSize to set
	 */
	void setUpdateSize(long updateSize) {
		this.updateSize = updateSize;
	}

	/**
	 * @param updateURL the updateURL to set
	 */
	void setUpdateURL(String updateURL) {
		this.updateURL = updateURL;
	}

	/**
	 * @param updateVersion the updateVersion to set
	 */
	void setUpdateVersion(String updateVersion) {
		this.updateVersion = updateVersion;
	}

	/**
	 * @param currentVersion the currentVersion to set
	 */
	void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}

	/**
	 * @return the mustUpdate
	 */
	public boolean isMustUpdate() {
		return mustUpdate;
	}

	/**
	 * @return the updateMessage
	 */
	public String getUpdateMessage() {
		return updateMessage;
	}

	/**
	 * @return the updateSize
	 */
	public long getUpdateSize() {
		return updateSize;
	}

	/**
	 * @return the updateURL
	 */
	public String getUpdateURL() {
		return updateURL;
	}

	/**
	 * @return the updateVersion
	 */
	public String getUpdateVersion() {
		return updateVersion;
	}

	/**
	 * @return the currentVersion
	 */
	public String getCurrentVersion() {
		return currentVersion;
	}
	
	
	
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getVersionIconUrl() {
		return versionIconUrl;
	}
	public void setVersionIconUrl(String versionIconUrl) {
		this.versionIconUrl = versionIconUrl;
	}
	
	
	@Override
	public String toString() {
		return "UpdateInfo [clientName=" + clientName + ", versionCode="
				+ versionCode + ", deviceType=" + deviceType
				+ ", versionIconUrl=" + versionIconUrl + ", mustUpdate="
				+ mustUpdate + ", updateMessage=" + updateMessage
				+ ", updateSize=" + updateSize + ", updateURL=" + updateURL
				+ ", updateVersion=" + updateVersion + ", currentVersion="
				+ currentVersion + "]";
	}

}
