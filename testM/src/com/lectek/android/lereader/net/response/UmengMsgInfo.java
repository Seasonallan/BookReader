/*
 * ========================================================
 * ClassName:UmengMsgInfo.java* 
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
 * 2014-2-25     chendt          #00000       create
 */
package com.lectek.android.lereader.net.response;

import java.io.Serializable;

public class UmengMsgInfo implements Serializable{
	private static final long serialVersionUID = -2963394849094574812L;
	/*-----------友盟用户标签------------start--*/
	public static final String TAG_REGISTER_USER = "已注册用户";
	public static final String TAG_TOURIST = "游客";
	public static final String TAG_PALNTFORM_ANDROID = "android平台";
	public static final String TAG_PALNTFORM_IOS = "ios平台";
	public static final String TAG_HAS_ORDER_RECORD = "有订购记录";
	public static final String TAG_NOT_ORDER_RECORD = "无订购记录";
	public static final String SEX_MALE = "男";
	public static final String SEX_FEMAIL = "女";
	/*-----------友盟用户标签------------end--*/
	/**消息id*/
	private String msgId;
	
	/**消息json信息体,通过 {@link #NotifyCustomInfo} 进行Gson映射解析*/
	private String msgJsonStr;
	
	/**创建时间*/
	private String msgCreateTime;
	
	/**消息状态*/
	private int msgStatus;
	

	public UmengMsgInfo(String msgId, String msgJsonStr, String msgCreateTime,
			int msgStatus) {
		super();
		this.msgId = msgId;
		this.msgJsonStr = msgJsonStr;
		this.msgCreateTime = msgCreateTime;
		this.msgStatus = msgStatus;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getMsgJsonStr() {
		return msgJsonStr;
	}

	public void setMsgJsonStr(String msgJsonStr) {
		this.msgJsonStr = msgJsonStr;
	}

	public String getMsgCreateTime() {
		return msgCreateTime;
	}

	public void setMsgCreateTime(String msgCreateTime) {
		this.msgCreateTime = msgCreateTime;
	}

	public int getMsgStatus() {
		return msgStatus;
	}

	public void setMsgStatus(int msgStatus) {
		this.msgStatus = msgStatus;
	}
	
}
