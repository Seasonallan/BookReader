/*
 * ========================================================
 * ClassName:MusicInfo.java* 
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
 * 2013-12-13     chendt          #00000       create
 */
package com.lectek.android.lereader.ui.basereader_leyue.expand_audio;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 * @description
	音乐播放与乐谱对应实体信息
 * @author chendt
 * @date 2013-12-13
 * @Version 1.0
 */
public class MusicInfo extends BaseDao implements Serializable{
	
	public static final long serialVersionUID = 2843288233914858035L;
	
	/**MP3资源地址*/
	@Json(name ="src")
	public String src;
	
	/**图片资源地址*/
	@Json(name ="img")
	public String img;
	
	/**播放进度域*/ //TODO:后续扩展，针对简谱，一张图有多个进度域。存在list里。isInview 判断时，返回对应的索引，生成当前的progress以及其他参数。listview滚动的话，需要记录当前图片的index
	@Json(name ="progress")
	public String progress;//0-12000 (ms)

	/**移动区间*/
	@Json(name ="bound")
	public String bound;//0.1-0.97 (比例)

	/**所属音调*/
	@Json(name ="tone")
	public String tone;//A、A1 ...
	
	/**当前位置*/
	public transient float curPosition;//(ms)
	
	/**播放乐谱的第几行*/
	public transient  int playIndex;
	public transient  int screen_W;
	
	public transient Drawable drawable;
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	public String getBound() {
		return bound;
	}
	public void setBound(String bound) {
		this.bound = bound;
	}
	public String getTone() {
		return tone;
	}
	public void setTone(String tone) {
		this.tone = tone;
	}
	
	public float getLeftBound(){
		return Float.valueOf(bound.substring(0, bound.indexOf("-")));
	}
	
	public float getRightBound(){
		return Float.valueOf(bound.substring(bound.indexOf("-")+1));
	}
	
	public float getLeftProgress(){
		return Float.valueOf(progress.substring(0, progress.indexOf("-")));
	}
	
	public float getRightProgress(){
		return Float.valueOf(progress.substring(progress.indexOf("-")+1));
	}
	
	/**当前时间是否在该区域内*/
	public boolean isInView(){
		if (curPosition>getLeftProgress() && curPosition<getRightProgress()) {
			return true;
		}else {
			return false;
		}
	}
	
	/**获取当前时间占 该区域的比例*/
	public float getSale(){
		float total = getRightProgress()-getLeftProgress();
		float current = curPosition-getLeftProgress();
		return current/total;
	}
	
	/**获取乐谱有效区域大小*/
	public float getValidZone(){
		return screen_W*(getRightBound()-getLeftBound());
	}
	
	/**获取左偏移量*/
	public float getLeftBoundWidth(){
		return getLeftBound()*screen_W;
	}
	
	/**获取右偏移量*/
	public float getRightBoundWidth(){
		return getRightBound()*screen_W;
	}
	
	public float getValidProgress(){
		return getRightProgress() - getLeftProgress();
	}
	
	@Override
	public String toString() {
		return "MusicInfo [src=" + src + ", img=" + img + ", progress="
				+ progress + ", bound=" + bound + ", tone=" + tone
				+ ", curPosition=" + curPosition + ", playIndex=" + playIndex
				+ ", screen_W=" + screen_W + "]";
	}
	
}
