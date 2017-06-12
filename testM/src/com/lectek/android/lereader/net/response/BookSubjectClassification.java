/*
 * ========================================================
 * ClassName:BookSubjectClassification.java* 
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
 * 2013-9-12     chendt          #00000       create
 */
package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**专题分类对象*/
public class BookSubjectClassification extends BaseDao{
	
	@Json(name = "subjectId")
	public int subjectId;//专题ID
	
	@Json(name = "subjectName")
	public String subjectName;//专题名称
	
	@Json(name = "subjectIntro")
	public String subjectIntro;//专题介绍
	
	@Json(name = "type")
	public String subjectType;//专题类型（1：单本书籍，2：列表书籍，3：url形式，4：包月专区）
	
	@Json(name = "subjectPic")
	public String subjectPic;//专题图片
	
	@Json(name = "bookNum")
	public int subjectBookNum;//书籍数量
	
	@Json(name = "sequence")
	public int sequence;//排序
	
	@Json(name = "memo")
	public String memo;//备注
	
	@Json(name = "outBookId")
	public String outBookId;
	
	public String getOutBookId() {
		return outBookId;
	}

	public void setOutBookId(String outBookId) {
		this.outBookId = outBookId;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public int getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getSubjectIntro() {
		return subjectIntro;
	}

	public void setSubjectIntro(String subjectIntro) {
		this.subjectIntro = subjectIntro;
	}

	public String getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}

	public String getSubjectPic() {
		return subjectPic;
	}

	public void setSubjectPic(String subjectPic) {
		this.subjectPic = subjectPic;
	}

	public int getSubjectBookNum() {
		return subjectBookNum;
	}

	public void setSubjectBookNum(int subjectBookNum) {
		this.subjectBookNum = subjectBookNum;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	@Override
	public String toString() {
		return "BookSubjectClassification [subjectId=" + subjectId
				+ ", subjectName=" + subjectName + ", subjectIntro="
				+ subjectIntro + ", subjectType=" + subjectType
				+ ", subjectPic=" + subjectPic + ", subjectBookNum="
				+ subjectBookNum + ", sequence=" + sequence + "]";
	}
	
}
