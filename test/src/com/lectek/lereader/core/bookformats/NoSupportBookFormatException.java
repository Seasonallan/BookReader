package com.lectek.lereader.core.bookformats;

/** 不支持的书籍格式异常
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-3-14
 */
public class NoSupportBookFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5491953681321808249L;
	
	public NoSupportBookFormatException(String msg){
		super(msg);
	}

}
