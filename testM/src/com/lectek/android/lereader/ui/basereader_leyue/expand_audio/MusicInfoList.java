/*
 * ========================================================
 * ClassName:MusicInfoList.java* 
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
 * 2013-12-14     chendt          #00000       create
 */
package com.lectek.android.lereader.ui.basereader_leyue.expand_audio;

import java.io.Serializable;
import java.util.ArrayList;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

public class MusicInfoList extends BaseDao implements Serializable{
	
	private static final long serialVersionUID = 5073480771339331808L;
	
	@Json(name ="audio", className = MusicInfo.class)
	public ArrayList<MusicInfo> musicInfo;
	
	/**乐谱名称*/
	@Json(name ="title")
	public String title;
	
	public ArrayList<MusicInfo> getMusicInfo() {
		return musicInfo;
	}

	public void setMusicInfo(ArrayList<MusicInfo> musicInfo) {
		this.musicInfo = musicInfo;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "MusicInfoList [musicInfo=" + musicInfo + ", title=" + title
				+ "]";
	}
	
}
