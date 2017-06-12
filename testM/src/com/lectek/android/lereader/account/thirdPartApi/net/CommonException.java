package com.lectek.android.lereader.account.thirdPartApi.net;

public class CommonException extends Throwable{
	private static final long serialVersionUID = 1L;
	private int mErrorCode = 0;
	private String mErrorType;

	public CommonException(String message){
	    super(message);
	}

	public CommonException(String message, String type, int code) {
	    super(message);
	    this.mErrorType = type;
	    this.mErrorCode = code;
	}

	public int getErrorCode() {
	    return this.mErrorCode;
	}

	public String getErrorType() {
	    return this.mErrorType;
	}
}
