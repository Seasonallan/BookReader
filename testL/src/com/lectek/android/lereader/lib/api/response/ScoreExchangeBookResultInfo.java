/*
 * ========================================================
 * ClassName:ScoreExchangeBookResultInfo.java* 
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
 * 2013-12-25     chendt          #00000       create
 */
package com.lectek.android.lereader.lib.api.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 * @description
	积分兑书返回结果
 * @author chendt
 * @date 2013-12-25
 * @Version 1.0
 */
public class ScoreExchangeBookResultInfo extends BaseDao{

	/**记录添加是否成功*/
	@Json(name = "success")
	public boolean isSuccess;
	
	/**订单id*/
	@Json(name = "orderId")
	public String orderId;
	
	/**实际扣减积分*/
	@Json(name = "deductScore")
	public String deductScore;
	
	/**剩余可用积分*/
	@Json(name = "usableScore")
	public String usableScore;
	
	/**描述*/
	@Json(name = "desc")
	public String desc;

    /**天翼阅读开放平台_错误码*/
    @Json(name = "code")
    public int code = -1;

    public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getDeductScore() {
		return deductScore;
	}

	public void setDeductScore(String deductScore) {
		this.deductScore = deductScore;
	}

	public String getUsableScore() {
		return usableScore;
	}

	public void setUsableScore(String usableScore) {
		this.usableScore = usableScore;
	}

	@Override
	public String toString() {
		return "ScoreExchangeBookResultInfo [isSuccess=" + isSuccess
				+ ", orderId=" + orderId + ", deductScore=" + deductScore
				+ ", usableScore=" + usableScore + ", desc="
				+ desc + "]";
	}

}
