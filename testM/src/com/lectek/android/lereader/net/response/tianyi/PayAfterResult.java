package com.lectek.android.lereader.net.response.tianyi;

import android.text.TextUtils;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;


public class PayAfterResult extends BaseDao {

	public static final long serialVersionUID = 3047954165905673964L;
//	{"resultCode":"1","resultDescription":"success"} 话费按本支付成功返回结果
	@Json(name ="resultCode")
	public int payResultCode;
	
	@Json(name ="resultDescription")
	public String resultDescription;

	/**天翼阅读开放平台_错误描述*/
	@Json(name = "msg")
	public String surfingMsg;
	/**天翼阅读开放平台_错误码*/
	@Json(name = "code")
	public int surfingCode = -1;
	public int getResultCode() {
		if (payResultCode!=0) {
			return payResultCode;
		}
		return getSurfingCode();
	}

	public String getMessge(){
		if (!TextUtils.isEmpty(resultDescription)) {
			return resultDescription;
		}
		return surfingMsg;
	}


	public int getSurfingCode() {
		return surfingCode;
	}

	public void setSurfingCode(int surfingCode) {
		this.surfingCode = surfingCode;
	}


	public void setSurfingMsg(String surfingMsg) {
		this.surfingMsg = surfingMsg;
	}
	
	
	public int getPayResultCode() {
		return payResultCode;
	}

	public void setPayResultCode(int payResultCode) {
		this.payResultCode = payResultCode;
	}

	public String getResultDescription() {
		return resultDescription;
	}

	public void setResultDescription(String resultDescription) {
		this.resultDescription = resultDescription;
	}

	@Override
	public String toString() {
		return "PayAfterResult [resultCode=" + getResultCode() + " , msg= "+getMessge()+"]";
	}
	
}
