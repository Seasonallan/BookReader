package com.lectek.android.lereader.account.thirdPartApi.entity;

public class UserInfo {
	private String mNickName;
	private String mIcon_30;
	private String mIcon_50;
	private String mIcon_100;

	public UserInfo(String nickName, String icon_30, String icon_50,
			String icon_100) {
		this.mNickName = nickName;
		this.mIcon_30 = icon_30;
		this.mIcon_50 = icon_50;
		this.mIcon_100 = icon_100;
	}

	public String getNickName() {
		return this.mNickName;
	}

	public void setNickName(String nickName) {
		this.mNickName = nickName;
	}

	public String getIcon_30() {
		return this.mIcon_30;
	}

	public void setIcon_30(String icon_30) {
		this.mIcon_30 = icon_30;
	}

	public String getIcon_50() {
		return this.mIcon_50;
	}

	public void setIcon_50(String icon_50) {
		this.mIcon_50 = icon_50;
	}

	public String getIcon_100() {
		return this.mIcon_100;
	}

	public void setIcon_100(String icon_100) {
		this.mIcon_100 = icon_100;
	}

	public String toString() {
		return "nickname: " + this.mNickName + "\nicon_30: " + this.mIcon_30
				+ "\nicon_50: " + this.mIcon_50 + "\nicon_100: "
				+ this.mIcon_100 + "\n";
	}
}
