/*
 * ========================================================
 * ClassName:TagPairs.java* 
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
package com.lectek.android.lereader.data;


/**
 * @description
	针对那些互斥的标签串。如果用户设置了A标签，就意味着跟它互斥的B标签需要删除。<br/>
	如果该标签不存在互斥标签，则把 {@link #DEFAULT_TAG}作为 互斥标签传入。
 * @author chendt	
 * @date 2014-2-25
 * @Version 1.0
 */
public class TagPairs implements Comparable<TagPairs>{
	public final static String DEFAULT_TAG = "默认标签"; 
	private String tagA;
	
	private String tagB;
	
	private String currentTag;
	
	public TagPairs(String tagA, String tagB, String tempTag) {
		super();
		this.tagA = tagA;
		this.tagB = tagB;
		this.currentTag = tempTag;
	}

	public String getOppsiteTag(){
		if (currentTag == null) {
			return DEFAULT_TAG;
		}
		if (currentTag.equals(tagA)) {
			return tagB;
		}
		if (currentTag.equals(tagB)) {
			return tagA;
		}
		return DEFAULT_TAG;
	}
	
	public String getCurrentTag(){
		if (currentTag == null) {
			return DEFAULT_TAG;
		}
		return currentTag;
	}

	@Override
	public int compareTo(TagPairs o) {
		TagPairs tag = (TagPairs) o;
		if (this.tagA.compareTo(tag.tagA) > 0) {
			return 1;
		}
		if (this.tagA.compareTo(tag.tagA) < 0) {
			return -1;
		}
		if (this.tagB.compareTo(tag.tagB) > 0) {
			return 1;
		}
		if (this.tagB.compareTo(tag.tagB) < 0) {
			return -1;
		}
		return 0;
	}
}
