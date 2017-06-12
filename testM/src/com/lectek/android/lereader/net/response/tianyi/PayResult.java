package com.lectek.android.lereader.net.response.tianyi;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/** 话费的结果处理
 * @author mingkg21
 * @email  mingkg21@gmail.com
 * @date   2013-8-26
 */
public class PayResult extends BaseDao{

	/**天翼阅读开放平台_错误码*/
	@Json(name = "code")
	public int surfingCode = -1;
	
	/**天翼阅读开放平台_错误描述*/
	@Json(name = "msg")
	public String surfingMsg;
	@Json(name ="order_url")
	public String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getSurfingCode() {
		return surfingCode;
	}

	public void setSurfingCode(int surfingCode) {
		this.surfingCode = surfingCode;
	}

	public String getSurfingMsg() {
		return surfingMsg;
	}

	public void setSurfingMsg(String surfingMsg) {
		this.surfingMsg = surfingMsg;
	}
	
	@Override
	public String toString() {
		return "PayResult [content=" + content + "]";
	}
	
}
