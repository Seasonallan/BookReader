package com.lectek.android.lereader.account.thirdPartApi.entity;

public class OpenId {

	private String mOpenId; // openId
	private String mClientId;

	public OpenId(String openId, String clientId) {
		this.mOpenId = openId;
		this.mClientId = clientId;
	}

	public String getOpenId() {
		return this.mOpenId;
	}

	public void setOpenId(String openId) {
		this.mOpenId = openId;
	}

	public String getClientId() {
		return this.mClientId;
	}

	public void setClientId(String clientId) {
		this.mClientId = clientId;
	}

	public String toString() {
		return this.mOpenId;
	}
}
