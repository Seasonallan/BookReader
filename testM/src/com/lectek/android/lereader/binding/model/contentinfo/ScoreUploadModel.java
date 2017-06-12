/*
 * ========================================================
 * ClassName:ScoreUploadModel.java* 
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
 * 2013-9-28     chendt          #00000       create
 */
package com.lectek.android.lereader.binding.model.contentinfo;

import java.util.ArrayList;

import android.util.Log;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataBackgroundModel;
import com.lectek.android.lereader.lib.storage.dbase.JsonArrayList;
import com.lectek.android.lereader.lib.utils.DateUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.ScoreUploadResponseInfo;
import com.lectek.android.lereader.storage.dbase.UserScoreInfo;
import com.lectek.android.lereader.storage.dbase.util.UserScoreRecordDB;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;

/**
 * @description
	积分上传model,主要处理 游客，用户 场景：提交积分包括当前指定积分和离线积分
	<br/>主要传参：type:{@link UserScoreRecordDB #TYPE TYPE} 
	;sourceId:{@link UserScoreRecordDB #SOURCE_ID SOURCE_ID}
 * @author chendt
 * @date 2013-9-28
 * @Version 1.0
 */
public class ScoreUploadModel extends BaseLoadNetDataBackgroundModel<ScoreUploadResponseInfo> {
	public static final String TAG = ScoreUploadModel.class.getSimpleName(); 
	private JsonArrayList<UserScoreInfo> updateList;

	@Override
	protected ScoreUploadResponseInfo onLoad(Object... params) throws Exception {
		ScoreUploadResponseInfo uploadResponseInfo = null;
		if (params!=null && params.length == 2) {
			String type =  (String) params[0];
			String sourceId =  (String) params[1];
			LogUtil.e("----model--type = "+type);
			ArrayList<UserScoreInfo> scoreList = UserScoreRecordDB.getInstance(getContext()).
															getUserScoreRecordByType(type, sourceId);
			uploadResponseInfo = new ScoreUploadResponseInfo();
			/*游客*/
			if (!PreferencesUtil.getInstance(getContext()).getIsLogin()) {
				uploadResponseInfo.isGuset = 1;
				uploadResponseInfo.scoreInfos = scoreList;
				if (scoreList==null || scoreList.size()<1) {
					uploadResponseInfo.setRecordStatus(ScoreUploadResponseInfo.IS_NOT_REPEAT_RECORD);
					UserScoreRecordDB.getInstance(getContext()).setUserScoreRecordByType(type, sourceId, UserScoreRecordDB.STATUS_FAIL);
				}else {
					String ruleId = null;
					if (type.equals(UserScoreInfo.ANDROID_LOGIN)) {
						ruleId = UserScoreInfo.RULE_LOGIN;
					}else if(type.equals(UserScoreInfo.QQ_FRIEND)||type.equals(UserScoreInfo.SINA)
							||type.equals(UserScoreInfo.WX_FRIEND)||type.equals(UserScoreInfo.WX_ZONE)
							||type.equals(UserScoreInfo.YX_FRIEND)||type.equals(UserScoreInfo.YX_ZONE)){
						ruleId = UserScoreInfo.RULE_SHARE;
					}
					int count = UserScoreRecordDB.getInstance(getContext()).getUserScoreRecordByType(ruleId);
					if (ruleId.equals(UserScoreInfo.ANDROID_LOGIN)) {
						if (count>=UserScoreRecordDB.LOGIN_LIMIT) {
							uploadResponseInfo.setRecordStatus(ScoreUploadResponseInfo.IS_LIMIT_RECORD);
						}else {
							uploadResponseInfo.setRecordStatus(ScoreUploadResponseInfo.IS_REPEAT_RECORD);
						}
					}else if (ruleId.equals(UserScoreInfo.RULE_SHARE)) {
						if (count>=UserScoreRecordDB.SHARE_LIMIT) {
							uploadResponseInfo.setRecordStatus(ScoreUploadResponseInfo.IS_LIMIT_RECORD);
						}else {
							uploadResponseInfo.setRecordStatus(ScoreUploadResponseInfo.IS_REPEAT_RECORD);
						}
					}
				}
			}else {
			/*用户*/
				updateList = new JsonArrayList<UserScoreInfo>(UserScoreInfo.class);
				// 本地无记录
				if (scoreList==null || scoreList.size()<1) {
					UserScoreRecordDB.getInstance(getContext()).setUserScoreRecordByType(type, sourceId, UserScoreRecordDB.STATUS_FAIL);
					// 提交当前 类型积分
					String ruleId = null;
					if (UserScoreInfo.ANDROID_LOGIN.equals(type)) {
						ruleId = UserScoreInfo.RULE_LOGIN;
					}else {
						ruleId = UserScoreInfo.RULE_SHARE;
					}
					UserScoreInfo scoreInfo = new UserScoreInfo(PreferencesUtil.getInstance(getContext()).getUserId()
							, ruleId, type, sourceId, DateUtil.getCurrentTimeStyle1(), 
										UserScoreRecordDB.STATUS_OK,UserScoreInfo.IS_CURRENT_RECORD);
					updateListAdd(scoreInfo);
				}else {
					UserScoreInfo userScoreInfo = scoreList.get(0);
					userScoreInfo.setIsHistoryRecord(UserScoreInfo.IS_CURRENT_RECORD);
					// 提交积分。
					if (userScoreInfo.status == UserScoreRecordDB.STATUS_FAIL) {
						updateListAdd(userScoreInfo);
					}else {
						int count = UserScoreRecordDB.getInstance(getContext()).getUserScoreRecordByType(userScoreInfo.getRuleId());
						if (count>=UserScoreRecordDB.SHARE_LIMIT) {//不用处理登陆，因为不会显示通知
							uploadResponseInfo.setRecordStatus(ScoreUploadResponseInfo.IS_LIMIT_RECORD);
						}else {
							uploadResponseInfo.setRecordStatus(ScoreUploadResponseInfo.IS_REPEAT_RECORD);
						}
					}
				}
				// 同时获取离线记录积分，一并提交。
				ArrayList<UserScoreInfo> failList = UserScoreRecordDB.getInstance(getContext()).getUserScoreRecordByStatusFail();
				for (int i = 0; i < failList.size(); i++) {
					updateListAdd(failList.get(i));
				}
				if (updateList.size() > 0) {
					Log.d(TAG, "----updateList-----"+updateList.toString());
					String jsonScore = updateList.toJsonArray().toString();//new Gson().toJson(updateList);
					Log.d(TAG, "----toJson-----"+jsonScore);
					uploadResponseInfo = ApiProcess4Leyue.getInstance(getContext()).updateUserScoreListNormal(jsonScore);
					//更改数据库记录状态。
					if (uploadResponseInfo!=null) {
						UserScoreRecordDB.getInstance(getContext()).updateRecordStatusOk(updateList);
					}
				}
			}
		}
		return uploadResponseInfo;
	}

	/**
	 * 参数1_type:{@link UserScoreRecordDB #TYPE TYPE} <br/>
	 * 参数2_sourceId:{@link UserScoreRecordDB #SOURCE_ID SOURCE_ID}
	 * */
	@Override
	public String start(Object... params) {
		return super.start(params);
	}
	
	private void updateListAdd(UserScoreInfo info){
		if (!updateList.contains(info)) {
			updateList.add(info);
		}
	}
}
