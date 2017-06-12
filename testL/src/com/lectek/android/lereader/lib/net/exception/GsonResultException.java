/*
 * ========================================================
 * ClassName:GsonResultException.java* 
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

import com.lectek.android.lereader.lib.net.ResponseResultCode;



/**
 * @description
	处理Gson 解析的 异常和错误码。 外部接收 根据场景需要，对返回字符串选择性提示。
	<br/>根据 
	<code><br/>
	try{}
	catch(GsonResultException e){
		<br/> String targetStr = ResultCodeControl.getResultString(mContext, e.getmResponseInfo().getErrorCode());
	<br/>}
	</code>
 * @author chendt
 * @note:注意—— 错误码常量类不同，所以onPostFail 调用时注意，调用正确的返回提示语。
 * @date 2013-9-26
 * @Version 1.0
 */
public class GsonResultException extends Exception{
	private ErrorMessage mResponseInfo;
	private static final long serialVersionUID = 4587686951878327870L;
	private GsonErrorEnum mEnum;
	public enum GsonErrorEnum{
		/**服务器连接失败*/
		CONNET_FAIL_EXCEPTION,
		
		/**服务器返回错误*/
		SERVICE_RESPONSE_ERROR_EXCEPTION,
		
		/**GSON解析错误*/
		GSON_ERROR_EXCEPTION
	}
	
	/**
	 * @param errorEnum 枚举类型
	 * @param info 服务器返回的错误码信息；如果为服务器连接失败，或者解析异常。则传null
	 */
	public GsonResultException(GsonErrorEnum errorEnum,ErrorMessage info){
		switch (errorEnum) {
		case CONNET_FAIL_EXCEPTION:
			mEnum = GsonErrorEnum.CONNET_FAIL_EXCEPTION;
			mResponseInfo = new ErrorMessage();
			mResponseInfo.setErrorCode(ResponseResultCode.STATUS_FAILD);
			mResponseInfo.setErrorDescription("服务器不给力，请稍后再试！");
			mResponseInfo.setSurfingMsg("服务器不给力，请稍后再试！");
			break;
		case SERVICE_RESPONSE_ERROR_EXCEPTION:
			mEnum = GsonErrorEnum.SERVICE_RESPONSE_ERROR_EXCEPTION;
			mResponseInfo = info;
			break;
		case GSON_ERROR_EXCEPTION:
			mEnum = GsonErrorEnum.GSON_ERROR_EXCEPTION;
			mResponseInfo = new ErrorMessage();
			mResponseInfo.setErrorDescription("Gson解析异常！");
			mResponseInfo.setSurfingMsg("Gson解析异常！");
			break;
			
		default:
			break;
		}
	}

	public GsonErrorEnum getEnum() {
		return mEnum;
	}

	public void setEnum(GsonErrorEnum mEnum) {
		this.mEnum = mEnum;
	}

	public ErrorMessage getResponseInfo() {
		return mResponseInfo;
	}

	public void setResponseInfo(ErrorMessage mResponseInfo) {
		this.mResponseInfo = mResponseInfo;
	}
	
}
