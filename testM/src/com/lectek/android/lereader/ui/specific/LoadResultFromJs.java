/*
 * ========================================================
 * ClassName:LoadResultFromJs.java* 
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
 * 2013-10-9     chendt          #00000       create
 */
package com.lectek.android.lereader.ui.specific;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Intent;

import com.lectek.android.lereader.lib.utils.DateUtil;
import com.lectek.android.lereader.lib.utils.IProguardFilterOnlyPublic;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.permanent.ApiConfig;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.ui.login_leyue.UserLoginLeYueNewActivity;
import com.lectek.android.lereader.utils.CommonUtil;

public class LoadResultFromJs implements IProguardFilterOnlyPublic{
	private static final String TAG = LoadResultFromJs.class.getSimpleName();
	private Object objectContext;
	private LoadReselutCallBack mLoadReselutCallBack;
	private LoadResultFromJs(){}
	public LoadResultFromJs(Object context,LoadReselutCallBack mLoadReselutCallBack){
		objectContext = context;
		this.mLoadReselutCallBack = mLoadReselutCallBack;
	}
	private String resultStatus;
	/**加载成功与否*/
	public void onLoadSuccess(String result){
//		LogUtil.e(Tag, "--isSuccess--"+result);
		resultStatus = result;//由于webview展示要在showRetryView之前。这里先保存结果。
		mLoadReselutCallBack.onLoadReselutCallBack();
	}
	
	/**打印当前请求的结果*/
	public void logForResponseStatus(String result,String url){
		LogUtil.e(TAG, "【Status"+result+"】：url = "+url);
	}

    public String getTimeStamp(){
        /**
         * TODO:需要数据库来处理。
         *@condition1:一天更新一次 ——时间精确到天
         *@condition2:重试时——时间精确到秒
         *
         *@logic:a.如果不为空  且c1不满足（当天内）就传Xml。否则传当前时间
         * */
        String urlTag = BaseWebView.getCurrentWebUrlTag(objectContext);
        if (urlTag == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        String deadlineStr = null;
        String currentTimeStr = DateUtil.getNowDay(c.getTime());
        String targetStr = null;

        if (deadlineStr!=null) {
            if (isInToday(deadlineStr,currentTimeStr)) {
                //当天内
                targetStr = deadlineStr;
            }else {
                //隔天了，直接传当前时间
                targetStr = currentTimeStr;
            }
        }else {
            targetStr = currentTimeStr;
        }
        return targetStr;
    }
	public String getServerUrl(){
		return ApiConfig.URL+"/";
	}
	
	private String targetStr;
	
	public String getParaStr(String urlTag) {
		return CommonUtil.getUrlParamStr(urlTag, targetStr);
	}
	
	public String getTargetStr() {
		return targetStr;
	}
	
	public void setTargetStr(String targetStr) {
		this.targetStr = targetStr;
	}
	/**
	 * 判断获取xml时间 是否在当天内 
	 * @param dealinesStr
	 * @return
	 */
	private boolean isInToday(String dealinesStr,String currentTimeStr){
		long deadlinesTimeYMD =0 ,currentTimeYMD = 0;
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			deadlinesTimeYMD = sdf.parse(dealinesStr).getTime();
			currentTimeYMD = sdf.parse(currentTimeStr).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (deadlinesTimeYMD - currentTimeYMD == 0) {
			//当天内
			return true;
		}else {
			//隔天了，直接传当前时间
			return false;
		}
	}
	
	public String getResultStatus() {
		return resultStatus;
	}
	
	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}
	
	public static interface LoadReselutCallBack{
		public void onLoadReselutCallBack();
	}
	
	public void gotoLoginOpt(String url){
		if (objectContext instanceof ThirdUrlActivity) {
			ThirdUrlActivity activity = (ThirdUrlActivity) objectContext;
			Intent intent = new Intent(activity,UserLoginLeYueNewActivity.class);
//			activity.startActivityForResult(intent, ThirdUrlActivity.THIRD_URL_TAG);
			activity.startActivity(intent);
		}
	}
	
	/**
	 * 启动ThirdUrlActivity的摇一摇监听
	 */
	public void shakeReady(boolean canShake){
		if (objectContext instanceof ThirdUrlActivity) {
			ThirdUrlActivity activity = (ThirdUrlActivity) objectContext;
			if (canShake) {
				//发广播
				activity.sendBroadcast(new Intent(AppBroadcast.ACTION_BEGIN_SHAKE));
			}else {
				//发广播
				activity.sendBroadcast(new Intent(AppBroadcast.ACTION_CANNOT_SHAKE));
			}
		}
	}
	
}