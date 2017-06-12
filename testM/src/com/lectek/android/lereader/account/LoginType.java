package com.lectek.android.lereader.account;

public enum LoginType  implements ICodeEnum{
	AUTO_LOGIN(1,"自动登录"),
	CHANGE_LOGIN(2,"切换账号登录"),
	BASE_LOGIN(3,"普通登录"),
	PAY_LOGIN(4,"购买时登录"),
	PAGE_LOGIN(5,"跳转到登录界面")
	;

	private int code;
	private String desc;
	private LoginType(int code,String desc) {
		this.code = code;
		this.desc = desc;
	}
	@Override
	public int getCode() {
		return code;
	}

	@Override
	public String getDesc() {
		return desc;
	}

}
