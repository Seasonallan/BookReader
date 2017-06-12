/*
 * ========================================================
 * ClassName:RuleLimitInfo.java* 
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
 * 2013-12-26     chendt          #00000       create
 */
package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;
import com.lectek.android.lereader.storage.dbase.UserScoreInfo;

public class RuleLimitInfo extends BaseDao {

	public static final long serialVersionUID = -3857379806136299938L;
	
	/**规则总次数*/
	@Json(name = "allCount")
	public int allCount;
	
	/**规则剩余次数*/
	@Json(name = "countlimit")
	public int remainCounts;
	
	/**规则id
	 * {@link UserScoreInfo #RULE_LOGIN}，{@link UserScoreInfo #RULE_SHARE}
	 * */
	@Json(name = "ruleId")
	public String ruleId;

	public int getAllCount() {
		return allCount;
	}

	public void setAllCount(int allCount) {
		this.allCount = allCount;
	}

	public int getRemainCounts() {
		return remainCounts;
	}

	public void setRemainCounts(int remainCounts) {
		this.remainCounts = remainCounts;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	@Override
	public String toString() {
		return "RuleLimitInfo [allCount=" + allCount + ", remainCounts="
				+ remainCounts + ", ruleId=" + ruleId + "]";
	}
	
}
