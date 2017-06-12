/*
 * ========================================================
 * ClassName:ScoreUploadResponseInfo.java* 
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

import java.util.ArrayList;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;
import com.lectek.android.lereader.storage.dbase.UserScoreInfo;

/**
 * @description
	积分上传结果
 * @author chendt
 * @date 2013-12-26
 * @Version 1.0
 */
public class ScoreUploadResponseInfo extends BaseDao{

	/**是否为游客。0默认为用户，1为游客*/
	public int isGuset;  
	/**存储数据库返回的查询结果*/
	public ArrayList<UserScoreInfo> scoreInfos;
	/**0.记录不重复。*/
	public static final int IS_NOT_REPEAT_RECORD = 0;
    /** 1.记录重复。*/
	public static final int IS_REPEAT_RECORD = 1;
	/** 2.规则记录已超过上限*/
	public static final int IS_LIMIT_RECORD = 2;
	
	/**可用总积分*/
	@Json(name = "alllimitscore")
	public String allAvailableScore;
	
	/**今日已获得总积分*/
	@Json(name = "dayscore")
	public String todayGainScore;
	
	/**历史总积分*/
	@Json(name = "historytotalscore")
	public String historyTotalScore;
	
	/**当前提交各规则剩余次数*/
	@Json(name = "rulelimit")
	public ArrayList<RuleLimitInfo> limitInfos;
	
	/**请求返回结果里，属于当前记录的请求 的 返回状态。
	 * 0.记录不重复。{@link #IS_NOT_REPEAT_RECORD}
	 * 1.记录重复。{@link #IS_REPEAT_RECORD}
	 * 2.记录已超过上限 {@link #IS_LIMIT_RECORD}*/
	@Json(name = "recordStatus")
	public int recordStatus;
	
	/**当前记录 积分奖励值*/
	@Json(name = "thisscore")
	public int thisScore;
	
	/**
	 * 获取可用总积分
	 * @return
	 */
	public String getAllAvailableScore() {
		return allAvailableScore;
	}
	
	/**
	 * 设置可用总积分
	 * @param allAvailableScore
	 */
	public void setAllAvailableScore(String allAvailableScore) {
		this.allAvailableScore = allAvailableScore;
	}

	/**
	 * 获取今日已获得总积分
	 * @return
	 */
	public String getTodayGainScore() {
		return todayGainScore;
	}

	/**
	 * 设置今日已获得总积分
	 * @param todayGainScore
	 */
	public void setTodayGainScore(String todayGainScore) {
		this.todayGainScore = todayGainScore;
	}

	public String getHistoryTotalScore() {
		return historyTotalScore;
	}

	public void setHistoryTotalScore(String historyTotalScore) {
		this.historyTotalScore = historyTotalScore;
	}

	/**
	 * 获取当前提交各规则剩余次数
	 * @return
	 */
	public ArrayList<RuleLimitInfo> getLimitInfos() {
		return limitInfos;
	}

	public void setLimitInfos(ArrayList<RuleLimitInfo> limitInfos) {
		this.limitInfos = limitInfos;
	}

	public int getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(int recordStatus) {
		this.recordStatus = recordStatus;
	}

	/**
	 * 获取当前记录 积分奖励值
	 * @return
	 */
	public int getThisScore() {
		return thisScore;
	}

	public void setThisScore(int thisScore) {
		this.thisScore = thisScore;
	}

	@Override
	public String toString() {
		return "ScoreUploadResponseInfo [isGuset=" + isGuset + ", scoreInfos="
				+ scoreInfos + ", allAvailableScore=" + allAvailableScore
				+ ", todayGainScore=" + todayGainScore + ", historyTotalScore="
				+ historyTotalScore + ", limitInfos=" + limitInfos
				+ ", recordStatus=" + recordStatus + ", thisScore=" + thisScore
				+ "]";
	}

}
