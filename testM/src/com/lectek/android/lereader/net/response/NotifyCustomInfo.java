/*
 * ========================================================
 * ClassName:NotifyCustomInfo.java* 
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

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 * @description
	友盟推送能力，自定义消息 解析内容。
 * @author chendt
 * @date 2014-2-25
 * @Version 1.0
 */
public class NotifyCustomInfo extends BaseDao implements Serializable{

	public static final long serialVersionUID = -2602018113138135301L;
	/**书籍推荐*/
	public final static int TYPE_BOOK = 0x1;
	/**专题活动推荐*/
	public final static int TYPE_SUBJECT = 0x2;
	/**我的消息*/
	public final static int TYPE_MY_INFO = 0x3;
	
	/**消息标题*/
	@Json(name = "title")
	public String msgTitle;
	
	/**消息描述*/
	@Json(name = "description")
	public String msgDecription;
	
	/**消息自定义参数*/
	@Json(name = "args")
	public String msgArgs;
	
	/**消息类型</br>
	 * {@link NotifyCustomInfo#TYPE_BOOK},{@link NotifyCustomInfo#TYPE_SUBJECT}
	 * {@link #TYPE_MY_INFO}*/
	@Json(name = "type")
	public int msgType;
	
	/**消息id*/
	@Json(name = "msgId")
	public String msgId;
	
	public String getMsgTitle() {
		return msgTitle;
	}

	public void setMsgTitle(String msgTitle) {
		this.msgTitle = msgTitle;
	}

	public String getMsgDecription() {
		return msgDecription;
	}

	public void setMsgDecription(String msgDecription) {
		this.msgDecription = msgDecription;
	}

	public String getMsgArgs() {
		return msgArgs;
	}

	public void setMsgArgs(String msgArgs) {
		this.msgArgs = msgArgs;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	@Override
	public String toString() {
		return "NotifyCustomInfo [msgTitle=" + msgTitle + ", msgDecription="
				+ msgDecription + ", msgArgs=" + msgArgs + ", msgType="
				+ msgType + "]";
	}

}
