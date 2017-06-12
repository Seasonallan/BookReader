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

import org.json.JSONException;
import org.json.JSONObject;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;
import com.lectek.android.lereader.lib.utils.LogUtil;

/**
 * @description
	处理解析 json数据 _判断包月产品是否被订购
 * @author chendt
 * @date 2013-8-27
 * @Version 1.0
 */
public class MonthlyIsOrderInfoPaser implements PaserInterface{

	@Override
	public MonthlyIsOrderInfo paser(String str) { 
		MonthlyIsOrderInfo data = new MonthlyIsOrderInfo();
		try {
			data.fromJsonObject(new JSONObject(str));
		} catch (JSONException e) {
			data = null;
		} 
		if(data!=null){
			LogUtil.i("pinotao","info = "+data.toString());
			return data;
		}
		return null;
	}
	
	public class MonthlyIsOrderInfo extends BaseDao{
		
		@Json(name ="response")
		public Response response;
		
		public class Response extends BaseDao{
			
			@Json(name ="code")
			public String code;
			
			@Json(name ="message")
			public String message;

			public String getCode() {
				return code;
			}

			public void setCode(String code) {
				this.code = code;
			}

			public String getMessage() {
				return message;
			}

			public void setMessage(String message) {
				this.message = message;
			}

			@Override
			public String toString() {
				return "Response [code=" + code + ", message=" + message + "]";
			}
			
		}
	} 
}
