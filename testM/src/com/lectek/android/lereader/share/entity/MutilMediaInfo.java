/*
 * ========================================================
 * ClassName:MutilMediaInfo.java* 
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
 * 2013-5-8     chendt          #00000       create
 */
package com.lectek.android.lereader.share.entity;

import im.yixin.sdk.api.SendMessageToYX;

import java.io.Serializable;

import com.tencent.mm.sdk.openapi.SendMessageToWX;

/**
 * @description
	分享 信息实体<br>
	调用前 请封装好 相应信息内容。 如：分享类型type;填充对应分享类型的 数据。
 * @author chendt
 * @date 2013-5-8
 * @Version 1.0
 */
public class MutilMediaInfo implements Serializable {

	private static final long serialVersionUID = 4209804588876627761L;
	public static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;//分享到朋友圈的版本
	public static final int WX_FRIEND = SendMessageToWX.Req.WXSceneSession;//朋友 
	public static final int WX_FRIEND_ZONE = SendMessageToWX.Req.WXSceneTimeline;//朋友圈
	public static final int YX_FRIEND = SendMessageToYX.Req.YXSceneSession;//朋友 
	public static final int YX_FRIEND_ZONE = SendMessageToYX.Req.YXSceneTimeline;//朋友圈
	
	/**
	 * 发送文本或短信内容
	 */
	private String text;
	
	private int type;//朋友还是朋友圈
	
	private String filePath;//文件路径
	
	private String title;//仅用于微信/易信
	
	private String linkUrl;//仅用于微信/易信
	
	public MutilMediaInfo(){}
	
	public MutilMediaInfo(String text,String filePath,int type){
		this.text = text;
		this.filePath = filePath;
		this.type = type;
	}
	
	public MutilMediaInfo(String title,String text,String filePath,int type,String linkUrl){
		this.title = title;
		this.text = text;
		this.filePath = filePath;
		this.type = type;
		this.linkUrl = linkUrl;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	
}
