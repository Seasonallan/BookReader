package com.lectek.android.lereader.net.response.tianyi;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**　购买结果
 * @author mingkg21
 * @email  mingkg21@gmail.com
 * @date   2013-8-23
 */
public class OrderedResult  extends BaseDao {
	
//	public int code = -1;

    @Json(name ="message")
	public String message;

	/**天翼阅读开放平台_错误描述*/
	@Json(name = "msg")
	public String surfingMsg;
	/**天翼阅读开放平台_错误码*/
	@Json(name = "code")
	public int surfingCode = -1;
	/**
	 * @return the code
	 */
	public int getCode() {
		return getSurfingCode();
	}

	public String getSurfingMsg() {
		return surfingMsg;
	}

	public void setSurfingMsg(String surfingMsg) {
		this.surfingMsg = surfingMsg;
	}

	public int getSurfingCode() {
		return surfingCode;
	}

	public void setSurfingCode(int surfingCode) {
		this.surfingCode = surfingCode;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.setSurfingCode(code);
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
