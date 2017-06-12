/*
 * ========================================================
 * ClassName:MapTypeInfoPaser.java* 
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
 * 2013-8-26     chendt          #00000       create
 */
package com.lectek.android.lereader.net.response.tianyi;

import java.lang.reflect.Type;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.lectek.android.lereader.analysis.MobclickAgentUtil;
import com.lectek.android.lereader.lib.net.exception.ErrorMessage;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.JsonArrayList;

/**
 * @description
	处理解析 json数据_用于一般情况下，对应json数据结构，只有一层的情况
	调用该对象的parseCommonEntity方法。
 * @author chendt
 * @date 2013-9-27
 * @Version 1.0
 */
public class CommonEntiyPaser <T extends BaseDao>{

    /**
     * 用于一般情况下，对应json数据结构。正常这个方法即可满足<br/>
     * 【注意】:如果数据结果为 code:Xxxx msg:xxx 同异常情况一致，不会走onPostLoad。会在onFail中执行。
     * @param <T>
     * @param str
     * @param c 传对应的实体 class对象
     * @return
     * @throws GsonResultException
     */
    public T parseCommonEntity(String str,Class<T> c) throws GsonResultException{
        if (TextUtils.isEmpty(str)) {//通常soket异常时，str为空。此时应该抛异常。而不是return null
            throw new GsonResultException(GsonResultException.GsonErrorEnum.CONNET_FAIL_EXCEPTION,null);
        }
        JSONObject obj = null;
		try {
			obj = new JSONObject(str);
		} catch (JSONException e1) {
            throw new GsonResultException(GsonResultException.GsonErrorEnum.SERVICE_RESPONSE_ERROR_EXCEPTION, null);
		}
        
        ErrorMessage info =  new ErrorMessage();
        try {
        	info.fromJsonObject(obj);
        } catch (Exception e) {
        	info = null;
        }
        if (info!=null && (!TextUtils.isEmpty(info.getErrorCode()))) {
            uploadException(null,info,"服务器返回错误");//友盟统计：
            throw new GsonResultException(GsonResultException.GsonErrorEnum.SERVICE_RESPONSE_ERROR_EXCEPTION,info);
        }
        try { 
            T newObject = c.newInstance();
	       	newObject.fromJsonObject(obj);
	       	return newObject; 
        } catch (Exception e) {
            uploadException(e, info,"GSON解析错误");//友盟统计：
            throw new GsonResultException(GsonResultException.GsonErrorEnum.GSON_ERROR_EXCEPTION,null);
        }
    }

    /**
     * 转化列表数据
     * @param <T>
     * @param str
     * @return
     * @throws com.lectek.android.lereader.net.exception.GsonResultException
     */
    public JsonArrayList<T> parseLeyueListEntity(String str, Class<T> tClass) throws GsonResultException {
    	if (TextUtils.isEmpty(str)) {//通常soket异常时，str为空。此时应该抛异常。而不是return null
            throw new GsonResultException(GsonResultException.GsonErrorEnum.CONNET_FAIL_EXCEPTION,null);
        }
        JSONArray array = null;
        try {
			array = new JSONArray(str);
		} catch (Exception e) {
            throw new GsonResultException(GsonResultException.GsonErrorEnum.SERVICE_RESPONSE_ERROR_EXCEPTION, null);
		}
        
        ErrorMessage info =  new ErrorMessage();
        try {
        	info.fromJsonObject(new JSONObject(str));
        } catch (Exception e) {
        	info = null;
        }
        if (info!=null && (!TextUtils.isEmpty(info.getErrorCode()))) {
            uploadException(null,info,"服务器返回错误");//友盟统计：
            throw new GsonResultException(GsonResultException.GsonErrorEnum.SERVICE_RESPONSE_ERROR_EXCEPTION,info);
        }
        try { 
            JsonArrayList<T> list = new JsonArrayList<T>(tClass);
            list.fromJsonArray(array);
	       	return list; 
        } catch (Exception e) {
            uploadException(e, info,"GSON解析错误");//友盟统计：
            throw new GsonResultException(GsonResultException.GsonErrorEnum.GSON_ERROR_EXCEPTION,null);
        } 
    }


    /**
     * 上传失败信息到友盟。
     * @param e
     * @param info
     */
    public static void uploadException(Exception e,ErrorMessage info,String extraInfo){
        HashMap<String, String > exceptionInfo = new HashMap<String, String>();
        extraInfo = "【"+extraInfo+"】";
        if (e != null) {
            if (e instanceof GsonResultException) {
                GsonResultException ge = (GsonResultException) e;
                exceptionInfo.put(extraInfo+"Gson解析异常：", ge.getResponseInfo().toString());
            }else {
                exceptionInfo.put(extraInfo+"异常：", e.getMessage());
            }
        }
        if (info != null) {
            exceptionInfo.put(extraInfo+"服务器返回错误：", info.toString());
        }
        MobclickAgentUtil.uploadUmengMsg("exception_from_app", exceptionInfo);
    }

    class tc implements Type{

    }
}
