/*
 * ========================================================
 * ClassName:UmengShareInfo.java* 
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
 * 2013-11-27     chendt          #00000       create
 */
package com.lectek.android.lereader.lib.share.entity;

public class UmengShareInfo {
	/**分享文本内容*/
	private String shareText;
	
	/**分享图片地址*/
	private String sharePicUrl;
	
	/**分享音频地址*/
	private String musicUrl;
	
	/**分享视频地址*/
	private String videoUrl;
	
	public UmengShareInfo(String shareText,String sharePicUrl){
		this.sharePicUrl = sharePicUrl;
		this.shareText = shareText;
	}
	
	public String getShareText() {
		return shareText;
	}

	public void setShareText(String shareText) {
		this.shareText = shareText;
	}

	public String getSharePicUrl() {
		return sharePicUrl;
	}

	public void setSharePicUrl(String sharePicUrl) {
		this.sharePicUrl = sharePicUrl;
	}

	public String getMusicUrl() {
		return musicUrl;
	}

	public void setMusicUrl(String musicUrl) {
		this.musicUrl = musicUrl;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	
}
