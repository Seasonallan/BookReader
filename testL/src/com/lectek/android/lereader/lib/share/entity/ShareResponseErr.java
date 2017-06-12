package com.lectek.android.lereader.lib.share.entity;

/**
 * 
 * 该类定义为微博返回的错误信息
 * code 返回的错误码
 * message  返回的错误信息
 *
 */
public class ShareResponseErr {
	//code  常量
	public final static int UNKNOWN_ERROR = 0x1;//未知失败
	public final static int ERR_USER_CANCEL = 0x2;//用户取消操作
	public final static int ERR_AUTH_DENIED = 0x3;//授权失败
	public final static int FileNotFoundException = 0x4;//FileNotFoundException
	public final static int NullPointerException = 0x5;//FileNotFoundException
	private int code = -1;
	private String message;
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
