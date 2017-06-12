package com.lectek.android.lereader.account;
/**
 * 用户类型enum
 * @author wuwq
 *
 */
public enum AccountType implements ICodeEnum{
	VISITOR(1,"游客"),
	LEYUE(2,"乐阅"),
	SINA(3,"新浪"),
	QQ(4,"腾讯"),
	TIANYI(5,"天翼"),
	WEIXIN(6,"微信")
	;

	private int code;
	private String desc;
	private AccountType(int code,String desc) {
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
