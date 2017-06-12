/*
 * ========================================================
 * ClassName:ResponseInfo.java* 
 * Description:
 * Copyright (C) 
 * ========================================================
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *===================================================================*
 * Revision History
 *
 * Modification                    Tracking
 * Date         Author          Number       Description of changes
 *____________________________________________________________________
 * 
 * 2013-9-26     chendt          #00000       create
 */
package com.lectek.android.lereader.lib.net.exception;

import java.io.Serializable;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;
/**
 *  服务器返回错误码HTTP数据结构
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public class ErrorMessage extends BaseDao implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2641479511954723692L;

	@Json(name = "error_code")
	public String errorCode;//错误码
	
	@Json(name = "request")
	public String request;//请求的相对 Url
	
	@Json(name = "error")
	public String errorDescription;//错误描述
	
	/**天翼阅读开放平台_错误码*/
	@Json(name = "code")
	public int surfingCode = -1;
	
	/**天翼阅读开放平台_错误描述*/
	@Json(name = "msg")
	public String surfingMsg;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
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
		return "ErrorMessage [errorCode=" + errorCode + ", request="
				+ request + ", errorDescription=" + errorDescription
				+ ", surfingCode=" + surfingCode + ", surfingMsg=" + surfingMsg
				+ "]";
	}
	
}
