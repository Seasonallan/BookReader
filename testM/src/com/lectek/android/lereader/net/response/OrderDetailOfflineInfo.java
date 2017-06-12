/*
 * ========================================================
 * ClassName:OrderDetailOfflineInfo.java* 
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
 * 2014-1-6     chendt          #00000       create
 */
package com.lectek.android.lereader.net.response;

import java.io.Serializable;

import android.text.TextUtils;

import com.lectek.android.lereader.permanent.LeyueConst;

public class OrderDetailOfflineInfo implements Serializable{
	private static final long serialVersionUID = -8005872735018329579L;
	private String userId ;//天翼
	private String bookId ;//天翼
	private String calType ;//计费类型（0：免费，1：按书，2：按卷，3：按章，4：按频道）
	private String calObj ;//计费对象_按书时，bookId
	private String charge ;//费用
	private String purchaser ;//购买人 leyueUserId
	private String purchaserType ;//支付类型（1：账户，2：支付宝，3：银行卡，4：快捷支付，5：翼支付，6：阅点,7:移动MM）
	private String sourceType ;//来源类型（来源（0：乐阅平台，1：天翼阅读，2：中信版本）
	private String version ;
	
	public OrderDetailOfflineInfo(
			String userId,String bookId,String calType,String calObj,String charge,String purchaser,
			String purchaserType,String sourceType,String version){
		this.userId = userId;
		this.bookId = bookId;
		this.calType = calType;
		this.calObj = calObj;
		this.charge = charge;
		this.purchaser = purchaser;
		this.purchaserType = purchaserType;
		this.sourceType = sourceType;
		this.version = version;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getBookId() {
		return bookId;
	}
	public void setBookId(String bookId) {
		this.bookId = bookId;
	}
	public String getCalType() {
		return calType;
	}
	public void setCalType(String calType) {
		this.calType = calType;
	}
	public String getCalObj() {
		return calObj;
	}
	public void setCalObj(String calObj) {
		this.calObj = calObj;
	}
	public String getCharge() {
		return charge;
	}
	public void setCharge(String charge) {
		this.charge = charge;
	}
	public String getPurchaser() {
		return purchaser;
	}
	public void setPurchaser(String purchaser) {
		this.purchaser = purchaser;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	public String getPurchaserType() {
		return purchaserType;
	}

	public void setPurchaserType(String purchaserType) {
		this.purchaserType = purchaserType;
	}
	
	public boolean isValidInfo(){
		if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(bookId)&& !TextUtils.isEmpty(calType)
				&& !TextUtils.isEmpty(calObj)&& !TextUtils.isEmpty(charge)&& !TextUtils.isEmpty(purchaserType)
				&& !TextUtils.isEmpty(sourceType)&& !TextUtils.isEmpty(sourceType)
				&& !userId.equals(LeyueConst.TOURIST_USER_ID)) {//旧数据导致两个游客id
			return true;
		}
		return false;
	}
}
